public class Projectile {
    int px, py, vx, vy;

    Projectile(int px, int py, int vx, int vy) {
        this.px = px;
        this.py = py;
        this.vx = vx;
        this.vy = vy;
    }

    public void update() {
        px += vx;
        py += vy;
    }

    public void collide(/* some entity */) {

    }
}
