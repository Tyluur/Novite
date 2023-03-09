package novite.rs.networking.packet.impl;

import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.bosses.glacor.Glacyte;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.networking.codec.handlers.NPCHandler;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "9,31,28,67,92,66")
public class NPCInteractionPacketHandler extends PacketHandler {

	private final static int NPC_CLICK1_PACKET = 9;
	private final static int NPC_CLICK2_PACKET = 31;
	private final static int NPC_CLICK3_PACKET = 28;
	private final static int NPC_CLICK5_PACKET = 67;
	private final static int NPC_EXAMINE_PACKET = 92;
	private final static int ATTACK_NPC = 66;

	@Override
	public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
		//player.setPacketsDecoderPing(Utils.currentTimeMillis());
		switch (packetId) {
		case ATTACK_NPC:
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
				return;
			}
			if (player.getLockDelay() > Utils.currentTimeMillis()) {
				return;
			}
			stream.readByte128();
			int npcIndex = stream.readUnsignedShort128();
			NPC npc = World.getNPCs().get(npcIndex);
			if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || !npc.getDefinitions().hasAttackOption()) {
				return;
			}
			if (!player.getControllerManager().canAttack(npc)) {
				return;
			}
			if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (familiar == player.getFamiliar()) {
					player.getPackets().sendGameMessage("You can't attack your own familiar.");
					return;
				}
				if (!familiar.canAttack(player)) {
					player.getPackets().sendGameMessage("You can't attack this npc.");
					return;
				}
			} else if (!npc.isForceMultiAttacked()) {
				if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
					if (!(npc instanceof Glacyte)) {
						if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
							player.getPackets().sendGameMessage("You are already in combat.");
							return;
						}
						if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
							player.getPackets().sendGameMessage("This npc is already in combat.");
							return;
						}
					}
				}
			}
			if (!npc.canBeAttacked(player)) {
				return;
			}
			player.stopAll(false);
			player.getActionManager().setAction(new PlayerCombat(npc));
			break;
		case NPC_CLICK1_PACKET:
			NPCHandler.handleNPCInteraction(player, 1, stream);
			break;
		case NPC_CLICK2_PACKET:
			NPCHandler.handleNPCInteraction(player, 2, stream);
			break;
		case NPC_CLICK3_PACKET:
			NPCHandler.handleNPCInteraction(player, 3, stream);
			break;
		case NPC_EXAMINE_PACKET:
			NPCHandler.handleNPCInteraction(player, 4, stream);
			break;
		case NPC_CLICK5_PACKET:
			NPCHandler.handleNPCInteraction(player, 5, stream);
			break;
		}
	}

}
