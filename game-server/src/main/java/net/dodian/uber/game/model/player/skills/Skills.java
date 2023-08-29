package net.dodian.uber.game.model.player.skills;

/**
 * @author Dashboard
 */
public enum Skills {

    ATTACK(0, "attack"),
    DEFENCE(1, "defence"),
    STRENGTH(2, "strength"),
    HITPOINTS(3, "hitpoints"),
    RANGED(4, "ranged"),
    PRAYER(5, "prayer"),
    MAGIC(6, "magic"),
    COOKING(7, "cooking"),
    WOODCUTTING(8, "woodcutting"),
    FLETCHING(9, "fletching"),
    FISHING(10, "fishing"),
    FIREMAKING(11, "firemaking"),
    CRAFTING(12, "crafting"),
    SMITHING(13, "smithing"),
    MINING(14, "mining"),
    HERBLORE(15, "herblore"),
    AGILITY(16, "agility"),
    THIEVING(17, "thieving"),
    SLAYER(18, "slayer"),
    FARMING(19, "farming"),
    RUNECRAFTING(20, "runecrafting");

    private final int id;
    private final String name;

    Skills(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Skills getSkill(int id) {
        for (Skills skill : values()) {
            if (skill.getId() == id)
                return skill;
        }
        return null;
    }

    public static int getLevelForExperience(int exp) {
        double output = 0;
        int playerLevel = 0;
        for (int lvl = 2; lvl <= 100 && (int) output <= exp; lvl++) {
            output += (Math.floor((lvl - 1) + 300 * Math.pow(2.0, (double) (lvl - 1) / 7.0))) / 4.0;
            playerLevel++;
        }
        return playerLevel;
    }

    public static int getXPForLevel(int level) {
        double points = 0.0;
        int output = 0;
        for (int lvl = 1; lvl < level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) Math.floor(points / 4);
        }
        return output;
    }

}
