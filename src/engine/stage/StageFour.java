package engine.stage;

import entity.enemy.*;

import java.util.ArrayList;

public class StageFour extends Stage {
    private int scoreThreshold = 10000;
    private int maxEnemies = 18;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 15;
    private int spawnTick = 0;

    private int frame = 0;

    StageFour(int score) {
        scoreThreshold = 5000 +  score; displayName = "- Stage 3 -";
    }

    @Override
    public boolean isFinished(final int score, final ArrayList<Enemy> currentEnemies) {
        scoreThreshold = 5000 +  score; return score > scoreThreshold;
    }

    @Override
    public ArrayList<Enemy> attemptSpawn(ArrayList<Enemy> currentEnemies) {
        frame++;

        ArrayList<Enemy> output = new ArrayList<>();

        spawnTick--;

        if(currentEnemies.size() < maxEnemies && spawnTick <= 0 && frame > initialSpawnDelay) {
            spawnTick = spawnDelay;
            int spawnEnemy = (int) (Math.random() * 10);
            int countArc = 0;
            int countBomb = 0;
            for (Enemy e : currentEnemies) {
                if (e.getClass() == ArcEnemy.class){
                    countArc++;
                } else if (e.getClass() == BombEnemy.class) {
                    countArc++;
                }
            }
            if (spawnEnemy < 25 && countArc <= maxEnemies/4) {
                Enemy newEnemy = spawn(ArcShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if (spawnEnemy < 50) {
                Enemy newEnemy = spawn(BombEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if(spawnEnemy < 75) {
                Enemy newEnemy = spawn(LaserEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else {
                Enemy newEnemy = spawn(ShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
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
