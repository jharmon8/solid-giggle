package entity.powerup;

import engine.util.GraphicsWrapper;
import entity.Entity;
import entity.EntityCartesian;
import entity.Player;

import java.awt.*;
import java.util.ArrayList;

public abstract class Powerup extends EntityCartesian {
    //reaping
    //powerup vector
    //assign

    public double vx, vy;
    protected double speed;
    protected Color color;

    protected int frame;
    protected int timeToLive;

    // The thing that fired this bullet
    public Entity parent;
    public ArrayList<Entity> ignoreList = new ArrayList<>();
    public boolean dead = false;

    private double drawTheta = 0;

    protected boolean active;

    public Powerup(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;

        this.vx = vx;
        this.vy = vy;

        this.ignoreList.add(parent);

        this.dead = false;

        this.size = 1;
        this.speed = 0.2;
    }

    @Override
    public void update() {
        x += vx;
        y += vy;

        drawTheta += 0.05;
    }

    public void playerUpdate() {
        if(active) {
            frame++;
        }
    }

    public boolean collides(Player p) {
        return collides(p.getX(), p.getY(), p.getSize());
    }

    protected boolean collides(double x, double y, double targetSize) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < (size+targetSize) * 0.9;
    }

    public void onCollide(Player p) {
        // Why does powerup have an ignore list???
        ignoreList.add(p);
        this.dead = true;

        if(!isRevive()) {
            p.givePowerup(this);
        }
    }

    public boolean onScreen(int gameWidth, int gameHeight) {
        if(Math.abs(x) > gameWidth/2 || Math.abs(y) > gameHeight/2) {
            return false;
        }

        return true;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
//        gw.setColor(getStatusColor());
        gw.fillPowerup(x, y, size, 6, drawTheta, getStatusColor(), Color.gray);
    }

    public boolean isDead() {
        return dead;
    }

    public boolean inPlayfield(int escapeRadius) {
        if(getR() > escapeRadius * 1.1) {
            return false;
        }

        return true;
    }

    public boolean isFinished() {
        return frame > timeToLive;
    }

    // if this powerup changes ammo type, put the new class here
    // null means that this powerup doesn't change ammo type
    public Class getAmmoType() {
        return null;
    }

    public Color getStatusColor() {
        return color;
    }

    public int getHeal() {
        return 0;
    }

    public void activate() {
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    // for drawing on status panels
    public double percentLeft() {
        return (double) (timeToLive - frame) / timeToLive;
    }

    public boolean isRevive() {
        return false;
    }
}
