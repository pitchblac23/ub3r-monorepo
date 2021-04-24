package net.dodian.old.uber.game.model.player.packets.incoming;

import net.dodian.old.Config;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.Packet;

public class BankX1 implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        client.getOutputStream().createFrame(27);
        client.XremoveSlot = client.getInputStream().readSignedWordBigEndian();
        client.XinterfaceID = client.getInputStream().readUnsignedWordA();
        client.XremoveID = client.getInputStream().readSignedWordBigEndian();
        if (Config.getWorldId() > 1)
            client.println_debug(
                    "RemoveItem X: " + client.XremoveID + " InterID: " + client.XinterfaceID + " slot: " + client.XremoveSlot);
    }

}
