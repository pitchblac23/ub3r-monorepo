package net.dodian.uber.game.model.player.skills.fishing;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import net.dodian.utilities.Utils


class Fishing {

    companion object {
        @JvmStatic
        fun startFishing(p: Client, clickObject: Int, clickOption: Int) {
            var valid = false;

            for (i in 0 until Utils.fishSpots.size) {
                if (Utils.fishSpots[i] == clickObject) {
                    if (clickOption == 1 && (i == 0 || i == 2 || i == 4 || i == 6)) {
                        valid = true;
                        p.fishIndex = i;
                        break;
                    } else if (clickOption == 2 && (i == 1 || i == 3 || i == 5 || i == 7)) {
                        valid = true;
                        p.fishIndex = i;
                        break;
                    }
                }
            }
            if (!valid) {
                p.resetAction(true);
                return;
            }
            if (p.getLevel(Skills.FISHING) < Utils.fishReq[p.fishIndex]) {
                p.send(SendMessage("You need " + Utils.fishReq[p.fishIndex] + " fishing to fish here"));
                p.resetAction(true);
                return;
            }
            if (!p.playerHasItem(Utils.fishTool[p.fishIndex])) {
                p.send(SendMessage("You need a " + p.GetItemName(Utils.fishTool[p.fishIndex]) + " to fish here"));
                p.resetAction(true);
                return;
            }
            p.lastAction = System.currentTimeMillis() + Utils.fishTime[p.fishIndex];
            p.requestAnim(Utils.fishAnim[p.fishIndex], 0);
            p.fishing = true;
        }

        @JvmStatic
        fun Fish(p: Client) {

            p.lastAction = System.currentTimeMillis();
            if (!p.playerHasItem(-1)) {
                p.send(SendMessage("Not enough inventory space!"));
                p.resetAction(true);
                return;
            }
            if (!p.playerHasItem(314) && p.fishIndex == 1) {
                p.send(SendMessage("You do not have any feathers."));
                p.resetAction(true);
                return;
            }
            if (p.fishIndex == 0) {
                val random = Misc.random(6)
                if (p.getLevel(Skills.FISHING) >= 15 && random < 3) {
                    p.addItem(321, 1);
                    p.giveExperience(Utils.fishExp[p.fishIndex] + 100, Skills.FISHING);
                    p.send(SendMessage("You catch some raw anchovies."));
                } else {
                    p.giveExperience(Utils.fishExp[p.fishIndex], Skills.FISHING);
                    p.addItem(Utils.fishId[p.fishIndex], 1);
                    p.send(SendMessage("You catch some raw shrimp."));
                }
            } else if (p.fishIndex == 1) {
                p.deleteItem(314, 1);
                val random = Misc.random(6)
                if (p.getLevel(Skills.FISHING) >= 30 && random < 3) {
                    p.addItem(331, 1);
                    p.giveExperience(Utils.fishExp[p.fishIndex] + 100, Skills.FISHING);
                    p.send(SendMessage("You catch some raw salmon."));
                } else {
                    p.giveExperience(Utils.fishExp[p.fishIndex], Skills.FISHING);
                    p.addItem(Utils.fishId[p.fishIndex], 1);
                    p.send(SendMessage("You catch some raw trout."));
                }
            } else {
                p.giveExperience(Utils.fishExp[p.fishIndex], Skills.FISHING);
                p.addItem(Utils.fishId[p.fishIndex], 1);
                p.send(SendMessage("You catch some " + p.GetItemName(Utils.fishId[p.fishIndex]).toLowerCase() + "."));
            }
            p.requestAnim(Utils.fishAnim[p.fishIndex], 0);
            p.triggerRandom(Utils.fishExp[p.fishIndex]);
        }
    }
}