package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.UpdateFlag;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.SendSideTab;
import net.dodian.uber.game.model.player.skills.Skills;

import static net.dodian.uber.game.model.player.skills.smithing.SmeltingKt.*;
import static net.dodian.utilities.DotEnvKt.getServerDebugMode;

public class MagicOnItems implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int castOnSlot = client.getInputStream().readSignedWord();
        int castOnItem = client.getInputStream().readSignedWordA();
        client.getInputStream().readSignedWord();
        int castSpell = client.getInputStream().readSignedWordA();
        int lowAlch = (int) Math.floor(Server.itemManager.getShopBuyValue(castOnItem));
        int highAlch = (int) Math.floor(Server.itemManager.getAlchemy(castOnItem));
        if (getServerDebugMode()) { client.println("castSpell = " + castSpell); }
        if (!(System.currentTimeMillis() - client.lastMagic >= 1800) || !client.playerHasItem(castOnItem) || !(client.playerItems[castOnSlot] == (castOnItem + 1))) {
            client.send(new SendSideTab(6));
            return;
        }
        if (client.randomed || client.randomed2) {
            return;
        }
        if (castSpell == 1155) {// Sapphire enchant
            if (client.getLevel(Skills.MAGIC) < 7) {
                client.sendMessage("You need a magic level of 7 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{2})) {
                client.sendMessage("You need 2 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 1637)
                item = 2550;
            else if (castOnItem == 1656)
                item = 0;
            else if (castOnItem == 1694)
                item = 1727;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{2});
            client.addItem(item, 1);
            client.giveExperience(175, Skills.MAGIC);
        }
        if (castSpell == 1162) { //LowAlch
            if (client.getLevel(Skills.MAGIC) < 21) {
                client.sendMessage("You need a magic level of 21 to cast this spell.");
                return;
            }
            if (!client.playerHasItem(561) || (castOnItem == 561 && !client.playerHasItem(561, 2))) {
                client.sendMessage("Requires nature rune.");
                return;
            }
            if (castOnItem == 995 || (castOnItem >= 2415 && castOnItem <= 2417) || lowAlch < 1) {
                client.sendMessage("This item can't be alched.");
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.giveExperience(600, Skills.MAGIC);
            client.animationReset = System.currentTimeMillis() + 3200;
            client.requestAnim(713, 0);
            client.callGfxMask(113, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, castOnSlot, 1);
            client.deleteItem(561, 1);
            client.addItem(995, lowAlch);
            client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        }
        if (castSpell == 1165) {// Emerald enchant
            if (client.getLevel(Skills.MAGIC) < 27) {
                client.sendMessage("You need a magic level of 27 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{4})) {
                client.sendMessage("You need 4 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 1639)
                item = 0;
            else if (castOnItem == 1658)
                item = 0;
            else if (castOnItem == 1696)
                item = 1729;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{4});
            client.addItem(item, 1);
            client.giveExperience(370, Skills.MAGIC);
        }
        if (castSpell == 1173) { //Super heat
            client.resetAction();
            if (client.getLevel(Skills.MAGIC) < 43) {
                client.sendMessage("You need a magic level of 43 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{561}, new int[]{2})) {
                client.sendMessage("You need 2 nature runes to cast this spell.");
                return;
            }
            superHeat(castOnItem, client);
        }
        if (castSpell == 1176) {// Ruby
            if (client.getLevel(Skills.MAGIC) < 49) {
                client.sendMessage("You need a magic level of 49 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{6})) {
                client.sendMessage("You need 6 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 1641)
                item = 2568;
            else if (castOnItem == 1660)
                item = 0;
            else if (castOnItem == 1698)
                item = 1725;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{6});
            client.addItem(item, 1);
            client.giveExperience(590, Skills.MAGIC);
        }
        if (castSpell == 1178) { //HighAlch
            if (client.getLevel(Skills.MAGIC) < 55) {
                client.sendMessage("You need a magic level of 55 to cast this spell.");
                return;
            }
            if (!client.playerHasItem(561) || (castOnItem == 561 && !client.playerHasItem(561, 2))) {
                client.sendMessage("Requires two nature runes to cast this spell.");
                return;
            }
            if (castOnItem == 995 || (castOnItem >= 2415 && castOnItem <= 2417) || lowAlch < 1) {
                client.sendMessage("This item can't be alched.");
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.giveExperience(600, Skills.MAGIC);
            client.animationReset = System.currentTimeMillis() + 3200;
            client.requestAnim(713, 0);
            client.callGfxMask(113, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, castOnSlot, 1);
            client.deleteItem(561, 2);
            client.addItem(995, highAlch);
            client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        }
        if (castSpell == 1180) {// Diamond
            if (client.getLevel(Skills.MAGIC) < 57) {
                client.sendMessage("You need a magic level of 57 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{8})) {
                client.sendMessage("You need 8 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 1643)
                item = 2570;
            else if (castOnItem == 1662)
                item = 0;
            else if (castOnItem == 1700)
                item = 1731;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{8});
            client.addItem(item, 1);
            client.giveExperience(670, Skills.MAGIC);
        }
        if (castSpell == 1187) {// Dragonstone
            if (client.getLevel(Skills.MAGIC) < 68) {
                client.sendMessage("You need a magic level of 68 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{10})) {
                client.sendMessage("You need 10 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 1645)
                item = 2572;
            else if (castOnItem == 1664)
                item = 0;
            else if (castOnItem == 1702)
                item = 1704;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{10});
            client.addItem(item, 1);
            client.giveExperience(780, Skills.MAGIC);
        }
        if (castSpell == 24559) {
            if (client.getLevel(Skills.MAGIC) < 87) {
                client.sendMessage("You need a magic level of 87 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{10})) {
                client.sendMessage("You need 10 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 6575)
                item = 6583;
            else if (castOnItem == 6577)
                item = 11128;
            else if (castOnItem == 6581)
                item = 6585;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{10});
            client.addItem(item, 1);
            client.giveExperience(1150, Skills.MAGIC);
        }
        if (castSpell == 6003) {// Onyx
            if (client.getLevel(Skills.MAGIC) < 87) {
                client.sendMessage("You need a magic level of 87 to cast this spell.");
                return;
            }
            if (!client.hasRunes(new int[]{564}, new int[]{10})) {
                client.sendMessage("You need 10 cosmic runes to cast this spell.");
                return;
            }
            int item = 0;
            if (castOnItem == 6575)
                item = 6583;
            else if (castOnItem == 6577)
                item = 11128;
            else if (castOnItem == 6581)
                item = 6585;
            else
                client.sendMessage("Cant enchant this item.");
            if (item == 0) {
                return;
            }
            client.lastMagic = System.currentTimeMillis();
            client.requestAnim(720, 0);
            client.callGfxMask(115, 100);
            client.send(new SendSideTab(6));
            client.deleteItem(castOnItem, 1);
            client.deleteRunes(new int[]{564}, new int[]{10});
            client.addItem(item, 1);
            client.giveExperience(1150, Skills.MAGIC);
        }
    }
}