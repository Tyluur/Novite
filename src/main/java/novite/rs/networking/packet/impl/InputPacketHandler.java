package novite.rs.networking.packet.impl;

import novite.rs.api.input.IntegerInputAction;
import novite.rs.api.input.StringInputAction;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "3,59,7")
public class InputPacketHandler extends PacketHandler {

	private final static int ENTER_INTEGER_PACKET = 3;
	private final static int ENTER_STRING_PACKET = 59;
	private final static int ENTER_LONG_STRING_PACKET = 7;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		switch (packetId) {
		case ENTER_INTEGER_PACKET:
			handleIntegerInput(player, packetId, length, stream);
			break;
		case ENTER_STRING_PACKET:
			handleStringInput(player, packetId, length, stream);
			break;
		case ENTER_LONG_STRING_PACKET:
			String value = stream.readString();
			if (player.getTemporaryAttributtes().get("long_string_input_action") != null) {
				StringInputAction action = (StringInputAction) player.getTemporaryAttributtes().remove("long_string_input_action");
				action.handle(value);
				return;
			}
			break;
		}
	}

	private void handleIntegerInput(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.isRunning() || player.isDead()) {
			return;
		}
		int value = stream.readInt();
		if (player.getTemporaryAttributtes().get("integer_input_action") != null) {
			IntegerInputAction action = (IntegerInputAction) player.getTemporaryAttributtes().remove("integer_input_action");
			action.handle(value);
			return;
		}
		if ((player.getInterfaceManager().containsInterface(762) && player.getInterfaceManager().containsInterface(763)) || player.getInterfaceManager().containsInterface(11)) {
			if (value < 0) {
				return;
			}
			Integer bank_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bank_item_X_Slot");
			if (bank_item_X_Slot == null) {
				return;
			}
			if (player.getTemporaryAttributtes().remove("bank_isWithdraw") != null) {
				player.getBank().withdrawItem(bank_item_X_Slot, value);
			} else {
				player.getBank().depositItem(bank_item_X_Slot, value, player.getInterfaceManager().containsInterface(11) ? false : true);
			}
		} else if (player.getInterfaceManager().containsInterface(206) && player.getInterfaceManager().containsInterface(207)) {
			if (value < 0) {
				return;
			}
			Integer pc_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("pc_item_X_Slot");
			if (pc_item_X_Slot == null) {
				return;
			}
			if (player.getTemporaryAttributtes().remove("pc_isRemove") != null) {
				player.getPriceCheckManager().removeItem(pc_item_X_Slot, value);
			} else {
				player.getPriceCheckManager().addItem(pc_item_X_Slot, value);
			}
		} else if (player.getInterfaceManager().containsInterface(671) && player.getInterfaceManager().containsInterface(665)) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) {
				return;
			}
			if (value < 0) {
				return;
			}
			Integer bob_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bob_item_X_Slot");
			if (bob_item_X_Slot == null) {
				return;
			}
			if (player.getTemporaryAttributtes().remove("bob_isRemove") != null) {
				player.getFamiliar().getBob().removeItem(bob_item_X_Slot, value);
			} else {
				player.getFamiliar().getBob().addItem(bob_item_X_Slot, value);
			}
		} else if (player.getTemporaryAttributtes().get("skillId") != null) {
			int skillId = (Integer) player.getTemporaryAttributtes().remove("skillId");
			if (skillId == Skills.HITPOINTS && value == 1) {
				value = 10;
			} else if (value < 1) {
				value = 1;
			} else if (value > 99) {
				value = 99;
			}
			player.getSkills().set(skillId, value);
			player.getSkills().setXp(skillId, Skills.getXPForLevel(value));
			player.getAppearence().generateAppearenceData();
			player.getDialogueManager().finishDialogue();
		}
	}

	private void handleStringInput(Player player, Integer packetId, Integer length, InputStream stream) {
		if (!player.isRunning() || player.isDead()) {
			return;
		}
		String value = stream.readString();
		if (player.getInterfaceManager().containsInterface(1108)) {
			player.getFriendsIgnores().setChatPrefix(value);
		}
		if (player.getTemporaryAttributtes().get("string_input_action") != null) {
			StringInputAction action = (StringInputAction) player.getTemporaryAttributtes().remove("string_input_action");
			action.handle(value);
			return;
		}
	}

}
