package novite.rs.game.npc.combat.impl;

import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;

public class BarricadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Barricade" };
	}

	/*
	 * empty
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		return 0;
	}

}
