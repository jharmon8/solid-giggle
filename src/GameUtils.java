public class GameUtils {
    public static Position radialLocation(double radius, double radians) {
        double x = Math.cos(radians) * radius;
        double y = Math.sin(radians) * radius;

        return new Position(x, y);
    }

    public static class Position {
        double x, y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}
