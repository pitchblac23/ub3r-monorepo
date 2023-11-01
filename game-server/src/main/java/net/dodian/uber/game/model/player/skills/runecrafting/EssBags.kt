package net.dodian.uber.game.model.player.skills.runecrafting

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.skills.Skills
import kotlin.math.min

        fun getPouches(c: Client): String {
            var out = ""
            for (i in c.runePouchesAmount.indices) {
                out += c.runePouchesAmount.get(i).toString() + if (i == c.runePouchesAmount.size - 1) "" else ":"
            }
            return out
        }

        fun fillEssencePouch(pouch: Int, c: Client): Boolean {
            val slot = if (pouch == 5509) 0 else (pouch - 5508) / 2
            if (slot in 0..3) {
                if (c.getLevel(Skills.RUNECRAFTING) < c.runePouchesLevel.get(slot)) {
                    c.sendMessage("You need level " + c.runePouchesLevel.get(slot) + " runecrafting to do this!")
                    return true
                }
                if (c.runePouchesAmount.get(slot) >= c.runePouchesMaxAmount.get(slot)) {
                    c.sendMessage("This pouch is currently full of essence!")
                    return true
                }
                val max: Int = c.runePouchesMaxAmount.get(slot) - c.runePouchesAmount.get(slot)
                val amount = min(c.getInvAmt(1436).toDouble(), max.toDouble()).toInt()
                if (amount > 0) {
                    for (i in 0 until amount) c.deleteItem(1436, 1)
                    c.runePouchesAmount[slot] += amount
                } else c.sendMessage("No essence in your inventory!")
                return true
            }
            return false
        }

        fun emptyEssencePouch(pouch: Int, c: Client): Boolean {
            val slot = if (pouch == 5509) 0 else (pouch - 5508) / 2
            if (slot in 0..3) {
                if (c.getLevel(Skills.RUNECRAFTING) < c.runePouchesLevel.get(slot)) {
                    c.sendMessage("You need level " + c.runePouchesLevel.get(slot) + " runecrafting to do this!")
                    return true
                }
                var amount: Int = c.freeSlots()
                if (amount <= 0) {
                    c.sendMessage("Not enough inventory slot to empty the pouch!")
                    return true
                }
                amount = min(amount.toDouble(), c.runePouchesAmount.get(slot).toDouble()).toInt()
                if (amount > 0) {
                    for (i in 0 until amount) c.addItem(1436, 1)
                    c.runePouchesAmount[slot] -= amount
                } else c.sendMessage("No essence in your pouch!")
                return true
            }
            return false
        }