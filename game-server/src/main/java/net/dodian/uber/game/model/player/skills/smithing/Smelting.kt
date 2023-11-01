package net.dodian.uber.game.model.player.skills.smithing

import net.dodian.uber.game.model.entity.player.Client
import net.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces
import net.dodian.uber.game.model.player.packets.outgoing.SendSideTab
import net.dodian.uber.game.model.player.skills.Skills
import net.dodian.utilities.Range
import net.dodian.utilities.Utils


    fun startSmelt(id: Int, c: Client) {
        val amounts = intArrayOf(1, 5, 10, 28)
        var index = 0
        var index2 = 0
        for (i in Utils.buttons_smelting.indices) {
            if (id == Utils.buttons_smelting[i]) {
                index = i % 4
                index2 = i / 4
            }
        }
        c.smelt_id = Utils.smelt_bars[index2][0]
        c.smeltCount = amounts[index]
        c.smeltExperience = Utils.smelt_bars[index2][1] * 4
        c.smelting = true
        c.send(RemoveInterfaces())
    }

    fun smelt(id: Int, c: Client) {
        c.smelt_id = id
        c.smelting = true
        var smeltBarId = -1
        val removed = ArrayList<Int>()
        if (c.smeltCount < 1) {
            c.resetAction(true)
            return
        }
        c.smeltCount--
        when (id) {
            2349 -> if (c.playerHasItem(436) && c.playerHasItem(438)) {
                smeltBarId = 2349
                removed.add(436)
                removed.add(438)
            } else c.sendMessage("You need a tin and copper to do this.")

            2351 -> {
                if (c.getLevel(Skills.SMITHING) < 15) {
                    c.sendMessage("You need level 15 smithing to do this.")
                    return
                }
                if (c.playerHasItem(440)) {
                    val ran = Range(1, 100).value
                    val diff = (c.getLevel(Skills.SMITHING) + 1) / 4
                    if (ran <= 50 + diff) {
                        smeltBarId = 2351
                        removed.add(440)
                    } else {
                        smeltBarId = 0
                        removed.add(440)
                        c.sendMessage("You fail to refine the iron.")
                    }
                } else c.sendMessage("You need a iron ore to do this.")
            }

            2355 -> {
                if (c.getLevel(Skills.SMITHING) < 20) {
                    c.sendMessage("You need level 20 smithing to do this.")
                    return
                }
                if (c.playerHasItem(442, 1)) {
                    smeltBarId = 2355
                    removed.add(442)
                } else c.sendMessage("You need a silver ore to do this.")
            }

            2353 -> {
                if (c.getLevel(Skills.SMITHING) < 30) {
                    c.sendMessage("You need level 30 smithing to do this.")
                    return
                }
                if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                    smeltBarId = 2353
                    removed.add(440)
                    removed.add(453)
                    removed.add(453)
                } else c.sendMessage("You need a iron ore and 2 coal to do this.")
            }

            2357 -> {
                if (c.getLevel(Skills.SMITHING) < 40) {
                    c.sendMessage("You need level 40 smithing to do this.")
                    return
                }
                if (c.playerHasItem(444, 1)) {
                    smeltBarId = 2357
                    removed.add(444)
                } else c.sendMessage("You need a gold ore to do this.")
            }

            2359 -> {
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.sendMessage("You need level 55 smithing to do this.")
                    return
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smeltBarId = 2359
                    removed.add(447)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                } else c.sendMessage("You need a mithril ore and 3 coal to do this.")
            }

            2361 -> {
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.sendMessage("You need level 70 smithing to do this.")
                    return
                }
                if (c.playerHasItem(449) && c.playerHasItem(453, 4)) {
                    smeltBarId = 2361
                    removed.add(449)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                } else c.sendMessage("You need a adamantite ore and 4 coal to do this.")
            }

            2363 -> {
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.sendMessage("You need level 85 smithing to do this.")
                    return
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smeltBarId = 2363
                    removed.add(451)
                    var i = 0
                    while (i < 6) {
                        removed.add(453)
                        i++
                    }
                } else c.sendMessage("You need a runite ore and 6 coal to do this.")
            }
            else -> c.println("Unknown smelt: $id")
        }
        if (smeltBarId == -1) {
            c.resetAction()
            return
        }
        c.requestAnim(0x383, 0)
        for (removeId in removed) {
            c.deleteItem(removeId, 1)
        }
        if (smeltBarId > 0) {
            c.addItem(smeltBarId, 1)
            c.giveExperience(c.smeltExperience, Skills.SMITHING)
            c.triggerRandom(c.smeltExperience)
        }
    }

    fun superHeat(id: Int, c: Client) {
        c.resetAction(false)
        val removed = ArrayList<Int>()
        var smelt_barId = 0
        var fail = false
        when (id) {
            436, 438 -> if (c.playerHasItem(436) && c.playerHasItem(438)) {
                smelt_barId = 2349
                removed.add(436)
                removed.add(438)
            } else c.sendMessage("You need a tin and copper to do this.")

            440 -> if (c.playerHasItem(440) && !c.playerHasItem(453, 2)) {
                if (c.getLevel(Skills.SMITHING) < 15) {
                    c.sendMessage("You need level 15 smithing to do this.")
                    return
                }
                smelt_barId = 2351
                removed.add(440)
            } else if (c.playerHasItem(440) && c.playerHasItem(453, 2)) {
                if (c.getLevel(Skills.SMITHING) < 30) {
                    c.sendMessage("You need level 30 smithing to do this.")
                    return
                }
                smelt_barId = 2353
                removed.add(440)
                removed.add(453)
                removed.add(453)
            }

            444 -> {
                if (c.getLevel(Skills.SMITHING) < 40) {
                    c.sendMessage("You need level 40 smithing to do this.")
                    return
                }
                smelt_barId = 2357
                removed.add(444)
            }

            447 -> {
                if (c.getLevel(Skills.SMITHING) < 55) {
                    c.sendMessage("You need level 55 smithing to do this.")
                    return
                }
                if (c.playerHasItem(447) && c.playerHasItem(453, 3)) {
                    smelt_barId = 2359
                    removed.add(447)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                } else c.sendMessage("You need a mithril ore and 3 coal to do this.")
            }

            449 -> {
                if (c.getLevel(Skills.SMITHING) < 70) {
                    c.sendMessage("You need level 70 smithing to do this.")
                   return
                }
                if (c.playerHasItem(449) && c.playerHasItem(453, 4)) {
                    smelt_barId = 2361
                    removed.add(449)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                    removed.add(453)
                } else c.sendMessage("You need a adamantite ore and 4 coal to do this.")
            }

            451 -> {
                if (c.getLevel(Skills.SMITHING) < 85) {
                    c.sendMessage("You need level 85 smithing to do this.")
                    return
                }
                if (c.playerHasItem(451) && c.playerHasItem(453, 6)) {
                    smelt_barId = 2363
                    removed.add(451)
                    var i = 0
                    while (i < 6) {
                        removed.add(453)
                        i++
                    }
                } else c.sendMessage("You need a runite ore and 6 coal to do this.")
            }

            else -> fail = true
        }
        var xp = 0
        var i = 0
        while (i < Utils.smelt_bars.size && xp == 0) {
            if (Utils.smelt_bars[i][0] == smelt_barId) xp = Utils.smelt_bars[i][1] * 4
            i++
        }
        if (fail) {
            c.sendMessage("You can only use this spell on ores.")
            c.callGfxMask(85, 100)
        } else if (smelt_barId > 0 && xp > 0) {
            c.lastMagic = System.currentTimeMillis()
            c.requestAnim(725, 0)
            c.callGfxMask(148, 100)
            c.deleteRunes(intArrayOf(561), intArrayOf(1))
            for (removeId in removed) c.deleteItem(removeId, 1)
            c.addItem(smelt_barId, 1)
            c.giveExperience(xp, Skills.SMITHING)
            c.giveExperience(500, Skills.MAGIC)
        } else c.sendMessage("This give no xp?!")
        c.send(SendSideTab(6))
    }