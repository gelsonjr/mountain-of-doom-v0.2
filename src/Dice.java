import java.util.Random;

/** Dice.java
 *  04/28/2020
 *
 *  Used to create random numbers
 */

public class Dice {
    private final Random randomGenerator;

    public Dice() {
        randomGenerator = new Random();
    }

    public int roll8() {
        return randomGenerator.nextInt(8) + 1; // 1-8
    }

    /* // ----- Not used in this version of the game -----
     public int roll3() {
         return randomGenerator.nextInt(3) + 1; // 1-3
     }

        public int roll20() {
        return randomGenerator.nextInt(21); // 0-20
    }*/
}