package engine.stage;

import entity.enemy.*;

import java.util.ArrayList;

public class StageFour extends Stage {
    private int scoreThreshold = 10000;
    private int maxEnemies = 12;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 15;
    private int spawnTick = 0;

    private int frame = 0;

    StageFour(int score) {
        scoreThreshold = 5000 +  score; displayName = "- Stage 4 -";
    }

    @Override
    public boolean isFinished(final int score, final ArrayList<Enemy> currentEnemies) {
        scoreThreshold = 2000 +  score; return score > scoreThreshold;
    }

    @Override
    public ArrayList<Enemy> attemptSpawn(ArrayList<Enemy> currentEnemies) {
        frame++;

        ArrayList<Enemy> output = new ArrayList<>();

        spawnTick--;

        if(currentEnemies.size() < maxEnemies && spawnTick <= 0 && frame > initialSpawnDelay) {
            spawnTick = spawnDelay;
            int spawnEnemy = (int) (Math.random() * 100);
            int countArc = 0;
            int countBomb = 0;
            for (Enemy e : currentEnemies) {
                if (e.getClass() == ArcShootEnemy.class){
                    countArc++;
                } else if (e.getClass() == BombEnemy.class) {
                    countBomb++;
                }
            }
            if (spawnEnemy < 10 && countBomb < maxEnemies / 6) {
                Enemy newEnemy = spawn(BombEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if (spawnEnemy < 50 && countArc <= maxEnemies/4) {
                Enemy newEnemy = spawn(ArcShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if(spawnEnemy < 85) {
                Enemy newEnemy = spawn(ShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else {
                Enemy newEnemy = spawn(LaserEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            }
            return output;
        }

        return null;
    }

    @Override
    public Stage getNextStage(int score) {
        return new StageKraken();
    }
}
