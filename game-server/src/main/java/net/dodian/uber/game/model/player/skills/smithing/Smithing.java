package net.dodian.uber.game.model.player.skills.smithing;

import net.dodian.uber.game.Constants;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.Player;
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.packets.outgoing.SendSideTab;
import net.dodian.uber.game.model.player.packets.outgoing.SendString;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Range;
import net.dodian.utilities.Utils;
import java.util.ArrayList;

public class Smithing {

    public static void SetSmithing(int WriteFrame, Client c) {
        c.getOutputStream().createFrameVarSizeWord(53);
        c.getOutputStream().writeWord(WriteFrame);
        c.getOutputStream().writeWord(Constants.SmithingItems.length);
        for (int i = 0; i < Constants.SmithingItems.length; i++) {
            Constants.SmithingItems[i][0] += 1;
            if (Constants.SmithingItems[i][1] > 254) {
                c.getOutputStream().writeByte(255); // item's stack count. if over 254,
                // write byte 255
                c.getOutputStream().writeDWord_v2(Constants.SmithingItems[i][1]); // and
                // then the real value with writeDWord_v2
            } else {
                c.getOutputStream().writeByte(Constants.SmithingItems[i][1]);
            }
            if (Constants.SmithingItems[i][0] < 0) {
                c.playerItems[i] = 7500;
            }
            c.getOutputStream().writeWordBigEndianA(Constants.SmithingItems[i][0]); // item
            // id
        }
        c.getOutputStream().endFrameVarSizeWord();
    }

    public static int CheckSmithing(int ItemID, Client c) {
        int Type = -1;
        if (!c.IsItemInBag(2347)) {
            c.send(new SendMessage("You need a " + c.GetItemName(2347) + " to hammer bars."));
            return -1;
        }
        switch (ItemID) {
            case 2349: // Bronze Bar
                Type = 1;
                break;

            case 2351: // Iron Bar
                Type = 2;
                break;

            case 2353: // Steel Bar
                Type = 3;
                break;

            case 2359: // Mithril Bar
                Type = 4;
                break;

            case 2361: // Adamantite Bar
                Type = 5;
                break;

            case 2363: // Runite Bar
                Type = 6;
                break;
        }
        if (Type == -1)
            c.send(new SendMessage("You cannot smith this item."));
        else
            c.smithing[3] = ItemID;
        return Type;
    }

    public static boolean smithCheck(int id, Client c) {
        for (int i = 0; i < Constants.smithing_frame.length; i++) {
            for (int i1 = 0; i1 < Constants.smithing_frame[i].length; i1++) {
                if (id == Constants.smithing_frame[i][i1][0]) {
                    return true;
                }
            }
        }
        c.send(new SendMessage("Client hack detected!"));
        return false;
    }

