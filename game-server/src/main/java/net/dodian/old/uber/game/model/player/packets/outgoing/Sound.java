package net.dodian.old.uber.game.model.player.packets.outgoing;

import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.OutgoingPacket;

/**
 * 
 * @author Dashboard
 *
 */
public class Sound implements OutgoingPacket {

  public final int soundId, delay, byteSetting;

  public Sound(int soundId, int byteSetting, int delay) {
    this.soundId = soundId;
    this.byteSetting = byteSetting;
    this.delay = delay;
  }

  public Sound(int soundId, int delay) {
    this(soundId, 0, delay);
  }

  public Sound(int soundId) {
    this(soundId, 0);
  }

  @Override
  public void send(Client client) {
    client.getOutputStream().createFrame(174);
    client.getOutputStream().writeWord(soundId);
    client.getOutputStream().writeByte(0);
    client.getOutputStream().writeWord(delay);
  }

}
