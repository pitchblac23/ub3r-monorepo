package net.dodian.client;

public final class RSInterface {

    public void swapInventoryItems(int i, int j) {
        int k = inv[i];
        inv[i] = inv[j];
        inv[j] = k;
        k = invStackSizes[i];
        invStackSizes[i] = invStackSizes[j];
        invStackSizes[j] = k;
    }

    public static void unpack(StreamLoader streamLoader, TextDrawingArea[] textDrawingAreas, StreamLoader streamLoader_1) {
        aMRUNodes_238 = new MRUNodes(50000);
        Stream stream = new Stream(streamLoader.getDataForName("data"));
        int i = -1;
        int j = stream.readUnsignedWord();
        interfaceCache = new RSInterface[31000 + j];
        while (stream.currentOffset < stream.buffer.length) {
            int k = stream.readUnsignedWord();
            if (k == 65535) {
                i = stream.readUnsignedWord();
                k = stream.readUnsignedWord();
            }
            RSInterface rsInterface = interfaceCache[k] = new RSInterface();
            rsInterface.id = k;
            rsInterface.parentID = i;
            rsInterface.type = stream.readUnsignedByte();
            rsInterface.atActionType = stream.readUnsignedByte();
            rsInterface.contentType = stream.readUnsignedWord();
            rsInterface.width = stream.readUnsignedWord();
            rsInterface.height = stream.readUnsignedWord();
            rsInterface.aByte254 = (byte) stream.readUnsignedByte();
            rsInterface.hoverType = stream.readUnsignedByte();
            if (rsInterface.hoverType != 0)
                rsInterface.hoverType = (rsInterface.hoverType - 1 << 8) + stream.readUnsignedByte();
            else
                rsInterface.hoverType = -1;
            int i1 = stream.readUnsignedByte();
            if (i1 > 0) {
                rsInterface.anIntArray245 = new int[i1];
                rsInterface.anIntArray212 = new int[i1];
                for (int j1 = 0; j1 < i1; j1++) {
                    rsInterface.anIntArray245[j1] = stream.readUnsignedByte();
                    rsInterface.anIntArray212[j1] = stream.readUnsignedWord();
                }
            }
            int k1 = stream.readUnsignedByte();
            if (k1 > 0) {
                rsInterface.scripts = new int[k1][];
                for (int l1 = 0; l1 < k1; l1++) {
                    int i3 = stream.readUnsignedWord();
                    rsInterface.scripts[l1] = new int[i3];
                    for (int l4 = 0; l4 < i3; l4++)
                        rsInterface.scripts[l1][l4] = stream.readUnsignedWord();
                }
            }
            if (rsInterface.type == 0) {
                rsInterface.drawsTransparent = false;
                rsInterface.scrollMax = stream.readUnsignedWord();
                rsInterface.isMouseoverTriggered = stream.readUnsignedByte() == 1;
                int i2 = stream.readUnsignedWord();
                rsInterface.children = new int[i2];
                rsInterface.childX = new int[i2];
                rsInterface.childY = new int[i2];
                for (int j3 = 0; j3 < i2; j3++) {
                    rsInterface.children[j3] = stream.readUnsignedWord();
                    rsInterface.childX[j3] = stream.readSignedWord();
                    rsInterface.childY[j3] = stream.readSignedWord();
                }
            }
            if (rsInterface.type == 1) {
                stream.readUnsignedWord();
                stream.readUnsignedByte();
            }
            if (rsInterface.type == 2) {
                rsInterface.inv = new int[rsInterface.width * rsInterface.height];
                rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
                rsInterface.aBoolean259 = stream.readUnsignedByte() == 1;
                rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
                rsInterface.usableItemInterface = stream.readUnsignedByte() == 1;
                rsInterface.aBoolean235 = stream.readUnsignedByte() == 1;
                rsInterface.invSpritePadX = stream.readUnsignedByte();
                rsInterface.invSpritePadY = stream.readUnsignedByte();
                rsInterface.spritesX = new int[20];
                rsInterface.spritesY = new int[20];
                rsInterface.sprites = new Sprite[20];
                for (int j2 = 0; j2 < 20; j2++) {
                    int k3 = stream.readUnsignedByte();
                    if (k3 == 1) {
                        rsInterface.spritesX[j2] = stream.readSignedWord();
                        rsInterface.spritesY[j2] = stream.readSignedWord();
                        String s1 = stream.readString();
                        if (streamLoader_1 != null && s1.length() > 0) {
                            int i5 = s1.lastIndexOf(",");
                            rsInterface.sprites[j2] = method207(Integer.parseInt(s1.substring(i5 + 1)), streamLoader_1, s1.substring(0, i5));
                        }
                    }
                }
                rsInterface.actions = new String[5];
                for (int l3 = 0; l3 < 5; l3++) {
                    rsInterface.actions[l3] = stream.readString();
                    if (rsInterface.actions[l3].isEmpty())
                        rsInterface.actions[l3] = null;
                    if(rsInterface.id == 3900)
                        rsInterface.actions[4] = "Buy X";
                    if(rsInterface.parentID == 3822)
                        rsInterface.actions[4] = "Sell X";
                    if (rsInterface.parentID == 1644)
                        rsInterface.actions[2] = "Operate";
                }
            }
            if (rsInterface.type == 3)
                rsInterface.aBoolean227 = stream.readUnsignedByte() == 1;
            if (rsInterface.type == 4 || rsInterface.type == 1) {
                rsInterface.centerText = stream.readUnsignedByte() == 1;
                int k2 = stream.readUnsignedByte();
                if (textDrawingAreas != null)
                    rsInterface.textDrawingAreas = textDrawingAreas[k2];
                rsInterface.textShadow = stream.readUnsignedByte() == 1;
            }
            if (rsInterface.type == 4) {
                rsInterface.message = stream.readString().replaceAll("RuneScape", "Dodian");
                rsInterface.aString228 = stream.readString();
            }
            if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4)
                rsInterface.textColor = stream.readDWord();
            if (rsInterface.type == 3 || rsInterface.type == 4) {
                rsInterface.anInt219 = stream.readDWord();
                rsInterface.anInt216 = stream.readDWord();
                rsInterface.anInt239 = stream.readDWord();
            }
            if (rsInterface.type == 5) {
                rsInterface.drawsTransparent = false;
                String s = stream.readString();
                if (streamLoader_1 != null && s.length() > 0) {
                    int i4 = s.lastIndexOf(",");
                    rsInterface.sprite1 = method207(Integer.parseInt(s.substring(i4 + 1)), streamLoader_1, s.substring(0, i4));
                }
                s = stream.readString();
                if (streamLoader_1 != null && s.length() > 0) {
                    int j4 = s.lastIndexOf(",");
                    rsInterface.sprite2 = method207(Integer.parseInt(s.substring(j4 + 1)), streamLoader_1, s.substring(0, j4));
                }
            }
            if (rsInterface.type == 6) {
                int l = stream.readUnsignedByte();
                if (l != 0) {
                    rsInterface.anInt233 = 1;
                    rsInterface.mediaID = (l - 1 << 8) + stream.readUnsignedByte();
                }
                l = stream.readUnsignedByte();
                if (l != 0) {
                    rsInterface.anInt255 = 1;
                    rsInterface.anInt256 = (l - 1 << 8) + stream.readUnsignedByte();
                }
                l = stream.readUnsignedByte();
                if (l != 0)
                    rsInterface.anInt257 = (l - 1 << 8) + stream.readUnsignedByte();
                else
                    rsInterface.anInt257 = -1;
                l = stream.readUnsignedByte();
                if (l != 0)
                    rsInterface.anInt258 = (l - 1 << 8) + stream.readUnsignedByte();
                else
                    rsInterface.anInt258 = -1;
                rsInterface.modelZoom = stream.readUnsignedWord();
                rsInterface.modelRotation1 = stream.readUnsignedWord();
                rsInterface.modelRotation2 = stream.readUnsignedWord();
            }
            if (rsInterface.type == 7) {
                rsInterface.inv = new int[rsInterface.width * rsInterface.height];
                rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
                rsInterface.centerText = stream.readUnsignedByte() == 1;
                int l2 = stream.readUnsignedByte();
                if (textDrawingAreas != null)
                    rsInterface.textDrawingAreas = textDrawingAreas[l2];
                rsInterface.textShadow = stream.readUnsignedByte() == 1;
                rsInterface.textColor = stream.readDWord();
                rsInterface.invSpritePadX = stream.readSignedWord();
                rsInterface.invSpritePadY = stream.readSignedWord();
                rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
                rsInterface.actions = new String[5];
                for (int k4 = 0; k4 < 5; k4++) {
                    rsInterface.actions[k4] = stream.readString();
                    if (rsInterface.actions[k4].length() == 0)
                        rsInterface.actions[k4] = null;
                }
            }
            if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
                rsInterface.selectedActionName = stream.readString();
                rsInterface.spellName = stream.readString();
                rsInterface.spellUsableOn = stream.readUnsignedWord();
            }

