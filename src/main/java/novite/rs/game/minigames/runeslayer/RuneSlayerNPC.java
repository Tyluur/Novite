package novite.rs.game.minigames.runeslayer;

import novite.rs.Constants;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.minigames.runeslayer.RuneSlayerMonsters.Monsters;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 24, 2013
 */
public class RuneSlayerNPC extends NPC {

	public RuneSlayerNPC(int id, WorldTile tile, RuneSlayerFloor floor, boolean boss) {
		super(id, tile, -1, true);
		this.floor = floor;
		this.boss = boss;

		setCustomStats();
		setForceAgressive(true);
		setSpawned(true);
		setForceMultiArea(true);
	}

	private void setCustomStats() {
		setBonuses(new int[13]);
		Monsters monsters = Monsters.getBestByFloor(floor.getFloorsComplete());
		if (monsters != null) {
			if (boss) {
				getCombatDefinitions().setMaxHit(300);
				for (int i = 0; i <= 5; i++) {
					getBonuses()[i] = 500;
				}
				for (int i = 0; i < 10; i++) {
					if (i <= 5) {
						continue;
					}
					getBonuses()[i] = 300;
				}
				return;
			}
			switch (monsters) {
			case BEGINNING:
				getCombatDefinitions().setMaxHit(100);
				for (int i = 0; i <= 5; i++) {
					getBonuses()[i] = 500;
				}
				break;
			case MEDIUM:
				getCombatDefinitions().setMaxHit(125);
				for (int i = 0; i <= 5; i++) {
					getBonuses()[i] = 500;
				}
				for (int i = 0; i < 10; i++) {
					if (i <= 5) {
						continue;
					}
					getBonuses()[i] = 100;
				}
				break;
			case ADVANCED:
				getCombatDefinitions().setMaxHit(200);
				for (int i = 0; i <= 5; i++) {
					getBonuses()[i] = 500;
				}
				for (int i = 0; i < 10; i++) {
					if (i <= 5) {
						continue;
					}
					getBonuses()[i] = 250;
				}
				break;
			}
		}
	}

	@Override
	public void drop() {

	}

	public void updateGameUponDeath() {
		floor.removeNPC(this);
		Player hitter = getMostDamageReceivedSourcePlayer();
		if (hitter != null) {
			if (hitter.getControllerManager().getController() instanceof RuneSlayerGame) {
				RuneSlayerGame game = (RuneSlayerGame) hitter.getControllerManager().getController();
				game.setKills(game.getKills() + 1);
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					updateGameUponDeath();
					reset();
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		try {
			if (!(hit.getSource() instanceof Player)) {
				hit.setDamage(0);
				return;
			}
			super.handleIngoingHit(hit);
			Player hitter = (Player) hit.getSource();
			if (Constants.isVPS) {
				switch (floor.getWeakness()) {
				case MAGIC:
					if (hit.getLook() != HitLook.MAGIC_DAMAGE) {
						hit.setDamage(hit.getDamage() / 3);
					} else {
						hit.setDamage((int) (hit.getDamage() + (hit.getDamage() * 0.25)));
					}
					break;
				case MELEE:
					if (hit.getLook() != HitLook.MELEE_DAMAGE) {
						hit.setDamage(hit.getDamage() / 3);
					} else {
						hit.setDamage((int) (hit.getDamage() + (hit.getDamage() * 0.25)));
					}
					break;
				case RANGE:
					if (hit.getLook() != HitLook.RANGE_DAMAGE) {
						hit.setDamage(hit.getDamage() / 3);
					} else {
						hit.setDamage((int) (hit.getDamage() + (hit.getDamage() * 0.25)));
					}
					break;
				}
			}
			if (hitter.getControllerManager().getController() instanceof RuneSlayerGame) {
				RuneSlayerGame game = (RuneSlayerGame) hitter.getControllerManager().getController();
				game.addDamage(hit.getDamage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private RuneSlayerFloor floor;
	private final boolean boss;

	/**
	 *
	 */
	private static final long serialVersionUID = -7929799449753580722L;

}
