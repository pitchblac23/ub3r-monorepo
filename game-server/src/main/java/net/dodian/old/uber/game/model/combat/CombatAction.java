package net.dodian.old.uber.game.model.combat;

import net.dodian.old.uber.game.model.entity.Entity;

public abstract class CombatAction {

  public abstract void performCombatAction(Entity attacker, Entity victim);
}
