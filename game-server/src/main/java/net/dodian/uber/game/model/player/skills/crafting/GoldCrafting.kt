package net.dodian.uber.game.model.player.skills.crafting;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills
import java.util.*

@kotlin.jvm.JvmField
val jewelry_levels = arrayOf(
    intArrayOf(5, 20, 27, 34, 43, 55, 67),
    intArrayOf(6, 22, 29, 40, 56, 72, 82),
    intArrayOf(8, 23, 31, 50, 70, 80, 90),
    intArrayOf(7, 24, 30, 42, 58, 74, 84)
)

@kotlin.jvm.JvmField
val jewelry_xp = arrayOf(
    intArrayOf(15, 40, 55, 70, 85, 100, 115),
    intArrayOf(20, 55, 60, 75, 90, 105, 120),
    intArrayOf(30, 65, 70, 85, 100, 150, 165),
    intArrayOf(25, 60, 65, 80, 95, 110, 125)
)

class GoldCrafting {
    companion object {

        @kotlin.jvm.JvmField
        val jewelry = arrayOf(
            intArrayOf(1635, 1637, 1639, 1641, 1643, 1645, 6575),
            intArrayOf(1654, 1656, 1658, 1660, 1662, 1664, 6577),
            intArrayOf(1673, 1675, 1677, 1679, 1681, 1683, 6579),
            intArrayOf(11069, 11072, 11076, 11085, 11092, 11115, 11130)
        )

        @JvmStatic
        fun startGoldCrafting(interfaceID: Int, slot: Int, amount: Int, c: Client) {
            var index = 0
            val inters = intArrayOf(4233, 4239, 4245)
            for (i in 0..2) if (inters[i] == interfaceID) index = i
            val level: Int = jewelry_levels.get(index).get(slot)
            if (level > c.getLevel(Skills.CRAFTING)) {
                c.send(SendMessage("You need a crafting level of $level to make this."))
                return
            }
            if (!c.playerHasItem(2357)) {
                c.send(SendMessage("You need at least one gold bar."))
                return
            }
            if (slot != 0 && !c.playerHasItem(c.items.get(slot))) {
                c.send(
                    SendMessage(
                        "You need a " + c.GetItemName(c.items.get(slot))
                            .lowercase(Locale.getDefault()) + " to make this."
                    )
                )
                return
            }
            c.goldCraftingCount = amount
            c.goldIndex = index
            c.goldSlot = slot
            c.goldCrafting = true
            c.send(RemoveInterfaces())
        }

        private fun getItem(i: Int, i2: Int, c: Client): Int {
            return if (!c.playerHasItem(c.items.get(i2)) && c.items.get(i2) != -1) {
                c.blanks.get(i).get(i2)
            } else jewelry.get(i).get(i2)
        }

        @JvmStatic
        fun findStrungAmulet(amulet: Int, c: Client): Int {
            for (i in c.strungAmulets.indices) {
                if (jewelry[2][i] == amulet) {
                    return c.strungAmulets.get(i)
                }
            }
            return -1
        }

        @JvmStatic
        fun goldCraft(c: Client) {
            // int gem = gemReq[goldSlot];
            val level = jewelry_levels[c.goldIndex][c.goldSlot]
            val amount: Int = c.goldCraftingCount
            val item = jewelry[c.goldIndex][c.goldSlot]
            val xp = jewelry_xp[c.goldIndex][c.goldSlot]
            if (c.goldIndex == -1 || c.goldSlot == -1) {
                c.goldCrafting = false
                c.resetAction()
                return
            }
            if (amount <= 0) {
                c.goldCrafting = false
                c.resetAction()
                return
            }
            if (level > c.getLevel(Skills.CRAFTING)) {
                c.send(SendMessage("You need a crafting level of $level to make this."))
                c.goldCrafting = false
                return
            }
            if (!c.playerHasItem(2357)) {
                c.goldCrafting = false
                c.send(SendMessage("You need at least one gold bar."))
                return
            }
            if (c.goldSlot != 0 && !c.playerHasItem(c.items.get(c.goldSlot))) {
                c.goldCrafting = false
                c.send(
                    SendMessage(
                        "You need a " + c.GetItemName(c.items.get(c.goldSlot))
                            .lowercase(Locale.getDefault()) + " to make this."
                    )
                )
                return
            }
            c.goldCraftingCount--
            if (c.goldCraftingCount <= 0) {
                c.goldCrafting = false
            }
            c.requestAnim(0x383, 0)
            c.deleteItem(2357, 1)
            if (c.goldSlot != 0) c.deleteItem(c.items.get(c.goldSlot), 1)
            c.send(SendMessage("You craft a " + c.GetItemName(item).lowercase(Locale.getDefault()) + ""))
            c.addItem(item, 1)
            c.giveExperience(xp * 10, Skills.CRAFTING)
            c.triggerRandom(xp * 10)
        }

        fun setGoldItems(slot: Int, items: IntArray, c: Client) {
            c.getOutputStream().createFrameVarSizeWord(53)
            c.getOutputStream().writeWord(slot)
            c.getOutputStream().writeWord(items.size)
            for (i in items.indices) {
                c.getOutputStream().writeByte(1.toByte().toInt())
                c.getOutputStream().writeWordBigEndianA(items[i] + 1)
            }
            c.getOutputStream().endFrameVarSizeWord()
        }

        @JvmStatic
        fun showItemsGold(c: Client) {
            var slot = 0
            for (i in 0..2) {
                slot = c.startSlots.get(i)
                if (!c.playerHasItem(c.moulds.get(i))) {
                    c.changeInterfaceStatus(c.startSlots.get(i) - 5, true)
                    c.changeInterfaceStatus(c.startSlots.get(i) - 1, false)
                    continue
                } else {
                    c.changeInterfaceStatus(c.startSlots.get(i) - 5, false)
                    c.changeInterfaceStatus(c.startSlots.get(i) - 1, true)
                }
                val itemsToShow = IntArray(7)
                for (i2 in 0..6) {
                    itemsToShow[i2] = getItem(i, i2, c)
                    if (i2 != 0 && itemsToShow[i2] != jewelry[i][i2]) if (i2 < 7) c.sendFrame246(
                        slot + 13 + i2 - 1 - i,
                        c.sizes.get(i),
                        c.black.get(i)
                    ) else c.sendFrame246(
                        slot + 1788 - i * 5, c.sizes.get(i), c.black.get(i)
                    ) else if (i2 != 0) {
                        if (i2 < 7) c.sendFrame246(
                            slot + 13 + i2 - 1 - i,
                            c.sizes.get(i),
                            -1
                        ) else c.sendFrame246(slot + 1788 - i * 5, c.sizes.get(i), -1)
                    }
                }
                setGoldItems(slot, itemsToShow, c)
            }
        }
    }
}