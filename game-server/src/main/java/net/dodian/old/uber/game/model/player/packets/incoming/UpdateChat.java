package net.dodian.old.uber.game.model.player.packets.incoming;

import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.entity.player.PlayerHandler;
import net.dodian.old.uber.game.model.player.packets.Packet;
import net.dodian.old.utilities.Utils;

public class UpdateChat implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    client.getInputStream().readUnsignedByte();
    client.Privatechat = client.getInputStream().readUnsignedByte();
    client.getInputStream().readUnsignedByte();
    if (System.currentTimeMillis() - client.lastButton < 750) {
      return;
    }
    client.lastButton = System.currentTimeMillis();
    for (int p = 0; p < PlayerHandler.players.length; p++) {
      Client o = client.getClient(p);
      if (client.validClient(p) && o.hasFriend(Utils.playerNameToInt64(client.getPlayerName()))) {
        o.refreshFriends();
      }
    }
  }

}
