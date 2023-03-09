package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.runecrafting.AbbysObsticals;
import novite.rs.game.player.content.Runecrafting;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 8, 2014
 */
public class AbyssalCrafting extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 7143, 7153, 7152, 7144, 7150, 7146, 7147, 7148, 7149, 7151, 7145, 7137, 7140, 7131, 713, 7129, 7133, 7132, 7141, 7134, 7138 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int id, WorldObject object, WorldTile tile, ClickOption option) {
		if (id == 7143 || id == 7153)
			AbbysObsticals.clearRocks(player, object);
		else if (id == 7152 || id == 7144)
			AbbysObsticals.clearTendrills(player, object, new WorldTile(id == 7144 ? 3028 : 3051, 4824, 0));
		else if (id == 7150 || id == 7146)
			AbbysObsticals.clearEyes(player, object, new WorldTile(object.getX() == 3021 ? 3028 : 3050, 4839, 0));
		else if (id == 7147)
			AbbysObsticals.clearGap(player, object, new WorldTile(3030, 4843, 0), false);
		else if (id == 7148)
			AbbysObsticals.clearGap(player, object, new WorldTile(3040, 4845, 0), true);
		else if (id == 7149)
			AbbysObsticals.clearGap(player, object, new WorldTile(3048, 4842, 0), false);
		else if (id == 7151)
			AbbysObsticals.burnGout(player, object, new WorldTile(3053, 4831, 0));
		else if (id == 7145)
			AbbysObsticals.burnGout(player, object, new WorldTile(3024, 4834, 0));
		else if (id == 7137)
			Runecrafting.enterWaterAltar(player);
		else if (id == 7139)
			Runecrafting.enterAirAltar(player);
		else if (id == 7140)
			Runecrafting.enterMindAltar(player);
		else if (id == 7131)
			Runecrafting.enterBodyAltar(player);
		else if (id == 7130)
			Runecrafting.enterEarthAltar(player);
		else if (id == 7129)
			Runecrafting.enterFireAltar(player);
		else if (id == 7133)
			Runecrafting.enterNatureAltar(player);
		else if (id == 7132)
			Runecrafting.enterCosmicAltar(player);
		else if (id == 7141)
			Runecrafting.enterBloodAltar(player);
		else if (id == 7134)
			Runecrafting.enterChoasAltar(player);
		else if (id == 7138)
			player.getPackets().sendGameMessage("A strange power blocks your exit..");
		return true;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
