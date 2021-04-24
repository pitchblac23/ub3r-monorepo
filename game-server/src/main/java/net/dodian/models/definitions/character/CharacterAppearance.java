package net.dodian.models.definitions.character;

import javax.persistence.Embeddable;

@Embeddable
public class CharacterAppearance {
    private int id;
    private int characterId;
    private int head;
    private int jaw;
    private int torso;
    private int arms;
    private int hands;
    private int legs;
    private int feet;
    private int hairColor;
    private int torsoColor;
    private int legsColor;
    private int feetColor;
    private int skinColor;
    private Gender gender;

    public enum Gender {
        MALE,
        FEMALE
    }
}
