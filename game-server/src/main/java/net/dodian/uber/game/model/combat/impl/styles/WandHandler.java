package net.dodian.uber.game.model.combat.impl.styles;

import net.dodian.uber.game.model.combat.impl.CombatStyleHandler;
import net.dodian.uber.game.model.entity.Entity;
import net.dodian.uber.game.model.entity.player.Client;

public class WandHandler extends CombatStyleHandler {

    @Override
    public void handleWeaponInterface(Entity entity, int buttonId) {
        Client client = (Client) entity;

        if (buttonId == -1) {
            switch (client.getCombatStyle()) {
                case ACCURATE_MELEE:
                case ACCURATE_RANGED:
                    client.setCombatStyle(CombatStyles.ACCURATE_MELEE);
                    client.frame87(43, 0);
                    break;
                case CONTROLLED_MELEE:
                case AGGRESSIVE_MELEE:
                case RAPID_RANGED:
                    client.setCombatStyle(CombatStyles.AGGRESSIVE_MELEE);
                    client.frame87(43, 1);
                    break;
                case DEFENSIVE_MELEE:
                case LONGRANGE_RANGED:
                    client.setCombatStyle(CombatStyles.DEFENSIVE_MELEE);
                    client.frame87(43, 2);
                    break;
                case ATTACK_WITH_SPELL:
                    client.FightType = 0;
                    client.frame87(43, 4);
                    client.frame87(108, 1);
                    break;
                case ATTACK_WITH_SPELL_DEFENCE:
                    client.FightType = 1;
                    client.frame87(43, 4);
                    client.frame87(108, 2);
                    break;
            }
        }

        switch (buttonId) {
            case 1080:
                client.setCombatStyle(CombatStyles.ACCURATE_MELEE);
                break;
            case 1079:
                client.setCombatStyle(CombatStyles.AGGRESSIVE_MELEE);
                break;
            case 1078:
                client.setCombatStyle(CombatStyles.DEFENSIVE_MELEE);
                break;
            case 1093:
                client.setCombatStyle(CombatStyles.ATTACK_WITH_SPELL);
                break;
            case 94047:
                client.setCombatStyle(CombatStyles.ATTACK_WITH_SPELL_DEFENCE);
                break;
        }
    }
}