import java.awt.Color;

public enum PlayerColor {
    WHITE (Color.WHITE), 
    BLACK (Color.BLACK);
    
    private Color c;
    
    PlayerColor(Color c) {
        this.c = c;
    }

    public Color getColor() {
        return c;
    }
    
    public static PlayerColor getOpposite(PlayerColor color) {
        if (color == PlayerColor.WHITE)
            return PlayerColor.BLACK;
        else
            return PlayerColor.WHITE;
    }
    
    public String toString() {
        if (c == Color.WHITE)
            return "white";
        else
            return "black";
    }
}
