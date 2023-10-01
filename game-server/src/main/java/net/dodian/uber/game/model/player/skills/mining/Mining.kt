package net.dodian.uber.game.model.player.skills.mining;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.item.Equipment
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import net.dodian.utilities.Utils
import java.util.*


class Mining {

    companion object {

        @JvmStatic
        fun getMiningEmote(item: Int): Int {
            when (item) {
                1275 -> return 624
                1271 -> return 628
                1273 -> return 629
                1269 -> return 627
                1267 -> return 626
                1265 -> return 625
                11920, 20014 -> return 7139
            }
            return -1
        }

        @JvmStatic
        fun findPick(player: Client): Int {
            var Eaxe = -1
            var Iaxe = -1
            val weapon = player.equipment[Equipment.Slot.WEAPON.id]
            for (i in Utils.picks.indices) {
                if (Utils.picks[i] == weapon) {
                    if (player.getLevel(Skills.MINING) >= Utils.pickReq[i]) Eaxe = i
                }
                for (ii in player.playerItems.indices) {
                    if (Utils.picks[i] == player.playerItems[ii] - 1) {
                        if (player.getLevel(Skills.MINING) >= Utils.pickReq[i]) {
                            Iaxe = i
                        }
                    }
                }
            }
            return if (Eaxe > Iaxe) Eaxe else if (Iaxe > Eaxe) Iaxe else -1
        }

        @JvmStatic
        fun getMiningSpeed(player: Client): Long {
            val pickBonus = Utils.pickBonus[player.minePick]
            val level = player.getLevel(Skills.MINING).toDouble() / 600
            val random = Misc.random(150).toDouble() / 100
            val bonus = 1 + pickBonus * random + level
            val time = Utils.mineTimes[player.mineIndex] / bonus
            return time.toLong()
        }

        @JvmStatic
        fun Gemchance(player: Client) {
            val chance = doubleArrayOf(7.03, 3.91, 3.91, 3.12)
            val gemId = intArrayOf(1623, 1621, 1619, 1617)
            var rolledChance = 0
            var gem = -1
            val roll = Misc.chance(10000)
            var i = 0
            while (i < chance.size && gem == -1) {
                rolledChance += (chance[i] * 100).toInt()
                if (roll <= rolledChance) gem = gemId[i + 1] else if (i + 1 == chance.size) gem = gemId[0]
                i++
            }
            if (player.freeSlots() > 0) {
                player.addItem(gem, 1)
                player.send(SendMessage("You managed to find a " + player.GetItemName(gem).lowercase(Locale.getDefault()) + " while mining."))
            }
        }

        @JvmStatic
        fun mining(index: Int, player: Client) {
            if (index != 6) {
                player.send(SendMessage("You mine some " + player.GetItemName(Utils.ore[index]).lowercase(Locale.getDefault()) + "."))
            }
            DoAction(player, Utils.ore[index], Utils.oreExp[index])
            if (Misc.chance(86) == 1) { //256 without glory
                Gemchance(player)
            }
        }

        @JvmStatic
        fun miningEss(p: Client) {
            p.send(SendMessage("You mine some " + p.GetItemName(1436).lowercase(Locale.getDefault()) + "."))
            DoAction(p, 1436, 50)
        }

        fun DoAction(p: Client, i: Int, xp: Int) {
            if (!p.playerHasItem(-1)) {
                p.send(SendMessage("There is not enough space in your inventory."))
                p.resetAction(true)
                return
            }
            p.addItem(i, 1)
            p.giveExperience(xp, Skills.MINING)
            p.triggerRandom(xp)
        }
    }
}