import implementation.ClassificationAlgorithm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ClassificationAlgorithm algorithm = new ClassificationAlgorithm();
        byte[][] colors = algorithm.getPicture();

        SwingUtilities.invokeLater(() -> {
            Visualizer mainPanel = new Visualizer(colors);
            JFrame frame = new JFrame("Results");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(mainPanel);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }
}