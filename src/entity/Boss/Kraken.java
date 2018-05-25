
package entity.Boss;

import engine.util.GameUtils;
import entity.Player;
import entity.projectile.*;
import engine.util.GraphicsWrapper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

import static engine.util.GameUtils.distance;
import static engine.util.GameUtils.flipAngle;

public class Kraken extends Boss {
    private int frame;
    private int damageTick;

    private double escapeRadius;
    private double spawnRadius;
    private double speed;
    private double direction;
    private double vx;
    private double vy;

    private Color highlite;

    private int[] maxCooldown = {0,0,0,0};
    private int[] attackLength = {0,0,0,0};
    private int[] currentCooldown = {0,0,0,0};

    private int numbAttack = 4;
    private int wepToFire;

    private double initialThetaRange = Math.PI * 2;
    private double[] lastPlayerPx;
    private double[] lastPlayerPy;

    private double attackChance;
    private boolean isPointed = false;

    private double safeThetaCenter  = (2*Math.PI*Math.random());

    private double safeThetaIncrement = (Math.PI/4*(Math.random()-0.5));

    public Kraken(int escapeRadius, int spawnRadius) {
        this.x = 0;
        this.y = 0;

        this.size = 3.5;
        this.speed = 0.08;
        this.collisionDamage = 3;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 450;
        this.health = maxHealth;
        this.spawnRadius = spawnRadius;
        this.escapeRadius = escapeRadius;

        this.wepToFire = -1;

        this.attackChance = 0.1;

        maxCooldown[0] = 350; //trackLaser
        maxCooldown[1] = 1000; //laser vomit <- remove
        maxCooldown[2] = 800; //interlock arc
        maxCooldown[3] = 5; // wave arc

        attackLength[0] = 400;
        attackLength[1] = 800;
        attackLength[2] = 200;
        attackLength[3] = -1;

        currentCooldown[0] = -1 * maxCooldown[0];
        currentCooldown[1] = -1 * maxCooldown[1];
        currentCooldown[2] = -1 * maxCooldown[2];
        currentCooldown[3] = -1 * maxCooldown[3];

        damageTick = 8;

        direction = computeTrajectory(x,y);
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;

    }

    @Override
    public void update() {
        frame++;
        damageTick++;
        wepToFire = -1;

        if(health <= 0) {
            dead = true;
        }

        int doMove = 0;
        for (int i = 0; i < numbAttack; i++) {
            if (currentCooldown[i] > 0) {
                currentCooldown[i] = currentCooldown[i] - 1;
                if (i == 1) {
                    doMove = 1;
                    isPointed = distance(x, y) > distance(x+vx, y+vy);
                }

                if(i == 0 || i == 1){
                    wepToFire = i;
                }
            } else if (Math.abs(currentCooldown[i]) < maxCooldown[i]){
                currentCooldown[i] = currentCooldown[i] - 1;
            }
        }

        if (doMove == 0) {
            if ((int) getR() == spawnRadius) {
                direction = flipAngle(direction);
                vx = -vx;
                vy = -vy;
            } else if (distance(this.x, this.y) <= spawnRadius*0.01){
                double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
                double thetaCenter = Math.atan2(-y, -x);
                direction = thetaCenter + thetaOffset;
                vx = Math.cos(direction) * speed;
                vy = Math.sin(direction) * speed;
            }
        } else {
            if (distance(this.x, this.y) > spawnRadius*0.01 && !isPointed) {
                direction = flipAngle(direction);
                vx = -vx;
                vy = -vy;
                currentCooldown[doMove] = attackLength[doMove];
            } else if (distance(this.x, this.y) <= spawnRadius*0.01) {
                vx = 0;
                vy = 0;
            }
        }

        x += vx;
        y += vy;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        if (Math.ceil(damageTick / 3) == 0 || Math.ceil(damageTick / 3) == 2) {
            gw.setColor(color.darker().darker());
            gw.fillCircle(getX() - size, getY() - size, size * 2);

            if (!Double.isNaN(direction)) {
                gw.setColor(highlite.darker().darker());
                gw.fillTriangle(x, y, direction, size);
            }
        }
        else {
            gw.setColor(color);
            gw.fillCircle(getX() - size, getY() - size, size * 2);

            if (!Double.isNaN(direction)) {
                gw.setColor(highlite);
                gw.fillTriangle(x, y, direction, size);
            }
        }
    }

