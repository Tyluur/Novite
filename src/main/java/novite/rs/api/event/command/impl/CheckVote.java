package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

import java.util.concurrent.TimeUnit;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 1, 2014
 */
public class CheckVote extends CommandSkeleton {

    @Override
    public Rights getRightsRequired() {
        return Rights.PLAYER;
    }

    @Override
    public String[] getCommandApplicable() {
        return new String[]{"auth", "check"};
    }

    @Override
    public void execute(Player player) {
        Long lastTime = (Long) player.getTemporaryAttributtes().get("last_auth_sent");
        if (lastTime == null || (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastTime) > 10)) {
//			VoteVerification.checkRTLVote(player); removed as part of runetoplist dependency removal
            player.getTemporaryAttributtes().put("last_auth_sent", System.currentTimeMillis());
        } else {
            player.sendMessage("You can only use this command once every 10 seconds...");
        }
    }

}