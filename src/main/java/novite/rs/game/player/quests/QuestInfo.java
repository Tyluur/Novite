package novite.rs.game.player.quests;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 28, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QuestInfo {

	  Class<? extends Enum<?>> enumClass();
	
}
