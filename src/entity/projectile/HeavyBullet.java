package entity.projectile;

import entity.Entity;
import util.GameUtils;

import java.awt.Color;

public class HeavyBullet extends Projectile {

    public HeavyBullet(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = new Color(200,200,200);
        this.damage = 5;
        this.size = 0.75;
        this.speed = 1; // AHA! With graphics wrappers, speeds no longer must scale to the window size

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = vx / velocityMag * speed;
        this.vy = vy / velocityMag * speed;
    }
}
