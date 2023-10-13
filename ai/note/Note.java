package ai.note;

import javax.swing.*;

public class Note {
    public static void note() {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Handwriting App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new DrawingPanel());
        frame.pack();
        frame.setVisible(true);
    }
}