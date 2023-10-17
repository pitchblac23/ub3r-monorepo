package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.WalkToTask;
import net.dodian.uber.game.model.entity.npc.Npc;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.thieving.Thieving;

import static net.dodian.uber.game.model.player.skills.fishing.FishingKt.*;

public class ClickNpc2 implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int npcIndex = client.getInputStream().readSignedWordBigEndianA();
        Npc tempNpc = Server.npcManager.getNpc(npcIndex);
        if (tempNpc == null)
            return;
        int npcId = tempNpc.getId();

        final WalkToTask task = new WalkToTask(WalkToTask.Action.NPC_SECOND_CLICK, npcId, tempNpc.getPosition());
        client.setWalkToTask(task);
        if (client.randomed) {
            return;
        }
        if (!client.playerPotato.isEmpty())
            client.playerPotato.clear();
        EventManager.getInstance().registerEvent(new Event(600) {

            @Override
            public void execute() {

                if (client.disconnected || client.getWalkToTask() != task) {
                    this.stop();
                    return;
                }

                if (!client.goodDistanceEntity(tempNpc, 1)) {
                    return;
                }

                clickNpc2(client, tempNpc);
                client.setWalkToTask(null);
                this.stop();
            }

        });
    }

    public void clickNpc2(Client client, Npc tempNpc) {
        int npcId = tempNpc.getId();
        client.faceNpc(tempNpc.getSlot());
        long time = System.currentTimeMillis();
        if (time - client.globalCooldown[0] <= 50) {
            client.send(new SendMessage("Action throttled... please wait longer before acting!"));
            return;
        }

        client.globalCooldown[0] = time;
        int npcX = tempNpc.getPosition().getX();
        int npcY = tempNpc.getPosition().getY();
		/*if (Math.abs(client.getPosition().getX() - npcX) > 50 || Math.abs(client.getPosition().getY() - npcY) > 50) {
			// send(new SendMessage("Client hack detected!");
			// break;
		}*/
        if (!tempNpc.isAlive()) {
            client.send(new SendMessage("That monster has been killed!"));
            return;
        }

        client.skillX = npcX;
        client.setSkillY(npcY);
        startFishing(client, npcId, 2);

        switch (npcId) {
            case 3086:
            case 3257:
                Thieving.attemptSteal(client, npcId, tempNpc.getPosition());
                break;
        }
        if (npcId == 1174) {
            client.WanneShop = 39;
        }
        if (npcId == 394 || npcId == 395 || npcId == 7677) { /* Banking */
            client.WanneBank = 1;
        } else if (npcId == 11433) {
            client.stairs = 26;
            client.stairDistance = 1;
            client.Essence = 1;
        } else if (npcId == 2345) {
            client.NpcWanneTalk = npcId + 1;
        } else if (npcId == 2180) {
            client.NpcWanneTalk = npcId + 1;
        } else if (npcId == 3648) {
            client.setTravelMenu();
        } else if (npcId == 402 || npcId == 403 || npcId == 405) {
            client.NpcWanneTalk = 13;
        }

//3, 9, 10, 11, 15, 18, 20, 25, 34, 40, 55
        /*Shops Start*/
        else if (npcId == 522 || npcId == 523) { // shopkeeper + Assistant General Store
            client.WanneShop = 1;
        } else if (npcId == 2813 || npcId == 527) { // ShopKeeper + Assistant General Store
            client.WanneShop = 3;
        } else if (npcId == 4965) {
            client.WanneShop = 4;
        } else if (npcId == 577) { // Cassie Falador Shield Shop
            client.WanneShop = 4;
        } else if (npcId == 1032) { // Catherby general store
            client.WanneShop = 5;
        } else if (npcId == 580) { // Flynn Falador Mace Shop
            client.WanneShop = 5;
        } else if (npcId == 538) { // Peksa Barbarian Vullage Helmet Shop
            client.WanneShop = 6;
        } else if (npcId == 546) { // Zaff Varrock Staff Shop
            client.WanneShop = 7;
        } else if (npcId == 548) { // Thessalia Varrock Cloth shop
            client.WanneShop = 8;
        }else if (npcId == 637) { // Aubury rune shop
            client.WanneShop = 9;
        }else if (npcId == 2882) { // Horvik Armor shop
            client.WanneShop = 10;
        } else if (npcId == 6060) { // Bow and arrows
            client.WanneShop = 11;
        } else if (npcId == 584) { // Heruin Falador Gem Shop
            client.WanneShop = 12;
        } else if (npcId == 581) { // Wayne Falador Chainmail Shop
            client.WanneShop = 13;
        } else if (npcId == 585) { // Rommik Rimmington Crafting Shop
            client.WanneShop = 14;
        } else if (npcId == 531 || npcId == 530) { // ShopKeeper + Assistant General Store
            client.WanneShop = 15;
        } else if (npcId == 1860) { // Brian Rimmington Archery Shop
            client.WanneShop = 16;
        } else if (npcId == 557) { // Wydin Port Sarim Food Shop
            client.WanneShop = 17;
        } else if (npcId == 1027) { // Gerrant Port Sarim Fishing Shop
            client.WanneShop = 18;
        } else if (npcId == 559) { // Brian Port Sarim Battleaxe Shop
            client.WanneShop = 19;
        } else if (npcId == 556) { // Grum Port Sarim Jewelery Shop
            client.WanneShop = 20;
        } else if (npcId == 583) { // Betty Port Sarim Magic Shop
            client.WanneShop = 21;
        } else if (npcId == 520 || npcId == 521) { // ShopKeeper + Assistant General Store
            client.WanneShop = 22;
        } else if (npcId == 519) { // Bob Lumbridge Axe Shop
            client.WanneShop = 23;
        } else if (npcId == 541) { // Zeke Al-Kharid Scimitar Shop
            client.WanneShop = 24;
        } else if (npcId == 545) { // Dommik Al-Kharid Crafting Shop
            client.WanneShop = 25;
        } else if (npcId == 5809) { // Crafting shop
            client.WanneShop = 25;
        } else if (npcId == 524 || npcId == 525) { // ShopKeeper + Assistant General Store
            client.WanneShop = 26;
        } else if (npcId == 542) { // Louie Legs Al-Kharid Legs Shop
            client.WanneShop = 27;
        } else if (npcId == 544) { // Ranael Al-Kharid Skirt Shop
            client.WanneShop = 28;
        } else if (npcId == 2621) { // Hur-Koz TzHaar Shop Weapons,Amour
            client.WanneShop = 29;
        } else if (npcId == 2622) { // Hur-Lek TzHaar Shop Runes
            client.WanneShop = 30;
        } else if (npcId == 2620) { // Hur-Tel TzHaar Shop General Store
            client.WanneShop = 31;
        } else if (npcId == 692) { // Throwing shop Authentic Throwing Weapons
            client.WanneShop = 32;
        } else if (npcId == 6059) { // Archer's Armour Aaron's Archery Appendages
            client.WanneShop = 34;
        } else if (npcId == 537) { // Scavvo Champion's Rune shop
            client.WanneShop = 35;
        } else if (npcId == 536) { // Valaine Champion's guild shop
            client.WanneShop = 36;
        } else if (npcId == 933) { // Legend's Shop
            client.WanneShop = 37;
        } else if (npcId == 932) { // Legends General Store
            client.WanneShop = 38;
        }
        /*Ends*/
        else {
            client.println_debug("atNPC 2: " + npcId);
        }
    }
}