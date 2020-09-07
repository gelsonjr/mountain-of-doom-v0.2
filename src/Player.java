/** Player.java
 *  04/28/2020
 *
 *  Subclass of Character, used to create a Player object
 */

public class Player extends Character {

    public Player(String name, int attackPower, int health, int maxHealth) {
        super(name, attackPower, health, maxHealth);
    }

    public int specialAttack() {
        return attack() + 25;
    }

    public void drinkPotion() {
        setHealth(getMaxHealth());
    }

    public void damageReceived(int damage) {
        setHealth(getHealth() - damage);
    }
}