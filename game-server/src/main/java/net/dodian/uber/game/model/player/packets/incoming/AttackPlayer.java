package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.PlayerHandler;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class AttackPlayer implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int victim = client.getInputStream().readSignedWordBigEndian();
        // client.getCombat().initialize(PlayerHandler.players[victim]);
        client.attackingOnPid = victim;
        if (!client.canAttack) {
            client.send(new SendMessage("You cannot attack your oppenent yet!"));
            return;
        }
        client.faceNPC(32768 + client.attackingOnPid);
        if (client.attackingOnPid >= PlayerHandler.players.length || client.attackingOnPid < 1) {
            client.attackingOnPid = -1;
            client.IsAttacking = false;
            return;
        }
        client.IsAttacking = true;
    }

}
