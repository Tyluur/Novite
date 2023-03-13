package novite.rs.networking.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketInformation {
	
	String listeners();

}
