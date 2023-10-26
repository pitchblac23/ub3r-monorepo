package net.dodian.uber.game.model.player.skills.herblore;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Utils

fun herbCleaning(client: Client, dirtyHerb: Int, slot: Int) {
    when (dirtyHerb) {
        199, 203, 207, 209,
        213, 215, 217, 219,
        3049, 3051 -> {
            cleanHerb(client, slot)
        }
    }
}

fun cleanHerb(client: Client, slot: Int){
    var i = 0
    val item = client.playerItems[slot] - 1
    var used = true

    while (i < Utils.grimy_herbs.size && used) {
        if (Utils.grimy_herbs[i] == item) {
            used = false
            if (Skills.getLevelForExperience(client.getExperience(Skills.HERBLORE)) < Utils.grimy_herbs_lvl[i]) {
                client.send(SendMessage("You need level " + Utils.grimy_herbs_lvl[i] + " herblore to clean this herb."))
            } else {
                client.giveExperience(Utils.grimy_herbs_xp[i], Skills.HERBLORE)
                client.deleteItem(item, slot, 1)
                client.addItemSlot(if (item == 3051 || item == 3049) item - 51 else item + 50, 1, slot)
                client.send(SendMessage("You clean the " + client.GetItemName(item) + "."))
            }
        }
        i++
    }
}