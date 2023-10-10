package net.dodian.uber.game.model.player.content.Minigames;

import net.dodian.cache.object.CacheObject;
import net.dodian.cache.object.ObjectLoader;
import net.dodian.uber.game.Server;
import net.dodian.uber.game.event.Event;
import net.dodian.uber.game.event.EventManager;
import net.dodian.uber.game.model.Position;
import net.dodian.uber.game.model.entity.npc.Npc;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.Player;
import net.dodian.uber.game.model.object.GlobalObject;
import net.dodian.uber.game.model.object.Object;
import net.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Misc;

/** ~Author: natsu Rune-server~  **/

public class Barrows {

    public static final int[][] barrowsBrothers = { { 20772, 1677 }, { 20721, 1676 }, { 20771, 1675 }, { 20722, 1674 }, { 20720, 1673 }, { 20770, 1672 } };

    public static final int[] Items = { 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759, 165, 117, 141, 129 };

    public static final int[][] Stackables = { { 4740, 30 }, { 564, 5 }, { 561, 15 }, { 565, 30 }, { 995, 5000 }, {386, 10} };

    public static final int[] BarrowsHeadRED = {4771,4767,4765,4763,4761};
    public static final int[] BarrowsHeadGREEN = {4772,4770,4768,4766,4764,4762};

    public static int randomBarrowsHeadRED() {
        return BarrowsHeadRED[(int) (Math.random() * BarrowsHeadRED.length)];
    }

    public static int randomBarrowsHeadGREEN() {
        return BarrowsHeadGREEN[(int) (Math.random() * BarrowsHeadGREEN.length)];
    }

    public static final int[] NpcSpawn = {2037,2033,2031,2035};

    public static int randomNpc() {
        return NpcSpawn[(int) (Math.random() * NpcSpawn.length)];
    }

    public void BarrowsEvent(Player player, Client client) {
        //player.getActionSender().sendItemOnInterface(4537, 100, player.getPosition().getZ() == 3 ? randomBarrowsHeadRED(): randomBarrowsHeadGREEN());
        int prayerLevel = client.getSkillLevel(Skills.PRAYER) - (Misc.random(12)+4);
        if (prayerLevel < 0)
        client.refreshSkill(Skills.PRAYER);
        EventManager.getInstance().registerEvent(new Event(0) {
            @Override
            public void execute() {
                //player.getActionSender().sendItemOnInterface(4537, 100, 7047);
                client.sendFrame246(4537, 100, 7047);
                stop();
            }

            @Override
            public void stop() {
            }

        });//, 3);
    }



    public static void BarrowsDoor(final int id1, final int id2, final int x1, final int y1, final int x2, final int y2, final int h) {
        EventManager.getInstance().registerEvent(new Event(2000) {

            @Override
            public void execute() {

                final Object o = new Object(id1, x1, y1, h, 10, 2, id1);
                if (!GlobalObject.addGlobalObject(o, 1000)) {
                    stop();
                }
                final Object o2 = new Object(id2, x2, y2, h, 10, 2, id2);
                if (!GlobalObject.addGlobalObject(o2, 1000)) {
                    stop();
                }
            }
        });

        final CacheObject g1 = ObjectLoader.object(id1, x1, y1, h);
        final CacheObject g2 = ObjectLoader.object(id2, x2, y2, h);
        //new GameObject(id1, x1, y1, h, g1.getRotation() - 3, 0, id1, 1, g1.getRotation(), x1, y1, false);
        //new GameObject(id2, x2, y2, h, g2.getRotation() + 3, 0, id2, 1, g2.getRotation(), x2, y2, false);
        //CycleEventHandler.getInstance().addEvent(player, new CycleEvent() {
        //}, 2);

    }


