package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import src.Exceptions.InvalidMoveException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Game {
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public enum Theme {
        FOOTBALLERS,
        COUNTRIES,
        ANIMALS
    }    

    private enum State {
        NEW_GAME,
        LEADERBOARD_MENU,
        EXIT,
        MAIN_MENU,
    }

    private boolean isRunning;
    private Game.Difficulty difficulty;
    private Game.Theme theme;
    private Labyrinth map;
    private Player player;

    // in game
    private String collectedWord;
    private String message;

    Terminal terminal;  // this is used to capture key strokes on the fly

    // getters and setters
    public boolean isRunning() { return this.isRunning; }
    public void setRunning(boolean isRunning) { this.isRunning = isRunning; }

    public Game.Difficulty getDifficulty() { return this.difficulty; }
    public void setDifficulty(Game.Difficulty difficulty) { this.difficulty = difficulty; }

    public Labyrinth getMap() { return this.map; }
    public void setMap(Labyrinth map) { this.map = map; }

    public Player getPlayer() { return this.player; }
    public void setPlayer(Player player) { this.player = player; }

    public String getCollectedWord() { return this.collectedWord; }
    public void setCollectedWord(String collectedWord) { this.collectedWord = collectedWord; }

    public String getMessage() { return this.message; }
    public void setMessage(String message) { this.message = message; }

    public Terminal getTerminal() { return this.terminal; }
    public void setTerminal(Terminal terminal) { this.terminal = terminal; }

    public Game() throws IOException, InterruptedException, ExecutionException {
        this.terminal = TerminalBuilder.terminal();
        terminal.enterRawMode();
        this.handleGameFlow();

        this.isRunning = false;

        this.map = new Labyrinth("dictionaries/" + this.theme.toString().toLowerCase() + ".txt", difficulty);
        this.player = new Player(this.map.getStart(), Style.BG_RED, Style.ST_BOLD);

        this.collectedWord = "";
        this.message = "";
    }

    public void start() throws IOException, InterruptedException {
        this.isRunning = true;
        ArrayList<ArrayList<Vertex>> allPaths = this.map.getAllValidPaths();

        int shortestPathLength = this.map.getShortestPathLength(allPaths);
        int steps = 0; // to keep track of the steps taken by the player to compare it with the length of the shortest path

        while (this.isRunning) {
            Renderer.renderMap(this);

            int key = this.terminal.reader().read();
            if (key == 'h') {
                this.help();
                continue;
            }
            
            Vertex oldPosition = this.player.getPosition();
            try {
                this.player.move((char) key);
            } catch (InvalidMoveException e) {
                this.player.decreaseScore(3); // subtract 3 points for each letter from when the move is invalid
                this.message = "invalid move! ヾ( ･`⌓´･)ﾉﾞ" + e.getMessage();
                continue;
            }

            if (this.player.getPosition() == this.map.getEnd()) {   // reached the end, this will not provoke a bug as when generating the labyrinth, once the end is one of the neighbors, select the path.
                this.isRunning = false;
                
                if (steps == shortestPathLength) {
                    this.message = "Congratulations! you found the shortest path! \\(≧∇≦)/, here is a 15 points bonus!";
                    this.player.increaseScore(15);
                }
                this.message = this.player.getScore() < 0 ? "You lost the game! (✖﹏✖), your score is: " + this.player.getScore() : "Congratulations! you won the game! ♡＼(￣▽￣)／♡, your score is: " + this.player.getScore();
                break;
            }
            
            this.collectedWord += this.player.getPosition().getLabel();
            if (this.map.endIsReachable(this.player.getPosition(), this.collectedWord)) {
                steps++;
                if (this.map.getDictionary().contains(this.collectedWord)) {
                    this.message = "Congratulations!, you found the word: `" + this.collectedWord + "`! keep it up (≧▽≦)";
                    this.player.increaseScore(this.collectedWord.length());
                    this.collectedWord = "";
                } else {
                    this.message = "Keep going! (≧▽≦)";
                }
            } else {
                if (this.map.doesPrefixExist(this.collectedWord)) {
                    this.player.decreaseScore(2); // subtract 2 points
                    this.message = "that path leads to a dead end! (╥﹏╥)";
                } else {
                    this.player.decreaseScore(3); // 3 points
                    this.message = "No word starts with: `" + this.collectedWord + "` try again please (╥﹏╥)";
                }
                this.player.setPosition(oldPosition);
                this.collectedWord = this.collectedWord.substring(0, this.collectedWord.length() - 1);
            }
            this.player.getPosition().addStyles(Style.BG_CYAN);
        }
    }

    private void help() throws IOException, InterruptedException {
        int helpLevel;
        if      (this.difficulty == Game.Difficulty.EASY)   helpLevel = 3;
        else if (this.difficulty == Game.Difficulty.MEDIUM) helpLevel = 2;
        else                                                helpLevel = 1;

        this.player.decreaseScore(10);
        
        for (int i = 0; i < this.map.getDistinctPaths().size(); i++) {
            for (Vertex v : this.map.getDistinctPaths().get(i).getKey()) {
                v.addStyles("\u001B[4" + (1 + i) + "m");
            }
            Renderer.renderMap(this);
            Thread.sleep(500 * helpLevel);
            for (Vertex v : this.map.getDistinctPaths().get(i).getKey()) {
                v.removeStyles("\u001B[4" + (1 + i) + "m");
            }
        }
    }

    private void handleGameFlow() throws IOException {
        Game.State currentState = Game.State.MAIN_MENU;
        while (currentState != Game.State.NEW_GAME) {
            switch (currentState) {
                case Game.State.MAIN_MENU        : currentState = showMainMenu(); break;
                case Game.State.LEADERBOARD_MENU : currentState = showLeaderBoardMenu(); break;
                case Game.State.EXIT             : { Style.clearScreen(); System.exit(0); } break;
            }
        }
        this.themesMenu();
        this.difficultyMenu();
    }

    private Game.State showMainMenu() throws IOException {
        int key;
        int selectedOption = 0;
        do {
            Renderer.renderMainMenu(selectedOption);
            key = this.terminal.reader().read();
            if (key == '6') {
                selectedOption = ((++selectedOption % 3) + 3) % 3; // the +3 % 3 again is to handle the negative values (meaning that in java -1 % 3 = -1, and we want it to be 2)
            } else if (key == '4') {
                selectedOption = ((--selectedOption % 3) + 3) % 3;
            }
        } while (key != 0xD); // // 0xD is the enter key
        return Game.State.values()[selectedOption];
    }

    private Game.State showLeaderBoardMenu() throws IOException {
        Renderer.renderLeaderBoardMenu();
        while (this.terminal.reader().read() != 0xD);
        return Game.State.MAIN_MENU;
    }

    private void difficultyMenu() throws IOException {
        int key;
        int selectedOption = 0;
        do {
            Renderer.renderDifficultyMenu(selectedOption);
            key = this.terminal.reader().read();
            if (key == '6') {
                selectedOption = ((++selectedOption % 3) + 3) % 3; // the +3 % 3 again is to handle the negative values (meaning that in java -1 % 3 = -1, and we want it to be 2)
            } else if (key == '4') {
                selectedOption = ((--selectedOption % 3) + 3) % 3;
            }
        } while (key != 0xD); // // 0xD is the enter key
        this.difficulty = Game.Difficulty.values()[selectedOption];
    }

    private void themesMenu() throws IOException {
        int key;
        int selectedOption = 0;
        do {
            Renderer.renderThemesMenu(selectedOption);
            key = this.terminal.reader().read();
            if (key == '6') {
                selectedOption = ((++selectedOption % 3) + 3) % 3; // the +3 % 3 again is to handle the negative values (meaning that in java -1 % 3 = -1, and we want it to be 2)
            } else if (key == '4') {
                selectedOption = ((--selectedOption % 3) + 3) % 3;
            }
        } while (key != 0xD); // // 0xD is the enter key
        this.theme = Game.Theme.values()[selectedOption];
    }
}
