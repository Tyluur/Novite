package novite.rs.api.event.command.impl;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class LoopInterface extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "loopi" };
	}

	@Override
	public void execute(final Player player) {
		final int inter = Integer.parseInt(cmd[1]);
		final boolean save = Boolean.parseBoolean(cmd[2]);
		WorldTasksManager.schedule(new WorldTask() {

			int interfaceId = inter;

			@Override
			public void run() {
				boolean stop = player.getTemporaryAttributtes().remove("stop_loop") != null;
				if (stop || !World.containsPlayer(player.getUsername())) {
					stop();
				} else {
					player.sendMessage("Sent interface: " + interfaceId);
					System.out.println("Sent interface: " + interfaceId);
					player.getInterfaceManager().sendInterface(interfaceId);
					final int count = interfaceId;
					if (save) {
						Robot robot;
						try {
							robot = new Robot();
							BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
							ImageIO.write(screenShot, "PNG", new File(System.getProperty("user.home") + "/Desktop/Screenies/FOOLISH_NAME" + count + ".png"));
						} catch (AWTException | IOException e) {
							e.printStackTrace();
						}
					}
					interfaceId = interfaceId + 1;
				}
			}
		}, 3, 1);
	}

}
