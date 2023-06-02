package net.dodian.uber.game.combat

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import net.dodian.uber.game.Server
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.entity.player.PlayerHandler
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.skills.Skill
import net.dodian.utilities.Utils
import kotlin.math.abs

class PlayerOnPlayerCombatProcessor(
    client: Client
) : CombatProcessor(client) {

    override fun processAttack(): Result<Boolean, Any?> {
        return Err(null)
    }
}

fun Client.attackPlayer(): Boolean {
    if (attackingOnPid <= 0 || attackingOnPid >= PlayerHandler.players.size) {
        resetAttack()
        return false
    }

    if (slot < 1) send(SendMessage("Error: Your player id is invalid. Please try again or relog."))

    if (attackingOnPid > 0 && !(duelFight && duelWithPid == attackingOnPid) && !Server.pking) {
        send(SendMessage("PVP has been disabled!"))
        resetAttack()
        return false
    }

    val opponent = getClient(attackingOnPid)
    if (!validClient(attackingOnPid)) {
        send(SendMessage("Invalid player!"))
        resetAttack()
        return false
    }

    if (opponent.immune) {
        send(SendMessage("That player is immune!"))
        resetAttack()
        return false
    }

    if (duelFight && duelRule[0] && usingBow) {
        send(SendMessage("You can't range in this duel!"))
        return false
    }

    if (duelFight && duelRule[1] && !usingBow) {
        send(SendMessage("You can't melee in this duel!"))
        resetAttack()
        return false
    }

    if (!(duelFight && attackingOnPid == duelWithPid) && (wildyLevel < 1 || opponent.wildyLevel < 1)) {
        send(SendMessage("You can't fight here!"))
        resetAttack()
        return false
    }

    if (!(duelFight && opponent.slot == duelWithPid)
        && (wildyLevel > 0 && opponent.wildyLevel > 0 && abs(opponent.determineCombatLevel() - determineCombatLevel()) > wildyLevel
                || abs(opponent.determineCombatLevel() - determineCombatLevel()) > opponent.wildyLevel)
    ) {
        send(SendMessage("You need to be deeper into the wilderness to fight that player!"))
        resetAttack()
        return false
    }

    val opponentHp = PlayerHandler.players[attackingOnPid].getLevel(Skill.HITPOINTS)
    val opponent2 = PlayerHandler.players[attackingOnPid]

    var hit = if (usingBow) rangedMaxHit()
    else meleeMaxHit()

    val randomAttack = if (attackPot > 0.0)
        Utils.random(((1 + (attackPot / 100)) * getLevel(Skill.ATTACK)).toInt())
    else Utils.random(getLevel(Skill.ATTACK))

    val randomDefense = (0.65 * Utils.random(opponent2.getLevel(Skill.DEFENCE))).toInt()

    var attackBonus = 0
    when (fightType) {
        1 -> attackBonus += (playerBonus[1] / 20)
        2 -> hit = (hit * 1.2).toInt()
    }

    val randomU = Utils.random(playerBonus[1] + attackBonus) * 2


    return false
}