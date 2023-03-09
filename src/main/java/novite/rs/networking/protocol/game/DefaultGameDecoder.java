package novite.rs.networking.protocol.game;

import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.api.input.StringInputAction;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.randoms.RandomEventManager;
import novite.rs.game.player.controlers.impl.StartTutorial;
import novite.rs.game.player.dialogues.impl.ExperienceRateSelector;
import novite.rs.networking.Session;
import novite.rs.networking.codec.Decoder;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketSystem;
import novite.rs.networking.packet.impl.ButtonPacketHandler;
import novite.rs.networking.packet.impl.ChatPacketHandler;
import novite.rs.networking.packet.impl.WalkingPacketHandler;
import novite.rs.utility.Utils;

public final class DefaultGameDecoder extends Decoder {

	private static final byte[] PACKET_SIZES = new byte[256];

	static {
		loadPacketSizes();
	}

	public static void loadPacketSizes() {
		for (int id = 0; id < 256; id++) {
			PACKET_SIZES[id] = -4;
		}
		PACKET_SIZES[64] = 8;
		PACKET_SIZES[18] = 8;
		PACKET_SIZES[25] = 8;
		PACKET_SIZES[41] = -1;
		PACKET_SIZES[14] = 3;
		PACKET_SIZES[46] = 3;
		PACKET_SIZES[87] = 6;
		PACKET_SIZES[47] = 9;
		PACKET_SIZES[57] = 3;
		PACKET_SIZES[67] = 3;
		PACKET_SIZES[91] = 8;
		PACKET_SIZES[24] = 7;
		PACKET_SIZES[73] = 16;
		PACKET_SIZES[40] = 11;
		PACKET_SIZES[36] = -1;
		PACKET_SIZES[74] = -1;
		PACKET_SIZES[31] = 3;
		PACKET_SIZES[54] = 6;
		PACKET_SIZES[12] = 5;
		PACKET_SIZES[23] = 1;
		PACKET_SIZES[9] = 3;
		PACKET_SIZES[17] = -1;
		PACKET_SIZES[44] = -1;
		PACKET_SIZES[88] = -1;
		PACKET_SIZES[42] = 17;
		PACKET_SIZES[49] = 3;
		PACKET_SIZES[21] = 15;
		PACKET_SIZES[59] = -1;
		PACKET_SIZES[37] = -1;
		PACKET_SIZES[6] = 8;
		PACKET_SIZES[55] = 7;
		PACKET_SIZES[69] = 9;
		PACKET_SIZES[26] = 16;
		PACKET_SIZES[39] = 12;
		PACKET_SIZES[71] = 4;
		PACKET_SIZES[22] = 2;
		PACKET_SIZES[32] = -1;
		PACKET_SIZES[79] = -1;
		PACKET_SIZES[89] = 4;
		PACKET_SIZES[90] = -1;
		PACKET_SIZES[15] = 4;
		PACKET_SIZES[72] = -2;
		PACKET_SIZES[20] = 8;
		PACKET_SIZES[92] = 3;
		PACKET_SIZES[82] = 3;
		PACKET_SIZES[28] = 3;
		PACKET_SIZES[81] = 8;
		PACKET_SIZES[7] = -1;
		PACKET_SIZES[4] = 8;
		PACKET_SIZES[60] = -1;
		PACKET_SIZES[13] = 2;
		PACKET_SIZES[52] = 8;
		PACKET_SIZES[65] = 11;
		PACKET_SIZES[85] = 2;
		PACKET_SIZES[86] = 7;
		PACKET_SIZES[78] = -1;
		PACKET_SIZES[83] = 18;
		PACKET_SIZES[27] = 7;
		PACKET_SIZES[2] = 9;
		PACKET_SIZES[93] = 1;
		PACKET_SIZES[70] = -1;
		PACKET_SIZES[1] = -1;
		PACKET_SIZES[8] = -1;
		PACKET_SIZES[11] = 9;
		PACKET_SIZES[0] = 9;
		PACKET_SIZES[51] = -1;
		PACKET_SIZES[5] = 4;
		PACKET_SIZES[45] = 7;
		PACKET_SIZES[75] = 4;
		PACKET_SIZES[53] = 3;
		PACKET_SIZES[33] = 0;
		PACKET_SIZES[50] = 3;
		PACKET_SIZES[76] = 9;
		PACKET_SIZES[80] = -1;
		PACKET_SIZES[77] = 3;
		PACKET_SIZES[68] = -1;
		PACKET_SIZES[43] = 3;
		PACKET_SIZES[30] = -1;
		PACKET_SIZES[19] = 3;
		PACKET_SIZES[16] = 0;
		PACKET_SIZES[34] = 4;
		PACKET_SIZES[48] = 0;
		PACKET_SIZES[56] = 0;
		PACKET_SIZES[58] = 2;
		PACKET_SIZES[10] = 8;
		PACKET_SIZES[35] = 7;
		PACKET_SIZES[84] = 6;
		PACKET_SIZES[66] = 3;
		PACKET_SIZES[61] = 8;
		PACKET_SIZES[29] = -1;
		PACKET_SIZES[62] = 3;
		PACKET_SIZES[3] = 4;
		PACKET_SIZES[63] = 4;
		PACKET_SIZES[73] = 16;
		PACKET_SIZES[38] = -1;
	}

