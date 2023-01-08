
import static org.junit.Assert.*;

import java.util.Set;
import org.junit.Test;

public class Testing {
    
    @Test
    public void testsAddingPieces() {
        Board b = new Board();
        @SuppressWarnings("serial")
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (0, 0));
        m.updateBoard(b);
        Piece p = b.getTopPiece(new HexVector(0, 0));
        assertTrue(p.equals(new Piece(Bug.QUEEN, PlayerColor.WHITE)));
        b.getLegalEntries(PlayerColor.BLACK);
    }
    
    @Test
    public void testAntMoves() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (0, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-2, 0));
        m2.updateBoard(b);
        Set<HexVector> moves = b.getLegalMoves(Bug.ANT, new HexVector (1, 0), true);
        assertEquals("moves is right size", moves.size(), 10);
    }
    
    @Test
    public void testFreedomRule() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (1, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-1, 0));
        m2.updateBoard(b);
        Move m3 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (0, 1));
        m3.updateBoard(b);
        Move m4 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (1, 1));
        m4.updateBoard(b);
        Move m5 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (0, -1));
        m5.updateBoard(b);
        
        Set<HexVector> steps = b.getSteps(new HexVector (0, 0));
        assertEquals("steps is right size", steps.size(), 0);

        Set<HexVector> moves = b.getLegalMoves(Bug.ANT, new HexVector (0, 0), true);
        assertEquals("moves is right size", moves.size(), 0);
    }
    
    @Test
    public void testSpiderMoves() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (0, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-2, 0));
        m2.updateBoard(b);
        Move m3 = new EntryMove(new Piece(Bug.ANT, PlayerColor.WHITE), new HexVector (2, 3));
        m3.updateBoard(b);
        Set<HexVector> moves = b.getLegalMoves(Bug.SPIDER, new HexVector (1, 0), true);
        
        assertEquals("moves is right size", moves.size(), 8);
    }
    
    @Test
    public void testHopperMoves() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (1, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-1, -1));
        m2.updateBoard(b);
        Move m3 = new EntryMove(new Piece(Bug.ANT, PlayerColor.WHITE), new HexVector (-2, -2));
        m3.updateBoard(b);
        Move m4 = new EntryMove(new Piece(Bug.ANT, PlayerColor.BLACK), new HexVector (-2, 0));
        m4.updateBoard(b);
        Set<HexVector> moves = b.getLegalMoves(Bug.GRASSHOPPER, new HexVector (0, 0), true);
        
        assertEquals("moves is right size", moves.size(), 2);
    }
    
    @Test
    public void testQueenMoves() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (1, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-1, -1));
        m2.updateBoard(b);
        Move m3 = new EntryMove(new Piece(Bug.ANT, PlayerColor.WHITE), new HexVector (-2, -2));
        m3.updateBoard(b);
        Move m4 = new EntryMove(new Piece(Bug.ANT, PlayerColor.BLACK), new HexVector (-2, 0));
        m4.updateBoard(b);
        Set<HexVector> moves = b.getLegalMoves(Bug.QUEEN, new HexVector (0, 0), true);
        
        assertEquals("moves is right size", moves.size(), 2);
        
        for (HexVector h : moves) {
            System.out.println(h);
        }        
    }
    
    @Test
    public void testQueenMovesAgain() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (0, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-2, 0));
        m2.updateBoard(b);
        Move m3 = new EntryMove(new Piece(Bug.ANT, PlayerColor.WHITE), new HexVector (2, 3));
        m3.updateBoard(b);
        Set<HexVector> moves = b.getLegalMoves(Bug.QUEEN, new HexVector (-1, 0), true);
        
        assertEquals("moves is right size", moves.size(), 4);
    }
    
    @Test
    public void testBeetleMoves() {
        Board b = new Board();
        
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (1, 0));
        m.updateBoard(b);
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (-1, 0));
        m2.updateBoard(b);
        
        Set<HexVector> moves = b.getLegalMoves(Bug.BEETLE, new HexVector (0, 0), true);
        assertEquals("moves is right size", moves.size(), 6);
        
        Move m3 = new EntryMove(new Piece(Bug.BEETLE, PlayerColor.WHITE), new HexVector (0, 0));
        m3.updateBoard(b);
        
        Set<HexVector> moves2 = b.getLegalMoves(Bug.BEETLE, new HexVector (0, 0), true);
        assertEquals("moves is right size", moves2.size(), 6);
        
        Move m4 = new ChangeMove(null, new HexVector(0, 0), new HexVector(1, 0));
        m4.updateBoard(b);
        
        Set<HexVector> moves3 = b.getLegalMoves(Bug.BEETLE, new HexVector (0, 0), true);
        assertEquals("moves is right size", moves3.size(), 6);
        assertEquals("yea whatever", b.getTopPiece(new HexVector(1, 0)), new Piece(Bug.BEETLE, PlayerColor.WHITE));
        
        Move m5 = new ChangeMove(new HexVector(1, 0), new HexVector(0, 0));
        m5.updateBoard(b);
        
        assertEquals("yea whatever", b.getTopPiece(new HexVector(0, 0)), new Piece(Bug.BEETLE, PlayerColor.WHITE));
        assertEquals("yea whatever", b.getTopPiece(new HexVector(1, 0)), new Piece(Bug.QUEEN, PlayerColor.WHITE));
        assertEquals("yea whatever", b.getTopPiece(new HexVector(-1, 0)), new Piece(Bug.QUEEN, PlayerColor.BLACK));
    }
    
    @Test
    public void testBeetleMovesAgain() {
        Board b = new Board();
        Move m = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.WHITE), new HexVector (1, 1));
        Move m2 = new EntryMove(new Piece(Bug.QUEEN, PlayerColor.BLACK), new HexVector (0, -1));
        m.updateBoard(b);
        m2.updateBoard(b);
        
        Set<HexVector> moves = b.getLegalMoves(Bug.BEETLE, new HexVector (0, 0), true);
        assertEquals("moves is right size", moves.size(), 5);
    }
}
