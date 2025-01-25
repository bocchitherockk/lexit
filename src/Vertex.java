package src;
import java.util.ArrayList;

class Vertex {
    private char label; //label is not unique
    private Vertex up, down, left, right;
    private int x, y;

    public Vertex(char label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public Vertex(char label, int x, int y, Vertex up, Vertex down, Vertex left, Vertex right) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    // Getters and Setters
    public char getLabel() { return label; }
    public void setLabel(char label) { this.label = label; }

    public Vertex getUp() { return up; }
    public void setUp(Vertex up) { this.up = up; }

    public Vertex getDown() { return down; }
    public void setDown(Vertex down) { this.down = down; }

    public Vertex getLeft() { return left; }
    public void setLeft(Vertex left) { this.left = left; }

    public Vertex getRight() { return right; }
    public void setRight(Vertex right) { this.right = right; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }



    public ArrayList<Vertex> getNeighbors() {
        ArrayList<Vertex> neighbors = new ArrayList<>();
        neighbors.add(up);
        neighbors.add(down);
        neighbors.add(left);
        neighbors.add(right);
        return neighbors;
    }
}