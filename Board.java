import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

enum GameStatus {
    INPLAY,
    WHITEWIN,
    BLACKWIN,
    DRAW
}

public class Board {
    
    private HexGrid<List<Piece>> board;
    private GameStatus status;
    private int turn;
    
    public Board() {
        board = new HexGrid<List<Piece>>();
        status = GameStatus.INPLAY;
        turn = 1;
    }
    
    //Copy Constructor
    public Board(Board b) {
        board = new HexGrid<List<Piece>>();
        for (HexVector h : b.getOccSpaces()) {
            List<Piece> stack = new ArrayList<Piece>();
            for (Piece p : b.getStack(h)) {
                stack.add(new Piece(p));
            }
            board.add(stack, h);;
        }
        
        status = b.getStatus();
        turn = b.getTurn();
    }
    
    //Updating status
    
    public void update() {
        updateStatus();
        turn++;
    }
    
    private void updateStatus() {
        boolean whiteLost = false, blackLost = false;
        
        for (HexVector h : board) {
            List<Piece> tgt = board.get(h);
            if (tgt.size() == 0) { continue; }
            Piece p = tgt.get(0);        
            if (p.getBug() == Bug.QUEEN && isSurrounded(h)) {
                if (p.getColor() == PlayerColor.WHITE) { whiteLost = true; }
                else { blackLost = true; }
            }
        }
        
        if (!whiteLost && !blackLost) { status = GameStatus.INPLAY; }
        if (whiteLost && !blackLost) { status = GameStatus.BLACKWIN; }
        if (!whiteLost && blackLost) { status = GameStatus.WHITEWIN; }
        if (whiteLost && blackLost) { status = GameStatus.DRAW; }
    }
    
    public GameStatus getStatus() { 
        return status;
    }
    
    // Accessor and mutator methods
    
    public void add(Piece p, HexVector h) {
        if (board.isEmpty(h))
            board.add(new ArrayList<Piece>(), h);
        board.get(h).add(p);
    }
    
    public Piece remove(HexVector h) {
        if (board.isEmpty(h)) { return null; }
        Piece removed = board.get(h).remove(board.get(h).size() - 1);
        if (board.get(h).size() == 0) { board.remove(h); }
        return removed;
    }
    
    public Piece getTopPiece(HexVector h) {
        if (board.get(h) == null) {
            return null;
        }
        if (board.get(h).size() == 0) {
            return null;
        }
        return board.get(h).get(board.get(h).size() - 1);
    }
    
    public List<Piece> getStack(HexVector h) {
        return board.get(h);
    }
    
    public boolean isEmpty(HexVector h) {
        List<Piece> p = board.get(h);
        return (p == null || p.isEmpty());
    }
    
    public boolean hasColor(HexVector h, PlayerColor c) {
        Piece p = getTopPiece(h);
        return (p != null && p.getColor() == c);
    }
    
    public int getTurn() {
        return turn;
    }
    
    public Set<HexVector> getOccSpaces() {
        Set<HexVector> entries = new HashSet<HexVector>();
        for (HexVector h : board) {
            if(!isEmpty(h)) { entries.add(h); }
        }
        return entries;
    }
    
    public Set<HexVector> getOccSpaces(PlayerColor c) {
        Set<HexVector> entries = new HashSet<HexVector>();
        for (HexVector h : board) {
            Piece toCheck = getTopPiece(h);
            if(toCheck != null && toCheck.getColor() == c) { entries.add(h); }
        }
        return entries;
    }
    
    public int getStackSize(HexVector h) {
        if (isEmpty(h)) { return 0; }
        return getStack(h).size();
    }
    
    public HexVector getQueenLocation(PlayerColor c) {
        for (HexVector h : board) {
            List<Piece> tgt = board.get(h);
            if (tgt.size() == 0) { continue; }
            Piece p = tgt.get(0);        
            if (p.getBug() == Bug.QUEEN && p.getColor() == c) { 
                return h;
            }
        }
        return null;
    }
    
