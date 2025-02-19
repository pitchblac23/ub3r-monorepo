package net.dodian.client;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;

public class RSApplet extends Applet implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, WindowListener {

    private static final long serialVersionUID = 1L;

    public static int hotKey = 508;
    public boolean resizing;
    private int anInt4;
    private int delayTime;
    int minDelay;
    private final long[] aLongArray7 = new long[10];
    int fps;
    boolean shouldDebug;
    int myWidth;
    int myHeight;
    Graphics graphics;
    RSImageProducer fullGameScreen;
    RSFrame gameFrame;
    private boolean shouldClearScreen;
    boolean awtFocus;
    int idleTime;
    int clickMode2;
    public int mouseX;
    public int mouseY;
    private int clickMode1;
    private int clickX;
    private int clickY;
    private long clickTime;
    public int clickMode3;
    public int saveClickX;
    public int saveClickY;
    long aLong29;
    final int[] keyArray = new int[128];
    private final int[] charQueue = new int[128];
    public boolean isLoading;
    private int readIndex;
    private int writeIndex;
    public static int anInt34;
    public boolean isApplet;

    public void rebuildFrame(boolean undecorated, int width, int height, boolean resizable, boolean full) {
        boolean createdByApplet = (isApplet && !full);
        myWidth = width;
        myHeight = height;
        if (gameFrame != null) {
            gameFrame.dispose();
        }
        if (!createdByApplet) {
            gameFrame = new RSFrame(this, width, height, undecorated, resizable);
            gameFrame.addWindowListener(this);
        }
        graphics = (createdByApplet ? this : gameFrame).getGraphics();
        if (!createdByApplet) {
            getGameComponent().addMouseWheelListener(this);
            getGameComponent().addMouseListener(this);
            getGameComponent().addMouseMotionListener(this);
            getGameComponent().addKeyListener(this);
            getGameComponent().addFocusListener(this);
        }
    }

