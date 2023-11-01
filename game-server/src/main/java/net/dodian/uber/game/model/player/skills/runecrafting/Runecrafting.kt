package net.dodian.uber.game.model.player.skills.runecrafting

import net.dodian.uber.api.Animation.RUNECRAFTING_CRAFT_RUNES
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import java.util.*

//TODO: add in pure ess

fun runecraft(player: Client, rune: Int, level: Int, xp: Int) {
    if (!player.contains(1436)) {
        player.sendMessage("You do not have any rune essence!")
        return
    }

    if (player.getLevel(Skills.RUNECRAFTING) < level) {
        player.sendMessage("You must have " + level + " runecrafting to craft " + player.GetItemName(rune).lowercase(Locale.getDefault()) + ".")
        return
    }
    var count = 0
    var extra = 0
    for (c in player.playerItems.indices) {
        if (player.playerItems[c] == 1437 && player.playerItemsN[c] > 0) {
            count++
            player.deleteItem(1436, 1)
            val chance: Int = (player.getLevel(Skills.RUNECRAFTING) + 1) / 2
            val roll = 1 + Misc.random(99)
            if (roll <= chance) extra++
        }
    }
    player.requestAnim(RUNECRAFTING_CRAFT_RUNES, 0)
    player.sendMessage("You craft " + (count + extra) + " " + player.GetItemName(rune).lowercase(Locale.getDefault()) + "s.")
    player.addItem(rune, count + extra)
    player.giveExperience(xp * count, Skills.RUNECRAFTING)
    player.triggerRandom(xp * count)
}

fun runeAltar(client: Client, objectId: Int) {
    when (objectId) {
        14898 -> {
            runecraft(client, 558, 1 , 60)
            return
        }
        14903 ->{
            runecraft(client, 564, 30, 75)
            return
        }
        14905 -> {
            runecraft(client, 561, 45, 85)
            return
        }
        27978 -> {
            runecraft(client, 565, 70, 120)
            return
        }
    }
}