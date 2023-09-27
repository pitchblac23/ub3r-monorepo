package net.dodian.uber.game.model.player.skills.mining;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.player.packets.incoming.ClickObject;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Misc;
import net.dodian.utilities.Utils;

public class Mining {

    public static int getMiningEmote(int item) {
        switch (item) {
            case 1275: //bronze
                return 624;
            case 1271: //iron
                return 628;
            case 1273: //steel
                return 629;
            case 1269: // mithril
                return 627;
            case 1267: // addy
                return 626;
            case 1265: // rune
                return 625;
            case 11920: //dragon
            case 20014: //3rd age
                return 7139;
        }
        return -1;
    }

    public static int findPick(Client player) {
        int Eaxe = -1, Iaxe = -1;
        int weapon = player.getEquipment()[Equipment.Slot.WEAPON.getId()];
        for (int i = 0; i < Utils.picks.length; i++) {
            if (Utils.picks[i] == weapon) {
                if (player.getLevel(Skills.MINING) >= Utils.pickReq[i])
                    Eaxe = i;
            }
            for (int ii = 0; ii < player.playerItems.length; ii++) {
                if (Utils.picks[i] == player.playerItems[ii] - 1) {
                    if (player.getLevel(Skills.MINING) >= Utils.pickReq[i]) {
                        Iaxe = i;
                    }
                }
            }
        }
        return Eaxe > Iaxe ? Eaxe : Iaxe > Eaxe ? Iaxe : -1;
    }

    public static long getMiningSpeed(Client player) {
        double pickBonus = Utils.pickBonus[player.minePick];
        double level = (double) player.getLevel(Skills.MINING) / 600;
        double random = (double) Misc.random(150) / 100;
        double bonus = 1 + pickBonus * random + level;
        double time = Utils.mineTimes[player.mineIndex] / bonus;
        return (long) time;
    }

    public static void Gemchance(Client player) {
        double[] chance = new double[]{7.03, 3.91, 3.91, 3.12};
        int[] gemId = new int[]{1623, 1621, 1619, 1617};
        int rolledChance = 0, gem = -1, roll = Misc.chance(10000);
        for (int i = 0; i < chance.length && gem == -1; i++) {
            rolledChance += (int) (chance[i] * 100);
            if (roll <= rolledChance) gem = gemId[i + 1];
            else if (i + 1 == chance.length) gem = gemId[0];
        }
        if (player.freeSlots() > 0) {
            player.addItem(gem, 1);
            player.send(new SendMessage("You managed to find a " + player.GetItemName(gem).toLowerCase() + " while mining."));
        }
    }

    public static void mining(int index, Client player) {
        if (!player.playerHasItem(-1)) {
            player.send(new SendMessage("There is not enough space in your inventory."));
            player.resetAction(true);
            return;
        } else
        if (index != 6) {
            player.send(new SendMessage("You mine some " + player.GetItemName(Utils.ore[index]).toLowerCase() + "."));
        }
        player.addItem(Utils.ore[index], 1);
        player.giveExperience(Utils.oreExp[index], Skills.MINING);
        player.triggerRandom(Utils.oreExp[index]);
        if (Misc.chance(30) == 1) {
            player.send(new SendMessage("You take a rest."));
            player.resetAction(true);
        } else if (Misc.chance(86) == 1) {//256 without glory
            Gemchance(player);
        }
    }
}