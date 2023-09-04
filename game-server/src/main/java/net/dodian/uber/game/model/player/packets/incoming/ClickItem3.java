package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class ClickItem3 implements Packet {

    public void ProcessPacket(Client c, int packetType, int packetSize) {
        int itemSlot = c.getInputStream().readUnsignedWordA();
        int itemId = c.getInputStream().readUnsignedWordBigEndian();
        int itemId1 = c.getInputStream().readUnsignedWordA();
        c.println_debug("" + packetType);

        switch (itemId1) {
            case 12019:
                c.emptyCoalBag();
                break;

            case 1712:
                c.send(new SendMessage("Test."));
                break;
        }
    }
}