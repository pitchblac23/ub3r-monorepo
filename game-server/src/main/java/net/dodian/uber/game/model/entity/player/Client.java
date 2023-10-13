package net.dodian.uber.game.model.entity.player;

import net.dodian.uber.comm.LoginManager;
import net.dodian.uber.comm.PacketData;
import net.dodian.uber.comm.SocketHandler;
import net.dodian.uber.game.Constants;
import net.dodian.uber.game.Server;
import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.Position;
import net.dodian.uber.game.model.ShopHandler;
import net.dodian.uber.game.model.UpdateFlag;
import net.dodian.uber.game.model.combat.impl.CombatStyleHandler;
import net.dodian.uber.game.model.entity.Entity;
import net.dodian.uber.game.model.entity.npc.Npc;
import net.dodian.uber.game.model.entity.npc.NpcUpdating;
import net.dodian.uber.game.model.item.*;
import net.dodian.uber.game.model.object.DoorHandler;
import net.dodian.uber.game.model.object.RS2Object;
import net.dodian.uber.game.model.object.Stairs;
import net.dodian.uber.game.model.player.content.Skillcape;
import net.dodian.uber.game.model.player.packets.OutgoingPacket;
import net.dodian.uber.game.model.player.packets.PacketHandler;
import net.dodian.uber.game.model.player.packets.outgoing.*;
import net.dodian.uber.game.model.player.quests.QuestSend;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.uber.game.model.player.skills.cooking.Cooking;
import net.dodian.uber.game.model.player.skills.crafting.Crafting;
import net.dodian.uber.game.model.player.skills.crafting.GoldCrafting;
import net.dodian.uber.game.model.player.skills.crafting.Spinning;
import net.dodian.uber.game.model.player.skills.crafting.Tanning;
import net.dodian.uber.game.model.player.skills.fishing.Fishing;
import net.dodian.uber.game.model.player.skills.fletching.Fletching;
import net.dodian.uber.game.model.player.skills.mining.Mining;
import net.dodian.uber.game.model.player.skills.prayer.Prayer;
import net.dodian.uber.game.model.player.skills.prayer.Prayers;
import net.dodian.uber.game.model.player.skills.runecrafting.EssBags;
import net.dodian.uber.game.model.player.skills.slayer.SlayerTask;
import net.dodian.uber.game.model.player.skills.smithing.Smelting;
import net.dodian.uber.game.model.player.skills.smithing.Smithing;
import net.dodian.uber.game.model.player.skills.woodcutting.Woodcutting;
import net.dodian.uber.game.party.RewardItem;
import net.dodian.uber.game.security.DuelLog;
import net.dodian.uber.game.security.PickupLog;
import net.dodian.uber.game.security.PmLog;
import net.dodian.uber.game.security.TradeLog;
import net.dodian.utilities.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.dodian.uber.game.combat.ClientExtensionsKt.getRangedStr;
import static net.dodian.uber.game.combat.PlayerAttackCombatKt.attackTarget;
import static net.dodian.uber.game.model.player.dialogue.DialogueKt.updateNPCChat;
import static net.dodian.utilities.DatabaseKt.getDbConnection;
import static net.dodian.utilities.DotEnvKt.*;

public class Client extends Player implements Runnable {

	public boolean FarmingWatered = false;
	public boolean immune = false, loggingOut = false, loadingDone = false, reloadHp = false;
	public boolean canPreformAction = true;
	long lastBar = 0;
	public long lastSave, lastProgressSave, snaredUntil = 0;
	public boolean checkTime = false;
	public Entity target = null;
	int otherdbId = -1;
	public int convoId = -1, nextDiag = -1, npcFace = 591;
	public boolean pLoaded = false;
	public String failer = "";
	Date now = new Date();
	public long mutedHours;
	public long mutedTill;
	public long rightNow = now.getTime();
	public boolean filling = false;
	public int boneAltar = -1;
	public int boneChaos = -1;
	public long lastDoor = 0;
	public int clientPid = -1;
	public long session_start = 0;
	public boolean pickupWanted = false, duelWin = false;
	public int pickX, pickY, pickId, pickTries;
	public CopyOnWriteArrayList<Friend> friends = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Friend> ignores = new CopyOnWriteArrayList<>();
	public boolean tradeLocked = false;
	public boolean officialClient = true;
	public int currentSkill = -1;
	/*
	 * Danno: Last row all disabled. As none have effect.
	 */
	public int[] duelButtons = {26069, 26070, 26071, 30136, 2158, 26065 /* 26072, 26073, 26074, 26066, 26076 */};
	public String[] duelNames = {"No Ranged", "No Melee", "No Magic", "No Gear Change", "Fun Weapons", "No Deflect Damage",
			"No Drinks", "No Food", "No prayer", "No Movement", "Obstacles"};
	/*
	 * Danno: Last row all disabled. As none have effect.
	 */
	public boolean[] duelRule = {false, false, false, false, false, true, true, true, true, true, true};
	/*
	 * Danno: Testing for armor restriction rules.
	 */
	private final boolean[] duelBodyRules = new boolean[11];
	private final int[] trueSlots = {0, 1, 2, 13, 3, 4, 5, 7, 12, 10, 9};
	private final int[] falseSlots = {0, 1, 2, 4, 5, 6, -1, 7, -1, 10, 9, -1, 9, 3};
	private final int[] stakeConfigId = new int[23];
	public int[] duelLine = {6698, 6699, 6697, 7817, 669, 6696, 6701, 6702, 6703, 6704, 6731};
	public boolean duelRequested = false, inDuel = false, duelConfirmed = false, duelConfirmed2 = false, duelFight = false;
	public int duel_with = 0;
	public boolean tradeRequested = false, inTrade = false, canOffer = true, tradeConfirmed = false, tradeConfirmed2 = false, tradeResetNeeded = false;
	public int trade_reqId = 0;
	public CopyOnWriteArrayList<GameItem> offeredItems = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<GameItem> otherOfferedItems = new CopyOnWriteArrayList<>();
	public boolean adding = false;
	public ArrayList<RS2Object> objects = new ArrayList<>();
	public long animationReset = 0, lastButton = 0;
	public int dialogInterface = 2459;
	public int random_skill = -1;
	public String[] otherGroups = new String[10];
	public int loginDelay = 0;
	public boolean validClient = true, muted = false;
	public int newPms = 0;
	public String properName = "";
	public int actionButtonId = 0;
	public long lastAttack = 0;
	public long[] globalCooldown = new long[10];
	public boolean validLogin = false;
	public boolean wearing = false;
	public int resetanim = 8;
	public boolean AnimationReset; // Resets Animations With The Use Of The
	public int newheightLevel = 0;
	public int bonusSpec = 0, animationSpec = 0, emoteSpec = 0;
	public boolean specsOn = true;
	public int[] statId = {10252, 11000, 10253, 11001, 10254, 11002, 10255, 11011, 11013, 11014, 11010, 11012, 11006,
						   11009, 11008, 11004, 11003, 11005, 47002, 54090, 11007};
	public String[] BonusName = {"Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Prayer", "Range", "Str", "Spell Dmg"};
	// public int pGender;
	public int i;
	// public int gender;
	public int XremoveSlot = 0;
	public int XinterfaceID = 0;
	public int XremoveID = 0;
	public int stairs = 0;
	public int stairDistance = 1;
	public int skillX = -1;
	public int skillY = -1;
	public int CombatExpRate = 1;
	public int WanneBank = 0;
	public int WanneShop = 0;
	public int WanneThieve = 0;
	public static final int bufferSize = 1000000;
	public java.net.Socket mySock;
	private java.io.InputStream in;
	private java.io.OutputStream out;
	public Stream inputStream, outputStream;
	public byte[] buffer;
	public int readPtr, writePtr;
	public Cryption inStreamDecryption = null, outStreamDecryption = null;
	public int timeOutCounter = 0; // to detect timeouts on the connection to the client
	public int returnCode = 2; // Tells the client if the login was successfull
	private SocketHandler mySocketHandler;
	public PacketData currentPacket;
	public int refundSlot = -1;
	int tX = 0, tY = 0, tStage = 0, tH = 0, tEmote = 0;
	private boolean travelInitiate = false;
	public ArrayList<RewardItem> rewardList = new ArrayList<>();
	public boolean checkInv = false;
	public long potTime = 0;
	public boolean mixPots = false;
	private int mixPotAmt = 0, mixPotId1 = 0, mixPotId2 = 0, mixPotId3 = 0, mixPotXp = 0;
	private boolean tradeSuccessful = false;
	public boolean inHeat() {
		return getPosition().getX() >= 3264 && getPosition().getX() <= 3327 && getPosition().getY() >= 9344 && getPosition().getY() <= 9407;
	}//King black dragon's domain!
	public boolean randomed2;
	// private int setLastVote = 0;
	public boolean usingBow = false;

