package src;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private boolean isRunning;
    private int difficulty; // 1 - easy, 2 - medium, 3 - hard
    private Graph map;
    private Player player;
    private ArrayList<String> words;

    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;

    public Game(String filePath) throws IOException {
        this(filePath, Game.EASY);
    }

    public Game(String filePath, int difficulty) throws IOException {
        this.isRunning = false;
        this.difficulty = difficulty;
        this.map = Labyrinth.create();
        this.player = new Player(this.map.getVertexAt(0, 0), Color.BG_RED, Color.ST_BOLD);
        this.words = Labyrinth.readWords(filePath);
        if (this.difficulty == Game.HARD) {
            for (int i = 0; i < this.words.size(); i++) {
                String currentString = this.words.get(i);
                String reversedString = "";
                for (int j = currentString.length() - 1; j >= 0; j--) {
                    reversedString += currentString.charAt(j);
                }
                this.words.set(i, reversedString);
            }
        }
    }

    private void resetCursorPosition() {
        int cols = this.map.getRowsCount() * 4 + 3;
        System.out.print("\u001B[" + cols + "A");
    }

    public void start() throws IOException {
        this.isRunning = true;
        Scanner scanner = new Scanner(System.in);
        int visitedVerticesCount = 0;
        String collectedWord = "";
        String errorMessage = "";
        while (this.isRunning) {
            this.render();
            System.out.print("Score: " + this.player.getScore());
            System.out.print("        collected word: " + collectedWord);
            System.out.print(errorMessage.length() == 0 ? "" : "      message: " + errorMessage);
            System.out.println();
            System.out.print("enter your move(s): ");
            String input = scanner.nextLine().trim().toLowerCase();
            System.out.println();
            while (!input.isEmpty()) {
                char direction = input.charAt(0);
                if (direction == 'z' || direction == 's' || direction == 'q' || direction == 'd') {
                    this.player.move(direction);
                    collectedWord += this.player.getPosition().getLabel();
                    // if (the start of the word is found as the beginning of a word in the list of words) {
                    //    > do a dfs search to see if that path can reach you to the ending point of the labyrinth
                    // }
                    // else {
                    //    > subtract 5 points for each letter in the constructed word
                    //    > undo the last move (don't forget to remove the last letter from the constructed word)
                    // } 
                } else {
                    this.player.setScore(this.player.getScore() - input.length() * 5);
                    break;
                }
                input = input.substring(1);
            }
            this.resetCursorPosition();
        }
        scanner.close();
    }

    public void render() {
        /*
          ┌───┐  ┌───┐  ┌───┐
          │ A ├──┤ B ├──┤ D │
          └─┬─┘  └───┘  └───┘
            │    
          ┌─┴─┐
          │ C │
          └───┘
         */
        char[][] matrix = this.map.toMatrix();
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

        // String cross = "\u253c"; // ┼
        String space = " ";

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                    this.player.getColor().apply();
                }

                if (matrix[y][x] == ' ') {
                    System.out.print(space + space + space + space + space);
                } else {
                    System.out.print(topLeftCorner);
                    System.out.print(horizontalLine);
                    if (y != 0 && matrix[y - 1][x] != ' ') {
                        System.out.print(middleBottom);
                    } else {
                        System.out.print(horizontalLine);
                    }
                    System.out.print(horizontalLine);
                    System.out.print(topRightCorner);
                }
                this.player.getColor().reset();
                System.out.print(space + space);
            }
            System.out.println();

            for (int x = 0; x < matrix[y].length; x++) {
                if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                    this.player.getColor().apply();
                }

                if (matrix[y][x] == ' ') {
                    System.out.print(space + space + space + space + space);
                } else {
                    if (x != 0 && matrix[y][x - 1] != ' ') {
                        System.out.print(middleRight);
                    } else {
                        System.out.print(verticalLine);
                    }
                    System.out.print(space);
                    System.out.print(matrix[y][x]);
                    System.out.print(space);
                    if (x != matrix[y].length - 1 && matrix[y][x + 1] != ' ') {
                        System.out.print(middleLeft);
                    } else {
                        System.out.print(verticalLine);
                    }
                }
                this.player.getColor().reset();
                if (matrix[y][x] != ' ' && x != matrix[y].length - 1 && matrix[y][x + 1] != ' ') {
                    System.out.print(horizontalLine + horizontalLine);
                } else {
                    System.out.print(space + space);
                }
            }
            System.out.println();

            for (int x = 0; x < matrix[y].length; x++) {
                if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                    this.player.getColor().apply();
                }

                if (matrix[y][x] == ' ') {
                    System.out.print(space + space + space + space + space);
                } else {
                    System.out.print(bottomLeftCorner);
                    System.out.print(horizontalLine);
                    if (y != matrix.length - 1 && matrix[y + 1][x] != ' ') {
                        System.out.print(middleTop);
                    } else {
                        System.out.print(horizontalLine);
                    }
                    System.out.print(horizontalLine);
                    System.out.print(bottomRightCorner);
                }
                this.player.getColor().reset();
                System.out.print(space + space);
            }
            System.out.println();

            for (int x = 0; x < matrix[y].length; x++) {
                System.out.print(space + space);
                if (matrix[y][x] != ' ' && y != matrix.length - 1 && matrix[y + 1][x] != ' ') {
                    System.out.print(verticalLine);
                } else {
                    System.out.print(space);
                }
                System.out.print(space + space + space + space);
            }
            System.out.println();
        }
    }
}
