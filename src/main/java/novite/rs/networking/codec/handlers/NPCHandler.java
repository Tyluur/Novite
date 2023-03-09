package novite.rs.networking.codec.handlers;

import java.util.Iterator;
import java.util.List;

import novite.rs.Constants;
import novite.rs.api.event.EventListener.ClickOption;
import novite.rs.api.event.EventManager;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.npc.others.ConditionalDeath;
import novite.rs.game.npc.others.GraveStone;
import novite.rs.game.npc.others.LivingRock;
import novite.rs.game.npc.others.pet.Pet;
import novite.rs.game.npc.slayer.Strykewyrm;
import novite.rs.game.player.Player;
import novite.rs.game.player.RouteEvent;
import novite.rs.game.player.actions.Fishing;
import novite.rs.game.player.actions.Fishing.FishingSpots;
import novite.rs.game.player.actions.mining.LivingMineralMining;
import novite.rs.game.player.actions.thieving.PickPocketAction;
import novite.rs.game.player.actions.thieving.PickPocketableNPC;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.content.PlayerLook;
import novite.rs.game.player.content.exchange.ExchangeManagement;
import novite.rs.game.player.content.randoms.RandomEventManager;
import novite.rs.game.player.dialogues.DialogueHandler;
import novite.rs.game.player.dialogues.FremennikShipmaster;
import novite.rs.game.player.dialogues.impl.Banker;
import novite.rs.game.player.dialogues.impl.Mage_of_Zamorak;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.TeleportLocations;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.npc.NPCExamines;
import novite.rs.utility.game.npc.NPCSpawning;

public class NPCHandler {

