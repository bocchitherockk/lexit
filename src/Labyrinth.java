package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

public class Labyrinth {
    // the terminal width can not hold more than 26 vertices
    // the terminal height can not hold more than 10 vertices
    private int maxColumns;
    private int maxRows; // it can hold up to 11, but then the last row will cause rerendering issues
    private int scalar;
    private ArrayList<String> dictionary;
    private Graph graph;
    private Vertex start;
    private Vertex end;
    private ArrayList<ArrayList<Vertex>> distinctPaths;

    // getters and setters
    public int getMaxColumns() { return this.maxColumns; }
    public int getMaxRows() { return this.maxRows; }
    public int getScalar() { return this.scalar; }
    public ArrayList<String> getDictionary() { return this.dictionary; }
    public Graph getGraph() { return this.graph; }
    public Vertex getStart() { return this.start; }
    public Vertex getEnd() { return this.end; }


    public Labyrinth(String filePath, Game.Difficulty difficulty) throws IOException, Exception {
        this.scalar = difficulty == Game.Difficulty.EASY ? 2 : 1;
        this.maxColumns = 26 / this.scalar;
        this.maxRows = 10 / this.scalar;
        this.dictionary = Labyrinth.readDictionary(filePath);
        // if the difficulty is hard, the player should find the words flipped
        if (difficulty == Game.Difficulty.HARD) this.flipDictionary();
        Collections.shuffle(this.dictionary);
        this.graph = new Graph();
        this.start = null;
        this.end = null;
        this.createEmpty();
        this.distinctPaths = this.getDistinctPaths();
        this.fill();
    }

    public void createEmpty() {
        Random random = new Random();
        Stack<Vertex> verticesStack = new Stack<>();

        this.start = new Vertex('$', random.nextInt(this.maxColumns), random.nextInt(this.maxRows));
        this.start.setStart(true);
        this.graph.addVertex(this.start);
        verticesStack.push(this.start);

        this.end = new Vertex('@', random.nextInt(this.maxColumns), random.nextInt(this.maxRows));
        this.end.setEnd(true);
        this.graph.addVertex(this.end);
        /* int maxVertices = this.maxColumns * this.maxRows; */ // may be used to limit the number of vertices
        // note: if the number of vertices is limited, the end vertex may not be reached
        
        while (!verticesStack.isEmpty() /* && maxVertices-- != 0 */) {
            Vertex currentVertex = verticesStack.getLast();
            ArrayList<Graph.Direction> allowedDirections = this.getAllowedDirections(currentVertex);
            if (allowedDirections.size() == 0) {
                verticesStack.pop();
                continue;
            }
            Graph.Direction direction = allowedDirections.get(random.nextInt(allowedDirections.size())); // choose a random direction
            Vertex newVertex = new Vertex('\0'); // empty vertex represented by a '\0'
            boolean isWall = random.nextInt(100) < 15; // 15% chance of being a wall
            newVertex.setWall(isWall);
            this.graph.attachVertexTo(newVertex, currentVertex, direction);
            if (!isWall) verticesStack.add(newVertex);
        }
        verticesStack.push(this.end);
    }

    public void fill() throws Exception {
        for (ArrayList<Vertex> path : this.distinctPaths) {
            ArrayList<String> fittingWords = this.getFittingWords(this.dictionary, path.size());
            if (fittingWords == null) {
                throw new Exception("no combination of words with length " + path.size() + " found!");
            }

            String wordConcatinated = "";
            for (String word : fittingWords) {
                wordConcatinated += word;
            }

            for (int i = 0; i < path.size(); i++) {
                path.get(i).setLabel(wordConcatinated.charAt(i));
            }

            this.dictionary.removeAll(fittingWords);
        }
        Random random = new Random();
        for (Vertex v : this.graph.getVertices()) {
            if (v.getLabel() == '\0') {
                boolean isWall = random.nextInt(0, 101) < 50; // 50% chance of being a wall
                if (isWall) v.setWall(isWall);
                else v.setLabel((char) (random.nextInt(0, 27) + 'a'));
            }
        }
    }

    public ArrayList<Graph.Direction> getAllowedDirections(Vertex currentVertex) {
        ArrayList<Graph.Direction> allowedDirections = new ArrayList<>();
        if (currentVertex.getX() > 0 && currentVertex.getLeft() == null)
            allowedDirections.add(Graph.Direction.LEFT);
        if (currentVertex.getX() < this.maxColumns - 1 && currentVertex.getRight() == null)
            allowedDirections.add(Graph.Direction.RIGHT);
        if (currentVertex.getY() > 0 && currentVertex.getUp() == null)
            allowedDirections.add(Graph.Direction.UP);
        if (currentVertex.getY() < this.maxRows - 1 && currentVertex.getDown() == null)
            allowedDirections.add(Graph.Direction.DOWN);
        if (currentVertex.getX() > 0 && currentVertex.getY() > 0 && currentVertex.getUpLeft() == null)
            allowedDirections.add(Graph.Direction.UP_LEFT);
        if (currentVertex.getX() < this.maxColumns - 1 && currentVertex.getY() > 0 && currentVertex.getUpRight() == null)
            allowedDirections.add(Graph.Direction.UP_RIGHT);
        if (currentVertex.getX() > 0 && currentVertex.getY() < this.maxRows - 1 && currentVertex.getDownLeft() == null)
            allowedDirections.add(Graph.Direction.DOWN_LEFT);
        if (currentVertex.getX() < this.maxColumns - 1 && currentVertex.getY() < this.maxRows - 1 && currentVertex.getDownRight() == null)
            allowedDirections.add(Graph.Direction.DOWN_RIGHT);

        return allowedDirections;
    }

