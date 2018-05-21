import java.awt.*;

public class BasicBullet extends Projectile {
    int damage = 0;

    BasicBullet(double px, double py, double vx, double vy, Color color, Shooter parent, int damage) {
        super(px, py, vx, vy, color, parent);

        this.damage = damage;
    }

    @Override
    public void onCollide(Player p) {
        // prevents us from hitting p on every tick
        hasHit.add(p);

        p.doDamage(damage);
    }
}
