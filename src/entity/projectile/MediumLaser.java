package entity.projectile;

import entity.Entity;

import java.awt.Color;

public class MediumLaser extends Projectile {

    public MediumLaser(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = Color.red;
        this.damage = 2;
        this.speed = 40;
    }
}
