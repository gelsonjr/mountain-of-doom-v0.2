import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** BattlePanel.java
 *  04/28/2020
 *
 *  This class creates an instance of a JPanel that acts as a Battle Area between Player and Enemy.
 */

public class BattlePanel extends JPanel {

    // instance of player class: Name, atk power, current health, maximum health
    Player player = new Player("Player", 25, 100, 100);

    // using array of enemies from the Enemy class
    static Enemy[] enemy = Enemy.getEnemies();

    // using JProgressbar to display the game health bars
    private JProgressBar playerHealthBar, enemyHealthBar;

    private JTextArea messageUpdates = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane (messageUpdates,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    // Player CardLayout panels
    private CardLayout cardLayout = new CardLayout();
    private JPanel playerCardsContainer; // holds player cards
    private JPanel playerCard1, playerCard2, playerCard3, playerCard4, playerCard5;

    // Enemy CardLayout panels
    private JPanel enemyCardsPanel; // holds enemy cards
    private JPanel enemyCard1, enemyCard2, enemyCard3;

    private int index; // important to determine stage and enemy used in this BattlePanel

    // Delay timers, mainly for attack animations
    Timer playerStillAnimation = new Timer(300, event ->
            cardLayout.show(playerCardsContainer, "player still")
    );

    Timer gameOverTimer = new Timer(2000, event ->
            GUI.showGameOverScreen()
    );

    Timer playerDeathAnimation = new Timer(300, event -> {
        cardLayout.show(playerCardsContainer, "player dead");
        gameOverTimer.setRepeats(false);
        gameOverTimer.start();
    });

    Timer enemyStillAnimation = new Timer(300, event ->
            cardLayout.show(enemyCardsPanel, "enemy still")
    );

    Timer enemyAttackAnimation = new Timer(1000, event -> {
        attackAnimation(enemyCardsPanel, "enemy attacking");
        enemyAttacking();
    });

    Timer enemyDeathAnimation = new Timer(300, event ->
        cardLayout.show(enemyCardsPanel, "enemy dead")
    );

    public BattlePanel(int index) {
        this.index = index;

        // top and bottoms halves of the panel
        TopPanel topPanel = new TopPanel();
        BottomPanel bottomPanel = new BottomPanel();

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(600, 600));
        this.setOpaque(false);
        this.add(topPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    // TopPanel displays the background image for the stage, also the player and enemy battling
    public class TopPanel extends JPanel {

        //     WEST                  CENTER       SOUTH      EAST
        JPanel playerHealthBarPanel, centerPanel, infoPanel, enemyHealthBarPanel;

        public TopPanel() {

            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(600, 300));
            this.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            this.setOpaque(false);

            createPlayerHealthBarPanel();
            createCenterPanel();
            createInfoPanel();
            createEnemyHealthBarPanel();

            this.add(playerHealthBarPanel, BorderLayout.WEST);
            this.add(centerPanel, BorderLayout.CENTER);
            this.add(infoPanel, BorderLayout.SOUTH);
            this.add(enemyHealthBarPanel, BorderLayout.EAST);
        }

        @Override // change background image
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage image = null;

            try {
                image = ImageIO.read(new File(MountainOfDoom.PATH + "bg_" + index + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(image, 0, 0, null);
        }

        // using JProgress as health bar
        public void createPlayerHealthBarPanel() { // BorderLayout.WEST -> player health nar

            playerHealthBarPanel = new JPanel();
            playerHealthBarPanel.setOpaque(false);
            playerHealthBarPanel.setPreferredSize(new Dimension(36, 248));
            playerHealthBarPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

            playerHealthBar = new JProgressBar(SwingConstants.VERTICAL, 0, player.getMaxHealth());
            playerHealthBar.setValue(player.getHealth()); // current bar filled position
            playerHealthBar.setPreferredSize(new Dimension(16, 208));
            playerHealthBar.setBackground(Color.BLACK);
            playerHealthBar.setForeground(Color.YELLOW);
            playerHealthBar.setBorderPainted(false);
            playerHealthBarPanel.add(playerHealthBar);
        }

        // this panel uses OverlayLayout to display multiple JPanels on top of each other
        public void createCenterPanel() { // BorderLayout.CENTER -> battle panels
            centerPanel = new JPanel();
            centerPanel.setLayout(new OverlayLayout(centerPanel));
            centerPanel.setOpaque(false);
            centerPanel.setPreferredSize(new Dimension(528, 248));

            createPlayerCardsContainer();
            createEnemyCardsPanel();

            centerPanel.add(enemyCardsPanel);
            centerPanel.add(playerCardsContainer);
        }

        // Displays player and enemy names
        public void createInfoPanel() { // BorderLayout.SOUTH -> messages & status info

            infoPanel = new JPanel(new BorderLayout());
            infoPanel.setPreferredSize(new Dimension(600, 47));
            infoPanel.setBackground(new Color(255, 255, 255, 150));

            Font font = new Font("Dialog", Font.BOLD, 20);

            JLabel playerInfoLabel = new JLabel("Player");
            playerInfoLabel.setPreferredSize(new Dimension(300, 47));
            playerInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            playerInfoLabel.setFont(font);

            JLabel enemyInfoLabel = new JLabel(enemy[index].getName(), SwingConstants.RIGHT);
            enemyInfoLabel.setPreferredSize(new Dimension(300, 47));
            enemyInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
            enemyInfoLabel.setFont(font);

            infoPanel.add(playerInfoLabel, BorderLayout.WEST);
            infoPanel.add(enemyInfoLabel, BorderLayout.EAST);
        }

        public void createEnemyHealthBarPanel() { // BorderLayout.EAST -> enemy health nar
            enemyHealthBarPanel = new JPanel();
            enemyHealthBarPanel.setOpaque(false);
            enemyHealthBarPanel.setPreferredSize(new Dimension(36, 248));
            enemyHealthBarPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 20));

            enemyHealthBar = new JProgressBar(SwingConstants.VERTICAL, 0, enemy[index].getMaxHealth());

            enemyHealthBar.setValue(enemy[index].getHealth());
            enemyHealthBar.setPreferredSize(new Dimension(16, 208));
            enemyHealthBar.setBackground(Color.BLACK);
            enemyHealthBar.setForeground(Color.YELLOW);
            enemyHealthBar.setBorderPainted(false);
            enemyHealthBarPanel.add(enemyHealthBar);
        }

        // JPanel that used Cardlayout to switch between different attack animations (cards / panels)
        public void createPlayerCardsContainer() {
            playerCardsContainer = new JPanel(cardLayout);
            playerCardsContainer.setOpaque(false);

            // all player and enemy "cards" are .png files with transparent backgrounds
            playerCard1 = GUI.createImagePanel("player_still.png");
            playerCard1.setPreferredSize(new Dimension(528, 248));
            playerCard1.setOpaque(false);

            playerCard2 = GUI.createImagePanel("player_attacking.png");
            playerCard2.setPreferredSize(new Dimension(528, 248));
            playerCard2.setOpaque(false);

            playerCard3 = GUI.createImagePanel("player_dead.png");
            playerCard3.setPreferredSize(new Dimension(528, 248));
            playerCard3.setOpaque(false);

            playerCard4 = GUI.createImagePanel("player_special.png");
            playerCard4.setPreferredSize(new Dimension(528, 248));
            playerCard4.setOpaque(false);

            playerCard5 = GUI.createImagePanel("player_heal.png");
            playerCard5.setPreferredSize(new Dimension(528, 248));
            playerCard5.setOpaque(false);

            playerCardsContainer.add(playerCard1, "player still");
            playerCardsContainer.add(playerCard2, "player attacking");
            playerCardsContainer.add(playerCard3, "player dead");
            playerCardsContainer.add(playerCard4, "player special attack");
            playerCardsContainer.add(playerCard5, "player heal");
        }

        public void createEnemyCardsPanel() {
            enemyCardsPanel = new JPanel(cardLayout);
            enemyCardsPanel.setOpaque(false);

            enemyCard1 = GUI.createImagePanel(enemy[index].getStillImage());
            enemyCard1.setPreferredSize(new Dimension(528, 248));
            enemyCard1.setOpaque(false);

            enemyCard2 = GUI.createImagePanel(enemy[index].getAttackingImage());
            enemyCard2.setPreferredSize(new Dimension(528, 248));
            enemyCard2.setOpaque(false);

            enemyCard3 = GUI.createImagePanel(enemy[index].getDeadImage());
            enemyCard3.setPreferredSize(new Dimension(528, 248));
            enemyCard3.setOpaque(false);

            enemyCardsPanel.add(enemyCard1, "enemy still");
            enemyCardsPanel.add(enemyCard2, "enemy attacking");
            enemyCardsPanel.add(enemyCard3, "enemy dead");
        }
    }

    // Displays actions buttons and message updates screen
    public class BottomPanel extends JPanel {

        JPanel actionsPanel, buttonsPanel, messagePanel, trophiesPanel, nextButtonPanel, hiddenPanel;
        JButton attackButton, specialAttackButton, healButton;

        public BottomPanel() {
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(600, 300));
            this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            this.setOpaque(false);

            createActionsPanel();
            createTrophiesPanel();

            this.add(actionsPanel, BorderLayout.CENTER);
            this.add(trophiesPanel, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage image = null;

            try {
                image = ImageIO.read(new File(MountainOfDoom.PATH + "bg_bottom.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(image, 0, 0, null);
        }

        public void createActionsPanel() {
            actionsPanel = new JPanel();
            actionsPanel.setLayout(new BorderLayout());
            actionsPanel.setPreferredSize(new Dimension(300, 295));
            actionsPanel.setBackground(Color.DARK_GRAY);
            actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

            createButtonsPanel();
            createMessagePanel();
            createNextButtonPanel();

            actionsPanel.add(buttonsPanel, BorderLayout.NORTH);
            actionsPanel.add(messagePanel, BorderLayout.CENTER);
            actionsPanel.add(nextButtonPanel, BorderLayout.SOUTH);

            actionsPanel.setOpaque(false);
        }

        // Not fully used in this version of the game. Needs to add defeated enemy weapons as trophies
        // TODO add image for enemy trophies (weapons)
        public void createTrophiesPanel() {
            trophiesPanel = new JPanel();
            trophiesPanel.setLayout(new BorderLayout());
            trophiesPanel.setPreferredSize(new Dimension(300, 295));
            trophiesPanel.setBackground(new Color(118, 229, 252));
            trophiesPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            trophiesPanel.setOpaque(false);
        }

        // BoxLayout used to align buttons
        public void createButtonsPanel() {
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
            buttonsPanel.setPreferredSize(new Dimension(295, 45));
            buttonsPanel.setOpaque(false);

            attackButton = new JButton("Attack");
            attackButton.setPreferredSize(new Dimension(91, 45));
            attackButton.setMaximumSize(new Dimension(91, 45));
            attackButton.setContentAreaFilled(false);
            attackButton.setForeground(Color.WHITE);
            attackButton.addActionListener(event -> {

                // attack sequence
                playerAttack(); // players turn

                if (enemy[index].isDead()) {
                    sendMessageUpdates(enemy[index].getName() + " is dead.\n");
                    enemyDeathAnimation.setRepeats(false);
                    enemyDeathAnimation.start();
                    hiddenPanel.setVisible(true);
                }
                else if (enemy[index].getHealth() < (enemy[index].getMaxHealth() / 2)) {
                    enemyHealthBar.setForeground(Color.RED);
                    updateHealthBars();
                    enemyAttack();
                }
                else {
                    enemyAttack(); // enemy's turn
                }
            });

            specialAttackButton = new JButton("Special");
            specialAttackButton.setPreferredSize(new Dimension(91, 45));
            specialAttackButton.setMaximumSize(new Dimension(91, 45));
            specialAttackButton.setContentAreaFilled(false);
            specialAttackButton.setForeground(Color.WHITE);
            specialAttackButton.addActionListener(event -> {

                playerSpecialAttack();

                if (enemy[index].getHealth() <= 0) {
                    sendMessageUpdates(enemy[index].getName() + " is dead.\n");
                    enemyDeathAnimation.setRepeats(false);
                    enemyDeathAnimation.start();
                    hiddenPanel.setVisible(true);
                }
                // if enemy's healthbar goes bellow 50% it turns red
                else if (enemy[index].getHealth() < (enemy[index].getMaxHealth() / 2)) {
                    enemyHealthBar.setForeground(Color.RED);
                    updateHealthBars();
                    enemyAttack();
                }
                else {
                    enemyAttack();
                }

            });

            healButton = new JButton("Heal");
            healButton.setPreferredSize(new Dimension(91, 45));
            healButton.setMaximumSize(new Dimension(91, 45));
            healButton.setContentAreaFilled(false);
            healButton.setForeground(Color.WHITE);
            healButton.addActionListener(event -> playerHeal()); // drinks potion to restore life

            buttonsPanel.add(attackButton);
            buttonsPanel.add(Box.createHorizontalGlue()); // To add maximum spacing between buttons
            buttonsPanel.add(specialAttackButton);
            buttonsPanel.add(Box.createHorizontalGlue());
            buttonsPanel.add(healButton);
        }

        // for battle updates
        public void createMessagePanel() {
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new Dimension(295, 175));
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            messagePanel.setOpaque(false);

            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            scrollPane.getViewport().getView().setBackground(new Color(252, 213, 117));
            scrollPane.setOpaque(false);

            messagePanel.add(scrollPane);
        }

        // this JPanel displays a JButton to advance to next stage, once enemy is defeated
        public void createNextButtonPanel() {

            nextButtonPanel = new JPanel(new BorderLayout());
            nextButtonPanel.setPreferredSize(new Dimension(295, 75));
            nextButtonPanel.setOpaque(false);

            hiddenPanel = GUI.createImagePanel("bg_next.png");
            hiddenPanel.setLayout(new BoxLayout(hiddenPanel, BoxLayout.X_AXIS));
            hiddenPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            hiddenPanel.setPreferredSize(new Dimension(295, 75));
            hiddenPanel.setMaximumSize(new Dimension(295, 75));
            hiddenPanel.setOpaque(false);

            JButton nextButton = new JButton("NEXT >>>");
            nextButton.setPreferredSize(new Dimension(120, 45));
            nextButton.setMaximumSize(new Dimension(120, 45));
            nextButton.setContentAreaFilled(false);
            nextButton.setForeground(Color.WHITE);
            nextButton.addActionListener(event -> GUI.changePanels());

            hiddenPanel.add(Box.createHorizontalGlue());
            hiddenPanel.add(nextButton, Component.CENTER_ALIGNMENT);
            hiddenPanel.add(Box.createHorizontalGlue());
            hiddenPanel.setVisible(false); // Important: starts invisible

            nextButtonPanel.add(hiddenPanel);
        }
    }

    // both player and enemy use this method for their attack animations
    public void attackAnimation(JPanel panel, String card) {
        cardLayout.show(panel, card);
    }

    // player attack sequence
    public void playerAttack() {
        playSound(MountainOfDoom.PATH + "attack_sound.wav"); // attack sound
        attackAnimation(playerCardsContainer, "player attacking"); // attack swing animation

        // delay to return to standing stance (using Timers)
        playerStillAnimation.setRepeats(false);
        playerStillAnimation.start();

        enemy[index].damageReceived(player.attack());
        updateHealthBars();
        sendMessageUpdates(
                "Your attack deals " + player.attack() + " to " + enemy[index].getName() + ".\n"
        );
    }

    public void playerSpecialAttack() {

        playSound(MountainOfDoom.PATH + "attack_sound.wav");
        attackAnimation(playerCardsContainer, "player special attack");

        playerStillAnimation.setRepeats(false);
        playerStillAnimation.start();

        enemy[index].damageReceived(player.specialAttack());
        updateHealthBars();
        sendMessageUpdates("Your special attack deals " + player.specialAttack() + " to "
                + enemy[index].getName() + ".\n"
        );
    }

    // restores life
    public void playerHeal() {

        player.drinkPotion();
        cardLayout.show(playerCardsContainer, "player heal");
        playerStillAnimation.setRepeats(false);
        playerStillAnimation.start();
        updateHealthBars();
    }

    public void enemyAttack() {
        enemyAttackAnimation.setRepeats(false);
        enemyAttackAnimation.start();
    }

    // enemy attack sequence
    public void enemyAttacking() {
        playSound(MountainOfDoom.PATH + "attack_sound.wav");

        enemyStillAnimation.setRepeats(false);
        enemyStillAnimation.start();

        player.damageReceived(enemy[index].attack());

        if (player.getHealth() < (player.getMaxHealth() / 2)) {
            playerHealthBar.setForeground(Color.RED);
        }

        updateHealthBars();
        sendMessageUpdates(enemy[index].getName() + " attacks you and deals " +
                enemy[index].attack() + " damage.\n"
        );

        if (player.isDead()) {
            sendMessageUpdates("You are dead. GAME OVER\n");
            playerDeathAnimation.setRepeats(false);
            playerDeathAnimation.start();
        }
    }

    // to "refresh" healthbar display
    public void updateHealthBars() {
        playerHealthBar.setValue(player.getHealth());
        enemyHealthBar.setValue(enemy[index].getHealth());
    }

    public void sendMessageUpdates(String text) {
        messageUpdates.append(text);
    }

    // attack sound
    public void playSound(String filename) {

        try {
            Clip attackSound = AudioSystem.getClip();
            attackSound.open(AudioSystem.getAudioInputStream(new File(filename)));
            attackSound.start();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error. Did you add correct path?");
        }
    }
}