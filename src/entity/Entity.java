package entity;

import entity.projectile.Projectile;

import java.awt.Graphics;

public interface Entity {
    // cartesian position
    public double getX();
    public double getY();

    // polar position
    public double getTheta();
    public double getR();

    // collision
    public double getSize();
    public boolean collides(Projectile p);

    // graphics
    public void draw(Graphics g);

    // engine
    public void update();
}
