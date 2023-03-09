package novite.rs.game.player.content;

import novite.rs.cache.loaders.ClientScriptMap;
import novite.rs.cache.loaders.GeneralRequirementMap;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.impl.StartTutorial;

public final class PlayerLook {

	public static void openCharacterCustomizing(Player player) {
		int interfaceId = 1028;

		player.getPackets().sendWindowsPane(interfaceId, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(interfaceId, 45, 0, 11, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(interfaceId, 107, 0, 50, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(interfaceId, 111, 0, 250, 0);
		player.getVarsManager().sendVarBit(8093, player.getAppearence().isMale() ? 0 : 1);

		/*
		 * ActionSender.sendAMask(player, 2, 1028, 45, 0, 204);
		 * ActionSender.sendAMask(player, 2, 1028, 111, 0, 204);
		 * ActionSender.sendAMask(player, 2, 1028, 107, 0, 204);
		 */
		/*
		 * player.getPackets().sendUnlockIComponentOptionSlots(1028, 65, 0, 11,
		 * 0); player.getPackets().sendUnlockIComponentOptionSlots(1028, 128, 0,
		 * 50, 0); player.getPackets().sendUnlockIComponentOptionSlots(1028,
		 * 132, 0, 250, 0); player.getPackets().sendConfig(8093,
		 * player.getAppearence().isMale() ? 0 : 1);
		 */
	}

	public static void handleCharacterCustomizingButtons(Player player, int buttonId, int slotId) {
		if (buttonId == 117) { // confirm
			player.getPackets().sendWindowsPane(player.getInterfaceManager().hasResizableScreen() ? 746 : 548, 0);
			player.getTemporaryAttributtes().remove("SelectWearDesignD");
			player.getTemporaryAttributtes().remove("ViewWearDesign");
			player.getTemporaryAttributtes().remove("ViewWearDesignD");
			player.getAppearence().generateAppearenceData();
			if (player.getTemporaryAttributtes().remove("new_player") != null)
				StartTutorial.giveStarter(player);
		} else if (buttonId >= 48 && buttonId <= 67) {// top buttons
			player.getTemporaryAttributtes().put("ViewWearDesign", (buttonId - 48));
			player.getTemporaryAttributtes().put("ViewWearDesignD", 0);
			setDesign(player, buttonId - 48, 0);
		} else if (buttonId >= 83 && buttonId <= 88) {// bottom buttons
			Integer index = (Integer) player.getTemporaryAttributtes().get("ViewWearDesign");
			if (index == null) {
				return;
			}
			player.getTemporaryAttributtes().put("ViewWearDesignD", (buttonId - 83));
			setDesign(player, index, buttonId - 83);
		} else if (buttonId == 39 || buttonId == 38) {
			setGender(player, buttonId == 38);
		} else if (buttonId == 45) {
			setSkin(player, slotId);
		} else if (buttonId >= 95 && buttonId <= 100) {
			player.getTemporaryAttributtes().put("SelectWearDesignD", (buttonId - 95));
		} else if (buttonId == 107) {
			Integer index = (Integer) player.getTemporaryAttributtes().get("SelectWearDesignD");
			if (index == null || index == 1) {
				boolean male = player.getAppearence().isMale();
				int map1 = ClientScriptMap.getMap(male ? 3304 : 3302).getIntValue(slotId);
				if (map1 == 0) {
					return;
				}
				GeneralRequirementMap map = GeneralRequirementMap.getMap(map1);
				player.getAppearence().setHairStyle(map.getIntValue(788));
				if (!male) {
					player.getAppearence().setBeardStyle(player.getAppearence().getHairStyle());
				}
			} else if (index == 2) {
				player.getAppearence().setTopStyle(ClientScriptMap.getMap(player.getAppearence().isMale() ? 3287 : 1591).getIntValue(slotId));
				player.getAppearence().setArmsStyle(player.getAppearence().isMale() ? 26 : 65); // default
				player.getAppearence().setWristsStyle(player.getAppearence().isMale() ? 34 : 68); // default
				player.getAppearence().generateAppearenceData();
			} else if (index == 3) {
				player.getAppearence().setLegsStyle(ClientScriptMap.getMap(player.getAppearence().isMale() ? 3289 : 1607).getIntValue(slotId));
			} else if (index == 4) {
				player.getAppearence().setBootsStyle(ClientScriptMap.getMap(player.getAppearence().isMale() ? 1136 : 1137).getIntValue(slotId));
			} else if (player.getAppearence().isMale()) {
				player.getAppearence().setBeardStyle(ClientScriptMap.getMap(3307).getIntValue(slotId));
			}
		} else if (buttonId == 111) {
			Integer index = (Integer) player.getTemporaryAttributtes().get("SelectWearDesignD");
			if (index == null || index == 0) {
				setSkin(player, slotId);
			} else {
				if (index == 1 || index == 5) {
					player.getAppearence().setHairColor(ClientScriptMap.getMap(2345).getIntValue(slotId));
				} else if (index == 2) {
					player.getAppearence().setTopColor(ClientScriptMap.getMap(3283).getIntValue(slotId));
				} else if (index == 3) {
					player.getAppearence().setLegsColor(ClientScriptMap.getMap(3283).getIntValue(slotId));
				} else {
					player.getAppearence().setBootsColor(ClientScriptMap.getMap(3297).getIntValue(slotId));
				}
			}
		}
	}

	public static void setSkin(Player player, int index) {
		player.getAppearence().setSkinColor(ClientScriptMap.getMap(748).getIntValue(index));
	}

	public static void setGender(Player player, boolean male) {
		if (male == player.getAppearence().isMale()) {
			return;
		}
		if (!male) {
			player.getAppearence().female();
		} else {
			player.getAppearence().male();
		}
		Integer index1 = (Integer) player.getTemporaryAttributtes().get("ViewWearDesign");
		Integer index2 = (Integer) player.getTemporaryAttributtes().get("ViewWearDesignD");
		setDesign(player, index1 != null ? index1 : 0, index2 != null ? index2 : 0);
		player.getAppearence().generateAppearenceData();
		player.getVarsManager().sendVarBit(8093, male ? 0 : 1);
	}

	public static void setDesign(Player player, int index1, int index2) {
		int map1 = ClientScriptMap.getMap(3278).getIntValue(index1);
		if (map1 == 0) {
			return;
		}
		boolean male = player.getAppearence().isMale();
		int map2Id = GeneralRequirementMap.getMap(map1).getIntValue((male ? 1169 : 1175) + index2);
		if (map2Id == 0) {
			return;
		}
		GeneralRequirementMap map = GeneralRequirementMap.getMap(map2Id);
		for (int i = 1182; i <= 1186; i++) {
			int value = map.getIntValue(i);
			if (value == -1) {
				continue;
			}
			player.getAppearence().setLook(i - 1180, value);
		}
		for (int i = 1187; i <= 1190; i++) {
			int value = map.getIntValue(i);
			if (value == -1) {
				continue;
			}
			player.getAppearence().setColor(i - 1186, value);
		}
		if (!player.getAppearence().isMale()) {
			player.getAppearence().setBeardStyle(player.getAppearence().getHairStyle());
		}

	}

	public static void openMageMakeOver(Player player) {
		player.getInterfaceManager().sendInterface(900);
		player.getPackets().sendIComponentText(900, 33, "Confirm");
		player.getPackets().sendConfigByFile(6098, player.getAppearence().isMale() ? 0 : 1);
		player.getPackets().sendConfigByFile(6099, player.getAppearence().getSkinColor());
		player.getTemporaryAttributtes().put("MageMakeOverGender", player.getAppearence().isMale());
		player.getTemporaryAttributtes().put("MageMakeOverSkin", player.getAppearence().getSkinColor());
	}

	public static void handleMageMakeOverButtons(Player player, int buttonId) {
		if (buttonId == 16 || buttonId == 17) {
			player.getTemporaryAttributtes().put("MageMakeOverGender", buttonId == 16);
		} else if (buttonId >= 20 && buttonId <= 31) {

			int skin;
			if (buttonId == 31) {
				skin = 11;
			} else if (buttonId == 30) {
				skin = 10;
			} else if (buttonId == 20) {
				skin = 9;
			} else if (buttonId == 21) {
				skin = 8;
			} else if (buttonId == 22) {
				skin = 7;
			} else if (buttonId == 29) {
				skin = 6;
			} else if (buttonId == 28) {
				skin = 5;
			} else if (buttonId == 27) {
				skin = 4;
			} else if (buttonId == 26) {
				skin = 3;
			} else if (buttonId == 25) {
				skin = 2;
			} else if (buttonId == 24) {
				skin = 1;
			} else {
				skin = 0;
			}
			player.getTemporaryAttributtes().put("MageMakeOverSkin", skin);
		} else if (buttonId == 33) {
			Boolean male = (Boolean) player.getTemporaryAttributtes().remove("MageMakeOverGender");
			Integer skin = (Integer) player.getTemporaryAttributtes().remove("MageMakeOverSkin");
			player.closeInterfaces();
			if (male == null || skin == null) {
				return;
			}
			if (male == player.getAppearence().isMale() && skin == player.getAppearence().getSkinColor()) {
				player.getDialogueManager().startDialogue("MakeOverMage", 2676, 1);
			} else {
				player.getDialogueManager().startDialogue("MakeOverMage", 2676, 2);
				if (player.getAppearence().isMale() != male) {
					if (male) {
						player.getAppearence().resetAppearence();
					} else {
						player.getAppearence().female();
					}
				}
				player.getAppearence().setSkinColor(skin);
				player.getAppearence().generateAppearenceData();
			}
		}
	}

	public static void handleBeardButtons(Player player, int buttonId, int slotId) {// Hair
																					// and
																					// color
																					// match
																					// button
																					// count
																					// so
																					// just
																					// loop
																					// and
		// do ++, but cant find button ids
		if (buttonId == 6) {
			player.getTemporaryAttributtes().put("hairSaloon", true);
		} else if (buttonId == 7) {
			player.getTemporaryAttributtes().put("hairSaloon", false);
		} else if (buttonId == 18) {
			player.getInterfaceManager().closeScreenInterface();
			player.getAppearence().generateAppearenceData();
		} else if (buttonId == 10) {
			int hairCoordinate = -1;
			if ((boolean) player.getTemporaryAttributtes().get("hairSaloon")) {
				if (slotId == 1) {
					hairCoordinate = 256;
				} else if (slotId == 3) {
					hairCoordinate = 257;
				} else if (slotId == 5) {
					hairCoordinate = 258;
				} else if (slotId == 7) {
					hairCoordinate = 259;
				} else if (slotId == 9) {
					hairCoordinate = 260;
				} else if (slotId == 11) {
					hairCoordinate = 261;
				} else if (slotId == 12) {
					hairCoordinate = 262;
				} else if (slotId == 13) {
					hairCoordinate = 5;
				} else if (slotId == 15) {
					hairCoordinate = 7;
				} else if (slotId == 17) {
					hairCoordinate = 264;
				} else if (slotId == 19) {
					hairCoordinate = 91;
				} else if (slotId == 23) {
					hairCoordinate = 93;
				} else if (slotId == 21) {
					hairCoordinate = 92;
				} else if (slotId == 29) {
					hairCoordinate = 93;
				} else if (slotId == 25) {
					hairCoordinate = 94;
				} else if (slotId == 27) {
					hairCoordinate = 95;
				} else if (slotId == 29) {
					hairCoordinate = 96;
				} else if (slotId == 31) {
					hairCoordinate = 97;
				} else {
					hairCoordinate = 0;
				}
				player.getAppearence().setHairStyle(hairCoordinate);
			} else {
				int beardCoordinate = -1;
				if (slotId == 1) {
					beardCoordinate = 20;
				} else if (slotId == 3) {
					beardCoordinate = 13;
				} else if (slotId == 5) {
					beardCoordinate = 98;
				} else if (slotId == 7) {
					beardCoordinate = 18;
				} else if (slotId == 9) {
					beardCoordinate = 4;
				} else if (slotId == 11) {
					beardCoordinate = 5;
				} else if (slotId == 13) {
					beardCoordinate = 10;
				} else if (slotId == 15) {
					beardCoordinate = 15;
				} else if (slotId == 17) {
					beardCoordinate = 16;
				} else if (slotId == 19) {
					beardCoordinate = 100;
				} else if (slotId == 21) {
					beardCoordinate = 12;
				} else if (slotId == 23) {
					beardCoordinate = 11;
				} else if (slotId == 25) {
					beardCoordinate = 102;
				} else if (slotId == 27) {
					beardCoordinate = 17;
				} else if (slotId == 29) {
					beardCoordinate = 99;
				} else if (slotId == 31) {
					beardCoordinate = 101;
				} else if (slotId == 33) {
					beardCoordinate = 104;
				} else if (slotId == 35) {
					beardCoordinate = 17;
				} else if (slotId == 36) {
					beardCoordinate = 103;
				} else {
					beardCoordinate = 14;
				}
				player.getAppearence().setBeardStyle(beardCoordinate);
			}
		} else if (buttonId == 16) {// 350
			int hairColourCoordinate = 0;
			if (slotId == 1) {
				hairColourCoordinate = 20;
			} else if (slotId == 3) {
				hairColourCoordinate = 19;
			} else if (slotId == 5) {
				hairColourCoordinate = 10;
			} else if (slotId == 7) {
				hairColourCoordinate = 18;
			} else if (slotId == 9) {
				hairColourCoordinate = 4;
			} else if (slotId == 11) {
				hairColourCoordinate = 5;
			} else if (slotId == 13) {
				hairColourCoordinate = 15;
			} else if (slotId == 15) {
				hairColourCoordinate = 7;
			} else if (slotId == 17) {
				hairColourCoordinate = 26;
			} else if (slotId == 19) {
				hairColourCoordinate = 6;
			} else if (slotId == 21) {
				hairColourCoordinate = 21;
			} else if (slotId == 23) {
				hairColourCoordinate = 9;
			} else if (slotId == 25) {
				hairColourCoordinate = 22;
			} else if (slotId == 27) {
				hairColourCoordinate = 17;
			} else if (slotId == 29) {
				hairColourCoordinate = 8;
			} else if (slotId == 31) {
				hairColourCoordinate = 16;
			} else if (slotId == 33) {
				hairColourCoordinate = 11;
			} else if (slotId == 35) {
				hairColourCoordinate = 24;
			} else if (slotId == 37) {
				hairColourCoordinate = 23;
			} else if (slotId == 39) {
				hairColourCoordinate = 3;
			} else if (slotId == 41) {
				hairColourCoordinate = 2;
			} else if (slotId == 43) {
				hairColourCoordinate = 1;
			} else if (slotId == 45) {
				hairColourCoordinate = 14;
			} else if (slotId == 47) {
				hairColourCoordinate = 13;
			} else if (slotId == 49) {
				hairColourCoordinate = 12;
			}
			player.getAppearence().setHairColor(hairColourCoordinate);

		}
	}

	public static void openBeardInterface(Player player) {
		player.getInterfaceManager().sendInterface(309);
		player.getPackets().sendIComponentText(309, 20, "Confirm");
		player.getPackets().sendUnlockIComponentOptionSlots(309, 10, 0, 63, 0); // hairs
		// and
		// mustaches
		player.getPackets().sendUnlockIComponentOptionSlots(309, 16, 1, 49, 0); // colors
		player.getTemporaryAttributtes().put("hairSaloon", true);
	}

	private PlayerLook() {

	}

}
