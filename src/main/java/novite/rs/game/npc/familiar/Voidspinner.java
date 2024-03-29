package novite.rs.game.npc.familiar;

import novite.rs.game.Graphics;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Pouches;
import novite.rs.game.player.content.Magic;

public class Voidspinner extends Familiar {

	private static final long serialVersionUID = -1639238550551778316L;
	private int healTicks;

	public Voidspinner(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		healTicks++;
		if (healTicks == 20) {
			getOwner().heal(10);
			getOwner().setNextGraphics(new Graphics(1507));
			healTicks = 0;
		}
	}

	@Override
	public String getSpecialName() {
		return "Call To Arms";
	}

	@Override
	public String getSpecialDescription() {
		return "Teleports the player to Void Outpost.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 4;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Magic.sendTeleportSpell((Player) object, 14388, -1, 1503, 1502, 0, 0, new WorldTile(2662, 2649, 0), 3, true, Magic.OBJECT_TELEPORT);
		return true;
	}
}
