package engine.stage;

import entity.Boss.Minotaur;
import entity.Player;
import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;

import java.util.ArrayList;

public class StageMinotaur extends Stage {
    private int spawnRadius = 16;
    private int escapeRadius = 48;

    private int initialSpawnDelay = 150;

    private int frame = 0;
    private boolean spawned = false;

    StageMinotaur() {
        displayName = "- Minotaur -";
    }

    public int computeScore (int score) {
        int bossScore = 2000;
        return score + bossScore;
    }
    @Override
    public boolean isFinished(final int score, final ArrayList<Enemy> currentEnemies) {
        return spawned && currentEnemies.isEmpty();
    }

    @Override
    public ArrayList<Enemy> attemptSpawn(ArrayList<Enemy> currentEnemies) {
        if(!currentEnemies.isEmpty()) {
            return null;
        }

        frame++;

        ArrayList<Enemy> output = new ArrayList<>();

        if(frame > initialSpawnDelay && !spawned) {
            output.add(new Minotaur());
            spawned = true;
            return output;
        }

        return null;
    }

    @Override
    public Stage getNextStage(int score) {
        //score += computeScore(score);
        return new StageFour(score);
    }
}
