package entity.projectile;

import entity.Entity;
import engine.util.GameUtils;

import java.awt.Color;

import static engine.util.GameUtils.distance;
import static engine.util.GameUtils.radialLocation;

public class InterlockBullet extends Projectile {
    private int rotationSide;
    private double theta;

    private double startX;
    private double startY;

    private double radius = 1;
    private double thetaSpeed = 0.0075;
    private double radiusSpeed = 0.4;
    private int tick;
    private double accelFactor = 0.05;

    public InterlockBullet(double px, double py, double vx, double vy, Entity parent, int rotationSide, double theta) {
        super(px, py, vx, vy, parent);

        this.color = new Color(200,0,200);
        this.damage = 1;
        this.size = 0.5;

        this.theta = theta;

        this.startX = px;
        this.startY = py;

        this.rotationSide = rotationSide;
        this.tick = 0;
    }

    ///Math.cos(speed)
    @Override
    public void update() {
        tick++;
        if (rotationSide == 1) {
            theta += thetaSpeed;
            GameUtils.Position pos = radialLocation(radius, theta);
            x = pos.x+startX;
            y = pos.y+startY;
            if (tick%40 < 19) {
                radius = radius + (1 + accelFactor*(tick%40 - 9)) * radiusSpeed;
            }
        } else {
            theta -= thetaSpeed;
            GameUtils.Position pos = radialLocation(radius, theta);
            x = pos.x+startX;
            y = pos.y+startY;
            if (tick%40 < 19) {
                radius = radius + (1 + accelFactor*(tick%40 - 9)) * radiusSpeed;
            }
        }
    }
}
