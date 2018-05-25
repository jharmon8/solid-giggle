package entity.powerup;

import engine.util.GameUtils;
import entity.projectile.LightLaser;
import entity.projectile.Projectile;
import entity.projectile.SlowBullet;
import entity.projectile.Splitshot;

import java.awt.*;

public class SplitshotPowerup extends Powerup {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range

    public SplitshotPowerup(double px, double py, double vx, double vy) {
        super(px, py, vx, vy);

//        this.color = new Color(0, 200, 0, 140);
        this.color = new Color(138,43,226);

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = vx / velocityMag * speed;
        this.vy = vy / velocityMag * speed;

        this.timeToLive = 600;
    }

    @Override
    public Class getAmmoType() {
        if(active) {
            return Splitshot.class;
        }

        return null;
    }
}
