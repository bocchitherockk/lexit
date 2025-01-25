package src;
import java.util.ArrayList;

public class Graph {
    private ArrayList<Vertex> vertices;

    // the terminal width can not hold more than 26 vertices
    // the terminal height can not hold more than 10 vertices
    public static final int MAX_Vertices_COLUMNS = 26;
    public static final int MAX_Vertices_ROWS = 10; // it can hold up to 11, but then the last row will cause rerendering issues

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public void addVertexAt(char label, int x, int y) {
        if (x >= Graph.MAX_Vertices_COLUMNS || y >= Graph.MAX_Vertices_ROWS) {
            throw new IllegalArgumentException("The terminal can not hold more than " + Graph.MAX_Vertices_COLUMNS +  "columns and " + Graph.MAX_Vertices_ROWS + " rows.");
        }
        Vertex newVertex = new Vertex(label, x, y);
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

    public void addVertexTo(char label, Vertex to, int direction) {
        int x = to.getX();
        int y = to.getY();
        if (direction == Graph.UP) {
            y--;
        } else if (direction == Graph.DOWN) {
            y++;
        } else if (direction == Graph.LEFT) {
            x--;
        } else if (direction == Graph.RIGHT) {
            x++;
        }

        if (x < 0 || x >= Graph.MAX_Vertices_COLUMNS || y < 0 || y >= Graph.MAX_Vertices_ROWS) {
            throw new IllegalArgumentException("The vertex can not be added outside the terminal.");
        }
        this.addVertexAt(label, x, y);
    }

    public Vertex getVertexAt(int x, int y) {
        for (Vertex vertex : this.vertices) {
            if (vertex.getX() == x && vertex.getY() == y) {
                return vertex;
            }
        }
        return null;
    }

    public int getColumnsCount() {
        int max = 0;
        for (Vertex vertex : this.vertices) {
            if (vertex.getX() > max) {
                max = vertex.getX();
            }
        }
        return max + 1;
    }

    public int getRowsCount() {
        int max = 0;
        for (Vertex vertex : this.vertices) {
            if (vertex.getY() > max) {
                max = vertex.getY();
            }
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
                if (vertex != null) {
                    matrix[y][x] = vertex.getLabel();
                } else {
                    matrix[y][x] = ' ';
                }
            }
        }
        return matrix;
    }
}