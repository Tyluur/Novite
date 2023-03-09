package novite.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import novite.rs.utility.Utils.CombatRates;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class Facade implements Serializable {

	public Facade() {
		modifiers = new double[3];
	}

	private int achievementPoints;
	private int akrisaePoints;
	private int pkPoints;
	private int runeSlayerPoints;
	private int lastRFDWave;
	private int rowCharges;
	private int timesVoted;
	private int noviteGamePoints;

	private boolean acceptingAid;
	private boolean expLocked;

	private long voteBonus;
	private long goldPoints;
	private long totalPointsPurchased;
	private long loyaltyPoints;
	private long lastDisplayNameChange;

	private double[] modifiers;
	private long[] ardougneOperators;

	private String title;
	private String email;
	
	private List<Integer> desertTreasureKills = new ArrayList<Integer>();

	/**
	 * @return the achievementPoints
	 */
	public int getAchievementPoints() {
		return achievementPoints;
	}

	/**
	 * @param achievementPoints
	 *            the achievementPoints to set
	 */
	public void setAchievementPoints(int achievementPoints) {
		this.achievementPoints = achievementPoints;
	}

	/**
	 * @return the acceptingAid
	 */
	public boolean isAcceptingAid() {
		return acceptingAid;
	}

	/**
	 * @param acceptingAid
	 *            the acceptingAid to set
	 */
	public void setAcceptingAid(boolean acceptingAid) {
		this.acceptingAid = acceptingAid;
	}

	/**
	 * @return the runeSlayerPoints
	 */
	public int getRuneSlayerPoints() {
		return runeSlayerPoints;
	}

	/**
	 * @param runeSlayerPoints
	 *            the runeSlayerPoints to set
	 */
	public void setRuneSlayerPoints(int runeSlayerPoints) {
		this.runeSlayerPoints = runeSlayerPoints;
	}

	/**
	 * @return the lastRFDWave
	 */
	public int getLastRFDWave() {
		return lastRFDWave;
	}

	/**
	 * @param lastRFDWave
	 *            the lastRFDWave to set
	 */
	public void setLastRFDWave(int lastRFDWave) {
		this.lastRFDWave = lastRFDWave;
	}

	/**
	 * @return the pkPoints
	 */
	public int getPkPoints() {
		return pkPoints;
	}

	/**
	 * @param pkPoints
	 *            the pkPoints to set
	 */
	public void setPkPoints(int pkPoints) {
		this.pkPoints = pkPoints;
	}

	/**
	 * @return the rowCharges
	 */
	public int getRowCharges() {
		return rowCharges;
	}

	/**
	 * @param rowCharges
	 *            the rowCharges to set
	 */
	public void setRowCharges(int rowCharges) {
		this.rowCharges = rowCharges;
	}

	/**
	 * @return the voteBonus
	 */
	public long getVoteBonus() {
		return voteBonus;
	}

	/**
	 * @param voteBonus
	 *            the voteBonus to set
	 */
	public void setVoteBonus(long voteBonus) {
		this.voteBonus = voteBonus;
	}

	/**
	 * @return the runeCoins
	 */
	public long getGoldPoints() {
		return goldPoints;
	}

	/**
	 * @param goldPoints
	 *            the runeCoins to set
	 */
	public void setGoldPoints(long goldPoints) {
		this.goldPoints = goldPoints;
	}

	/**
	 * Adds the gold points to the total amount
	 *
	 * @param goldPoints
	 *            The amount to add
	 */
	public void rewardCoins(int goldPoints) {
		this.goldPoints += goldPoints;
	}

	/**
	 * @return the loyaltyPoints
	 */
	public long getLoyaltyPoints() {
		return loyaltyPoints;
	}

	/**
	 * @param loyaltyPoints
	 *            the loyaltyPoints to set
	 */
	public void setLoyaltyPoints(long loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	/**
	 * @return the timesVoted
	 */
	public int getTimesVoted() {
		return timesVoted;
	}

	/**
	 * @param timesVoted
	 *            the timesVoted to set
	 */
	public void setTimesVoted(int timesVoted) {
		this.timesVoted = timesVoted;
	}

	/**
	 * @return the modifiers
	 */
	public double[] getModifiers() {
		return modifiers;
	}

	/**
	 * Sets the modifier array
	 * 
	 * @param modifier
	 */
	public void setModifier(double[] modifier) {
		this.modifiers = modifier;
	}

	/**
	 * Sets the rates to the values in #CombatRates
	 * 
	 * @param rates
	 *            The rates
	 */
	public void setModifiers(CombatRates rates) {
		if (rates == null) {
			modifiers = null;
			return;
		}
		modifiers[COMBAT_MODIFIER_INDEX] = rates.getCombat();
		modifiers[SKILL_MODIFIER_INDEX] = rates.getSkill();
		modifiers[LOOT_MODIFIER_INDEX] = rates.getLoot();
	}

	/**
	 * Gets the combat rate we are on
	 * 
	 * @return
	 */
	public CombatRates getRates() {
		for (CombatRates rate : CombatRates.values()) {
			if (rate.getCombat() == modifiers[COMBAT_MODIFIER_INDEX])
				return rate;
		}
		return null;
	}

	/**
	 * @return the akrisaePoints
	 */
	public int getAkrisaePoints() {
		return akrisaePoints;
	}

	/**
	 * @param akrisaePoints
	 *            the akrisaePoints to set
	 */
	public void setAkrisaePoints(int akrisaePoints) {
		this.akrisaePoints = akrisaePoints;
	}

	/**
	 * @return the totalPointsPurchased
	 */
	public long getTotalPointsPurchased() {
		return totalPointsPurchased;
	}

	/**
	 * @param totalPointsPurchased
	 *            the totalPointsPurchased to set
	 */
	public void setTotalPointsPurchased(long totalPointsPurchased) {
		this.totalPointsPurchased = totalPointsPurchased;
	}

	/**
	 * @return the noviteGamePoints
	 */
	public int getNoviteGamePoints() {
		return noviteGamePoints;
	}

	/**
	 * @param noviteGamePoints
	 *            the noviteGamePoints to set
	 */
	public void setNoviteGamePoints(int noviteGamePoints) {
		this.noviteGamePoints = noviteGamePoints;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the lastDisplayNameChange
	 */
	public long getLastDisplayNameChange() {
		return lastDisplayNameChange;
	}

	/**
	 * @param lastDisplayNameChange
	 *            the lastDisplayNameChange to set
	 */
	public void setLastDisplayNameChange(long lastDisplayNameChange) {
		this.lastDisplayNameChange = lastDisplayNameChange;
	}

	/**
	 * @return the desertTreasureKills
	 */
	public List<Integer> getDesertTreasureKills() {
		if (desertTreasureKills == null) {
			desertTreasureKills = new ArrayList<Integer>(); 
		}
		return desertTreasureKills;
	}

	public boolean isExpLocked() {
		return expLocked;
	}

	public void setExpLocked(boolean expLocked) {
		this.expLocked = expLocked;
	}

	public long[] getArdougneOperators() {
		if (ardougneOperators == null)
			ardougneOperators = new long[3];
		return ardougneOperators;
	}

	public void setArdougneOperators(long[] ardougneOperators) {
		this.ardougneOperators = ardougneOperators;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static final int COMBAT_MODIFIER_INDEX = 0;
	public static final int SKILL_MODIFIER_INDEX = 1;
	public static final int LOOT_MODIFIER_INDEX = 2;

	private static final long serialVersionUID = -3187404322997250838L;

}
