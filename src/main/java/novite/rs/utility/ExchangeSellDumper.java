package novite.rs.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.ItemConstants;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 23, 2014
 */
public class ExchangeSellDumper {

	public static void main(String... args) throws IOException {
		Config.get().load();
		Cache.init();
		while (true) {
			System.err.print("Dump all items containing: ");
			String input = TextIO.getlnString();
			if (input.equalsIgnoreCase("/exit")) {
				System.exit(1);
				return;
			}
			dumpItems(input);
		}
	}

	/**
	 * Dump items containing the name
	 * 
	 * @param name
	 */
	private static void dumpItems(String name) {
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			if (!ItemConstants.canExchange(i))
				continue;
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
			if (def.isNoted())
				continue;
			String itemName = def.getName();
			if (itemName.toLowerCase().contains(name.toLowerCase())) {
				write(itemName + ": " + i);
				System.out.println(itemName + ":" + i);
			}
		}
	}

	private static void write(String text) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/exchange/unlimited.txt", true)))) {
			out.println(text);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
