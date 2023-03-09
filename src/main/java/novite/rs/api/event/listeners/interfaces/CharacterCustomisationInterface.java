package novite.rs.api.event.listeners.interfaces;

import java.util.Random;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Appearence;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.impl.StartTutorial;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class CharacterCustomisationInterface extends EventListener {

	private static final Random RANDOM = new Random();

	public static enum CustomizeCategory {
		SKIN, HAIR, TORSO, LEGS, SHOES, FACIAL_HAIR;

		public static CustomizeCategory getCustomIndex(int index) {
			switch (index) {
			case 0:
				return CustomizeCategory.SKIN;
			case 1:
				return CustomizeCategory.HAIR;
			case 2:
				return CustomizeCategory.TORSO;
			case 3:
				return CustomizeCategory.LEGS;
			case 4:
				return CustomizeCategory.SHOES;
			case 5:
				return CustomizeCategory.FACIAL_HAIR;
			}
			return null;
		}
	}

	private CustomizeCategory customIndex = CustomizeCategory.SKIN;
	private int designIndex = -1;
	private int secondaryDesignIndex = -1;

	@Override
	public int[] getEventIds() {
		return new int[] { 1028 };
	}

	@Override
	public boolean handleButtonClick(final Player player, int interfaceId, int buttonId, int packetId, int slotId, int slotId2) {
		try {
			Appearence app = player.getAppearence();
			CharacterCustomisationInterface des = (CharacterCustomisationInterface) player.getAttributes().get("character_customisation");
			if (des == null) {
				player.getAttributes().put("character_customisation", new CharacterCustomisationInterface());
				handleButtonClick(player, interfaceId, buttonId, packetId, slotId, slotId);
				return true;
			}
			player.setCloseInterfacesEvent(new Runnable() {

				@Override
				public void run() {
					player.getAttributes().remove("character_customisation");
				}
			});
			switch (buttonId) {
			case 115:
				break;
			case 117:
				player.closeInterfaces();
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasResizableScreen() ? 746 : 548, 0);
				player.getAppearence().generateAppearenceData();
				if (player.getControllerManager().getController() instanceof StartTutorial) {
					((StartTutorial) player.getControllerManager().getController()).onFinish();
					player.getControllerManager().getController().forceClose();
				}
				break;
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
				int ordinal = buttonId - 95;
				des.customIndex = CustomizeCategory.getCustomIndex(ordinal);
				break;
			case 45:
				app.setColor(4, SKIN_COLOURS[slotId]);
				break;
			case 107:
				if (app.isMale()) {
					switch (des.customIndex) {
					case HAIR:
						app.setLook(0, MALE_HAIR_LOOKS[slotId]);
						break;
					case TORSO:
						int[] torso = MALE_TORSO_LOOKS[slotId];
						app.setLook(2, torso[0] == -1 ? 0 : torso[0]);
						int arms = torso[1];
						if (arms != -1)
							app.setLook(3, torso[1]);
						app.setLook(4, torso[2] == -1 ? 0 : torso[2]);
						break;
					case LEGS:
						app.setLook(5, MALE_LEG_LOOKS[slotId]);
						break;
					case SHOES:
						app.setLook(6, MALE_FEET_LOOKS[slotId]);
						break;
					case FACIAL_HAIR:
						app.setLook(1, MALE_FACIAL_LOOKS[slotId]);
						break;
					case SKIN:
						break;
					default:
						break;
					}
				} else {
					switch (des.customIndex) {
					case HAIR:
						app.setLook(0, FEMALE_HAIR_LOOKS[slotId]);
						break;
					case TORSO:
						int[] torso = FEMALE_TORSO_LOOKS[slotId];
						app.setLook(2, torso[0] == -1 ? 0 : torso[0]);
						int arms = torso[1];
						if (arms != -1)
							app.setLook(3, torso[1]);
						app.setLook(4, torso[2] == -1 ? 0 : torso[2]);
						break;
					case LEGS:
						app.setLook(5, FEMALE_LEG_LOOKS[slotId]);
						break;
					case SHOES:
						app.setLook(6, FEMALE_FEET_LOOKS[slotId]);
						break;
					case FACIAL_HAIR:
						break;
					case SKIN:
						break;
					default:
						break;
					}
				}
				break;
			case 111: // modify colours
				switch (des.customIndex) {
				case SKIN:
					app.setColor(4, SKIN_COLOURS[slotId]);
					break;
				case HAIR:
					app.setColor(0, HAIR_COLOURS[slotId]);
					break;
				case TORSO:
					app.setColor(1, CLOTH_COLOURS[slotId]);
					break;
				case LEGS:
					app.setColor(2, CLOTH_COLOURS[slotId]);
					break;
				case SHOES:
					app.setColor(3, FEET_COLOURS[slotId]);
					break;
				case FACIAL_HAIR:
					if (app.isMale()) {
						app.setColor(0, HAIR_COLOURS[slotId]);
					}
					break;
				}
				break;
			case 38:
				app.setMale(true);
				app.setLook(0, 5);
				app.setLook(1, 14);
				setDefaultLook(app, des.designIndex, des.secondaryDesignIndex);
				player.getAppearence().generateAppearenceData();
				break;
			case 39:
				app.female();
				app.setMale(false);
				app.setLook(0, 48);
				app.setLook(1, 1000);
				app.setLook(2, 57);
				app.setLook(3, 64);
				app.setLook(4, 68);
				app.setLook(5, 77);
				app.setLook(6, 80);
				setDefaultLook(app, des.designIndex, des.secondaryDesignIndex);
				player.getAppearence().generateAppearenceData();
				break;
			case 48:
			case 49:
			case 50:
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
				setDefaultLook(app, buttonId - 48, 0);
				des.designIndex = buttonId - 48;
				break;
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
				setDefaultLook(app, des.designIndex, buttonId - 83);
				des.secondaryDesignIndex = buttonId - 83;
				break;
			case 120:
				int index = CLOTH_COLOURS[RANDOM.nextInt(CLOTH_COLOURS.length)];
				app.setColor(1, index);
				player.getPackets().sendConfig(1016, index);
				index = CLOTH_COLOURS[RANDOM.nextInt(CLOTH_COLOURS.length)];
				app.setColor(2, index);
				player.getPackets().sendConfig(1016, index);
				break;
			}
		} catch (Exception e) {

		}
		return true;
	}

	public static void randomizeLook(Player player, Appearence app) {
		app.setColor(0, HAIR_COLOURS[RANDOM.nextInt(HAIR_COLOURS.length)]);
		app.setColor(1, CLOTH_COLOURS[RANDOM.nextInt(CLOTH_COLOURS.length)]);
		app.setColor(2, CLOTH_COLOURS[RANDOM.nextInt(CLOTH_COLOURS.length)]);
		app.setColor(3, FEET_COLOURS[RANDOM.nextInt(FEET_COLOURS.length)]);
		app.setColor(4, SKIN_COLOURS[RANDOM.nextInt(SKIN_COLOURS.length)]);

		int hairIndex = RANDOM.nextInt(MALE_HAIR_LOOKS.length);
		app.setLook(0, MALE_HAIR_LOOKS[hairIndex]);
		int facialIndex = RANDOM.nextInt(MALE_FACIAL_LOOKS.length);
		app.setLook(1, MALE_FACIAL_LOOKS[facialIndex]);
		int torsoIndex = RANDOM.nextInt(MALE_TORSO_LOOKS.length);
		int[] torso = MALE_TORSO_LOOKS[torsoIndex];
		app.setLook(2, torso[0] == -1 ? 0 : torso[0]);
		int arms = torso[1];
		if (arms != -1)
			app.setLook(3, torso[1]);
		app.setLook(4, torso[2] == -1 ? 0 : torso[2]);
		int legIndex = RANDOM.nextInt(MALE_LEG_LOOKS.length);
		app.setLook(5, MALE_LEG_LOOKS[legIndex]);
		int feetIndex = RANDOM.nextInt(MALE_FEET_LOOKS.length);
		app.setLook(6, MALE_FEET_LOOKS[feetIndex]);
	}

	public static void setDefaultLook(Appearence app, int primaryIndex, int secondaryIndex) {
		if (primaryIndex == -1) {
			primaryIndex = 0;
		}
		if (secondaryIndex == -1) {
			secondaryIndex = 0;
		}
		if (app.isMale()) {
			DefaultDesign dd = MALE_DEFAULT_DESIGNS[primaryIndex];
			DefaultSubDesign dsd = dd.subDesigns[secondaryIndex];
			for (int[] i : dsd.looks) {
				int j = i[1];
				if (j != -1)
					app.setLook(i[0], j);

			}
			for (int[] i : dd.colours) {
				app.setColor(i[0], i[1]);
			}
		} else {
			DefaultDesign dd = FEMALE_DEFAULT_DESIGNS[primaryIndex];
			DefaultSubDesign dsd = dd.subDesigns[secondaryIndex];
			for (int[] i : dsd.looks) {
				int j = i[1];
				if (j != -1)
					app.setLook(i[0], j);

			}
			for (int[] i : dd.colours) {
				app.setColor(i[0], i[1]);
			}
		}

	}

	public static final int[] SKIN_COLOURS = new int[] { 9, 8, 7, 0, 1, 2, 3, 4, 5, 6, 10, 11 };

	public static final int[] HAIR_COLOURS = new int[] { 20, 19, 10, 18, 4, 5, 15, 7, 0, 6, 21, 9, 22, 17, 8, 16, 24, 11, 23, 3, 2, 1, 14, 13, 12 };

	public static final int[] CLOTH_COLOURS = new int[] { 32, 101, 48, 56, 165, 103, 167, 106, 54, 198, 199, 200, 225, 35, 39, 53, 42, 46, 29, 91, 57, 90, 34, 102, 104, 105, 107, 173, 137, 201, 204, 211, 197, 108, 217, 220, 221, 226, 227, 215, 222, 166, 212, 174, 175, 169, 144, 135, 136, 133, 123, 119, 192, 194, 117, 115, 111, 141, 45, 49, 84, 77, 118, 88, 85, 138, 51, 92, 112, 145, 179, 143, 149, 151, 153, 44, 154, 155, 86, 89, 72, 66, 33, 206, 109, 110, 114, 116, 184, 170, 120, 113, 150, 205, 210, 207, 209, 193, 152, 156, 183, 161, 159, 160, 73, 75, 181, 185, 208, 74, 36, 37, 43, 50, 58, 55, 139, 148, 147, 64, 69, 70, 71, 68, 93, 94, 95, 124, 182, 96, 97, 219, 63, 228, 79, 82, 98, 99, 100, 125, 126, 127, 40, 128, 129, 188, 130, 131, 186, 132, 164, 157, 180, 187, 31, 162, 168, 52, 163, 158, 196, 59, 60, 87, 78, 61, 76, 80, 171, 172, 176, 177, 178, 38, 41, 47, 62, 65, 67, 81, 83, 121, 122, 134, 140, 142, 146, 189, 190, 191, 195, 202, 203, 213, 214, 216, 218, 223, 224 };

	public static final int[] FEET_COLOURS = new int[] { 55, 54, 14, 120, 194, 53, 11, 154, 0, 1, 2, 3, 4, 5, 9, 78, 25, 33, 142, 80, 144, 83, 31, 175, 176, 177, 12, 16, 30, 19, 23, 6, 68, 34, 67, 11, 79, 81, 82, 84, 150, 114, 178, 181, 188, 174, 85, 194, 197, 198, 203, 204, 192, 199, 143, 189, 151, 152, 146, 121, 112, 113, 110, 100, 96, 169, 171, 94, 92, 88, 118, 22, 26, 61, 95, 65, 62, 115, 28, 69, 89, 122, 156, 126, 128, 130, 21, 131, 132, 63, 66, 49, 43, 10, 183, 86, 87, 91, 93, 161, 147, 97, 90, 127, 182, 187, 184, 186, 170, 129, 133, 160, 138, 136, 137, 50, 52, 158, 162, 185, 51, 13, 20, 27, 35, 32, 116, 125, 124, 41, 46, 47, 48, 45, 70, 71, 72, 101, 159, 73, 74, 196, 40, 205, 56, 59, 75, 76, 77, 102, 103, 104, 17, 105, 106, 165, 107, 108, 163, 109, 141, 134, 157, 164, 8, 139, 145, 29, 140, 135, 173, 36, 37, 64, 38, 57, 148, 149, 153, 155, 15, 18, 24, 39, 42, 44, 58, 60, 98, 99, 117, 119, 123, 166, 167, 168, 172, 179, 180, 190, 191, 193, 195, 200, 201 };

	public static final int[] MALE_HAIR_LOOKS = new int[] { 5, 6, 93, 96, 92, 268, 265, 264, 267, 315, 94, 263, 312, 313, 311, 314, 261, 310, 1, 0, 97, 95, 262, 316, 309, 3, 91, 4 };

	public static final int[][] MALE_TORSO_LOOKS = new int[][] { { 457, 588, 364 }, { 445, -1, 366 }, { 459, 591, 367 }, { 460, 592, 368 }, { 461, 593, 369 }, { 462, 594, 370 }, { 452, -1, 371 }, { 463, 596, 372 }, { 464, 597, 373 }, { 446, -1, 374 }, { 465, 599, 375 }, { 466, 600, 376 }, { 467, 601, 377 }, { 451, -1, 378 }, { 468, 603, 379 }, { 453, -1, 380 }, { 454, -1, 381 }, { 455, -1, 382 }, { 469, 607, 383 }, { 470, 608, 384 }, { 450, -1, 385 }, { 458, 589, 365 }, { 447, -1, 386 }, { 448, -1, 387 }, { 449, -1, 388 }, { 471, 613, 389 }, { 443, -1, 390 }, { 472, 615, 391 }, { 473, 616, 392 }, { 444, -1, 393 }, { 474, 618, 394 }, { 456, -1, 9 } };

	public static final int[] MALE_LEG_LOOKS = new int[] { 620, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 621, 642, 643, 644, 645, 646, 647, 648, 649, 650, 651 };

	public static final int[] MALE_FEET_LOOKS = new int[] { 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 42, 43 };

	public static final int[] MALE_FACIAL_LOOKS = new int[] { 14, 13, 98, 308, 305, 307, 10, 15, 16, 100, 12, 11, 102, 306, 99, 101, 104, 17 };

	public static final int[] FEMALE_HAIR_LOOKS = new int[] { 141, 361, 272, 273, 359, 274, 353, 277, 280, 360, 356, 269, 358, 270, 275, 357, 145, 271, 354, 355, 45, 52, 49, 47, 48, 46, 143, 362, 144, 279, 142, 146, 278, 135 };

	public static final int[][] FEMALE_TORSO_LOOKS = new int[][] { { 565, 395, 507 }, { 567, 397, 509 }, { 568, 398, 510 }, { 569, 399, 511 }, { 570, 400, 512 }, { 571, 401, 513 }, { 561, -1, 514 }, { 572, 403, 515 }, { 573, 404, 516 }, { 574, 405, 517 }, { 575, 406, 518 }, { 576, 407, 519 }, { 577, 408, 520 }, { 560, -1, 521 }, { 578, 410, 522 }, { 562, -1, 523 }, { 563, -1, 524 }, { 564, -1, 525 }, { 579, 414, 526 }, { 559, -1, 527 }, { 580, 416, 528 }, { 566, 396, 508 }, { 581, 417, 529 }, { 582, 418, 530 }, { 557, -1, 531 }, { 583, 420, 532 }, { 584, 421, 533 }, { 585, 422, 534 }, { 586, 423, 535 }, { 556, -1, 536 }, { 587, 425, 537 }, { 558, -1, 538 } };

	public static final int[] FEMALE_LEG_LOOKS = new int[] { 475, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 476, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506 };

	public static final int[] FEMALE_FEET_LOOKS = new int[] { 539, 540, 541, 542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 554, 555, 79, 80 };

	public static final DefaultDesign[] MALE_DEFAULT_DESIGNS = new DefaultDesign[] { new DefaultDesign(new DefaultSubDesign[] { // ADVENTURER
			new DefaultSubDesign(new int[][] { // ADVENTURER #1
					{ 2, 471 }, { 3, 613 }, { 4, 389 }, { 5, 645 }, { 6, 439 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																														// #2
							{ 2, 443 }, { 3, -1 }, { 4, 390 }, { 5, 646 }, { 6, 440 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #3
							{ 2, 472 }, { 3, 615 }, { 4, 391 }, { 5, 647 }, { 6, 441 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #4
							{ 2, 473 }, { 3, 616 }, { 4, 392 }, { 5, 648 }, { 6, 441 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #5
							{ 2, 444 }, { 3, -1 }, { 4, 393 }, { 5, 649 }, { 6, 441 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #6
							{ 2, 474 }, { 3, 618 }, { 4, 394 }, { 5, 650 }, { 6, 441 } }) }, new int[][] { { 1, 37 }, { 2, 213 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // WARRIOR
			new DefaultSubDesign(new int[][] { // WARRIOR #1
					{ 2, 453 }, { 3, -1 }, { 4, 380 }, { 5, 636 }, { 6, 429 } }), new DefaultSubDesign(new int[][] { // WARRIOR
																														// #2

							{ 2, 454 }, { 3, -1 }, { 4, 381 }, { 5, 637 }, { 6, 429 } }), new DefaultSubDesign(new int[][] { // WARROR
																																// #3

							{ 2, 455 }, { 3, -1 }, { 4, 382 }, { 5, 638 }, { 6, 429 } }) }, new int[][] { { 1, 197 }, { 2, 202 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // MAGE
			new DefaultSubDesign(new int[][] { // MAGE #1
					{ 2, 447 }, { 3, -1 }, { 4, 386 }, { 5, 642 }, { 6, 429 } }), new DefaultSubDesign(new int[][] { // MAGE
																														// #2
							{ 2, 448 }, { 3, -1 }, { 4, 387 }, { 5, 643 }, { 6, 429 } }), new DefaultSubDesign(new int[][] { // MAGE
																																// #3
							{ 2, 449 }, { 3, -1 }, { 4, 388 }, { 5, 644 }, { 6, 429 } }) }, new int[][] { { 1, 125 }, { 2, 125 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // RANGER
			new DefaultSubDesign(new int[][] { // RANGER #1
					{ 2, 469 }, { 3, 607 }, { 4, 383 }, { 5, 639 }, { 6, 432 } }), new DefaultSubDesign(new int[][] { // RANGER
																														// #2
							{ 2, 470 }, { 3, 608 }, { 4, 384 }, { 5, 640 }, { 6, 429 } }), new DefaultSubDesign(new int[][] { // RANGER
																																// #3

							{ 2, 450 }, { 3, -1 }, { 4, 385 }, { 5, 641 }, { 6, 429 } }) }, new int[][] { { 1, 149 }, { 2, 150 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // SLAYER
			new DefaultSubDesign(new int[][] { // SLAYER #1

					{ 2, 451 }, { 3, -1 }, { 4, 378 }, { 5, 634 }, { 6, 429 } }) }, new int[][] { { 1, 165 }, { 2, 165 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // THIEF
			new DefaultSubDesign(new int[][] { // THIEF #1
					{ 2, 452 }, { 3, -1 }, { 4, 371 }, { 5, 627 }, { 6, 434 } }) }, new int[][] { { 1, 189 }, { 2, 189 }, { 3, 39 } }), new DefaultDesign(new DefaultSubDesign[] { // ATHLETE
			new DefaultSubDesign(new int[][] { // ATHLETE #1
					{ 2, 464 }, { 3, 597 }, { 4, 373 }, { 5, 629 }, { 6, 435 } }) }, new int[][] { { 1, 29 }, { 2, 30 }, { 3, 0 } }), new DefaultDesign(new DefaultSubDesign[] { // HUNTER
			new DefaultSubDesign(new int[][] { // HUNTER #1
					{ 2, 468 }, { 3, 603 }, { 4, 379 }, { 5, 635 }, { 6, 429 } }) }, new int[][] { { 1, 117 }, { 2, 118 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // CHEF
			new DefaultSubDesign(new int[][] { // CHEF #1
					{ 2, 458 }, { 3, 589 }, { 4, 365 }, { 5, 621 }, { 6, 428 } }) }, new int[][] { { 1, 69 }, { 2, 69 }, { 3, 154 } }), new DefaultDesign(new DefaultSubDesign[] { // WOODCUTTER
			new DefaultSubDesign(new int[][] { // WOODCUTTER #1
					{ 2, 465 }, { 3, 599 }, { 4, 375 }, { 5, 631 }, { 6, 436 } }) }, new int[][] { { 1, 205 }, { 2, 206 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FISHER
			new DefaultSubDesign(new int[][] { // FISHER #1
					{ 2, 460 }, { 3, 592 }, { 4, 368 }, { 5, 624 }, { 6, 431 } }) }, new int[][] { { 1, 93 }, { 2, 94 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // PRIEST
			new DefaultSubDesign(new int[][] { // PRIEST #1
					{ 2, 456 }, { 3, -1 }, { 4, 9 }, { 5, 651 }, { 6, 442 } }) }, new int[][] { { 1, 141 }, { 2, 141 }, { 3, 118 } }), new DefaultDesign(new DefaultSubDesign[] { // MINER
			new DefaultSubDesign(new int[][] { // MINER #1
					{ 2, 459 }, { 3, 591 }, { 4, 367 }, { 5, 623 }, { 6, 430 } }) }, new int[][] { { 1, 133 }, { 2, 134 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FARMER
			new DefaultSubDesign(new int[][] { // FARMER #1
					{ 2, 462 }, { 3, 594 }, { 4, 370 }, { 5, 626 }, { 6, 433 } }) }, new int[][] { { 1, 85 }, { 2, 85 }, { 3, 69 } }), new DefaultDesign(new DefaultSubDesign[] { // SMITH
			new DefaultSubDesign(new int[][] { // SMITH #1
					{ 2, 467 }, { 3, 601 }, { 4, 377 }, { 5, 633 }, { 6, 438 } }), new DefaultSubDesign(new int[][] { // SMITH
																														// #2

							{ 2, 457 }, { 3, 588 }, { 4, 364 }, { 5, 620 }, { 6, 427 } }) }, new int[][] { { 1, 173 }, { 2, 174 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // CRAFTER
			new DefaultSubDesign(new int[][] { // CRAFTER #1
					{ 2, 461 }, { 3, 593 }, { 4, 369 }, { 5, 625 }, { 6, 432 } }) }, new int[][] { { 1, 77 }, { 2, 78 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FLETCHER
			new DefaultSubDesign(new int[][] { // FLETCHER #1
					{ 2, 445 }, { 3, -1 }, { 4, 366 }, { 5, 622 }, { 6, 429 } }) }, new int[][] { { 1, 101 }, { 2, 102 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // HERBLORIST
			new DefaultSubDesign(new int[][] { // HERBLORIST #1
					{ 2, 446 }, { 3, -1 }, { 4, 374 }, { 5, 630 }, { 6, 429 } }) }, new int[][] { { 1, 109 }, { 2, 109 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // SUMMONER
			new DefaultSubDesign(new int[][] { // SUMMONER #1
					{ 2, 466 }, { 3, 600 }, { 4, 376 }, { 5, 632 }, { 6, 437 } }) }, new int[][] { { 1, 181 }, { 2, 181 }, { 3, 56 } }), new DefaultDesign(new DefaultSubDesign[] { // RUNECRAFTER
			new DefaultSubDesign(new int[][] { // RUNECRAFTER #1
					{ 2, 463 }, { 3, 596 }, { 4, 372 }, { 5, 628 }, { 6, 429 } }) }, new int[][] { { 1, 157 }, { 2, 157 }, { 3, 134 } }) };

	public static final DefaultDesign[] FEMALE_DEFAULT_DESIGNS = new DefaultDesign[] { new DefaultDesign(new DefaultSubDesign[] { // ADVENTURER
			new DefaultSubDesign(new int[][] { // ADVENTURER #1
					{ 2, 583 }, { 3, 420 }, { 4, 532 }, { 5, 500 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																														// #2
							{ 2, 584 }, { 3, 421 }, { 4, 533 }, { 5, 501 }, { 6, 553 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #3
							{ 2, 585 }, { 3, 422 }, { 4, 534 }, { 5, 502 }, { 6, 554 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #4
							{ 2, 586 }, { 3, 423 }, { 4, 535 }, { 5, 503 }, { 6, 553 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #5
							{ 2, 556 }, { 3, -1 }, { 4, 536 }, { 5, 504 }, { 6, 554 } }), new DefaultSubDesign(new int[][] { // ADVENTURER
																																// #6
							{ 2, 587 }, { 3, 425 }, { 4, 537 }, { 5, 505 }, { 6, 551 } }) }, new int[][] { { 1, 45 }, { 2, 221 }, { 3, 15 } }), new DefaultDesign(new DefaultSubDesign[] { // WARRIOR
			new DefaultSubDesign(new int[][] { // WARRIOR #1
					{ 2, 562 }, { 3, -1 }, { 4, 523 }, { 5, 491 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // WARRIOR
																														// #2
							{ 2, 563 }, { 3, -1 }, { 4, 524 }, { 5, 492 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // WARRIOR
																																// #3

							{ 2, 564 }, { 3, -1 }, { 4, 525 }, { 5, 493 }, { 6, 551 } }) }, new int[][] { { 1, 197 }, { 2, 202 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // MAGE
			new DefaultSubDesign(new int[][] { // MAGE #1
					{ 2, 581 }, { 3, 417 }, { 4, 529 }, { 5, 497 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // MAGE
																														// #2
							{ 2, 582 }, { 3, 418 }, { 4, 530 }, { 5, 498 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // MAGE
																																// #3
							{ 2, 557 }, { 3, -1 }, { 4, 531 }, { 5, 499 }, { 6, 551 } }) }, new int[][] { { 1, 125 }, { 2, 125 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // RANGER
			new DefaultSubDesign(new int[][] { // RANGER #1
					{ 2, 579 }, { 3, 414 }, { 4, 526 }, { 5, 494 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // RANGER
																														// #2
							{ 2, 559 }, { 3, -1 }, { 4, 527 }, { 5, 495 }, { 6, 551 } }), new DefaultSubDesign(new int[][] { // RANGER
																																// #3
							{ 2, 580 }, { 3, 416 }, { 4, 528 }, { 5, 496 }, { 6, 552 } }) }, new int[][] { { 1, 149 }, { 2, 150 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // SLAYER
			new DefaultSubDesign(new int[][] { // SLAYER #1
					{ 2, 560 }, { 3, -1 }, { 4, 521 }, { 5, 489 }, { 6, 551 } }) }, new int[][] { { 1, 165 }, { 2, 166 }, { 3, 142 } }), new DefaultDesign(new DefaultSubDesign[] { // THIEF
			new DefaultSubDesign(new int[][] { // THIEF #1
					{ 2, 561 }, { 3, -1 }, { 4, 514 }, { 5, 482 }, { 6, 545 } }) }, new int[][] { { 1, 189 }, { 2, 189 }, { 3, 39 } }), new DefaultDesign(new DefaultSubDesign[] { // ATHLETE
			new DefaultSubDesign(new int[][] { // ATHLETE #1

					{ 2, 573 }, { 3, 404 }, { 4, 516 }, { 5, 484 }, { 6, 547 } }) }, new int[][] { { 1, 29 }, { 2, 30 }, { 3, 0 } }), new DefaultDesign(new DefaultSubDesign[] { // HUNTER
			new DefaultSubDesign(new int[][] { // HUNTER #1
					{ 2, 578 }, { 3, 410 }, { 4, 522 }, { 5, 490 }, { 6, 550 } }) }, new int[][] { { 1, 117 }, { 2, 118 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // CHEF
			new DefaultSubDesign(new int[][] { // CHEF #1
					{ 2, 566 }, { 3, 396 }, { 4, 508 }, { 5, 476 }, { 6, 540 } }) }, new int[][] { { 1, 69 }, { 2, 69 }, { 3, 154 } }), new DefaultDesign(new DefaultSubDesign[] { // WOODCUTTER

			new DefaultSubDesign(new int[][] { // WOODCUTTER #1

					{ 2, 575 }, { 3, 406 }, { 4, 518 }, { 5, 486 }, { 6, 551 } }) }, new int[][] { { 1, 205 }, { 2, 206 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FISHER
			new DefaultSubDesign(new int[][] { // FISHER #1
					{ 2, 569 }, { 3, 399 }, { 4, 511 }, { 5, 479 }, { 6, 551 } }) }, new int[][] { { 1, 93 }, { 2, 94 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // PRIESTESS
			new DefaultSubDesign(new int[][] { // PRIESTESS #1
					{ 2, 558 }, { 3, -1 }, { 4, 538 }, { 5, 506 }, { 6, 555 } }) }, new int[][] { { 1, 141 }, { 2, 141 }, { 3, 118 } }), new DefaultDesign(new DefaultSubDesign[] { // MINER
			new DefaultSubDesign(new int[][] { // MINER #1
					{ 2, 568 }, { 3, 398 }, { 4, 510 }, { 5, 478 }, { 6, 542 } }) }, new int[][] { { 1, 133 }, { 2, 134 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FARMER
			new DefaultSubDesign(new int[][] { // FARMER #1
					{ 2, 571 }, { 3, 401 }, { 4, 513 }, { 5, 481 }, { 6, 544 } }) }, new int[][] { { 1, 85 }, { 2, 86 }, { 3, 69 } }), new DefaultDesign(new DefaultSubDesign[] { // SMITH
			new DefaultSubDesign(new int[][] { // SMITH #1
					{ 2, 577 }, { 3, 408 }, { 4, 520 }, { 5, 488 }, { 6, 549 } }), new DefaultSubDesign(new int[][] { // SMITH
																														// #2
							{ 2, 565 }, { 3, 395 }, { 4, 507 }, { 5, 475 }, { 6, 539 } }) }, new int[][] { { 1, 61 }, { 2, 61 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // CRAFTER
			new DefaultSubDesign(new int[][] { // CRAFTER #1
					{ 2, 570 }, { 3, 400 }, { 4, 512 }, { 5, 480 }, { 6, 543 } }) }, new int[][] { { 1, 77 }, { 2, 78 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // FLETCHER
			new DefaultSubDesign(new int[][] { // FLETCHER #1
					{ 2, 567 }, { 3, 397 }, { 4, 509 }, { 5, 477 }, { 6, 541 } }) }, new int[][] { { 1, 101 }, { 2, 102 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // HERBLORIST
			new DefaultSubDesign(new int[][] { // HERBLORIST #1
					{ 2, 574 }, { 3, 405 }, { 4, 517 }, { 5, 485 }, { 6, 548 } }) }, new int[][] { { 1, 109 }, { 2, 109 }, { 3, 4 } }), new DefaultDesign(new DefaultSubDesign[] { // SUMMONER
			new DefaultSubDesign(new int[][] { // SUMMONER #1
					{ 2, 576 }, { 3, 407 }, { 4, 519 }, { 5, 487 }, { 6, 553 } }) }, new int[][] { { 1, 181 }, { 2, 181 }, { 3, 158 } }), new DefaultDesign(new DefaultSubDesign[] { // RUNECRAFTER
			new DefaultSubDesign(new int[][] { // RUNECRAFTER #1
					{ 2, 572 }, { 3, 403 }, { 4, 515 }, { 5, 483 }, { 6, 546 } }) }, new int[][] { { 1, 157 }, { 2, 157 }, { 3, 54 } }) };

	public static class DefaultDesign {

		protected DefaultSubDesign[] subDesigns;
		protected int[][] colours;

		public DefaultDesign(DefaultSubDesign[] subDesigns, int[][] colours) {
			this.subDesigns = subDesigns;
			this.colours = colours;
		}

	}

	public static class DefaultSubDesign {

		protected int[][] looks;

		public DefaultSubDesign(int[][] looks) {
			this.looks = looks;
		}

	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}
}