    final void createClientFrame(int w, int h) {
        isApplet = false;
        myWidth = w;
        myHeight = h;
        gameFrame = new RSFrame(this, myWidth, myHeight, Client.clientSize == 2, Client.clientSize == 1);
        gameFrame.setFocusTraversalKeysEnabled(false);
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());
        startRunnable(this, 1);
    }

    final void initClientFrame(int w, int h) {
        isApplet = true;
        myWidth = w;
        myHeight = h;
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());
        startRunnable(this, 1);
    }

    public void run() {
        getGameComponent().addMouseListener(this);
        getGameComponent().addMouseMotionListener(this);
        getGameComponent().addKeyListener(this);
        getGameComponent().addFocusListener(this);
        getGameComponent().addMouseWheelListener(this);
        oldClick = System.currentTimeMillis();
        if (gameFrame != null) {
            gameFrame.addWindowListener(this);
        }
        drawLoadingText(0, "Loading...");
        startUp();
        int i = 0;
        int j = 256;
        int k = 1;
        int l = 0;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++) {
            aLongArray7[j1] = System.currentTimeMillis();
        }
        do {
            if (anInt4 < 0) {
                break;
            }
            if (anInt4 > 0) {
                anInt4--;
                if (anInt4 == 0) {
                    exit();
                    return;
                }
            }
            int k1 = j;
            int i2 = k;
            j = 300;
            k = 1;
            long l2 = System.currentTimeMillis();
            if (aLongArray7[i] == 0L) {
                j = k1;
                k = i2;
            } else if (l2 > aLongArray7[i]) {
                j = (int) ((long) (2560 * delayTime) / (l2 - aLongArray7[i]));
            }
            if (j < 25) {
                j = 25;
            }
            if (j > 256) {
                j = 256;
                k = (int) ((long) delayTime - (l2 - aLongArray7[i]) / 10L);
            }
            if (k > delayTime) {
                k = delayTime;
            }
            aLongArray7[i] = l2;
            i = (i + 1) % 10;
            if (k > 1) {
                for (int j2 = 0; j2 < 10; j2++) {
                    if (aLongArray7[j2] != 0L) {
                        aLongArray7[j2] += k;
                    }
                }

            }
            if (k < minDelay) {
                k = minDelay;
            }
            try {
                Thread.sleep(k);
            } catch (InterruptedException interruptedexception) {
                i1++;
            }
            for (; l < 256; l += j) {
                clickMode3 = clickMode1;
                saveClickX = clickX;
                saveClickY = clickY;
                aLong29 = clickTime;
                clickMode1 = 0;
                processGameLoop();
                readIndex = writeIndex;
            }

            l &= 0xff;
            if (delayTime > 0) {
                fps = (1000 * j) / (delayTime * 256);
            }
            processDrawing();
            if (shouldDebug) {
                System.out.println("ntime:" + l2);
                for (int k2 = 0; k2 < 10; k2++) {
                    int i3 = ((i - k2 - 1) + 20) % 10;
                    System.out.println("otim" + i3 + ":" + aLongArray7[i3]);
                }

                System.out.println("fps:" + fps + " ratio:" + j + " count:" + l);
                System.out.println("del:" + k + " deltime:" + delayTime + " mindel:" + minDelay);
                System.out.println("intex:" + i1 + " opos:" + i);
                shouldDebug = false;
                i1 = 0;
            }
        } while (true);
        if (anInt4 == -1) {
            exit();
        }
    }

    public boolean badZoomPosition() {
        if ((mouseX > 0 && mouseY > 340 && mouseX < 510 && mouseY < 500) ||
                (mouseY > 210 && mouseY < 473 && mouseX > 514 && mouseX < 762)) {
            return true;
        }
        return false;
    }

    public boolean tabOpen() {
        if ((Client.tabInterfaceIDs[Client.tabID] == 638) || (Client.tabInterfaceIDs[Client.tabID] == 147) || (Client.tabInterfaceIDs[Client.tabID] == 638) || (Client.tabInterfaceIDs[Client.tabID] == 18128)
                || (Client.tabInterfaceIDs[Client.tabID] == 5715) || (Client.tabInterfaceIDs[Client.tabID] == 5065) || (Client.openInterfaceID == 5292)) {
            return true;
        }
        return false;
    }

    private void exit() {
        anInt4 = -2;
        cleanUpForQuit();
        if (gameFrame != null) {
            try {
                Thread.sleep(1000L);
            } catch (Exception ignored) {
            }
            try {
                System.exit(0);
            } catch (Throwable ignored) {
            }
        }
    }

    final void method4(int i) {
        delayTime = 1000 / i;
    }

    public final void start() {
        if (anInt4 >= 0) {
            anInt4 = 0;
        }
    }

    public final void stop() {
        if (anInt4 >= 0) {
            anInt4 = 4000 / delayTime;
        }
    }

    public final void destroy() {
        anInt4 = -1;
        try {
            Thread.sleep(5000L);
        } catch (Exception ignored) {
        }
        if (anInt4 == -1) {
            exit();
        }
    }

    public final void update(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    public final void paint(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        if (mouseX > 0 && mouseX < 512 && mouseY > Client.clientHeight - 165 && mouseY < Client.clientHeight - 25) {
            if (rotation < 0) {
                if (Client.getClient().aClass9_1059.scrollPosition < 1) {
                    Client.inputTaken = true;
                } else {
                    Client.chatScrollPos = Client.chatScrollPos + 30;
                    Client.inputTaken = true;
                }
            } else {
                if (Client.chatScrollPos < 1) {
                    Client.chatScrollPos = 0;
                    Client.inputTaken = true;
                } else {
                    Client.chatScrollPos = Client.chatScrollPos - 30;
                    Client.inputTaken = true;
                }
            }
        } else if (mouseX > 550 && mouseY > 207 && mouseX < 743 && mouseY < 468) {
            RSInterface inter = RSInterface.interfaceCache[Client.tabInterfaceIDs[Client.tabID]];
            //net.dodian.client.RSInterface inter = net.dodian.client.RSInterface.interfaceList[client.tabbedInterfaces[client.anInt1221]];
            for (int i = 0; i < inter.children.length; i++) {
                RSInterface child = RSInterface.interfaceCache[inter.children[i]];
                if (child == null) {
                    continue;
                }
                if (child.type != 0) {// must be layer type interface.
                    continue;
                }
                if (child.scrollMax > child.height) { // has a scrollbar!
                    if (/*true || */ mouseX > 549 + inter.childX[i] &&
                            mouseX < 549 + inter.childX[i] + child.width &&
                            mouseY > 205 + inter.childY[i] &&
                            mouseY < 205 + inter.childY[i] + child.height) {
                        if (rotation < 0) {
                            child.scrollPosition = child.scrollPosition - 30;
                            if (child.scrollPosition > child.scrollMax) {
                                child.scrollPosition = child.scrollMax;
                            }
                            Client.needDrawTabArea = true;
                        } else {
                            child.scrollPosition = child.scrollPosition + 30;
                            if (child.scrollPosition < 0) {
                                child.scrollPosition = 0;
                            }
                            Client.needDrawTabArea = true;
                        }
                        break;
                    }
                }
            }
        } else
            handleInterfaceScrolling(event);
        if (event.isControlDown()) {
            if (rotation == -1 && !tabOpen() && !badZoomPosition()) {
                if (Client.zoom > -1)
                    Client.zoom--;
            } else {
                if (Client.zoom < 2 && !tabOpen() && !badZoomPosition())
                    Client.zoom++;
            }
        }
    }

    public void handleInterfaceScrolling(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        int positionX = 0;
        int positionY = 0;
        int width = 0;
        int height = 0;
        int offsetX = 0;
        int offsetY = 0;
        int childID = 0;
        /* Tab interface scrolling */
        int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
        if (tabInterfaceID != -1) {
            RSInterface tab = RSInterface.interfaceCache[tabInterfaceID];
            offsetX = Client.clientSize == 0 ? Client.clientWidth - 218 : (Client.clientSize == 0 ? 28 : Client.clientWidth - 197);
            offsetY = Client.clientSize == 0 ? Client.clientHeight - 298 : (Client.clientSize == 0 ? 37 : Client.clientHeight - (Client.clientWidth >= 1000 ? 37 : 74) - 267);
            if (tab.children == null) {
                return;
            }
            for (int index = 0; index < tab.children.length; index++) {
                if (RSInterface.interfaceCache[tab.children[index]].scrollMax > 0) {
                    childID = index;
                    positionX = tab.childX[index];
                    positionY = tab.childY[index];
                    width = RSInterface.interfaceCache[tab.children[index]].width;
                    height = RSInterface.interfaceCache[tab.children[index]].height;
                    break;
                }
            }
            if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
                if (RSInterface.interfaceCache[tab.children[childID]].scrollPosition > 0) {
                    RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
                    return;
                } else {
                    if (rotation > 0) {
                        RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
                        return;
                    }
                }
            }
        }
        /* Main interface scrolling */
        if (Client.openInterfaceID != -1) {
            RSInterface rsi = RSInterface.interfaceCache[Client.openInterfaceID];
            offsetX = Client.clientSize == 0 ? 4 : (Client.clientWidth / 2) - 356;
            offsetY = Client.clientSize == 0 ? 4 : (Client.clientHeight / 2) - 230;
            for (int index = 0; index < rsi.children.length; index++) {
                if (RSInterface.interfaceCache[rsi.children[index]].scrollMax > 0) {
                    childID = index;
                    positionX = rsi.childX[index];
                    positionY = rsi.childY[index];
                    width = RSInterface.interfaceCache[rsi.children[index]].width;
                    height = RSInterface.interfaceCache[rsi.children[index]].height;
                    break;
                }
            }
            if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
                if (RSInterface.interfaceCache[rsi.children[childID]].scrollPosition > 0) {
                    RSInterface.interfaceCache[rsi.children[childID]].scrollPosition += rotation * 30;
                    return;
                } else {
                    if (rotation > 0) {
                        RSInterface.interfaceCache[rsi.children[childID]].scrollPosition += rotation * 30;
                        return;
                    }
                }
            }
        }
    }

    public int clickType;
    public final int LEFT = 0;
    public final int RIGHT = 1;
    public final int DRAG = 2;
    public final int RELEASED = 3;
    public final int MOVE = 4;
    public int releasedX;
    public int releasedY;
    public boolean mouseWheelDown;
    public long oldClick;

    public final void mousePressed(MouseEvent e) {
        if(System.currentTimeMillis() - oldClick >= 200) {
            oldClick = System.currentTimeMillis();
            int type = e.getButton();
            int x = e.getX();
            int y = e.getY();
            if (gameFrame != null) {
                Insets insets = gameFrame.getInsets();
                x -= insets.left;//4
                y -= insets.top;//22
            }
            idleTime = 0;
            clickX = x;
            clickY = y;
            clickTime = System.currentTimeMillis();
            if (SwingUtilities.isMiddleMouseButton(e)) {
                mouseWheelDown = true;
                mouseWheelX = x;
                mouseWheelY = y;
                return;
            }
            if (SwingUtilities.isRightMouseButton(e)) {
                clickType = RIGHT;
                clickMode1 = 2;
                clickMode2 = 2;
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                clickType = LEFT;
                clickMode1 = 1;
                clickMode2 = 1;
            }
        }
    }

    public final void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;//4
            y -= insets.top;//22
        }
        releasedX = x;
        releasedY = y;
        idleTime = 0;
        clickMode2 = 0;
        clickType = RELEASED;
        mouseWheelDown = false;
    }

    public final void mouseClicked(MouseEvent mouseevent) {
    }

    public final void mouseEntered(MouseEvent mouseevent) {
    }

    public final void mouseExited(MouseEvent mouseevent) {
        idleTime = 0;
        mouseX = -1;
        mouseY = -1;
    }

    public int mouseWheelX;
    public int mouseWheelY;

    public final void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;//4
            y -= insets.top;//22
        }
        if (mouseWheelDown) {
            y = mouseWheelX - e.getX();
            int k = mouseWheelY - e.getY();
            Client.getClient().mouseWheelDragged(y, -k);
            mouseWheelX = e.getX();
            mouseWheelY = e.getY();
            return;
        }
		/*if (client.isApplet) {
			x -= (client.appletWidth / 2) - (client.clientWidth / 2);
		}*/
        idleTime = 0;
        mouseX = x;
        mouseY = y;
        clickType = DRAG;
    }

    public final void mouseMoved(MouseEvent mouseevent) {
        int x = mouseevent.getX();
        int y = mouseevent.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;//4
            y -= insets.top;//22
        }
        idleTime = 0;
        mouseX = x;
        mouseY = y;
        clickType = MOVE;
    }

    public final void keyPressed(KeyEvent keyevent) {
        idleTime = 0;
        int i = keyevent.getKeyCode();
        int j = keyevent.getKeyChar();
        Client c = Client.getClient();
        if (i == KeyEvent.VK_ESCAPE && Client.openInterfaceID != -1) {
            c.clearTopInterfaces();
            return;
        }
        if (i == KeyEvent.VK_SHIFT) {
            c.shiftIsDown = true;
        }
        if (i == KeyEvent.VK_ESCAPE) {
            net.dodian.client.Client.setTab(3);
        } else if (i == KeyEvent.VK_F1) {
            net.dodian.client.Client.setTab(0);
        } else if (i == KeyEvent.VK_F2) {
            net.dodian.client.Client.setTab(1);
        } else if (i == KeyEvent.VK_F3) {
            net.dodian.client.Client.setTab(2);
        } else if (i == KeyEvent.VK_F4) {
            net.dodian.client.Client.setTab(4);
        } else if (i == KeyEvent.VK_F5) {
            net.dodian.client.Client.setTab(5);
        } else if (i == KeyEvent.VK_F6) {
            net.dodian.client.Client.setTab(6);
        } else if (i == KeyEvent.VK_F7) {
            net.dodian.client.Client.setTab(10);
        } else if (i == KeyEvent.VK_F8) {
            net.dodian.client.Client.setTab(11);
        } else if (i == KeyEvent.VK_F12) {
            takeScreenshot();
        }
        if (j < 30)
            j = 0;
        if (i == 37)
            j = 1;
        if (i == 39)
            j = 2;
        if (i == 38)
            j = 3;
        if (i == 40)
            j = 4;
        if (i == 17)
            j = 5;
        if (i == 8)
            j = 8;
        if (i == 127)
            j = 8;
        if (i == 9)
            j = 9;
        if (i == 10)
            j = 10;
        if (i >= 112 && i <= 123)
            j = (1008 + i) - 112;
        if (i == 36)
            j = 1000;
        if (i == 35)
            j = 1001;
        if (i == 33)
            j = 1002;
        if (i == 34)
            j = 1003;
        if (j > 0 && j < 128)
            keyArray[j] = 1;
        if (j > 4) {
            charQueue[writeIndex] = j;
            writeIndex = writeIndex + 1 & 0x7f;
        }
    }

    public final void keyReleased(KeyEvent keyevent) {
        idleTime = 0;
        int i = keyevent.getKeyCode();
        char c = keyevent.getKeyChar();
        Client cl = Client.getClient();
        if (i == KeyEvent.VK_SHIFT) {
            cl.shiftIsDown = false;
        }
        if (i == 17) {
            resizing = false;
        }
        if (c < '\036') {
            c = '\0';
        }
        if (i == 37) {
            c = '\001';
        }
        if (i == 39) {
            c = '\002';
        }
        if (i == 38) {
            c = '\003';
        }
        if (i == 40) {
            c = '\004';
        }
        if (i == 17) {
            c = '\005';
        }
        if (i == 8) {
            c = '\b';
        }
        if (i == 127) {
            c = '\b';
        }
        if (i == 9) {
            c = '\t';
        }
        if (i == 10) {
            c = '\n';
        }
        if (c > 0 && c < '\200') {
            keyArray[c] = 0;
        }
    }

    public final void keyTyped(KeyEvent keyevent) {
    }

    public final int readChar(int i) {
        while (i >= 0) {
            int j = 1;
            while (j > 0) {
                j++;
            }
        }
        int k = -1;
        if (writeIndex != readIndex) {
            k = charQueue[readIndex];
            readIndex = readIndex + 1 & 0x7f;
        }
        return k;
    }

    public final void focusGained(FocusEvent focusevent) {
        awtFocus = true;
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    public final void focusLost(FocusEvent focusevent) {
        awtFocus = false;
        for (int i = 0; i < 128; i++) {
            keyArray[i] = 0;
        }

    }

    public final void windowActivated(WindowEvent windowevent) {
    }

    public final void windowClosed(WindowEvent windowevent) {
    }

    public final void windowClosing(WindowEvent windowevent) {
        destroy();

    }

    public final void windowDeactivated(WindowEvent windowevent) {
    }

    public final void windowDeiconified(WindowEvent windowevent) {
    }

    public final void windowIconified(WindowEvent windowevent) {
    }

    public final void windowOpened(WindowEvent windowevent) {
    }

    void startUp() {
    }

    void processGameLoop() {
    }

    void cleanUpForQuit() {
    }

    void processDrawing() {
    }

    void raiseWelcomeScreen() {
    }

    Component getGameComponent() {
        if (gameFrame != null && !isApplet) {
            return gameFrame;
        } else {
            return this;
        }
    }

    public static int random(int i) {
        return (int) (Math.random() * (double) (i + 1));
    }

    public void takeScreenshot() {
        try {
            Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            Point point = window.getLocationOnScreen();
            int x = (int)point.getX();
            int y = (int)point.getY();
            int w = window.getWidth();
            int h = window.getHeight();
            Robot robot = new Robot(window.getGraphicsConfiguration().getDevice());
            Rectangle captureSize = new Rectangle(x, y, w, h);
            java.awt.image.BufferedImage bufferedimage = robot.createScreenCapture(captureSize);
            long picNumber = random(9999);
            System.out.println(picNumber);
            String fileExtension = Client.myUsername;
            File file = new File(Signlink.findCacheDir() + "Screenshots/" + fileExtension + " " + picNumber + ".png");
            ImageIO.write(bufferedimage, "png", file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void startRunnable(Runnable runnable, int i) {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.setPriority(i);
    }

    void drawLoadingText(int percentage, String loadingText) {
        Client.getClient().checkSize();
        while (graphics == null) {
            graphics = (isApplet ? this : gameFrame).getGraphics();
            try {
                getGameComponent().repaint();
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception ignored) {
            }
        }
        Font font = new Font("Helvetica", Font.BOLD, 13);
        FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
        Font font1 = new Font("Helvetica", Font.PLAIN, 13);
        FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
        if (shouldClearScreen) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, Client.clientWidth, Client.clientHeight);
            shouldClearScreen = false;
        }
        Color color = new Color(140, 17, 17);
        int y = Client.clientHeight / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(Client.clientWidth / 2 - 152, y, 304, 34);
        graphics.fillRect(Client.clientWidth / 2 - 150, y + 2, percentage * 3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((Client.clientWidth / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(loadingText, (Client.clientWidth - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
        graphics.drawString("", (Client.clientWidth - fontmetrics1.stringWidth("")) / 2, y - 8);
    }

    RSApplet() {
        resizing = true;
        delayTime = 20;
        minDelay = 1;
        shouldDebug = false;
        shouldClearScreen = true;
        awtFocus = true;
    }
}