package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;

public class Trade implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int temp = client.getInputStream().readSignedWordBigEndian();
        if (client.inDuel || client.duelFight) {
            client.sendMessage("You are busy at the moment");
            return;
        }
        if (!client.inTrade && !client.inDuel && !client.duelFight) {
            client.trade_reqId = temp;
            client.tradeReq(client.trade_reqId);
        }
    }
}