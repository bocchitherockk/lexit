package src;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private boolean isRunning;
    private Game.Difficulty difficulty;
    private Labyrinth map;
    private Player player;

    // in game
    private String collectedWord;
    private String message;

    // getters and setters
    public boolean isRunning() { return this.isRunning; }
    public void setRunning(boolean isRunning) { this.isRunning = isRunning; }

    public Game.Difficulty getDifficulty() { return this.difficulty; }
    public void setDifficulty(Game.Difficulty difficulty) { this.difficulty = difficulty; }

    public Labyrinth getMap() { return this.map; }
    public void setMap(Labyrinth map) { this.map = map; }

    public Player getPlayer() { return this.player; }
    public void setPlayer(Player player) { this.player = player; }



    public Game(String filePath) throws IOException, NoPathsException {
        this(filePath, Game.Difficulty.EASY);
    }

    public Game(String filePath, Game.Difficulty difficulty) throws IOException, NoPathsException {
        this.isRunning = false;
        this.difficulty = difficulty;
        this.map = new Labyrinth(filePath, difficulty);
        this.player = new Player(this.map.getStart(), Style.BG_RED, Style.ST_BOLD);

        this.collectedWord = "";
        this.message = "";
    }

    public void start() throws IOException {
        this.isRunning = true;
        Scanner scanner = new Scanner(System.in);
        // for losing: (✖﹏✖)
        // for winning: ♡＼(￣▽￣)／♡
        ArrayList<Vertex> shortestPath = this.map.getShortestPath();
        // SoundPlayer.loop("./pekora bgm(in game).wav");
        Pair<ArrayList<Vertex>, ArrayList<String>> activePath = null; // this is the path the player chooses to traverse, it will be set once the player starts moving
        int activePathStep = 0; // this is the vertex index the player is currently at in the active path
        FileWriter fw = new FileWriter(new File("help.txt"));
        while (this.isRunning) {
            this.render();
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("help")) {
                for (Pair<ArrayList<Vertex>, ArrayList<String>> pair : this.map.getDistinctPaths()) {
                    for (String s : pair.getValue()) {
                        fw.write(s + " ");
                    }
                    fw.write("\n********************************\n");
                }
                fw.flush();
                continue;
            }
            while (!input.isEmpty()) {
                char direction = input.charAt(0);
                Vertex oldPosition = this.player.getPosition();
                try {
                    this.player.move(direction);
                } catch (InvalidMoveException e) {
                    this.player.decreaseScore(input.length() * 5);; // subtract 5 points for each letter from when the move is invalid
                    this.message = "invalid move! ヾ( ･`⌓´･)ﾉﾞ" + e.getMessage();
                    break;
                }
                if (this.player.getPosition() == this.map.getEnd()) {
                    this.message = "Congratulations! you won the game! ♡＼(￣▽￣)／♡";
                    this.isRunning = false;
                    break;
                }
                this.collectedWord += this.player.getPosition().getLabel();
                if (activePath == null) {
                    activePath = this.getActivePath(this.player.getPosition());
                }
                if (activePath == null || activePath.getKey().get(activePathStep) != this.player.getPosition()) {
                    this.player.decreaseScore(5); // subtract only 5 points
                    this.message = "No word starts with: `" + this.collectedWord + "` try again please (╥﹏╥)";
                    this.player.setPosition(oldPosition);
                    this.collectedWord = this.collectedWord.substring(0, this.collectedWord.length() - 1);
                    break;
                } else if (activePath.getValue().contains(this.collectedWord)) {
                    this.message = "Congratulations!, you found the word: `" + this.collectedWord + "`! keep it up (≧▽≦)";
                    this.player.increaseScore(this.collectedWord.length() * 10);
                    this.collectedWord = "";
                } else {
                    this.message = "";
                }
                this.player.getPosition().setStyle(Style.BG_BLUE);
                activePathStep++;
                input = input.substring(1);
            }
        }
        scanner.close();
    }

    public Pair<ArrayList<Vertex>, ArrayList<String>> getActivePath(Vertex start) {
        for (Pair<ArrayList<Vertex>, ArrayList<String>> pair : this.map.getDistinctPaths()) {
            if (pair.getKey().get(0) == start) return pair;
        }
        return null;
    }

    public void render() {
        /* level: MEDIUM/HARD
          ┌───┐  ┌───┐  ┌───┐
          │ A ├──┤ B ├──┤ D │
          └─┬─┘  └─┬─┘  └───┘
            │   ╳  │   ╱     
          ┌─┴─┐  ┌─┴─┐       
          │ C ├──┤ E │       
          └───┘  └───┘       
         */
        /* level: EASY
          ┌─────┐    ┌─────┐    ┌─────┐
          │     │    │     │    │     │
          │  A  ├────┤  B  ├────┤  D  │
          └──┬──┘    └──┬──┘    └─────┘
             │    ╲╱    │
             │    ╱╲    │
          ┌──┴──┐    ┌──┴──┐
          │     │    │     │
          │  C  ├────┤  E  │
          └─────┘    └─────┘
         */
        Style.clearScreen();
        char[][] matrix = this.map.getGraph().toMatrix();
        String horizontalLine = "\u2500"; // ─
        String verticalLine = "\u2502"; // │

        String topLeftCorner = "\u250c"; // ┌
        String topRightCorner = "\u2510"; // ┐
        String bottomLeftCorner = "\u2514"; // └
        String bottomRightCorner = "\u2518"; // ┘

        String middleLeft = "\u251c"; // ├
        String middleRight = "\u2524"; // ┤
        String middleTop = "\u252c"; // ┬
        String middleBottom = "\u2534"; // ┴

        String space = " ";

        int scalar = this.map.getScalar();

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] == '\0') {
                    System.out.print(space); // for the top left corner
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the middle bottom line
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the top right corner
                } else {
                    Vertex currentVertex = this.map.getGraph().getVertexAt(x, y);
                    Style.applyStyle(currentVertex == this.player.getPosition() ? this.player.getStyle() : currentVertex.getStyle());
                    System.out.print(topLeftCorner);
                    System.out.print(horizontalLine.repeat(scalar));
                    if (y != 0 && matrix[y - 1][x] != '\0') {
                        System.out.print(middleBottom);
                    } else {
                        System.out.print(horizontalLine);
                    }
                    System.out.print(horizontalLine.repeat(scalar));
                    System.out.print(topRightCorner);
                }
                Style.resetStyle();
                System.out.print(space.repeat(2).repeat(scalar));
            }
            System.out.println();

            for (int i = 0; i < scalar; i++) {
                for (int x = 0; x < matrix[y].length; x++) {
                    if (matrix[y][x] == '\0') {
                        System.out.print(space); // for the middle right / vertical line
                        System.out.print(space.repeat(scalar)); // for the space
                        System.out.print(space); // for the label
                        System.out.print(space.repeat(scalar)); // for the space
                        System.out.print(space); // for the middle left / vertical line
                    } else {
                        Vertex currentVertex = this.map.getGraph().getVertexAt(x, y);
                        Style.applyStyle(currentVertex == this.player.getPosition() ? this.player.getStyle() : currentVertex.getStyle());

                        if (x != 0 && matrix[y][x - 1] != '\0' && i == scalar / 2) {
                            System.out.print(middleRight);
                        } else {
                            System.out.print(verticalLine);
                        }
                        System.out.print(space.repeat(scalar));
                        if (i == scalar / 2) {
                            System.out.print(matrix[y][x]);
                        } else {
                            System.out.print(space);
                        }
                        System.out.print(space.repeat(scalar));
                        if (x != matrix[y].length - 1 && matrix[y][x + 1] != '\0' && i == scalar / 2) {
                            System.out.print(middleLeft);
                        } else {
                            System.out.print(verticalLine);
                        }
                    }
                    Style.resetStyle();
                    if (matrix[y][x] != '\0' && x != matrix[y].length - 1 && matrix[y][x + 1] != '\0' && i == scalar / 2) {
                        System.out.print(horizontalLine.repeat(2).repeat(scalar));
                    } else {
                        System.out.print(space.repeat(2).repeat(scalar));
                    }
                }
                System.out.println();
            }

            for (int x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] == '\0') {
                    System.out.print(space); // for the bottom left corner
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the middle top / horizontal line
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the bottom right corner
                } else {
                    Vertex currentVertex = this.map.getGraph().getVertexAt(x, y);
                    Style.applyStyle(currentVertex == this.player.getPosition() ? this.player.getStyle() : currentVertex.getStyle());

                    System.out.print(bottomLeftCorner);
                    System.out.print(horizontalLine.repeat(scalar));
                    if (y != matrix.length - 1 && matrix[y + 1][x] != '\0') {
                        System.out.print(middleTop);
                    } else {
                        System.out.print(horizontalLine);
                    }
                    System.out.print(horizontalLine.repeat(scalar));
                    System.out.print(bottomRightCorner);
                }
                Style.resetStyle();
                System.out.print(space.repeat(2).repeat(scalar));
            }
            System.out.println();

            for (int i = 0; i < scalar; i++) {
                for (int x = 0; x < matrix[y].length; x++) {
                    System.out.print(space); // for the left corner
                    System.out.print(space.repeat(scalar)); // for the horizontal line(s)
                    if (matrix[y][x] != '\0' && y != matrix.length - 1 && matrix[y + 1][x] != '\0') {
                        System.out.print(verticalLine);
                    } else {
                        System.out.print(space);
                    }
                    System.out.print(space.repeat(scalar)); // for the horizontal line(s)
                    System.out.print(space); // for the right corner
                    System.out.print(space.repeat(2).repeat(scalar));
                }
                System.out.println();
            }
        }

        // print the player's score and the collected word
        System.out.print("Score: " + this.player.getScore());
        System.out.print("        collected word: " + this.collectedWord);
        System.out.println("        message: " + this.message);
        System.out.print("enter your move(s): ");
    }
}
