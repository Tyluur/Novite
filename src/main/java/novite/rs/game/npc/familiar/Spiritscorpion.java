package novite.rs.game.npc.familiar;

import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Pouches;

public class Spiritscorpion extends Familiar {

	/**
	 *
	 */
	private static final long serialVersionUID = 6369083931716875985L;

	public Spiritscorpion(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Venom Shot";
	}

	@Override
	public String getSpecialDescription() {
		return "Chance of next Ranged attack becoming mildly poisonous, given that the Ranged weapon being used can be poisoned";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}
}
