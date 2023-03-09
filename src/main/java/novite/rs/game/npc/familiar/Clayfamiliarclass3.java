package novite.rs.game.npc.familiar;

import novite.rs.game.Animation;
import novite.rs.game.Graphics;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.stealingcreation.Score;
import novite.rs.game.minigames.stealingcreation.StealingCreationController;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Pouches;

public class Clayfamiliarclass3 extends Familiar {

	private static final long serialVersionUID = 7289857564889907408L;

	public Clayfamiliarclass3(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Clay Deposit";
	}

	@Override
	public String getSpecialDescription() {
		return "Deposit all items in the beast of burden's possession in exchange for points.";
	}

	@Override
	public int getBOBSize() {
		return 12;
	}

	@Override
	public int getSpecialAmount() {
		return 30;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getControllerManager().getController() == null || !(getOwner().getControllerManager().getController() instanceof StealingCreationController)) {
			dissmissFamiliar(false);
			return false;
		}
		getOwner().setNextGraphics(new Graphics(1316));
		getOwner().setNextAnimation(new Animation(7660));
		StealingCreationController sc = (StealingCreationController) getOwner().getControllerManager().getController();
		Score score = sc.getGame().getScore(getOwner());
		if (score == null) {
			return false;
		}
		for (Item item : getBob().getBeastItems().getItems()) {
			if (item == null) {
				continue;
			}
			sc.getGame().sendItemToBase(getOwner(), item, sc.getTeam(), true, false);
		}
		return true;
	}
}
