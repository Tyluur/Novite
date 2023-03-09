package novite.rs.game.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.api.database.DatabaseConnection;
import novite.rs.api.database.mysql.impl.Highscores;
import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.minigames.clanwars.ClanWars;
import novite.rs.game.minigames.clanwars.FfaZone;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.npc.godwars.zaros.Nex;
import novite.rs.game.npc.others.GraveStone;
import novite.rs.game.npc.others.pet.Pet;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.game.player.content.FriendChatsManager;
import novite.rs.game.player.content.Notes;
import novite.rs.game.player.content.Pots;
import novite.rs.game.player.content.SkillCapeCustomizer;
import novite.rs.game.player.content.achievements.AchievementManager;
import novite.rs.game.player.content.cannon.DwarfCannon;
import novite.rs.game.player.content.loyalty.LoyaltyManager;
import novite.rs.game.player.content.pet.PetManager;
import novite.rs.game.player.content.randoms.RandomEvent;
import novite.rs.game.player.content.scrolls.ClueScrollManager;
import novite.rs.game.player.content.slayer.SlayerManager;
import novite.rs.game.player.content.slayer.SlayerTask;
import novite.rs.game.player.content.trading.Trade;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.game.player.controlers.impl.dice.DiceSession;
import novite.rs.game.player.controlers.impl.guilds.warriors.WarriorsGuild;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.Session;
import novite.rs.networking.protocol.game.DefaultGameEncoder;
import novite.rs.utility.IsaacKeyPair;
import novite.rs.utility.Saving;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.ChatMessage;
import novite.rs.utility.game.ForumGroup;
import novite.rs.utility.game.ForumGroup.ForumGroups;
import novite.rs.utility.game.ForumGroup.GroupType;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ExchangeItemLoader;

import com.runetoplist.RuneTopList;

public class Player extends Entity {
	private static final long serialVersionUID = 2011932556974180375L;

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	private transient boolean finishing;

	public int slayerPoints;
	// transient stuff
	private transient Trade trade;
	private transient ClanWars clanWars;
	private transient String username;
	private transient Session session;
	private transient boolean clientLoadedMapRegion;
	private transient int displayMode;
	private transient int screenWidth;
	private transient boolean toogleLootShare;
	private transient boolean usingTicket;
	private transient int trapAmount;
	private transient Pet pet;
	private transient int screenHeight;
	private transient InterfaceManager interfaceManager;
	private transient DialogueManager dialogueManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient CutscenesManager cutscenesManager;
	private transient DuelRules lastDuelRules;
	private transient DiceSession diceSession;
	private transient PriceCheckManager priceCheckManager;
	private transient ClansManager clanManager;
	private transient RouteEvent routeEvent;
	private transient ClansManager guestClanManager;
	private transient FriendChatsManager currentFriendChat;
	private transient VarsManager varsManager;
	private transient IsaacKeyPair isaacKeyPair;

	private final SlayerManager slayerManager;
	private final ClueScrollManager clueScrollManager;

	// used for update
	private transient LocalPlayerUpdate localPlayerUpdate;
	private transient LocalNPCUpdate localNPCUpdate;

	private int temporaryMovementType;
	private boolean updateMovementType;

	// player stages
	private transient boolean started;
	private transient boolean running;
	private transient boolean cantTrade;
	private transient long packetsDecoderPing;
	private transient boolean resting;
	private transient boolean canPvp;
	private transient long lockDelay; // used for doors and stuff like that
	private transient long foodDelay;
	private transient long potDelay;
	private transient long boneDelay;
	private transient Runnable closeInterfacesEvent;
	private transient long lastPublicMessage;
	private transient long polDelay;
	private transient Runnable interfaceListenerEvent;// used for static
	private transient List<ForumGroup> forumGroups;
	private transient boolean disableEquip;
	private transient ItemsContainer<Item> duelSpoils;
	private transient RandomEvent currentRandomEvent;
	private transient int chatType;

	// interface

	// saving stuff
	private String password;
	private Rights rights = Rights.PLAYER;
	private List<Item> untradeableItems = new ArrayList<Item>();
	private String displayName;
	private String lastIP;
	private Appearence appearence;
	private LoyaltyManager loyaltyManager;
	private Inventory inventory;
	private Equipment equipment;
	private PetManager petManager;
	private AchievementManager achievementManager;
	private Skills skills;
	private QuestManager questManager;
	private DwarfCannon dwarfCannon;
	private CombatDefinitions combatDefinitions;
	private Prayer prayer;
	private Bank bank;
	private ControlerManager controlerManager;
	private MusicsManager musicsManager;
	private EmotesManager emotesManager;
	private FriendsIgnores friendsIgnores;
	private DominionTower dominionTower;
	private FarmingManager farmingManager;
	private Familiar familiar;
	private AuraManager auraManager;
	private Facade facade;
	private SlayerTask slayerTask;
	private byte runEnergy;
	private boolean allowChatEffects;
	private boolean mouseButtons;
	private int privateChatSetup;
	private int friendChatSetup = 12;
	private int skullDelay;
	private int skullId;
	private int prayerRenewalDelay;
	private boolean forceNextMapLoadRefresh;
	private long poisonImmune;
	private long fireImmune;
	private int[] pouches;
	private long displayTime;
	private long jailed;
	private boolean filterGame;
	private int secondsPlayed;
	private String clanName;
	private int clanChatSetup;
	private int guestChatSetup;
	private boolean connectedClanChannel;
	private boolean khalphiteLairSetted;

	private transient LinkedHashMap<String, Long> lastKills;
	private int graveStone;
	private int killCount, deathCount;

	// honor
	private ChargesManager charges;
	// barrows
	private boolean[] killedBarrowBrothers;
	private boolean completedFightCaves;
	private int hiddenBrother;
	private int barrowsKillCount;
	private int pestPoints;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	private int[] completionistCapeCustomized;

	private int overloadDelay;

	private String currentFriendChatOwner = "Novite";
	private int summoningLeftClickOption;
	private List<String> ownedObjectsManagerKeys;

	private int clanStatus;
	private boolean capturedCastleWarsFlag;
	private int finishedCastleWars;

	private int runeSpanPoints;
	private int stealingCreationPoints;
	private boolean completedStealingCreation;
	private int favorPoints;
	private double[] warriorPoints;
	private long actionTime;

	public void sendMessage(Object message) {
		getPackets().sendGameMessage("" + message);
	}

	public Player(String password) {
		super(Constants.HOME_TILE);
		setHitpoints(Constants.START_PLAYER_HITPOINTS);
		this.password = password;
		appearence = new Appearence();
		inventory = new Inventory();
		equipment = new Equipment();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		setLoyaltyManager(new LoyaltyManager(this));
		prayer = new Prayer();
		bank = new Bank();
		controlerManager = new ControlerManager();
		musicsManager = new MusicsManager();
		setPetManager(new PetManager());
		emotesManager = new EmotesManager();
		setQuestManager(new QuestManager(this));
		friendsIgnores = new FriendsIgnores();
		dominionTower = new DominionTower();
		farmingManager = new FarmingManager();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		pouches = new int[4];
		resetBarrows();
		killedBarrowBrothers = new boolean[6];
		ownedObjectsManagerKeys = new LinkedList<String>();
		setAchievementManager(new AchievementManager(this));
		setFacade(new Facade());
		slayerManager = new SlayerManager();
		clueScrollManager = new ClueScrollManager();
		SkillCapeCustomizer.resetSkillCapes(this);
	}

