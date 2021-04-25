package net.dodian.models.definitions;

public class NpcDefinition {
    private int id;
    private String name;
    private String description;
    private int combatLevel;
    private int attackEmote;
    private int deathEmote;
    private int health;
    private int respawnRate;
    private int size;
    private int attackBonus;
    private int strengthBonus;
    private int defenceBonus;
    private int rangedBonus;
    private int magicBonus;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int getAttackEmote() {
        return attackEmote;
    }

    public int getDeathEmote() {
        return deathEmote;
    }

    public int getHealth() {
        return health;
    }

    public int getRespawnRate() {
        return respawnRate;
    }

    public int getSize() {
        return size;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getDefenceBonus() {
        return defenceBonus;
    }

    public int getRangedBonus() {
        return rangedBonus;
    }

    public int getMagicBonus() {
        return magicBonus;
    }
}
