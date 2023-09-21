package net.dodian.uber.game.model.player.skills.smithing;

import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.packets.outgoing.SendSideTab;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Range;
import net.dodian.utilities.Utils;

import java.util.ArrayList;

public class Smelting {

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
                    c.send(new SendMessage("You need a tin and copper to do this."));
                break;
            case 2351: // iron ore
                if (c.getLevel(Skills.SMITHING) < 15) {
                    c.send(new SendMessage("You need level 15 smithing to do this."));
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
                        c.send(new SendMessage("You fail to refine the iron."));
                    }
                } else
                    c.send(new SendMessage("You need a iron ore to do this."));
                break;
            case 2353:
                if (c.getLevel(Skills.SMITHING) < 30) {
                    c.send(new SendMessage("You need level 30 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                    smelt_barId = 2353;
                    removed.add(440);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a iron ore and 2 coal to do this."));
                break;
            case 2357:
                if (c.getLevel(Skills.SMITHING) < 40) {
                    c.send(new SendMessage("You need level 40 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(444, 1)) {
                    smelt_barId = 2357;
                    removed.add(444);
                } else
                    c.send(new SendMessage("You need a gold ore to do this."));
                break;
            case 2359:
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.send(new SendMessage("You need level 55 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smelt_barId = 2359;
                    removed.add(447);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a mithril ore and 3 coal to do this."));
                break;
            case 2361:
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.send(new SendMessage("You need level 70 smithing to do this."));
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
                    c.send(new SendMessage("You need a adamantite ore and 4 coal to do this."));
                break;
            case 2363:
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.send(new SendMessage("You need level 85 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smelt_barId = 2363;
                    removed.add(451);
                    for (int i = 0; i < 6; i++)
                        removed.add(453);
                } else
                    c.send(new SendMessage("You need a runite ore and 6 coal to do this."));
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
                    c.send(new SendMessage("You need a tin and copper to do this."));
                break;
            case 440: // iron ore
                if (c.playerHasItem(440) && !c.playerHasItem(453, 2)) {
                    if (c.getLevel(Skills.SMITHING) < 15) {
                        c.send(new SendMessage("You need level 15 smithing to do this."));
                        break;
                    }
                    smelt_barId = 2351;
                    removed.add(440);
                } else if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                    if (c.getLevel(Skills.SMITHING) < 30) {
                        c.send(new SendMessage("You need level 30 smithing to do this."));
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
                    c.send(new SendMessage("You need level 40 smithing to do this."));
                    break;
                }
                smelt_barId = 2357;
                removed.add(444);
                break;
            case 447:
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.send(new SendMessage("You need level 55 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smelt_barId = 2359;
                    removed.add(447);
                    removed.add(453);
                    removed.add(453);
                    removed.add(453);
                } else
                    c.send(new SendMessage("You need a mithril ore and 3 coal to do this."));
                break;
            case 449:
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.send(new SendMessage("You need level 70 smithing to do this."));
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
                    c.send(new SendMessage("You need a adamantite ore and 4 coal to do this."));
                break;
            case 451:
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.send(new SendMessage("You need level 85 smithing to do this."));
                    break;
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smelt_barId = 2363;
                    removed.add(451);
                    for (int i = 0; i < 6; i++)
                        removed.add(453);
                } else
                    c.send(new SendMessage("You need a runite ore and 6 coal to do this."));
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
}