package net.dodian.client;

public final class SpotAnim {

    public static void unpackConfig(StreamLoader streamLoader) {
        Stream stream = new Stream(streamLoader.getDataForName("spotanim.dat"));
        int length = stream.readUnsignedWord();
        if (cache == null)
            cache = new SpotAnim[length];
        for (int j = 0; j < length; j++) {
            if (cache[j] == null)
                cache[j] = new SpotAnim();
            cache[j].anInt404 = j;
            cache[j].readValues(stream);
        }
    }

    public void readValues(Stream stream) {
        do {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                return;
            if (opcode == 1)
                modelId = stream.readUnsignedShort();
            else if (opcode == 2) {
                animationId = stream.readUnsignedShort();
                if (Animation.anims != null)
                    aAnimation_407 = Animation.anims[animationId];
            } else if (opcode == 4)
                resizeX = stream.readUnsignedShort();
            else if (opcode == 5)
                resizeY = stream.readUnsignedShort();
            else if (opcode == 6)
                rotation = stream.readUnsignedShort();
            else if (opcode == 7)
                ambient = stream.readUnsignedByte();
            else if (opcode == 8)
                contrast = stream.readUnsignedByte();
            else if (opcode == 40) {
                int var3 = stream.readUnsignedByte();
                recolorToFind = new int[var3];
                recolorToReplace = new int[var3];

                for (int var4 = 0; var4 < var3; var4++) {
                    recolorToFind[var4] = stream.readUnsignedShort();
                    recolorToReplace[var4] = stream.readUnsignedShort();
                }
            } else if (opcode == 41) {
                int var3 = stream.readUnsignedByte();
                textureToFind = new int[var3];
                textureToReplace = new int[var3];

                for (int var4 = 0; var4 < var3; ++var4) {
                    textureToFind[var4] = (short) stream.readUnsignedShort();
                    textureToReplace[var4] = (short) stream.readUnsignedShort();
                }
            } else
                System.out.println("Error unrecognised spot-anim config code: " + opcode);
        } while (true);
    }

    public Model getModel() {
        Model model = (Model) aMRUNodes_415.insertFromCache(anInt404);
        if (model != null)
            return model;
        model = Model.method462(modelId);
        if (model == null)
            return null;
        for (int i = 0; i < 6; i++)
            if (recolorToFind[0] != 0)
                model.method476(recolorToFind[i], recolorToReplace[i]);

        aMRUNodes_415.removeFromCache(model, anInt404);
        return model;
    }

    private SpotAnim() {
        animationId = -1;
        recolorToFind = new int[6];
        recolorToReplace = new int[6];
        resizeX = 128;
        resizeY = 128;
    }

    public static SpotAnim[] cache;
    private int anInt404;
    private int modelId;
    private int animationId;
    public Animation aAnimation_407;
    private int[] recolorToFind;
    private int[] recolorToReplace;
    private int[] textureToFind;
    private int[] textureToReplace;
    public int resizeX;
    public int resizeY;
    public int rotation;
    public int ambient;
    public int contrast;
    public static MRUNodes aMRUNodes_415 = new MRUNodes(30);
}