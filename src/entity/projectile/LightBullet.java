package entity.projectile;

import entity.Entity;
import util.GameUtils;

import java.awt.Color;

public class LightBullet extends Projectile {

    public LightBullet(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = Color.white;
        this.damage = 1;
        this.size = 8;
        this.speed = 10; // fuck, speeds have to be scaled to window size...

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = vx / velocityMag * speed;
        this.vy = vy / velocityMag * speed;
    }
}
