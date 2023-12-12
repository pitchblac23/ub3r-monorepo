package net.dodian.uber.game.model.player.skills.fishing

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import java.util.*

var fishSpots = intArrayOf(1514, 1514, 1510, 1510, 1511, 1511, 1517, 1517, 1526, 1526)
var fishId = intArrayOf(317, 327, 377, 359, 7944, 359, 383, 395, 389)
var fishAnim = intArrayOf(621, 622, 619, 618, 621, 618, 619, 618)
var fishReq = intArrayOf(1, 5, 40, 35, 62, 35, 70, 85, 95)
var fishTime = intArrayOf(1200, 1800, 2400, 3000, 2400, 3000, 4200, 4800, 5400)
var fishTool = intArrayOf(303, 307, 301, 311, 303, 311, 301, 311)
var fishExp = intArrayOf(45, 90, 405, 360, 540, 360,/**/ 1100, 1450, 1900) //*4.5 osrs

fun startFishing(p: Client, clickObject: Int, clickOption: Int) {
    var valid = false

    for (i in 0 until fishSpots.size) {
        if (fishSpots[i] == clickObject) {
            if (clickOption == 1 && (i == 0 || i == 2 || i == 4 || i == 6 || i == 8)) {
                valid = true
                p.fishIndex = i
                break
            } else if (clickOption == 2 && (i == 1 || i == 3 || i == 5 || i == 7 || i == 9)) {
                valid = true
                p.fishIndex = i
                break
            }
        }
    }
    if (!valid) {
        p.resetAction(true)
        return
    }
    if (p.getLevel(Skills.FISHING) < fishReq[p.fishIndex]) {
        p.sendMessage("You need " + fishReq[p.fishIndex] + " fishing to fish here.")
        p.resetAction(true)
        return
    }
    if (!p.playerHasItem(-1)) {
        p.sendMessage("Not enough inventory space!")
        p.resetAction(true)
        return
    }
    if (!p.playerHasItem(fishTool[p.fishIndex])) {
        p.sendMessage("You need a " + p.GetItemName(fishTool[p.fishIndex]) + " to fish here.")
        p.resetAction(true)
        return
    }
    if (!p.playerHasItem(313) && p.fishIndex == 1) {
        p.sendMessage("You do not have any fishing bait.")
        p.resetAction(true)
        return
    }
    if (p.fishIndex == 0) {
        p.sendMessage("You cast out you net...")
    }
    if (p.fishIndex == 1) {
        p.sendMessage("You cast out you line...")
        p.sendMessage("You attempt to catch a fish.")
    }
    p.lastAction = System.currentTimeMillis() + fishTime[p.fishIndex]
    p.requestAnim(fishAnim[p.fishIndex], 0)
    p.fishing = true
}

fun fish(p: Client) {
    if (!p.playerHasItem(-1)) {
        p.sendMessage("Not enough inventory space!")
        p.resetAction(true)
        return
    }
    p.lastAction = System.currentTimeMillis()
    if (p.fishIndex == 0) {
        val random = Misc.random(6)
        if (p.getLevel(Skills.FISHING) >= 15 && random < 3) {
            p.addItem(321, 1)
            p.giveExperience(fishExp[p.fishIndex] + 135, Skills.FISHING)
            p.sendMessage("You catch some anchovies.")
        } else {
            p.giveExperience(fishExp[p.fishIndex], Skills.FISHING)
            p.addItem(fishId[p.fishIndex], 1)
            p.sendMessage("You catch some shrimps.")
        }
    } else if (p.fishIndex == 1) {
        p.deleteItem(313, 1)
        val random = Misc.random(6)
        if (p.getLevel(Skills.FISHING) >= 10 && random < 3) {
            p.addItem(345, 1)
            p.giveExperience(fishExp[p.fishIndex] + 45, Skills.FISHING)
            p.sendMessage("You catch a herring.")
        } else {
            p.giveExperience(fishExp[p.fishIndex], Skills.FISHING)
            p.addItem(fishId[p.fishIndex], 1)
            p.sendMessage("You catch a sardine.")
        }
    } else if (p.fishIndex == 3 || p.fishIndex == 5) {
        val random = Misc.random(6)
        if (p.getLevel(Skills.FISHING) >= 50 && random < 3) {
            p.addItem(371, 1)
            p.giveExperience(fishExp[p.fishIndex] + 90, Skills.FISHING)
            p.sendMessage("You catch some raw swordfish.")
        } else {
            p.giveExperience(fishExp[p.fishIndex], Skills.FISHING)
            p.addItem(fishId[p.fishIndex], 1)
            p.sendMessage("You catch some raw tuna.")
        }
    } else {
        p.giveExperience(fishExp[p.fishIndex], Skills.FISHING)
        p.addItem(fishId[p.fishIndex], 1)
        p.sendMessage("You catch some " + p.GetItemName(fishId[p.fishIndex]).lowercase(Locale.getDefault()) + ".")
    }
    p.requestAnim(fishAnim[p.fishIndex], 0)
    p.triggerRandom(fishExp[p.fishIndex])
}