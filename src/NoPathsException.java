package src;

public class NoPathsException extends Exception {
    public NoPathsException() {
        super("No paths found.");
    }
    public NoPathsException(String message) {
        super(message);
    }
    
}
