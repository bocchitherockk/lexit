package src;
public class Main {
    public static void main(String[] args) throws Exception {
        Game game = new Game("singers.txt", Game.Difficulty.HARD);
        game.start();
    }
}
