package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.utilities.Utils;

public class DuelRequest implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int PID = (Utils.HexToInt(client.getInputStream().buffer, 0, packetSize) / 1000);
        Client other = client.getClient(PID);
        if (!client.validClient(PID)) {
            return;
        }
        if (client.inTrade || client.inDuel || (client.duelFight && client.duel_with != PID)) {
            client.sendMessage("You are busy at the moment");
            return;
        }
        if (client.inWildy() || other.inWildy()) {
            client.sendMessage("You cant duel in the wilderness!");
            return;
        }
        client.duelReq(PID);
    }
}