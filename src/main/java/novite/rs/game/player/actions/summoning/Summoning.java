package novite.rs.game.player.actions.summoning;

import java.util.List;

import novite.rs.cache.loaders.ClientScriptMap;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.clanwars.ClanWars;
import novite.rs.game.minigames.clanwars.ClanWars.Rules;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;

public class Summoning {

	public static void spawnFamiliar(Player player, Pouches pouch) {
		if (player.getFamiliar() != null || player.getPet() != null) {
			player.getPackets().sendGameMessage("You already have a follower.");
			return;
		}
		if (!player.getControllerManager().canSummonFamiliar()) {
			return;
		}
		if (player.getSkills().getLevel(Skills.SUMMONING) < pouch.getSummoningCost()) {
			player.sendMessage("You do not have enought summoning points to spawn this.");
			return;
		}
		int levelReq = getRequiredLevel(pouch.getRealPouchId());
		if (player.getSkills().getLevelForXp(Skills.SUMMONING) < levelReq) {
			player.getPackets().sendGameMessage("You need a summoning level of " + levelReq + " in order to use this pouch.");
			return;
		}
		if (player.getCurrentFriendChat() != null) {
			ClanWars war = player.getCurrentFriendChat().getClanWars();
			if (war != null) {
				if (war.get(Rules.NO_FAMILIARS) && (war.getFirstPlayers().contains(player) || war.getSecondPlayers().contains(player))) {
					player.getPackets().sendGameMessage("You can't summon familiars during this war.");
					return;
				}
			}
		}
		final Familiar npc = createFamiliar(player, pouch);
		if (npc == null) {
			player.getPackets().sendGameMessage("This familiar is not added yet.");
			return;
		}
		player.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		player.getSkills().drainSummoning(pouch.getSummoningCost());
		player.setFamiliar(npc);
	}

	public static Familiar createFamiliar(Player player, Pouches pouch) {
		try {
			return (Familiar) Class.forName("novite.rs.game.npc.familiar." + (NPCDefinitions.getNPCDefinitions(getNPCId(pouch.getRealPouchId()))).getName().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")).getConstructor(Player.class, Pouches.class, WorldTile.class, int.class, boolean.class).newInstance(player, pouch, player, -1, true);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean hasPouch(Player player) {
		for (Pouches pouch : Pouches.values()) {
			if (player.getInventory().containsOneItem(pouch.getRealPouchId())) {
				return true;
			}
		}
		return false;
	}

	public static final int POUCHES_INTERFACE = 672, SCROLLS_INTERFACE = 666;
	private static final Animation SCROLL_INFUSIN_ANIMATION = new Animation(723);
	private static final Animation POUCH_INFUSION_ANIMATION = new Animation(725);
	private static final Graphics POUCH_INFUSION_GRAPHICS = new Graphics(1207);

	public static int getScrollId(int id) {
		return ClientScriptMap.getMap(1283).getIntValue(id);
	}

	public static int getRequiredLevel(int id) {
		return ClientScriptMap.getMap(1185).getIntValue(id);
	}

	public static int getNPCId(int id) {
		return ClientScriptMap.getMap(1320).getIntValue(id);
	}

	public static String getRequirementsMessage(int id) {
		return ClientScriptMap.getMap(1186).getStringValue(id);
	}

	public static void openInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(POUCHES_INTERFACE);
		player.getPackets().sendPouchInfusionOptionsScript(POUCHES_INTERFACE, 16, 78, 8, 10, "Infuse<col=FF9040>", "Infuse-5<col=FF9040>", "Infuse-10<col=FF9040>", "Infuse-X<col=FF9040>", "Infuse-All<col=FF9040>", "List<col=FF9040>");
		player.getPackets().sendIComponentSettings(POUCHES_INTERFACE, 16, 0, 462, 190);
		player.getTemporaryAttributtes().put("infusing_scroll", false);
	}

	public static void openScrollInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(SCROLLS_INTERFACE);
		player.getPackets().sendScrollInfusionOptionsScript(SCROLLS_INTERFACE, 16, 78, 8, 10, "Transform<col=FF9040>", "Transform-5<col=FF9040>", "Transform-10<col=FF9040>", "Transform-All<col=FF9040>", "Transform-X<col=FF9040>");
		player.getPackets().sendIComponentSettings(SCROLLS_INTERFACE, 16, 0, 462, 126);
		player.getTemporaryAttributtes().put("infusing_scroll", true);
	}
	
