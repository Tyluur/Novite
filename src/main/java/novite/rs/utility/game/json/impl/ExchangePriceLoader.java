package novite.rs.utility.game.json.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import novite.rs.Constants;
import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.utility.Config;
import novite.rs.utility.Utils;
import novite.rs.utility.game.item.CustomItemPrices;
import novite.rs.utility.game.item.ExchangeItem;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.JsonLoader;

import com.google.gson.reflect.TypeToken;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 22, 2014
 */
public class ExchangePriceLoader extends JsonLoader<ExchangeItem> {

	public static void main(String... args) throws IOException {
		Config.get().load();
		Cache.init();
		JsonHandler.initialize();

		ExchangePriceLoader loader = ((ExchangePriceLoader) JsonHandler.getJsonLoader(ExchangePriceLoader.class));
		List<ExchangeItem> list = new ArrayList<>();
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
			ExchangeItem item = new ExchangeItem(itemId);
			item.getPrices().add(def.getValue());
			list.add(item);

			System.out.println("Finished " + itemId);
		}
		loader.save(list);
	}

	/**
	 * Adds the price of the item to the list
	 * 
	 * @param item
	 *            The item id
	 * @param price
	 *            The price of the item
	 */
	public void addPrice(int item, int price) {
		synchronized (LOCK) {
			ExchangeItem exchangeItem = exchangeItems.get(item);
			if (exchangeItem == null) {
				System.err.println("[SEVERE] No grand exchange item found for id: " + item + " when adding price.");
				return;
			}
			List<Integer> prices = exchangeItem.getPrices();
			prices.add(price);
			List<ExchangeItem> list = load();
			ListIterator<ExchangeItem> it$ = list.listIterator();

			while (it$.hasNext()) {
				ExchangeItem listItem = it$.next();
				if (listItem.getItemId() == item) {
					it$.remove();
					break;
				}
			}
			list.add(exchangeItem);
			save(list);
			initialize();

			//System.out.println("Added exchange price for " + ItemDefinitions.getItemDefinitions(item).getName() + "[" + item + "] in " + stopwatch.elapsed() + " ms.");
		}
	}

	@Override
	public void initialize() {
		synchronized (LOCK) {
			exchangeItems.clear();
			for (ExchangeItem item : load()) {
				exchangeItems.put(item.getItemId(), item);
			}
			if (unlimitedPrices.size() == 0) {
				try {
					List<String> list = (ArrayList<String>) Files.readAllLines(new File("./data/exchange/unlimited_prices.txt").toPath(), Charset.defaultCharset());
					for (String line : list) {
						String[] data = line.split(":");
						if (!line.contains(",")) {
							unlimitedPrices.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
						} else {
							String[] digits = data[0].split(",");
							for (String digit : digits) {
								unlimitedPrices.put(Integer.parseInt(digit), Integer.parseInt(data[1]));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the average price of an item from the grand exchange
	 * 
	 * @param item
	 *            The item
	 * @return
	 */
	public int getAveragePrice(int item) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item);;
		if (defs.isNoted()) {
			item = defs.getCertId();
		}
		ExchangeItem exchangeItem = exchangeItems.get(item);
		if (exchangeItem == null) {
			System.err.println("[SEVERE] No grand exchange item found for id: " + item);
			return -1;
		}
		return exchangeItem.getAveragePrice();
	}

	/**
	 * Gets the price of an item for its infinite quantity stock
	 * 
	 * @param itemId
	 *            The item
	 * @return
	 */
	public static int getInfiniteQuantityPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.isNoted()) {
			itemId = defs.getCertId();
		}
		CustomItemPrices prices = CustomItemPrices.getItemPrice(itemId);
		if (prices != null) {
			return prices.getPrice();
		}
		Integer mapPrice = unlimitedPrices.get(itemId);
		if (mapPrice != null) {
			return mapPrice;
		} else {
			return ItemDefinitions.getItemDefinitions(itemId).getValue();
		}
	}

	@Override
	public String getFileLocation() {
		return Constants.FILES_PATH + "exchange/prices.json";
	}

	@Override
	public List<ExchangeItem> load() {
		List<ExchangeItem> autospawns = null;
		String json = null;
		try {
			File file = new File(getFileLocation());
			if (!file.exists()) {
				return null;
			}
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			json = new String(chars);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		autospawns = gson.fromJson(json, new TypeToken<List<ExchangeItem>>() {
		}.getType());
		return autospawns;
	}

	/**
	 * The map of exchange items with their prices
	 */
	private Map<Integer, ExchangeItem> exchangeItems = new HashMap<>();

	/**
	 * The map of the prices of the unlimited items
	 */
	private static Map<Integer, Integer> unlimitedPrices = new HashMap<>();

	/**
	 * The object to synchronize with
	 */
	private static final Object LOCK = new Object();
}
