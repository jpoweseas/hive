import java.awt.Polygon;

@SuppressWarnings("serial")
public class RegHexagon extends Polygon {

    public RegHexagon(int cx, int cy, int r) {
        super();
        
        for(double angle = 0d; angle < 2d * Math.PI; angle += Math.PI / 3d) {
            super.addPoint(
                    cx + (int) Math.round(r * Math.cos(angle)),
                    cy + (int) Math.round(r * Math.sin(angle))
                    );
        }
    }
}
