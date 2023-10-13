package net.dodian.uber.game.model.player.skills;

import net.dodian.uber.game.Constants
import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces
import net.dodian.uber.game.model.player.packets.outgoing.SendString
import net.dodian.utilities.Utils
import java.util.*

var slot = 8720;

    fun showSkillMenu(skillID: Int, child: Int, player: Client) {
        if (player.currentSkill != skillID)
            player.send(RemoveInterfaces());

        repeat(80) {
            player.send(SendString("", slot))
            slot++
        }

        var skillName: String = Skills.getSkill(skillID).getName()
        skillName = skillName.substring(0, 1).uppercase(Locale.getDefault()) + skillName.substring(1)

        player.send(SendString(skillName, 8716))
        player.send(SendString("Close Window", 8812))

        if (skillID < 23) {
            player.changeInterfaceStatus(15307, false)
            player.changeInterfaceStatus(15304, false)
            player.changeInterfaceStatus(15294, false)
            player.changeInterfaceStatus(8863, false)
            player.changeInterfaceStatus(8860, false)
            player.changeInterfaceStatus(8850, false)
            player.changeInterfaceStatus(8841, false)
            player.changeInterfaceStatus(8838, false)
            player.changeInterfaceStatus(8828, false)
            player.changeInterfaceStatus(8825, true)
            player.changeInterfaceStatus(8813, true)
            player.send(SendString("", 8849))
        }

        if (skillID == Skills.ATTACK.id) {
            player.send(SendString("Attack", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Abyssal Whip",
                "Bronze",
                "Iron",
                "Steel",
                "Mithril",
                "Adamant",
                "Rune",
                "Unholy book",
                "Unholy blessing",
                "Granite longsword",
                "Obsidian weapon",
                "Dragon",
                "Skillcape")
            val s1 = arrayOf("1", "1", "1", "10", "20", "30", "40", "45", "45", "50", "55", "60", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(4151, 1291, 1293, 1295, 1299, 1301, 1303, 3842, 20223, 21646, 6523, 1305, 9747)
            player.setMenuItems(items)

        } else if (skillID == Skills.DEFENCE.id) {
            player.send(SendString("Defence", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Skeletal",
                "Bronze",
                "Iron",
                "Steel",
                "Mithril",
                "Splitbark",
                "Adamant",
                "Rune",
                "Ancient blessing",
                "Granite",
                "Obsidian",
                "Dragon",
                "Crystal shield (with 60 agility)",
                "Dragonfire shield",
                "Skillcape")
            val s1 = arrayOf("1", "1", "1", "10", "20", "20", "30", "40", "45", "50", "55", "60", "70", "75", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(6139, 1117, 1115, 1119, 1121, 3387, 1123, 1127, 20235, 10564, 21301, 3140, 4224, 11284, 9753)
            player.setMenuItems(items)

        } else if (skillID == Skills.STRENGTH.id) {
            player.send(SendString("Strength", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf("Unholy book", "Unholy blessing", "War blessing", "Granite maul", "Obsidian maul", "Skillcape")
            val s1 = arrayOf("45", "45", "45", "50", "55", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(3842, 20223, 20232, 4153, 6528, 9750)
            player.setMenuItems(items)

        } else if (skillID == Skills.HITPOINTS.id) {
            player.send(SendString("Hitpoints", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Shrimps (3 health)",
                "Rat meat (3 health)",
                "Bread (5 health)",
                "Thin snail (7 health)",
                "Trout (8 health)",
                "Salmon (10 health)",
                "Lobster (12 health)",
                "Swordfish (14 health)",
                "Monkfish (16 health)",
                "Shark (20 health)",
                "Sea Turtle (22 health)",
                "Manta Ray (24 health)")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            val items = intArrayOf(315, 2142, 2309, 3369, 333, 329, 379, 373, 7946, 385, 397, 391)
            player.setMenuItems(items)

        } else if (skillID == Skills.RANGED.id) {
            player.changeInterfaceStatus(8825, false)
            player.send(SendString("Bows", 8846))
            player.send(SendString("Armour", 8823))
            player.send(SendString("Misc", 8824))
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf(
                    "Oak bow",
                    "Willow bow",
                    "Maple bow",
                    "Yew bow",
                    "Magic bow",
                    "Crystal bow",
                    "Seercull" /*, "Twisted bow"*/)
                s1 = arrayOf("1", "20", "30", "40", "50", "65", "75" /*, "85"*/)
            } else if (child == 1) {
                s = arrayOf(
                    "Leather",
                    "Green dragonhide body (with 40 defence)",
                    "Green dragonhide chaps",
                    "Green dragonhide vambraces",
                    "Book of balance",
                    "Peaceful blessing",
                    "Honourable blessing",
                    "Blue dragonhide body (with 40 defence)",
                    "Blue dragonhide chaps",
                    "Blue dragonhide vambraces",
                    "Red dragonhide body (with 40 defence)",
                    "Red dragonhide chaps",
                    "Red dragonhide vambraces",
                    "Black dragonhide body (with 40 defence)",
                    "Black dragonhide chaps",
                    "Black dragonhide vambraces",
                    "Spined")
                s1 = arrayOf("1", "40", "40", "40", "45", "45", "45", "50", "50", "50", "60", "60",
                             "60", "70", "70", "70", "75")
            } else if (child == 2) {
                s = arrayOf(
                    "Bronze arrow",
                    "Iron arrow",
                    "Steel arrow",
                    "Mithril arrow",
                    "Adamant arrow",
                    "Rune arrow",
                    "Dragon arrow",
                    "Skillcape")
                s1 = arrayOf("1", "1", "10", "20", "30", "40", "60", "99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(intArrayOf(843, 849, 853, 857, 861, 4212, 6724 /*, 20997*/))
            else if (child == 1) player.setMenuItems(
                intArrayOf(1129, 1135, 1099, 1065, 3844, 20226, 20229, 2499, 2493, 2487, 2501, 2495,
                           2489, 2503, 2497, 2491, 6133))
            else if (child == 2) player.setMenuItems(intArrayOf(882, 884, 886, 888, 890, 892, 11212, 9756))

        } else if (skillID == Skills.MAGIC.id) {
            player.changeInterfaceStatus(8825, false)
            player.send(SendString("Spells", 8846))
            player.send(SendString("Armor", 8823))
            player.send(SendString("Misc", 8824))
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf(
                    "High Alch",
                    "Smoke Rush",
                    "Enchant Sapphire",
                    "Shadow Rush",
                    "Blood Rush",
                    "Enchant Emerald",
                    "Ice Rush",
                    "Smoke Burst",
                    "Superheat",
                    "Enchant Ruby",
                    "Shadow Burst",
                    "Enchant Diamond",
                    "Blood Burst",
                    "Enchant Dragonstone",
                    "Ice Burst",
                    "Smoke Blitz",
                    "Shadow Blitz",
                    "Blood Blitz",
                    "Ice Blitz",
                    "Smoke Barrage",
                    "Enchant Onyx",
                    "Shadow Barrage",
                    "Blood Barrage",
                    "Ice Barrage")
                s1 = arrayOf("1", "1", "7", "10", "20", "27", "30", "40", "43", "49", "50",
                             "57", "60", "68", "70", "74", "76", "80", "82", "86", "87", "88", "92", "94")
            } else if (child == 1) {
                s = arrayOf(
                    "Blue Mystic",
                    "White Mystic",
                    "Splitbark (with 20 defence)",
                    "Black Mystic",
                    "Holy book",
                    "Holy blessing",
                    "Infinity")
                s1 = arrayOf("1", "20", "20", "35", "45", "45", "50")
            } else if (child == 2) {
                s = arrayOf(
                    "Zamorak staff",
                    "Saradomin staff",
                    "Guthix staff",
                    "Ancient staff",
                    "Obsidian staff",
                    "Master wand",
                    "Skillcape")
                s1 = arrayOf("1", "1", "1", "25", "40", "50", "99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(
                intArrayOf(561, 565, 564, 565, 565, 564, 565, 565, 561, 564, 565, 564, 565, 564,
                           565, 565, 565, 565, 565, 565, 564, 565, 565, 565),
                intArrayOf(1, 1, 10, 1, 1, 10, 1, 1, 1, 10, 1, 10, 1, 10, 1, 1, 1, 1, 1, 1, 10, 1, 1, 1))
            else if (child == 1) player.setMenuItems(intArrayOf(4089, 4109, 3385, 4099, 3840, 20220, 6918))
            else if (child == 2) player.setMenuItems(intArrayOf(2417, 2415, 2416, 4675, 6526, 6914, 9762))

        } else if (skillID == Skills.WOODCUTTING.id) {
            player.send(SendString("Axes", 8846))
            player.send(SendString("Logs", 8823))
            player.send(SendString("Misc", 8824))
            player.changeInterfaceStatus(8825, false)
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf(
                    "Bronze Axe",
                    "Iron Axe",
                    "Steel Axe",
                    "Mithril Axe",
                    "Adamant Axe",
                    "Rune Axe",
                    "Dragon Axe")
                s1 = arrayOf("1", "1", "6", "21", "31", "41", "61")
            } else if (child == 1) {
                s = arrayOf("Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs")
                s1 = arrayOf("1", "15", "30", "45", "60", "75")
            } else if (child == 2) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(intArrayOf(1351, 1349, 1353, 1355, 1357, 1359, 6739))
            else if (child == 1) player.setMenuItems(intArrayOf(1511, 1521, 1519, 1517, 1515, 1513))
            else if (child == 2) player.setMenuItems(intArrayOf(9807))

        } else if (skillID == Skills.FIREMAKING.id) {
            player.send(SendString("Firemaking", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            player.changeInterfaceStatus(8825, false)
            slot = 8760
            val s: Array<String>
            val s1: Array<String>
            s = arrayOf("Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs", "Skillcape")
            s1 = arrayOf("1", "15", "30", "45", "60", "75", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot++)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot++)) }
            player.setMenuItems(intArrayOf(1511, 1521, 1519, 1517, 1515, 1513, 9804))
        } else if (skillID == Skills.FISHING.id) {
            player.send(SendString("Fishing", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Shrimps",
                "Anchovies",
                "Trout",
                "Salmon",
                "Lobster",
                "Swordfish",
                "Monkfish",
                "Shark",
                "Sea Turtle",
                "Manta Ray",
                "")
            val s1 = arrayOf("1", "15", "20", "30", "40", "50", "60", "70", "85", "95")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(317, 321, 335, 331, 377, 371, 7944, 383, 395, 389)
            player.setMenuItems(items)

        } else if (skillID == Skills.COOKING.id) {
            player.send(SendString("Cooking", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Shrimps",
                "Meat",
                "Bread",
                "Anchovies",
                "Thin snail",
                "Trout",
                "Salmon",
                "Lobster",
                "Swordfish",
                "Monkfish",
                "Shark",
                "Sea Turtle",
                "Manta Ray")
            val s1 = arrayOf("1", "1", "10", "15", "15", "20", "30", "40", "50", "60", "70", "85", "95")
            for (i in s.indices) {
                player.send(SendString(s[i], slot + i))
            }
            slot = 8720
            for (i in s1.indices) {
                player.send(SendString(s1[i], slot + i))
            }
            val items = intArrayOf(315, 2142, 2309, 319, 3369, 333, 329, 379, 373, 7946, 385, 397, 391)
            player.setMenuItems(items)

        } else if (skillID == Skills.FLETCHING.id) {
            player.send(SendString("Fletching", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Arrow Shafts",
                "Oak Shortbow",
                "Oak Longbow",
                "Willow Shortbow",
                "Willow Longbow",
                "Maple Shortbow",
                "Maple Longbow",
                "Yew Shortbow",
                "Yew Longbow",
                "Magic Shortbow",
                "Magic Longbow")
            val s1 = arrayOf("1", "20", "25", "35", "40", "50", "55", "65", "70", "80", "85")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(52, 54, 56, 60, 58, 64, 62, 68, 66, 72, 70)
            player.setMenuItems(items)

        } else if (skillID == Skills.CRAFTING.id) {
            player.changeInterfaceStatus(8827, true)
            player.send(SendString("Spinning", 8846))
            player.send(SendString("Armor", 8823))
            player.send(SendString("Jewelry", 8824))
            player.send(SendString("Other", 8827))
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf("Ball of wool", "Bow string", "2 tick stringing", "1 tick stringing")
                s1 = arrayOf("1", "10", "40", "70")
            } else if (child == 1) {
                s = arrayOf(
                    "Leather gloves",
                    "Leather boots",
                    "Leather cowl",
                    "Leather vambraces",
                    "Leather body",
                    "Leather chaps",
                    "Coif",
                    "Green d'hide vamb",
                    "Green d'hide chaps",
                    "Green d'hide body",
                    "Blue d'hide vamb",
                    "Blue d'hide chaps",
                    "Blue d'hide body",
                    "Red d'hide vamb",
                    "Red d'hide chaps",
                    "Red d'hide body",
                    "Black d'hide vamb",
                    "Black d'hide chaps",
                    "Black d'hide body")
                s1 = arrayOf("1", "7", "9", "11", "14", "18", "39", "50", "54", "58", "62",
                             "66", "70", "73", "76", "79", "82", "85", "88")
            } else if (child == 2) {
                s = arrayOf(
                    "Gold ring",
                    "Gold necklace",
                    "Gold bracelet",
                    "Gold amulet",
                    "Cut sapphire",
                    "Sapphire ring",
                    "Sapphire necklace",
                    "Sapphire bracelet",
                    "Sapphire amulet",
                    "Cut emerald",
                    "Emerald ring",
                    "Emerald necklace",
                    "Emerald bracelet",
                    "Emerald amulet",
                    "Cut ruby",
                    "Ruby ring",
                    "Ruby necklace",
                    "Ruby bracelet",
                    "Cut diamond",
                    "Diamond ring",
                    "Ruby amulet",
                    "Cut dragonstone",
                    "Dragonstone ring",
                    "Diamond necklace",
                    "Diamond bracelet",
                    "Cut onyx",
                    "Onyx ring",
                    "Diamond amulet",
                    "Dragonstone necklace",
                    "Dragonstone bracelet",
                    "Dragonstone amulet",
                    "Onyx necklace",
                    "Onyx bracelet",
                    "Onyx amulet")
                s1 = arrayOf("5", "6", "7", "8", "20", "20", "22", "23", "24", "27", "27",
                             "29", "30", "31", "34", "34", "40", "42", "43", "43", "50", "55",
                             "55", "56", "58", "67", "67", "70", "72", "74", "80", "82", "84", "90")
            } else if (child == 3) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(intArrayOf(1759, 1777)) else if (child == 1) player.setMenuItems(
                intArrayOf(1059, 1061, 1167, 1063, 1129, 1095, 1169, 1065, 1099, 1135, 2487,
                           2493, 2499, 2489, 2495, 2501, 2491, 2497, 2503))
            else if (child == 2) player.setMenuItems(
                intArrayOf(1635, 1654, 11069, 1692, 1607, 1637, 1656, 11072, 1694, 1605, 1639,
                           1658, 11076, 1696, 1603, 1641, 1660, 11085, 1601, 1643, 1698, 1615,
                           1645, 1662, 11092, 6573, 6575, 1700, 1664, 11115, 1702, 6577, 11130, 6581))
            else if (child == 3) player.setMenuItems(intArrayOf(9780))

        } else if (skillID == Skills.SMITHING.id) {
            player.changeInterfaceStatus(8827, true)
            player.changeInterfaceStatus(8828, true)
            player.changeInterfaceStatus(8838, true)
            player.changeInterfaceStatus(8841, true)
            player.changeInterfaceStatus(8850, true)
            player.send(SendString("Smelting", 8846))
            player.send(SendString("Bronze", 8823))
            player.send(SendString("Iron", 8824))
            player.send(SendString("Steel", 8827))
            player.send(SendString("Mithril", 8837))
            player.send(SendString("Adamant", 8840))
            player.send(SendString("Runite", 8843))
            player.send(SendString("Special", 8859))
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            var item = IntArray(0)
            var amt = IntArray(0)
            if (child == 0) {
                s = arrayOf(
                    "Bronze bar",
                    "Iron bar (" + (50 + (player.getLevel(Skills.SMITHING) + 1) / 4) + "% success)",
                    "Steel bar (2 coal & 1 iron ore)",
                    "Gold bar",
                    "Mithril bar (3 coal & 1 mithril ore)",
                    "Adamantite bar (4 coal & 1 adamantite ore)",
                    "Runite bar (6 coal & 1 runite ore)")
                s1 = arrayOf("1", "15", "30", "40", "55", "70", "85")
            } else if (child > 0 && child <= Constants.smithing_frame.size) {
                s = arrayOfNulls(Constants.smithing_frame[child - 1].size)
                s1 = arrayOfNulls(Constants.smithing_frame[child - 1].size)
                item = IntArray(Constants.smithing_frame[child - 1].size)
                amt = IntArray(Constants.smithing_frame[child - 1].size)
                for (i in s.indices) {
                    item[i] = Constants.smithing_frame[child - 1][i][0]
                    amt[i] = Constants.smithing_frame[child - 1][i][1]
                    s[i] = player.GetItemName(item[i])
                    s1[i] = Constants.smithing_frame[child - 1][i][2].toString()
                }
            } else if (child == Constants.smithing_frame.size + 1) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(intArrayOf(2349, 2351, 2353, 2357, 2359, 2361, 2363))
            else if (child > 0 && child <= Constants.smithing_frame.size) player.setMenuItems(item, amt)
            else if (child == Constants.smithing_frame.size + 1) player.setMenuItems(intArrayOf(9795))

        } else if (skillID == Skills.MINING.id) {
            player.send(SendString("Pickaxes", 8846))
            player.send(SendString("Ores", 8823))
            player.send(SendString("Misc", 8824))
            player.changeInterfaceStatus(8825, false)
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf(
                    "Bronze Pickaxe",
                    "Iron Pickaxe",
                    "Steel Pickaxe",
                    "Mithril Pickaxe",
                    "Adamant Pickaxe",
                    "Rune Pickaxe",
                    "Dragon Pickaxe")
                s1 = arrayOf("1", "1", "6", "21", "31", "41", "61")
            } else if (child == 1) {
                s = arrayOf(
                    "Rune essence",
                    "Copper ore",
                    "Tin ore",
                    "Iron ore",
                    "Coal",
                    "Gold ore",
                    "Mithril ore",
                    "Adamant ore",
                    "Runite ore")
                s1 = arrayOf("1", "1", "1", "15", "30", "40", "55", "70", "85")
            } else if (child == 2) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }

            if (child == 0) player.setMenuItems(intArrayOf(1265, 1267, 1269, 1273, 1271, 1275, 11920))
            else if (child == 1) player.setMenuItems(intArrayOf(1436, 436, 438, 440, 453, 444, 447, 449, 451))
            else if (child == 2) player.setMenuItems(intArrayOf(9792))

        } else if (skillID == Skills.HERBLORE.id) {
            player.send(SendString("Potions", 8846))
            player.send(SendString("Herbs", 8823))
            player.send(SendString("Misc", 8824))
            player.changeInterfaceStatus(8825, false)
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf(
                    "Attack Potion \\nGuam leaf & eye of newt",
                    "Strength Potion\\nTarromin & limpwurt root",
                    "Defence Potion\\nRanarr weed & white berries",
                    "Prayer potion\\nRanarr weed & snape grass",
                    "Super Attack Potion\\nIrit leaf & eye of newt",
                    "Super Strength Potion\\nKwuarm & limpwurt root",
                    "Super Restore Potion\\nSnapdragon & red spiders' eggs",
                    "Super Defence Potion\\nCadantine & white berries",
                    "Ranging Potion\\nDwarf weed & wine of Zamorak")
                s1 = arrayOf("3", "14", "25", "38", "46", "55", "60", "65", "75")
            } else if (child == 1) {
                val array = Array(2) { arrayOfNulls<String>(Utils.grimy_herbs.size) }
                for (i in array[1].indices) {
                    array[0][i] = player.GetItemName(if (Utils.grimy_herbs[i] != 3051 && Utils.grimy_herbs[i] != 3049) Utils.grimy_herbs[i] + 50 else Utils.grimy_herbs[i] - 51)
                    array[1][i] = Utils.grimy_herbs_lvl[i].toString()
                }
                s = array[0]
                s1 = array[1]
            } else if (child == 2) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            if (child == 0) player.setMenuItems(intArrayOf(121, 115, 133, 139, 145, 157, 3026, 163, 169))
            else if (child == 1) player.setMenuItems(Utils.grimy_herbs)
            else if (child == 2) player.setMenuItems(intArrayOf(9774))

        } else if (skillID == Skills.AGILITY.id) {
            player.send(SendString("Agility", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Gnome Course",
                "Barbarian Course",
                "Yanille castle wall",
                "Crystal Shield",
                "Taverly dungeon shortcut",
                "Wilderness course",
                "Prime boss shortcut",
                "Skillcape")
            val s1 = arrayOf("1", "40", "50", "60", "70", "70", "85", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(751, 1365, -1, 4224, 4155, 964, 4155, 9771)
            player.setMenuItems(items)

        } else if (skillID == Skills.THIEVING.id) {
            player.send(SendString("Thieving", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Cage",
                "Farmer",
                "Baker stall",
                "fur stall",
                "silver stall",
                "Master Farmer",
                "Yanille chest",
                "Spice Stall",
                "Legends chest",
                "Gem Stall")
            val s1 = arrayOf("1", "10", "10", "40", "65", "70", "70", "80", "85", "90")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(4443, 3243, 2309, 1739, 2349, 5068, 6759, 199, 6759, 1623)
            player.setMenuItems(items)

        } else if (skillID == Skills.RUNECRAFTING.id) {
            player.send(SendString("Runecrafting", 8846))
            player.changeInterfaceStatus(8825, false)
            player.changeInterfaceStatus(8813, false)
            slot = 8760
            val s = arrayOf(
                "Small pouch",
                "Nature rune",
                "Medium pouch",
                "Large pouch",
                "Blood rune",
                "Giant pouch",
                "Cosmic rune",
                "Skillcape")
            val s1 = arrayOf("1", "1", "20", "40", "50", "60", "75", "99")
            for (i in s.indices) { player.send(SendString(s[i], slot + i)) }
            slot = 8720
            for (i in s1.indices) { player.send(SendString(s1[i], slot + i)) }
            val items = intArrayOf(5509, 561, 5510, 5512, 565, 5514, 564, 9765)
            player.setMenuItems(items)
        } else if (skillID == Skills.SLAYER.id) {
            player.send(SendString("Master", 8846))
            player.send(SendString("Monsters", 8823))
            player.send(SendString("Misc", 8824))
            player.changeInterfaceStatus(8825, false)
            slot = 8760
            var s = arrayOfNulls<String>(0)
            var s1 = arrayOfNulls<String>(0)
            if (child == 0) {
                s = arrayOf("Mazchna (level 3 combat)", "Vannaka (level 3 combat)", "Duradel (level 50 combat)")
                s1 = arrayOf("1", "50", "50")
            } else if (child == 1) {
                s = arrayOf(
                    "Crawling hands",
                    "Pyrefiend",
                    "Albino bat",
                    "Death spawn",
                    "Jelly",
                    "Head mourner",
                    "Jungle horrors",
                    "Skeletal hellhound",
                    "Lesser demon",
                    "Bloodvelds",
                    "Greater demon",
                    "Black demon",
                    "Gargoyles",
                    "Cave horrors",
                    "Berserker Spirit",
                    "Aberrant Spectres",
                    "Tzhaar",
                    "Mithril Dragon",
                    "Abyssal demon",
                    "Dagannoth Prime")
                s1 = arrayOf("1", "20", "25", "30", "30", "45", "45", "50", "50", "53", "55", "60", "63", "65", "70", "73", "80", "83", "85", "90")
            } else if (child == 2) {
                s = arrayOf("Skillcape")
                s1 = arrayOf("99")
            }
            for (i in s.indices) {
                player.send(SendString(s[i], slot + i))
            }
            slot = 8720
            for (i in s1.indices) {
                player.send(SendString(s1[i], slot + i))
            }
            if (child == 0) player.setMenuItems(intArrayOf(4155, 4155, 4155))
            else if (child == 1) player.setMenuItems(intArrayOf(4133, 4138, -1, -1, 4142, -1, -1, -1, -1, 4141, -1, -1, 4147, 8900, -1, 4144, -1, -1, 4149, -1))
            else if (child == 2) player.setMenuItems(intArrayOf(9786))
        }

        player.sendQuestSomething(8717)
        if (player.currentSkill != skillID) player.showInterface(8714)
        player.currentSkill = skillID
    }