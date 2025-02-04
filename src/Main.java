package src;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, Exception {
        Game game = new Game("footballers.txt", Game.Difficulty.EASY);
        game.start();
    }
}
