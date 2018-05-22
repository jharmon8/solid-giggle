package graphics;

import entity.Entity;
import entity.Player;
import entity.enemy.BasicEnemy;
import entity.projectile.Projectile;
import util.GameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PewPanel extends JPanel implements KeyListener, ActionListener {

    /*
     * There are two vector spaces in play here.
     * The graphics space is in units of pixels, and is defined by the screen size (and sWidth/sHeight) below
     * The game space is always the same size, defined by gameWidth, below
     *
     * To make the two play nice, wrapper methods for all graphics are defined in util.GraphicsWrappers
     *
     * EVERY draw command from a gameplay element should be done through GraphicsWrappers.
     */
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public int sHeight = screenSize.height;
    public int sWidth = screenSize.width;
    public int sMargin = 0;
    private boolean simulateProjectorAspectRatio = true;

    private Timer timer;

    private Subpanel currentSubpanel;
    private boolean currentSubpanelFinished = false; // when this is true, we'll swap to a new panel on the next tick

    public PewPanel () {
        if(simulateProjectorAspectRatio) {
            sWidth = screenSize.height * 4 / 3;
            sMargin = (screenSize.width - sWidth) / 2;
        }

        addKeyListener(this);

        if(!PewPewDriver.FULLSCREEN) {
            setPreferredSize(new Dimension(sWidth, sHeight));
            sMargin = 0;
        }

/*
        currentSubpanel = new GameSubpanel(sWidth, sHeight, this);
*/
        currentSubpanel = new MenuSubpanel(sWidth, sHeight, this);
        currentSubpanelFinished = false;

        setFocusable(true);
        requestFocus();

        repaint();

        timer = new Timer(40, this);
        timer.setInitialDelay(1000);
        timer.start();
    }

    public void declareSubpanelFinished() {
        currentSubpanelFinished = true;
    }

    private void swapSubpanel(Subpanel nextSubpanel) {
        // this weird temp swap is to prevent concurrency issues
        // probably unneccessary, but I don't really wanna think about it right now
        Subpanel prevSubpanel = currentSubpanel;
        currentSubpanelFinished = false;

        currentSubpanel = nextSubpanel;

        prevSubpanel.close();
    }

    @Override
    public void paintComponent(Graphics g) {
        // margins
        g.setColor(Color.gray);
        g.fillRect(0, 0, screenSize.width, screenSize.height);

        // black background
        g.setColor(Color.black);
        g.fillRect(0 + sMargin, 0, sWidth, sHeight);

        // from now on, (0,0) is the middle of the screen
        // and we'll use graphics wrappers
        g.translate(sWidth/2 + sMargin, sHeight/2);

        currentSubpanel.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(currentSubpanelFinished) {
            swapSubpanel(new GameSubpanel(sWidth, sHeight, this));
        } else {
            currentSubpanel.actionPerformed(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
/*
        System.out.println("PRESS: " + e.getKeyChar());
*/
        currentSubpanel.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
/*
        System.out.println("RELEASE: " + e.getKeyChar());
*/
        currentSubpanel.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
/*
        System.out.println("TYPE: " + e.getKeyChar());
*/
    }
}
