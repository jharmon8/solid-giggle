package engine.stage;

import entity.Boss.Kraken;
import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;
import entity.enemy.ShootEnemy;

import java.util.ArrayList;

public class StageOne extends Stage {
    private int scoreThreshold;
    private int maxEnemies = 8;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 30;
    private int spawnTick = 0;

    private int frame = 0;

    StageOne(int score) {
        scoreThreshold = 2000 +  score; displayName = "- Stage 1 -";
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
            Enemy newEnemy = spawn(BasicEnemy.class, currentEnemies, spawnRadius, escapeRadius);
            output.add(newEnemy);
            return output;
        }
        return null;
    }

    @Override
    public Stage getNextStage(int score) {
        return new StageTwo(score);
    }
}
