package entity.Boss;

import java.awt.Color;

public class Minotaur extends Boss {

    public Minotaur() {
        this.scoreValue = 1000;

        this.x = 0;
        this.y = 0;

        this.size = 5;
        this.collisionDamage = 3;

        this.color = Color.orange;
        this.highlite = Color.black;

        this.maxHealth = 1000;
        this.health = maxHealth;
    }

    @Override
    public void update() {

    }
}
