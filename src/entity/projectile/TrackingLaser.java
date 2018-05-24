
package entity.projectile;

import engine.util.GameUtils;
import engine.util.GraphicsWrapper;
import entity.Entity;

import java.awt.Color;

public class TrackingLaser extends Projectile {

    private double thick;
    public double tx, ty;

    public TrackingLaser(double px, double py, double tx, double ty, Entity parent)  {
        super(px, py, tx, ty, parent);

        this.color = Color.red;
        this.damage = 0;
        this.size = 0.6;
        this.speed = 2; // AHA! With graphics wrappers, speeds no longer must scale to the window size
        this.thick = 0.5;

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = 0;
        this.vy = 0;

        this.tx = px;
        this.ty = py;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        gw.setColor(color);
        gw.drawLine(x, y, tx, ty, thick);
    }
}
