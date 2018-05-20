import java.awt.*;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double size;
    protected Color c;

    Entity(double x, double y, double size, Color c) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.c = c;
    }

    Entity(GameUtils.Position p, double size, Color c) {
        this.x = p.x;
        this.y = p.y;
        this.size = size;
        this.c = c;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean collides(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;

        return Math.sqrt(dx * dx + dy * dy) < size;
    }

    public void draw(Graphics g, int margin) {
        g.setColor(c);
        g.fillOval((int) (x + margin), (int) y, (int) size, (int) size);
    }
}
