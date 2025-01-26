package src;
import java.io.IOException;
import java.util.Scanner;

public class Game {
    private boolean isRunning;
    private Difficulty difficulty;
    private Labyrinth map;
    private Player player;

    public Game(String filePath) throws IOException {
        this(filePath, Difficulty.EASY);
    }

    public Game(String filePath, Difficulty difficulty) throws IOException {
        this.isRunning = false;
        this.difficulty = difficulty;
        int scalar = 1;
        if (difficulty == Difficulty.EASY) scalar = 2;
        this.map = new Labyrinth(filePath, scalar);
        // if the difficulty is hard, the player should find the words flipped
        if (this.difficulty == Difficulty.HARD) this.map.flipWords();
        this.map.fill();
        this.player = new Player(this.map.getGraph().getVertexAt(0, 0), Style.BG_RED, Style.ST_BOLD);
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
            Style.resetCursorPosition(this.map);
        }
        scanner.close();
    }

    public void render() {
        /* level: EASY
          ┌───┐  ┌───┐  ┌───┐
          │ A ├──┤ B ├──┤ D │
          └─┬─┘  └───┘  └───┘
            │    
          ┌─┴─┐
          │ C │
          └───┘
         */
        /* level: MEDIUM/HARD
          ┌─────┐    ┌─────┐    ┌─────┐
          │     │    │     │    │     │
          │  A  ├────┤  B  ├────┤  D  │
          └──┬──┘    └─────┘    └─────┘
             │    
             │
          ┌──┴──┐
          │     │
          │  C  │
          └─────┘
         */
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

        // String cross = "\u253c"; // ┼
        String space = " ";

        int scalar = this.map.getScalar();

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                    Style.applyStyle(this.player.getStyle());
                }

                if (matrix[y][x] == ' ') {
                    System.out.print(space); // for the top left corner
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the middle bottom line
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the top right corner
                } else {
                    System.out.print(topLeftCorner);
                    System.out.print(horizontalLine.repeat(scalar));
                    if (y != 0 && matrix[y - 1][x] != ' ') {
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
                    if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                        Style.applyStyle(this.player.getStyle());
                    }

                    if (matrix[y][x] == ' ') {
                        System.out.print(space); // for the middle right / vertical line
                        System.out.print(space.repeat(scalar)); // for the space
                        System.out.print(space); // for the label
                        System.out.print(space.repeat(scalar)); // for the space
                        System.out.print(space); // for the middle left / vertical line
                    } else {
                        if (x != 0 && matrix[y][x - 1] != ' ' && i == scalar / 2) {
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
                        // System.out.print("i == scalar / 2 = " + i + " == " + scalar / 2 + " = " + (boolean)(i == scalar / 2));
                        System.out.print(space.repeat(scalar));
                        if (x != matrix[y].length - 1 && matrix[y][x + 1] != ' ' && i == scalar / 2) {
                            System.out.print(middleLeft);
                        } else {
                            System.out.print(verticalLine);
                        }
                    }
                    Style.resetStyle();
                    if (matrix[y][x] != ' ' && x != matrix[y].length - 1 && matrix[y][x + 1] != ' ' && i == scalar / 2) {
                        System.out.print(horizontalLine.repeat(2).repeat(scalar));
                    } else {
                        System.out.print(space.repeat(2).repeat(scalar));
                    }
                }
                System.out.println();
            }

            for (int x = 0; x < matrix[y].length; x++) {
                if (x == this.player.getPosition().getX() && y == this.player.getPosition().getY()) {
                    Style.applyStyle(this.player.getStyle());
                }

                if (matrix[y][x] == ' ') {
                    System.out.print(space); // for the bottom left corner
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the middle top / horizontal line
                    System.out.print(space.repeat(scalar)); // for the horizontal line
                    System.out.print(space); // for the bottom right corner
                } else {
                    System.out.print(bottomLeftCorner);
                    System.out.print(horizontalLine.repeat(scalar));
                    if (y != matrix.length - 1 && matrix[y + 1][x] != ' ') {
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
                    if (matrix[y][x] != ' ' && y != matrix.length - 1 && matrix[y + 1][x] != ' ') {
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
    }
}
