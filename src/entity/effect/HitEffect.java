package entity.effect;

import entity.Player;
import entity.enemy.BasicEnemy;
import graphics.GraphicsWrapper;

/* We need some way to show when an entity takes damage from a bullet */
public class HitEffect {
    //if damaged, do not draw for 2 frames, draw for 2 frames, do not draw for 2 frames
    //need some sort of effect header

    public void drawDamage(Player p) {
        p.countTick = 0;
    }

    public void drawDamage(BasicEnemy e) {
        e.countTick = 0;
    }
}
