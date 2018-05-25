package engine.stage;

import entity.enemy.ArcEnemy;
import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;
import entity.enemy.ShootEnemy;

import java.util.ArrayList;

public class StageTwo extends Stage {
    private int scoreThreshold;
    private int maxEnemies = 10;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 30;

    private int spawnDelay = 30;
    private int spawnTick = 0;

    private int frame = 0;

    StageTwo(int score) {
        scoreThreshold = 2000 +  score; displayName = "- Stage 2 -";
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
            if (spawnEnemy < 33 && countArc <= maxEnemies/3) {
                Enemy newEnemy = spawn(ArcEnemy.class, currentEnemies, spawnRadius, escapeRadius);
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
        return new StageMinotaur();
    }
}
