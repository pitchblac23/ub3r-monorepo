package net.dodian.uber.game.model.player.skills.cooking

import net.dodian.uber.game.Server
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.item.Equipment
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Utils
import java.util.*

fun startCooking(id: Int, c: Client) {
    if (c.inTrade || c.inDuel) {
        c.sendMessage("Cannot cook in duel or trade.")
        return
    }
    var valid = false
    for (i in Utils.rawIds.indices) {
        if (id == Utils.rawIds[i]) {
            c.cookIndex = i
            valid = true
        }
    }
    if (valid) {
        c.cookAmount = c.getInvAmt(id)
        c.cooking = true
    }
}

fun cook(c: Client) {
    if (c.inTrade || c.inDuel || c.cookAmount < 1) {
        c.resetAction(true)
        return
    }
    if (!c.playerHasItem(Utils.rawIds[c.cookIndex])) {
        c.sendMessage("You are out of fish.")
        c.resetAction(true)
        return
    }
    val id = Utils.rawIds[c.cookIndex]
    var ran = 0
    var index = 0
    for (i in Utils.rawIds.indices) {
        if (id == Utils.rawIds[i]) {
            index = i
        }
    }
    if (c.getLevel(Skills.COOKING) < Utils.cookLevel[index]) {
        c.sendMessage("You need " + Utils.cookLevel[index] + " cooking to cook the " + Server.itemManager.getName(id).lowercase(Locale.getDefault()) + ".")
        c.resetAction(true)
        return
    }
    when (id) {
        2134, 317, 321 -> ran = 30 - c.getLevel(Skills.COOKING)
        2307 -> ran = 36 - c.getLevel(Skills.COOKING)
        3363 -> ran = 42 - c.getLevel(Skills.COOKING)
        335 -> ran = 50 - c.getLevel(Skills.COOKING)
        331 -> ran = 60 - c.getLevel(Skills.COOKING)
        377 -> ran = 70 - c.getLevel(Skills.COOKING)
        371 -> ran = 80 - c.getLevel(Skills.COOKING)
        7944 -> ran = 90 - c.getLevel(Skills.COOKING)
        383 -> ran = 100 - c.getLevel(Skills.COOKING)
        395 -> ran = 110 - c.getLevel(Skills.COOKING)
        389 -> ran = 120 - c.getLevel(Skills.COOKING)
    }
    if (c.getEquipment().get(Equipment.Slot.HANDS.id) == 775) ran -= 4
    if (c.getEquipment().get(Equipment.Slot.HEAD.id) == 1949) ran -= 4
    if (c.getEquipment().get(Equipment.Slot.HEAD.id) == 1949 && c.getEquipment().get(Equipment.Slot.HANDS.id) == 775) ran -= 2
    ran = if (ran < 0) 0 else if (ran > 100) 100 else ran
    val burn = 1 + Utils.random(99) <= ran
    if (Utils.cookExp[index] > 0) {
        c.cookAmount--
        c.deleteItem(id, 1)
        c.setFocus(c.skillX, c.skillY)
        c.requestAnim(883, 0)
        if (!burn) {
            c.addItem(Utils.cookedIds[index], 1)
            c.sendMessage("You cook the " + c.GetItemName(id).lowercase(Locale.getDefault()) + ".")
            c.giveExperience(Utils.cookExp[index], Skills.COOKING)
        } else {
            c.addItem(Utils.burnId[index], 1)
            c.sendMessage("You burn the " + c.GetItemName(id).lowercase(Locale.getDefault()) + ".")
        }
        c.triggerRandom(Utils.cookExp[index])
    }
}