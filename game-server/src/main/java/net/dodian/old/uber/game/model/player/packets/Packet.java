package net.dodian.old.uber.game.model.player.packets;

import net.dodian.old.uber.game.model.entity.player.Client;

public interface Packet {

  public void ProcessPacket(Client client, int packetType, int packetSize);

}
