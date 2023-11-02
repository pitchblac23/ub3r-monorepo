package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.cache.object.GameObjectData;
import net.dodian.cache.object.GameObjectDef;
import net.dodian.uber.game.Constants;
import net.dodian.uber.game.Server;
import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.Position;
import net.dodian.uber.game.model.WalkToTask;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.PlayerHandler;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.object.*;
import net.dodian.uber.game.model.object.Object;
import net.dodian.uber.game.model.player.content.Minigames.Barrows;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.uber.game.model.player.skills.agility.Agility;
import net.dodian.uber.game.model.player.skills.farming.Farming;
import net.dodian.uber.game.model.player.skills.runecrafting.RunecraftingKt;
import net.dodian.uber.game.model.player.skills.smithing.Smithing;
import net.dodian.uber.game.model.player.skills.thieving.Thieving;
import net.dodian.uber.game.model.player.skills.woodcutting.Woodcutting;
import net.dodian.uber.game.party.Balloons;
import net.dodian.utilities.Misc;
import net.dodian.utilities.Utils;

import static net.dodian.uber.api.Animation.*;
import static net.dodian.uber.game.model.player.skills.mining.MiningKt.*;
import static net.dodian.utilities.DotEnvKt.getGameWorldId;

public class ClickObject implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int objectX = client.getInputStream().readSignedWordBigEndianA();
        int objectID = client.getInputStream().readUnsignedWord();
        int objectY = client.getInputStream().readUnsignedWordA();

        final WalkToTask task = new WalkToTask(WalkToTask.Action.OBJECT_FIRST_CLICK, objectID,
                new Position(objectX, objectY));
        GameObjectDef def = Misc.getObject(objectID, objectX, objectY, client.getPosition().getZ());
        GameObjectData object = GameObjectData.forId(task.getWalkToId());
        client.setWalkToTask(task);
        if (client.playerGroup >= 3 && object != null)
            client.sendMessage("Obj click1: " + object.getId() + ", " + object.getName() + ", Coord: " + objectX + ", " + objectY + ", " + (def == null ? "Def is null!" : def.getFace()));
        if (client.randomed) {
            return;
        }
        EventManager.getInstance().registerEvent(new Event(600) {

            @Override
            public void execute() {

                if (client.disconnected) {
                    this.stop();
                    return;
                }

                if (client.getWalkToTask() != task) {
                    this.stop();
                    return;
                }
                Position objectPosition = null;
                Object o = new Object(objectID, task.getWalkToPosition().getX(), task.getWalkToPosition().getY(), task.getWalkToPosition().getZ(), 10);
                if (def != null && !GlobalObject.hasGlobalObject(o)) {
                    objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(), object.getSizeX(def.getFace()), object.getSizeY(def.getFace()), client.getPosition().getZ());
                } else {
                    if (GlobalObject.hasGlobalObject(o)) {
                        objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(), object.getSizeX(o.face), object.getSizeY(o.type), o.z);
                    } else if (object != null) {
                        objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(), object.getSizeX(), object.getSizeY(), client.getPosition().getZ());
                    }
                }
                if (objectID == 23131)
                    objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), 3552, client.getPosition().getX(), client.getPosition().getY(), object.getSizeX(), object.getSizeY(), client.getPosition().getZ());
                if(objectID == 16466)
                    objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), 2972, client.getPosition().getX(), client.getPosition().getY(), 1, 3, client.getPosition().getZ());
                if (objectPosition == null)
                    return;
                atObject(client, task.getWalkToId(), task.getWalkToPosition(), object);
                Barrows.barrowsObject(client, objectID);
                RunecraftingKt.runeAltar(client, objectID);
                client.setWalkToTask(null);
                this.stop();
            }
        });
    }

    public void atObject(Client client, int objectID, Position position, GameObjectData obj) {
        String objectName = obj == null ? "" : obj.getName().toLowerCase();
        if (getGameWorldId() > 0) {
            client.println("atObject: " + position.getX() + "," + position.getY() + " objectID: " + objectID);
        }
        if (!client.validClient || client.randomed) {
            return;
        }
        Position pos = client.getPosition().copy();
        int xDiff = Math.abs(pos.getX() - position.getX());
        int yDiff = Math.abs(pos.getY() - position.getY());
        if (client.adding) {
            client.objects.add(new RS2Object(objectID, position.getX(), position.getY(), 1));
        }
        if (System.currentTimeMillis() < client.walkBlock || client.genie) {
            return;
        }
        client.resetAction(false);
        client.setFocus(position.getX(), position.getY());
        if (xDiff > 5 || yDiff > 5) {
            return;
        }
        /*if (objectID == client.farm.HarvestID) {
            client.farm.Harvest(client);
        }*/
        if (objectID == 8553) {
            if (client.playerHasItem(5341)) {
                if (Misc.goodDistance(position.getX(), position.getY(), client.getPosition().getX(), client.getPosition().getY(), 1)) {
                    client.requestAnim(FARMING_RAKING, 0);
                    EventManager.getInstance().registerEvent(new Event(2000) {
                        public void execute() {
                            Farming.updatePatches(8573, client);
                            client.addItem(6055, 1);
                            client.addItem(6055, 1);
                            client.addItem(6055, 1);
                            client.giveExperience(50, Skills.FARMING);
                            //needs to wait patch 8575 8574 8573
                            stop();
                        }
                    });
                    EventManager.getInstance().registerEvent(new Event(20000) {
                        public void execute() {
                            Farming.updatePatches(8553, client);
                            stop();
                        }
                    });
                }
            } else {
                client.sendMessage("Nothing interesting happens.");
            }
        }
        if (Balloons.lootBalloon(client, position.copy()) && objectID >= 115 && objectID <= 122) {
            return;
        }
        if (objectID == 26193) {
            Balloons.openInterface(client);
            return;
        }
        if (objectID == 26194) {
            Balloons.triggerPartyEvent(client);
            return;
        }
        if (objectID == 23271) {
            client.teleportToX = position.getX();
            client.teleportToY = position.getY() + (client.getPosition().getY() == 3523 ? -1 : 2);
        }
        if ((objectID == 6451 && client.getPosition().getY() == 9375) || (objectID == 6452 && client.getPosition().getY() == 9376)) {
            if (client.getPosition().getX() == 3305) {
                Agility agi = new Agility(client);
                agi.kbdEntrance();
            } else
                client.NpcDialogue = 536;
            return;
        }
        if (objectID == 20873) {
            Thieving.attemptSteal(client, objectID, position);
        }
        if (objectID == 2391 || objectID == 2392) {
            client.ReplaceObject(2728, 3349, 2391, 0, 0);
            client.ReplaceObject(2729, 3349, 2392, -2, 0);
            return;
        }
        if (objectID == 2097) {
            int type = -1;
            int[] possibleBars = {2349, 2351, 2353, 2359, 2361, 2363};
            for (int possibleBar : possibleBars)
                if (client.contains(possibleBar))
                    type = possibleBar;
            if (type != -1 && Smithing.CheckSmithing(type, client) != -1) {
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                Smithing.OpenSmithingFrame(Smithing.CheckSmithing(type, client), client);
            } else if (type == -1)
                client.sendMessage("You do not have any bars to smith.");
        }
        if (objectID == 1294) {
            client.teleportToX = 2485;
            client.teleportToY = 9912;
            client.newheightLevel = 0;
        }
        if (objectID == 17384 && position.getX() == 2892 && position.getY() == 3507) {
            client.teleportToX = 2893;
            client.teleportToY = 3507 + 6400;
            client.newheightLevel = 0;
        }
        if (objectID == 17384 && position.getX() == 2677 && position.getY() == 3405) {
            client.teleportToX = 2677;
            client.teleportToY = 9806;
            client.newheightLevel = 0;
        }
        if (objectID == 17385 && position.getX() == 2677 && position.getY() == 9805) {
            client.teleportToX = 2677;
            client.teleportToY = 3404;
            client.newheightLevel = 0;
        }
        if (objectID == 17387 && position.getX() == 2892 && position.getY() == 9907) {
            client.teleportToX = 2893;
            client.teleportToY = 3507;
            client.newheightLevel = 0;
        }
        if (objectID == 20877 && position.getX() == 2743 && position.getY() == 3153) {
            if (!client.checkUnlock(0) && client.checkUnlockPaid(0) != 1) {
                client.showNPCChat(2345, 596, new String[]{"You have not paid yet to enter my dungeon."});
                return;
            }
            client.addUnlocks(0, "0", client.checkUnlock(0) ? "1" : "0");
            client.teleportToX = 3748;
            client.teleportToY = 9373 + Misc.random(1);
            client.newheightLevel = 0;
            client.showNPCChat(2345, 592, new String[]{"Welcome to my dungeon."});
        }
        if (objectID == 5553 && position.getX() == 3749 && position.getY() == 9373) {
            client.teleportToX = 2744 + Misc.random(1);
            client.teleportToY = 3153;
            client.newheightLevel = 0;
            client.showNPCChat(2345, 593, new String[]{"Welcome back out from my dungeon."});
        }
        if (objectID == 6702 && position.getX() == 3749 && position.getY() == 9374) {
            client.teleportToX = 2744 + Misc.random(1);
            client.teleportToY = 3153;
            client.newheightLevel = 0;
            client.showNPCChat(2345, 593, new String[]{"Welcome back out from my dungeon."});
        }
        if (objectID == 14914) {
            if (!client.checkUnlock(1) && client.checkUnlockPaid(1) != 1) {
                client.showNPCChat(2180, 596, new String[]{"You have not paid yet to enter my cave."});
                return;
            }
            client.addUnlocks(1, "0", client.checkUnlock(1) ? "1" : "0");
            client.teleportToX = 2444;
            client.teleportToY = 5169;
            client.newheightLevel = 0;
            client.showNPCChat(2180, 592, new String[]{"Welcome to my cave."});
            client.GetBonus(true);
        }
        if (objectID == 2352 && position.getX() == 2443 && position.getY() == 5169) {
            client.teleportToX = 2848;
            client.teleportToY = 2991;
            client.newheightLevel = 0;
            client.showNPCChat(2180, 593, new String[]{"Welcome back out from my cave."});
            client.GetBonus(true);
        }
        if (objectID == 16466) {
            if (client.getLevel(Skills.AGILITY) < 75) {
                client.sendMessage("You need level 75 agility to use this shortcut!");
                return;
            }
            client.teleportToX = 2863;
            client.teleportToY = client.getPosition().getY() == 2971 ? 2976 : 2971;
            client.newheightLevel = 0;
        }
        if (objectID == 882 && position.getX() == 2899 && position.getY() == 9728) {
            if (client.getLevel(Skills.AGILITY) < 85) {
                client.sendMessage("You need level 85 agility to use this shortcut!");
                return;
            }
            client.teleportToX = 2885;
            client.teleportToY = 9795;
            client.newheightLevel = 0;
        }
        if (objectID == 882 && position.getX() == 2885 && position.getY() == 9794) {
            if (client.getLevel(Skills.AGILITY) < 85) {
                client.sendMessage("You need level 85 agility to use this shortcut!");
                return;
            }
            client.teleportToX = 2899;
            client.teleportToY = 9729;
            client.newheightLevel = 0;
        }
        if (objectID == 16509) {
            if (!client.checkItem(989) || client.getLevel(Skills.AGILITY) < 70) {
                client.sendMessage("You need a crystal key and 70 agility to use this shortcut!");
                return;
            }
            if (client.getPosition().getX() == 2886 && client.getPosition().getY() == 9799) {
                client.teleportToX = 2892;
                client.teleportToY = 9799;
            } else if (client.getPosition().getX() == 2892 && client.getPosition().getY() == 9799) {
                client.teleportToX = 2886;
                client.teleportToY = 9799;
            }
        }
        if (objectID == 16510) {
            if (!client.checkItem(989) || client.getLevel(Skills.AGILITY) < 70) {
                client.sendMessage("You need a crystal key and 70 agility to use this shortcut!");
                return;
            }
            if (client.getPosition().getX() == 2880 && client.getPosition().getY() == 9813) {
                client.teleportToX = 2878;
                client.teleportToY = 9813;
            } else if (client.getPosition().getX() == 2878 && client.getPosition().getY() == 9813) {
                client.teleportToX = 2880;
                client.teleportToY = 9813;
            }
        }
        if (objectID == 6847) {
            Thieving.attemptSteal(client, objectID, position);
            // 	client.addItem(4084, 1);
        }
        if (objectID == 133) { // new dragon teleport?
            client.teleportToX = 3235;
            client.teleportToY = 9366;
            client.sendMessage("Welcome to the dragon lair!");
        }
        if (objectID == 3994 || objectID == 11666 || objectID == 16469) {
            for (int fi = 0; fi < Utils.smelt_frame.length; fi++) {
                client.sendFrame246(Utils.smelt_frame[fi], 150, Utils.smelt_bars[fi][0]);
            }
            client.sendFrame164(24501);
        }
        if (objectID == 2309 && position.getX() == 2998 && position.getY() == 3917) {
            if (client.getLevel(Skills.AGILITY) < 75) {
                client.sendMessage("You need at least 75 agility to enter!");
                return;
            }
            client.ReplaceObject(2998, 3917, 2309, 2, 0);
            return;
        }
        if (objectID == 2624 || objectID == 2625) { //Heroes dungeon for runite rock.
            client.ReplaceObject(2901, 3510, 2624, -1, 0);
            client.ReplaceObject(2901, 3511, 2625, -3, 0);
            client.ReplaceObject(2902, 3510, -1, -1, 0);
            client.ReplaceObject(2902, 3511, -1, -3, 0);
            return;
        }
        if (objectID == 1516 && position.getX() == 2908 && position.getY() == 9698) {
            if (!client.checkItem(989)) {
                client.sendMessage("You need a crystal key to open this door.");
                return;
            }
            if (client.getLevel(Skills.SLAYER) < 90) {
                client.sendMessage("You need at least 90 slayer to enter!");
                return;
            }
            client.ReplaceObject(2908, 9698, -1, 0, 0);
            client.ReplaceObject(2907, 9698, -1, 0, 0);
            client.ReplaceObject(2908, 9697, 1516, 2, 0);
            client.ReplaceObject(2907, 9697, 1519, 0, 0);
            return;
        }
        if (objectID == 1519 && position.getX() == 2907 && position.getY() == 9698) {
            if (!client.checkItem(989)) {
                client.sendMessage("You need a crystal key to open this door.");
                return;
            }
            if (client.getLevel(Skills.SLAYER) < 90) {
                client.sendMessage("You need at least 90 slayer to enter!");
                return;
            }
            client.ReplaceObject(2908, 9698, -1, 0, 0);
            client.ReplaceObject(2907, 9698, -1, 0, 0);
            client.ReplaceObject(2908, 9697, 1516, 2, 0);
            client.ReplaceObject(2907, 9697, 1519, 0, 0);
            return;
        }
        if (objectID == 2623) {
            if (client.checkItem(989)) {
                client.ReplaceObject(2924, 9803, 2623, -3, 0);
            } else {
                client.sendMessage("You need the crystal key to enter");
                client.sendMessage("The crystal key is made from 2 crystal pieces");
            }
        }
        if (objectID == 16680 && position.getX() == 2884 && position.getY() == 3397) {
            if (client.getLevel(Skills.SLAYER) >= 50) {
                client.teleportToX = 2884;
                client.teleportToY = 9798;
            } else {
                client.sendMessage("You need 50 slayer to enter the Taverly Dungeon");
            }
        }
        if (objectID == 17385 && position.getX() == 2884 && position.getY() == 9797) {
            client.teleportToX = 2884;
            client.teleportToY = 3398;
        }
        if (objectID == 25939 && position.getX() == 2715 && position.getY() == 3470) {
            client.teleportToX = 2715;
            client.teleportToY = 3471;
            client.getPosition().setZ(0);
        }
        if (objectID == 25938 && position.getX() == 2715 && position.getY() == 3470) {
            client.teleportToX = 2714;
            client.teleportToY = 3470;
            client.getPosition().setZ(1);
        }
        if (objectID == 16683 && position.getX() == 2597 && position.getY() == 3107) {
            /*client.teleportToX = 2597;
            client.teleportToY = 3106;
            client.getPosition().setZ(1);*/
            client.teleportTo(2597, 3108, 1);
        }
        if (objectID == 16681 && position.getX() == 2597 && position.getY() == 3107) {
            client.teleportToX = 2597;
            client.teleportToY = 3106;
            client.getPosition().setZ(0);
        }
        if (objectID == 410 && position.getX() == 2925 && position.getY() == 3483) { //Guthix altar to cosmic
            client.requestAnim(645, 0);
            client.triggerTele(2162, 4833, 0, false);
            return;
        }
        if (objectID == 14847) {
            client.requestAnim(645, 0);
            client.triggerTele(2924, 3483, 0, false);
            return;
        }
        if (objectID == 1725) {
            client.stairs = "legendsUp".hashCode();
            client.skillX = position.getX();
            client.setSkillY(position.getY());
            client.stairDistance = 1;
        }
        if (objectID == 1725 && position.getX() == 2732 && position.getY() == 3377) {
            if (Utils.getDistance(client.getPosition().getX(), client.getPosition().getY(), position.getX(),
                    position.getY()) > 2) {
                return;
            }
            client.teleportToX = 2732;
            client.teleportToY = 3380;
            client.getPosition().setZ(1);
        }
        if (objectID == 1726 && position.getX() == 2732 && position.getY() == 3378) {
            if (Utils.getDistance(client.getPosition().getX(), client.getPosition().getY(), position.getX(),
                    position.getY()) > 2) {
                return;
            }
            client.teleportToX = 2732;
            client.teleportToY = 3376;
            client.getPosition().setZ(0);
        }
        if (objectID == 1726) {
            client.stairs = "legendsDown".hashCode();
            client.skillX = position.getX();
            client.setSkillY(position.getY());
            client.stairDistance = 1;
        }
        /* Agility */
        Agility agility = new Agility(client);
        if (objectID == 23145) {
            agility.GnomeLog();
            return;
        } else if (objectID == 23134 && client.distanceToPoint(position.getX(), position.getY()) < 2) {
            agility.GnomeNet1();
            return;
        } else if (objectID == 23559) {
            agility.GnomeTree1();
            return;
        } else if (objectID == 23557) {
            agility.GnomeRope();
            return;
        } else if (objectID == 23560 || objectID == 23561) {
            agility.GnomeTreebranch2();
            return;
        } else if (objectID == 23135 && client.distanceToPoint(position.getX(), position.getY()) < 3) {
            agility.GnomeNet2();
            return;
        } else if (objectID == 23138 && client.getPosition().getX() == 2484 && client.getPosition().getY() == 3430 && client.distanceToPoint(position.getX(), position.getY()) < 2) {
            agility.GnomePipe();
            return;
        } else if (objectID == 23139 && client.getPosition().getX() == 2487 && client.getPosition().getY() == 3430 && client.distanceToPoint(position.getX(), position.getY()) < 2) {
            agility.GnomePipe();
            return;
        } else if (objectID == 23137) {
            agility.WildyPipe();
            return;
        } else if (objectID == 23132) {
            agility.WildyRope();
            return;
        } else if (objectID == 23556) {
            agility.WildyStones();
            return;
        } else if (objectID == 23542) {
            agility.WildyLog();
            return;
        } else if (objectID == 23640) {
            agility.WildyClimb();
            return;
        } else if (objectID == 23131) {
            agility.BarbRope();
            return;
        } else if (objectID == 23144) {
            agility.BarbLog();
            return;
        } else if (objectID == 20211) {
            agility.BarbNet();
            return;
        } else if (objectID == 23547) {
            agility.BarbLedge();
            return;
        } else if (objectID == 16682) {
            agility.BarbStairs();
            return;
        } else if (objectID == 1948 && position.getX() == 2536 && position.getY() == 3553) {
            agility.BarbFirstWall();
            return;
        } else if (objectID == 1948 && position.getX() == 2539 && position.getY() == 3553) {
            agility.BarbSecondWall();
            return;
        } else if (objectID == 1948 && position.getX() == 2542 && position.getY() == 3553) {
            agility.BarbFinishWall();
            return;
        } else if (objectID == 23567) {
            agility.orangeBar();
        } else if (objectID == 23548) {
            agility.yellowLedge();
        }
        if (objectID == 1558 || objectID == 1557 && client.distanceToPoint(2758, 3482) < 5 && client.playerRights > 0) {
            client.ReplaceObject(2758, 3482, 1558, -2, 0);
            client.ReplaceObject(2757, 3482, 1557, 0, 0);
            client.sendMessage("Welcome to the Castle");
        }
        if (objectID == 2104) {
            objectID = 2105;
        }
        if (objectID == 2102) {
            objectID = 2103;
        }

        /*Mining Rocks*/
        for (int r = 0; r < Utils.rocks.length; r++) {
            if (objectID == Utils.rocks[r]) {
                if (client.getPositionName(client.getPosition()) == Client.positions.TZHAAR) {
                    client.sendMessage("You can not mine here or the Tzhaar's will be angry!");
                    return;
                }
                int pickaxe = findPick(client);
                if (pickaxe < 0) {
                    client.minePick = -1;
                    client.resetAction();
                    client.sendMessage("You need a pickaxe to mine this rock.");
                    return;
                } else if (client.getLevel(Skills.MINING) < Utils.rockLevels[r]) {
                    client.sendMessage("You need a Mining level of " + Utils.rockLevels[r] + " to mine this rock.");
                    return;
                } else if (!client.playerHasItem(-1)) {
                    client.sendMessage("There is not enough space in your inventory.");
                    client.resetAction(true);
                    return;
                }
                client.minePick = pickaxe;
                client.mineIndex = r;
                client.mining = true;
                client.lastAction = System.currentTimeMillis() + getMiningSpeed(client);
                client.requestAnim(getMiningEmote(Utils.picks[pickaxe]), 0);
                client.sendMessage("You swing your pick at the rock.");
                return;
            } else if (objectID == 7471) {
                int pickaxe = findPick(client);
                if (pickaxe < 0) {
                    client.minePick = -1;
                    client.resetAction();
                    client.sendMessage("You need a pickaxe to mine this rock.");
                    return;
                } else if (!client.playerHasItem(-1)) {
                    client.sendMessage("There is not enough space in your inventory.");
                    client.resetAction(true);
                    return;
                }
                client.minePick = pickaxe;
                client.mineIndex = r;
                client.miningEss = true;
                client.lastAction = System.currentTimeMillis() + getMiningSpeed(client);
                client.requestAnim(getMiningEmote(Utils.picks[pickaxe]), 0);
                client.sendMessage("You swing your pick at the rock.");
                return;
            }
        }
        if (client.mining) {
            return;
        }

        if (objectID == 2634 && position.getX() == 2838 && position.getY() == 3517) { //2838, 3517
            client.teleportToX = 2840;
            client.teleportToY = 3517;
            client.sendMessage("You jump to the other side of the rubble");
//      if (client.getLevel(Skill.MINING) < 40) {
//        client.send(new SendMessage("You need 40 mining to clear this rubble"));
//        return;
//      }
//      client.requestAnim(client.getMiningEmote(624), 0);
//      client.animationReset = System.currentTimeMillis() + 2000;
//      client.ReplaceObject2(2838, 3517, -1, -1, 11);
//      client.send(new SendMessage("You clear the rubble"));
        }
        if (objectID == 16680) {
            int[] x = {2845, 2848, 2848};
            int[] y = {3516, 3513, 3519};
            for (int c = 0; c < x.length; c++) {
                if (position.getX() == x[c] && position.getY() == y[c]) {
                    client.teleportToX = 2868;
                    client.teleportToY = 9945;
                    c = x.length;
                }
            }
        }
        if (objectID == 2107) {
            if (System.currentTimeMillis() - Server.TICK < 60000) {
                client.println("invalid timer");
                return;
            }
        }
        if (objectID == 2492) {
            client.teleportToX = 2591;
            client.teleportToY = 3087;
            client.getPosition().setZ(0);
            return;
        }
        if (objectID == 2158 || objectID == 2156) {
            client.triggerTele(2921, 4844, 0, false);
            return;
        }
        if (objectID == 733) {
            int chance = Misc.chance(100);
            if (chance <= 50) {
                client.sendMessage("You failed to cut the web!");
                return;
            }
            if (System.currentTimeMillis() - client.lastAction < 2000) {
                client.lastAction = System.currentTimeMillis();
                return;
            }
            final Object emptyObj = new Object(734, position.getX(), position.getY(), client.getPosition().getZ(), 10, 1, objectID);
            if (GlobalObject.addGlobalObject(emptyObj, 30000)) {
                return;
            }
            client.lastAction = System.currentTimeMillis();
            return;
        }
        if (objectID == 16520 || objectID == 16519) {
            if (client.getLevel(Skills.AGILITY) < 50) {
                client.sendMessage("You need level 50 agility to use this shortcut!");
                return;
            }
            if (position.getX() == 2575 && position.getY() == 3108) {
                client.teleportToX = 2575;
                client.teleportToY = 3112;
                client.getPosition().setZ(0);
            } else if (position.getX() == 2575 && position.getY() == 3111) {
                client.teleportToX = 2575;
                client.teleportToY = 3107;
                client.getPosition().setZ(0);
            }
            return;
        }
        if (objectID == 16664 && position.getX() == 2724 && position.getY() == 3374) {
            client.teleportToX = 2727;
            client.teleportToY = 9774;
            client.getPosition().setZ(0);
            return;
        }
        if (objectID == 2833) {
            if (position.getX() == 2544 && position.getY() == 3111) {
                client.teleportToX = 2544;
                client.teleportToY = 3112;
                client.getPosition().setZ(1);
            }
            return;
        }
        if (objectID == 12260) {
            if (client.playerRights < 1) {
                return;
            }
            if (position.getX() == 2542 && position.getY() == 3097) {
                client.teleportToX = 2462;
                client.teleportToY = 4359;
                client.getPosition().setZ(0);
            } else if (position.getX() == 2459 && position.getY() == 4354) {
                client.teleportToX = 2544;
                client.teleportToY = 3096;
                client.getPosition().setZ(0);
            }
            return;
        }
        if (objectID == 17122) {
            if (position.getX() == 2544 && position.getY() == 3111) {
                client.teleportToX = 2544;
                client.teleportToY = 3112;
                client.getPosition().setZ(0);
            }
            return;
        }
        if (objectID == 2796) {
            if (position.getX() == 2549 && position.getY() == 3111) {
                client.teleportToX = 2549;
                client.teleportToY = 3112;
                client.getPosition().setZ(2);
            }
            return;
        }
        if (objectID == 2797) {
            if (position.getX() == 2549 && position.getY() == 3111) {
                client.teleportToX = 2549;
                client.teleportToY = 3112;
                client.getPosition().setZ(1);
            }
            return;
        }
        if (objectID == 17384) {
            if (position.getX() == 2594 && position.getY() == 3085) {
                client.teleportToX = 2594;
                client.teleportToY = 9486;
                client.getPosition().setZ(0);
            }
            return;
        }
        if (objectID == 17385) {
            if (position.getX() == 2594 && position.getY() == 9485) {
                client.teleportToX = 2594;
                client.teleportToY = 3086;
                client.getPosition().setZ(0);
            }
            return;
        }
        if (objectID == 16665) {
            if (position.getX() == 2724 && position.getY() == 9774) {
                client.teleportToX = 2723;
                client.teleportToY = 3375;
                client.getPosition().setZ(0);
            } else if (position.getX() == 2603 && position.getY() == 9478) {
                client.teleportToX = 2606;
                client.teleportToY = 3079;
                client.getPosition().setZ(0);
            } else if (position.getX() == 2569 && position.getY() == 9522) {
                client.teleportToX = 2570;
                client.teleportToY = 3121;
                client.getPosition().setZ(0);
            }
            return;
        }

//    if (objectID == 377 && client.playerHasItem(605)) {
//      {
//
//        double roll = Math.random() * 100;
//        if (roll < 100) {
//          int[] items = { 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736,
//              4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759 };
//          int r = (int) (Math.random() * items.length);
//          client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
//          client.addItem(items[r], 1);
//          client.deleteItem(605, 1);
//          client.yell("[Server] - " + client.getPlayerName() + " has just received from the BKT chest a  "
//              + client.GetItemName(items[r]));
//        } else {
//          int coins = Utils.random(7000);
//          client.send(new SendMessage("You find " + coins + " coins inside the BKT chest"));
//          client.addItem(995, coins);
//        }
//
//      }
//      for (int p = 0; p < Constants.maxPlayers; p++) {
//        Client player = (Client) PlayerHandler.players[p];
//        if (player == null) {
//          continue;
//        }
//        if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
//            && !player.disconnected && Math.abs(player.getPosition().getY() - client.getPosition().getY()) < 30
//            && Math.abs(player.getPosition().getX() - client.getPosition().getX()) < 30 && player.dbId > 0) {
//          player.stillgfx(444, position.getY(), position.getX());
//        }
//      }
//    }
        if (objectID == 375 && position.getX() == 2593 && position.getY() == 3108 && client.getPosition().getZ() == 1) {
            if(client.chestEventOccur) {
                return;
            }
            if (client.getLevel(Skills.THIEVING) < 70) {
                client.sendMessage("You must be level 70 thieving to open this chest");
                return;
            }
            if (client.freeSlots() < 1) {
                client.sendMessage("you don't have enough inventory space to hold that item.");
                return;
            }
            if (System.currentTimeMillis() - client.lastAction < 1200) {
                client.lastAction = System.currentTimeMillis();
                return;
            }
            client.lastAction = System.currentTimeMillis();
            double roll = Math.random() * 100;
            if (roll <= 0.3) {
                int[] items = {2577, 2579, 2631, 10400, 10402, 10404, 10406, 10408,
                               10410, 10412, 10414, 10416, 10418, 12315, 12217, 10420,
                               10422, 10424, 10426, 10428, 10430, 10432, 10434, 10436,
                               10438, 12339, 12341};
                int r = (int) (Math.random() * items.length);
                client.sendMessage("You have received a " + client.GetItemName(items[r]) + "!");
                client.addItem(items[r], 1);
                client.yell("@bla@[@yel@Server@bla@] - @blu@" + client.getPlayerName() + " @or2@has received a " + client.GetItemName(items[r]).toLowerCase() +  " from a chest.");
            } else if (roll <= 2.5) {
                int natures = 50 + Utils.random(100);
                client.sendMessage("You find " + natures + " natures inside the chest.");
                client.addItem(561, natures);
            } else {
                int coins = 300 + Utils.random(1200);
                client.sendMessage("You find " + coins + " coins inside the chest.");
                client.addItem(995, coins);
            }
            if (client.getEquipment()[Equipment.Slot.HEAD.getId()] == 2631)
                client.giveExperience(300, Skills.THIEVING);
            client.chestEvent++;
            client.stillgfx(444, position.getY(), position.getX());
            client.triggerRandom(900);
            /*final Object o = new Object(6421, 2733, 3374, position.getZ(), 11, -1, objectID);
            if (!GlobalObject.addGlobalObject(o, 10000)) {
                GlobalObject.updateObject(client);
                return;
            }*/
        }
        if (objectID == 6420 && position.getX() == 2733 && position.getY() == 3374) {
            if(client.chestEventOccur) {
                return;
            }
            if (client.getLevel(Skills.THIEVING) < 85) {
                client.sendMessage("You must be level 85 thieving to open this chest.");
                return;
            }
            if (client.freeSlots() < 1) {
                client.sendMessage("You don't have enough inventory space to hold that item.");
                return;
            }
            if (System.currentTimeMillis() - client.lastAction < 1200) {
                client.lastAction = System.currentTimeMillis();
                return;
            }
            client.requestAnim(THIEVING_STALL, 0);
            EventManager.getInstance().registerEvent(new Event(600) {
                @Override
                public void execute() {
                    /*final Object object = new Object(6421, 2733, 3374, 0, 11, -1, 6420);
                    if (GlobalObject.addGlobalObject(object, 15000)) {
                        //GlobalObject.updateObject(client);
                        stop();
                    }*/
                    client.lastAction = System.currentTimeMillis();
                    double roll = Math.random() * 100;
                    if (roll <= 0.3) {
                        int[] items = {1038, 1040, 1042, 1044, 1046, 1048, 1050,
                                2581, 2631, 12343, 12345, 12347, 12349, 4565};
                        int r = (int) (Math.random() * items.length);
                        client.sendMessage("You have received a " + client.GetItemName(items[r]) + ".");
                        client.addItem(items[r], 1);
                        client.yell("@bla@[@yel@Server@bla@] - @blu@" + client.getPlayerName() + " @or2@has received a " + client.GetItemName(items[r]).toLowerCase() + " from the burnt chest.");
                    } else if (roll <= 2.5) {
                        int bloods = 100 + Utils.random(150);
                        client.sendMessage("You find " + bloods + " bloods inside the chest.");
                        client.addItem(565, bloods);
                    } else {
                        int coins = 500 + Utils.random(2000);
                        client.sendMessage("You find " + coins + " coins inside the chest");
                        client.addItem(995, coins);
                    }
                    if (client.getEquipment()[Equipment.Slot.HEAD.getId()] == 2631)
                        client.giveExperience(500, Skills.THIEVING);
                    client.chestEvent++;
                    client.stillgfx(444, position.getY(), position.getX());
                    client.triggerRandom(1500);
                    /*final Object object = new Object(6421, 2733, 3374, 0, 11, -1, 6420);
                    if (GlobalObject.addGlobalObject(object, 15000)) {
                        return;
                        TODO: needs fixing
                    }*/
                    stop();
                }
            });
        }
        if (System.currentTimeMillis() - client.lastDoor > 1000) {
            client.lastDoor = System.currentTimeMillis();
            for (int d = 0; d < DoorHandler.doorX.length; d++) {
                if (objectID == DoorHandler.doorId[d] && position.getX() == DoorHandler.doorX[d]
                        && position.getY() == DoorHandler.doorY[d]) {
                    int newFace;
                    if (DoorHandler.doorState[d] == 0) { // closed
                        newFace = DoorHandler.doorFaceOpen[d];
                        DoorHandler.doorState[d] = 1;
                        DoorHandler.doorFace[d] = newFace;
                    } else {
                        newFace = DoorHandler.doorFaceClosed[d];
                        DoorHandler.doorState[d] = 0;
                        DoorHandler.doorFace[d] = newFace;
                    }
                    //client.send(new Sound(326));
                    for (int p = 0; p < Constants.maxPlayers; p++) {
                        Client player = (Client) PlayerHandler.players[p];
                        if (player == null) {
                            continue;
                        }
                        if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
                                && !player.disconnected && player.getPosition().getY() > 0 && player.getPosition().getX() > 0
                                && player.dbId > 0) {
                            player.ReplaceObject(DoorHandler.doorX[d], DoorHandler.doorY[d], DoorHandler.doorId[d], newFace, 0);
                        }
                    }
                }
            }
        }
        if (objectID == 23140) {
            if (!client.checkItem(1544)) {
                client.sendMessage("You need a orange key to use this pipe!");
                return;
            }
            if (position.getX() == 2576 && position.getY() == 9506) {
                client.teleportToX = 2572;
                client.teleportToY = 9506;
            } else if (position.getX() == 2573 && position.getY() == 9506) {
                client.teleportToX = 2578;
                client.teleportToY = 9506;
            }
        }
        if (objectID == 23564) {
            client.teleportToX = 2621;
            client.teleportToY = 9496;
        }
        if (objectID == 15656) {
            client.teleportToX = 2614;
            client.teleportToY = 9505;
        }
        if(objectID == 409 || objectID == 412) {
            if(client.getCurrentPrayer() != client.getMaxPrayer()) {
                client.requestAnim(PRAY_AT_ALTAR, 0);
                client.pray(client.getMaxPrayer());
                client.sendMessage("You restore your prayer points!");
            } else client.sendMessage("You are at maximum prayer points!");
        }
        //if (objectID == 6836) {
        //  client.skillX = position.getX();
        //  client.setSkillY(position.getY());
        //  client.WanneThieve = 6836;
        // }
        if (objectID == 881) {
            client.getPosition().setZ(client.getPosition().getZ() - 1);
        }
        if (objectID == 1591 && position.getX() == 3268 && position.getY() == 3435) {
            if (client.determineCombatLevel() >= 80) {
                client.teleportToX = 2540;
                client.teleportToY = 4716;
            } else {
                client.sendMessage("You need to be level 80 or above to enter the mage arena.");
                client.sendMessage("The skeletons at the varrock castle are a good place until then.");
            }
        }
        if (objectID == 5960 && position.getX() == 2539 && position.getY() == 4712) {
            client.teleportToX = 3105;
            client.teleportToY = 3933;
        }

        // Wo0t Tzhaar Objects
        if (objectID == 9369 && (position.getX() == 2399) && (position.getY() == 5176)) {
            if (client.getPosition().getY() == 5177) {
                client.teleportToX = 2399;
                client.teleportToY = 5175;

            }
        }
        if (objectID == 9369 && (position.getX() == 2399) && (position.getY() == 5176)) {
            if (client.getPosition().getY() == 5175) {
                client.teleportToX = 2399;
                client.teleportToY = 5177;

            }
        }

        if (objectID == 9368 && (position.getX() == 2399) && (position.getY() == 5168)) {
            if (client.getPosition().getY() == 5169) {
                client.teleportToX = 2399;
                client.teleportToY = 5167;

            }
        }
        if (objectID == 9391 && (position.getX() == 2399) && (position.getY() == 5172)) // Tzhaar bank?
        {
            client.openUpBank();
        }
        if (objectName.toLowerCase().startsWith("bank") || objectName.toLowerCase().contains("bank"))
            client.openUpBank();
        if (objectID == 11833 && position.getX() == 2437 && position.getY() == 5166) // Jad entrance
        {
            client.teleportToX = 2413;
            client.teleportToY = 5117;
            client.sendMessage("You have entered the Jad Cave.");
        }
        if (objectID == 11834 && position.getX() == 2412 && position.getY() == 5118) // Jad exit
        {
            client.teleportToX = 2438;
            client.teleportToY = 5168;
            client.sendMessage("You have left the Jad Cave.");
        }
        // End of Tzhaar Objects

        if ((objectID == 2213) || (objectID == 2214) || (objectID == 3045) || (objectID == 5276)
                || (objectID == 6084)) {
            //System.out.println("Banking..");
            client.skillX = position.getX();
            client.setSkillY(position.getY());
            client.WanneBank = 1;
            client.WanneShop = -1;
        }
        // woodCutting
        // mining
        // if (actionTimer == 0) {
        if (Woodcutting.CheckObjectSkill(objectID, objectName, client)) {
            client.skillX = position.getX();
            client.setSkillY(position.getY());
        }
        /* Gnome Village stairs! */
        if (objectID == 16675 && position.getX() == 2488 && position.getY() == 3407) // spinning wheel stairs 1
        {
            client.getPosition().setZ(1);
            client.teleportToX = 2489;
            client.teleportToY = 3409;
        }
        if (objectID == 16677 && position.getX() == 2489 && position.getY() == 3408) // spinning wheel stairs 1
        {
            client.getPosition().setZ(0);
            client.teleportToX = 2488;
            client.teleportToY = 3406;
        }
        if (objectID == 16675 && position.getX() == 2485 && position.getY() == 3402) // spinning wheel stairs 2
        {
            client.getPosition().setZ(1);
            client.teleportToX = 2485;
            client.teleportToY = 3401;
        }
        if (objectID == 16677 && position.getX() == 2485 && position.getY() == 3402) // spinning wheel stairs 2
        {
            client.getPosition().setZ(0);
            client.teleportToX = 2485;
            client.teleportToY = 3404;
        }

        if (objectID == 16675 && position.getX() == 2445 && position.getY() == 3434) // Bank staircase 1
        {
            client.getPosition().setZ(1);
            client.teleportToX = 2445;
            client.teleportToY = 3433;
        }
        if (objectID == 16677 && position.getX() == 2445 && position.getY() == 3434) // Bank staircase 1
        {
            client.getPosition().setZ(0);
            client.teleportToX = 2446;
            client.teleportToY = 3436;
        }
        if (objectID == 16675 && position.getX() == 2444 && position.getY() == 3414) // Bank staircase 2
        {
            client.getPosition().setZ(1);
            client.teleportToX = 2445;
            client.teleportToY = 3416;
        }
        if (objectID == 16677 && position.getX() == 2445 && position.getY() == 3415) // Bank staircase 2
        {
            client.getPosition().setZ(0);
            client.teleportToX = 2444;
            client.teleportToY = 3413;
        }
        // go upstairs
            if (objectID == 1747) {
                client.stairs = 1;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 1738) {
                client.stairs = 1;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 2;
            } else if (objectID == 1734) {
                client.stairs = 10;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 3;
                Stairs.stairDistanceAdd = 1;
            } else if (objectID == 55) {
                client.stairs = 15;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 3;
                Stairs.stairDistanceAdd = 1;
            } else if (objectID == 57) {
                client.stairs = 15;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 3;
            } else if (objectID == 1755 || objectID == 5946 || objectID == 1757) {
                client.stairs = 4;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 2;
            } else if (objectID == 1764) {
                client.stairs = 12;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 2148) {
                client.stairs = 8;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 3608) {
                client.stairs = 13;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 2408) {
                client.stairs = 16;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 5055) {
                client.stairs = 18;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 5131) {
                client.stairs = 20;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 9359) {
                client.stairs = 24;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 2406) { /* Lost City Door */
                if (client.getEquipment()[Equipment.Slot.WEAPON.getId()] == 772) { // Dramen
                    // Staff
                    client.stairs = 27;
                    client.skillX = position.getX();
                    client.setSkillY(position.getY());
                    client.stairDistance = 1;
                }
            }
            // go downstairs
            if (objectID == 1746 || objectID == 1749) {
                client.stairs = 2;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 1740) {
                client.stairs = 2;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 1723) {
                client.stairs = 22;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 2;
                Stairs.stairDistanceAdd = 2;
            } else if (objectID == 16664) {
                if (position.getX() == 2603 && position.getY() == 3078) {
                    if (!client.checkItem(1543)) {
                        client.sendMessage("You need a red key to go down these stairs!");
                        return;
                    }
                    client.teleportToX = 2602;
                    client.teleportToY = 9479;
                    client.getPosition().setZ(0);
                } else if (position.getX() == 2569 && position.getY() == 3122) {
                    if (!client.checkItem(1545)) {
                        client.sendMessage("You need a yellow key to use this staircase!");
                        return;
                    }
                    client.teleportToX = 2570;
                    client.teleportToY = 9525;
                    client.getPosition().setZ(0);
                }
            } else if (objectID == 1992 && position.getX() == 2558 && position.getY() == 3444) {
//        if (client.playerHasItem(537, 50)) {
//          client.deleteItem(537, client.getItemSlot(537), 50);
//          client.send(new SendMessage("The gods accept your offer!"));
                client.teleportToX = 2717;
                client.teleportToY = 9820;
                client.getPosition().setZ(0);
//        } else {
//          client.send(new SendMessage("The gods require 50 dragon bones as a sacrifice!"));
//          return;
//        }
            } else if (objectID == 54) {
                client.stairs = 14;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 3;
                Stairs.stairDistanceAdd = 1;
            } else if (objectID == 56) {
                client.stairs = 14;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 3;
            } else if (objectID == 1568 || objectID == 5947 || objectID == 6434
                    || /* objectID == 1759 || */objectID == 1570) {
                if (position.getX() == 2594 && position.getY() == 3085)
                    return;
                client.stairs = 3;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 2113) { // Mining guild stairs
                if (client.getLevel(Skills.MINING) >= 60) {
                    client.stairs = 3;
                    client.skillX = position.getX();
                    client.setSkillY(position.getY());
                    client.stairDistance = 1;
                } else {
                    client.sendMessage("You need 60 mining to enter the mining guild.");
                }
            } else if (objectID == 492) {
                client.stairs = 11;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 2;
            } else if (objectID == 2147) {
                client.stairs = 7;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 5054) {
                client.stairs = 17;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 5130) {
                client.stairs = 19;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 9358) {
                client.stairs = 23;
                client.skillX = position.getX();
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 5488) {
                client.stairs = 28;
                client.setSkillX(position.getX());
                client.setSkillY(position.getY());
                client.stairDistance = 1;
            } else if (objectID == 9294) {
                if (position.getX() == 2879 && position.getY() == 9813) {
                    client.stairs = "trap".hashCode();
                    client.stairDistance = 1;
                    client.setSkillX(position.getX());
                    client.setSkillY(position.getY());
                }
            }
    }
}