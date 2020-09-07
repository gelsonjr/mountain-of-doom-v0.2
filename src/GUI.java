import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** GUI.java
 *  04/28/2020
 *
 *  This class creates a JFrame to hold all the game panels together, also dynamically creates
 *  new JPanels for the map and combat areas.
 *
 *  CardLayout is the main Layout Manager used to switch between the game areas (individual JPanels)
 */

public class GUI {

    private JFrame frame;
    private static CardLayout cardLayout;
    static JPanel cardContainer; // holds all cards (game panels)
    private JPanel titlePanel, introPanel, gameOverPanel, endPanel;
    private JPanel[] mapPanels = new JPanel[8];
    private JPanel[] battlePanels = new JPanel[8];

    public GUI() {
        initGUI(); // Initializes the GUI
    }

    private void initGUI() {

        frame = new JFrame("Mountain of Doom");

        createCardContainerPanel();
        createTitlePanel(); // Starting Screen
        // createIntroPanel(); // Not used in this version of the game
        createGameOverPanel();
        createEndPanel(); // Screen displayed when you beat the game
        createMapPanels();
        createBattlePanels();

        // < ******* adding "cards" (JPanels) to cardContainer, which used the CardLayout manager
        cardContainer.add(titlePanel, "title"); // CardLayout.add(Container parent, String name)
        for (int i = 0; i < 8; i++) { // creating JPanels for all maps and battle areas
            cardContainer.add(mapPanels[i], "map_" + i);
            cardContainer.add(battlePanels[i], "battle_" + i);
        }
        cardContainer.add(endPanel, "the end");
        cardContainer.add(gameOverPanel, "game over");
        // ************************************************************* >

        frame.add(cardContainer);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playMusic(MountainOfDoom.PATH + "battle_music.wav"); // Background music
    }

    public void showGUI() {
        frame.setVisible(true);
    }

    // creates panel to hold JPanel cards
    public void createCardContainerPanel() {

        cardContainer = createImagePanel("bg_border.png");
        cardContainer.setLayout(cardLayout = new CardLayout());
        cardContainer.setPreferredSize(new Dimension(648, 648));
        cardContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        cardContainer.setOpaque(false);
    }

    public void createTitlePanel() {

        titlePanel = createImagePanel("bg_title.png");
        // Mouse listener to advance to next JPanel when user clicks anywhere on the panel
        titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.next(cardContainer);
            }
        });
        titlePanel.setPreferredSize(new Dimension(600, 600));
        titlePanel.setOpaque(false);
    }

    // Not used in this version of the game
    public void createIntroPanel() {}

    public void createGameOverPanel() {

        gameOverPanel = createImagePanel("bg_gameover.png");
        gameOverPanel.setLayout(new BorderLayout());
        gameOverPanel.setPreferredSize(new Dimension(600, 600));
        gameOverPanel.setOpaque(false);

        JPanel fillerPanelTop = new JPanel();
        fillerPanelTop.setPreferredSize(new Dimension(600, 350));
        fillerPanelTop.setOpaque(false);

        JPanel fillerPanelMiddle = new JPanel();
        fillerPanelMiddle.setPreferredSize(new Dimension(600, 45));
        fillerPanelMiddle.setOpaque(false);

        JPanel fillerPanelBottom = new JPanel();
        fillerPanelBottom.setPreferredSize(new Dimension(600, 195));
        fillerPanelBottom.setOpaque(false);

        JButton quitButton = new JButton("QUIT");
        quitButton.setPreferredSize(new Dimension(110, 45));
        quitButton.setForeground(Color.white);
        quitButton.setContentAreaFilled(false);
        quitButton.setForeground(Color.white);
        quitButton.addActionListener(event -> System.exit(0));

        JButton playAgainButton = new JButton("PLAY AGAIN");
        playAgainButton.setPreferredSize(new Dimension(110, 45));
        playAgainButton.setContentAreaFilled(false);
        playAgainButton.setForeground(Color.white);

        fillerPanelMiddle.add(quitButton);
        fillerPanelMiddle.add(playAgainButton);

        gameOverPanel.add(fillerPanelTop, BorderLayout.NORTH);
        gameOverPanel.add(fillerPanelMiddle, BorderLayout.CENTER);
        gameOverPanel.add(fillerPanelBottom, BorderLayout.SOUTH);
    }

    public void createEndPanel() {

        endPanel = createImagePanel("bg_end.png");
        endPanel.setLayout(new BorderLayout());
        endPanel.setPreferredSize(new Dimension(600, 600));
        endPanel.setBackground(Color.BLACK);

        endPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
    }

    public void createMapPanels() {

        String imageFile;

        for (int i = 0; i < 8; i++) {
            imageFile = "map_" + i + ".png";
            mapPanels[i] = createImagePanel(imageFile);
            mapPanels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.next(cardContainer);
                }
            });
            mapPanels[i].setPreferredSize(new Dimension(600, 600));
            mapPanels[i].setOpaque(false);
        }
    }

    public void createBattlePanels() {
        for (int i = 0; i < 8; i++) {
            battlePanels[i] = new BattlePanel(i);
        }
    }

    // advances current JPanel to next JPanel inside CardLayout container
    public static void changePanels() {
        cardLayout.next(cardContainer);
    }

    public static void showGameOverScreen() {
        cardLayout.show(cardContainer, "game over");
    }

    // creates JPanels with custom background images
    public static JPanel createImagePanel(String imageName) {

        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage image = null;

                try {
                    image = ImageIO.read(new File(MountainOfDoom.PATH + imageName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g.drawImage(image, 0, 0, null);
            }
        };
    }

    // plays background music
    public void playMusic(String filename) {

        try {
            Clip attackSound = AudioSystem.getClip();
            attackSound.open(AudioSystem.getAudioInputStream(new File(filename)));
            attackSound.start();
            attackSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error. Did you add correct path?");
        }
    }
}