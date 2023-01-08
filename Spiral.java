import java.util.Iterator;

public class Spiral implements Iterator<HexVector> {
    
    int level = 0;
    private HexVector last;
    int dir;
    
    public Spiral() {
       last = null; 
       dir = 0;
    }

    @Override
    public boolean hasNext() {
        return true;
    }
    
    @Override
    public HexVector next() {
        last = getNext();
        return last; 
    }
    
    private HexVector getNext() {
        if (level == 0) {
            level++;
            return HexVector.ZERO;
        }
        if (last.equals(HexVector.ZERO)) {
            dir = 0;
            return HexVector.UPRIGHT;
        }
        if (last.equals(HexVector.UP.scale(level))) {
            level++;
            dir = 0;
            return HexVector.UP.scale(level).add(HexVector.DOWNRIGHT);
        }
        if (last.equals(HexVector.CARDINALDIRECTIONS[(dir + 5) % 6].scale(level))) {
            dir = (dir + 5) % 6;
        }
        return last.add(HexVector.CARDINALDIRECTIONS[(dir + 4) % 6]);
    }
}
