package net.dodian.client;

public final class Animation {

    public static void unpackConfig(StreamLoader streamLoader) {
        Stream stream = new Stream(streamLoader.getDataForName("seq.dat"));
        //net.dodian.client.Stream stream = new net.dodian.client.Stream(net.dodian.client.FileOperations.ReadFile(signlink.findcachedir()+ "seq.dat"));
        int length = stream.readUnsignedWord();
        if (anims == null)
            anims = new Animation[length + 5000];
        for (int j = 0; j < length; j++) {
            if (anims[j] == null)
                anims[j] = new Animation();
            anims[j].readValues(stream);

        }
    }

    public int method258(int i) {
        int j = anIntArray355[i];
        if (j == 0) {
            Class36 class36 = Class36.method531(anIntArray353[i]);
            if (class36 != null)
                j = anIntArray355[i] = class36.anInt636;
        }
        if (j == 0)
            j = 1;
        return j;
    }

    private void readValues(Stream stream) {
        int opcode;

        while ((opcode = stream.readUnsignedByte()) != 0) {
            int var3;
            int var4;

            if (opcode == 1) {
                //var3 = stream.readUnsignedShort();
                anInt352 = stream.readUnsignedWord();
                anIntArray353 = new int[anInt352];
                anIntArray354 = new int[anInt352];
                anIntArray355 = new int[anInt352];
                for (var4 = 0; var4 < anInt352; var4++) {
                    anIntArray353[var4] += stream.readDWord();
                    anIntArray354[var4] = -1;
                }

                for (var4 = 0; var4 < anInt352; var4++)
                    anIntArray355[var4] = stream.readUnsignedByte();

            } else if (opcode == 2)
                anInt356 = stream.readUnsignedWord();
            else if (opcode == 3) {
                var3 = stream.readUnsignedByte();
                anIntArray357 = new int[var3 + 1];
                for (var4 = 0; var4 < var3; var4++)
                    anIntArray357[var4] = stream.readUnsignedByte();
                anIntArray357[var3] = 9999999;
            } else if (opcode == 4)
                aBoolean358 = true;
            else if (opcode == 5)
                anInt359 = stream.readUnsignedByte();
            else if (opcode == 6)
                anInt360 = stream.readUnsignedWord();
            else if (opcode == 7)
                anInt361 = stream.readUnsignedWord();
            else if (opcode == 8)
                anInt362 = stream.readUnsignedByte();
            else if (opcode == 9)
                anInt363 = stream.readUnsignedByte();
            else if (opcode == 10)
                anInt364 = stream.readUnsignedByte();
            else if (opcode == 11)
                anInt365 = stream.readUnsignedByte();
            else if (opcode == 12)
                stream.readDWord();
            else
                System.out.println("Error unrecognised seq config code: " + opcode);
        }
        if (anInt352 == 0) {
            anInt352 = 1;
            anIntArray353 = new int[1];
            anIntArray353[0] = -1;
            anIntArray354 = new int[1];
            anIntArray354[0] = -1;
            anIntArray355 = new int[1];
            anIntArray355[0] = -1;
        }
        if (anInt363 == -1)
            if (anIntArray357 != null)
                anInt363 = 2;
            else
                anInt363 = 0;
        if (anInt364 == -1) {
            if (anIntArray357 != null) {
                anInt364 = 2;
                return;
            }
            anInt364 = 0;
        }
    }

    private Animation() {
        anInt356 = -1;
        aBoolean358 = false;
        anInt359 = 5;
        anInt360 = -1; //Removes shield
        anInt361 = -1; //Removes weapon
        anInt362 = 99;
        anInt363 = -1; //Stops character from moving
        anInt364 = -1;
        anInt365 = 1;
    }

    public static Animation anims[];
    public int anInt352;
    public int anIntArray353[];
    public int anIntArray354[];
    public int[] anIntArray355;
    public int anInt356;
    public int anIntArray357[];
    public boolean aBoolean358;
    public int anInt359;
    public int anInt360;
    public int anInt361;
    public int anInt362;
    public int anInt363;
    public int anInt364;
    public int anInt365;
    public static int anInt367;
}
