package novite.rs.game.minigames.stealingcreation;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceMovement;
import novite.rs.game.Graphics;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Equipment;
import novite.rs.game.player.Inventory;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.utility.Utils;

public class StealingCreationController extends Controller {

	private StealingCreationGame game;
	private boolean team;

	@Override
	public void start() {
		game = (StealingCreationGame) getArguments()[0];
		team = (Boolean) getArguments()[1];
		setArguments(null);
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		player.setLocation(Helper.EXIT);
		Helper.reset(player);
		return true;
	}

	@Override
	public boolean login() {
		getPlayer().getPackets().sendGameMessage("How did you manage to remain here eh? REPORTED!!");
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setOverlay(809, false);
		player.getPackets().sendGlobalConfig(558, (int) ((game.getEndTime() - Utils.currentTimeMillis()) / 600)); // sync
		// time
		player.getVarsManager().sendVarBit(5493, getTeam() ? 2 : 1); // unlock
		// portal
		// opts
	}

	public void sendScore(Score score) {
		player.getVarsManager().sendVar(1332, score.getGathering());
		player.getVarsManager().sendVar(1333, score.getDepositing());
		player.getVarsManager().sendVar(1334, score.getProcessing());
		player.getVarsManager().sendVar(1335, score.getWithdrawing());
		player.getVarsManager().sendVar(1337, score.getDamaging());
	}

	@Override
	public void moved() {
		GameArea area = game.getArea();
		if (area.getFlags() != null) {
			int flagX = player.getChunkX() - (area.getMinX() >> 3);
			int flagY = player.getChunkY() - (area.getMinY() >> 3);
			if (flagX >= 8 || flagY >= 8 || flagX < 0 || flagY < 0) {
				return;
			}
			if (area.getType(flagX, flagY) == 5) {
				if (Helper.withinArea2(player, area, flagX, flagY, new int[] { 1, 1, 4, 4 })) {
					if (!player.getAppearence().isNPC()) {
						player.getAppearence().transformIntoNPC(1957);
						player.getAppearence().setHidden(true);
						player.setRunHidden(false);
						player.getNextHits().clear();
					}
				} else {
					resetFOG();
				}
			} else {
				resetFOG();
			}
		}
	}

