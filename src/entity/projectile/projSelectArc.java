package entity.projectile;

import engine.util.GameUtils;
import entity.Entity;
import java.awt.*;
import entity.Entity;
import engine.util.GameUtils;

import java.awt.Color;

import static engine.util.GameUtils.radialLocation;

public class projSelectArc extends Projectile {

    private double radius = 1;
    private double startX;
    private double startY;
    private double theta;

    private double radiusSpeed = 0.225;

    public projSelectArc(double px, double py, double vx, double vy, Entity parent, double theta) {
        super(px, py, vx, vy, parent);

        this.color = new Color(0,200,0);
        this.damage = 1;
        this.size = 0.5;
        this.startX = px;
        this.startY = py;
        this.theta = theta;

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
    }

    @Override
    public void update() {
        GameUtils.Position pos = radialLocation(radius, theta);
        x = pos.x+startX;
        y = pos.y+startY;
        radius+=radiusSpeed;
    }
}