package novite.rs.game.npc.godwars.bandos;

import java.util.ArrayList;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.GodWars;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class GodwarsBandosFaction extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -5402107124695396153L;

	public GodwarsBandosFaction(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!withinDistance(new WorldTile(2881, 5306, 0), 200)) {
			return super.getPossibleTargets();
		} else {
			ArrayList<Entity> targets = getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsBandosFaction || (t instanceof Player && hasGodItem((Player) t))) {
					continue;
				}
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	private boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null) {
				continue; // shouldn't happen
			}
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("bandos mitre") || name.contains("bandos Full helm") || name.contains("bandos coif") || name.contains("torva full helm") || name.contains("pernix cowl") || name.contains("vitus mask")) {
				return true;
			} else if (name.contains("bandos cloak")) {
				return true;
			} else if (name.contains("bandos stole")) {
				return true;
			} else if (name.contains("ancient mace") || name.contains("granite mace") || name.contains("bandos godsword") || name.contains("bandos crozier") || name.contains("zaryte bow")) {
				return true;
			} else if (name.contains("bandos body") || name.contains("bandos robe top") || name.contains("bandos chestplate") || name.contains("bandos platebody") || name.contains("torva platebody") || name.contains("pernix body") || name.contains("virtus robe top")) {
				return true;
			} else if (name.contains("illuminated book of war") || name.contains("book of war") || name.contains("bandos kiteshield")) {
				return true;
			} else if (name.contains("bandos robe legs") || name.contains("bandos tassets") || name.contains("bandos chaps") || name.contains("bandos platelegs") || name.contains("bandos plateskirt") || name.contains("torva platelegs") || name.contains("pernix chaps") || name.contains("virtus robe legs")) {
				return true;
			} else if (name.contains("bandos vambraces")) {
				return true;
			} else if (name.contains("bandos boots")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void sendDeath(final Entity source) {
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
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controler = player.getControllerManager().getController();
						if (controler != null && controler instanceof GodWars) {
							GodWars godControler = (GodWars) controler;
							godControler.incrementKillCount(0);
						}
					}
					drop();
					reset();
					setLocation(getRespawnTile());
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
}
