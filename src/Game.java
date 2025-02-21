package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import src.Exceptions.InvalidMoveException;

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

    public String getCollectedWord() { return this.collectedWord; }
    public void setCollectedWord(String collectedWord) { this.collectedWord = collectedWord; }

    public String getMessage() { return this.message; }
    public void setMessage(String message) { this.message = message; }

    public Game(String filePath) throws IOException, InterruptedException, ExecutionException {
        this(filePath, Game.Difficulty.EASY);
    }

    public Game(String filePath, Game.Difficulty difficulty) throws IOException, InterruptedException, ExecutionException {
        this.isRunning = false;
        this.difficulty = difficulty;
        this.map = new Labyrinth(filePath, difficulty);
        this.player = new Player(this.map.getStart(), Style.BG_RED, Style.ST_BOLD);

        this.collectedWord = "";
        this.message = "";
    }

    public void start() throws IOException, InterruptedException {
        this.isRunning = true;
        Scanner scanner = new Scanner(System.in);
        ArrayList<ArrayList<Vertex>> allPaths = this.map.getAllValidPaths();

        int shortestPathLength = this.map.getShortestPathLength(allPaths);
        int steps = 0;

        while (this.isRunning) {
            Renderer.renderMap(this);

            String moves = scanner.nextLine().trim().toLowerCase();
            if (moves.startsWith("help")) {
                this.help();
                continue;
            }

            while (!moves.isEmpty()) {
                char direction = moves.charAt(0);
                Vertex oldPosition = this.player.getPosition();
                try {
                    this.player.move(direction);
                } catch (InvalidMoveException e) {
                    this.player.decreaseScore(moves.length() * 5);; // subtract 5 points for each letter from when the move is invalid
                    this.message = "invalid move! ヾ( ･`⌓´･)ﾉﾞ" + e.getMessage();
                    break;
                }

                if (this.player.getPosition() == this.map.getEnd()) {   // reached the end, this will not provoke a bug as when generating the labyrinth, once the end is one of the neighbors, select the path.
                    this.isRunning = false;
                    
                    if (steps == shortestPathLength) {
                        this.message = "Congratulations! you found the shortest path! \\(≧∇≦)/, here is a 200 points bonus!";
                        this.player.increaseScore(200);
                    }
                    this.message = this.player.getScore() < 0 ? "You lost the game! (✖﹏✖), your score is: " + this.player.getScore() : "Congratulations! you won the game! ♡＼(￣▽￣)／♡, your score is: " + this.player.getScore();
                    break;
                }

                this.collectedWord += this.player.getPosition().getLabel();
                if (this.map.endIsReachable(this.player.getPosition(), this.collectedWord)) {
                    steps++;
                    if (this.map.getDictionary().contains(this.collectedWord)) {
                        this.message = "Congratulations!, you found the word: `" + this.collectedWord + "`! keep it up (≧▽≦)";
                        this.player.increaseScore(this.collectedWord.length() * 10);
                        this.collectedWord = "";
                    } else {
                        this.message = "Keep going! (≧▽≦)";
                    }
                } else {
                    if (this.map.doesPrefixExist(this.collectedWord)) {
                        this.player.decreaseScore(5); // subtract only 5 points
                        this.message = "that path leads to a dead end! (╥﹏╥)";
                    } else {
                        this.player.decreaseScore(10); // subtract 10 points
                        this.message = "No word starts with: `" + this.collectedWord + "` try again please (╥﹏╥)";
                    }
                    this.player.setPosition(oldPosition);
                    this.collectedWord = this.collectedWord.substring(0, this.collectedWord.length() - 1);
                }
                this.player.getPosition().addStyles(Style.BG_CYAN);
                Renderer.renderMap(this);;
                moves = moves.substring(1);
            }
        }
        scanner.close();
    }

    public void help() throws IOException, InterruptedException {
        int helpLevel;
        if      (this.difficulty == Game.Difficulty.EASY)   helpLevel = 3;
        else if (this.difficulty == Game.Difficulty.MEDIUM) helpLevel = 2;
        else                                                helpLevel = 1;

        this.player.decreaseScore(30);
        
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
}
