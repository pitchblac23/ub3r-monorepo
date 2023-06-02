package net.dodian.uber.game.combat

import com.github.michaelbull.result.Result
import net.dodian.uber.game.model.entity.Entity
import net.dodian.uber.game.model.entity.player.Client
import kotlin.math.min
import kotlin.math.sqrt

abstract class CombatProcessor(val client: Client) {
    protected abstract fun processAttack(): Result<Boolean, Any?>

    protected val target: Entity = client.selectedNpc

    fun executeAttack() {

    }

    protected class CriticalHit(val slayer: Int, val agility: Int, val skill: Int) {

        private val baseChance: Double
            get() {
                val slayerModifier = (slayer - 1) / 5.5
                val agilityModifier = (agility - 1)
                val justifiedChance = (slayerModifier + agilityModifier) * 0.33

                return String.format("%.3f", (justifiedChance * (agility * 0.0333)) + (slayer * 0.075)).toDouble()
            }

        private val baseModifier: Double
            get() {
                val slayerBaseBonus = slayer + 98
                val slayerBaseModifier = slayer * 0.055
                val justification = (slayer - 1) + (agility - 1)
                val preSkillBase = (slayerBaseBonus * slayerBaseModifier * justification)

                return String.format("%.3f", sqrt(preSkillBase * min(skill - 1, 1))).toDouble()
            }

        private val baseStrength: Double get() = String.format("%.3f", sqrt(baseModifier * (skill * 0.0333))).toDouble()

        val chance: Double get() = min(((((baseChance * 0.125) + agility * 0.00255)) + (slayer * 0.055)) / 100, 0.15)
        val max: Int get() = min(((baseStrength * 0.35) + (slayer * 0.055) + min(1.0, (agility * 0.0125))).toInt(), 20)
        val min: Int get() = min(((max - 1) * (chance + 0.075)).toInt(), max)

        override fun toString() =
            "chance = ${chance * 100}%, max = $max, min = $min |> baseChance = $baseChance, baseModifier = $baseModifier, baseStrength = $baseStrength"
    }
}