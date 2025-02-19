package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Constants;
import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Utils;

import static net.dodian.uber.game.model.player.skills.crafting.CraftingKt.*;
import static net.dodian.uber.game.model.player.skills.crafting.GoldCraftingKt.*;

public class ItemOnItem implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int usedWithSlot = client.getInputStream().readUnsignedWord();
        int itemUsedSlot = client.getInputStream().readUnsignedWordA();
        client.getInputStream().readUnsignedWord();
        client.getInputStream().readUnsignedWord();

        int useWith = client.playerItems[usedWithSlot] - 1;
        int itemUsed = client.playerItems[itemUsedSlot] - 1;
        if (!client.playerHasItem(itemUsed) || !client.playerHasItem(useWith)) {
            return;
        }
        if (useWith == 5733 || itemUsed == 5733) { //Potato
            client.playerPotato.clear();
            client.playerPotato.add(0, 4);
            client.playerPotato.add(1, useWith == 5733 ? itemUsedSlot : usedWithSlot);
            client.playerPotato.add(2, useWith == 5733 ? itemUsed : useWith);
            client.playerPotato.add(3, 1);
            return;
        }
        int otherItem = client.playerItems[usedWithSlot] - 1;
        boolean knife = (useWith == 946 || itemUsed == 946) || (useWith == 5605 || itemUsed == 5605) ? true : false;
        if ((itemUsed == 2383 && useWith == 2382) || (itemUsed == 2382 && useWith == 2383)) {
            if (client.getSkillLevel(Skills.CRAFTING) >= 60) {
                client.deleteItem(itemUsed, itemUsedSlot, 1);
                client.deleteItem(otherItem, usedWithSlot, 1);
                client.addItem(989, 1);
                client.sendMessage("You have crafted the crystal key!  I wonder what it's for?");
            } else {
                client.sendMessage("You need 60 crafting to make the crystal key");
            }
        }
        if (itemUsed >= 6157 && itemUsed <= 6161 && useWith >= 6157 && useWith <= 6161) {
            client.NpcDialogueSend = false;
            client.NpcDialogue = 10000;
        }
        for (int h = 0; h < Utils.herbs.length; h++) {
            if ((itemUsed == Utils.herbs[h] && otherItem == 227) || (itemUsed == 227 && otherItem == Utils.herbs[h])) {
                if (client.getSkillLevel(Skills.HERBLORE) < Utils.req[h]) {
                    client.sendMessage("Requires herblore level " + Utils.req[h]);
                    return;
                }
                int xp = 0;
                xp = Utils.grimy_herbs_xp[h];
                client.setPots(600, itemUsed, otherItem, Utils.unfinished[h], xp);
                return;
            }
            if ((itemUsed == Utils.secondary[h] && otherItem == Utils.unfinished[h])
                    || (itemUsed == Utils.unfinished[h] && otherItem == Utils.secondary[h])) {
                if (client.getLevel(Skills.HERBLORE) < Utils.req[h]) {
                    client.sendMessage("Requires herblore level " + Utils.req[h]);
                    return;
                }
                client.setPots(1200, itemUsed, otherItem, Utils.finished[h], Utils.potexp[h]);
            }
        }
        for (int i = 0; i < Utils.pot_4_dose.length; i++) {
            if ((itemUsed == Utils.pot_4_dose[i] && useWith == 229) || (itemUsed == 229 && useWith == Utils.pot_4_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_2_dose[i], 1);
                client.addItem(Utils.pot_2_dose[i], 1);
            }
        }
        for (int i = 0; i < Utils.pot_3_dose.length; i++) {
            if ((itemUsed == Utils.pot_3_dose[i] && useWith == Utils.pot_3_dose[i]) || (itemUsed == Utils.pot_3_dose[i] && useWith == Utils.pot_3_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_4_dose[i], 1);
                client.addItem(Utils.pot_2_dose[i], 1);
            } else if ((itemUsed == Utils.pot_3_dose[i] && useWith == Utils.pot_2_dose[i]) || (itemUsed == Utils.pot_2_dose[i] && useWith == Utils.pot_3_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_4_dose[i], 1);
                client.addItem(Utils.pot_1_dose[i], 1);
            }
        }
        for (int i = 0; i < Utils.pot_2_dose.length; i++) {
            if ((itemUsed == Utils.pot_2_dose[i] && useWith == 229) || (itemUsed == 229 && useWith == Utils.pot_2_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_1_dose[i], 1);
                client.addItem(Utils.pot_1_dose[i], 1);
            } else if ((itemUsed == Utils.pot_2_dose[i] && useWith == Utils.pot_2_dose[i]) || (itemUsed == Utils.pot_2_dose[i] && useWith == Utils.pot_2_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_4_dose[i], 1);
                client.addItem(229, 1);
            }
        }
        for (int i = 0; i < Utils.pot_1_dose.length; i++) {
            if ((itemUsed == Utils.pot_1_dose[i] && useWith == Utils.pot_1_dose[i]) || (itemUsed == Utils.pot_1_dose[i] && useWith == Utils.pot_1_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_2_dose[i], 1);
                client.addItem(229, 1);
            } else if ((itemUsed == Utils.pot_1_dose[i] && useWith == Utils.pot_2_dose[i]) || (itemUsed == Utils.pot_2_dose[i] && useWith == Utils.pot_1_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_3_dose[i], 1);
                client.addItem(229, 1);
            } else if ((itemUsed == Utils.pot_1_dose[i] && useWith == Utils.pot_3_dose[i]) || (itemUsed == Utils.pot_3_dose[i] && useWith == Utils.pot_1_dose[i])) {
                client.deleteItem(itemUsed, 1);
                client.deleteItem(useWith, 1);
                client.addItem(Utils.pot_4_dose[i], 1);
                client.addItem(229, 1);
            }
        }

        if (itemUsed == 85 || otherItem == 85) {
            int otherId = itemUsed == 85 ? otherItem : itemUsed;
            if (otherId == 1543 || otherId == 1544) {
                if (!client.checkItem(otherId + 1)) {
                    client.deleteItem(85, 1);
                    client.addItem(otherId + 1, 1);
                    client.sendMessage("Your key shine bright and turned your " + client.GetItemName(85).toLowerCase() + " into a " + client.GetItemName(otherId + 1).toLowerCase());
                } else
                    client.sendMessage("I already have a " + client.GetItemName(otherId + 1).toLowerCase() + " in " + (client.playerHasItem(otherId + 1) ? "my inventory!" : "my bank!"));
            } else if (otherId == 2382 || otherId == 2383) {
                if (!client.checkItem(989) && (!client.checkItem(2382) || !client.checkItem(2383))) {
                    client.deleteItem(85, 1);
                    client.addItem(otherId == 2382 ? 2383 : 2382, 1);
                    client.sendMessage("Your key shine bright and turned your " + client.GetItemName(85).toLowerCase() + " into a " + client.GetItemName(otherId == 2382 ? 2383 : 2382).toLowerCase());
                } else if (!client.checkItem(989) && client.checkItem(2382) && client.checkItem(2383))
                    client.sendMessage("You already have the crystals, perhaps you should combine them?");
                else
                    client.sendMessage("I already have a " + client.GetItemName(989).toLowerCase() + " in " + (client.playerHasItem(989) ? "my inventory!" : "my bank!"));
            }
        }

        boolean rainbowHat = Server.itemManager.getName(itemUsed).endsWith("partyhat") && Server.itemManager.getName(otherItem).endsWith("partyhat") && Server.itemManager.isNote(itemUsed) && Server.itemManager.isNote(otherItem);
        if (rainbowHat) {
            boolean hasItems = true;
            for (int i = 1038; i <= 1048 && hasItems; i += 2)
                if (!client.playerHasItem(i))
                    hasItems = false;
            if (hasItems) {
                for (int i = 1038; i <= 1048; i += 2)
                    client.deleteItem(i, 1);
                client.addItemSlot(11863, 1, itemUsedSlot);
            } else
                client.sendMessage("You need one of each partyhat to combine it into the rainbow partyhat!");
        }
        if (knife && (itemUsed == 11863 || otherItem == 11863)) {
            int slotRemain = 5 - client.getFreeSpace();
            if (slotRemain <= 0) {
                client.deleteItem(11863, 1);
                for (int i = 1038; i <= 1048; i += 2)
                    client.addItem(i, 1);
                client.sendMessage("You gentle used the knife on the paper hat and cut it into different color partyhats.");
            } else
                client.sendMessage("You need to have " + (slotRemain == 1 ? "one" : slotRemain) + " empty slot" + (slotRemain != 1 ? "s" : "") + " to tear the rainbow partyhat apart.");
        }
        if ((itemUsed == 962 || otherItem == 962) && (itemUsed == 11863 || otherItem == 11863)) {
            client.deleteItem(962, itemUsed == 962 ? itemUsedSlot : usedWithSlot, 1);
            client.deleteItem(11863, itemUsed == 11863 ? itemUsedSlot : usedWithSlot, 1);
            client.addItemSlot(11862, 1, itemUsed == 11863 ? itemUsedSlot : usedWithSlot);
        }
        if (knife && (itemUsed == 11862 || otherItem == 11862)) {
            if (client.getFreeSpace() > 0) {
                client.deleteItem(11862, itemUsed == 11862 ? itemUsedSlot : usedWithSlot, 1);
                client.addItemSlot(11863, 1, itemUsed == 11862 ? itemUsedSlot : usedWithSlot);
                client.addItem(962, 1);
                client.sendMessage("You gentle used the knife on the paper hat and cut it into a cracker and a rainbow partyhat.");
            } else client.sendMessage("You need to have at least one space to tear the black partyhat apart!");
        }

        int[][] dyes = {{-1, 1019}, {1763, 1007}, {1765, 1023}, {1767, 1021}, {1769, 1031}, {1771, 1027}, {1773, 1029}}; //Black, Red, yellow, blue, orange, green, purple
        for (int i = 0; i < dyes.length; i++)
            if (itemUsed == dyes[i][0] || otherItem == dyes[i][0]) {
                for (int dye = 0; dye < dyes.length; dye++)
                    if ((itemUsed == dyes[i][0] && otherItem == dyes[dye][1]) || (otherItem == dyes[i][0] && itemUsed == dyes[dye][1])) {
                        if (dyes[dye][1] != dyes[i][1]) {
                            client.deleteItem(itemUsed, itemUsedSlot, 1);
                            client.deleteItem(otherItem, usedWithSlot, 1);
                            client.addItemSlot(dyes[i][1], 1, itemUsed == dyes[dye][1] ? itemUsedSlot : usedWithSlot);
                        } else client.sendMessage("There is no point in using the same color as the cape!");
                        break;
                    }
            }

        int[] otherSlayerItems = {8923, 6708, 4166};
        boolean maskCreation = false, blackmask = (itemUsed == 8921 && otherItem != 11784) || (itemUsed == 11784 && otherItem != 8921) || (otherItem == 8921 && itemUsed != 11784) || (otherItem == 11784 && itemUsed != 8921);
        if (blackmask) {
            for (int i = 0; i < otherSlayerItems.length && !maskCreation; i++)
                if (itemUsed == otherSlayerItems[i] || otherItem == otherSlayerItems[i])
                    maskCreation = true;
            if (maskCreation) {
                if (client.getSkillLevel(Skills.CRAFTING) >= 70) {
                    boolean gotItems = true;
                    for (int i = 0; i < otherSlayerItems.length && gotItems; i++)
                        if (!client.playerHasItem(otherSlayerItems[i])) {
                            gotItems = false;
                            client.sendMessage("You are missing " + Server.itemManager.getName(otherSlayerItems[i]).toLowerCase() + " in order to craft the " + Server.itemManager.getName(itemUsed == 8921 || otherItem == 8921 ? 11864 : 11865).toLowerCase() + "!");
                        }
                    if (gotItems) {
                        for (int i = 0; i < otherSlayerItems.length && gotItems; i++)
                            client.deleteItem(otherSlayerItems[i], 1);
                        client.deleteItem(itemUsed == 8921 || otherItem == 8921 ? 8921 : 11784, 1);
                        client.addItem(itemUsed == 8921 || otherItem == 8921 ? 11864 : 11865, 1);
                    }
                } else client.sendMessage("You need level 70 crafting to combine these items!");
            }
        }
        if ((itemUsed == 1733 || otherItem == 1733) && (itemUsed == 1741 || otherItem == 1741)) {
            client.showInterface(2311);
        }
        if (knife && (itemUsed == 1511 || otherItem == 1511)) {
            client.resetAction();
            client.shafting = true;
        }
        if ((itemUsed == 314 || otherItem == 314)
                && (itemUsed == 52 || otherItem == 52)) {
            client.resetAction();
            client.sendString(client.GetItemName(53), 2799);
            client.sendFrame246(1746, 175, 53);
            client.sendFrame164(4429);
            client.dialogInterface = 53;
            client.fletchOtherAmt = 15;
            client.fletchOtherId1 = itemUsed;
            client.fletchOtherId2 = otherItem;
            client.fletchOtherId3 = 53;
            client.fletchOtherXp = 5;
            client.fletchOtherTime = 600;
        }
        for (int d = 0; d < Constants.darttip.length; d++) {
            if ((itemUsed == Constants.darttip[d] || otherItem == Constants.darttip[d])
                    && (itemUsed == 314 || otherItem == 314)) {
                client.resetAction();
                if (client.getLevel(Skills.FLETCHING) < Constants.darttip_required[d]) {
                    client.sendMessage("You need level " + Constants.darttip_required[d] + " fletching to make " + client.GetItemName(Constants.darts[d]).toLowerCase());
                    return;
                }
                if (!client.playerHasItem(314, 10) || !client.playerHasItem(Constants.darttip[d], 10)) {
                    client.sendMessage("You need ten " + client.GetItemName(314).toLowerCase() + "s and ten " + client.GetItemName(Constants.darttip[d]).toLowerCase() + "s.");
                    return;
                }
                if (!client.playerHasItem(Constants.darts[d], 10)) {
                    client.sendMessage("Your inventory is full!"); //Might send this if all else fails
                    return;
                }
                client.deleteItem(314, 10);
                client.deleteItem(Constants.darttip[d], 10);
                client.addItem(Constants.darts[d], 10);
                client.giveExperience(Constants.darttip_xp[d] * 5, Skills.FLETCHING);
                client.sendMessage("You fletch ten " + client.GetItemName(Constants.darts[d]).toLowerCase() + "s.");
            }
        }
        for (int h = 0; h < Constants.heads.length; h++) {
            if ((itemUsed == Constants.heads[h] || otherItem == Constants.heads[h])
                    && (itemUsed == 53 || otherItem == 53)) {
                client.resetAction();
                if (client.getLevel(Skills.FLETCHING) < Constants.required[h]) {
                    client.sendMessage("Requires level " + Constants.required[h] + " fletching");
                    return;
                }
                client.sendString(client.GetItemName(Constants.arrows[h]), 2799);
                client.sendFrame246(1746, 180, Constants.arrows[h]);
                client.sendFrame164(4429);
                client.dialogInterface = Constants.arrows[h];
                client.fletchOtherAmt = 15;
                client.fletchOtherId1 = itemUsed;
                client.fletchOtherId2 = otherItem;
                client.fletchOtherId3 = Constants.arrows[h];
                client.fletchOtherXp = Constants.xp[h];
                client.fletchOtherTime = 1200;
                break;
            }
        }
        for (int id = 0; id < Constants.logs.length; id++) {
            if ((itemUsed == Constants.logs[id] || otherItem == Constants.logs[id]) && knife) {
                client.resetAction();
                client.dialogInterface = 2459;
                client.fletchLog = id;
                client.sendString("Select a bow", 8879);
                client.sendFrame246(8870, 250, Constants.longbowU[id]);// right picture
                client.sendFrame246(8869, 250, Constants.shortbowU[id]);// left picture
                client.sendString(client.GetItemName(Constants.shortbowU[id]), 8871);
                client.sendString(client.GetItemName(Constants.shortbowU[id]), 8874);
                client.sendString(client.GetItemName(Constants.longbowU[id]), 8878);
                client.sendString(client.GetItemName(Constants.longbowU[id]), 8875);
                client.sendFrame164(8866);
                break;
            }
        }
        for (int id1 = 0; id1 < Constants.shortbow.length; id1++) {
            if ((itemUsed == Constants.shortbowU[id1] || otherItem == Constants.shortbowU[id1])
                    && (itemUsed == 1777 || otherItem == 1777)) {
                client.resetAction();
                client.sendString(client.GetItemName(Constants.shortbow[id1]), 2799);
                client.sendFrame246(1746, 220, Constants.shortbow[id1]);
                client.sendFrame164(4429);
                client.dialogInterface = Constants.shortbow[id1];
                client.fletchOtherAmt = 1;
                client.fletchOtherId1 = itemUsed;
                client.fletchOtherId2 = otherItem;
                client.fletchOtherId3 = Constants.shortbow[id1];
                client.fletchOtherXp = Constants.shortexp[id1];
                client.fletchOtherTime = 1200;
            }
        }
        for (int b2 = 0; b2 < Constants.longbowU.length; b2++) {
            if ((itemUsed == Constants.longbowU[b2] || otherItem == Constants.longbowU[b2])
                    && (itemUsed == 1777 || otherItem == 1777)) {
                client.resetAction();
                client.sendString(client.GetItemName(Constants.longbow[b2]), 2799);
                client.sendFrame246(1746, 220, Constants.longbow[b2]);
                client.sendFrame164(4429);
                client.dialogInterface = Constants.longbow[b2];
                client.fletchOtherAmt = 1;
                client.fletchOtherId1 = itemUsed;
                client.fletchOtherId2 = otherItem;
                client.fletchOtherId3 = Constants.longbow[b2];
                client.fletchOtherXp = Constants.longexp[b2];
                client.fletchOtherTime = 1200;
            }
        }
        for (int id = 0; id < Constants.leathers.length; id++) {
            if ((itemUsed == 1733 || otherItem == 1733)
                    && (itemUsed == Constants.leathers[id] || otherItem == Constants.leathers[id])) {
                craftMenu(id, client);
                cIndex = id;
            }
        }

        if (itemUsed == 1755 || otherItem == 1755) {
            int gem = -1, slot = -1;
            if (itemUsed == 1755)
                gem = otherItem;
            else
                gem = itemUsed;
            for (int i11 = 0; i11 < Utils.uncutGems.length; i11++) {
                if (Utils.uncutGems[i11] == gem) {
                    slot = i11;
                }
            }
            if (slot < 0)
                return;
            if (Utils.gemReq[slot] > client.getLevel(Skills.CRAFTING)) {
                client.sendMessage("You need a crafting level of " + Utils.gemReq[slot] + " to cut this.");
                return;
            }
            client.deleteItem(gem, 1);
            client.addItem(Utils.cutGems[slot], 1);
            client.giveExperience((int) (Utils.gemExp[slot] * 6), Skills.CRAFTING);
            client.sendMessage("You cut the " + client.GetItemName(Utils.cutGems[slot]) + ".");
        }
        if (itemUsed == 1759 || otherItem == 1759) {
            int amulet;
            if (itemUsed == 1759)
                amulet = otherItem;
            else
                amulet = itemUsed;
            int strung = findStrungAmulet(amulet, client);
            if (strung < 0) {
                client.sendMessage("You cannot string this item with wool!");
                return;
            }
            client.deleteItem(amulet, 1);
            client.deleteItem(1759, 1);
            client.addItem(strung, 1);
            client.giveExperience(60, Skills.CRAFTING);
        }

        if (itemUsed == 590 && useWith == 1511 || itemUsed == 1511 && useWith == 590) {
            client.deleteItem(1511, 1);
            client.giveExperience(160, Skills.FIREMAKING);
            client.resetAction();
        } else if (itemUsed == 1521 && useWith == 590 || itemUsed == 590 && useWith == 1521) {
            if (client.getLevel(Skills.FIREMAKING) >= 15) {
                client.deleteItem(1521, 1);
                client.giveExperience(240, Skills.FIREMAKING);
                client.resetAction();
            } else {
                client.sendMessage("You need a firemaking level of 15 to burn oak logs.");
            }
        } else if (itemUsed == 1519 && useWith == 590 || itemUsed == 590 && useWith == 1519) {
            if (client.getLevel(Skills.FIREMAKING) >= 30) {
                client.deleteItem(1519, 1);
                client.giveExperience(360, Skills.FIREMAKING);
                client.resetAction();
            } else {
                client.sendMessage("You need a firemaking of 30 to burn willow logs.");
            }
        } else if (itemUsed == 1517 && useWith == 590 || itemUsed == 590 && useWith == 1517) {
            if (client.getLevel(Skills.FIREMAKING) >= 45) {
                client.deleteItem(1517, 1);
                client.giveExperience(540, Skills.FIREMAKING);
                client.resetAction();
            } else {
                client.sendMessage("You need a firemaking level of 45 to burn maple logs.");
            }
        } else if (itemUsed == 1515 && useWith == 590 || itemUsed == 590 && useWith == 1515) {
            if (client.getLevel(Skills.FIREMAKING) >= 60) {
                client.deleteItem(1515, 1);
                client.giveExperience(812, Skills.FIREMAKING);
                client.resetAction();
            } else {
                client.sendMessage("You need a firemaking of 60 to burn yew logs.");
            }
        } else if (itemUsed == 1513 && useWith == 590 || itemUsed == 590 && useWith == 1513) {
            if (client.getLevel(Skills.FIREMAKING) >= 75) {
                client.deleteItem(1513, 1);
                client.giveExperience(1216, Skills.FIREMAKING);
                client.resetAction();
            } else {
                client.sendMessage("You need a firemaking level of 75 to burn magic logs.");
            }
        }
    }
}