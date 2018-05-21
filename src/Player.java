import java.awt.*;

public class Player implements Shooter {
    private double radians;
    private double radius;
    private double size;

    private Color color;

    private int fireDelay = 10;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private int maxHealth = 10;
    private int health = 10;

    private boolean dead = false;

    Player(double radians, double size, Color c, double railRadius) {
        this.radians = radians;
        this.radius = railRadius;
        this.size = size;

        this.color = c;
    }

    public void update() {
        if(health <= 0) {
            dead = true;
        }

        fireDelayTimer--;
    }

    // returns a projectile to be added to the projectiles array
    // null if I can't fire
    public Projectile fire() {
        if(fireDelayTimer < 0) {
            double bulletSpeed = 20;

            GameUtils.BulletVector vec = GameUtils.bulletVector(
                    radius * .95,
                    radians,
                    bulletSpeed
            );

/*
            System.out.println("PX: " + vec.px + ", PY: " + vec.py);
            System.out.println("VX: " + vec.vx + ", VY: " + vec.vy);
*/

            Projectile p = new BasicBullet(
                    vec.px, vec.py,
                    vec.vx, vec.vy,
                    color, this,
                    bulletDamage);
            fireDelayTimer = fireDelay;

            return p;
        }

        return null;
    }

    public void swap() {

    }

    public void move(boolean clockwise) {
        if(clockwise) {
            this.radians += 0.1;
        } else {
            this.radians -= 0.1;
        }
    }

    public boolean collides(Projectile proj) {
        if(proj.parent == this) {
            return false;
        }

        if(proj.hasHit.contains(this)) {
            return false;
        }

        return collides(proj.px, proj.py);
    }

    public boolean collides(double x, double y) {
        GameUtils.Position p = GameUtils.radialLocation(radius, radians);

        double dx = p.x - x;
        double dy = p.y - y;

        // We give a leeway on size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < size * 0.8;
    }

    public void draw(Graphics g) {
        GameUtils.Position p = GameUtils.radialLocation(radius, radians);

        if(dead) {
            g.setColor(Color.white);
        } else {
            g.setColor(color);
        }
        g.fillOval((int)(p.x - size/2), (int)(p.y - size/2), (int)size, (int)size);
    }

    public void doDamage(int dmg) {
        health -= dmg;
    }
}
