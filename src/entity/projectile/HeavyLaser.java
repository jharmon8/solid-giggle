package entity.projectile;

import entity.Entity;

import java.awt.Color;

public class HeavyLaser extends Projectile {

    public HeavyLaser(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = Color.red;
        this.damage = 5;
        this.speed = 40;
    }
}
