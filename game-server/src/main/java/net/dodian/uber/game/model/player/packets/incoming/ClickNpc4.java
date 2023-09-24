package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.WalkToTask;
import net.dodian.uber.game.model.entity.npc.Npc;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.party.Balloons;
import net.dodian.utilities.Utils;

public class ClickNpc4 implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int npcIndex = client.getInputStream().readSignedWord();
        Npc tempNpc = Server.npcManager.getNpc(npcIndex);
        if (tempNpc == null)
            return;
        int NPCID = tempNpc.getId();

        final WalkToTask task = new WalkToTask(WalkToTask.Action.NPC_THIRD_CLICK, NPCID, tempNpc.getPosition());
        client.setWalkToTask(task);
        if (client.randomed) {
            return;
        }
        if (!client.playerPotato.isEmpty())
            client.playerPotato.clear();
        EventManager.getInstance().registerEvent(new Event(600) {

            @Override
            public void execute() {

                if (client == null || client.disconnected) {
                    this.stop();
                    return;
                }

                if (client.getWalkToTask() != task) {
                    this.stop();
                    return;
                }

                if (!client.goodDistanceEntity(tempNpc, 1)) {
                    return;
                }

                clickNpc4(client, tempNpc);
                client.setWalkToTask(null);
                this.stop();
            }
        });
    }

    public void clickNpc4(Client client, Npc tempNpc) {
        int NPCID = tempNpc.getId();
        /*if (NPCID == npcid) {
            //do stuff
        }*/
    }
}