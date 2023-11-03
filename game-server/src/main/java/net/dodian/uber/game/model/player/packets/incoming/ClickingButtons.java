package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.UpdateFlag;
import net.dodian.uber.game.model.combat.impl.CombatStyleHandler;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.Emotes;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.item.Ground;
import net.dodian.uber.game.model.item.GroundItem;
import net.dodian.uber.game.model.player.content.Skillcape;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import net.dodian.uber.game.model.player.quests.QuestSend;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.uber.game.model.player.skills.prayer.Prayers;
import net.dodian.uber.game.party.Balloons;
import net.dodian.utilities.Utils;

import static net.dodian.uber.game.model.player.dialogue.DialogueKt.triggerChat;
import static net.dodian.uber.game.model.player.skills.SkillGuidesKt.*;
import static net.dodian.uber.game.model.player.skills.Skills.*;
import static net.dodian.uber.game.model.player.skills.crafting.CraftingKt.*;
import static net.dodian.uber.game.model.player.skills.crafting.TanningKt.*;
import static net.dodian.uber.game.model.player.skills.smithing.SmeltingKt.*;
import static net.dodian.utilities.DotEnvKt.getServerDebugMode;

public class ClickingButtons implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int actionButton = Utils.HexToInt(client.getInputStream().buffer, 0, packetSize);
        if (getServerDebugMode()) { client.println("button= " + actionButton); }
        if (System.currentTimeMillis() - client.lastButton < 600 || !client.validClient) { //To prevent some shiez!
            client.lastButton = System.currentTimeMillis();
            return;
        }
        if(!(actionButton >= 9157 && actionButton <= 9194))
            client.actionButtonId = actionButton;
        client.resetAction(false);
        CombatStyleHandler.setWeaponHandler(client, actionButton);
        if (client.duelButton(actionButton)) {
            return;
        }
        Prayers.Prayer prayer = Prayers.Prayer.forButton(actionButton);
        if (prayer != null) {
            client.getPrayerManager().togglePrayer(prayer);
            return;
        }
        if(QuestSend.questMenu(client)) {
            return;
        }
        if(client.refundSlot != -1) { //Refund code!
            int size = client.rewardList.size();
            int checkSlot = 1;
            int position = size - client.refundSlot;
            if(actionButton == 9158 || actionButton == 9168 || actionButton == 9179 || actionButton == 9191)
                checkSlot = 2;
            else if(actionButton == 9169 || actionButton == 9180 || actionButton == 9192)
                checkSlot = 3;
            else if(actionButton == 9181 || actionButton == 9193)
                checkSlot = 4;
            else if(actionButton == 9194)
                checkSlot = 5;
            if(client.refundSlot == 0 && ((size > 3 && checkSlot == 5) || (size == 3 && checkSlot == 4) || (size == 1 && checkSlot == 2) || (size == 2 && checkSlot == 3))) { //Close!
                client.refundSlot = -1;
                client.send(new RemoveInterfaces());
            } else if(((client.refundSlot == 0 && position > 3) || position > 3) && checkSlot == 4)
                client.refundSlot += 3;
            else if(client.refundSlot != 0 && ((position <= 3 && checkSlot == position + 1) || (position > 3 && checkSlot == 5)))
                client.refundSlot -= 3;
            else client.reclaim(checkSlot);
            if(!client.rewardList.isEmpty())
                client.setRefundOptions();
            return;
        }
        Emotes.doEmote(actionButton, client);
        switch (actionButton) {
            case 58073:
                client.sendMessage("Visit the Dodian.net UserCP and click edit pin to remove your pin");
                break;
            case 151:
                client.NpcDialogue = 27;
                client.NpcDialogueSend = false;
                break;
            case 150:
                client.NpcDialogue = 26;
                client.NpcDialogueSend = false;
                break;
            case 8198:
                Balloons.acceptItems(client);
                break;
            case 83093:
                client.sendFrame248(21172, 3213);
                break;
            case 83051:
            case 9118://Might not need?
            case 19022:
            case 96078:
                client.send(new RemoveInterfaces());
                break;
            case 24136:
                client.yellOn = true;
                client.sendMessage("You enabled the boss yell messages.");
                break;
            case 24137:
                client.yellOn = false;
                client.sendMessage("You disabled the boss yell messages.");
                break;
            case 89223: // TODO: Check what this button do!
                for (int i = 0; i < client.playerItems.length; i++) {
                    client.bankItem(client.playerItems[i], i, client.playerItemsN[i]);
                }
		    break;
            case 3056: //Small tree
            case 3057: //Big Tree
            case 3058: //Mountain
            case 3059: //Castle
            case 3060: //Tent
            case 48054: //totem!
                int pos = client.skillX == 2772 && client.skillY == 3235 ? 5:
                client.skillX == 2864 && client.skillY == 2971 ? 4:
                client.skillX == 3511 && client.skillY == 3505 ? 2: 0;
                client.travelTrigger(pos);
                break;

            case 84237: //Home teleport aka Yanille
            case 75010:
                client.triggerTele(2606, 3102, 0, false);
                break;

            case 50235: //Seers
                client.triggerTele(2723, 3485, 0, false);
                break;
            case 50245: //Ardougne
                client.triggerTele(2662, 3309, 0, false);
                break;
            case 50253: // Catherby
                client.triggerTele(2804, 3434, 0, false);
                break;
            case 51005: //Legends guild
                client.triggerTele(2728, 3346, 0, false);
                break;
            case 51013: //Taverly
                client.triggerTele(2895, 3457, 0, false);
                break;
            case 51023: //Fishing guild
                client.triggerTele(2597, 3409, 0, true);
                break;
            case 51031: //Gnome village
                client.triggerTele(2472, 3438, 0, false);
                break;
            case 51039: //Empty teleport
                client.triggerTele(3087, 3492, 0, false);
                break;
            case 74212:

            case 49047: // old magic on
            case 49046: // old magic off

                if (client.ancients == 1) {
                    client.setSidebarInterface(6, 1151); // magic tab (ancient = 12855);
                    client.ancients = 0;
                    client.sendMessage("You feel a strange drain upon your power...");
                } else {
                    client.setSidebarInterface(6, 12855); // magic tab (ancient = 12855);
                    client.ancients = 1;
                    client.sendMessage("You feel a strange power fill your mind...");
                }
                break;
            case 26076:
                // frame36(6575, 1);
                break;
            case 53245:
            case 53246:
            case 53247:
            case 53248:
            case 53249:
            case 53250:
            case 53251:
            case 53252:
            case 53253:
            case 53254:
            case 53255:
                client.duelButton2(client.actionButtonId - 53245);
                break;
            case 54074:
                Server.slots.playSlots(client, -1);
                break;
            case 25120:
                if (System.currentTimeMillis() - client.lastButton < 1000) {
                    client.lastButton = System.currentTimeMillis();
                    break;
                } else {
                    client.lastButton = System.currentTimeMillis();
                }
                Client dw = client.getClient(client.duel_with);
                /*
                 * Danno: Sometimes dcs a player. So we break if other player is null.
                 */
                if (dw == null)
                    break;
                client.canOffer = false;
                if (!client.validClient(client.duel_with)) {
                    client.declineDuel();
                }
                if (client.duelConfirmed2) {
                    break;
                }
                client.duelConfirmed2 = true;
                if (dw.duelConfirmed2) {
                    client.removeEquipment();
                    dw.removeEquipment();
                    client.startDuel();
                    dw.startDuel();
                } else {
                    client.sendString("Waiting for other player...", 6571);
                    dw.sendString("Other player has accepted", 6571);
                }
                break;
            case 95197://Bronze
            case 95196:
            case 95195:
            case 95194:

            case 95201://Iron
            case 95200:
            case 95199:
            case 95198:

            case 95205://Silver
            case 95204:
            case 95203:
            case 95202:

            case 95209://Steel
            case 95208:
            case 95207:
            case 95206:

            case 95213://Gold
            case 95212:
            case 95211:
            case 95210:

            case 95217://Mithril
            case 95216:
            case 95215:
            case 95214:

            case 95221://Adamant
            case 95220:
            case 95219:
            case 95218:

            case 95225://Rune
            case 95224:
            case 95223:
            case 95222:
                startSmelt(client.actionButtonId, client);
                break;

            case 34185:
            case 34184: // vamps
            case 34183:
            case 34182:
            case 34189: // chaps
            case 34188:
            case 34187:
            case 34186:
            case 34193:
            case 34192:
            case 34191:
            case 34190:
                startHideCraft(client.actionButtonId, client);
                break;
            case 33187: // armor
            case 33186:
            case 33185:

            case 33190: // gloves
            case 33189:
            case 33188:

            case 33193: // boots
            case 33192:
            case 33191:

            case 33196: // vamps
            case 33195:
            case 33194:

            case 33199: // chaps
            case 33198:
            case 33197:

            case 33202: // coif
            case 33201:
            case 33200:

            case 33205:// cowl
            case 33204:
            case 33203:
                startCraft(client.actionButtonId, client);
            break;
            case 57225:
                startTan(1, 0, client);
            break;
            case 57217:
                startTan(5, 0, client);
                break;
            case 57201:
            case 57209:
                startTan(27, 0, client);
            break;
            case 57229: //Hard leather!
                startTan(1, 1, client);
            break;
            case 57221:
                startTan(5, 1, client);
            break;
            case 57205:
            case 57213:
                startTan(27, 1, client);
            break;
            case 57227:
                startTan(1, 2, client);
                break;
            case 57219:
                startTan(5, 2, client);
                break;
            case 57211:
            case 57203:
                startTan(27, 2, client);
                break;
            case 57228:
                startTan(1, 3, client);
                break;
            case 57220:
                startTan(5, 3, client);
                break;
            case 57212:
            case 57204:
                startTan(27, 3, client);
                break;
            case 57231:
                startTan(1, 4, client);
                break;
            case 57223:
                startTan(5, 4, client);
                break;
            case 57215:
            case 57207:
                startTan(27, 4, client);
                break;
            case 57232:
                startTan(1, 5, client);
                break;
            case 57224:
                startTan(5, 5, client);
                break;
            case 57216:
            case 57208:
                startTan(27, 5, client);
                break;
            case 10239: //make stuff 1
                client.fletching.fletchOther(client, 1);
                break;
            case 10238: //make stuff 5
                client.fletching.fletchOther(client, 5);
                break;
            case 6212: //make stuff 10 (x)
                client.fletching.fletchOther(client, 10);
                break;
            case 6211: //make stuff 28 (all)
                client.fletching.fletchOther(client, 28);
                break;
            case 34170:
                client.fletching.fletchBow(client, true, 1);
                break;
            case 34169:
                client.fletching.fletchBow(client, true, 5);
                break;
            case 34168:
                client.fletching.fletchBow(client, true, 10);
                break;
            case 34167:
                client.fletching.fletchBow(client, true, 27);
                break;
            case 34174: // 1
                client.fletching.fletchBow(client, false, 1);
                break;
            case 34173: // 5
                client.fletching.fletchBow(client, false, 5);
                break;
            case 34172: // 10
                client.fletching.fletchBow(client, false, 10);
                break;
            case 34171:
                client.fletching.fletchBow(client, false, 27);
                break;
            case 10252:
            case 11000:
            case 10253:
            case 11001:
            case 10254:
            case 10255:
            case 11002:
            case 11011:
            case 11013:
            case 11014:
            case 11010:
            case 11012:
            case 11006:
            case 11009:
            case 11008:
            case 11004:
            case 11003:
            case 11005:
            case 47002:
            case 54090:
            case 11007:
                if (client.genie) {
                    int[] skillTrain = {
                            10252, 11000, 10253, 11001, 10254, 11002, 10255, 11011,
                            11013, 11014, 11010, 11012, 11006, 11009, 11008, 11004,
                            11003, 11005, 47002, 54090, 11007
                    };
                    client.send(new RemoveInterfaces());
                    client.genie = false;
                    if (client.inDuel || client.duelFight || client.IsBanking || client.checkBankInterface || !client.playerHasItem(2528)) //To prevent stuff!
                        break;
                    for (int i = 0; i < skillTrain.length; i++) {
                        Skills trainedSkill = Skills.getSkill(i);
                        if (trainedSkill != null && skillTrain[i] == client.actionButtonId && client.actionButtonId != 54090) {
                            client.deleteItem(2528, 1);
                            int level = Skills.getLevelForExperience(client.getExperience(trainedSkill));
                            int experience = 250 * level;
                            client.giveExperience(experience, trainedSkill);
                            client.sendMessage("You rub the lamp and gained " + experience + " experience in " + trainedSkill.getName() + ".");
                        }
                    }
                } else if (client.randomed && client.actionButtonId == client.statId[client.random_skill]) {
                    client.randomed = false;
                    client.send(new RemoveInterfaces());
                    if (!client.addItem(2528, 1)) {
                        GroundItem item = new GroundItem(client.getPosition(), 2528, 1, client.getSlot(), -1);
                        Ground.items.add(item);
                        client.sendMessage("You dropped the lamp on the floor!");
                    }
                }
                break;
            case 1097: //?
            case 1094: //?
            case 1093: //Autocast spellbook
                client.setSidebarInterface(0, 1689);
                client.FightType = 0;
                client.frame87(43, 4);
                client.frame87(108, 1);
                client.setCombatStyle(CombatStyleHandler.CombatStyles.ATTACK_WITH_SPELL);
                break;

            case 94047:
                client.setSidebarInterface(0, 1689);
                client.FightType = 1;
                client.frame87(43, 4);
                client.frame87(108, 2);
                client.setCombatStyle(CombatStyleHandler.CombatStyles.ATTACK_WITH_SPELL_DEFENCE);
                break;
            case 94051:
                client.sendMessage("TODO");
                break;
            case 51133:
            case 51185:
            case 51091:
            case 24018:
            case 51159:
            case 51211:
            case 51111:
            case 51069:
            case 51146:
            case 51198:
            case 51102:
            case 51058:
            case 51172:
            case 51224:
            case 51122:
            case 51080:
                for (int index = 0; index < client.ancientButton.length; index++) {
                    if (client.actionButtonId == client.ancientButton[index]) {
                        client.autocast_spellIndex = index;
                        CombatStyleHandler.setWeaponHandler(client, -1);
                        //client.debug("autocast_spellIndex=" + client.autocast_spellIndex);
                        break;
                    }
                }
                break;
            case 24017:
                CombatStyleHandler.setWeaponHandler(client, -1);
                client.frame87(108, 0);
                break;

            case 2171: // Retribution
                break;

            case 14067: //Apperance accepted!
                client.send(new RemoveInterfaces());
                client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
                break;

            case 152:
                client.buttonOnRun = false;
                break;
            case 153:
                client.buttonOnRun = true;
                break;

            case 130: // close interface
                client.println("Closing Interface");
                break;

            case 1177: // Gmaul!
            case 9125: // Accurate
            case 22228: // punch (unarmed)
            case 48010: // flick (whip)
            case 21200: // spike (pickaxe)
            case 1080: // bash (staff)
            case 6168: // chop (axe)
            case 6236: // accurate (long bow)
            case 17102: // accurate (darts)
            case 8234: // stab (dagger)
                client.FightType = 0;
                client.frame87(43, 0);
                client.frame87(108, 0);
                client.autocast_spellIndex = -1;
                client.setCombatStyle(CombatStyleHandler.CombatStyles.ACCURATE_MELEE);
                break;

            case 1175: // Gmaul!
            case 9126: // Defensive
            case 48008: // deflect (whip)
            case 22229: // block (unarmed)
            case 21201: // block (pickaxe)
            case 1078: // focus - block (staff)
            case 6169: // block (axe)
            case 33019: // fend (hally)
            case 18078: // block (spear)
            case 8235: // block (dagger)
                client.FightType = 1;
                client.frame87(43, 2);
                client.frame87(108, 0);
                client.autocast_spellIndex = -1;
                client.setCombatStyle(CombatStyleHandler.CombatStyles.DEFENSIVE_MELEE);
                break;

            case 9127: // Controlled
            case 48009: // lash (whip)
            case 33018: // jab (hally)
            case 6234: // longrange (long bow)
            case 18077: // lunge (spear)
            case 18080: // swipe (spear)
            case 18079: // pound (spear)
            case 17100: // longrange (darts)
                client.FightType = 3;
                client.frame87(43, 3);
                client.frame87(108, 0);
                client.autocast_spellIndex = -1;
                // client.SkillID = 3;
                client.setCombatStyle(CombatStyleHandler.CombatStyles.CONTROLLED_MELEE);
                break;

            case 1176: // Gmaul!
            case 9128: // Aggressive
            case 22230: // kick (unarmed)
            case 21203: // impale (pickaxe)
            case 21202: // smash (pickaxe)
            case 1079: // pound (staff)
            case 6171: // hack (axe)
            case 6170: // smash (axe)
            case 33020: // swipe (hally)
            case 6235: // rapid (long bow)
            case 17101: // repid (darts)
            case 8237: // lunge (dagger)
            case 8236: // slash (dagger)
                client.FightType = 2;
                client.frame87(43, 1);
                client.frame87(108, 0);
                client.autocast_spellIndex = -1;
                client.setCombatStyle(CombatStyleHandler.CombatStyles.AGGRESSIVE_MELEE);
                break;

            case 9154: // Log out
                if (System.currentTimeMillis() < client.walkBlock) {
                    client.sendMessage("You are unable to logout right now.");
                    break;
                }
                if (client.isInCombat()) {
                    client.sendMessage("You must wait until you are out of combat before logging out!");
                    break;
                }
                if (System.currentTimeMillis() - client.getLastCombat() <= 7000 && client.inWildy()) {
                    client.sendMessage("You must wait 7 seconds after npc combat to logout.");
                    break;
                }
                if (System.currentTimeMillis() - client.lastPlayerCombat <= 30000 && client.inWildy()) {
                    client.sendMessage("You must wait 30 seconds after combat in the wilderness to logout.");
                    client.sendMessage("If you X out or disconnect you will stay online for up to a minute");
                    break;
                }
                // if(currentHealth > 0)
                client.logout();
                break;

            case 21010:
                if(client.IsBanking) { client.takeAsNote = true; }
                break;
            case 21011:
                if (client.IsBanking) { client.takeAsNote = false; }
                break;
            case 95075:
                if(client.IsBanking) {
                    if (client.freeSlots() < 28) {
                        for (int i = 0; i < client.playerItems.length; i++)
                            if (client.playerItems[i] > 0)
                                client.bankItem(client.playerItems[i] - 1, i, client.playerItemsN[i]);
                        client.sendMessage("You banked all your items.");
                    } else
                        client.sendMessage("You do not have anything that can be banked.");
                }
                break;

            case 13092:
                try {
                    Client other = client.getClient(client.trade_reqId);
                    if (other == null || !client.validClient(client.trade_reqId) || System.currentTimeMillis() - client.lastButton < 600 || !client.inTrade) {
                        break;
                    }
                    client.lastButton = System.currentTimeMillis();
                    if (client.inTrade && !client.tradeConfirmed) {
                        client.tradeConfirmed = true;
                        if (other != null && other.tradeConfirmed) {
                            if (!other.hasTradeSpace() || !client.hasTradeSpace()) {
                                client.sendMessage(client.failer);
                                other.sendMessage(client.failer);
                                client.declineTrade();
                                return;
                            }
                            client.confirmScreen();
                            other.confirmScreen();
                            break;
                        }
                        client.sendString("Waiting for other player...", 3431);
                        if (other != null && client.validClient(client.trade_reqId)) {
                            other.sendString("Other player has accepted", 3431);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 13218:
                try {
                    Client other = client.getClient(client.trade_reqId);
                    if (other == null || !client.validClient(client.trade_reqId) || System.currentTimeMillis() - client.lastButton < 600 || !client.inTrade) {
                        break;
                    }
                    client.lastButton = System.currentTimeMillis();
                    if (client.inTrade && client.tradeConfirmed && other.tradeConfirmed && !client.tradeConfirmed2) {
                        client.tradeConfirmed2 = true;
                        if (other.tradeConfirmed2) {
                            client.giveItems();
                            other.giveItems();
                            break;
                        }
                        other.sendString("Other player has accepted.", 3535);
                        client.sendString("Waiting for other player...", 3535);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 9157:
            case 9167:
            case 9178:
            case 9190:
                triggerChat(1, client);
                break;
            case 9158:
            case 9168:
            case 9179:
            case 9191:
                triggerChat(2, client);
                break;
            case 9169:
            case 9180:
            case 9192:
                triggerChat(3, client);
                break;
            case 9181:
            case 9193:
                triggerChat(4, client);
                break;
            case 9194:
                triggerChat(5, client);
                break;

            case 7212:
                client.setSidebarInterface(0, 328);
                break;
            case 26018:
                if (!client.inDuel || !client.validClient(client.duel_with)) {
                    break;
                }
                Client o = client.getClient(client.duel_with);
                boolean sendMsgToOther = client.getMaxHealth() - client.getCurrentHealth() == 0 && o.getMaxHealth() - o.getCurrentHealth() != 0;
                if (o.getMaxHealth() - o.getCurrentHealth() != 0 || client.getMaxHealth() - client.getCurrentHealth() != 0) {
                    client.sendMessage(sendMsgToOther ? "Your opponent is low on health!" : "You are low on health, so please heal up!");
                    if(sendMsgToOther)
                        o.sendMessage("You are low on health, so please heal up!");
                    break;
                }
                if (System.currentTimeMillis() - client.lastButton < 1000) {
                    client.lastButton = System.currentTimeMillis();
                    break;
                } else {
                    client.lastButton = System.currentTimeMillis();
                }
                if (client.duelConfirmed) {
                    break;
                }
                client.duelConfirmed = true;
                client.canOffer = false;
                if (o.duelConfirmed) {
                    /*
                     * Danno: Fix; stop a duel with all combat styles disabled.
                     */
                    if (client.duelRule[0] && client.duelRule[1] && client.duelRule[2]) {
                        client.declineDuel();
                        client.sendMessage("At least one combat style must be enabled!");
                        o.sendMessage("At least one combat style must be enabled!");
                        return;
                    }
                    if (!client.hasEnoughSpace() || !o.hasEnoughSpace()) {
                        client.sendMessage(client.failer);
                        o.sendMessage(client.failer);
                        client.declineDuel();
                        return;
                    }
                    client.canOffer = false;
                    o.canOffer = false;
                    client.confirmDuel();
                    o.confirmDuel();
                } else {
                    client.sendString("Waiting for other player...", 6684);
                    o.sendString("Other player has accepted.", 6684);
                }

                break;

            case 94167:
                try {
                    showSkillMenu(ATTACK.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94168:
                try {
                    showSkillMenu(HITPOINTS.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94169:
                try {
                    showSkillMenu(MINING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94170:
                try {
                    showSkillMenu(STRENGTH.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94171:
                try {
                    showSkillMenu(AGILITY.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94173:
                try {
                    showSkillMenu(DEFENCE.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94176:
                try {
                    showSkillMenu(RANGED.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94177:
                try {
                    showSkillMenu(THIEVING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94174:
                try {
                    showSkillMenu(HERBLORE.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94180:
                try {
                    showSkillMenu(CRAFTING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94172:
                try {
                    showSkillMenu(SMITHING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94184:
                try {
                    showSkillMenu(WOODCUTTING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94182:
                try {
                    showSkillMenu(MAGIC.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94181:
                try {
                    showSkillMenu(FIREMAKING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94178:
                try {
                    showSkillMenu(COOKING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 95053:
                try {
                    showSkillMenu(RUNECRAFTING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94183:
                try {
                    showSkillMenu(FLETCHING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 94175:
                try {
                    showSkillMenu(FISHING.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 95061:
                try {
                    showSkillMenu(SLAYER.getId(), 0, client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 95068:
            case 94179:
                client.sendMessage("Coming soon!");
                break;

            case 88060: //Idea
                client.requestAnim(4276, 0);
                client.gfx0(712);
                break;
            case 88061: //Stomp
                client.requestAnim(4278, 0);
                client.gfx0(713);
                break;
            case 88062: //Flap
                client.requestAnim(4280, 0);
                break;
            case 88063: //Slap head
                client.requestAnim(4275, 0);
                break;
            case 59062: //Scared
                client.requestAnim(2836, 0);
                break;
            case 72254: //Bunny hop
                client.requestAnim(6111, 0);
                break;
            case 72033: //Zombie dance
                client.requestAnim(3543, 0);
                break;
            case 72032: //Zombie walk
                client.requestAnim(3544, 0);
            break;
            case 74108:
                Skillcape skillcape = Skillcape.getSkillCape(client.getEquipment()[Equipment.Slot.CAPE.getId()]);
                if (skillcape != null) {
                    client.requestAnim(skillcape.getEmote(), 0);
                    client.gfx0(skillcape.getGfx());
                } else if (client.getEquipment()[Equipment.Slot.CAPE.getId()] == 9813) { //Questpoint cape
                    client.requestAnim(4945, 0);
                    client.gfx0(816);
                } else if (client.getEquipment()[Equipment.Slot.CAPE.getId()] == 13280) { //Max cape
                    skillcape = Skillcape.getRandomCape();
                    client.requestAnim(skillcape.getEmote(), 0);
                    client.gfx0(skillcape.getGfx());
                } else {
                    client.sendMessage("You need to be wearing a skillcape to do that!");
                }
                break;
            case 32195:
            case 32196:
                if (client.playerHasItem(2996)) {
                    client.giveExperience(700, Skills.AGILITY);
                    client.sendMessage("You exchange your agility tickets");
                    client.deleteItem(2996, 1);
                } else {
                    client.sendMessage("You have no agility tickets!");
                }
                break;
            case 32197:
            case 32203:
                if (client.playerHasItem(2996, 10)) {
                    client.giveExperience(10 * 700, Skills.AGILITY);
                    client.sendMessage("You exchange your agility tickets.");
                    client.deleteItem(2996, 10);
                } else {
                    client.sendMessage("You need 10 agility tickets.");
                }
                break;
            case 32204:
            case 32198:
                if (client.playerHasItem(2996, 25)) {
                    client.giveExperience(25 * 700, Skills.AGILITY);
                    client.sendMessage("You exchange your agility tickets.");
                    client.deleteItem(2996, 25);
                } else {
                    client.sendMessage("You need 25 agility tickets.");
                }
                break;
            case 32193:
            case 32189:
                if (client.playerHasItem(2996, 10)) {
                    client.sendMessage("You exchange your agility tickets.");
                    client.addItem(11738, 1);
                    client.deleteItem(2996, 10);
                } else {
                    client.sendMessage("You need 10 agility tickets.");
                }
                break;

            default:
                // System.out.println("Player stands in: X="+absX+" Y="+absY);
                if (client.playerRights > 1) {
                    client.println("Case 185: Action Button: " + client.actionButtonId);
                }
                break;
        }
    }
}