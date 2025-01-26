package src;
public class Main {
    public static void main(String[] args) throws Exception {
        Game game = new Game("singers.txt", Difficulty.HARD);
        game.start();
    }
}
