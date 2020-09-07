/** Enemy.java
 *  04/28/2020
 *
 *  Subclass of Character, used to create a Enemy objects
 */

public class Enemy extends Character {

    String stillImage;
    String attackingImage;
    String deadImage;

    public Enemy(String name, int attackPower, int health, int maxHealth) {
        super(name, attackPower, health, maxHealth);
    }

    public void setStillImage(String stillImage) {
        this.stillImage = stillImage;
    }

    public void setAttackingImage(String attackingImage) {
        this.attackingImage = attackingImage;
    }

    public void setDeadImage(String deadImage) {
        this.deadImage = deadImage;
    }

    public String getStillImage() {
        return stillImage;
    }

    public String getAttackingImage() {
        return attackingImage;
    }

    public String getDeadImage() {
        return deadImage;
    }

    public void damageReceived(int damage) {
        damage = getHealth() - damage;
        setHealth(damage);
    }

    public static Enemy[] getEnemies() {

        Enemy[] enemies = new Enemy[8];

        enemies[0] = new Enemy("Goblin Scout", 15, 80, 80);
        enemies[1] = new Enemy("Fallen Knight", 15, 90, 90);
        enemies[2] = new Enemy("Skeleton Warrior", 20, 100, 100);
        enemies[3] = new Enemy("Okral, the Bonecaster", 23, 110, 110);
        enemies[4] = new Enemy("Mountain Troll", 25, 120, 120);
        enemies[5] = new Enemy("Spirit of the Mountain", 27, 125, 125);
        enemies[6] = new Enemy("Garuk, the Tall", 28, 130, 130);
        enemies[7] = new Enemy("Baalzebul, Prince of Demons", 30, 150, 150);

        for (int i = 0; i < 8; i++) {
            setEnemyImage(enemies, i);
        }

        return enemies;
    }

    public static void setEnemyImage(Enemy[] enemies, int index) {
        enemies[index].setStillImage("enemy_" + index + "_still.png");
        enemies[index].setAttackingImage("enemy_" + index + "_attacking.png");
        enemies[index].setDeadImage("enemy_" + index + "_dead.png");
    }
}