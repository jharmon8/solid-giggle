package engine.stage;

import engine.util.GameUtils;
import entity.Boss.Kraken;
import entity.enemy.ArcEnemy;
import entity.enemy.BasicEnemy;
import entity.enemy.BombEnemy;
import entity.enemy.Enemy;
import entity.enemy.LaserEnemy;
import entity.enemy.ShootEnemy;

import java.util.ArrayList;

public abstract class Stage {
    protected String displayName;

    public abstract boolean isFinished(final int score);

    public abstract ArrayList<Enemy> attemptSpawn(final ArrayList<Enemy> currentEnemies);

    public String getDisplayName() {
        return displayName;
    }

    // if this returns null, the next stage will be the victory screen
    public abstract Stage getNextStage();

    // the ubiquitous helper method that creates an enemy of a given class in the circle
    protected Enemy spawn(Class enemyClass, final ArrayList<Enemy> enemies, final int spawnRadius, final int escapeRadius) {
        // at a random angle
        double theta = Math.random() * 2 * Math.PI;
        // pick a random point from 10% spawn radius to 100% spawn radius
        double radius = (Math.random() * 0.9 + 0.1) * spawnRadius;

        GameUtils.Position p = GameUtils.radialLocation(radius, theta);

        Enemy newEnemy;
        if (enemyClass == BasicEnemy.class) {
            newEnemy = new BasicEnemy(p.x, p.y, escapeRadius);
        } else if (enemyClass == ShootEnemy.class) {
            newEnemy = new ShootEnemy(p.x, p.y, escapeRadius);
        } else if (enemyClass == LaserEnemy.class) {
            newEnemy = new LaserEnemy(p.x, p.y, escapeRadius);
        } else if (enemyClass == BombEnemy.class) {
            newEnemy = new BombEnemy(p.x, p.y, escapeRadius);
        } else if (enemyClass == Kraken.class) {
            newEnemy = new Kraken(escapeRadius, spawnRadius);
        } else {
            int rotateSide = (int)Math.round(Math.random());
            newEnemy = new ArcEnemy(p.x, p.y, escapeRadius, rotateSide);
        }

        return newEnemy;
    }
}
