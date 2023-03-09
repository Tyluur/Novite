package novite.rs.utility.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.cache.loaders.ObjectDefinitions;
import novite.rs.utility.Utils;

public class RunListDumpers {

	public static void main(String[] args) throws IOException {
		System.out.print("Dumping Npc List");
		dumpnpcs();
		System.out.print("Dumping Item List");
		dumpitems();
		System.out.print("Dumping Object List");
		dumpobjects();
		System.out.print("Finished Dumping the Lists");
	}

	public static void dumpnpcs() throws IOException {
		Cache.init();
		File file = new File("./lists/npcList.txt");
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 667.706\n");
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

	public static void dumpobjects() throws IOException {
		Cache.init();
		File file = new File("./lists/objectList.txt");
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 667.706\n");
		writer.flush();
		for (int id = 0; id < Utils.getObjectDefinitionsSize(); id++) {
			ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(id);
			writer.append(id + " - " + def.name);
			writer.newLine();
			System.out.println(id + " - " + def.name);
			writer.flush();
		}
		writer.close();
	}

	public static void dumpitems() throws IOException {
		Cache.init();
		File file = new File("./lists/itemlist.txt");
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 667.706\n");
		writer.flush();
		for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
			writer.append(id + " - " + def.getName() + "\n");
			writer.newLine();
			System.out.println(id + " - " + def.getName());
			writer.flush();
		}
		writer.close();
	}
}