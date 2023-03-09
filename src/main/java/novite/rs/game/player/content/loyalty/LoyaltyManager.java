package novite.rs.game.player.content.loyalty;

import java.io.Serializable;
import java.util.ArrayList;

import novite.rs.game.player.InterfaceManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

public class LoyaltyManager implements Serializable {

	/**
	 * The loyalty manager constructor
	 *
	 * @param player
	 *            The player
	 */
	public LoyaltyManager(Player player) {
		this.player = player;
		setFavorites(new Favorites());
	}

	/**
	 * Sets the player instance
	 *
	 * @param player
	 *            The player to set it to
	 */
	public void setPlayer(Player player) {
		this.player = player;
		if (unlockedTitles == null) {
			unlockedTitles = new ArrayList<LoyaltyTitle>();
		}
		if (unlockedEffects == null) {
			unlockedEffects = new ArrayList<LoyaltyEffect>();
		}
	}

	/**
	 * The player instance
	 *
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * The amount of loyalty points
	 *
	 * @return
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Sets the new users point value
	 *
	 * @param amount
	 *            The amount of points to set it to
	 */
	public void setPoints(int amount) {
		points = amount;
		player.getPackets().sendGlobalConfig(1648, points);
	}

	/**
	 * Adds the points to the user
	 *
	 * @param amount
	 *            The amount of points to add
	 */
	public void addPoints(int amount) {
		points += amount;
		player.getPackets().sendGlobalConfig(1648, points);

		if (points % 1000 == 0) {
			player.sendMessage("<col=" + ChatColors.MAROON + ">You now have " + Utils.format(points) + " loyalty points! Check out the Party Pete's shop at home for great rewards!");
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You now have " + Utils.format(points) + " loyalty points!", "Purchase cool rewards via <col=" + ChatColors.BLUE + ">Party Pete's</col> loyalty store at home!");
		}
	}

	/**
	 * Removes the points from the user
	 *
	 * @param amount
	 *            The amount of points to remove
	 */
	public void removePoints(int amount) {
		points = Math.max(0, points - amount);
		player.getPackets().sendGlobalConfig(1648, points);
	}

	/**
	 * If the user has enough points
	 *
	 * @param req
	 *            The amount of points to check for
	 * @return
	 */
	public boolean hasSufficientPoints(int req) {
		return points >= req;
	}

	/**
	 * Handling the displaying of the loyalty store, selecting the 'home' tab,
	 * and unlocking all settings
	 */
	public void displayStore() {
		int interfaceId = 1143;

		player.getPackets().sendSound(7649, 0, 2);
		player.getPackets().sendWindowsPane(753, 0);
		player.getPackets().sendInterface(true, 753, 1, interfaceId);
		player.getPackets().sendConfigByFile(9487, 9);
		player.getLoyaltyManager().setCurrentTabConfig(9);
		player.getPackets().sendGlobalConfig(1648, points);

		for (int i = 0; i <= 71; i++) {
			player.getPackets().sendIComponentSettings(interfaceId, i, 0, 73, 2);
		}

		sendPurchaseConfigs();
	}

	/**
	 * Shows the join programme sub interface
	 */
	public void showJoinProgramme() {
		player.getPackets().sendSound(7650, 0, 2);
		player.getPackets().sendHideIComponent(1143, 16, false);
		player.getPackets().sendHideIComponent(1143, 56, true);
		player.getPackets().sendHideIComponent(1143, 57, false);
	}

	/**
	 * Closes the loyalty programme shop interface
	 */
	public void closeStore() {
		player.getPackets().sendSound(7654, 0, 2);
		player.getPackets().sendWindowsPane(player.getDisplayMode() == 1 ? InterfaceManager.FIXED_WINDOW_ID : InterfaceManager.RESIZABLE_WINDOW_ID, 0);
	}

	/**
	 * Hides the join window components
	 */
	public void closeJoinWindow() {
		player.getPackets().sendHideIComponent(1143, 16, true);
		player.getPackets().sendHideIComponent(1143, 56, true);
		player.getPackets().sendHideIComponent(1143, 57, true);
		player.getPackets().sendHideIComponent(1143, 58, true);
	}

