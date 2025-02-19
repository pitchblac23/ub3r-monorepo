package net.dodian.uber.game.model.player.skills.mining

import net.dodian.uber.game.model.entity.player.Client
import kotlin.math.min

    fun fillCoalBag(c: Client) {
        if (c.coalBagAmount >= c.coalBagMaxAmount) {
            c.sendMessage("Your coal bag is already full.")
        } else {
            val max = c.coalBagMaxAmount - c.coalBagAmount
            val amount = min(c.getInvAmt(453).toDouble(), max.toDouble()).toInt()
            if (amount > 0) {
                c.i = 0
                while (c.i < amount) {
                    c.deleteItem(453, 1)
                    c.i++
                }
                c.coalBagAmount += amount
                c.sendMessage("You add the coal to your bag.")
                c.sendMessage("The coal bag contains " + c.coalBagAmount + " pieces of coal.")
            } else {
                c.sendMessage("The coal bag can be filled only with coal. You haven't got any.")
            }
        }
    }

    fun emptyCoalBag(c: Client) {
        var amount = c.freeSlots()
        if (amount <= 0) {
            c.sendMessage("Not enough inventory slots to empty the bag.")
        } else {
            amount = min(amount.toDouble(), c.coalBagAmount.toDouble()).toInt()
            if (amount > 0) {
                c.i = 0
                while (c.i < amount) {
                    c.addItem(453, 1)
                    c.i++
                }
                c.coalBagAmount -= amount
            } else if (c.coalBagAmount == 0) {
                c.sendMessage("The coal bag is empty.")
            }
        }
    }