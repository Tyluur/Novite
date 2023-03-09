package novite.rs.utility.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.utility.Utils;

public class ItemListDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("information/itemlist.txt");
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//RE-VISION 667/728\n");
		writer.flush();
		for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
			writer.append(id + " - " + def.getName() + "\n");
			// writer.append("AAAAAAAA"+id+"GGGGGGGGBBBBBBB"+def.getName()+"BBBBBBBCCCCCCCCC");
			// //Apache's SQL format
			writer.newLine();
			System.out.println(id + " - " + def.getName());
			writer.flush();
		}
		writer.close();
	}

}
