package novite.rs.networking.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 19, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketInformation {
	
	String listeners();

}