    public ArrayList<Projectile> laserTrack (ArrayList<Player> players) {
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int deathTime = 50;
        int padding = 50;

        if (currentCooldown[2] >= (attackLength[2] - (attackLength[2] - deathTime - padding))) {
            lastPlayerPx = new double[players.size()];
            lastPlayerPy = new double[players.size()];
            for (int i = 0; i < players.size(); i ++) {
                Projectile p = new TrackingLaser(x, y, players.get(i).getX(), players.get(i).getY(), this);
                lastPlayerPx[i] = players.get(i).getX();
                lastPlayerPy[i] = players.get(i).getY();
                projToAdd.add(p);
            }
        } else if (currentCooldown[2] >= deathTime && currentCooldown[2] < (attackLength[2] - (attackLength[2] - deathTime - padding))) {
            for (int i = 0; i < lastPlayerPx.length; i++) {
                Projectile p = new TrackingLaser(x, y, lastPlayerPx[i], lastPlayerPy[i], this);
                projToAdd.add(p);
            }
        } else if (currentCooldown[2] < deathTime && currentCooldown[2] > 0) {
            for (int i = 0; i < lastPlayerPx.length; i++) {
                Projectile p = new DamageLaser(x, y, lastPlayerPx[i], lastPlayerPy[i], this);
                projToAdd.add(p);
            }
        }
        //System.out.println(projToAdd.size());
        return projToAdd;
    }

    public ArrayList<Projectile> interArc (ArrayList<Player> players) {
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int numberAroundCircumference = 6;
        double randomComponent = (2*Math.PI*Math.random());

        if (currentCooldown[0] % 100 == 0 && currentCooldown[0] > 0){
            for (int i = 0; i < numberAroundCircumference; i++) {
                double theta = i * 2 * Math.PI / numberAroundCircumference + randomComponent;
                double spawnX = x + size * Math.cos(theta);
                double spawnY = y + size * Math.sin(theta);
                Projectile l = new InterlockBullet(spawnX, spawnY, 0, 0, this, i % 2, theta);
                Projectile r = new InterlockBullet(spawnX, spawnY, 0, 0, this, (i + 1) % 2, theta);
                projToAdd.add(l);
                projToAdd.add(r);
            }
        }

        return projToAdd;
    }

