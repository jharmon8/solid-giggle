
package entity.Boss;

import entity.Player;
import entity.enemy.Enemy;
import entity.projectile.Projectile;

import java.util.ArrayList;
import java.util.Vector;

public abstract class Boss extends Enemy {

    private double initialThetaRange = Math.PI / 2;

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

    public int[] canShoot (int[] cooldown, int[] maxCooldown){
        int totalShoot = 0;
        //System.arraycopy(cooldown, 0, canShoot, 0, cooldown.length);

        for (int i = 0; i < cooldown.length; i++) {
            if (cooldown[i] < 0 && cooldown[i] <= -1*maxCooldown[i]) {
                totalShoot += 1;
            }
        }

        int[] canShoot = new int[totalShoot];
        int index = 0;

        for (int i = 0; i < cooldown.length; i++) {
            if (cooldown[i] < 0 && cooldown[i] <= -1*maxCooldown[i]) {
                canShoot[index] = i;
                index ++;
            }
        }

        return canShoot;
    }

    public int selectShoot (int[] availWep){

        int toFire = (int) (Math.random() * availWep.length);
        return availWep[toFire];

    }

    @Override
    public boolean shouldPruneOffScreen() {
        return false;
    }
}
