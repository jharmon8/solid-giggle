package engine.stage;

import entity.enemy.ArcEnemy;
import entity.enemy.ArcShootEnemy;
import entity.enemy.BasicEnemy;
import entity.enemy.BombEnemy;
import entity.enemy.Enemy;
import entity.enemy.LaserEnemy;
import entity.enemy.ShootEnemy;

import java.util.ArrayList;

public class StageDebug extends Stage {
    private int scoreThreshold = 1000000;
    private int maxEnemies = 100;

    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int spawnDelay = 30;
    private int spawnTick = 0;

    private int frame = 0;

    private Class[] enemyTypes = {
            ArcEnemy.class,
            ArcShootEnemy.class,
            BasicEnemy.class,
            BombEnemy.class,
            LaserEnemy.class,
            ShootEnemy.class
    };

    StageDebug() {
        displayName = "- Debug -";
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

            Class type = enemyTypes[(int)(Math.random() * enemyTypes.length)];

            Enemy newEnemy = spawn(type, currentEnemies, spawnRadius, escapeRadius);
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
