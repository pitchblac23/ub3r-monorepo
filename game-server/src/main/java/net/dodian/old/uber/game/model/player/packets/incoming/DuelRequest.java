package net.dodian.old.uber.game.model.player.packets.incoming;

import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.Packet;
import net.dodian.old.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.old.utilities.Utils;

public class DuelRequest implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int PID = (Utils.HexToInt(client.getInputStream().buffer, 0, packetSize) / 1000);
    Client other = client.getClient(PID);
    if (!client.validClient(PID)) {
      return;
    }
    if (client.inTrade || client.inDuel || (client.duelFight && client.duel_with != PID)) {
      client.send(new SendMessage("You are busy at the moment"));
      return;
    }
    if(client.inWildy() || other.inWildy()) {
      client.send(new SendMessage("You cant duel in the wilderness!"));
      return;
    }
    client.duelReq(PID);
  }

}