	private Player player;

	public DefaultGameDecoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	@Override
	public void decode(InputStream stream) {
		while (stream.getRemaining() > 0 && session.getChannel().isConnected() && !player.hasFinished()) {
			int packetId = stream.readUnsignedByte();
			if (packetId >= PACKET_SIZES.length && Constants.DEBUG) {
				System.out.println("PacketId " + packetId + " has fake packet id.");
				break;
			}
			int length = PACKET_SIZES[packetId];
			if (length == -1) {
				length = stream.readUnsignedByte();
			} else if (length == -2) {
				length = stream.readUnsignedShort();
			} else if (length == -3) {
				length = stream.readInt();
			} else if (length == -4) {
				length = stream.getRemaining();
				if (Constants.DEBUG) {
					System.out.println("Invalid size for PacketId " + packetId + ". Size guessed to be " + length);
				}
			}
			if (length > stream.getRemaining()) {
				length = stream.getRemaining();
				if (Constants.DEBUG) {
					System.out.println("PacketId " + packetId + " has fake size. - expected size " + length);
				}
			}
			int startOffset = stream.getOffset();
			if (player.controlerAvailable() && World.containsPlayer(player.getUsername()) && ((TimeUnit.MILLISECONDS.toMinutes(player.getActionTime()) > 20) || player.getTemporaryAttributtes().remove("random_event_requested") != null)) {
				RandomEventManager.get().startRandomEvent(player);
				player.setActionTime(0);
			}
			PacketHandler handler = PacketSystem.getHandler(packetId);
			player.setPacketsDecoderPing(Utils.currentTimeMillis());
			if (handler != null) {
				if (player.getFacade().getModifiers() == null || player.getFacade().getModifiers()[0] == 0.0 && !(player.getControllerManager().getController() instanceof StartTutorial)) {
					if (handler.getClass() == WalkingPacketHandler.class || handler.getClass() == ChatPacketHandler.class || handler.getClass() == ButtonPacketHandler.class) {
						player.getDialogueManager().startDialogue(ExperienceRateSelector.class);
						return;
					}
				}
				if (player.requiresEmailSet() && !(player.getControllerManager().getController() instanceof StartTutorial)) {
					if (handler.getClass() == WalkingPacketHandler.class || handler.getClass() == ChatPacketHandler.class || handler.getClass() == ButtonPacketHandler.class) {
						player.getPackets().sendInputLongTextScript("Enter Email:", new StringInputAction() {

							@Override
							public void handle(String input) {
								player.getFacade().setEmail(input);
							}
						});
						return;
					}
				}
				try {
					handler.handle(player, packetId, length, stream);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (Constants.DEBUG)
					System.out.println("No packet handler for opcode: " + packetId);
			}
			stream.setOffset(startOffset + length);
		}
	}

	public final static int ACTION_BUTTON1_PACKET = 61;
	public final static int ACTION_BUTTON2_PACKET = 64;
	public final static int ACTION_BUTTON3_PACKET = 4;
	public final static int ACTION_BUTTON4_PACKET = 52;
	public final static int ACTION_BUTTON5_PACKET = 81;
	public final static int ACTION_BUTTON6_PACKET = 18;
	public final static int ACTION_BUTTON7_PACKET = 10;
	public final static int ACTION_BUTTON8_PACKET = 25;
	public final static int ACTION_BUTTON9_PACKET = 91;
	public final static int ACTION_BUTTON10_PACKET = 20;

}