    public ArrayList<Projectile> selectArc (){ // select two random enemies to shoot
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int numberAroundCircumference = 90;

        if (currentCooldown[1] % 10 == 0 && currentCooldown[1] > 0 && currentCooldown[1] < attackLength[1]){
            double changeDirect = Math.random();
            if (changeDirect <= 0.15) {
                safeThetaIncrement = (Math.PI/8*(Math.random()-0.5));
            }
            safeThetaCenter = safeThetaCenter + safeThetaIncrement;
            double safeThetaFactor = Math.PI/6;
            int flag = 0;
            if (safeThetaCenter < safeThetaFactor) {
                safeThetaCenter += 2*Math.PI;
                flag = 1;
            } else if (safeThetaCenter + safeThetaFactor > 2*Math.PI) {
                safeThetaCenter -= 2*Math.PI;
                flag = 2;
            }
            for (int i = 0; i < numberAroundCircumference; i++) {
                double bossTheta = this.getTheta();

                double theta = i * 2 * Math.PI / numberAroundCircumference + bossTheta;
                theta = theta % (2*Math.PI); // always positive 0 to 2pi
                if (theta < 0) {
                    theta += 2*Math.PI;
                }
                double minAngle = (safeThetaCenter-safeThetaFactor);
                double maxAngle = (safeThetaCenter+safeThetaFactor);
                if(flag == 1){
                    double minAngleAdj = maxAngle - (2*Math.PI);
                    double maxAngleAdj = minAngle;
                    if (theta > minAngleAdj && theta < maxAngleAdj) {
                        double spawnX = x + size * Math.cos(theta);
                        double spawnY = y + size * Math.sin(theta);
                        Projectile p = new projSelectArc(spawnX, spawnY, 0, 0, this, theta);
                        projToAdd.add(p);
                    }
                } else if (flag == 2) {
                    double minAngleAdj = maxAngle;
                    double maxAngleAdj = minAngle + (2*Math.PI);
                    if (theta > minAngleAdj && theta < maxAngleAdj) {
                        double spawnX = x + size * Math.cos(theta);
                        double spawnY = y + size * Math.sin(theta);
                        Projectile p = new projSelectArc(spawnX, spawnY, 0, 0, this, theta);
                        projToAdd.add(p);
                    }
                } else {
                    if (theta < minAngle || theta > maxAngle) {
                        double spawnX = x + size * Math.cos(theta);
                        double spawnY = y + size * Math.sin(theta);
                        Projectile p = new projSelectArc(spawnX, spawnY, 0, 0, this, theta);
                        projToAdd.add(p);
                    }
                }
            }
        }

        return projToAdd;
    }

    public ArrayList<Projectile> simpleShot (ArrayList<Player> players){ // select two random enemies to shoot
        /*Player weakPlayer = targetWeakPlayer(players);
        Projectile p = new SlowBullet(x, y, weakPlayer.getX() - x, weakPlayer.getY() - y,this);
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        projToAdd.add(p);
        currentCooldown[3] = currentCooldown[3] - 1;
        return projToAdd;
        */
        currentCooldown[3] = currentCooldown[3] - 1;
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int numbShot = 1 + (int) (Math.random() * 1);
        for(int i = 0; i < numbShot; i++) {
            GameUtils.Position vec = GameUtils.randomUnitVector();
            projToAdd.add(new KrakenBullet(x, y, vec.x, vec.y, this));
        }
        return projToAdd;
    }

    @Override
    public ArrayList<Projectile> attemptShoot (ArrayList<Player> players){
        ArrayList<Projectile> projAdded = new ArrayList<>();
        ArrayList<Projectile> projAddedTotal = new ArrayList<>();
        maxCooldown[3] = 20;

        projAdded.clear();
        if (wepToFire < 0 && (1-Math.random()) < attackChance ) {
            int[] canShootArray = canShoot(currentCooldown, maxCooldown);

            if (canShootArray != null) {
                wepToFire = selectShoot(canShootArray);
            }
        }
        if (wepToFire == 0){ // overlapping arc
             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }

             projAdded = interArc(players);
             projAddedTotal.addAll(projAdded);
        } else if (wepToFire == 1){ //selective arc
             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
            projAdded = selectArc();
            projAddedTotal.addAll(projAdded);
        } else {
            maxCooldown[3] = 10;
        }

        //weapons that may be fired in overlap
        if (frame % maxCooldown[3] == 0) { //constantfiring
             projAdded = simpleShot(players);
             projAddedTotal.addAll(projAdded);
        }

        if (currentCooldown[2] > 0 || currentCooldown[2] == -1 * maxCooldown[2]) { //lasertracking

            if (currentCooldown[2] == -1* maxCooldown[2]) {
                currentCooldown[2] = attackLength[2];
            }
            projAdded = laserTrack(players);
            projAddedTotal.addAll(projAdded);
        }

        //insert shield that may randomly occur. tie shield to power attack!

        return projAddedTotal;
    }

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        damageTick = 0;
    }

    @Override
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }
}
