package net.dodian.old.uber.game.model.player.packets.outgoing;

import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.OutgoingPacket;

public class RemoveInterfaces implements OutgoingPacket {

  @Override
  public void send(Client client) {
    client.IsBanking = false;
    client.currentSkill = -1;
    client.getOutputStream().createFrame(219);
    client.flushOutStream();
  }

}
