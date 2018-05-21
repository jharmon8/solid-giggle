package entity.projectile;

import entity.Entity;

import java.awt.Color;

public class LightLaser extends Projectile {

    public LightLaser(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = Color.red;
        this.damage = 1;
        this.speed = 40;
    }
}
