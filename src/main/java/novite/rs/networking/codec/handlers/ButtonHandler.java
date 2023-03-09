package novite.rs.networking.codec.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import novite.rs.api.event.EventManager;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.engine.process.impl.SwitchingProcessor;
import novite.rs.engine.process.impl.SwitchingProcessor.ItemSwitch;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.games.MainGameHandler;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.npc.familiar.Familiar.SpecialAttack;
import novite.rs.game.player.CombatDefinitions;
import novite.rs.game.player.EmotesManager;
import novite.rs.game.player.Equipment;
import novite.rs.game.player.Inventory;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.player.actions.Rest;
import novite.rs.game.player.actions.Smithing.ForgingInterface;
import novite.rs.game.player.content.Enchanting;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.content.PlayerDeathInformation;
import novite.rs.game.player.content.PlayerLook;
import novite.rs.game.player.content.Runecrafting;
import novite.rs.game.player.content.Runecrafting.Talismans;
import novite.rs.game.player.content.SkillCapeCustomizer;
import novite.rs.game.player.content.SkillsDialogue;
import novite.rs.game.player.content.randoms.RandomEventManager;
import novite.rs.game.player.content.shop.Shop;
import novite.rs.game.player.controlers.impl.DuelControler;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.Transportation;
import novite.rs.game.player.dialogues.impl.WorldMapConfirmation;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.codec.stream.InputStream;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.utility.Utils;

public class ButtonHandler {

