package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;

/*
 * Making a game that scales to resolution in this way is a huge pain.
 *
 * This class will draw an element from game space into graphics space
 * ALL gameplay elements (entities, etc) should use these wrappers
 */
public class GraphicsWrapper {
    // screen sizes
    private int sWidth;
    private int sHeight;

    // the size of the game area (in game space)
    private int gameWidth;
    private int gameHeight;

    // a silly, futile attempt to make this a little faster (Yay software renders)
    private double rWidth;
    private double rHeight;

    // the current canvas
    private Graphics g;

    public GraphicsWrapper(int sWidth, int sHeight, int gameWidth, int gameHeight) {
        this.sWidth = sWidth;
        this.sHeight = sHeight;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        this.rWidth = (double) sWidth / gameWidth;
        this.rHeight = (double) sHeight / gameHeight;
    }

    public void setGraphics(Graphics g) {
        this.g = g;
    }

    public void setColor(Color color) {
        g.setColor(color);
    }

    public void fillCircle(double x, double y, double size) {
        g.fillOval((int)(x * rWidth), (int)(y * rHeight), (int)(size * rWidth), (int)(size * rHeight));
    }

    public void drawCircle(double x, double y, double size) {
        g.drawOval((int)(x * rWidth), (int)(y * rHeight), (int)(size * rWidth), (int)(size * rHeight));
    }

    public void fillTriangle(double x, double y, double centerTheta, double radius) {
        double leftTheta = centerTheta - 0.67 * Math.PI;
        double rightTheta = centerTheta + 0.67 * Math.PI;

        int[] xVals = {
                (int)((Math.cos(leftTheta) * radius + x) * rWidth),
                (int)((Math.cos(centerTheta) * radius + x) * rWidth),
                (int)((Math.cos(rightTheta) * radius + x) * rWidth)
        };
        int[] yVals = {
                (int)((Math.sin(leftTheta) * radius + y) * rHeight),
                (int)((Math.sin(centerTheta) * radius + y) * rHeight),
                (int)((Math.sin(rightTheta) * radius + y) * rHeight)
        };

//        Polygon p = new Polygon(xVals, yVals, xVals.length);
        g.fillPolygon(xVals, yVals, xVals.length);
    }

    public void fillRect(double x, double y, double width, double height) {
        g.fillRect((int)(x * rWidth), (int)(y * rHeight), (int)(width * rWidth), (int)(height * rHeight));
    }

    public void drawImage(String filename, double x, double y, double width, double height) {
        Image img = Toolkit.getDefaultToolkit().getImage(filename);
        g.drawImage(img, (int)(x * rWidth), (int)(y * rHeight), (int)(width * rWidth), (int)(height * rHeight), null);
    }
}
