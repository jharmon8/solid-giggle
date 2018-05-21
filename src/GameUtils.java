public class GameUtils {
    public static Position radialLocation(double radius, double radians) {
        double x = Math.cos(radians) * radius;
        double y = Math.sin(radians) * radius;

        return new Position(x, y);
    }

    public static BulletVector bulletVector(double radius, double radians, double speed) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double px = cos * radius;
        double py = sin * radius;

        double vx = -cos * speed;
        double vy = -sin * speed;

        return new BulletVector(px, py, vx, vy);
    }

    public static class Position {
        double x, y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class BulletVector {
        double px, py, vx, vy;

        public BulletVector(double px, double py, double vx, double vy) {
            this.px = px;
            this.py = py;
            this.vx = vx;
            this.vy = vy;
        }
    }
}
