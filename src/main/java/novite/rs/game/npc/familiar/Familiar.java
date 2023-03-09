package novite.rs.game.npc.familiar;

import java.io.Serializable;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.player.actions.summoning.Summoning;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public abstract class Familiar extends NPC implements Serializable {

	private static final long serialVersionUID = -3255206534594320406L;

	private transient Player owner;
	private int ticks;
	private int trackTimer;
	private int specialEnergy;
	private transient boolean finished = false;
	private boolean trackDrain;

	private BeastOfBurden bob;
	private Pouches pouch;

	public Familiar(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(Summoning.getNPCId(pouch.getRealPouchId()), tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		this.owner = owner;
		this.pouch = pouch;
		resetTickets();
		specialEnergy = 60;
		if (getBOBSize() > 0) {
			bob = new BeastOfBurden(canDepositOnly(), getBOBSize());
		}
		call(true);
		setRun(true);
	}

	public void store() {
		if (bob == null) {
			return;
		}
		bob.open();
	}

	public boolean canDepositOnly() {
		return getDefinitions().hasOption("withdraw");
	}

	public boolean canStoreEssOnly() {
		return pouch == Pouches.ABYSSAL_LURKER || pouch == Pouches.ABYSSAL_PARASITE || pouch == Pouches.ABYSSAL_TITAN;
	}

	public int getOriginalId() {
		return Summoning.getNPCId(pouch.getRealPouchId());
	}

	public void resetTickets() {
		ticks = (int) (pouch.getPouchTime() / 1000 / 30);
		trackTimer = 0;
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex()) {
			setNextFaceEntity(owner);
		}
		if (isFrozen()) {
			return;
		}
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true) || !Utils.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0)) {
			calcFollow(owner, 2, true, false);
		}
	}

	@Override
	public void processNPC() {
		if (isDead()) {
			return;
		}
		unlockOrb();
		trackTimer++;
		if (trackTimer == 50) {
			trackTimer = 0;
			ticks--;
			if (trackDrain) {
				owner.getSkills().drainSummoning(1);
			}
			trackDrain = !trackDrain;
			if (ticks == 2) {
				owner.getPackets().sendGameMessage("You have 1 minute before your familiar vanishes.");
			} else if (ticks == 1) {
				owner.getPackets().sendGameMessage("You have 30 seconds before your familiar vanishes.");
			} else if (ticks == 0) {
				removeFamiliar();
				dissmissFamiliar(false);
				return;
			}
			sendTimeRemaining();
		}
		int originalId = getOriginalId() + 1;
		if (owner.isCanPvp() && getId() == getOriginalId()) {
			transformInto(originalId);
			call(false);
			return;
		} else if (!owner.isCanPvp() && getId() == originalId && pouch != Pouches.MAGPIE && pouch != Pouches.IBIS && pouch != Pouches.BEAVER && pouch != Pouches.MACAW && pouch != Pouches.FRUIT_BAT) {
			transformInto(originalId - 1);
			call(false);
			return;
		} else if (!withinDistance(owner, 12)) {
			call(false);
			return;
		}
		if (!getCombat().process()) {
			if (isAgressive() && owner.getAttackedBy() != null && owner.getAttackedByDelay() > Utils.currentTimeMillis() && canAttack(owner.getAttackedBy()) && Utils.getRandom(25) == 0) {
				getCombat().setTarget(owner.getAttackedBy());
			} else {
				sendFollow();
			}
		}
	}

	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player player = (Player) target;
			if (!owner.isCanPvp() || !player.isCanPvp()) {
				return false;
			}
		}
		return !target.isDead() && ((owner.isAtMultiArea() && isAtMultiArea() && target.isAtMultiArea()) || (owner.isForceMultiArea() && target.isForceMultiArea())) && owner.getControllerManager().canAttack(target);
	}

	public boolean renewFamiliar() {
		if (ticks > 5) {
			owner.getPackets().sendGameMessage("You need to have at least two minutes and fifty seconds remaining before you can renew your familiar.", true);
			return false;
		} else if (!owner.getInventory().getItems().contains(new Item(pouch.getRealPouchId(), 1))) {
			owner.getPackets().sendGameMessage("You need a " + ItemDefinitions.getItemDefinitions(pouch.getRealPouchId()).getName().toLowerCase() + " to renew your familiar's timer.");
			return false;
		}
		resetTickets();
		owner.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		call(true);
		owner.getPackets().sendGameMessage("You use your remaining pouch to renew your familiar.");
		return true;
	}

	public void takeBob() {
		if (bob == null) {
			return;
		}
		bob.takeBob();
	}

	public void sendTimeRemaining() {
		owner.getVarsManager().sendVar(1176, ticks * 65);
	}

	public void sendMainConfigs() {
		switchOrb(true);
		owner.getPackets().sendConfig(448, pouch.getRealPouchId());// configures
		// familiar type
		// based on
		// pouch?
		owner.getPackets().sendConfig(1160, 243269632); // sets npc emote
		refreshSpecialEnergy();
		sendTimeRemaining();
		owner.getPackets().sendConfig(1175, getSpecialAmount() << 23);// check
		owner.getPackets().sendGlobalString(204, getSpecialName());
		owner.getPackets().sendGlobalString(205, getSpecialDescription());
		owner.getPackets().sendGlobalConfig(1436, getSpecialAttack() == SpecialAttack.CLICK ? 1 : 0);
		unlockOrb(); // temporary
	}

	public void sendFollowerDetails() {
		boolean res = owner.getInterfaceManager().hasResizableScreen();
		owner.getPackets().sendInterface(true, res ? 746 : 548, res ? 98 : 212, 662);
		owner.getPackets().sendHideIComponent(662, 44, true);
		owner.getPackets().sendHideIComponent(662, 45, true);
		owner.getPackets().sendHideIComponent(662, 46, true);
		owner.getPackets().sendHideIComponent(662, 47, true);
		owner.getPackets().sendHideIComponent(662, 48, true);
		owner.getPackets().sendHideIComponent(662, 71, false);
		owner.getPackets().sendHideIComponent(662, 72, false);
		unlock();
		owner.getPackets().sendGlobalConfig(168, 8);// tab id
	}

	public void switchOrb(boolean on) {
		owner.getPackets().sendConfig(1174, on ? -1 : 0);
		if (on) {
			unlock();
		} else {
			lockOrb();
		}
	}

	public void unlockOrb() {
		owner.getPackets().sendHideIComponent(747, 8, false);
		sendLeftClickOption(owner);
	}

	public static void selectLeftOption(Player player) {
		boolean res = player.getInterfaceManager().hasResizableScreen();
		player.getPackets().sendInterface(true, res ? 746 : 548, res ? 98 : 212, 880);
		sendLeftClickOption(player);
		player.getPackets().sendGlobalConfig(168, 8);// tab id
	}

	public static void confirmLeftOption(Player player) {
		player.getPackets().sendGlobalConfig(168, 4);// inv tab id
		boolean res = player.getInterfaceManager().hasResizableScreen();
		player.getPackets().closeInterface(res ? 98 : 212);
	}

	public static void setLeftclickOption(Player player, int summoningLeftClickOption) {
		if (summoningLeftClickOption == player.getSummoningLeftClickOption()) {
			return;
		}
		player.setSummoningLeftClickOption(summoningLeftClickOption);
		sendLeftClickOption(player);
	}

	public static void sendLeftClickOption(Player player) {
		player.getPackets().sendConfig(1493, player.getSummoningLeftClickOption());
		player.getPackets().sendConfig(1494, player.getSummoningLeftClickOption());
	}

	public void unlock() {
		switch (getSpecialAttack()) {
			case CLICK:
				owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 2);
				owner.getPackets().sendIComponentSettings(662, 74, 0, 0, 2);
				break;
			case ENTITY:
				owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 20480);
				owner.getPackets().sendIComponentSettings(662, 74, 0, 0, 20480);
				break;
			case OBJECT:
			case ITEM:
				owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 65536);
				owner.getPackets().sendIComponentSettings(662, 74, 0, 0, 65536);
				break;
		}
		owner.getPackets().sendHideIComponent(747, 8, false);
	}

	public void lockOrb() {
		owner.getPackets().sendHideIComponent(747, 8, true);
	}

	private transient int[][] checkNearDirs;
	private transient boolean sentRequestMoveMessage;

	public void call() {
		if (isDead()) {
			return;
		}
		if (getAttackedBy() != null && getAttackedByDelay() > Utils.currentTimeMillis()) {
			// TODO or something as this
			owner.getPackets().sendGameMessage("You cant call your familiar while it under combat.");
			return;
		}
		call(false);
	}

	public void call(boolean login) {
		int size = getSize();
		if (login) {
			if (bob != null) {
				bob.setEntitys(owner, this);
			}
			checkNearDirs = Utils.getCoordOffsetsNear(size);
			sendMainConfigs();
		} else {
			removeTarget();
		}
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir], owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) { // if
				// found
				// done
				teleTile = tile;
				break;
			}
		}
		if (login || teleTile != null) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					setNextGraphics(new Graphics(getDefinitions().size > 1 ? 1315 : 1314));
				}
			});
		}
		if (teleTile == null) {
			if (!sentRequestMoveMessage) {
				owner.getPackets().sendGameMessage("Theres not enough space for your familiar appear.");
				sentRequestMoveMessage = true;
			}
			return;
		}
		sentRequestMoveMessage = false;
		setNextWorldTile(teleTile);
	}

	public void removeFamiliar() {
		owner.setFamiliar(null);
	}

	public void dissmissFamiliar(boolean logged) {
		finish();
		if (!logged && !isFinished()) {
			setFinished(true);
			switchOrb(false);
			owner.getPackets().closeInterface(owner.getInterfaceManager().hasResizableScreen() ? 98 : 212);
			owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
			if (bob != null) {
				bob.dropBob();
			}
		}
	}

	private transient boolean dead;

	@Override
	public boolean isDead() {
		return dead || super.isDead();
	}

	@Override
	public void sendDeath(Entity source) {
		if (dead) {
			return;
		}
		dead = true;
		removeFamiliar();
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					owner.getPackets().sendGameMessage("Your familiar slowly begins to fade away..");
				} else if (loop >= defs.getDeathDelay()) {
					dissmissFamiliar(false);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void respawnFamiliar(Player owner) {
		this.owner = owner;
		initEntity();
		deserialize();
		call(true);
	}

	public abstract String getSpecialName();

	public abstract String getSpecialDescription();

	public abstract int getBOBSize();

	public abstract int getSpecialAmount();

	public abstract SpecialAttack getSpecialAttack();

	public abstract boolean submitSpecial(Object object);

	public boolean isAgressive() {
		return true;
	}

	public static enum SpecialAttack {
		ITEM,
		ENTITY,
		CLICK,
		OBJECT
	}

	public BeastOfBurden getBob() {
		return bob;
	}

	public void refreshSpecialEnergy() {
		owner.getVarsManager().sendVar(1177, specialEnergy);
	}

	public void restoreSpecialAttack(int energy) {
		if (specialEnergy >= 60) {
			return;
		}
		specialEnergy = energy + specialEnergy >= 60 ? 60 : specialEnergy + energy;
		refreshSpecialEnergy();
	}

	public void setSpecial(boolean on) {
		if (!on) {
			owner.getTemporaryAttributtes().remove("FamiliarSpec");
		} else {
			if (specialEnergy < getSpecialAmount()) {
				owner.getPackets().sendGameMessage("Your special move bar is too low to use this scroll.");
				return;
			}
			owner.getTemporaryAttributtes().put("FamiliarSpec", Boolean.TRUE);
		}
	}

	public void drainSpecial(int specialReduction) {
		specialEnergy -= specialReduction;
		if (specialEnergy < 0) {
			specialEnergy = 0;
		}
		refreshSpecialEnergy();
	}

	public void drainSpecial() {
		specialEnergy -= getSpecialAmount();
		refreshSpecialEnergy();
	}

	public boolean hasSpecialOn() {
		if (owner.getTemporaryAttributtes().remove("FamiliarSpec") != null) {
			int scrollId = Summoning.getScrollId(pouch.getRealPouchId());
			if (!owner.getInventory().containsItem(scrollId, 1)) {
				owner.getPackets().sendGameMessage("You don't have the scrolls to use this move.");
				return false;
			}
			owner.getInventory().deleteItem(scrollId, 1);
			drainSpecial();
			return true;
		}
		return false;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean isFinished() {
		return finished;
	}

	public Pouches getPouch() {
		return pouch;
	}
}
