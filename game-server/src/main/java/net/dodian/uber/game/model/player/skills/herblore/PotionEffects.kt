package net.dodian.uber.game.model.player.skills.herblore

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Utils
import java.util.*

fun drinkPotion(client: Client, potion: Int, slot: Int) {
    when (potion) {
        121, 123, 125, 2428 -> potion(client, Skills.ATTACK, slot, 3, 0.1)
        113, 115, 117, 119 -> potion(client, Skills.STRENGTH, slot, 3, 0.1)
        2432, 133, 135, 137 -> potion(client, Skills.DEFENCE, slot, 3, 0.1)
        2436, 145, 147, 149 -> potion(client, Skills.ATTACK, slot, 5, 0.15)
        2440, 157, 159, 161 -> potion(client, Skills.STRENGTH, slot,5, 0.15)
        2442, 163, 165, 167 -> potion(client, Skills.DEFENCE, slot, 5, 0.15)
        2444, 169, 171, 173 -> potion(client, Skills.RANGED, slot, 4, 0.10)
        3040, 3042, 3044, 3046 -> potion(client, Skills.MAGIC, slot, 4, 0.00)
        11726, 11727, 11728, 11729 -> potion(client, Skills.MAGIC, slot, 5, 0.15)
        139, 141, 143, 2434 -> prayerPotion(client, slot)
        3026, 3028, 3030, 3024 -> restorePotion(client, slot)
    }
}

fun potion(client: Client, skill: Skills, slot: Int, i1: Int, i2: Double) {
    potionDose(client, slot)
    client.boost((i1 + (Skills.getLevelForExperience(client.getExperience(skill)) * i2)).toInt(), skill)
}

fun prayerPotion(client: Client, slot: Int) {
    potionDose(client, slot)
    client.pray(7 + (client.getMaxPrayer() * 0.25).toInt())
    client.refreshSkill(Skills.PRAYER)
}

fun restorePotion(client: Client, slot: Int) {
    potionDose(client, slot)
    client.pray(8 + (client.getMaxPrayer() * 0.25).toInt())
    client.refreshSkill(Skills.PRAYER)
    for (i in 0..20) if (i != 3) {
        client.boostedLevel[i] = 0
        client.refreshSkill(Skills.getSkill(i))
    }
}

fun potionDose(client: Client, slot: Int) {
    var nextId = -1
    var i = 0
    val item = client.playerItems[slot] -1

    if (client.deathStage > 0 || client.deathTimer > 0 || client.inDuel) {
        return
    }

    while (i < Utils.pot_4_dose.size && nextId == -1) {
        nextId =
            when {
                (Utils.pot_4_dose[i] == item) -> Utils.pot_3_dose[i]
                (Utils.pot_3_dose[i] == item) -> Utils.pot_2_dose[i]
                (Utils.pot_2_dose[i] == item) -> Utils.pot_1_dose[i]
                (Utils.pot_1_dose[i] == item) -> 229
                else -> {-1}
            }
        i++
    }
    if (nextId > 0) {
        client.addItemSlot(nextId, 1, slot)
    }
    client.requestAnim(1327, 0)
    client.sendMessage(if (nextId == 229) "You have finished your potion." else "You drink some of your ${client.GetItemName(item).lowercase(Locale.getDefault())} potion.")
    //TODO: "You have # doses of potion left." "Fix get item name -(# dose)"
}