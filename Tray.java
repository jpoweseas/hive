import java.awt.Graphics;
import java.awt.Point;

public class Tray {
   /*
    * Number of each type of bug remaining:
    * 0 : QUEEN
    * 1 : ANT
    * 2 : SPIDER
    * 3 : GRASSHOPPER
    * 4 : BEETLE
    */
    int[] data;
    static final Bug[] BUGS = new Bug[]{
            Bug.QUEEN, Bug.ANT, Bug.SPIDER, Bug.GRASSHOPPER, Bug.BEETLE
    };
    static final HexVector[] POSKEY = new HexVector[]{
            new HexVector(0, 0),
            new HexVector(0, 1),
            new HexVector(-1, 0),
            new HexVector(-1, 1),
            new HexVector(-2, 0)
    };
    public static final int TRAY_YOFFSET = 150;
    public static final int TRAY_X = 75;
    public static final int SCALE = 50;
    PlayerColor color;
    
    public Tray(PlayerColor c) {
        color = c;
        data = new int[]{1, 3, 2, 3, 2};
    }
    
    public boolean hasBug(Bug b) {
       return (data[b.getIndex()] != 0);
    }
    
    public void take(Bug b) {
        data[b.getIndex()] -= 1;
    }
    
    public void draw(Graphics g, Bug selected) {
        Board tempBoard = new Board();
        HexVector highlighted = null;
        int y = TRAY_YOFFSET;
        if (color == PlayerColor.BLACK) {
            y += Match.BLACK_Y;
        }
        
        for (int i = 0; i < 5; i++) {
            if (!hasBug(BUGS[i])) {
                continue;
            }
            tempBoard.add(new Piece(BUGS[i], color), POSKEY[i]);
            if (BUGS[i] == selected) {
                highlighted = POSKEY[i];
            }
        }
        tempBoard.draw(g, highlighted, TRAY_X, y, SCALE);
    }
    
    public Bug bugFromPoint(Point p) {
        int y = TRAY_YOFFSET;
        if (color == PlayerColor.BLACK) {
            y += Match.BLACK_Y;
        }
        HexVector h = HexVector.getHexCoords(p, TRAY_X, y, SCALE);
        
        for (int i = 0; i < 5; i++) {
            if (POSKEY[i].equals(h)) {
                return BUGS[i];
            }
        } 
        return null;
    }
}
