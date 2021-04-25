package net.dodian.models.definitions.spawn;

import net.dodian.old.uber.game.model.Position;

public class GroundItemSpawnDefinition {
    private Position position;
    private String description;
    private boolean canPickup;

    public Position getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCanPickup() {
        return canPickup;
    }
}
