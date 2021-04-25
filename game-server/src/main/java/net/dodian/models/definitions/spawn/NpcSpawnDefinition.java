package net.dodian.models.definitions.spawn;

import net.dodian.old.uber.game.model.Position;

public class NpcSpawnDefinition {
    private int npcId;
    private Position position;
    private int health;
    private int directionToFace;

    public int getNpcId() {
        return npcId;
    }

    public Position getPosition() {
        return position;
    }

    public int getHealth() {
        return health;
    }

    public int getDirectionToFace() {
        return directionToFace;
    }
}
