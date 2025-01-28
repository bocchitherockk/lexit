package src;
import java.util.ArrayList;

class Vertex {
    private char label; //label is not unique
    private Vertex up, down, left, right, upLeft, upRight, downLeft, downRight;
    private int x, y;
    private boolean isWall;
    private boolean isStart;
    private boolean isEnd;
    private ArrayList<String> style;

    public Vertex(char label) {
        this(label, 0, 0);
    }

    public Vertex(char label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
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

    public Vertex getUpLeft() { return this.upLeft; }
    public void setUpLeft(Vertex upLeft) { this.upLeft = upLeft; }

    public Vertex getUpRight() { return this.upRight; }
    public void setUpRight(Vertex upRight) { this.upRight = upRight; }

    public Vertex getDownLeft() { return this.downLeft; }
    public void setDownLeft(Vertex downLeft) { this.downLeft = downLeft; }

    public Vertex getDownRight() { return this.downRight; }
    public void setDownRight(Vertex downRight) { this.downRight = downRight; }

    public int getX() { return this.x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; }
    public void setY(int y) { this.y = y; }

    public boolean isWall() { return this.isWall; }
    public void setWall(boolean isWall) {
        if (!isWall) {
            this.style = null;
            return;
        }
        this.style = new ArrayList<>();
        this.style.add(Style.BG_BLUE);
        this.isWall = isWall;
    }

    public boolean isStart() { return this.isWall; }
    public void setStart(boolean isStart) {
        if (!isStart) {
            this.style = null;
            return;
        }
        this.style = new ArrayList<>();
        this.style.add(Style.BG_YELLOW);
        this.style.add(Style.FG_BLACK);
        this.isStart = isStart;
    }

    public boolean isEnd() { return this.isEnd; }
    public void setEnd(boolean isEnd) {
        if (!isEnd) {
            this.style = null;
            return;
        }
        this.style = new ArrayList<>();
        this.style.add(Style.BG_GREEN);
        this.style.add(Style.FG_BLACK);
        this.isEnd = isEnd;
    }

    public ArrayList<String> getStyle() { return this.style; }

    public ArrayList<Vertex> getNeighbors() {
        ArrayList<Vertex> neighbors = new ArrayList<>();
        neighbors.add(up);
        neighbors.add(down);
        neighbors.add(left);
        neighbors.add(right);
        neighbors.add(upLeft);
        neighbors.add(upRight);
        neighbors.add(downLeft);
        neighbors.add(downRight);
        return neighbors;
    }
}