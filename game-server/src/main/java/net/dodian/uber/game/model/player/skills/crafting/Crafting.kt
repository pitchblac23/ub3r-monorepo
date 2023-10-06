package net.dodian.uber.game.model.player.skills.crafting;

import net.dodian.uber.game.Constants
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.packets.outgoing.SendString
import net.dodian.uber.game.model.player.skills.Skills
import java.util.*

var cLevel = 1
var cExp = 0
var cSelected = -1

class Crafting {

    companion object {

        @kotlin.jvm.JvmField
        var cIndex: Int = -1

        @JvmStatic
        fun startCraft(actionbutton: Int, c: Client) {
            c.send(RemoveInterfaces())
            val buttons = intArrayOf(
                33187, 33186, 33185,/*Armour*/
                33190, 33189, 33188,/*Gloves*/
                33193, 33192, 33191,/*Boots*/
                33196, 33195, 33194,/*Vambraces*/
                33199, 33198, 33197,/*Chaps*/
                33202, 33201, 33200,/*Coif*/
                33205, 33204, 33203)/*Cowl*/
            val amounts = intArrayOf(
                1, 5, 10,
                1, 5, 10,
                1, 5, 10,
                1, 5, 10,
                1, 5, 10,
                1, 5, 10,
                1, 5, 10)
            val ids = intArrayOf(
                1129, 1129, 1129,
                1059, 1059, 1059,
                1061, 1061, 1061,
                1063, 1063, 1063,
                1095, 1095, 1095,
                1169, 1169, 1169,
                1167, 1167, 1167)
            val levels = intArrayOf(14, 1, 7, 11, 18, 38, 9)
            val exp = intArrayOf(33, 18, 21, 29, 38, 52, 20)
            var amount = 0
            var id = -1
            var index = 0
            for (i in buttons.indices) {
                if (actionbutton == buttons[i]) {
                    amount = amounts[i]
                    id = ids[i]
                    index = i / 3
                    break
                }
            }
            if (c.getLevel(Skills.CRAFTING) >= levels[index]) {
                cSelected = 1741
                c.crafting = true
                c.cItem = id
                c.cAmount = if (amount == 10) c.getInvAmt(cSelected) else amount
                cLevel = levels[index]
                cExp = Math.round((exp[index] * 8).toFloat())
            } else if (id != -1) {
                c.send(SendMessage("You need level " + levels[index] + " crafting to craft a " + c.GetItemName(id).lowercase(Locale.getDefault())))
                c.send(RemoveInterfaces())
            }
        }

        @JvmStatic
        fun craft(c: Client) {
            if (c.getLevel(Skills.CRAFTING) < cLevel) {
                c.send(SendMessage("You need " + cLevel + " crafting to make a " + c.GetItemName(c.cItem).lowercase(Locale.getDefault())))
                c.resetAction(true)
                return
            }
            if (!c.playerHasItem(1733) || !c.playerHasItem(1734) || !c.playerHasItem(cSelected, 1)) {
                c.send(
                    SendMessage(
                        if (!c.playerHasItem(1733))
                            "You need a needle to craft!" else if (!c.playerHasItem(1734)) "You have run out of thread!" else "You have run out of " +
                                c.GetItemName(cSelected).lowercase(Locale.getDefault()) + "!"))
                c.resetAction(true)
                return
            }
            if (c.cAmount > 0) {
                c.requestAnim(1249, 0)
                c.deleteItem(cSelected, 1)
                c.deleteItem(1734, 1)
                c.send(SendMessage("You crafted a " + c.GetItemName(c.cItem).lowercase(Locale.getDefault())))
                c.addItem(c.cItem, 1)
                c.giveExperience(cExp, Skills.CRAFTING)
                c.cAmount--
                c.triggerRandom(cExp)
            } else c.resetAction(true)
        }

        @JvmStatic
        fun craftMenu(i: Int, c: Client) {
            c.send(SendString("What would you like to make?", 8898))
            c.send(SendString("Vambraces", 8889))
            c.send(SendString("Chaps", 8893))
            c.send(SendString("Body", 8897))
            c.sendFrame246(8883, 250, Constants.gloves[i])
            c.sendFrame246(8884, 250, Constants.legs[i])
            c.sendFrame246(8885, 250, Constants.chests[i])
            c.sendFrame164(8880)
        }

        @JvmStatic
        fun startHideCraft(b: Int, c: Client) {
            val buttons = intArrayOf(34185, 34184, 34183, 34182, 34189, 34188, 34187, 34186, 34193, 34192, 34191, 34190)
            val amounts = intArrayOf(1, 5, 10, 27)
            var index = 0
            var index2 = 0
            for (i in buttons.indices) {
                if (buttons[i] == b) {
                    index = i % 4
                    index2 = i / 4
                    break
                }
            }
            cSelected = Constants.leathers[cIndex]
            c.cAmount = if (amounts[index] == 27) c.getInvAmt(cSelected) else amounts[index]
            cExp = Constants.leatherExp[cIndex]
            val required: Int
            if (index2 == 0) {
                required = Constants.gloveLevels[cIndex]
                c.cItem = Constants.gloves[cIndex]
            } else if (index2 == 1) {
                required = Constants.legLevels[cIndex]
                c.cItem = Constants.legs[cIndex]
            } else {
                required = Constants.chestLevels[cIndex]
                c.cItem = Constants.chests[cIndex]
            }
            if (required != -1 && c.getLevel(Skills.CRAFTING) >= required) {
                cExp *= 8
                c.crafting = true
                c.send(RemoveInterfaces())
            } else if (required >= 0 && c.cItem != -1) {
                c.send(SendMessage("You need level " + required + " crafting to craft a " + c.GetItemName(c.cItem).lowercase(Locale.getDefault())))
                c.send(RemoveInterfaces())
            } else c.send(SendMessage("Can't make this??"))
        }
    }
}