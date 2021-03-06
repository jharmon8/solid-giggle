package entity;

import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;
import engine.util.GameUtils;

import java.awt.Color;

public abstract class EntityPolar implements Entity {
    protected double theta;
    protected double radius;

    // size is the collision radius
    protected double size;

    protected int health;
    protected int maxHealth;

    protected Color color;


    // Interface stuff
    @Override
    public double getX() {
        return Math.cos(theta) * radius;
    }
    @Override
    public double getY() {
        return Math.sin(theta) * radius;
    }

    @Override
    public double getTheta() {
        return theta;
    }
    @Override
    public double getR() {
        return radius;
    }

    @Override
    public double getSize() {
        return size;
    }
    @Override
    public boolean collides(Projectile p) {
        return size + p.getSize() < GameUtils.distance(getX(), getY(), p.getX(), p.getY());
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        gw.setColor(color);
        gw.fillCircle(getX() - size, getY() - size, size * 2);
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }
}
