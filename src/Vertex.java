package src;
import java.util.ArrayList;

class Vertex {
    private char label; //label is not unique
    private Vertex up, down, left, right;
    private int x, y;
    private boolean isWall;
    private ArrayList<String> style;

    public Vertex(char label, int x, int y) {
        this(label, x, y, false);
    }

    public Vertex(char label, int x, int y, boolean isWall) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        this.style = new ArrayList<>();
        if (isWall) this.style.add(Style.BG_BLUE);
    }

    public Vertex(char label, int x, int y, Vertex up, Vertex down, Vertex left, Vertex right) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.isWall = false;
    }

    // Getters and Setters
    public char getLabel() { return this.label; }
    public void setLabel(char label) { this.label = label; }

    public Vertex getUp() { return this.up; }
    public void setUp(Vertex up) { this.up = up; }

    public Vertex getDown() { return this.down; }
    public void setDown(Vertex down) { this.down = down; }

    public Vertex getLeft() { return this.left; }
    public void setLeft(Vertex left) { this.left = left; }

    public Vertex getRight() { return this.right; }
    public void setRight(Vertex right) { this.right = right; }

    public int getX() { return this.x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; }
    public void setY(int y) { this.y = y; }

    public boolean isWall() { return this.isWall; }
    public ArrayList<String> getStyle() { return this.style; }

    public ArrayList<Vertex> getNeighbors() {
        ArrayList<Vertex> neighbors = new ArrayList<>();
        neighbors.add(up);
        neighbors.add(down);
        neighbors.add(left);
        neighbors.add(right);
        return neighbors;
    }
}