	public void init(Session session, String username, int displayMode, int screenWidth, int screenHeight, IsaacKeyPair isaacKeyPair) {
		// temporary deleted after reset all chars
		if (dominionTower == null) {
			dominionTower = new DominionTower();
		}
		if (warriorPoints == null)
			warriorPoints = new double[6];
		if (auraManager == null) {
			auraManager = new AuraManager();
		}
		if (farmingManager == null) {
			farmingManager = new FarmingManager();
		}
		this.session = session;
		this.username = username;
		this.displayMode = displayMode;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.isaacKeyPair = isaacKeyPair;
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		priceCheckManager = new PriceCheckManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		cutscenesManager = new CutscenesManager(this);
		varsManager = new VarsManager(this);
		appearence.setPlayer(this);
		inventory.setPlayer(this);
		equipment.setPlayer(this);
		skills.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		controlerManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		getPetManager().setPlayer(this);
		friendsIgnores.setPlayer(this);
		loyaltyManager.setPlayer(this);
		farmingManager.setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		charges.setPlayer(this);
		setDirection(Utils.getFaceDirection(0, -1));
		temporaryMovementType = -1;
		slayerManager.setPlayer(this);
		clueScrollManager.setPlayer(this);
		setTrade(new Trade(this));
		setLastKills(new LinkedHashMap<String, Long>());
		initEntity();
		packetsDecoderPing = Utils.currentTimeMillis();
		// inited so lets add it
		World.addPlayer(this);
		World.updateEntityRegion(this);
		System.out.println("Added Game Player: [username=" + username + ", size=" + World.getPlayers().size() + ", regionId=" + getRegionId() + "]");
	}

