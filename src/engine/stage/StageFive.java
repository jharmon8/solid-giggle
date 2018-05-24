package engine.stage;

import entity.enemy.*;

import java.util.ArrayList;

public class StageFive extends Stage {
    private int scoreThreshold = 12500;
    private int maxEnemies = 15;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 30;
    private int spawnTick = 0;

    private int frame = 0;

    StageFive() {
        displayName = "- Stage 3 -";
    }

    @Override
    public boolean isFinished(final int score) {
        return score > scoreThreshold;
    }

    @Override
    public ArrayList<Enemy> attemptSpawn(ArrayList<Enemy> currentEnemies) {
        frame++;

        ArrayList<Enemy> output = new ArrayList<>();

        spawnTick--;

        if(currentEnemies.size() < maxEnemies && spawnTick <= 0 && frame > initialSpawnDelay) {
            spawnTick = spawnDelay;
            int spawnEnemy = (int) (Math.random() * 10);
            if (spawnEnemy <= 1) {
                Enemy newEnemy = spawn(BombEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if (spawnEnemy <= 3) {
                Enemy newEnemy = spawn(LaserEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if (spawnEnemy <= 6) {
                Enemy newEnemy = spawn(ArcShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else {
                Enemy newEnemy = spawn(BasicEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            }
            return output;
        }

        return null;
    }

    @Override
    public Stage getNextStage() {
        return null;
    }
}
