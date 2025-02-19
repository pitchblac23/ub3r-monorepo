package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.entity.player.PlayerHandler;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.player.packets.Packet;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.uber.game.model.player.skills.prayer.Prayers;
import net.dodian.utilities.Utils;
import static net.dodian.uber.game.combat.ClientExtensionsKt.magicBonusDamage;

public class MagicOnPlayer implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int playerIndex = client.getInputStream().readSignedWordA();
        if (!(playerIndex >= 0 && playerIndex < PlayerHandler.players.length)
                || PlayerHandler.players[playerIndex] == null) {
            return;
        }
        int EnemyX3 = PlayerHandler.players[playerIndex].getPosition().getX();
        int EnemyY3 = PlayerHandler.players[playerIndex].getPosition().getY();
        Client castOnPlayer = (Client) PlayerHandler.players[playerIndex];
        int EnemyHP2 = castOnPlayer.getCurrentHealth();
        if (castOnPlayer == null) {
            return;
        }
        if (!client.canAttack) {
            client.sendMessage("You cannot attack your oppenent yet!");
            return;
        }
        int magicID = client.getInputStream().readSignedWordBigEndian();
        if (!client.GoodDistance(EnemyX3, EnemyY3, client.getPosition().getX(), client.getPosition().getY(), 5)) {
            return;
        }
        int diff = Math.abs(castOnPlayer.determineCombatLevel() - client.determineCombatLevel());
        if (!((castOnPlayer.inWildy() && diff <= client.wildyLevel && diff <= castOnPlayer.wildyLevel)
                || client.duelFight && client.duel_with == castOnPlayer.getSlot()) || castOnPlayer.saving) {
            client.sendMessage("You can't attack that player");
            return;
        }
        if (!(client.duelFight && client.duel_with == playerIndex) && !Server.pking) {
            client.sendMessage("Pking has been disabled");
            return;
        }
        if (client.duelFight && client.duelRule[2]) {
            client.sendMessage("Magic has been disabled for this duel!");
            return;
        }
        int wildLevel = client.getWildLevel();
        if ((playerIndex == client.duel_with && client.duelFight) || wildLevel > 0) {
            int slot = -1, type = 0;
            for (int i2 = 0; i2 < client.ancientId.length && slot == -1; i2++) {
                if(magicID == client.ancientId[i2]) {
                    slot = i2;
                    type = i2%4;
                }
            }
            if (System.currentTimeMillis() - client.lastAttack < client.coolDown[type]) {
                return;
            }
            if (client.getLevel(Skills.MAGIC) >= client.requiredLevel[slot]) {
                if (client.runeCheck()) {
                    int hitDiff = 0;
                    double extra = client.getLevel(Skills.MAGIC) * 0.195;
                    double critChance = client.getLevel(Skills.AGILITY) / 9;
                    boolean hitCrit = Math.random() * 100 <= critChance * (client.getEquipment()[Equipment.Slot.SHIELD.getId()] == 4224 ? 1.5 : 1);
                    client.deleteItem(565, 1);
                    double dmg = client.baseDamage[slot] * magicBonusDamage(client);
                    if(castOnPlayer.getPrayerManager().isPrayerOn(Prayers.Prayer.PROTECT_MAGIC)) dmg /= 2;
                    hitDiff = Utils.random((int) dmg);
                    hitDiff = hitCrit ? hitDiff + (int)(Utils.dRandom2((extra))) : hitDiff;
                    if (hitDiff >= EnemyHP2)
                        hitDiff = EnemyHP2;
                    client.requestAnim(1979, 0);
                    if(type == 2) { //Blood effect!
                        client.stillgfx(377, EnemyY3, EnemyX3);
                        client.heal(hitDiff/3);
                    } else if (type == 3) { //Freeze effect!
                        client.stillgfx(369, EnemyY3, EnemyX3);
                    } else
                        client.stillgfx(78, EnemyY3, EnemyX3);
                    client.lastAttack = System.currentTimeMillis();
                    castOnPlayer.target = client;
                    client.target = castOnPlayer;
                    client.setFocus(EnemyX3, EnemyY3);
                    castOnPlayer.dealDamage(hitDiff, hitCrit);
                }
            } else
                client.sendMessage("You need a magic level of " + client.requiredLevel[slot] + ".");
        } else
            client.sendMessage("You can't attack here!");
    }
}