    //Returns a higher number the better position that c's queen is in. Max is 0.
    public int evalQueen(HexVector h, PlayerColor c) {
        if (h == null) {
            return 0;
        }
        int result = -1;
        for (HexVector dir : HexVector.CARDINALDIRECTIONS) {
            HexVector tgt = h.add(dir);
            if (isEmpty(tgt)) { continue; }
            
            int n = getStackSize(h);
            if (getTopPiece(h).getColor() != c) { n *= 2; }
            
            result += n;
        }
        result += getStackSize(h);
        return -1 * result;
    }
    
    //Getting Moves
    
    private boolean connectedHelper(HexVector h, Set<HexVector> seen) {
        for (HexVector dir : HexVector.CARDINALDIRECTIONS) {
            HexVector tgt = h.add(dir);
            
            if (isEmpty(tgt) || seen.contains(tgt)) { continue; }
            
            seen.add(tgt);
            
            connectedHelper(tgt, seen);
        }
        return false;
    }
    
    //Checks to see if board, which has just had tile at point h removed
    //is still connected. Board must not be empty.
    private boolean isConnected() {
        Set<HexVector> seen = new HashSet<HexVector>();
        
        Iterator<HexVector> iter = board.iterator();
        
        connectedHelper(iter.next(), seen);
        
        while (iter.hasNext()) {
            if (!seen.contains(iter.next())) { return false; }
        }
        return true;
    }
    
    private boolean hasNeighbor(HexVector h) {
        for (HexVector dir : HexVector.CARDINALDIRECTIONS) {
            if (!board.isEmpty(h.add(dir))) { return true; }
        }
        return false;
    }
    
    private boolean isSurrounded(HexVector h) {
        for (HexVector dir : HexVector.CARDINALDIRECTIONS) {
            if (board.isEmpty(h.add(dir))) { return false; }
        }
        return true;
    }
    
    public Set<HexVector> getSteps(HexVector h) {
        
        Set<HexVector> result = new HashSet<HexVector>();
        
        for (int i = 0; i < 6; i++) {
            HexVector left = h.add(HexVector.CARDINALDIRECTIONS[i]);
            HexVector tgt = h.add(HexVector.CARDINALDIRECTIONS[(i + 1) % 6]);
            HexVector right = h.add(HexVector.CARDINALDIRECTIONS[(i + 2) % 6]);
            
            if (!board.isEmpty(tgt)) { continue; }
            
            if (!board.isEmpty(left) && !board.isEmpty(right)) { continue; } 

            if (hasNeighbor(tgt)) { result.add(tgt); }
        }
        
        return result;
    }
    
    private Set<HexVector> getBeetleMoves(HexVector h) {
        Set<HexVector> moves = new HashSet<HexVector>();
        
        for (int i = 0; i < 6; i++) {
            HexVector left = h.add(HexVector.CARDINALDIRECTIONS[i]);
            HexVector tgt = h.add(HexVector.CARDINALDIRECTIONS[(i + 1) % 6]);
            HexVector right = h.add(HexVector.CARDINALDIRECTIONS[(i + 2) % 6]);
            int lsize = getStackSize(left);
            int rsize = getStackSize(right);
            int hsize = getStackSize(h);
            int tgtsize = getStackSize(tgt);
            
            if (hsize >= lsize || tgtsize >= lsize ||
                hsize >= rsize || tgtsize >= rsize) {
                moves.add(tgt);
            }
        }
        
        return moves;
    }
    
    private Set<HexVector> getAntMovesHelper(HexVector h, Set<HexVector> seen, int depth) {
        Set<HexVector> moves = new HashSet<HexVector>();
        
        seen.add(h);
        
        //for (int i = 0; i < depth; i++) { System.out.print("\t"); }
        //System.out.println("testing " + h);
        
        for (HexVector tgt : getSteps(h)) {
            //for (int i = 0; i < depth + 1; i++) { System.out.print("\t"); }
            //System.out.println("tgt: " + tgt);
            if (seen.contains(tgt)) { continue; }
            
            moves.add(tgt);
            Set<HexVector> s = getAntMovesHelper(tgt, seen, depth + 1);
            moves.addAll(s);
            seen.addAll(s);
        }
        
        return moves;
    }
    
