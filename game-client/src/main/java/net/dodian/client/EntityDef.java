package net.dodian.client;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class EntityDef {

    public static EntityDef forID(int i) {
        for (int j = 0; j < 20; j++)
            if (cache[j].interfaceType == (long) i)
                return cache[j];
        anInt56 = (anInt56 + 1) % 20;
        EntityDef entityDef = cache[anInt56] = new EntityDef();
        stream.currentOffset = streamIndices[i];
        entityDef.interfaceType = i;
        entityDef.readValues(stream);
        customValues(entityDef);
        return entityDef;
    }

    private static void customValues(EntityDef def) {
        int id = (int) def.interfaceType;
        if (id == 1510)
            def.actions = new String[]{"Shrimp", null, "Trout", null, null};
        if (id == 1511)
            def.actions = new String[]{"Lobster", null, "Swordfish", null, null};
        if (id == 1514)
            def.actions = new String[]{"Monkfish", null, "Shark", null, null};
        if (id == 1517)
            def.actions = new String[]{"Sea Turtle", null, "Manta Ray", null, null};
    }

    public Model method160() {
        if (childrenIDs != null) {
            EntityDef entityDef = method161();
            if (entityDef == null)
                return null;
            else
                return entityDef.method160();
        }
        if (chatheadModels == null)
            return null;
        boolean flag1 = false;
        for (int i = 0; i < chatheadModels.length; i++)
            if (!Model.method463(chatheadModels[i]))
                flag1 = true;

        if (flag1)
            return null;
        Model aclass30_sub2_sub4_sub6s[] = new Model[chatheadModels.length];
        for (int j = 0; j < chatheadModels.length; j++)
            aclass30_sub2_sub4_sub6s[j] = Model.method462(chatheadModels[j]);
        Model model;
        if (aclass30_sub2_sub4_sub6s.length == 1)
            model = aclass30_sub2_sub4_sub6s[0];
        else
            model = new Model(aclass30_sub2_sub4_sub6s.length,
                    aclass30_sub2_sub4_sub6s);
        if (recolorToFind != null) {
            for (int k = 0; k < recolorToFind.length; k++)
                model.method476(recolorToFind[k], recolorToReplace[k]);
        }
        return model;
    }

    public EntityDef method161() {
        int j = -1;
        if (varbitId != -1) {
            VarBit varBit = VarBit.cache[varbitId];
            int k = varBit.anInt648;
            int l = varBit.anInt649;
            int i1 = varBit.anInt650;
            int j1 = Client.anIntArray1232[i1 - l];
            j = clientInstance.variousSettings[k] >> l & j1;
        } else if (varpIndex != -1)
            j = clientInstance.variousSettings[varpIndex];
        if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1)
            return null;
        else
            return forID(childrenIDs[j]);
    }

    public static void unpackConfig(StreamLoader streamLoader) {
        stream = new Stream(streamLoader.getDataForName("npc.dat"));
        Stream stream2 = new Stream(streamLoader.getDataForName("npc.idx"));
        int totalNPCs = stream2.readUnsignedWord();
        streamIndices = new int[totalNPCs];
        int i = 2;
        for (int j = 0; j < totalNPCs; j++) {
            streamIndices[j] = i;
            i += stream2.readUnsignedWord();
        }

        cache = new EntityDef[20];
        for (int k = 0; k < 20; k++)
            cache[k] = new EntityDef();
        for (int index = 0; index < totalNPCs; index++) {
            EntityDef ed = forID(index);
            if (ed == null)
                continue;
            if (ed.name == null)
                continue;
        }
        npcDump(totalNPCs);
    }

    /*
     *Dump item variables to a file
     */
    public static void npcDump(int max) {
        try {
            FileWriter fw = new FileWriter(System.getProperty("user.home") + "/Dodian-Dev/Npc Dump.txt");
            for (int i = 0; i < max; i++) {
                EntityDef def = EntityDef.forID(i);
                if (def != null) {
                    fw.write("Npc Id = " + i + ":");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Npc Name = \"" + def.name + "\";");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Npc Combat = " + def.combatLevel + ";");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Actions = " + Arrays.toString(def.actions) +";");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Stand Anim = " + def.standAnim + ";");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Walk Anim = " + def.walkAnim + ";");
                    fw.write(System.getProperty("line.separator"));
                    fw.write("Npc Models = " + Arrays.toString(def.models) + ";");
                    fw.write(System.getProperty("line.separator"));
                    if (def.description != null) {
                        String test = new String(def.description, StandardCharsets.UTF_8);
                        fw.write("Npc.description = " + test + ";");
                        fw.write(System.getProperty("line.separator"));
                    }
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
            System.out.println("Done dumping npcs!");
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void nullLoader() {
        mruNodes = null;
        streamIndices = null;
        cache = null;
        stream = null;
    }

    public Model method164(int j, int k, int ai[]) {
        if (childrenIDs != null) {
            EntityDef entityDef = method161();
            if (entityDef == null)
                return null;
            else
                return entityDef.method164(j, k, ai);
        }
        Model model = (Model) mruNodes.insertFromCache(interfaceType);
        if (model == null) {
            boolean flag = false;
            for (int i1 = 0; i1 < models.length; i1++)
                if (!Model.method463(models[i1]))
                    flag = true;

            if (flag)
                return null;
            Model aclass30_sub2_sub4_sub6s[] = new Model[models.length];
            for (int j1 = 0; j1 < models.length; j1++)
                aclass30_sub2_sub4_sub6s[j1] = Model
                        .method462(models[j1]);

            if (aclass30_sub2_sub4_sub6s.length == 1)
                model = aclass30_sub2_sub4_sub6s[0];
            else
                model = new Model(aclass30_sub2_sub4_sub6s.length,
                        aclass30_sub2_sub4_sub6s);
            if (recolorToFind != null) {
                for (int k1 = 0; k1 < recolorToFind.length; k1++)
                    model.method476(recolorToFind[k1], recolorToReplace[k1]);

            }
            model.method469();
            model.setLighting(64 + ambient, 850 + contrast, -30, -50, -30, true);
            mruNodes.removeFromCache(model, interfaceType);
        }
        Model model_1 = Model.aModel_1621;
        model_1.method464(model, Class36.method532(k) & Class36.method532(j));
        if (k != -1 && j != -1)
            model_1.method471(ai, j, k);
        else if (k != -1)
            model_1.method470(k);
        if (widthScale != 128 || heightScale != 128)
            model_1.method478(widthScale, widthScale, heightScale);
        model_1.method466();
        model_1.anIntArrayArray1658 = null;
        model_1.anIntArrayArray1657 = null;
        if (size == 1)
            model_1.aBoolean1659 = true;
        return model_1;
    }

    public void readValues(Stream stream) {
        do {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                return;
            if (opcode == 1) {
                int length = stream.readUnsignedByte();
                models = new int[length];
                for (int index = 0; index < length; index++)
                    models[index] = stream.readUnsignedShort();
            } else if (opcode == 2)
                name = stream.readString();
            else if (opcode == 3)
                description = stream.readBytes();
            else if (opcode == 12)
                size = stream.readSignedByte();
            else if (opcode == 13)
                standAnim = stream.readUnsignedShort();
            else if (opcode == 14)
                walkAnim = stream.readUnsignedShort();
            else if (opcode == 15)
                stream.readUnsignedShort();
            else if (opcode == 16)
                stream.readUnsignedShort();
            else if (opcode == 17) {
                walkAnim = stream.readUnsignedShort();
                rotate180Animation = stream.readUnsignedShort();
                rotate90RightAnimation = stream.readUnsignedShort();
                rotate90LeftAnimation = stream.readUnsignedShort();
                if (rotate180Animation == 65535) {
                    rotate180Animation = walkAnim;
                }
                if (rotate90RightAnimation == 65535) {
                    rotate90RightAnimation = walkAnim;
                }
                if (rotate90LeftAnimation == 65535) {
                    rotate90LeftAnimation = walkAnim;
                }
            } else if (opcode == 18) {
                stream.readUnsignedShort();
            } else if (opcode >= 30 && opcode < 35) {//40
                if (actions == null)
                    actions = new String[5];
                actions[opcode - 30] = stream.readString();
                if (actions[opcode - 30].equalsIgnoreCase("hidden"))
                    actions[opcode - 30] = null;
            } else if (opcode == 40) {
                int length = stream.readUnsignedByte();
                recolorToFind = new int[length];
                recolorToReplace = new int[length];
                for (int index = 0; index < length; index++) {
                    recolorToFind[index] = stream.readUnsignedShort();
                    recolorToReplace[index] = stream.readUnsignedShort();
                }
            } else if (opcode == 41) {
                int length = stream.readUnsignedByte();
                recolorToFind = new int[length];
                recolorToReplace = new int[length];
                for (int index = 0; index < length; index++) {
                    recolorToFind[index] = stream.readUnsignedShort();
                    recolorToReplace[index] = stream.readUnsignedShort();
                }
            } else if (opcode == 60) {
                int length = stream.readUnsignedByte();
                chatheadModels = new int[length];
                for (int index = 0; index < length; index++)
                    chatheadModels[index] = stream.readUnsignedShort();
            } else if (opcode == 90)
                stream.readUnsignedShort();
            else if (opcode == 91)
                stream.readUnsignedShort();
            else if (opcode == 92)
                stream.readUnsignedShort();
            else if (opcode == 93)
                isMinimapVisible = false;
            else if (opcode == 95)
                combatLevel = stream.readUnsignedShort();
            else if (opcode == 97)
                widthScale = stream.readUnsignedShort();
            else if (opcode == 98)
                heightScale = stream.readUnsignedShort();
            else if (opcode == 99)
                hadRenderPriority = true;
            else if (opcode == 100)
                ambient = stream.readSignedByte();
            else if (opcode == 101)
                contrast = stream.readSignedByte() * 5;
            else if (opcode == 102)
                headIcon = stream.readUnsignedShort();
            else if (opcode == 103)
                rotationSpeed = stream.readUnsignedShort();
            else if (opcode == 106) {
                varbitId = stream.readUnsignedShort();
                if (varbitId == 65535)
                    varbitId = -1;
                varpIndex = stream.readUnsignedShort();
                if (varpIndex == 65535)
                    varpIndex = -1;
                int length = stream.readUnsignedByte();
                childrenIDs = new int[length + 2];//+1
                for (int index = 0; index <= length; index++) {
                    childrenIDs[index] = stream.readUnsignedShort();
                    if (childrenIDs[index] == '\uffff')//65535
                        childrenIDs[index] = -1;
                }
                childrenIDs[length + 1] = -1;
            } else if (opcode == 107)
                isInteractable = false;
            else if (opcode == 109)
                rotationFlag = false;
            else if (opcode == 111)
                isPet = true;
            else if (opcode == 118) {
                varbitId = stream.readUnsignedShort();
                if (varbitId == 65535) {
                    varbitId = -1;
                }
                varpIndex = stream.readUnsignedShort();
                if (varpIndex == 65535) {
                    varpIndex = -1;
                }
                int var = stream.readUnsignedShort();
                if (var == 0xFFFF) {
                    var = -1;
                }
                int length = stream.readUnsignedByte();
                childrenIDs = new int[length +2];
                for (int index = 0; index <= length; ++index) {
                    childrenIDs[index] = stream.readUnsignedShort();
                    if (childrenIDs[index] == '\uffff') {
                        childrenIDs[index] = -1;
                    }
                }
                childrenIDs[length + 1] = var;
            } else if (opcode == 249) {
                int length = stream.readUnsignedByte();
                Map<Integer, Object> prams = new HashMap<>(length);
                for (int index = 0; index < length; index++) {
                    boolean isString = stream.readUnsignedByte() == 1;
                    int key = stream.readUSmart2();
                    Object value;
                    if (isString) {
                        value = stream.readString();
                    } else {
                        value = stream.readDWord();
                    }
                    prams.put(key, value);
                }
            } else {
                System.err.printf("Unrecognized EntityDef opcode {"+ opcode +"}");
            }
        } while (true);
    }

    public EntityDef() {
        rotate180Animation = -1;
        rotate90LeftAnimation = -1;
        rotate90RightAnimation = -1;
        varbitId = -1;
        varpIndex = -1;
        combatLevel = -1;
        anInt64 = 1834;
        walkAnim = -1;
        size = 1;
        headIcon = -1;
        standAnim = -1;
        interfaceType = -1L; //Id!
        rotationSpeed = 32;
        isInteractable = true;
        widthScale = 128;
        heightScale = 128;
        isMinimapVisible = true;
        hadRenderPriority = false;
    }

    public static int anInt56;
    public int varbitId;
    public int varpIndex;
    public int rotate180Animation;
    public int rotate90RightAnimation;
    public int rotate90LeftAnimation;
    public static Stream stream;
    public int combatLevel;
    public final int anInt64;
    public String name;
    public String actions[];
    public int walkAnim;
    public byte size;
    public static int[] streamIndices;
    public int[] chatheadModels;
    public int headIcon;
    public int[] recolorToFind;
    public int[] recolorToReplace;
    public int standAnim;
    public long interfaceType;
    public int rotationSpeed;
    public static EntityDef[] cache;
    public static Client clientInstance;
    public boolean isInteractable;
    public int ambient;
    public int widthScale;
    public int heightScale;
    public boolean isPet;
    public boolean rotationFlag;
    public boolean isMinimapVisible;
    public int childrenIDs[];
    public byte description[];
    public int contrast;
    public boolean hadRenderPriority;
    public int[] models;
    public static MRUNodes mruNodes = new MRUNodes(30);
}