import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game implements Runnable {

    public static final Color HIGHLIGHTGREEN = new Color(204, 255, 153);
    final private JFrame frame = new JFrame("HIVE");
    final private JLabel status = new JLabel("Running...");
    
    final private String filename = "savestate";

    private Match currentMatch;
    
    @Override
    public void run() {
        final JPanel status_panel = new JPanel();
        frame.getContentPane().add(status_panel, BorderLayout.SOUTH);
        status.setFont(new Font("Dialog", Font.PLAIN, 30));
        status_panel.add(status);
        
        JMenuBar menubar = makeMenuBar();
        
        frame.setJMenuBar(menubar);
        
        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Would you like to load previous game?",
                "New Game",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == 0) {
            currentMatch = loadGame();
        } else if (choice == 1) {
            currentMatch = newGame();
        } else {
            throw new IllegalArgumentException("Dialog box went crazy");
        }
        
        frame.add(currentMatch, BorderLayout.CENTER);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private String getName(String prompt) {
        return (String) JOptionPane.showInputDialog(
                frame, prompt, "New Game",
                JOptionPane.PLAIN_MESSAGE, null, null, "");
    }
    
    private Match newGame() {
        
        String whiteName = getName("Choose name for White:");
        String blackName = getName("Choose name for Black:");
        
        String answers = whiteName + ",human," + blackName + ",human";
        return new Match(status, answers);
    }
    
    private Match loadGame() {
        Match m = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            m = new Match(status, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    
    private void saveGame() {

        try {
            Writer out = new BufferedWriter(new FileWriter(filename));
            currentMatch.writeState(out);
            out.flush();
            out.close();
            JOptionPane.showMessageDialog(frame, "Game Saved!", "HIVE", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void printToScreen(String s) {
        JOptionPane.showMessageDialog(frame, s, "HIVE Instructions", JOptionPane.PLAIN_MESSAGE);
    }
    
    
    private static final String[] helpLines = new String[]{
            "Hive is a game for 2 players. The goal of the game is to surround the opposing \n"
            + "player's Queen Bee - the first player to do this wins. Each player begins with\n"
            + " 11 tiles of their color, each of which represents a different bug. Players take \n"
            + "turns alternately to either place a new tile to the board or move a tile.\n",
            
            "Placing: Players must place a tile such that it is not touching any of their \n"
            + "opponent's pieces. The only exception to this rule is on each player's first \n"
            + "moves. The Queen Bee must be placed within the first 4 moves.\n",
            
            "Moving: Each tile moves in different ways depending on which type of bug it is. \n"
            + "For a tile to be moveable, it must abide by the One Hive Rule - the hive must stay\n"
            + "connected when this tile is removed. A tile must also be able to move into its new\n"
            + "position by sliding into it (except for Beetles).",
            
            "Queen: Queen Bees can move around the outside of the Hive one space at a time. Each "
            + "player has one Queen Bee.\n"
            + "Ant: Ants can move to any position on the outside of the Hive. Each player has 3 Ants.\n"
            + "Spider: Spiders can move around the outside of the Hive three spaces at a time. Each \n"
            + "player has 3 Spiders.\n"
            + "Beetle: Beetle can move one tile in any direction, including on top of the Hive.\n"
            + "Grasshopper: Grasshoppers can hop in any direction in which there is a tile directly \n"
            + "adjacent to it, and they keep moving until they reach an empty square."
    };
    
    private static final String[] UILines = new String[]{
            "To the left, you will see your tray of tiles. These are the tiles that you can play \n"
            + "into the board. On the right, you will see the Hive. Click on a tile to select it.\n"
            + "Once you click on a tile, dots will appear at each place where you can legally move\n"
            + "the tile. Hovering over one of these dots will enlarge it, and clicking on it will \n"
            + "move the piece there.",
            
            "If you want to close the program mid-game, click on File on the menu bar and select\n"
            + "Save Game. This will save the state of your game to a text file contained in the \n"
            + "directory. When you run the program, you will see an option to either load your old \n"
            + "game or run a new one. By load game, you will immeadietly return the previous state \n"
            + "of the game. Pressing new game will start a new game without overriding your saved \n"
            + "game.",
            
            "Note that there is only one save slot - saving a game will erase the previous game."
    };
    
    private void runHelp() {
        for (String s : helpLines) {
            printToScreen(s);
        }
    }
    
    private void runUIHelp() {
        for (String s : UILines) {
            printToScreen(s);
        }
    }
    
    private JMenuBar makeMenuBar() {
        final JMenuBar menubar = new JMenuBar();
        menubar.setFont(new Font("Dialog", Font.PLAIN, 30));
        
        JMenu file = new JMenu("File");
        file.setFont(new Font("Dialog", Font.PLAIN, 30));
        menubar.add(file);
        
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.setFont(new Font("Dialog", Font.PLAIN, 30));
        file.add(saveGame);
        
        saveGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                saveGame();
            }
        });
        
        JMenuItem saveAndExit = new JMenuItem("Save & Exit");
        saveAndExit.setFont(new Font("Dialog", Font.PLAIN, 30));
        file.add(saveAndExit);
        
        saveAndExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                saveGame();
                currentMatch.terminate();
                System.exit(0);
            }
        });
        
        JMenu help = new JMenu("Help");
        help.setFont(new Font("Dialog", Font.PLAIN, 30));
        menubar.add(help);
        
        JMenuItem rules = new JMenuItem("Rules");
        rules.setFont(new Font("Dialog", Font.PLAIN, 30));
        help.add(rules);
        
        rules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                runHelp();
            }
        });
        
        JMenuItem UIhelp = new JMenuItem("UI Help");
        UIhelp.setFont(new Font("Dialog", Font.PLAIN, 30));
        help.add(UIhelp);
        
        UIhelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                runUIHelp();
            }
        });
        
        return menubar;
    }

    public static void main(String[] args) { 
        SwingUtilities.invokeLater(new Game());
    }
}