    private Set<HexVector> getAntMoves(HexVector h0) {
        Set<HexVector> seen = new HashSet<HexVector>();
        seen.add(h0);
        return getAntMovesHelper(h0, seen, 0);
    }
    
    private Set<HexVector> getSpiderMovesHelper(HexVector h, Set<HexVector> path, int depth) { 
        Set<HexVector> moves = new HashSet<HexVector>();
        path.add(h);
        
        if (depth == 0) {
            moves.add(h);
            return moves;
        }
        
        for (HexVector tgt : getSteps(h)) {
            if (path.contains(tgt)) { 
                continue; 
                }
            
            moves.addAll(getSpiderMovesHelper(tgt, new HashSet<HexVector>(path), depth - 1));
        }
        
        return moves;
    }
    
    private Set<HexVector> getSpiderMoves(HexVector h0) {
        Set<HexVector> seen = new HashSet<HexVector>();
        seen.add(h0);
        return getSpiderMovesHelper(h0, seen, 3);
    }
    
    private Set<HexVector> getHopperMoves(HexVector h) {
        Set<HexVector> moves = new HashSet<HexVector>();
        
        for (HexVector dir : HexVector.CARDINALDIRECTIONS) {
            HexVector tgt = h.add(dir);
            if(board.isEmpty(tgt)) { continue; }
            
            HexVector hop = tgt.add(dir);
            while(!board.isEmpty(hop)) {
                hop = hop.add(dir);
            }
            
            moves.add(hop);
        }
        
        return moves;
    }
    
    //The board should contain the piece we want to move
    public Set<HexVector> getLegalMoves(Bug bug, HexVector h, boolean turn) {
        Board b = new Board(this);
        b.remove(h);
        
        Set<HexVector> moves = new HashSet<HexVector>();
        
        if (!turn && !b.isConnected()) { return moves; }
        
        switch(bug) {
            case BEETLE:
                moves = b.getBeetleMoves(h);
                break;
            case ANT:
                moves = b.getAntMoves(h);
                break;
            case SPIDER:
                moves = b.getSpiderMoves(h);
                break;
            case QUEEN:
                moves = b.getSteps(h);
                break;
            case GRASSHOPPER:
                moves = b.getHopperMoves(h);
                break;
        }
        
        return moves;
    }
    
    public Set<HexVector> getLegalMoves(HexVector h) {
        return this.getLegalMoves(getTopPiece(h).getBug(), h, false);
    }
    
    private boolean isLegalEntry (HexVector h, PlayerColor c) {
        boolean hasNeighbor = false;
        for(HexVector dir : HexVector.CARDINALDIRECTIONS) {
            HexVector toCheck = h.add(dir);
            if (!isEmpty(toCheck)) {
                hasNeighbor = true;
                if (!getTopPiece(toCheck).getColor().equals(c)) { return false; }
            }
        }
        return hasNeighbor;
    }
    
    public Set<HexVector> getLegalEntries(PlayerColor c) {
        Set<HexVector> good = new HashSet<HexVector>();
        Set<HexVector> bad = new HashSet<HexVector>();
        
        if (turn == 1) {
            good.add(HexVector.ZERO);
            return good;
        }
        
        if (turn == 2) {
            for (HexVector h : HexVector.CARDINALDIRECTIONS) {
                good.add(h);
            }
            return good;
        }
        
        for(HexVector h : board) {
            for(HexVector dir : HexVector.CARDINALDIRECTIONS) {
                HexVector tgt = h.add(dir);
                
                if (good.contains(tgt) || bad.contains(tgt)) { continue; }
                
                if (!isEmpty(tgt)) {
                    bad.add(tgt);
                    continue;
                }
                
                if (isLegalEntry(tgt, c)) {
                    good.add(tgt);
                } else {
                    bad.add(tgt);
                }
            }
        }
        return good;
    }
    
    //Drawing
    
    public void draw(Graphics g, HexVector highlighted, int xoffset, int yoffset, int scale) {
        
        for (HexVector h : board) {
            Piece p = getTopPiece(h);
            p.draw(g, h.getXYCoord(xoffset, yoffset, scale), scale, (h.equals(highlighted)));
        }
    }
}
