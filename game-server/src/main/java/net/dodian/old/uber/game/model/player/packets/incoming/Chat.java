package net.dodian.old.uber.game.model.player.packets.incoming;

import net.dodian.old.uber.game.Server;
import net.dodian.old.uber.game.model.ChatLine;
import net.dodian.old.uber.game.model.UpdateFlag;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.Packet;
import net.dodian.old.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.old.uber.game.security.ChatLog;
import net.dodian.old.utilities.Utils;

public class Chat implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    if (!client.validClient) {
      client.send(new SendMessage("Please use another client"));
      return;
    }
    if (client.muted) {
      return;
    }
    if (!Server.chatOn && client.playerRights == 0) {
      client.send(new SendMessage("Public chat has been temporarily restricted"));
      return;
    }
    client.setChatTextEffects(client.getInputStream().readUnsignedByteS());
    client.setChatTextColor(client.getInputStream().readUnsignedByteS());
    client.setChatTextSize((byte) (packetSize - 2));
    client.getInputStream().readBytes_reverseA(client.getChatText(), client.getChatTextSize(), 0);
    String chat = Utils.textUnpack(client.getChatText(), packetSize - 2);
    client.getUpdateFlags().setRequired(UpdateFlag.CHAT, true);
    ChatLog.recordChat(client.getPlayerName(), chat);
    client.println_debug("Text [" + client.getChatTextEffects() + "," + client.getChatTextColor() + "]: " + chat);
    Server.login.sendChat(client.dbId, 2, client.getPosition().getX(), client.getPosition().getY(), chat);
    Server.chat.add(new ChatLine(client.getPlayerName(), client.dbId, 2, chat, client.getPosition().getX(),
        client.getPosition().getY()));
  }

}
