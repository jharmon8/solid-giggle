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
    private Class nextSubpanelClass;

    private int extraDummy = -1;

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

    /*
     * This is a weird system, but it's honestly not that bad
     *
     * The worst part is this extra num field. It's how the lobby communicates player numbers,
     *   and how the game communicates score to game over or victory
     *
     * Everyone else should pass -1
     *
     * (or nothing works now, too, cuz overloading)
     */
    public void declareSubpanelFinished(Class nextSubpanelClass) {
        declareSubpanelFinished(nextSubpanelClass, -1);
    }

    public void declareSubpanelFinished(Class nextSubpanelClass, int extraNum) {
        this.extraDummy = extraNum;
        currentSubpanelFinished = true;
        this.nextSubpanelClass = nextSubpanelClass;
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
            // here's the big gross logic block for our state machine... ew

            if(nextSubpanelClass.equals(LobbySubpanel.class)) {
                swapSubpanel(new LobbySubpanel(sWidth, sHeight, this));
            } else
            if(nextSubpanelClass.equals(GameSubpanel.class)) {
                // pass in the number of players to this one
                swapSubpanel(new GameSubpanel(sWidth, sHeight, this, extraDummy == -1 ? 3 : extraDummy));
            } else
            if(nextSubpanelClass.equals(LoseSubpanel.class)) {
                swapSubpanel(new LoseSubpanel(sWidth, sHeight, this));
            } else
            if(nextSubpanelClass.equals(WinSubpanel.class)) {
                swapSubpanel(new WinSubpanel(sWidth, sHeight, this));
            } else
            if(nextSubpanelClass.equals(MenuSubpanel.class)) {
                swapSubpanel(new MenuSubpanel(sWidth, sHeight, this));
            } else {
                swapSubpanel(new ErrorSubpanel());
            }



        } else {
            currentSubpanel.actionPerformed(e);
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Close window on escape
        // Maybe take this out before the real game
        if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        currentSubpanel.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentSubpanel.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
