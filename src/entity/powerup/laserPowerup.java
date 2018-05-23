package entity.powerup;

import engine.util.GameUtils;

import java.awt.*;

public class laserPowerup extends Powerup {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range

    public laserPowerup(double px, double py, double vx, double vy) {
        super(px, py, vx, vy);

        this.color = new Color(0, 200, 0, 45);
        this.size = 0.5;
        this.speed = 0.1;

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = vx / velocityMag * speed;
        this.vy = vy / velocityMag * speed;
    }
}