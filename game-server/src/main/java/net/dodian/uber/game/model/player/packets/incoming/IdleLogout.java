package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;

public class IdleLogout implements Packet {

    @Override
    public void ProcessPacket(Client c, int packetType, int packetSize) {
        if (!c.getPlayerName().equals("null"));
            //c.logout();
    }
}