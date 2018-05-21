package util;

/*
 * this class is a collection of random bullshit
 */
public class GameUtils {
    public static Position radialLocation(double radius, double radians) {
        double x = Math.cos(radians) * radius;
        double y = Math.sin(radians) * radius;

        return new Position(x, y);
    }

    public static BulletVector bulletVector(double radius, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double px = cos * radius;
        double py = sin * radius;

        double vx = -cos;
        double vy = -sin;

        return new BulletVector(px, py, vx, vy);
    }

    public static class Position {
        public double x, y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class BulletVector {
        public double px, py, vx, vy;

        public BulletVector(double px, double py, double vx, double vy) {
            this.px = px;
            this.py = py;
            this.vx = vx;
            this.vy = vy;
        }
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy);
    }

    // this is a magnitude function
    public static double distance(double x1, double y1) {
        return Math.sqrt(x1 * x1 + y1 * y1);
    }

    public static double flipAngle(double theta) {
        return theta + Math.PI;
    }
}