	/**
	 * Shows the buy message components
	 *
	 * @param scriptValue
	 *            The value to be sent with the config of 1238, and with the
	 *            runscript
	 */
	public void showBuyMessage(int scriptValue) {
		player.getPackets().sendConfig(1238, scriptValue);
		player.getPackets().sendHideIComponent(1143, 16, false);
		player.getPackets().sendHideIComponent(1143, 57, true);
		player.getPackets().sendHideIComponent(1143, 56, false);
		player.getPackets().sendHideIComponent(1143, 58, true);
		player.getPackets().sendIComponentSettings(1143, 40, 0, 4, 0);
		player.getPackets().sendRunScript(5355, 0, scriptValue);
	}

	public void forceBuyMessage(Object bought) {
		int interfaceId = 1143;
		if (bought instanceof LoyaltyTitle) {
			LoyaltyTitle title = (LoyaltyTitle) bought;
			player.getPackets().sendHideIComponent(interfaceId, 30, false);
			player.getPackets().sendHideIComponent(interfaceId, 31, false);
			player.getPackets().sendHideIComponent(interfaceId, 38, true);
			
			player.getPackets().sendHideIComponent(interfaceId, 36, false);
			player.getPackets().sendHideIComponent(interfaceId, 180, false);
			
			player.getPackets().sendIComponentText(interfaceId, 25, "Confirm Purchase");
			player.getPackets().sendIComponentText(interfaceId, 26, "Your Points: " + getPoints());
			player.getPackets().sendIComponentText(interfaceId, 27, "Item Cost: " + title.getCost());
			player.getPackets().sendIComponentText(interfaceId, 28, "Your are buying the title: " + title.getName() + ".");
			player.getPackets().sendIComponentText(interfaceId, 29, "");
		} else if (bought instanceof LoyaltyAura) {
			LoyaltyAura aura = (LoyaltyAura) bought;
			
			player.getPackets().sendHideIComponent(interfaceId, 30, false);
			player.getPackets().sendHideIComponent(interfaceId, 31, false);
			player.getPackets().sendHideIComponent(interfaceId, 38, true);
			
			player.getPackets().sendHideIComponent(interfaceId, 36, false);
			player.getPackets().sendHideIComponent(interfaceId, 180, false);
			
			player.getPackets().sendIComponentText(interfaceId, 25, "Confirm Purchase");
			player.getPackets().sendIComponentText(interfaceId, 26, "Your Points: " + getPoints());
			player.getPackets().sendIComponentText(interfaceId, 27, "Item Cost: " + aura.getPrice());
			player.getPackets().sendIComponentText(interfaceId, 28, "Your are buying the aura: " + aura.name + ".");
			player.getPackets().sendIComponentText(interfaceId, 29, "");
		}
	}

	/**
	 * Hides the buy message components
	 */
	public void hideBuyMessage() {
		player.getPackets().sendHideIComponent(1143, 16, true);
		player.getPackets().sendHideIComponent(1143, 57, false);
		player.getPackets().sendHideIComponent(1143, 56, true);
		player.getPackets().sendHideIComponent(1143, 58, false);
	}

	/**
	 * Sends the message that they have purchased an aura
	 *
	 * @param given
	 *            The given message
	 */
	public void showPurchaseSuccess(String given) { // You receive the aura:
		// Oddball
		// InterString: id=1143, child=48, string='Select Colour'
		// [INFO ][01/26/13 11:43:14 AM]: Config: id=1234, value=46000
		// [INFO ][01/26/13 11:43:14 AM]: Config: id=1241, value=1
		// [INFO ][01/26/13 11:43:13 AM]: InterString: id=1143, child=162,
		// string='An error occurred when trying to complete your
		// purchase.<br><br>Click Ok to return to the shop.' LOL
		showMessage("Your purchase was successful!<br><br>" + given + "<br><br>Click 'OK' to return to the Loyalty Shop.", 7650);

		// Close: 7648 sound
		// RunScript: iI, objs: [5349, 1, 74907653]?
	}

