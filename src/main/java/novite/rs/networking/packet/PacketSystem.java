package novite.rs.networking.packet;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
public class PacketSystem {

	/**
	 * Loads all packet handlers
	 */
	public static void load() {
		for (Object packet : FileClassLoader.getClassesInDirectory(PacketSystem.class.getPackage().getName() + ".impl")) {
			PacketHandler skeleton = (PacketHandler) packet;
			if (packet.getClass().getAnnotations().length == 0)
				throw new RuntimeException(packet.getClass() + " has no @PacketInfo attribute.");
			for (Annotation annotation : packet.getClass().getAnnotations()) {
				if (annotation instanceof PacketInformation) {
					PacketInformation info = (PacketInformation) annotation;
					String listeners = info.listeners();
					String[] split = listeners.split(",");
					for (String listener : split) {
						Integer packetId = Integer.valueOf(listener);
						LISTENERS.put(packetId, skeleton);
					}
				}
			}
		}
	}

	/**
	 * The array of loaded packets
	 */
	private final static Map<Integer, PacketHandler> LISTENERS = new HashMap<>();

	/**
	 * Gets the packet handler by the packet id
	 * 
	 * @param packetId
	 *            The packet id
	 * @return
	 */
	public static PacketHandler getHandler(int packetId) {
		PacketHandler handler = LISTENERS.get(packetId);
		return handler;
	}

}
