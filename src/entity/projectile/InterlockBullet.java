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
    private double thetaSpeed = 0.025;
    private double radiusSpeed = 0.1;

    public InterlockBullet(double px, double py, double vx, double vy, Entity parent, int rotationSide, double theta) {
        super(px, py, vx, vy, parent);

        this.color = new Color(200,0,200);
        this.damage = 1;
        this.size = 0.5;

        this.theta = theta;

        this.startX = px;
        this.startY = py;

        this.rotationSide = rotationSide;
    }

    ///Math.cos(speed)
    @Override
    public void update() {
        if (rotationSide == 1) {
            theta += thetaSpeed;
            GameUtils.Position pos = radialLocation(radius, theta);
            x = pos.x+startX;
            y = pos.y+startY;
            radius+=radiusSpeed;
        } else {
            theta -= thetaSpeed;
            GameUtils.Position pos = radialLocation(radius, theta);
            x = pos.x+startX;
            y = pos.y+startY;
            radius+=radiusSpeed;
        }
    }

/*
    public void updateVel() {
        if (rotationSide == 0) {
            thetaCenter = Math.atan2(-y, -x);
            //thetaCenter = 0;
            direction = thetaCenter + Math.PI / 2 + arcFactor;
        } else {
            thetaCenter = Math.atan2(y, x);
            //thetaCenter = 0;
            direction = thetaCenter + Math.PI / 2 - arcFactor;
        }

        double radialScale = 0.33 + Math.pow(getR() / escapeRadius, 2);
        vx = Math.cos(direction) * (speed*radialScale);
        vy = Math.sin(direction) * (speed*radialScale);
    }
    */
}
