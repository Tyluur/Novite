package novite.rs.game.player.content.exchange;

import static novite.rs.game.player.content.exchange.ExchangeConfiguration.MAIN_INTERFACE;

import java.util.HashMap;

import novite.rs.Constants;
import novite.rs.api.event.listeners.interfaces.GrandExchangeListener;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.player.CombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.content.exchange.ExchangeConfiguration.Progress;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ExchangeItemLoader;
import novite.rs.utility.game.json.impl.ExchangePriceLoader;

/**
 * Handles all interaction with grand exchange buttons, displaying of the items,
 * collecting the items, and all internal processes of the grand exchange
 * 
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class ExchangeManagement {

	/**
	 * Display the main grand exchange interface with the progression of all of
	 * the player's offers
	 * 
	 * @param player
	 *            The player
	 */
	public static void sendSummary(final Player player) {
		if (player.getBank().getPin().hasPin() && !player.getBank().getPin().enteredPinDuringSession()) {
			player.getAttributes().put("entering_pin", "grand_exchange_main");
			player.getBank().getPin().showEnterPin();
			return;
		}

		player.stopAll();
		player.getInterfaceManager().closeChatBoxInterface();
		player.getInterfaceManager().closeInventoryInterface();

		sendMainComponentConfigs(player);

		player.getPackets().sendUnlockIComponentOptionSlots(MAIN_INTERFACE, 209, -1, -1, 1, 2, 3, 5, 6);
		player.getPackets().sendUnlockIComponentOptionSlots(MAIN_INTERFACE, 211, -1, -1, 1, 2, 3, 5, 6);

		sendProgress(player);

		player.getInterfaceManager().sendInterface(MAIN_INTERFACE);
		/** Closes the search bar when the interface is closed */
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getAttributes().remove("exchange_offer");
				player.getAttributes().remove("exchange_sell_item");
				closeSearchBar(player);
			}
		});
	}

	/**
	 * Sends the progress bars information
	 * 
	 * @param player
	 *            The player
	 */
	public static void sendProgress(Player player) {
		for (int i = 0; i < 6; i++) {
			player.getPackets().sendGrandExchangeBar(player, i, 0, Progress.RESET, 0, 0, 0);
		}

		for (ExchangeOffer offer : ((ExchangeItemLoader) JsonHandler.getJsonLoader(ExchangeItemLoader.class)).getOffersList(player.getUsername())) {
			switch (offer.getType()) {
			case BUY:
				if (!offer.isAborted()) {
					player.getPackets().sendGrandExchangeBar(player, offer.getSlot(), offer.getItemId(), offer.isFinished() ? Progress.FINISHED_BUYING : Progress.BUY_PROGRESSING, offer.getPrice(), offer.getAmountProcessed(), offer.getAmountRequested());
				} else {
					player.getPackets().sendGrandExchangeBar(player, offer.getSlot(), offer.getItemId(), Progress.BUY_ABORTED, offer.getPrice(), offer.getAmountProcessed(), offer.getAmountRequested());
				}
				break;
			case SELL:
				if (!offer.isAborted()) {
					player.getPackets().sendGrandExchangeBar(player, offer.getSlot(), offer.getItemId(), offer.isFinished() ? Progress.FINISHED_SELLING : Progress.SELL_PROGRESSING, offer.getPrice(), offer.getAmountProcessed(), offer.getAmountRequested());
				} else {
					player.getPackets().sendGrandExchangeBar(player, offer.getSlot(), offer.getItemId(), Progress.SELL_ABORTED, offer.getPrice(), offer.getAmountProcessed(), offer.getAmountRequested());
				}
				break;
			}
		}
	}

	/**
	 * Sends the collection box to the player
	 * 
	 * @param player
	 */
	public static void openCollectionBox(Player player) {
		sendSummary(player);
		for (int i = 0; i < 6; i++) {
			GrandExchangeListener.sendCollectInformation(player, i);
		}
		player.getInterfaceManager().sendInterface(109);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 19, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 23, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 27, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 32, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 37, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 42, 0, 2, 0, 1);
	}

	/**
	 * Sends the main screen configs and sets them to their default value
	 * 
	 * @param player
	 *            The player to send it to
	 */
	public static void sendMainComponentConfigs(Player player) {
		player.getPackets().sendConfig(1112, -1);
		player.getPackets().sendConfig(1113, -1);
		player.getPackets().sendConfig(1109, -1);
		player.getPackets().sendConfig(1110, 0);
		player.getPackets().sendConfig(563, 4194304);
		player.getPackets().sendConfig(1112, -1);
		player.getPackets().sendConfig(1113, -1);
		player.getPackets().sendConfig(1114, 0);
		player.getPackets().sendConfig(1109, -1);
		player.getPackets().sendConfig(1110, 0);
		player.getPackets().sendConfig(1111, 1);
		closeSearchBar(player);
	}

	/**
	 * Closes the search bar that displays the names
	 * 
	 * @param player
	 *            The player to close it for
	 */
	public static void closeSearchBar(Player player) {
		player.getPackets().sendRunScript(571);
	}

	/**
	 * Handles what is done when the player selects an item from the list of
	 * items to buy
	 * 
	 * @param player
	 *            The player
	 * @param itemId
	 *            The item id selected
	 */
	public static void chooseBuyItem(Player player, int itemId) {
		if (player.getAttributes().get("exchange_slot") == null) {
			System.out.println(player.getDisplayName() + " had no exchange slot selected.");
			return;
		}
		ExchangeItemLoader loader = JsonHandler.getJsonLoader(ExchangeItemLoader.class);
		ExchangePriceLoader priceLoader = JsonHandler.getJsonLoader(ExchangePriceLoader.class);
		ExchangeOffer best = loader.getBestOffer(ExchangeType.SELL, itemId);

		StringBuilder bldr = new StringBuilder();
		if (best == null) {
			bldr.append(ItemDefinitions.getItemDefinitions(itemId).getName() + " has no sell offers currently available<br>");
			bldr.append("You may submit an offer for it and hope someone will sell it.<br>");
			bldr.append("Average Price: " + Utils.format(priceLoader.getAveragePrice(itemId)));
		} else {
			bldr.append("We've found the best offer for you!<br><br>");
			bldr.append("Price: " + Utils.format(best.getPrice()) + "<br>");
			bldr.append("Quantity: " + (best.isUnlimited() ? "UNLIMITED STOCK" : best.getAmountRequested()));
		}
		
		if (Constants.DEBUG) {
			System.out.println("Picked item: " + best);
		}

		player.getInterfaceManager().sendInterface(MAIN_INTERFACE);

		int amountToBuy = 1;
		int price = best == null ? priceLoader.getAveragePrice(itemId) : best.getPrice();

		ExchangeOffer offer = new ExchangeOffer(player.getUsername(), itemId, ExchangeType.BUY, (int) player.getAttributes().get("exchange_slot"), amountToBuy, price);

		player.getPackets().sendConfig(1109, offer.getItemId());
		player.getPackets().sendConfig(1110, amountToBuy);
		player.getPackets().sendConfig(1111, price);
		player.getPackets().sendConfig(1114, price);

		player.getAttributes().put("exchange_offer", offer);
		player.getPackets().sendIComponentText(MAIN_INTERFACE, 143, bldr.toString());
	}

	public static void sendInfo(Player player, Item item) {
		player.getInterfaceManager().sendInventoryInterface(449);
		player.getPackets().sendGlobalConfig(741, item.getId());
		player.getPackets().sendGlobalString(25, ItemExamines.getExamine(item));
		player.getPackets().sendGlobalString(34, ""); // quest id for some items
		int[] bonuses = new int[18];
		ItemDefinitions defs = item.getDefinitions();
		bonuses[CombatDefinitions.STAB_ATTACK] += defs.getStabAttack();
		bonuses[CombatDefinitions.SLASH_ATTACK] += defs.getSlashAttack();
		bonuses[CombatDefinitions.CRUSH_ATTACK] += defs.getCrushAttack();
		bonuses[CombatDefinitions.MAGIC_ATTACK] += defs.getMagicAttack();
		bonuses[CombatDefinitions.RANGE_ATTACK] += defs.getRangeAttack();
		bonuses[CombatDefinitions.STAB_DEF] += defs.getStabDef();
		bonuses[CombatDefinitions.SLASH_DEF] += defs.getSlashDef();
		bonuses[CombatDefinitions.CRUSH_DEF] += defs.getCrushDef();
		bonuses[CombatDefinitions.MAGIC_DEF] += defs.getMagicDef();
		bonuses[CombatDefinitions.RANGE_DEF] += defs.getRangeDef();
		bonuses[CombatDefinitions.SUMMONING_DEF] += defs.getSummoningDef();
		bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS] += defs.getAbsorveMeleeBonus();
		bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS] += defs.getAbsorveMageBonus();
		bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS] += defs.getAbsorveRangeBonus();
		bonuses[CombatDefinitions.STRENGTH_BONUS] += defs.getStrengthBonus();
		bonuses[CombatDefinitions.RANGED_STR_BONUS] += defs.getRangedStrBonus();
		bonuses[CombatDefinitions.PRAYER_BONUS] += defs.getPrayerBonus();
		bonuses[CombatDefinitions.MAGIC_DAMAGE] += defs.getMagicDamage();
		boolean hasBonus = false;
		for (int bonus : bonuses)
			if (bonus != 0) {
				hasBonus = true;
				break;
			}
		if (hasBonus) {
			HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
			System.out.println(requiriments);
			if (requiriments != null && !requiriments.isEmpty()) {
				String reqsText = "";
				for (int skillId : requiriments.keySet()) {
					if (skillId > 24 || skillId < 0)
						continue;
					int level = requiriments.get(skillId);
					if (level < 0 || level > 120)
						continue;
					boolean hasReq = player.getSkills().getLevelForXp(skillId) >= level;
					reqsText += "<br>" + (hasReq ? "<col=00ff00>" : "<col=ff0000>") + "Level " + level + " " + Skills.SKILL_NAME[skillId];
				}
				player.getPackets().sendGlobalString(26, "<br>Worn on yourself, requiring: " + reqsText);
			} else
				player.getPackets().sendGlobalString(26, "<br>Worn on yourself");
			player.getPackets().sendGlobalString(35, "<br>Attack<br><col=ffff00>+" + bonuses[CombatDefinitions.STAB_ATTACK] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.SLASH_ATTACK] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.CRUSH_ATTACK] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.MAGIC_ATTACK] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.RANGE_ATTACK] + "<br><col=ffff00>---" + "<br>Strength" + "<br>Ranged Strength" + "<br>Magic Damage" + "<br>Absorve Melee" + "<br>Absorve Magic" + "<br>Absorve Ranged" + "<br>Prayer Bonus");
			player.getPackets().sendGlobalString(36, "<br><br>Stab<br>Slash<br>Crush<br>Magic<br>Ranged<br>Summoning");
			player.getPackets().sendGlobalString(52, "<<br>Defence<br><col=ffff00>+" + bonuses[CombatDefinitions.STAB_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.SLASH_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.CRUSH_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.MAGIC_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.RANGE_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.SUMMONING_DEF] + "<br><col=ffff00>+" + bonuses[CombatDefinitions.STRENGTH_BONUS] + "<br><col=ffff00>" + bonuses[CombatDefinitions.RANGED_STR_BONUS] + "<br><col=ffff00>" + bonuses[CombatDefinitions.MAGIC_DAMAGE] + "%<br><col=ffff00>" + bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS] + "%<br><col=ffff00>" + bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS] + "%<br><col=ffff00>" + bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS] + "%<br><col=ffff00>" + bonuses[CombatDefinitions.PRAYER_BONUS]);
		} else {
			player.getPackets().sendGlobalString(26, "");
			player.getPackets().sendGlobalString(35, "");
			player.getPackets().sendGlobalString(36, "");
			player.getPackets().sendGlobalString(52, "");
		}

	}

}
