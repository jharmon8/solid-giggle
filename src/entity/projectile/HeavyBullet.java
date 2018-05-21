package entity.projectile;

import entity.Entity;

import java.awt.Color;

public class HeavyBullet extends Projectile {

    public HeavyBullet(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = Color.white;
        this.damage = 5;
        this.speed = 20;
    }
}
