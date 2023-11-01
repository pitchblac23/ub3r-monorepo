package net.dodian.uber.game.model.player.skills.prayer

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.skills.Skills
import java.util.*
import net.dodian.uber.api.Animation

fun buryBones(client: Client, itemId: Int, itemSlot: Int): Boolean {
        val bone = Bones.getBone(itemId)
        if (bone == null || !client.playerHasItem(itemId)) return false
        client.requestAnim(Animation.BURY_BONE, 0)
        client.giveExperience(bone.experience, Skills.PRAYER)
        client.deleteItem(itemId, itemSlot, 1)
        client.sendMessage("You bury the " + client.GetItemName(itemId).lowercase(Locale.getDefault()))
        return true
    }

    fun altarBones(client: Client, itemId: Int): Boolean {
        client.sendMessage("The gods are pleased with your offering.")
        doAction(client, itemId, 2.0)
        return true
    }

    fun chaosAltarBones(client: Client, itemId: Int): Boolean {
        client.sendMessage("Zamorak smiles down upon you and blesses your offering.")
        doAction(client, itemId, 3.5)
        return true
    }

    fun doAction(client: Client, itemId: Int, i: Double): Boolean {
        val bone = Bones.getBone(itemId)
        if (bone == null || !client.playerHasItem(itemId)) {
            client.boneChaos = -1
            client.boneAltar = -1
            return false
        }
        client.deleteItem(itemId, 1)
        client.requestAnim(Animation.OFFER_BONES_TO_ALTER, 0)
        val extra = (client.getLevel(Skills.FIREMAKING) + 1).toDouble() / 100
        val chance = i + extra
        client.giveExperience(((bone.experience * chance).toInt()), Skills.PRAYER)
        client.triggerRandom((Bones.getBone(itemId).experience * chance).toInt())
        return true
    }