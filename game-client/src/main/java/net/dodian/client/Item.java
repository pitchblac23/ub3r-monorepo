package net.dodian.client;

final class Item extends Animable {

    public final Model getRotatedModel() {
        ItemDef itemDef = ItemDef.forID(ID);
        return itemDef.method201(anInt1559);
    }

    public Item() {
    }

    public int ID;
    public int x;
    public int y;
    public int anInt1559;
}