package novite.rs.game.npc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.cache.Cache;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.SecondaryBar;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.combat.NPCCombat;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Facade;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.HerbCleaning;
import novite.rs.game.player.actions.prayer.Bone;
import novite.rs.game.player.content.achievements.impl.KBDAchievement;
import novite.rs.game.player.content.achievements.impl.KillBandosAchievement;
import novite.rs.game.player.content.scrolls.ClueScrollManager;
import novite.rs.game.player.content.slayer.SlayerTask;
import novite.rs.game.player.content.slayer.Type;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.game.player.quests.Quest;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.impl.Helpless_Lawgof;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Caskets;
import novite.rs.utility.MapAreas;
import novite.rs.utility.Utils;
import novite.rs.utility.game.TeleportLocations;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.json.impl.NPCBonuses;
import novite.rs.utility.game.json.impl.NPCDropManager;
import novite.rs.utility.game.npc.NPCCombatDefinitionsL;
import novite.rs.utility.game.npc.NPCNames;
import novite.rs.utility.game.npc.Nonmoving;
import novite.rs.utility.game.npc.drops.Drop;
import novite.rs.utility.game.npc.drops.Drop.Chance;
import novite.rs.utility.logging.types.FileLogger;

public class NPC extends Entity implements Serializable {

	private static final long serialVersionUID = -4794678936277614443L;
	public static int NO_WALK = 0x0, NORMAL_WALK = 0x2, WATER_WALK = 0x4, FLY_WALK = 0x8;

	private int id;
	private WorldTile respawnTile;
	private WorldTile startTile;
	private int mapAreaNameHash;
	private boolean canBeAttackFromOutOfArea;
	private boolean randomwalk;
	private int[] bonuses; // 0 stab, 1 slash, 2 crush,3 mage, 4 range, 5 stab
	// def, blahblah till 9
	private boolean spawned;
	private transient NPCCombat combat;
	private WorldTile forceWalk;

	private long lastAttackedByTarget;
	private boolean cantInteract;
	private int capDamage;
	private int lureDelay;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private int forceTargetDistance;
	private boolean forceFollowClose;
	private int walkType;
	private boolean forceMultiAttacked;
	private String customName;
	private boolean intelligentRouteFinder;

	// npc masks
	private transient Transformation nextTransformation;
	private transient SecondaryBar nextSecondaryBar;
	// name changing masks
	private String name;
	private transient boolean changedName;
	private int combatLevel;
	private transient boolean changedCombatLevel;

	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	@Override
	public boolean isFamiliar() {
		return this instanceof Familiar;
	}

