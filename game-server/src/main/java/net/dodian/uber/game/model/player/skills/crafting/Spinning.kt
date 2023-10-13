package net.dodian.uber.game.model.player.skills.crafting;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skills

fun getSpinSpeed(c: Client): Long {
    val craftingLevel: Int = c.getLevel(Skills.CRAFTING)
    return if (craftingLevel in 40..69) 1200 else if (craftingLevel >= 70) 600 else 1800
}

fun spin(c: Client) {
    c.requestAnim(894, 0);

    if (c.playerHasItem(1779)) {
        spinAction(c, 1779, 1777, 50, 50)
    } else if (c.playerHasItem(1737)) {
        spinAction(c, 1737, 1759, 100, 100)
    } else {
        c.resetAction(true)
        c.send(SendMessage("You do not have anything to spin!"))
    }
    c.lastAction = System.currentTimeMillis()
}

private fun spinAction(c: Client, itemdel: Int, itemadd: Int, xp: Int, random: Int) {
    c.deleteItem(itemdel, 1)
    c.addItem(itemadd, 1)
    c.giveExperience(xp, Skills.CRAFTING)
    c.triggerRandom(random)
}