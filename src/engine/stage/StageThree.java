package engine.stage;

import entity.enemy.*;

import java.util.ArrayList;

public class StageThree extends Stage {
    private int scoreThreshold = 5000;
    private int maxEnemies = 12;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 20;
    private int spawnTick = 0;

    private int frame = 0;

    StageThree(int score) {
        scoreThreshold = 3000 +  score; displayName = "- Stage 3 -";
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
            int spawnEnemy = (int) (Math.random() * 100);
            int countArc = 0;
            for (Enemy e : currentEnemies) {
                if (e.getClass() == ArcEnemy.class){
                    countArc++;
                }
            }
            if (spawnEnemy < 40 && countArc <= maxEnemies/4) {
                Enemy newEnemy = spawn(ArcShootEnemy.class, currentEnemies, spawnRadius, escapeRadius);
                output.add(newEnemy);
            } else if (spawnEnemy < 80) {
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
        return new StageMinotaur();
    }
}
