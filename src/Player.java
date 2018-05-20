import java.awt.*;

public class Player {
    private double radians;
    private double radius;
    private double size;

    private Color color;

    Player(double radians, double size, Color c, double railRadius) {
        this.radians = radians;
        this.radius = railRadius;
        this.size = size;

        this.color = c;
    }

    public void move(boolean clockwise) {
        if(clockwise) {
            this.radians -= 0.1;
        } else {
            this.radians += 0.1;
        }
    }

    public boolean collides(double x, double y) {
        GameUtils.Position p = GameUtils.radialLocation(radius, radians);

        double dx = p.x - x;
        double dy = p.y - y;

        return Math.sqrt(dx * dx + dy * dy) < size;
    }

    public void draw(Graphics g) {
        GameUtils.Position p = GameUtils.radialLocation(radius, radians);

        g.setColor(color);
        g.fillOval((int)(p.x - size/2), (int)(p.y - size/2), (int)size, (int)size);
    }
}
