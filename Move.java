
public abstract class Move {
    
    public abstract void updateBoard(Board b);
    
    public abstract String toString();
    
    public Move() {
        
    }
}

class EntryMove extends Move {
    
    private final Piece p;
    private final HexVector h;
    
    public EntryMove(Piece p, HexVector h) {
        super();
        this.p = p;
        this.h = h;
    }
    
    public void updateBoard(Board b) {
        b.add(p, h);
        b.update();
    }
    
    public String toString() {
        return p.getAbbrev() + " " + h.toString();
    }
}

class ChangeMove extends Move {
    
    private Piece p;
    private final HexVector h0;
    private final HexVector h1;
    
    public ChangeMove(HexVector h0, HexVector h1) {
        super();
        this.p = null;
        this.h0 = h0;
        this.h1 = h1;
    }
    
    public ChangeMove(Piece p, HexVector h0, HexVector h1) {
        super();
        this.p = p;
        this.h0 = h0;
        this.h1 = h1;
    }
    
    public void updateBoard(Board b) {
        Piece temp = b.remove(h0);
        b.add(temp, h1);
        
        this.p = new Piece(temp);
        
        b.update();
    }
    
    public String toString() {
        return p.getAbbrev() + " " + h0.toString() + " " + h1.toString();
    }
}

class PassMove extends Move {
    
    public PassMove() {
        super();
    }
    
    public void updateBoard(Board b) {
        b.update();
    }
    
    public String toString() {
        return "pass";
    }
}