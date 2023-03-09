package novite.rs.game.npc.others;

import novite.rs.game.Entity;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.godwars.bandos.GodwarsBandosFaction;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.guilds.warriors.WarriorsGuild;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Cyclopse extends GodwarsBandosFaction {

	/**
	 *
	 */
	private static final long serialVersionUID = -348753458086327348L;

	public Cyclopse(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player) {
			WarriorsGuild.killedCyclopses++;
			final NPC npc = this;
			final Player player = (Player) source;
			Controller controler = player.getControllerManager().getController();
			if (controler == null || !(controler instanceof WarriorsGuild) || Utils.random(15) != 0) {
				return;
			}
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					World.addGroundItem(new Item(WarriorsGuild.getBestDefender(player)), new WorldTile(getCoordFaceX(npc.getSize()), getCoordFaceY(npc.getSize()), getPlane()), player, true, 60);
				}
			}, getCombatDefinitions().getDeathDelay());
		}
	}
}
