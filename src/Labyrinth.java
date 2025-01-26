package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Labyrinth {
    // the terminal width can not hold more than 26 vertices
    // the terminal height can not hold more than 10 vertices
    private int maxColumns;
    private int maxRows; // it can hold up to 11, but then the last row will cause rerendering issues
    private int scalar;
    private ArrayList<String> words;
    private Graph graph;

    // getters and setters
    public int getMaxColumns() { return this.maxColumns; }
    public int getMaxRows() { return this.maxRows; }
    public int getScalar() { return this.scalar; }
    public ArrayList<String> getWords() { return this.words; }
    public Graph getGraph() { return this.graph; }


    public Labyrinth(String filePath, int scalar) throws IOException {
        this.scalar = scalar;
        this.maxColumns = 26 / scalar;
        this.maxRows = 10 / scalar;
        this.words = Labyrinth.readWords(filePath);
        this.graph = new Graph();
    }

    public void fill() {
        int currentX = 0;
        int currentY = 0;
        this.graph.addVertexAt('S', currentX, currentY);
        int i = 50;

        do {
            ArrayList<Graph.Direction> allowedDirections = new ArrayList<>();
            if (currentY > 0 && this.graph.getVertexAt(currentX, currentY - 1) == null) {
                allowedDirections.add(Graph.Direction.UP);
            }
            if (currentY < this.maxRows - 1 && this.graph.getVertexAt(currentX, currentY + 1) == null) {
                allowedDirections.add(Graph.Direction.DOWN);
            }
            if (currentX > 0 && this.graph.getVertexAt(currentX - 1, currentY) == null) {
                allowedDirections.add(Graph.Direction.LEFT);
            }
            if (currentX < this.maxColumns - 1 && this.graph.getVertexAt(currentX + 1, currentY) == null) {
                allowedDirections.add(Graph.Direction.RIGHT);
            }

            if (allowedDirections.size() == 0) {
                break;
            }

            Graph.Direction direction = this.getRandomDirection(allowedDirections);

            if (direction == Graph.Direction.UP) {
                this.graph.addVertexTo((char) ('A' + i), this.graph.getVertexAt(currentX, currentY), Graph.Direction.UP);
                currentY--;
            } else if (direction == Graph.Direction.DOWN) {
                this.graph.addVertexTo((char) ('A' + i), this.graph.getVertexAt(currentX, currentY), Graph.Direction.DOWN);
                currentY++;
            } else if (direction == Graph.Direction.LEFT) {
                this.graph.addVertexTo((char) ('A' + i), this.graph.getVertexAt(currentX, currentY), Graph.Direction.LEFT);
                currentX--;
            } else if (direction == Graph.Direction.RIGHT) {
                this.graph.addVertexTo((char) ('A' + i), this.graph.getVertexAt(currentX, currentY), Graph.Direction.RIGHT);
                currentX++;
            }
        } while (i-- != 0);
    }

    public Graph.Direction getRandomDirection(ArrayList<Graph.Direction> allowedDirections) {
        if (allowedDirections.size() == 0) {
            throw new IllegalArgumentException("At least one direction must be allowed.");
        }
        Random random = new Random();
        return allowedDirections.get(random.nextInt(allowedDirections.size()));
    }

    public static ArrayList<String> readWords(String filePath) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String word;
        while ((word = bufferedReader.readLine()) != null) {
            words.add(word);
        }
        bufferedReader.close();
        return words;
    }

    public void addVertexAt(char label, int x, int y) {
        if (x < 0 || x >= this.maxColumns || y < 0 || y >= this.maxRows) {
            throw new IllegalArgumentException("The vertex can not be added outside the terminal.");
        }
        this.graph.addVertexAt(label, x, y);
    }

    public void addVertexTo(char label, Vertex to, Graph.Direction direction) {
        int x = to.getX();
        int y = to.getY();
        if (x == this.maxColumns - 1 && direction == Graph.Direction.RIGHT ||
            y == this.maxRows && direction == Graph.Direction.DOWN ||
            x == 0 && direction == Graph.Direction.LEFT ||
            y == 0 && direction == Graph.Direction.UP
        ) {
            throw new IllegalArgumentException("The vertex can not be added outside the terminal.");
        }
        this.graph.addVertexTo(label, to, direction);
    }

    public void flipWords() {
        for (int i = 0; i < this.words.size(); i++) {
            this.words.set(i, new StringBuilder(this.words.get(i)).reverse().toString());
        }
    }
}
