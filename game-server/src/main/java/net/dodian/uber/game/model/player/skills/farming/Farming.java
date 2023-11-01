package net.dodian.uber.game.model.player.skills.farming;

import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Misc;

/** ~ Revision of Rune-Server ~ */
/*  * The only thing you need to do to add more plants is to add more configurations for the seed ID.
 *  * I have left the Potato seed and configurations to you as an example. */

public class Farming {

    public int[][] Settings;
    public int HarvestID = -1;
    public boolean seedWatered = false;
    public int patch = -1;


    public void Harvest(final Client c) {
        c.requestAnim(2291, 0);
        EventManager.getInstance().registerEvent(new Event(500) {
            public void execute() {
                if (patch <= 10) {
                    c.requestAnim(2291, 0);
                    int r = Misc.random(2);
                    if (r == 1 || r == 0) {
                        c.addItem(Settings[1][4], r);
                        c.giveExperience(Settings[1][5], Skills.FARMING);
                    }
                    patch++;
                } else {
                    updatePatches(8553, c);
                    Settings = null;
                    HarvestID = -1;
                    stop();
                }
            }
        });
        c.FarmingWatered = false;
    }

    public void digDead(final Client c) {
        c.requestAnim(2291, 0);
        EventManager.getInstance().registerEvent(new Event(500) {
            public void execute() {
                if (patch <= 5) {
                    c.requestAnim(2291, 0);
                    patch++;
                } else {
                    updatePatches(8553, c);
                    c.addItem(6055, 1);
                    stop();
                }
            }
        });
    }

    public int[][] getSettings(int seedID) {
        // { { stages, .., .., .. }, { lvl req, watered, dead, time between stages, harvestedItemID, harvestExp } };
        int ret[][] = new int[][]{};
        switch (seedID) {
            case 5318: // Potato
                ret = new int[][]{new int[]{8558, 8559, 8560, 8561, 8562},
                        {1, 8563, 8570, 15, 1942, 1942, 9}};
                return ret;
        }
        return null;
    }

    public boolean WaterSeed(final Client c) {
        if (!c.playerHasItem(5340, 1) || seedWatered) {
            return false;
        }

        c.requestAnim(2291, 0);
        EventManager.getInstance().registerEvent(new Event(0) {
            public void execute() {
                if (Settings == null) {
                    stop();
                    return;
                }
                updatePatches(Settings[1][1], c);
                seedWatered = true;
                stop();
            }
        });

        return false;
    }

    public void growCrop(final Client c, final int farmingLevel, final int seedID, final String[] messages) {

        Settings = getSettings(seedID);

        final int lvlReq, watered, dead, time;

        if (Settings != null) {
            lvlReq = Settings[1][0];
            watered = Settings[1][1];
            dead = Settings[1][2];
            time = Settings[1][3];
            HarvestID = Settings[0][Settings[0].length - 1];
        } else {
            c.sendMessage("Nothing interesting happens.");
            return;
        }

        if (!c.playerHasItem(5343, 1)) {
            Settings = null;
            HarvestID = 1;
            c.sendMessage("You need a seed dibber to plant seeds.");
            return;
        }

        if (farmingLevel < lvlReq) {
            c.sendMessage("You need a farming level of " + lvlReq + " to plant this.");
            return;
        }

        c.requestAnim(2291, 0);
        c.deleteItem(seedID, 1);
        EventManager.getInstance().registerEvent(new Event(1500) {
            public void execute() {
                updatePatches(Settings[0][0], c);
                stop();
            }
        });

        final int max = Settings[0].length;

        EventManager.getInstance().registerEvent(new Event(1000) {
            public void execute() {
                if (c == null || c.disconnected) {
                    stop();
                    return;
                }
                if (patch <= max) {
                    if (patch == 0) {
                        if (!seedWatered) {
                            patch = 0;
                            updatePatches(dead, c);
                            c.sendMessage(messages[1]);
                            stop();
                            return;
                        }
                        patch++;
                    }
                    if (patch > 0 && !seedWatered) {
                        stop();
                        return;
                    }
                    updatePatches(Settings[0][patch], c);
                    patch++;
                    if (patch == max) {
                        patch = 0;
                        c.sendMessage(messages[0]);
                        stop();
                    }
                } else {
                    patch = 0;
                    c.sendMessage(messages[0]);
                    stop();
                }
            }
        });
    }

    public static void updatePatches(int newPatch, Client c) {
        c.ReplaceObject(2805, 3461, newPatch, 0, 10);
        c.ReplaceObject(2806, 3461, newPatch, 0, 10);
        c.ReplaceObject(2805, 3460, newPatch, 0, 10);
        c.ReplaceObject(2806, 3460, newPatch, 0, 10);
        c.ReplaceObject(2807, 3460, newPatch, 0, 10);
        c.ReplaceObject(2808, 3460, newPatch, 0, 10);
        c.ReplaceObject(2809, 3460, newPatch, 0, 10);
        c.ReplaceObject(2810, 3460, newPatch, 0, 10);
        c.ReplaceObject(2811, 3460, newPatch, 0, 10);
        c.ReplaceObject(2812, 3460, newPatch, 0, 10);
        c.ReplaceObject(2813, 3460, newPatch, 0, 10);
        c.ReplaceObject(2814, 3460, newPatch, 0, 10);
        c.ReplaceObject(2805, 3459, newPatch, 0, 10);
        c.ReplaceObject(2806, 3459, newPatch, 0, 10);
        c.ReplaceObject(2807, 3459, newPatch, 0, 10);
        c.ReplaceObject(2808, 3459, newPatch, 0, 10);
        c.ReplaceObject(2809, 3459, newPatch, 0, 10);
        c.ReplaceObject(2810, 3459, newPatch, 0, 10);
        c.ReplaceObject(2811, 3459, newPatch, 0, 10);
        c.ReplaceObject(2812, 3459, newPatch, 0, 10);
        c.ReplaceObject(2813, 3459, newPatch, 0, 10);
        c.ReplaceObject(2814, 3459, newPatch, 0, 10);
    }
}