package entity.projectile;

import entity.Entity;
import engine.util.GameUtils;

import java.awt.Color;
import java.util.ArrayList;

import static engine.util.GameUtils.distance;
import static engine.util.GameUtils.radialLocation;

public class ExploderBullet extends Projectile {

    private int ticksAlive;
    private int bombLife;

    public ExploderBullet(double px, double py, double vx, double vy, Entity parent) {
        super(px, py, vx, vy, parent);

        this.color = new Color(200, 100, 0);
        this.damage = 2;
        this.size = 0.6;
        this.speed = 0.1; // AHA! With graphics wrappers, speeds no longer must scale to the window size
        this.ticksAlive = 0;
        this.bombLife = 50;

        // We don't care about the magnitude of the velocity vector passed in
        // We keep its direction and scale it to speed, defined above
        double velocityMag = GameUtils.distance(vx, vy);
        this.vx = vx / velocityMag * speed;
        this.vy = vy / velocityMag * speed;
    }

    @Override
    public void update() {
        x += vx;
        y += vy;
        ticksAlive++;
    }

    @Override
    public ArrayList<Projectile> attemptExplode(double x, double y, double vx, double vy){
       if (ticksAlive > bombLife) {
           ArrayList<Projectile> projToAdd = new ArrayList<>();
           Projectile p;
           double theta = parent.getTheta();
           double thetaNew;
           int numbProj = 6;
           for (int i = 0; i < numbProj; i++) {
               thetaNew = theta + i*Math.PI*2/numbProj;
               double vxNew = radialLocation(distance(0.0, 0.0, vx, vy), thetaNew).x;
               double vyNew = radialLocation(distance(0.0, 0.0, vx, vy), thetaNew).y;
               if (x > 0){
                   vxNew = -1 * vxNew;
                   vyNew = -1 * vyNew;
               }
               p = new SlowBullet(x, y, vxNew, vyNew, this);

               projToAdd.add(p);
           }
           return projToAdd;
       } else {
           return null;
       }
    }
}
