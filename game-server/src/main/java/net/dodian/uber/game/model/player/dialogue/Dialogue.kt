package net.dodian.uber.game.model.player.dialogue;

import net.dodian.uber.game.Server
import net.dodian.uber.game.event.Event
import net.dodian.uber.game.event.EventManager
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.packets.outgoing.SendString
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.uber.game.model.player.skills.agility.Agility
import net.dodian.uber.game.model.player.skills.slayer.SlayerTask
import net.dodian.utilities.DbTables
import net.dodian.utilities.Misc
import net.dodian.utilities.dbConnection
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

/**~Special thanks to @Nozemi~**/
/**~for the functions and conversions~**/

fun Client.sendString(value: String, component: Int) = send(SendString(value, component))
fun Client.sendMessage(text: String) = send(SendMessage(text))
fun Client.playerChat(emote: Int, vararg lines: String) = showPlayerChat(lines, emote)
fun Client.npcChat(npc: Int, emote: Int, vararg lines: String) = showNPCChat(npc, emote, lines)
fun Client.playerOptions(vararg lines: String) = showPlayerOption(lines)

    fun updateNPCChat(p: Client) = when (p.NpcDialogue) {
            1 -> p.npcChat(p.NpcTalkTo, 591, "Good day, how can I help you?")

            2 -> {
                p.playerOptions(
                    "What would you like to say?",
                    "I'd like to access my bank account, please.",
                    "I'd like to access my PIN settings.")
            }

            3 -> {
                p.npcChat(p.NpcTalkTo, 591, "Do you want to buy some runes?")
                p.nextDiag = 4
            }

            4 -> {
                p.playerOptions(
                    "Select an Option",
                    "Yes please!",
                    "Oh it's a rune shop. No thank you, then.")
            }

            5 -> p.playerChat(592, "Oh it's a rune shop. No thank you, then.")

            6 -> p.npcChat(p.NpcTalkTo, 592, "Well, if you find someone who does want runes, please", "send them my way.")

            7 -> p.npcChat(p.NpcTalkTo, 591, "Well, if you find someone who does want runes, please", "send them my way.")

            8 -> p.npcChat(p.NpcTalkTo, 591, "Pins have not been implemented yet.")

            9 -> {
                p.sendFrame200(4883, 1597)
                p.sendString(p.GetNpcName(p.NpcTalkTo), 4884)
                p.playerOptions(
                    "Select an Option",
                    "Can you teleport me to the mage arena?",
                    "Whats at the mage arena?")
            }

            1002 -> p.npcChat(p.NpcTalkTo, 591, "Cant talk right now!")

            11 -> {
                p.npcChat(p.NpcTalkTo, 591, "Hi there noob, what do you want?")
                p.nextDiag = 12
            }

            12 -> {
                if (p.NpcTalkTo == 405 && (p.determineCombatLevel() < 50 || p.getLevel(Skills.SLAYER) < 50)) {
                    p.npcChat(p.NpcTalkTo, 591, "You need 50 combat and slayer", "to be assign tasks from me!")
                } else if (p.NpcTalkTo == 403 && (!p.checkItem(989) || p.getLevel(Skills.SLAYER) < 50)) {
                    p.npcChat(p.NpcTalkTo, 591, "You need a crystal key and 50 slayer", "to be assign tasks from me!")
                } else {
                    val taskName =
                        if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) ""
                        else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                    val slayerMaster = arrayOf("What would you like to say?", "I'd like a task please",
                        if (taskName != "") "Cancel " + taskName.lowercase(Locale.getDefault()) + " task"
                        else "No task to skip", "I'd like to upgrade my slayer mask", "Can you teleport me to west ardougne?")
                    p.showPlayerOption(slayerMaster)
                }
                p.NpcDialogueSend = true
            }

            13 -> {
                if (p.NpcTalkTo == 405 && (p.determineCombatLevel() < 50 || p.getLevel(Skills.SLAYER) < 50)) {
                    p.npcChat(p.NpcTalkTo, 591, "You need 50 combat and slayer", "to be assign tasks from me!")
                } else if (p.NpcTalkTo == 403 && (!p.checkItem(989) || p.getLevel(Skills.SLAYER) < 50)) {
                    p.npcChat(p.NpcTalkTo, 591, "You need a crystal key and 50 slayer", "to be assign tasks from me!")
                } else {
                    if (p.NpcTalkTo == 402) {
                        val tasks = SlayerTask.mazchnaTasks(p)
                        if (tasks.isEmpty()) {
                            p.sendMessage("You cant get any task!")
                            //break
                        }
                        if (p.getSlayerData().get(3) > 0) {
                            p.npcChat(p.NpcTalkTo, 591, "You already have a task!")
                            //break
                        }
                        val task = tasks[Misc.random(tasks.size - 1)]
                        if (task != null) {
                            val amt = task.assignedAmountRange.value
                            p.npcChat(p.NpcTalkTo, 591, "You must go out and kill", "If you want a new task that's too bad", "Visit Dodian.net for a slayer guide")
                            p.getSlayerData().set(0, p.NpcTalkTo)
                            p.getSlayerData().set(1, task.ordinal)
                            p.getSlayerData().set(2, amt)
                            p.getSlayerData().set(3, amt)
                        }
                    } else if (p.NpcTalkTo == 403) {
                        val tasks = SlayerTask.vannakaTasks(p)
                        if (tasks.isEmpty()) {
                            p.sendMessage("You cant get any task!")
                            //break
                        }
                        if (p.getSlayerData().get(3) > 0) {
                            p.npcChat(p.NpcTalkTo, 591, "You already have a task!")
                            //break
                        }
                        val task = tasks[Misc.random(tasks.size - 1)]
                        if (task != null) {
                            val amt = task.assignedAmountRange.value
                            p.npcChat(p.NpcTalkTo, 591, "You must go out and kill " + amt + " " + task.textRepresentation + ".", "If you want a new task that's too bad", "Visit Dodian.net for a slayer guide")
                            p.getSlayerData().set(0, p.NpcTalkTo)
                            p.getSlayerData().set(1, task.ordinal)
                            p.getSlayerData().set(2, amt)
                            p.getSlayerData().set(3, amt)
                        }
                    } else if (p.NpcTalkTo == 405) {
                        val tasks = SlayerTask.duradelTasks(p)
                        if (tasks.isEmpty()) {
                            p.sendMessage("You cant get any task!")
                            //break
                        }
                        if (p.getSlayerData().get(3) > 0) {
                            p.npcChat(p.NpcTalkTo, 591, "You already have a task!")
                            //break
                        }
                        val task = tasks[Misc.random(tasks.size - 1)]
                        if (task != null) {
                            val amt = task.assignedAmountRange.value
                            p.npcChat(p.NpcTalkTo, 591, "You must go out and kill " + amt + " " + task.textRepresentation + ".", "If you want a new task that's too bad", "Visit Dodian.net for a slayer guide")
                            p.getSlayerData().set(0, p.NpcTalkTo)
                            p.getSlayerData().set(1, task.ordinal)
                            p.getSlayerData().set(2, amt)
                            p.getSlayerData().set(3, amt)
                        }
                    } else p.sendMessage("This npc do not wish to talk to you!")
                }
                p.NpcDialogueSend = true
            }

            31 -> {
                val taskName =
                    if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) "" else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                val cost =
                    if (p.getSlayerData().get(0) == 403) 250000 else if (p.getSlayerData().get(0) == 405) 500000 else 100000 //Default 100k!
                p.npcChat(p.NpcTalkTo, 591, if (taskName == "") "You do not have a task currently." else "I can cancel your " + taskName.lowercase(Locale.getDefault()) + " task.", if (taskName == "") "Talk to me to get one." else "task for $cost coins.")
                if (taskName != "") p.nextDiag = 32
                p.NpcDialogueSend = true
            }

            32 -> {
                val taskName =
                    if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) ""
                    else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                if (taskName != "") p.playerOptions(
                                        "Do you wish to cancel your " + taskName.lowercase(Locale.getDefault()) + " task?",
                                        "Yes",
                                        "No")
                p.NpcDialogueSend = true
            }

            33 -> {
                val taskName =
                    if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) "" else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                val cost =
                    if (p.getSlayerData().get(0) == 403) 250000 else if (p.getSlayerData().get(0) == 405) 500000 else 100000 //Default 100k!
                val coinAmount: Int = p.getInvAmt(995)
                if (coinAmount >= cost) {
                    p.deleteItem(995, cost)
                    p.getSlayerData().set(3, 0)
                    p.npcChat(p.NpcTalkTo, 591, "I have now canceled your " + taskName.lowercase(Locale.getDefault()) + " task!")
                    p.nextDiag = 12
                } else {
                    p.npcChat(p.NpcTalkTo, 591, "You do not have enough coins to cancel your task!")
                }
                p.sendString("Click here to continue", 4886)
                p.NpcDialogueSend = true
            }

            34 -> {
                if (!p.playerHasItem(8921)) p.npcChat(p.NpcTalkTo, 596, "You do not have a black mask!")
                else {
                    p.npcChat(p.NpcTalkTo, 591, "Would you like to upgrade your black mask?", "It will cost you 2 million gold pieces.")
                    p.nextDiag = 35
                }
                p.NpcDialogueSend = true
            }

            35 -> p.playerOptions("Do you wish to upgrade?", "Yes, please", "No, please")

            36 -> {
                p.playerChat(614, "Yes, thank you.")
                p.nextDiag = 37
            }

            37 -> {
                if (!p.playerHasItem(995, 2000000)) p.npcChat(p.NpcTalkTo, 596, "You do not have enough money!")
                else {
                    p.deleteItem(995, 2000000)
                    p.deleteItem(8921, 1)
                    p.addItem(11784, 1)
                    p.npcChat(p.NpcTalkTo, 592, "Here is your imbued black mask.")
                }
                p.NpcDialogueSend = true
            }

            14 -> {
                p.npcChat(p.NpcTalkTo, 591, "Be careful out there!")
                p.triggerTele(2542, 3306, 0, false)
            }

            15 -> {
                if (p.getSlayerData().get(0) != -1) {
                    val slayerMaster: Int = p.getSlayerData().get(0)
                    var out = "Talk to me to get a new task!"
                    val checkTask = SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1))
                    if (checkTask != null && p.getSlayerData().get(3) > 0) out = "You need to kill " + p.getSlayerData().get(3) + " more " + checkTask.textRepresentation
                    p.npcChat(p.NpcTalkTo, 591, p.GetNpcName(slayerMaster), out)
                    //p.send(NpcDialogueHead(slayerMaster, 4883))
                } else p.sendMessage("You have yet to get a task. Talk to a slayer master!")
                p.NpcDialogueSend = true
            }

            16 -> {
                p.npcChat(p.NpcTalkTo, 591, "Would you like to buy some herblore supplies?")
                p.nextDiag = 17
            }

            17 -> p.playerOptions(
                    "Would you like to buy herblore supplies?",
                    "Sure, what do you have?",
                    "No thanks")

            19 -> {
                p.npcChat(p.NpcTalkTo, 591, "Would you like to buy some supplies?")
                p.nextDiag = 20
            }

            20 -> p.playerOptions(
                    "Would you like to buy some supplies?",
                    "Sure, what do you have?",
                    "No thanks")

            21 -> p.npcChat(if (p.getGender() == 0) 1306 else 1307, 588, "Hello there, would you like to change your looks?", "If so, it will be free of charge")

            22 -> p.playerOptions(
                "Would you like to change your looks?",
                "Sure", "No thanks")

            23 -> p.playerChat(614, "I would love that.")

            24 -> p.playerChat(614, "Not at the moment.")

            25 -> {
                //showInterface(3559);
                p.sendFrame248(3559, 3213)
                p.NpcDialogue = 0
                p.NpcDialogueSend = false
            }

            26 -> p.playerOptions(
                "What would you like to do?",
                "Enable specials",
                "Disable specials")

            27 -> p.playerOptions(
                "What would you like to do?",
                "Enable boss yell messages",
                "Disable boss yell messages")

            162 -> {
                p.npcChat(p.NpcTalkTo, 591, "Fancy meeting you here maggot.", "If you have any agility ticket,", "I would gladly take them from you.")
                p.nextDiag = 163
                p.NpcDialogueSend = true
            }

            163 -> p.playerOptions(
                "Trade in tickets or teleport to agility course?",
                "Trade in tickets.",
                "Another course, please.")

            164 -> {
                val type =
                    if (p.skillX == 3002 && p.skillY == 3931) 3 else if (p.skillX == 2547 && p.skillY == 3554) 2 else 1
                val type_gnome =
                    arrayOf("Which course do you wish to be taken to?", "Barbarian", "Wilderness", "Stay here")
                val type_barbarian =
                    arrayOf("Which course do you wish to be taken to?", "Gnome", "Wilderness", "Stay here")
                val type_wilderness =
                    arrayOf("Which course do you wish to be taken to?", "Gnome", "Barbarian", "Stay here")
                p.showPlayerOption(if (type == 3) type_wilderness else if (type == 2) type_barbarian else type_gnome)
                p.NpcDialogueSend = true
            }

            536 -> p.playerOptions(
                "Do you wish to enter?",
                "Sacrifice 5 dragon bones",
                "Stay here")

            2345 -> {
                if (!p.checkUnlock(0)) {
                    p.npcChat(p.NpcTalkTo, 591,
                                "Hello!",
                                "Are you looking to enter my dungeon?",
                                "You have to pay a to enter.",
                                "You can also pay a one time fee.")
                    p.nextDiag = ++p.NpcDialogue
                } else
                    p.npcChat(p.NpcTalkTo, 591, "You can enter freely, no need to pay me anything.")
                p.NpcDialogueSend = true
            }

            2346 -> {
                if (!p.checkUnlock(0) && p.checkUnlockPaid(0) != 1) p.playerOptions(
                    "Select a option",
                    "Enter fee",
                    "Permanent unlock",
                    "Nevermind")
                else if (p.checkUnlock(0)) p.npcChat(p.NpcTalkTo, 591, "You can enter freely, no need to pay me anything.")
                else
                    p.npcChat(p.NpcTalkTo, 591, "You have already paid.", "Just enter the dungeon now.")
                p.NpcDialogueSend = true
            }

            2182, 2347 -> p.playerOptions(
                "Select a option",
                "Ship ticket",
                "Coins")

            2180 -> {
                if (!p.checkUnlock(1)) {
                    p.npcChat(p.NpcTalkTo, 591,
                        "Hello!",
                        "Are you looking to enter my cave?",
                        "You have to pay a to enter.",
                        "You can also pay a one time fee.")
                    p.nextDiag = ++p.NpcDialogue
                } else
                    p.npcChat(p.NpcTalkTo, 591, "You can enter freely, no need to pay me anything.")
                p.NpcDialogueSend = true
            }

            2181 -> {
                if (!p.checkUnlock(1) && p.checkUnlockPaid(1) != 1) p.playerOptions(
                    "Select a option",
                    "Enter fee",
                    "Permanent unlock",
                    "Nevermind")
                else if (p.checkUnlock(1)) p.npcChat(p.NpcTalkTo, 591, "You can enter freely, no need to pay me anything.")
                else
                    p.npcChat(p.NpcTalkTo, 591, "You have already paid.", "Just enter the cave now.")
                p.NpcDialogueSend = true
            }

            3648 -> {
                p.npcChat(p.NpcTalkTo, 591, "Hello dear.", "Would you like to travel?")
                p.nextDiag = 3649
            }

            3649 -> p.playerOptions(
                "Do you wish to travel?",
                "Yes",
                "No")

            6481 -> {
                if (p.totalLevel() >= Skills.maxTotalLevel()) p.npcChat(p.NpcTalkTo, 591, "I see that you have trained up all your skills.", "I am utmost impressed!")
                else
                    p.npcChat(p.NpcTalkTo, 591, "You are quite weak!")
                p.nextDiag = p.NpcDialogue + 1
                p.NpcDialogueSend = true
            }

            6482 -> {
                if (p.totalLevel() >= Skills.maxTotalLevel()) {
                    p.npcChat(p.NpcTalkTo, 591, "Would you like to purchase this cape on my back?", "It will cost you 13.37 million coins.")
                    p.nextDiag = p.NpcDialogue + 1
                } else
                    p.npcChat(p.NpcTalkTo, 591, "Come back when you have trained up your skills!")
                p.NpcDialogueSend = true
            }

            6483 -> p.playerOptions(
                "Purchase the max cape?",
                "Yes",
                "No")

            6484 -> {
                val coins = 13370000
                val freeSlot = if (p.getInvAmt(995) == coins) 1 else 2
                if (p.freeSlots() < freeSlot) {
                    p.npcChat(p.NpcTalkTo, 591, "You need atleast " + (if (freeSlot == 1) "one" else "two") + " free inventory slot" + (if (freeSlot != 1) "s" else "") + ".")
                    p.nextDiag = p.NpcTalkTo
                } else if (!p.playerHasItem(995, coins)) p.npcChat(p.NpcTalkTo, 591, "You are missing " + NumberFormat.getNumberInstance().format((coins - p.getInvAmt(995)).toLong()) + " coins!")
                else {
                    p.npcChat(p.NpcTalkTo, 591, "Here you go.", "Max cape just for you.")
                    p.deleteItem(995, coins)
                    p.addItem(13281, 1)
                    p.addItem(13280, 1)
                }
                p.NpcDialogueSend = true
            }

            8051 -> {
                p.npcChat(p.NpcTalkTo, 591, "Happy Holidays adventurer!")
                p.nextDiag = 8052
            }

            8052 -> {
                p.npcChat(p.NpcTalkTo, 591, "The monsters are trying to ruin the new year!", "You must slay them to take back your gifts and", "save the spirit of 2021!")
                p.nextDiag = 8053
            }

            8053 -> p.playerOptions(
                "Select a option",
                "I'd like to see your shop.",
                "I'll just be on my way.")

            48054 -> p.playerOptions(
                "Unlock the travel?",
                "Yes",
                "No")

            10000 -> if (p.getLevel(Skills.SMITHING) >= 60 && p.playerHasItem(2347)) p.playerOptions("What would you like to make?", "Head", "Body", "Legs", "Boots", "Gloves")
            else {
                p.sendMessage(if (p.getLevel(Skills.SMITHING) < 60) "You need level 60 smithing to do this." else "You need a hammer to handle this material.")
                p.NpcDialogueSend = true
            }

        else -> { }
    }

    fun triggerChat(button: Int, p: Client) {
        if (!p.playerPotato.isEmpty()) if (p.playerPotato.get(0) == 2 && p.playerPotato.get(3) == 1) {
            p.send(RemoveInterfaces())
            val tempNpc = Server.npcManager.getNpc(p.playerPotato.get(1))
            val npcId: Int = p.playerPotato.get(2)
            if (button == 1) {
                try {
                    val conn = dbConnection
                    val statement = conn.createStatement()
                    val sql =
                        "delete from " + DbTables.GAME_NPC_SPAWNS + " where id='" + npcId + "' && x='" + tempNpc.position.x + "' && y='" + tempNpc.position.y + "' && height='" + tempNpc.position.z + "'"
                    if (statement.executeUpdate(sql) < 1) p.send(SendMessage("This npc has already been removed!")) else { //Functions to remove npc!
                        tempNpc.die()
                        EventManager.getInstance().registerEvent(object : Event(tempNpc.getTimeOnFloor() + 600) {
                            override fun execute() {
                                Server.npcManager.npcs.remove(tempNpc)
                                stop()
                            }
                        })
                        p.sendMessage("You removed this npc spawn!")
                    }
                    statement.executeUpdate(sql)
                    statement.close()
                } catch (e: Exception) {
                    p.sendMessage("Something went wrong in removing this npc!")
                }
            } else if (button == 2) {
                if (!tempNpc.getData().drops.isEmpty()) {
                    var line = 8147
                    var totalChance = 0.0
                    p.clearQuestInterface()
                    p.sendString("@dre@Drops for @blu@" + tempNpc.npcName() + "@bla@(@gre@" + npcId + "@bla@)", 8144)
                    val text = ArrayList<String>()
                    /* 100% drops! */for (drop in tempNpc.getData().drops) {
                        if (drop.chance >= 100.0) text.add((if (drop.minAmount == drop.maxAmount) drop.minAmount.toString() + "" else drop.minAmount.toString() + " - " + drop.maxAmount) + " " + p.GetItemName(drop.id) + "(" + drop.id + ")")
                    }
                    /* All other drops! */for (drop in tempNpc.getData().drops) {
                        if (drop.chance < 100.0) {
                            val min = drop.minAmount
                            val max = drop.maxAmount
                            val itemId = drop.id
                            val chance = drop.chance
                            text.add((if (min == max) min.toString() + "" else "$min - $max") + " " + p.GetItemName(itemId) + "(" + itemId + ") " + chance + "% (1:" + Math.round(100.0 / chance) + ")" + if (drop.rareShout()) ", YELL" else "")
                            totalChance += chance
                        }
                    }
                    for (txt in text) {
                        p.sendString(txt, line)
                        line++
                        if (line == 8196) line = 12174
                    }
                    p.sendString(if (totalChance > 100.0) "You are currently " + (totalChance - 100.0) + " % over!" else "Total drops %: $totalChance%", 8145)
                    p.sendString(if (totalChance < 0.0 || totalChance >= 100.0) "" else "Nothing " + (100.0 - totalChance) + "%", line)
                    p.sendQuestSomething(8143)
                    p.showInterface(8134)
                    p.flushOutStream();
                } else p.sendMessage("Npc " + tempNpc.npcName() + " (" + npcId + ") has no assigned drops!")
            } else if (button == 3) {
                Server.npcManager.reloadDrops(p, npcId)
            } else if (button == 4) {
                tempNpc.showConfig(p)
            } else if (button == 5) {
                Server.npcManager.reloadAllData(p, npcId)
            }
            p.playerPotato.clear()
        }
        if (p.convoId == 0) {
            if (button == 1) {
                p.openUpBank()
            } else {
                p.nextDiag = 8
            }
        }
        if (p.NpcDialogue == 12) { //Slayer dialogue
            p.nextDiag = if (button == 1) 13 else if (button == 2) 31 else if (button == 4) 14 else 34
        }
        if (p.NpcDialogue == 32) { //Slayer dialogue
            if (button == 1) p.nextDiag = 33 else p.send(RemoveInterfaces())
        }
        if (p.NpcDialogue == 35) { //Slayer dialogue
            if (button == 1) {
                p.nextDiag = 36
            } else p.playerChat(614, "No, thank you.")
        }
        if (p.convoId == 2) {
            if (button == 1) {
                p.WanneShop = 39
            } else {
                p.send(RemoveInterfaces())
            }
        }
        if (p.convoId == 3) {
            if (button == 1) {
                p.WanneShop = 9
            } else {
                p.send(RemoveInterfaces())
            }
        }
        if (p.convoId == 4) {
            if (button == 1) {
                p.WanneShop = 22
            } else {
                p.send(RemoveInterfaces())
            }
        }

		 if (p.NpcDialogue == 163) {
             if (button == 1) p.showInterface(8292) else p.nextDiag = 164
         } else if (p.NpcDialogue == 164) {
             val type =
                 if (p.skillX == 3002 && p.skillY == 3931) 3 else if (p.skillX == 2547 && p.skillY == 3554) 2 else 1
             if (button == 1) p.teleportTo(if (type == 1) 2547 else 2474, if (type == 1) 3553 else 3438, 0)
             else if (button == 2) p.teleportTo(if (type == 3) 2547 else 3002, if (type == 3) 3553 else 3932, 0)
             p.send(RemoveInterfaces())
         } else if (p.NpcDialogue == 3649) {
             if (button == 1)
                 p.setTravelMenu();
             else if (button == 2)
                 p.playerChat(614, "No thank you.")
             doAction(p, -1)
         } else if (p.NpcDialogue == 2346) {
             if (button == 1) { //One time pay!
                 p.nextDiag = 2347
             } else if (button == 2) { //One time fee
                 if (!p.checkUnlock(0)) {
                     val maximumTickets = 10
                     val minimumTicket = 1
                     val ticketValue = 300000
                     var missing = (maximumTickets - minimumTicket) * ticketValue
                     if (!p.playerHasItem(621, minimumTicket)) {
                         p.npcChat(p.NpcTalkTo, 591, "You need a minimum of $minimumTicket ship ticket", "to unlock permanent!")
                         return
                     }
                     missing -= (p.getInvAmt(621) - minimumTicket) * ticketValue
                     if (missing > 0) {
                         if (p.getInvAmt(995) >= missing) {
                             p.deleteItem(621, if (p.getInvAmt(621) < maximumTickets) p.getInvAmt(621) else maximumTickets)
                             p.deleteItem(995, missing)
                             p.addUnlocks(0, p.checkUnlockPaid(0).toString() + "", "1")
                             p.npcChat(p.NpcTalkTo, 591, "Thank you for the payment.", "You may enter freely into my dungeon.")
                         } else p.npcChat(p.NpcTalkTo, 591, "You do not have enough coins to do this!", "You are currently missing "
                                     + (missing - p.getInvAmt(995)) + " coins or", ceil((missing - p.getInvAmt(995)) / 300000.0).toInt().toString() + " ship tickets.")
                     } else { //Using ship tickets as payment!
                         p.deleteItem(621, maximumTickets)
                         p.addUnlocks(0, p.checkUnlockPaid(0).toString() + "", "1")
                         p.npcChat(p.NpcTalkTo, 591, "Thank you for the ship tickets.", "You may enter freely into my dungeon.")
                     }
                 }
            } else if (button == 3) { //Nevermind!
                p.playerChat(614, "I do not want anything.")
            } else p.send(RemoveInterfaces())
        } else if (p.NpcDialogue == 2347) {
            if (p.checkUnlockPaid(0) > 0 || p.checkUnlock(0)) {
                p.npcChat(p.NpcTalkTo, 591, "You have already paid me.", "Please step into my dungeon.")
            } else if (button == 1) {
                if (p.getInvAmt(621) > 0 || p.getBankAmt(621) > 0) {
                    p.addUnlocks(0, "1", if (p.checkUnlock(0)) "1" else "0")
                    if (p.getInvAmt(621) > 0) p.deleteItem(621, 1) else p.deleteItemBank(621, 1)
                    p.npcChat(p.NpcTalkTo, 591, "You can now step into the dungeon.")
                } else p.npcChat(p.NpcTalkTo, 596, "You need a ship ticket to enter my dungeon!")
            } else if (button == 2) {
                val amount: Long = (p.getInvAmt(995) + p.getBankAmt(995)).toLong()
                val total = 300000
                if (amount >= total) {
                    p.addUnlocks(0, "1", if (p.checkUnlock(0)) "1" else "0")
                    val remain: Int = total - p.getInvAmt(995)
                    p.deleteItem(995, total)
                    if (remain > 0) p.deleteItemBank(995, remain)
                    p.npcChat(p.NpcTalkTo, 591, "You can now step into the dungeon.")
                } else p.npcChat(p.NpcTalkTo, 596, "You need atleast " + (300000 - amount) + " more coins to enter my dungeon!")
            }
        } else if (p.NpcDialogue == 2181) {
            if (button == 1) { //One time pay!
                p.nextDiag = 2182
            } else if (button == 2) { //One time fee
                if (!p.checkUnlock(1)) {
                    val maximumTickets = 20
                    val minimumTicket = 3
                    val ticketValue = 300000
                    var missing = (maximumTickets - minimumTicket) * ticketValue
                    if (!p.playerHasItem(621, minimumTicket)) {
                        p.npcChat(p.NpcTalkTo, 591, "You need a minimum of $minimumTicket ship ticket", "to unlock permanent!")
                        return
                    }
                    missing -= (p.getInvAmt(621) - minimumTicket) * ticketValue
                    if (missing > 0) {
                        if (p.getInvAmt(995) >= missing) {
                            p.deleteItem(621, if (p.getInvAmt(621) < maximumTickets) p.getInvAmt(621) else maximumTickets)
                            p.deleteItem(995, missing)
                            p.addUnlocks(1, p.checkUnlockPaid(1).toString() + "", "1")
                            p.npcChat(p.NpcTalkTo, 591, "Thank you for the payment.", "You may enter freely into my cave.")
                        } else p.npcChat(p.NpcTalkTo, 591, "You do not have enough coins to do this!", "You are currently missing "
                                        + (missing - p.getInvAmt(995)) + " coins or", ceil((missing - p.getInvAmt(995)) / 300000.0).toInt().toString() + " ship tickets.")
                    } else { //Using ship tickets as payment!
                        p.deleteItem(621, maximumTickets)
                        p.addUnlocks(1, p.checkUnlockPaid(1).toString() + "", "1")
                        p.npcChat(p.NpcTalkTo, 591, "Thank you for the ship tickets.", "You may enter freely into my cave.")
                    }
                }
            } else if (button == 3) { //Nevermind!
                p.playerChat(614, "I do not want anything.")
            } else p.send(RemoveInterfaces())
        } else if (p.NpcDialogue == 2182) {
            if (p.checkUnlockPaid(1) > 0 || p.checkUnlock(1)) {
                p.npcChat(p.NpcTalkTo, 591, "You have already paid me.", "Please step into my cave.")
            } else if (button == 1) {
                if (p.getInvAmt(621) > 0 || p.getBankAmt(621) > 0) {
                    p.addUnlocks(1, "1", if (p.checkUnlock(1)) "1" else "0")
                    if (p.getInvAmt(621) > 0) p.deleteItem(621, 1) else p.deleteItemBank(621, 1)
                    p.npcChat(p.NpcTalkTo, 591, "You can now step into the cave.")
                } else p.npcChat(p.NpcTalkTo, 596, "You need a ship ticket to enter my cave!")
            } else if (button == 2) {
                val amount: Long = (p.getInvAmt(995) + p.getBankAmt(995)).toLong()
                val total = 300000
                if (amount >= total) {
                    p.addUnlocks(1, "1", if (p.checkUnlock(1)) "1" else "0")
                    val remain: Int = total - p.getInvAmt(995)
                    p.deleteItem(995, total)
                    if (remain > 0) p.deleteItemBank(995, remain)
                    p.npcChat(p.NpcTalkTo, 591, "You can now step into the cave.")
                } else p.npcChat(p.NpcTalkTo, 596, "You need atleast " + (300000 - amount) + " more coins to enter my cave!")
            }
        } else if (p.NpcDialogue == 8053) {
            if (button == 1) {
                p.send(RemoveInterfaces())
                p.openUpShop(55)
                //TODO: Add reward shop
            } else p.send(RemoveInterfaces())
        } else if (p.NpcDialogue == 10000) {
            if (button == 1) {
                if (p.playerHasItem(6161) && p.playerHasItem(6159)) {
                    p.deleteItem(6159, 1)
                    p.deleteItem(6161, 1)
                    p.addItem(6128, 1)
                    p.playerChat(614, "I just made Rock-shell head.")
                } else p.playerChat(614, "I need the following items:", p.GetItemName(6161) + " and " + p.GetItemName(6159) + ".")
            } else if (button == 2) {
                if (p.playerHasItem(6157) && p.playerHasItem(6159) && p.playerHasItem(6161)) {
                    p.deleteItem(6157, 1)
                    p.deleteItem(6159, 1)
                    p.deleteItem(6161, 1)
                    p.addItem(6129, 1)
                    p.playerChat(614, "I just made Rock-shell body.")
                } else p.playerChat(614, "I need the following items:", p.GetItemName(6161) + ", " + p.GetItemName(6159) + " and " + p.GetItemName(6157) + ".")
            } else if (button == 3) {
                if (p.playerHasItem(6159) && p.playerHasItem(6157)) {
                    p.deleteItem(6157, 1)
                    p.deleteItem(6159, 1)
                    p.addItem(6130, 1)
                    p.playerChat(614, "I just made Rock-shell legs.")
                } else p.playerChat(614, "I need the following items:", p.GetItemName(6159) + " and " + p.GetItemName(6157) + ".")
            } else if (button == 4) {
                if (p.playerHasItem(6161) && p.playerHasItem(6159)) {
                    p.deleteItem(6159, 1)
                    p.deleteItem(6161, 1)
                    p.addItem(6145, 1)
                    p.playerChat(614, "I just made Rock-shell boots.")
                } else p.playerChat(614, "I need the following items:", p.GetItemName(6161) + " and " + p.GetItemName(6159) + ".")
            } else if (button == 5) {
                if (p.playerHasItem(6161, 2)) {
                    p.deleteItem(6161, 1)
                    p.deleteItem(6161, 1)
                    p.addItem(6151, 1)
                    p.playerChat(614, "I just made Rock-shell gloves.")
                } else p.playerChat(614, "I need two of " + p.GetItemName(6161) + ".")
            }
            p.NpcDialogueSend = true
        } else if (p.NpcDialogue == 536) {
            if (button == 1) {
                val amount: Long = (p.getInvAmt(536) + p.getInvAmt(537) + p.getBankAmt(536)).toLong()
                var amt = 5
                if (amount >= 5) {
                    while (amt > 0) {
                        run {
                            var slot = 0
                            while (slot < 28 && amt > 0) {
                                if (p.playerItems.get(slot) - 1 == 536) {
                                    p.deleteItem(536, slot, 1)
                                    amt--
                                }
                                slot++
                            }
                        }
                        for (slot in 0..27) {
                            if (p.playerItems.get(slot) - 1 == 537) {
                                val toDelete = if (p.playerItemsN.get(slot) >= amt) amt else p.playerItemsN.get(slot)
                                p.deleteItem(537, slot, toDelete)
                                amt -= toDelete
                                break
                            }
                        }
                        for (slot in p.bankItems.indices) {
                            if (p.bankItems[slot] - 1 == 536) {
                                p.bankItemsN[slot] -= amt
                                break
                            }
                        }
                        amt = 0
                    }
                    val agi = Agility(p)
                    agi.kbdEntrance()
                    p.sendMessage("You sacrifice 5 dragon bones!")
                } else p.sendMessage("You need to have 5 dragon bones to sacrifice!")
                p.send(RemoveInterfaces())
            } else p.send(RemoveInterfaces())
        } else if (p.NpcDialogue == 48054) {
            if (p.getInvAmt(621) < 1) {
                p.sendMessage("You need a ship ticket to unlock this travel!")
            } else if (button == 1) {
                val id =
                    if (p.actionButtonId == 48054) 4 else if (p.actionButtonId == 3056) 3 else p.actionButtonId - 3058
                if (!p.getTravel(id)) {
                    p.deleteItem(621, 1)
                    p.saveTravel(id)
                    p.sendMessage("You have now unlocked the travel!")
                } else p.sendMessage("You have already unlocked this travel!")
            }
            doAction(p, -1)
            p.setTravelMenu()
        }
        if (p.nextDiag > 0) {
            p.NpcDialogue = p.nextDiag
            p.NpcDialogueSend = false
            p.nextDiag = -1
        }
        if (p.NpcDialogue == 2) {
            if (button == 1) {
                doAction(p, 0)
                p.openUpBank()
            } else if (button == 2) {
                doAction(p, 0)
            }
        } else if (p.NpcDialogue == 4) { // Aubury
            if (button == 1) {
                doAction(p, 0)
                p.openUpShop(2)
            } else if (button == 2) {
                doAction(p, 5)
            }
        } else if (p.NpcDialogue == 1001) { // Aubury
            p.getOutputStream().createFrame(27)
        } else if (p.NpcDialogue == 22) { // Makeover Mage
            if (button == 1) {
                doAction(p, 23)
            } else if (button == 2) {
                doAction(p, 24)
            }
        } else if (p.NpcDialogue == 26) {
            if (button == 1) {
                p.specsOn = true
                p.sendMessage("You have enabled specials.")
                p.send(RemoveInterfaces())
                doAction(p, 0)
            } else if (button == 2) {
                p.specsOn = false
                p.sendMessage("You have disabled specials.")
                p.send(RemoveInterfaces())
                doAction(p, 0)
            }
        } else if (p.NpcDialogue == 27) {
            if  (button == 1) {
                p.yellOn = true
                p.sendMessage("You have enabled boss yell messages.")
                p.send(RemoveInterfaces())
                doAction(p, 0)
            } else if (button == 2) {
                p.yellOn = false
                p.sendMessage("You have disabled boss yell messages.")
                p.send(RemoveInterfaces())
                doAction(p, 0)
            }
        } else if (p.NpcDialogue == 9) { // mage arena
            if (p.determineCombatLevel() >= 80) {
                p.moveTo(3105, 3933, 0)
                p.send(RemoveInterfaces())
            } else {
                p.sendMessage("You need to be level 80 or above to enter the mage arena.")
                p.sendMessage("The skeletons at the varrock castle are a good place until then.")
            }
        }
    }

    fun doAction(p: Client, i: Int) {
        p.NpcDialogue = i
        p.NpcDialogueSend = false
    }