package mfi.Util.QuickNotes;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MfiUtilQuickNotes {

    private static String FILE_NAME = "mfi-util-quick-notes";
    private static String FILE = FILE_NAME + ".txt";

    private static final String CONFIG_FILE =
            System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Roaming"
            + File.separator + "MFIQuickNotes"
            + File.separator + FILE_NAME + "-" + "config.properties";

    private static JDialog frame;
    private static JTextArea textArea;
    private static Timer saveTimer;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            FILE = loadOrChooseFilePath();

            if (FILE == null) {
                System.exit(0);
            }

            ensureFileExists(FILE);

            frame = new JDialog();
            frame.setTitle(FILE_NAME);

            createUI();
            loadFromFile();
            registerAutoSave();
            registerGlobalHotkey();
        });
    }

    private static void createUI() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // smaller font

        frame.add(new JScrollPane(textArea));
        frame.setSize(350, 200);

        frame.setAlwaysOnTop(true);
        frame.setResizable(true);

        // Disable close button behavior
        frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        // Remove minimize/maximize buttons
        frame.setModalityType(JDialog.ModalityType.MODELESS);

        frame.setVisible(true);
    }

    private static void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            textArea.read(reader, null);
        } catch (IOException ignored) {}
    }

    private static void registerAutoSave() {
        saveTimer = new Timer(500, e -> saveToFile());
        saveTimer.setRepeats(false);

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                saveTimer.restart();
            }
        });
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            textArea.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerGlobalHotkey() {

        java.util.logging.Logger logger =
                java.util.logging.Logger.getLogger(
                        com.github.kwhat.jnativehook.GlobalScreen.class.getPackage().getName()
                );

        logger.setLevel(java.util.logging.Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            if (!com.github.kwhat.jnativehook.GlobalScreen.isNativeHookRegistered()) {
                com.github.kwhat.jnativehook.GlobalScreen.registerNativeHook();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        com.github.kwhat.jnativehook.GlobalScreen.addNativeKeyListener(
            new com.github.kwhat.jnativehook.keyboard.NativeKeyListener() {

                @Override
                public void nativeKeyPressed(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {

                    boolean alt = (e.getModifiers()
                            & com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.ALT_MASK) != 0;

                    // ALT + 2 → SHOW/HIDE WINDOW
                    if (alt && e.getKeyCode() ==
                            com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_2) {

                        SwingUtilities.invokeLater(() -> {
                            if (!frame.isVisible()) {
                                frame.setVisible(true);
                                frame.toFront();
                                frame.requestFocus();
                                textArea.requestFocusInWindow();
                            } else {
                                frame.setVisible(false);
                            }
                        });
                    }

                    // ALT + 3 → EXIT PROGRAM
                    if (alt && e.getKeyCode() ==
                            com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_3) {

                        SwingUtilities.invokeLater(() -> {
                            saveToFile();
                            try {
                                com.github.kwhat.jnativehook.GlobalScreen.unregisterNativeHook();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            System.exit(0);
                        });
                    }
                }

                @Override public void nativeKeyReleased(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {}

                @Override public void nativeKeyTyped(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {}
            }
        );
    }

    private static String loadOrChooseFilePath() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        try {
            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) {
                    props.load(in);
                    String path = props.getProperty("notesPath");

                    if (path == null || !new File(path).exists()) {
                        return chooseNewLocation(configFile, props);
                    }

                    return path;
                }
            } else {
                return chooseNewLocation(configFile, props);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String chooseNewLocation(File configFile, Properties props) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose where to store your notes");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();

            String path = folder.getAbsolutePath()
                    + File.separator + "mfi-util-quick-notes.txt";

            try {
                File parent = configFile.getParentFile();
                if (parent != null) parent.mkdirs();

                try (FileOutputStream out = new FileOutputStream(configFile)) {
                    props.setProperty("notesPath", path);
                    props.store(out, "MFI Quick Notes Config");
                }

                ensureFileExists(path);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return path;
        }

        return null;
    }

    private static void ensureFileExists(String path) {
        try {
            File f = new File(path);
            File parent = f.getParentFile();
            if (parent != null) parent.mkdirs();
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}