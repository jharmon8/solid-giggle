package entity;

import entity.powerup.Powerup;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;

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
    public void draw(GraphicsWrapper gw);

    // engine
    public void update();

    // drawing avatars and stuff
    public int getHealth();
    public int getMaxHealth();
}
