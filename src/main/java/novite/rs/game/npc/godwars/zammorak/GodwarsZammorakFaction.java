package novite.rs.game.npc.godwars.zammorak;

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

public class GodwarsZammorakFaction extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -5987114276158053165L;

	public GodwarsZammorakFaction(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
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
				if (t instanceof GodwarsZammorakFaction || (t instanceof Player && hasGodItem((Player) t))) {
					continue;
				}
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null || item.getId() == -1) {
				continue; // shouldn't happen
			}
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak coif") || name.contains("zamorak mitre") || name.contains("zamorak full helm") || name.contains("zamorak halo") || name.contains("torva full helm") || name.contains("pernix cowl") || name.contains("virtus mask")) {
				return true;
			} else if (name.contains("zamorak cape") || name.contains("zamorak cloak")) {
				return true;
			} else if (name.contains("unholy symbol") || name.contains("zamorak stole")) {
				return true;
			} else if (name.contains("illuminated unholy book") || name.contains("unholy book") || name.contains("zamorak kiteshield")) {
				return true;
			} else if (name.contains("zamorak arrows")) {
				return true;
			} else if (name.contains("zamorak godsword") || name.contains("zamorakian spear") || name.contains("zamorak staff") || name.contains("zamorak crozier") || name.contains("zaryte Bow")) {
				return true;
			} else if (name.contains("zamorak d'hide") || name.contains("zamorak platebody") || name.contains("torva platebody") || name.contains("pernix body") || name.contains("virtus robe top")) {
				return true;
			} else if (name.contains("zamorak robe") || name.contains("zamorak robe bottom ") || name.contains("zamorak chaps") || name.contains("zamorak platelegs") || name.contains("zamorak plateskirt") || name.contains("torva platelegs") || name.contains("pernix chaps") || name.contains("virtus robe legs")) {
				return true;
			} else if (name.contains("zamorak vambraces")) {
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
							godControler.incrementKillCount(3);
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
