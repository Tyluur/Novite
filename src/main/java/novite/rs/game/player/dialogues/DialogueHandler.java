package novite.rs.game.player.dialogues;

import java.util.HashMap;

import novite.rs.utility.tools.FileClassLoader;

public final class DialogueHandler {

	private static final HashMap<Object, Class<Dialogue>> handledDialogues = new HashMap<Object, Class<Dialogue>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Dialogue> value1 = (Class<Dialogue>) Class.forName(LevelUp.class.getCanonicalName());
			handledDialogues.put("LevelUp", value1);
			Class<Dialogue> value3 = (Class<Dialogue>) Class.forName(ClimbNoEmoteStairs.class.getCanonicalName());
			handledDialogues.put("ClimbNoEmoteStairs", value3);
			Class<Dialogue> value5 = (Class<Dialogue>) Class.forName(DestroyItemOption.class.getCanonicalName());
			handledDialogues.put("DestroyItemOption", value5);
			Class<Dialogue> value6 = (Class<Dialogue>) Class.forName(FremennikShipmaster.class.getCanonicalName());
			handledDialogues.put("FremennikShipmaster", value6);
			Class<Dialogue> value8 = (Class<Dialogue>) Class.forName(NexEntrance.class.getCanonicalName());
			handledDialogues.put("NexEntrance", value8);
			Class<Dialogue> value9 = (Class<Dialogue>) Class.forName(MagicPortal.class.getCanonicalName());
			handledDialogues.put("MagicPortal", value9);
			Class<Dialogue> value12 = (Class<Dialogue>) Class.forName(FletchingD.class.getCanonicalName());
			handledDialogues.put("FletchingD", value12);
			Class<Dialogue> value16 = (Class<Dialogue>) Class.forName(SimpleMessage.class.getCanonicalName());
			handledDialogues.put("SimpleMessage", value16);
			Class<Dialogue> value17 = (Class<Dialogue>) Class.forName(ItemMessage.class.getCanonicalName());
			handledDialogues.put("ItemMessage", value17);
			Class<Dialogue> value18 = (Class<Dialogue>) Class.forName(ClimbEmoteStairs.class.getCanonicalName());
			handledDialogues.put("ClimbEmoteStairs", value18);
			Class<Dialogue> value20 = (Class<Dialogue>) Class.forName(GemCuttingD.class.getCanonicalName());
			handledDialogues.put("GemCuttingD", value20);
			Class<Dialogue> value21 = (Class<Dialogue>) Class.forName(CookingD.class.getCanonicalName());
			handledDialogues.put("CookingD", value21);
			Class<Dialogue> value22 = (Class<Dialogue>) Class.forName(HerbloreD.class.getCanonicalName());
			handledDialogues.put("HerbloreD", value22);
			Class<Dialogue> value23 = (Class<Dialogue>) Class.forName(BarrowsD.class.getCanonicalName());
			handledDialogues.put("BarrowsD", value23);
			Class<Dialogue> value24 = (Class<Dialogue>) Class.forName(SmeltingD.class.getCanonicalName());
			handledDialogues.put("SmeltingD", value24);
			Class<Dialogue> value25 = (Class<Dialogue>) Class.forName(LeatherCraftingD.class.getCanonicalName());
			handledDialogues.put("LeatherCraftingD", value25);
			Class<Dialogue> value26 = (Class<Dialogue>) Class.forName(EnchantedGemDialouge.class.getCanonicalName());
			handledDialogues.put("EnchantedGemDialouge", value26);
			Class<Dialogue> value27 = (Class<Dialogue>) Class.forName(ForfeitDialouge.class.getCanonicalName());
			handledDialogues.put("ForfeitDialouge", value27);
			Class<Dialogue> value28 = (Class<Dialogue>) Class.forName(Transportation.class.getCanonicalName());
			handledDialogues.put("Transportation", value28);
			Class<Dialogue> value30 = (Class<Dialogue>) Class.forName(SimpleNPCMessage.class.getCanonicalName());
			handledDialogues.put("SimpleNPCMessage", value30);
			Class<Dialogue> value31 = (Class<Dialogue>) Class.forName(Transportation.class.getCanonicalName());
			handledDialogues.put("Transportation", value31);
			Class<Dialogue> value32 = (Class<Dialogue>) Class.forName(DTSpectateReq.class.getCanonicalName());
			handledDialogues.put("DTSpectateReq", value32);
			Class<Dialogue> value33 = (Class<Dialogue>) Class.forName(StrangeFace.class.getCanonicalName());
			handledDialogues.put("StrangeFace", value33);
			Class<Dialogue> value34 = (Class<Dialogue>) Class.forName(AncientEffigiesD.class.getCanonicalName());
			handledDialogues.put("AncientEffigiesD", value34);
			Class<Dialogue> value35 = (Class<Dialogue>) Class.forName(DTClaimRewards.class.getCanonicalName());
			handledDialogues.put("DTClaimRewards", value35);
			Class<Dialogue> value37 = (Class<Dialogue>) Class.forName(DismissD.class.getCanonicalName());
			handledDialogues.put("DismissD", value37);
			Class<Dialogue> value39 = (Class<Dialogue>) Class.forName(MakeOverMage.class.getCanonicalName());
			handledDialogues.put("MakeOverMage", value39);
			for (Object packet : FileClassLoader.getClassesInDirectory(DialogueHandler.class.getPackage().getName() + ".impl")) {
				Dialogue dialogue = (Dialogue) packet;
				if (!handledDialogues.containsKey(dialogue.getClass().getName())) {
					handledDialogues.put(dialogue.getClass().getSimpleName(), (Class<Dialogue>) dialogue.getClass());
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static final void reload() {
		handledDialogues.clear();
		init();
	}

	public static final Dialogue getDialogue(Object key) {
		if (key instanceof Dialogue) {
			return (Dialogue) key;
		}
		Class<Dialogue> classD = handledDialogues.get(key);
		if (classD == null) {
			return null;
		}
		try {
			return classD.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DialogueHandler() {

	}
}
