package src;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private Vertex position;
    private int score;
    private ArrayList<String> style;

    // getters and sertters
    public Vertex getPosition() { return this.position; }
    public void setPosition(Vertex position) { this.position = position; }
    public int getScore() { return this.score; }
    public void setScore(int score) { this.score = score; }
    public ArrayList<String> getStyle() { return this.style; }
    public void setStyle(ArrayList<String> style) { this.style = style; }

    public Player(Vertex position, String ...styles) {
        this.position = position;
        this.style = new ArrayList<>(Arrays.asList(styles));
        this.score = 100;
    }

    public boolean move(char move) {
        Vertex to;
        if (move == 'z') to = this.position.getUp();
        else if (move == 's') to = this.position.getDown();
        else if (move == 'q') to = this.position.getLeft();
        else if (move == 'd') to = this.position.getRight();
        else return false;

        if (to == null) return false;
        this.position = to;
        return true;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }
    public void decreaseScore(int amount) {
        this.score -= amount;
    }
}
