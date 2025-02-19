package net.dodian.client;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ItemDef {

    public static void nullLoader() {
        mruNodes2 = null;
        mruNodes1 = null;
        streamIndices = null;
        cache = null;
        stream = null;
    }

    public boolean method192(int j) {
        int k = maleHeadModel;
        int l = maleHeadModel2;
        if (j == 1) {
            k = femaleHeadModel;
            l = femaleHeadModel2;
        }
        if (k == -1)
            return true;
        boolean flag = true;
        if (!Model.method463(k))
            flag = false;
        if (l != -1 && !Model.method463(l))
            flag = false;
        return flag;
    }

    public static void unpackConfig(StreamLoader archive) {
        stream = new Stream(archive.getDataForName("obj.dat"));
        Stream stream = new Stream(archive.getDataForName("obj.idx"));
        totalItems = stream.readUnsignedShort();
        System.out.printf("Loaded: %d items%n", totalItems);
        streamIndices = new int[totalItems];
        int i = 2;
        for (int j = 0; j < totalItems; j++) {
            streamIndices[j] = i;
            i += stream.readUnsignedShort();
        }
        cache = new ItemDef[10];
        for (int k = 0; k < 10; k++)
            cache[k] = new ItemDef();
        itemDump(totalItems);
    }

    public static void itemDump(int max) {
        try {
            FileWriter fw = new FileWriter(System.getProperty("user.home") + "/Dodian-Dev/Item Dump.txt");
            for (int i = 0; i < max; i++) {
                ItemDef def = ItemDef.forID(i);
                if (def != null) {
                    fw.write("Id " + i + ":");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Item Name = " + def.name);
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Actions = [" + Arrays.toString(def.itemActions) +"];");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("break;");
                    fw.write(System.getProperty("line.separator"));
                    fw.write(System.getProperty("line.separator"));
                } else {
                    fw.write("case " + i + ":");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("itemDef.name = \"NULL\";");
                }
            }
            System.out.println("Done dumping items!");
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Model method194(int j) {
        int k = maleHeadModel;
        int l = maleHeadModel2;
        if (j == 1) {
            k = femaleHeadModel;
            l = femaleHeadModel2;
        }
        if (k == -1)
            return null;
        Model model = Model.method462(k);
        if (l != -1) {
            Model model_1 = Model.method462(l);
            Model[] aclass30_sub2_sub4_sub6s = {model, model_1};
            model = new Model(2, aclass30_sub2_sub4_sub6s);
        }
        if (modifiedModelColors != null) {
            for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
                model.method476(modifiedModelColors[i1],
                        originalModelColors[i1]);
        }
        return model;
    }

    public boolean method195(int j) {
        int k = maleModel0;
        int l = maleModel1;
        int i1 = maleModel2;
        if (j == 1) {
            k = femaleModel0;
            l = femaleModel1;
            i1 = femaleModel2;
        }
        if (k == -1)
            return true;
        boolean flag = true;
        if (!Model.method463(k))
            flag = false;
        if (l != -1 && !Model.method463(l))
            flag = false;
        if (i1 != -1 && !Model.method463(i1))
            flag = false;
        return flag;
    }

    public Model method196(int i) {
        int j = maleModel0;
        int k = maleModel1;
        int l = maleModel2;
        if (i == 1) {
            j = femaleModel0;
            k = femaleModel1;
            l = femaleModel2;
        }
        if (j == -1)
            return null;
        Model model = Model.method462(j);

        if (k != -1)
            if (l != -1) {
                Model model_1 = Model.method462(k);
                Model model_3 = Model.method462(l);
                Model[] aclass30_sub2_sub4_sub6_1s = {model, model_1, model_3};
                model = new Model(3, aclass30_sub2_sub4_sub6_1s);
            } else {
                Model model_2 = Model.method462(k);
                Model[] aclass30_sub2_sub4_sub6s = {model, model_2};
                model = new Model(2, aclass30_sub2_sub4_sub6s);
            }
        if (i == 0 && maleOffset != 0)
            model.method475(0, maleOffset, 0);
        if (i == 1 && femaleOffset != 0)
            model.method475(0, femaleOffset, 0);
        if (modifiedModelColors != null) {
            for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
                model.method476(modifiedModelColors[i1], originalModelColors[i1]);
        }
        return model;
    }

    private void setDefaults() {
        modelID = 0;
        name = null;
        description = null;
        modifiedModelColors = null;
        originalModelColors = null;
        modelZoom = 2000;
        modelRotationY = 0;
        modelRotationX = 0;
        modelRotationZ = 0;
        modelOffset1 = 0;
        modelOffset2 = 0;
        stackable = false;
        value = 1;
        membersObject = false;
        groundActions = null;
        itemActions = null;
        maleModel0 = -1;
        maleModel1 = -1;
        maleOffset = 0;
        femaleModel0 = -1;
        femaleModel1 = -1;
        femaleOffset = 0;
        maleModel2 = -1;
        femaleModel2 = -1;
        maleHeadModel = -1;
        maleHeadModel2 = -1;
        femaleHeadModel = -1;
        femaleHeadModel2 = -1;
        stackIDs = null;
        stackAmounts = null;
        unnotedTemplate = -1;
        notedTemplate = -1;
        resizeX = 128;
        resizeY = 128;
        resizeZ = 128;
        ambient = 0;
        contrast = 0;
        team = 0;
    }

    public static ItemDef forID(int i) {
        for (int j = 0; j < 10; j++)
            if (cache[j].id == i)
                return cache[j];

        cacheIndex = (cacheIndex + 1) % 10;
        ItemDef itemDef = cache[cacheIndex];
        stream.currentOffset = streamIndices[i];
        itemDef.id = i;
        itemDef.setDefaults();
        itemDef.readValues(stream);
        customValues(itemDef);
        return itemDef;
    }

    private static void customValues(ItemDef def) {
        //def.itemActions = new String[]{null, "option1", null, null, null};
        //def.originalModelColors[0] = is the modified color;
        //def.modifiedModelColors[0] = is the original color;
        switch (def.id) {
            case 5733:
                def.name = "Admin Tool";
                def.itemActions[0] = "Add Npc Spawn";
                def.itemActions[1] = "Option 2";
                def.itemActions[2] = "Option 3";
                def.itemActions[3] = "Option 4";
                break;
            case 12854:
                def.name = "Santa's sack";
                break;
            case 13203:
                def.name = "Spunky Mask";
                break;
            case 11996:
                def.name = "Present";
                def.itemActions[0] = "Open";
                def.itemActions[1] = null;
                def.itemActions[3] = null;
                break;
            case 13345:
                def.name = "Present";
                def.itemActions[0] = "Open";
                break;
            case 13346:
                def.itemActions[0] = "Open";
                break;
            case 11918:
                def.name = "New Year's gift";
                def.itemActions[0] = "Open";
                break;
            case 11997:
                def.name = "Event shards";
                def.itemActions[2] = "Info";
                break;
            case 11738:
                def.itemActions = new String[]{"Open", null, null, null, null};
                break;
            case 480:
                def.modelID = 65298;
                def.originalModelColors = new int[1];
                def.modifiedModelColors = new int[1];
                def.originalModelColors[0] = 5652;
                def.modifiedModelColors[0] = 61;
                break;
            case 482:
                def.modelID = 65298;
                def.originalModelColors = new int[1];
                def.modifiedModelColors = new int[1];
                def.originalModelColors[0] = 33;
                def.modifiedModelColors[0] = 61;
                break;
            case 484://steel
                def.modelID = 65298;
                break;
            case 486://mithril
                def.modelID = 65298;
                def.originalModelColors = new int[1];
                def.modifiedModelColors = new int[1];
                def.originalModelColors[0] = 43297;
                def.modifiedModelColors[0] = 61;
                break;
            case 488://adamant
                def.modelID = 65298;
                def.originalModelColors = new int[1];
                def.modifiedModelColors = new int[1];
                def.originalModelColors[0] = 21662;
                def.modifiedModelColors[0] = 61;
                break;
            case 490:
                def.modelID = 65298;
                def.originalModelColors = new int[1];
                def.modifiedModelColors = new int[1];
                def.originalModelColors[0] = 36133;
                def.modifiedModelColors[0] = 61;
                break;
        }
        if (def.notedTemplate != -1)
            def.toNote();
    }

    private void toNote() {
        ItemDef itemDef = forID(notedTemplate);
        modelID = itemDef.modelID;
        modelZoom = itemDef.modelZoom;
        modelRotationY = itemDef.modelRotationY;
        modelRotationX = itemDef.modelRotationX;

        modelRotationZ = itemDef.modelRotationZ;
        modelOffset1 = itemDef.modelOffset1;
        modelOffset2 = itemDef.modelOffset2;
        modifiedModelColors = itemDef.modifiedModelColors;
        originalModelColors = itemDef.originalModelColors;
        ItemDef itemDef_1 = forID(unnotedTemplate);
        if (itemDef_1 == null || itemDef_1.name == null) {
            return;
        }
        name = itemDef_1.name;
        membersObject = itemDef_1.membersObject;
        value = itemDef_1.value;
        String aOrAn = "a";
        char vowelChar = itemDef_1.name.charAt(0);
        if (vowelChar == 'A' || vowelChar == 'E' || vowelChar == 'I' || vowelChar == 'O' || vowelChar == 'U') {
            aOrAn = "an";
        }
        description = "Swap this note at any bank for " + aOrAn + " " + itemDef_1.name + ".";
        stackable = true;
    }

    public static Sprite getSprite(int i, int j, int k) {
        if (k == 0) {
            Sprite sprite = (Sprite) mruNodes1.insertFromCache(i);
            if (sprite != null && sprite.anInt1445 != j && sprite.anInt1445 != -1) {

                sprite.unlink();
                sprite = null;
            }
            if (sprite != null)
                return sprite;
        }
        ItemDef itemDef = forID(i);
        if (itemDef.stackIDs == null)
            j = -1;
        if (j > 1) {
            int i1 = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (j >= itemDef.stackAmounts[j1]
                        && itemDef.stackAmounts[j1] != 0)
                    i1 = itemDef.stackIDs[j1];

            if (i1 != -1)
                itemDef = forID(i1);
        }
        Model model = itemDef.method201(1);
        if (model == null)
            return null;
        Sprite sprite = null;
        if (itemDef.notedTemplate != -1) {
            sprite = getSprite(itemDef.unnotedTemplate, 10, -1);
            if (sprite == null)
                return null;
        }
        Sprite enabledSprite = new Sprite(32, 32);
        int k1 = Texture.textureInt1;
        int l1 = Texture.textureInt2;
        int[] ai = Texture.anIntArray1472;
        int[] ai1 = DrawingArea.pixels;
        int i2 = DrawingArea.width;
        int j2 = DrawingArea.height;
        int k2 = DrawingArea.topX;
        int l2 = DrawingArea.bottomX;
        int i3 = DrawingArea.topY;
        int j3 = DrawingArea.bottomY;
        Texture.aBoolean1464 = false;
        DrawingArea.initDrawingArea(32, 32, enabledSprite.myPixels);
        //DrawingArea.method336(0, 0, 32, 32, 0);
        DrawingArea.method336(32, 0, 0, 0, 32);
        Texture.method364();
        int k3 = itemDef.modelZoom;
        if (k == -1)
            k3 = (int) ((double) k3 * 1.5D);
        if (k > 0)
            k3 = (int) ((double) k3 * 1.04D);
        int l3 = Texture.anIntArray1470[itemDef.modelRotationY] * k3 >> 16;
        int i4 = Texture.anIntArray1471[itemDef.modelRotationY] * k3 >> 16;
        model.method482(itemDef.modelRotationX, itemDef.modelRotationZ,
                itemDef.modelRotationY, itemDef.modelOffset1, l3
                        + model.modelHeight / 2 + itemDef.modelOffset2, i4
                        + itemDef.modelOffset2);
        for (int i5 = 31; i5 >= 0; i5--) {
            for (int j4 = 31; j4 >= 0; j4--)
                if (enabledSprite.myPixels[i5 + j4 * 32] == 0)
                    if (i5 > 0
                            && enabledSprite.myPixels[(i5 - 1) + j4 * 32] > 1)
                        enabledSprite.myPixels[i5 + j4 * 32] = 1;
                    else if (j4 > 0
                            && enabledSprite.myPixels[i5 + (j4 - 1) * 32] > 1)
                        enabledSprite.myPixels[i5 + j4 * 32] = 1;
                    else if (i5 < 31
                            && enabledSprite.myPixels[i5 + 1 + j4 * 32] > 1)
                        enabledSprite.myPixels[i5 + j4 * 32] = 1;
                    else if (j4 < 31
                            && enabledSprite.myPixels[i5 + (j4 + 1) * 32] > 1)
                        enabledSprite.myPixels[i5 + j4 * 32] = 1;
        }

        if (k > 0) {
            for (int j5 = 31; j5 >= 0; j5--) {
                for (int k4 = 31; k4 >= 0; k4--)
                    if (enabledSprite.myPixels[j5 + k4 * 32] == 0)
                        if (j5 > 0
                                && enabledSprite.myPixels[(j5 - 1) + k4 * 32] == 1)
                            enabledSprite.myPixels[j5 + k4 * 32] = k;
                        else if (k4 > 0
                                && enabledSprite.myPixels[j5 + (k4 - 1) * 32] == 1)
                            enabledSprite.myPixels[j5 + k4 * 32] = k;
                        else if (j5 < 31
                                && enabledSprite.myPixels[j5 + 1 + k4 * 32] == 1)
                            enabledSprite.myPixels[j5 + k4 * 32] = k;
                        else if (k4 < 31
                                && enabledSprite.myPixels[j5 + (k4 + 1) * 32] == 1)
                            enabledSprite.myPixels[j5 + k4 * 32] = k;
            }
        } else if (k == 0) {
            for (int k5 = 31; k5 >= 0; k5--) {
                for (int l4 = 31; l4 >= 0; l4--)
                    if (enabledSprite.myPixels[k5 + l4 * 32] == 0
                            && k5 > 0
                            && l4 > 0
                            && enabledSprite.myPixels[(k5 - 1) + (l4 - 1) * 32] > 0)
                        enabledSprite.myPixels[k5 + l4 * 32] = 0x302020;
            }
        }
        if (itemDef.notedTemplate != -1) {
            int l5 = sprite.anInt1444;
            int j6 = sprite.anInt1445;
            sprite.anInt1444 = 32;
            sprite.anInt1445 = 32;
            sprite.drawSprite(0, 0);
            sprite.anInt1444 = l5;
            sprite.anInt1445 = j6;
        }
        if (k == 0)
            mruNodes1.removeFromCache(enabledSprite, i);
        DrawingArea.initDrawingArea(j2, i2, ai1);
        DrawingArea.setDrawingArea(j3, k2, l2, i3);
        Texture.textureInt1 = k1;
        Texture.textureInt2 = l1;
        Texture.anIntArray1472 = ai;
        Texture.aBoolean1464 = true;
        if (itemDef.stackable)
            enabledSprite.anInt1444 = 33;
        else
            enabledSprite.anInt1444 = 32;
        enabledSprite.anInt1445 = j;
        return enabledSprite;
    }

    public Model method201(int i) {
        if (stackIDs != null && i > 1) {
            int j = -1;
            for (int k = 0; k < 10; k++)
                if (i >= stackAmounts[k] && stackAmounts[k] != 0)
                    j = stackIDs[k];
            if (j != -1)
                return forID(j).method201(1);
        }
        Model model = (Model) mruNodes2.insertFromCache(id);
        if (model != null)
            return model;

        model = Model.method462(modelID);
        if (model == null)
            return null;
        if (resizeX != 128 || resizeY != 128 || resizeZ != 128)
            model.method478(resizeX, resizeZ, resizeY);
        if (modifiedModelColors != null) {
            for (int l = 0; l < modifiedModelColors.length; l++)
                model.method476(modifiedModelColors[l], originalModelColors[l]);
        }
        model.setLighting(64 + ambient, 768 + contrast, -50, -10, -50, true);
        model.aBoolean1659 = true;
        mruNodes2.removeFromCache(model, id);
        return model;
    }

    public Model method202(int i) {
        if (stackIDs != null && i > 1) {
            int j = -1;
            for (int k = 0; k < 10; k++)
                if (i >= stackAmounts[k] && stackAmounts[k] != 0)
                    j = stackIDs[k];

            if (j != -1)
                return forID(j).method202(1);
        }
        Model model = Model.method462(modelID);
        if (model == null)
            return null;
        if (modifiedModelColors != null) {
            for (int l = 0; l < modifiedModelColors.length; l++)
                model.method476(modifiedModelColors[l], originalModelColors[l]);
        }
        return model;
    }
    private void readValues(Stream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                return;
            if (opcode == 1)
                modelID = stream.readUnsignedWord();
            else if (opcode == 2)
                name = stream.readString();
            else if (opcode == 4)
                modelZoom = stream.readUnsignedWord();
            else if (opcode == 5)
                modelRotationY = stream.readUnsignedWord();
            else if (opcode == 6)
                modelRotationX = stream.readUnsignedWord();
            else if (opcode == 7) {
                modelOffset1 = stream.readUnsignedWord();
                if (modelOffset1 > 32767)
                    modelOffset1 -= 65536;//0x10000
            } else if (opcode == 8) {
                modelOffset2 = stream.readUnsignedWord();
                if (modelOffset2 > 32767)
                    modelOffset2 -= 65536;
            } else if (opcode == 9)
                unknown1 = stream.readString();
            else if (opcode == 11)
                stackable = true;
            else if (opcode == 12)
                value = stream.readDWord();
            else if (opcode == 13)
                wearPos1 = stream.readBytes();
            else if (opcode == 14)
                wearPos2 = stream.readBytes();
            else if (opcode == 16)
                membersObject = true;
            else if (opcode == 23) {
                maleModel0 = stream.readUnsignedWord();
                maleOffset = stream.readSignedByte();
            } else if (opcode == 24)
                maleModel1 = stream.readUnsignedWord();
            else if (opcode == 25) {
                femaleModel0 = stream.readUnsignedWord();
                femaleOffset = stream.readSignedByte();
            } else if (opcode == 26)
                femaleModel1 = stream.readUnsignedWord();
            else if (opcode == 27)
                wearPos3 = stream.readBytes();
            else if (opcode >= 30 && opcode < 35) {
                if (groundActions == null)
                    groundActions = new String[5];
                groundActions[opcode - 30] = stream.readString();
                if (groundActions[opcode - 30].equalsIgnoreCase("Hidden"))
                    groundActions[opcode - 30] = null;
            } else if (opcode >= 35 && opcode < 40) {
                if (itemActions == null)
                    itemActions = new String[5];
                itemActions[opcode - 35] = stream.readString();
            } else if (opcode == 40) {
                int j = stream.readUnsignedByte();
                originalModelColors = new int[j];
                modifiedModelColors = new int[j];
                for (int k = 0; k < j; k++) {
                    originalModelColors[k] = stream.readUnsignedWord();
                    modifiedModelColors[k] = stream.readUnsignedWord();
                }
            } else if (opcode == 41) {
                int var5 = stream.readUnsignedByte();
                originalTexture = new int[var5];
                modifiedTexture = new int[var5];
                for (int var4 = 0; var4 < var5; ++var4) {
                    originalTexture[var4] = (short) stream.readUnsignedShort();
                    modifiedTexture[var4] = (short) stream.readUnsignedShort();
                }
            } else if (opcode == 42)
                shiftClickDropIndex = stream.readUnsignedByte();
            else if (opcode == 65)
                searchable = true;
            else if (opcode == 75)
                weight = stream.readUnsignedWord();
            else if (opcode == 78)
                maleModel2 = stream.readUnsignedWord();
            else if (opcode == 79)
                femaleModel2 = stream.readUnsignedWord();
            else if (opcode == 90)
                maleHeadModel = stream.readUnsignedWord();
            else if (opcode == 91)
                femaleHeadModel = stream.readUnsignedWord();
            else if (opcode == 92)
                maleHeadModel2 = stream.readUnsignedWord();
            else if (opcode == 93)
                femaleHeadModel2 = stream.readUnsignedWord();
            else if (opcode == 94)
                category = stream.readUnsignedShort();
            else if (opcode == 95)
                modelRotationZ = stream.readUnsignedWord();
            else if (opcode == 97)
                unnotedTemplate = stream.readUnsignedWord() & 0xFFFF;
            else if (opcode == 98)
                notedTemplate = stream.readUnsignedWord() & 0xFFFF;
            else if (opcode >= 100 && opcode < 110) {
                if (stackIDs == null) {
                    stackIDs = new int[10];
                    stackAmounts = new int[10];
                }
                stackIDs[opcode - 100] = stream.readUnsignedShort();
                stackAmounts[opcode - 100] = stream.readUnsignedShort();
            } else if (opcode == 110)
                resizeX = stream.readUnsignedWord();
            else if (opcode == 111)
                resizeY = stream.readUnsignedWord();
            else if (opcode == 112)
                resizeZ = stream.readUnsignedWord();
            else if (opcode == 113)
                ambient = stream.readSignedByte();
            else if (opcode == 114)
                contrast = stream.readSignedByte();
            else if (opcode == 115)
                team = stream.readUnsignedByte();
            else if (opcode == 139)
                unnotedId = stream.readUnsignedWord();
            else if (opcode == 140)
                notedId = stream.readUnsignedWord();
            else if (opcode == 148)
                stream.readUnsignedWord(); // placeholder id
            else if (opcode == 149)
                stream.readUnsignedWord(); // placeholder template
            else if (opcode == 249) {
                int length = stream.readUnsignedByte();

                Map<Integer, Object> params = new HashMap<>(length);

                for (int i = 0; i < length; i++) {
                    boolean isString = stream.readUnsignedByte() == 1;
                    int key = stream.read3Bytes();
                    Object value;

                    if (isString) { value = stream.readString(); }
                    else { value = stream.readDWord(); }
                    params.put(key, value);
                }
            } else
            System.out.println("Unrecognized itemDef opcode {"+ opcode +"}");
        }
    }

    private ItemDef() { id = -1; }

    public int id;
    public String name;
    public String description;
    public String unknown1;

    private int resizeX;
    private int resizeY;
    private int resizeZ;

    public int modelRotationX;
    public int modelRotationY;
    public int modelRotationZ;

    public int value;
    public boolean stackable;
    public int[] stackIDs;
    public int[] stackAmounts;
    public int modelID;

    public byte[] wearPos1;
    public byte[] wearPos2;
    public byte[] wearPos3;

    public boolean membersObject;
    public static boolean isMembers = false;

    public int[] originalModelColors;
    public int[] modifiedModelColors;
    public int[] originalTexture;
    public int[] modifiedTexture;

    public int modelZoom;
    public int modelOffset1;
    public int modelOffset2;

    private int ambient;
    private int contrast;

    public int maleModel0;
    public int maleModel1;
    private int maleModel2;
    public byte maleOffset;
    public int maleHeadModel;
    private int maleHeadModel2;

    public int femaleModel0;
    public int femaleModel1;
    private int femaleModel2;
    public byte femaleOffset;
    public int femaleHeadModel;
    private int femaleHeadModel2;

    public int category;

    public int unnotedTemplate;
    public int unnotedId = -1;
    private int notedTemplate;
    public int notedId = -1;

    public int team;
    public int weight;

    public int shiftClickDropIndex = -2;

    private static ItemDef[] cache;
    private static int cacheIndex;
    private static int[] streamIndices;
    private static Stream stream;
    static MRUNodes mruNodes1 = new MRUNodes(100);
    public static MRUNodes mruNodes2 = new MRUNodes(50);
    public static int totalItems;
    public String[] itemActions;
    public String[] groundActions;
    public boolean searchable;
}