    public static ArrayList<String> readDictionary(String filePath) throws IOException {
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
        if (x < 0 || x >= this.maxColumns || y < 0 || y >= this.maxRows)
            throw new IllegalArgumentException("The vertex can not be added outside the terminal.");
        this.graph.addVertexAt(label, x, y);
    }

    public void attachVertexTo(char label, Vertex to, Graph.Direction direction) {
        int x = to.getX();
        int y = to.getY();
        if (x == this.maxColumns - 1 && direction == Graph.Direction.RIGHT ||
            y == this.maxRows && direction == Graph.Direction.DOWN ||
            x == 0 && direction == Graph.Direction.LEFT ||
            y == 0 && direction == Graph.Direction.UP
        ) {
            throw new IllegalArgumentException("The vertex can not be added outside the terminal.");
        }
        this.graph.attachVertexTo(label, to, direction);
    }

    public void flipDictionary() {
        for (int i = 0; i < this.dictionary.size(); i++)
            this.dictionary.set(i, new StringBuilder(this.dictionary.get(i)).reverse().toString());
    }

    // TODO: getDistinctPaths, make it choose the end when it's one of the neighbors, don't procrastinate
    public ArrayList<ArrayList<Vertex>> getDistinctPaths() {
        ArrayList<ArrayList<Vertex>> distinctPaths = new ArrayList<>();
        ArrayList<Vertex> currentPath = new ArrayList<>();
        HashSet<Vertex> marked = new HashSet<>(); // this will hold the vertices that are already in a path
        Stack<Pair<Vertex, Vertex>> stack = new Stack<>(); // the first vertex is the parent, the second is the current, i am storing the parent to correctly backtrack the path

        for (Vertex neighbor : this.start.getNeighborsShuffled()) {
            if (neighbor != null && !neighbor.isWall() && !neighbor.isEnd()) {
                stack.push(new Pair<>(null, neighbor));
            }
        }

        int initialStartingVerticesCount = stack.size();

        while (!stack.isEmpty()) {
            Pair<Vertex, Vertex> currentPair = stack.pop();
            Vertex currentParent = currentPair.getKey();
            Vertex currentVertex = currentPair.getValue();

            if (marked.contains(currentVertex)) { // this could happen when a path is found, and the initial neighbors of the start are to be processed, but they are already in the first path
                continue;
            }

            while (currentPath.size() != 0 && currentPath.getLast() != currentParent) {
                currentPath.removeLast();
            }

            if (currentVertex == this.end) {
                distinctPaths.add(new ArrayList<>(currentPath));
                marked.addAll(currentPath);
                currentPath.clear();
                stack.setSize(--initialStartingVerticesCount); // clear the stack (except the first added ones, the neighbors of the start)
            } else {
                boolean hasExplorableNeighbors = currentVertex.getNeighbors().stream().anyMatch(v -> v != null && !v.isWall() && !v.isStart() && !currentPath.contains(v) && !marked.contains(v));
                if (!hasExplorableNeighbors) {
                    continue;
                }
                currentPath.add(currentVertex);
                for (Vertex neighbor : currentVertex.getNeighborsShuffled()) {
                    if (neighbor != null && !neighbor.isWall() && !neighbor.isStart() && !currentPath.contains(neighbor) && !marked.contains(neighbor)) {
                        stack.push(new Pair<Vertex, Vertex>(currentVertex, neighbor));
                    }
                }
            }
        }
        return distinctPaths;
    }

    public ArrayList<String> getFittingWords(ArrayList<String> dictionary, int pathLength) {
        for (int i = 0; i < dictionary.size(); i++) {
            String word = dictionary.remove(i);
            pathLength -= word.length();
            if (pathLength == 0) {
                ArrayList<String> result = new ArrayList<>();
                result.add(word);
                return result;
            } else if (pathLength < 0) {
                dictionary.add(i, word);
                return null;
            } else if (pathLength > 0) {
                ArrayList<String> result = this.getFittingWords(dictionary, pathLength);
                if (result == null) {
                    dictionary.add(i, word);
                    pathLength += word.length();
                } else {
                    result.add(0, word);
                    return result;
                }
            }
        }
        return null; // this code is unreachable
    }
}
