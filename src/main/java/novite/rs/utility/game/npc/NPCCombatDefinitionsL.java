package novite.rs.utility.game.npc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.utility.Utils;

public final class NPCCombatDefinitionsL {

	private final static HashMap<Integer, NPCCombatDefinitions> npcCombatDefinitions = new HashMap<Integer, NPCCombatDefinitions>();
	private static final String PACKED_PATH = "data/npcs/packedCombatDefinitions.ncd";
	public final static NPCCombatDefinitions DEFAULT_DEFINITION = new NPCCombatDefinitions(1000, -1, -1, -1, 5, 1, 33, 0, NPCCombatDefinitions.MELEE, -1, -1, NPCCombatDefinitions.PASSIVE);

	public static void main(String... args) throws IOException {
		dumpFile(PACKED_PATH);
	}

	private static void dumpFile(String fileLocation) throws IOException {
		try {
			Cache.init();
			RandomAccessFile in = new RandomAccessFile(fileLocation, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int hitpoints = buffer.getShort() & 0xffff;
				int attackAnim = buffer.getShort() & 0xffff;
				if (attackAnim == 65535) {
					attackAnim = -1;
				}
				int defenceAnim = buffer.getShort() & 0xffff;
				if (defenceAnim == 65535) {
					defenceAnim = -1;
				}
				int deathAnim = buffer.getShort() & 0xffff;
				if (deathAnim == 65535) {
					deathAnim = -1;
				}
				int attackDelay = buffer.get() & 0xff;
				int deathDelay = buffer.get() & 0xff;
				int respawnDelay = buffer.getInt();
				int maxHit = buffer.getShort() & 0xffff;
				int attackStyle = buffer.get() & 0xff;
				int attackGfx = buffer.getShort() & 0xffff;
				if (attackGfx == 65535) {
					attackGfx = -1;
				}
				int attackProjectile = buffer.getShort() & 0xffff;
				if (attackProjectile == 65535) {
					attackProjectile = -1;
				}
				int agressivenessType = buffer.get() & 0xff;
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(hitpoints, attackAnim, defenceAnim, deathAnim, attackDelay, deathDelay, respawnDelay, maxHit, attackStyle, attackGfx, attackProjectile, agressivenessType));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Iterator<Entry<Integer, NPCCombatDefinitions>> it = npcCombatDefinitions.entrySet().iterator();
		BufferedWriter writer = new BufferedWriter(new FileWriter("combatdumped.txt"));
		while (it.hasNext()) {
			Entry<Integer, NPCCombatDefinitions> entry = it.next();
			int id = entry.getKey();
			if (!Utils.npcExists(id)) {
				continue;
			}
			NPCCombatDefinitions defs = entry.getValue();
			if (defs.getAttackEmote() > 0 && defs.getDefenceEmote() > 0 && defs.getDeathEmote() > 0) {
				if (!Utils.animationExists(defs.getAttackEmote()) || !Utils.animationExists(defs.getDefenceEmote()) || !Utils.animationExists(defs.getDeathEmote())) {
					System.out.println("REDO NPC: " + id);
				}
			}
			String name = NPCDefinitions.getNPCDefinitions(id).getName();
			writer.write("//" + name);
			writer.newLine();
			writer.flush();
			writer.write(id + " - ");
			writer.write(defs.getHitpoints() + " ");
			writer.write(defs.getAttackEmote() + " ");
			writer.write(defs.getDefenceEmote() + " ");
			writer.write(defs.getDeathEmote() + " ");
			writer.write(defs.getAttackDelay() + " ");
			writer.write(defs.getDeathDelay() + " ");
			writer.write(defs.getRespawnDelay() + " ");
			writer.write(defs.getMaxHit() + " ");
			String style = "";
			switch (defs.getAttackStyle()) {
				case NPCCombatDefinitions.MELEE:
					style = "MELEE";
					break;
				case NPCCombatDefinitions.RANGE:
					style = "RANGE";
					break;
				case NPCCombatDefinitions.MAGE:
					style = "MAGE";
					break;
				case NPCCombatDefinitions.SPECIAL:
					style = "SPECIAL";
					break;
				case NPCCombatDefinitions.SPECIAL2:
					style = "SPECIAL2";
					break;

			}
			writer.write(style + " ");
			writer.write(defs.getAttackGfx() + " ");
			writer.write(defs.getAttackProjectile() + " ");
			String agressiveType = "";
			switch (defs.getAgressivenessType()) {
				case NPCCombatDefinitions.AGRESSIVE:
					agressiveType = "AGRESSIVE";
					break;
				case NPCCombatDefinitions.PASSIVE:
					agressiveType = "PASSIVE";
					break;
			}
			writer.write(agressiveType);
			writer.newLine();
			//15454 - 2000 -1 -1 -1 5 5 60 0 SPECIAL -1 -1 AGRESSIVE
		}
		writer.close();
	}

	public static void init() {
		if (new File(PACKED_PATH).exists()) {
			loadPackedNPCCombatDefinitions();
		} else {
			loadUnpackedNPCCombatDefinitions();
		}
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions(int npcId) {
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		if (def == null) {
			return DEFAULT_DEFINITION;
		}
		return def;
	}

	private static void loadUnpackedNPCCombatDefinitions() {
		int count = 0;
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
			BufferedReader in = new BufferedReader(new FileReader("data/npcs/unpackedCombatDefinitionsList.txt"));
			while (true) {
				String line = in.readLine();
				count++;
				if (line == null) {
					break;
				}
				if (line.startsWith("//")) {
					continue;
				}
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2) {
					out.close();
					in.close();
					throw new RuntimeException("Invalid NPC Combat Definitions line: " + count + ", " + line);
				}
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 12);
				if (splitedLine2.length != 12) {
					out.close();
					in.close();
					throw new RuntimeException("Invalid NPC Combat Definitions line: " + count + ", " + line);
				}
				int hitpoints = Integer.parseInt(splitedLine2[0]);
				int attackAnim = Integer.parseInt(splitedLine2[1]);
				int defenceAnim = Integer.parseInt(splitedLine2[2]);
				int deathAnim = Integer.parseInt(splitedLine2[3]);
				int attackDelay = Integer.parseInt(splitedLine2[4]);
				int deathDelay = Integer.parseInt(splitedLine2[5]);
				int respawnDelay = Integer.parseInt(splitedLine2[6]);
				int maxHit = Integer.parseInt(splitedLine2[7]);
				int attackStyle;
				if (splitedLine2[8].equalsIgnoreCase("MELEE")) {
					attackStyle = NPCCombatDefinitions.MELEE;
				} else if (splitedLine2[8].equalsIgnoreCase("RANGE")) {
					attackStyle = NPCCombatDefinitions.RANGE;
				} else if (splitedLine2[8].equalsIgnoreCase("MAGE")) {
					attackStyle = NPCCombatDefinitions.MAGE;
				} else if (splitedLine2[8].equalsIgnoreCase("SPECIAL")) {
					attackStyle = NPCCombatDefinitions.SPECIAL;
				} else if (splitedLine2[8].equalsIgnoreCase("SPECIAL2")) {
					attackStyle = NPCCombatDefinitions.SPECIAL2;
				} else {
					out.close();
					in.close();
					throw new RuntimeException("Invalid NPC Combat Definitions line: " + line);
				}
				int attackGfx = Integer.parseInt(splitedLine2[9]);
				int attackProjectile = Integer.parseInt(splitedLine2[10]);
				int agressivenessType;
				if (splitedLine2[11].equalsIgnoreCase("PASSIVE")) {
					agressivenessType = NPCCombatDefinitions.PASSIVE;
				} else if (splitedLine2[11].equalsIgnoreCase("AGRESSIVE")) {
					agressivenessType = NPCCombatDefinitions.AGRESSIVE;
				} else {
					out.close();
					in.close();
					throw new RuntimeException("Invalid NPC Combat Definitions line: " + line);
				}
				out.writeShort(npcId);
				out.writeShort(hitpoints);
				out.writeShort(attackAnim);
				out.writeShort(defenceAnim);
				out.writeShort(deathAnim);
				out.writeByte(attackDelay);
				out.writeByte(deathDelay);
				out.writeInt(respawnDelay);
				out.writeShort(maxHit);
				out.writeByte(attackStyle);
				out.writeShort(attackGfx);
				out.writeShort(attackProjectile);
				out.writeByte(agressivenessType);
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(hitpoints, attackAnim, defenceAnim, deathAnim, attackDelay, deathDelay, respawnDelay, maxHit, attackStyle, attackGfx, attackProjectile, agressivenessType));
			}
			in.close();
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void loadPackedNPCCombatDefinitions() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int hitpoints = buffer.getShort() & 0xffff;
				int attackAnim = buffer.getShort() & 0xffff;
				if (attackAnim == 65535) {
					attackAnim = -1;
				}
				int defenceAnim = buffer.getShort() & 0xffff;
				if (defenceAnim == 65535) {
					defenceAnim = -1;
				}
				int deathAnim = buffer.getShort() & 0xffff;
				if (deathAnim == 65535) {
					deathAnim = -1;
				}
				int attackDelay = buffer.get() & 0xff;
				int deathDelay = buffer.get() & 0xff;
				int respawnDelay = buffer.getInt();
				int maxHit = buffer.getShort() & 0xffff;
				int attackStyle = buffer.get() & 0xff;
				int attackGfx = buffer.getShort() & 0xffff;
				if (attackGfx == 65535) {
					attackGfx = -1;
				}
				int attackProjectile = buffer.getShort() & 0xffff;
				if (attackProjectile == 65535) {
					attackProjectile = -1;
				}
				int agressivenessType = buffer.get() & 0xff;
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(hitpoints, attackAnim, defenceAnim, deathAnim, attackDelay, deathDelay, respawnDelay, maxHit, attackStyle, attackGfx, attackProjectile, agressivenessType));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private NPCCombatDefinitionsL() {

	}

}