	/**
	 * Tells the user they already owned the item they are trying to reclaim
	 *
	 * @param bank
	 *            If it is contained in the bank or not
	 */
	public void showReclaimAlreadyOwned(boolean bank) {
		showMessage("You already have this item in your " + (bank ? "bank" : "inventory") + " and cannot reclaim another.", 7650);
	}

	/**
	 * Tells the user that they have successfully reclaimed their purchase
	 */
	public void showReclaimSuccess() {
		showMessage("You have successfully reclaimed your purchase.<br><br>Click 'Ok' to return to the Loyalty Shop.", 7651);
	}

	/**
	 * Tells the user that the item is unavailable
	 */
	public void showUnavailable() {
		showMessage("We're sorry, but this item is not available yet.<br><br>Please try again soon!", 7650);
	}

	/**
	 * Tells the user they do not have sufficient loyalty points for the item
	 *
	 * @param required
	 *            The required loyalty points
	 */
	public void showInsufficientPoints(int required) {
		showMessage("You currently do not have enough Loyalty Points to purchase this item.<br><br>My Points: " + Utils.format(points) + "<br><br>Item Cost: " + Utils.format(required) + "<br><br>Click 'Ok' to return to the Loyalty Shop.", 7650);
	}

	/**
	 * Sends the message telling the user they do not have the requirements for
	 * the aura
	 */
	public void showRequirementNotMet() {
		showMessage("You must have purchased each of the previous tiers of this aura before you can purchase this tier.<br><br>Click 'Ok' to return to the Loyalty Shop.", 7650);
	}

	/**
	 * Shows the message to over the interface
	 *
	 * @param message
	 *            The message to show
	 * @param sound
	 *            The sound to send with the message
	 */
	public void showMessage(String message, int sound) {
		int interfaceId = 1143;
		if (sound != -1) {
			player.getPackets().sendSound(sound, 0, 2);
		}
//		player.getPackets().sendHideIComponent(1143, 16, false);
//		player.getPackets().sendHideIComponent(1143, 57, true);
//		player.getPackets().sendHideIComponent(1143, 56, true);
//		player.getPackets().sendHideIComponent(1143, 58, false);
//		player.getPackets().sendIComponentText(1143, 162, message);

		player.getPackets().sendHideIComponent(interfaceId, 30, true);
		player.getPackets().sendHideIComponent(interfaceId, 31, true);
		player.getPackets().sendHideIComponent(interfaceId, 38, false);
		player.getPackets().sendHideIComponent(interfaceId, 36, true);
		
		player.getPackets().sendIComponentText(interfaceId, 209, message);
	}

	/**
	 * Sends the hidden components over the interface
	 */
	public void closeMessage() {
		player.getPackets().sendHideIComponent(1143, 180, true);
	}

	public void resetAllPurchases() {
		unlockedAuras.clear();
	}

	public void setViewingSlot(int slot) {
		currentViewingSlot = slot;
	}

	public void setCurrentTabConfig(int conf) {
		currentTabConfig = conf;
	}

	/**
	 * Send the reclaiming configuratios for all loyalty rewards
	 */
	public void sendPurchaseConfigs() {
		player.getPackets().sendConfig(2229, 0); // claim
		player.getPackets().sendConfig(2391, 0); // fav
		player.getPackets().sendConfigByFile(9487, currentTabConfig);
	}

