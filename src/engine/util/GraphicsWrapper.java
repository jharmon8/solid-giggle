package engine.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Line2D;

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
    private Graphics2D g2;

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
        this.g2 = (Graphics2D) g;
    }

    public int getsWidth() {
        return gameWidth;
    }

    public int getsHeight() {
        return gameHeight;
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
        double leftTheta = centerTheta - 0.75 * Math.PI;
        double rightTheta = centerTheta + 0.75 * Math.PI;

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
        Image img = AssetLoader.loadAndPrepareImage(filename, width, rWidth, height, rHeight);

        // Changing this to a for loop that will try a maximum number of times
        // TODO these need to all be loaded into a hash map and cached
        boolean loaded = false;
/*
        for(int i = 0; i < 1000000; i++) {
            if(Toolkit.getDefaultToolkit().prepareImage(img, (int)(width * rWidth), (int)(height * rHeight), null)) {
                loaded = true;
                break;
            }
        }

        if(!loaded) {
            System.err.println(filename + " not found");
            System.exit(1);
        }
*/

        g.drawImage(img, (int)(x * rWidth), (int)(y * rHeight), (int)(width * rWidth), (int)(height * rHeight), null);
    }

    public void drawText(String message, double x, double y, double size) {
        // Sadly there's no way to scale fonts perfectly... I think
        g.setFont(new Font("Verdana", Font.BOLD, (int)(size * rWidth)));
        g.drawString(message, (int)(x * rWidth), (int)(y * rHeight));
    }

    public void drawText(String message, double x, double y, double size, boolean bold) {
        // Sadly there's no way to scale fonts perfectly... I think
        g.setFont(new Font("Verdana", bold ? Font.BOLD : Font.PLAIN, (int)(size * rWidth)));
        g.drawString(message, (int)(x * rWidth), (int)(y * rHeight));
    }

    public void drawLine(double x1, double y1, double x2, double y2, double thickness) {
        // again, an imperfect scale :(
        g2.setStroke(new BasicStroke((int)(thickness * rWidth)));
        g2.draw(new Line2D.Float((int)(x1 * rWidth), (int)(y1 * rHeight), (int)(x2 * rWidth), (int)(y2 * rHeight)));
    }

    public void drawPolygon(double centerX, double centerY, double radius, int faces, double thetaStart) {
        int[] xPoints = new int[faces];
        int[] yPoints = new int[faces];

        double theta = thetaStart;
        double dTheta = 6.28 / faces;

        for(int i = 0; i < faces; i++) {
            // find current point
            double[] currentPoint = getRadialPoint(centerX, centerY, radius, theta);

            // put point in arrays
            xPoints[i] = (int)(currentPoint[0] * rWidth);
            yPoints[i] = (int)(currentPoint[1] * rHeight);

            // rotate by one face
            theta += dTheta;
        }

        g.drawPolygon(xPoints, yPoints, faces);
    }

    public void fillPowerup(double centerX, double centerY, double radius, int faces, double thetaStart, Color outside, Color inside) {
        int[] xPointsOutside = new int[faces];
        int[] yPointsOutside = new int[faces];

        int[] xPointsInside = new int[faces];
        int[] yPointsInside = new int[faces];

        double insideRadius = radius * 0.8;
        double theta = thetaStart;
        double dTheta = 6.28 / faces;

        for(int i = 0; i < faces; i++) {
            // find current point
            double[] currentPointOutside = getRadialPoint(centerX, centerY, radius, theta);
            double[] currentPointInside = getRadialPoint(centerX, centerY, insideRadius, theta);

            // put point in arrays
            xPointsOutside[i] = (int)(currentPointOutside[0] * rWidth);
            yPointsOutside[i] = (int)(currentPointOutside[1] * rHeight);

            // put point in arrays
            xPointsInside[i] = (int)(currentPointInside[0] * rWidth);
            yPointsInside[i] = (int)(currentPointInside[1] * rHeight);

            // rotate by one face
            theta += dTheta;
        }

        g.setColor(outside);
        g.fillPolygon(xPointsOutside, yPointsOutside, faces);

        g.setColor(inside);
        g.fillPolygon(xPointsInside, yPointsInside, faces);
    }

    // returns {x, y} in game coords
    private double[] getRadialPoint(double centerX, double centerY, double radius, double theta) {
        double[] point = new double[2];

        point[0] = Math.cos(theta) * radius + centerX;
        point[1] = Math.sin(theta) * radius + centerY;

        return point;
    }
}
