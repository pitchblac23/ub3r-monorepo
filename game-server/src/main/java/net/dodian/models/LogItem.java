package net.dodian.models;

import java.sql.Timestamp;

public class LogItem {
    private int id;
    private Timestamp created;
    private LogCategory category;
    private int playerId;
    private int targetId;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public LogCategory getCategory() {
        return category;
    }

    public void setCategory(LogCategory category) {
        this.category = category;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum LogCategory {
        COMMAND_EXECUTE,
        ITEM_PICKUP,
        ITEM_DROP,
        PLAYER_TRADE,
        PLAYER_DUEL,
        PLAYER_DEATH,
        PLAYER_PRIVATE_MESSAGE,
        PLAYER_LOCAL_MESSAGE,
        PLAYER_YELL_MESSAGE,
        PLAYER_LOGIN,
        PLAYER_LOGOUT,
        NPC_DEATH,
        NPC_DROP
    }
}
