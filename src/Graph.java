package src;
import java.util.ArrayList;

public class Graph {
    public static enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private ArrayList<Vertex> vertices;
    
    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public void addVertexAt(char label, int x, int y, boolean isWall) {
        Vertex newVertex = new Vertex(label, x, y, isWall);
        for (Vertex vertex : this.vertices) {
            if (vertex.getX() == x && vertex.getY() == y) {
                throw new IllegalArgumentException("A vertex already exists at this position.");
            }
            if (vertex.getX() == x && vertex.getY() == y - 1) {
                vertex.setDown(newVertex);
                newVertex.setUp(vertex);
            }
            if (vertex.getX() == x && vertex.getY() == y + 1) {
                vertex.setUp(newVertex);
                newVertex.setDown(vertex);
            }
            if (vertex.getX() == x - 1 && vertex.getY() == y) {
                vertex.setRight(newVertex);
                newVertex.setLeft(vertex);
            }
            if (vertex.getX() == x + 1 && vertex.getY() == y) {
                vertex.setLeft(newVertex);
                newVertex.setRight(vertex);
            }
        }
        this.vertices.add(newVertex);
    }

    public void addVertexTo(char label, Vertex to, Graph.Direction direction, boolean isWall) {
        int x = to.getX();
        int y = to.getY();
        if (direction == Graph.Direction.UP) y--;
        else if (direction == Graph.Direction.DOWN) y++;
        else if (direction == Graph.Direction.LEFT) x--;
        else if (direction == Graph.Direction.RIGHT) x++;
        this.addVertexAt(label, x, y, isWall);
    }

    public Vertex getVertexAt(int x, int y) {
        for (Vertex vertex : this.vertices) {
            if (vertex.getX() == x && vertex.getY() == y) return vertex;
        }
        return null;
    }

    public int getColumnsCount() {
        int max = 0;
        for (Vertex vertex : this.vertices) {
            if (vertex.getX() > max) max = vertex.getX();
        }
        return max + 1;
    }

    public int getRowsCount() {
        int max = 0;
        for (Vertex vertex : this.vertices) {
            if (vertex.getY() > max) max = vertex.getY();
        }
        return max + 1;
    }

    public char[][] toMatrix() {
        int rows = this.getRowsCount();
        int columns = this.getColumnsCount();
        char[][] matrix = new char[rows][columns];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Vertex vertex = this.getVertexAt(x, y);
                if (vertex == null)       matrix[y][x] = '\0';
                else if (vertex.isWall()) matrix[y][x] = '#';
                else                      matrix[y][x] = vertex.getLabel();
            }
        }
        return matrix;
    }
}