    public static void BarrowsDoor2(final int id1, final int id2, final int x1, final int y1, final int x2, final int y2, final int h) {
        final CacheObject g1 = ObjectLoader.object(id1, x1, y1, h);
        final CacheObject g2 = ObjectLoader.object(id2, x2, y2, h);
        //new GameObject(id1, x1, y1, h, g1.getRotation() - 1, 0, id1, 1, g1.getRotation(), x1, y1, false);
        //new GameObject(id2, x2, y2, h, g2.getRotation() + 1, 0, id2, 1, g2.getRotation(), x2, y2, false);
        //CycleEventHandler.getInstance().addEvent(player, new CycleEvent() {
        //}, 2);

        EventManager.getInstance().registerEvent(new Event(2000) {

            @Override
            public void execute() {

                final Object o = new Object(id1, x1, y1, h, 10, 2, id1);
                if (!GlobalObject.addGlobalObject(o, 1000)) {
                    stop();
                }
                final Object o2 = new Object(id2, x2, y2, h, 10, 2, id2);
                if (!GlobalObject.addGlobalObject(o2, 1000)) {
                    stop();
                }
            }
        });
    }
    public static void barrowsObject(Client client, int objectId) {

        switch (objectId) {
            case 20973: // chest
                client.sendAnimation(832);
                client.sendAnimation(426);
                getReward(client);
                /*if (player.getKillCount() >= 5 && !chest_open) {
                    for(int x3 = 0; x3 < barrowsBrothers.length; x3 = x3+1) {
                        if (NpcLoader.checkSpawn(player, barrowsBrothers[x3][1])) {
                            return false;
                        }*/
                        /*if (!player.getBarrowsNpcDead()[x3]) {
                            Server.npcManager.createNpc(barrowsBrothers[x3][1], new Position(client.getCurrentX(), client.getCurrentY()), 4);
                            return true;
                        }*/
                    //}
                //}
                /*if (player.getKillCount() == 6 && chest_open) {
                    getReward(client);
                    return true;
                }*/
                /*if(player.getKillCount() == 6){
                    chest_open = true;
                    client.sendAnimation(832);
                    client.sendAnimation(426);
                    chest(client);
                    return true;
                }*/
                break;

            //Chest Door West
            case 20689:
            case 20708:
                if(client.getPosition().getX() == 3540){
                    //client.getBarrowsPuzzel().RANDOM_PUZZEL(client);
                }
                if(client.getPosition().getX() == 3546 || client.getPosition().getX() == 3545) {
                    client.AddToCords(0, -7, 100);
                    BarrowsDoor2(20689, 6724, 3545, 9695, 3545, 9694, 0);
                }
                if(client.getPosition().getX() == 3541) {
                    BarrowsDoor(6724, 6743, 3541, 9695, 3541, 9694, 0);
                }
                break;

            //Chest Door East
            case 6725:
            case 6744:
                if(client.getPosition().getX() == 3563){
                    //player.getBarrowsPuzzel().RANDOM_PUZZEL(client);
                }
                if(client.getPosition().getX() == 3562) {
                    BarrowsDoor(6725, 6744, 3562, 9694, 3562, 9695, 0);
                }
                if(client.getPosition().getX() == 3558 || client.getPosition().getX() == 3557) {
                    BarrowsDoor2(6744, 6725, 3558, 9694, 3558, 9695, 0);
                }
                break;

            //Chest Door South
            case 6727:
            case 6746:
                if(client.getPosition().getY() == 9683){
                    //player.getBarrowsPuzzel().RANDOM_PUZZEL(client);
                }
                if(client.getPosition().getY() == 9684) {
                    BarrowsDoor(6727, 6746, 3551, 9684, 3552, 9684, 0);
                }
                if(client.getPosition().getY() == 9688 || client.getPosition().getY() == 9689) {
                    BarrowsDoor2(6746, 6727, 3551, 9688, 3552, 9688, 0);
                }
                break;

            //Chest Door North
            case 6720:
            case 6739:
                if(client.getPosition().getY() == 9706){
                    //player.getBarrowsPuzzel().RANDOM_PUZZEL(client);
                }
                if(client.getPosition().getY() == 9705) {
                    BarrowsDoor(6720, 6739, 3552, 9705, 3551, 9705, 0);
                }
                if(client.getPosition().getY() == 9700 || client.getPosition().getY() == 9701) {
                    BarrowsDoor2(6739, 6720, 3552, 9701, 3551, 9701, 0);
                }
                break;

            case 6721:
            case 6740:
                if(client.getPosition().getX() == 3557 || client.getPosition().getX() == 3558) {
                    BarrowsDoor(6721, 6740, 3558, 9712, 3558, 9711, 0);
                }
                if(client.getPosition().getX() == 3562 || client.getPosition().getX() == 3563) {
                    BarrowsDoor2(6740, 6721, 3562, 9712, 3562, 9711, 0);
                }
                break;

            case 6719:
            case 6738:
                if(client.getPosition().getX() == 3540 || client.getPosition().getX() == 3541) {
                    BarrowsDoor(6719, 6738, 3541, 9712, 3541, 9711, 0);
                }
                if(client.getPosition().getX() == 3546 || client.getPosition().getX() == 3545) {
                    BarrowsDoor2(6738, 6719, 3545, 9712, 3545, 9711, 0);
                }
                break;

            case 6730:
            case 6749:
                if(client.getPosition().getX() == 3557 || client.getPosition().getX() == 3558) {
                    BarrowsDoor(6730, 6749, 3558, 9678, 3558, 9677, 0);
                }
                if(client.getPosition().getX() == 3562 || client.getPosition().getX() == 3563) {
                    BarrowsDoor2(6749, 6730, 3562, 9678, 3562, 9677, 0);
                }
                break;

            case 6723:
            case 6742:
                if(client.getPosition().getY() == 9678 && client.getPosition().getX() == 3574 ||
                   client.getPosition().getY() == 9677 && client.getPosition().getX() == 3574 ||
                   client.getPosition().getY() == 9678 && client.getPosition().getX() == 3575 ||
                   client.getPosition().getY() == 9677 && client.getPosition().getX() == 3575) {

                    BarrowsDoor(6723, 6742, 3575, 9678, 3575, 9677, 0);
                }
                if(client.getPosition().getY() == 9712 && client.getPosition().getX() == 3574 ||
                   client.getPosition().getY() == 9711 && client.getPosition().getX() == 3574 ||
                   client.getPosition().getY() == 9711 && client.getPosition().getX() == 3575 ||
                   client.getPosition().getY() == 9712 && client.getPosition().getX() == 3575) {

                    BarrowsDoor(6723, 6742, 3575, 9712, 3575, 9711, 0);
                }
                break;

            case 6729:
            case 6748:
                if(client.getPosition().getX() == 3540 || client.getPosition().getX() == 3541) {
                    BarrowsDoor(6729, 6748, 3541, 9678, 3541, 9677, 0);
                }
                if(client.getPosition().getX() == 3545 || client.getPosition().getX() == 3546) {
                    BarrowsDoor2(6748, 6729, 3545, 9678, 3545, 9677, 0);
                }
                break;

            case 6736:
            case 6717:
                if(client.getPosition().getY() == 9712 && client.getPosition().getX() == 3529 ||
                   client.getPosition().getY() == 9711 && client.getPosition().getX() == 3529 ||
                   client.getPosition().getY() == 9712 && client.getPosition().getX() == 3528 ||
                   client.getPosition().getY() == 9711 && client.getPosition().getX() == 3528) {

                    BarrowsDoor(6717, 6736, 3528, 9711, 3528, 9712, 0);
                }
                if(client.getPosition().getY() == 9678 && client.getPosition().getX() == 3529 ||
                        client.getPosition().getY() == 9677 && client.getPosition().getX() == 3529 ||
                        client.getPosition().getY() == 9678 && client.getPosition().getX() == 3528 ||
                        client.getPosition().getY() == 9677 && client.getPosition().getX() == 3528) {

                    BarrowsDoor2(6736, 6717, 3528, 9678, 3528, 9677, 0);
                }
                break;

            case 6716:
            case 6735:
                if(client.getPosition().getY() == 9717 && client.getPosition().getX() == 3568 ||
                   client.getPosition().getY() == 9717 && client.getPosition().getX() == 3569 ||
                   client.getPosition().getY() == 9718 && client.getPosition().getX() == 3569 ||
                   client.getPosition().getY() == 9718 && client.getPosition().getX() == 3568) {

                    BarrowsDoor(6716, 6735, 3568, 9718, 3569, 9718, 0);
                }
                if(client.getPosition().getY() == 9717 && client.getPosition().getX() == 3534 ||
                   client.getPosition().getY() == 9717 && client.getPosition().getX() == 3535 ||
                   client.getPosition().getY() == 9718 && client.getPosition().getX() == 3534 ||
                   client.getPosition().getY() == 9718 && client.getPosition().getX() == 3535) {

                    BarrowsDoor(6716, 6735, 3534, 9718, 3535, 9718, 0);
                }
                break;

            case 6750:
            case 6731:
                if(client.getPosition().getY() == 9671 && client.getPosition().getX() == 3534 ||
                   client.getPosition().getY() == 9671 && client.getPosition().getX() == 3535 ||
                   client.getPosition().getY() == 9672 && client.getPosition().getX() == 3534 ||
                   client.getPosition().getY() == 9672 && client.getPosition().getX() == 3535) {

                    BarrowsDoor(6731, 6750, 3535, 9671, 3534, 9671, 0);
                }
                if(client.getPosition().getY() == 9671 && client.getPosition().getX() == 3568 ||
                   client.getPosition().getY() == 9671 && client.getPosition().getX() == 3569 ||
                   client.getPosition().getY() == 9672 && client.getPosition().getX() == 3568 ||
                   client.getPosition().getY() == 9672 && client.getPosition().getX() == 3569) {

                    BarrowsDoor2(6750, 6731, 3568, 9671, 3569, 9671, 0);
                }
                break;

            case 6726:
            case 6745:
                if(client.getPosition().getY() == 9683 || client.getPosition().getY() == 9684) {
                    BarrowsDoor(6726, 6745, 3534, 9684, 3535, 9684, 0);
                }
                if(client.getPosition().getY() == 9688 || client.getPosition().getY() == 9689) {
                    BarrowsDoor2(6745, 6726, 3534, 9688, 3535, 9688, 0);
                }
                break;

            case 6718:
            case 6737:
                if(client.getPosition().getY() == 9705 || client.getPosition().getY() == 9706) {
                    BarrowsDoor(6718, 6737, 3535, 9705, 3534, 9705, 0);
                }
                if(client.getPosition().getY() == 9701 || client.getPosition().getY() == 9700) {
                    BarrowsDoor2(6737, 6718, 3535, 9701, 3534, 9701, 0);
                }
                break;

            case 6722:
            case 6741:
                if(client.getPosition().getY() == 9705 || client.getPosition().getY() == 9706) {
                    BarrowsDoor(6722, 6741, 3569, 9705, 3568, 9705, 0);
                }
                if(client.getPosition().getY() == 9701 || client.getPosition().getY() == 9700) {
                    BarrowsDoor2(6741, 6722, 3569, 9701, 3568, 9701, 0);
                }
                break;

            case 6728:
            case 6747:
                if(client.getPosition().getY() == 9689 || client.getPosition().getY() == 9688) {
                    BarrowsDoor(6728, 6747, 3569, 9688, 3568, 9688, 0);
                }
                if(client.getPosition().getY() == 9684 || client.getPosition().getY() == 9683) {
                    BarrowsDoor2(6747, 6728, 3569, 9684, 3568, 9684, 0);
                }
                break;

            case 20772:
            case 20771:
            case 20770:
            case 20722:
            case 20721:
            case 20720:
                for (int x1 = 0; x1 < barrowsBrothers.length; x1++) {
                    if (objectId == barrowsBrothers[x1][0]) {
                        /*if (player.getKillCount() >= 5) {
                            Dialogues.startDialogue(player, 10001);
                            return true;
                        }*/
                        /*if (NpcLoader.checkSpawn(player, barrowsBrothers[x1][1])) {
                            client.send(new SendMessage("You must kill all brothers before searching this.."));
                            return true;
                        }*/
                        /*if (Client.getBarrowsNpcDead()[x1]) {
                            client.send(new SendMessage("You have already searched this sarcophagus."));
                            return true;
                        }*/
                        Server.npcManager.createNpc(barrowsBrothers[x1][1], new Position(client.getPosition().getX(), client.getPosition().getY() +1, 3), 4);
                        client.send(new SendMessage("You have disturbed " + client.GetNpcName(barrowsBrothers[x1][1]) + " from his slumber."));
                        return;
                    }
                }
                return;

            case 6710: // Ladder
            case 6711:
            case 6712:
            case 6709:
                client.teleportTo(3565, 3302, 0);
                return;
            case 20672: // verac stairs
                client.teleportTo(3556, 3298, 0);
                return;
            case 20671: // torag stairs
                client.teleportTo(3553, 3283, 0);
                break;
            case 20670: // karil stairs
                client.teleportTo(3565, 3276, 0);
                return;
            case 20669: // guthan stairs
                client.teleportTo(3578, 3284, 0);
                return;
            case 20668: // dharok stairs
                client.teleportTo(3574, 3298, 0);
                return;
            case 20667: // ahrim stairs
                client.teleportTo(3565, 3290, 0);
        }
    }

