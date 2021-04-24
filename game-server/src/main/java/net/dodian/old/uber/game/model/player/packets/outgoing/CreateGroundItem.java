package net.dodian.old.uber.game.model.player.packets.outgoing;

import net.dodian.old.uber.game.model.Position;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.item.GameItem;
import net.dodian.old.uber.game.model.player.packets.OutgoingPacket;

public class CreateGroundItem implements OutgoingPacket {
  
  private GameItem item;
  private Position position;
  
  public CreateGroundItem(GameItem item, Position position) {
    this.item = item;
    this.position = position;
  }

  @Override
  public void send(Client client) {
    client.getOutputStream().createFrame(85);
    client.getOutputStream().writeByteC(position.getY() - (client.mapRegionY * 8));
    client.getOutputStream().writeByteC(position.getX() - (client.mapRegionX * 8));
    client.getOutputStream().createFrame(44);
    client.getOutputStream().writeWordBigEndianA(item.getId());
    client.getOutputStream().writeWord(item.getAmount());
    client.getOutputStream().writeByte(0); 
  }

}
