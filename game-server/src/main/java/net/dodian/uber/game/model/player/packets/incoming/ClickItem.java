package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.Position;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.herblore.HerbloreKt;
import net.dodian.uber.game.model.player.skills.herblore.PotionEffectsKt;
import net.dodian.utilities.DbTables;
import net.dodian.utilities.Misc;
import net.dodian.utilities.Utils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static net.dodian.uber.game.model.player.content.Minigames.Barrows.digCrypt;
import static net.dodian.uber.game.model.player.skills.mining.CoalBagKt.*;
import static net.dodian.uber.game.model.player.skills.prayer.PrayerKt.*;
import static net.dodian.uber.game.model.player.skills.runecrafting.EssBagsKt.*;
import static net.dodian.utilities.DatabaseKt.getDbConnection;

public class ClickItem implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        client.getInputStream().readSignedWordBigEndianA();
        int itemSlot = client.getInputStream().readUnsignedWordA();
        int itemId = client.getInputStream().readUnsignedWordBigEndian();

        if (fillEssencePouch(itemId, client)) {
            return;
        }
        if (itemId == 5733) {
            try {
                if (client.getPlayerNpc() < 1) {
                    client.send(new SendMessage("please try to do ::pnpc id"));
                    return;
                }
                if (Server.npcManager.getData(client.getPlayerNpc()) == null) {
                    Server.npcManager.reloadNpcConfig(client, client.getPlayerNpc(), "New Npc", "-1");
                    return;
                }
                Connection conn = getDbConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT 1 FROM " + DbTables.GAME_NPC_SPAWNS + " where id='" + client.getPlayerNpc() + "' && x='" + client.getPosition().getX() + "' && y='" + client.getPosition().getY() + "' && height='" + client.getPosition().getZ() + "'");
                if (rs.next()) {
                    client.send(new SendMessage("You already got a spawn on this position!"));
                    return;
                }
                int health = Server.npcManager.getData(client.getPlayerNpc()).getHP();
                statement
                        .executeUpdate("INSERT INTO " + DbTables.GAME_NPC_SPAWNS + " SET id = " + client.getPlayerNpc() + ", x=" + client.getPosition().getX()
                                + ", y=" + client.getPosition().getY() + ", height=" + client.getPosition().getZ() + ", hitpoints="
                                + health + ", live=1, face=0, rx=0,ry=0,rx2=0,ry2=0,movechance=0");
                statement.close();
                Server.npcManager.createNpc(client.getPlayerNpc(), new Position(client.getPosition().getX(), client.getPosition().getY(), client.getPosition().getZ()), 0);
                client.send(new SendMessage("Npc added = " + client.getPlayerNpc() + ", at x = " + client.getPosition().getX()
                        + " y = " + client.getPosition().getY() + "."));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (itemId == 2528) {
            client.openGenie();
            return;
        }
        if (itemId == 12019) {
            fillCoalBag(client);
            return;
        }
        else if (System.currentTimeMillis() - client.lastAction > 100) { //Due to how system handles time need this for 1 tick delay! Perhaps better way to do it?
            clickItem(client, itemSlot, itemId);
            client.lastAction = System.currentTimeMillis();
        }
        if (itemId == 952) {
            client.requestAnim(830, 0);
            digCrypt(client);
        }
        if (itemId == 11738) {
            client.deleteItem(itemId, 1);
            int[] herbs = {200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220};
            int Herbs = herbs[Misc.random(herbs.length -1)] ;
                client.addItem(Herbs, 5);
        }
    }

    public void clickItem(Client client, int slot, int id) {
        int item = client.playerItems[slot] - 1;
        if (item != id) {
            return; // might prevent stuff
        }
        if (client.duelRule[7] && client.inDuel && client.duelFight) {
            client.send(new SendMessage("Food has been disabled for this duel"));
            return;
        }
        boolean used = true;
        if (client.inDuel || client.duelFight || client.duelConfirmed || client.duelConfirmed2) {
            client.send(new SendMessage("RewardItem cannot be used in a duel!"));
            return;
        }
        if (buryBones(client, item, slot)) {
            return;
        }
        if (client.playerHasItem(item)) {
            HerbloreKt.herbCleaning(client, item, slot);
            PotionEffectsKt.drinkPotion(client, item, slot);
            switch (item) {
                case 1856:
                    used = false;
                    client.guideBook();
                break;
                case 315: //Shrimp
                case 319: //Anchovies
                case 2142: //Meat
                    client.eat(3, item, slot);
                    used = false;
                break;
                case 2309: //Bread
                    client.eat(5, item, slot);
                    used = false;
                break;
                case 3369: //Thin Snail
                    client.eat(7, item, slot);
                    used = false;
                break;
                case 333: //Trout
                    client.eat(8, item, slot);
                    used = false;
                break;
                case 329: //Salmon
                    client.eat(10, item, slot);
                    used = false;
                break;
                case 379: //Lobster
                    client.eat(12, item, slot);
                    used = false;
                break;
                case 373: //Swordfish
                    client.eat(14, item, slot);
                    used = false;
                break;
                case 7946: //Monkfish
                    client.eat(16, item, slot);
                    used = false;
                break;
                case 385: //Shark
                    client.eat(20, item, slot);
                    used = false;
                break;
                case 397: //Sea turtle
                    client.eat(22, item, slot);
                    used = false;
                break;
                case 391: //Manta ray
                    client.eat(24, item, slot);
                    used = false;
                break;
                case 1961: //Pumpkin
                case 1959: //Easter egg
                    client.eat(2, item, slot);
                    used = false;
                break;
                case 4155:
                    if (client.inTrade || client.inDuel)
                        break;
                    client.NpcDialogue = 15;
                    client.NpcDialogueSend = false;
                    client.nextDiag = -1;
                    used = false;
                    break;
                case 11877:
                    client.deleteItem(11877, slot, 1);
                    if(!client.playerHasItem(230))
                        client.addItemSlot(230,100, slot);
                    else
                        client.addItem(230, 100);
                    used = false;
                    break;
                case 11879:
                    client.deleteItem(11879, slot, 1);
                    if(!client.playerHasItem(228))
                        client.addItemSlot(228,100, slot);
                    else
                        client.addItem(228, 100);
                    used = false;
                    break;
                case 12859:
                    client.deleteItem(12859, slot,1);
                    if(!client.playerHasItem(222))
                        client.addItemSlot(222,100, slot);
                    else
                        client.addItem(222, 100);
                    used = false;
                    break;
                case 6199: //Something here!
                    int[] idss = {6856, 6857, 6859, 6861, 6860, 6858};
                    int rs = Utils.random(idss.length - 1);
                    client.deleteItem(6199, 1);
                    client.addItem(idss[rs], 1);
                    client.send(new SendMessage("Thank you for waiting patiently on us, take this as a token of gratitude!"));
                    used = false;
                    break;
                case 12854:
                    used = false;
                    int[] xPresents = {6542, 11996, 13345, 13346};
                    int slotNeeded = 3;
                    /* Check so we got enough slots! */
                    for (int i = 0; i < xPresents.length && slotNeeded > 0; i++) //check if we got the items or not!
                        if (client.playerHasItem(xPresents[i]))
                            slotNeeded--;
                    if (client.freeSlots() < slotNeeded) {
                        client.send(new SendMessage("You need at least " + slotNeeded + " free slot to open this!"));
                        break;
                    }
                    /* Delete item and add stuff! */
                    client.deleteItem(item, 1);
                    for (int xPresent : xPresents)
                        client.addItem(xPresent, 3 + Misc.random(6));
                    break;
                case 6542:
                case 11996:
                case 13345:
                case 13346:
                    used = false;
                    if (client.freeSlots() < 1) {
                        client.send(new SendMessage("You need at least one free slot to open this!"));
                        break;
                    }
                    int[] randomEventItem = {12887, 12888, 12889, 12890, 12891, 13343, 13344, 13203};
                    client.deleteItem(item, 1);
                    client.addItem(11997, 55 + Misc.random(500));
                    int chance = Misc.chance(1000);
                    if (chance == 1) {
                        int eventItemId = randomEventItem[Misc.random(randomEventItem.length - 1)];
                        client.addItem(eventItemId, 1);
                        client.send(new SendMessage("You found something of interest!"));
                        client.yell(client.getPlayerName() + " just found " + client.GetItemName(eventItemId).toLowerCase() + " in a " + client.GetItemName(item).toLowerCase() + "!");
                    }
                    break;
                case 11918:
                    used = false;
                    if (client.freeSlots() < 1) {
                        client.send(new SendMessage("You need at least one free slot to open this!"));
                        break;
                    }
                    int[] halloweenMasks = {1053, 1055, 1057};
                    client.deleteItem(item, 1);
                    int itemId = halloweenMasks[Misc.random(halloweenMasks.length - 1)];
                    client.addItem(itemId, 1);
                    client.send(new SendMessage("You found a " + client.GetItemName(itemId).toLowerCase() + "!"));
                    client.yell(client.getPlayerName() + " just found " + client.GetItemName(itemId).toLowerCase() + " in a " + client.GetItemName(item).toLowerCase() + "!");
                    break;
                default:
                    // client.send(new SendMessage("Nothing interesting happens"));
                    used = false;
                    break;
            }
        }
        if (used) {
            client.deleteItem(item, slot, 1);
        }
    }
}