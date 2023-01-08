import java.awt.Point;

public class HexVector implements Comparable<HexVector> {
    
    public final int u;
    public final int v;
    
    public static final HexVector ZERO = new HexVector(0, 0);
    public static final HexVector UP = new HexVector(1, 0);
    public static final HexVector DOWN = new HexVector(-1, 0);
    public static final HexVector UPRIGHT = new HexVector(1, 1);
    public static final HexVector DOWNRIGHT = new HexVector(0, 1);
    public static final HexVector UPLEFT = new HexVector(0, -1);
    public static final HexVector DOWNLEFT = new HexVector(-1, -1);
    
    public static final HexVector U = new HexVector(1, 0);
    public static final HexVector V = new HexVector(0, 1);
    
    public static final HexVector[] CARDINALDIRECTIONS = new HexVector[]{
            UP, UPLEFT, DOWNLEFT, DOWN, DOWNRIGHT, UPRIGHT
    };
    
    public HexVector (int u, int v) {
        this.u = u;
        this.v = v;
    }

    public HexVector add(HexVector o) {
        return new HexVector(this.u + o.u, this.v + o.v);
    }
    
    public HexVector scale(int scale) {
        return new HexVector(scale * this.u, scale * this.v);
    }

    public Point getXYCoord(int zerox, int zeroy, int size) {
        
        int x = zerox, y = zeroy;
        y -= u * size * 2;
        y += v * size;
        x += (int) (Math.sqrt(3d) * size * v);

        return new Point(x, y);
    }
    
    static public int round(double d) {
        return (int) Math.round(d);
    }
    
    static private boolean isInHexagon(Point p, HexVector h, int xoffset, int yoffset, double r) {
        double x = p.getX() - xoffset;
        double y = p.getY() - yoffset;
        return (Math.sqrt(Math.pow(x, 2d) + Math.pow(y, 2d)) <= r);

    }
    
    static public HexVector getHexCoords(Point p, int xoffset, int yoffset, int scale) {
        
        double r = 2d / Math.sqrt(3) * scale;
        double x = p.getX() - xoffset;
        double y = p.getY() - yoffset;

        int set = round((x - r) / (3d * r));
        
        double localx = x + r / 2d - set * (3d * r);
        
        int u = 0, v = 0;
        
        if (localx <= r) {
            u = round((-y) / (2 * scale)) + set;
            v = set * 2;
        } else if (localx <= (3d * r / 2)) {
            int yset = round((-scale - y) / (2 * scale));
            double localy = - yset * 2 * scale - y;
            if ((Math.abs(localy - scale) / scale) > (2d * (localx - r) / r)) {
                if (localy > scale) {
                    u = yset + 1 + set;
                } else {
                    u = yset + set;
                }
                v = 2 * set;
            } else {
                u = yset + set + 1;
                v = 2 * set + 1;
            } 
        } else if (localx <= (5d * r / 2d)) {
            u = round((-y + scale) / (2 * scale)) + set;
            v = 1 + set * 2;
        } else {
            int yset = round((-scale - y) / (2 * scale));
            double localy = - yset * 2 * scale - y; 
            if ((Math.abs(localy - scale) / scale) > (6 - 2d * (localx) / r)) {
                if (localy > scale) {
                    u = yset + 2 + set;
                } else {
                    u = yset + set + 1;
                }
                v = 2 * set + 2;
            } else {
                u = yset + set + 1;
                v = 2 * set + 1;
            } 
        }
        return new HexVector(u, v);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + u;
        result = prime * result + v;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        HexVector other = (HexVector) o;
        if (this.u == other.u && this.v == other.v)
            return true;
        return false;
    }

    @Override
    public int compareTo(HexVector o) {
        if (this.u == o.u)
            return this.v - o.v;
        else
            return this.u - o.u;
    }

    public static HexVector fromString(String s) {
        String[] parsed = s.split(",");
        if (parsed.length != 2) {
            throw new IllegalArgumentException();
        }
        Integer u = Integer.parseInt(parsed[0]);
        Integer v = Integer.parseInt(parsed[1]);
        return new HexVector(u, v);
    }
    
    @Override
    public String toString() {
        return u + "," + v;
    }
}
