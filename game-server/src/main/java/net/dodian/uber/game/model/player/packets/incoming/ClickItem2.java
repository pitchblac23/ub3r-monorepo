package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.Player;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.utilities.Misc;

import java.util.Arrays;

public class ClickItem2 implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int itemId = client.getInputStream().readUnsignedWordA();
        int itemSlot = client.getInputStream().readSignedWordBigEndianA();
        if (client.playerItems[itemSlot] - 1 != itemId) {
            return;
        }
        int pouches = itemId == 5509 ? 0 : ((itemId - 5508) / 2);
        if (pouches >= 0 && pouches <= 3) {
            client.send(new SendMessage("There is " + client.runePouchesAmount[pouches] + " rune essence in this pouch!"));
        }
        if (itemId == 12019) {
            client.send(new SendMessage("Your coal bag has " + client.coalBagAmount[0] + " pieces of coal in it."));
        }
        if (itemId == 13203) {
            String[] quotes = {
                    "You are easily the spunkiest warrior alive!",
                    "Not a single soul can challenge your spunk!",
                    "You are clearly the most spunktastic in all the land!",
                    "Your might is spunktacular!",
                    "It's spunkalicious!",
                    "You... are... spunky!",
                    "You are too spunktacular to measure!",
                    "You are the real M.V.P. dude!",
                    "More lazier then Spunky is Ivan :D"
            };
            client.send(new SendMessage(quotes[Misc.random(quotes.length - 1)]));
        }
        if (itemId == 11997) {
            client.send(new SendMessage("Event is over! Will use in the future?!")); //I need to bring these back to Duke!
        }
        if (itemId == 4566) {
            client.requestAnim(1835, 0);
        }
    }

}