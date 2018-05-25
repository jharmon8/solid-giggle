package entity.enemy;

import engine.util.GraphicsWrapper;
import entity.Entity;
import entity.EntityCartesian;
import entity.Player;
import entity.powerup.LaserPowerup;
import entity.powerup.Powerup;
import entity.powerup.RegenPowerup;
import entity.powerup.SplitshotPowerup;
import entity.projectile.Projectile;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class Enemy extends EntityCartesian {
    protected int scoreValue;
    protected boolean dead = false;
    protected int collisionDamage;

    protected double powerupChance;

    // just used for graphics
    protected double direction;
    protected Color highlite;

    protected int damageTick;
    protected int countTick;

    protected int fadeInTick = 0;
    protected int maxFadeIn = 16;

    // The types is self explanator
    protected Class[] powerupTypes = {RegenPowerup.class, LaserPowerup.class, SplitshotPowerup.class};
    // The chances is the chance (out of 1) that each of the types is spawned, respectively
    // Only one can spawn at a time (unless you override dropPowerup())
    // These should spawn to less than one
    protected Double[] powerupChances = {0.05, 0.02, 0.02};

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
        damageTick = 0;
        countTick = 0;
    }

    public ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        return null;
    }

    public ArrayList<Powerup> dropPowerup () {
        ArrayList<Powerup> output = new ArrayList<>();

        double roll = Math.random();
        double total = 0;

        for(int i = 0; i < powerupTypes.length; i++) {
            total += powerupChances[i];
            if(roll < total) {
                output.add(createPowerup(powerupTypes[i]));
                break;
            }
        }

        return output;
    }

    protected Powerup createPowerup(Class powerupType) {
        Powerup output = null;

        try {
            Constructor constructor = powerupType.getConstructor(new Class[]{Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE});
            output = (Powerup) constructor.newInstance(getX(), getY(), (Math.random() * 2) - 1, (Math.random() * 2) - 1);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Enemy cannot drop powerup of type " + powerupType);
            e.printStackTrace();
        }

        return output;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        fadeInTick++;

        Color circleColor = color;
        Color triangleColor = highlite;

        if (damageTick == 0 || damageTick == 1 || damageTick == 4 || damageTick == 5) {
            circleColor = circleColor.darker().darker();
            triangleColor = triangleColor.darker().darker();
        }

        if(fadeInTick < maxFadeIn) {
            color = setAlpha(color, (int)(255 * (double) fadeInTick / maxFadeIn));
            highlite = setAlpha(highlite, (int)(255 * (double) fadeInTick / maxFadeIn));
        }

        gw.setColor(color);
        gw.fillCircle(getX() - size, getY() - size, size * 2);

        gw.setColor(highlite);
        gw.fillTriangle(x, y, direction, size);
    }

    protected Color setAlpha(Color c, int alpha) {
        Color output = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);

        return output;
    }

    public boolean shouldPruneOffScreen() {
        return true;
    }

    // number from 0 to 1 indicating how much health to draw
    // -1 means this is not a boss and does not need a health bar
    public double bossHealth() {
        return -1;
    }
}