	/**
	 * Finds out if the player has unlocked the aura
	 *
	 * @param aura
	 *            The aura to check if is unlocked
	 * @return
	 */
	public boolean hasUnlockedAura(LoyaltyAura aura) {
		for (LoyaltyAura a : unlockedAuras) {
			if (a == aura) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUnlockedEffect(LoyaltyEffect effect) {
		for (LoyaltyEffect a : unlockedEffects) {
			if (a == effect) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUnlockedTitle(LoyaltyTitle title) {
		for (LoyaltyTitle t : unlockedTitles) {
			if (t == title) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the loyalty aura by the button clicked by the player,
	 *
	 * @param slot
	 *            The slot sent from the client to the server
	 * @return A {@code LoyaltyAura} {@code Object}
	 */
	public static LoyaltyAura getAuraByButton(int slot) {
		for (LoyaltyAura a : LoyaltyAura.values()) {
			if (a.getButtonId() == slot) {
				return a;
			}
		}
		return null;
	}

	public static LoyaltyEffect getEffectByButton(int slot) {
		for (LoyaltyEffect e : LoyaltyEffect.values()) {
			if (e.getButtonId() == slot) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets the loyalty aura by the item id
	 *
	 * @param itemId
	 *            The id of the aura
	 * @return A {@code LoyaltyAura} {@code Object]
	 */
	public static LoyaltyAura getAuraById(int itemId) {
		for (LoyaltyAura a : LoyaltyAura.values()) {
			if (a.getItemId() == itemId) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Confirms the purchase of a loyalty item, sends appropriate messages if
	 * invalid or insufficient points
	 */
	public void confirmPurchase() {
		Object current = player.getTemporaryAttributtes().remove("current_loyalty_purchase");
		if (current != null) {
			if (current instanceof LoyaltyTitle) {
				LoyaltyTitle title = LoyaltyTitle.getLoyaltyTitleBySlot(currentViewingSlot);
				if (title == null) {
					System.out.println(currentViewingSlot + " was an invalid title! Did not purchase");
					return;
				}
				if (!hasSufficientPoints(title.getCost())) {
					showInsufficientPoints(title.getCost());
					return;
				}
				removePoints(title.getCost());
				unlockedTitles.add(title);
				player.getAppearence().setTitle(title.getValue());
				player.getAppearence().generateAppearenceData();
				showPurchaseSuccess("You receive the title: " + title.getName());
			} else if (current instanceof LoyaltyAura) {
				LoyaltyAura aura = getAuraByButton(currentViewingSlot);
				if (aura == null) {
					System.out.println(currentViewingSlot + " was an invalid aura! Did not purchase");
					return;
				}
				if (!hasSufficientPoints(aura.pointCost)) {
					showInsufficientPoints(aura.pointCost);
					return;
				}
				removePoints(aura.pointCost);
				unlockedAuras.add(aura);
				player.getInventory().addItem(aura.itemId, 1);
				showPurchaseSuccess("You receive the aura: " + aura.name);
				sendPurchaseConfigs();
			} else if (current instanceof LoyaltyEffect) {
				LoyaltyEffect effect = getEffectByButton(currentViewingSlot);
				if (effect == null) {
					System.out.println(currentViewingSlot + " was an invalid effect! Did not purchase");
					return;
				}
				if (!hasSufficientPoints(effect.pointCost)) {
					showInsufficientPoints(effect.pointCost);
					return;
				}
				removePoints(effect.pointCost);
				unlockedEffects.add(effect);
				player.getInventory().addItem(effect.itemId, 1);
				showPurchaseSuccess("You receive the effect: " + effect.name);
				sendPurchaseConfigs();
			}
		}
	}

	public Favorites getFavorites() {
		return favorites;
	}

	public void setFavorites(Favorites favorites) {
		this.favorites = favorites;
	}

	private static final long serialVersionUID = 9162334808715849419L;

	/**
	 * The player object
	 */
	private transient Player player;

	/**
	 * The favourites object
	 */
	private Favorites favorites;

	/**
	 * The amount of points the player has
	 */
	private int points;

	/**
	 * The list of auras the user has unlocked
	 */
	private ArrayList<LoyaltyAura> unlockedAuras = new ArrayList<LoyaltyAura>();

	/**
	 * The list of auras the user has unlocked
	 */
	private ArrayList<LoyaltyEffect> unlockedEffects = new ArrayList<LoyaltyEffect>();

	/**
	 * The list of titles the user has unlocked
	 */
	private ArrayList<LoyaltyTitle> unlockedTitles = new ArrayList<LoyaltyTitle>();

	/**
	 * The slot the player is currently viewing
	 */
	private int currentViewingSlot = -1;

	/**
	 * The tab the player is currently viewing
	 */
	private int currentTabConfig = 0;

}

class Favorites implements Serializable {

	private static final long serialVersionUID = -4046181381165359996L;

	private int auraFavorites;

	public void setAuraFavorite(int bit) {
		auraFavorites |= bit;
	}

	public void removeAuraFavorite(int bit) {
		auraFavorites &= ~bit;
	}

	public int getAuraFavorites() {
		return auraFavorites;
	}

}