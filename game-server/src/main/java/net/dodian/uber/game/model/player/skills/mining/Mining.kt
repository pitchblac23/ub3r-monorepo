package net.dodian.uber.game.model.player.skills.mining

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.item.Equipment
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import net.dodian.utilities.Utils
import java.util.*

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

    fun getMiningSpeed(player: Client): Long {
        val pickBonus = Utils.pickBonus[player.minePick]
        val level = player.getLevel(Skills.MINING).toDouble() / 600
        val random = (50 + Misc.random(100)) / 100.0
        val bonus = 1 + pickBonus * random + level
        val time = Utils.mineTimes[player.mineIndex] / bonus
        return time.toLong()
    }

    fun gemChance(player: Client) {
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
            player.sendMessage("You managed to find a " + player.GetItemName(gem).lowercase(Locale.getDefault()) + " while mining.")
        }
    }

    fun mining(index: Int, player: Client) {
        doAction(player, Utils.ore[index], Utils.oreExp[index])
        if (Misc.chance(86) == 1) { //256 without glory
            gemChance(player)
        }
    }

    fun miningEss(p: Client) {
        if (p.getLevel(Skills.MINING) < 30) {
            doAction(p, 1436, 50)
        } else
            doAction(p, 7936, 50)
    }

    fun doAction(p: Client, i: Int, xp: Int) {
        if (!p.playerHasItem(-1)) {
            p.sendMessage("There is not enough space in your inventory.")
            p.resetAction(true)
            return
        }
        p.sendMessage("You mine some " + p.GetItemName(i).lowercase(Locale.getDefault()) + ".")
        p.addItem(i, 1)
        p.giveExperience(xp, Skills.MINING)
        p.triggerRandom(xp)
    }