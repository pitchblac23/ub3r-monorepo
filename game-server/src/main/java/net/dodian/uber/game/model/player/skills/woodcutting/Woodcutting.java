package net.dodian.uber.game.model.player.skills.woodcutting;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Misc;
import net.dodian.utilities.Utils;

public class Woodcutting {

    public static int[] woodcuttingDelays = {1200, 1800, 3000, 4200, 5400, 7200};
    public static int[] woodcuttingLevels = {1, 15, 30, 45, 60, 75};
    public static int[] woodcuttingLogs = {1511, 1521, 1519, 1517, 1515, 1513};
    public static int[] woodcuttingExp = {80, 152, 272, 400, 700, 1000};

    public static boolean CheckObjectSkill(int objectID, String name, Client c) {
        boolean GoFalse = false;
        /* Do we wish to keep? */
        if (name.contains("oak"))
            objectID = 1281;
        else if (name.contains("willow"))
            objectID = 1308;
        else if (name.contains("maple tree"))
            objectID = 1307;
        else if (name.contains("yew"))
            objectID = 1309;
        else if (name.contains("magic tree"))
            objectID = 1306;
        else if (name.equalsIgnoreCase("tree"))
            objectID = 1276;

        switch (objectID) {

            case 1276:
            case 1277:
            case 1278:
            case 1279:
            case 1280:
            case 1330:
            case 1332:
            case 2409:
            case 3033:
            case 3034:
            case 3035:
            case 3036:
            case 3879:
            case 3881:
            case 3882:
            case 3883: // Normal Tree
            case 1315:
            case 1316:
            case 1318:
            case 1319: // Evergreen
            case 1282:
            case 1283:
            case 1284:
            case 1285:
            case 1286:
            case 1287:
            case 1289:
            case 1290:
            case 1291:
            case 1365:
            case 1383:
            case 1384:
            case 5902:
            case 5903:
            case 5904: // Dead Tree
                c.woodcuttingIndex = 0;
                break;

            case 1281:
            case 3037: // Oak Tree
                c.woodcuttingIndex = 1;
                break;

            case 1308:
            case 5551:
            case 5552: // Willow Tree
                c.woodcuttingIndex = 2;
                break;

            case 1307:
            case 4674: // Maple Tree
                c.woodcuttingIndex = 3;
                break;

            case 1309: // Yew Tree
            case 1754:
                c.woodcuttingIndex = 4;
                break;

            case 1306: // Magic Tree
            case 1762:
                c.woodcuttingIndex = 5;
                break;

            default:
                GoFalse = true;
                break;
        }
        return !GoFalse;
    }

    public static int getWoodcuttingEmote(int item) {
        switch (item) {
            case 1351: //bronze
                return 879;
            case 1349: //iron
                return 877;
            case 1353: //steel
                return 875;
            case 1355: // mithril
                return 871;
            case 1357: // addy
                return 869;
            case 1359: // rune
                return 867;
            case 6739: // dragon
            case 20011: //3rd age
                return 2846;
        }
        return -1;
    }

    public static int findAxe(Client c) {
        int Eaxe = -1;
        int Iaxe = -1;
        int weapon = c.getEquipment()[Equipment.Slot.WEAPON.getId()];
        for (int i = 0; i < Utils.axes.length; i++) {
            if (Utils.axes[i] == weapon) {
                if (c.getLevel(Skills.WOODCUTTING) >= Utils.axeReq[i])
                    Eaxe = i;
            }
            for (int ii = 0; ii < c.playerItems.length; ii++) {
                if (Utils.axes[i] == c.playerItems[ii] - 1) {
                    if (c.getLevel(Skills.WOODCUTTING) >= Utils.axeReq[i]) {
                        Iaxe = i;
                    }
                }
            }
        }
        if (Eaxe >= Iaxe)
            return Eaxe;
        if (Iaxe >= Eaxe)
            return Iaxe;
        return -1;
    }

    public static long getWoodcuttingSpeed(Client c) {
        double axeBonus = Utils.axeBonus[findAxe(c)];
        double level = (double) c.getLevel(Skills.WOODCUTTING) / 600;
        double random = (double) Misc.random(150) / 100;
        double bonus = 1 + axeBonus * random + level;
        double time = woodcuttingDelays[c.woodcuttingIndex] / bonus;
        return (long) time;
    }

    public static boolean woodcutting(Client c) {
        if (c.randomed || c.fletchings || c.isFiremaking || c.shafting) {
            return false;
        }
        if (c.woodcuttingIndex < 0) {
            c.resetAction();
            return false;
        }

        int WCAxe = findAxe(c);
        if (WCAxe < 0) {
            c.send(new SendMessage("You need a axe in which you got the required woodcutting level for."));
            c.resetAction();
            return false;
        }
        if (woodcuttingLevels[c.woodcuttingIndex] > c.getLevel(Skills.WOODCUTTING)) {
            c.send(new SendMessage(
                    "You need a woodcutting level of " + woodcuttingLevels[c.woodcuttingIndex] + " to cut this tree."));
            c.resetAction();
            return false;
        }
        if (c.freeSlots() < 1) {
            c.send(new SendMessage("Your inventory is too full to hold anymore " + c.GetItemName(woodcuttingLogs[c.woodcuttingIndex]).toLowerCase() + "."));
            c.resetAction();
            return false;
        }
        if (System.currentTimeMillis() - c.lastAction >= 600 && !c.IsCutting) {
            c.lastAction = System.currentTimeMillis();
            c.send(new SendMessage("You swing your axe at the tree..."));
            c.requestAnim(getWoodcuttingEmote(Utils.axes[WCAxe]), 0);
            c.IsCutting = true;
        }
        if (c.IsCutting)
            c.requestAnim(getWoodcuttingEmote(Utils.axes[WCAxe]), 0);
        if (System.currentTimeMillis() - c.lastAction >= getWoodcuttingSpeed(c) && c.IsCutting) {
            c.lastAction = System.currentTimeMillis();
            c.giveExperience(woodcuttingExp[c.woodcuttingIndex], Skills.WOODCUTTING);
            c.send(new SendMessage("You cut some " + c.GetItemName(woodcuttingLogs[c.woodcuttingIndex]).toLowerCase() + "."));
            c.addItem(woodcuttingLogs[c.woodcuttingIndex], 1);
            c.triggerRandom(woodcuttingExp[c.woodcuttingIndex]);
            if (Misc.chance(30) == 1) {
                c.send(new SendMessage("You take a rest"));
                c.resetAction(true);
                return false;
            }
        }
        return true;
    }

    public static void resetWC(Client c) {
        c.woodcutting[0] = 0;
        c.woodcutting[1] = 0;
        c.woodcutting[2] = 0;
        c.woodcutting[4] = 0;
        c.skillX = -1;
        c.setSkillY(-1);
        c.woodcuttingIndex = -1;
        c.IsCutting = false;
        c.rerequestAnim();
    }
}