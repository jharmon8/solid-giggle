package entity.enemy;

import entity.EntityCartesian;
import entity.Player;

public abstract class Enemy extends EntityCartesian {
    protected int scoreValue;
    protected boolean dead = false;
    protected int collisionDamage;

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean collides(Player p) {
        return collides(p.getX(), p.getY(), p.getSize());
    }

    public boolean inPlayfield(int escapeRadius) {
        if(getR() > escapeRadius * 1.1) {
            return false;
        }

        return true;
    }

    protected boolean collides(double x, double y, double targetSize) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < (size+targetSize) * 0.9;
    }

    public void onCollide(Player p) {
        dead = true;

        p.takeDamage(collisionDamage);
    }

    public boolean isDead() {
        return dead;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }
}