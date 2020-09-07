/** Character.java
 *  04/28/2020
 *
 *  Abstract class to create Character object
 */

public abstract class Character {

    Dice dice = new Dice();

    private String name;
    private int attackPower;
    private int health;
    private int maxHealth;

    public Character(String name, int attackPower, int health, int maxHealth) {
        this.name = name;
        this.attackPower = attackPower;
        this.health = health;
        this.maxHealth = maxHealth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int attack() {
        return attackPower + dice.roll8();
    }
}