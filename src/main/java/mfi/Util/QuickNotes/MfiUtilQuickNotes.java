package mfi.Util.QuickNotes;

import javax.swing.*;
import java.io.*;

public class MfiUtilQuickNotes {

    private static String FILE = "mfi-util-quick-notes.txt";
    private static String FILE_NAME = "mfi-util-quick-notes.txt";

    private static final String CONFIG_FILE =
    	    System.getProperty("user.home") +
    	    "\\AppData\\Roaming\\MFIQuickNotes\\config.properties";
    private static JFrame frame;
    private static JTextArea textArea;

    public static void main(String[] args) {
        FILE = loadOrChooseFilePath();

        if (FILE == null) {
            System.exit(0); // user cancelled
        }
        
        try {
            new java.io.File(FILE).getParentFile().mkdirs(); // ensure folder
            new java.io.File(FILE).createNewFile();          // create file if missing
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame(FILE_NAME);

        createUI();
        loadFromFile();
        registerAutoSave();
        registerGlobalHotkey();
    }

    private static void createUI() {
        textArea = new JTextArea();

        frame.add(new JScrollPane(textArea));
        frame.setSize(350, 200);
        //frame.setAlwaysOnTop(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frame.setVisible(true);
    }

    // ✅ Load previous notes
    private static void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            textArea.read(reader, null);
        } catch (IOException e) {
            // first run → file doesn't exist → ignore
        }
    }

    // ✅ Auto-save on typing
    private static void registerAutoSave() {
        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                saveToFile();
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

        // 🔕 Disable logging (optional but recommended)
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
                com.github.kwhat.jnativehook.GlobalScreen.class.getPackage().getName());
        logger.setLevel(java.util.logging.Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            com.github.kwhat.jnativehook.GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        com.github.kwhat.jnativehook.GlobalScreen.addNativeKeyListener(
            new com.github.kwhat.jnativehook.keyboard.NativeKeyListener() {

                @Override
                public void nativeKeyPressed(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {

                    // ALT + 3
                    if ((e.getModifiers() &
                         com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.ALT_MASK) != 0 &&
                        e.getKeyCode() ==
                         com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_3) {

                        SwingUtilities.invokeLater(() -> {
                            frame.setVisible(!frame.isVisible());
                        });
                    }
                }

                @Override public void nativeKeyReleased(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {}
                @Override public void nativeKeyTyped(
                        com.github.kwhat.jnativehook.keyboard.NativeKeyEvent e) {}
            });
    }
    private static String loadOrChooseFilePath() {
        java.util.Properties props = new java.util.Properties();
        java.io.File configFile = new java.io.File(CONFIG_FILE);

        try {
            if (configFile.exists()) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(configFile)) {
                    props.load(in);
                    String path = props.getProperty("notesPath");

                    // If file is missing → ask again
                    if (path == null || !new File(path).exists()) {
                        return chooseNewLocation(configFile, props);
                    }

                    return path;
                }
            } else {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose where to store your notes");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File folder = chooser.getSelectedFile();

                    String path = folder.getAbsolutePath()
                            + File.separator + "mfi-util-quick-notes.txt";

                    // Save config
                    configFile.getParentFile().mkdirs();

                    try (FileOutputStream out = new FileOutputStream(configFile)) {
                        props.setProperty("notesPath", path);
                        props.store(out, "MFI Quick Notes Config");
                    }

                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private static String chooseNewLocation(File configFile, java.util.Properties props) {
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
                configFile.getParentFile().mkdirs();

                try (FileOutputStream out = new FileOutputStream(configFile)) {
                    props.setProperty("notesPath", path);
                    props.store(out, "MFI Quick Notes Config");
                }

                // ✅ create the file immediately
                new File(path).createNewFile();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return path;
        }

        return null;
    }
}