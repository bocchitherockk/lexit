package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Renderer {
    private static final String horizontalLine    = "\u2500"; // ─
    private static final String verticalLine      = "\u2502"; // │

    private static final String topLeftCorner     = "\u250c"; // ┌
    private static final String topRightCorner    = "\u2510"; // ┐
    private static final String bottomLeftCorner  = "\u2514"; // └
    private static final String bottomRightCorner = "\u2518"; // ┘

    private static final String middleLeft        = "\u251c"; // ├
    private static final String middleRight       = "\u2524"; // ┤
    private static final String middleTop         = "\u252c"; // ┬
    private static final String middleBottom      = "\u2534"; // ┴

    private static final String slash             = "\u2571"; // ╱
    private static final String backSlash         = "\u2572"; // ╲
    private static final String cross             = "\u2573"; // ╳

    private static final String space             = " ";

    private static final String graffiti = """
                            __      __       .__                                  __           .____                 .__  __  ._.
                           /  \\    /  \\ ____ |  |   ____  ____   _____   ____   _/  |_  ____   |    |    ____ ___  __|__|/  |_| |
                           \\   \\/\\/   // __ \\|  | _/ ___\\/  _ \\ /     \\_/ __ \\  \\   __\\/  _ \\  |    |  _/ __ \\\\  \\/  /  \\   __\\ |
                            \\        /\\  ___/|  |_\\  \\__(  <_> )  Y Y  \\  ___/   |  | (  <_> ) |    |__\\  ___/ >    <|  ||  |  \\|
                             \\__/\\  /  \\___  >____/\\___  >____/|__|_|  /\\___  >  |__|  \\____/  |_______ \\___  >__/\\_ \\__||__|  __
                                  \\/       \\/          \\/            \\/     \\/                         \\/   \\/      \\/         \\/
               
               
               ___________.__            .___   __  .__              __      __                .___                          .___                                            
               \\_   _____/|__| ____    __| _/ _/  |_|  |__   ____   /  \\    /  \\___________  __| _/______ _____    ____    __| _/   ____   ______ ____ _____  ______   ____  
                |    __)  |  |/    \\  / __ |  \\   __\\  |  \\_/ __ \\  \\   \\/\\/   /  _ \\_  __ \\/ __ |/  ___/ \\__  \\  /    \\  / __ |  _/ __ \\ /  ___// ___\\\\__  \\ \\____ \\_/ __ \\ 
                |     \\   |  |   |  \\/ /_/ |   |  | |   Y  \\  ___/   \\        (  <_> )  | \\/ /_/ |\\___ \\   / __ \\|   |  \\/ /_/ |  \\  ___/ \\___ \\\\  \\___ / __ \\|  |_> >  ___/ 
                \\___  /   |__|___|  /\\____ |   |__| |___|  /\\___  >   \\__/\\  / \\____/|__|  \\____ /____  > (____  /___|  /\\____ |   \\___  >____  >\\___  >____  /   __/ \\___  >
                    \\/            \\/      \\/             \\/     \\/         \\/                   \\/    \\/       \\/     \\/      \\/       \\/     \\/     \\/     \\/|__|        \\/ 
               
                                                                                                 __  .__                _____                        
                                                                                               _/  |_|  |__   ____     /     \\ _____  ________ ____  
                                                                                               \\   __\\  |  \\_/ __ \\   /  \\ /  \\\\__  \\ \\___   // __ \\ 
                                                                                                |  | |   Y  \\  ___/  /    Y    \\/ __ \\_/    /\\  ___/ 
                                                                                                |__| |___|  /\\___  > \\____|__  (____  /_____ \\\\___  >
                                                                                                          \\/     \\/          \\/     \\/      \\/    \\/ 
""";

    private static String center(String s, int width) {
        int leftPadding = (width - s.length()) / 2;
        int rightPadding = width - s.length() - leftPadding;
        return " ".repeat(leftPadding) + s + " ".repeat(rightPadding);
    }

    private static void renderMenu(String[] options, int selected) { // the selected parameter is used to highlight the selected option, 0, 1, 2
        Style.clearScreen();

        System.out.print("\n".repeat(3));
        Style.printStyled(graffiti, Style.FG_RED);
        System.out.print("\n".repeat(8));

        String[][] parts = new String[options.length][];
        for (int i = 0; i < options.length; i++) {
            parts[i] = new String[] {
                "┌──────────────────────────────┐",
                "│                              │",
                "│                              │",
                "│" + center(options[i], 30) + "│",
                "│                              │",
                "│                              │",
                "└──────────────────────────────┘"
            };
        }

        for (int j = 0; j < parts[0].length; j++) {
            for (int i = 0; i < options.length; i++) {
                System.out.print("                    ");
                if (i == selected) Style.applyStyle(Style.BG_BLUE, Style.ST_BOLD);
                System.out.print(parts[i][j]);
                Style.resetStyle();
            }
            System.out.println();
        }
    }

    public static void renderMainMenu(int selected) {
        renderMenu(new String[] {"New game", "LeaderBoard", "Exit"}, selected);
    }

    public static void renderDifficultyMenu(int selected) {
        renderMenu(new String[] {"Easy", "Medium", "Hard"}, selected);
    }

    public static void renderThemesMenu(int selected) {
        renderMenu(new String[] {"footballers", "countries", "animals"}, selected);
    }

    public static void renderLeaderBoardMenu() throws FileNotFoundException, IOException {
        Style.clearScreen();
        BufferedReader reader = new BufferedReader(new FileReader("leaderboard.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(center(line, 180)); // TODO: later on this should be centered based on the terminal width
        }

        String[] parts = new String[] {
                "┌──────────────────────────────┐",
                "│                              │",
                "│                              │",
                "│" + center("Return", 30) + "│",
                "│                              │",
                "│                              │",
                "└──────────────────────────────┘"
        };

        for (int j = 0; j < parts.length; j++) {
            System.out.print(" ".repeat(75));
            Style.applyStyle(Style.BG_BLUE, Style.ST_BOLD);
            System.out.println(parts[j]);
            Style.resetStyle();
        }

    }

    private static String[][][] getBoxes(char[][] matrix, int scalar) {
        // the scalar functionality is hard coded for now
        String[][][] boxes = new String[matrix.length][][];
        for (int y = 0; y < matrix.length; y++) {
            boxes[y] = new String[matrix[y].length][];
            for (int x = 0; x < matrix[y].length; x++) {
                boolean isLeftEdge   = x == 0;
                boolean isTopEdge    = y == 0;
                boolean isRightEdge  = x == matrix[y].length - 1;
                boolean isBottomEdge = y == matrix.length - 1;

                boolean hasTopNeighbor         = !isTopEdge    && matrix[y - 1][x] != '\0';
                boolean hasRightNeighbor       = !isRightEdge  && matrix[y][x + 1] != '\0';
                boolean hasBottomNeighbor      = !isBottomEdge && matrix[y + 1][x] != '\0';
                boolean hasLeftNeighbor        = !isLeftEdge   && matrix[y][x - 1] != '\0';

                if (matrix[y][x] == '\0') {
                    if (scalar == 1) {
                        boxes[y][x] = new String[] {
                            "     ",
                            "     ",
                            "     ",
                            "",
                        };
                    } else if (scalar == 2) {
                        boxes[y][x] = new String[] {
                            "       ",
                            "       ",
                            "       ",
                            "       ",
                            "",
                            ""
                        };
                    }
                } else {
                    if (scalar == 1) {
                        boxes[y][x] = new String[] {
                            hasTopNeighbor ? "┌─┴─┐" : "┌───┐",
                            (hasLeftNeighbor ? "┤ " : "│ ") + matrix[y][x] + (hasRightNeighbor ? " ├" : " │"),
                            hasBottomNeighbor ? "└─┬─┘" : "└───┘",
                            "",
                        };
                    } else if (scalar == 2) {
                        boxes[y][x] = new String[] {
                            hasTopNeighbor ? "┌──┴──┐" : "┌─────┐",
                            "│     │",
                            (hasLeftNeighbor ? "┤  " : "│  ") + matrix[y][x] + (hasRightNeighbor ? "  ├" : "  │"),
                            hasBottomNeighbor ? "└──┬──┘" : "└─────┘",
                            "",
                            "",
                        };
                    }
                }
            }
        }
        return boxes;
    }

    private static String[][][] getLinks(char[][] matrix, int scalar) {
        // the scalar functionality is hard coded for now
        String[][][] links = new String[matrix.length][][];
        for (int y = 0; y < matrix.length; y++) {
            links[y] = new String[matrix[y].length][];
            for (int x = 0; x < matrix[y].length; x++) {
                boolean isRightEdge  = x == matrix[y].length - 1;
                boolean isBottomEdge = y == matrix.length - 1;

                boolean hasRightNeighbor       = !isRightEdge  && matrix[y][x + 1] != '\0';
                boolean hasBottomNeighbor      = !isBottomEdge && matrix[y + 1][x] != '\0';
                boolean hasBottomRightNeighbor = !isRightEdge  && !isBottomEdge && matrix[y + 1][x + 1] != '\0';

                if (matrix[y][x] == '\0') {
                    if (scalar == 1) {
                        links[y][x] = new String[] {
                            isRightEdge ? "" : "   ",
                            isRightEdge ? "" : "   ",
                            isRightEdge ? "" : "   ",
                            "     " + (isRightEdge ? "" : (" " + (hasRightNeighbor && hasBottomNeighbor ? slash : " ") + " ")),
                        };
                    } else if (scalar == 2) {
                        links[y][x] = new String[] {
                            isRightEdge ? "" : "    ",
                            isRightEdge ? "" : "    ",
                            isRightEdge ? "" : "    ",
                            isRightEdge ? "" : "    ",
                            "       " + (isRightEdge ? "" : ("  " + (hasRightNeighbor && hasBottomNeighbor ? slash : " ") + " ")),
                            "       " + (isRightEdge ? "" : (" " + (hasRightNeighbor && hasBottomNeighbor ? slash : " ") + "  ")),
                        };
                    }
                } else {
                    if (scalar == 1) {
                        links[y][x] = new String[] {
                            isRightEdge ? "" : "   ",
                            isRightEdge ? "" : (hasRightNeighbor ? "───" : "   "),
                            isRightEdge ? "" : "   ",
                            (hasBottomNeighbor ? "  │  " : "     ") + (isRightEdge ? "" : (" " + (hasBottomRightNeighbor ? (hasRightNeighbor && hasBottomNeighbor ? cross : backSlash) : (hasRightNeighbor && hasBottomNeighbor ? slash : " ")) + " ")),
                        };
                    } else if (scalar == 2) {
                        links[y][x] = new String[] {
                            isRightEdge ? "" : "    ",
                            isRightEdge ? "" : "    ",
                            isRightEdge ? "" : (hasRightNeighbor ? "────" : "    "),
                            isRightEdge ? "" : "    ",
                            (hasBottomNeighbor ? "   │   " : "       ") + (isRightEdge ? "" : (" " + (hasBottomRightNeighbor ? backSlash : " ") + (hasBottomNeighbor && hasRightNeighbor ? slash : " ") + " ")),
                            (hasBottomNeighbor ? "   │   " : "       ") + (isRightEdge ? "" : (" " + (hasBottomNeighbor && hasRightNeighbor ? slash : " ") + (hasBottomRightNeighbor ? backSlash : " ") + " ")),
                        };
                    }
                }
            }
        }
        return links;
    }

    private static void renderGameInfo(Game game) {
        // print the player's score and the collected word
        Style.printStyled("Score: " + game.getPlayer().getScore(), game.getPlayer().getScore() >= 0 ? Style.FG_GREEN : Style.FG_RED);
        Style.printStyled("        collected word: " + game.getCollectedWord(), Style.FG_YELLOW);
        Style.printStyled("        message: " + game.getMessage() + '\n', Style.FG_CYAN);
        Style.printStyled("enter your move(s): ", Style.FG_MAGENTA);
    }

    public static void renderMap(Game game) {
        /* level: MEDIUM/HARD
          ┌───┐   ┌───┐   ┌───┐
          │ A ├───┤ B ├───┤ D │
          └─┬─┘   └─┬─┘   └───┘
            │   ╳   │   ╱     
          ┌─┴─┐   ┌─┴─┐       
          │ C ├───┤ E │       
          └───┘   └───┘       
         */
        /* level: EASY
          ┌─────┐    ┌─────┐    ┌─────┐
          │     │    │     │    │     │
          │  A  ├────┤  B  ├────┤  D  │
          └──┬──┘    └──┬──┘    └─────┘
             │    ╲╱    │     ╱
             │    ╱╲    │    ╱
          ┌──┴──┐    ┌──┴──┐
          │     │    │     │
          │  C  ├────┤  E  │
          └─────┘    └─────┘
         */
        Style.clearScreen();
        char[][] matrix = game.getMap().getGraph().toMatrix();
        String[][][] boxes = Renderer.getBoxes(matrix, game.getMap().getScalar());
        String[][][] links = Renderer.getLinks(matrix, game.getMap().getScalar());

        for (int y = 0; y < boxes.length; y++) {
            for (int j = 0; j < boxes[y][0].length; j++) {
                for (int x = 0; x < boxes[y].length; x++) {
                    Vertex currentVertex = game.getMap().getGraph().getVertexAt(x, y);
                    Style.applyStyle(currentVertex == game.getPlayer().getPosition() ? game.getPlayer().getStyle() : currentVertex.getStyle());
                    System.out.print(boxes[y][x][j]);
                    Style.resetStyle();
                    System.out.print(links[y][x][j]);
                }
                System.out.println();
            }
        }

        Renderer.renderGameInfo(game);
    }
}
