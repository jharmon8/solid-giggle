
package entity.Boss;

import engine.util.GraphicsWrapper;
import entity.Player;
import entity.enemy.Enemy;
import entity.projectile.Projectile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

public abstract class Boss extends Enemy {

    private double initialThetaRange = Math.PI / 2;

    protected boolean shielded = false;

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
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }

    @Override
    public boolean shouldPruneOffScreen() {
        return false;
    }

    @Override
    public void onCollide(Player p) {
        p.takeDamage(collisionDamage);
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        fadeInTick++;

        Color circleColor = color;
        Color triangleColor = highlite;

        if (damageTick == 0 || damageTick == 1 || damageTick == 4 || damageTick == 5) {
            circleColor = circleColor.darker().darker();
            triangleColor = triangleColor.darker().darker();
        }

        if(fadeInTick < maxFadeIn) {
            color = setAlpha(color, (int)(255 * (double) fadeInTick / maxFadeIn));
            highlite = setAlpha(highlite, (int)(255 * (double) fadeInTick / maxFadeIn));
        }

        gw.setColor(color);
        gw.fillCircle(getX() - size, getY() - size, size * 2);

        gw.setColor(highlite);
        gw.fillTriangle(x, y, direction, size);

        if(shielded) {
            gw.setColor(new Color(140,150,240,85));
            gw.fillCircle(x - size * 1.1, y - size * 1.1, size * 2.2);
        }
    }

    @Override
    public void takeDamage(int dmg) {
        if(!shielded) {
            health -= dmg;
            damageTick = 0;
            countTick = 0;
        }
    }

    @Override
    public double bossHealth() {
        return health / (double) maxHealth;
    }
}
