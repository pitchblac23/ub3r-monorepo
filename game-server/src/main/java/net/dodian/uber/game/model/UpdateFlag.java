package net.dodian.uber.game.model;

import net.dodian.uber.game.model.entity.Entity;

/**
 * @author Dashboard
 */
public enum UpdateFlag {
    FORCED_MOVEMENT(0x400, -1),
    GRAPHICS(0x100, 0x80),
    ANIM(0x8, 0x10),
    CHAT(0x80, -1),
    FACE_CHARACTER(1, 0x20),
    APPEARANCE(0x10, -1),
    FACE_COORDINATE(2, 0x4),
    HIT(0x20, 0x40),//0x8
    HIT2(0x200, 0x8),
    FORCED_CHAT(0x4, 0x1),
    FACE_DIRECTION(0x40, -1),
    DUMMY(0, 0) // Dummy is required when initializing NPCs, causes update with no flags
    ;

    private int playerMask, npcMask;

    UpdateFlag(int playerMask, int npcMask) {
        this.playerMask = playerMask;
        this.npcMask = npcMask;
    }

    @SuppressWarnings("serial")
    public int getMask(Entity.Type type) {
        if (type == Entity.Type.PLAYER) {
            if (playerMask == -1) {
                throw new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return "Invalid Player Update Mask. Please change the mask from -1 to the proper value.";
                    }
                };
            }
            return playerMask;
        } else {
            if (npcMask == -1) {
                throw new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return "Invalid NPC Update Mask. Please change the mask from -1 to the proper value.";
                    }
                };
            }
            return npcMask;
        }
    }
}