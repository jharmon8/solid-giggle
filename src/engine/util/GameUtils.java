package engine.util;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

/*
 * this class is a collection of random bullshit
 */
public class GameUtils {
    public static DecimalFormat debugger = new DecimalFormat("00.000");

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

        @Override
        public String toString() {
            return "(" + debugger.format(x) + ", " + debugger.format(y) + ")";
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

    /*
     * Config util stuff
     */
    // if this is true, keyboard control mapping will be used
    // otherwise, joypad mapping will be used
    public static final boolean KEYBOARD_CONTROLS = true;

    // These are the default controls for each player
    // I guess right now the order is shoot, swap, left, right
    private static int[][] defaultControls = {
            {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R}, // player 1
            {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F}, // player 2
            {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V}, // etc
            {KeyEvent.VK_T, KeyEvent.VK_Y, KeyEvent.VK_U, KeyEvent.VK_I},
            {KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_K},
            {KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_M, KeyEvent.VK_COMMA},
    };

    // Here's the controls that map to the gaming controllers
    private static int[][] joypadControls = {
            {KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_7, KeyEvent.VK_8}, // player 1
            {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_X}, // player 2
            {KeyEvent.VK_4, KeyEvent.VK_3, KeyEvent.VK_5, KeyEvent.VK_6}, // etc
            {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_C, KeyEvent.VK_F},
            {KeyEvent.VK_2, KeyEvent.VK_1, KeyEvent.VK_Z, KeyEvent.VK_V},
            {KeyEvent.VK_T, KeyEvent.VK_Y, KeyEvent.VK_I, KeyEvent.VK_U},
    };

    public static int[][] getControls() {
        return KEYBOARD_CONTROLS ? defaultControls : joypadControls;
    }

    public static Color[] playerColors = {
            Color.red,      // player 1
            Color.green,    // player 2
            Color.blue,     // etc
            Color.yellow,
            Color.pink,
            Color.orange,
    };

    // Great for bosses! Some cubics or something would be cool, too
    public static Position[] interpolate(Position start, Position end, int frames) {
/*
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
*/

        Position[] output = new Position[frames];

        double currentX = start.x;
        double currentY = start.y;

        double xRate = (end.x - start.x) / frames;
        double yRate = (end.y - start.y) / frames;

        for(int i = 0; i < frames - 1; i++) {
            currentX += xRate;
            currentY += yRate;

            output[i] = new Position(currentX, currentY);
        }

        // manually set last position to avoid rounding error
        output[frames - 1] = new Position(end.x, end.y);

        return output;
    }

    public static Position randomUnitVector() {
        double theta = Math.random() * 6.28;
        double radius = 1;

        return radialLocation(radius, theta);
    }
}

//878787qw8de7xq8rwde78qxrd7we8qxd7ewd8xqr7ex8wdqrex7d8wqedrx78qwdxe7rq8weq7r8wqe7qr8we7q8wreq78wreq7w8erqwe87qrewqr87eqr8w7er8qwe7r8q7878xdxdxdxdwqw