    public static void OpenSmithingFrame(int Type, Client c) {
        int Type2 = Type - 1;
        int Length = 22;

        if (Type == 1 || Type == 2) {
            Length += 1;
        } else if (Type == 3) {
            Length += 2;
        }
        // Sending amount of bars + make text green if lvl is highenough
        c.send(new SendString("", 1132)); // Wire
        c.send(new SendString("", 1096));
        c.send(new SendString("", 11459)); // Lantern
        c.send(new SendString("", 11461));
        c.send(new SendString("", 1135)); // Studs
        c.send(new SendString("", 1134));
        String bar, color, color2, name = "";

        if (Type == 1) {
            name = "Bronze ";
        } else if (Type == 2) {
            name = "Iron ";
        } else if (Type == 3) {
            name = "Steel ";
        } else if (Type == 4) {
            name = "Mithril ";
        } else if (Type == 5) {
            name = "Adamant ";
        } else if (Type == 6) {
            name = "Rune ";
        }
        for (int i = 0; i < Length; i++) {
            bar = "bar";
            color = "@red@";
            color2 = "@bla@";
            if (Constants.smithing_frame[Type2][i][3] > 1) {
                bar = bar + "s";
            }
            if (c.getLevel(Skills.SMITHING) >= Constants.smithing_frame[Type2][i][2]) {
                color2 = "@whi@";
            }
            int Type3 = Type2;

            if (Type2 >= 3) {
                Type3 = (Type2 + 2);
            }
            if (c.AreXItemsInBag((2349 + (Type3 * 2)), Constants.smithing_frame[Type2][i][3])) {
                color = "@gre@";
            }
            c.send(new SendString(color + "" + Constants.smithing_frame[Type2][i][3] + "" + bar,
                    Constants.smithing_frame[Type2][i][4]));
            String linux_hack = c.GetItemName(Constants.smithing_frame[Type2][i][0]);
            int index = c.GetItemName(Constants.smithing_frame[Type2][i][0]).indexOf(name);
            if (index > 0) {
                linux_hack = linux_hack.substring(index + 1);
                c.send(new SendString(linux_hack, Constants.smithing_frame[Type2][i][5]));
            }
            // send(new SendString(
            // color2 + ""
            // + GetItemName(Constants.smithing_frame[Type2][i][0]).replace(name,
            // ""),
            // Constants.smithing_frame[Type2][i][5]);
        }
        Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][0][0]; // Dagger
        Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][0][1];
        Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][4][0]; // Sword
        Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][4][1];
        Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][8][0]; // Scimitar
        Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][8][1];
        Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][9][0]; // Long
        // Sword
        Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][9][1];
        Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][18][0]; // 2
        // hand
        // sword
        Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][18][1];
        Smithing.SetSmithing(1119, c);
        Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][1][0]; // Axe
        Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][1][1];
        Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][2][0]; // Mace
        Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][2][1];
        Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][13][0]; // Warhammer
        Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][13][1];
        Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][14][0]; // Battle
        // axe
        Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][14][1];
        Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][17][0]; // Claws
        Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][17][1];
        Smithing.SetSmithing(1120, c);
        Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][15][0]; // Chain
        // body
        Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][15][1];
        Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][20][0]; // Plate
        // legs
        Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][20][1];
        Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][19][0]; // Plate
        // skirt
        Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][19][1];
        Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][21][0]; // Plate
        // body
        Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][21][1];
        Constants.SmithingItems[4][0] = -1; // Lantern
        Constants.SmithingItems[4][1] = 0;
        if (Type == 2 || Type == 3) {
            color2 = "@bla@";
            if (c.getLevel(Skills.SMITHING) >= Constants.smithing_frame[Type2][22][2]) {
                color2 = "@whi@";
            }
            Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][22][0]; // Lantern
            Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][22][1];
            c.send(new SendString(color2 + "" + c.GetItemName(Constants.smithing_frame[Type2][22][0]).replace(name, ""), 11461));
        }
        Smithing.SetSmithing(1121, c);
        Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][3][0]; // Medium
        Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][3][1];
        Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][10][0]; // Full
        // Helm
        Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][10][1];
        Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][12][0]; // Square
        Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][12][1];
        Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][16][0]; // Kite
        Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][16][1];
        Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][6][0]; // Nails
        Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][6][1];
        Smithing.SetSmithing(1122, c);
        Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][5][0]; // Dart
        // Tips
        Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][5][1];
        Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][7][0]; // Arrow
        // Heads
        Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][7][1];
        Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][11][0]; // Knives
        Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][11][1];
        Constants.SmithingItems[3][0] = -1; // Wire
        Constants.SmithingItems[3][1] = 0;
        if (Type == 1) {
            color2 = "@bla@";
            if (c.getLevel(Skills.SMITHING) >= Constants.smithing_frame[Type2][22][2]) {
                color2 = "@whi@";
            }
            Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][22][0]; // Wire
            Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][22][1];
            c.send(new SendString(color2 + "" + c.GetItemName(Constants.smithing_frame[Type2][22][0]).replace(name, ""), 1096));
        }
        for (int i = 0; i < 22; i++) {
            if (c.getLevel(Skills.SMITHING) >= Constants.smithing_frame[Type2][i][2]) {
                color2 = "@whi@";
            } else
                color2 = "@bla@";
            c.send(new SendString(color2 + "" + c.GetItemName(Constants.smithing_frame[Type2][i][0]).replace(name, ""), Constants.smithing_frame[Type2][i][5]));
        }
        Constants.SmithingItems[4][0] = -1; // Studs
        Constants.SmithingItems[4][1] = 0;
        if (Type == 3) {
            color2 = "@bla@";
            if (c.getLevel(Skills.SMITHING) >= Constants.smithing_frame[Type2][23][2]) {
                color2 = "@whi@";
            }
            Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][23][0]; // Studs
            Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][23][1];
            c.send(new SendString(color2 + "" + c.GetItemName(Constants.smithing_frame[Type2][23][0]).replace(name, ""), 1134));
        }
        Smithing.SetSmithing(1123, c);
        c.showInterface(994);
        c.smithing[2] = Type;
    }

    public static void startSmelt(int id, Client c) {
        int[] amounts = {1, 5, 10, 28};
        int index = 0, index2 = 0;
        for (int i = 0; i < Utils.buttons_smelting.length; i++) {
            if (id == Utils.buttons_smelting[i]) {
                index = i % 4;
                index2 = i / 4;
            }
        }
        c.smelt_id = Utils.smelt_bars[index2][0];
        c.smeltCount = amounts[index];
        c.smeltExperience = Utils.smelt_bars[index2][1] * 4;
        c.smelting = true;
        c.send(new RemoveInterfaces());
    }

    public static boolean smithing(Client c) {
        if (c.IsItemInBag(2347)) {
            if (!smithCheck(c.smithing[4], c)) {
                c.IsAnvil = true;
                c.resetAction();
                return false;
            }
            int bars = 0;
            int Length = 22;
            int barid;
            int xp = 0;
            int ItemN = 1;

            if (c.smithing[2] >= 4) {
                barid = (2349 + ((c.smithing[2] + 1) * 2));
            } else {
                barid = (2349 + ((c.smithing[2] - 1) * 2));
            }
            if (c.smithing[2] == 1 || c.smithing[2] == 2) {
                Length += 1;
            } else if (c.smithing[2] == 3) {
                Length += 2;
            }
            int[] possibleBars = {2349, 2351, 2353, 2359, 2361, 2363};
            int[] bar_xp = {13, 25, 38, 50, 63, 75};
            for (int i = 0; i < Constants.smithing_frame.length; i++) {
                for (int i1 = 0; i1 < Constants.smithing_frame[i].length; i1++) {
                    for (int i2 = 0; i2 < Constants.smithing_frame[i][i1].length; i2++) {
                        if (Constants.smithing_frame[i][i1][0] == c.smithing[4]) {
                            if (!c.AreXItemsInBag(possibleBars[i], Constants.smithing_frame[i][i1][3])) {
                                c.send(new SendMessage("You are missing bars needed to smith this!"));
                                Player.IsAnvil = true;
                                c.resetAction();
                                return false;
                            }
                            xp = bar_xp[i];
                        }
                    }
                }
            }
            for (int i = 0; i < Length; i++) {
                if (Constants.smithing_frame[(c.smithing[2] - 1)][i][0] == c.smithing[4]) {
                    bars = Constants.smithing_frame[(c.smithing[2] - 1)][i][3];
                    if (c.smithing[1] == 0) {
                        c.smithing[1] = Constants.smithing_frame[(c.smithing[2] - 1)][i][2];
                    }
                    ItemN = Constants.smithing_frame[(c.smithing[2] - 1)][i][1];
                }
            }
            if (c.getLevel(Skills.SMITHING) >= c.smithing[1]) {
                if (c.AreXItemsInBag(barid, bars)) {
                    int[] barLevelRequired = {1, 15, 30, 55, 70, 85};
                    if (System.currentTimeMillis() - c.lastAction >= 600 && !Player.IsAnvil) {
                        c.lastAction = System.currentTimeMillis();
                        c.setFocus(c.skillX, c.skillY);
                        c.send(new SendMessage("You start hammering the bar..."));
                        c.requestAnim(0x382, 0);
                        Player.IsAnvil = true;
                        int diff = c.getLevel(Skills.SMITHING) - barLevelRequired[CheckSmithing(c.smithing[3], c) - 1];
                        c.smithing[0] = 5 - (diff >= 14 ? 2 : diff >= 7 ? 1 : 0);
                    }
                    if (System.currentTimeMillis() - c.lastAction >= (c.smithing[0] * 600) && Player.IsAnvil) {
                        c.lastAction = System.currentTimeMillis();
                        for (int i = 0; i < bars; i++) {
                            c.deleteItem(barid, c.GetItemSlot(barid), c.playerItemsN[c.GetItemSlot(barid)]);
                        }
                        int experience = xp * bars * 30;
                        c.giveExperience(experience, Skills.SMITHING);
                        c.addItem(c.smithing[4], ItemN);
                        c.send(new SendMessage("You smith a " + c.GetItemName(c.smithing[4]) + ""));
                        c.requestAnim(0x382, 0);
                        c.triggerRandom(experience);
                    }
                } else {
                    c.send(new SendMessage("You need " + bars + " " + c.GetItemName(barid) + " to smith a " + c.GetItemName(c.smithing[4])));
                    c.rerequestAnim();
                    c.resetAction();
                }
            } else {
                c.send(new SendMessage("You need " + c.smithing[1] + " Smithing to smith a " + c.GetItemName(c.smithing[4])));
                Player.IsAnvil = true;
                c.resetAction();
                return false;
            }
        } else {
            c.send(new SendMessage("You need a " + c.GetItemName(2347) + " to hammer bars."));
            Player.IsAnvil = true;
            c.resetAction();
            return false;
        }
        return true;
    }

    public static void smelt(int id, Client c) {
        c.requestAnim(0x383, 0);
        c.smelt_id = id;
        c.smelting = true;
        int smelt_barId = -1;
        ArrayList<Integer> removed = new ArrayList<>();
        if (c.smeltCount < 1) {
            c.resetAction(true);
            return;
        }
        c.smeltCount--;
        switch (id) {
            case 2349: // bronze
                if (c.playerHasItem(436) && c.playerHasItem(438)) {
                    smelt_barId = 2349;
                    removed.add(436);
                    removed.add(438);
                } else
                    c.send(new SendMessage("You need a tin and copper to do this!"));
                break;
            case 2351: // iron ore
                if (c.getLevel(Skills.SMITHING) < 15) {
                    c.send(new SendMessage("You need level 15 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(440)) {
                    int ran = new Range(1, 100).getValue();
                    int diff = (c.getLevel(Skills.SMITHING) + 1) / 4;
                    if (ran <= 50 + diff) {
                        smelt_barId = 2351;
                        removed.add(440);
                    } else {
                        smelt_barId = 0;
                        removed.add(440);
                        c.send(new SendMessage("You fail to refine the iron"));
                    }
                } else
                    c.send(new SendMessage("You need a iron ore to do this!"));
                break;
            case 2353:
                if (c.getLevel(Skills.SMITHING) < 30) {
                    c.send(new SendMessage("You need level 30 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                    smelt_barId = 2353;
                    removed.add(440);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a iron ore and 2 coal to do this!"));
                break;
            case 2357:
                if (c.getLevel(Skills.SMITHING) < 40) {
                    c.send(new SendMessage("You need level 40 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(444, 1)) {
                    smelt_barId = 2357;
                    removed.add(444);
                } else
                    c.send(new SendMessage("You need a gold ore to do this!"));
                break;
            case 2359:
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.send(new SendMessage("You need level 55 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smelt_barId = 2359;
                    removed.add(447);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a mithril ore and 3 coal to do this!"));
                break;
            case 2361:
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.send(new SendMessage("You need level 70 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(449) && c.playerHasItem(453, 4)) {
                    smelt_barId = 2361;
                    removed.add(449);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a adamantite ore and 4 coal to do this!"));
                break;
            case 2363:
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.send(new SendMessage("You need level 85 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smelt_barId = 2363;
                    removed.add(451);
                    for (int i = 0; i < 6; i++)
                        removed.add(453);
                } else
                    c.send(new SendMessage("You need a runite ore and 6 coal to do this!"));
                break;
            default:
                c.println("Unknown smelt: " + id);
                break;
        }
        if (smelt_barId == -1) {
            c.resetAction();
            return;
        }
        for (Integer removeId : removed) {
            c.deleteItem(removeId, 1);
        }
        if (smelt_barId > 0) {
            c.addItem(smelt_barId, 1);
            c.giveExperience(c.smeltExperience, Skills.SMITHING);
            c.triggerRandom(c.smeltExperience);
        }
    }

    public static void superHeat(int id, Client c) {
        c.resetAction(false);
        ArrayList<Integer> removed = new ArrayList<>();
        int smelt_barId = 0;
        boolean fail = false;
        switch (id) {

            case 436: // Tin
            case 438: // Copper
                if (c.playerHasItem(436) && c.playerHasItem(438)) {
                    smelt_barId = 2349;
                    removed.add(436);
                    removed.add(438);
                } else
                    c.send(new SendMessage("You need a tin and copper to do this!"));
                break;
            case 440: // iron ore
                if (c.playerHasItem(440) && !c.playerHasItem(453, 2)) {
                    if (c.getLevel(Skills.SMITHING) < 15) {
                        c.send(new SendMessage("You need level 15 smithing to do this!"));
                        break;
                    }
                    smelt_barId = 2351;
                    removed.add(440);
                } else if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                    if (c.getLevel(Skills.SMITHING) < 30) {
                        c.send(new SendMessage("You need level 30 smithing to do this!"));
                        break;
                    }
                    smelt_barId = 2353;
                    removed.add(440);
                    removed.add(453);
                    removed.add(453);
                }
                break;
            case 444:
                if (c.getLevel(Skills.SMITHING) < 40) {
                    c.send(new SendMessage("You need level 40 smithing to do this!"));
                    break;
                }
                smelt_barId = 2357;
                removed.add(444);
                break;
            case 447:
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.send(new SendMessage("You need level 55 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smelt_barId = 2359;
                    removed.add(447);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a mithril ore and 3 coal to do this!"));
                break;
            case 449:
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.send(new SendMessage("You need level 70 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(449) && c.playerHasItem(453, 4)) {
                    smelt_barId = 2361;
                    removed.add(449);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a adamantite ore and 4 coal to do this!"));
                break;
            case 451:
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.send(new SendMessage("You need level 85 smithing to do this!"));
                    break;
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smelt_barId = 2363;
                    removed.add(451);
                    for (int i = 0; i < 6; i++)
                        removed.add(453);
                } else
                    c.send(new SendMessage("You need a runite ore and 6 coal to do this!"));
                break;
            default:
                fail = true;
                break;
        }
        int xp = 0;
        for (int i = 0; i < Utils.smelt_bars.length && xp == 0; i++)
            if (Utils.smelt_bars[i][0] == smelt_barId)
                xp = Utils.smelt_bars[i][1] * 4;
        if (fail) {
            c.send(new SendMessage("You can only use this spell on ores."));
            c.callGfxMask(85, 100);
        } else if (smelt_barId > 0 && xp > 0) {
            c.lastMagic = System.currentTimeMillis();
            c.requestAnim(725, 0);
            c.callGfxMask(148, 100);
            c.deleteRunes(new int[]{561}, new int[]{1});
            for (Integer removeId : removed)
                c.deleteItem(removeId, 1);
            c.addItem(smelt_barId, 1);
            c.giveExperience(xp, Skills.SMITHING);
            c.giveExperience(500, Skills.MAGIC);
        } else
            c.send(new SendMessage("This give no xp?!"));
            c.send(new SendSideTab(6));
    }

    public static void resetSM(Client c) {
        if(Player.IsAnvil) {
            c.smithing[0] = 0;
            c.smithing[1] = 0;
            c.smithing[2] = 0;
            c.smithing[3] = -1;
            c.smithing[4] = -1;
            c.smithing[5] = 0;
            Player.IsAnvil = false;
            c.skillX = -1;
            c.setSkillY(-1);
            Player.IsAnvil = false;
            c.rerequestAnim();
        }
    }
}