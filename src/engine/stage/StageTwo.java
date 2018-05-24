package engine.stage;

import entity.enemy.ArcEnemy;
import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;

import java.util.ArrayList;

public class StageTwo extends Stage {
    private int scoreThreshold = 2500;
    private int maxEnemies = 10;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 30;
    private int spawnTick = 0;

    private int frame = 0;

    StageTwo() {
        displayName = "- Stage 2 -";
    }

    @Override
    public boolean isFinished(final int score, final ArrayList<Enemy> currentEnemies) {
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
            if (spawnEnemy <= 4) {
                Enemy newEnemy = spawn(BasicEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else {
                Enemy newEnemy = spawn(ArcEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            }
            return output;
        }

        return null;
    }

    @Override
    public Stage getNextStage() {
        return new StageThree();
    }
}
