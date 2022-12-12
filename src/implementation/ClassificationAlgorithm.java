package implementation;

import java.util.*;

public class ClassificationAlgorithm {

    public static final short K = 7;
    public static final short POINTS_IN_SQUARE = 20;
    public static final short MESH_SEPARATIONS = 10000 / POINTS_IN_SQUARE;
    public static final int NUM_OF_GENERATED_POINTS = 40000;
    public static final byte RED = 0b00;
    public static final byte GREEN = 0b01;
    public static final byte BLUE = 0b10;
    public static final byte PURPLE = 0b11;

    public static final Point[] INITIAL_POINTS = {
            new Point(-4500, -4400, RED),
            new Point(-4100, -3000, RED),
            new Point(-1800, -2400, RED),
            new Point(-2500, -3400, RED),
            new Point(-2000, -1400, RED),

            new Point(4500, -4400, GREEN),
            new Point(4100, -3000, GREEN),
            new Point(1800, -2400, GREEN),
            new Point(2500, -3400, GREEN),
            new Point(2000, -1400, GREEN),

            new Point(-4500, 4400, BLUE),
            new Point(-4100, 3000, BLUE),
            new Point(-1800, 2400, BLUE),
            new Point(-2500, 3400, BLUE),
            new Point(-2000, 1400, BLUE),

            new Point(4500, 4400, PURPLE),
            new Point(4100, 3000, PURPLE),
            new Point(1800, 2400, PURPLE),
            new Point(2500, 3400, PURPLE),
            new Point(2000, 1400, PURPLE),
    };
    public static final int[][] POINT_RANGES = {
            {5000, 5000},
            {500, 5000},
            {5000, 500},
            {500, 500}
    };

    final ArrayList<Point>[][] mesh = new ArrayList[MESH_SEPARATIONS][MESH_SEPARATIONS];
    final Set<String> generatedPoints = new HashSet<>();

    public byte[][] getPicture() {
        for (Point point : INITIAL_POINTS)
            addToMesh(point, getRelativeX(point.getX()), getRelativeY(point.getY()));

        Random rn = new Random();

        int rangeIndex = 0;
        double correctClassifications = 0;

        for (int i = 0; i < NUM_OF_GENERATED_POINTS; i++) {
            short x;
            short y;
            if (rn.nextInt(100) == 0){
                do {
                    x = (short) (rn.nextInt(10000) - 5000);
                    y = (short) (rn.nextInt(10000) - 5000);
                } while (generatedPoints.contains(x+" "+y));

            } else {
                do {
                    int[] range = POINT_RANGES[rangeIndex];
                    x = (short) (rn.nextInt(5500) - range[0]);
                    y = (short) (rn.nextInt(5500) - range[1]);
                } while (generatedPoints.contains(x+" "+y));
            }
            generatedPoints.add(x+" "+y);
            byte color = classify(x, y, true);
            if (color == rangeIndex) correctClassifications++;
            rangeIndex = ++rangeIndex % 4;
        }
        System.out.println(correctClassifications / 40000 * 100);

        byte[][] colors = new byte[MESH_SEPARATIONS][MESH_SEPARATIONS];
        for (byte[] color : colors) {
            Arrays.fill(color, (byte) 4);
        }
        for (int i = 0; i < mesh.length; i++) {
            for (int i1 = 0; i1 < mesh[i].length; i1++) {
                if (mesh[i][i1] != null) {
                    List<Point> points = mesh[i][i1];
                    if (points.size() == 1)
                        colors[i][i1] = points.get(0).getClassification();
                    else {
                        int[] classCount = new int[4];
                        for (Point point : points) {
                            classCount[point.getClassification()]++;
                        }

                        byte mostUsedClass = getMostUsedClass(classCount);

                        colors[i][i1] = mostUsedClass;
                    }
                } else {
                    colors[i][i1] = classify(
                            (short) (i*POINTS_IN_SQUARE - 5000 + POINTS_IN_SQUARE / 2),
                            (short) (i1*POINTS_IN_SQUARE - 5000 + POINTS_IN_SQUARE / 2),
                            false);
                }
            }
        }
        return colors;
    }

