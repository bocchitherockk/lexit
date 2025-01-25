package src;
public class Player {
    private Vertex position;
    private Color color;
    private int score;

    // getters and sertters
    public Vertex getPosition() { return position; }
    public void setPosition(Vertex position) { this.position = position; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Player(Vertex position, String ...colors) {
        this.position = position;
        this.color = new Color(colors);
        this.score = 100;
    }

    public void move(char move) {
        Vertex to;
        if (move == 'z') {
            to = this.position.getUp();
        } else if (move == 's') {
            to = this.position.getDown();
        } else if (move == 'q') {
            to = this.position.getLeft();
        } else if (move == 'd') {
            to = this.position.getRight();
        } else {
            throw new IllegalArgumentException("Invalid move");
        }

        if (to != null) {
            this.position = to;
        } else {
            this.score -= 5;
        }
   }
}
