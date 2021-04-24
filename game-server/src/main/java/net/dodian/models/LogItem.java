package net.dodian.models;

import java.sql.Timestamp;

public class LogItem {
    private int id;
    private Timestamp created;
    private LogCategory category;
    private int playerId;
    private int targetId;
    private String content;

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
