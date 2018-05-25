package entity.projectile;

import engine.util.GraphicsWrapper;
import entity.Entity;
import engine.util.GameUtils;

import java.awt.Color;

import static engine.util.GameUtils.radialLocation;

public class Splitshot extends Projectile {
    private double thetaStart;
    private double thetaCurr;

    private double radius = 1;
    private double thetaSpeed = 0.001;
    private double radiusSpeed = 0.1;

    private double startX;
    private double startY;

    private double topX;
    private double topY;
    private double botX;
    private double botY;

    public Splitshot(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = new Color(138,43,226);
        this.damage = 1;
        this.size = 0.5;
        this.speed = 0.1; // AHA! With graphics wrappers, speeds no longer must scale to the window size
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = 0;
        this.vy = 0;
        this.thetaStart = parent.getTheta();

        this.startX = px;
        this.startY = py;
    }
}
