package net.dodian.old.uber.game.model.player.packets.incoming;

import net.dodian.old.uber.game.model.combat.impl.CombatStyleHandler;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.Packet;

public class WearItem implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int wearID = client.getInputStream().readUnsignedWord();
    int wearSlot = client.getInputStream().readUnsignedWordA();
    int interfaceID = client.getInputStream().readUnsignedWordA();
    if(client.emptyEssencePouch(wearID)) {
    	return;
    }
    client.wear(wearID, wearSlot, interfaceID);
    CombatStyleHandler.setWeaponHandler(client, -1);
  }

}
