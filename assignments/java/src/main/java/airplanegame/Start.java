package airplanegame;

import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        JFrame frame = new JFrame("1945");
        GamePanel gp = new GamePanel();

        frame.add(gp);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gp.requestFocusInWindow();
        gp.startGame();
    }
}
