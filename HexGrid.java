import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HexGrid<E> implements Iterable<HexVector> {
    
    Map<HexVector, E> data;
    
    public HexGrid() {
        data = new TreeMap<HexVector, E>();
    }
    
    public E get(HexVector h) {
        return data.get(h);
    }
    
    public E get(int u, int v) {
        return this.get(new HexVector(u, v));
    }
    
    public void add(E e, HexVector h) {
        data.put(h, e);
    }
    
    public void add(E e, int u, int v) {
        data.put(new HexVector(u, v), e);
    }
    
    public boolean isEmpty(HexVector h) {
        return !data.containsKey(h);
    }

    public boolean isEmpty(int u, int v) {
        return !data.containsKey(new HexVector(u, v));
    }
    
    public E remove(HexVector h) {
        return data.remove(h);
    }
    
    public E remove(int u, int v) {
        return remove(new HexVector(u, v));
    }

    @Override
    public Iterator<HexVector> iterator() {
        Set<HexVector> x = data.keySet();
        return x.iterator();
    } 
    
    public int[] getExtent() {
        int minu = 0, maxu = 0, minv = 0, maxv = 0;
        
        for (HexVector h : this) {
            minu = Math.min(minu, h.u);
            maxu = Math.max(maxu, h.u);
            minv = Math.min(minv, h.v);
            maxv = Math.max(maxv, h.v);
        }
        
        return new int[]{minu, maxu, minv, maxv};
    }
}


