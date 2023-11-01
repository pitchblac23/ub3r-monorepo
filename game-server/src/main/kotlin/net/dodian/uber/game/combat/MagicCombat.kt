package net.dodian.uber.game.combat

import net.dodian.uber.api.Animation.CAST_MAGIC_SPELL
import net.dodian.uber.game.Server
import net.dodian.uber.game.model.entity.npc.Npc
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.entity.player.Player
import net.dodian.uber.game.model.item.Equipment
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.uber.game.model.player.skills.prayer.Prayers
import net.dodian.utilities.Misc
import net.dodian.utilities.Utils
import kotlin.math.min

fun Client.handleMagic(): Int {
    if (!canReach(target, 5))
        return 0

    val staves = listOf(1381, 2415, 2416, 2417, 4675, 4710, 6914, 6526)
    if (equipment[Equipment.Slot.WEAPON.id] !in staves || autocast_spellIndex < 0)
        return -1

    val time = System.currentTimeMillis()
    val slot = autocast_spellIndex%4
    if (time - lastAttack > coolDown[slot]) {
        isInCombat = true
        lastCombat = System.currentTimeMillis()
    } else return 0
    setFocus(target.position.x, target.position.y)
    if (getLevel(Skills.MAGIC) < requiredLevel[autocast_spellIndex]) {
        sendMessage("You need a magic level of ${requiredLevel[autocast_spellIndex]} to cast this spell!")
        return 0
    }
    if (!runeCheck()) {
        resetAttack()
        return 0
    }
    if (target is Player && duelFight && duelRule[2]) {
        sendMessage("Magic has been disabled for this duel!")
        resetAttack()
        return 0
    }
    deleteItem(requiredRune[autocast_spellIndex], requiredAmount[autocast_spellIndex])
    requestAnim(CAST_MAGIC_SPELL, 0)
    var maxHit = baseDamage[autocast_spellIndex] * magicBonusDamage()
    if (target is Npc) { // Slayer damage!
        val checkNpc = Server.npcManager.getNpc(target.slot)
        if(getSlayerDamage(checkNpc.id, true) == 2)
            maxHit *= 1.2
        if(checkNpc.boss) {
            val reduceDefence = min(checkNpc.defence / 15, 18)
            val value = (12.0 + Misc.random(reduceDefence)) / 100.0
            maxHit *= 1.0 - value
            //System.out.println("reduce value: $value and defence $reduceDefence to be new max hit $maxHit")
        }
    }
    if(target is Player) {
        val player = Server.playerHandler.getClient(target.slot)
        if (player.prayerManager.isPrayerOn(Prayers.Prayer.PROTECT_MAGIC)) maxHit /= 2.0
    }
    var hit = Utils.random(maxHit.toInt())
    val criticalChance = getLevel(Skills.AGILITY) / 9
    val extra = getLevel(Skills.MAGIC) * 0.195
    val landCrit = Math.random() * 100 <= criticalChance
    if(equipment[Equipment.Slot.SHIELD.id]==4224)
        criticalChance * 1.5
    if (target is Npc) {
        val npc = Server.npcManager.getNpc(target.slot)
        if (landCrit)
            hit + Utils.dRandom2(extra).toInt()
        if(hit >= npc.currentHealth)
            hit = npc.currentHealth
        if(slot == 2) { //Heal effect!
            heal(hit / 3)
        }
        npc.dealDamage(this, hit, landCrit)
    }
    if (target is Player) {
        val player = Server.playerHandler.getClient(target.slot)
        if (landCrit)
            hit + Utils.dRandom2(extra).toInt()
        if(hit >= player.currentHealth)
            hit = player.currentHealth
        if(slot == 2) { //Heal effect!
            currentHealth = min(getLevel(Skills.HITPOINTS), currentHealth + (hit / 3))
            refreshSkill(Skills.HITPOINTS)
        }
        player.dealDamage(hit, landCrit)
    }
    /* Magic graphics! */
    when (slot) {
        2 //Blood effect
        -> stillgfx(377, target.position.y, target.position.x)
        3 //Freeze effect
        -> stillgfx(369, target.position.y, target.position.x)
        else //Other ancient effect!
        -> stillgfx(78, target.position.y, target.position.x)
    }

    if (target is Npc) {
        if (FightType == 1) {
            giveExperience(40 * hit, Skills.DEFENCE)
            giveExperience(40 * hit, Skills.MAGIC)
        } else giveExperience(40 * hit, Skills.MAGIC)

        giveExperience(15 * hit, Skills.HITPOINTS)
    }

    if (debug) sendMessage("hit = $hit, elapsed = ${time - lastAttack}")
    resetWalkingQueue()
    lastAttack = System.currentTimeMillis()

    return 1
}