package net.dodian.old.uber.game.model.player.packets;

import net.dodian.old.uber.game.model.entity.player.Client;

public interface OutgoingPacket {

  public abstract void send(Client client);

}