    public static void digCrypt(Client client) {
        if (client.inZone(3553, 3294, 3560, 3301)) {//Verac
            client.teleportTo(3578, 9706, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        } else if (client.inZone(3550, 3279, 3556, 3286)) { // torag
            client.teleportTo(3568, 9683, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        } else if (client.inZone(3562, 3285, 3569, 3292)) { // ahrims
            client.teleportTo(3557, 9703, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        } else if (client.inZone(3572, 3294, 3577, 3301)) { // dh
            client.teleportTo(3556, 9718, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        } else if (client.inZone(3573, 3279, 3581, 3285)) { // guthan
            client.teleportTo(3534, 9704, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        } else if (client.inZone(3562, 3272, 3569, 3279)) { // karil
            client.teleportTo(3546, 9684, 3);
            client.send(new SendMessage("You've broken into a crypt!"));
        }
    }

    public static void getReward(Client client) {
        final int number = Misc.randomMinusOne(Stackables.length);
        final int rune = Stackables[number][0];
        final int amount = Stackables[number][1];
        final int reward = Items[Misc.randomMinusOne(Items.length)];
        int kills = brotherKillCount(client);
        if (kills < 6) {
            return;
        }
        boolean getBarrows = Misc.random(648 - (kills * 100)) == 0;
        if (getBarrows) {
            if (client.freeSlots() == 1) {
                if (!client.playerHasItem(rune)) {
                    client.send(new SendMessage("You must have three empty spaces in order to take this loot."));
                    return;
                }
            } else if (client.freeSlots() < 1) {
                client.send(new SendMessage("You must have three empty spaces in order to take this loot."));
                return;
            }
            client.addItem(rune, 972 + Misc.random(351));
            client.addItem(reward, 1);
        } else {
            final int number2 = Misc.randomMinusOne(Stackables.length);
            final int rune2 = Stackables[number2][0];
            final int amount2 = Stackables[number2][1];
            if (client.freeSlots() < 1) {
                if (!client.playerHasItem(rune) || !client.playerHasItem(rune2)) {
                    client.send(new SendMessage("You must have three empty spaces in order to take this loot."));
                    return;
                }
            }
            if (client.freeSlots() == 1) {
                if (!client.playerHasItem(rune) && !client.playerHasItem(rune2)) {
                    client.send(new SendMessage("You must have three empty spaces in order to take this loot."));
                    return;
                }
            }
            client.addItem(rune, Misc.random(amount * kills) +1);
            client.addItem(rune2, Misc.random(amount2 * kills) +1);
        }
        //player.barrowsRuns++;
        client.send(new SendMessage("You loot the chest.."));
        resetBarrows(client);
        client.teleportTo(3565, 3302, 0);
    }

    private static int brotherKillCount(Player player) {
        int brotherKillCount = 0;
        /*for (boolean kill : player.getBarrowsNpcDead()) {
            if (kill) {
                brotherKillCount++;
            }
        }*/
        return brotherKillCount;
    }

    public static void resetBarrows(Player player) {
        for (int x = 0; x < Barrows.barrowsBrothers.length; x++) {
            //player.setBarrowsNpcDead(x, false);
        }
        //player.setKillCount(0);
        //player.setRandomGrave(Misc.random(5));
    }

    public static void handleDeath(Client client, Npc npc) {
        for (int x = 0; x < barrowsBrothers.length; x++) {
            /*if (npc.NpcId() == barrowsBrothers[x][1]) {
                player.setKillCount(client.getKillCount() + 1);
                client.send(new SendMessage("Kill count: " + client.getKillCount(), 4536));
                player.setBarrowsNpcDead(x, true);
                break;
            }*/
        }
    }
}