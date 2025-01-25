package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public abstract class Labyrinth {
    public static Graph create() {
        // this.map.addVertexAt('A', 0, 0);
        // this.map.addVertexAt('B', 1, 0);
        // this.map.addVertexAt('C', 1, 1);
        // this.map.addVertexAt('D', 2, 1);
        // this.map.addVertexAt('E', 2, 2);
        // this.map.addVertexAt('F', 1, 2);
        // this.map.addVertexAt('G', 0, 2);
        // this.map.addVertexAt('H', 0, 3);
        // this.map.addVertexAt('I', 0, 4);
        Graph map = new Graph();
        int currentX = 0;
        int currentY = 0;
        map.addVertexAt('S', currentX, currentY);
        int i = 50;

        do {
            ArrayList<Integer> allowedDirections = new ArrayList<>();
            if (currentY > 0 && map.getVertexAt(currentX, currentY - 1) == null) {
                allowedDirections.add(Graph.UP);
            }
            if (currentY < Graph.MAX_Vertices_ROWS - 1 && map.getVertexAt(currentX, currentY + 1) == null) {
                allowedDirections.add(Graph.DOWN);
            }
            if (currentX > 0 && map.getVertexAt(currentX - 1, currentY) == null) {
                allowedDirections.add(Graph.LEFT);
            }
            if (currentX < Graph.MAX_Vertices_COLUMNS - 1 && map.getVertexAt(currentX + 1, currentY) == null) {
                allowedDirections.add(Graph.RIGHT);
            }

            if (allowedDirections.size() == 0) {
                break;
            }

            int direction = getRandomDirection(allowedDirections);

            if (direction == Graph.UP) {
                map.addVertexTo((char) ('A' + i), map.getVertexAt(currentX, currentY), Graph.UP);
                currentY--;
            } else if (direction == Graph.DOWN) {
                map.addVertexTo((char) ('A' + i), map.getVertexAt(currentX, currentY), Graph.DOWN);
                currentY++;
            } else if (direction == Graph.LEFT) {
                map.addVertexTo((char) ('A' + i), map.getVertexAt(currentX, currentY), Graph.LEFT);
                currentX--;
            } else if (direction == Graph.RIGHT) {
                map.addVertexTo((char) ('A' + i), map.getVertexAt(currentX, currentY), Graph.RIGHT);
                currentX++;
            }

        } while (i-- != 0);
        return map;
    }

    static int getRandomDirection(ArrayList<Integer> allowedDirections) {
        if (allowedDirections.size() == 0) {
            throw new IllegalArgumentException("At least one direction must be allowed.");
        }
        Random random = new Random();
        return allowedDirections.get(random.nextInt(allowedDirections.size()));
    }

    static ArrayList<String> readWords(String path) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String word;
        while ((word = bufferedReader.readLine()) != null) {
            words.add(word);
        }
        bufferedReader.close();
        return words;
    }
}
