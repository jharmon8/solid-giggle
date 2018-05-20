import java.awt.*;

public abstract class Projectile {
    double px, py, vx, vy;
    Color color;

    // The thing that fired this bullet
    Shooter parent;

    int size = 16;

    Projectile(double px, double py, double vx, double vy, Color color, Shooter parent) {
        this.px = px;
        this.py = py;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.parent = parent;
    }

    public void update() {
        px += vx;
        py += vy;
    }

    public void onCollide(Player p) {
        System.exit(0);
    }

    public boolean onScreen(int sWidth, int sHeight) {
        if(Math.abs(px) > sWidth/2 || Math.abs(py) > sHeight / 2) {
            return false;
        }

        return true;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int)px - size/2, (int)py - size/2, size, size);
    }
}
