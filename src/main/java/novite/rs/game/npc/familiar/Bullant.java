package novite.rs.game.npc.familiar;

import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.summoning.Pouches;

public class Bullant extends Familiar {

	/**
	 *
	 */
	private static final long serialVersionUID = 4667052662212699631L;

	public Bullant(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Unburden";
	}

	@Override
	public String getSpecialDescription() {
		return "Restores the owner's run energy by half of their Agility level.";
	}

	@Override
	public int getBOBSize() {
		return 9;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getRunEnergy() == 100) {
			player.getPackets().sendGameMessage("This wouldn't effect you at all.");
			return false;
		}
		int agilityLevel = getOwner().getSkills().getLevel(Skills.AGILITY);
		int runEnergy = player.getRunEnergy() + (Math.round(agilityLevel / 2));
		if (runEnergy > 100) {
			runEnergy = 100;
		}
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		setNextAnimation(new Animation(7895));
		setNextGraphics(new Graphics(1382));
		player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
		return true;
	}
}
