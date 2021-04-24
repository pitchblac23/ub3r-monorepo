package net.dodian.old.uber.game.model.combat.impl;

import net.dodian.old.uber.game.model.entity.Entity;

public abstract class CombatExperienceHandler {

  public abstract void appendExperience(Entity attacker, int hit);

}
