package net.dodian.client;

import javax.imageio.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.awt.*;
import static net.dodian.client.config.Constants.SERVER_HOSTNAME;
import static net.dodian.client.config.Constants.WINDOW_TITLE;

public class Jframe extends Client implements ActionListener {

    private static JMenuItem menuItem;
    private static final long serialVersionUID = 1L;

    public Jframe(String[] args) {
        super();
        try {
            Signlink.startpriv(InetAddress.getByName(server));
            initUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            frame = new JFrame(WINDOW_TITLE);
            frame.setLayout(new BorderLayout());
            setFocusTraversalKeysEnabled(false);
            frame.setIconImage(new ImageIcon(Signlink.findCacheDir() + "Sprites/icon.png").getImage());
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel gamePanel = new JPanel();

            gamePanel.setLayout(new BorderLayout());
            gamePanel.add(this);
            gamePanel.setPreferredSize(new Dimension(765, 503));

            initMenubar();
            frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true); // can see the client
            frame.setResizable(false); // resizeable frame

            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initMenubar() {
        JButton screenshot = new JButton("");
        screenshot.setActionCommand("screenshot");
        screenshot.addActionListener(this);
        Icon screenicon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Signlink.findCacheDir() + "Sprites/PrintScreenIcon.png"));
        screenshot.setIcon(screenicon);
        JMenu fileMenu = new JMenu("File");
        String[] mainButtons = new String[]{"Website", "-", "Exit"};
        for (String name : mainButtons) {
            JMenuItem menuItem = new JMenuItem(name);
            if (name.equalsIgnoreCase("-")) {
                fileMenu.addSeparator();
            } else if (name.equalsIgnoreCase("Website")) {
                JMenu forumsMenu = new JMenu("Website");
                fileMenu.add(forumsMenu);
                JMenuItem DodianServer = new JMenuItem("Dodian.net");
                DodianServer.addActionListener(this);
                forumsMenu.add(DodianServer);
            } else {
                menuItem.addActionListener(this);
                fileMenu.add(menuItem);
            }
        }

        JMenuBar menuBar = new JMenuBar();
        JMenuBar jmenubar = new JMenuBar();

        frame.add(jmenubar);
        menuBar.add(fileMenu);
        menuBar.add(screenshot);
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
    }

    private int screenshot;
    private boolean takeScreenshot = true;

    public void screeny() {
        try {
            Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            Point point = window.getLocationOnScreen();
            int x = (int)point.getX()+2;
            int y = (int)point.getY();
            int w = window.getWidth()-7;
            int h = window.getHeight()-6;
            Robot robot = new Robot(window.getGraphicsConfiguration().getDevice());
            Rectangle captureSize = new Rectangle(x, y, w, h);
            java.awt.image.BufferedImage bufferedimage = robot.createScreenCapture(captureSize);
            String fileExtension = myUsername;
            for (int i = 1; i <= 1000; i++) {
                File file = new File(Signlink.findCacheDir() + "Screenshots/"+ fileExtension +" "+ i +".png");
                if (!file.exists()) {
                    screenshot = i;
                    takeScreenshot = true;
                    break;
                }
            }
            File file = new File(Signlink.findCacheDir() + "Screenshots/" + fileExtension + " " + screenshot + ".png");
            if (takeScreenshot) {
                pushMessage("<col=6E0085>" + fileExtension + " "+ screenshot +" was saved in your screenshot folder!", 0, "");
                ImageIO.write(bufferedimage, "png", file);
            } else {
                pushMessage("<col=FF0000>Your screenshots folder is full!", 0, "");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public URL getCodeBase() {
        try {
            return new URL("http://" + SERVER_HOSTNAME + "/cache");
        } catch (Exception e) {
            return super.getCodeBase();
        }
    }

    public URL getDocumentBase() {
        return getCodeBase();
    }

    public void loadError(String s) {
        System.out.println("loadError: " + s);
    }

    public String getParameter(String key) {
        return "";
    }

    static void openURL(String url) {
        Desktop d = Desktop.getDesktop();
        try {
            d.browse(new URI(url));
        } catch (Exception ignored) {
        }
    }

    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        try {
            if (cmd != null) {
                if (cmd.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }
                else if (cmd.equalsIgnoreCase("Screenshot")) {
                    screeny();
                }
                else if (cmd.equalsIgnoreCase("Dodian.net")) {
                    openURL("https://dodian.net/");
                }
            }
        } catch (Exception ignored) {
        }
    }
}