package novite.rs.engine.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import novite.Main;
import novite.rs.Constants;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.player.content.exchange.ExchangeOffer;
import novite.rs.game.player.content.exchange.ExchangeType;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ExchangeItemLoader;
import novite.rs.utility.game.json.impl.ExchangePriceLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 23, 2014
 */
public class ExchangeTask implements Runnable {

	/*
	 * public ExchangeTask() { setName("Exchange Thread");
	 * setPriority(Thread.MAX_PRIORITY); start(); }
	 */
	
	@Override
	public void run() {
		//while (!CoresManager.shutdown) {
			
		//}
	}
	
	public void process() {
		try {
			synchronized (ExchangeItemLoader.LOCK) {
				if (Main.STARTUP_TIME != -1) {
					ExchangeItemLoader loader = JsonHandler.<ExchangeItemLoader> getJsonLoader(ExchangeItemLoader.class);
					ExchangePriceLoader priceLoader = JsonHandler.<ExchangePriceLoader> getJsonLoader(ExchangePriceLoader.class);
					if (loader == null || priceLoader == null)
						return;
					synchronized (LOCK) {
						ArrayList<ExchangeOffer> list = new ArrayList<>(JsonHandler.<ExchangeItemLoader> getJsonLoader(ExchangeItemLoader.class).getExchangeOffers());
						list.trimToSize();
						ArrayList<ExchangeOffer> buyerList = getOffersByType(list, ExchangeType.BUY);
						ArrayList<ExchangeOffer> sellerList = getOffersByType(list, ExchangeType.SELL);
						if (buyerList.size() > 0) {
							for (ExchangeOffer buyOffer : buyerList) {
								/**
								 * So only the offers that are buying are
								 * processed
								 */
								if (buyOffer.isAborted() || buyOffer.isFinished())
									continue;
								final int buyPrice = buyOffer.getPrice();
								final int buy = buyOffer.getAmountRequested() - buyOffer.getAmountReceived();
								if (buy <= 0)
									continue;
								List<ExchangeOffer> sellers = getBestList(sellerList, ExchangeType.SELL, buyOffer.getItemId());
								for (ExchangeOffer sellOffer : sellers) {
									if (buyOffer.isFinished() || buyOffer.isAborted())
										continue;
									int sellPrice = sellOffer.getPrice();
									/**
									 * The selling offer matches the buy
									 * price
									 */
									if (buyPrice >= sellPrice) {
										/**
										 * The difference between the prices
										 */
										final int difference = buyPrice - sellPrice;
										int sell = sellOffer.getAmountRequested() - sellOffer.getAmountReceived();

										int newAmount = -1;
										if (buy > sell) {
											newAmount = sell;
										} else {
											newAmount = buy;
										}

										if ((buyOffer.getAmountReceived() + newAmount) > buyOffer.getAmountRequested()) {
											newAmount = buyOffer.getAmountRequested() - buyOffer.getAmountReceived();
										}

										if (Constants.DEBUG)
											System.err.println("Buying " + buyOffer.getAmountRequested() + "x" + " " + ItemDefinitions.getItemDefinitions(buyOffer.getItemId()).getName() + "" + ".[newAmount=" + newAmount + ", " + "amountReceived=" + buyOffer.getAmountReceived() + "" + ", sellAmount= " + sell + "]");

										if (newAmount == -1)
											continue;

										if (difference > 0) {
											buyOffer.setSurplus(buyOffer.getSurplus() + (difference * newAmount));
										}
										priceLoader.addPrice(buyOffer.getItemId(), sellPrice);
										priceLoader.addPrice(buyOffer.getItemId(), buyPrice);

										buyOffer.setAmountProcessed(buyOffer.getAmountProcessed() + newAmount);
										buyOffer.setAmountReceived(buyOffer.getAmountReceived() + newAmount);

										if (!sellOffer.isUnlimited()) {
											sellOffer.setAmountProcessed(sellOffer.getAmountProcessed() + newAmount);
											sellOffer.setAmountReceived(sellOffer.getAmountReceived() + newAmount);
										}

										loader.removeOffer(buyOffer);
										loader.addOffer(buyOffer);

										if (!sellOffer.isUnlimited()) {
											loader.removeOffer(sellOffer);
											loader.addOffer(sellOffer);
										}

										if (sellOffer.isUnlimited())
											sellOffer.notifyUpdated();

										buyOffer.notifyUpdated();
									}
								}
							}
						}
						if (sellerList.size() > 0) {
							for (ExchangeOffer sellOffer : sellerList) {
								/**
								 * So only the offers that are sell are
								 * processed
								 */
								if (sellOffer.isAborted() || sellOffer.isUnlimited() || sellOffer.isFinished())
									continue;
								int averagePrice = ExchangePriceLoader.getInfiniteQuantityPrice(sellOffer.getItemId());
								int sellPrice = sellOffer.getPrice();
								int fivePercent = (int) Math.ceil(averagePrice - (averagePrice) * 0.05);

								boolean saved = false;

								if (sellPrice <= fivePercent) {

									int amountNeeded = sellOffer.getAmountRequested() - sellOffer.getAmountReceived();

									sellOffer.setAmountProcessed(sellOffer.getAmountProcessed() + amountNeeded);
									sellOffer.setAmountReceived(amountNeeded);

									loader.saveProgress(sellOffer);
									sellOffer.notifyUpdated();
									saved = true;
								}

								if (!saved) {
									loader.saveProgress(sellOffer);
								}
							}
						}
					}
				}
			}
		//	Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a list of offers by the type
	 * 
	 * @param list
	 *            The list of offers
	 * @param type
	 *            The type
	 * @return
	 */
	private ArrayList<ExchangeOffer> getOffersByType(List<ExchangeOffer> list, ExchangeType type) {
		ArrayList<ExchangeOffer> offers = new ArrayList<ExchangeOffer>();
		ListIterator<ExchangeOffer> it$ = list.listIterator();
		while (it$.hasNext()) {
			ExchangeOffer offer = it$.next();
			if (offer.isFinished() || offer.isAborted()) {
				continue;
			}
			if (offer.getType() == type) {
				offers.add(offer);
			}
		}
		offers.trimToSize();
		return offers;
	}

	/**
	 * Creates a list of best offers for the type we're looking for
	 * 
	 * @param offers
	 *            The list of offers to search through
	 * @param type
	 *            The type of offer to sort for
	 * @param itemId
	 *            The id of the item
	 * @return
	 */
	public List<ExchangeOffer> getBestList(List<ExchangeOffer> offers, ExchangeType type, int itemId) {
		List<ExchangeOffer> sorted = new ArrayList<>();
		for (ExchangeOffer offer : offers) {
			if (offer.isAborted())
				continue;
			if (offer.getType() == type && offer.getItemId() == itemId) {
				sorted.add(offer);
			}
		}
		Collections.sort(sorted, new Comparator<ExchangeOffer>() {

			@Override
			public int compare(ExchangeOffer arg0, ExchangeOffer arg1) {
				return Integer.compare(arg0.getPrice(), arg1.getPrice());
			}
		});
		return sorted;
	}

	/**
	 * @return the instance
	 */
	public static ExchangeTask get() {
		return INSTANCE;
	}

	/**
	 * The sync object
	 */
	private static final Object LOCK = new Object();
	private static final ExchangeTask INSTANCE = new ExchangeTask();
	
}
