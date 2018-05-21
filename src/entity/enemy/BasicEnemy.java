package entity.enemy;

import entity.EntityCartesian;
import engine.GraphicsWrapper;

import java.awt.Color;

public class BasicEnemy extends EntityCartesian {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;
    private boolean dead = false;

    private double speed;

    // just used for graphics
    private double direction;

    private Color highlite;

    private int escapeRadius;

    public BasicEnemy(double x, double y, int escapeRadius) {
        this.x = x;
        this.y = y;

        this.size = 1.5;
        this.speed = 0.25;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 5;
        this.health = maxHealth;

        // calculate trajectory
        double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
        double thetaCenter = Math.atan2(-y, -x);
        direction = thetaCenter + thetaOffset;

        if(Double.isNaN(direction)) {
            System.err.println("NaN angle in enemy constructor!");
        }

        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;

        this.escapeRadius = escapeRadius;
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

        x += vx;
        y += vy;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        gw.setColor(color);
        gw.fillCircle(getX() - size, getY() - size, size * 2);

        if(!Double.isNaN(direction)) {
            gw.setColor(highlite);
            gw.fillTriangle(x, y, direction, size);
        }
    }
}
