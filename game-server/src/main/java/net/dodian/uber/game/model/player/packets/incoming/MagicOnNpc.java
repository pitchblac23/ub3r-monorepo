package net.dodian.uber.game.model.player.packets.incoming;

import net.dodian.uber.game.Server;
import net.dodian.uber.game.model.entity.npc.Npc;
import net.dodian.uber.game.model.entity.player.Client;
import net.dodian.uber.game.model.item.Equipment;
import net.dodian.uber.game.model.player.packets.Packet;
import static net.dodian.uber.game.combat.ClientExtensionsKt.magicBonusDamage;
import static net.dodian.uber.game.combat.PlayerAttackCombatKt.*;
import net.dodian.uber.game.model.player.skills.Skills;
import net.dodian.utilities.Utils;

public class MagicOnNpc implements Packet {

    @Override
    public void ProcessPacket(Client client, int packetType, int packetSize) {
        int npcIndex = client.getInputStream().readSignedWordBigEndianA();
        Npc tempNpc = Server.npcManager.getNpc(npcIndex);
        if (tempNpc == null) { //No null npcs here!
            return;
        }
        int magicID = client.getInputStream().readSignedWordA();
        int id = tempNpc.getId();
        int EnemyX2 = tempNpc.getPosition().getX();
        int EnemyY2 = tempNpc.getPosition().getY();
        int EnemyHP2 = tempNpc.getCurrentHealth();

        if (!client.GoodDistance(EnemyX2, EnemyY2, client.getPosition().getX(), client.getPosition().getY(), 5)) {
            return;
        }
        if (EnemyHP2 < 1 || client.deathTimer > 0 || !canAttackNpc(client, id)) {
            if(EnemyHP2 < 1 || client.deathTimer > 0)
                client.sendMessage("That monster has already been killed!");
        } else {
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
                    double hit = client.blackMaskImbueEffect(type) ? 1.2 * dmg : dmg;
                    hitDiff = Utils.random((int) hit);
                    hitDiff = hitCrit ? hitDiff + (int)(Utils.dRandom2((extra))) : hitDiff;
                    hit = hitDiff;
                    if (hitDiff >= EnemyHP2)
                        hitDiff = EnemyHP2;
                    client.requestAnim(1979, 0);
                    if(type == 2) { //Blood effect!
                        client.stillgfx(377, EnemyY2, EnemyX2);
                        client.heal(hitDiff/3);
                    } else if (type == 3) { //Freeze effect!
                        client.stillgfx(369, EnemyY2, EnemyX2);
                    } else
                        client.stillgfx(78, EnemyY2, EnemyX2);
                    client.target = tempNpc;
                    client.setFocus(EnemyX2, EnemyY2);
                    client.giveExperience(40 * hitDiff, Skills.MAGIC);
                    client.giveExperience(hitDiff * 15, Skills.HITPOINTS);
                    tempNpc.dealDamage(client, hitDiff, hitCrit);
                    client.lastAttack = System.currentTimeMillis();
                }
            } else
                client.sendMessage("You need a magic level of " + client.requiredLevel[slot] + ".");
        }
    }
}