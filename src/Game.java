package src;
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
    private ArrayList<Vertex> visitedVertices; // to be used to verify if the path conforms with what Dijsktra's algorithm returns
    private String collectedWord;
    private String message;

    public boolean isRunning() { return this.isRunning; }
    public void setRunning(boolean isRunning) { this.isRunning = isRunning; }

    public Game.Difficulty getDifficulty() { return this.difficulty; }
    public void setDifficulty(Game.Difficulty difficulty) { this.difficulty = difficulty; }

    public Labyrinth getMap() { return this.map; }
    public void setMap(Labyrinth map) { this.map = map; }

    public Player getPlayer() { return this.player; }
    public void setPlayer(Player player) { this.player = player; }



    public Game(String filePath) throws IOException {
        this(filePath, Game.Difficulty.EASY);
    }

    public Game(String filePath, Game.Difficulty difficulty) throws IOException {
        this.isRunning = false;
        this.difficulty = difficulty;
        this.map = new Labyrinth(filePath, difficulty);
        this.player = new Player(this.map.getStart(), Style.BG_RED, Style.ST_BOLD);

        this.visitedVertices = new ArrayList<>();
        this.collectedWord = "";
        this.message = "";
    }

    public void start() throws IOException {
        this.isRunning = true;
        Scanner scanner = new Scanner(System.in);
        // for losing: (✖﹏✖)
        // for winning: ♡＼(￣▽￣)／♡
        /*
        dijkstra = applyDijkstra();
        if (this.visitedVerticesCount == dijkstra.get(this.map.getEnd())) {
            this.message = "Congratulations!, you took the shortest path to the way out. Here is a 50 points reward for your braveness(≧▽≦)";
            this.player.increaseScore(50);
            }
            */
        // SoundPlayer.loop("./pekora bgm(in game).wav");
        while (this.isRunning) {
            this.render();
            String input = scanner.nextLine().trim().toLowerCase();
            while (!input.isEmpty()) {
                char direction = input.charAt(0);
                Vertex oldPosition = this.player.getPosition();
                if (this.player.move(direction) == false) {
                    // this.player.decreaseScore(input.length() * 5);; // subtract 5 points for each letter from when the move is invalid
                    // this.message = "invalid move! ヾ( ･`⌓´･)ﾉﾞ";
                    // break;
                }
                this.collectedWord += this.player.getPosition().getLabel();
                // if (this.collectedWordExists(this.collectedWord)) {
                //     // > do a dfs search to see if that path can reach you to the ending point of the labyrinth
                //     /*  if (!dfs()) {
                //             // if the path can not reach you to the ending point of the labyrinth
                //             this.player.setScore(this.player.getScore() - 20); // subtract only 5 points
                //         } else*/ if (this.map.getWords().contains(this.collectedWord)) {
                //         this.message = "Congratulations!, you found the word: `" + this.collectedWord + "`! keep it up (≧▽≦)";
                //         this.player.increaseScore(this.collectedWord.length() * 10);
                //         this.collectedWord = "";
                //     }
                // } else {
                //     this.player.decreaseScore(20); // subtract only 20 points
                //     this.message = "No word starts with: `" + this.collectedWord + "` try again please (╥﹏╥)";
                //     this.player.setPosition(oldPosition);
                //     this.collectedWord = this.collectedWord.substring(0, this.collectedWord.length() - 1);
                // }
                input = input.substring(1);
            }
        }
        scanner.close();
    }

    public boolean collectedWordExists(String collectedWord) {
        for (String word : this.map.getWords()) {
            if (word.startsWith(collectedWord)) return true;
        }
        return false;
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