            if (rsInterface.type == 8)
                rsInterface.message = stream.readString();

            if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4 || rsInterface.atActionType == 5 || rsInterface.atActionType == 6) {
                rsInterface.tooltip = stream.readString();
                if (rsInterface.tooltip.length() == 0) {
                    if (rsInterface.atActionType == 1)
                        rsInterface.tooltip = "Ok";
                    if (rsInterface.atActionType == 4)
                        rsInterface.tooltip = "Select";
                    if (rsInterface.atActionType == 5)
                        rsInterface.tooltip = "Select";
                    if (rsInterface.atActionType == 6)
                        rsInterface.tooltip = "Continue";
                }
            }
        }
        aClass44 = streamLoader;
        aMRUNodes_238 = null;
    }

    public static void addButton(int id, int sid, String spriteName, String tooltip) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;
        tab.parentID = id;
        tab.type = 5;
        tab.atActionType = 1;
        tab.contentType = 0;
        tab.aByte254 = (byte) 0;
        tab.hoverType = 52;
        tab.sprite1 = imageLoader(sid, spriteName);
        tab.sprite2 = imageLoader(sid, spriteName);
        tab.width = tab.sprite1.myWidth;
        tab.height = tab.sprite2.myHeight;
        tab.tooltip = tooltip;
    }

    public String popupString;

    public static void addTooltipBox(int id, String text) {
        RSInterface rsi = addInterface(id);
        rsi.id = id;
        rsi.parentID = id;
        rsi.type = 8;
        rsi.popupString = text;
    }

    public static void addTooltip(int id, String text) {
        RSInterface rsi = addInterface(id);
        rsi.id = id;
        rsi.type = 0;
        rsi.isMouseoverTriggered = true;
        rsi.hoverType = -1;
        addTooltipBox(id + 1, text);
        rsi.totalChildren(1);
        rsi.child(0, id + 1, 0, 0);
    }

    private static Sprite CustomSpriteLoader(int id, String s) {
        long l = (TextClass.method585(s) << 8) + (long) id;
        Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
        if (sprite != null) {
            return sprite;
        }
        try {
            sprite = new Sprite("/Attack/" + id + s);
            aMRUNodes_238.removeFromCache(sprite, l);
        } catch (Exception exception) {
            return null;
        }
        return sprite;
    }

    public static RSInterface addInterface(int id) {
        RSInterface rsi = interfaceCache[id] = new RSInterface();
        rsi.id = id;
        rsi.parentID = id;
        rsi.width = 512;
        rsi.height = 334;
        return rsi;
    }

    public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
        RSInterface rsi = interfaceCache[id] = new RSInterface();
        if (centered)
            rsi.centerText = true;
        rsi.textShadow = true;
        rsi.textDrawingAreas = tda[idx];
        rsi.message = text;
        rsi.textColor = color;
        rsi.id = id;
        rsi.type = 4;
    }

    public static void textColor(int id, int color) {
        RSInterface rsi = interfaceCache[id];
        rsi.textColor = color;
    }

    public static void textSize(int id, TextDrawingArea tda[], int idx) {
        RSInterface rsi = interfaceCache[id];
        rsi.textDrawingAreas = tda[idx];
    }

    public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
        RSInterface rsi = interfaceCache[id] = new RSInterface();
        rsi.sprite1 = method207(sprite1, aClass44, sprites);
        rsi.sprite2 = method207(sprite2, aClass44, sprites);
        rsi.parentID = id;
        rsi.id = id;
        rsi.type = 5;
    }

    public static void sprite1(int id, int sprite) {
        RSInterface class9 = interfaceCache[id];
        class9.sprite1 = CustomSpriteLoader(sprite, "");
    }

    public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
        RSInterface rsi = interfaceCache[id] = new RSInterface();
        rsi.sprite1 = CustomSpriteLoader(sprite, "");
        if (sprite2 == sprite)
            rsi.sprite2 = CustomSpriteLoader(sprite, "a");
        else
            rsi.sprite2 = CustomSpriteLoader(sprite2, "");
        rsi.tooltip = s;
        rsi.contentType = 0;
        rsi.atActionType = 1;
        rsi.width = width;
        rsi.hoverType = 52;
        rsi.parentID = id;
        rsi.id = id;
        rsi.type = 5;
        rsi.height = height;
    }

    public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
        RSInterface rsi = addInterface(id);
        rsi.sprite1 = CustomSpriteLoader(sprite, "");
        rsi.sprite2 = CustomSpriteLoader(sprite, "a");
        rsi.anIntArray212 = new int[1];
        rsi.anIntArray212[0] = 1;
        rsi.anIntArray245 = new int[1];
        rsi.anIntArray245[0] = 1;
        rsi.scripts = new int[1][3];
        rsi.scripts[0][0] = 5;
        rsi.scripts[0][1] = setconfig;
        rsi.scripts[0][2] = 0;
        rsi.atActionType = 4;
        rsi.width = width;
        rsi.hoverType = -1;
        rsi.parentID = id;
        rsi.id = id;
        rsi.type = 5;
        rsi.height = height;
        rsi.tooltip = s;
    }

    public void totalChildren(int id, int x, int y) {
        children = new int[id];
        childX = new int[x];
        childY = new int[y];
    }

    public String hoverText;

    public static void addHoverBox(int id, int ParentID, String text, String text2, int configId, int configFrame) {
        RSInterface rsi = addTabInterface(id);
        rsi.id = id;
        rsi.parentID = ParentID;
        rsi.type = 8;
        rsi.aString228 = text;
        rsi.message = text2;
        rsi.anIntArray245 = new int[1];
        rsi.anIntArray212 = new int[1];
        rsi.anIntArray245[0] = 1;
        rsi.anIntArray212[0] = configId;
        rsi.scripts = new int[1][3];
        rsi.scripts[0][0] = 5;
        rsi.scripts[0][1] = configFrame;
        rsi.scripts[0][2] = 0;
    }

    public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean center, boolean shadow) {
        RSInterface tab = addTabInterface(id);
        tab.parentID = id;
        tab.id = id;
        tab.type = 4;
        tab.atActionType = 0;
        tab.width = 0;
        tab.height = 11;
        tab.contentType = 0;
        tab.aByte254 = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.textDrawingAreas = tda[idx];
        tab.message = text;
        tab.aString228 = "";
        tab.textColor = color;
        tab.anInt219 = 0;
        tab.anInt216 = 0;
        tab.anInt239 = 0;
    }

    public static void addText(int i, String s, int k, boolean l, boolean m, int a, TextDrawingArea[] TDA, int j) {
        RSInterface RSInterface = addInterface(i);
        RSInterface.parentID = i;
        RSInterface.id = i;
        RSInterface.type = 4;
        RSInterface.atActionType = 0;
        RSInterface.width = 0;
        RSInterface.height = 0;
        RSInterface.contentType = 0;
        RSInterface.aByte254 = 0;
        RSInterface.hoverType = a;
        RSInterface.centerText = l;
        RSInterface.textShadow = m;
        RSInterface.textDrawingAreas = TDA[j];
        RSInterface.message = s;
        RSInterface.aString228 = "";
        RSInterface.textColor = k;
    }

    public static void addButton(int id, int sid, String spriteName, String tooltip, int w, int h) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;
        tab.parentID = id;
        tab.type = 5;
        tab.atActionType = 1;
        tab.contentType = 0;
        tab.aByte254 = (byte) 0;
        tab.hoverType = 52;
        tab.sprite1 = imageLoader(sid, spriteName);
        tab.sprite2 = imageLoader(sid, spriteName);
        tab.width = w;
        tab.height = h;
        tab.tooltip = tooltip;
    }

    public static void addConfigButton(int ID, int pID, int bID, int bID2, String bName, int width, int height, String tT, int configID, int aT, int configFrame) {
        RSInterface Tab = addTabInterface(ID);
        Tab.parentID = pID;
        Tab.id = ID;
        Tab.type = 5;
        Tab.atActionType = aT;
        Tab.contentType = 0;
        Tab.width = width;
        Tab.height = height;
        Tab.aByte254 = 0;
        Tab.hoverType = -1;
        Tab.anIntArray245 = new int[1];
        Tab.anIntArray212 = new int[1];
        Tab.anIntArray245[0] = 1;
        Tab.anIntArray212[0] = configID;
        Tab.scripts = new int[1][3];
        Tab.scripts[0][0] = 5;
        Tab.scripts[0][1] = configFrame;
        Tab.scripts[0][2] = 0;
        Tab.sprite1 = imageLoader(bID, bName);
        Tab.sprite2 = imageLoader(bID2, bName);
        Tab.tooltip = tT;
    }

    public static void addSprite(int id, int spriteId, String spriteName) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;
        tab.parentID = id;
        tab.type = 5;
        tab.atActionType = 0;
        tab.contentType = 0;
        tab.aByte254 = (byte) 0;
        tab.hoverType = 52;
        tab.sprite1 = imageLoader(spriteId, spriteName);
        tab.sprite2 = imageLoader(spriteId, spriteName);
        tab.width = 512;
        tab.height = 334;
    }

    public static void addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {// hoverable
        // button
        RSInterface tab = addTabInterface(i);
        tab.id = i;
        tab.parentID = i;
        tab.type = 5;
        tab.atActionType = aT;
        tab.contentType = contentType;
        tab.aByte254 = 0;
        tab.hoverType = hoverOver;
        tab.sprite1 = imageLoader(j, imageName);
        tab.sprite2 = imageLoader(j, imageName);
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
    }

    public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {// hoverable
        // button
        RSInterface tab = addTabInterface(i);
        tab.parentID = i;
        tab.id = i;
        tab.type = 0;
        tab.atActionType = 0;
        tab.width = w;
        tab.height = h;
        tab.isMouseoverTriggered = true;
        tab.aByte254 = 0;
        tab.hoverType = -1;
        tab.scrollMax = 0;
        addHoverImage(IMAGEID, j, j, imageName);
        tab.totalChildren(1);
        tab.child(0, IMAGEID, 0, 0);
    }

    public static void addHoverImage(int i, int j, int k, String name) {
        RSInterface tab = addTabInterface(i);
        tab.id = i;
        tab.parentID = i;
        tab.type = 5;
        tab.atActionType = 0;
        tab.contentType = 0;
        tab.width = 512;
        tab.height = 334;
        tab.aByte254 = 0;
        tab.hoverType = 52;
        tab.sprite1 = imageLoader(j, name);
        tab.sprite2 = imageLoader(k, name);
    }

    public static void addTransparentSprite(int id, int spriteId, String spriteName) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;
        tab.parentID = id;
        tab.type = 5;
        tab.atActionType = 0;
        tab.contentType = 0;
        tab.aByte254 = (byte) 0;
        tab.hoverType = 52;
        tab.sprite1 = imageLoader(spriteId, spriteName);
        tab.sprite2 = imageLoader(spriteId, spriteName);
        tab.width = 512;
        tab.height = 334;
        tab.drawsTransparent = true;
    }

    public static RSInterface addScreenInterface(int id) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;
        tab.parentID = id;
        tab.type = 0;
        tab.atActionType = 0;
        tab.contentType = 0;
        tab.width = 512;
        tab.height = 334;
        tab.aByte254 = (byte) 0;
        tab.hoverType = 0;
        return tab;
    }

    public static RSInterface addTabInterface(int id) {
        RSInterface tab = interfaceCache[id] = new RSInterface();
        tab.id = id;// 250
        tab.parentID = id;// 236
        tab.type = 0;// 262
        tab.atActionType = 0;// 217
        tab.contentType = 0;
        tab.width = 512;// 220
        tab.height = 700;// 267
        tab.aByte254 = (byte) 0;
        tab.hoverType = -1;// Int 230
        return tab;
    }

    private static Sprite imageLoader(int i, String s) {
        long l = (TextClass.method585(s) << 8) + (long) i;
        Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
        if (sprite != null)
            return sprite;
        try {
            sprite = new Sprite(s + " " + i);
            aMRUNodes_238.removeFromCache(sprite, l);
        } catch (Exception exception) {
            return null;
        }
        return sprite;
    }

    public void child(int id, int interID, int x, int y) {
        children[id] = interID;
        childX[id] = x;
        childY[id] = y;
    }

    public void totalChildren(int t) {
        children = new int[t];
        childX = new int[t];
        childY = new int[t];
    }

    private Model method206(int i, int j) {
        Model model = (Model) aMRUNodes_264.insertFromCache((i << 16) + j);
        if (model != null)
            return model;
        if (i == 1)
            model = Model.method462(j);
        if (i == 2)
            model = EntityDef.forID(j).method160();
        if (i == 3)
            model = Client.myPlayer.method453();
        if (i == 4)
            model = ItemDef.forID(j).method202(50);
        if (i == 5)
            model = null;
        if (model != null)
            aMRUNodes_264.removeFromCache(model, (i << 16) + j);
        return model;
    }

    private static Sprite method207(int i, StreamLoader streamLoader, String s) {
        long l = (TextClass.method585(s) << 8) + (long) i;
        Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
        if (sprite != null)
            return sprite;
        try {
            sprite = new Sprite(streamLoader, s, i);
            aMRUNodes_238.removeFromCache(sprite, l);
        } catch (Exception _ex) {
            return null;
        }
        return sprite;
    }

    public static void method208(boolean flag, Model model) {
        int i = 0;// was parameter
        int j = 5;// was parameter
        if (flag)
            return;
        aMRUNodes_264.unlinkAll();
        if (model != null && j != 4)
            aMRUNodes_264.removeFromCache(model, (j << 16) + i);
    }

    public Model method209(int j, int k, boolean flag) {
        Model model;
        if (flag)
            model = method206(anInt255, anInt256);
        else
            model = method206(anInt233, mediaID);
        if (model == null)
            return null;
        if (k == -1 && j == -1 && model.anIntArray1640 == null)
            return model;
        Model model_1 = new Model(true, Class36.method532(k) & Class36.method532(j), false, model);
        if (k != -1 || j != -1)
            model_1.method469();
        if (k != -1)
            model_1.method470(k);
        if (j != -1)
            model_1.method470(j);
        model_1.setLighting(64, 768, -50, -10, -50, true);
        return model_1;
    }

    public RSInterface() {
    }

    public static StreamLoader aClass44;
    public boolean drawsTransparent;
    public Sprite sprite1;
    public int anInt208;
    public Sprite[] sprites;
    public static RSInterface[] interfaceCache;
    public int[] anIntArray212;
    public int contentType;// anInt214
    public int[] spritesX;
    public int anInt216;
    public int atActionType;
    public String spellName;
    public int anInt219;
    public int width;
    public String tooltip;
    public String selectedActionName;
    public boolean centerText;
    public int scrollPosition;
    public String[] actions;
    public int[][] scripts;
    public boolean aBoolean227;
    public String aString228;
    public int hoverType;
    public int invSpritePadX;
    public int textColor;
    public int anInt233;
    public int mediaID;
    public boolean aBoolean235;
    public int parentID;
    public int spellUsableOn;
    private static MRUNodes aMRUNodes_238;
    public int anInt239;
    public int[] children;
    public int[] childX;
    public boolean usableItemInterface;
    public TextDrawingArea textDrawingAreas;
    public int invSpritePadY;
    public int[] anIntArray245;
    public int anInt246;
    public int[] spritesY;
    public String message;
    public boolean isInventoryInterface;
    public int id;
    public int[] invStackSizes;
    public int[] inv;
    public byte aByte254;
    private int anInt255;
    private int anInt256;
    public int anInt257;
    public int anInt258;
    public boolean aBoolean259;
    public Sprite sprite2;
    public int scrollMax;
    public int type;
    public int anInt263;
    private static final MRUNodes aMRUNodes_264 = new MRUNodes(30);
    public int anInt265;
    public boolean isMouseoverTriggered;
    public int height;
    public boolean textShadow;
    public int modelZoom;
    public int modelRotation1;
    public int modelRotation2;
    public int[] childY;

    public static void addLunarSprite(int i, int j, String name) {
        RSInterface RSInterface = addInterface(i);
        RSInterface.id = i;
        RSInterface.parentID = i;
        RSInterface.type = 5;
        RSInterface.atActionType = 0;
        RSInterface.contentType = 0;
        RSInterface.aByte254 = 0;
        RSInterface.hoverType = 52;
        RSInterface.sprite1 = imageLoader(j, name);
        RSInterface.width = 500;
        RSInterface.height = 500;
        RSInterface.tooltip = "";
    }

    public static void drawRune(int i, int id, String runeName) {
        RSInterface RSInterface = addInterface(i);
        RSInterface.type = 5;
        RSInterface.atActionType = 0;
        RSInterface.contentType = 0;
        RSInterface.aByte254 = 0;
        RSInterface.hoverType = 52;
        RSInterface.sprite1 = imageLoader(id, "Lunar/RUNE");
        RSInterface.width = 500;
        RSInterface.height = 500;
    }

    public static void addRuneText(int ID, int runeAmount, int RuneID, TextDrawingArea[] font) {
        RSInterface rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parentID = 1151;
        rsInterface.type = 4;
        rsInterface.atActionType = 0;
        rsInterface.contentType = 0;
        rsInterface.width = 0;
        rsInterface.height = 14;
        rsInterface.aByte254 = 0;
        rsInterface.hoverType = -1;
        rsInterface.anIntArray245 = new int[1];
        rsInterface.anIntArray212 = new int[1];
        rsInterface.anIntArray245[0] = 3;
        rsInterface.anIntArray212[0] = runeAmount;
        rsInterface.scripts = new int[1][4];
        rsInterface.scripts[0][0] = 4;
        rsInterface.scripts[0][1] = 3214;
        rsInterface.scripts[0][2] = RuneID;
        rsInterface.scripts[0][3] = 0;
        rsInterface.centerText = true;
        rsInterface.textDrawingAreas = font[0];
        rsInterface.textShadow = true;
        rsInterface.message = "%1/" + runeAmount + "";
        rsInterface.aString228 = "";
        rsInterface.textColor = 12582912;
        rsInterface.anInt219 = 49152;
    }

    public static void homeTeleport() {
        RSInterface RSInterface = addInterface(30000);
        RSInterface.tooltip = "Cast @gre@Lunar Home Teleport";
        RSInterface.id = 30000;
        RSInterface.parentID = 30000;
        RSInterface.type = 5;
        RSInterface.atActionType = 5;
        RSInterface.contentType = 0;
        RSInterface.aByte254 = 0;
        RSInterface.hoverType = 30001;
        RSInterface.sprite1 = imageLoader(1, "Lunar/SPRITE");
        RSInterface.width = 20;
        RSInterface.height = 20;
        RSInterface Int = addInterface(30001);
        Int.isMouseoverTriggered = true;
        Int.hoverType = -1;
        setChildren(1, Int);
        addLunarSprite(30002, 0, "SPRITE");
        setBounds(30002, 0, 0, 0, Int);
    }

    public static void setChildren(int total, RSInterface i) {
        i.children = new int[total];
        i.childX = new int[total];
        i.childY = new int[total];
    }

    public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
        RSinterface.children[frame] = ID;
        RSinterface.childX[frame] = X;
        RSinterface.childY[frame] = Y;
    }

    public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
        RSInterface RSInterface = addInterface(i);
        RSInterface.id = i;
        RSInterface.parentID = i;
        RSInterface.type = 5;
        RSInterface.atActionType = AT;
        RSInterface.contentType = 0;
        RSInterface.aByte254 = 0;
        RSInterface.hoverType = 52;
        RSInterface.sprite1 = imageLoader(j, name);
        RSInterface.sprite2 = imageLoader(j, name);
        RSInterface.width = W;
        RSInterface.height = H;
        RSInterface.tooltip = S;
    }
}