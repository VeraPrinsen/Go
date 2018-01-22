package tests;

import boardView.GoGUIIntegrator;

/**
 * Example on how to use the GOGUI
 *
 * @author Daan van Beek
 */

public class TestBoardMain {

    public static void main(String[] args) {
        GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, 9);
        gogui.startGUI();
        gogui.setBoardSize(9);

        gogui.addStone(3, 4, false);
        gogui.addStone(2, 5, false);
        gogui.addStone(4, 5, false);
        gogui.addStone(3, 6, false);
        
        gogui.addStone(3, 5, true);
        
        
      

        
    }
}
