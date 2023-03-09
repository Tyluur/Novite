package novite.rs.networking.codec.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import novite.rs.Constants;
import novite.rs.api.event.EventListener.ClickOption;
import novite.rs.api.event.EventManager;
import novite.rs.cache.loaders.ObjectDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.ForceMovement;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.CastleWars;
import novite.rs.game.player.OwnedObjectManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.RouteEvent;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.BoxAction.HunterEquipment;
import novite.rs.game.player.actions.BoxAction.HunterNPC;
import novite.rs.game.player.actions.Cooking;
import novite.rs.game.player.actions.Cooking.Cookables;
import novite.rs.game.player.actions.Hunter;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.player.actions.Smithing.ForgingBar;
import novite.rs.game.player.actions.Smithing.ForgingInterface;
import novite.rs.game.player.actions.WaterFilling;
import novite.rs.game.player.actions.Woodcutting;
import novite.rs.game.player.actions.Woodcutting.TreeDefinitions;
import novite.rs.game.player.actions.mining.EssenceMining;
import novite.rs.game.player.actions.mining.EssenceMining.EssenceDefinitions;
import novite.rs.game.player.actions.mining.Mining;
import novite.rs.game.player.actions.mining.Mining.RockDefinitions;
import novite.rs.game.player.actions.prayer.AltarAction;
import novite.rs.game.player.actions.summoning.Summoning;
import novite.rs.game.player.actions.thieving.Thieving;
import novite.rs.game.player.content.ItemOnTypeHandler;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.content.Runecrafting;
import novite.rs.game.player.content.achievements.impl.VarrockEasyDitchAchievement;
import novite.rs.game.player.content.agility.Agility;
import novite.rs.game.player.content.agility.BarbarianOutpostAgility;
import novite.rs.game.player.content.agility.GnomeAgility;
import novite.rs.game.player.content.agility.WildernessAgility;
import novite.rs.game.player.content.exchange.ExchangeManagement;
import novite.rs.game.player.controlers.impl.FightCaves;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.controlers.impl.guilds.warriors.WarriorsGuild;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.impl.PrayerBoneD;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.utility.Utils;
import novite.rs.utility.game.Rights;

public class ObjectHandler {

