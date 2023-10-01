package net.dodian.uber.game.model.player.skills.crafting;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.packets.outgoing.SendString
import kotlin.math.min


class Tanning {

    companion object {

        @JvmStatic
        fun openTan(c: Client) {
            c.send(SendString("Regular Leather", 14777))
            c.send(SendString("50gp", 14785))
            c.send(SendString("", 14781))
            c.send(SendString("", 14789))
            c.send(SendString("", 14778))
            c.send(SendString("", 14786))
            c.send(SendString("", 14782))
            c.send(SendString("", 14790))
            val soon = intArrayOf(14779, 14787, 14783, 14791, 14780, 14788, 14784, 14792)
            val dhide = arrayOf("Green", "Red", "Blue", "Black")
            val cost = arrayOf("1,000gp", "5,000gp", "2,000gp", "10,000gp")
            var type = 0
            for (i in soon.indices) {
                type = if (type == 0) {
                    c.send(SendString(dhide[i / 2], soon[i]))
                    1
                } else {
                    c.send(SendString(cost[i / 2], soon[i]))
                    0
                }
            }
            c.sendFrame246(14769, 250, 1741)
            c.sendFrame246(14773, 250, -1)
            c.sendFrame246(14771, 250, 1753)
            c.sendFrame246(14772, 250, 1751)
            c.sendFrame246(14775, 250, 1749)
            c.sendFrame246(14776, 250, 1747)
            c.showInterface(14670)
        }

        @JvmStatic
        fun startTan(amount: Int, type: Int, c: Client) {
            var amount = amount
            val hide = intArrayOf(1739, -1, 1753, 1751, 1749, 1747)
            val leather = intArrayOf(1741, -1, 1745, 2505, 2507, 2509)
            val charge = intArrayOf(50, 0, 1000, 2000, 5000, 10000)
            if (!c.playerHasItem(995, charge[type])) {
                c.send(SendMessage("You need atleast " + charge[type] + " coins to do this!"))
                return
            }
            amount = if (c.getInvAmt(995) > amount * charge[type]) c.getInvAmt(995) / charge[type] else amount
            amount = min(amount.toDouble(), c.getInvAmt(hide[type]).toDouble()).toInt()
            for (i in 0 until amount) {
                c.deleteItem(hide[type], 1)
                c.deleteItem(995, charge[type])
                c.addItem(leather[type], 1)
            }
        }
    }
}