    byte classify(short x, short y, boolean addToMesh) {
        short relX = getRelativeX(x);
        short relY = getRelativeY(y);
        Point newPoint = new Point(x, y);
        List<Point> closestPoints = new LinkedList<>();

        List<Point> points = mesh[relX][relY];
        if (points != null) {
            closestPoints.addAll(points);
        }

        if (closestPoints.size() < K) {
            int startX, startY, endX, endY, iteration = 1;
            boolean leftOut, rightOut, topOut, downOut;

            while (closestPoints.size() < K) {
                startX = (relX - iteration);
                startY = (relY - iteration);
                endX = (relX + iteration);
                endY = (relY + iteration);

                if (startX < 0) {
                    startX = 0;
                    leftOut = true;
                } else leftOut = false;
                if (endX >= MESH_SEPARATIONS) {
                    endX = MESH_SEPARATIONS - 1;
                    rightOut = true;
                } else rightOut = false;
                if (startY < 0) {
                    startY = 0;
                    topOut = true;
                } else topOut = false;
                if (endY >= MESH_SEPARATIONS) {
                    endY = MESH_SEPARATIONS - 1;
                    downOut = true;
                } else downOut = false;


                if (!topOut)
                    for (int i = startX; i <= endX; i++)
                        if (mesh[i][startY] != null) closestPoints.addAll(mesh[i][startY]);

                if (!leftOut || !rightOut)
                    for (int i = startY + (topOut ? 0 : 1); i <= endY - (downOut ? 0 : 1); i++) {
                        if (!leftOut && mesh[startX][i] != null) closestPoints.addAll(mesh[startX][i]);
                        if (!rightOut && mesh[endX][i] != null) closestPoints.addAll(mesh[endX][i]);
                    }

                if (!downOut)
                    for (int i = startX; i <= endX; i++)
                        if (mesh[i][endY] != null) closestPoints.addAll(mesh[i][endY]);


                iteration++;
            }
        }
        double[] distances = new double[K];
        int[] classes = new int[K];

        Arrays.fill(classes, -1);
        Arrays.fill(distances, Double.MAX_VALUE);

        double distance;
        for (Point closestPoint : closestPoints) {
            distance = getDistance(newPoint, closestPoint);

            for (int i = 0; i < K; i++) {
                if (distances[i] > distance) {
                    if (i < K - 1) {
                        for (int j = i + 1; j < K; j++) {
                            distances[j] = distances[j - 1];
                            classes[j] = classes[j - 1];
                        }
                    }
                    distances[i] = distance;
                    classes[i] = closestPoint.getClassification();
                    break;
                }
            }
        }

        int[] classCount = new int[4];
        for (int aClass : classes) {
            classCount[aClass]++;
        }

        byte mostUsedClass = getMostUsedClass(classCount);

        newPoint.setClassification(mostUsedClass);
        if (addToMesh)
            addToMesh(newPoint, relX, relY);

        return mostUsedClass;
    }

    byte getMostUsedClass(int[] classesCount) {

        int mostCount = 0;
        byte mostIndex = -1;

        for (byte i = 0; i < classesCount.length; i++) {
            if (classesCount[i] > mostCount) {
                mostCount = classesCount[i];
                mostIndex = i;
            }
        }
        return mostIndex;
    }

    void addToMesh(Point point, short relX, short relY) {
        if (mesh[relX][relY] == null) {
            mesh[relX][relY] = new ArrayList<>();
        }
        mesh[relX][relY].add(point);
    }

    double getDistance(Point one, Point two) {
        return Math.sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2));
    }

    short getRelativeX(short x) {
        return (short) ((x + 5000) / POINTS_IN_SQUARE);
    }

    short getRelativeY(short y) {
        return (short) ((y + 5000) / POINTS_IN_SQUARE);
    }
}
