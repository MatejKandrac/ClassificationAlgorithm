import implementation.ClassificationAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Visualizer extends JPanel {

    private final int prefW;
    private final int prefH;
    private final byte[][] colors;

    HashMap<Byte, Color> map = new HashMap<>();

    public Visualizer(byte[][] colors) throws HeadlessException {
        this.colors = colors;
        prefW = colors.length;
        prefH = colors[0].length;
        map.put(ClassificationAlgorithm.RED, Color.RED);
        map.put(ClassificationAlgorithm.GREEN, Color.GREEN);
        map.put(ClassificationAlgorithm.BLUE, Color.BLUE);
        map.put(ClassificationAlgorithm.PURPLE, Color.MAGENTA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < colors.length; i++) {
            for (int i1 = 0; i1 < colors[i].length; i1++) {
                g2.setColor(map.get(colors[i][i1]));
                g2.drawRect(i, i1, 1, 1);
            }
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(prefW, prefH);
    }
}
