package implementation;

public class Point {

    private final short x;
    private final short y;
    private byte classification;

    public Point(int x, int y, byte classification) {
        this.x = (short) x;
        this.y = (short) y;
        this.classification = classification;
    }

    public Point(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public void setClassification(byte classification) {
        this.classification = classification;
    }

    public byte getClassification() {
        return classification;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    @Override
    public String toString() {
        return "{X: " + x + ", Y: " + y + "}";
    }
}