	public static void createScroll(Player player, int itemId, int amount) {
		Scrolls scroll = Scrolls.get(itemId);
		if (scroll == null) {
			player.sendMessage("You do not have the pouch required to create this scroll.");
			return;
		}
		if (amount == 28 || amount > player.getInventory().getItems().getNumberOf(scroll.getPouchId())) {
			amount = player.getInventory().getItems().getNumberOf(scroll.getPouchId());
		}
		if (!player.getInventory().containsItem(scroll.getPouchId(), 1)) {
			player.sendMessage("You do not have enough " + ItemDefinitions.getItemDefinitions(scroll.getPouchId()).getName().toLowerCase() + "es to create " + amount + " " + ItemDefinitions.getItemDefinitions(scroll.getScrollId()).getName().toLowerCase() + "s.");
			return;
		}
		if (player.getSkills().getLevel(Skills.SUMMONING) < scroll.getReqLevel()) {
			player.sendMessage("You need a summoning level of " + scroll.getReqLevel() + " to create " + amount + " " + ItemDefinitions.getItemDefinitions(scroll.getScrollId()).getName().toLowerCase() + "s.");
			return;
		}
		player.getInventory().deleteItem(scroll.getPouchId(), amount);
		player.getInventory().addItem(scroll.getScrollId(), amount * 10);
		player.getSkills().addXp(Skills.SUMMONING, scroll.getExperience());

		player.closeInterfaces();
		player.setNextAnimation(SCROLL_INFUSIN_ANIMATION);
	}
	
	
	public static void handlePouchInfusion(Player player, int slotId, int creationCount) {
		int slotValue = (slotId - 2) / 5;
		Pouches pouch = Pouches.values()[slotValue];
		if (pouch == null) {
			return;
		}
		boolean infusingScroll = (boolean) player.getTemporaryAttributtes().remove("infusing_scroll"), hasRequirements = false;
		ItemDefinitions def = ItemDefinitions.getItemDefinitions(pouch.getRealPouchId());
		List<Item> itemReq = def.getCreateItemRequirements(infusingScroll);
		int level = getRequiredLevel(pouch.getRealPouchId());
		if (itemReq != null) {
			itemCount: for (int i = 0; i < creationCount; i++) {
				if (!player.getInventory().containsItems(itemReq)) {
					sendItemList(player, infusingScroll, creationCount, slotId);
					break itemCount;
				} else if (player.getSkills().getLevelForXp(Skills.SUMMONING) < level) {
					player.getPackets().sendGameMessage("You need a summoning level of " + level + " to create this pouch.");
					break itemCount;
				}
				hasRequirements = true;
				player.getInventory().removeItems(itemReq);
				player.getInventory().addItem(new Item(infusingScroll ? getScrollId(pouch.getRealPouchId()) : pouch.getRealPouchId(), infusingScroll ? 10 : 1));
				player.getSkills().addXp(Skills.SUMMONING, infusingScroll ? pouch.getMinorExperience() : pouch.getExperience());
			}
		}
		if (!hasRequirements) {
			player.getTemporaryAttributtes().put("infusing_scroll", infusingScroll);
			return;
		}
		player.closeInterfaces();
		player.setNextAnimation(POUCH_INFUSION_ANIMATION);
		player.setNextGraphics(POUCH_INFUSION_GRAPHICS);
	}

	public static void switchInfusionOption(Player player) {
		boolean infusingScroll = (boolean) player.getTemporaryAttributtes().get("infusing_scroll");
		if (infusingScroll) {
			openInfusionInterface(player);
		} else {
			openScrollInfusionInterface(player);
		}
	}

	public static void sendItemList(Player player, boolean infusingScroll, int count, int slotId) {
		int slotValue = (slotId - 2) / 5;
		Pouches pouch = Pouches.values()[slotValue];
		if (pouch == null) {
			return;
		}
		if (infusingScroll) {
			player.getPackets().sendGameMessage("This scroll requires 1 " + ItemDefinitions.getItemDefinitions(pouch.getRealPouchId()).name.toLowerCase() + ".");
		} else {
			player.getPackets().sendGameMessage(getRequirementsMessage(pouch.getRealPouchId()));
		}
	}
}