	private void resetFOG() {
		if (player.getAppearence().isNPC()) {
			player.getAppearence().transformIntoNPC(-1);
			player.getAppearence().setHidden(false);
			if (!player.getRun()) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						player.setRunHidden(true);
					}
				});
			}
		}
	}

	@Override
	public void process() {
		if (game == null || (getPlayer().getX() < game.getArea().getMinX() || getPlayer().getX() > game.getArea().getMaxX() || player.getY() < game.getArea().getMinY() || player.getY() > game.getArea().getMaxY())) {
			getPlayer().getPackets().sendGameMessage("An error has occured, please submit bug report.");
			player.getControllerManager().forceStop();
			return;
		}
	}

	@Override
	public boolean canEquip(int slot, int item) {
		if (slot == Equipment.SLOT_CAPE) {
			player.getPackets().sendGameMessage("You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof Player) {
			Player playerTarget = (Player) target;
			if (playerTarget.getEquipment().getCapeId() == player.getEquipment().getCapeId() || Helper.withinSafeArea(playerTarget, game.getArea(), !getTeam())) {
				return false;
			}
		} else if (target instanceof Familiar) {
			Familiar familiar = (Familiar) target;
			Player owner = familiar.getOwner();
			if (owner.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player playerTarget = (Player) target;
			if (playerTarget.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
				player.getPackets().sendGameMessage("You cannot attack player's on the same team!");
				return false;
			} else if (target.getTemporaryAttributtes().get("in_kiln") != null && (Long) target.getTemporaryAttributtes().get("in_kiln") >= Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("The power of the creation kiln is protecting the player.");
				return false;
			}
		} else if (target instanceof Familiar) {
			Familiar familiar = (Familiar) target;
			Player owner = familiar.getOwner();
			if (owner.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
				player.getPackets().sendGameMessage("You cannot attack a familiar on the same side as you!");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processItemOnPlayer(Player target, Item item) {
		if (player.withinDistance(target, 3)) {
			if (target.isDead() || player.isDead()) {
				return false;
			} else if (target.getEquipment().getCapeId() != player.getEquipment().getCapeId()) {
				player.getPackets().sendGameMessage("You cannot give an item to a player on the opposite team!");
				return false;
			} else if (!target.getFacade().isAcceptingAid()) {
				player.getPackets().sendGameMessage("That player currently does not want your aid.");
				return false;
			} else {
				if (target.getInventory().addItem(item)) {
					player.getInventory().deleteItem(item);
					target.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(player.getDisplayName()) + " has given you an item.");
					return false;
				} else {
					player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(player.getDisplayName()) + " has insufficient room in their inventory.");
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (object.getId() == 39533) {
			game.sendItemToBase(player, item, getTeam(), false, false);
			return true;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC n) {
		for (int element : Helper.MANAGER_NPCS) {
			if (n.getId() == element) {
				n.setNextFaceEntity(player);
				player.getDialogueManager().startDialogue("StealingCreationManagerD", n, getGame());
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC n) {
		for (int element : Helper.MANAGER_NPCS) {
			if (n.getId() == element) {
				n.setNextFaceEntity(player);
				Helper.displayClayStatus(game.getArea(), player);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canPlayerOption2(Player target) {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile tile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean canPlayerOption4(Player target) {
		if (target.getEquipment().getCapeId() != player.getEquipment().getCapeId()) {
			player.getPackets().sendGameMessage("You cannot give an item to a player on the opposite team!");
			return false;
		} else if (!target.getFacade().isAcceptingAid()) {
			player.getPackets().sendGameMessage("That player currently does not want your aid.");
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean canPlayerOption3(final Player target) {
		final int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
		Long PICKPOCK_DELAY = (Long) player.getTemporaryAttributtes().get("PICKPOCK_DELAY");
		if (PICKPOCK_DELAY != null && PICKPOCK_DELAY + 1500 > Utils.currentTimeMillis()) {
			return false;
		} else if (Helper.withinSafeArea(target, game.getArea(), !getTeam()) || Helper.withinSafeArea(player, game.getArea(), getTeam())) {
			return false;
		} else if (player.getAttackedBy() != null && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You can't do this while you're under combat.");
			return false;
		} else if (target.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
			player.getPackets().sendGameMessage("You cannot pickpocket player that is on the same team!");
			return false;
		} else if (target.getTemporaryAttributtes().get("in_kiln") != null && (Long) target.getTemporaryAttributtes().get("in_kiln") >= Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("The power of the creation kiln is protecting the player.");
			return false;
		} else if (target.getSkills().getLevel(Skills.THIEVING) - thievingLevel >= 20) {
			player.getPackets().sendGameMessage("You need a theiving level of at least " + (target.getSkills().getLevel(Skills.THIEVING) - 20) + " to pickpocket " + Utils.formatPlayerNameForDisplay(target.getDisplayName() + "."));
			return false;
		} else if (target.getInventory().getFreeSlots() == 28) {
			player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(target.getDisplayName() + " appears to have nothing in his pockets."));
			return false;
		} else if (player.getInventory().getFreeSlots() == 0) {
			player.getPackets().sendGameMessage("You don't have enough space in your inventory to steal from your target.");
			return false;
		} else if (target.isDead()) {
			player.getPackets().sendGameMessage("Too late.");
			return false;
		}
		player.setNextFaceEntity(target);
		player.setNextAnimation(new Animation(881));
		player.getPackets().sendGameMessage("You attempt to pickpocket from " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pockets.");
		player.getPackets().sendGameMessage("You pick " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pocket.");
		player.getTemporaryAttributtes().put("PICKPOCK_DELAY", Utils.currentTimeMillis());
		int level = Utils.getRandom(thievingLevel);
		double ratio = level / (Utils.random(target.getSkills().getLevel(Skills.THIEVING)) + 6);
		if (!(Math.round(ratio * thievingLevel) > target.getSkills().getLevel(Skills.THIEVING))) {
			player.getPackets().sendGameMessage("You fail to pickpocket " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + ".");
		} else {
			Item caughtItem = getCalculatedItem(target);
			itemLoop:
				for (int i = 0; i < 100; i++) {
					if (caughtItem == null) {
						caughtItem = getCalculatedItem(target);
					} else {
						if (player.getInventory().addItem(caughtItem)) {
							target.getInventory().deleteItem(caughtItem);
						}
						break itemLoop;
					}
				}
			player.getPackets().sendGameMessage("You sucessfully pickpocket an item from " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pockets!");
		}
		return false;
	}

	public Item getCalculatedItem(Player target) {
		return target.getInventory().getItem(Utils.random(target.getInventory().getItemsContainerSize()));
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof Player) {
			Player playerTarget = (Player) target;
			if (playerTarget.getAppearence().isNPC()) {
				player.getPackets().sendGameMessage("Your target is nowhere to be found.");
				return false;
			} else if (Helper.withinSafeArea(playerTarget, game.getArea(), !getTeam()) || Helper.withinSafeArea(player, game.getArea(), getTeam())) {
				return false;
			}
		}
		if (player.getAppearence().isNPC()) {
			player.getPackets().sendGameMessage("You cannot attack while you are hidden.");
			return false;
		}
		return true;
	}

	@Override
	public void trackXP(int skillId, int addedXp) {
		if (skillId == 3) {
			Score score = game.getScore(player);
			if (score == null) {
				return;
			}
			score.updateDamaging((int) (addedXp * 7.2148148148148148148148148148148));
			sendScore(score);
		}
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		Score score = game.getScore(player);
		String name = item.getName().toLowerCase();
		int nameIndex = name.indexOf("(class");
		int clayQuality = 1;
		if (nameIndex != -1) {
			clayQuality = name.contains("potion") ? 1 : Integer.parseInt(name.substring(nameIndex).replace("(class ", "").replace(")", ""));
		}
		int pointsSubtracted = 2 * (((item.getDefinitions().isStackable() ? 1 : 15) * clayQuality) * item.getAmount());
		if (score.getWithdrawing() - pointsSubtracted <= -3000) {
			player.getPackets().sendGameMessage("You cannot take this amount of items as your score is too low.");
			return false;
		} else if (!item.hasOwner()) {
			score.updateWithdrawing(pointsSubtracted);
			sendScore(score);
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (interfaceId == 813) {
			if (componentId >= 37 && componentId <= 71) {
				processKilnExchange(componentId, packetId);
			} else if (componentId >= 99 && componentId <= 107) {
				int index = (componentId - 99) / 2;
				if (player.getInventory().containsItem(Helper.SACRED_CLAY[index], 1)) {
					player.getTemporaryAttributtes().put("sc_kiln_quality", index);
					Helper.refreshKiln(player);
				}
			}
			return true;
		} else if (interfaceId == 387) {
			if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET && componentId == 9) {
				player.getPackets().sendGameMessage("You can't remove your team's colours.");
				return false;
			}
		} else if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null) {
				String itemName = item.getName().toLowerCase();
				if (itemName.contains("food (class")) {
					doFoodEffect(item, Integer.parseInt(item.getName().substring(item.getName().indexOf("(class")).replace("(class ", "").replace(")", "")));
					return false;
				} else if (itemName.contains("potion (") || itemName.contains("super")) {
					boolean superPotion = itemName.contains("super");
					int index = 0;
					for (String name : Skills.SKILL_NAME) {
						int doses = Integer.parseInt(itemName.substring(itemName.indexOf("(")).replace("(", "").replace(")", ""));
						String skill = superPotion ? item.getName().toLowerCase().replace("super ", "").replace(" (" + doses + ")", "") : item.getName().toLowerCase().replace(" potion (" + doses + ")", "");
						if (!name.toLowerCase().equals(skill)) {
							index++;
							continue;
						}
						player.getPackets().sendGameMessage("You drink a dose of the " + item.getName().toLowerCase().replace("(" + doses + ")", "") + ".");
						if (doses == 1) {
							player.getInventory().deleteItem(item);
							player.getPackets().sendGameMessage("The glass shatters as you drink the last dose of the potion.");
						} else {
							player.getInventory().getItems().set(slotId, new Item(item.getId() + 2, 1));
							player.getInventory().refresh(slotId);
						}
						int actualLevel = player.getSkills().getLevel(index);
						int realLevel = player.getSkills().getLevelForXp(index);
						if (!skill.equals("prayer")) {
							int level = actualLevel > realLevel ? realLevel : actualLevel;
							player.getSkills().set(index, level + (superPotion ? 7 : 4));
						} else {
							player.getPrayer().restorePrayer((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER) * .5 + (superPotion ? 250 : 200))));
						}
						player.setNextAnimation(new Animation(829));
						player.getPackets().sendSound(4580, 0, 1);
					}
					return false;
				}
			}
		}
		return true;
	}

	private void doFoodEffect(Item item, int itemTier) {
		if (player.getFoodDelay() > Utils.currentTimeMillis()) {
			return;
		}
		player.setNextAnimation(new Animation(829));
		player.addFoodDelay(1800);
		player.getPackets().sendGameMessage("You eat the food.");
		int hp = player.getHitpoints();
		player.heal(40 * itemTier);
		if (player.getHitpoints() > hp) {
			player.getPackets().sendGameMessage("It heals some health.");
		}
		player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() + 3);
		player.getInventory().deleteItem(item);
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		final GameArea area = game.getArea();
		final int flagX = player.getChunkX() - (area.getMinX() >> 3);
		final int flagY = player.getChunkY() - (area.getMinY() >> 3);

		boolean isEnemySCGate = false;
		boolean isEnemySCWall = false;

		gateLoop:
			for (int[] wallIDS : (getTeam() ? Helper.BLUE_BARRIER_GATES : Helper.RED_BARRIER_GATES)) {
				for (int id : wallIDS) {
					if (object.getId() == id) {
						isEnemySCGate = true;
						break gateLoop;
					}
				}
			}
		wallLoop:
			for (int[] wallIDS : (getTeam() ? Helper.BLUE_BARRIER_WALLS : Helper.RED_BARRIER_WALLS)) {
				for (int id : wallIDS) {
					if (object.getId() == id) {
						isEnemySCWall = true;
						break wallLoop;
					}
				}
			}

			if (object.getId() == Helper.KILN) {
				Helper.displayKiln(player);
				return false;
			} else if (object.getId() == 39533) {
				for (Item item : player.getInventory().getItems().getItems()) {
					if (item == null) {
						continue;
					}
					game.sendItemToBase(player, item, getTeam(), false, true);
				}
				return false;
			} else if ((!getTeam() && (object.getId() == Helper.BLUE_DOOR_1 || object.getId() == Helper.BLUE_DOOR_2)) || (getTeam() && (object.getId() == Helper.RED_DOOR_1 || object.getId() == Helper.RED_DOOR_2))) {
				passWall(player, object, getTeam());
				return false;
			} else if (isEnemySCGate || isEnemySCWall) {
				final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
				final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
				final int weaponId = player.getEquipment().getWeaponId();
				final int attackStyle = player.getCombatDefinitions().getAttackStyle();
				final int combatDelay = PlayerCombat.getMeleeCombatDelay(player, weaponId);
				if (player.getActionManager().getAction() != null || player.getActionManager().getActionDelay() > 0) {
					return false;
				}
				player.getActionManager().addActionDelay(combatDelay);
				player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(weaponId, attackStyle)));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						game.damageBarrier(x, y);
					}
				});
				return false;
			} else if (object.getId() == Helper.PRAYER_ALTAR) {
				boolean runEnergy = Utils.getRandom(1) == 0;
				if (runEnergy) {
					player.setRunEnergy(100);
				}
				return !runEnergy;
			} else if (object.getId() >= 39534 && object.getId() <= 39545) {
				player.getTemporaryAttributtes().put("sc_object", object);
				if (object.getId() == 39541) {
					player.getDialogueManager().startDialogue("StealingCreationMagic");
				} else if (object.getId() == 39539) {
					player.getDialogueManager().startDialogue("StealingCreationRange");
				} else if (object.getId() == 39534) {
					player.getDialogueManager().startDialogue("StealingCreationClay");
				}
				return false;
			} else if (object.getId() == 39602 || object.getId() == 39613 || object.getId() == 39612 || object.getId() == 39611) {
				boolean isWall = object.getId() == 39613 || object.getId() == 39612 || object.getId() == 39611;
				if (isWall) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 60) {
						player.getPackets().sendGameMessage("You need to have an Agility level of 60 to clim over the wall.");
						return false;
					}
				}
				int rotation = object.getRotation();
				int xExtra = 0, yExtra = 0, direction = 0, totalDistance = isWall ? 2 : 3;
				int DX = object.getX() - player.getX();
				int DY = object.getY() - player.getY();
				if (!isWall && (rotation == 1 || rotation == 3) || isWall && (rotation == 0 || rotation == 2)) {
					if (DX >= 0) {
						xExtra += totalDistance;
						direction = ForceMovement.EAST;
					} else if (DX < 0) {
						xExtra -= totalDistance;
						direction = ForceMovement.WEST;
					}
				} else {
					if (DY >= 0) {
						yExtra += totalDistance;
						direction = ForceMovement.NORTH;
					} else if (DY < 0) {
						yExtra -= totalDistance;
						direction = ForceMovement.SOUTH;
					}
				}
				final WorldTile toTile = new WorldTile(player.getX() + xExtra, player.getY() + yExtra, player.getPlane());
				ForceMovement nextForceMovement;
				if (isWall) {
					nextForceMovement = new ForceMovement(toTile, 2, direction);
				} else {
					nextForceMovement = new ForceMovement(player, 0, toTile, 2, direction);
				}
				player.setNextForceMovement(nextForceMovement);
				player.setNextAnimation(new Animation(object.getId() == 39602 ? 6132 : 10590));
				final int finalDirection = direction;
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						player.setDirection(finalDirection);
						player.setNextWorldTile(toTile);
					}
				}, 1);
			} else {
				final String name = object.getDefinitions().name.toLowerCase();
				final int clayQuality = Integer.parseInt(name.substring(name.indexOf("(class")).replace("(class ", "").replace(")", "")) - 1;
				final CreationChunks skill = CreationChunks.valueOf(name.replace(" (class " + (clayQuality + 1) + ")", "").toUpperCase());

				player.getActionManager().setAction(new Action() {

					Item bestItem;
					int itemTier;

					@Override
					public boolean start(Player player) {
						if (name.contains(("empty"))) {
							return false;
						} else if (player.getSkills().getLevel(skill.getRequestedSkill()) < clayQuality * 20) {
							player.getPackets().sendGameMessage("You need a " + Skills.SKILL_NAME[skill.getRequestedSkill()] + " level of " + clayQuality * 20 + " to collect this level of clay.");
							return false;
						}
						itemLoop:
							for (int index = 4; index >= 0; index--) {
								int baseItem = skill.getBaseItem();
								if (baseItem == -1) {
									break itemLoop;
								}
								Item bestItem = new Item(baseItem + (index * 2), 1);
								if (player.getEquipment().getWeaponId() == bestItem.getId() || player.getInventory().containsItem(bestItem.getId(), bestItem.getAmount())) {
									if (player.getSkills().getLevel(skill.getRequestedSkill()) >= index * 20) {
										this.bestItem = bestItem;
										this.itemTier = index;
										break itemLoop;
									}
								}
							}
						setActionDelay(player, getActionDelay());
						return true;
					}

					@Override
					public boolean process(Player player) {
						if (game.isEmpty(flagX, flagY) || player.getInventory().getFreeSlots() == 0) {
							return false;
						}
						player.setNextAnimation(bestItem != null ? new Animation(skill.getBaseAnimation() + itemTier) : new Animation(10602));
						player.setNextFaceWorldTile(object);
						return true;
					}

					@Override
					public int processWithDelay(Player player) {
						Score score = game.getScore(player);
						if (score == null) {
							return -1;
						}
						if (Utils.getRandom(clayQuality + 1) == 0) {
							game.useSkillPlot(flagX, flagY);
						}
						player.getInventory().addItem(new Item(Helper.SACRED_CLAY[clayQuality], 1));
						score.updateGathering(15 * (clayQuality + 1));
						sendScore(score);
						return getActionDelay();
					}

					private int getActionDelay() {
						if (clayQuality == 0) {
							return 2;
						}
						int baseTime = Helper.OBJECT_TIERS[clayQuality];
						int mineTimer = baseTime - player.getSkills().getLevel(skill.getRequestedSkill()) - (bestItem == null ? 1 : Helper.TOOL_TIERS[itemTier]);
						if (mineTimer < 2) {
							mineTimer = 2;
						}
						return mineTimer;
					}

					@Override
					public void stop(Player player) {
						setActionDelay(player, 3);
					}
				});
			}
			return false;
	}

	enum CreationChunks {

		FRAGMENTS(
		10602,
		-1,
		Skills.HUNTER),

		TREE(
		10603,
		14132,
		Skills.WOODCUTTING),

		ROCK(
		10608,
		14122,
		Skills.MINING),

		POOL(
		10613,
		14142,
		Skills.FISHING),

		SWARM(
		10618,
		14152,
		Skills.HUNTER);

		private int baseAnimation, baseItem, skillRequested;

		private CreationChunks(int baseAnimation, int baseItem, int skillRequested) {
			this.baseAnimation = baseAnimation;
			this.baseItem = baseItem;
			this.skillRequested = skillRequested;
		}

		public int getBaseAnimation() {
			return baseAnimation;
		}

		public int getBaseItem() {
			return baseItem;
		}

		public int getRequestedSkill() {
			return skillRequested;
		}
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		boolean isFriendlySCGate = false;

		gateLoop:
			for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_GATES : Helper.BLUE_BARRIER_GATES)) {
				for (int id : gateIDS) {
					if (object.getId() == id) {
						isFriendlySCGate = true;
						break gateLoop;
					}
				}
			}

		if (object.getId() == Helper.EMPTY_BARRIER1 || object.getId() == Helper.EMPTY_BARRIER2 || object.getId() == Helper.EMPTY_BARRIER3) {
			boolean redTeam = getTeam();
			int tier = -1;
			for (int i = 4; i >= 0; i--) {
				if (player.getInventory().containsItem(Helper.BARRIER_ITEMS[i], 4)) {
					tier = i;
					break;
				}
			}
			if (tier == -1) {
				player.getPackets().sendGameMessage("You don't have enough barrier items to build.");
				return false;
			}
			final int t = tier;
			final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
			final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
			for (Player otherPlayer : redTeam ? game.getBlueTeam() : game.getRedTeam()) {
				if (otherPlayer == null || !otherPlayer.withinDistance(object, 6)) {
					continue;
				}
				if (Helper.withinArea(otherPlayer, game.getArea(), x, y, new int[] { 2, 2 })) {
					player.getPackets().sendGameMessage("You cannot build a barrier while players from the other team are near the pallet.");
					return false;
				}
				otherPlayer.resetWalkSteps();
				otherPlayer.lock(3);
			}
			player.lock(2);
			WorldTasksManager.schedule(new WorldTask() {
				private int step = 0;

				@Override
				public void run() {
					if (step == 0) {
						player.setNextAnimation(new Animation(10589));
						step++;
					} else if (step == 1) {
						if (player.getInventory().removeItems(new Item(Helper.BARRIER_ITEMS[t], 4)) && !game.buildBarrier(getTeam(), t + 1, x, y)) {
							player.getInventory().addItem(new Item(Helper.BARRIER_ITEMS[t], 4));
						}
						player.unlock();
						stop();
					}
				}

			}, 0, 0);
			return false;
		} else if (isFriendlySCGate) {
			passWall(player, object, getTeam());
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		boolean isFriendlySCGate = false;
		boolean isFriendlySCWall = false;

		gateLoop:
			for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_GATES : Helper.BLUE_BARRIER_GATES)) {
				for (int id : gateIDS) {
					if (object.getId() == id) {
						isFriendlySCGate = true;
						break gateLoop;
					}
				}
			}
		wallLoop:
			for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_WALLS : Helper.BLUE_BARRIER_WALLS)) {
				for (int id : gateIDS) {
					if (object.getId() == id) {
						isFriendlySCWall = true;
						break wallLoop;
					}
				}
			}

			if (isFriendlySCGate || isFriendlySCWall) {
				final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
				final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
				synchronized (game.getLock()) {
					final int team = game.getArea().getWallTeam(x, y);
					final int tier = game.getArea().getWallTier(x, y);
					final int health = game.getArea().getWallStatus(x, y);
					if (team != (getTeam() ? 2 : 1)) {
						return false;
					}
					if (health <= 0 || health >= (tier * 4)) {
						player.getPackets().sendGameMessage("This barrier doesn't need any repairing.");
						return false;
					}
					if (!player.getInventory().containsItem(Helper.BARRIER_ITEMS[tier - 1], 1)) {
						player.getPackets().sendGameMessage("You don't have enough barriers of required type to repair this barrier.");
						return false;
					}
					player.lock(2);
					WorldTasksManager.schedule(new WorldTask() {
						private int step = 0;

						@Override
						public void run() {
							if (step == 0) {
								player.setNextAnimation(new Animation(10589));
								step++;
							} else if (step == 1) {
								if (player.getInventory().removeItems(new Item(Helper.BARRIER_ITEMS[tier - 1], 1)) && !game.repairBarrier(x, y)) {
									player.getInventory().addItem(new Item(Helper.BARRIER_ITEMS[tier - 1], 1));
								}
								player.unlock();
								stop();
							}
						}

					}, 0, 0);
				}

				return false;
			}
			return true;
	}

	public static void passWall(Player player, WorldObject object, final boolean red) {
		if (player.isFrozen()) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from moving.");
			return;
		}
		player.lock(3);
		if (!Helper.setWalkToGate(object, player)) {
			player.unlock();
		}
		final Player p = player;
		final WorldObject o = object;
		WorldTasksManager.schedule(new WorldTask() {
			private int step = 0;

			@Override
			public void run() {
				if (step == 0 && !Helper.isAtGate(o, p)) {
					if (!p.hasWalkSteps() && p.getNextWalkDirection() == -1) {
						// unstuck
						stop();
						p.unlock();
					}
					return;
				}
				if (step == 0) {
					WorldTile fromTile = new WorldTile(p.getX(), p.getY(), p.getPlane());
					WorldTile faceTile = Helper.getFaceTile(o, p);
					p.getPackets().sendGameMessage("You pass through the barrier.");
					p.setNextWorldTile(faceTile);
					p.setNextForceMovement(new ForceMovement(fromTile, 0, faceTile, 1, Helper.getFaceDirection(faceTile, p)));
					p.setNextAnimation(new Animation(10584));
					p.setNextGraphics(new Graphics(red ? 1871 : 1870));
					step++;
				} else if (step == 1) {
					stop();
					p.unlock();
				}
			}
		}, 0, 0);
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					if (player.getFamiliar() != null) {
						player.getFamiliar().sendDeath(player);
					}
				} else if (loop == 3) {
					Score score = game.getScore(player);
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						Score killerScore = game.getScore(killer);
						killerScore.updateKilled(1);
						killer.removeDamage(player);
						killer.getPackets().sendGameMessage("You have killed " + player.getDisplayName() + ", you now have " + killerScore.getKilled() + " kills.");
						player.getPackets().sendGameMessage("You have been killed by " + killer.getDisplayName());
					}
					player.getEquipment().getItems().set(Equipment.SLOT_CAPE, null);
					player.sendItemsOnDeath(killer, true);
					player.setNextWorldTile(Helper.getNearestRespawnPoint(player, game.getArea(), getTeam()));
					player.stopAll();
					player.reset();
					score.updateDied(1);
					sendScore(score);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					Helper.giveCape(player, getTeam());
					player.getPackets().sendMusicEffect(90);
					player.resetWalkSteps();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void processKilnExchange(int componentId, int packetId) {
		int quality = player.getTemporaryAttributtes().get("sc_kiln_quality") != null ? (int) player.getTemporaryAttributtes().get("sc_kiln_quality") : 0;
		int clayId = Helper.SACRED_CLAY[quality];
		int amount = 0;
		if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
			amount = 1;
		} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
			amount = 5;
		} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
			player.getTemporaryAttributtes().put("sc_component", componentId);
			player.getTemporaryAttributtes().put("kilnX", true);
			player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
		} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
			amount = player.getInventory().getNumberOf(clayId);
		} else {
			amount = (int) player.getTemporaryAttributtes().get("sc_amount_making");
		}
		if (Helper.checkSkillRequriments(player, Helper.getRequestedKilnSkill(componentId - 37), quality)) {
			if ((amount != 0 && Helper.proccessKilnItems(player, componentId, quality, clayId, amount))) {
				Score score = game.getScore(player);
				if (score == null) {
					return;
				}
				score.updateProcessing(15 * quality);
				sendScore(score);
				return;
			}
		}
	}

	@Override
	public void forceClose() {
		if (game != null) {
			game.remove(player);
		} else {
			Helper.sendHome(player);
		}
	}

	public StealingCreationGame getGame() {
		return game;
	}

	public boolean getTeam() {
		return team;
	}
}
