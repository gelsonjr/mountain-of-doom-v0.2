import javax.swing.*;

/** MountainOfDoom.java
 *  04/28/2020
 *
 *  Mountain of Doom is a turn-based RPG game using Swing to create the GUI.
 *  The combat animation is done entirely by the manipulation of CardLayout JPanels and
 *  transparency of .png images
 */

public class MountainOfDoom {

    public static final String PATH = "src/assets/";

    public static void main(String[] args) {

        // To keep Swing application thread safe.
        // Also, using lambda expressions (->) to simplify the method and make it more readable
        SwingUtilities.invokeLater(() -> {
            GUI game = new GUI();
            game.showGUI();
        });
    }
}