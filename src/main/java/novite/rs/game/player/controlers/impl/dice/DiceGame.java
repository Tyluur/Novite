package novite.rs.game.player.controlers.impl.dice;

import java.util.Random;

import novite.rs.api.input.IntegerInputAction;
import novite.rs.game.Animation;
import novite.rs.game.ForceTalk;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.dice.DiceSession.Stages;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 29, 2014
 */
public class DiceGame extends Controller {

	private Player target;

	@Override
	public void start() {
		this.target = (Player) getArguments()[0];
		openOfferScreen();
	}

	public void openOfferScreen() {
		synchronized (this) {
			sendInterItems();
			sendOptions();
			player.getInterfaceManager().sendInterface(OFFER_SCREEN);
			player.getInterfaceManager().sendInventoryInterface(INVENTORY_SCREEN);

			player.getPackets().sendGlobalString(203, target.getDisplayName());
			int length = Utils.getInterfaceDefinitionsComponentsSize(OFFER_SCREEN);
			for (int i = 0; i < length; i++) {
				if (i == 36 || i == 35 || i == 17 || i == 19)
					continue;
				player.getPackets().sendIComponentText(OFFER_SCREEN, i, "");
			}
			player.getPackets().sendIComponentText(OFFER_SCREEN, 15, "Dicing With: " + target.getDisplayName());
			player.getPackets().sendIComponentText(OFFER_SCREEN, 36, "Your Dice Offer");
			player.getPackets().sendIComponentText(OFFER_SCREEN, 35, "Partner's Dice Offer");
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					closeOffer(CloseType.DECLINED);
				}
			});
		}
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, final int slotId, int packetId) {
		switch (interfaceId) {
		case REWARDS:
			return false;
		case OFFER_SCREEN:
			if (componentId == 16) {
				accept();
			} else if (componentId == 18) {
				player.closeInterfaces();
			} else if (componentId == 31) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					removeItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					removeItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					removeItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					removeItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int value) {
							if (value < 0 || slotId < 0) {
								return;
							}
							removeItem(slotId, value);
						}
					});
				}
			}
			return false;
		case INVENTORY_SCREEN:
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					addItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					addItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					addItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					addItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int value) {
							if (value < 0 || slotId < 0) {
								return;
							}
							addItem(slotId, value);
						}
					});
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
			return false;
		case CONFIRM_SCREEN:
			if (componentId == 22) {
				player.closeInterfaces();
			} else if (componentId == 21) {
				accept();
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean logout() {
		closeOffer(CloseType.DECLINED);
		player.getControllerManager().startController("DiceControler");
		return false;
	}

	public void accept() {
		synchronized (this) {
			if (target.getDiceSession().hasAccepted()) {
				if (player.getDiceSession().getCurrentStage() == Stages.OFFERING_ITEMS) {
					if (nextStage()) {
						getTargetGame().nextStage();
					}
				} else {
					player.setCloseInterfacesEvent(null);
					player.closeInterfaces();
					closeOffer(CloseType.ACCEPTED);
				}
			} else {
				player.getDiceSession().setAccepted(true);
				refreshBothStageMessage();
			}
		}
	}

	public boolean nextStage() {
		if (!isTrading()) {
			return false;
		}
		if (player.getDiceSession().getStake().getUsedSlots() == 0 || target.getDiceSession().getStake().getUsedSlots() == 0) {
			player.sendMessage("Both players must be risking something!");
			target.sendMessage("Both players must be risking something!");
			return false;
		}
		if (player.getInventory().getItems().getUsedSlots() + target.getDiceSession().getStake().getUsedSlots() > 28) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeOffer(CloseType.NO_SPACE);
			return false;
		}
		player.getDiceSession().setAccepted(false);
		player.getInterfaceManager().sendInterface(CONFIRM_SCREEN);
		player.getPackets().sendIComponentText(CONFIRM_SCREEN, 35, "<col=" + ChatColors.RED + ">There is NO WAY to reverse a dice duel if you change your mind.");
		player.getPackets().sendIComponentText(CONFIRM_SCREEN, 32, "Your Duel Offer");
		player.getPackets().sendIComponentText(CONFIRM_SCREEN, 33, target.getDisplayName() + "'s duel offer");
		player.getPackets().sendIComponentText(CONFIRM_SCREEN, 53, "Dicing With:");

		player.getDiceSession().setCurrentStage(Stages.CONFIRMING);
		player.getInterfaceManager().closeInventoryInterface();
		refreshBothStageMessage();
		return true;
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null) {
					return;
				}
				if (!isTrading()) {
					return;
				}
				if (!ItemConstants.isTradeable(item) && player.getRights() < 3) {
					player.getPackets().sendGameMessage("That item isn't tradeable.");
					return;
				}
				Item[] itemsBefore = player.getDiceSession().getStake().getItemsCopy();
				int maxAmount = player.getInventory().getItems().getNumberOf(item);
				if (amount < maxAmount) {
					item = new Item(item.getId(), amount);
				} else {
					item = new Item(item.getId(), maxAmount);
				}
				player.getDiceSession().getStake().add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void cancelAccepted() {
		player.getDiceSession().setAccepted(false);
		target.getDiceSession().setAccepted(false);
		refreshBothStageMessage();
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!isTrading()) {
				return;
			}
			if (!player.getInterfaceManager().containsInventoryInter()) {
				return;
			}
			synchronized (target.getTrade()) {
				Item item = player.getDiceSession().getStake().get(slot);
				if (item == null) {
					return;
				}
				Item[] itemsBefore = player.getDiceSession().getStake().getItemsCopy();
				int maxAmount = player.getDiceSession().getStake().getNumberOf(item);
				if (amount < maxAmount) {
					item = new Item(item.getId(), amount);
				} else {
					item = new Item(item.getId(), maxAmount);
				}
				player.getDiceSession().getStake().remove(slot, item);
				player.getInventory().addItem(item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	private boolean isTrading() {
		return target != null && World.containsPlayer(target.getUsername()) && player.getInterfaceManager().containsScreenInterface();
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = player.getDiceSession().getStake().getItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null && (item == null || item.getId() != itemsBefore[index].getId() || item.getAmount() < itemsBefore[index].getAmount())) {
					sendFlash(index);
				}
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public void refresh(int... slots) {
		player.getPackets().sendItems(90, false, player.getDiceSession().getStake());
		player.getPackets().sendItems(90, true, target.getDiceSession().getStake());
		target.getPackets().sendItems(90, false, target.getDiceSession().getStake());
		target.getPackets().sendItems(90, true, player.getDiceSession().getStake());
	}

	public void closeOffer(CloseType stage) {
		synchronized (this) {
			if (stage == CloseType.NO_SPACE) {
				player.getPackets().sendGameMessage("You don't have enough space in your inventory for this stake.");
				target.getPackets().sendGameMessage("Other player doesn't have enough space in their inventory for this stake.");

				closeInterfaces(player);
				closeInterfaces(target);

				returnStake(player);
				returnStake(target);
			} else if (stage == CloseType.DECLINED) {
				closeInterfaces(player);
				closeInterfaces(target);

				returnStake(player);
				returnStake(target);
			} else {
				closeInterfaces(player);
				closeInterfaces(target);
				target.lock();
				player.lock();

				rollDice(true, player, target);

				WorldTasksManager.schedule(new WorldTask() {

					int loop = 3;

					@Override
					public void run() {
						if (loop == 0) {
							target.setNextForceTalk(new ForceTalk("I rolled <col=db3535>" + target.getDiceSession().getRollResult() + "</col>!"));
							player.setNextForceTalk(new ForceTalk("I rolled <col=db3535>" + player.getDiceSession().getRollResult() + "</col>!"));

							Player winner = null, loser = null;
							if (player.getDiceSession().getRollResult() > target.getDiceSession().getRollResult()) {
								winner = player;
								loser = target;
							} else if (target.getDiceSession().getRollResult() > player.getDiceSession().getRollResult()) {
								winner = target;
								loser = player;
							} else if (target.getDiceSession().getRollResult() == player.getDiceSession().getRollResult()) {
								handleDraw();
								this.stop();
								return;
							}
							giveRewards(winner, loser);
							this.stop();
							return;
						} else {
							rollDice(false, player, target);
							target.setNextForceTalk(new ForceTalk("" + loop));
							player.setNextForceTalk(new ForceTalk("" + loop));
						}
						loop--;
					}
				}, 0, 1);

			}
		}
	}

	protected void handleDraw() {
		closeInterfaces(player);
		closeInterfaces(target);
		for (Item item : player.getDiceSession().getStake().toArray()) {
			if (item == null)
				continue;
			player.getInventory().addDroppable(item);
		}
		for (Item item : target.getDiceSession().getStake().toArray()) {
			if (item == null)
				continue;
			target.getInventory().addDroppable(item);
		}
		player.getDiceSession().getStake().clear();
		player.sendMessage("<col=" + ChatColors.MAROON + ">The dice duel was a draw! You receive your original bet.");
		target.getDiceSession().getStake().clear();
		target.sendMessage("<col=" + ChatColors.MAROON + ">The dice duel was a draw! You receive your original bet.");
		if (player != null) {
			player.unlock();
			player.getControllerManager().startController("DiceControler");
		}
		if (target != null) {
			target.unlock();
			target.getControllerManager().startController("DiceControler");
		}
	}

	private void giveRewards(final Player winner, final Player loser) {
		if (winner == null) { // was a draw
			closeInterfaces(target);
			closeInterfaces(winner);
		} else {
			final ItemsContainer<Item> rewards = new ItemsContainer<>(46, false);
			rewards.addAll(winner.getDiceSession().getStake());
			rewards.addAll(loser.getDiceSession().getStake());

			loser.getDiceSession().getStake().clear();
			winner.getDiceSession().getStake().clear();

			int length = Utils.getInterfaceDefinitionsComponentsSize(REWARDS);

			for (int i = 0; i < length; i++) {
				player.getPackets().sendIComponentText(REWARDS, i, "");
			}

			winner.getPackets().sendIComponentText(REWARDS, 15, "The Dice-Duel Spoils");
			winner.getPackets().sendIComponentText(REWARDS, 18, "Items");
			winner.getPackets().sendIComponentText(REWARDS, 17, "Claim");
			winner.getPackets().sendIComponentText(REWARDS, 32, "" + loser.getSkills().getCombatLevelWithSummoning());
			winner.getPackets().sendIComponentText(REWARDS, 29, "The Loser");
			winner.getPackets().sendIComponentText(REWARDS, 30, "Name");
			winner.getPackets().sendIComponentText(REWARDS, 31, "Combat Level");
			winner.getPackets().sendGlobalString(274, " " + Utils.formatPlayerNameForDisplay(loser.getUsername()));

			winner.getInterfaceManager().sendInterface(REWARDS);
			winner.getPackets().sendIComponentSettings(REWARDS, 28, 0, 28, 1026);
			winner.getPackets().sendRunScript(149, new Object[] { "", "", "", "", "", -1, 0, 6, 6, 136, REWARDS << 16 | 28 });
			winner.getPackets().sendItems(136, false, rewards);

			winner.sendMessage("<col=" + ChatColors.MAROON + ">Congratulations! You have won the dice-duel.");
			loser.sendMessage("Oh no, you lost! Try again soon.");
			winner.setCloseInterfacesEvent(new Runnable() {

				@Override
				public void run() {
					if (winner.getDiceSession().isGaveItems())
						return;
					for (Item item : rewards.getItems()) {
						if (item == null)
							continue;
						winner.getInventory().addDroppable(item);
					}
					winner.getDiceSession().setGaveItems(true);
				}
			});
		}
		if (player != null) {
			player.unlock();
			player.getControllerManager().startController("DiceControler");
		}
		if (target != null) {
			target.unlock();
			target.getControllerManager().startController("DiceControler");
		}
	}

	private void rollDice(boolean change, Player... players) {
		for (Player p : players) {
			p.setNextAnimation(new Animation(11900));
			p.setNextGraphics(new Graphics(2069));
			if (change) {
				p.getDiceSession().setRollResult(getRandom(1, 100));
			}
		}
	}

	public static int getRandom(int lowest, int highest) {
		Random r = new Random();
		if (lowest > highest) {
			return -1;
		}
		long range = (long) highest - (long) lowest + 1;
		long fraction = (long) (range * r.nextDouble());
		int numberRolled = (int) (fraction + lowest);
		return numberRolled;
	}

	public void refreshBothStageMessage() {
		try {
			refreshStageMessage(player.getDiceSession().getCurrentStage() == Stages.CONFIRMING ? false : true);
			getTargetGame().refreshStageMessage(player.getDiceSession().getCurrentStage() == Stages.CONFIRMING ? false : true);
		} catch (Exception e) {

		}
	}

	public DiceGame getTargetGame() {
		if (target.getControllerManager().getController() != null && target.getControllerManager().getController() instanceof DiceGame) {
			return (DiceGame) target.getControllerManager().getController();
		}
		return null;
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().sendIComponentText(firstStage ? OFFER_SCREEN : CONFIRM_SCREEN, firstStage ? 37 : 34, getAcceptMessage(firstStage));
	}

	public String getAcceptMessage(boolean firstStage) {
		if (player.getDiceSession().hasAccepted()) {
			return "Waiting for other player...";
		}
		if (target.getDiceSession().hasAccepted()) {
			return "Other player has accepted.";
		}
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public void closeInterfaces(Player p) {
		try {
			if (p == null || p.getControllerManager() == null)
				return;
			p.getControllerManager().removeControlerWithoutCheck();
			p.getControllerManager().startController("DiceControler");

			p.setCloseInterfacesEvent(null);
			p.closeInterfaces();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void returnStake(Player p) {
		if (p.getDiceSession().isGaveItems())
			return;
		for (Item item : p.getDiceSession().getStake().getItems()) {
			if (item == null)
				continue;
			p.getInventory().addDroppable(item);
		}
		p.getDiceSession().getStake().clear();
		p.getDiceSession().setGaveItems(true);
	}

	public void sendFlash(int slot) {
		// player.getPackets().sendInterFlashScript(335, 32, 4, 7, slot);
		// target.getPackets().sendInterFlashScript(335, 32, 4, 7, slot);
	}

	public void sendOptions() {
		Object[] tparams1 = new Object[] { "", "", "", "Value<col=FF9040>", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 90, 335 << 16 | 31 };
		player.getPackets().sendRunScript(150, tparams1);
		player.getPackets().sendIComponentSettings(335, 31, 0, 27, 1150); // Access
		Object[] tparams3 = new Object[] { "", "", "", "", "", "", "", "", "Value<col=FF9040>", -1, 0, 7, 4, 90, 335 << 16 | 34 };
		player.getPackets().sendRunScript(695, tparams3);
		player.getPackets().sendIComponentSettings(335, 34, 0, 27, 1026); // Access
		Object[] tparams2 = new Object[] { "", "", "Lend", "Value<col=FF9040>", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 336 << 16 };
		player.getPackets().sendRunScript(150, tparams2);
		player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278); // Access
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, player.getDiceSession().getStake());
		target.getPackets().sendItems(90, true, player.getDiceSession().getStake());
	}

	public enum CloseType {
		ACCEPTED, DECLINED, NO_SPACE
	}

	private static final int OFFER_SCREEN = 335;
	private static final int CONFIRM_SCREEN = 334;
	private static final int INVENTORY_SCREEN = 336;
	private static final int REWARDS = 634;

}
