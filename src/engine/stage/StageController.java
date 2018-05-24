package engine.stage;

import engine.util.AudioManager;
import engine.util.GraphicsWrapper;
import entity.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;

public class StageController {
    private Stage currentStage;

    private int frame = 0;
    private int fadeInFrames = 30;
    private int fadeOutFrames = 80;
    // this should be more than fade in + fade out
    private int lastDisplayFrame = 140;

    private boolean DEBUG_STAGE = true;

    public StageController() {
        if(DEBUG_STAGE) {
            currentStage = new StageMinotaur();
        } else {
            currentStage = new StageKraken();
        }
    }

    // Spawn and stage transition logic goes here, and this is called on every tick
    public ArrayList<Enemy> update(final ArrayList<Enemy> enemies, final int score) {
        if(frame == 0) {
            AudioManager.playSound("res/alarm1.wav", -10f, 2);
        }

        frame ++;

        if(currentStage.isFinished(score, enemies)) {
            currentStage = currentStage.getNextStage();
            frame = 0;
            return null;
        }

        return currentStage.attemptSpawn(enemies);
    }

    // this returns true if the game has been won
    public boolean isVictory() {
        return currentStage == null;
    }

    // If there's a fancy "STAGE ONE" type jumbotron text we wanna do, that goes here
    // also boss health and/or stage progress?
    public void draw(GraphicsWrapper gw, ArrayList<Enemy> enemies) {
        if(frame < lastDisplayFrame && currentStage != null) {
            int numChar = currentStage.getDisplayName().length();

            double w = gw.getsWidth();
            double h = gw.getsHeight();

            int alpha = 255;

            if(frame < fadeInFrames) { // if fading in
                alpha = 255 - (int)(255 * (double)(fadeInFrames - frame)/fadeInFrames);
            } else if(frame > lastDisplayFrame - fadeOutFrames + 2) { // if fading out
                alpha = (int)(255 * (double)(lastDisplayFrame - frame)/fadeOutFrames);
            }

            gw.setColor(new Color(255,255,255, alpha));
            gw.drawText(currentStage.getDisplayName(), -w*numChar*0.025, 0, 10, false);
        }
    }
}
