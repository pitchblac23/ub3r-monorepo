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
