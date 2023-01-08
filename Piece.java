import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piece {
    
    private final Bug bug;
    private final PlayerColor color;
    
    public Piece(Bug b, PlayerColor c) {
        bug = b;
        color = c;
        /*
        try {
            if (img == null) {
                img = ImageIO.read(new File(bug.getIMGFile()));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        */
    }
    
    public Piece(Piece p) {
        this.bug = p.getBug();
        this.color = p.getColor();
    }

    public PlayerColor getColor() {
        return color;
    }

    public Bug getBug() {
        return bug;
    }
    
    public char getAbbrev() {
        return bug.getAbbrev();
    }
    
    public String getName() {
        return bug.getName();
    }

    public void draw(Graphics g, Point p, int size, boolean highlighted) {
        int x = (int) p.getX();
        int y = (int) p.getY();
        
        BufferedImage img = Match.imgs[bug.getIndex()];
        
        if (highlighted) 
            g.setColor(Game.HIGHLIGHTGREEN);
        else
            g.setColor(color.getColor());
        
        int r = (int) (2d / Math.sqrt(3) * size);
        
        g.fillPolygon(new RegHexagon(x, y, r));
        g.drawImage(img, x - size / 2, y  - size / 2, size, size, null);
        g.setColor(Color.BLACK);
        
        
        
        g.drawPolygon(new RegHexagon(x, y, r));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Piece other = (Piece) obj;
        if (bug != other.bug)
            return false;
        if (color != other.color)
            return false;
        return true;
    }
    
}