	public static void handleObjectInteraction(Player player, int option, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
			return;
		}
		if (player.isLocked() || player.isFrozen() || player.getEmotesManager().isDoingEmote()) {
			return;
		}
		final boolean forceRun = stream.readUnsignedByte128() == 1;
		final int x = stream.readUnsignedShort128();
		final int id = stream.readInt();
		final int y = stream.readUnsignedShortLE();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId)) {
			return;
		}
		if (forceRun)
			player.setRun(true);
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null || mapObject.getId() != id) {
			return;
		}
		final WorldObject object = mapObject;
		switch (option) {
		case 1:
			handleOption1(player, object);
			break;
		case 2:
			handleOption2(player, object);
			break;
		case 3:
			handleOption3(player, object);
			break;
		case 4:
			handleExamine(player, object);
			break;
		default:
			System.err.println("Unhandled object option: " + option);
			break;
		}
	}

	private static void handleOption1(final Player player, final WorldObject object) {
		final int id = object.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		final WorldTile tile = object;
		@SuppressWarnings("unused")
		final int regionId = tile.getRegionId();
		final int x = tile.getX(), y = tile.getY();

		player.stopAll();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
				if (!player.getControllerManager().processObjectClick1(object)) {
					return;
				}
				if (CastleWars.handleObjects(player, id)) {
					return;
				}
				if (EventManager.get().handleObjectClick(player, object.getId(), object, tile, ClickOption.FIRST)) {
					return;
				}
				if (player.getFarmingManager().isFarming(id, null, 1))
					return;
				if (player.getQuestManager().handleObject(player, object))
					return;
				HunterNPC hunterNpc = HunterNPC.forObjectId(id);
				if (hunterNpc != null) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(hunterNpc.getEquipment().getPickUpAnimation());
						for (Item item : hunterNpc.getItems())
							player.getInventory().addItem(item);
						player.getInventory().addItem(hunterNpc.getEquipment().getId(), 1);
						player.getSkills().addXp(Skills.HUNTER, hunterNpc.getXp());
					} else {
						player.getPackets().sendGameMessage("This isn't your trap.");
					}
				} else if (object.getId() == 19205) {
					Hunter.createLoggedObject(player, object, true);
				} else if (id == HunterEquipment.BOX.getObjectId() || id == 19192) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(new Animation(5208));
						player.getInventory().addItem(HunterEquipment.BOX.getId(), 1);
					} else
						player.getPackets().sendGameMessage("This isn't your trap.");
				} else if (id == HunterEquipment.BRID_SNARE.getObjectId() || id == 19174) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(new Animation(5207));
						player.getInventory().addItem(HunterEquipment.BRID_SNARE.getId(), 1);
					} else
						player.getPackets().sendGameMessage("This isn't your trap.");
				} else if (id == 20602) { // gamers groto
					player.useStairs(-1, new WorldTile(2954, 9675, 0), 0, 1);
				} else if (id == 9294) {
					if (!Agility.hasLevel(player, 80))
						return;
					final boolean isRunning = player.getRun();
					final boolean isSouth = player.getY() >= 9813;
					final WorldTile tile = isSouth ? new WorldTile(2878, 9812, 0) : new WorldTile(2881, 9814, 0);
					player.setRun(true);
					player.addWalkSteps(isSouth ? 2881 : 2877, isSouth ? 9814 : 9812);
					WorldTasksManager.schedule(new WorldTask() {
						int ticks = 0;

						@Override
						public void run() {
							ticks++;
							if (ticks == 2)
								player.setNextFaceWorldTile(object);
							else if (ticks == 3) {
								player.setNextAnimation(new Animation(1995));
								player.setNextForceMovement(new ForceMovement(player, 0, tile, 4, Utils.getFaceDirection(object.getX() - player.getX(), object.getY() - player.getY())));
							} else if (ticks == 4)
								player.setNextAnimation(new Animation(1603));
							else if (ticks == 7) {
								player.setNextWorldTile(tile);
								player.setRun(isRunning);
								stop();
								return;
							}
						}
					}, 0, 0);
				} else if (id == 20604) { // gamers groto
					player.useStairs(-1, new WorldTile(3018, 3404, 0), 0, 1);
				} else if (id == 4495) {
					player.useStairs(-1, new WorldTile(3417, 3540, 2), 1, 2);
				} else if (id == 4496) {
					player.useStairs(-1, new WorldTile(3412, 3540, 1), 1, 2);
				} else if (id == 45076) {
					player.getActionManager().setAction(new Mining(object, RockDefinitions.LRC_Gold_Ore));
				} else if (id == 5999) {
					player.getActionManager().setAction(new Mining(object, RockDefinitions.LRC_Coal_Ore));
				} else if (id == 45078) {
					player.useStairs(2413, new WorldTile(3012, 9832, 0), 2, 2);
				} else if (id == 24357 && object.getX() == 3188 && object.getY() == 3355) {
					player.useStairs(-1, new WorldTile(3189, 3354, 1), 0, 1);
				} else if (id == 24359 && object.getX() == 3188 && object.getY() == 3355) {
					player.useStairs(-1, new WorldTile(3189, 3358, 0), 0, 1);
				} else if (id == 1805 && object.getX() == 3191 && object.getY() == 3363) {
					WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() - 1, object.getX(), object.getY(), object.getPlane());
					if (World.removeTemporaryObject(object, 1200)) {
						World.spawnTemporaryObject(openedDoor, 1200);
						player.lock(2);
						player.stopAll();
						player.addWalkSteps(3191, player.getY() >= object.getY() ? object.getY() - 1 : object.getY(), -1, false);
						if (player.getY() >= object.getY()) {
							player.getDialogueManager().startDialogue("SimpleNPCMessage", 198, "Greetings bolt adventurer. Welcome to the guild of", "Champions.");
						}
					}
				} else if (id == 12290 || id == 12272) {
					if (id == 12290)
						player.setFavorPoints(1 - player.getFavorPoints());
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.STRAIT_VINE));// start
																												// of
					// jadinkos
				} else if (id == 15653) {
					if (World.isSpawnedObject(object) || !WarriorsGuild.canEnter(player))
						return;
					player.lock(2);
					WorldObject opened = new WorldObject(object.getId(), object.getType(), object.getRotation() - 1, object.getX(), object.getY(), object.getPlane());
					World.spawnObjectTemporary(opened, 600);
					player.addWalkSteps(2876, 3542, 2, false);
				} else if (id == 12328) {
					player.useStairs(3527, new WorldTile(3012, 9275, 0), 5, 6);
					player.setNextForceMovement(new ForceMovement(player, 3, object, 2, ForceMovement.WEST));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextFaceWorldTile(new WorldTile(3012, 9274, 0));
							player.setNextAnimation(new Animation(11043));
							player.getControllerManager().startController("JadinkoLair");
						}
					}, 4);
				} else if (id == 12277) {
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.STRAIT_VINE_COLLECTABLE));// start
																															// of
																															// jadinkos
				} else if (id == 12291) {
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MUTATED_VINE));
				} else if (id == 12274) {
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURLY_VINE));
				} else if (id == 12279) {
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURLY_VINE_COLLECTABLE));
				} else if (id == 7288) {
					Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3253, 3401, 0));
				} else if (id == HunterEquipment.BOX.getObjectId()) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(HunterEquipment.BOX.getPickUpAnimation());
						player.getInventory().addItem(HunterEquipment.BOX.getId(), 1);
						player.setTrapAmount(player.getTrapAmount() - 1);
					} else {
						player.getPackets().sendGameMessage("This isn't your trap.");
					}
				} else if (id == 59463) { // works now
					player.getDialogueManager().startDialogue("Crate");
					// } else if (id == 66017){
					// Barrows.processObjectClick1(object);
				} else if (id == 4277) {
					// player.sendMessage("You successfully thieve from the stall");
					player.lock(4);
					player.getInventory().addItem(995, 1270);
					player.setNextAnimation(new Animation(881));
					player.getSkills().addXp(17, 100);
				} else if (id == HunterEquipment.BRID_SNARE.getObjectId() || id == 19174) {
					if (OwnedObjectManager.removeObject(player, object)) {
						player.setNextAnimation(HunterEquipment.BRID_SNARE.getPickUpAnimation());
						player.getInventory().addItem(HunterEquipment.BRID_SNARE.getId(), 1);
					} else
						player.getPackets().sendGameMessage("This isn't your trap.");
				} else if (object.getDefinitions().name.equalsIgnoreCase("Obelisk") && object.getY() > 3527) {
					player.getControllerManager().startController("ObeliskControler", object);
				} else if (id == 2350 && (object.getX() == 3352 && object.getY() == 3417 && object.getPlane() == 0)) {
					player.useStairs(832, new WorldTile(3177, 5731, 0), 1, 2);
				} else if (Mining.getRockDefinitions(id) != null) {
					player.getActionManager().setAction(new Mining(object, Mining.getRockDefinitions(id)));
				} else if (id == 2353 && (object.getX() == 3177 && object.getY() == 5730 && object.getPlane() == 0)) {
					player.useStairs(828, new WorldTile(3353, 3416, 0), 1, 2);
				} else if (id == 2491) {
					player.getActionManager().setAction(new EssenceMining(object, player.getSkills().getLevel(Skills.MINING) < 30 ? EssenceDefinitions.Rune_Essence : EssenceDefinitions.Pure_Essence));
				} else if (id == 2478) {
					Runecrafting.craftEssence(player, 556, 1, 5, false, 11, 2, 22, 3, 33, 4, 44, 5, 55, 6, 66, 7, 77, 7, 88, 9, 99, 10);
				} else if (id == 2479) {
					Runecrafting.craftEssence(player, 558, 2, 5.5, false, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8);
				} else if (id == 2480) {
					Runecrafting.craftEssence(player, 555, 5, 6, false, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6);
				} else if (id == 2481) {
					Runecrafting.craftEssence(player, 557, 9, 6.5, false, 26, 2, 52, 3, 78, 4);
				} else if (id == 2482) {
					Runecrafting.craftEssence(player, 554, 14, 7, false, 35, 2, 70, 3);
				} else if (id == 2483) {
					Runecrafting.craftEssence(player, 559, 20, 7.5, false, 46, 2, 92, 3);
				} else if (id == 2484) {
					Runecrafting.craftEssence(player, 564, 27, 8, true, 59, 2);
				} else if (id == 2487) {
					Runecrafting.craftEssence(player, 562, 35, 8.5, true, 74, 2);
				} else if (id == 17010) {
					Runecrafting.craftEssence(player, 9075, 40, 8.7, true, 82, 2);
				} else if (id == 2486) {
					Runecrafting.craftEssence(player, 561, 45, 9, true, 91, 2);
				} else if (id == 2485) {
					Runecrafting.craftEssence(player, 563, 50, 9.5, true);
				} else if (id == 2488) {
					Runecrafting.craftEssence(player, 560, 65, 10, true);
				} else if (id == 30624) {
					Runecrafting.craftEssence(player, 565, 77, 10.5, true);
				} else if (id == 2452) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.AIR_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterAirAltar(player);
					}
				} else if (id == 2455) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.EARTH_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterEarthAltar(player);
					}
				} else if (id == 2456) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.FIRE_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterFireAltar(player);
					}
				} else if (id == 2454) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.WATER_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterWaterAltar(player);
					}
				} else if (id == 2457) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.BODY_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterBodyAltar(player);
					}
				} else if (id == 2453) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == Runecrafting.MIND_TIARA || hatId == Runecrafting.OMNI_TIARA) {
						Runecrafting.enterMindAltar(player);
					}
				} else if (id == 47120) { // zaros altar-recharge if needed
					if (player.getPrayer().getPrayerpoints() < player.getSkills().getLevelForXp(Skills.PRAYER) * 10) {
						player.lock(12);
						player.setNextAnimation(new Animation(12563));
						player.getPrayer().setPrayerpoints((int) ((player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * 1.15));
						player.getPrayer().refreshPrayerPoints();
					}
					player.getDialogueManager().startDialogue("ZarosAltar");
				} else if (id == 36786) {
					player.getDialogueManager().startDialogue("Banker", 4907);
				} else if (id == 42377 || id == 42378) {
					player.getDialogueManager().startDialogue("Banker", 2759);
				} else if (id == 42217 || id == 782 || id == 34752 || id == 4369) {
					player.getDialogueManager().startDialogue("Banker", 553);
				} else if (id == 42425 && object.getX() == 3220 && object.getY() == 3222) { // zaros
					// portal
					player.useStairs(10256, new WorldTile(3353, 3416, 0), 4, 5, "And you find yourself into a digsite.");
					player.addWalkSteps(3222, 3223, -1, false);
					player.getPackets().sendGameMessage("You examine portal and it aborves you...");
				} else if (id == 46500 && object.getX() == 3351 && object.getY() == 3415) { // zaros
					// portal
					player.useStairs(-1, new WorldTile(Constants.DEATH_TILE.getX(), Constants.DEATH_TILE.getY(), Constants.DEATH_TILE.getPlane()), 2, 3, "You found your way back to home.");
					player.addWalkSteps(3351, 3415, -1, false);
				} else if (id == 9293) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
						player.getPackets().sendGameMessage("You need an agility level of 70 to use this obstacle.", true);
						return;
					}
					int x = player.getX() == 2886 ? 2892 : 2886;
					WorldTasksManager.schedule(new WorldTask() {
						int count = 0;

						@Override
						public void run() {
							player.setNextAnimation(new Animation(844));
							if (count++ == 1) {
								stop();
							}
						}

					}, 0, 0);
					player.setNextForceMovement(new ForceMovement(new WorldTile(x, 9799, 0), 3, player.getX() == 2886 ? 1 : 3));
					player.useStairs(-1, new WorldTile(x, 9799, 0), 3, 4);
				} else if (id == 2295) {
					GnomeAgility.walkGnomeLog(player);
				} else if (id == 2285) {
					GnomeAgility.climbGnomeObstacleNet(player);
				} else if (id == 35970) {
					GnomeAgility.climbUpGnomeTreeBranch(player);
				} else if (id == 2312) {
					GnomeAgility.walkGnomeRope(player);
				} else if (id == 4059) {
					GnomeAgility.walkBackGnomeRope(player);
				} else if (id == 2314) {
					GnomeAgility.climbDownGnomeTreeBranch(player);
				} else if (id == 2286) {
					GnomeAgility.climbGnomeObstacleNet2(player);
				} else if (id == 43544 || id == 43543) {
					GnomeAgility.enterGnomePipe(player, object.getX(), object.getY());
				} else if (id == 20210) {
					BarbarianOutpostAgility.enterObstaclePipe(player, object);
				} else if (id == 43526) {
					BarbarianOutpostAgility.swingOnRopeSwing(player, object);
				} else if (id == 43595 && x == 2550 && y == 3546) {
					BarbarianOutpostAgility.walkAcrossLogBalance(player, object);
				} else if (id == 20211 && x == 2538 && y == 3545) {
					BarbarianOutpostAgility.climbObstacleNet(player, object);
				} else if (id == 2302 && x == 2535 && y == 3547) {
					BarbarianOutpostAgility.walkAcrossBalancingLedge(player, object);
				} else if (id == 1948) {
					BarbarianOutpostAgility.climbOverCrumblingWall(player, object);
				} else if (id == 43533) {
					BarbarianOutpostAgility.runUpWall(player, object);
				} else if (id == 43597) {
					BarbarianOutpostAgility.climbUpWall(player, object);
				} else if (id == 43587) {
					BarbarianOutpostAgility.fireSpringDevice(player, object);
				} else if (id == 43527) {
					BarbarianOutpostAgility.crossBalanceBeam(player, object);
				} else if (id == 43531) {
					BarbarianOutpostAgility.jumpOverGap(player, object);
				} else if (id == 43532) {
					BarbarianOutpostAgility.slideDownRoof(player, object);
					// Wilderness course start
				} else if (id == 2297) {
					WildernessAgility.walkAcrossLogBalance(player, object);
				} else if (id == 37704) {
					WildernessAgility.jumpSteppingStones(player, object);
				} else if (id == 2288) {
					WildernessAgility.enterWildernessPipe(player, object.getX(), object.getY());
				} else if (id == 2328) {
					WildernessAgility.climbUpWall(player, object);
				} else if (id == 2283) {
					WildernessAgility.swingOnRopeSwing(player, object);
				} else if (id == 2309) {
					WildernessAgility.enterWildernessCourse(player);
				} else if (id == 2307 || id == 2308) {
					WildernessAgility.exitWildernessCourse(player);
				} else if (Wilderness.isDitch(id)) {// wild ditch
					player.lock(4);
					player.setNextAnimation(new Animation(6132));
					final WorldTile toTile = new WorldTile(player.getX(), object.getY() + 2, object.getPlane());
					player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2, 0));
					final ObjectDefinitions objectDef = object.getDefinitions();
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (player.getRegionId() == 12854 || player.getRegionId() == 12855) {
								player.getAchievementManager().notifyUpdate(VarrockEasyDitchAchievement.class);
							}
							player.setNextWorldTile(toTile);
							player.setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
							player.getControllerManager().startController("Wilderness");
						}
					}, 2);
				} else if (id == 42611) {// Magic Portal
					player.getDialogueManager().startDialogue("MagicPortal");
				} else if (id == 27254) {// Edgeville portal
					player.getPackets().sendGameMessage("You enter the portal...");
					player.useStairs(10584, new WorldTile(3087, 3488, 0), 2, 3, "..and are transported to Edgeville.");
					player.addWalkSteps(1598, 4506, -1, false);
				} else if (id == 15522) {// portal sign
					if (player.withinDistance(new WorldTile(1598, 4504, 0), 1)) {// PORTAL
						// 1
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Edgeville");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to edgeville. There " + "you can multi pk once past the wilderness ditch.");
					}
					if (player.withinDistance(new WorldTile(1598, 4508, 0), 1)) {// PORTAL
						// 2
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Mage Bank");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to the mage bank. " + "The mage bank is a 1v1 deep wilderness area.");
					}
					if (player.withinDistance(new WorldTile(1598, 4513, 0), 1)) {// PORTAL
						// 3
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Magic's Portal");
						player.getPackets().sendIComponentText(327, 14, "This portal will allow you to teleport to areas that " + "will allow you to change your magic spell book.");
					}
				} else if (id == 37929) {// corp beast
					if (object.getX() == 2971 && object.getY() == 4382 && object.getPlane() == 0) {
						player.getInterfaceManager().sendInterface(650);
					} else if (object.getX() == 2918 && object.getY() == 4382 && object.getPlane() == 0) {
						player.stopAll();
						player.setNextWorldTile(new WorldTile(player.getX() == 2921 ? 2917 : 2921, player.getY(), player.getPlane()));
					}
				} else if (id == 37928 && object.getX() == 2883 && object.getY() == 4370 && object.getPlane() == 0) {
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3214, 3782, 0));
					player.getControllerManager().startController("Wilderness");
				} else if (id == 38815 && object.getX() == 3209 && object.getY() == 3780 && object.getPlane() == 0) {
					if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < 37 || player.getSkills().getLevelForXp(Skills.MINING) < 45 || player.getSkills().getLevelForXp(Skills.SUMMONING) < 23 || player.getSkills().getLevelForXp(Skills.FIREMAKING) < 47 || player.getSkills().getLevelForXp(Skills.PRAYER) < 55) {
						player.getPackets().sendGameMessage("You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
						return;
					}
					player.stopAll();
					player.setNextWorldTile(new WorldTile(2885, 4372, 0));
					player.getControllerManager().forceStop();
					// TODO all reqs, skills not added
				} else if (id == 48803 && player.isKalphiteLairSetted()) {
					player.setNextWorldTile(new WorldTile(3508, 9494, 0));
				} else if (id == 9369) {
					player.getControllerManager().startController("FightPits");
				} else if (id == 50205) {
					Summoning.openInfusionInterface(player);
				} else if (id == 1817 && object.getX() == 2273 && object.getY() == 4680) { // kbd
					// lever
					Magic.pushLeverTeleport(player, new WorldTile(3067, 10254, 0));
				} else if (id == 1816 && object.getX() == 3067 && object.getY() == 10252) { // kbd
					// out
					// lever
					Magic.pushLeverTeleport(player, new WorldTile(2273, 4681, 0));
				} else if (id == 9356) {
					FightCaves.enterFightCaves(player);
				} else if (id == 28779) {
					player.getDialogueManager().startDialogue("BorkEnter");
				} else if (id == 28698) {
					player.getDialogueManager().startDialogue("LunarAltar");
				} else if (id == 32015 && object.getX() == 3069 && object.getY() == 10256) { // kbd
					// stairs
					player.useStairs(828, new WorldTile(3017, 3848, 0), 1, 2);
					player.getControllerManager().startController("Wilderness");
				} else if (id == 1765 && object.getX() == 3017 && object.getY() == 3849) { // kbd
					// out
					// stairs
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3069, 10255, 0));
					player.getControllerManager().forceStop();
					// Ancient cavern shit
				} else if (id == 25336) {
					player.useStairs(-1, new WorldTile(1768, 5366, 1), 0, 1);
				} else if (id == 25338) {
					player.useStairs(-1, new WorldTile(1772, 5366, 0), 0, 1);
				} else if (id == 25339) {
					player.useStairs(-1, new WorldTile(1778, 5343, 1), 0, 1);
				} else if (id == 25340) {
					player.useStairs(-1, new WorldTile(1778, 5346, 0), 0, 1);
				} else if (id == 5959) {
					Magic.pushLeverTeleport(player, new WorldTile(2539, 4712, 0));
				} else if (id == 31149) {
					player.setNextWorldTile(new WorldTile(player.getX() <= 3295 ? 3296 : 3295, 3498, player.getPlane()));
				} else if (id == 5960) {
					Magic.pushLeverTeleport(player, new WorldTile(3089, 3957, 0));
				} else if (id == 1815) {
					Magic.pushLeverTeleport(player, Constants.HOME_TILE);
				} else if (id == 62675) {
					player.getCutscenesManager().play("DTPreview");
				} else if (id == 62681) {
					player.getDominionTower().viewScoreBoard();
				} else if (id == 62678 || id == 62679) {
					player.getDominionTower().openModes();
				} else if (id == 62688) {
					player.getDialogueManager().startDialogue("DTClaimRewards");
				} else if (id == 62677) {
					player.getDominionTower().talkToFace();
				} else if (id == 62680) {
					player.getDominionTower().openBankChest();
				} else if (id == 62676) { // dominion exit
					player.useStairs(-1, new WorldTile(3374, 3093, 0), 0, 1);
				} else if (id == 62674) { // dominion entrance
					player.useStairs(-1, new WorldTile(3744, 6405, 0), 0, 1);
				} else {
					switch (objectDef.id) {
					case 61190:
					case 61191:
					case 61192:
					case 61193:
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));
						}
						break;
					case 2092:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Iron_Ore));
						break;
					case 2094:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Tin_Ore));
						break;
					case 2090:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Copper_Ore));
						break;
					case 2100:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Silver_Ore));
						break;
					case 2098:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Gold_Ore));
						break;
					case 2102:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Mithril_Ore));
						break;
					case 2104:
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Adamant_Ore));
						break;
					}
					switch (objectDef.name.toLowerCase()) {
					case "web":
						if (objectDef.containsOption(0, "Slash")) {
							player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
							slashWeb(player, object);
						}
						break;
					case "bank booth":
						if (objectDef.containsOption(0, "Bank") || objectDef.containsOption(0, "Use")) {
							player.getBank().openBank();
						}
						break;
					case "bank chest":
						if (objectDef.containsOption(0, "Use")) {
							player.getBank().openBank();
						}
					case "bank":
						player.getBank().openBank();
						break;
					// Woodcutting start
					case "tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));
						}
						break;
					case "dead tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.DEAD));
						}
						break;
					case "oak":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.OAK));
						}
						break;
					case "willow":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.WILLOW));
						}
						break;
					case "maple tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAPLE));
						}
						break;
					case "ivy":
						if (objectDef.containsOption(0, "Chop")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.IVY));
						}
						break;
					case "yew":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.YEW));
						}
						break;
					case "magic tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAGIC));
						}
						break;
					case "cursed magic tree":
						if (objectDef.containsOption(0, "Chop down")) {
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURSED_MAGIC));
						}
						break;
					// Woodcutting end
					case "ardougne wall door":
						player.setNextWorldTile(new WorldTile(player.getX() <= 2557 ? 2559 : 2557, player.getY(), player.getPlane()));
						break;
					case "gate":
					case "large door":
					case "metal door":
						if (id == 21600) {
							World.removeObject(object);
							return;
						}
						if (object.getType() == 0 && objectDef.containsOption(0, "Open")) {
							if (!handleGate(player, object)) {
								handleDoor(player, object);
							}
						}
						break;
					case "door":
						if (id == 21507 || id == 21505) {
							World.removeObject(object);
							return;
						}

						if (object.getType() == 0 && (objectDef.containsOption(0, "Open") || objectDef.containsOption(0, "Unlock"))) {
							handleDoor(player, object);
						}
						break;
					case "ladder":
						if (id == 21512 || id == 21514) {
							return;
						}
						handleLadder(player, object, 1);
						break;
					case "spikey chain":
					case "staircase":
						handleStaircases(player, object, 1);
						break;
					case "small obelisk":
						if (objectDef.containsOption(0, "Renew-points")) {
							int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
							if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
								player.lock(3);
								player.setNextAnimation(new Animation(8502));
								player.getSkills().set(Skills.SUMMONING, summonLevel);
								player.getPackets().sendGameMessage("You have recharged your Summoning points.", true);
							} else {
								player.getPackets().sendGameMessage("You already have full Summoning points.");
							}
						}
						break;
					case "altar":
					case "gorilla statue":
						if (objectDef.containsOption(0, "Pray-at")) {
							final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.lock(5);
								player.getPackets().sendGameMessage("You pray to the gods...", true);
								player.setNextAnimation(new Animation(645));
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										player.getPrayer().restorePrayer(maxPrayer);
										player.getPackets().sendGameMessage("...and recharged your prayer.", true);
									}
								}, 2);
							} else {
								player.getPackets().sendGameMessage("You already have full prayer.", true);
							}
							if (id == 6552) {
								player.getDialogueManager().startDialogue("AncientAltar");
							}
						}
						break;
					}
				}
				if (Constants.DEBUG) {
					System.out.println("cliked 1 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", " + object.getType() + ", " + object.getRotation() + ", " + object.getDefinitions().name);
				}
			}

		}, true));
	}

	private static void slashWeb(Player player, WorldObject object) {

		if (Utils.getRandom(1) == 0) {
			World.spawnTemporaryObject(new WorldObject(object.getId() + 1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane()), 60000);
			player.getPackets().sendGameMessage("You slash through the web!");
		} else {
			player.getPackets().sendGameMessage("You fail to cut through the web.");
		}
	}

	@SuppressWarnings("unused")
	private static void handleOption2(final Player player, final WorldObject object) {
		final int id = object.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		final WorldTile tile = object;
		final int regionId = tile.getRegionId();
		final int x = tile.getX(), y = tile.getY();

		player.stopAll();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
				if (!player.getControllerManager().processObjectClick2(object)) {
					return;
				}
				if (EventManager.get().handleObjectClick(player, object.getId(), object, tile, ClickOption.SECOND)) {
					return;
				}
				if (player.getFarmingManager().isFarming(id, null, 2))
					return;
				if (id == 36786 || id == 42378 || id == 42377 || id == 42217 || id == 27663) {
					player.getBank().openBank();
				} else if (object.getDefinitions().name.equalsIgnoreCase("furnace")) {
					player.getDialogueManager().startDialogue("SmeltingD", object);
				} else if (id == 61) {
					player.getDialogueManager().startDialogue("LunarAltar");
				} else if (id == 62677) {
					player.getDominionTower().openRewards();
				} else if (id == 62688) {
					player.getDialogueManager().startDialogue("SimpleMessage", "You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");
				} else if (id == 34384 || id == 34383 || id == 14011 || id == 7053 || id == 34387 || id == 34386 || id == 34385) {
					Thieving.handleStalls(player, object);
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "bank booth":
						if (objectDef.containsOption(1, "Use-quickly")) {
							player.getBank().openBank();
						}
						break;
					case "gate":
					case "metal door":
						if (object.getType() == 0 && objectDef.containsOption(1, "Open")) {
							handleGate(player, object);
						}
						break;
					case "door":
						if (id == 21507) {
							World.removeObject(object);
							return;
						}

						if (object.getType() == 0 && objectDef.containsOption(1, "Open")) {
							handleDoor(player, object);
						}
						break;
					case "ladder":
						if (id == 21512) {
							return;
						}

						handleLadder(player, object, 2);
						break;
					case "staircase":
						handleStaircases(player, object, 2);
						break;
					}
				}
				if (Constants.DEBUG) {
					System.out.println("cliked 2 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane());
				}
			}
		}, true));
	}

	private static void handleExamine(final Player player, WorldObject object) {
		final int id = object.getId();
		final WorldTile tile = object;
		final int x = tile.getX(), y = tile.getY();

		WorldObject mapObject = World.getObjectWithId(id, tile);
		if (mapObject == null || mapObject.getId() != id) {
			return;
		}
		if (player.getTemporaryAttributtes().get("removing_object_spawns") != null) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/map/nonspawning.txt", true)));
				out.println(mapObject.getId() + " " + mapObject.getX() + " " + mapObject.getY() + " " + mapObject.getPlane());
				out.close();
				System.out.println("Added " + mapObject.getId() + " to be removed from spawns.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		object = !player.isAtDynamicRegion() ? mapObject : new WorldObject(id, mapObject.getType(), mapObject.getRotation(), x, y, player.getPlane());

		if (player.getRights() == Rights.OWNER.ordinal())
			player.getPackets().sendGameMessage("It's an " + object.getDefinitions().name + "." + (player.getRights() > 2 ? "[Id: " + object.getId() + ", " + object + "]" : ""));
	}

	@SuppressWarnings("unused")
	private static void handleOption3(final Player player, final WorldObject object) {
		final int id = object.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		final WorldTile tile = object;
		final int regionId = tile.getRegionId();
		final int x = tile.getX(), y = tile.getY();

		player.stopAll();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
				if (!player.getControllerManager().processObjectClick3(object)) {
					return;
				}
				if (EventManager.get().handleObjectClick(player, object.getId(), object, tile, ClickOption.THIRD)) {
					return;
				}
				if (player.getFarmingManager().isFarming(id, null, 3))
					return;
				player.setNextFaceWorldTile(tile);
				switch (objectDef.name.toLowerCase()) {
				case "gate":
				case "metal door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open")) {
						handleGate(player, object);
					}
					break;
				case "door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open")) {
						handleDoor(player, object);
					}
					break;
				case "ladder":
					if (id == 21512) {
						return;
					}
					handleLadder(player, object, 3);
					break;
				case "staircase":
					handleStaircases(player, object, 3);
					break;
				case "counter":
					ExchangeManagement.openCollectionBox(player);
					break;
				}
				if (Constants.DEBUG) {
					System.out.println("cliked 3 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", ");
				}
			}
		}, true));
	}

	private static boolean handleGate(Player player, WorldObject object) {
		if (World.isSpawnedObject(object)) {
			return false;
		}
		if (object.getRotation() == 0) {

			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()), object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
					return false;
				}
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.moveLocation(-1, 0, 0);
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor2.setRotation(3);
			}

			if (World.removeTemporaryObject(object, 60000) && World.removeTemporaryObject(otherDoor, 60000)) {
				player.faceObject(openedDoor1);
				World.spawnTemporaryObject(openedDoor1, 60000);
				World.spawnTemporaryObject(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 2) {

			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()), object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
					return false;
				}
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			}
			if (World.removeTemporaryObject(object, 60000) && World.removeTemporaryObject(otherDoor, 60000)) {
				player.faceObject(openedDoor1);
				World.spawnTemporaryObject(openedDoor1, 60000);
				World.spawnTemporaryObject(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()), object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
					return false;
				}
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.setRotation(0);
				openedDoor1.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			}
			if (World.removeTemporaryObject(object, 60000) && World.removeTemporaryObject(otherDoor, 60000)) {
				player.faceObject(openedDoor1);
				World.spawnTemporaryObject(openedDoor1, 60000);
				World.spawnTemporaryObject(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()), object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation() || otherDoor.getType() != object.getType() || !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
					return false;
				}
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			}
			if (World.removeTemporaryObject(object, 60000) && World.removeTemporaryObject(otherDoor, 60000)) {
				player.faceObject(openedDoor1);
				World.spawnTemporaryObject(openedDoor1, 60000);
				World.spawnTemporaryObject(openedDoor2, 60000);
				return true;
			}
		}
		return false;
	}

	public static boolean handleDoor(Player player, WorldObject object, long timer) {
		if (World.isSpawnedObject(object)) {
			return false;
		}
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), (object.getRotation() + 1) & 0x3, object.getX(), object.getY(), object.getPlane());
		World.spawnTemporaryObject(openedDoor, timer);
		return false;
	}

	private static boolean handleDoor(Player player, WorldObject object) {
		if (World.isSpawnedObject(object)) {
			return false;
		}
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane());
		if (object.getRotation() == 0) {
			openedDoor.moveLocation(-1, 0, 0);
		} else if (object.getRotation() == 1) {
			openedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 2) {
			openedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 3) {
			openedDoor.moveLocation(0, -1, 0);
		}
		if (World.removeTemporaryObject(object, 60000)) {
			player.faceObject(openedDoor);
			World.spawnTemporaryObject(openedDoor, 60000);
			return true;
		}
		return false;
	}

	private static boolean handleStaircases(Player player, WorldObject object, int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3) {
				return false;
			}
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0) {
				return false;
			}
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0) {
				return false;
			}
			player.getDialogueManager().startDialogue("ClimbNoEmoteStairs", new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Go up the stairs.", "Go down the stairs.");
		} else {
			return false;
		}
		return false;
	}

	private static boolean handleLadder(Player player, WorldObject object, int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (object.getId() == 1757 && object.getX() == 2892 && object.getY() == 9907) {
				player.useStairs(828, new WorldTile(2892, 3508, 0), 1, 2);
				return true;
			}
			if (player.getPlane() == 3) {
				return false;
			}
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (object.getId() == 1754 && object.getX() == 2892 && object.getY() == 3507) {
				player.useStairs(828, new WorldTile(2894, 9907, 0), 1, 2);
				return true;
			}
			if (player.getPlane() == 0) {
				return false;
			}
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0) {
				return false;
			}
			player.getDialogueManager().startDialogue("ClimbEmoteStairs", new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Climb up the ladder.", "Climb down the ladder.", 828);
		} else {
			return false;
		}
		return true;
	}

	public static void handleItemOnObject(final Player player, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
			return;
		}
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() >= currentTime
		// || player.getFreezeDelay() >= currentTime
				|| player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
			return;
		}

		stream.readUnsignedByteC();
		final int y = stream.readUnsignedShortLE();
		final int itemSlot = stream.readUnsignedShortLE();
		final int interfaceHash = stream.readIntLE();
		final int interfaceId = interfaceHash >> 16;
		final int itemId = stream.readUnsignedShortLE128();
		final int x = stream.readUnsignedShortLE();
		final int id = stream.readInt();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId)) {
			return;
		}
		WorldObject mapObject = World.getObjectWithId(id, tile);
		if (mapObject == null || mapObject.getId() != id) {
			return;
		}
		final WorldObject object = !player.isAtDynamicRegion() ? mapObject : new WorldObject(id, mapObject.getType(), mapObject.getRotation(), x, y, player.getPlane());
		final Item item = player.getInventory().getItem(itemSlot);
		if (player.isDead() || Utils.getInterfaceDefinitionsSize() <= interfaceId) {
			return;
		}
		if (player.getLockDelay() > Utils.currentTimeMillis()) {
			return;
		}
		if (!player.getInterfaceManager().containsInterface(interfaceId)) {
			return;
		}
		if (item == null || item.getId() != itemId) {
			return;
		}
		player.stopAll(false); // false
		final ObjectDefinitions objectDef = object.getDefinitions();

		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.faceObject(object);
				if (!player.getControllerManager().handleItemOnObject(object, item))
					return;
				if (ItemOnTypeHandler.handleItemOnObject(player, object, item)) {
					return;
				}
				if (AltarAction.handleBoneOnAltar(player, object, item)) {
					return;
				}
				if (object.getId() == 409) {
					if (player.getRegionId() == 12633 || (player.getRegionId() == 10301 && player.isDonator())) {
						if (player.getRegionId() == 12633) {
							if (player.getFacade().getVoteBonus() > System.currentTimeMillis()) {
								player.getDialogueManager().startDialogue(PrayerBoneD.class, item.getId());
							} else {
								player.getDialogueManager().startDialogue(SimpleMessage.class, "You don't have access to the vote zone.", "Vote on the forums for 12 hour access to it.");
							}
						} else {
							player.getDialogueManager().startDialogue(PrayerBoneD.class, item.getId());
						}
					}
					return;
				} else if (object.getDefinitions().name.toLowerCase().contains("furnace") && item.getId() == 2353) {
					player.getDialogueManager().startDialogue("CannonBallD");
					return;
				} else if (object.getDefinitions().name.equals("Anvil")) {
					player.getTemporaryAttributtes().put("itemUsed", itemId);
					ForgingBar bar = ForgingBar.forId(itemId);
					if (bar != null) {
						ForgingInterface.sendSmithingInterface(player);
					}
				} else if (player.getFarmingManager().isFarming(object.getId(), item, 0)) {
					return;
				} else if (itemId == 1438 && object.getId() == 2452) {
					Runecrafting.enterAirAltar(player);
				} else if (itemId == 1440 && object.getId() == 2455) {
					Runecrafting.enterEarthAltar(player);
				} else if (itemId == 1442 && object.getId() == 2456) {
					Runecrafting.enterFireAltar(player);
				} else if (itemId == 1444 && object.getId() == 2454) {
					Runecrafting.enterWaterAltar(player);
				} else if (itemId == 1446 && object.getId() == 2457) {
					Runecrafting.enterBodyAltar(player);
				} else if (itemId == 1448 && object.getId() == 2453) {
					Runecrafting.enterMindAltar(player);
				} else if (object.getId() == 733 || object.getId() == 64729) {
					player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(-1, 0)));
					slashWeb(player, object);
				} else if (object.getId() == 48803 && itemId == 954) {
					if (player.isKalphiteLairSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLair();
				} else if (objectDef.name.toLowerCase().contains("range") || objectDef.name.toLowerCase().contains("stove") || id == 2732) {
					Cookables cook = Cooking.isCookingSkill(item);
					if (cook != null) {
						player.getDialogueManager().startDialogue("CookingD", cook, object);
					}
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "fountain":
					case "well":
					case "sink":
						if (WaterFilling.isFilling(player, itemId, false))
							return;
						break;
					default:
						if (Constants.DEBUG) {
							System.out.println("item on object: " + id);
						}
						break;
					}
				}
			}
		}, true));
	}

}