	public static void handleButtons(final Player player, InputStream stream, int packetId) {
		int interfaceHash = stream.readIntV2();
		int interfaceId = interfaceHash >> 16;
		if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
			return;
		}
		if ((player.isDead() || !player.getInterfaceManager().containsInterface(interfaceId))) {
			return;
		}
		final int componentId = interfaceHash - (interfaceId << 16);
		if (componentId != 65535 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) + 1 <= componentId) {
			return;
		}
		final int itemId = stream.readUnsignedShortLE128();
		final int slotId = stream.readUnsignedShort();
		if (!player.getControllerManager().processButtonClick(interfaceId, componentId, slotId, packetId)) {
			return;
		}
		if (RandomEventManager.get().handleInterfaceInteraction(player, interfaceId, componentId)) {
			return;
		}
		if (EventManager.get().handleButtonClick(player, interfaceId, componentId, packetId, slotId, itemId)) {
			return;
		}
		if (interfaceId == 458) {
			player.getDialogueManager().continueDialogue(interfaceId, componentId);
			return;
		}
		if (interfaceId == 387) {
			if (componentId == 39 & packetId == 61) {
				player.stopAll();

				// player.getInterfaceManager().sendInventoryInterface(670);
				// player.getPackets().sendInterSetItemsOptionsScript(670, 0,
				// 93, 4, 7, "Equip", "Compare", "Stats", "Examine");
				// player.getPackets().sendUnlockIComponentOptionSlots(670, 0,
				// 0, 27, 0, 1, 2, 3);
				// player.getPackets().sendItems(93,
				// player.getInventory().getItems());
				player.getInterfaceManager().sendInterface(667);
				player.getPackets().sendIComponentSettings(667, 7, 0, 15, 1538);
				player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponRenderEmote());
				refreshEquipBonuses(player);
			}
			if (componentId == 42) {
				if (player.getInterfaceManager().containsScreenInterface()) {
					player.getPackets().sendGameMessage("Please finish what you're doing before opening the price checker.");
					return;
				}
				player.stopAll();
				player.getPriceCheckManager().initPriceCheck();
			}
			if (componentId == 45) {
				player.stopAll();
				PlayerDeathInformation.openItemsKeptOnDeath(player);
			}
			if (componentId == 8 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_HAT);
			} else if (componentId == 8 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_HAT);
			} else if (componentId == 17 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_WEAPON);
			} else if (componentId == 17 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_WEAPON);
			} else if (componentId == 20 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_CHEST);
			} else if (componentId == 20 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CHEST);
			} else if (componentId == 23) {
				if (packetId == 25) {
					player.getEquipment().sendExamine(Equipment.SLOT_SHIELD);
				} else if (packetId == 64) {
					if (player.getEquipment().getShieldId() == 11283 || player.getEquipment().getShieldId() == 11284) {
						int speed, gfxDelay;
						if (!(player.getActionManager().getAction() instanceof PlayerCombat)) {
							return;
						}
						Entity target = ((PlayerCombat) player.getActionManager().getAction()).getTarget();
						if (target != null) {
							if (player.withinDistance(target, 1)) {
								speed = 70;
							} else if (player.withinDistance(target, 5)) {
								speed = 90;
							} else if (player.withinDistance(target, 8)) {
								speed = 110;
							} else {
								speed = 130;
							}
							gfxDelay = speed + 10;
							if (player.getAttributes().get("dfs_delay") != null && (long) player.getAttributes().get("dfs_delay") - Utils.currentTimeMillis() > 0) {
								player.sendMessage("You have to wait " + (TimeUnit.MILLISECONDS.toSeconds((long) player.getAttributes().get("dfs_delay") - Utils.currentTimeMillis())) + " more seconds to activate your special attack.");
								return;
							}
							player.getAttributes().put("dfs_delay", Utils.currentTimeMillis() + 45000);
							player.setNextAnimation(new Animation(6696));
							player.setNextGraphics(new Graphics(1165));
							World.sendProjectile(player, player, ((PlayerCombat) player.getActionManager().getAction()).getTarget(), 1166, 76, 50, speed, 43, 55, 0);
							target.setNextGraphics(new Graphics(1167, gfxDelay, 100));
							target.applyHit(new Hit(target, new Random().nextInt(player.getSkills().getCombatLevel() / 2), Hit.HitLook.MAGIC_DAMAGE));
						}
						return;
					}
				} else if (packetId == 61)
					ButtonHandler.sendRemove(player, Equipment.SLOT_SHIELD);
			} else if (componentId == 26 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_LEGS);
			} else if (componentId == 26 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_LEGS);
			} else if (componentId == 29 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_HANDS);
			} else if (componentId == 29 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_HANDS);
			} else if (componentId == 32 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_FEET);
			} else if (componentId == 32 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_FEET);
			} else if (componentId == 35 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_RING);
			} else if (componentId == 35 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_RING);
			} else if (componentId == 35 && packetId == 64) {
				if (itemId == 2572) {
					player.sendMessage("You currently have: " + player.getFacade().getRowCharges() + " ring of wealth charges.");
				}
			} else if (componentId == 38 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_ARROWS);
			} else if (componentId == 38 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_ARROWS);
			} else if (componentId == 14 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_AMULET);
			} else if (componentId == 14 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_AMULET);
			} else if (componentId == 11 && packetId == 61) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CAPE);
			} else if (componentId == 11 && packetId == 25) {
				player.getEquipment().sendExamine(Equipment.SLOT_CAPE);
			} else if (componentId == 11 && packetId == 64 && itemId != 19748) {
				ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(itemId);
				if (definitions.getName().contains("cape")) {
					int skillId = -1;
					for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
						String skillName = Skills.SKILL_NAME[i];
						if (skillName.equalsIgnoreCase(definitions.getName().split(" ")[0])) {
							skillId = i;
							break;
						}
					}
					if (skillId != -1) {
						player.getSkills().set(skillId, 101);
						player.getDialogueManager().startDialogue(SimpleMessage.class, "You feel your " + Skills.SKILL_NAME[skillId].toLowerCase() + " skills increase past your regular levels.");
					}
				}
			} else if (componentId == 11 && itemId == 19748) {
				long hours = -1;
				switch (packetId) {
				case 64: // kandarin monstery
					Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2606, 3223, 0));
					break;
				case 4: // farm
					Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(2673, 3374, 0));
					break;
				case 52: // sum restore
					if (player.getFacade().getArdougneOperators()[1] == -1 || TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - player.getFacade().getArdougneOperators()[1]) >= 24) {
						player.getSkills().set(Skills.SUMMONING, player.getSkills().getLevelForXp(Skills.SUMMONING));
						player.getSkills().refresh(Skills.SUMMONING);
						player.getFacade().getArdougneOperators()[1] = System.currentTimeMillis();
					} else {
						long h = player.getFacade().getArdougneOperators()[1] + TimeUnit.DAYS.toMillis(1);
						hours = TimeUnit.MILLISECONDS.toHours(h - System.currentTimeMillis());
						player.getDialogueManager().startDialogue(SimpleMessage.class, "You must wait " + hours + " more hours to do this.");
					}
					break;
				default:
					System.out.println(packetId);
					break;
				}
			}
		}
		if (interfaceId == 548 || interfaceId == 746 || interfaceId == 387) {
			if (componentId == 11) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20769 || capeId == 20771) {
						SkillCapeCustomizer.startCustomizing(player, capeId);
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20767) {
						SkillCapeCustomizer.startCustomizing(player, capeId);
					}
				}
			}
			if (componentId == 14) {
				int amuletId = player.getEquipment().getAmuletId();
				if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					if (amuletId <= 1712 && amuletId >= 1706 || amuletId >= 10354 && amuletId <= 10361) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3087, 3496, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId == 1704 || amuletId == 10352) {
						player.getPackets().sendGameMessage("The amulet has ran out of charges. You need to recharge it if you wish it use it once more.");
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					if (amuletId <= 1712 && amuletId >= 1706 || amuletId >= 10354 && amuletId <= 10361) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(2918, 3176, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					if (amuletId <= 1712 && amuletId >= 1706 || amuletId >= 10354 && amuletId <= 10361) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3105, 3251, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					if (amuletId <= 1712 && amuletId >= 1706 || amuletId >= 10354 && amuletId <= 10361) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3293, 3163, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				}
			} // TODO
			if (componentId == 50) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					ButtonHandler.sendRemove(player, Equipment.SLOT_AURA);
					player.getAuraManager().removeAura();
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getEquipment().sendExamine(Equipment.SLOT_AURA);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getAuraManager().activate();
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getAuraManager().sendAuraRemainingTime();
				}
			}
			if ((interfaceId == 548 && componentId == 180) || (interfaceId == 746 && componentId == 182)) {
				if (player.getInterfaceManager().containsScreenInterface() || player.getInterfaceManager().containsInventoryInter()) {
					player.getPackets().sendGameMessage("Please finish what you're doing before opening the world map.");
					return;
				}
				player.getDialogueManager().startDialogue(WorldMapConfirmation.class);
			} else if ((interfaceId == 548 && componentId == 0) || (interfaceId == 746 && componentId == 229)) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON7_PACKET) {
					player.getSkills().resetXpCounter();
				}
			}
		} else if (interfaceId == 182) {
			if (player.getInterfaceManager().containsInventoryInter()) {
				return;
			}
			if (componentId == 6 || componentId == 13) {
				if (!player.hasFinished()) {
					player.logout();
				}
			}
		} else if (interfaceId == 432) {
			final int index = Enchanting.getComponentIndex(componentId);
			if (index == -1) {
				return;
			}
			Enchanting.processBoltEnchantSpell(player, index, packetId == 61 ? 1 : packetId == 64 ? 5 : 10);
		} else if (interfaceId == 880) {
			if (componentId >= 7 && componentId <= 19) {
				Familiar.setLeftclickOption(player, (componentId - 7) / 2);
			} else if (componentId == 21) {
				Familiar.confirmLeftOption(player);
			} else if (componentId == 25) {
				Familiar.setLeftclickOption(player, 7);
			}
		} else if (interfaceId == 662) {
			if (player.getFamiliar() == null) {
				if (player.getPet() == null) {
					return;
				}
				if (componentId == 49) {
					player.getPet().call();
				} else if (componentId == 51) {
					player.getDialogueManager().startDialogue("DismissD");
				}
				return;
			}
			if (componentId == 49) {
				player.getFamiliar().call();
			} else if (componentId == 51) {
				player.getDialogueManager().startDialogue("DismissD");
			} else if (componentId == 67) {
				player.getFamiliar().takeBob();
			} else if (componentId == 69) {
				player.getFamiliar().renewFamiliar();
			} else if (componentId == 74) {
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK) {
					player.getFamiliar().setSpecial(true);
				}
				if (player.getFamiliar().hasSpecialOn()) {
					player.getFamiliar().submitSpecial(player);
				}
			}
		} else if (interfaceId == 747) {
			if (componentId == 7) {
				Familiar.selectLeftOption(player);
			} else if (player.getFamiliar() == null) {
				return;
			}
			if (componentId == 10 || componentId == 19) {
				player.getFamiliar().call();
			} else if (componentId == 11 || componentId == 20) {
				player.getDialogueManager().startDialogue("DismissD");
			} else if (componentId == 12 || componentId == 21) {
				player.getFamiliar().takeBob();
			} else if (componentId == 13 || componentId == 22) {
				player.getFamiliar().renewFamiliar();
			} else if (componentId == 18 || componentId == 18) {
				player.getFamiliar().sendFollowerDetails();
			} else if (componentId == 17) {
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK) {
					player.getFamiliar().setSpecial(true);
				}
				if (player.getFamiliar().hasSpecialOn()) {
					player.getFamiliar().submitSpecial(player);
				}
			}
		} else if (interfaceId == 17) {
			if (componentId == 28) {
				PlayerDeathInformation.sendItemsKeptOnDeath(player, player.getVarsManager().getBitValue(9226) == 0);
			}
		} else if (interfaceId == 309) {
			PlayerLook.handleBeardButtons(player, componentId, slotId);
		} else if (interfaceId == 187) {
			if (componentId == 1) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getMusicsManager().playAnotherMusic(slotId / 2);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getMusicsManager().addToPlayList(slotId / 2);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getMusicsManager().removeFromPlayList(slotId / 2);
				}
			} else if (componentId == 4) {
				player.getMusicsManager().addPlayingMusicToPlayList();
			} else if (componentId == 10) {
				player.getMusicsManager().switchPlayListOn();
			} else if (componentId == 11) {
				player.getMusicsManager().clearPlayList();
			} else if (componentId == 13) {
				player.getMusicsManager().switchShuffleOn();
			}
		} else if (interfaceId == 464) {
			player.getEmotesManager().useBookEmote(interfaceId == 464 ? componentId : EmotesManager.getId(slotId, packetId));
		} else if (interfaceId == 192) {
			if (componentId == 2) {
				player.getCombatDefinitions().switchDefensiveCasting();
			} else if (componentId == 7) {
				player.getCombatDefinitions().switchShowCombatSpells();
			} else if (componentId == 9) {
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			} else if (componentId == 11) {
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			} else if (componentId == 13) {
				player.getCombatDefinitions().switchShowSkillSpells();
			} else if (componentId >= 15 & componentId <= 17) {
				player.getCombatDefinitions().setSortSpellBook(componentId - 15);
			} else {
				Magic.processNormalSpell(player, componentId, packetId);
			}
		} else if (interfaceId == 334) {
			if (componentId == 22) {
				player.closeInterfaces();
			} else if (componentId == 21) {
				player.getTrade().accept(false);
			}
		} else if (interfaceId == 335) {
			if (componentId == 16) {
				player.getTrade().accept(true);
			} else if (componentId == 18) {
				player.closeInterfaces();
			} else if (componentId == 31) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().removeItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getTrade().removeItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getTrade().removeItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getTrade().removeItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int value) {
							if (value < 0 || slotId < 0) {
								return;
							}
							player.getTrade().removeItem(slotId, value);
						}
					});
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON6_PACKET) {
					player.getTrade().sendValue(slotId, false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON10_PACKET) {
					player.getTrade().sendExamine(slotId, false);
				}
			} else if (componentId == 34) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().sendValue(slotId, true);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getTrade().sendExamine(slotId, true);
				}
			}
		} else if (interfaceId == 336) {
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().addItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getTrade().addItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getTrade().addItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getTrade().addItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int value) {
							if (value < 0 || slotId < 0) {
								return;
							}
							player.getTrade().addItem(slotId, value);
						}
					});
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON6_PACKET) {
					player.sendMessage("Lending is not yet added!"); // TODO
																		// lending
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getInventory().sendExamine(slotId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getTrade().sendValue(slotId);
				}
			}
		} else if (interfaceId == 300) {
			ForgingInterface.handleIComponents(player, componentId);
		} else if (interfaceId == 206) {
			if (componentId == 15) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getPriceCheckManager().removeItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getPriceCheckManager().removeItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getPriceCheckManager().removeItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getPriceCheckManager().removeItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("pc_isRemove", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				}
			}
			/*
			 * } else if (interfaceId == 672) { if (componentId == 16) { if
			 * (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
			 * Summoning.sendCreatePouch(player, slotId2, 1); else if (packetId
			 * == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
			 * Summoning.sendCreatePouch(player, slotId2, 5); else if (packetId
			 * == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
			 * Summoning.sendCreatePouch(player, slotId2, 10); else if (packetId
			 * == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
			 * Summoning.sendCreatePouch(player, slotId2, Integer.MAX_VALUE);
			 * else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
			 * Summoning.sendCreatePouch(player, slotId2, 28);// x else if
			 * (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET) {
			 * player.getPackets().sendGameMessage( "You currently need " +
			 * ItemDefinitions.getItemDefinitions( slotId2)
			 * .getCreateItemRequirements()); } }
			 */
		} else if (interfaceId == 207) {
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getPriceCheckManager().addItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getPriceCheckManager().addItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getPriceCheckManager().addItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getPriceCheckManager().addItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("pc_isRemove");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
		} else if (interfaceId == 665) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) {
				return;
			}
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getFamiliar().getBob().addItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getFamiliar().getBob().addItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getFamiliar().getBob().addItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getFamiliar().getBob().addItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bob_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("bob_isRemove");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
		} else if (interfaceId == 671) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) {
				return;
			}
			if (componentId == 27) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getFamiliar().getBob().removeItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getFamiliar().getBob().removeItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getFamiliar().getBob().removeItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getFamiliar().getBob().removeItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bob_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("bob_isRemove", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				}
			} else if (componentId == 29) {
				player.getFamiliar().takeBob();
			}
		} else if (interfaceId == 916) {
			SkillsDialogue.handleSetQuantityButtons(player, componentId);
		} else if (interfaceId == 193) {
			if (componentId == 5) {
				player.getCombatDefinitions().switchShowCombatSpells();
			} else if (componentId == 7) {
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			} else if (componentId >= 9 && componentId <= 11) {
				player.getCombatDefinitions().setSortSpellBook(componentId - 9);
			} else if (componentId == 18) {
				player.getCombatDefinitions().switchDefensiveCasting();
			} else {
				Magic.processAncientSpell(player, componentId, packetId);
			}
		} else if (interfaceId == 430) {
			if (componentId == 5) {
				player.getCombatDefinitions().switchShowCombatSpells();
			} else if (componentId == 7) {
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			} else if (componentId == 9) {
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			} else if (componentId >= 11 & componentId <= 13) {
				player.getCombatDefinitions().setSortSpellBook(componentId - 11);
			} else if (componentId == 20) {
				player.getCombatDefinitions().switchDefensiveCasting();
			} else {
				Magic.processLunarSpell(player, componentId, packetId);
			}
		} else if (interfaceId == 261) {
			if (player.getInterfaceManager().containsInventoryInter()) {
				return;
			}
			if (componentId == 18) {
				player.getAchievementManager().sendAchievements();
				return;
			}
			if (componentId == 14) {
				if (player.getInterfaceManager().containsScreenInterface()) {
					player.getPackets().sendGameMessage("Please close the interface you have open before setting your graphic options.");
					return;
				}
				player.stopAll();
				player.getInterfaceManager().sendInterface(742);
			} else if (componentId == 4) {
				player.switchAllowChatEffects();
			} else if (componentId == 5) {
				player.getInterfaceManager().sendSettings(982);
			} else if (componentId == 6) {
				player.switchMouseButtons();
			} else if (componentId == 16) {
				if (player.getInterfaceManager().containsScreenInterface()) {
					player.getPackets().sendGameMessage("Please close the interface you have open before setting your audio options.");
					return;
				}
				player.stopAll();
				player.getInterfaceManager().sendInterface(743);
			} else if (componentId == 7) {
				player.switchAcceptAid();
			}
		} else if (interfaceId == 982) {
			if (componentId == 5) {
				player.getInterfaceManager().sendSettings();
			} else if (componentId == 41) {
				player.setPrivateChatSetup(player.getPrivateChatSetup() == 0 ? 1 : 0);
			} else if (componentId >= 49 && componentId <= 61) {
				player.setPrivateChatSetup(componentId - 48);
			} else if (componentId >= 72 && componentId <= 91) {
				player.setFriendChatSetup(componentId - 72);
			}
			player.refreshOtherChatsSetup();
		} else if (interfaceId == 271) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (componentId == 8 || componentId == 42) {
						player.getPrayer().switchPrayer(slotId);
					} else if (componentId == 43 && player.getPrayer().isUsingQuickPrayer()) {
						player.getPrayer().switchSettingQuickPrayer();
					}
				}
			});
		} else if (interfaceId == 499) {
			int skillMenu = -1;
			if (player.getTemporaryAttributtes().get("skillMenu") != null) {
				skillMenu = (Integer) player.getTemporaryAttributtes().get("skillMenu");
			}
			switch (componentId) {
			case 10:
				player.getPackets().sendConfig(965, skillMenu);
				break;
			case 11:
				player.getPackets().sendConfig(965, 1024 + skillMenu);
				break;
			case 12:
				player.getPackets().sendConfig(965, 2048 + skillMenu);
				break;
			case 13:
				player.getPackets().sendConfig(965, 3072 + skillMenu);
				break;
			case 14:
				player.getPackets().sendConfig(965, 4096 + skillMenu);
				break;
			case 15:
				player.getPackets().sendConfig(965, 5120 + skillMenu);
				break;
			case 16:
				player.getPackets().sendConfig(965, 6144 + skillMenu);
				break;
			case 17:
				player.getPackets().sendConfig(965, 7168 + skillMenu);
				break;
			case 18:
				player.getPackets().sendConfig(965, 8192 + skillMenu);
				break;
			case 19:
				player.getPackets().sendConfig(965, 9216 + skillMenu);
				break;
			case 20:
				player.getPackets().sendConfig(965, 10240 + skillMenu);
				break;
			case 21:
				player.getPackets().sendConfig(965, 11264 + skillMenu);
				break;
			case 22:
				player.getPackets().sendConfig(965, 12288 + skillMenu);
				break;
			case 23:
				player.getPackets().sendConfig(965, 13312 + skillMenu);
				break;
			case 29: // close inter
				player.stopAll();
				break;
			}

		} else if (interfaceId == 449) {
			if (componentId == 1) {
				Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
				if (shop == null) {
					return;
				}
				shop.sendInventory(player);
			} else if (componentId == 21) {
				Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
				if (shop == null) {
					return;
				}
				Integer slot = (Integer) player.getTemporaryAttributtes().get("ShopSelectedSlot");
				if (slot == null) {
					return;
				}
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					shop.buy(player, slot, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					shop.buy(player, slot, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					shop.buy(player, slot, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					shop.buy(player, slot, 50);
				}

			}
		} else if (interfaceId == 620) {
			if (componentId == 25) {
				Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
				if (shop == null) {
					return;
				}
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					shop.sendInfo(player, slotId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					shop.buy(player, slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					shop.buy(player, slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					shop.buy(player, slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					shop.buy(player, slotId, 50);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					shop.buy(player, slotId, 500);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					shop.sendExamine(player, slotId);
				}
			}
			return;
		} else if (interfaceId == 621) {
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getInventory().sendExamine(slotId);
				} else {
					Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
					if (shop == null) {
						return;
					}
					if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
						shop.sendValue(player, slotId);
					} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
						shop.sell(player, slotId, 1);
					} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
						shop.sell(player, slotId, 5);
					} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
						shop.sell(player, slotId, 10);
					} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
						shop.sell(player, slotId, 50);
					}
				}
			}
		} else if (interfaceId == 640) {
			if (componentId == 18 || componentId == 22) {
				player.getTemporaryAttributtes().put("WillDuelFriendly", true);
				player.getPackets().sendConfig(283, 67108864);
			} else if (componentId == 19 || componentId == 21) {
				player.getTemporaryAttributtes().put("WillDuelFriendly", false);
				player.getPackets().sendConfig(283, 134217728);
			} else if (componentId == 20) {
				DuelControler.challenge(player);
			}
		} else if (interfaceId == 650) {
			if (componentId == 17) {
				player.stopAll();
				player.setNextWorldTile(new WorldTile(2974, 4384, 0));
				player.getControllerManager().startController("CorpBeastControler");
			} else if (componentId == 18) {
				player.closeInterfaces();
			}
		} else if (interfaceId == 670) {
			if (componentId == 0) {
				if (slotId >= player.getInventory().getItemsContainerSize()) {
					return;
				}
				Item item = player.getInventory().getItem(slotId);
				if (item == null) {
					return;
				}
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					if (sendWear(player, slotId, item.getId())) {
						ButtonHandler.refreshEquipBonuses(player);
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
		} else if (interfaceId == Inventory.INVENTORY_INTERFACE) { // inventory
			if (componentId == 0) {
				if (slotId > 27 || player.getInterfaceManager().containsInventoryInter()) {
					return;
				}
				Item item = player.getInventory().getItem(slotId);
				if (item == null || item.getId() != itemId) {
					return;
				}
				if (Talismans.getTalisman(itemId) != null) {
					Talismans talisman = Talismans.getTalisman(itemId);
					Magic.sendNormalTeleportSpell(player, 0, 0, talisman.getTile());
					return;
				}
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					InventoryOptionsHandler.handleItemOption1(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					InventoryOptionsHandler.handleItemOption2(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					InventoryOptionsHandler.handleItemOption3(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					InventoryOptionsHandler.handleItemOption4(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					InventoryOptionsHandler.handleItemOption5(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON6_PACKET) {
					InventoryOptionsHandler.handleItemOption6(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON7_PACKET) {
					InventoryOptionsHandler.handleItemOption7(player, slotId, itemId, item);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					InventoryOptionsHandler.handleItemOption8(player, slotId, itemId, item);
				}
			}
		} else if (interfaceId == 742) {
			if (componentId == 46) {
				player.stopAll();
			}
		} else if (interfaceId == 743) {
			if (componentId == 20) {
				player.stopAll();
			}
		} else if (interfaceId == 741) {
			if (componentId == 9) {
				player.stopAll();
			}
		} else if (interfaceId == 749) {
			if (componentId == 1) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getPrayer().switchQuickPrayers();
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getPrayer().switchSettingQuickPrayer();
				}
			}
		} else if (interfaceId == 750) {
			if (componentId == 1) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.toogleRun(player.isResting() ? false : true);
					if (player.isResting()) {
						player.stopAll();
					}
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					if (player.isResting()) {
						player.stopAll();
						return;
					}
					long currentTime = Utils.currentTimeMillis();
					if (player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an emote.");
						return;
					}
					if (player.getLockDelay() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an action.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest());
				}
			}
		} else if (interfaceId == 11) {
			if (componentId == 17) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getBank().depositItem(slotId, 1, false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getBank().depositItem(slotId, 5, false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getBank().depositItem(slotId, 10, false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getBank().depositItem(slotId, Integer.MAX_VALUE, false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("bank_isWithdraw");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			} else if (componentId == 18) {
				player.getBank().depositAllInventory(false);
			} else if (componentId == 20) {
				player.getBank().depositAllEquipment(false);
			}
		} else if (interfaceId == 762) {
			if (componentId == 15) {
				player.getBank().switchInsertItems();
			} else if (componentId == 19) {
				player.getBank().switchWithdrawNotes();
			} else if (componentId == 33) {
				player.getBank().depositAllInventory(true);
			} else if (componentId == 35) {
				player.getBank().depositAllEquipment(true);
			} else if (componentId >= 44 && componentId <= 62) {
				int tabId = 9 - ((componentId - 44) / 2);
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getBank().setCurrentTab(tabId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getBank().collapse(tabId);
				}
			} else if (componentId == 93) {
				if (player.getAttributes().get("novite_games_chest") != null) {
					MainGameHandler.get().withdrawItemChest(player, slotId, packetId);
					return;
				}
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getBank().withdrawItem(slotId, 1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getBank().withdrawItem(slotId, 5);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getBank().withdrawItem(slotId, 10);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getBank().withdrawLastAmount(slotId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int input) {
							player.getBank().withdrawItem(slotId, input);
							player.getBank().setLastX(input);
							player.getBank().refreshLastX();
						}
					});
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getBank().withdrawItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON6_PACKET) {
					player.getBank().withdrawItemButOne(slotId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getBank().sendExamine(slotId);
				}

			}
		} else if (interfaceId == 763) {
			if (componentId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					player.getBank().depositItem(slotId, 1, true);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getBank().depositItem(slotId, 5, true);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getBank().depositItem(slotId, 10, true);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getBank().depositLastAmount(slotId);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON5_PACKET) {
					player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

						@Override
						public void handle(int input) {
							player.getBank().depositItem(slotId, input, player.getInterfaceManager().containsInterface(11) ? false : true);
							player.getBank().setLastX(input);
							player.getBank().refreshLastX();
						}
					});
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON9_PACKET) {
					player.getBank().depositItem(slotId, Integer.MAX_VALUE, true);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON8_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
		} else if (interfaceId == 767) {
			if (componentId == 10) {
				player.getBank().openBank();
			}
		} else if (interfaceId == 884) {
			if (componentId == 4) {
				int weaponId = player.getEquipment().getWeaponId();
				synchronized (SwitchingProcessor.LOCK) {
					if (SwitchingProcessor.getSwitchesForPlayer(player, false).size() > 0) {
						List<ItemSwitch> switches = SwitchingProcessor.getSwitchesForPlayer(player, true);
						for (ItemSwitch itemSwitch : switches) {
							ButtonHandler.sendWear(itemSwitch.getPlayer(), itemSwitch.getSlotId(), itemSwitch.getItemId());
						}
					}
				}
				if (player.hasInstantSpecial(weaponId)) {
					player.performInstantSpecial(weaponId);
					return;
				}
				player.getCombatDefinitions().switchUsingSpecialAttack();
			} else if (componentId >= 11 && componentId <= 14) {
				player.getCombatDefinitions().setAttackStyle(componentId - 11);
			} else if (componentId == 15) {
				player.getCombatDefinitions().switchAutoRelatie();
			}
		} else if (interfaceId == 755) {
			if (componentId == 44) {
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasResizableScreen() ? 746 : 548, 2);
			}
		} else if (interfaceId == 20) {
			SkillCapeCustomizer.handleSkillCapeCustomizer(player, componentId);
		} else if (interfaceId == 1056) {
			if (componentId == 102) {
				player.getInterfaceManager().sendInterface(917);
			}
		} else if (interfaceId == 751) {
			if (componentId == 25) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.getFriendsIgnores().setPrivateStatus(0);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET) {
					player.getFriendsIgnores().setPrivateStatus(1);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.getFriendsIgnores().setPrivateStatus(2);
				}
			} else if (componentId == 25) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET) {
					player.setFilterGame(false);
				} else if (packetId == DefaultGameDecoder.ACTION_BUTTON4_PACKET) {
					player.setFilterGame(true);
				}
			}
		} else if (interfaceId == 1163 || interfaceId == 1164 || interfaceId == 1168 || interfaceId == 1170 || interfaceId == 1173) {
			player.getDominionTower().handleButtons(interfaceId, componentId);
		} else if (interfaceId == 900) {
			PlayerLook.handleMageMakeOverButtons(player, componentId);
		} else if (interfaceId == 1108 || interfaceId == 1109 || interfaceId == 1110) {
			player.getFriendsIgnores().handleFriendChatButtons(interfaceId, componentId, packetId);
		} else if (interfaceId == 1079) {
			player.closeInterfaces();
		}
		// if (Constants.DEBUG) {
		// System.out.println("InterfaceId " + interfaceId + ", componentId " +
		// componentId + ", slotId " + slotId + ", slotId2 " + itemId +
		// ", PacketId: " + packetId);
		// }
	}

	public static void sendRemove(Player player, int slotId) {
		if (slotId >= 15) {
			return;
		}
		Item item = player.getEquipment().getItem(slotId);
		if (item == null || !player.getInventory().addItem(item.getId(), item.getAmount())) {
			return;
		}
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		player.getAppearence().generateAppearenceData();
		if (Runecrafting.isTiara(item.getId())) {
			player.getPackets().sendConfig(491, 0);
		}
		if (slotId == 3) {
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		}
		player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponRenderEmote());
	}

	public static boolean sendWear(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead()) {
			return false;
		}
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId) {
			return false;
		}
		if (!Equipment.canWear(item, player)) {
			return false;
		}
		if (item.getDefinitions().isNoted() || !item.getDefinitions().isWearItem(player.getAppearence().isMale()) && item.getDefinitions().id != 4084) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots() && player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage("Not enough free space in your inventory.");
			return true;
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0) {
					continue;
				}
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120) {
					continue;
				}
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments) {
						player.getPackets().sendGameMessage("You are not high enough level to use this item.");
					}
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments) {
			return true;
		}
		if (!player.getControllerManager().canEquip(targetSlot, itemId)) {
			return false;
		}
		player.getInventory().deleteItem(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(5).getId(), player.getEquipment().getItem(5).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null && Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(3).getId(), player.getEquipment().getItem(3).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null && (itemId != player.getEquipment().getItem(targetSlot).getId() || !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
				player.getInventory().refresh(slotId);
			} else {
				player.getInventory().addItem(new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
			}
			player.getEquipment().getItems().set(targetSlot, null);
		}
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		player.getAppearence().generateAppearenceData();
		player.getPackets().sendSound(2240, 0, 1);
		if (targetSlot == 3) {
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		}
		player.getCharges().wear(targetSlot);
		player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponRenderEmote());
		if (player.getInterfaceManager().containsInterface(667))
			ButtonHandler.refreshEquipBonuses(player);
		return true;
	}

	public static boolean sendWear2(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead()) {
			return false;
		}
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId) {
			return false;
		}
		if (item.getDefinitions().isNoted() || !item.getDefinitions().isWearItem(player.getAppearence().isMale())) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots() && player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		if (item.getId() == 2572 && player.getFacade().getRowCharges() <= 0) {
			player.getFacade().setRowCharges(100);
			player.sendMessage("You equip a new ring of wealth and receive " + player.getFacade().getRowCharges() + " charges on it.");
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0) {
					continue;
				}
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120) {
					continue;
				}
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments) {
						player.getPackets().sendGameMessage("You are not high enough level to use this item.");
					}
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments) {
			return false;
		}
		if (!player.getControllerManager().canEquip(targetSlot, itemId)) {
			return false;
		}
		player.getInventory().getItems().remove(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null && Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null && (itemId != player.getEquipment().getItem(targetSlot).getId() || !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
			} else {
				player.getInventory().getItems().add(new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
			}
			player.getEquipment().getItems().set(targetSlot, null);
		}
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3) {
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		}
		player.getCharges().wear(targetSlot);
		player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponRenderEmote());
		return true;
	}

	public static void sendWear(Player player, int[] slotIds) {
		if (player.hasFinished() || player.isDead()) {
			return;
		}

		boolean worn = false;
		Item[] copy = player.getInventory().getItems().getItemsCopy();
		for (int slotId : slotIds) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null) {
				continue;
			}
			if (sendWear2(player, slotId, item.getId())) {
				worn = true;
			}
		}
		player.getInventory().refreshItems(copy);
		if (worn) {
			player.getAppearence().generateAppearenceData();
			player.getPackets().sendSound(2240, 0, 1);
			player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponRenderEmote());
		}
	}

	public static void refreshEquipBonuses(Player player) {
		final int interfaceId = 667;
		for (Object[] element : info) {
			int bonus = player.getCombatDefinitions().getBonuses()[(int) element[1]];
			String sign = bonus > 0 ? "+" : "";
			player.getPackets().sendIComponentText(interfaceId, (int) element[0], element[2] + ": " + sign + bonus);
		}
	}

	private static final Object[][] info = new Object[][] { { 31, 0, "Stab" }, { 32, 1, "Slash" }, { 33, 2, "Crush" }, { 34, 3, "Magic" }, { 35, 4, "Range" }, { 36, 5, "Stab" }, { 37, 6, "Slash" }, { 38, 7, "Crush" }, { 39, 8, "Magic" }, { 40, 9, "Range" }, { 41, 10, "Summoning" }, { 42, CombatDefinitions.ABSORVE_MELEE_BONUS, "Absorb Melee" }, { 43, CombatDefinitions.ABSORVE_MAGE_BONUS, "Absorb Magic" }, { 44, CombatDefinitions.ABSORVE_RANGE_BONUS, "Absorb Range" }, { 45, 14, "Strength" }, { 46, 15, "Ranged Str" }, { 47, 16, "Prayer" }, { 48, 17, "Magic Damage" } };
}
