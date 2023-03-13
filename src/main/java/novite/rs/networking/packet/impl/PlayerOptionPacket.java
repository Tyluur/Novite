package novite.rs.networking.packet.impl;
 
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.RouteEvent;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.player.actions.PlayerFollow;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.packet.PacketHandler;
import novite.rs.networking.packet.PacketInformation;
import novite.rs.utility.Utils;
 
/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@PacketInformation(listeners = "14,53,43,77,46")
public class PlayerOptionPacket extends PacketHandler {
 
    private final static int PLAYER_OPTION_1 = 14;
    private final static int PLAYER_OPTION_2 = 53;
    private final static int PLAYER_OPTION_4 = 77;
    private final static int PLAYER_OPTION_9 = 43;
    private final static int ACCEPT_TRADE_CHAT_PACKET = 46;
 
    @Override
    public void handle(Player player, Integer packetId, Integer length, InputStream stream) {
       // player.setPacketsDecoderPing(Utils.currentTimeMillis());
        switch (packetId) {
        case PLAYER_OPTION_1:
            handlePlayerOption1(player, packetId, length, stream);
            break;
        case PLAYER_OPTION_2:
            handlePlayerOption2(player, packetId, length, stream);
            break;
        case ACCEPT_TRADE_CHAT_PACKET:
        case PLAYER_OPTION_4:
            handlePlayerOption4(player, packetId, length, stream);
            break;
        case PLAYER_OPTION_9:
            handlePlayerOption9(player, packetId, length, stream);
            break;
        }
    }
 
    private void handlePlayerOption1(Player player, Integer packetId, Integer length, InputStream stream) {
        if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
            return;
        }
        boolean forceRun = stream.readByte() == 1;
        int playerIndex = stream.readUnsignedShort();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())) {
            return;
        }
        if (player.getLockDelay() > Utils.currentTimeMillis() || !player.getControllerManager().canPlayerOption1(p2)) {
            return;
        }
        if (!player.isCanPvp()) {
            return;
        }
        if (!player.getControllerManager().canAttack(p2)) {
            return;
        }
        if (!player.isCanPvp() || !p2.isCanPvp()) {
            player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
            return;
        }
        if (forceRun) {
            player.setRun(forceRun);
        }
        if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
            if (player.getAttackedBy() != p2 && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
                player.getPackets().sendGameMessage("You are already in combat.");
                return;
            }
            if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
                if (p2.getAttackedBy() instanceof NPC) {
                    p2.setAttackedBy(player);
                } else {
                    player.getPackets().sendGameMessage("That player is already in combat.");
                    return;
                }
            }
        }
        player.stopAll(false);
        player.getActionManager().setAction(new PlayerCombat(p2));
    }
 
    private void handlePlayerOption2(Player player, Integer packetId, Integer length, InputStream stream) {
        if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
            return;
        }
        stream.readByte();
        int playerIndex = stream.readUnsignedShort();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())) {
            return;
        }
        if (player.isLocked()) {
            return;
        }
        if (!player.getControllerManager().canPlayerOption2(p2)) {
            return;
        }
        player.stopAll(false);
        player.getActionManager().setAction(new PlayerFollow(p2));
    }
 
    private void handlePlayerOption4(final Player player, Integer packetId, Integer length, InputStream stream) {
        stream.readByte();
        int playerIndex = stream.readUnsignedShort();
        final Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())) {
            return;
        }
        if (player.getLockDelay() > Utils.currentTimeMillis()) {
            return;
        }
        if (!player.getControllerManager().canPlayerOption4(p2)) {
            return;
        }
        if (player.isCantTrade()) {
            player.getPackets().sendGameMessage("You are busy.");
            return;
        }
        player.stopAll(false);
 
        player.setRouteEvent(new RouteEvent(p2, new Runnable() {
 
            @Override
            public void run() {
                if (!p2.withinDistance(player, 14)) {
                    player.getPackets().sendGameMessage("Unable to find target: " + p2.getDisplayName());
                    return;
                }
                if (p2.getInterfaceManager().containsScreenInterface() || p2.isCantTrade()) {
                    player.getPackets().sendGameMessage("The other player is busy.");
                    return;
                }
                player.turnTo(p2);
                if (p2.getTemporaryAttributtes().get("TradeTarget") == player) {
                    p2.getTemporaryAttributtes().remove("TradeTarget");
                    player.getTrade().openTrade(p2);
                    p2.getTrade().openTrade(player);
                    return;
                }
                if (player.getControllerManager().getController() != null) {
                    Controller controller = player.getControllerManager().getController();
                    if (controller.getClass().getSimpleName().toLowerCase().contains("dice")) {
                        player.getDialogueManager().startDialogue(SimpleMessage.class, "You cannot trade in here.");
                        return;
                    }
                }
                player.getTemporaryAttributtes().put("TradeTarget", p2);
                player.getPackets().sendGameMessage("Sending " + p2.getDisplayName() + " a request...");
                p2.getPackets().sendTradeRequestMessage(player);
            }
        }, true));
    }
 
    private void handlePlayerOption9(Player player, Integer packetId, Integer length, InputStream stream) {
        boolean forceRun = stream.readByte() == 1;
        int playerIndex = stream.readUnsignedShort();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())) {
            return;
        }
        if (player.isLocked()) {
            return;
        }
        if (forceRun) {
            player.setRun(forceRun);
        }
        player.stopAll();
        ClansManager.viewInvite(player, p2);
    }
 
}