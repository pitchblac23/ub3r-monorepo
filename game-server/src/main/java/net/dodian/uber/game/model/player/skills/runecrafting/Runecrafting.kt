package net.dodian.uber.game.model.player.skills.runecrafting;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Misc
import java.util.*

        fun runecraft(player: Client, rune: Int, level: Int, xp: Int) {
            if (!player.contains(1436)) {
                player.send(SendMessage("You do not have any rune essence!"))
                return
            }
            if (player.getLevel(Skills.RUNECRAFTING) < level) {
                player.send(SendMessage("You must have " + level + " runecrafting to craft " + player.GetItemName(rune).lowercase(Locale.getDefault())))
                return
            }
            var count = 0
            var extra = 0
            for (c in player.playerItems.indices) {
                if (player.playerItems.get(c) == 1437 && player.playerItemsN.get(c) > 0) {
                    count++
                    player.deleteItem(1436, 1)
                    val chance: Int = (player.getLevel(Skills.RUNECRAFTING) + 1) / 2
                    val roll = 1 + Misc.random(99)
                    if (roll <= chance) extra++
                }
            }
            player.send(SendMessage("You craft " + (count + extra) + " " + player.GetItemName(rune).lowercase(Locale.getDefault()) + "s."))
            player.addItem(rune, count + extra)
            player.giveExperience(xp * count, Skills.RUNECRAFTING)
            player.triggerRandom(xp * count)
        }