	/*
	 * creates and adds npc
	 */
	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile);
		this.id = id;
		this.respawnTile = new WorldTile(tile);
		this.mapAreaNameHash = mapAreaNameHash;
		this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		this.setSpawned(spawned);
		this.customName = NPCNames.getName(id) == null ? name : NPCNames.getName(id);
		combatLevel = -1;
		setStartTile(tile);
		setHitpoints(getMaxHitpoints());
		JsonHandler.waitForLoad();
		setDirection(((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).getDirection(this).ordinal());
		for (int i : Nonmoving.getList()) {
			if (i == id) {
				setWalkType(NO_WALK);
			} else {
				setWalkType(getDefinitions().walkMask);
			}
		}
		setBonuses(NPCBonuses.getBonuses(id));
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		// npc is inited on creating instance
		initEntity();
		World.addNPC(this);
		World.updateEntityRegion(this);
		// npc is started on creating instance
		loadMapRegions();
		checkMultiArea();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextSecondaryBar != null || nextTransformation != null || changedCombatLevel || changedName;
	}

	public void setNextNPCTransformation(NPC npc) {
		setNPC(npc.getId());
		nextTransformation = new Transformation(npc.getId());
	}

	public void transformInto(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public void setNPC(int id) {
		this.id = id;
		setBonuses(NPCBonuses.getBonuses(id));
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedCombatLevel = false;
		changedName = false;
		nextSecondaryBar = null;
	}

	public int getMapAreaNameHash() {
		return mapAreaNameHash;
	}

	public boolean canBeAttackFromOutOfArea() {
		return canBeAttackFromOutOfArea;
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(id);
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitionsL.getNPCCombatDefinitions(id);
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatDefinitions().getHitpoints();
	}

	public int getId() {
		return id;
	}

	public static void main(String[] args) throws IOException {
		Cache.init();
	}

	public void processNPC() {
		if (isDead()) {
			return;
		}
		try {
			if (customName != null) {
				setName(customName);
			}
		} catch (Exception e) {
			System.err.println("Error setting " + getName() + "'s name.");
		}
		if (!combat.process()) {
			if (!isForceWalking()) {
				if (!cantInteract) {
					if (!checkAgressivity()) {
						if (getFreezeDelay() < Utils.currentTimeMillis()) {
							if (!Nonmoving.contained(id) && ((getWalkType() & NORMAL_WALK) != 0) && Math.random() * 1000.0 < 100.0) {
								int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
								int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
								resetWalkSteps();
								if (getMapAreaNameHash() != -1) {
									if (!MapAreas.isAtArea(getMapAreaNameHash(), this)) {
										forceWalkRespawnTile();
										return;
									}
									addWalkSteps(getX() + moveX, getY() + moveY, 5, (getWalkType() & FLY_WALK) == 0);
								} else {
									addWalkSteps(respawnTile.getX() + moveX, respawnTile.getY() + moveY, 5, (getWalkType() & FLY_WALK) == 0);
								}
							}
						}
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getFreezeDelay() < Utils.currentTimeMillis()) {
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						addWalkSteps(forceWalk.getX(), forceWalk.getY(), getSize(), true);
					}
					if (!hasWalkSteps()) {
						setNextWorldTile(new WorldTile(forceWalk));
						forceWalk = null;
					}
				} else {
					forceWalk = null;
				}
			}
		}
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
	}

	public int getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.anInt853 << 32 != 0 && definitions.respawnDirection > 0 && definitions.respawnDirection <= 8) {
			return (4 + definitions.respawnDirection) << 11;
		}
		return 0;
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final NPC target = this;
		if (hit.getDamage() > 0) {
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		}
		user.heal(hit.getDamage() / 5);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0) {
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
				}
			}
		}, 1);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage) {
			hit.setDamage(capDamage);
		}
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE) {
			return;
		}
		Entity source = hit.getSource();
		if (source == null) {
			return;
		}
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.getPrayer().hasPrayersOn()) {
				if (p2.getPrayer().usingPrayer(1, 18)) {
					sendSoulSplit(hit, p2);
				}
				if (hit.getDamage() == 0) {
					return;
				}
				if (!p2.getPrayer().isBoostedLeech()) {
					if (hit.getLook() == HitLook.MELEE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 19)) {
							p2.getPrayer().setBoostedLeech(true);
							return;
						} else if (p2.getPrayer().usingPrayer(1, 1)) { // sap
							// att
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(0)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
								} else {
									p2.getPrayer().increaseLeechBonus(0);
									p2.getPackets().sendGameMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2214));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2215, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2216));
									}
								}, 1);
								return;
							}
						} else {
							if (p2.getPrayer().usingPrayer(1, 10)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(3)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
									} else {
										p2.getPrayer().increaseLeechBonus(3);
										p2.getPackets().sendGameMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2231, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2232));
										}
									}, 1);
									return;
								}
							}
							if (p2.getPrayer().usingPrayer(1, 14)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(7)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
									} else {
										p2.getPrayer().increaseLeechBonus(7);
										p2.getPackets().sendGameMessage("Your curse drains Strength from the enemy, boosting your Strength.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2248, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2250));
										}
									}, 1);
									return;
								}
							}

						}
					}
					if (hit.getLook() == HitLook.RANGE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 2)) { // sap range
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(1)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
								} else {
									p2.getPrayer().increaseLeechBonus(1);
									p2.getPackets().sendGameMessage("Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2217));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2219));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 11)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(4)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
								} else {
									p2.getPrayer().increaseLeechBonus(4);
									p2.getPackets().sendGameMessage("Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2236, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2238));
									}
								});
								return;
							}
						}
					}
					if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 3)) { // sap mage
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(2)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
								} else {
									p2.getPrayer().increaseLeechBonus(2);
									p2.getPackets().sendGameMessage("Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2220));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2222));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 12)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(5)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
								} else {
									p2.getPrayer().increaseLeechBonus(5);
									p2.getPackets().sendGameMessage("Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2240, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2242));
									}
								}, 1);
								return;
							}
						}
					}

					// overall

					if (p2.getPrayer().usingPrayer(1, 13)) { // leech defence
						if (Utils.getRandom(10) == 0) {
							if (p2.getPrayer().reachedMax(6)) {
								p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
							} else {
								p2.getPrayer().increaseLeechBonus(6);
								p2.getPackets().sendGameMessage("Your curse drains Defence from the enemy, boosting your Defence.", true);
							}
							p2.setNextAnimation(new Animation(12575));
							p2.getPrayer().setBoostedLeech(true);
							World.sendProjectile(p2, this, 2244, 35, 35, 20, 5, 0, 0);
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									setNextGraphics(new Graphics(2246));
								}
							}, 1);
							return;
						}
					}
				}
			}
		}

	}

	@Override
	public void reset() {
		super.reset();
		setDirection(getRespawnDirection());
		combat.reset();
		setBonuses(NPCBonuses.getBonuses(id)); // back to real bonuses
		forceWalk = null;
	}

	@Override
	public void finish() {
		if (hasFinished()) {
			return;
		}
		setFinished(true);
		World.updateEntityRegion(this);
		World.removeNPC(this);
	}

	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600, TimeUnit.MILLISECONDS);
	}

	public void deserialize() {
		if (combat == null) {
			combat = new NPCCombat(this);
		}
		spawn();
	}

	public void spawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

	public NPCCombat getCombat() {
		return combat;
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						((Player) source).getControllerManager().processNPCDeath(getId());
					}
					drop();
					reset();
					setLocation(respawnTile);
					finish();
					if (!isSpawned()) {
						setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void drop() {
		try {
			Player killer = getMostDamageReceivedSourcePlayer();
			if (killer == null) {
				Entity last = getAttackedBy();
				if (last != null && last instanceof Player) {
					killer = (Player) last;
				}
				if (killer == null) {
					return;
				}
			}
			handleDeathReward(killer);
			for (Item item : getItemsToDrop(killer)) {
				if (killer.getInventory().contains(18337)) {
					Bone bone = Bone.forId(item.getId());
					if (bone != null) {
						double xp = bone.getExperience() * 5;
						killer.getSkills().addXp(Skills.PRAYER, xp);
						killer.sendMessage("Your bonecrusher transforms the " + item.getName() + " to " + Utils.format((long) xp) + " prayer experience.");
						continue;
					}
				}
				if (killer.getInventory().containsItem(19675, 1) && item.getDefinitions().getName().toLowerCase().contains("grimy")) {
					if (HerbCleaning.getHerb(item.getId()) != null && killer.getSkills().getLevelForXp(Skills.HERBLORE) >= HerbCleaning.getHerb(item.getId()).getLevel()) {
						double xp = (HerbCleaning.getHerb(item.getId()).getExperience()) * 200;
						killer.sendMessage("Your herbicide transforms the " + item.getName() + " to " + Utils.format((long) xp) + " herblore experience.");
						killer.getSkills().addXp(Skills.HERBLORE, xp);
						continue;
					}
				}
				dropItem(item, killer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	/**
	 * Drops an item for the owner for 3 minutes
	 * 
	 * @param item
	 *            The item
	 * @param owner
	 *            The owner of the item
	 */
	public void dropItem(Item item, Player owner) {
		if (item != null && item.getId() > 0) {
			World.addGroundItem(item, new WorldTile(this), owner, true, 60);
			FileLogger.getFileLogger().writeLog("drops/", getName() + " dropped " + item.getAmount() + "x [itemId=" + item.getId() + ", name=" + item.getName() + "] for " + owner.getDisplayName(), true);
		}
	}

	private void handleDeathReward(Player killer) {
		switch (getId()) {
		case 50:
			killer.getAchievementManager().notifyUpdate(KBDAchievement.class);
			break;
		case 6260:
			killer.getAchievementManager().notifyUpdate(KillBandosAchievement.class);
			break;
		case 4485:
		case 4484:
		case 4483:
			Quest<?> quest = killer.getQuestManager().getProgressedQuest(QuestManager.getQuest(Helpless_Lawgof.class).getName());
			if (quest != null) {
				if (quest.getQuestStage(killer) == Helpless_Lawgof.Stages.KILLING_GOBLIN) {
					killer.setNextWorldTile(TeleportLocations.QUESTING_DOME);
					killer.getQuestManager().finishQuest(quest.getName());
					killer.getDialogueManager().startDialogue(SimpleNPCMessage.class, 208, "Speak to me to receive a reward for your help.");
				}
			}
			break;
		}
		
		if (killer.getSlayerTask() != null && getName().equalsIgnoreCase(killer.getSlayerTask().getName())) {
			killer.getSlayerTask().handleDeath(killer, this);
		}
		
		/** Dropping of clue scrolls */
		if (Utils.percentageChance(3)) {
			int chance = Utils.random(30);
			int scrollId = ClueScrollManager.EASY_SCROLL;
			if (chance <= 3 && getSize() > 2)
				scrollId = ClueScrollManager.ELITE_SCROLL;
			else if (chance <= 10 && getSize() > 1)
				scrollId = ClueScrollManager.HARD_SCROLL;
			else if (chance <= 15 && getSize() > 1)
				scrollId = ClueScrollManager.MEDIUM_SCROLL;
			dropItem(new Item(scrollId, 1), killer);
			return;
		}

		/** Dropping of effigies */
		if (Utils.percentageChance(1) && getCombatLevel() > 30) {
			dropItem(new Item(18778, 1), killer);
			return;
		}
		
		/** Dropping of charms */
		if (Utils.percentageChance(getCharmChance())) {
			dropItem(new Item(NPCDropManager.CHARMS[Utils.random(NPCDropManager.CHARMS.length)], Utils.random(1, getSize() * 2)), killer);
		}
		
		/**
		 * Handles the dropping of caskets, if the npc is your task it will have
		 * a greater chance of dropping caskets
		 */
		int casketChance = getCasketChance();
		if (killer.getSlayerTask() != null) {
			SlayerTask task = killer.getSlayerTask();
			if (killer.getSlayerTask().equals(this)) {
				casketChance += (task.getType() == Type.ELITE ? 30 : task.getType() == Type.HARD ? 20 : task.getType() == Type.MEDIUM ? 10 : 5);
			}
			if (killer.isDonator())
				casketChance += 10;
		}
		if (Utils.percentageChance(casketChance)) {
			dropItem(new Item(Caskets.REGULAR.getItemId(), 1), killer);
		}
		/** Casket drops complete */
	}
	
	public int getCasketChance() {
		int combatLevel = getCombatLevel();
		if (combatLevel < 5) 
			return 5;
		else if (combatLevel >= 30 && combatLevel < 50)
			return 8;
		else if (combatLevel >= 50 && combatLevel < 70)
			return 10;
		else if (combatLevel >= 70 && combatLevel < 100)
			return 15;
		else if (combatLevel > 100)
			return 20;
		return 10;
	}
	
	public int getCharmChance() {
		int combatLevel = getCombatLevel();
		if (combatLevel < 30) 
			return 10;
		else if (combatLevel >= 30 && combatLevel < 50)
			return 20;
		else if (combatLevel >= 50 && combatLevel < 70)
			return 30;
		else if (combatLevel >= 70 && combatLevel < 100)
			return 40;
		else if (combatLevel > 100)
			return 50;
		return 0;
	}
 
	public void sendDrop(Player player, Drop drop) {
		int size = getSize();
		World.addGroundItem(new Item(drop.getItemId(), drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount())), new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()), player, true, 60);
	}

	@Override
	public int getSize() {
		return getDefinitions().size;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	public int[] getBonuses() {
		return bonuses;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public WorldTile getRespawnTile() {
		return respawnTile;
	}

	public boolean isUnderCombat() {
		return combat.underCombat();
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar)) {
			lastAttackedByTarget = Utils.currentTimeMillis();
		}
	}

	public boolean canBeAttackedByAutoRelatie() {
		return Utils.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) {
			return;
		}
		combat.setTarget(entity);
		lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void removeTarget() {
		if (combat.getTarget() == null) {
			return;
		}
		combat.removeTarget();
	}

	private List<Item> getItemsToDrop(Player killer) {
		List<Item> items = new ArrayList<Item>();
		List<Drop> drops = NPCDropManager.getDrops(getName());

		if (drops == null || drops.size() == 0) {
			if (Constants.DEBUG) {
				System.out.println(getName() + " has no drops");
			}
			return items;
		}
		ListIterator<Drop> it = drops.listIterator();
		Drop[] possibleDrops = new Drop[drops.size()];
		int possibleDropsCount = 0;
		boolean equippingROW = killer.getEquipment().getRingId() == 2572;
		double dropRate = killer.getFacade().getModifiers() == null ? 1 : killer.getFacade().getModifiers()[Facade.LOOT_MODIFIER_INDEX];
		double multiplier = dropRate < 1 ? 1 - dropRate : dropRate == 1 ? 1 : 1 + (dropRate / 100);
		while (it.hasNext()) {
			Drop drop = it.next();
			if (drop.getRate() == Chance.ALWAYS) {
				items.add(new Item(drop.getItemId()));
			} else {
				double chance = Utils.getRandomDouble(100);;
				if (equippingROW && killer.getFacade().getRowCharges() > 0) {
					chance = Utils.getRandomDouble(80);
				}
				double modif = (double) chance / (double) multiplier;
				double dropChance = drop.getRate().getChance();
				chance = modif;
				if (chance <= dropChance) {
					possibleDrops[possibleDropsCount++] = drop;
				}
			}
		}
		if (possibleDropsCount > 0) {
			Drop drop = possibleDrops[Utils.getRandom(possibleDropsCount - 1)];
			Item item = new Item(drop.getItemId(), Utils.random(drop.getMinAmount(), drop.getMaxAmount()));
			if (item.getAmount() > 1) {
				if (!item.getDefinitions().isStackable() && !item.getDefinitions().isNoted() && item.getDefinitions().getCertId() != -1) {
					item.setId(item.getDefinitions().getCertId());
				}
			}
			items.add(item);
			if (equippingROW && killer.getFacade().getRowCharges() > 0) {
				killer.getFacade().setRowCharges(killer.getFacade().getRowCharges() <= 1 ? 0 : killer.getFacade().getRowCharges() - 1);
				if (killer.getFacade().getRowCharges() > 0) {
					killer.sendMessage("You now have " + killer.getFacade().getRowCharges() + " ring of wealth charges left.");
				}
			}
			if (equippingROW && killer.getFacade().getRowCharges() <= 0) {
				killer.sendMessage("You have a ring of wealth equipped with 0 charges. Charge it with Max at the home portal!");
			}
		}
		return items;
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public void setForceWalk(WorldTile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
		int size = getSize();
		int agroRatio = getCombatDefinitions().getAgressivenessType();
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player == null || player.isDead() || player.hasFinished() || !player.isRunning() || player.getAppearence().isHidden() || !Utils.isInRange(getX(), getY(), size, player.getX(), player.getY(), player.getSize(), forceTargetDistance > 0 ? forceTargetDistance : agroRatio) || (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea()) && (player.getAttackedBy() != this && (player.getAttackedByDelay() > Utils.currentTimeMillis() || player.getFindTargetDelay() > Utils.currentTimeMillis()))) || !clipedProjectile(player, false) || (!forceAgressive && !Wilderness.isAtWild(this) && player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2)) {
							continue;
						}
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == null || npc == this || npc.isDead() || npc.hasFinished() || !Utils.isInRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(), forceTargetDistance > 0 ? forceTargetDistance : agroRatio) || !npc.getDefinitions().hasAttackOption() || ((!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this && npc.getAttackedByDelay() > Utils.currentTimeMillis()) || !clipedProjectile(npc, false)) {
							continue;
						}
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ArrayList<Entity> getPossibleTargets() {
		return getPossibleTargets(false, true);
	}

	public boolean checkAgressivity() {
		// if(!(Wilderness.isAtWild(this) &&
		// getDefinitions().hasAttackOption())) {
		if (!forceAgressive) {
			NPCCombatDefinitions defs = getCombatDefinitions();
			if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE) {
				return false;
			}
		}
		// }
		ArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.getRandom(possibleTarget.size() - 1));
			setTarget(target);
			target.setAttackedBy(target);
			target.setFindTargetDelay(Utils.currentTimeMillis() + 10000);
			return true;
		}
		return false;
	}

	public boolean isCantInteract() {
		return cantInteract;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract) {
			combat.reset();
		}
	}

	public int getCapDamage() {
		return capDamage;
	}

	public void setCapDamage(int capDamage) {
		this.capDamage = capDamage;
	}

	public int getLureDelay() {
		return lureDelay;
	}

	public void setLureDelay(int lureDelay) {
		this.lureDelay = lureDelay;
	}

	public boolean isCantFollowUnderCombat() {
		return cantFollowUnderCombat;
	}

	public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
		this.cantFollowUnderCombat = canFollowUnderCombat;
	}

	public Transformation getNextTransformation() {
		return nextTransformation;
	}

	@Override
	public String toString() {
		return "NPC[index=" + getIndex() + ", id=" + getId() + ", name=" + getName() + ", regionId=" + getRegionId() + "]";
	}

	public boolean isForceAgressive() {
		return forceAgressive;
	}

	public void setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
	}

	public int getForceTargetDistance() {
		return forceTargetDistance;
	}

	public void setForceTargetDistance(int forceTargetDistance) {
		this.forceTargetDistance = forceTargetDistance;
	}

	public boolean isForceFollowClose() {
		return forceFollowClose;
	}

	public void setForceFollowClose(boolean forceFollowClose) {
		this.forceFollowClose = forceFollowClose;
	}

	public boolean isForceMultiAttacked() {
		return forceMultiAttacked;
	}

	public void setForceMultiAttacked(boolean forceMultiAttacked) {
		this.forceMultiAttacked = forceMultiAttacked;
	}

	public boolean hasRandomWalk() {
		return randomwalk;
	}

	public void setRandomWalk(boolean forceRandomWalk) {
		this.randomwalk = forceRandomWalk;
	}

	public String getCustomName() {
		return name;
	}

	public void setName(String string) {
		this.name = getDefinitions().getName().equals(string) ? null : string;
		changedName = true;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public int getCombatLevel() {
		return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
	}

	public String getName() {
		return name != null ? name : getDefinitions().getName();
	}

	public void setCombatLevel(int level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public boolean hasChangedName() {
		return changedName;
	}

	public boolean hasChangedCombatLevel() {
		return changedCombatLevel;
	}

	public WorldTile getMiddleWorldTile() {
		int size = getSize();
		return new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
	}

	/**
	 * @return the spawned
	 */
	public boolean isSpawned() {
		return spawned;
	}

	/**
	 * @param spawned
	 *            the spawned to set
	 */
	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	/**
	 * @return the walkType
	 */
	public int getWalkType() {
		return walkType;
	}

	/**
	 * @param walkType
	 *            the walkType to set
	 */
	public void setWalkType(int walkType) {
		this.walkType = walkType;
	}

	/**
	 * @return the startTile
	 */
	public WorldTile getStartTile() {
		return startTile;
	}

	/**
	 * @param startTile
	 *            the startTile to set
	 */
	public void setStartTile(WorldTile startTile) {
		this.startTile = startTile;
	}

	/**
	 * @param bonuses
	 *            the bonuses to set
	 */
	public void setBonuses(int[] bonuses) {
		this.bonuses = bonuses;
	}

	public boolean isIntelligentRouteFinder() {
		return intelligentRouteFinder;
	}

	public void setIntelligentRouteFinder(boolean intelligentRouteFinder) {
		this.intelligentRouteFinder = intelligentRouteFinder;
	}

	/**
	 * @param customName
	 *            the customName to set
	 */
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public boolean withinDistance(Player tile, int distance) {
		return super.withinDistance(tile, distance);
	}

	public SecondaryBar getNextSecondaryBar() {
		return nextSecondaryBar;
	}

	public void setNextSecondaryBar(SecondaryBar secondaryBar) {
		this.nextSecondaryBar = secondaryBar;
	}
}
