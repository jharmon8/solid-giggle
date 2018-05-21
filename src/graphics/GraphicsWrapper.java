package graphics;

import java.awt.Color;
import java.awt.Graphics;

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
}
