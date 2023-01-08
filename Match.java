import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class Match extends JPanel {
    Player whitePlayer;
    Player blackPlayer;
    boolean whiteTurn;
    
    Board mainBoard;
    
    public static final int WINDOW_WIDTH = 1250;
    public static final int WINDOW_HEIGHT = 1000;
    public static final int BLACK_Y = WINDOW_HEIGHT / 2;
    public static final int BOARD_LEFT_EDGE = 250;
    
    public static final BufferedImage[] imgs = new BufferedImage[5];
    
    //The x and y coordinates of the tile at HexVector(0, 0)
    private int xoffset = (WINDOW_WIDTH - BOARD_LEFT_EDGE) / 2 + BOARD_LEFT_EDGE;
    private int yoffset = WINDOW_HEIGHT / 2;
    //The apothem of a tile (not sure which)
    private int scale = 50;
    
    public static final int MOVE_SPEED = 20;
    public static final int SCALE_SPEED = 5;
    
    private JLabel status;
    
    private List<Move> movesRecord;

    public Match(JLabel status) {
        createBugPics();
        super.setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        whiteTurn = true;
        
        mainBoard = new Board();
     
        Mouse mouseListener = new Mouse();
        this.addMouseMotionListener(mouseListener);
        this.addMouseListener(mouseListener);
        
        movesRecord = new LinkedList<Move>();
        
        this.status = status;
        

        setFocusable(true); 
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    xoffset -= MOVE_SPEED;
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    xoffset += MOVE_SPEED;
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    yoffset += MOVE_SPEED;
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                    yoffset -= MOVE_SPEED;
                else if (e.getKeyCode() == KeyEvent.VK_EQUALS)
                    scale += SCALE_SPEED;
                else if (e.getKeyCode() == KeyEvent.VK_MINUS && scale >= 10)
                    scale -= SCALE_SPEED;
                repaint();
            }
        });
    }
    
    public Match(JLabel status, String info) {
        this(status);
        readHeader(info);
        
        status.setText(whitePlayer.getName() + "'s Turn");
        currentPlayer().runMove();
    }   
    
    public Match(JLabel status, BufferedReader r) throws IOException {
        this(status);
        readHeader(r.readLine());
        
        MoveReader mreader = new MoveReader(r);
        
        if(!mreader.hasNext()) {
            return;
        }
        
        Move lastMove = new PassMove();
        while(mreader.hasNext()) {
            Move mx = mreader.nextMove();
            
            if(mreader.hasNext()) {
                update(mx);
            } else {
                lastMove = mx;
            }
        }
        
        whitePlayer.updateTray();
        blackPlayer.updateTray();
        
        if (mainBoard.getStatus() == GameStatus.INPLAY) {
            currentPlayer().runMove();
        } else {
            endGame();
        }
        
        turn(lastMove);
    }
    
    private void createBugPics() {
        for(int i = 0; i < 5; i++) {
            try {
                imgs[i] = ImageIO.read(new File(Bug.BUGS[i].getIMGFile()));
            } catch (IOException e) {
                System.out.println("Internal Error:" + e.getMessage());
            }
        }
    }
    
    private void readHeader(String header) {
        String[] parsed = header.split(",");
        if (parsed.length != 4) {
            throw new IllegalArgumentException("Invalid Header");
        }
            
        if (parsed[1].equals("human")) {
            whitePlayer = new HumanPlayer(PlayerColor.WHITE, parsed[0]);
        } else if (parsed[1].equals("AI")) {
            whitePlayer = new AIPlayer(PlayerColor.WHITE, parsed[0]);
        } else {
            throw new IllegalArgumentException("Invalid Header");
        }
        
        if (parsed[3].equals("human")) {
            blackPlayer = new HumanPlayer(PlayerColor.BLACK, parsed[2]);
        } else if (parsed[3].equals("AI")) {
            blackPlayer = new AIPlayer(PlayerColor.BLACK, parsed[2]);
        } else {
            throw new IllegalArgumentException("Invalid Header");
        }
        
    }
    
    public void terminate() {
        whitePlayer.stop();
        blackPlayer.stop();
    }
    
    private Player currentPlayer() {
        if (whiteTurn) {
            return whitePlayer;
        } else {
            return blackPlayer;
        }
    }
    
    private void update(Move m) {
        m.updateBoard(mainBoard);
        whiteTurn = !whiteTurn;
        status.setText(currentPlayer().getName() + "'s Turn");
        movesRecord.add(m);
    }
    
    private void turn(Move m) {
        update(m);
        repaint();
        if (mainBoard.getStatus() == GameStatus.INPLAY) {
            currentPlayer().runMove();
        } else {
            endGame();
        }
    }
    
    private void endGame() {
        switch(mainBoard.getStatus()) {
            case WHITEWIN:
                status.setText(whitePlayer.getName() + " wins!");
                break;
            case BLACKWIN:
                status.setText(blackPlayer.getName() + " wins!");
                break;
            case DRAW:
            default:
                status.setText("The game is drawn");
                break;
        }
    }
    
    //IO
    
    public void writeState(Writer w) throws IOException {
        w.write(whitePlayer.getName() + "," + whitePlayer.getID() + "," + 
                blackPlayer.getName() + "," + blackPlayer.getID());
        for (Move m : movesRecord) {
            w.write("\n" + m.toString());
        }
    }
    
    //Drawing
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        mainBoard.draw(g, currentPlayer().getHighlighted(), xoffset, yoffset, scale);
        whitePlayer.drawMoves(g);
        blackPlayer.drawMoves(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, BOARD_LEFT_EDGE, WINDOW_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, BOARD_LEFT_EDGE, WINDOW_HEIGHT);
        
        g.drawLine(BOARD_LEFT_EDGE, 0, BOARD_LEFT_EDGE, WINDOW_HEIGHT);
        g.drawLine(0, BLACK_Y, BOARD_LEFT_EDGE, BLACK_Y);
        
        g.setFont(new Font("Dialog", Font.PLAIN, 40));
        g.drawString(whitePlayer.getName(), 25, 60);
        g.drawString(blackPlayer.getName(), 25, 60 + BLACK_Y);
        
        whitePlayer.drawTray(g);
        blackPlayer.drawTray(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    
    private class Mouse extends MouseAdapter {
        
        @Override
        public void mouseMoved(MouseEvent arg0) {
            currentPlayer().mouseMoved(arg0);
            repaint();
        }
      
        @Override
        public void mouseReleased(MouseEvent arg0) {
            currentPlayer().mouseReleased(arg0);
            repaint();
        }
    }
    
    /*Stuff with Players
     * 
     */
    
    abstract class Player {
        
        PlayerColor color;
        Tray tray;
        String name;
        
        public Player(PlayerColor c, String n) {
            color = c;
            tray = new Tray(c);
            name = n;
        }
        
        public String getName() {
            return name;
        }
        
        public abstract void runMove();
        
        public HexVector getHighlighted() {
            return null;
        }
        
        public void mouseMoved(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        
        public void drawMoves(Graphics g) { }
        public abstract void drawTray(Graphics g);
        
        public abstract String getID();
        
        public void updateTray() {
            for (HexVector h : mainBoard.getOccSpaces()) {
                List<Piece> stack = mainBoard.getStack(h);
                for (Piece p : stack) {
                    if (p.getColor() == color) {
                        tray.take(p.getBug());
                    }
                }
            }
        }
        
        public Tray getTray() {
            return tray;
        }
        
        public abstract void stop();
    }

    enum Mode {
        WAITMODE, PICKMODE, ENTRYMODE, MOVEMODE
    }
    
    class HumanPlayer extends Player {
        
        Mode mode;
        
        HexVector hover;
        HexVector selected;
        Bug toEnter;
        Bug hoverBug;
        Set<HexVector> possibleMoves;

        public HumanPlayer(PlayerColor c, String n) {
            super(c, n);
            hover = null;
            mode = Mode.WAITMODE;
            toEnter = null;
            possibleMoves = new HashSet<HexVector>();
        }
        
        private boolean hasMove() {
            for (HexVector h : mainBoard.getOccSpaces(color)) {
                if (!mainBoard.getLegalMoves(h).isEmpty()) {
                    return true;
                }
            }
            return !mainBoard.getLegalEntries(color).isEmpty();
        }
        
        @Override
        public void stop() {
            mode = Mode.WAITMODE;
        }

        @Override
        public void runMove() {
            if (hasMove()) {
                mode = Mode.PICKMODE;
            } else {
                turn(new PassMove());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            switch (mode) {
                case WAITMODE:
                    break;
                case PICKMODE:
                    Point click = e.getPoint();
                    
                    if (click.getX() >= 200) {
                        HexVector h = HexVector.getHexCoords(click, xoffset, yoffset, scale);
                        hoverBug = null;
                        if (mainBoard.hasColor(h, color)) {
                            hover = h;
                        } else {
                            hover = null;
                        }
                    } else {
                        Bug hovered = tray.bugFromPoint(click);
                        hover = null;
                        if (hovered != null && tray.hasBug(hovered)) {
                            hoverBug = hovered;
                        } else {
                            hoverBug = null;
                        }
                    }
                    break;
                case ENTRYMODE:
                case MOVEMODE:
                    Point click1 = e.getPoint();
                    if (click1.getX() >= 200) {
                        HexVector h = HexVector.getHexCoords(click1, xoffset, yoffset, scale);
                        if (possibleMoves.contains(h)) {
                            hover = h;
                        } else {
                            hover = null;
                        }
                    } else {
                        hover = null;
                    }
                    break;
                default:
                    hover = null;
            }
        }
        
        private void reset() {
            hover = null;
            selected = null;
            toEnter = null;
            hoverBug = null;
            possibleMoves.clear();
            mode = Mode.PICKMODE;
        }
        
        private void toWait() {
            hover = null;
            selected = null;
            toEnter = null;
            hoverBug = null;
            possibleMoves.clear();
            mode = Mode.WAITMODE;
        }
        
        private boolean needsQueen() {
            return (mainBoard.getTurn() > 6 && tray.hasBug(Bug.QUEEN));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            switch (mode) {
                case WAITMODE:
                    break;
                case PICKMODE:
                    Point click = e.getPoint();
                    
                    if (click.getX() >= 200) {
                        HexVector h = HexVector.getHexCoords(click, xoffset, yoffset, scale);
                        if (needsQueen()) {
                            status.setText("Must place queen by 4th turn!");
                        }
                        else if (mainBoard.hasColor(h, color)) {
                            selected = h;
                            possibleMoves = mainBoard.getLegalMoves(h);
                            mode = Mode.MOVEMODE;
                        }
                    } else {
                        Bug clickedBug = tray.bugFromPoint(click);
                        if (needsQueen() && clickedBug != Bug.QUEEN) {
                            status.setText("Must place queen by 4th turn!");
                        }
                        else if (clickedBug != null  && tray.hasBug(clickedBug)) {
                            toEnter = clickedBug;
                            possibleMoves = mainBoard.getLegalEntries(color);
                            mode = Mode.ENTRYMODE;
                        }
                    }
                    break;
                case ENTRYMODE:
                    Point click1 = e.getPoint();
                    
                    if (click1.getX() >= 200) {
                        HexVector h = HexVector.getHexCoords(click1, xoffset, yoffset, scale);
                        if (possibleMoves.contains(h)) {
                            Move m = new EntryMove(new Piece(toEnter, color), h);
                            tray.take(toEnter);
                            toWait();
                            turn(m);
                        } else {
                            reset();
                        }
                    } else {
                        reset();
                    }
                case MOVEMODE:
                    Point click2 = e.getPoint();
                    
                    if (click2.getX() >= 200) {
                        HexVector h = HexVector.getHexCoords(click2, xoffset, yoffset, scale);
                        if (possibleMoves.contains(h)) {
                            Move m = new ChangeMove(null, selected, h);
                            toWait();
                            turn(m);
                        } else {
                            reset();
                        }
                    } else {
                        reset();
                    }
                default:
                    break;
            }
        }
        
        @Override
        public HexVector getHighlighted() {
            switch (mode) {
                case PICKMODE: 
                    return hover;
                case MOVEMODE:
                    return selected;
                default:
                    return null;
            }
        }
        
        @Override
        public void drawTray(Graphics g) {
            tray.draw(g, hoverBug);
        }
        
        @Override
        public void drawMoves(Graphics g) {
            for (HexVector h : possibleMoves) {
                Point p = h.getXYCoord(xoffset, yoffset, scale);
                if (h.equals(hover)) {
                    g.fillOval((int) p.getX() - 15, (int) p.getY() - 15, 30, 30);
                }
                g.fillOval((int) p.getX() - 5, (int) p.getY() - 5, 10, 10);
            }
        }

        @Override
        public String getID() {
            return "human";
        }
    }
    
    
    //AI does not work. Functionality is left in because I might revisit this later.
    class AIPlayer extends Player {
        
        final int depth = 2;
        AI ai;

        public AIPlayer(PlayerColor c, String n) {
            super(c, n);
        }

        @Override
        public void runMove() {
            ai = new AI();
            ai.execute();
        }

        @Override
        public void drawTray(Graphics g) {
            tray.draw(g, null);
        }
        
        @Override
        public String getID() {
            return "AI";
        }

        @Override
        public void stop() {
            //Do Nothing
        }
        
        class AI extends SwingWorker<Move, Object> {
            public AI() {
                super();
            }
            
            private HexVector randHVFromSet(Set<HexVector> items) {
                Random rn = new Random();
                List<HexVector> list = new ArrayList<HexVector>();
                list.addAll(items);
                return list.get(rn.nextInt(list.size()));
            }
            
            private Integer checkWin(Board b, PlayerColor turn) {
                if (b.getStatus() == GameStatus.DRAW) {
                    return 0;
                } else if (b.getStatus() == GameStatus.WHITEWIN) {
                    if (turn == PlayerColor.WHITE) {
                        return 10000;
                    } else {
                        return -10000;
                    }
                } else if (b.getStatus() == GameStatus.BLACKWIN) {
                    if (turn == PlayerColor.WHITE) {
                        return -10000;
                    } else {
                        return 10000;
                    }
                }
                return null;
            }
            
            private int finalEvalBoard(Board b, PlayerColor turn) {
                Integer checkWin = checkWin(b, turn);
                if (checkWin != null) { return checkWin; }
                
                HexVector thisQ = b.getQueenLocation(turn);
                HexVector enemyQ = b.getQueenLocation(PlayerColor.BLACK);
                
                int queens = b.evalQueen(thisQ, turn) - 
                             b.evalQueen(enemyQ, PlayerColor.getOpposite(turn));
                
                int pieces = b.getOccSpaces(turn).size() - 
                            b.getOccSpaces(PlayerColor.getOpposite(turn)).size();
                return queens + 3 * pieces;
            }
            
            private List<Move> getAllMoves(Board b, PlayerColor turn, boolean onlyEntry) {
                List<Move> moves = new ArrayList<Move>();
                
                //Gets all possible entry moves
                Set<HexVector> entries = b.getLegalEntries(turn);
                for (Bug bug : Bug.BUGS) {
                    if (!tray.hasBug(bug)) { continue; }
                    
                    for (HexVector h : entries) {
                        Move m = new EntryMove(new Piece(bug, turn), h);
                        moves.add(m);
                    }
                }
                
                if (!onlyEntry) {
                    //Gets all possible movement moves
                    for (HexVector h0 : b.getOccSpaces(turn)) {
                        for (HexVector hf : b.getLegalMoves(h0)) {
                            Move m = new ChangeMove(null, h0, hf);
                            moves.add(m);
                        }
                    }
                }
                
                if (moves.isEmpty()) {
                    moves.add(new PassMove());
                }
                
                return moves;
            }
            
            //Returns minimax evaluation of move m on Board b.
            private int evalBoard(Board b, int depth, PlayerColor turn) {
                
                Integer checkWin = checkWin(b, turn);
                if (checkWin != null) { return checkWin; }
                
                List<Move> moves = getAllMoves(b, turn, false);
                
                int max = -100000;
                for (Move m : moves) {
                    Board newBoard = new Board(b);
                    m.updateBoard(newBoard);
                    if (depth == 0) {
                        max = Math.max(max, finalEvalBoard(newBoard, turn));
                    } else {
                        max = Math.max(max, -1 * evalBoard(newBoard, depth - 1, 
                                       PlayerColor.getOpposite(turn)));
                    }
                }

                return max;
            }
            
            @Override
            public Move doInBackground() {
                if (mainBoard.getTurn() == 1) {
                    return new EntryMove(new Piece(Bug.SPIDER, color), new HexVector(0, 0));
                }
                if (mainBoard.getTurn() == 2) {
                    return new EntryMove(new Piece(Bug.SPIDER, color), new HexVector(1, 0));
                }
                if (mainBoard.getTurn() <= 4) {
                    Set<HexVector> hs = mainBoard.getLegalEntries(color);
                    HexVector h = randHVFromSet(hs);
                    return new EntryMove(new Piece(Bug.QUEEN, color), h);
                }
                
                List<Move> moves = getAllMoves(mainBoard, color, mainBoard.getTurn() <= 10);
                
                int max = -100000;
                Move bestMove = new PassMove();
                for (Move m : moves) {
                    Board newBoard = new Board(mainBoard);
                    m.updateBoard(newBoard);

                    max = Math.max(max, -1 * evalBoard(newBoard, depth - 1, 
                                   PlayerColor.getOpposite(color)));
                    bestMove = m;
                }
                
                return bestMove;
            }

            @Override
            protected void done() {
                try {
                    turn(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ai = null;
                updateTray();
            }
        }
    }
}