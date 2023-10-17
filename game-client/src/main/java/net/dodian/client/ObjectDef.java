package net.dodian.client;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public final class ObjectDef {

    public static ObjectDef forID(int i) {
        if (i > offsets.length)
            i = offsets.length - 1;
        /* Seers village fix */
		/*if (i == 25913)
			i = 15552;
		if (i == 25916 || i == 25926)
			i = 15553;
		if (i == 25917)
			i = 15554;*/
        for (int j = 0; j < 20; j++)
            if (cache[j].type == i)
                return cache[j];

        cacheIndex = (cacheIndex + 1) % 20;
        ObjectDef class46 = cache[cacheIndex];
        stream.currentOffset = offsets[i];
        class46.type = i;
        class46.setDefaults();
        class46.readValues(stream);
        customValues(class46);
        return class46;
    }

    public static void objectDump(int max) {
        try {
            FileWriter fw = new FileWriter(System.getProperty("user.home") + "/Dodian-Dev/Object Dump.txt");
            for (int i = 0; i < max; i++) {
                ObjectDef def = ObjectDef.forID(i);
                if (def != null) {
                    fw.write("case " + i + ":");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("itemDef.name = \"" + def.name + "\";");
                    if (def.actions != null) {
                        fw.write(System.getProperty("line.separator"));
                        fw.write("Object.actions = new String[" + def.actions.length + "]");
                        fw.write(System.getProperty("line.separator"));
                        for (int act = 0; act < def.actions.length && def.actions != null; act++) {
                            if (def.actions[act] != null) {
                                fw.write("Object.actions[" + act + "] = \"" + def.actions[act] + "\";");
                                fw.write(System.getProperty("line.separator"));
                            }
                        }
                    }
                    fw.write(System.getProperty("line.separator"));
                } else {
                    fw.write("case " + i + ":");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("itemDef.name = \"NULL\";");
                }
            }
            System.out.println("Done dumping objs!");
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void customValues(ObjectDef def) {
        if (def.type >= 15552 && def.type <= 15554) {
            def.modelClipped = true;
        }
        if (def.type == 2097) {
            def.actions = new String[5];
            def.actions[0] = "Smith";
        }
        if (def.type == 3994) {
            def.actions = new String[5];
            def.actions[0] = "Smelt";
            def.actions[1] = "Gold Craft";
        }
        if (def.type == 133) {
            def.name = "Dragon Ladder";
        }
        if (def.type == 7451 || def.type == 7484) {
            def.name = "Copper Rock";
        }
        if (def.type == 7452 || def.type == 7485) {
            def.name = "Tin Rock";
        }
        if (def.type == 7455 || def.type == 7488) {
            def.name = "Iron Rock";
        }
        if (def.type == 7456 || def.type == 7489) {
            def.name = "Coal Rock";
        }
        if (def.type == 7458 || def.type == 7491) {
            def.name = "Gold Rock";
        }
        if (def.type == 7459 || def.type == 7492) {
            def.name = "Mithril Rock";
        }
        if (def.type == 7460 || def.type == 7493) {
            def.name = "Adamant Rock";
        }
        if (def.type == 7461 || def.type == 7494) {
            def.name = "Runite Rock";
        }
        if (def.type == 16664 /*&& def.anInt744 == 2603 && anInt761 == 3078*/) {
            //anInt744 anInt761
            //def.actions[2] = "Boss Check";
        }
        if (def.type >= 26259 && def.type <= 26263) {
            def.name = "Hidden cave";
            def.actions = new String[5];
            def.actions[0] = "Enter";
        }
        if (def.type == 19038) {
            def.name = "Supertje's X-mas Tree";
            def.actions = new String[5];
            def.actions[0] = "Chop down";
        }
        if (def.type == 4528) {
            def.name = "The Oaktree Fountain";
            def.actions = new String[5];
        }
        if (def.type == 585) {
            def.name = "Statue of king Nozemi";
            def.actions = new String[5];
        }
        if (def.type == 586) {
            def.name = "Statue of the holy Pro Noob";
            def.actions = new String[5];
        }

    }

    public void setDefaults() {
        modelIds = null;
        modelTypes = null;
        //name = null;
        description = null;
        modifiedModelColors = null;
        originalModelColors = null;
        width = 1;
        length = 1;
        solid = true;
        impenetrable = true;
        hasActions = false;
        contouredGround = false;
        nonFlatShading = false;
        modelClipped = false;
        animation = -1;
        decorDisplacement = 16;
        ambientLighting = 0;
        contrast = 0;
        actions = null;
        mapIcon = -1;
        mapscene = -1;
        inverted = false;
        castsShadow = true;
        scaleX = 128;
        scaleY = 128;
        scaleZ = 128;
        surroundings = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        obstructsGround = false;
        removeClipping = false;
        supportItems = -1;
        varbit = -1;
        varp = -1;
        childrenIDs = null;
    }

    public void method574(OnDemandFetcher class42_sub1) {
        if (modelIds == null)
            return;
        for (int j = 0; j < modelIds.length; j++)
            class42_sub1.method560(modelIds[j] & 0xffff, 0);
    }

    public static void nullLoader() {
        mruNodes1 = null;
        mruNodes2 = null;
        offsets = null;
        cache = null;
        stream = null;
    }

    public static void unpackConfig(StreamLoader streamLoader) {
        stream = new Stream(streamLoader.getDataForName("loc.dat"));
        Stream stream = new Stream(streamLoader.getDataForName("loc.idx"));
        int totalObjects = stream.readUnsignedWord();
        System.out.println(String.format("Loaded: %d objects", totalObjects));
        offsets = new int[totalObjects];
        int metaOffset = 2;
        for (int j = 0; j < totalObjects; j++) {
            offsets[j] = metaOffset;
            metaOffset += stream.readUnsignedWord();
        }
        cache = new ObjectDef[20];
        for (int index = 0; index < 20; index++)
            cache[index] = new ObjectDef();
        objectDump(totalObjects);
    }

    public boolean method577(int i) {
        if (modelTypes == null) {
            if (modelIds == null)
                return true;
            if (i != 10)
                return true;
            boolean flag1 = true;
            for (int k = 0; k < modelIds.length; k++)
                flag1 &= Model.method463(modelIds[k] & 0xffff);
            return flag1;
        }
        for (int j = 0; j < modelTypes.length; j++)
            if (modelTypes[j] == i)
                return Model.method463(modelIds[j] & 0xffff);
        return true;
    }

    public Model method578(int i, int j, int k, int l, int i1, int j1, int k1) {
        Model model = method581(i, k1, j);
        if (model == null)
            return null;
        if (contouredGround || nonFlatShading)
            model = new Model(contouredGround, nonFlatShading, model);
        if (contouredGround) {
            int l1 = (k + l + i1 + j1) / 4;
            for (int i2 = 0; i2 < model.anInt1626; i2++) {
                int j2 = model.anIntArray1627[i2];
                int k2 = model.anIntArray1629[i2];
                int l2 = k + ((l - k) * (j2 + 64)) / 128;
                int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
                int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
                model.anIntArray1628[i2] += j3 - l1;
            }
            model.method467();
        }
        return model;
    }

    public boolean method579() {
        if (modelIds == null)
            return true;
        boolean flag1 = true;
        for (int i = 0; i < modelIds.length; i++)
            flag1 &= Model.method463(modelIds[i] & 0xffff);
        return flag1;
    }

    public ObjectDef method580() {
        int i = -1;
        if (varbit != -1) {
            VarBit varBit = VarBit.cache[varbit];
            int j = varBit.anInt648;
            int k = varBit.anInt649;
            int l = varBit.anInt650;
            int i1 = Client.anIntArray1232[l - k];
            i = clientInstance.variousSettings[j] >> k & i1;
        } else if (varp != -1)
            i = clientInstance.variousSettings[varp];
        if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
            return null;
        else
            return forID(childrenIDs[i]);
    }

    public Model method581(int j, int k, int l) {
        Model model = null;
        long l1;
        if (modelTypes == null) {
            if (j != 10)
                return null;
            l1 = (long) ((type << 8) + (j << 3) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) mruNodes2.insertFromCache(l1);
            if (model_1 != null)
                return model_1;
            if (modelIds == null)
                return null;
            boolean flag1 = inverted ^ (l > 3);
            int k1 = modelIds.length;
            for (int i2 = 0; i2 < k1; i2++) {
                int l2 = modelIds[i2];
                if (flag1)
                    l2 += 0x10000;
                model = (Model) mruNodes1.insertFromCache(l2);
                if (model == null) {
                    model = Model.method462(l2 & 0xffff);
                    if (model == null)
                        return null;
                    if (flag1)
                        model.method477();
                    mruNodes1.removeFromCache(model, l2);
                }
                if (k1 > 1)
                    aModelArray741s[i2] = model;
            }

            if (k1 > 1)
                model = new Model(k1, aModelArray741s);
        } else {
            int i1 = -1;
            for (int j1 = 0; j1 < modelTypes.length; j1++) {
                if (modelTypes[j1] != j)
                    continue;
                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;
            l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            Model model_2 = (Model) mruNodes2.insertFromCache(l1);
            if (model_2 != null)
                return model_2;
            int j2 = modelIds[i1];
            boolean flag3 = inverted ^ (l > 3);
            if (flag3)
                j2 += 0x10000;
            model = (Model) mruNodes1.insertFromCache(j2);
            if (model == null) {
                model = Model.method462(j2 & 0xffff);
                if (model == null)
                    return null;
                if (flag3)
                    model.method477();
                mruNodes1.removeFromCache(model, j2);
            }
        }
        boolean flag;
        flag = scaleX != 128 || scaleY != 128 || scaleZ != 128;
        boolean flag2;
        flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
        Model model_3 = new Model(modifiedModelColors == null, Class36
                .method532(k), l == 0 && k == -1 && !flag && !flag2, model);
        if (k != -1) {
            model_3.method469();
            model_3.method470(k);
            model_3.anIntArrayArray1658 = null;
            model_3.anIntArrayArray1657 = null;
        }
        while (l-- > 0)
            model_3.method473();
        if (modifiedModelColors != null) {
            for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
                model_3.method476(modifiedModelColors[k2],
                        originalModelColors[k2]);

        }
        if (flag)
            model_3.method478(scaleX, scaleZ, scaleY);
        if (flag2)
            model_3.method475(translateX, translateY, translateZ);
        //model_3.method479(64, 768, -50, -10, -50, !aBoolean769);
        //model_3.method479(64 + aByte737, 768 + aByte742 * 5, -50, -10, -50, !aBoolean769);

        model_3.setLighting(84, 1500, -90, -280, -70, !nonFlatShading);
        if (supportItems == 1)
            model_3.anInt1654 = model_3.modelHeight;
        mruNodes2.removeFromCache(model_3, l1);
        return model_3;
    }

    public void readValues(Stream stream) {
        int flag = -1;
        do {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            if (opcode == 1) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null) {
                        modelTypes = new int[len];
                        modelIds = new int[len];
                        for (int i = 0; i < len; i++) {
                            modelIds[i] = stream.readUnsignedWord();
                            modelTypes[i] = stream.readUnsignedByte();
                        }
                    } else {
                        stream.currentOffset += len * 3;
                    }
                }
            } else if (opcode == 2)
                name = stream.readString();
            else if (opcode == 5) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null) {
                        modelTypes = null;
                        modelIds = new int[len];
                        for (int i = 0; i < len; i++)
                            modelIds[i] = stream.readUnsignedWord();
                    } else {
                        stream.currentOffset += len * 2;
                    }
                }
            } else if (opcode == 14)
                width = stream.readUnsignedByte();
            else if (opcode == 15)
                length = stream.readUnsignedByte();
            else if (opcode == 17)
                solid = false;
            else if (opcode == 18)
                impenetrable = false;
            else if (opcode == 19)
                hasActions = (stream.readUnsignedByte() == 1);
            else if (opcode == 21)
                contouredGround = true;
            else if (opcode == 22)
                nonFlatShading = true;
            else if (opcode == 23)
                modelClipped = true;
            else if (opcode == 24) {
                animation = stream.readUnsignedShort();
                if (animation == 0xFFFF)
                    animation = -1;
            } else if (opcode == 27) {
                clipType = 1;
            } else if (opcode == 28)
                decorDisplacement = stream.readUnsignedByte();
            else if (opcode == 29)
                ambientLighting = stream.readSignedByte();
            else if (opcode >= 30 && opcode < 39) {
                if (actions == null)
                    actions = new String[5];
                actions[opcode - 30] = stream.readString();
                if (actions[opcode - 30].equalsIgnoreCase("Hidden"))
                    actions[opcode - 30] = null;
            } else if (opcode == 39)
                contrast = stream.readSignedByte() * 25;
            else if (opcode == 40) {
                int len = stream.readUnsignedByte();
                modifiedModelColors = new int[len];
                originalModelColors = new int[len];
                for (int i = 0; i < len; i++) {
                    modifiedModelColors[i] = stream.readUnsignedShort();
                    originalModelColors[i] = stream.readUnsignedShort();
                }
            } else if (opcode == 41) {
                int len = stream.readUnsignedByte();
                modifiedModelColors = new int[len];
                originalModelColors = new int[len];
                for (int i = 0; i < len; i++) {
                    modifiedModelColors[i] = stream.readUnsignedShort();
                    originalModelColors[i] = stream.readUnsignedShort();
                }
            } else if (opcode == 61)
                stream.readUnsignedShort();
            else if (opcode == 62)
                inverted = true;
            else if (opcode == 64)
                castsShadow = false;
            else if (opcode == 65)
                scaleX = stream.readUnsignedShort();
            else if (opcode == 66)
                scaleY = stream.readUnsignedShort();
            else if (opcode == 67)
                scaleZ = stream.readUnsignedWord();
            else if (opcode == 68)
                mapscene = stream.readUnsignedWord();
            else if (opcode == 69)
                surroundings = stream.readUnsignedByte();
            else if (opcode == 70)
                translateX = stream.readUnsignedShort();
            else if (opcode == 71)
                translateY = stream.readUnsignedShort();
            else if (opcode == 72)
                translateZ = stream.readUnsignedShort();
            else if (opcode == 73)
                obstructsGround = true;
            else if (opcode == 74)
                removeClipping = true;
            else if (opcode == 75)
                supportItems = stream.readUnsignedByte();
            else if (opcode == 78) {
                stream.readUnsignedShort(); // ambient sound id
                stream.readUnsignedByte();
            } else if (opcode == 79) {
                stream.readUnsignedShort();
                stream.readUnsignedShort();
                stream.readUnsignedByte();
                int len = stream.readUnsignedByte();
                for (int i = 0; i < len; i++) {
                    stream.readUnsignedShort();
                }
            } else if (opcode == 81) {
                stream.readUnsignedByte();
            } else if (opcode == 82) {
                int mapIcon = stream.readUnsignedShort();
                if (mapIcon == 0xFFFF)
                    mapIcon = -1;
            }else if (opcode == 89) {
                stream.readUnsignedShort();
            } else if (opcode == 77 || opcode == 92) {
                varbit = stream.readUnsignedShort();
                if (varbit == 0xFFFF)//65535
                    varbit = -1;
                varp = stream.readUnsignedShort();
                if (varp == 0xFFFF)//65535
                    varp = -1;
                int value = -1;

                /* Morphis */
                int len = stream.readUnsignedByte();
                childrenIDs = new int[len + 2];
                for (int i = 0; i <= len; i++) {
                    childrenIDs[i] = stream.readUnsignedWord();
                    if (childrenIDs[i] == 0xFFFF)
                        childrenIDs[i] = -1;
                    childrenIDs[i + 1] = value;
                }
            } else if (opcode == 249) {
                int length = stream.readUnsignedByte();

                Map<Integer, Object> params = new HashMap<>(length);
                for (int i = 0; i < length; i++) {
                    boolean isString = stream.readUnsignedByte() == 1;
                    int key = stream.read3Bytes();
                    Object value;
                    if (isString) {
                        value = stream.readString();
                    } else {
                        value = stream.readDWord();
                    }
                    params.put(key, value);
                }
            } else
                System.out.println("invalid Object opcode:" + opcode);
        } while (true);
        if (flag == -1 && name != "null" && name != null) {
            hasActions = modelIds != null
                    && (modelTypes == null || modelTypes[0] == 10);
            if (actions != null)
                hasActions = true;
        }
        if (removeClipping) {
            solid = false;
            impenetrable = false;
        }
        if (supportItems == -1)
            supportItems = solid ? 1 : 0;
    }

    public ObjectDef() { type = -1; }

    public boolean obstructsGround;
    public byte ambientLighting;
    public int translateX;
    public String name;
    public int scaleZ;
    public static final Model[] aModelArray741s = new Model[4];
    public int contrast;
    public int width;
    public int translateY;
    public int mapIcon;
    public int[] originalModelColors;
    public int scaleX;
    public int varp;
    public boolean inverted;
    public static boolean lowMem;
    public static Stream stream;
    public int type;
    public static int[] offsets;
    public boolean impenetrable;
    public int mapscene;
    public int childrenIDs[];
    public int supportItems;
    public static int length;
    public boolean contouredGround;
    public boolean modelClipped;
    public static Client clientInstance;
    public boolean removeClipping;
    public boolean solid;
    public int surroundings;
    public boolean nonFlatShading;
    public static int cacheIndex;
    public int scaleY;
    public int[] modelIds;
    public int varbit;
    public int decorDisplacement;
    public int[] modelTypes;
    public byte description[];
    public boolean hasActions;
    public boolean castsShadow;
    public static MRUNodes mruNodes2 = new MRUNodes(30);
    public int animation;
    public static ObjectDef[] cache;
    public int translateZ;
    public int[] modifiedModelColors;
    public static MRUNodes mruNodes1 = new MRUNodes(500);
    public String[] actions;
    int clipType = 2;
}