
package entity.projectile;

import engine.util.GameUtils;
import engine.util.GraphicsWrapper;
import entity.Entity;
import entity.Player;
import entity.enemy.Enemy;

import java.awt.Color;

public class DamageLaser extends Projectile {

    private double thick;
    public double tx, ty;

    private int lifetime = 2;

    public DamageLaser(double tx, double ty, double px, double py, Entity parent)  {
        super(px, py, tx, ty, parent);

        this.color = Color.red;
        this.damage = 9999999;
        this.size = 0.5;
        this.speed = 2; // AHA! With graphics wrappers, speeds no longer must scale to the window size
        this.thick = this.size;

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        this.vx = 0;
        this.vy = 0;
        this.x = px;
        this.y = py;

        this.tx = tx;
        this.ty = ty;

    }

    @Override
    public void update() {
        this.lifetime--;
        if (this.lifetime == 0) {
            this.dead = true;
        }
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        gw.setColor(color);
        gw.drawLine(x, y, tx, ty, this.thick);
    }

    @Override
    public void onCollide(Enemy e) {    }

    @Override
    public void onCollide(Player p) {
        // prevents us from hitting p on every tick
        p.takeDamage(damage);
    }
}
