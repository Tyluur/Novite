package novite.rs.utility.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.utility.Utils;

public class NPCListDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("information/npcList.txt");
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//REVISION 667/728\n");
		writer.flush();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			writer.append(id + " - " + def.getName());
			writer.newLine();
			System.out.println(id + " - " + def.getName());
			writer.flush();
		}
		writer.close();
	}

}
