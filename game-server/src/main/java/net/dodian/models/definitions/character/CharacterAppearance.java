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

    public int getId() {
        return id;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getHead() {
        return head;
    }

    public int getJaw() {
        return jaw;
    }

    public int getTorso() {
        return torso;
    }

    public int getArms() {
        return arms;
    }

    public int getHands() {
        return hands;
    }

    public int getLegs() {
        return legs;
    }

    public int getFeet() {
        return feet;
    }

    public int getHairColor() {
        return hairColor;
    }

    public int getTorsoColor() {
        return torsoColor;
    }

    public int getLegsColor() {
        return legsColor;
    }

    public int getFeetColor() {
        return feetColor;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public Gender getGender() {
        return gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public void setJaw(int jaw) {
        this.jaw = jaw;
    }

    public void setTorso(int torso) {
        this.torso = torso;
    }

    public void setArms(int arms) {
        this.arms = arms;
    }

    public void setHands(int hands) {
        this.hands = hands;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public void setFeet(int feet) {
        this.feet = feet;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }

    public void setTorsoColor(int torsoColor) {
        this.torsoColor = torsoColor;
    }

    public void setLegsColor(int legsColor) {
        this.legsColor = legsColor;
    }

    public void setFeetColor(int feetColor) {
        this.feetColor = feetColor;
    }

    public void setSkinColor(int skinColor) {
        this.skinColor = skinColor;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public enum Gender {
        MALE,
        FEMALE
    }
}
