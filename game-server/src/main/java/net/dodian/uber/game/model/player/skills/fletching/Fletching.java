package net.dodian.uber.game.model.player.skills.fletching;

import net.dodian.uber.game.Constants;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.Skills;

public class Fletching {

    public void fletchOther(Client player, int amount) {
        player.send(new RemoveInterfaces());
        player.fletchOtherAmount = amount;
        player.fletchingOther = true;
    }

    public void fletchOther(Client player) {
        if (player.fletchOtherAmount < 1) {
            player.resetAction();
            return;
        }
        player.fletchOtherAmount--;
        player.IsBanking = false;
        if (player.fletchOtherAmt == 1)
            player.requestAnim(6683, 0);
        if (player.playerHasItem(player.fletchOtherId1, player.fletchOtherAmt) && player.playerHasItem(player.fletchOtherId2, player.fletchOtherAmt)) {
            player.deleteItem(player.fletchOtherId1, player.fletchOtherAmt);
            player.deleteItem(player.fletchOtherId2, player.fletchOtherAmt);
            player.addItem(player.fletchOtherId3, player.fletchOtherAmt);
            player.giveExperience(player.fletchOtherXp * player.fletchOtherAmt, Skills.FLETCHING);
            player.triggerRandom(player.fletchOtherXp * player.fletchOtherAmt);
        } else {
            if (player.fletchOtherAmt == 15)
                player.send(new SendMessage("You need at least 15 " + player.GetItemName(player.fletchOtherId1).toLowerCase() + " and " + player.GetItemName(player.fletchOtherId2).toLowerCase() + ""));
            player.resetAction();
        }
    }

    public void fletchBow(Client player, boolean shortBow, int amount) {
        player.send(new RemoveInterfaces());
        if (shortBow) {
            if (player.getLevel(Skills.FLETCHING) < Constants.shortreq[player.fletchLog]) {
                player.send(new SendMessage("Requires fletching " + Constants.shortreq[player.fletchLog] + "!"));
                player.resetAction();
                return;
            }
            player.fletchId = Constants.shortbows[player.fletchLog];
            player.fletchExp = Constants.shortexp[player.fletchLog];
        } else {
            if (player.getLevel(Skills.FLETCHING) < Constants.longreq[player.fletchLog]) {
                player.send(new SendMessage("Requires fletching " + Constants.longreq[player.fletchLog] + "!"));
                player.resetAction();
                return;
            }
            player.fletchId = Constants.longbows[player.fletchLog];
            player.fletchExp = Constants.longexp[player.fletchLog];
        }
        player.fletchings = true;
        player.fletchAmount = amount;
    }

    public void fletchBow(Client player) {
        if (player.fletchAmount < 1) {
            player.resetAction();
            return;
        }
        player.fletchAmount--;
        player.send(new RemoveInterfaces());
        player.IsBanking = false;
        player.requestAnim(4433, 0);
        if (player.playerHasItem(Constants.logs[player.fletchLog])) {
            player.deleteItem(Constants.logs[player.fletchLog], 1);
            player.addItem(player.fletchId, 1);
            player.giveExperience(player.fletchExp, Skills.FLETCHING);
            player.triggerRandom(player.fletchExp);
        } else {
            player.resetAction();
        }
    }

    public static void shaft(Client c) {
        if (c.IsCutting || c.isFiremaking)
            c.resetAction();
        c.send(new RemoveInterfaces());
        if (c.playerHasItem(1511)) {
            c.deleteItem(1511, 1);
            c.addItem(52, 15);
            c.requestAnim(1248, 0);
            c.giveExperience(50, Skills.FLETCHING);
            c.triggerRandom(50);
        } else {
            c.resetAction();
        }
    }

    public static void pickHandle(Client c) {
        if (c.IsCutting || c.isFiremaking)
            c.resetAction();
        c.send(new RemoveInterfaces());
        if (c.playerHasItem(1517)) {
            c.deleteItem(1517, 1);
            c.addItem(466, 1);
            c.requestAnim(1248, 0);
            c.giveExperience(50, Skills.FLETCHING);
            c.triggerRandom(50);
        } else {
            c.resetAction();
        }
    }

    public static void axeHandle(Client c) {
        if (c.IsCutting || c.isFiremaking)
            c.resetAction();
        c.send(new RemoveInterfaces());
        if (c.playerHasItem(1515)) {
            c.deleteItem(1515, 1);
            c.addItem(492, 1);
            c.requestAnim(1248, 0);
            c.giveExperience(50, Skills.FLETCHING);
            c.triggerRandom(50);
        } else {
            c.resetAction();
        }
    }
}