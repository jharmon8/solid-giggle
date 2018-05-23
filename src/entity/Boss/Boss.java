package entity.Boss;

import entity.Player;
import entity.enemy.Enemy;
import entity.projectile.Projectile;

import java.util.ArrayList;
import java.util.Vector;

public abstract class Boss extends Enemy {

    public int chooseAttack(int numbAttack) {
        return (int)Math.round(Math.random()*numbAttack);
    }

    public double computeTrajectory (double x, double y) {
        double initialThetaRange = Math.PI / 2;
        double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
        double thetaCenter = Math.atan2(-y, -x);
        double direction = thetaCenter + thetaOffset;

        if(Double.isNaN(direction)) {
            System.err.println("NaN angle in enemy constructor!");
        }

        return direction;
    }

    public Player targetPlayer(ArrayList<Player> players) {
        return null;
    }

    public Vector<Integer> canShoot (Vector<Integer> cooldown){
        Vector<Integer> canShoot = new Vector<>();
        for (int v : cooldown) {
            if (v == 0){
                canShoot.add(v);
            }
        }
        return canShoot;
    }

    public int selectShoot (Vector<Integer> availWep){
        int wepToFire = (int) Math.random()*availWep.size();
        return availWep.get(wepToFire);
    }
}
