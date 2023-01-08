import java.io.BufferedReader;
import java.io.IOException;

public class MoveReader {
    private BufferedReader input;  
    private String next;
    private PlayerColor currentPlayer;
    
    public MoveReader(BufferedReader in) throws IOException {
        input = in;
        next = in.readLine();
        currentPlayer = PlayerColor.WHITE;
    }
    
    public Move parser() throws IOException {
        if (next.equals("pass")) {
            return new PassMove();
        }
        
        String[] parsed = next.split(" ");
        
        if (parsed.length == 2) {
            return new EntryMove(
                    new Piece(Bug.fromAbbrev(parsed[0]), currentPlayer), 
                    HexVector.fromString(parsed[1])
                    );
        } else if (parsed.length == 3) {
            return new ChangeMove(
                    new Piece(Bug.fromAbbrev(parsed[0]), currentPlayer), 
                    HexVector.fromString(parsed[1]),
                    HexVector.fromString(parsed[2])
                    );
        } else {
            throw new IOException ("Invalid Format");
        }
    }

    public Move nextMove() throws IOException {
        Move output = parser();
        next = input.readLine();
        if (currentPlayer == PlayerColor.WHITE) {
            currentPlayer = PlayerColor.BLACK;
        } else {
            currentPlayer = PlayerColor.WHITE;
        }
        return output;
    }
    
    public boolean hasNext() {
        return (next != null);
    }
}
