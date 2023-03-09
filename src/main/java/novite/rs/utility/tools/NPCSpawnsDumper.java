package novite.rs.utility.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.networking.codec.stream.InputStream;

public class NPCSpawnsDumper {

	private static int writtenCount;

	public static final void main(String[] args) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter("data/npcs/unpackedSpawnsList.txt", true));
		System.out.println("Initing Cache...");
		Cache.init();
		System.out.println("Initing Data File...");
		for (int regionId = 0; regionId < 20000; regionId++) {

			dumpRegionNPCs(regionId, out);
		}
		out.close();
		System.out.println("found " + writtenCount + " npc spawns on cache.");

	}

	public static final void dumpRegionNPCs(int regionId, BufferedWriter writer) throws IOException {
		writer.flush();
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;

		int npcSpawnsContainerId = Cache.STORE.getIndexes()[5].getArchiveId("n" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		if (npcSpawnsContainerId == -1) {
			return;
		}
		byte[] npcSpawnsContainerData = Cache.STORE.getIndexes()[5].getFile(npcSpawnsContainerId, 0, null);
		if (npcSpawnsContainerData == null) {
			return;
		}
		System.out.println(regionId);
		InputStream stream = new InputStream(npcSpawnsContainerData);
		while (stream.getRemaining() > 0) {
			int hash = stream.readUnsignedShort();
			int npcId = stream.readUnsignedShort();
			int plane = hash >> 758085070;
			int localX = (0x1f92 & hash) >> -585992921;
			int x = regionX + localX;
			int localY = 0x3f & hash;
			int y = regionY + localY;
			writer.newLine();
			writer.write(npcId + ":" + x + ":" + y + ":" + plane);
			writer.flush();
			writtenCount++;
		}
	}

}
