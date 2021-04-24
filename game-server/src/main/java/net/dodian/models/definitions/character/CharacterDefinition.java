package net.dodian.models.definitions.character;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class CharacterDefinition {
    @Id
    private int id;
    private int userId;
    @Embedded
    private CharacterAppearance appearance;
    private Timestamp lastLogin;
    private int autoCastSpell;
    //private List<int> friends;
    //private List<int> ignores;
    private int fightStyle;
}