	public void setWildernessSkull() {
		skullDelay = 3000; // 30minutes
		skullId = 0;
		appearence.generateAppearenceData();
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public int setSkullDelay(int delay) {
		return this.skullDelay = delay;
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null) {
				continue;
			}
			for (FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave()) && this != item.getOwner() || item.getTile().getPlane() != getPlane()) {
					continue;
				}
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null) {
				continue;
			}
			for (FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave()) && this != item.getOwner() || item.getTile().getPlane() != getPlane()) {
					continue;
				}
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> spawnedObjects = World.getRegion(regionId).getSpawnedObjects();
			if (spawnedObjects != null) {
				for (WorldObject object : spawnedObjects) {
					if (object.getPlane() == getPlane()) {
						getPackets().sendSpawnedObject(object);
					}
				}
			}
			List<WorldObject> removedObjects = World.getRegion(regionId).getRemovedObjects();
			if (removedObjects != null) {
				for (WorldObject object : removedObjects) {
					if (object.getPlane() == getPlane()) {
						getPackets().sendDestroyObject(object);
					}
				}
			}
		}
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		started = true;
		run();
		if (isDead()) {
			sendDeath(null);
		}
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterfaces) {
		routeEvent = null;
		if (stopInterfaces) {
			closeInterfaces();
		}
		if (stopWalk) {
			resetWalkSteps();
		}
		setNextFaceEntity(null);
		actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void reset() {
		super.reset();
		refreshHitPoints();
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		skullDelay = 0;
		foodDelay = 0;
		potDelay = 0;
		poisonImmune = 0;
		fireImmune = 0;
		setPrayerRenewalDelay(0);
		setRunEnergy(100);
		appearence.generateAppearenceData();
	}

	public void restoreAll() {
		setHitpoints(getMaxHitpoints());
		refreshHitPoints();
		prayer.setPrayerpoints(getSkills().getLevel(Skills.PRAYER) * 10);
		combatDefinitions.resetSpecialAttack();
		combatDefinitions.resetSpells(true);
		getPoison().reset();
		poisonImmune = 0;
		skullDelay = 0;
		setRunEnergy(100);
		appearence.generateAppearenceData();
		skills.restoreSkills();
		prayer.refreshPrayerPoints();
	}

	public void closeInterfaces() {
		if (interfaceManager != null) {
			if (interfaceManager.containsScreenInterface()) {
				interfaceManager.closeScreenInterface();
			}
			if (interfaceManager.containsInventoryInter()) {
				interfaceManager.closeInventoryInterface();
			}
		}
		if (dialogueManager != null) {
			dialogueManager.finishDialogue();
		}
		if (getCloseInterfacesEvent() != null) {
			getCloseInterfacesEvent().run();
			setCloseInterfacesEvent(null);
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(!started);
			if (!wasAtDynamicRegion) {
				localNPCUpdate.reset();
			}
		} else {
			getPackets().sendMapRegion(!started);
			if (wasAtDynamicRegion) {
				localNPCUpdate.reset();
			}
		}
		forceNextMapLoadRefresh = false;
	}

	@Override
	public void processEntity() {
		RuneTopList.runPendingCallbacks(getUsername().replaceAll("_", " "), this);
		cutscenesManager.process();
		if (routeEvent != null && routeEvent.processEvent(this)) {
			routeEvent = null;
		}
		super.processEntity();
		if (musicsManager.musicEnded()) {
			musicsManager.replayMusic();
		}
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull()) {
				appearence.generateAppearenceData();
			}
		}

		/*
		 * boolean isBot = username.contains("bot"); if (!Constants.isVPS &&
		 * isBot) { int random = Utils.random(1, 5); switch (random) { case 1:
		 * this.setNextForceTalk(new
		 * ForceTalk("hey im just chilling atm haha")); break; case 2:
		 * this.setNextAnimation(new Animation(892)); break; case 3:
		 * this.setNextFaceEntity
		 * (World.getPlayers().get(getRegion().getPlayerIndexes().get(0)));
		 * break; case 4: case 5: this.addWalkSteps(getX() + Utils.random(-10,
		 * 5), getY() + Utils.random(-10, 5)); break; } }
		 */

		if (polDelay == 1) {
			getPackets().sendGameMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
		}
		if (overloadDelay > 0) {
			if (overloadDelay == 1 || isDead()) {
				Pots.resetOverLoadEffect(this);
				return;
			} else if ((overloadDelay - 1) % 25 == 0) {
				Pots.applyOverLoadEffect(this);
			}
			overloadDelay--;
		}
		if (getPrayerRenewalDelay() > 0) {
			if (getPrayerRenewalDelay() == 1 || isDead()) {
				getPackets().sendGameMessage("<col=0000FF>Your prayer renewal has ended.");
				setPrayerRenewalDelay(0);
				return;
			} else {
				if (getPrayerRenewalDelay() == 50)
					getPackets().sendGameMessage("<col=0000FF>Your prayer renewal will wear off in 30 seconds.");
				if (!prayer.hasFullPrayerpoints()) {
					getPrayer().restorePrayer(1);
					if ((getPrayerRenewalDelay() - 1) % 25 == 0)
						setNextGraphics(new Graphics(1295));
				}
			}
			setPrayerRenewalDelay(getPrayerRenewalDelay() - 1);
		}
		charges.process();
		auraManager.process();
		actionManager.process();
		prayer.processPrayer();
		controlerManager.process();
		farmingManager.process();
		if (controlerAvailable() && currentRandomEvent != null)
			currentRandomEvent.process(this);
	}

	@Override
	public void processReceivedHits() {
		if (lockDelay > Utils.currentTimeMillis()) {
			return;
		}
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1 || updateMovementType;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
		updateMovementType = false;
		if (!clientHasLoadedMapRegion()) {
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		updateMovementType = true;
		if (update) {
			sendRunButtonConfig();
		}
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		updateMovementType = true;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			updateMovementType = true;
			sendRunButtonConfig();
		}
	}

	public void sendRunButtonConfig() {
		getPackets().sendConfig(173, resting ? 3 : getRun() ? 1 : 0);
	}

	public void restoreRunEnergy() {
		if (getNextRunDirection() == -1 && runEnergy < 100) {
			runEnergy++;
			if (resting && runEnergy < 100) {
				runEnergy++;
			}
			getPackets().sendRunEnergy();
		}
	}

	public ForumGroups getDonationGroup() {
		long purchasedTotal = facade.getTotalPointsPurchased();
		ForumGroups group = null;
		if (purchasedTotal >= 10 && purchasedTotal < 30)
			group = ForumGroups.REGULAR_DONATOR;
		else if (purchasedTotal >= 30 && purchasedTotal < 60)
			group = ForumGroups.SUPER_DONATOR;
		else if (purchasedTotal >= 60)
			group = ForumGroups.EXTREME_DONATOR;
		return group;
	}

	public boolean checkDonationUpdate() {
		ForumGroups donationGroup = getDonationGroup();
		if (donationGroup == null)
			return false;
		ListIterator<ForumGroup> it$ = forumGroups.listIterator();
		while (it$.hasNext()) {
			ForumGroup group = it$.next();
			if (group.getType() == GroupType.MAIN) {
				boolean prioritized = group.getGroup().ordinal() <= 4;
				if (group.getGroup() == donationGroup)
					continue;
				if (!prioritized) {
					setMembergroupId(donationGroup);
					System.out.println("User required a group change!");
					return true;
				}
			}
		}
		return false;
	}

	public void setMembergroupId(ForumGroups group) {
		if (Constants.SQL_ENABLED) {
			DatabaseConnection connection = World.getConnectionPool().nextFree();
			try {
				Statement stmt = connection.createStatement();
				stmt.execute("UPDATE `vb_user` SET `usergroupid` ='" + group.getId() + "' WHERE " + "username='" + Utils.formatPlayerNameForDisplay(getUsername()) + "'");
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.returnConnection();
				}
			}
		} else {
			new RuntimeException("SQL was not enabled, could not perform query.");
		}
	}

	public void addForumGroups() {
		if (Constants.SQL_ENABLED) {
			setForumGroups(new ArrayList<ForumGroup>());

			String memberGroupIds = (String) getForumTable("membergroupids");
			if (memberGroupIds.contains(",")) {
				String[] groups = memberGroupIds.split(",");
				for (String s : groups) {
					getForumGroups().add(new ForumGroup(GroupType.SECONDARY, ForumGroups.getGroup(Integer.parseInt(s))));
				}
			} else {
				if (memberGroupIds.length() > 0) {
					getForumGroups().add(new ForumGroup(GroupType.SECONDARY, ForumGroups.getGroup(Integer.parseInt(memberGroupIds))));
				}
			}
			getForumGroups().add(new ForumGroup(GroupType.MAIN, ForumGroups.getGroup((int) getForumTable("usergroupid"))));
			if (checkDonationUpdate() && getAttributes().get("checked_groups") == null) {
				getAttributes().put("checked_groups", true);
				addForumGroups();
			} else {
				applyIntegration();
			}

			if (Constants.DEBUG)
				System.out.println(getForumGroups() + " " + getFacade().getTotalPointsPurchased());
		} else {
			rights = Rights.OWNER;
		}
	}

	private void applyIntegration() {
		if (isInGroup(ForumGroups.OWNER)) {
			rights = Rights.OWNER;
		} else if (isInGroup(ForumGroups.ADMINISTRATOR)) {
			rights = Rights.ADMINISTRATOR;
		} else if (isInGroup(ForumGroups.SERVER_MODERATOR, ForumGroups.GLOBAL_MODERATOR)) {
			rights = Rights.MODERATOR;
		} else {
			rights = Rights.PLAYER;
		}
	}

	public boolean isSupporter() {
		return isInGroup(ForumGroups.SUPPORT);
	}

	public boolean isInGroup(ForumGroups... groupz) {
		for (ForumGroup g : getForumGroups()) {
			if (g.getGroup() == null) {
				continue;
			}
			for (ForumGroups group : groupz) {
				if (g.getGroup().equals(group)) {
					return true;
				}
			}
		}
		return false;
	}

	public Object getForumTable(String table) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			Statement stmt = connection.createStatement();
			String username = getUsername();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `vb_user` WHERE " + "username='" + Utils.formatPlayerNameForDisplay(username) + "' LIMIT 1");
			if (rs.next()) {
				return rs.getObject(table);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
		return -1;
	}

	public void run() {
		if (World.exiting_start != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - World.exiting_start) / 1000);
			getPackets().sendSystemUpdate(World.exiting_delay - delayPassed);
		}
		addForumGroups();
		StringBuilder bldr = new StringBuilder();
		bldr.append("<col=" + ChatColors.BLUE + ">Welcome to " + Constants.SERVER_NAME + " [#" + Constants.REVISION + "]</col>.");
		if (Constants.isDoubleExp) {
			bldr.append("<br><col=" + ChatColors.MAROON + ">Double EXP: <col=" + ChatColors.GREEN + ">ON</col>!");
		}
		if (Constants.isDoubleVotes) {
			bldr.append(" <col=" + ChatColors.MAROON + ">Double Votes: <col=" + ChatColors.GREEN + ">ON</col>!");
		}
		sendMessage(bldr.toString());
		if (rights != Rights.PLAYER)
			sendMessage("Check the commands available to you via ::commands");
		lastIP = getSession().getIP();
		interfaceManager.sendInterfaces();
		getPackets().sendRunEnergy();
		refreshAllowChatEffects();
		refreshMouseButtons();
		refreshPrivateChatSetup();
		refreshAcceptAid();
		sendRunButtonConfig();
		refreshProfanityFilter(false);
		getEmotesManager().refreshListConfigs();
		sendDefaultPlayersOptions();
		checkMultiArea();
		inventory.init();
		equipment.init();
		skills.init();
		combatDefinitions.init();
		prayer.init();
		friendsIgnores.init();
		farmingManager.init();
		Notes.sendUnlockNotes(this);
		refreshHitPoints();
		prayer.refreshPrayerPoints();
		getPoison().refresh();
		sendConfigs();
		musicsManager.init();
		emotesManager.refreshListConfigs();
		sendUnlockedObjectConfigs();
		if (currentFriendChatOwner != null) {
			FriendChatsManager.joinChat(currentFriendChatOwner, this);
			if (currentFriendChat == null) {
				currentFriendChatOwner = null;
			}
		}
		if (familiar != null) {
			familiar.respawnFamiliar(this);
		} else {
			getPetManager().init();
		}
		running = true;
		updateMovementType = true;
		appearence.generateAppearenceData();
		controlerManager.login();
		questManager.login();
		OwnedObjectManager.linkKeys(this);
		refreshOtherChatsSetup();
		if (getClanName() != null) {
			if (!ClansManager.connectToClan(this, getClanName(), false)) {
				setClanName(null);
			}
		}
		JsonHandler.<ExchangeItemLoader> getJsonLoader(ExchangeItemLoader.class).sendLogin(this);
	}

	private void sendConfigs() {
		getPackets().sendConfig(281, 1000); // Quest Drop Menu
		getPackets().sendConfig(1384, 512); // Quest Filter Button
		getPackets().sendConfig(1160, -1);
		getPackets().sendConfig(1960, 1); // Unlocks Task System
		getPackets().sendConfig(1961, 524160); // Something to do with Task
												// System. Idk
		getPackets().sendConfig(1384, 512); // Something to do with Quests. Idk
		getPackets().sendConfig(1962, 8384512); // Task System
		getPackets().sendConfig(1963, 299354); // Task System
		getPackets().sendConfig(1964, 1499501); // Task System
		getPackets().sendConfig(1965, 1470822); // Task System
		getPackets().sendGameBarStages();
		getPackets().sendConfig(130, 4); // Black Knights Fortress Done (Green)
		getPackets().sendConfig(101, 3); // Quest Points the player completed
											// (54)
		getPackets().sendConfig(904, 326); // Maximum Quest Points in 2011 (326)
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 3, false);
	}

	@Override
	public void checkMultiArea() {
		if (!started) {
			return;
		}
		boolean isAtMultiArea = isForceMultiArea() ? true : World.isMultiArea(this);
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	public void logout() {
		if (!running) {
			return;
		}
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime) {
			getPackets().sendGameMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().getNextEmoteEnd() >= currentTime) {
			getPackets().sendGameMessage("You can't log out while perfoming an emote.");
			return;
		}
		if (lockDelay >= currentTime) {
			getPackets().sendGameMessage("You can't log out while perfoming an action.");
			return;
		}
		getPackets().sendLogout();
		running = false;
	}

	public void forceLogout() {
		getPackets().sendLogout();
		running = false;
		realFinish();
	}

	@Override
	public void finish() {
		if (finishing || hasFinished()) {
			return;
		}
		finishing = true;
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime || getEmotesManager().getNextEmoteEnd() >= currentTime || lockDelay >= currentTime) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						packetsDecoderPing = Utils.currentTimeMillis();
						finishing = false;
						finish();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}, 10, TimeUnit.SECONDS);
			return;
		}
		realFinish();
	}

	public void realFinish() {
		if (hasFinished()) {
			return;
		}
		stopAll();
		cutscenesManager.logout();
		controlerManager.logout(); // checks what to do on before logout for
		// login
		running = false;
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null) {
			currentFriendChat.leaveChat(this, true);
		}
		if (familiar != null) {
			familiar.dissmissFamiliar(true);
		} else if (getPet() != null) {
			getPet().finish();
		}
		if (clanManager != null) {
			clanManager.disconnect(this, false);
		}
		if (guestClanManager != null) {
			guestClanManager.disconnect(this, true);
		}
		if (getDwarfCannon() != null) {
			getDwarfCannon().finish(true);
		}
		setFinished(true);
		session.setDecoder(-1);
		Saving.savePlayer(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		Highscores.saveNewHighscores(this);
		System.out.println("Deregistered player! [username=" + username + ", size=" + World.getPlayers().size() + ", regionId=" + getRegionId() + "]");
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9)) {
				super.restoreHitPoints();
			}
			if (resting) {
				super.restoreHitPoints();
			}
			refreshHitPoints();
		}
		return update;
	}

	public int petId;

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getPetId() {
		return petId;
	}

	public void refreshHitPoints() {
		getPackets().sendConfigByFile(7198, getHitpoints());
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public int getRights() {
		return rights.ordinal();
	}

	public int getMessageIcon() {
		if (isInGroup(ForumGroups.OWNER) || getRights() == Rights.OWNER.ordinal()) {
			return 5;
		} else if (isInGroup(ForumGroups.ADMINISTRATOR)) {
			return 2;
		} else if (isInGroup(ForumGroups.SERVER_MODERATOR, ForumGroups.GLOBAL_MODERATOR)) {
			return 1;
		} else if (isInGroup(ForumGroups.SUPPORT)) {
			return 9;
		} else if (isInGroup(ForumGroups.REGULAR_DONATOR)) {
			return 8;
		} else if (isInGroup(ForumGroups.SUPER_DONATOR)) {
			return 7;
		} else if (isInGroup(ForumGroups.EXTREME_DONATOR)) {
			return 6;
		} else if (isInGroup(ForumGroups.NORMAL)) {
			return 0;
		}
		return getRights();
	}

	public int getChatIcon() {
		if (rights == Rights.OWNER) {
			return 5;
		} else if (rights == Rights.ADMINISTRATOR) {
			return 1;
		} else if (rights == Rights.MODERATOR) {
			return 0;
		} else if (isInGroup(ForumGroups.SUPPORT)) {
			return 9;
		} else if (isInGroup(ForumGroups.REGULAR_DONATOR)) {
			return 8;
		} else if (isInGroup(ForumGroups.SUPER_DONATOR)) {
			return 7;
		} else if (isInGroup(ForumGroups.EXTREME_DONATOR)) {
			return 6;
		}
		return -1;
	}

	public DefaultGameEncoder getPackets() {
		return session.getWorldPackets();
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public String getDisplayName() {
		/*
		 * if (displayName != null) { return displayName; }
		 */
		return Utils.formatPlayerNameForDisplay(username);
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public Appearence getAppearence() {
		return appearence;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public void setPacketsDecoderPing(long packetsDecoderPing) {
		this.packetsDecoderPing = packetsDecoderPing;
	}

	public long getPacketsDecoderPing() {
		return packetsDecoderPing;
	}

	public Session getSession() {
		return session;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Skills getSkills() {
		return skills;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	public void drainRunEnergy() {
		setRunEnergy(runEnergy - 1);
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	public boolean isResting() {
		return resting;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public void handleSoulSplit(final Player user, final Entity target, final Hit hit) {
		if (hit.getDamage() <= 0) {
			return;
		}
		World.sendProjectile(user, target, 2263, 11, 11, 20, 5, 0, 0);
		user.heal(hit.getDamage() / 5);
		if (target.isPlayer()) {
			target.player().prayer.drainPrayer(hit.getDamage() / 5);
		}

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0) {
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
				}
			}
		}, 1);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE) {
			return;
		}
		if (auraManager.usingPenance()) {
			int amount = (int) (hit.getDamage() * 0.2);
			if (amount > 0) {
				prayer.restorePrayer(amount);
			}
		}
		Entity source = hit.getSource();
		if (source == null) {
			return;
		}
		int shieldId = equipment.getShieldId();
		int ammy = equipment.getAmuletId();
		if (ammy == 11090) { // pho neck
			double hpPercent = ((float) getHitpoints() / (float) getMaxHitpoints()) * 100;
			if (hpPercent <= 20) {
				heal((int) (getMaxHitpoints() * 0.3));
				getEquipment().deleteItem(11090, 1);
				getPackets().sendGameMessage("Your pheonix necklace heals you, but is destroyed in the process.");
				return;
			}
		} else if (shieldId == 13742) { // elsyian
			if (Utils.getRandom(100) <= 70) {
				hit.setDamage((int) (hit.getDamage() * 0.75));
			}
		} else if (shieldId == 13740) { // divine
			int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (polDelay > Utils.currentTimeMillis()) {
			hit.setDamage((int) (hit.getDamage() * 0.5));
		}
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.usingPrayer(0, 17)) {
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 7)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.usingPrayer(0, 18)) {
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 8)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				if (prayer.usingPrayer(0, 19)) {
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 9)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2230));
						setNextAnimation(new Animation(12573));
					}
				}
			}
		}
		if (hit.getDamage() >= 200) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			}
		}
		if (getTemporaryAttributtes().get("cast_vengeance") != null && (getTemporaryAttributtes().get("cast_vengeance").equals(true)) && hit.getDamage() >= 4) {
			getTemporaryAttributtes().remove("cast_vengeance");
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			source.applyHit(new Hit(this, (int) (hit.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
		}
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.prayer.hasPrayersOn()) {
				if (p2.prayer.usingPrayer(0, 24)) { // smite
					int drain = hit.getDamage() / 4;
					if (drain > 0) {
						prayer.drainPrayer(drain);
					}
				} else {
					if (hit.getDamage() == 0) {
						return;
					}
					if (!p2.prayer.isBoostedLeech()) {
						if (hit.getLook() == HitLook.MELEE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 19)) {
								if (Utils.getRandom(4) == 0) {
									p2.prayer.increaseTurmoilBonus(this);
									p2.prayer.setBoostedLeech(true);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 1)) { // sap att
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(0)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
									} else {
										p2.prayer.increaseLeechBonus(0);
										p2.getPackets().sendGameMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2214));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2215, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2216));
										}
									}, 1);
									return;
								}
							} else {
								if (p2.prayer.usingPrayer(1, 10)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(3)) {
											p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
										} else {
											p2.prayer.increaseLeechBonus(3);
											p2.getPackets().sendGameMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2231, 35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(new WorldTask() {
											@Override
											public void run() {
												setNextGraphics(new Graphics(2232));
											}
										}, 1);
										return;
									}
								}
								if (p2.prayer.usingPrayer(1, 14)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(7)) {
											p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
										} else {
											p2.prayer.increaseLeechBonus(7);
											p2.getPackets().sendGameMessage("Your curse drains Strength from the enemy, boosting your Strength.", true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2248, 35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(new WorldTask() {
											@Override
											public void run() {
												setNextGraphics(new Graphics(2250));
											}
										}, 1);
										return;
									}
								}

							}
						}
						if (hit.getLook() == HitLook.RANGE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 2)) { // sap range
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(1)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
									} else {
										p2.prayer.increaseLeechBonus(1);
										p2.getPackets().sendGameMessage("Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2217));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2219));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 11)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(4)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
									} else {
										p2.prayer.increaseLeechBonus(4);
										p2.getPackets().sendGameMessage("Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2236, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2238));
										}
									});
									return;
								}
							}
						}
						if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 3)) { // sap mage
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(2)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
									} else {
										p2.prayer.increaseLeechBonus(2);
										p2.getPackets().sendGameMessage("Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2220));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2222));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 12)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(5)) {
										p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
									} else {
										p2.prayer.increaseLeechBonus(5);
										p2.getPackets().sendGameMessage("Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2240, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2242));
										}
									}, 1);
									return;
								}
							}
						}

						// overall

						if (p2.prayer.usingPrayer(1, 13)) { // leech defence
							if (Utils.getRandom(10) == 0) {
								if (p2.prayer.reachedMax(6)) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
								} else {
									p2.prayer.increaseLeechBonus(6);
									p2.getPackets().sendGameMessage("Your curse drains Defence from the enemy, boosting your Defence.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2244, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2246));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 15)) {
							if (Utils.getRandom(10) == 0) {
								if (getRunEnergy() <= 0) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
								} else {
									p2.setRunEnergy(p2.getRunEnergy() > 90 ? 100 : p2.getRunEnergy() + 10);
									setRunEnergy(p2.getRunEnergy() > 10 ? getRunEnergy() - 10 : 0);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2256, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2258));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 16)) {
							if (Utils.getRandom(10) == 0) {
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your leech curse has no effect.", true);
								} else {
									p2.combatDefinitions.restoreSpecialAttack();
									combatDefinitions.desecreaseSpecialAttack(10);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2252, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2254));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 4)) { // sap spec
							if (Utils.getRandom(10) == 0) {
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2223));
								p2.prayer.setBoostedLeech(true);
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
								} else {
									combatDefinitions.desecreaseSpecialAttack(10);
								}
								World.sendProjectile(p2, this, 2224, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2225));
									}
								}, 1);
								return;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void sendDeath(final Entity source) {
		if (prayer.hasPrayersOn() && getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isAtMultiArea()) {
					for (int regionId : getMapRegionsIds()) {
						List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(playerIndex);
								if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || !player.withinDistance(this, 1) || !target.getControllerManager().canHit(player)) {
									continue;
								}
								player.applyHit(new Hit(target, Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)), HitLook.REGULAR_DAMAGE));
							}
						}
						List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
						if (npcsIndexes != null) {
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(this, 1) || !npc.getDefinitions().hasAttackOption() || !target.getControllerManager().canHit(npc)) {
									continue;
								}
								npc.applyHit(new Hit(target, Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)), HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead() && !source.hasFinished() && source.withinDistance(this, 1)) {
						source.applyHit(new Hit(target, Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)), HitLook.REGULAR_DAMAGE));
					}
				}
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() - 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() + 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX(), target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX(), target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() - 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() - 1, target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() + 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438), new WorldTile(target.getX() + 1, target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.usingPrayer(1, 17)) {
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						if (isAtMultiArea()) {
							for (int regionId : getMapRegionsIds()) {
								List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
								if (playersIndexes != null) {
									for (int playerIndex : playersIndexes) {
										Player player = World.getPlayers().get(playerIndex);
										if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || !player.withinDistance(target, 2) || !target.getControllerManager().canHit(player)) {
											continue;
										}
										player.applyHit(new Hit(target, Utils.getRandom(skills.getLevelForXp(Skills.PRAYER) * 3), HitLook.REGULAR_DAMAGE));
									}
								}
								List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
								if (npcsIndexes != null) {
									for (int npcIndex : npcsIndexes) {
										NPC npc = World.getNPCs().get(npcIndex);
										if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(target, 2) || !npc.getDefinitions().hasAttackOption() || !target.getControllerManager().canHit(npc)) {
											continue;
										}
										npc.applyHit(new Hit(target, Utils.getRandom(skills.getLevelForXp(Skills.PRAYER) * 3), HitLook.REGULAR_DAMAGE));
									}
								}
							}
						} else {
							if (source != null && source != target && !source.isDead() && !source.hasFinished() && source.withinDistance(target, 2)) {
								source.applyHit(new Hit(target, Utils.getRandom(skills.getLevelForXp(Skills.PRAYER) * 3), HitLook.REGULAR_DAMAGE));
							}
						}

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 1, getY() - 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 1, getY() - 1, getPlane()));
					}
				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath()) {
			return;
		}
		lock(7);
		stopAll();
		if (familiar != null) {
			familiar.sendDeath(this);
		}
		final Player thisPlayer = this;
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = thisPlayer;
					if (killer != null) {
						killer.removeDamage(thisPlayer);
						killer.increaseKillCount(thisPlayer);
					}
					thisPlayer.sendItemsOnDeath(killer);
					thisPlayer.getEquipment().init();
					thisPlayer.getInventory().init();
					thisPlayer.reset();
					thisPlayer.setNextWorldTile(Constants.DEATH_TILE);
					thisPlayer.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void sendItemsOnDeath(Player killer) {
		sendItemsOnDeath(killer, hasSkull());
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this, true, dropItems, getPrayer().isProtectingItem());
		sendItemsOnDeath(killer, new WorldTile(this), new WorldTile(this), slots);
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, Integer[][] slots) {
		boolean wilderness = false;
		if (getControllerManager().getController() instanceof Wilderness || (getControllerManager().getController() instanceof FfaZone && ((FfaZone) getControllerManager().getController()).isRisk())) {
			wilderness = true;
		}
		charges.die(slots[1], slots[3]); // degrades droped and lost items only
		auraManager.removeAura();
		Item[][] items = GraveStone.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		appearence.generateAppearenceData();
		for (Item item : items[0]) {
			inventory.addItemDrop(item.getId(), item.getAmount(), respawnTile);
		}
		if (items[1].length != 0) {
			if (wilderness) {
				for (Item item : items[1]) {
					if (item.getDefinitions().isTradeable())
						World.addGroundItem(item, deathTile, killer == null ? this : killer, true, 60, 0);
					else
						World.addGroundItem(item, deathTile, this, true, 120, 2);
				}
			} else
				new GraveStone(this, deathTile, items[1]);
		}
	}

	public void increaseKillCount(Player killed) {
		if (killed.getSession().getIP().equals(getSession().getIP())) {
			return;
		}
		killed.setDeathCount(killed.getDeathCount() + 1);
		setKillCount(getKillCount() + 1);
	}

	public void handleKill(Player died) {
		if (died.getUsername().equals(getUsername()))
			return;
		if (lastKills.size() >= 5) {
			lastKills.remove(0);
		}
		if (Constants.DEBUG)
			System.out.println("Player: " + getUsername() + ", Killed: " + died.getUsername());
		if (!shouldIncreasePkPoints(died.getUsername(), died.getSession().getIP())) {
			sendMessage("<col=" + ChatColors.MAROON + ">You have defeated " + died.getDisplayName() + ", but did not gain any pk points.");
			return;
		}
		getFacade().setPkPoints(getFacade().getPkPoints() + 1);
		sendMessage("<col=" + ChatColors.MAROON + ">You have defeated " + died.getDisplayName() + "! You now have " + getFacade().getPkPoints() + " pk points.");
		lastKills.put(died.getUsername(), System.currentTimeMillis());
	}

	/**
	 * Sends a notification to the player and hides it after the delay
	 * 
	 * @param message
	 *            The message
	 * @param delay
	 *            The amount of ticks to wait before hiding the notification
	 */
	public void sendNotification(String message, int delay) {
		int interfaceId = 1073;
		getPackets().sendIComponentText(interfaceId, 10, "Game Notification");
		getPackets().sendIComponentText(interfaceId, 11, message);
		getInterfaceManager().sendTab(getInterfaceManager().hasResizableScreen() ? 10 : 8, interfaceId);
		if (delay != -1)
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					getPackets().closeInterface(getInterfaceManager().hasResizableScreen() ? 10 : 8);
				}
			}, delay);
	}

	public boolean shouldIncreasePkPoints(String username, String ip) {
		if (!Constants.DEBUG)
			if (ip.equalsIgnoreCase(getSession().getIP())) {
				return false;
			}
		if (lastKills.containsKey(username)) {
			long lastKill = lastKills.get(username);

			if ((System.currentTimeMillis() - lastKill) < TimeUnit.MINUTES.toMillis(5))
				return false;
		}
		return true;
	}

	public void sendRandomJail(Player p) {
		p.resetWalkSteps();
		switch (Utils.getRandom(6)) {
		case 0:
			p.setNextWorldTile(new WorldTile(3014, 3195, 0));
			break;
		case 1:
			p.setNextWorldTile(new WorldTile(3015, 3189, 0));
			break;
		case 2:
			p.setNextWorldTile(new WorldTile(3014, 3189, 0));
			break;
		case 3:
			p.setNextWorldTile(new WorldTile(3014, 3192, 0));
			break;
		case 4:
			p.setNextWorldTile(new WorldTile(3018, 3180, 0));
			break;
		case 5:
			p.setNextWorldTile(new WorldTile(3018, 3189, 0));
			break;
		case 6:
			p.setNextWorldTile(new WorldTile(3018, 3189, 0));
			break;
		}
	}

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		appearence.generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public long getLockDelay() {
		return lockDelay;
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void lock(long delay) {
		lockDelay = Utils.currentTimeMillis() + (delay * 600);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message, final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1) {
			setNextAnimation(new Animation(emoteId));
		}
		if (useDelay == 0) {
			setNextWorldTile(dest);
		} else {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead()) {
						return;
					}
					if (resetAnimation) {
						setNextAnimation(new Animation(-1));
					}
					setNextWorldTile(dest);
					if (message != null) {
						getPackets().sendGameMessage(message);
					}
				}
			}, useDelay - 1);
		}
	}

	public Bank getBank() {
		return bank;
	}

	public ControlerManager getControllerManager() {
		return controlerManager;
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7]; // includes new bro for future
		// use
		barrowsKillCount = 0;
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void refreshAllowChatEffects() {
		getPackets().sendConfig(171, allowChatEffects ? 0 : 1);
	}

	public void refreshMouseButtons() {
		getPackets().sendConfig(170, mouseButtons ? 0 : 1);
	}

	public void refreshPrivateChatSetup() {
		getPackets().sendConfig(287, privateChatSetup);
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
		refreshPrivateChatSetup();
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	/*
	 * do not use this, only used by pm
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void setDisplayName(String displayName) {
		if (Utils.formatPlayerNameForDisplay(username).equals(displayName)) {
			this.displayName = null;
		} else {
			this.displayName = displayName;
		}
	}

	public void addPotDelay(long time) {
		potDelay = time + Utils.currentTimeMillis();
	}

	public long getPotDelay() {
		return potDelay;
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utils.currentTimeMillis();
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public long getBoneDelay() {
		return boneDelay;
	}

	public void addBoneDelay(long time) {
		boneDelay = time + Utils.currentTimeMillis();
	}

	public void addPoisonImmune(long time) {
		poisonImmune = time + Utils.currentTimeMillis();
		getPoison().reset();
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public void addFireImmune(long time) {
		fireImmune = time + Utils.currentTimeMillis();
	}

	public long getFireImmune() {
		return fireImmune;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setInterfaceListenerEvent(Runnable listener) {
		this.interfaceListenerEvent = listener;
	}

	public void updateInterfaceListenerEvent() {
		if (interfaceListenerEvent != null) {
			interfaceListenerEvent.run();
			interfaceListenerEvent = null;
		}
	}

	public long getJailed() {
		return jailed;
	}

	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public boolean[] setKilledBarrowBrothers(boolean[] b) {
		return this.killedBarrowBrothers = b;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public boolean isDonator() {
		return isInGroup(ForumGroups.REGULAR_DONATOR) || isInGroup(ForumGroups.SUPER_DONATOR) || isInGroup(ForumGroups.EXTREME_DONATOR);
	}

	public int[] getPouches() {
		return pouches;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public boolean isLocked() {
		return lockDelay >= Utils.currentTimeMillis();
	}

	public String getLastIP() {
		return lastIP;
	}

	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public DuelRules getLastDuelRules() {
		return lastDuelRules;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		this.lastDuelRules = duelRules;
	}

	public boolean isDueling() {
		return lastDuelRules != null;
	}

	public ItemsContainer<Item> getDuelSpoils() {
		return duelSpoils;
	}

	public void setDuelSpoils(ItemsContainer<Item> spoils) {
		this.duelSpoils = spoils;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	public void sendFriendsChannelMessage(ChatMessage message) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null || !p.hasStarted() || p.hasFinished() || p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null) {
					continue;
				}
				if (p.getFriendsIgnores().containsIgnore(this.getUsername()) || this.getFriendsIgnores().containsIgnore(p.getUsername())) {
					continue;
				}
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public boolean withinDistance(Player tile) {
		if (cutscenesManager.hasCutscene()) {
			return getMapRegionsIds().contains(tile.getRegionId());
		} else {
			if (tile.getPlane() != getPlane()) {
				return false;
			}
			return Math.abs(tile.getX() - getX()) <= 14 && Math.abs(tile.getY() - getY()) <= 14;
		}
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public int getSkullId() {
		return skullId;
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	public int getOverloadDelay() {
		return overloadDelay;
	}

	public void setOverloadDelay(int overloadDelay) {
		this.overloadDelay = overloadDelay;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public LinkedHashMap<String, Long> getLastKills() {
		return lastKills;
	}

	public void setLastKills(LinkedHashMap<String, Long> list) {
		this.lastKills = list;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributtes().put("TeleBlocked", teleDelay + Utils.currentTimeMillis());
	}

	public long getTeleBlockDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("TeleBlocked");
		if (teleblock == null) {
			return 0;
		}
		return teleblock;
	}

	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributtes().put("PrayerBlocked", teleDelay + Utils.currentTimeMillis());
		prayer.closeAllPrayers();
	}

	public long getPrayerDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("PrayerBlocked");
		if (teleblock == null) {
			return 0;
		}
		return teleblock;
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public FriendChatsManager getCurrentFriendChat() {
		return currentFriendChat;
	}

	public void setCurrentFriendChat(FriendChatsManager currentFriendChat) {
		this.currentFriendChat = currentFriendChat;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	/**
	 * Tells you if the player is in a controler which makes them unavailable to
	 * outside features, such as random events
	 * 
	 * @return
	 */
	public boolean controlerAvailable() {
		return controlerManager.getController() == null;
	}

	public void setTrapAmount(int trapAmount) {
		this.trapAmount = trapAmount;
	}

	public int getTrapAmount() {
		return trapAmount;
	}

	public long getPolDelay() {
		return polDelay;
	}

	public void addPolDelay(long delay) {
		polDelay = delay + Utils.currentTimeMillis();
	}

	public void setPolDelay(long delay) {
		this.polDelay = delay;
	}

	public boolean isUsingTicket() {
		return usingTicket;
	}

	public void setUsingTicket(boolean usingTicket) {
		this.usingTicket = usingTicket;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public int getMovementType() {
		if (getTemporaryMoveType() != -1) {
			return getTemporaryMoveType();
		}
		return isRunning() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) {
			ownedObjectsManagerKeys = new LinkedList<String>();
		}
		return ownedObjectsManagerKeys;
	}

	public ClanWars getClanWars() {
		return clanWars;
	}

	public ClanWars setClanWars(ClanWars clanWars) {
		return this.clanWars = clanWars;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
		case 4153:
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 1377:
		case 13472:
		case 35:// Excalibur
		case 8280:
		case 14632:
		case 24455:
		case 24456:
		case 24457:
		case 14679:
			return true;
		default:
			return false;
		}
	}

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			getPackets().sendGameMessage("You don't have enough power left.");
			combatDefinitions.desecreaseSpecialAttack(0);
			return;
		}
		switch (weaponId) {
		case 24455:
		case 24456:
		case 24457:
			getPackets().sendGameMessage("Aren't you strong enough already?");
			break;
		case 4153:
		case 14679:
			if (!(getActionManager().getAction() instanceof PlayerCombat)) {
				getPackets().sendGameMessage("Warning: Since the maul's special is an instant attack, it will be wasted when used on a first strike.");
				combatDefinitions.switchUsingSpecialAttack();
				return;
			}
			PlayerCombat combat = (PlayerCombat) getActionManager().getAction();
			Entity target = combat.getTarget();
			if (!Utils.isOnRange(getX(), getY(), getSize(), target.getX(), target.getY(), target.getSize(), 5)) {
				combatDefinitions.switchUsingSpecialAttack();
				return;
			}
			setNextAnimation(new Animation(1667));
			setNextGraphics(new Graphics(340, 0, 96 << 16));
			int attackStyle = getCombatDefinitions().getAttackStyle();
			combat.delayNormalHit(weaponId, attackStyle, combat.getMeleeHit(this, combat.getRandomMaxHit(this, weaponId, attackStyle, false, true, 1.1, true)));
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevelForXp(Skills.DEFENCE) * 0.90D);
			int attack = (int) (skills.getLevelForXp(Skills.ATTACK) * 0.90D);
			int range = (int) (skills.getLevelForXp(Skills.RANGE) * 0.90D);
			int magic = (int) (skills.getLevelForXp(Skills.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevelForXp(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			setNextForceTalk(new ForceTalk("For " + Constants.SERVER_NAME + "!"));
			final boolean enhanced = weaponId == 14632;
			skills.set(Skills.DEFENCE, enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D) : (skills.getLevel(Skills.DEFENCE) + 8));
			WorldTasksManager.schedule(new WorldTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished() || getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			addPolDelay(60000);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		}
	}

	public boolean takeMoney(int amount) {
		if (inventory.getNumberOf(995) >= amount) {
			inventory.deleteItem(995, amount);
			return true;
		} else {
			return false;
		}
	}

	public void switchAcceptAid() {
		facade.setAcceptingAid(!facade.isAcceptingAid());
		refreshAcceptAid();
	}

	private void refreshAcceptAid() {
		getPackets().sendConfig(427, facade.isAcceptingAid() ? 1 : 0);
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public void addDisplayTime(long i) {
		this.displayTime = i + Utils.currentTimeMillis();
	}

	public long getDisplayTime() {
		return displayTime;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	/**
	 * @return the achievementManager
	 */
	public AchievementManager getAchievementManager() {
		return achievementManager;
	}

	/**
	 * @param achievementManager
	 *            the achievementManager to set
	 */
	public void setAchievementManager(AchievementManager achievementManager) {
		this.achievementManager = achievementManager;
	}

	/**
	 * @return the facade
	 */
	public Facade getFacade() {
		return facade;
	}

	/**
	 * @param facade
	 *            the facade to set
	 */
	public void setFacade(Facade facade) {
		this.facade = facade;
	}

	/**
	 * @return the slayerTask
	 */
	public SlayerTask getSlayerTask() {
		return slayerTask;
	}

	/**
	 * @param slayerTask
	 *            the slayerTask to set
	 */
	public void setSlayerTask(SlayerTask slayerTask) {
		this.slayerTask = slayerTask;
	}

	/**
	 * @return the questManager
	 */
	public QuestManager getQuestManager() {
		return questManager;
	}

	/**
	 * @param questManager
	 *            the questManager to set
	 */
	public void setQuestManager(QuestManager questManager) {
		this.questManager = questManager;
	}

	/**
	 * @return the dwarfCannon
	 */
	public DwarfCannon getDwarfCannon() {
		return dwarfCannon;
	}

	/**
	 * @param dwarfCannon
	 *            the dwarfCannon to set
	 */
	public void setDwarfCannon(DwarfCannon dwarfCannon) {
		this.dwarfCannon = dwarfCannon;
	}

	/**
	 * @return the forumGroups
	 */
	public List<ForumGroup> getForumGroups() {
		if (forumGroups == null) {
			forumGroups = new ArrayList<ForumGroup>();
		}
		return forumGroups;
	}

	/**
	 * @param forumGroups
	 *            the forumGroups to set
	 */
	public void setForumGroups(List<ForumGroup> forumGroups) {
		this.forumGroups = forumGroups;
	}

	/**
	 * @return the loyaltyManager
	 */
	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	/**
	 * @return the slayerManager
	 */
	public SlayerManager getSlayerManager() {
		return slayerManager;
	}

	/**
	 * @param loyaltyManager
	 *            the loyaltyManager to set
	 */
	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}

	/**
	 * @return the petManager
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	/**
	 * @param petManager
	 *            the petManager to set
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	/**
	 * @return the pet
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * @param pet
	 *            the pet to set
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * @return the completedFightCaves
	 */
	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
		}
	}

	/**
	 * @return the deathCount
	 */
	public int getDeathCount() {
		return deathCount;
	}

	/**
	 * @param deathCount
	 *            the deathCount to set
	 */
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}

	/**
	 * @return the killCount
	 */
	public int getKillCount() {
		return killCount;
	}

	/**
	 * @param killCount
	 *            the killCount to set
	 */
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	/**
	 * @return the graveStone
	 */
	public int getGraveStone() {
		return graveStone;
	}

	/**
	 * @param graveStone
	 *            the graveStone to set
	 */
	public void setGraveStone(int graveStone) {
		this.graveStone = graveStone;
	}

	/**
	 * @return the untradeableItems
	 */
	public List<Item> getUntradeableItems() {
		return untradeableItems;
	}

	/**
	 * @param untradeableItems
	 *            the untradeableItems to set
	 */
	public void setUntradeableItems(List<Item> untradeableItems) {
		this.untradeableItems = untradeableItems;
	}

	/**
	 * @return the secondsPlayed
	 */
	public int getSecondsPlayed() {
		return secondsPlayed;
	}

	@Override
	public boolean isFrozen() {
		return getFreezeDelay() >= Utils.currentTimeMillis();
	}

	/**
	 * @param secondsPlayed
	 *            the secondsPlayed to set
	 */
	public void setSecondsPlayed(int secondsPlayed) {
		this.secondsPlayed = secondsPlayed;
	}

	/**
	 * Gets the time you've played
	 * 
	 * @return
	 */
	public String getTimePlayed() {
		if (secondsPlayed < 600) {
			return secondsPlayed + " Seconds";
		} else if (secondsPlayed < 3600) {
			return TimeUnit.SECONDS.toMinutes(secondsPlayed) + " Minutes";
		} else {
			return TimeUnit.SECONDS.toHours(secondsPlayed) + " Hours";
		}
	}

	/**
	 * @return the friendChatSetup
	 */
	public int getFriendChatSetup() {
		return friendChatSetup;
	}

	public VarsManager getVarsManager() {
		return varsManager;
	}

	/**
	 * @param friendChatSetup
	 *            the friendChatSetup to set
	 */
	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
	}

	public void refreshOtherChatsSetup() {
		int value = friendChatSetup << 6;
		getPackets().sendConfig(1438, value);
		getPackets().sendConfigByFile(3612, clanChatSetup);
		getPackets().sendConfigByFile(9191, getGuestChatSetup());
	}

	public void kickPlayerFromClanChannel(String name) {
		if (clanManager == null) {
			return;
		}
		clanManager.kickPlayerFromChat(this, name);
	}

	public void sendClanChannelMessage(ChatMessage message) {
		if (clanManager == null) {
			return;
		}
		clanManager.sendMessage(this, message);
	}

	public void sendClanChannelQuickMessage(QuickChatMessage message) {
		if (clanManager == null) {
			return;
		}
		clanManager.sendQuickMessage(this, message);
	}

	public void sendGuestClanChannelMessage(ChatMessage message) {
		if (guestClanManager == null) {
			return;
		}
		guestClanManager.sendMessage(this, message);
	}

	public void sendGuestClanChannelQuickMessage(QuickChatMessage message) {
		if (guestClanManager == null) {
			return;
		}
		guestClanManager.sendQuickMessage(this, message);
	}

	public int getClanChatSetup() {
		return clanChatSetup;
	}

	public void setClanChatSetup(int clanChatSetup) {
		this.clanChatSetup = clanChatSetup;
	}

	public ClansManager getClanManager() {
		return clanManager;
	}

	public void setClanManager(ClansManager clanManager) {
		this.clanManager = clanManager;
	}

	public ClansManager getGuestClanManager() {
		return guestClanManager;
	}

	public void setGuestClanManager(ClansManager guestClanManager) {
		this.guestClanManager = guestClanManager;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public int getGuestChatSetup() {
		return guestChatSetup;
	}

	public void setGuestChatSetup(int guestChatSetup) {
		this.guestChatSetup = guestChatSetup;
	}

	private boolean profanityFilter;

	public boolean isFilteringProfanity() {
		return profanityFilter;
	}

	public void switchProfanityFilter() {
		profanityFilter = !profanityFilter;
		refreshProfanityFilter(true);
	}

	public void refreshProfanityFilter(boolean notify) {
		getVarsManager().sendVarBit(8780, profanityFilter ? 0 : 1);
		if (notify) {
			sendMessage("You are<col=" + ChatColors.RED + ">" + (profanityFilter ? "" : " not") + "</col> filtering messages received.");
		}
	}

	public int getFinishedCastleWars() {
		return finishedCastleWars;
	}

	public boolean isCapturedCastleWarsFlag() {
		return capturedCastleWarsFlag;
	}

	public void setCapturedCastleWarsFlag() {
		capturedCastleWarsFlag = true;
	}

	public void increaseFinishedCastleWars() {
		finishedCastleWars++;
	}

	public boolean containsOneItem(boolean checkBank, int... itemIds) {
		if (getInventory().containsOneItem(itemIds)) {
			return true;
		}
		if (getEquipment().containsOneItem(itemIds)) {
			return true;
		}
		if (checkBank)
			for (int id : itemIds) {
				if (getBank().containsItem(id, 1))
					return true;
			}
		Familiar familiar = getFamiliar();
		if (familiar != null && ((familiar.getBob() != null && familiar.getBob().containsOneItem(itemIds) || familiar.hasFinished()))) {
			return true;
		}
		return false;
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final Player target = this;
		if (hit.getDamage() > 0) {
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		}
		user.heal(hit.getDamage() / 5);
		prayer.drainPrayer(hit.getDamage() / 5);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0) {
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
				}
			}
		}, 0);
	}

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public void setWarriorPoints(int index, double pointsDifference) {
		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			Controller controler = getControllerManager().getController();
			if (controler == null || !(controler instanceof WarriorsGuild)) {
				return;
			}
			WarriorsGuild guild = (WarriorsGuild) controler;
			guild.setInCyclopse(false);
			setNextWorldTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535) {
			warriorPoints[index] = 65535;
		}
		refreshWarriorPoints(index);
	}

	public void refreshWarriorPoints(int index) {
		varsManager.sendVarBit(index + 8662, (int) warriorPoints[index]);
	}

	public int getFavorPoints() {
		return favorPoints;
	}

	public void setFavorPoints(int points) {
		if (points + favorPoints >= 2000) {
			points = 2000;
			getPackets().sendGameMessage("The offering stone is full! The jadinkos won't deposite any more rewards until you have taken some.");
		}
		this.favorPoints = points;
		refreshFavorPoints();
	}

	public void refreshFavorPoints() {
		varsManager.sendVarBit(9511, favorPoints);
	}

	public int getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public void setRuneSpanPoint(int runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void addRunespanPoints(int points) {
		this.runeSpanPoints += points;
	}

	public FarmingManager getFarmingManager() {
		return farmingManager;
	}

	public boolean isCompletedStealingCreation() {
		return completedStealingCreation;
	}

	public void setCompletedStealingCreation() {
		completedStealingCreation = true;
	}

	public void increaseStealingCreationPoints(int scPoints) {
		stealingCreationPoints += scPoints;
	}

	public int getStealingCreationPoints() {
		return stealingCreationPoints;
	}

	/**
	 * @return the currentRandomEvent
	 */
	public RandomEvent getCurrentRandomEvent() {
		return currentRandomEvent;
	}

	/**
	 * @param currentRandomEvent
	 *            the currentRandomEvent to set
	 */
	public void setCurrentRandomEvent(RandomEvent currentRandomEvent) {
		this.currentRandomEvent = currentRandomEvent;
	}

	/**
	 * @return the actionTime
	 */
	public long getActionTime() {
		return actionTime;
	}

	/**
	 * @param actionTime
	 *            the actionTime to set
	 */
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

	/**
	 * @return the closeInterfacesEvent
	 */
	public Runnable getCloseInterfacesEvent() {
		return closeInterfacesEvent;
	}

	public boolean isToogleLootShare() {
		return toogleLootShare;
	}

	public void disableLootShare() {
		if (isToogleLootShare())
			toogleLootShare();
	}

	public void toogleLootShare() {
		this.toogleLootShare = !toogleLootShare;
		refreshToogleLootShare();
	}

	public void refreshToogleLootShare() {
		varsManager.forceSendVarBit(4071, toogleLootShare ? 1 : 0);
	}

	/**
	 * @return the clueScrollManager
	 */
	public ClueScrollManager getClueScrollManager() {
		return clueScrollManager;
	}

	/**
	 * @return the chatType
	 */
	public int getChatType() {
		return chatType;
	}

	/**
	 * @param chatType
	 *            the chatType to set
	 */
	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	/**
	 * @return the diceSession
	 */
	public DiceSession getDiceSession() {
		return diceSession;
	}

	/**
	 * @param diceSession
	 *            the diceSession to set
	 */
	public void setDiceSession(DiceSession diceSession) {
		this.diceSession = diceSession;
	}

	/**
	 * @return the prayerRenewalDelay
	 */
	public int getPrayerRenewalDelay() {
		return prayerRenewalDelay;
	}

	/**
	 * @param prayerRenewalDelay
	 *            the prayerRenewalDelay to set
	 */
	public void setPrayerRenewalDelay(int prayerRenewalDelay) {
		this.prayerRenewalDelay = prayerRenewalDelay;
	}

	/**
	 * Gets the main forum group you're in
	 * 
	 * @return
	 */
	public ForumGroups getMainGroup() {
		if (Constants.SQL_ENABLED) {
			for (ForumGroup group : forumGroups) {
				if (group.getType() == GroupType.MAIN) {
					return group.getGroup();
				}
			}
		}
		return ForumGroups.OWNER;
	}

	public boolean canSetEmail() {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			Statement stmt = connection.createStatement();
			String forumEmail = (String) getForumTable("email");
			if (forumEmail.length() > 1) {
				stmt.executeUpdate("UPDATE `vb_user` SET `email` = '" + forumEmail + "' WHERE " + "username='" + Utils.formatPlayerNameForDisplay(getUsername()) + "';");
				stmt.close();
				facade.setEmail(forumEmail);
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
		return false;
	}

	public boolean requiresEmailSet() {
		if (!Constants.SQL_ENABLED)
			return false;
		if (facade.getEmail() != null)
			return false;
		if (!canSetEmail()) {
			return true;
		}
		return false;
	}

	public IsaacKeyPair getIsaacKeyPair() {
		return isaacKeyPair;
	}

	private void sendUnlockedObjectConfigs() {
		refreshKalphiteLair();
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted)
			getVarsManager().sendVarBit(7263, 1);
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

}