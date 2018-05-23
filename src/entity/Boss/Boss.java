
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

    public Player targetWeakPlayer(ArrayList<Player> players) {
        int minHP = -1;
        int target = -1;
        int loop = -1;
        for (Player p : players) {
            loop++;
            if (p.getHealth() < minHP || minHP < 0) {
                minHP = p.getHealth();
                target = loop;
            }
        }

        return players.get(target);
    }

    public Vector<Integer> canShoot (Vector<Integer> cooldown){
        Vector<Integer> canShoot = new Vector<>();
        for (int v : cooldown) {
            if (v == 0){
                canShoot.add(v);
            }
        }

        if (canShoot.size()  == 0) {
            return null;
        } else {
            return canShoot;
        }
    }

    public int selectShoot (Vector<Integer> availWep){
        if (availWep == null) {
            return -1;
        } else {
            int wepToFire = (int) Math.random() * availWep.size();
            return availWep.get(wepToFire);
        }
    }
}
