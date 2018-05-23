package engine.stage;

import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;
import entity.enemy.ShootEnemy;

import java.util.ArrayList;

public class StageTwo extends Stage {
    private int scoreThreshold = 2000;
    private int maxEnemies = 10;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int spawnDelay = 20;
    private int spawnTick = 0;

    StageTwo() {
        displayName = "- Stage 2 -";
    }

    @Override
    public boolean isFinished(final int score) {
        return score > scoreThreshold;
    }

    @Override
    public ArrayList<Enemy> attemptSpawn(ArrayList<Enemy> currentEnemies) {
        ArrayList<Enemy> output = new ArrayList<>();

        spawnTick--;

        if(currentEnemies.size() < maxEnemies && spawnTick <= 0) {
            spawnTick = spawnDelay;

            Enemy newEnemy = spawn(ShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
            output.add(newEnemy);
            return output;
        }

        return null;
    }

    @Override
    public Stage getNextStage() {
        return null;
    }
}