	/**Gold Crafting*/
	public int goldIndex = -1, goldSlot = -1;
	public int goldCraftingCount = 0;
	public boolean goldCrafting = false;
	public int[][] blanks = {
			{-1, 1647, 1647, 1647, 1647, 1647, 1647},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1}};
	public int[] startSlots = {24469, 24481, 24493, 79};
	public int[] items = {-1, 1607, 1605, 1603, 1601, 1615, 6573};
	public int[] black = {1666, 1685, 1647};
	public int[] sizes = {100, 75, 120, 11067};
	public int[] moulds = {1592, 1597, 1595, 11065};
	public int[] strungAmulets = {1692, 1694, 1696, 1698, 1700, 1702, 6581};
	/**End*/

	/**Skills*/
	//cooking
	public int cookAmount = 0, cookIndex = 0, enterAmountId = 0;
	public boolean cooking = false;
	//fishing
	public int fishIndex;
	public boolean fishing = false;
	//crafting
	public boolean crafting = false;
	public int cItem = -1;
	public int cAmount = 0;
	public boolean spinning = false;
	//woodcutting
	public int[] woodcutting = {0, 0, 0, 1, -1, 3};
	public int woodcuttingIndex = -1;
	//fletching
	public Fletching fletching = new Fletching();
	public boolean fletchings = false, fletchingOther = false;
	public int fletchId = -1, fletchAmount = -1, fletchLog = -1, originalS = -1, fletchExp = 0;
	public int fletchOtherId1 = -1, fletchOtherId2 = -1, fletchOtherId3 = -1, fletchOtherAmount = -1, fletchOtherAmt = -1, fletchOtherXp = -1;
	public long fletchOtherTime = 0;
	public boolean shafting = false;
	public boolean stringing = false;
	//mining
	public boolean mining = false;
	public boolean miningEss = false;
	public int mineIndex = 0, minePick = 0;
	//smithing
	public boolean smelting = false;
	public int smelt_id, smeltCount, smeltExperience;
	public int[] smithing = {0, 0, 0, -1, -1, 0};
	/**End*/

	/**Magic*/
	public int[] staffs = {2415, 2416, 2417, 4675, 4710, 6526, 6914};
	public int autocast_spellIndex = -1;
	public int ancients = 1;
	public int[] ancientId = {12939, 12987, 12901, 12861, 12963, 13011, 12919, 12881, 12951, 12999, 12911, 12871, 12975, 13023, 12929, 12891};
	public int[] ancientButton = {51133, 51185, 51091, 24018, 51159, 51211, 51111, 51069, 51146, 51198, 51102, 51058, 51172, 51224, 51122, 51080};
	public String[] spellName = {"Smoke Rush", "Shadow Rush", "Blood Rush", "Ice Rush",
			"Smoke Burst", "Shadow Burst", "Blood Burst", "Ice Burst",
			"Smoke Blitz", "Shadow Blitz", "Blood Blitz", "Ice Blitz",
			"Smoke Barrage", "Shadow Barrage", "Blood Barrage", "Ice Barrage"};
	public int[] requiredLevel = {1, 10, 20, 30, 40, 50, 60, 70, 74, 76, 80, 82, 86, 88, 92, 94, 96};
	public int[] requiredRunes = {1, 2, 3, 4};
	public int[] baseDamage = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32};
	public long[] coolDown = {2400, 2400, 3000, 3000};
	/**End*/

	/**Quests*/
	public int maxQuests = QuestSend.values().length;
	public int[] quests = new int[maxQuests];
	public int[] QuestInterface = {8145, 8147, 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159,
			8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8177, 8178,
			8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194, 8195, 12174,
			12175, 12176, 12177, 12178, 12179, 12180, 12181, 12182, 12183, 12184, 12185, 12186, 12187, 12188, 12189, 12190,
			12191, 12192, 12193, 12194, 12195, 12196, 12197, 12198, 12199, 12200, 12201, 12202, 12203, 12204, 12205, 12206,
			12207, 12208, 12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216, 12217, 12218, 12219, 12220, 12221, 12222,	12223};
	/**End*/

	public void ReplaceObject2(Position pos, int NewObjectID, int Face, int ObjectType) {
		if (!withinDistance(new int[]{pos.getX(), pos.getY(), 60}) || getPosition().getZ() != pos.getZ())
			return;
		send(new SetMap(pos));
		getOutputStream().createFrame(101);
		getOutputStream().writeByteC((ObjectType << 2) + (Face & 3));
		getOutputStream().writeByte(0);
		/* CREATE OBJECT */
		if (NewObjectID != -1) {
			getOutputStream().createFrame(151);
			getOutputStream().writeByteS(0);
			getOutputStream().writeWordBigEndian(NewObjectID);
			getOutputStream().writeByteS((ObjectType << 2) + (Face & 3));
		}
	}

	/**
	 * @param o 0 = X | 1 = Y | = Distance allowed.
	 */
	private boolean withinDistance(int[] o) {
		int dist = o[2];
		int deltaX = o[0] - getPosition().getX(), deltaY = o[1] - getPosition().getY();
		return (deltaX <= (dist - 1) && deltaX >= -dist && deltaY <= (dist - 1) && deltaY >= -dist);
	}

	public void refreshSkill(Skills skill) {
		try {
			int out = Skills.getLevelForExperience(getExperience(skill));
			if (skill == Skills.HITPOINTS) out = getCurrentHealth();
			else if (skill == Skills.PRAYER) out = getCurrentPrayer();
			else if(boostedLevel[skill.getId()] != 0) out += boostedLevel[skill.getId()];
			setSkillLevel(skill, out, getExperience(skill));
			setLevel(out, skill);
			getOutputStream().createFrame(134);
			getOutputStream().writeByte(skill.getId());
			getOutputStream().writeDWord_v1(getExperience(skill));
			getOutputStream().writeByte(out);
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
			//System.out.println(skill.getName() + " XP: " + getExperience(skill));
		} catch (Exception e) {
			//e.printStackTrace();
			//TODO: Fix this
		}
	}

	public int getbattleTimer(int weapon) {
		String wep = GetItemName(weapon).toLowerCase();
		int speed = 4;
		if (wep.contains("dart") || wep.contains("knife"))
			speed = 2;
		else if (wep.contains("wolfbane"))
			speed = 3;
		else if (wep.contains("longsword") || wep.contains("mace") || wep.contains("axe") && !wep.contains("dharok")
				|| wep.contains("spear") || wep.contains("tzhaar-ket-em") || wep.contains("torag") || wep.contains("guthan")
				|| wep.contains("verac") || wep.contains("staff") && !wep.contains("ahrim") || wep.contains("composite")
				|| wep.contains("crystal") || wep.contains("thrownaxe") || wep.contains("longbow"))
			speed = 5;
		else if (wep.contains("battleaxe") || wep.contains("warhammer") || wep.contains("godsword")
				|| wep.contains("barrelchest") || wep.contains("ahrim") || wep.contains("toktz-mej-tal")
				|| wep.contains("dorgeshuun") || wep.contains("crossbow") || wep.contains("javelin"))
			speed = 6;
		else if (wep.contains("2h sword") || wep.contains("halberd") || wep.contains("maul") || wep.contains("balmung")
				|| wep.contains("tzhaar-ket-om") || wep.contains("dharok"))
			speed = 7;
		return (int) ((speed * 1000) * 0.6);
	}

	public void CheckGear() {
		boolean foundStaff = false;
		for (int a = 0; a < staffs.length && !foundStaff; a++) {
			if (getEquipment()[Equipment.Slot.WEAPON.getId()] == staffs[a])
				foundStaff = true;
		}
		if (!foundStaff)
			autocast_spellIndex = -1;
		checkBow();
	}

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getPosition().getX() - pointX, 2) + Math.pow(getPosition().getY() - pointY, 2));
	}

	public void animation(int id, Position pos) {
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Client person = (Client) p;
				if (person.distanceToPoint(pos.getX(), pos.getY()) <= 60 && pos.getZ() == getPosition().getZ())
					person.animation2(id, pos);
			}
		}
	}

	public void animation2(int id, Position pos) {
		send(new SetMap(pos));
		getOutputStream().createFrame(4);
		getOutputStream().writeByte(0);
		getOutputStream().writeWord(id);
		getOutputStream().writeByte(0);
		getOutputStream().writeWord(0);
	}

	public void stillgfx(int id, Position pos, int time) {
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Client person = (Client) p;
				if (person.distanceToPoint(pos.getX(), pos.getY()) <= 60 && getPosition().getZ() == pos.getZ())
					person.stillgfx2(id, pos, 0, time);
			}
		}
	}

	public void stillgfx(int id, int y, int x) {
		stillgfx(id, new Position(x, y, getPosition().getZ()), 0);
	}

	public void stillgfx2(int id, Position pos, int height, int time) {
		send(new SetMap(pos));
		getOutputStream().createFrame(4);
		getOutputStream().writeByte(0); // Tiles away (X >> 4 + Y & 7)
		getOutputStream().writeWord(id); // Graphic id
		getOutputStream().writeByte(height);
		// height of the spell above it's basic place, i think it's written in pixels 100 pixels higher
		getOutputStream().writeWord(time); // Time before casting the graphic
	}
	// ActionTimer

	public void createProjectile(int casterY, int casterX, int offsetY,
								 int offsetX, int angle, int speed, int gfxMoving, int startHeight,
								 int endHeight, int MageAttackIndex, int begin, int slope, int initDistance) {
		try {
			send(new SetMap(new Position(casterY, casterX)));
			getOutputStream().createFrame(117);
			getOutputStream().writeByte(angle); // Starting place of the projectile
			getOutputStream().writeByte(offsetY); // Distance between caster and enemy
			// Y
			getOutputStream().writeByte(offsetX); // Distance between caster and enemy
			// X
			getOutputStream().writeWord(MageAttackIndex); // The NPC the missle is
			// locked on to
			getOutputStream().writeWord(gfxMoving); // The moving graphic ID
			getOutputStream().writeByte(startHeight); // The starting height
			getOutputStream().writeByte(endHeight); // Destination height
			getOutputStream().writeWord(begin); // Time the missle is created
			getOutputStream().writeWord(speed); // Speed minus the distance making it
			// set
			getOutputStream().writeByte(slope); // Initial slope
			getOutputStream().writeByte(initDistance); // Initial distance from source (in the
			// direction of the missile) //64
		} catch (Exception e) {
			Server.logError(e.getMessage());
		}
	}

	public void arrowGfx(int offsetY, int offsetX, int angle, int speed,
						 int gfxMoving, int startHeight, int endHeight, int index, int begin, int slope) {
		for (int a = 0; a < Constants.maxPlayers; a++) {
			Client projCheck = (Client) PlayerHandler.players[a];
			if (projCheck != null && projCheck.dbId > 0 && projCheck.getPosition().getX() > 0 && !projCheck.disconnected
					&& Math.abs(getPosition().getX() - projCheck.getPosition().getX()) <= 60
					&& Math.abs(getPosition().getY() - projCheck.getPosition().getY()) <= 60) {
				projCheck.createProjectile(getPosition().getY(), getPosition().getX(), offsetY, offsetX, angle, speed, gfxMoving,
						startHeight, endHeight, index, begin, slope, 64);
			}
		}
	}

	public void println_debug(String str) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		System.out.println("[" + timestamp + "] [client-" + getSlot() + "-" + getPlayerName() + "]: " + str);
	}

	public void println(String str) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		System.out.println("[" + timestamp + "] [client-" + getSlot() + "-" + getPlayerName() + "]: " + str);
	}

	public void rerequestAnim() {
		requestAnim(-1, 0);
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
	}

	public void sendFrame200(int MainFrame, int SubFrame) {
		getOutputStream().createFrame(200);
		getOutputStream().writeWord(MainFrame);
		getOutputStream().writeWord(SubFrame);
		flushOutStream();
	}

	public void sendFrame164(int Frame) {
		getOutputStream().createFrame(164);
		getOutputStream().writeWordBigEndian_dup(Frame);
		flushOutStream();
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		getOutputStream().createFrame(246);
		getOutputStream().writeWordBigEndian(MainFrame);
		getOutputStream().writeWord(SubFrame);
		getOutputStream().writeWord(SubFrame2);
		flushOutStream();
	}

	public void sendFrame185(int Frame) {
		getOutputStream().createFrame(185);
		getOutputStream().writeWordBigEndianA(Frame);
		flushOutStream();
	}

	public void sendQuestSomething(int id) {
		getOutputStream().createFrame(79);
		getOutputStream().writeWordBigEndian(id);
		getOutputStream().writeWordA(0);
		flushOutStream();
	}

	public void clearQuestInterface() {
		for (int j : QuestInterface)
			send(new SendString("", j));
	}

	public void showInterface(int interfaceid) {
		resetAction();
		getOutputStream().createFrame(97);
		getOutputStream().writeWord(interfaceid);
		flushOutStream();
	}

	public Client(java.net.Socket s, int _playerId) {
		super(_playerId);
		try {
			in = s.getInputStream();
			out = s.getOutputStream();
		} catch (java.io.IOException ioe) {
			System.out.println("Dodian (1): Exception!");
			System.out.println(ioe.getMessage());
		}
		mySock = s;
		mySocketHandler = new SocketHandler(this, s);
		outputStream = new Stream(new byte[bufferSize]);
		outputStream.currentOffset = 0;
		inputStream = new Stream(new byte[bufferSize]);
		inputStream.currentOffset = 0;
		buffer = new byte[bufferSize];
		readPtr = writePtr = 0;
	}

	public void shutdownError(String errorMessage) {
		Utils.println(": " + errorMessage);
		destruct();
	}

	public void destruct() {
		if (mySock == null) {
			return;
		} // already shutdown
		try {
			if (saveNeeded && !tradeSuccessful) { //Attempt to fix a potential dupe?
				saveStats(true, true);
			}
			disconnected = true;
			mySock.close();
			mySock = null;
			mySocketHandler = null;
			inputStream = null;
			outputStream = null;
			isActive = false;
			buffer = null;
		} catch (java.io.IOException ioe) {
			System.out.println("something wrong with the destruct!");
		}
		super.destruct();
	}

	public Stream getInputStream() {
		return this.inputStream;
	}

	public Stream getOutputStream() {
		return this.outputStream;
	}

	public void send(OutgoingPacket packet) {
		packet.send(this);
	}

	// writes any data in outStream to the relaying buffer
	public void flushOutStream() {
		if (disconnected || getOutputStream().currentOffset == 0) {
			return;
		}
		Integer length = getOutputStream().currentOffset;
		//int length = getOutputStream().currentOffset;
		byte[] copy = new byte[length];
		System.arraycopy(getOutputStream().buffer, 0, copy, 0, copy.length);
		mySocketHandler.queueOutput(copy);
		getOutputStream().currentOffset = 0;
	}

	// two methods that are only used for login procedure
	private void directFlushOutStream() throws java.io.IOException {
		mySocketHandler.getOutput().write(getOutputStream().buffer, 0, getOutputStream().currentOffset);
		getOutputStream().currentOffset = 0; // reset
	}

	private void fillInStream(int forceRead) throws java.io.IOException {
		//getInputStream().currentOffset = 0;
		inputStream.currentOffset = 0;
		mySocketHandler.getInput().read(getInputStream().buffer, 0, forceRead);
	}

	private void fillInStream(PacketData pData) throws java.io.IOException {
		getInputStream().currentOffset = 0;
		getInputStream().buffer = pData.getData();
		currentPacket = pData;
	}

	public void run() {
		// we just accepted a new connection - handle the login stuff
		isActive = false;
		long serverSessionKey, clientSessionKey;

		// randomize server part of the session key
		serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32)
				+ (long) (java.lang.Math.random() * 99999999D);

		try {
			returnCode = 2;
			fillInStream(2);
			if (getInputStream().readUnsignedByte() != 14) {
				shutdownError("Expected login Id 14 from client.");
				disconnected = true;
				return;
			}
			getInputStream().readUnsignedByte();
			for (int i = 0; i < 8; i++) {
				mySocketHandler.getOutput().write(9 + getGameWorldId());
			}
			mySocketHandler.getOutput().write(0);
			//out.write(0);
			getOutputStream().writeQWord(serverSessionKey);
			directFlushOutStream();
			fillInStream(2);
			int loginType = getInputStream().readUnsignedByte(); // this is either 16
			if (loginType != 16 && loginType != 18) {
				shutdownError("Unexpected login type " + loginType);
				return;
			}
			int loginPacketSize = getInputStream().readUnsignedByte();
			int loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2); // the
			if (loginEncryptPacketSize <= 0) {
				shutdownError("Zero RSA packet size!");
				return;
			}
			fillInStream(loginPacketSize);
			if (getInputStream().readUnsignedByte() != 255 || getInputStream().readUnsignedWord() != 317) {
				returnCode = 6;
			}
			getInputStream().readUnsignedByte();
			for (int i = 0; i < 9; i++) { //Client shiet?!
				//getInputStream().readDWord();
				Integer.toHexString(getInputStream().readDWord());
			}

			loginEncryptPacketSize--; // don't count length byte
			int tmp = getInputStream().readUnsignedByte();
			if (loginEncryptPacketSize != tmp) {
				shutdownError("Encrypted packet data length (" + loginEncryptPacketSize + ") different from length byte thereof (" + tmp + ")");
				return;
			}
			tmp = getInputStream().readUnsignedByte();
			if (tmp != 10) {
				shutdownError("Encrypted packet Id was " + tmp + " but expected 10");
				return;
			}
			clientSessionKey = getInputStream().readQWord();
			serverSessionKey = getInputStream().readQWord();

			String customClientVersion = getInputStream().readString();
			officialClient = customClientVersion.equals(getGameClientCustomVersion());
			setPlayerName(getInputStream().readString());
			if (getPlayerName() == null || getPlayerName().isEmpty()) {
				setPlayerName("player" + getSlot());
			}
			playerPass = getInputStream().readString();
			String playerServer;
			try {
				playerServer = getInputStream().readString();
			} catch (Exception e) {
				playerServer = "play.dodian.com";
			}
			setPlayerName(getPlayerName().toLowerCase());
			// playerPass = playerPass.toLowerCase();
			// System.out.println("valid chars");
			char[] validChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
					's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
					'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
					'_', ' '};
			setPlayerName(getPlayerName().trim());
			int[] sessionKey = new int[4];

			sessionKey[0] = (int) (clientSessionKey >> 32);
			sessionKey[1] = (int) clientSessionKey;
			sessionKey[2] = (int) (serverSessionKey >> 32);
			sessionKey[3] = (int) serverSessionKey;
			inStreamDecryption = new Cryption(sessionKey);
			for (int i = 0; i < 4; i++) {
				sessionKey[i] += 50;
			}
			outStreamDecryption = new Cryption(sessionKey);
			getOutputStream().packetEncryption = outStreamDecryption;

			int letters = 0;
			for (int i = 0; i < getPlayerName().length(); i++) {
				boolean valid = false;
				for (char validChar : validChars) {
					if (getPlayerName().charAt(i) == validChar) {
						valid = true;
						// break;
					}
					if (valid && getPlayerName().charAt(i) != '_' && getPlayerName().charAt(i) != ' ') {
						letters++;
					}
				}
				if (!valid) {
					returnCode = 4;
					disconnected = true;
				}
			}
			if (letters < 1) {
				returnCode = 3;
				disconnected = true;
			}
			char first = getPlayerName().charAt(0);
			properName = Character.toUpperCase(first) + getPlayerName().substring(1).toLowerCase();
			setPlayerName(properName.replace("_", " "));
			longName = Utils.playerNameToInt64(getPlayerName());
			if (Server.updateRunning && (Server.updateStartTime + (Server.updateSeconds * 1000L)) - System.currentTimeMillis() < 60000) { //Checks if update!
				returnCode = 14;
				disconnected = true;
			}
			int loadgame = Server.loginManager.loadgame(this, getPlayerName(), playerPass);
			switch (playerGroup) {
				case 6: // root admin
					playerRights = 2;
					break;
				case 1:
				case 3:
					playerRights = 1;
					break;
				default:
					if(playerGroup == 0)
						Server.loginManager.updatePlayerForumRegistration(this);
					playerRights = 0;
			}
			for (int i = 0; i < getEquipment().length; i++) {
				if (getEquipment()[i] == 0) {
					getEquipment()[i] = -1;
					getEquipmentN()[i] = 0;
				}
			}
			if (loadgame == 0 && returnCode != 6) {
				validLogin = true;
				if (getPosition().getX() > 0 && getPosition().getY() > 0) {
					teleportToX = getPosition().getX();
					teleportToY = getPosition().getY();
				}
			} else {
				if (returnCode != 6 && returnCode != 5)
					returnCode = loadgame;
				setPlayerName("_");
				disconnected = true;
				teleportToX = 0;
				teleportToY = 0;
			}
			if (getSlot() == -1) {
				mySocketHandler.getOutput().write(7);
			} else if (playerServer.equals("INVALID")) {
				mySocketHandler.getOutput().write(10);
			} else {
				if (mySocketHandler.getOutput() != null)
					mySocketHandler.getOutput().write(returnCode); // login response (1: wait 2seconds,
					// 2=login successfull, 4=ban :-)
				else
					returnCode = 21;
				if (returnCode == 21)
					mySocketHandler.getOutput().write(loginDelay);
			}

			mySocketHandler.getOutput().write(getGameWorldId() > 1 && playerRights < 2 ? 2 : playerRights); // mod level
			mySocketHandler.getOutput().write(0);
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		} catch (java.lang.Exception __ex) {
			destruct();
			return;
		}
		if (getSlot() == -1 || returnCode != 2) {
			return;
		}
		isActive = true;
		Thread mySocketThread = Server.createNewConnection(mySocketHandler);
		mySocketThread.start();
		packetSize = 0;
		packetType = -1;
		readPtr = 0;
		writePtr = 0;
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
	}

	public void setSidebarInterface(int menuId, int form) {
		getOutputStream().createFrame(71);
		getOutputStream().writeWord(form);
		getOutputStream().writeByteA(menuId);
	}

	public void setSkillLevel(Skills skill, int currentLevel, int XP) {
		send(new SendString(String.valueOf(Math.max(currentLevel, 0)), skill.getCurrentComponent()));
		send(new SendString(String.valueOf(Math.max(Skills.getLevelForExperience(XP), 1)), skill.getLevelComponent()));
	}

	public void logout() {
		// declineDuel();
		if (!saveNeeded) {
			return;
		}
		if (UsingAgility) {
			xLog = true;
			return;
		}
		if (!validClient) {
			return;
		}
		saveStats(true, true);
		saveNeeded = false;
		//ConnectionList.getInstance().remove(mySock.getInetAddress());
		send(new SendMessage("Please wait... logging out may take time"));
		send(new SendString("     Please wait...", 2458));
		//saveStats(true, true);
		send(new SendString("Click here to logout", 2458));
		getOutputStream().createFrame(109);
		loggingOut = true;
	}

	/*
	 * public void saveStats(boolean logout){ server.login.saveStats(this,
	 * logout); if(logout){ long elapsed = System.currentTimeMillis() -
	 * session_start; server.login.sendSession(dbId, clientPid, elapsed,
	 * connectedFrom); } }
	 */

	public void saveStats(boolean logout, boolean updateProgress) {
		/*if (!loadingDone || !validLogin) {
			return;
		}*/
		if (loginDelay > 0) {
			println("Incomplete login, aborting save");
			return;
		}
		if (!loadingDone || !validLogin) {
			return;
		}
		if (getPlayerName() == null || getPlayerName().equals("null") || dbId < 1) {
			saveNeeded = false;
			return;
		}
		if (getPlayerName().indexOf("'") > 0 || playerPass.indexOf("`") > 0) {
			println("Invalid player name");
			return;
		}
		if (logout) {
			saving = true;
      /*for (Player p : PlayerHandler.players) {
        if (p != null && !p.disconnected && p.dbId > 0) {
          if (p.getDamage().containsKey(getSlot())) {
            p.getDamage().put(getSlot(), 0);
          }
        }
      }*/ //TODO: Fix this pvp shiet
			if (getGameWorldId() < 2) {
				long elapsed = System.currentTimeMillis() - start;
				int minutes = (int) (elapsed / 60000);
				Server.login.sendSession(dbId, officialClient ? 1 : 1337, minutes, connectedFrom, start, System.currentTimeMillis());
			}
			//System.out.println("exorth save!");
			PlayerHandler.playersOnline.remove(longName);
			PlayerHandler.allOnline.remove(longName);
			for (Client c : PlayerHandler.playersOnline.values()) {
				if (c.hasFriend(longName)) {
					c.refreshFriends();
				}
			}
			if(inTrade) declineTrade();
			else if(inDuel && !duelFight) declineDuel();
			else if (duel_with > 0 && validClient(duel_with) && inDuel && duelFight) {
				Client p = getClient(duel_with);
				p.duelWin = true;
				p.DuelVictory();
			}
		}
		// TODO: Look into improving this, and potentially a system to configure player saving per world id...
		if (getGameWorldId() < 2 || getPlayerName().toLowerCase().startsWith("Admin"))
			try {
				Statement statement = getDbConnection().createStatement();
				long allXp = Skills.enabledSkills()
						.mapToInt(this::getExperience)
						.sum();

				int totalLevel = totalLevel();
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				StringBuilder query = new StringBuilder("UPDATE " + DbTables.GAME_CHARACTERS_STATS + " SET total=" + totalLevel + ", combat=" + determineCombatLevel() + ", ");
				StringBuilder query2 = new StringBuilder("INSERT INTO " + DbTables.GAME_CHARACTERS_STATS_PROGRESS + " SET updated='" + timeStamp + "', total=" + totalLevel + ", combat=" + determineCombatLevel() + ", uid=" + dbId + ", ");
				Skills.enabledSkills().forEach(skill -> {
					query.append(skill.getName()).append("=").append(getExperience(skill)).append(", ");
					query2.append(skill.getName()).append("=").append(getExperience(skill)).append(", ");
				});

				query.append("totalxp=").append(allXp).append(" WHERE uid=").append(dbId);
				query2.append("totalxp=").append(allXp);

				statement.executeUpdate(query.toString());
				if (updateProgress) statement.executeUpdate(query2.toString());
				System.currentTimeMillis();
				StringBuilder inventory = new StringBuilder();
				StringBuilder equipment = new StringBuilder();
				StringBuilder bank = new StringBuilder();
				StringBuilder list = new StringBuilder();
				StringBuilder boss_log = new StringBuilder();
				StringBuilder prayer = new StringBuilder();
				StringBuilder boosted = new StringBuilder();
				for (int i = 0; i < playerItems.length; i++) {
					if (playerItems[i] > 0) {
						inventory.append(i).append("-").append(playerItems[i] - 1).append("-").append(playerItemsN[i]).append(" ");
					}
				}
				for (int i = 0; i < bankItems.length; i++) {
					if (bankItems[i] > 0) {
						bank.append(i).append("-").append(bankItems[i] - 1).append("-").append(bankItemsN[i]).append(" ");
					}
				}
				for (int i = 0; i < getEquipment().length; i++) {
					if (getEquipment()[i] > 0) {
						equipment.append(i).append("-").append(getEquipment()[i]).append("-").append(getEquipmentN()[i]).append(" ");
					}
				}
				for (int i = 0; i < boss_name.length; i++) {
					if (boss_amount[i] >= 0) {
						boss_log.append(boss_name[i]).append(":").append(boss_amount[i]).append(" ");
					}
				}
				prayer.append(getCurrentPrayer());
				for(Prayers.Prayer pray : Prayers.Prayer.values()) {
					if(getPrayerManager().isPrayerOn(pray))
						prayer.append(":").append(pray.getButtonId());
				}
				boosted.append(lastRecover);
				for(int boost : boostedLevel.clone()) {
					boosted.append(":").append(boost);
				}
				int num = 0;
				for (Friend f : friends) {
					if (f.name > 0 && num < 200) {
						list.append(f.name).append(" ");
						num++;
					}
				}
				if (logout)
					saveNeeded = false;

				String last = "";
				long elapsed = System.currentTimeMillis() - session_start;
				if (elapsed > 10000) {
					last = ", lastlogin = '" + System.currentTimeMillis() + "'";
				}
				statement.executeUpdate("UPDATE " + DbTables.GAME_CHARACTERS + " SET uuid= '" + LoginManager.UUID + "', lastvote=" + lastVoted + ", pkrating=" + 1500 + ", health="
						+ getCurrentHealth() + ", equipment='" + equipment + "', inventory='" + inventory + "', bank='" + bank
						+ "', friends='" + list + "', fightStyle = " + FightType +", slayerData='" + saveTaskAsString() + "', essence_pouch='" + EssBags.getPouches(this) + "'" + ", coal_bag=" + coalBagAmount + ", autocast=" + autocast_spellIndex + ", news=" + latestNews + ", agility = '" + agilityCourseStage + "', height = " + getPosition().getZ() + ", x = " + getPosition().getX()
						+ ", y = " + getPosition().getY() + ", lastlogin = '" + System.currentTimeMillis() + "', Boss_Log='"
						+ boss_log + "', songUnlocked='" + getSongUnlockedSaveText() + "', travel='" + saveTravelAsString() + "', look='" + getLook() + "', unlocks='" + saveUnlocksAsString() + "'"
						+ ", prayer='"+prayer+"', boosted='"+boosted+"'" + last + " WHERE id = " + dbId);
				statement.close();
				//println_debug("Save:  " + getPlayerName() + " (" + (System.currentTimeMillis() - start) + "ms)");
			} catch (Exception e) {
				e.printStackTrace();
				println_debug("Save Exception: " + getSlot() + ", " + getPlayerName());
			}
	}

	public void saveStats(boolean logout) {
		saveStats(logout, false);
	}

	public void fromBank(int itemID, int fromSlot, int amount) {
		if (!IsBanking) {
			send(new RemoveInterfaces());
			return;
		}
		if (bankItems[fromSlot] - 1 != itemID || (bankItems[fromSlot] - 1 != itemID && bankItemsN[fromSlot] != amount)) {
			return;
		}
		int id = GetNotedItem(itemID);
		if (amount > 0) {
			if (bankItems[fromSlot] > 0) {
				if (!takeAsNote) {
					// if (Item.itemStackable[bankItems[fromSlot] - 1]) {
					if (Server.itemManager.isStackable(itemID)) {
						if (bankItemsN[fromSlot] > amount) {
							if (addItem((bankItems[fromSlot] - 1), amount)) {
								bankItemsN[fromSlot] -= amount;
								resetBank();
								resetItems(5064);
							}
						} else {
							if (addItem(itemID, bankItemsN[fromSlot])) {
								bankItems[fromSlot] = 0;
								bankItemsN[fromSlot] = 0;
								resetBank();
								resetItems(5064);
							}
						}
					} else {
						while (amount > 0) {
							if (bankItemsN[fromSlot] > 0) {
								if (addItem(itemID, 1)) {
									bankItemsN[fromSlot] -= 1;
									amount--;
								} else {
									amount = 0;
								}
							} else {
								amount = 0;
							}
						}
						resetBank();
						resetItems(5064);
					}
				} else if (id > 0) {
					if (bankItemsN[fromSlot] > amount) {
						if (addItem(id, amount)) {
							bankItemsN[fromSlot] -= amount;
							resetBank();
							resetItems(5064);
						}
					} else {
						if (addItem(id, bankItemsN[fromSlot])) {
							bankItems[fromSlot] = 0;
							bankItemsN[fromSlot] = 0;
							resetBank();
							resetItems(5064);
						}
					}
				} else {
					send(new SendMessage(Server.itemManager.getName(itemID) + " can't be drawn as note."));
					if (Server.itemManager.isStackable(itemID)) {
						if (bankItemsN[fromSlot] > amount) {
							if (addItem(itemID, amount)) {
								bankItemsN[fromSlot] -= amount;
								resetBank();
								resetItems(5064);
							}
						} else {
							if (addItem(itemID, bankItemsN[fromSlot])) {
								bankItems[fromSlot] = 0;
								bankItemsN[fromSlot] = 0;
								resetBank();
								resetItems(5064);
							}
						}
					} else {
						while (amount > 0) {
							if (bankItemsN[fromSlot] > 0) {
								if (addItem(itemID, 1)) {
									bankItemsN[fromSlot] -= 1;
									amount--;
								} else {
									amount = 0;
								}
							} else {
								amount = 0;
							}
						}
						resetBank();
						resetItems(5064);
					}
				}
			}
		}
	}

	public int getInvAmt(int itemID) {
		int amt = 0;
		for (int slot = 0; slot < playerItems.length; slot++) {
			if (playerItems[slot] == (itemID + 1)) {
				amt += playerItemsN[slot];
			}
		}
		return amt;
	}

	public int getBankAmt(int itemID) {
		int slot = -1;
		for (int i = 0; i < playerBankSize && slot == -1; i++)
			if (bankItems[i] == itemID + 1)
				slot = i;
		return slot == -1 ? 0 : bankItemsN[slot];
	}

	public boolean giveExperience(int amount, Skills skill) {
		if (amount < 1)
			return false;
		if (randomed) {
			send(new SendMessage("You must answer the genie before you can gain experience!"));
			return false;
		}
		amount = amount * getGameMultiplierGlobalXp();
		int oldXP = getExperience(skill),
				newXP = Math.min(getExperience(skill) + amount, 200000000);
		int oldLevel = Skills.getLevelForExperience(oldXP), newLevel = Skills.getLevelForExperience(newXP);
		amount = oldXP < 200000000 && newXP == 200000000 ? 200000000 - oldXP : newXP == 200000000 ? 0 : amount;
		addExperience(amount, skill);
		int animation = -1;
		if(newLevel - oldLevel > 0) {
			animation = 199;
			if (newLevel == 99) {
				animation = 623;
				publicyell(getPlayerName() + " has just reached the max level for " + skill.getName() + "!");
			} else if (newLevel > 89)
				publicyell(getPlayerName() + "'s " + skill.getName() + " level is now " + newLevel + "!");
			send(new SendMessage("Congratulations, you just advanced " + (skill == Skills.ATTACK || skill == Skills.AGILITY ? "an" : "a") + " " + skill.getName() + " level."));
		}
		if (oldXP < 50000000 && newXP >= 50000000) { // 50 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 50 million experience!");
		}
		if (oldXP < 75000000 && newXP >= 75000000) { // 75 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 75 million experience!");
		}
		if (oldXP < 100000000 && newXP >= 100000000) { // 100 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 100 million experience!");
		}
		if (oldXP < 125000000 && newXP >= 125000000) { // 100 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 125 million experience!");
		}
		if (oldXP < 150000000 && newXP >= 150000000) { // 150 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 150 million experience!");
		}
		if (oldXP < 175000000 && newXP >= 175000000) { // 150 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached 175 million experience!");
		}
		if (oldXP < 200000000 && newXP >= 200000000) { // 200 million announcement!
			animation = 623;
			publicyell(getPlayerName() + "'s " + skill.getName() + " has just reached the maximum experience!");
		}
    /*double chance = (double)newXp / 1000000; //1 xp = 0.000001, 0.0001 %
    double roll = Math.random() * 1;
    if(getGameWorldId() > 1)
    	send(new SendMessage("XP gained: "+ newXp +", your chance is " + chance * 100 + "% and you rolled a " + roll * 100));
    if(roll <= chance && getGameWorldId() > 1) //Test world 2 or higher for this to trigger!
    	Balloons.triggerBalloonEvent(this);*/
		setLevel(Skills.getLevelForExperience(getExperience(skill)), skill);
		refreshSkill(skill);
		if(skill == Skills.FIREMAKING)
			updateBonus(11);
		if(skill == Skills.HITPOINTS && newLevel > maxHealth)
			maxHealth = newLevel;
		else if(skill == Skills.PRAYER && newLevel > maxPrayer)
			maxPrayer = newLevel;
		if(animation != -1)
			animation(animation, getPosition());
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		return true;
	}

	public boolean bankItem(int itemID, int fromSlot, int amount) {
		if (playerItemsN[fromSlot] <= 0 || playerItems[fromSlot] <= 0 || playerItems[fromSlot] - 1 != itemID) {
			return false;
		}
		int id = GetUnnotedItem(itemID);
		if (id == 0) {
			if (playerItems[fromSlot] <= 0) {
				return false;
			}
			if (Server.itemManager.isStackable(itemID) || playerItemsN[fromSlot] > 1) {
				int toBankSlot = 0;
				boolean alreadyInBank = false;

				for (int i = 0; i < playerBankSize; i++) {
					if (bankItems[i] - 1 == itemID) { //Bank starts at value 0 while items should start at -1!
						if (playerItemsN[fromSlot] < amount) {
							amount = playerItemsN[fromSlot];
						}
						alreadyInBank = true;
						toBankSlot = i;
						i = playerBankSize + 1;
					}
				}

				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < playerBankSize; i++) {
						if (bankItems[i] <= 0) {
							toBankSlot = i;
							i = playerBankSize + 1;
						}
					}
					bankItems[toBankSlot] = itemID + 1; //To continue on comment above..Dodian thing :D
					if (playerItemsN[fromSlot] < amount) {
						amount = playerItemsN[fromSlot];
					}
					if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
						bankItemsN[toBankSlot] += amount;
					} else {
						send(new SendMessage("Bank full!"));
						return false;
					}
					deleteItem(itemID, fromSlot, amount);
					resetItems(5064);
					resetBank();
					return true;
				} else if (alreadyInBank) {
					if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
						bankItemsN[toBankSlot] += amount;
					} else {
						send(new SendMessage("Bank full!"));
						return false;
					}
					deleteItem(itemID, fromSlot, amount);
					resetItems(5064);
					resetBank();
					return true;
				} else {
					send(new SendMessage("Bank full!"));
					return false;
				}
			} else {
				itemID = playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank = false;

				for (int i = 0; i < playerBankSize; i++) {
					if (bankItems[i] == playerItems[fromSlot]) {
						alreadyInBank = true;
						toBankSlot = i;
						i = playerBankSize + 1;
					}
				}
				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < playerBankSize; i++) {
						if (bankItems[i] <= 0) {
							toBankSlot = i;
							i = playerBankSize + 1;
						}
					}
					int firstPossibleSlot = 0;
					boolean itemExists = false;

					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < playerItems.length; i++) {
							if ((playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							bankItems[toBankSlot] = playerItems[firstPossibleSlot];
							bankItemsN[toBankSlot] += 1;
							deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetItems(5064);
					resetBank();
					return true;
				} else if (alreadyInBank) {
					int firstPossibleSlot = 0;
					boolean itemExists = false;

					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < playerItems.length; i++) {
							if ((playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							bankItemsN[toBankSlot] += 1;
							deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetItems(5064);
					resetBank();
					return true;
				} else {
					send(new SendMessage("Bank full!"));
					return false;
				}
			}
		} else if (id > 0) {
			if (playerItems[fromSlot] <= 0) {
				return false;
			}
			if (Server.itemManager.isStackable(playerItems[fromSlot] - 1) || playerItemsN[fromSlot] > 1) {
				int toBankSlot = 0;
				boolean alreadyInBank = false;
				for (int i = 0; i < playerBankSize; i++) {
					if (bankItems[i] == GetUnnotedItem(playerItems[fromSlot] - 1) + 1) {
						if (playerItemsN[fromSlot] < amount) {
							amount = playerItemsN[fromSlot];
						}
						alreadyInBank = true;
						toBankSlot = i;
						i = playerBankSize + 1;
					}
				}
				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < playerBankSize; i++) {
						if (bankItems[i] <= 0) {
							toBankSlot = i;
							i = playerBankSize + 1;
						}
					}
					bankItems[toBankSlot] = id + 1;
					if (playerItemsN[fromSlot] < amount) {
						amount = playerItemsN[fromSlot];
					}
					if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
						bankItemsN[toBankSlot] += amount;
					} else {
						return false;
					}
					deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
					resetItems(5064);
					resetBank();
					return true;
				} else if (alreadyInBank) {
					if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
						bankItemsN[toBankSlot] += amount;
					} else {
						return false;
					}
					deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
					resetItems(5064);
					resetBank();
					return true;
				} else {
					send(new SendMessage("Bank full!"));
					return false;
				}
			} else {
				itemID = playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank = false;

				for (int i = 0; i < playerBankSize; i++) {
					if (bankItems[i] == (playerItems[fromSlot] - 1)) {
						alreadyInBank = true;
						toBankSlot = i;
						i = playerBankSize + 1;
					}
				}
				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < playerBankSize; i++) {
						if (bankItems[i] <= 0) {
							toBankSlot = i;
							i = playerBankSize + 1;
						}
					}
					int firstPossibleSlot = 0;
					boolean itemExists = false;

					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < playerItems.length; i++) {
							if ((playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							bankItems[toBankSlot] = (playerItems[firstPossibleSlot] - 1);
							bankItemsN[toBankSlot] += 1;
							deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetItems(5064);
					resetBank();
					return true;
				} else if (alreadyInBank) {
					int firstPossibleSlot = 0;
					boolean itemExists = false;

					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < playerItems.length; i++) {
							if ((playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							bankItemsN[toBankSlot] += 1;
							deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetItems(5064);
					resetBank();
					return true;
				} else {
					send(new SendMessage("Bank full!"));
					return false;
				}
			}
		} else {
			send(new SendMessage("Item not supported " + itemID));
			return false;
		}
	}

	public void resetItems(int WriteFrame) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(WriteFrame);
		getOutputStream().writeWord(playerItems.length);
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItemsN[i] > 254) {
				getOutputStream().writeByte(255); // item's stack count. if over 254,
				// write byte 255
				getOutputStream().writeDWord_v2(playerItemsN[i]); // and then the real
				// value with writeDWord_v2
			} else {
				getOutputStream().writeByte(playerItemsN[i]);
			}
			if (playerItems[i] < 0) {
				playerItems[i] = -1;
			}
			getOutputStream().writeWordBigEndianA(playerItems[i]); // item id
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void sendInventory(int interfaceId, ArrayList<GameItem> inv) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(interfaceId); // bank
		getOutputStream().writeWord(inv.size()); // number of items
		for (GameItem item : inv) {
			int amt = item.getAmount();
			int id = item.getId();
			if (amt > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord_v2(amt);
			} else {
				getOutputStream().writeByte(amt); // amount
			}
			getOutputStream().writeWordBigEndianA(id + 1); // itemID
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void resetOTItems(int WriteFrame) {
		Client other = getClient(trade_reqId);
		if (!validClient(trade_reqId)) {
			return;
		}
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(WriteFrame);
		int len = other.offeredItems.toArray().length;
		int current = 0;
		getOutputStream().writeWord(len);
		for (GameItem item : other.offeredItems) {
			if (item.getAmount() > 254) {
				getOutputStream().writeByte(255);
				// item's stack count. if over 254, write byte 255
				getOutputStream().writeDWord_v2(item.getAmount());
				// and then the real value with writeDWord_v2
			} else {
				getOutputStream().writeByte(item.getAmount());
			}
			getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				getOutputStream().writeByte(1);
				getOutputStream().writeWordBigEndianA(-1);
			}
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void resetTItems(int WriteFrame) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(WriteFrame);
		int len = offeredItems.toArray().length;
		int current = 0;
		getOutputStream().writeWord(len);
		for (GameItem item : offeredItems) {
			if (item.getAmount() > 254) {
				getOutputStream().writeByte(255);
				// item's stack count. if over 254, write byte 255
				getOutputStream().writeDWord_v2(item.getAmount());
				// and then the real value with writeDWord_v2
			} else {
				getOutputStream().writeByte(item.getAmount());
			}
			getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				getOutputStream().writeByte(1);
				getOutputStream().writeWordBigEndianA(-1);
			}
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void resetShop(int ShopID) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(3900);
		getOutputStream().writeWord(ShopHandler.MaxShopItems);

		for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
			if (ShopHandler.ShopItems[ShopID][i] > 0) {
				if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
					getOutputStream().writeByte(255); // item's stack count. if over
					getOutputStream().writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i]); // and
				} else {
					getOutputStream().writeByte(ShopHandler.ShopItemsN[ShopID][i]);
				}
				getOutputStream().writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i]); // item
			} else { //If nothing to buy!
				getOutputStream().writeByte(0);
				getOutputStream().writeWordBigEndianA(0);
			}
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void resetBank() {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(5382); // bank
		getOutputStream().writeWord(playerBankSize); // number of items
		for (int i = 0; i < playerBankSize; i++) {
			if (bankItemsN[i] > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord_v2(bankItemsN[i]);
			} else {
				getOutputStream().writeByte(bankItemsN[i]); // amount
			}
			if (bankItemsN[i] < 1) {
				bankItems[i] = 0;
			}
			if (bankItems[i] < 0) {
				bankItems[i] = 7500;
			}
			getOutputStream().writeWordBigEndianA(bankItems[i]); // itemID
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void sendBank(ArrayList<Integer> id, ArrayList<Integer> amt) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(5382); // bank
		getOutputStream().writeWord(id.size()); // number of items
		for (int i = 0; i < id.size(); i++) {
			if (amt.get(i) > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord_v2(amt.get(i));
			} else {
				getOutputStream().writeByte(amt.get(i)); // amount
			}
			getOutputStream().writeWordBigEndianA(id.get(i) + 1); // itemID
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void sendBank(int interfaceId, ArrayList<GameItem> bank) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(interfaceId); // bank
		getOutputStream().writeWord(bank.size()); // number of items
		for (GameItem item : bank) {
			int amt = item.getAmount();
			int id = item.getId();
			if (amt > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord_v2(amt);
			} else {
				getOutputStream().writeByte(amt); // amount
			}
			getOutputStream().writeWordBigEndianA(id + 1); // itemID
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void moveItems(int from, int to, int moveWindow) {
		if (moveWindow == 3214 || moveWindow == 5064) {
			int tempI = playerItems[to];
			int tempN = playerItemsN[to];
			playerItems[to] = playerItems[from];
			playerItemsN[to] = playerItemsN[from];
			playerItems[from] = tempI;
			playerItemsN[from] = tempN;
			resetItems(moveWindow);
		}
		if (moveWindow == 5382 && from >= 0 && to >= 0 && from < playerBankSize && to < playerBankSize) {
			int tempI = bankItems[from];
			int tempN = bankItemsN[from];
			bankItems[from] = bankItems[to];
			bankItemsN[from] = bankItemsN[to];
			bankItems[to] = tempI;
			bankItemsN[to] = tempN;
			resetBank();
		}
	}

	public int freeBankSlots() {
		int freeS = 0;

		for (int i = 0; i < playerBankSize; i++) {
			if (bankItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public int freeSlots() {
		int freeSlot = 0;

		for (int playerItem : playerItems) {
			if (playerItem <= 0) {
				freeSlot++;
			}
		}
		return freeSlot;
	}

	public void pickUpItem(int id, int x, int y) {
		boolean specialItems = (x == 2611 && y == 3096) || (x == 2612 && y == 3096) || (x == 2563 && y == 9511) || (x == 2564 && y == 9511);
		if (specialItems && playerRights < 2) {
			dropAllItems();
			return;
		}
		for (GroundItem item : Ground.items) {
			if (item.id == id && getPosition().getX() == x && getPosition().getY() == y && (item.visible || dbId == item.playerId)) {
				if (getPosition().getX() == item.x && getPosition().getY() == item.y) {
					if (item.isTaken())
						continue;
					if (addItem(item.id, item.amount)) { //Fixed so stackable items can be added with full inv as long as you got it.
						item.setTaken(true);
						Ground.deleteItem(item);
						//send(new Sound(356)); //TODO: fix sounds!
						PickupLog.recordPickup(this, item.id, item.amount, item.npc ? Server.npcManager.getName(item.npcId) : item.canDespawn ? "" + item.playerId : "Global Item Pickup", getPosition().copy());
					}
					break;
				}
			}
		}
	}

	public void openUpBank() {
		if (!Server.banking) {
			send(new SendMessage("Banking have been disabled!"));
			return;
		}
		resetAction(true);
		resetBank();
		resetItems(5064);
		send(new SendString("Note", 5391));
		send(new SendString("The Bank of Dodian", 5383));
		IsBanking = true;
		checkBankInterface = false;
		send(new InventoryInterface(5292, 5063));
	}

	public void openUpShop(int ShopID) {
		if (!Server.shopping) {
			send(new SendMessage("Shopping has been disabled!"));
			return;
		}
		send(new SendString(ShopHandler.ShopName[ShopID], 3901));
		send(new InventoryInterface(3824, 3822));
		resetItems(3823);
		resetShop(ShopID);
		IsShopping = true;
		MyShopID = ShopID;
	}

	private void checkItemUpdate() {
		if (IsShopping) {
			resetItems(3823);
		} else if (IsBanking || checkBankInterface) {
			resetItems(5064);
			send(new InventoryInterface(5292, 5063));
		} else if (isPartyInterface) {
			resetItems(5064);
			send(new InventoryInterface(2156, 5063));
		}
	}

	public boolean addItem(int item, int amount) {
		if (item < 0 || amount < 1) {
			return false;
		}
		amount = !Server.itemManager.isStackable(item) ? 1 : amount;
		if ((freeSlots() >= amount && !Server.itemManager.isStackable(item)) || freeSlots() > 0) {
			for (int i = 0; i < playerItems.length; i++) {
				if (playerItems[i] == (item + 1) && Server.itemManager.isStackable(item) && playerItems[i] > 0) {
					playerItems[i] = (item + 1);
					if ((playerItemsN[i] + amount) < maxItemAmount && (playerItemsN[i] + amount) > -1) {
						playerItemsN[i] += amount;
					} else {
						playerItemsN[i] = maxItemAmount;
					}
					getOutputStream().createFrameVarSizeWord(34);
					getOutputStream().writeWord(3214);
					getOutputStream().writeByte(i);
					getOutputStream().writeWord(playerItems[i]);
					if (playerItemsN[i] > 254) {
						getOutputStream().writeByte(255);
						getOutputStream().writeDWord(playerItemsN[i]);
					} else {
						getOutputStream().writeByte(playerItemsN[i]); // amount
					}
					getOutputStream().endFrameVarSizeWord();
					checkItemUpdate();
					return true;
				}
			}
			for (int i = 0; i < playerItems.length; i++) {
				if (playerItems[i] <= 0) {
					playerItems[i] = item + 1;
					playerItemsN[i] = Math.min(amount, maxItemAmount);
					getOutputStream().createFrameVarSizeWord(34);
					getOutputStream().writeWord(3214);
					getOutputStream().writeByte(i);
					getOutputStream().writeWord(playerItems[i]);
					if (playerItemsN[i] > 254) {
						getOutputStream().writeByte(255);
						getOutputStream().writeDWord(playerItemsN[i]);
					} else {
						getOutputStream().writeByte(playerItemsN[i]); // amount
					}
					getOutputStream().endFrameVarSizeWord();
					checkItemUpdate();
					return true;
				}
			}
			return false;
		} else if (contains(item) && Server.itemManager.isStackable(item)) {
			int slot = -1;
			for (int i = 0; i < playerItems.length; i++) {
				if (playerItems[i] == item + 1) {
					slot = i;
					break;
				}
			}
			if ((long) playerItemsN[slot] + (long) amount > (long) Integer.MAX_VALUE) {
				send(new SendMessage("Failed! Reached max item amount!"));
				return false;
			}
			playerItemsN[slot] = playerItemsN[slot] + amount;
			getOutputStream().createFrameVarSizeWord(34);
			getOutputStream().writeWord(3214);
			getOutputStream().writeByte(slot);
			getOutputStream().writeWord(playerItems[slot]);
			if (playerItemsN[slot] > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord(playerItemsN[slot]);
			} else {
				getOutputStream().writeByte(playerItemsN[slot]); // amount
			}
			getOutputStream().endFrameVarSizeWord();
			checkItemUpdate();
			return true;
		} else {
			send(new SendMessage("You don't have enough inventory space to withdraw that many."));
			return false;
		}
	}

	public void addItemSlot(int item, int amount, int slot) {
		item++;
		playerItems[slot] = item;
		playerItemsN[slot] = amount;
		getOutputStream().createFrameVarSizeWord(34);
		getOutputStream().writeWord(3214);
		getOutputStream().writeByte(slot);
		getOutputStream().writeWord(item);
		if (amount > 254) {
			getOutputStream().writeByte(255);
			getOutputStream().writeDWord(amount);
		} else {
			getOutputStream().writeByte(amount); // amount
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void dropItem(int id, int slot) {
		if (inTrade || inDuel || IsBanking) {
			return;
		}
		if (!Server.dropping) {
			send(new SendMessage("Dropping has been disabled.  Please try again later"));
			return;
		}
		send(new RemoveInterfaces()); //Need this to stop interface abuse
		int amount = 0;
		if (playerItems[slot] == (id + 1) && playerItemsN[slot] > 0) {
			amount = playerItemsN[slot];
		}
		if (amount < 1) {
			return;
		}
		/*
		 * if(dropTries < 1){ dropTries++; for(int i = 0; i < 2; i++){ sendMessage (
		 * "WARNING: dropping this item will DELETE it, not drop it"); send(new
		 * SendMessage( "To confirm, drop again"); return; } }
		 */
		//send(new Sound(376));
		deleteItem(id, slot, amount);
		GroundItem drop = new GroundItem(getPosition().copy(), id, amount, getSlot(), -1);
		Ground.items.add(drop);
		//DropLog.recordDrop(this, drop.id, drop.amount, "Player", getPosition().copy(), "Inventory Drop");
	}

	public void deleteItem(int id, int amount) {
		deleteItem(id, GetItemSlot(id), amount);
	}

	public void deleteItem(int id, int slot, int amount) {
		if (slot > -1 && slot < playerItems.length) {
			if ((playerItems[slot] - 1) == id) {
				if (playerItemsN[slot] > amount) {
					playerItemsN[slot] -= amount;
				} else {
					playerItemsN[slot] = 0;
					playerItems[slot] = 0;
				}
				resetItems(3214);
				if (IsBanking) {
					send(new InventoryInterface(5292, 5063)); // 5292
					resetItems(5064);
				}
			}
		}
	}

	public void deleteItemBank(int id, int slot, int amount) {
		if (slot > -1 && slot < bankItems.length) {
			if ((bankItems[slot] - 1) == id) {
				if (bankItemsN[slot] > amount) {
					bankItemsN[slot] -= amount;
				} else {
					bankItemsN[slot] = 0;
					bankItems[slot] = 0;
				}
				resetItems(3214);
				if (IsBanking) {
					send(new InventoryInterface(5292, 5063)); // 5292
					resetItems(5064);
				}
			}
		}
	}

	public void deleteItemBank(int id, int amount) {
		deleteItemBank(id, GetBankItemSlot(id), amount);
	}

	public void setEquipment(int wearID, int amount, int targetSlot) {
		getOutputStream().createFrameVarSizeWord(34);
		getOutputStream().writeWord(1688);
		getOutputStream().writeByte(targetSlot);
		getOutputStream().writeWord(wearID == -1 ? 0 : wearID + 1);
		if (amount > 254) {
			getOutputStream().writeByte(255);
			getOutputStream().writeDWord(amount);
		} else
			getOutputStream().writeByte(amount); // amount
		getOutputStream().endFrameVarSizeWord();
		if (targetSlot == Equipment.Slot.WEAPON.getId()) {
			CheckGear();
			//FightType = 0; //TODO cancel shared xp on wep change
			CombatStyleHandler.setWeaponHandler(this, -1);
			requestAnims(wearID);
		}
		GetBonus(true);
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
	}

	public boolean wear(int wearID, int slot, int Interface) {
		if (inTrade) {
			return false;
		}
		if (EssBags.emptyEssencePouch(wearID, this)) { //Runecrafting Pouches
			return false;
		}
		if (wearID == 5733) { //Potato
			return false;
		}
		if (wearID == 4155) { //Enchanted gem
			SlayerTask.slayerTasks checkTask = SlayerTask.slayerTasks.getTask(getSlayerData().get(1));
			if (checkTask != null && getSlayerData().get(3) > 0)
				send(new SendMessage("You need to kill " + getSlayerData().get(3) + " more " + checkTask.getTextRepresentation()));
			else
				send(new SendMessage("You need to be assigned a task!"));
			return false;
		}
		if (duelFight && duelRule[3]) {
			send(new SendMessage("Equipment changing has been disabled in this duel"));
			return false;
		}
		if (duelConfirmed && !duelFight)
			return false;
		if (!playerHasItem(wearID)) {
			return false;
		}
		int targetSlot = Server.itemManager.getSlot(wearID);
		if (targetSlot != 8 && duelBodyRules[falseSlots[targetSlot]]) {
			send(new SendMessage("Current duel rules restrict this from being worn!"));
			return false;
		}
		if ((playerItems[slot] - 1) == wearID) {
			if(!checkEquip(wearID, targetSlot, slot))
				return false;
			int wearAmount = playerItemsN[slot];
			if (wearAmount < 1) {
				return false;
			}
			if (wearID >= 0) {
				deleteItem(wearID, slot, wearAmount);
				if (getEquipment()[targetSlot] != wearID && getEquipment()[targetSlot] >= 0) {
					addItem(getEquipment()[targetSlot], getEquipmentN()[targetSlot]);
				} else if (Server.itemManager.isStackable(wearID) && getEquipment()[targetSlot] == wearID) {
					wearAmount = getEquipmentN()[targetSlot] + wearAmount;
				} else if (getEquipment()[targetSlot] >= 0) {
					addItem(getEquipment()[targetSlot], getEquipmentN()[targetSlot]);
				}
			}
			getEquipment()[targetSlot] = wearID;
			getEquipmentN()[targetSlot] = wearAmount;
			setEquipment(getEquipment()[targetSlot], getEquipmentN()[targetSlot], targetSlot);
			wearing = false;
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
			return true;
		}
		return false;
	}

	public boolean checkEquip(int id, int slot, int invSlot) {
		if ((id == 13280 || id == 13281) && totalLevel() < Skills.maxTotalLevel()) {
			send(new SendMessage("You need a total level of " + Skills.maxTotalLevel() + " to equip max cape and/or hood."));
			return false;
		}

		int CLAttack = GetCLAttack(id);
		int CLDefence = GetCLDefence(id);
		int CLStrength = GetCLStrength(id);
		int CLMagic = GetCLMagic(id);
		int CLRanged = GetCLRanged(id);
		boolean failCheck = false;
		String itemName = Server.itemManager.getName(id);
		if (CLAttack > Skills.getLevelForExperience(getExperience(Skills.ATTACK))) {
			send(new SendMessage("You need " + CLAttack + " Attack to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		if (CLDefence > Skills.getLevelForExperience(getExperience(Skills.DEFENCE))) {
			send(new SendMessage("You need " + CLDefence + " Defence to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		if (CLStrength > Skills.getLevelForExperience(getExperience(Skills.STRENGTH))) {
			send(new SendMessage("You need " + CLStrength + " Strength to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		if (CLMagic > Skills.getLevelForExperience(getExperience(Skills.MAGIC))) {
			send(new SendMessage("You need " + CLMagic + " Magic to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		if (CLRanged > Skills.getLevelForExperience(getExperience(Skills.RANGED))) {
			send(new SendMessage("You need " + CLRanged + " Ranged to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		if (Skills.getLevelForExperience(getExperience(Skills.AGILITY)) < 60 && id == 4224) {
			send(new SendMessage("You need 60 Agility to equip " + itemName.toLowerCase() + "."));
			failCheck = true;
		}
		Skillcape skillcape = Skillcape.getSkillCape(id);
		if (skillcape != null) {
			if (Skillcape.isTrimmed(id) && getExperience(skillcape.getSkill()) < 50000000) {
				send(new SendMessage("This cape requires 50 million " + skillcape.getSkill().getName() + " experience to wear."));
				failCheck = true;
			} else if (Skills.getLevelForExperience(getExperience(skillcape.getSkill())) < 99) {
				send(new SendMessage("This cape requires level 99 " + skillcape.getSkill().getName() + " to wear."));
				failCheck = true;
			}
		}
		/* 2handed check! */
		int shield = getEquipment()[Equipment.Slot.SHIELD.getId()];
		int weapon = getEquipment()[Equipment.Slot.WEAPON.getId()];
		boolean twoHanded = Server.itemManager.isTwoHanded(id) || Server.itemManager.isTwoHanded(weapon);
		if(twoHanded && !failCheck) {
			if (slot == Equipment.Slot.WEAPON.getId()) {
				if (shield > 0 && hasSpace()) {
					remove(slot, true);
					remove(Equipment.Slot.SHIELD.getId(), true);
					if(invSlot == -1)
						addItem(weapon, 1);
					else
						addItemSlot(weapon, 1, invSlot);
					addItem(shield, 1);
				} else if (shield > 0) {
					send(new SendMessage("Not enough space to equip this item!"));
					failCheck = true;
				}
			} else if (slot == Equipment.Slot.SHIELD.getId()) {
				remove(Equipment.Slot.WEAPON.getId(), true);
				if(invSlot == -1)
					addItem(weapon, 1);
				else
					addItemSlot(weapon, 1, invSlot);
			}
		}
		/* Bow check! */
		boolean check = (id == 4212 || id == 6724 || id == 20997) || (weapon == 4212 || weapon == 6724 || weapon == 20997);
		if(check && !failCheck) {
			if(slot == 5 && id != 3844 && id != 4224 && id != 1540) {
				if (shield > 0 && hasSpace()) {
					remove(slot, true);
					remove(Equipment.Slot.WEAPON.getId(), true);
					if(invSlot == -1)
						addItem(weapon, 1);
					else
						addItemSlot(weapon, 1, invSlot);
					addItem(shield, 1);
				} else if(shield > 0) {
					send(new SendMessage("Not enough space to equip this item!"));
					failCheck = true;
				} else if (shield < 1 && invSlot != -1) {
					remove(Equipment.Slot.WEAPON.getId(), true);
					addItemSlot(weapon, 1, invSlot);
				} else if (invSlot == -1) failCheck = true;
			}
			if(slot == 3 && invSlot != -1 && (id == 4212 || id == 6724 || id == 20997)) {
				boolean shieldCheck = shield == 3844 || shield == 4224 || shield == 1540;
				if(!shieldCheck && shield > 0 && weapon > 0 && hasSpace()) {
					remove(slot, true);
					remove(Equipment.Slot.SHIELD.getId(), true);
					addItemSlot(weapon, 1, invSlot);
					addItem(shield, 1);
				} else if(shield > 0 && weapon > 0 && !hasSpace()) {
					send(new SendMessage("Not enough space to equip this item!"));
					failCheck = true;
				} else if(shield > 0 && !shieldCheck) {
					remove(Equipment.Slot.SHIELD.getId(), true);
					addItemSlot(shield, 1, invSlot);
				}
			}
		}
		return !failCheck;
	}

	public boolean remove(int slot, boolean force) {
		if (duelFight && duelRule[3] && !force) {
			send(new SendMessage("Equipment changing has been disabled in this duel!"));
			return false;
		}
		if (duelConfirmed && !force) {
			return false;
		}
		getEquipment()[slot] = -1;
		getEquipmentN()[slot] = 0;
		setEquipment(getEquipment()[slot], getEquipmentN()[slot], slot);
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		return true;
	}

	public void deleteequiment(int wearID, int slot) {
		if (getEquipment()[slot] == wearID) {
			getEquipment()[slot] = -1;
			getEquipmentN()[slot] = 0;
			setEquipment(getEquipment()[slot], getEquipmentN()[slot], slot);
		}
	}

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
		getOutputStream().createFrame(206);
		getOutputStream().writeByte(publicChat); // On = 0, Friends = 1, Off = 2, Hide = 3
		getOutputStream().writeByte(privateChat); // On = 0, Friends = 1, Off = 2
		getOutputStream().writeByte(tradeBlock); // On = 0, Friends = 1, Off = 2
	}

	// upon connection of a new client all the info has to be sent to client
	// prior to starting the regular communication
	public void initialize() {
		getOutputStream().createFrame(249);
		getOutputStream().writeByteA(playerIsMember); // 1 = member, 0 = f2p
		getOutputStream().writeWordBigEndianA(getSlot());

		// here is the place for seting up the UI, stats, etc...
		setChatOptions(0, 0, 0);

		for (int i = 0; i < 21; i++) {
			refreshSkill(Skills.getSkill(i));
		}

		frame36(287, 1); //SPLIT PRIVATE CHAT ON/OFF
		/*
		 * for (int i = 0; i < 25; i++) { if(i != 3) setSkillLevel(i,
		 * playerLevel[i], playerXP[i]); }
		 */
		getOutputStream().createFrame(107); // resets something in the client
		setSidebarInterface(0, 2423); // attack tab
		setSidebarInterface(1, 24126); // skills tab //3917//24126
		setSidebarInterface(2, 638); // quest tab
		setSidebarInterface(3, 3213); // backpack tab
		setSidebarInterface(4, 1644); // items wearing tab
		setSidebarInterface(5, 5608); // pray tab
		setSidebarInterface(6, 12855); // magic tab (ancient = 12855)
		setSidebarInterface(7, -1); // ancient magicks
		setSidebarInterface(8, 5065); // friend
		setSidebarInterface(9, 5715); // ignore
		setSidebarInterface(10, 2449); // logout tab
		setSidebarInterface(11, 904); // wrench tab
		setSidebarInterface(12, 147); // run tab
		setSidebarInterface(13, 962); // harp tab
      /*if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 2518) {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(1);
        getOutputStream().writeString("Throw At");
        getOutputStream().endFrameVarSize();
      } else {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("null");
        getOutputStream().endFrameVarSize();
      }*/ //We want this for snowballs????
		//send(new SendMessage("Please vote! You can earn your reward by doing ::redeem "+getPlayerName()+" every 6hrs."));
//    send(new SendMessage("<col=CB1D1D>Santa has come! A bell MUST be rung to celebrate!!!"));
//    send(new SendMessage("<col=CB1D1D>Click it for a present!! =)"));
//    send(new SendMessage("@redPlease have one inventory space open! If you don't PM Logan.."));
		CheckGear();
		/* Set a player active to a world! */
		PlayerHandler.playersOnline.put(longName, this);
		PlayerHandler.allOnline.put(longName, getGameWorldId());
		/* Sets look! */
		if (lookNeeded) {
			defaultCharacterLook(this);
			showInterface(3559);
		} else
			setLook(playerLooks);
		/* Friend configs! */
		for (Client c : PlayerHandler.playersOnline.values()) {
			if (c.hasFriend(longName)) {
				c.refreshFriends();
			}
		}
		/* Login write settings! */
		frame36(287, 1);
		WriteEnergy();
		pmstatus(2);
		send(new SendString("", 6067));
		send(new SendString("", 6071));
		CombatStyleHandler.setWeaponHandler(this, -1);
		PlayerUpdating.getInstance().update(this, getOutputStream());

		setEquipment(getEquipment()[Equipment.Slot.HEAD.getId()], getEquipmentN()[Equipment.Slot.HEAD.getId()],
				Equipment.Slot.HEAD.getId());
		setEquipment(getEquipment()[Equipment.Slot.CAPE.getId()], getEquipmentN()[Equipment.Slot.CAPE.getId()],
				Equipment.Slot.CAPE.getId());
		setEquipment(getEquipment()[Equipment.Slot.NECK.getId()], getEquipmentN()[Equipment.Slot.NECK.getId()],
				Equipment.Slot.NECK.getId());
		setEquipment(getEquipment()[Equipment.Slot.ARROWS.getId()], getEquipmentN()[Equipment.Slot.ARROWS.getId()],
				Equipment.Slot.ARROWS.getId());
		setEquipment(getEquipment()[Equipment.Slot.CHEST.getId()], getEquipmentN()[Equipment.Slot.CHEST.getId()],
				Equipment.Slot.CHEST.getId());
		setEquipment(getEquipment()[Equipment.Slot.SHIELD.getId()], getEquipmentN()[Equipment.Slot.SHIELD.getId()],
				Equipment.Slot.SHIELD.getId());
		setEquipment(getEquipment()[Equipment.Slot.LEGS.getId()], getEquipmentN()[Equipment.Slot.LEGS.getId()],
				Equipment.Slot.LEGS.getId());
		setEquipment(getEquipment()[Equipment.Slot.HANDS.getId()], getEquipmentN()[Equipment.Slot.HANDS.getId()],
				Equipment.Slot.HANDS.getId());
		setEquipment(getEquipment()[Equipment.Slot.FEET.getId()], getEquipmentN()[Equipment.Slot.FEET.getId()],
				Equipment.Slot.FEET.getId());
		setEquipment(getEquipment()[Equipment.Slot.RING.getId()], getEquipmentN()[Equipment.Slot.RING.getId()],
				Equipment.Slot.RING.getId());
		setEquipment(getEquipment()[Equipment.Slot.WEAPON.getId()], getEquipmentN()[Equipment.Slot.WEAPON.getId()],
				Equipment.Slot.WEAPON.getId());
		setEquipment(getEquipment()[Equipment.Slot.BLESSING.getId()], getEquipmentN()[Equipment.Slot.BLESSING.getId()],
				Equipment.Slot.BLESSING.getId());
		/* Reset values for items*/
		resetItems(3214);
		resetBank();
		send(new SendString("Using this will send a notification to all online mods", 5967));
		send(new SendString("@yel@Then click below to indicate which of our rules is being broken.", 5969));
		send(new SendString("4: Bug abuse (includes noclip)", 5974));
		send(new SendString("5: Dodian staff impersonation", 5975));
		send(new SendString("6: Monster luring or abuse", 5976));
		send(new SendString("8: Item Duplication", 5978));
		send(new SendString("10: Misuse of yell channel", 5980));
		send(new SendString("12: Possible duped items", 5982));
		send(new SendString("Old magic", 12585));
		send(new SendString("", 6067));
		send(new SendString("", 6071));
		//RegionMusic.sendSongSettings(this); //Music from client 2.95
		/* Set configurations! */
		setConfigIds();
		/* Done loading in a character! */
		send(new SendMessage("Welcome to Dodian"));
		if (newPms > 0) {
			send(new SendMessage("You have " + newPms + " new messages.  Check your inbox at Dodian.net to view them."));
		}
		loaded = true;
		/* Update of both player and npc! */
		PlayerUpdating.getInstance().update(this, outputStream);
		NpcUpdating.getInstance().update(this, outputStream);
	}

	public void update() { //Update npc before player!
		PlayerUpdating.getInstance().update(this, outputStream);
		flushOutStream();
		NpcUpdating.getInstance().update(this, outputStream);
		flushOutStream();
	}

	public int packetSize = 0, packetType = -1;
	public boolean canAttack = true;

	public void process() {
		// is being called regularily every 600 ms RegionMusic.handleRegionMusic(this);
		if (mutedTill * 1000 <= rightNow) {
			send(new SendString(invis ? "You are invisible!" : "", 6572));
		} else {
			mutedHours = ((mutedTill * 1000) - rightNow) / (60 * 60 * 1000);
			send(new SendString("Muted: " + mutedHours + " hours", 6572));
		}
		if(getPositionName(getPosition()) == positions.BRIMHAVEN_DUNGEON) {
			boolean gotIcon = getEquipment()[Equipment.Slot.NECK.getId()] == 8923;
			if(iconTimer > 0 && !gotIcon) iconTimer--;
			else if(iconTimer == 0 && !gotIcon) { //Hit with dmg!
				send(new SendMessage("The strange aura from the dungeon makes you vulnerable!"));
				int dmg = 5 + Misc.random(15);
				this.dealDamage(dmg, dmg >= 15);
				iconTimer = 6;
			} else iconTimer = 6;
		} else
			iconTimer = 6;
		/* Prayer drain*/
		if(prayers.getDrainRate() > 0.0) {
			prayers.drainRate += 0.6;
			if(prayers.drainRate >= prayers.getDrainRate()) {
				drainPrayer(1);
				prayers.drainRate = 0.0;
			}
		} else prayers.drainRate = 0.0;
		/* Stat restore + drain */
		lastRecoverEffect++;
		if (lastRecoverEffect % 25 == 0) {
			lastRecover--;
			if (lastRecover == 0) {
				lastRecoverEffect = 0;
				lastRecover = 4;
				Skills.enabledSkills().forEach(skill -> {
					switch (skill) {
						case HITPOINTS:
							heal(1);
							break;
						case PRAYER:
							if (boostedLevel[skill.getId()] > 0)
								boostedLevel[skill.getId()]--;
							else boostedLevel[skill.getId()]++;
							break;
					}
					refreshSkill(skill);
				});
			}
		}
		if (reloadHp) {
			heal(getMaxHealth());
			setCurrentPrayer(getMaxPrayer());
		}
		// RubberCheck();
		QuestSend.questInterface(this);
		long now = System.currentTimeMillis();
		if (now >= walkBlock && UsingAgility) {
			UsingAgility = false;
			disconnected = xLog;
			if (!disconnected)
				getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		}
		if (getWildLevel() < 1) {
			if (wildyLevel > 0)
				setWildLevel(0);
			updatePlayerDisplay();
		} else
			setWildLevel(getWildLevel());
		if (inZone(3521, 9662, 3579, 9722) || inZone(3545, 326, 3584, 3307)) {
			send(new SendString("Kill Count: 0", 4536));
			setInterfaceWalkable(4535);
		}
		if (duelFight || inWildy()) {
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(3);
			getOutputStream().writeByteA(1);
			getOutputStream().writeString("Attack");
			getOutputStream().endFrameVarSize();
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(2);
			getOutputStream().writeByteA(0);
			getOutputStream().writeString("null");
			getOutputStream().endFrameVarSize();
			if (!inWildy()) {
				getOutputStream().createFrameVarSize(104);
				getOutputStream().writeByteC(4); // command slot
				getOutputStream().writeByteA(0); // 0 or 1; 1 if command should be placed
				getOutputStream().writeString("null");
				getOutputStream().endFrameVarSize();
			} else {
				getOutputStream().createFrameVarSize(104);
				getOutputStream().writeByteC(4);
				getOutputStream().writeByteA(0);
				getOutputStream().writeString("Trade with");
				getOutputStream().endFrameVarSize();
			}
		} else {
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(3);
			getOutputStream().writeByteA(1);
			getOutputStream().writeString("null");
			getOutputStream().endFrameVarSize();
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(4);
			getOutputStream().writeByteA(0);
			getOutputStream().writeString("Trade with");
			getOutputStream().endFrameVarSize();
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(2);
			getOutputStream().writeByteA(0);
			getOutputStream().writeString("Duel");
			getOutputStream().endFrameVarSize();
		}
		if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4566) {
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(1);
			getOutputStream().writeByteA(1);
			getOutputStream().writeString("Whack");
			getOutputStream().endFrameVarSize();
		} else {
			getOutputStream().createFrameVarSize(104);
			getOutputStream().writeByteC(1);
			getOutputStream().writeByteA(0);
			getOutputStream().writeString("null");
			getOutputStream().endFrameVarSize();
		}
		if (disconnectAt > 0 && now >= disconnectAt) {
			disconnected = true;
		}
		if (checkTime && now >= lastAction) {
			send(new SendMessage("Time's up (went " + (now - lastAction) + " over)"));
			checkTime = false;
		}
		if (pickupWanted) {
			if (pickTries < 1) {
				pickupWanted = false;
			}
			pickTries--;
			if (getPosition().getX() == pickX && getPosition().getY() == pickY) {
				pickUpItem(pickId, pickX, pickY);
				pickupWanted = false;
			}
		}
		if (animationReset > 0 && System.currentTimeMillis() >= animationReset) {
			animationReset = 0;
			rerequestAnim();
			if (originalS > 0) {
				wear(originalS, Equipment.Slot.SHIELD.getId(), 0);
			}
		}
		if (inTrade && tradeResetNeeded) {
			Client o = getClient(trade_reqId);
			if (o.tradeResetNeeded) {
				resetTrade();
				o.resetTrade();
			}
		}
		if (tStage == 1) { //Set emote for teleport!
			requestAnim(tEmote, 0);
			animation(308, getPosition());
			tStage = 2;
		} else if (tStage == 2 && System.currentTimeMillis() - lastTeleport >= 1200) {
			getPosition().moveTo(tX, tY, tH);
			teleportToX = getPosition().getX();
			teleportToY = getPosition().getY();
			tH = 0;
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
			tStage = 0;
			GetBonus(true);
			rerequestAnim();
			UsingAgility = false;
		}
		long current = System.currentTimeMillis();
		if (isInCombat() && current - getLastCombat() >= 7500) {
			setInCombat(false);
		}
		if (wildyLevel < 1 && current - lastBar >= 30000) {
			lastBar = current;
			updatePlayerDisplay();

			// barTimer = 0;
		}

		// Save every minute
		if (current - lastSave >= 60000) {
			saveStats(false);
			lastSave = now;
		}

		// Update progress every hour
		if (current - lastProgressSave >= (60000) * 60) {
			saveStats(false, true);
			lastProgressSave = now;
		}
		if (startDuel && duelChatTimer <= 0) {
			startDuel = false;
		}
		if (resetanim <= 0) {
			rerequestAnim();
			resetanim = 8;
		}

		if (AnimationReset && actionTimer <= 0) {
			rerequestAnim();
			AnimationReset = false;
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		}
		if (actionTimer > 0) {
			actionTimer -= 1;
		}

		// Shop
		if (UpdateShop) {
			resetItems(3823);
			resetShop(MyShopID);
		}

		// check stairs
		if (stairs > 0) {
			if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), stairDistance)) {
				Stairs.stairs(stairs, getPosition().getX(), getPosition().getY(), this);
			}
		}

		// check banking
		if (WanneBank > 0) {
			if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), WanneBank)) {
				openUpBank();
				WanneBank = 0;
			}
		}

		// check shopping
		if (WanneShop > 0) {
			if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 1)) {
				openUpShop(WanneShop);
				WanneShop = 0;
			}
		}

		// woodcutting check
		if (woodcuttingIndex >= 0) {
			if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 3)) {
				send(new RemoveInterfaces());
				Woodcutting.woodcutting(this);
			}
		}

		// Attacking in wilderness
		if (attackingPlayer && deathStage == 0) {
			attackTarget(this);
		}
		// Attacking an NPC
		else if (attackingNpc && deathStage == 0) {
			attackTarget(this);
		}
		// If killed apply dead
		if (deathStage == 0 && getCurrentHealth() < 1) {
			if(target instanceof Npc) {
				Npc npc = Server.npcManager.getNpc(target.getSlot());
				npc.removeEnemy(this);
			} else {
				Client p = getClient(duel_with);
				if (duel_with > 0 && validClient(duel_with) && inDuel && duelFight) {
					p.duelWin = true;
					p.DuelVictory();
				}
			}
			resetAttack(); //Should reset values correctly ?!
			requestAnim(836, 5);
			setCurrentHealth(0);
			deathStage++;
			deathTimer = System.currentTimeMillis();
			prayers.reset();
			send(new SendMessage("Oh dear you have died!"));
		} else if (deathStage == 1 && now - deathTimer >= 1800) {
			getPosition().moveTo(2604 + Misc.random(6), 3101 + Misc.random(3), 0); //Update position!
			teleportToX = getPosition().getX();
			teleportToY = getPosition().getY();
			deathStage = 0;
			deathTimer = 0;
			/* Stats check! */
			for(int i = 0; i < boostedLevel.length; i++) {
				if(i == 3)
					heal(Skills.getLevelForExperience(getExperience(Skills.HITPOINTS)));
				else if (i == 5)
					setCurrentPrayer(Skills.getLevelForExperience(getExperience(Skills.PRAYER)));
				else
					boostedLevel[i] = 0;
				refreshSkill(Skills.getSkill(i));
			}
			/* Death in other content! */
			if (inWildy())
				died();
			if (getSkullIcon() >= 0)
				setSkullIcon(-1);
			/* Item check !*/
			GetBonus(true);
			requestAnims(getEquipment()[3]);
			getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		}
		if (smithing[4] > 0) {
			if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 1)) { Smithing.smithing(this); }
		}
		if (smelting && now - lastAction >= 1800) {
			lastAction = now;
			Smelting.smelt(smelt_id, this);
		} else if (goldCrafting && now - lastAction >= 1800) {
			lastAction = now;
			GoldCrafting.goldCraft(this);
		} else if (shafting && now - lastAction >= 1800) {
			lastAction = now;
			Fletching.shaft(this);
		} else if (fletchings && now - lastAction >= 1800) {
			lastAction = now;
			fletching.fletchBow(this);
		} else if (fletchingOther && now - lastAction >= fletchOtherTime) {
			lastAction = now;
			fletching.fletchOther(this);
		} else if (filling) {
			lastAction = now;
			fill();
		} else if (spinning && now - lastAction >= Spinning.getSpinSpeed(this)) {
			lastAction = now;
			Spinning.spin(this);
		} else if (boneAltar > 0 && now - lastAction >= 1800) {
			lastAction = now;
			stillgfx(624, new Position(skillY, skillX, getPosition().getZ()), 0);
			Prayer.AltarBones(this, boneAltar);
		} else if (boneChaos > 0 && now - lastAction >= 1800) {
			lastAction = now;
			stillgfx(624, new Position(skillY, skillX, getPosition().getZ()), 0);
			Prayer.ChaosAltarBones(this, boneChaos);
		} else if (mixPots && now - lastAction >= potTime) {
			lastAction = now;
			mixPots();
		} else if (crafting && now - lastAction >= 1800) {
			lastAction = now;
			Crafting.craft(this);
		} else if (fishing && now - lastAction >= Utils.fishTime[fishIndex]) {
			lastAction = now;
			Fishing.Fish(this);
		} else if (mining && now - lastAction >= Mining.getMiningSpeed(this)) {
			lastAction = now;
			requestAnim(Mining.getMiningEmote(Utils.picks[minePick]), 0);
			Mining.mining(mineIndex, this);
		} else if (miningEss && now - lastAction >= Mining.getMiningSpeed(this)) {
			lastAction = now;
			requestAnim(Mining.getMiningEmote(Utils.picks[minePick]), 0);
			Mining.miningEss(this);
		} else if (cooking && now - lastAction >= 1800) {
			lastAction = now;
			Cooking.cook(this);
		}
		// Snowing, Npc Talking
		if (NpcWanneTalk == 2) { // Bank Booth
			if (GoodDistance2(getPosition().getX(), getPosition().getY(), skillX, skillY, 1)) {
				NpcDialogue = 1;
				NpcTalkTo = 494;
				NpcWanneTalk = 0;
			}
		} else if (NpcWanneTalk > 0) {
			if (GoodDistance2(getPosition().getX(), getPosition().getY(), skillX, skillY, 2))
				if (NpcWanneTalk == 804) {
					Tanning.openTan(this);
					NpcWanneTalk = 0;
				} else {
					NpcDialogue = NpcWanneTalk;
					NpcTalkTo = GetNPCID(skillX, skillY);
					NpcWanneTalk = 0;
				}
		}
		if (NpcDialogue > 0 && !NpcDialogueSend) {
			updateNPCChat(this);
		}

		if (isKicked) {
			disconnected = true;
			if (saveNeeded) {
				saveStats(true);
			}
			getOutputStream().createFrame(109);
		}

		/* Items update! Might cause some lag if more than 500 items?! */
		/* TODO: Add better way of handling ground items! */
		if (!Ground.items.isEmpty() || !(Ground.items.size() < 0)) {
			for (GroundItem item : Ground.items) {
				if (!item.canDespawn && item.taken && now - item.dropped >= item.timeDisplay) {
					item.taken = false;
					item.visible = false;
				}
				if (!item.visible && (now - item.dropped >= item.timeDisplay || !item.canDespawn) || (!item.visible && !item.canDespawn && !item.taken) && (now - item.dropped >= item.timeDisplay || !item.canDespawn)) {
					if (Server.itemManager.isTradable(item.id) && dbId != item.playerId
							&& Math.abs(getPosition().getX() - item.x) <= 114 && Math.abs(getPosition().getY() - item.y) <= 114 && getPosition().getZ() == item.z) {
						send(new CreateGroundItem(new GameItem(item.id, item.amount), new Position(item.x, item.y, item.z)));
					}
				}
				item.visible = true;
				if (item.canDespawn && item.visible && now - item.dropped >= (item.timeDisplay + item.timeDespawn)) {
					Ground.deleteItem(item);
				}
			}
		}

		if (Server.updateRunning && now - Server.updateStartTime > (Server.updateSeconds * 1000L)) {
			logout();
		}
	}

	public boolean packetProcess() { //Packet fixed?!
		if (disconnected) {
			return false;
		}
		Queue<PacketData> data = mySocketHandler.getPackets();
		if (data == null || data.isEmpty() || data.stream() == null)
			return false;
		try {
			fillInStream(data.poll());
			//parseIncomingPackets();
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("Player" + getPlayerName() + " disconnected.");
			System.out.println(mySocketHandler.getPackets());
			//saveStats(true); //Not sure if needed yet!
			disconnected = true;
			return false;
		}
		parseIncomingPackets();
		return true;
	}

	public void parseIncomingPackets() {
		lastPacket = System.currentTimeMillis();
		PacketHandler.process(this, currentPacket.getId(), currentPacket.getLength());
	}

	public void changeInterfaceStatus(int inter, boolean show) {
		getOutputStream().createFrame(171);
		getOutputStream().writeByte((byte) (!show ? 1 : 0));
		getOutputStream().writeWord(inter);
	}

	public void setMenuItems(int[] items) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(8847);
		getOutputStream().writeWord(items.length);

		for (int i = 0; i < items.length; i++) {
			getOutputStream().writeByte((byte) 1);
			getOutputStream().writeWordBigEndianA(items[i] + 1);
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void setMenuItems(int[] items, int[] amount) {
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(8847);
		getOutputStream().writeWord(items.length);

		for (int i = 0; i < items.length; i++) {
			getOutputStream().writeByte((byte) amount[i]);
			getOutputStream().writeWordBigEndianA(items[i] + 1);
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public static void publicyell(String message) {
		for (Player p : PlayerHandler.players) {
			if (p == null || !p.isActive) {
				continue;
			}
			Client temp = (Client) p;
			if (temp.getPosition().getX() > 0 && temp.getPosition().getY() > 0) {
				if (temp != null && !temp.disconnected && p.isActive) {
					temp.send(new SendMessage(message));
				}
			}
		}
	}

	public void yell(String message) {
		for (Player p : PlayerHandler.players) {
			if (p == null || !p.isActive)
				continue;
			Client temp = (Client) p;
			temp.send(new SendMessage(message + ":yell:"));
		}
	}

	public void yellKilled(String message) {
		for (Player p : PlayerHandler.players) {
			if (p == null || !p.isActive || !(p.inWildy() || p.inEdgeville()))
				continue;
			Client temp = (Client) p;
			temp.send(new SendMessage(message + ":yell:"));
		}
	}

	public boolean inZone(int southWestX, int southWestY, int NorthEastX, int NorthEastY) { // South West, North East
		if (getPosition().getX() >= southWestX && getPosition().getX() <= NorthEastX && getPosition().getY() >= southWestY
				&& getPosition().getY() <= NorthEastY) {
			return true;
		} else if (getPosition().getX()!= southWestX && getPosition().getX() != NorthEastX
				&& getPosition().getY() != southWestY && getPosition().getY() != NorthEastY) {
			return false;
		}
		return false;
	}

	public boolean IsItemInBag(int ItemID) {
		for (int playerItem : playerItems) {
			if ((playerItem - 1) == ItemID) {
				return true;
			}
		}
		return false;
	}

	public boolean AreXItemsInBag(int ItemID, int Amount) {
		int ItemCount = 0;

		for (int playerItem : playerItems) {
			if ((playerItem - 1) == ItemID) {
				ItemCount++;
			}
			if (ItemCount == Amount) {
				return true;
			}
		}
		return false;
	}

	public int GetItemSlot(int ItemID) {
		for (int i = 0; i < playerItems.length; i++) {
			if ((playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	public int GetBankItemSlot(int ItemID) {
		for (int i = 0; i < bankItems.length; i++) {
			if ((bankItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	public void pmstatus(int status) { // status: loading = 0 connecting = 1 fine = 2
		getOutputStream().createFrame(221);
		getOutputStream().writeByte(status);
	}

	public boolean playerHasItem(int itemID) {
		itemID++;
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	public boolean checkItem(int itemID) {
		itemID++;
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == itemID) {
				return true;
			}
		}
		for (int i = 0; i < getEquipment().length; i++) {
			if (getEquipment()[i] == itemID) {
				return true;
			}
		}
		for (int i = 0; i < bankItems.length; i++) {
			if (bankItems[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == itemID) {
				if (playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		return found >= amt;
	}

	public void sendpm(long name, int rights, byte[] chatmessage, int messagesize) {
		getOutputStream().createFrameVarSize(196);
		getOutputStream().writeQWord(name);
		getOutputStream().writeDWord(handler.lastchatid++); // must be different for each message
		getOutputStream().writeByte(rights);
		getOutputStream().writeBytes(chatmessage, messagesize, 0);
		getOutputStream().endFrameVarSize();
	}

	public void loadpm(long name, int world) {
		/*
		 * if(world != 0) { world += 9; } else if(world == 0){ world += 1; }
		 */
		if (world != 0) {
			world += 9;
		}
		getOutputStream().createFrame(50);
		getOutputStream().writeQWord(name);
		getOutputStream().writeByte(world);
	}

	public boolean DeleteArrow() {
		if (getEquipmentN()[Equipment.Slot.ARROWS.getId()] == 0) {
			deleteequiment(getEquipment()[Equipment.Slot.ARROWS.getId()], Equipment.Slot.ARROWS.getId());
			return false;
		}
		if (getEquipmentN()[Equipment.Slot.ARROWS.getId()] > 0) {
			getOutputStream().createFrameVarSizeWord(34);
			getOutputStream().writeWord(1688);
			getOutputStream().writeByte(Equipment.Slot.ARROWS.getId());
			getOutputStream().writeWord(getEquipment()[Equipment.Slot.ARROWS.getId()] + 1);
			if (getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1 > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord(getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1);
			} else {
				getOutputStream().writeByte(getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1); // amount
			}
			getOutputStream().endFrameVarSizeWord();
			getEquipmentN()[Equipment.Slot.ARROWS.getId()] -= 1;
		}
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		return true;
	}

	public void ReplaceObject(int objectX, int objectY, int NewObjectID, int Face, int ObjectType) {
		send(new SetMap(new Position(objectX, objectY)));
		getOutputStream().createFrame(101);
		getOutputStream().writeByteC((ObjectType << 2) + (Face & 3));
		getOutputStream().writeByte(0);

		if (NewObjectID != -1) {
			getOutputStream().createFrame(151);
			getOutputStream().writeByteS(0);
			getOutputStream().writeWordBigEndian(NewObjectID);
			getOutputStream().writeByteS((ObjectType << 2) + (Face & 3));
		}
	}

	public int GetNPCID(int coordX, int coordY) {
		for (Npc n : Server.npcManager.getNpcs()) {
			if (n.getPosition().getX() == coordX && n.getPosition().getY() == coordY) {
				return n.getId();
			}
		}
		return 1;
	}

	public String GetNpcName(int NpcID) {
		return Server.npcManager.getName(NpcID).replaceAll("_", " ");
	}

	public String GetItemName(int ItemID) {
		return Server.itemManager.getName(ItemID);
	}

	public double GetShopSellValue(int ItemID, int Type, int fromSlot) {
		return Server.itemManager.getShopSellValue(ItemID);
	}

	public double GetShopBuyValue(int ItemID, int Type, int fromSlot) {
		return Server.itemManager.getShopBuyValue(ItemID);
	}

	public int GetUnnotedItem(int ItemID) {
		String NotedName = Server.itemManager.getName(ItemID);
		for (Item item : Server.itemManager.items.values()) {
			if (item.getNoteable() && item.getId() != ItemID && item.getName().equals(NotedName) && !item.getDescription().startsWith("Swap this note at any bank for a")) {
				return item.getId();
			}
		}
		return 0;
	}

	public int GetNotedItem(int ItemID) {
		String NotedName = Server.itemManager.getName(ItemID);
		for (Item item : Server.itemManager.items.values()) {
			if (!item.getNoteable() && item.getId() != ItemID && item.getName().equals(NotedName) && item.getDescription().startsWith("Swap_this_note_at_any_bank")) {
				return item.getId();
			}
		}
		return 0;
	}

	public void WriteEnergy() {
		send(new SendString("100%", 149));
	}

	public void ResetBonus() {
		for (int i = 0; i < playerBonus.length; i++) {
			playerBonus[i] = 0;
		}
	}

	public void GetBonus(boolean update) {
		ResetBonus();
		for (int i = 0; i < 14; i++) {
			if (getEquipment()[i] > -1) {
				int timed = checkObsidianBonus(getEquipment()[i]) ? 2 : 1;
				if(!(duelFight && i == 8))
				for (int k = 0; k < playerBonus.length; k++) {
					int bonus = Server.itemManager.getBonus(getEquipment()[i], k);
					playerBonus[k] += bonus * timed;
				}
			}
		}
		if(update) WriteBonus();
	}

	public void WriteBonus() {
		for (int i = 0; i < playerBonus.length; i++)
			updateBonus(i);
	}

	public int neglectDmg() {
		int bonus = 0;
		if(getEquipment()[Equipment.Slot.SHIELD.getId()] == 11284)
			bonus += ((getLevel(Skills.FIREMAKING) + 1) / 5) * 10;
		return Math.min(1000, playerBonus[11] + bonus);
	}

	public double magicDmg() {
		double bonus = playerBonus[3] / 10D;
		return bonus <= 0.0 ? 1.0 : (1.0 + (bonus / 100D));
	}

	public void updateBonus(int id) {
		String send;
		if(id == 3) {
			double dmg = (magicDmg() - 1.0) * 100D;
			send = "Spell Dmg: " + String.format("%3.1f", dmg) + "%";
		} else if(id == 11)
			send = "Neglect Dmg: " + String.format("%3.1f", neglectDmg() / 10D) + "%";
		else if(id == 10)
			send = (usingBow ? "Ranged str: " : "Melee str: ") + "" + (usingBow && getRangedStr(this) >= 0 ? "+" : playerBonus[id] >= 0 ? "+" : "-") + "" + (usingBow ? getRangedStr(this) : playerBonus[id]);
		else
			send = BonusName[id] + ": " + (playerBonus[id] >= 0 ? "+" + playerBonus[id] : playerBonus[id]);
		send(new SendString(send, 1675 + (id >= 10 ? id + 1 : id)));
	}

	public boolean GoodDistance2(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if (objectX == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY)) {
					return true;
				} else if (objectY == playerY
						&& ((objectX + j) == playerX || (objectX - j) == playerX)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean fromTrade(int itemID, int fromSlot, int amount) {
		if (System.currentTimeMillis() - lastButton >= 200) {
			lastButton = System.currentTimeMillis();
		} else {
			return false;
		}
		try {
			Client other = getClient(trade_reqId);
			if (!inTrade || !validClient(trade_reqId) || !canOffer) {
				declineTrade();
				return false;
			}
			if (!checkGameitemAmount(fromSlot, amount, offeredItems) || offeredItems.get(fromSlot).getId() != itemID) {
				return false;
			}
			int count = 0;
			if (!Server.itemManager.isStackable(itemID)) {
				for (GameItem item : offeredItems) {
					if (item.getId() == itemID) {
						count++;
					}
				}
			} else
				count = offeredItems.get(fromSlot).getAmount();
			amount = amount > count ? count : amount;
			boolean found = false;
			for (GameItem item : offeredItems) {
				if (item.getId() == itemID) {
					if (item.isStackable()) {
						if (amount < item.getAmount())
							offeredItems.set(fromSlot, new GameItem(item.getId(), item.getAmount() - amount));
						else
							offeredItems.remove(item);
						found = true;
					} else {
            /*if (item.getAmount() > amount) {
              item.removeAmount(amount);
              found = true;
            } else {
              amount = item.getAmount();
              found = true;
              offeredItems.remove(item);
            }*/
						if (amount == 1) {
							offeredItems.remove(item);
							found = true;
						} else {
							offeredItems.remove(item);
							addItem(itemID, 1);
							amount--;
						}
					}
					if (found) { //If found add item to inventory!
						addItem(itemID, amount);
						break;
					}
				}
			}
			tradeConfirmed = false;
			other.tradeConfirmed = false;
			resetItems(3322);
			resetTItems(3415);
			other.resetOTItems(3416);
			send(new SendString("", 3431));
			other.send(new SendString("", 3431));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean tradeItem(int itemID, int fromSlot, int amount) {
		if (System.currentTimeMillis() - lastButton >= 200) {
			lastButton = System.currentTimeMillis();
		} else {
			return false;
		}
		if (!Server.itemManager.isStackable(itemID))
			amount = amount > getInvAmt(itemID) ? getInvAmt(itemID) : amount;
		else
			amount = amount > playerItemsN[fromSlot] ? playerItemsN[fromSlot] : amount;
		Client other = getClient(trade_reqId);
		if (!inTrade || !validClient(trade_reqId) || !canOffer) {
			System.out.println("declining in tradeItem()");
			declineTrade();
			return false;
		}
		if (!playerHasItem(itemID, amount) || playerItems[fromSlot] != (itemID + 1)) {
			return false;
		}
		if (!Server.itemManager.isTradable(itemID) && playerRights < 2 && other.playerRights < 2) {
			send(new SendMessage("You can't trade this item"));
			return false;
		}
		if (Server.itemManager.isStackable(itemID)) {
			boolean inTrade = false;
			for (GameItem item : offeredItems) {
				if (item.getId() == itemID) {
					inTrade = true;
					item.addAmount(amount);
					deleteItem(itemID, fromSlot, amount);
					break;
				}
			}
			if (!inTrade) {
				offeredItems.add(new GameItem(itemID, amount));
				deleteItem(itemID, fromSlot, amount);
			}
		} else {
			for (int a = 1; a <= amount; a++) {
				if (a == 1) {
					offeredItems.add(new GameItem(itemID, 1));
					deleteItem(itemID, fromSlot, amount);
				} else {
					int slot = findItem(itemID, playerItems, playerItemsN);
					if (slot >= 0 && slot < 28)
						//tradeItem(itemID, slot, 1);
						offeredItems.add(new GameItem(itemID, 1));
					deleteItem(itemID, slot, amount);
				}
			}
		}
		resetItems(3322);
		resetTItems(3415);
		other.resetOTItems(3416);
		send(new SendString("", 3431));
		other.send(new SendString("", 3431));
		return true;
	}

	/* Shops */
	public boolean sellItem(int itemID, int fromSlot, int amount) {
		/* Item Values */
		int original = itemID;
		itemID = GetUnnotedItem(original) > 0 ? GetUnnotedItem(original) : itemID;
		int price = (int) Math.floor(GetShopBuyValue(itemID, 0, fromSlot));
		/* Functions */
		if (!Server.shopping || tradeLocked) {
			send(new SendMessage(tradeLocked ? "You are trade locked!" : "Currently selling stuff to the store has been disabled!"));
			return false;
		}
		if (price <= 0 || !Server.itemManager.isTradable(itemID) || ShopHandler.ShopBModifier[MyShopID] > 2) {
			send(new SendMessage("You cannot sell " + GetItemName(itemID).toLowerCase() + " in this store."));
			return false;
		}
		if (ShopHandler.ShopBModifier[MyShopID] == 2 && !ShopHandler.findDefaultItem(MyShopID, itemID)) {
			send(new SendMessage("Can't sell that item to the store!"));
			return false;
		}
		int slot = -1;
		for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
			if (ShopHandler.ShopItems[MyShopID][i] <= 0 && slot == -1)
				slot = i;
			else if (itemID == ShopHandler.ShopItems[MyShopID][i] - 1) {
				slot = i;
				i = ShopHandler.MaxShopItems; //Just to stop the loop!
			}
		}
		if (slot == -1) { //If we do not have a slot means the store is full!
			send(new SendMessage("Can't sell more items to the store!"));
			return false;
		}
		/* Amount checks */
		boolean stack = Server.itemManager.isStackable(original);
		amount = amount > getInvAmt(original) ? getInvAmt(original) : amount;
		amount = Integer.MAX_VALUE - ShopHandler.ShopItemsN[MyShopID][slot] < amount ? Integer.MAX_VALUE - ShopHandler.ShopItemsN[MyShopID][slot] : amount;
		amount = Integer.MAX_VALUE - getInvAmt(995) < amount * price ? (Integer.MAX_VALUE - getInvAmt(995)) / price : amount;

		if (amount > 0) { // Code to check if there is any amount to sell!
			if (!stack) {
				for (int i = 0; i < amount; i++) {
					deleteItem(original, 1);
				}
			} else {
				deleteItem(original, amount);
			}
			ShopHandler.ShopItems[MyShopID][slot] = itemID + 1;
			ShopHandler.ShopItemsN[MyShopID][slot] += amount;
			addItem(995, amount * price);
		} else
			send(new SendMessage("Could not sell anything!"));
		/* Store update! */
		resetItems(3823);
		resetShop(MyShopID);
		UpdatePlayerShop();
		return true;
	}

	public int eventShopValues(int slot) {
		switch (slot) {
			case 0:
				return 8000;
			case 1:
			case 2:
				return 12000;
			case 3:
			case 4:
				return 4000;
			case 5:
				return 25000;
			case 6:
				return 15000;
		}
		return 0;
	}

	public boolean buyItem(int itemID, int fromSlot, int amount) {
		if (amount > 0 && itemID == (ShopHandler.ShopItems[MyShopID][fromSlot] - 1)) {
			boolean stack = Server.itemManager.isStackable(itemID);
			amount = ShopHandler.ShopItemsN[MyShopID][fromSlot] < amount ? ShopHandler.ShopItemsN[MyShopID][fromSlot] : amount;
			if (!stack && freeSlots() < 1) {
				send(new SendMessage("Not enough space in your inventory."));
				return false;
			}
			int currency = MyShopID == 55 ? 11997 : 995;
			int TotPrice2 = MyShopID == 55 ? eventShopValues(fromSlot) : (int) Math.floor(GetShopSellValue(itemID, 0, fromSlot));
			TotPrice2 = MyShopID >= 9 && MyShopID <= 11 ? (int) (TotPrice2 * 1.5) : TotPrice2;
			int coins = getInvAmt(currency);
			amount = amount * TotPrice2 > coins ? coins / TotPrice2 : amount;
			if (amount == 0) {
				send(new SendMessage("You don't have enough " + GetItemName(currency).toLowerCase() + ""));
				return false;
			}
			if (!stack) {
				for (int i = amount; i > 0; i--) {
					if (freeSlots() == 0) {
						send(new SendMessage("Not enough space in your inventory."));
						return false;
					}
					if (addItem(itemID, 1)) {
						deleteItem(currency, TotPrice2);
						ShopHandler.ShopItemsN[MyShopID][fromSlot] -= 1;
						if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[MyShopID] && ShopHandler.ShopItemsN[MyShopID][fromSlot] <= 0) {
							ShopHandler.resetAnItem(MyShopID, fromSlot);
							break;
						}
					} else {
						send(new SendMessage("Not enough space in your inventory."));
						return false;
					}
				}
			} else {
				if (addItem(itemID, amount)) {
					deleteItem(currency, TotPrice2 * amount);
					ShopHandler.ShopItemsN[MyShopID][fromSlot] -= amount;
					if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[MyShopID] && ShopHandler.ShopItemsN[MyShopID][fromSlot] <= 0) {
						ShopHandler.resetAnItem(MyShopID, fromSlot);
					}
				} else
					return false;
			}
			resetItems(3823);
			resetShop(MyShopID);
			UpdatePlayerShop();
			return true;
		}
		return false;
	}

	public void UpdatePlayerShop() {
		for (int i = 1; i < Constants.maxPlayers; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].IsShopping && PlayerHandler.players[i].MyShopID == MyShopID
						&& i != getSlot()) {
					PlayerHandler.players[i].UpdateShop = true;
				}
			}
		}
	}

	public void showPlayerOption(String[] text) {
		int base = 2459;
		if (text.length == 4)
			base = 2469;
		if (text.length == 5)
			base = 2480;
		if (text.length == 6)
			base = 2492;
		send(new Frame171(1, base + 4 + text.length - 1));
		send(new Frame171(0, base + 7 + text.length - 1));
		for (int i = 0; i < text.length; i++)
			send(new SendString(text[i], base + 1 + i));
		sendFrame164(base);
		NpcDialogueSend = true;
	}

	public void showNPCChat(int npcId, int emote, String[] text) {
		int base = 4882;
		if (text.length == 2)
			base = 4887;
		if (text.length == 3)
			base = 4893;
		if (text.length == 4)
			base = 4900;
		send(new NpcDialogueHead(npcId, base + 1));
		sendFrame200(base + 1, emote);
		send(new SendString(GetNpcName(npcId), base + 2));
		for (int i = 0; i < text.length; i++)
			send(new SendString(text[i], base + 3 + i));
		send(new SendString("Click here to continue", base + 3 + text.length));
		sendFrame164(base);
		NpcDialogueSend = true;
	}

	public void showPlayerChat(String[] text, int emote) {
		int base = 968;
		if(text.length == 2)
			base = 973;
		if(text.length == 3)
			base = 979;
		if(text.length == 4)
			base = 986;
		send(new PlayerDialogueHead(base + 1));
		sendFrame200(base + 1, emote); //614 seems standard!
		send(new SendString(getPlayerName(), base + 2));
		for (int i = 0; i < text.length; i++)
			send(new SendString(text[i], base + 3 + i));
		send(new SendString("Click here to continue", base + 3 + text.length));
		sendFrame164(base);
		NpcDialogueSend = true;
	}

	/* Equipment level checking */
	public int GetCLAttack(int ItemID) {
		if (ItemID == -1) {
			return 1;
		}
		String ItemName = GetItemName(ItemID);
		String ItemName2 = ItemName.replaceAll("Bronze", "");

		ItemName2 = ItemName2.replaceAll("Iron", "");
		ItemName2 = ItemName2.replaceAll("Steel", "");
		ItemName2 = ItemName2.replaceAll("Black", "");
		ItemName2 = ItemName2.replaceAll("Mithril", "");
		ItemName2 = ItemName2.replaceAll("Adamant", "");
		ItemName2 = ItemName2.replaceAll("Rune", "");
		ItemName2 = ItemName2.replaceAll("Granite", "");
		ItemName2 = ItemName2.replaceAll("Dragon", "");
		ItemName2 = ItemName2.replaceAll("Crystal", "");
		ItemName2 = ItemName2.trim();
		if (ItemName2.startsWith("claws") || ItemName2.startsWith("dagger") || ItemName2.startsWith("sword")
				|| ItemName2.startsWith("scimitar") || ItemName2.startsWith("mace") || ItemName2.startsWith("longsword")
				|| ItemName2.startsWith("battleaxe") || ItemName2.startsWith("warhammer") || ItemName2.startsWith("2h sword")
				|| ItemName2.startsWith("halberd") || ItemName2.endsWith("axe") || ItemName2.endsWith("pickaxe")) {
			if (ItemName.startsWith("Bronze")) {
				return 1;
			} else if (ItemName.startsWith("Iron")) {
				return 1;
			} else if (ItemName.startsWith("Steel")) {
				return 10;
			} else if (ItemName.startsWith("Black")) {
				return 10;
			} else if (ItemName.startsWith("Mithril")) {
				return 20;
			} else if (ItemName.startsWith("Adamant")) {
				return 30;
			} else if (ItemName.startsWith("Rune")) {
				return 40;
			} else if (ItemName.startsWith("Dragon")) {
				return 60;
			}
		}
		if (ItemID == 21646)
			return 50;
		if (ItemID == 6523 || ItemID == 6525 || ItemID == 6527)
			return 55;
		if (ItemID == 3842 ||ItemID == 20223)
			return 45;
		return 1;
	}

	public int GetCLDefence(int ItemID) {
		if (ItemID == -1) return 1;
		String checkName = GetItemName(ItemID).toLowerCase();
		if (checkName.endsWith("arrow") || checkName.endsWith("hat") || (checkName.endsWith("axe") && !checkName.startsWith("battle")))
			return 1;
		String ItemName = GetItemName(ItemID);
		if (ItemName.toLowerCase().contains("beret") || ItemName.toLowerCase().contains("cavalier") || ItemName.toLowerCase().contains("mystic") || checkName.contains("mask") || checkName.contains("partyhat"))
			return 1;
		String ItemName2 = ItemName.replaceAll("Bronze", "");
		ItemName2 = ItemName2.replaceAll("Iron", "");
		ItemName2 = ItemName2.replaceAll("Steel", "");
		ItemName2 = ItemName2.replaceAll("Black", "");
		ItemName2 = ItemName2.replaceAll("Mithril", "");
		ItemName2 = ItemName2.replaceAll("Adamant", "");
		ItemName2 = ItemName2.replaceAll("Rune", "");
		ItemName2 = ItemName2.replaceAll("Granite", "");
		ItemName2 = ItemName2.replaceAll("Dragon", "");
		ItemName2 = ItemName2.replaceAll("Crystal", "");
		ItemName2 = ItemName2.trim();
		if (ItemName2.startsWith("claws") || ItemName2.startsWith("dagger") || ItemName2.startsWith("sword")
				|| ItemName2.startsWith("scimitar") || ItemName2.startsWith("mace") || ItemName2.startsWith("longsword")
				|| ItemName2.startsWith("battleaxe") || ItemName2.startsWith("warhammer") || ItemName2.startsWith("2h sword")
				|| ItemName2.startsWith("harlberd") || ItemName2.startsWith("pickaxe")) {// It's a weapon,
			return 1;
		} else if (ItemName.startsWith("Ahrims") || ItemName.startsWith("Karil") || ItemName.startsWith("Torag")
				|| ItemName.startsWith("Verac") || ItemName.endsWith("Guthan") || ItemName.endsWith("Dharok") ||
				ItemName.endsWith("staff") || ItemName.endsWith("crossbow") || ItemName.endsWith("hammers")
				|| ItemName.endsWith("flail") || ItemName.endsWith("warspear") || ItemName.endsWith("greataxe")) {
			return 1;
		} else {
			if (ItemName.startsWith("Saradomin") || ItemName.startsWith("Zamorak") || ItemName.startsWith("Guthix")
					&& ItemName.toLowerCase().contains("staff") && ItemName.toLowerCase().contains("cape"))
				return 1;
			if (ItemID == 10748 || ItemID == 10402)
				return 1;
			if (ItemID == 11284)
				return 75;
			if (ItemID == 4224)
				return 70;
			if (ItemName.startsWith("Bronze")) {
				return 1;
			} else if (ItemName.startsWith("Iron")) {
				return 1;
			} else if (ItemName.startsWith("Steel") && !ItemName.contains("arrow")) {
				return 5;
			} else if (ItemName.startsWith("Black") && !ItemName.contains("hide")) {
				return 10;
			} else if (ItemName.startsWith("Slayer helmet")) {
				return 10;
			} else if (ItemName.startsWith("Mithril") && !ItemName.contains("arrow")) {
				return 20;
			} else if (ItemName.startsWith("Splitbark")) {
				return 20;
			} else if (ItemName.startsWith("Adamant") && !ItemName.contains("arrow")) {
				return 30;
			} else if (ItemName.startsWith("Rune") && !ItemName.endsWith("cape") && !ItemName.contains("arrow")) {
				return 40;
			} else if (ItemName.startsWith("Granite") && ItemID != 4153 && ItemID != 21646) {
				return 50;
			} else if (ItemName.startsWith("Dragon") && !ItemName.contains("hide") && !ItemName.toLowerCase().contains("ring") && !ItemName.toLowerCase().contains("necklace") && !ItemName.toLowerCase().contains("amulet")) {
				return 60;
			} else if (ItemName.startsWith("Rock-shell")) {
				return 60;
			}
		}
		if (ItemName.toLowerCase().contains("ghostly"))
			return 70;
		if (ItemName.startsWith("Skeletal"))
			return 1;
		if (ItemName.startsWith("Snakeskin body") || ItemName.startsWith("Snakeskin chaps"))
			return 60;
		if (ItemID == 1135 || ItemID == 2499 || ItemID == 2501 || ItemID == 2503)
			return 40;
		if(ItemID == 20235)
			return 45;
		if (ItemID == 6524 || ItemID == 21298 || ItemID == 21301 || ItemID == 21304) //Obsidian
			return 55;
		if (ItemName.startsWith("Guthan") || (ItemName.startsWith("Karil")) || (ItemName.startsWith("Torag"))
		|| (ItemName.startsWith("Dharok")) || (ItemName.startsWith("Verac")) || (ItemName.startsWith("Ahrim")))
			return 70;
		return 1;
	}

	public int GetCLStrength(int ItemID) {
		if (ItemID == -1) return 1;
		if (ItemID == 3842 || ItemID == 20223 || ItemID == 20232)
			return 45;
		if (ItemID == 4153)
			return 50;
		if (ItemID == 6528)
			return 55;
		return 1;
	}

	public int GetCLMagic(int ItemID) {
		if (ItemID == -1) return 1;
		String ItemName = GetItemName(ItemID);
		if (ItemName.startsWith("White Mystic") || ItemName.startsWith("Splitbark"))
			return 20;
		if(ItemID == 4675)
			return 25;
		if (ItemName.startsWith("Black Mystic"))
			return 35;
		if(ItemID == 6526)
			return 40;
		if(ItemID == 3840 || ItemID == 20220)
			return 45;
		if (ItemName.startsWith("Infinity") || ItemID == 6914)
			return 50;
		if (ItemID == 13235)
			return 60;
		if (ItemName.toLowerCase().contains("ghostly"))
			return 70;
		if (ItemName.startsWith("Ahrim"))
			return 80;
		return 1;
	}

	public int GetCLRanged(int ItemID) {
		if (ItemID == -1) return 1;
		String ItemName = GetItemName(ItemID);
		if (ItemName.startsWith("Karil")) {
			return 80;
		}
		if (ItemName.startsWith("Snakeskin")) {
			return 55;
		}
		if (ItemName.startsWith("New crystal bow")) {
			return 65;
		}
		if (ItemName.startsWith("Oak")) {
			return 1;
		}
		if (ItemName.startsWith("Willow")) {
			return 20;
		}
		if (ItemName.startsWith("Maple")) {
			return 30;
		}
		if (ItemName.startsWith("Yew")) {
			return 40;
		}
		if (ItemName.startsWith("Magic") && !ItemName.toLowerCase().contains("cape")) {
			return 50;
		}
		if (ItemName.startsWith("Green d")) {
			return 40;
		}
		if (ItemName.startsWith("Blue d")) {
			return 50;
		}
		if (ItemName.startsWith("Red d")) {
			return 60;
		}
		if (ItemName.startsWith("Black d")) {
			return 70;
		}
		if (ItemName.startsWith("Spined")) {
			return 75;
		}
		if (ItemName.startsWith("Dragon arr")) {
			return 60;
		}
		if (ItemName.startsWith("Rune arr")) {
			return 40;
		}
		if (ItemName.startsWith("Adamant arr")) {
			return 30;
		}
		if (ItemName.startsWith("Mithril arr")) {
			return 20;
		}
		if (ItemName.startsWith("Steel arr")) {
			return 10;
		}
		if (ItemID == 3844 || ItemID == 20226 || ItemID == 20229)
			return 45;
		if (ItemID == 6724)
			return 75;
		if (ItemID == 20997)
			return 85;
		return 1;
	}

	public void setInterfaceWalkable(int ID) {
		getOutputStream().createFrame(208);
		getOutputStream().writeWordBigEndian_dup(ID);
		//flushOutStream();
	}

	public void RefreshDuelRules() {
		/*
		 * Danno: Testing ticks/armour blocks.
		 */

		// "No Ranged", "No Melee", "No Magic",
		// "No Gear Change", "Fun Weapons", "No Retreat", "No Drinks",
		// "No Food", "No prayer", "No Movement", "Obstacles" };
		int configValue = 0;
		for (int i = 0; i < duelLine.length; i++) {
			if (duelRule[i]) {
				send(new SendString(/* "@red@" + */duelNames[i], duelLine[i]));
				configValue += stakeConfigId[i + 11];
			} else {
				send(new SendString(/* "@gre@" + */duelNames[i], duelLine[i]));
			}
		}
		for (int i = 0; i < duelBodyRules.length; i++) {
			if (duelBodyRules[i])
				configValue += stakeConfigId[i];
		}
		frame87(286, configValue);
	}

	public void DuelVictory() {
		Client other = getClient(duel_with);
		if (validClient(duel_with)) {
			send(new SendMessage("You have defeated " + other.getPlayerName() + "!"));
			send(new SendString("" + other.determineCombatLevel(), 6839));
			send(new SendString(other.getPlayerName(), 6840));
		}
		boolean stake = false;
		String playerStake = "";
		for (GameItem item : offeredItems) {
			if (item.getId() > 0 && item.getAmount() > 0) {
				playerStake += "(" + item.getId() + ", " + item.getAmount() + ")";
				stake = true;
			}
		}
		String opponentStake = "";
		for (GameItem item : otherOfferedItems) {
			if (item.getId() > 0 && item.getAmount() > 0) {
				opponentStake += "(" + item.getId() + ", " + item.getAmount() + ")";
				stake = true;
			}
		}
		resetAttack();

		if (stake) {
			DuelLog.recordDuel(this.getPlayerName(), other.getPlayerName(), playerStake, opponentStake, this.getPlayerName());
			itemsToVScreen_old();
			acceptDuelWon();
			other.resetDuel();
		} else {
			if (validClient(duel_with))
				other.resetDuel();
			resetDuel();
			// duelStatus = -1;
		}
		if (stake) {
			showInterface(6733);
		}

		/*
		 * Danno: Reset health.
		 */
		heal(getMaxHealth());
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

	}

	public void itemsToVScreen_old() {
		if (disconnectAt > 0) {
			acceptDuelWon();
			return;
		}
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(6822);
		getOutputStream().writeWord(otherOfferedItems.toArray().length);
		for (GameItem item : otherOfferedItems) {
			if (item.getAmount() > 254) {
				getOutputStream().writeByte(255);
				// item's stack count. if over 254, write byte 255
				getOutputStream().writeDWord_v2(item.getAmount());
				// and then the real value with writeDWord_v2
			} else {
				getOutputStream().writeByte(item.getAmount());
			}
			getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public void refreshDuelScreen() {
		Client other = getClient(duel_with);
		if (!validClient(duel_with)) {
			return;
		}
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(6669);
		getOutputStream().writeWord(offeredItems.toArray().length);
		int current = 0;
		for (GameItem item : offeredItems) {
			if (item.getAmount() > 254) {
				getOutputStream().writeByte(255);
				// item's stack count. if over 254, write byte 255
				getOutputStream().writeDWord_v2(item.getAmount());
				// and then the real value with writeDWord_v2
			} else {
				getOutputStream().writeByte(item.getAmount());
			}
			getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				getOutputStream().writeByte(1);
				getOutputStream().writeWordBigEndianA(-1);
			}
		}
		getOutputStream().endFrameVarSizeWord();
		getOutputStream().createFrameVarSizeWord(53);
		getOutputStream().writeWord(6670);
		getOutputStream().writeWord(other.offeredItems.toArray().length);
		current = 0;
		for (GameItem item : other.offeredItems) {
			if (item.getAmount() > 254) {
				getOutputStream().writeByte(255);
				// item's stack count. if over 254, write byte 255
				getOutputStream().writeDWord_v2(item.getAmount());
				// and then the real value with writeDWord_v2
			} else {
				getOutputStream().writeByte(item.getAmount());
			}
			getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				getOutputStream().writeByte(1);
				getOutputStream().writeWordBigEndianA(-1);
			}
		}
		getOutputStream().endFrameVarSizeWord();
	}

	public boolean stakeItem(int itemID, int fromSlot, int amount) {
		if (System.currentTimeMillis() - lastButton < 200)
			return false;
		lastButton = System.currentTimeMillis();
		if (!Server.itemManager.isStackable(itemID))
			amount = Math.min(amount, getInvAmt(itemID));
		else
			amount = Math.min(amount, playerItemsN[fromSlot]);
		if (!Server.itemManager.isTradable(itemID)) {
			send(new SendMessage("You can't trade that item"));
			return false;
		}
		Client other = getClient(duel_with);
		if (!inDuel || !validClient(duel_with)) {
			declineDuel();
			return false;
		}
		if (!canOffer) {
			declineDuel();
			return false;
		}
		if (!playerHasItem(itemID, amount) || playerItems[fromSlot] != (itemID + 1)) {
			return false;
		}
		if (!Server.itemManager.isTradable(itemID)) {
			send(new SendMessage("You can't trade this item"));
			return false;
		}
		if (Server.itemManager.isStackable(itemID)) {
			boolean inTrade = false;
			for (GameItem item : offeredItems) {
				if (item.getId() == itemID) {
					inTrade = true;
					item.addAmount(amount);
					deleteItem(itemID, fromSlot, amount);
					break;
				}
			}
			if (!inTrade) {
				offeredItems.add(new GameItem(itemID, amount));
				deleteItem(itemID, fromSlot, amount);
			}
		} else {
			for (int a = 1; a <= amount; a++) {
				if (a == 1) {
					offeredItems.add(new GameItem(itemID, 1));
					deleteItem(itemID, fromSlot, amount);
				} else {
					int slot = findItem(itemID, playerItems, playerItemsN);
					if (slot >= 0 && slot < 28)
						//tradeItem(itemID, slot, 1);
						offeredItems.add(new GameItem(itemID, 1));
					deleteItem(itemID, slot, amount);
				}
			}
		}
		resetItems(3214);
		resetItems(3322);
		other.resetItems(3214);
		other.resetItems(3322);
		refreshDuelScreen();
		other.refreshDuelScreen();
		send(new SendString("", 6684));
		other.send(new SendString("", 6684));
		return true;
	}

	public boolean checkGameitemAmount(int slot, int amount, CopyOnWriteArrayList<GameItem> item) {
		int count = 0;
		if (item.isEmpty()) return false;
		if (!item.get(slot).isStackable()) {
			for (GameItem checkItem : item) {
				if (checkItem.getId() == id)
					count++;
			}
		} else
			count = item.get(slot).getAmount();
		return amount >= count;
	}

	public boolean fromDuel(int itemID, int fromSlot, int amount) {
		if (System.currentTimeMillis() - lastButton >= 200) {
			lastButton = System.currentTimeMillis();
		} else {
			return false;
		}
		Client other = getClient(duel_with);
		if (!inDuel || !validClient(duel_with)) {
			declineDuel();
			return false;
		}
		if (!checkGameitemAmount(fromSlot, amount, offeredItems) || offeredItems.get(fromSlot).getId() != itemID) {
			return false;
		}
		int count = 0;
		if (!Server.itemManager.isStackable(itemID)) {
			for (GameItem item : offeredItems) {
				if (item.getId() == itemID) {
					count++;
				}
			}
		} else
			count = offeredItems.get(fromSlot).getAmount();
		amount = amount > count ? count : amount;
		boolean found = false;
		for (GameItem item : offeredItems) {
			if (item.getId() == itemID) {
				if (item.isStackable()) {
					if (amount < item.getAmount())
						offeredItems.set(fromSlot, new GameItem(item.getId(), item.getAmount() - amount));
					else
						offeredItems.remove(item);
					found = true;
				} else {
					if (amount == 1) {
						offeredItems.remove(item);
						found = true;
					} else {
						offeredItems.remove(item);
						addItem(itemID, 1);
						amount--;
					}
				}
				if (found) { //If found add item to inventory!
					addItem(itemID, amount);
					break;
				}
			}
		}
		duelConfirmed = false;
		other.duelConfirmed = false;
		resetItems(3214);
		other.resetItems(3214);
		resetItems(3322);
		other.resetItems(3322);
		refreshDuelScreen();
		other.refreshDuelScreen();
		other.send(new SendString("", 6684));

		return true;
	}

	public static String passHash(String in, String salt) {
		String passM = new MD5(in).compute();
		return new MD5(passM + salt).compute();
	}

	public String getLook() {
		String out = "";
		for (int playerLook : playerLooks) {
			out += playerLook + " ";
		}
		return out;
	}

	public void setLook(int[] parts) {
		/*
		 * if (parts.length != 13) { println("setLook:  Invalid array length!"); return;
		 * }
		 */
		setGender(parts[0]);
		setHead(parts[1]);
		setBeard(parts[2]);
		setTorso(parts[3]);
		setArms(parts[4]);
		setHands(parts[5]);
		setLegs(parts[6]);
		setFeet(parts[7]);
		pHairC = parts[8];
		pTorsoC = parts[9];
		pLegsC = parts[10];
		pFeetC = parts[11];
		pSkinC = parts[12];
		playerLooks = parts;
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
	}

	public boolean runeCheck() {
		if (playerHasItem(565))
			return true;
		send(new SendMessage("This spell requires 1 blood rune"));
		return false;
	}

	public void resetPos() {
		getPosition().moveTo(2604 + Misc.random(6), 3101 + Misc.random(3), 0); //Update position!
		teleportToX = getPosition().getX();
		teleportToY = getPosition().getY();
	}

	public void debug(String text) {
		if (debug) {
			send(new SendMessage(text));
		}
	}

	public void showRandomEvent() {
		resetAction(true);
		if (!randomed || !randomed2) {
			random_skill = Utils.random(20);
			send(new SendString("Click the @or1@" + Skills.getSkill(random_skill).getName() + " @yel@button", 2810));
			send(new SendString("", 2811));
			send(new SendString("", 2831));
			randomed = true;
			showInterface(2808);
		}
	}

	public void triggerRandom(int xp) {
		xp /= 5;
		int reduceChance = Math.min(xp < 50 ? xp : xp < 100 ? (xp * 3) / 2 : xp < 200 ? xp * 2 : xp < 400 ? xp * 3 : xp * 4, 3000);
		reduceChance *= 2 + Misc.random(8);
		//System.out.println("testing..." + reduceChance + ", " + Math.min(reduceChance, 7000));
		if (Misc.chance(6500 - Math.min(reduceChance, 6000)) == 1) {
			showRandomEvent();
			chestEvent = 0;
		}
		//TODO need to make something better so its not a autoclicker
		/*if(chestEvent >= 50) {
			int chance = Misc.chance(100);
			int trigger = (chestEvent - 50) * 2;
			if(trigger >= chance) {
				chestEventOccur = true;
				chestEvent = 0;
				send(new SendMessage("The server randomly detect you auto-clicking!"));
			}
		}*/
	}

	public void openGenie() {
		if (inDuel || duelFight || IsBanking) {
			send(new SendMessage("Finish what you are doing first!"));
			return;
		}
		send(new SendString("Select a skill in which you wish to gain experience!", 2810));
		send(new SendString("", 2811));
		send(new SendString("", 2831));
		genie = true;
		showInterface(2808);
	}

	public int findItem(int id, int[] items, int[] amounts) {
		for (int i = 0; i < playerItems.length; i++) {
			if ((items[i] - 1) == id && amounts[i] > 0) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasSpace() {
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == -1 || playerItems[i] == 0) {
				return true;
			}
		}
		return false;
	}

	public int getFreeSpace() {
		int spaces = 0;
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == -1 || playerItems[i] == 0) {
				spaces += 1;
			}
		}
		return spaces;
	}

	public void resetAction(boolean full) {
		boneAltar = -1;
		boneChaos = -1;
		spinning = false;
		crafting = false;
		goldCrafting = false;
		goldIndex = -1;
		goldSlot = -1;
		fishing = false;
		fletchings = false;
		fletchingOther = false;
		shafting = false;
		stringing = false;
		mining = false;
		miningEss = false;
		minePick = -1;
		smelting = false;
		smelt_id = -1;
		cooking = false;
		filling = false;
		mixPots = false;

		if (full) { rerequestAnim(); }

		// woodcutting check
		if (woodcuttingIndex >= 0) { Woodcutting.resetWC(this);	}

		// smithing check
		if (smithing[0] > 0) {
			Smithing.resetSM(this);
			send(new RemoveInterfaces());
		}

		if (fletchings || fletchingOther) {	getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true); }
	}

	public void resetAction() {
		resetAction(true);
	}

	public void fill() {
		if (playerHasItem(229)) {
			deleteItem(229, 1);
			addItem(227, 1);
			requestAnim(832, 0);
			animationReset = System.currentTimeMillis() + 600;
		} else {
			resetAction(true);
			resetAction();
		}
	}

	public void replaceDoors() {
		for (int d = 0; d < DoorHandler.doorX.length; d++) {
			if (DoorHandler.doorX[d] > 0 && DoorHandler.doorHeight[d] == getPosition().getZ()
					&& Math.abs(DoorHandler.doorX[d] - getPosition().getX()) <= 120
					&& Math.abs(DoorHandler.doorY[d] - getPosition().getY()) <= 120) {
				if (distanceToPoint(DoorHandler.doorX[d], DoorHandler.doorY[d]) < 50) {
					ReplaceObject(DoorHandler.doorX[d], DoorHandler.doorY[d], DoorHandler.doorId[d], DoorHandler.doorFace[d], 0);
				}
			}
		}
	}
	public void triggerTele(int x, int y, int height, boolean prem) {
		triggerTele(x, y, height, prem, 1816);
	}

	public void triggerTele(int x, int y, int height, boolean prem, int emote) {
		if (inDuel || duelStatus == 3 || UsingAgility || System.currentTimeMillis() - lastTeleport < 3000) {
			return;
		}
		if (randomed2) {
			send(new SendMessage("You can't teleport out of here!"));
			return;
		}
		if (inWildy()) {
			send(new SendMessage("You can't teleport out of the wilderness!"));
			return;
		}
		lastTeleport = System.currentTimeMillis();
		resetAction();
		resetWalkingQueue();
		tX = x;
		tY = y;
		tH = height;
		tStage = 1;
		tEmote = emote;
		UsingAgility = true;
	}

	public void openTrade() {
		if (inHeat()) {
			send(new SendMessage("It would not be a wise idea to trade with the heat in the background!"));
			return;
		}
		send(new InventoryInterface(3323, 3321)); // trading window + bag
		inTrade = true;
		tradeRequested = false;
		resetItems(3322);
		resetTItems(3415);
		resetOTItems(3416);
		String out = PlayerHandler.players[trade_reqId].getPlayerName();
		if (PlayerHandler.players[trade_reqId].playerRights == 1) {
			out = "@cr1@" + out;
		} else if (PlayerHandler.players[trade_reqId].playerRights == 2) {
			out = "@cr2@" + out;
		}
		send(new SendString("Trading With: " + PlayerHandler.players[trade_reqId].getPlayerName(), 3417));
		send(new SendString("", 3431));
		send(new SendString("Are you sure you want to make this trade?", 3535));
	}

	public void declineTrade() {
		declineTrade(true);
	}

	public void declineTrade(boolean tellOther) {
		Client other = getClient(trade_reqId);
		/* Prevent a dupe? */
		inTrade = false;
		if (tellOther && validClient(trade_reqId)) {
			other.declineTrade(false);
			other.inTrade = false;
		}
		/* Clear the trade! */
		for (GameItem item : offeredItems) {
			if (item.getAmount() > 0) {
				if (Server.itemManager.isStackable(item.getId())) {
					addItem(item.getId(), item.getAmount());
				} else {
					for (int i = 0; i < item.getAmount(); i++) {
						addItem(item.getId(), 1);
					}
				}
			}
		}
		send(new RemoveInterfaces());
		canOffer = true;
		tradeConfirmed = false;
		tradeConfirmed2 = false;
		offeredItems.clear();
		trade_reqId = -1;
	}

	public boolean validClient(int index) {
		Client p = (Client) PlayerHandler.players[index];
		return p != null && !p.disconnected && p.dbId > 0;
	}

	public Client getClient(int index) {
		return index < 0 ? null : ((Client) PlayerHandler.players[index]);
	}

	public void tradeReq(int id) {
		// followPlayer(id);
		facePlayer(id);
		if (!Server.trading) {
			send(new SendMessage("Trading has been temporarily disabled"));
			return;
		}
		for (int a = 0; a < PlayerHandler.players.length; a++) {
			Client o = getClient(a);
			if (a != getSlot() && validClient(a) && o.dbId > 0 && o.dbId == dbId) {
				logout();
			}
		}
		Client other = (Client) PlayerHandler.players[id];
		if (validClient(trade_reqId)) {
			setFocus(other.getPosition().getX(), other.getPosition().getY());
			if (inTrade || inDuel || other.inTrade || other.inDuel) {
				send(new SendMessage("That player is busy at the moment"));
				trade_reqId = 0;
				return;
			}
			if (tradeLocked && other.playerRights < 1) {
				return;
			}
		}
		if (dbId == other.dbId) {
			return;
		}
		/*
		 * if(other.connectedFrom.equals(connectedFrom) &&
		 * !connectedFrom.equals("127.0.0.1")){ tradeRequested = false; return; }
		 */
		if (validClient(trade_reqId) && !inTrade && other.tradeRequested && other.trade_reqId == getSlot()) {
			openTrade();
			other.openTrade();
		} else if (validClient(trade_reqId) && !inTrade && System.currentTimeMillis() - lastButton > 1000) {
			lastButton = System.currentTimeMillis();
			tradeRequested = true;
			trade_reqId = id;
			send(new SendMessage("Sending trade request..."));
			other.send(new SendMessage(getPlayerName() + ":tradereq:"));
		}
	}

	public void confirmScreen() {
		canOffer = false;
		send(new InventoryInterface(3443, 3213)); // trade confirm + normal bag
		inTrade = true;
		resetItems(3214);
		StringBuilder SendTrade = new StringBuilder("Absolutely nothing!");
		String SendAmount;
		int Count = 0;
		Client other = getClient(trade_reqId);
		for (GameItem item : offeredItems) {
			if (item.getId() > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utils.format(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Utils.format(item.getAmount())
							+ ")";
				} else {
					SendAmount = "" + Utils.format(item.getAmount());
				}
				if (Count == 0) {
					SendTrade = new StringBuilder(GetItemName(item.getId()));
				} else {
					SendTrade.append("\\n").append(GetItemName(item.getId()));
				}
				if (item.isStackable()) {
					SendTrade.append(" x ").append(SendAmount);
				}
				Count++;
			}
		}
		send(new SendString(SendTrade.toString(), 3557));
		SendTrade = new StringBuilder("Absolutely nothing!");
		Count = 0;
		for (GameItem item : other.offeredItems) {
			if (item.getId() > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utils.format(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Utils.format(item.getAmount())
							+ ")";
				} else {
					SendAmount = "" + Utils.format(item.getAmount());
				}
				// SendAmount = SendAmount;
				if (Count == 0) {
					SendTrade = new StringBuilder(GetItemName(item.getId()));
				} else {
					SendTrade.append("\\n").append(GetItemName(item.getId()));
				}
				if (Server.itemManager.isStackable(item.getId())) {
					SendTrade.append(" x ").append(SendAmount);
				}
				Count++;
			}
		}
		send(new SendString(SendTrade.toString(), 3558));
	}

	public void giveItems() {
		Client other = getClient(trade_reqId);
		if (validClient(trade_reqId)) {
			try {
				CopyOnWriteArrayList<GameItem> offerCopy = new CopyOnWriteArrayList<>();
				CopyOnWriteArrayList<GameItem> otherOfferCopy = new CopyOnWriteArrayList<>();
				for (GameItem item : other.offeredItems) {
					otherOfferCopy.add(new GameItem(item.getId(), item.getAmount()));
				}
				for (GameItem item : offeredItems) {
					offerCopy.add(new GameItem(item.getId(), item.getAmount()));
				}
				for (GameItem item : other.offeredItems) {
					if (item.getId() > 0) {
						addItem(item.getId(), item.getAmount());
						println_debug("TradeConfirmed, item=" + item.getId());
					}
				}
				if (this.dbId > other.dbId)
					TradeLog.recordTrade(dbId, other.dbId, offerCopy, otherOfferCopy, true);
				send(new RemoveInterfaces());
				tradeResetNeeded = true;
				saveStats(false);
				tradeSuccessful = true;
				//System.out.println("trade succesful");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void resetTrade() {
		offeredItems.clear();
		inTrade = false;
		trade_reqId = 0;
		canOffer = true;
		tradeConfirmed = false;
		tradeConfirmed2 = false;
		send(new RemoveInterfaces());
		tradeResetNeeded = false;
		send(new SendString("Are you sure you want to make this trade?", 3535));
	}

	public void duelReq(int pid) {
		facePlayer(pid);
		Client other = getClient(pid);
		if (inWildy() || other.inWildy()) {
			send(new SendMessage("You cant duel in the wilderness!"));
			return;
		}
		if (!Server.dueling) {
			send(new SendMessage("Dueling has been temporarily disabled"));
			return;
		}
		for (int a = 0; a < PlayerHandler.players.length; a++) {
			Client o = getClient(a);
			if (a != getSlot() && validClient(a) && o.dbId > 0 && o.dbId == dbId) {
				logout();
			}
		}
		duel_with = pid;
		duelRequested = true;
		if (!validClient(duel_with)) {
			return;
		}
		setFocus(other.getPosition().getX(), other.getPosition().getY());
		if (inTrade || inDuel || other.inDuel || other.inTrade || other.duelFight || other.duelConfirmed
				|| other.duelConfirmed2) {
			send(new SendMessage("Other player is busy at the moment"));
			duelRequested = false;
			return;
		}
		if (tradeLocked && other.playerRights < 1) {
			return;
		}
		if (other.connectedFrom.equals(connectedFrom) && getGameWorldId() == 1) { //We do not wish for others to duel?!
			duelRequested = false;
			return;
		}
		if (duelRequested && other.duelRequested && duel_with == other.getSlot() && other.duel_with == getSlot()) {
			openDuel();
			other.openDuel();
		} else {
			send(new SendMessage("Sending duel request..."));
			other.send(new SendMessage(getPlayerName() + ":duelreq:"));
		}
	}

	public void openDuel() {
		RefreshDuelRules();
		refreshDuelScreen();
		inDuel = true;
		Client other = getClient(duel_with);
		send(new SendString("Dueling with: " + other.getPlayerName() + " (level-" + other.determineCombatLevel() + ")", 6671));
		send(new SendString("", 6684));
		send(new InventoryInterface(6575, 3321));
		resetItems(3322);
		sendArmour();
	}

	public void declineDuel() {
		Client other = getClient(duel_with);
		inDuel = false;
		if (validClient(duel_with) && other.inDuel) {
			other.declineDuel();
		}
		send(new RemoveInterfaces());
		canOffer = true;
		duel_with = 0;
		duelRequested = false;
		duelConfirmed = false;
		duelConfirmed2 = false;
		duelFight = false;
		for (GameItem item : offeredItems) {
			if (item.getAmount() < 1) {
				continue;
			}
			println_debug("adding " + item.getId() + ", " + item.getAmount());
			if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
				addItem(item.getId(), item.getAmount());
			} else {
				addItem(item.getId(), 1);
			}
		}
		offeredItems.clear();
		/*
		 * Danno: Reset's duel options when duel declined to stop scammers.
		 */
		resetDuel();
		RefreshDuelRules();
		failer = "";
	}

	public void confirmDuel() {
		Client other = getClient(duel_with);
		if (!validClient(duel_with)) {
			declineDuel();
		}
		StringBuilder out = new StringBuilder();
		for (GameItem item : offeredItems) {
			if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
				out.append(GetItemName(item.getId())).append(" x ").append(Utils.format(item.getAmount())).append(", ");
			} else {
				out.append(GetItemName(item.getId())).append(", ");
			}
		}
		send(new SendString(out.toString(), 6516));
		out = new StringBuilder();
		for (GameItem item : other.offeredItems) {
			if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
				out.append(GetItemName(item.getId())).append(" x ").append(Utils.format(item.getAmount())).append(", ");
			} else {
				out.append(GetItemName(item.getId())).append(", ");
			}
		}
		send(new SendString(out.toString(), 6517));
		send(new SendString("Movement will be disabled", 8242));
		for (int i = 8243; i <= 8253; i++) {
			send(new SendString("", i));
		}
		send(new SendString("Hitpoints will be restored", 8250));
		send(new SendString("", 6571));
		showInterface(6412);
	}

	public void startDuel() {
		canAttack = false;
		// canAttack = true;
		canOffer = false;
		send(new RemoveInterfaces());
		duelFight = true;
		prayers.reset();
		GetBonus(true); //Set bonus due to blessing!
		for(int i = 0; i < boostedLevel.length; i++) {
			boostedLevel[i] = 0;
			refreshSkill(Skills.getSkill(i));
		}
		Client other = getClient(duel_with);
		for (GameItem item : other.offeredItems) {
			otherOfferedItems.add(new GameItem(item.getId(), item.getAmount()));
		}
		otherdbId = other.dbId;

		final Client player = this;
		EventManager.getInstance().registerEvent(new Event(1000) {
			long start = System.currentTimeMillis();

			public void execute() {
				long now = System.currentTimeMillis();
				if (now - start >= 4000) {
					player.requestForceChat("Fight!");
					player.canAttack = true;
					stop();
				} else if (now - start >= 3000) {
					player.requestForceChat("1");
				} else if (now - start >= 2000) {
					player.requestForceChat("2");
				} else if (now - start >= 1000) {
					player.requestForceChat("3");

				}
			}
		});
	}

	/*
	 * Danno: Edited for new duel rules, for future use.
	 */
	public void resetDuel() {
		send(new RemoveInterfaces());
		duelWin = false;
		canOffer = true;
		duel_with = 0;
		duelRequested = false;
		duelConfirmed = false;
		duelConfirmed2 = false;
		offeredItems.clear();
		otherOfferedItems.clear();
		duelFight = false;
		canAttack = true;
		inDuel = false;
		duelRule = new boolean[]{false, false, false, false, false, true, true, true, true, true, true};
		Arrays.fill(duelBodyRules, false);
		otherdbId = -1;
	}

	public void frame36(int Interface, int Status) {
		getOutputStream().createFrame(36);
		getOutputStream().writeWordBigEndian(Interface); // The button
		getOutputStream().writeByte(Status); // The Status of the button
	}

	public void frame87(int Interface, int Status) {
		getOutputStream().createFrame(87);
		getOutputStream().writeWordBigEndian(Interface); // The button
		getOutputStream().writeDWord_v1(Status); // The Status of the button
	}

	public boolean duelButton(int button) {
		Client other = getClient(duel_with);
		boolean found = false;
		if (System.currentTimeMillis() - lastButton < 800) {
			return false;
		}
		if (inDuel && !duelFight && !duelConfirmed2 && !other.duelConfirmed2 && !(duelConfirmed && other.duelConfirmed)) {
			for (int i = 0; i < duelButtons.length; i++) {
				if (button == duelButtons[i]) {
					found = true;
					if (duelRule[i]) {
						duelRule[i] = false;
						other.duelRule[i] = false;
					} else {
						duelRule[i] = true;
						other.duelRule[i] = true;
					}
				}
			}
			if (found) {
				lastButton = System.currentTimeMillis();
				duelConfirmed = false;
				other.duelConfirmed = false;
				send(new SendString("", 6684));
				other.send(new SendString("", 6684));

				other.duelRule[i] = duelRule[i];
				RefreshDuelRules();
				other.RefreshDuelRules();
			}
		}
		return found;
	}

	public boolean duelButton2(int button) {
		Client other = getClient(duel_with);
		/*
		 * Danno: Null check :p
		 */
		if (other == null)
			return false;
		boolean found = false;
		if (System.currentTimeMillis() - lastButton < 400) {
			return false;
		}
		if (inDuel && !duelFight && !duelConfirmed2 && !other.duelConfirmed2 && !(duelConfirmed && other.duelConfirmed)) {
			if (duelBodyRules[button]) {
				duelBodyRules[button] = false;
				other.duelBodyRules[button] = false;
			} else {
				duelBodyRules[button] = true;
				other.duelBodyRules[button] = true;
			}
			lastButton = System.currentTimeMillis();
			duelConfirmed = false;
			other.duelConfirmed = false;
			send(new SendString("", 6684));
			other.send(new SendString("", 6684));
			other.duelBodyRules[i] = duelBodyRules[i];
			RefreshDuelRules();
			other.RefreshDuelRules();
		}
		return found;
	}

	public void addFriend(long name) {
		// On = 0, Friends = 1, Off = 2
		for (Friend f : friends) {
			if (f.name == name) {
				send(new SendMessage(Utils.longToPlayerName(name) + " is already on your friends list"));
				return;
			}
		}
		friends.add(new Friend(name, true));
		for (Client c : PlayerHandler.playersOnline.values()) {
			if (c.hasFriend(longName)) {
				c.refreshFriends();
			}
		}
		refreshFriends();
	}

	public void sendPmMessage(long friend, byte[] pmchatText, int pmchatTextSize) {
		if (muted) {
			return;
		}
		boolean found = false;
		for (Friend f : friends) {
			if (f.name == friend) {
				found = true;
				break;
			}
		}
		if (!found) {
			send(new SendMessage("That player is not on your friends list"));
			return;
		}
		if (PlayerHandler.playersOnline.containsKey(friend)) {
			Client to = PlayerHandler.playersOnline.get(friend);
			boolean specialRights = to.playerGroup == 6;
			if (specialRights && to.busy && playerRights < 1) {
				send(new SendMessage("<col=FF0000>This player is busy and did not receive your message."));
				//send(new SendMessage("Please only report glitch/bugs to him/her on the forums"));
				return;
			}
			if (to.Privatechat == 0 || (to.Privatechat == 1 && to.hasFriend(longName))) {
				to.sendpm(longName, playerRights, pmchatText, pmchatTextSize);
				PmLog.recordPm(this.getPlayerName(), to.getPlayerName(), Utils.textUnpack(pmchatText, pmchatTextSize));
			} else if (PlayerHandler.allOnline.containsKey(friend)) { //Not sure why we need this code!
			} else {
				send(new SendMessage("That player is not available"));
			}
		} else {
			send(new SendMessage("That player is not online"));
		}
		/*
		 * for (int i1 = 0; i1 < handler.players.length; i1++) { client to =
		 * getClient(i1); if (validClient(i1)) { if (validClient(i1) && to.dbId > 0
		 * && misc.playerNameToInt64(to.playerName) == friend) { if (to.Privatechat
		 * == 0 || (to.Privatechat == 1 && to.hasFriend(misc
		 * .playerNameToInt64(playerName)))) { // server.login.sendChat(dbId,
		 * to.dbId, 3, absX, absY, // ); if (to.officialClient || to.okClient) {
		 * to.sendpm(misc.playerNameToInt64(playerName), playerRights, pmchatText,
		 * pmchatTextSize); } else { to .send(new SendMessage(
		 * "A player has tried to send you a private message"); to .send(new
		 * SendMessage(
		 * "For technical reasons, you must use the dodian client for friends list features"
		 * ); } sent = true; break; } } } } if (!sent) { send(new SendMessage(
		 * "Could not find player"); }
		 */
	}

	public boolean hasFriend(long name) {
		for (Friend f : friends) {
			if (f.name == name) {
				return true;
			}
		}
		return false;
	}

	public void refreshFriends() {
		for (Friend f : friends) {
			if (PlayerHandler.allOnline.containsKey(f.name)) {
				boolean ignored = false;
				for (Player p : PlayerHandler.players) {
					if (p == null)
						continue;
					if (p.longName == f.name) {
						Client player = (Client) p;
						for (Friend ignore : player.ignores) {
							ignored = ignore.name == this.longName;
						}
					}
				}
				if (!ignored)
					loadpm(f.name, 1);
				else
					loadpm(f.name, 0);
			} else {
				loadpm(f.name, 0);
			}
		}
	}

	public void removeFriend(long name) {
		for (Friend f : friends) {
			if (f.name == name) {
				friends.remove(f);
				refreshFriends();
				return;
			}
		}
	}

	public void removeIgnore(long name) {
		for (Friend f : ignores) {
			if (f.name == name) {
				ignores.remove(f);
				refreshFriends();
				if (PlayerHandler.allOnline.containsKey(f.name)) {
					for (Player p : PlayerHandler.players) {
						if (p == null)
							continue;
						if (p.longName == f.name) {
							Client player = (Client) p;
							player.refreshFriends();
						}
					}
				}
				break;
			}
		}
	}

	public void addIgnore(long name) {
		boolean canAdd = true;
		for (Friend f : ignores) {
			if (f.name == name) {
				send(new SendMessage("You already got this guy on your ignoreList!"));
				canAdd = false;
				break;
			}
		}
		if (canAdd) {
			if (ignores.size() < 100) {
				ignores.add(new Friend(name, true));
				if (PlayerHandler.allOnline.containsKey(name)) {
					for (Player p : PlayerHandler.players) {
						if (p == null)
							continue;
						if (p.longName == name) {
							Client player = (Client) p;
							player.refreshFriends();
						}
					}
				}
			} else
				send(new SendMessage("Maximum ignores reached!"));
		}
	}

	public void callGfxMask(int id, int height) {
		setGraphic(id, height == 0 ? 65536 : 65536 * height);
		getUpdateFlags().setRequired(UpdateFlag.GRAPHICS, true);
	}

	public void AddToCords(int X, int Y) {
		newWalkCmdSteps = Math.abs(X + Y);
		if (newWalkCmdSteps % 2 != 0 && newWalkCmdSteps > 1) {
			newWalkCmdSteps /= 2;
		}
		if (++newWalkCmdSteps > 50) {
			newWalkCmdSteps = 0;
		}
		int l = getPosition().getX();
		l -= mapRegionX * 8;
		for (i = 1; i < newWalkCmdSteps; i++) {
			newWalkCmdX[i] = X;
			newWalkCmdY[i] = Y;
			tmpNWCX[i] = newWalkCmdX[i];
			tmpNWCY[i] = newWalkCmdY[i];
		}
		newWalkCmdX[0] = newWalkCmdY[0] = tmpNWCX[0] = tmpNWCY[0] = 0;
		int j1 = getPosition().getY();
		j1 -= mapRegionY * 8;
		newWalkCmdIsRunning = false;
		for (i = 0; i < newWalkCmdSteps; i++) {
			newWalkCmdX[i] += l;
			newWalkCmdY[i] += j1;
		}
	}

	public void AddToCords(int X, int Y, long time) {
		walkBlock = System.currentTimeMillis() + time;
		AddToCords(X, Y);
	}

	public void startAttack(Entity enemy) {
		target = enemy;
		if(target instanceof Npc) {
			attackingNpc = true;
			faceNpc(target.getSlot());
			//System.out.println("test for npc slot: " + target.getSlot());
		} else {
			Server.playerHandler.getClient(target.getSlot()).target = this;
			attackingPlayer = true;
			facePlayer(target.getSlot());
			//System.out.println("test for player slot: " + target.getSlot());
		}
	}

	public void resetAttack() {
		rerequestAnim();
		if (target instanceof Npc)
			attackingNpc = false;
		else
			attackingPlayer = false;
		target = null;
		faceTarget(65535);
	}

	private void requestAnims(int wearID) {
		setStandAnim(Server.itemManager.getStandAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
		setWalkAnim(Server.itemManager.getWalkAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
		setRunAnim(Server.itemManager.getRunAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
	}

	public int getWildLevel() {
		int lvl = 0;
		if (getPosition().getY() >= 3524 && getPosition().getY() < 3904 && getPosition().getX() >= 2954
				&& getPosition().getX() <= 3327)
			lvl = (((getPosition().getY() - 3520) / 8)) + 1;
		return lvl;
	}

	public void setWildLevel(int level) {
		wildyLevel = level;
		getOutputStream().createFrame(208);
		getOutputStream().writeWordBigEndian_dup(197);
		send(new SendString("Level: " + wildyLevel, 199));
	}

	public void updatePlayerDisplay() {
		String serverName = getGameWorldId() == 1 ? "Dodian" : "Beta World";
		send(new SendString("               " + serverName + " (" + PlayerHandler.getPlayerCount() + " online)", 6570));
		send(new SendString("", 6664));
		setInterfaceWalkable(6673);
	}

	public void playerKilled(Client other) {
		other.setSkullIcon(1);
		other.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
	}

	public void setSnared(int time) {
		snaredUntil = System.currentTimeMillis() + time;
		stillgfx(617, getPosition().getX(), getPosition().getY());
		send(new SendMessage("You have been snared!"));
		resetWalkingQueue();
	}

	public void died() {
		int highestDmg = 0, slot = -1;
		for (Entity e : getDamage().keySet()) {
			if (getDamage().get(e) > highestDmg) {
				highestDmg = getDamage().get(e);
				slot = e.getSlot();
			}
		}
		getDamage().clear();
		if(slot >= 0) {
			Ground.items.add(new GroundItem(getPosition().copy(), 526, 1, slot, -1));
			if (validClient(slot)) {
				getClient(slot).send(new SendMessage("You have defeated " + getPlayerName() + "!"));
				yellKilled(getClient(slot).getPlayerName() + " has just slain " + getPlayerName() + " in the wild!");
				Client other = getClient(slot);
				playerKilled(other);
			}
		}
		/* Stuff dropped to the floor! */
      /*for (int i = 0; i < getEquipment().length; i++) {
        if (getEquipment()[i] > 0) {
          if (Server.itemManager.isTradable(getEquipment()[i]))
            Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), getEquipment()[i],
                    getEquipmentN()[i], slot, -1));
          else
            Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), getEquipment()[i],
                    getEquipmentN()[i], getSlot(), -1));
        }
        getEquipment()[i] = -1;
        getEquipmentN()[i] = 0;
        deleteequiment(0, i);
      }
      for (int i = 0; i < playerItems.length; i++) {
        if (playerItems[i] > 0) {
          if (Server.itemManager.isTradable((playerItems[i] - 1)))
            Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), (playerItems[i] - 1),
                    playerItemsN[i], slot, -1));
          else
            Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), (playerItems[i] - 1),
                    playerItemsN[i], getSlot(), -1));
        }
        deleteItem((playerItems[i] - 1), i, playerItemsN[i]);
      }*/
	}

	public void acceptDuelWon() {
		if (duelFight && duelWin) {
			duelWin = false;
			if (System.currentTimeMillis() - lastButton < 1000) {
				lastButton = System.currentTimeMillis();
				return;
			} else {
				lastButton = System.currentTimeMillis();
			}
			Client other = getClient(duel_with);
			CopyOnWriteArrayList<GameItem> offerCopy = new CopyOnWriteArrayList<GameItem>();
			CopyOnWriteArrayList<GameItem> otherOfferCopy = new CopyOnWriteArrayList<GameItem>();
			for (GameItem item : otherOfferedItems) {
				otherOfferCopy.add(new GameItem(item.getId(), item.getAmount()));
			}
			for (GameItem item : offeredItems) {
				offerCopy.add(new GameItem(item.getId(), item.getAmount()));
			}
			for (GameItem item : otherOfferedItems) {
				if (item.getId() > 0 && item.getAmount() > 0) {
					if (Server.itemManager.isStackable(item.getId())) {
						addItem(item.getId(), item.getAmount());
					} else {
						addItem(item.getId(), 1);
					}
				}
			}
			for (GameItem item : offeredItems) {
				if (item.getId() > 0 && item.getAmount() > 0) {
					addItem(item.getId(), item.getAmount());
				}
			}
			if (this.dbId > other.dbId)
				TradeLog.recordTrade(dbId, otherdbId, offerCopy, otherOfferCopy, false);
			resetDuel();
			GetBonus(true);
			saveStats(false);
			if (validClient(duel_with)) {
				other.resetDuel();
				GetBonus(true);
				other.saveStats(false);
			}
		}
	}

	public boolean contains(int item) {
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItems[i] == item + 1)
				return true;
		}
		return false;
	}

	public void setConfigIds() {
		stakeConfigId[0] = 16384; // No head armour
		stakeConfigId[1] = 32768; // No capes
		stakeConfigId[2] = 65536; // No amulets
		stakeConfigId[3] = 134217728; // No arrows
		stakeConfigId[4] = 131072; // No weapon
		stakeConfigId[5] = 262144; // No body armour
		stakeConfigId[6] = 524288; // No shield
		stakeConfigId[7] = 2097152; // No leg armour
		stakeConfigId[8] = 67108864; // No hand armour
		stakeConfigId[9] = 16777216; // No feet armour
		stakeConfigId[10] = 8388608; // No rings
		stakeConfigId[11] = 16; // No ranging
		stakeConfigId[12] = 32; // No melee
		stakeConfigId[13] = 64; // No magic
		stakeConfigId[14] = 8192; // no gear change
		stakeConfigId[15] = 4096; // fun weapons
		stakeConfigId[16] = 1; // no retreat
		stakeConfigId[17] = 128; // No drinks
		stakeConfigId[18] = 256; // No food
		stakeConfigId[19] = 512; // No prayer
		stakeConfigId[20] = 2; // movement
		stakeConfigId[21] = 1024; // obstacles
		stakeConfigId[22] = -1; // No specials
	}

	/**
	 * Shows armour in the duel screen slots! (hopefully lol)
	 */
	public void sendArmour() {
		for (int e = 0; e < getEquipment().length; e++) {
			getOutputStream().createFrameVarSizeWord(34);
			getOutputStream().writeWord(13824);
			getOutputStream().writeByte(e);
			getOutputStream().writeWord(getEquipment()[e] + 1);
			if (getEquipmentN()[e] > 254) {
				getOutputStream().writeByte(255);
				getOutputStream().writeDWord(getEquipmentN()[e]);
			} else {
				getOutputStream().writeByte(getEquipmentN()[e]); // amount
			}
			getOutputStream().endFrameVarSizeWord();
		}
	}

	public boolean hasTradeSpace() {
		if (!validClient(trade_reqId)) {
			return false;
		}
		Client o = getClient(trade_reqId);
		int spaces = 0;
		ArrayList<GameItem> items = new ArrayList<GameItem>();
		for (GameItem item : o.offeredItems) {
			if (item == null)
				continue;
			if (item.getAmount() > 0) {
				if (!items.contains(item)) {
					items.add(item);
					spaces += 1;
				} else {
					if (!item.isStackable()) {
						spaces += 1;
					}
				}
			}
		}
		if (spaces > getFreeSpace()) {
			failer = getPlayerName() + " does not have enough space to hold items being traded.";
			o.failer = getPlayerName() + " does not have enough space to hold items being traded.";
			return false;
		}
		return true;
	}

	/**
	 * @return if player has enough space to remove items.
	 */
	public boolean hasEnoughSpace() {
		if (!inDuel || !validClient(duel_with)) {
			return false;
		}
		Client o = getClient(duel_with);
		int spaces = 0;
		for (int i = 0; i < duelBodyRules.length; i++) {
			if (!duelBodyRules[i])
				continue;
			if (getEquipmentN()[trueSlots[i]] > 0) {
				spaces += 1;
			}
		}
		ArrayList<GameItem> items = new ArrayList<GameItem>();
		for (GameItem item : offeredItems) {
			if (item == null)
				continue;
			if (item.getAmount() > 0) {
				if (!items.contains(item)) {
					items.add(item);
					spaces += 1;
				} else {
					if (!item.isStackable()) {
						spaces += 1;
					}
				}
			}
		}
		for (GameItem item : o.offeredItems) {
			if (item == null)
				continue;
			if (item.getAmount() > 0) {
				if (!items.contains(item)) {
					items.add(item);
					spaces += 1;
				} else {
					if (!Server.itemManager.isStackable(item.getId())) {
						spaces += 1;
					}
				}
			}
		}
		if (spaces > getFreeSpace()) {
			failer = getPlayerName() + " does not have enough space to hold items being removed and/or staked.";
			o.failer = getPlayerName() + " does not have enough space to hold items being removed and/or staked.";
			return false;
		}
		return true;

	}

	public void removeEquipment() {
		for (int i = 0; i < duelBodyRules.length; i++) {
			if (!duelBodyRules[i])
				continue;
			if (getEquipmentN()[trueSlots[i]] > 0) {
				int id = getEquipment()[trueSlots[i]];
				int amount = getEquipmentN()[trueSlots[i]];
				if(remove(trueSlots[i], true))
					addItem(id, amount);
			}
		}
	}

	public void requestForceChat(String s) {
		forcedChat = s;
		getUpdateFlags().setRequired(UpdateFlag.FORCED_CHAT, true);
	}

	/**
	 * @param skillX the skillX to set
	 */
	public void setSkillX(int skillX) {
		this.skillX = skillX;
	}

	/**
	 * @param skillY the skillY to set
	 */
	public void setSkillY(int skillY) {
		this.skillY = skillY;
		if (WanneBank > 0)
			WanneBank = 0;
		if (NpcWanneTalk > 0)
			NpcWanneTalk = 0;
	}

	public void setPots(long time, int id1, int id2, int id3, int xp) {
		mixPotAmt = 14;
		potTime = time;
		mixPotId1 = id1;
		mixPotId2 = id2;
		mixPotId3 = id3;
		mixPotXp = xp;
		mixPots = true;
	}

	public void mixPots() {
		if (mixPotAmt < 1) {
			resetAction(true);
			return;
		}
		if (!playerHasItem(mixPotId1, 1) || !playerHasItem(mixPotId2, 1)) {
			resetAction(true);
			return;
		}
		mixPotAmt--;
		IsBanking = false;
		this.requestAnim(363, 0);
		getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		deleteItem(mixPotId1, 1);
		deleteItem(mixPotId2, 1);
		addItem(mixPotId3, 1);
		giveExperience(mixPotXp, Skills.HERBLORE);
		triggerRandom(mixPotXp);
	}

	public void guideBook() {
		send(new SendMessage("this is me a guide book!"));
		clearQuestInterface();
		showInterface(8134);
		send(new SendString("Newcomer's Guide", 8144));
		send(new SendString("---------------------------", 8145));
		send(new SendString("Welcome to Dodian.net!", 8147));
		send(new SendString("This guide is to help new players to get a general", 8148));
		send(new SendString("understanding of how Dodian works!", 8149));
		send(new SendString("", 8150));
		send(new SendString("For specific boss or skill locations", 8151));
		send(new SendString("navigate to the 'Guides' section of the forums.", 8152));
		send(new SendString("", 8153));
		send(new SendString("Here in Yanille, there are various enemies to kill,", 8154));
		send(new SendString("with armor rewards that get better the higher their level.", 8155));
		send(new SendString("", 8156));
		send(new SendString("From Yanille, you can also head North-East to access", 8157));
		send(new SendString("the mining area or South-West", 8158));
		send(new SendString("up the stairs in the magic guild to access the essence mine.", 8159));
		send(new SendString("", 8160));
		send(new SendString("If you navigate over to your spellbook, you will see", 8161));
		send(new SendString("some teleports, these all lead to key points on the server", 8162));
		send(new SendString("", 8163));
		send(new SendString("Seers, Catherby, Fishing Guild, and Gnome Stronghold", 8164));
		send(new SendString("teleports will all bring you to skilling locations.", 8165));
		send(new SendString("", 8166));
		send(new SendString("Legends Guild, and Taverly teleports", 8167));
		send(new SendString("will all bring you to locations with more monsters to train on.", 8168));
		send(new SendString("", 8169));
		send(new SendString("Teleporting to Taverly and heading up the path", 8170));
		send(new SendString("is how you access the Slayer Master!", 8171));
		send(new SendString("", 8172));
		send(new SendString("If you have more questions please visit the 'Guides'", 8173));
		send(new SendString("section of the forums, and if you still can't find the answer.", 8174));
		send(new SendString("Feel free to just ask a moderator!", 8175));
		send(new SendString("---------------------------", 8176));
	}

	private long lastVoted;

	public Prayers getPrayerManager() {
		return prayers;
	}

	public void deleteRunes(int[] runes, int[] qty) {
		for (int i = 0; i < runes.length; i++) {
			deleteItem(runes[i], qty[i]);
		}
	}

	public boolean hasRunes(int[] runes, int[] qty) {
		for (int i = 0; i < runes.length; i++) {
			if (!playerHasItem(runes[i], qty[i])) {
				return false;
			}
		}
		return true;
	}

	public void checkBow() {
		int weaponId = getEquipment()[Equipment.Slot.WEAPON.getId()];
		usingBow = bowWeapon(weaponId);
	}

	public boolean bowWeapon(int weaponId) {
		boolean bow = false;
		for (int i = 0; i < Constants.shortbow.length && !bow; i++)
			if (weaponId == Constants.shortbow[i] || weaponId == Constants.longbow[i])
				bow = true;
		if (weaponId == 4212 || weaponId == 6724 || weaponId == 20997 ||
				weaponId == 11235 || (weaponId >= 12765 && weaponId <= 12768))
			bow = true;
		return bow;
	}

	public void openUpOtherInventory(String player) {
		if (IsBanking || IsShopping || duelFight) {
			send(new SendMessage("Please finish with what you are doing!"));
			return;
		}
		ArrayList<GameItem> otherInv = new ArrayList<GameItem>();
		if (PlayerHandler.getPlayer(player) != null) { //Online check
			Client other = (Client) PlayerHandler.getPlayer(player);
			for (int i = 0; i < other.playerItems.length; i++) {
				otherInv.add(i, new GameItem(other.playerItems[i] - 1, other.playerItemsN[i]));
			}
			sendInventory(3214, otherInv);
			send(new SendMessage("User " + player + "'s inventory is now being shown."));
			checkInv = true;
		} else {
			try {
				java.sql.Connection conn = getDbConnection();
				Statement statement = conn.createStatement();
				String query = "SELECT * FROM " + DbTables.WEB_USERS_TABLE + " WHERE username = '" + player + "'";
				ResultSet results = statement.executeQuery(query);
				int id = -1;
				if (results.next())
					id = results.getInt("userid");
				if (id >= 0) {
					query = "SELECT * FROM " + DbTables.GAME_CHARACTERS + " WHERE id = " + id + "";
					results = statement.executeQuery(query);
					if (results.next()) {
						String text = results.getString("inventory");
						if (text != null && text.length() > 2) {
							String lines[] = text.split(" ");
							for (int i = 0; i < lines.length; i++) {
								String[] parts = lines[i].split("-");
								@SuppressWarnings("unused")
								int slot = Integer.parseInt(parts[0]);
								int item = Integer.parseInt(parts[1]);
								int amount = Integer.parseInt(parts[2]);
								otherInv.add(new GameItem(item, amount));
							}
						}
						sendInventory(3214, otherInv);
						send(new SendMessage("User " + player + "'s inventory is now being shown."));
						checkInv = true;
					} else
						send(new SendMessage("username '" + player + "' have yet to login!"));
				} else
					send(new SendMessage("username '" + player + "' do not exist in the database!"));
				statement.close();
			} catch (Exception e) {
				System.out.println("issue: " + e.getMessage());
			}
		}
	}

	public void openUpOtherBank(String player) {
		if (IsBanking || IsShopping || duelFight) {
			send(new SendMessage("Please finish with what you are doing!"));
			return;
		}
		ArrayList<GameItem> otherBank = new ArrayList<>();
		IsBanking = false;
		if (PlayerHandler.getPlayer(player) != null) { //Online check
			Client other = (Client) PlayerHandler.getPlayer(player);
			for (int i = 0; i < other.bankItems.length; i++) {
				otherBank.add(i, new GameItem(other.bankItems[i] - 1, other.bankItemsN[i]));
			}
			send(new SendString("Examine the bank of " + player, 5383));
			sendBank(5382, otherBank);
			send(new InventoryInterface(5292, 5063));
			IsBanking = false;
			checkBankInterface = true;
		} else {
			try {
				java.sql.Connection conn = getDbConnection();
				Statement statement = conn.createStatement();
				String query = "SELECT * FROM " + DbTables.WEB_USERS_TABLE + " WHERE username = '" + player + "'";
				ResultSet results = statement.executeQuery(query);
				int id = -1;
				if (results.next())
					id = results.getInt("userid");
				if (id >= 0) {
					query = "SELECT * FROM " + DbTables.GAME_CHARACTERS + " WHERE id = " + id + "";
					results = statement.executeQuery(query);
					if (results.next()) {
						String text = results.getString("bank");
						if (text != null && text.length() > 2) {
							String lines[] = text.split(" ");
							for (int i = 0; i < lines.length; i++) {
								String[] parts = lines[i].split("-");
								@SuppressWarnings("unused")
								int slot = Integer.parseInt(parts[0]);
								int item = Integer.parseInt(parts[1]);
								int amount = Integer.parseInt(parts[2]);
								otherBank.add(new GameItem(item, amount));
							}
						}
						send(new SendString("Examine the bank of " + player, 5383));
						sendBank(5382, otherBank);
						send(new InventoryInterface(5292, 5063));
						checkBankInterface = true;
					} else
						send(new SendMessage("username '" + player + "' have yet to login!"));
				} else
					send(new SendMessage("username '" + player + "' do not exist in the database!"));
				statement.close();
			} catch (Exception e) {
				System.out.println("issue: " + e.getMessage());
			}
		}
	}

	private ArrayList<GroundItem> displayItems = new ArrayList<>();

	public void updateItems() {
		if (displayItems.size() > 0) {
			for (GroundItem display : displayItems) {
				send(new RemoveGroundItem(new GameItem(display.id, display.amount), new Position(display.x, display.y, display.z)));
			}
			displayItems.clear();
		}
		for (GroundItem ground : Ground.items) {
			if (Math.abs(getPosition().getX() - ground.x) <= 114 && Math.abs(getPosition().getY() - ground.y) <= 114) {
				if (!ground.canDespawn && !ground.taken) {
					send(new CreateGroundItem(new GameItem(ground.id, ground.amount), new Position(ground.x, ground.y, ground.z)));
					displayItems.add(ground);
				} else if (dbId == ground.playerId && ground.canDespawn) {
					send(new CreateGroundItem(new GameItem(ground.id, ground.amount), new Position(ground.x, ground.y, ground.z)));
					displayItems.add(ground);
				} else if (ground.canDespawn && ground.visible && ground.playerId != dbId
						&& Server.itemManager.isTradable(ground.id)) {
					send(new CreateGroundItem(new GameItem(ground.id, ground.amount), new Position(ground.x, ground.y, ground.z)));
					displayItems.add(ground);
				}
			}
		}
	}

	public void sendFrame248(int MainFrame, int SubFrame) {
		getOutputStream().createFrame(248);
		getOutputStream().writeWordA(MainFrame);
		getOutputStream().writeWord(SubFrame);
		//flushOutStream();
	}

	@Override
	public boolean equals(Object o) {
		return ((Client) o).getPlayerName().equalsIgnoreCase(this.getPlayerName());
	}

	public void removeExperienceFromPlayer(String user, int id, int xp) {
		String skillName = Skills.getSkill(id).getName();
		if (PlayerHandler.getPlayer(user) != null) { //Online check
			Client other = (Client) PlayerHandler.getPlayer(user);
			int currentXp = other.getExperience(Skills.values()[id]);
			xp = currentXp >= xp ? xp : currentXp;
			other.setExperience(currentXp - xp, Skills.getSkill(id));
			other.setLevel(Skills.getLevelForExperience(other.getExperience(Skills.values()[id])), Skills.getSkill(id));
			other.refreshSkill(Skills.getSkill(id));
			send(new SendMessage("Removed " + xp + "/" + currentXp + " xp from " + user + "'s " + skillName + "(id:" + id + ")!"));
		} else {
			try {
				boolean found = true;
				int currentXp = 0, totalXp = 0, totalLevel = 0;
				java.sql.Connection conn = getDbConnection();
				Statement statement = conn.createStatement();
				String query = "SELECT * FROM " + DbTables.WEB_USERS_TABLE + " WHERE username = '" + user + "'";
				ResultSet results = statement.executeQuery(query);
				int userid = -1;
				if (results.next())
					userid = results.getInt("userid");
				if (userid >= 0) {
					query = "SELECT * FROM " + DbTables.GAME_CHARACTERS_STATS + " WHERE uid = " + userid + "";
					results = statement.executeQuery(query);
					if (results.next()) {
						currentXp = results.getInt(skillName);
						totalXp = results.getInt("totalxp");
						totalLevel = results.getInt("total");
					}
				} else
					found = false;
				if (found) {
					statement = getDbConnection().createStatement();
					xp = currentXp >= xp ? xp : currentXp;
					int newXp = currentXp - xp;
					totalLevel -= Skills.getLevelForExperience(currentXp) - Skills.getLevelForExperience(newXp);
					totalXp -= xp;
					statement.executeUpdate("UPDATE " + DbTables.GAME_CHARACTERS_STATS + " SET " + skillName + "='" + newXp + "', totalxp='" + totalXp + "', total='" + totalLevel + "' WHERE uid = " + userid);
					send(new SendMessage("Removed " + xp + "/" + currentXp + " xp from " + user + "'s " + skillName + "(id:" + id + ")!"));
				} else
					send(new SendMessage("username '" + user + "' have yet to login!"));
				statement.close();
			} catch (Exception e) {
				System.out.println("issue: " + e.getMessage());
			}
		}
	}

	public void removeItemsFromPlayer(String user, int id, int amount) {
		int totalItemRemoved = 0;
		if (PlayerHandler.getPlayer(user) != null) { //Online check
			Client other = (Client) PlayerHandler.getPlayer(user);
			for (int i = 0; i < other.bankItems.length; i++) {
				if (other.bankItems[i] - 1 == id) {
					int canRemove = other.bankItemsN[i] < amount ? other.bankItemsN[i] : amount;
					other.bankItemsN[i] -= canRemove;
					amount -= canRemove;
					totalItemRemoved += canRemove;
					if (other.bankItemsN[i] <= 0)
						other.bankItems[i] = 0;
					other.resetBank();
				}
			}
			for (int i = 0; i < other.playerItems.length; i++) {
				if (other.playerItems[i] - 1 == id) {
					int canRemove = other.playerItemsN[i] < amount ? other.playerItemsN[i] : amount;
					other.playerItemsN[i] -= canRemove;
					amount -= canRemove;
					totalItemRemoved += canRemove;
					if (other.playerItemsN[i] <= 0)
						other.playerItems[i] = 0;
					if (other.IsBanking || other.isPartyInterface || other.checkBankInterface)
						other.resetItems(5064);
					else if (other.IsShopping)
						other.resetItems(3823);
					else
						other.resetItems(3214);

				}
			}
			for (int i = 0; i < getEquipment().length; i++) {
				if (other.getEquipment()[i] == id) {
					int canRemove = other.getEquipmentN()[i] < amount ? other.getEquipmentN()[i] : amount;
					other.getEquipmentN()[i] -= canRemove;
					amount -= canRemove;
					totalItemRemoved += canRemove;
					if (other.getEquipmentN()[i] <= 0)
						other.getEquipment()[i] = -1;
					other.deleteequiment(0, i);
				}
			}
			if (totalItemRemoved > 0)
				send(new SendMessage("Finished deleting " + totalItemRemoved + " of " + GetItemName(id).toLowerCase()));
			else
				send(new SendMessage("The user '" + user + "' did not had any " + GetItemName(id).toLowerCase()));
		} else { //Database check!
			try {
				boolean found = true;
				java.sql.Connection conn = getDbConnection();
				Statement statement = conn.createStatement();
				String query = "SELECT * FROM " + DbTables.WEB_USERS_TABLE + " WHERE username = '" + user + "'";
				ResultSet results = statement.executeQuery(query);
				int userid = -1;
				if (results.next())
					userid = results.getInt("userid");
				if (userid >= 0) {
					String bank = "", inventory = "", equipment = "";
					query = "SELECT * FROM " + DbTables.GAME_CHARACTERS + " WHERE id = " + userid + "";
					results = statement.executeQuery(query);
					if (results.next()) {
						String text = results.getString("bank");
						if (text != null && text.length() > 2) {
							String[] lines = text.split(" ");
							for (String line : lines) {
								String[] parts = line.split("-");
								int checkItem = Integer.parseInt(parts[1]);
								if (checkItem == id) {
									int canRemove = Integer.parseInt(parts[2]) < amount ? Integer.parseInt(parts[2]) : amount;
									if (canRemove < Integer.parseInt(parts[2]))
										bank += parts[0] + "-" + parts[1] + "-" + (Integer.parseInt(parts[2]) - canRemove) + " ";
									amount -= canRemove;
									totalItemRemoved += canRemove;
								} else
									bank += parts[0] + "-" + parts[1] + "-" + parts[2] + " ";
							}
						}
						text = results.getString("inventory");
						if (text != null && text.length() > 2) {
							String[] lines = text.split(" ");
							for (int i = 0; i < lines.length; i++) {
								String[] parts = lines[i].split("-");
								int checkItem = Integer.parseInt(parts[1]);
								if (checkItem == id) {
									int canRemove = Integer.parseInt(parts[2]) < amount ? Integer.parseInt(parts[2]) : amount;
									if (canRemove < Integer.parseInt(parts[2]))
										inventory += parts[0] + "-" + parts[1] + "-" + (Integer.parseInt(parts[2]) - canRemove) + " ";
									amount -= canRemove;
									totalItemRemoved += canRemove;
								} else
									inventory += parts[0] + "-" + parts[1] + "-" + parts[2] + " ";
							}
						}
						text = results.getString("equipment");
						if (text != null && text.length() > 2) {
							String[] lines = text.split(" ");
							for (String line : lines) {
								String[] parts = line.split("-");
								int checkItem = Integer.parseInt(parts[1]);
								if (checkItem == id) {
									int canRemove = Integer.parseInt(parts[2]) < amount ? Integer.parseInt(parts[2]) : amount;
									if (canRemove < Integer.parseInt(parts[2]))
										equipment += parts[0] + "-" + parts[1] + "-" + (Integer.parseInt(parts[2]) - canRemove) + " ";
									amount -= canRemove;
									totalItemRemoved += canRemove;
								} else
									equipment += parts[0] + "-" + parts[1] + "-" + parts[2] + " ";
							}
						}
					} else
						found = false;
					if (found) {
						statement = getDbConnection().createStatement();
						statement.executeUpdate("UPDATE " + DbTables.GAME_CHARACTERS + " SET equipment='" + equipment + "', inventory='" + inventory + "', bank='" + bank + "' WHERE id = " + userid);
						if (totalItemRemoved > 0)
							send(new SendMessage("Finished deleting " + totalItemRemoved + " of " + GetItemName(id).toLowerCase()));
						else
							send(new SendMessage("The user " + user + " did not had any " + GetItemName(id).toLowerCase()));
					} else
						send(new SendMessage("username '" + user + "' have yet to login!"));
					statement.close();
				}
			} catch (Exception e) {
				System.out.println("issue: " + e.getMessage());
			}
		}
	}

	public void dropAllItems() {
		//BAHAHAHAAHAH
      /*for (int i = 0; i < bankItems.length; i++) {
          if (bankItems[i] > 0) {
	    		  GroundItem drop = new GroundItem(getPosition().getX(), getPosition().getY(), bankItems[i] - 1, bankItemsN[i], getSlot(), -1);
	    		  bankItems[i] = 0;
	    		  bankItemsN[i] = 0;
	    		  Ground.items.add(drop);
          }
        }
      for (int i = 0; i < playerItems.length; i++) {
          if (playerItems[i] > 0) {
    		  GroundItem drop = new GroundItem(getPosition().getX(), getPosition().getY(), playerItems[i] - 1, playerItemsN[i], getSlot(), -1);
    		  playerItems[i] = 0;
    		  playerItemsN[i] = 0;
    		  Ground.items.add(drop);
          }
        }
        for (int i = 0; i < getEquipment().length; i++) {
          if (getEquipment()[i] > 0) {
  		  GroundItem drop = new GroundItem(getPosition().getX(), getPosition().getY(), getEquipment()[i] - 1, getEquipmentN()[i], getSlot(), -1);
  		 getEquipment()[i] = 0;
  		 getEquipmentN()[i] = 0;
  		  Ground.items.add(drop);
          }
        }*/
		resetPos();
		yell(getPlayerName() + " is currently bug abusing on a item!");
	}

	public boolean checkObsidianWeapons() {
		if (getEquipment()[2] != 11128) return false;
		int[] weapons = {6522, 6523, 6525, 6526, 6527, 6528};
		for (int weapon : weapons)
			if (getEquipment()[3] == weapon)
				return true;
		return false;
	}

	public void setTravelMenu() {
		frame36(153, 0);
		send(new SendString("Brimhaven", 12338));
		send(new SendString("Island", 12339));
		for (int i = 0; i < 5; i++)
			send(new SendString("", 809 + i));
		send(new SendString("Catherby", 809));
		send(new SendString("Shilo", 812));
		send(new SendString("Canifis", 810));
		showInterface(802);
	}

	public void travelTrigger(int checkPos) {
		if (travelInitiate) {
			return;
		}
		boolean home = checkPos != 0;
		int[] posTrigger = { 1, 3, 4, 7, 10, 2, 5, 6, 11 }; //0-4 is to something! rest is to the Catherby
		int[][] travel = {
				{3057, 2803, 3421, 0}, //Home!
				{3058, -1, -1, 0}, //Mountain!
				{3059, 3493, 3488, 0}, //Canifis
				{3060, -1, -1, 0}, //Tent!
				{3056, 2863, 2971, 0}, //Shilo
				{48054, 2772, 3234, 0} //Brimhaven
		};
		for (int i = 0; i < travel.length; i++)
			if (travel[i][0] == actionButtonId) { //Initiate the teleport!
				/* Check conditions! */
				if ((!home && i == 0) || (home && i != 0)) {
					send(new SendMessage(!home ? "You are already here!" : "Please select Catherby!"));
					return;
				}
				if (travel[i][1] == -1) {
					send(new SendMessage("This will lead you to nothing!"));
					return;
				}
				if(i > 0 && !getTravel(i - 1)) {
					NpcDialogue = 48054;
					return;
				}
				/* Set configs! */
				final int pos = i;
				frame36(153, home ? posTrigger[checkPos + 3] : posTrigger[i - 1]);
				travelInitiate = true;
				EventManager.getInstance().registerEvent(new Event(1800) {
					@Override
					public void execute() {
						if (disconnected)
							this.stop();
						else {
							getPosition().moveTo(travel[pos][1], travel[pos][2], 0); //Update position!
							teleportToX = getPosition().getX();
							teleportToY = getPosition().getY();
							send(new RemoveInterfaces());
							travelInitiate = false;
							this.stop();
						}
					}
				});
			}
	}

	public void setRefundList() {
		rewardList.clear();
		try {
			String query = "SELECT * FROM uber3_refunds WHERE receiver='"+dbId+"' AND claimed IS NULL ORDER BY date ASC";
			Statement stm = getDbConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
			ResultSet result = stm.executeQuery(query);
			while(result.next()) {
				rewardList.add(new RewardItem(result.getInt("item"), result.getInt("amount")));
			}
			stm.close();
		} catch (Exception e) {
			System.out.println("Error in checking sql!!" + e.getMessage() + ", " + e);
			e.printStackTrace();
		}
	}

	public void setRefundOptions() {
		if(rewardList.isEmpty()) {
			refundSlot = -1;
			send(new SendMessage("You got no items to collect!"));
			return;
		}
		int slot = refundSlot;
		String[] text = new String[rewardList.size() < 4 ? rewardList.size() + 2 : rewardList.size() - slot <= 3 ? rewardList.size() - slot + 2 : 6];
		text[0] = "Refund Item List";
		int position = Math.min(3, rewardList.size() - slot);
		for(int i = 0; i < position; i++)
			text[i + 1] = "Claim "+ rewardList.get(slot + i).getAmount() +" of " + GetItemName(rewardList.get(slot + i).getId());
		text[position + 1] = text.length < 6 && slot == 0 ? "Close" : text.length == 6 ? "Next" : "Previous";
		if(text.length == 6)
			text[position + 2] = slot == 0 ? "Close" : "Previous";
		showPlayerOption(text);
	}

	public void reclaim(int position) {
		int slot = refundSlot + position;
		try {
			String query = "SELECT * FROM uber3_refunds WHERE receiver='"+dbId+"' AND claimed IS NULL ORDER BY date ASC";
			Statement stm = getDbConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet result = stm.executeQuery(query);
			String date = "";
			RewardItem item = rewardList.get(slot - 1);
			while(result.next() && date.equals("")) {
				if(result.getRow() == slot) {
					date = result.getString("date");
				}
			}
			stm.executeUpdate("UPDATE uber3_refunds SET claimed='"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"' where date='"+date+"'");
			stm.close();
			/* Set back options! */
			setRefundList();
			if(!rewardList.isEmpty()) {
				refundSlot = 0;
				setRefundOptions();
			} else send(new RemoveInterfaces());
			/* Refund item function */
			int amount = item.getAmount() - getFreeSpace();
			if(Server.itemManager.isStackable(item.getId())) {
				if(getFreeSpace() == 0) {
					GroundItem groundItem = new GroundItem(getPosition().copy(), item.getId(), item.getAmount(), getSlot(), -1);
					Ground.items.add(groundItem);
					send(new SendMessage("Some items have been dropped to the ground!"));
				} else addItem(item.getId(), item.getAmount());
			} else if (amount > 0) {
				addItem(item.getId(), getFreeSpace());
				for(int i = 0; i < getFreeSpace(); i++)
					addItem(item.getId(), 1);
				for(int i = 0; i < amount; i++) {
					GroundItem groundItem = new GroundItem(getPosition().copy(), item.getId(), 1, getSlot(), -1);
					Ground.items.add(groundItem);
				}
				send(new SendMessage("Some items have been dropped to the ground!"));
			} else
				for(int i = 0; i < item.getAmount(); i++)
					addItem(item.getId(), 1);
		} catch (Exception e) {
			System.out.println("Error in checking sql!!" + e.getMessage() + ", " + e);
			e.printStackTrace();
		}
	}
	public int totalLevel() {
		return Skills.enabledSkills()
				.mapToInt(skill -> Skills.getLevelForExperience(getExperience(skill)))
				.sum() + (int) Skills.disabledSkills().count();
	}
}