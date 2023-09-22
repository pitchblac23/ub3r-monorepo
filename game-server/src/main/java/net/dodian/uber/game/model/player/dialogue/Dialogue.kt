package net.dodian.uber.game.model.player.dialogue;

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.Frame171
import net.dodian.uber.game.model.player.packets.outgoing.NpcDialogueHead
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage
import net.dodian.uber.game.model.player.packets.outgoing.SendString
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.uber.game.model.player.skills.slayer.SlayerTask
import net.dodian.utilities.Misc
import java.text.NumberFormat
import java.util.*


class Dialogue {

    companion object {
        @JvmStatic
        fun UpdateNPCChat(player: Client) {
            val p = player;

            when (p.NpcDialogue) {
                1 -> {
                    p.sendFrame200(4883, 591)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Good day, how can I help you?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                }

                2 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("What would you like to say?", 2460))
                    p.send(SendString("I'd like to access my bank account, please.", 2461))
                    p.send(SendString("I'd like to check my PIN settings.", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                3 -> {
                    p.sendFrame200(4883, 591)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Do you want to buy some runes?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.nextDiag = 4
                }

                4 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("Select an Option", 2460))
                    p.send(SendString("Yes please!", 2461))
                    p.send(SendString("Oh it's a rune shop. No thank you, then.", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                5 -> {
                    p.sendFrame200(969, 974)
                    p.send(SendString(p.playerName, 970))
                    p.send(SendString("Oh it's a rune shop. No thank you, then.", 971))
                    p.send(SendString("Click here to continue", 972))
                    //p.send(PlayerDialogueHead(968))
                    p.sendFrame185(969)
                    p.sendFrame164(968)
                    p.NpcDialogueSend = true
                }

                6 -> {
                    p.sendFrame200(4888, 592)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                    p.send(SendString("Well, if you find somone who does want runes, please", 4890))
                    p.send(SendString("send them my way.", 4891))
                    p.send(SendString("Click here to continue", 4892))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                    p.sendFrame164(4887)
                    p.NpcDialogueSend = true
                }

                7 -> {
                    p.sendFrame200(4883, 591)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Well, if you find somone who does want runes, please send them my way.", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                }

                8 -> {
                    p.sendFrame200(4883, 591)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Pins have not been implemented yet", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                }

                9 -> {
                    p.sendFrame200(4883, 1597)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Select an Option", 2460))
                    p.send(SendString("Can you teleport me to the mage arena?", 2461))
                    p.send(SendString("Whats at the mage arena?", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                10 -> {
                    p.sendFrame200(4883, 804)
                    p.send(SendString(p.GetNpcName(804), 4884))
                    p.send(SendString(p.dMsg, 4885))
                    p.send(NpcDialogueHead(804, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                }

                1000 -> {
                    p.sendFrame200(4883, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo).replace("_", " "), 4884))
                    p.send(SendString("Hi there, what would you like to do?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.nextDiag = 1001
                }

                1001 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("What would you like to do?", 2460))
                    p.send(SendString("Gamble", 2461))
                    p.send(SendString("Nothing", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                1002 -> {
                    p.sendFrame200(4883, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo).replace("_", " "), 4884))
                    p.send(SendString("Cant talk right now!", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                }

                11 -> {
                    p.sendFrame200(4883, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Hi there noob, what do you want?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.nextDiag = 12
                }

                12 -> {
                    if (p.NpcTalkTo == 405 && (p.determineCombatLevel() < 50 || p.getLevel(Skills.SLAYER) < 50)) {
                        p.sendFrame200(4888, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                        p.send(SendString("You need 50 combat and slayer", 4890))
                        p.send(SendString("to be assign tasks from me!", 4891))
                        p.send(SendString("Click here to continue", 4892))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                        p.sendFrame164(4887)
                    } else if (p.NpcTalkTo == 403 && (!p.checkItem(989) || p.getLevel(Skills.SLAYER) < 50)) {
                        p.sendFrame200(4888, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                        p.send(SendString("You need a crystal key and 50 slayer", 4890))
                        p.send(SendString("to be assign tasks from me!", 4891))
                        p.send(SendString("Click here to continue", 4892))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                        p.sendFrame164(4887)
                    } else {
                        val taskName =
                            if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) ""
                            else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                        val slayerMaster = arrayOf("What would you like to say?",
                                                   "I'd like a task please",
                                                   if (taskName != "") "Cancel " + taskName.lowercase(Locale.getDefault()) + " task" else "No task to skip",
                                                   "I'd like to upgrade my slayer mask",
                                                   "Can you teleport me to west ardougne?")
                        p.showPlayerOption(slayerMaster)
                    }
                    p.NpcDialogueSend = true
                }

                13 -> {
                    if (p.NpcTalkTo == 405 && (p.determineCombatLevel() < 50 || p.getLevel(Skills.SLAYER) < 50)) {
                        p.sendFrame200(4888, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                        p.send(SendString("You need 50 combat and slayer", 4890))
                        p.send(SendString("to be assign tasks from me!", 4891))
                        p.send(SendString("Click here to continue", 4892))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                        p.sendFrame164(4887)
                    } else if (p.NpcTalkTo == 403 && (!p.checkItem(989) || p.getLevel(Skills.SLAYER) < 50)) {
                        p.sendFrame200(4888, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                        p.send(SendString("You need a crystal key and 50 slayer", 4890))
                        p.send(SendString("to be assign tasks from me!", 4891))
                        p.send(SendString("Click here to continue", 4892))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                        p.sendFrame164(4887)
                    } else {
                        if (p.NpcTalkTo == 402) {
                            val tasks = SlayerTask.mazchnaTasks(player)
                            if (tasks.isEmpty()) {
                                p.send(SendMessage("You cant get any task!"))
                                //break
                            }
                            if (p.getSlayerData().get(3) > 0) {
                                p.sendFrame200(4883, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                                p.send(SendString("You already have a task!", 4885))
                                p.send(SendString("Click here to continue", 4886))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                                p.sendFrame164(4882)
                                p.NpcDialogueSend = true
                                //break
                            }
                            val task = tasks[Misc.random(tasks.size - 1)]
                            if (task != null) {
                                val amt = task.assignedAmountRange.value
                                p.sendFrame200(4901, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4902))
                                p.send(SendString("You must go out and kill " + amt + " " + task.textRepresentation + "", 4903))
                                p.send(SendString("If you want a new task that's too bad", 4904))
                                p.send(SendString("Visit Dodian.net for a slayer guide", 4905))
                                p.send(SendString("", 4906))
                                p.send(SendString("Click here to continue", 4907))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4901))
                                p.sendFrame164(4900)
                                p.getSlayerData().set(0, p.NpcTalkTo)
                                p.getSlayerData().set(1, task.ordinal)
                                p.getSlayerData().set(2, amt)
                                p.getSlayerData().set(3, amt)
                            }
                        } else if (p.NpcTalkTo == 403) {
                            val tasks = SlayerTask.vannakaTasks(player)
                            if (tasks.isEmpty()) {
                                p.send(SendMessage("You cant get any task!"))
                                //break
                            }
                            if (p.getSlayerData().get(3) > 0) {
                                p.sendFrame200(4883, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                                p.send(SendString("You already have a task!", 4885))
                                p.send(SendString("Click here to continue", 4886))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                                p.sendFrame164(4882)
                                p.NpcDialogueSend = true
                                //break
                            }
                            val task = tasks[Misc.random(tasks.size - 1)]
                            if (task != null) {
                                val amt = task.assignedAmountRange.value
                                p.sendFrame200(4901, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4902))
                                p.send(SendString("You must go out and kill " + amt + " " + task.textRepresentation + "", 4903))
                                p.send(SendString("If you want a new task that's too bad", 4904))
                                p.send(SendString("Visit Dodian.net for a slayer guide", 4905))
                                p.send(SendString("", 4906))
                                p.send(SendString("Click here to continue", 4907))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4901))
                                p.sendFrame164(4900)
                                p.getSlayerData().set(0, p.NpcTalkTo)
                                p.getSlayerData().set(1, task.ordinal)
                                p.getSlayerData().set(2, amt)
                                p.getSlayerData().set(3, amt)
                            }
                        } else if (p.NpcTalkTo == 405) {
                            val tasks = SlayerTask.duradelTasks(player)
                            if (tasks.isEmpty()) {
                                p.send(SendMessage("You cant get any task!"))
                                //break
                            }
                            if (p.getSlayerData().get(3) > 0) {
                                p.sendFrame200(4883, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                                p.send(SendString("You already have a task!", 4885))
                                p.send(SendString("Click here to continue", 4886))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                                p.sendFrame164(4882)
                                p.NpcDialogueSend = true
                                //break
                            }
                            val task = tasks[Misc.random(tasks.size - 1)]
                            if (task != null) {
                                val amt = task.assignedAmountRange.value
                                p.sendFrame200(4901, p.npcFace)
                                p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4902))
                                p.send(SendString("You must go out and kill " + amt + " " + task.textRepresentation + "", 4903))
                                p.send(SendString("If you want a new task that's too bad", 4904))
                                p.send(SendString("Visit Dodian.net for a slayer guide", 4905))
                                p.send(SendString("", 4906))
                                p.send(SendString("Click here to continue", 4907))
                                p.send(NpcDialogueHead(p.NpcTalkTo, 4901))
                                p.sendFrame164(4900)
                                p.getSlayerData().set(0, p.NpcTalkTo)
                                p.getSlayerData().set(1, task.ordinal)
                                p.getSlayerData().set(2, amt)
                                p.getSlayerData().set(3, amt)
                            }
                        } else p.send(SendMessage("This npc do not wish to talk to you!"))
                    }
                    p.NpcDialogueSend = true
                }

                31 -> {
                    val taskName =
                        if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) "" else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                    val cost =
                        if (p.getSlayerData().get(0) == 403) 250000 else if (p.getSlayerData().get(0) == 405) 500000 else 100000 //Default 100k!
                    p.sendFrame200(4888, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4889))
                    p.send(SendString(if (taskName == "") "You do not have a task currently." else "I can cancel your " + taskName.lowercase(Locale.getDefault()) + " task", 4890))
                    p.send(SendString(if (taskName == "") "Talk to me to get one." else "task for $cost coins.", 4891))
                    p.send(SendString("Click here to continue", 4892))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4888))
                    p.sendFrame164(4887)
                    if (taskName != "") p.nextDiag = 32
                    p.NpcDialogueSend = true
                }

                32 -> {
                    val taskName =
                        if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) ""
                        else "" + SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1)).textRepresentation
                        if (taskName != "") p.showPlayerOption(arrayOf<String>("Do you wish to cancel your " + taskName.lowercase(Locale.getDefault()) + " task?", "Yes", "No"))
                    p.NpcDialogueSend = true
                }

                33 -> {
                    val taskName =
                        if (p.getSlayerData().get(0) == -1 || p.getSlayerData().get(3) <= 0) "" else "" + SlayerTask.slayerTasks.getTask(
                            p.getSlayerData().get(1)
                        ).textRepresentation
                    val cost =
                        if (p.getSlayerData().get(0) == 403) 250000 else if (p.getSlayerData().get(0) == 405) 500000 else 100000 //Default 100k!
                    val coinAmount: Int = p.getInvAmt(995)
                    if (coinAmount >= cost) {
                        p.deleteItem(995, cost)
                        p.getSlayerData().set(3, 0)
                        p.sendFrame200(4883, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                        p.send(SendString("I have now canceled your " + taskName.lowercase(Locale.getDefault()) + " task!", 4885))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                        p.sendFrame164(4882)
                        p.nextDiag = 12
                    } else {
                        p.sendFrame200(4883, p.npcFace)
                        p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                        p.send(SendString("You do not have enough coins to cancel your task!", 4885))
                        p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                        p.sendFrame164(4882)
                    }
                    p.send(SendString("Click here to continue", 4886))
                    p.NpcDialogueSend = true
                }

                34 -> {
                    if (!p.playerHasItem(8921)) p.showNPCChat(p.NpcTalkTo, 596, arrayOf<String>("You do not have a black mask!"))
                    else {
                        p.showNPCChat(p.NpcTalkTo, 591,
                            arrayOf<String>("Would you like to upgrade your black mask?",
                                            "It will cost you 2 million gold pieces."))
                        p.nextDiag = 35
                    }
                    p.NpcDialogueSend = true
                }

                35 -> p.showPlayerOption(arrayOf<String>("Do you wish to upgrade?", "Yes, please", "No, please"))
                36 -> {
                    p.showPlayerChat(arrayOf<String>("Yes, thank you."), 614)
                    p.NpcDialogueSend = true
                    p.nextDiag = 37
                }

                37 -> {
                    if (!p.playerHasItem(995, 2000000)) p.showNPCChat(p.NpcTalkTo, 596, arrayOf<String>("You do not have enough money!"))
                    else {
                        p.deleteItem(995, 2000000)
                        p.deleteItem(8921, 1)
                        p.addItem(11784, 1)
                        p.showNPCChat(p.NpcTalkTo, 592, arrayOf<String>("Here is your imbued black mask."))
                    }
                    p.NpcDialogueSend = true
                }

                14 -> {
                    p.sendFrame200(4883, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Be careful out there!", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.triggerTele(2542, 3306, 0, false)
                }

                15 -> {
                    if (p.getSlayerData().get(0) != -1) {
                        val slayerMaster: Int = p.getSlayerData().get(0)
                        var out = "Talk to me to get a new task!"
                        val checkTask = SlayerTask.slayerTasks.getTask(p.getSlayerData().get(1))
                        if (checkTask != null && p.getSlayerData().get(3) > 0) out =
                            "You need to kill " + p.getSlayerData().get(3) + " more " + checkTask.textRepresentation
                        p.sendFrame200(4883, p.npcFace)
                        p.send(SendString(p.GetNpcName(slayerMaster), 4884))
                        p.send(SendString(out, 4885))
                        p.send(SendString("Click here to continue", 4886))
                        p.send(NpcDialogueHead(slayerMaster, 4883))
                        p.sendFrame164(4882)
                    } else p.send(SendMessage("You have yet to get a task. Talk to a slayer master!"))
                    p.NpcDialogueSend = true
                }

                16 -> {
                    p.sendFrame200(4883, p.npcFace)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Would you like to buy some herblore supplies?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.nextDiag = 17
                }

                17 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("Would you like to buy herblore supplies?", 2460))
                    p.send(SendString("Sure, what do you have?", 2461))
                    p.send(SendString("No thanks", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                19 -> {
                    p.sendFrame200(4883, 591)
                    p.send(SendString(p.GetNpcName(p.NpcTalkTo), 4884))
                    p.send(SendString("Would you like to buy some supplies?", 4885))
                    p.send(SendString("Click here to continue", 4886))
                    p.send(NpcDialogueHead(p.NpcTalkTo, 4883))
                    p.sendFrame164(4882)
                    p.NpcDialogueSend = true
                    p.nextDiag = 20
                }

                20 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("Would you like to buy some supplies?", 2460))
                    p.send(SendString("Sure, what do you have?", 2461))
                    p.send(SendString("No thanks", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                21 -> {
                    p.showNPCChat(if (p.getGender() == 0) 1306 else 1307, 588,
                        arrayOf<String>("Hello there, would you like to change your looks?",
                                        "If so, it will be free of charge"))
                    p.NpcDialogueSend = true
                }

                22 -> {
                    p.showPlayerOption(
                        arrayOf<String>("Would you like to change your looks?",
                                        "Sure",
                                        "No thanks"))
                    p.NpcDialogueSend = true
                }

                23 -> {
                    p.showPlayerChat(arrayOf<String>("I would love that."), 614)
                    p.NpcDialogueSend = true
                }

                24 -> {
                    p.showPlayerChat(arrayOf<String>("Not at the moment."), 614)
                    p.NpcDialogueSend = true
                }

                25 -> {
                    //showInterface(3559);
                    p.sendFrame248(3559, 3213)
                    p.NpcDialogue = 0
                    p.NpcDialogueSend = false
                }

                26 -> {
                    p.showPlayerOption(
                        arrayOf<String>("What would you like to do?",
                                        "Enable specials",
                                        "Disable specials"))
                    p.NpcDialogueSend = true
                }

                27 -> {
                    p.showPlayerOption(
                        arrayOf<String>(
                            "What would you like to do?",
                            "Enable boss yell messages",
                            "Disable boss yell messages"))
                    p.NpcDialogueSend = true
                }

                162 -> {
                    p.showNPCChat(p.NpcTalkTo, 591,
                        arrayOf<String>(
                            "Fancy meeting you here maggot.",
                            "If you have any agility ticket,",
                            "I would gladly take them from you."))
                    p.nextDiag = 163
                    p.NpcDialogueSend = true
                }

                163 -> {
                    p.send(Frame171(1, 2465))
                    p.send(Frame171(0, 2468))
                    p.send(SendString("Trade in tickets or teleport to agility course?", 2460))
                    p.send(SendString("Trade in tickets.", 2461))
                    p.send(SendString("Another course, please.", 2462))
                    p.sendFrame164(2459)
                    p.NpcDialogueSend = true
                }

                164 -> {
                    val type =
                        if (p.skillX == 3002 && p.skillY == 3931) 3 else if (p.skillX == 2547 && p.skillY == 3554) 2 else 1
                    val type_gnome = arrayOf("Which course do you wish to be taken to?", "Barbarian", "Wilderness", "Stay here")
                    val type_barbarian = arrayOf("Which course do you wish to be taken to?", "Gnome", "Wilderness", "Stay here")
                    val type_wilderness = arrayOf("Which course do you wish to be taken to?", "Gnome", "Barbarian", "Stay here")
                    p.showPlayerOption(if (type == 3) type_wilderness else if (type == 2) type_barbarian else type_gnome)
                    p.NpcDialogueSend = true
                }

                536 -> {
                    p.showPlayerOption(arrayOf<String>("Do you wish to enter?", "Sacrifice 5 dragon bones", "Stay here"))
                    p.NpcDialogueSend = true
                }

                2345 -> {
                    if (!p.checkUnlock(0)) {
                        p.showNPCChat(p.NpcTalkTo, 591,
                            arrayOf<String>(
                                "Hello!",
                                "Are you looking to enter my dungeon?",
                                "You have to pay a to enter.",
                                "You can also pay a one time fee."))
                        p.nextDiag = ++p.NpcDialogue
                    } else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You can enter freely, no need to pay me anything.")
                    )
                    p.NpcDialogueSend = true
                }

                2346 -> {
                    if (!p.checkUnlock(0) && p.checkUnlockPaid(0) != 1) p.showPlayerOption(
                        arrayOf<String>("Select a option", "Enter fee", "Permanent unlock", "Nevermind"))
                    else if (p.checkUnlock(0)) p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You can enter freely, no need to pay me anything."))
                    else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You have already paid.", "Just enter the dungeon now."))
                    p.NpcDialogueSend = true
                }

                2182, 2347 -> {
                    p.showPlayerOption(arrayOf<String>("Select a option", "Ship ticket", "Coins"))
                    p.NpcDialogueSend = true
                }

                2180 -> {
                    if (!p.checkUnlock(1)) { p.showNPCChat(p.NpcTalkTo, 591,
                            arrayOf<String>(
                                "Hello!",
                                "Are you looking to enter my cave?",
                                "You have to pay a to enter.",
                                "You can also pay a one time fee."))
                        p.nextDiag = ++p.NpcDialogue
                    } else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You can enter freely, no need to pay me anything."))
                    p.NpcDialogueSend = true
                }

                2181 -> {
                    if (!p.checkUnlock(1) && p.checkUnlockPaid(1) != 1) p.showPlayerOption(arrayOf<String>("Select a option", "Enter fee", "Permanent unlock", "Nevermind"))
                    else if (p.checkUnlock(1)) p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You can enter freely, no need to pay me anything."))
                    else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You have already paid.", "Just enter the cave now."))
                    p.NpcDialogueSend = true
                }

                3648 -> {
                    p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("Hello dear.", "Would you like to travel?"))
                    p.nextDiag = 3649
                }

                3649 -> p.showPlayerOption(arrayOf<String>("Do you wish to travel?", "Yes", "No"))
                6481 -> {
                    if (p.totalLevel() >= Skills.maxTotalLevel()) p.showNPCChat(p.NpcTalkTo, 591,
                        arrayOf<String>("I see that you have trained up all your skills.", "I am utmost impressed!"))
                    else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You are quite weak!"))
                    p.nextDiag = p.NpcDialogue + 1
                    p.NpcDialogueSend = true
                }

                6482 -> {
                    if (p.totalLevel() >= Skills.maxTotalLevel()) {
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("Would you like to purchase this cape on my back?", "It will cost you 13.37 million coins."))
                        p.nextDiag = p.NpcDialogue + 1
                    } else
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("Come back when you have trained up your skills!"))
                    p.NpcDialogueSend = true
                }

                6483 -> {
                    p.showPlayerOption(arrayOf<String>("Purchase the max cape?", "Yes", "No"))
                    p.NpcDialogueSend = true
                }

                6484 -> {
                    val coins = 13370000
                    val freeSlot = if (p.getInvAmt(995) == coins) 1 else 2
                    if (p.freeSlots() < freeSlot) {
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("You need atleast " + (if (freeSlot == 1) "one" else "two") + " free inventory slot" + (if (freeSlot != 1) "s" else "") + "."))
                        p.nextDiag = p.NpcTalkTo
                    } else if (!p.playerHasItem(995, coins)) p.showNPCChat(p.NpcTalkTo, 591,
                        arrayOf<String>("You are missing " + NumberFormat.getNumberInstance().format((coins - p.getInvAmt(995)).toLong()) + " coins!"))
                    else {
                        p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("Here you go.", "Max cape just for you."))
                        p.deleteItem(995, coins)
                        p.addItem(13281, 1)
                        p.addItem(13280, 1)
                    }
                    p.NpcDialogueSend = true
                }

                8051 -> {
                    p.showNPCChat(p.NpcTalkTo, 591, arrayOf<String>("Happy Holidays adventurer!"))
                    p.nextDiag = 8052
                    p.NpcDialogueSend = true
                }

                8052 -> {
                    p.showNPCChat(
                        p.NpcTalkTo, 591,
                        arrayOf<String>(
                            "The monsters are trying to ruin the new year!",
                            "You must slay them to take back your gifts and",
                            "save the spirit of 2021!"))
                    p.nextDiag = 8053
                    p.NpcDialogueSend = true
                }

                8053 -> {
                    p.showPlayerOption(arrayOf<String>("Select a option", "I'd like to see your shop.", "I'll just be on my way."))
                    p.NpcDialogueSend = true
                }

                48054 -> {
                    p.showPlayerOption(arrayOf<String>("Unlock the travel?", "Yes", "No"))
                    p.NpcDialogueSend = true
                }

                10000 -> if (p.getLevel(Skills.SMITHING) >= 60 && p.playerHasItem(2347)) p.showPlayerOption(
                    arrayOf<String>("What would you like to make?", "Head", "Body", "Legs", "Boots", "Gloves")
                ) else {
                    p.send(SendMessage(if (p.getLevel(Skills.SMITHING) < 60) "You need level 60 smithing to do this." else "You need a hammer to handle this material."))
                    p.NpcDialogueSend = true
                }
            }
        }
    }
}