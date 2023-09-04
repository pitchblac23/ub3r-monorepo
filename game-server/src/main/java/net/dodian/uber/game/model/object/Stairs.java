package net.dodian.uber.game.model.object;

import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.utilities.Utils;

public class Stairs {

    public static int[] EssenceMineX = {2893, 2921, 2911, 2926, 2899};
    public static int[] EssenceMineY = {4846, 4846, 4832, 4817, 4817};
    public static int[] EssenceMineRX = {3253, 3105, 2681, 2591};
    public static int[] EssenceMineRY = {3401, 9571, 3325, 3086};
    public static long stairBlock = 0;
    public static int stairDistanceAdd = 0;

    public static boolean stairs(int stairs, int teleX, int teleY, Client c) {
        if (stairBlock > System.currentTimeMillis()) {
            resetStairs(c);
            System.out.println(c.getPlayerName() + " stair blocked!");
            return false;
        }
        stairBlock = System.currentTimeMillis() + 1000;
        if (!c.IsStair) {
            c.IsStair = true;
            if (stairs == 1) {
                if (c.skillX == 2715 && c.skillY == 3470) {
                    if (c.getPosition().getY() < 3470 || c.getPosition().getX() < 2715) {
                        // resetStairs();
                        return false;
                    } else {
                        c.getPosition().setZ(1);
                        c.teleportToX = teleX;
                        c.teleportToY = teleY;
                        resetStairs(c);
                        return true;
                    }
                }
            }
            if (stairs == "legendsUp".hashCode()) {
                if (c.skillX == 2732 && c.skillY == 3377) {
                    c.getPosition().setZ(1);
                    c.teleportToX = 2732;
                    c.teleportToY = 3380;
                    resetStairs(c);
                    return true;
                }
            }
            if (stairs == "legendsDown".hashCode()) {
                if (c.skillX == 2732 && c.skillY == 3378) {
                    c.getPosition().setZ(0);
                    c.teleportToX = 2732;
                    c.teleportToY = 3376;
                    resetStairs(c);
                    return true;
                }
            }
            if (stairs == 1) {
                c.getPosition().setZ(c.getPosition().getZ() + 1);
            } else if (stairs == 2) {
                c.getPosition().setZ(c.getPosition().getZ() - 1);
            } else if (stairs == 21) {
                c.getPosition().setZ(c.getPosition().getZ() + 1);
            } else if (stairs == 22) {
                c.getPosition().setZ(c.getPosition().getZ() - 1);
            } else if (stairs == 69)
                c.getPosition().setZ(c.getPosition().getZ() + 1);
            c.teleportToX = teleX;
            c.teleportToY = teleY;
            if (stairs == 3 || stairs == 5 || stairs == 9) {
                c.teleportToY += 6400;
            } else if (stairs == 4 || stairs == 6 || stairs == 10) {
                c.teleportToY -= 6400;
            } else if (stairs == 7) {
                c.teleportToX = 3104;
                c.teleportToY = 9576;
            } else if (stairs == 8) {
                c.teleportToX = 3105;
                c.teleportToY = 3162;
            } else if (stairs == 11) {
                c.teleportToX = 2856;
                c.teleportToY = 9570;
            } else if (stairs == 12) {
                c.teleportToX = 2857;
                c.teleportToY = 3167;
            } else if (stairs == 13) {
                c.getPosition().setZ(c.getPosition().getZ() + 3);
                c.teleportToX = c.skillX;
                c.teleportToY = c.skillY;
            } else if (stairs == 15) {
                c.teleportToY += (6400 - (c.stairDistance + stairDistanceAdd));
            } else if (stairs == 14) {
                c.teleportToY -= (6400 - (c.stairDistance + stairDistanceAdd));
            } else if (stairs == 16) {
                c.teleportToX = 2828;
                c.teleportToY = 9772;
            } else if (stairs == 17) {
                c.teleportToX = 3494;
                c.teleportToY = 3465;
            } else if (stairs == 18) {
                c.teleportToX = 3477;
                c.teleportToY = 9845;
            } else if (stairs == 19) {
                c.teleportToX = 3543;
                c.teleportToY = 3463;
            } else if (stairs == 20) {
                c.teleportToX = 3549;
                c.teleportToY = 9865;
            } else if (stairs == 21) {
                c.teleportToY += (c.stairDistance + stairDistanceAdd);
            } else if (stairs == 69) {
                c.teleportToY = stairDistanceAdd;
                c.teleportToX = c.stairDistance;
            } else if (stairs == 22) {
                c.teleportToY -= (c.stairDistance + stairDistanceAdd);
            } else if (stairs == 23) {
                c.teleportToX = 2480;
                c.teleportToY = 5175;
            } else if (stairs == 24) {
                c.teleportToX = 2862;
                c.teleportToY = 9572;
            } else if (stairs == 25) {
                c.Essence = (c.getPosition().getZ() / 4);
                c.getPosition().setZ(0);
                c.teleportToX = EssenceMineRX[c.Essence];
                c.teleportToY = EssenceMineRY[c.Essence];
            } else if (stairs == 26) {
                int EssenceRnd = Utils.random3(EssenceMineX.length);

                c.teleportToX = EssenceMineX[EssenceRnd];
                c.teleportToY = EssenceMineY[EssenceRnd];
                c.getPosition().setZ((c.Essence * 4));
            } else if (stairs == 27) {
                c.teleportToX = 2453;
                c.teleportToY = 4468;
            } else if (stairs == 28) {
                c.teleportToX = 3201;
                c.teleportToY = 3169;
            }
            if (stairs == 5 || stairs == 10) {
                c.teleportToX += (c.stairDistance + stairDistanceAdd);
                c.teleportToY = c.getPosition().getY();
                c.getPosition().setZ(0);
            }
            if (stairs == 6 || stairs == 9) {
                c.teleportToX -= (c.stairDistance - stairDistanceAdd);
            }
        }
        resetStairs(c);
        return true;
    }

    public static boolean resetStairs(Client c) {
        c.stairs = 0;
        c.skillX = -1;
        c.setSkillY(-1);
        c.stairDistance = 1;
        stairDistanceAdd = 0;
        c.resetWalkingQueue();
        final Client p = c;
        EventManager.getInstance().registerEvent(new Event(500) {

            @Override
            public void execute() {
                p.resetWalkingQueue();
                stop();
            }
        });
        return true;
    }
}