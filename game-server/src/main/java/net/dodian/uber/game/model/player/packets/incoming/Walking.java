package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.Player;
import net.dodian.uber.game.model.object.Stairs;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import net.dodian.utilities.Utils;

public class Walking implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        if (packetType == 248)
            packetSize -= 14;

        if (packetType != 98 && packetType != 164) {
            client.setWalkToTask(null);
        }
        if (!client.pLoaded || System.currentTimeMillis() < client.walkBlock) {
            return;
        }
        if (client.getCurrentHealth() < 1 || client.deathStage > 0) { //You are dead here!
            return;
        }
        if (client.randomed) {
            return;
        }
        if(client.chestEventOccur && packetType != 98) client.chestEventOccur = false;
        /* Auto decline when walk away from trade! */
        if(client.inTrade && packetType != 164) client.declineTrade();
        else if(client.inDuel && !client.duelFight && packetType != 164) client.declineDuel();
        /* Check a players inventory! */
        if (client.checkInv) {
            client.checkInv = false;
            client.resetItems(3214);
        }
        if (client.genie)
            client.genie = false;
        if (!client.playerPotato.isEmpty())
            client.playerPotato.clear();
        if (client.NpcDialogue == 1001)
            client.setInterfaceWalkable(-1);
        client.convoId = -1;
        long currentTime = System.currentTimeMillis();
        if (currentTime < client.snaredUntil) {
            client.sendMessage("You are ensnared!");
            return;
        }
        if (!client.validClient) {
            client.sendMessage("You can't move on this account");
            return;
        }
        if(client.attackingNpc || client.attackingPlayer) //Adding a check for reset due to walking away!
            client.resetAttack();
        /* ? */
        client.send(new RemoveInterfaces());
        client.rerequestAnim();
        client.resetAction();
        /* Death Stage */
        if (client.deathStage == 0) {
            client.newWalkCmdSteps = packetSize - 5;
            if (client.inDuel/* && (duelRule[5] || duelRule[9]) */) {
                if (client.newWalkCmdSteps > 0)
                    client.sendMessage("You cannot move during this duel!");
                client.newWalkCmdSteps = 0;
                return;
            }
            if (client.newWalkCmdSteps % 2 != 0) {
                client.println("Warning: walkTo(" + packetType + ") command malformed: " + Utils.Hex(client.getInputStream().buffer, 0, packetSize));
            }
            client.newWalkCmdSteps /= 2;
            if (++client.newWalkCmdSteps > Player.WALKING_QUEUE_SIZE) {
                client.newWalkCmdSteps = 0;
                return;
            }
            int firstStepX = client.getInputStream().readSignedWordBigEndianA();
            firstStepX -= client.mapRegionX * 8;
            for (int i = 1; i < client.newWalkCmdSteps; i++) {
                client.newWalkCmdX[i] = client.getInputStream().readSignedByte();
                client.newWalkCmdY[i] = client.getInputStream().readSignedByte();
                client.tmpNWCX[i] = client.newWalkCmdX[i];
                client.tmpNWCY[i] = client.newWalkCmdY[i];
            }
            client.newWalkCmdX[0] = client.newWalkCmdY[0] = client.tmpNWCX[0] = client.tmpNWCY[0] = 0;
            int firstStepY = client.getInputStream().readSignedWordBigEndian();
            firstStepY -= client.mapRegionY * 8;
            client.newWalkCmdIsRunning = client.getInputStream().readSignedByteC() == 1;
            for (int i = 0; i < client.newWalkCmdSteps; i++) {
                client.newWalkCmdX[i] += firstStepX;
                client.newWalkCmdY[i] += firstStepY;
            }
            // stairs check
            if (client.stairs > 0) {
                Stairs.resetStairs(client);
            }
            // Npc Talking
            if (client.NpcDialogue > 0) {
                client.NpcDialogue = 0;
                client.NpcTalkTo = 0;
                client.NpcDialogueSend = false;
                client.send(new RemoveInterfaces());
            }
            if(client.refundSlot != -1)
                client.refundSlot = -1;
            // banking
            if (client.IsBanking) {
                client.IsBanking = false;
                client.send(new RemoveInterfaces());
            }
            if(client.checkBankInterface) {
                client.checkBankInterface = false;
                client.send(new RemoveInterfaces());
            }
            if (client.isPartyInterface) {
                client.isPartyInterface = false;
                client.send(new RemoveInterfaces());
            }
            // shopping
            if (client.IsShopping == true) {
                client.IsShopping = false;
                client.MyShopID = 0;
                client.UpdateShop = false;
                client.send(new RemoveInterfaces());
            }
        }
        //Reset npc face!
        client.faceNpc(65535);
    }
}