	public static void handleNPCInteraction(Player player, int option, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) {
			return;
		}
		if (player.isLocked() || player.isFrozen() || player.getEmotesManager().isDoingEmote()) {
			return;
		}
		boolean forceRun = stream.readByte128() == 1;
		int npcIndex = stream.readUnsignedShort128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId())) {
			return;
		}
		if (forceRun) {
			player.setRun(true);
		}
		switch (option) {
		case 1:
			handleOption1(player, npc);
			break;
		case 2:
			handleOption2(player, npc);
			break;
		case 3:
			handleOption3(player, npc);
			break;
		case 4:
			handleExamine(player, npc);
			break;
		case 5:
			handleOption5(player, npc);
			break;
		default:
			System.err.println("Unhandled object option: " + option);
			break;
		}
	}

	private static void handleExamine(final Player player, final NPC npc) {
		if (NPCExamines.getExamine(npc.getId()) != null) {
			player.getPackets().sendNPCMessage(0, npc, NPCExamines.getExamine(npc.getId()));
		}
		if (Constants.DEBUG && player.getTemporaryAttributtes().get("removingspawns") != null) {
			List<NPCSpawning> spawns = ((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).load();
			Iterator<NPCSpawning> it = spawns.iterator();
			while (it.hasNext()) {
				NPCSpawning spawn = it.next();
				if (spawn == null) {
					continue;
				}
				if (spawn.getId() == npc.getId() && spawn.getX() == npc.getStartTile().getX() && spawn.getY() == npc.getStartTile().getY() && spawn.getZ() == npc.getStartTile().getPlane()) {
					it.remove();
				}
			}
			((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).save(spawns);
			npc.finish();
			System.out.println("Removed a monster spawn! [" + npc + "]");
		}
		if (player.getRights() == Rights.OWNER.ordinal())
			player.sendMessage(npc.toString());
	}

	private static void handleOption1(final Player player, final NPC npc) {
		player.stopAll(false);
		if (npc.getDefinitions().getName().contains("Banker") || npc.getDefinitions().getName().contains("banker")) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 2)) {
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						player.getDialogueManager().startDialogue(Banker.class, npc.getId());
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			player.getDialogueManager().startDialogue(Banker.class, npc.getId());
			return;
		}
		if (npc.getId() == 2241 || npc.getId() == 12401) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 1)) {
				player.setRouteEvent(new RouteEvent(new WorldTile(npc.getX(), 3095, 0), new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						if (EventManager.get().handleNPCClick(player, npc, ClickOption.FIRST)) {
							return;
						}
						if (DialogueHandler.getDialogue(npc.getName().replaceAll(" ", "_")) != null) {
							player.getDialogueManager().startDialogue(npc.getName().replaceAll(" ", "_"), npc.getId());
							return;
						}
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			if (EventManager.get().handleNPCClick(player, npc, ClickOption.FIRST)) {
				return;
			}
			if (DialogueHandler.getDialogue(npc.getName().replaceAll(" ", "_")) != null) {
				player.getDialogueManager().startDialogue(npc.getName().replaceAll(" ", "_"), npc.getId());
				return;
			}
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.turnTo(npc);
				FishingSpots spot = FishingSpots.forId(npc.getId() | 1 << 24);
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return; // its a spot, they wont face us
				}
				npc.turnTo(player);
				if (!player.getControllerManager().processNPCClick1(npc)) {
					return;
				}
				if (player.getQuestManager().handleNPC(player, npc)) {
					return;
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.sendGraveInscription(player);
					return;
				}
				if (EventManager.get().handleNPCClick(player, npc, ClickOption.FIRST)) {
					return;
				}
				if (DialogueHandler.getDialogue(npc.getName().replaceAll(" ", "_")) != null) {
					player.getDialogueManager().startDialogue(npc.getName().replaceAll(" ", "_"), npc.getId());
					return;
				}
				if (RandomEventManager.get().handleNPCInteraction(player, npc)) {
					return;
				}
				if (npc.getId() == 9462) {
					Strykewyrm.handleStomping(player, npc);
				} else if (npc.getId() >= 8837 && npc.getId() <= 8839) {
					player.getActionManager().setAction(new LivingMineralMining((LivingRock) npc));
					return;
				} else if (npc.getId() == 9707) {
					player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), true);
				} else if (npc.getId() == 9708) {
					player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), false);
				} else if (npc.getId() == 15513) {
					player.getDialogueManager().startDialogue("RoyalGuard", npc.getId());
				} else if (npc.getId() == 8461) {
					player.getDialogueManager().startDialogue("Turael", npc.getId());
				} else if (npc.getId() == 2676) {
					player.getDialogueManager().startDialogue("MakeOverMage", npc.getId(), 0);
				} else if (npc.getId() == 2257) {
					player.getDialogueManager().startDialogue(Mage_of_Zamorak.class, npc.getId());
				} else if (npc instanceof Pet) {
					Pet pet = (Pet) npc;
					if (pet != player.getPet()) {
						player.getPackets().sendGameMessage("This isn't your pet.");
						return;
					}
					player.setNextAnimation(new Animation(827));
					pet.pickup();
				} else {
					player.getPackets().sendGameMessage(NPCDefinitions.getNPCDefinitions(npc.getId()).getName() + " does not feel like talking right now.");
					if (Constants.DEBUG) {
						System.out.println("Clicked 1 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
					}
				}
			}
		}, true));
	}

	private static void handleOption2(final Player player, final NPC npc) {
		player.stopAll(false);
		if (npc.getDefinitions().getName().contains("Banker") || npc.getDefinitions().getName().contains("banker")) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 2)) {
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						player.getBank().openBank();
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			player.getDialogueManager().startDialogue(Banker.class, npc.getId());
			return;
		}
		if (npc.getId() == 2241) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 1)) {
				player.setRouteEvent(new RouteEvent(new WorldTile(npc.getX(), 3095, 0), new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						if (EventManager.get().handleNPCClick(player, npc, ClickOption.SECOND)) {
							return;
						}
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			if (EventManager.get().handleNPCClick(player, npc, ClickOption.SECOND)) {
				return;
			}
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.turnTo(npc);
				FishingSpots spot = FishingSpots.forId(npc.getId() | (2 << 24));
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return;
				}
				PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
				if (pocket != null) {
					player.getActionManager().setAction(new PickPocketAction(npc, pocket));
					return;
				}
				if (npc instanceof Pet) {
					if (npc != player.getPet()) {
						player.getPackets().sendGameMessage("This isn't your pet!");
						return;
					}
					Pet pet = player.getPet();
					player.getPackets().sendMessage(99, "Pet [id=" + pet.getId() + ", hunger=" + pet.getDetails().getHunger() + ", growth=" + pet.getDetails().getGrowth() + ", stage=" + pet.getDetails().getStage() + "].", player);
				}
				if (npc.getId() == 1610) {
					if (npc.getHitpoints() > 10) {
						player.sendMessage("The gargoyle isn't weak enough to be harmed by the hammer.");
						return;
					}
					if (!(npc instanceof ConditionalDeath)) {
						return;
					}
					ConditionalDeath deathNPC = (ConditionalDeath) npc;
					deathNPC.useHammer(player);
					return;
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("store")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage("That isn't your familiar.");
							return;
						}
						player.getFamiliar().store();
					} else if (npc.getDefinitions().hasOption("cure")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage("That isn't your familiar.");
							return;
						}
						if (!player.getPoison().isPoisoned()) {
							player.getPackets().sendGameMessage("Your arent poisoned or diseased.");
							return;
						} else {
							player.getFamiliar().drainSpecial(2);
							player.addPoisonImmune(120);
						}
					}
					return;
				}
				npc.turnTo(player);
				if (!player.getControllerManager().processNPCClick2(npc)) {
					return;
				}
				if (EventManager.get().handleNPCClick(player, npc, ClickOption.SECOND)) {
					return;
				}
				if (npc.getId() == 8461) {
					player.getDialogueManager().startDialogue("Turael", npc.getId());
				}
				if (npc.getId() == 9707) {
					FremennikShipmaster.sail(player, true);
				} else if (npc.getId() == 9708) {
					FremennikShipmaster.sail(player, false);
				} else if (npc.getId() == 13455) {
					player.getBank().openBank();
				} else if (npc.getId() == 2676) {
					PlayerLook.openMageMakeOver(player);
				} else if (npc.getDefinitions().hasOption("Bank")) {
					player.getBank().openBank();
				} else {
					if (Constants.DEBUG) {
						System.out.println("Clicked 2 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
					}
				}
			}
		}, true));
	}

	private static void handleOption3(final Player player, final NPC npc) {
		player.stopAll(false);
		if (npc.getId() == 2241) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 1)) {
				player.setRouteEvent(new RouteEvent(new WorldTile(npc.getX(), 3095, 0), new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						if (EventManager.get().handleNPCClick(player, npc, ClickOption.THIRD)) {
							return;
						}
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			if (EventManager.get().handleNPCClick(player, npc, ClickOption.THIRD)) {
				return;
			}
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.turnTo(npc);
				if (EventManager.get().handleNPCClick(player, npc, ClickOption.THIRD)) {
					return;
				}
				if (npc.getId() == 5913) {
					Magic.sendNormalTeleportSpell(player, 0, 0, TeleportLocations.ESSENCE_MINE);
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.demolish(player);
					return;
				}
			}
		}, true));
	}

	private static void handleOption5(final Player player, final NPC npc) {
		player.stopAll(false);
		if (npc.getDefinitions().getName().contains("Banker") || npc.getDefinitions().getName().contains("banker")) {
			player.turnTo(npc);
			if (!player.withinDistance(npc, 2)) {
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {

					@Override
					public void run() {
						npc.turnTo(player);
						player.turnTo(npc);
						ExchangeManagement.openCollectionBox(player);
					}
				}, true));
				return;
			}
			npc.turnTo(player);
			ExchangeManagement.openCollectionBox(player);
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.turnTo(npc);
				if (EventManager.get().handleNPCClick(player, npc, ClickOption.FOURTH)) {
					return;
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.repair(player, false);
					return;
				}
			}
		}, true));
	}

}