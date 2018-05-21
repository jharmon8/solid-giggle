package entity;

import entity.projectile.Projectile;
import graphics.GraphicsWrapper;
import util.GameUtils;

import java.awt.Color;
import java.awt.Graphics;

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
}
