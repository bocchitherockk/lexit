package src;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Game game = new Game();
        game.start();
    }
}
