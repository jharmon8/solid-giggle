package entity.projectile;

import entity.Entity;
import entity.EntityCartesian;
import entity.Player;
import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;
import graphics.GraphicsWrapper;
import util.GameUtils;

import java.awt.*;
import java.util.ArrayList;

public abstract class Projectile extends EntityCartesian {
    // position and velocity
    // note that many bullet types will automatically scale the velocity vector to speed
    public double vx, vy;
    protected double speed;
    protected Color color;

    // The thing that fired this bullet
    public Entity parent;
    public ArrayList<Entity> ignoreList = new ArrayList<>();
    public boolean dead = false;

    int damage;

    public Projectile(double x, double y, double vx, double vy, Entity parent) {
        this.x = x;
        this.y = y;

        this.vx = vx;
        this.vy = vy;

        this.parent = parent;
        this.ignoreList.add(parent);
    }

    @Override
    public void update() {
        x += vx;
        y += vy;
    }

    public void onCollide(Player p) {
        // prevents us from hitting p on every tick
        ignoreList.add(p);

        p.takeDamage(damage);
    }

    public void onCollide(Enemy e) {
        // prevents us from hitting p on every tick
        ignoreList.add(e);
        dead = true;

        e.takeDamage(damage);
    }

    public boolean onScreen(int gameWidth, int gameHeight) {
        if(Math.abs(x) > gameWidth/2 || Math.abs(y) > gameHeight/2) {
            return false;
        }

        return true;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        gw.setColor(color);
        gw.fillCircle(x - size, y - size, size * 2);
    }

    public ArrayList<Projectile> attemptExplode(double x, double y, double vx, double vy){
        return null;
    }
}
