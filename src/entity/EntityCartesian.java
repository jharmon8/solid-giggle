package entity;

import entity.projectile.Projectile;
import util.GameUtils;

import java.awt.Color;
import java.awt.Graphics;

public abstract class EntityCartesian implements Entity {
    protected double x, y;

    // size is the collision radius
    protected double size;

    protected int health;
    protected int maxHealth;

    protected Color color;


    // Interface stuff
    @Override
    public double getX() {
        return x;
    }
    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getTheta() {
        return Math.tan(y / x);
    }
    @Override
    public double getR() {
        return Math.sqrt(x*x + y*y);
    }

    @Override
    public double getSize() {
        return size;
    }
    @Override
    public boolean collides(Projectile p) {
        return size + p.getSize() < GameUtils.distance(x, y, p.getX(), p.getY());
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawOval((int)(x - size/2), (int)(y - size/2), (int) size, (int) size);
    }
}
