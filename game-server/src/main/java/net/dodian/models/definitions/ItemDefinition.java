package net.dodian.models.definitions;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ItemDefinition {
    @Id
    private int id;
    private String name;
    private String description;
    private EquipmentSlot slot;
    private boolean stackAble;
    private boolean noteAble;
    private int shopSellValue;
    private int shopBuyValue;
    private int alchemyValue;
    private int standAnimation;
    private int walkAnimation;
    private int runAnimation;
    private int attackAnimation;
    private boolean premiumOnly;
    private EquipmentType equipmentType;
    private int attackStabBonus;
    private int attackSlashBonus;
    private int attackCrushBonus;
    private int attackMagicBonus;
    private int attackRangeBonus;
    private int defenceStabBonus;
    private int defenceSlashBonus;
    private int defenceCrushBonus;
    private int defenceMagicBonus;
    private int defenceRangeBonus;
    private int meleeStrengthBonus;
    private int rangedStrengthBonus;
    private int magicDamageBonus;
    private int prayerBonus;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public boolean isStackAble() {
        return stackAble;
    }

    public boolean isNoteAble() {
        return noteAble;
    }

    public int getShopSellValue() {
        return shopSellValue;
    }

    public int getShopBuyValue() {
        return shopBuyValue;
    }

    public int getAlchemyValue() {
        return alchemyValue;
    }

    public int getStandAnimation() {
        return standAnimation;
    }

    public int getWalkAnimation() {
        return walkAnimation;
    }

    public int getRunAnimation() {
        return runAnimation;
    }

    public int getAttackAnimation() {
        return attackAnimation;
    }

    public boolean isPremiumOnly() {
        return premiumOnly;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public int getAttackStabBonus() {
        return attackStabBonus;
    }

    public int getAttackSlashBonus() {
        return attackSlashBonus;
    }

    public int getAttackCrushBonus() {
        return attackCrushBonus;
    }

    public int getAttackMagicBonus() {
        return attackMagicBonus;
    }

    public int getAttackRangeBonus() {
        return attackRangeBonus;
    }

    public int getDefenceStabBonus() {
        return defenceStabBonus;
    }

    public int getDefenceSlashBonus() {
        return defenceSlashBonus;
    }

    public int getDefenceCrushBonus() {
        return defenceCrushBonus;
    }

    public int getDefenceMagicBonus() {
        return defenceMagicBonus;
    }

    public int getDefenceRangeBonus() {
        return defenceRangeBonus;
    }

    public int getMeleeStrengthBonus() {
        return meleeStrengthBonus;
    }

    public int getRangedStrengthBonus() {
        return rangedStrengthBonus;
    }

    public int getMagicDamageBonus() {
        return magicDamageBonus;
    }

    public int getPrayerBonus() {
        return prayerBonus;
    }

    public enum EquipmentType {
        TWO_HANDED,
        FULL_HELM,
        HELM,
        FULL_BODY,
        BODY,
    }

    public enum EquipmentSlot {
        HEAD(0),
        CAPE(1),
        NECK(2),
        RIGHT_HAND(3),
        BODY(4),
        LEFT_HAND(5),
        LEGS(7),
        HANDS(9),
        FEET(10),
        FINGER(12),
        AMMUNITION(13),
        ;

        private final int slot;

        EquipmentSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }
}
