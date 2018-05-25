package entity.Boss;

import engine.util.GameUtils;
import entity.Player;
import entity.projectile.HeavyBullet;
import entity.projectile.LightBullet;
import entity.projectile.MediumBullet;
import entity.projectile.Projectile;

import java.awt.Color;
import java.util.ArrayList;

import static entity.Boss.Minotaur.MinotaurState.*;

public class Minotaur extends Boss {
    enum MinotaurState {
        // In ready, the minotaur picks a move
        READY,

        // In charge rush, the Minotaur charges to random points with a shield
        CHARGE_RUSH,
        CHARGE_RUSH_PAUSE_1,
        CHARGE_RUSH_MOVE_1,
        CHARGE_RUSH_PAUSE_2,
        CHARGE_RUSH_MOVE_2,
        CHARGE_RUSH_PAUSE_3,
        CHARGE_RUSH_MOVE_3,
        CHARGE_RUSH_PAUSE_4,
        CHARGE_RUSH_RETURN,
        CHARGE_RUSH_DEACTIVATE,

        // In heavy shots, Minotaur will fire heavy bullets at players while wandering randomly
        HEAVY_SHOTS,
        HEAVY_SHOTS_RETURN,

        // in quick spiral, Minotaur fires 4 rapid beams of light projectiles while spinning
        QUICK_SPIRAL,

        // There might be a cool dying fade-out
        DYING,
    }

    private MinotaurState state = READY;
    private MinotaurState[] moves = {CHARGE_RUSH, HEAVY_SHOTS, QUICK_SPIRAL};

    private int totalFrame = -1;
    private int pathFrame = -1;
    private GameUtils.Position[] currentPath = null;

    private int pauseTime = 100;
    private int chargeTime = 25;

/*
    private double chargeTargetDistance = 40;
    private double chargeTargetRadius = 10;
*/

    private double chargeTargetDistance = 44;
    private double chargeThetaRange = 1.5;

    private double wanderRadius = 10;
    private int wanderTime = 60;

    private double wanderDuration = 700;
    private int wanderShootDelay = 8;

    private double spiralShootDuration = 700;
    private int spiralShootDelay = 6;

    private int spiralBeams = 5;
    private double spiralTheta = 0;
    private double spiralThetaChange = 0.15;
    private double spiralFlipProbability = 0.01;

    private ArrayList<Projectile> projToFire = new ArrayList<>();

    public Minotaur() {
        this.scoreValue = 10000;

/*
        this.x = 0;
        this.y = 0;

*/
        this.size = 5;
        this.collisionDamage = 3;

        this.color = Color.orange;
        this.highlite = Color.black;

        this.maxHealth = 100;
        this.health = maxHealth;
    }

    @Override
    public ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        if(!projToFire.isEmpty()) {
            ArrayList<Projectile> temp = projToFire;
            projToFire = new ArrayList<Projectile>();
            return temp;
        }

        return null;
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

        // this should be quite the ugly monster of a switch statement
        switch(state) {
            //// ENTRY ////
            case READY:
                // go to a random move
                changeState(moves[(int)(Math.random() * moves.length)]);
                break;

            //// CHARGE RUSH ////
            case CHARGE_RUSH:
                shielded = true;
                changeState(CHARGE_RUSH_PAUSE_1);
                break;
            case CHARGE_RUSH_PAUSE_1:
                wait(CHARGE_RUSH_MOVE_1);
                break;
            case CHARGE_RUSH_MOVE_1:
                charge(CHARGE_RUSH_PAUSE_2);
                break;
            case CHARGE_RUSH_PAUSE_2:
                wait(CHARGE_RUSH_MOVE_2);
                break;
            case CHARGE_RUSH_MOVE_2:
                charge(CHARGE_RUSH_PAUSE_3);
                break;
            case CHARGE_RUSH_PAUSE_3:
                wait(CHARGE_RUSH_MOVE_3);
                break;
            case CHARGE_RUSH_MOVE_3:
                charge(CHARGE_RUSH_PAUSE_4);
                break;
            case CHARGE_RUSH_PAUSE_4:
                wait(CHARGE_RUSH_RETURN);
                break;
            case CHARGE_RUSH_RETURN:
                home(CHARGE_RUSH_DEACTIVATE);
                break;
            case CHARGE_RUSH_DEACTIVATE:
                shielded = false;
                changeState(READY);
                break;

            //// HEAVY SHOTS ////
            case HEAVY_SHOTS:
                wander(HEAVY_SHOTS_RETURN);
                break;
            case HEAVY_SHOTS_RETURN:
                home(READY);
                break;

            //// QUICK SPIRAL ////
            case QUICK_SPIRAL:
                spiral(READY);
                break;


            case DYING:
                break;

/*
            default:
                System.err.println("Minotaur has no state " + state);
                state = READY;
                break;
*/
        }
    }

    // this is used to switch states to avoid bugs
    private void changeState(MinotaurState nextState) {
/*
        System.out.println("From " + state + " to " + nextState);
*/

        totalFrame = -1;
        pathFrame = -1;
        currentPath = null;
        state = nextState;
    }

    // do nothing for some frames
    private void wait(MinotaurState nextState) {
        if(totalFrame == -1) { // if we just got here
            totalFrame = pauseTime;
        } else if (totalFrame == 0) { // if it's the last tick
            changeState(nextState);
        } else {
            totalFrame--;
        }
    }

    // the running part
    private void charge(MinotaurState nextState) {
        pathFrame++;

        if(currentPath == null) { // first tick
            GameUtils.Position start = new GameUtils.Position(x, y);
            GameUtils.Position end = getChargeTarget();

            currentPath = GameUtils.interpolate(start, end, chargeTime);
            direction = GameUtils.flipAngle(Math.atan2(start.y - end.y, start.x - end.x));
        } else if(pathFrame == currentPath.length) { // last tick
            changeState(nextState);
        } else {
            x = currentPath[pathFrame].x;
            y = currentPath[pathFrame].y;
        }
    }

    // Heavy shots
    private void wander(MinotaurState nextState) {
        totalFrame++;
        pathFrame++;

        if(currentPath == null) { // first tick
            GameUtils.Position start = new GameUtils.Position(x, y);
            GameUtils.Position end = getWanderTarget();

            currentPath = GameUtils.interpolate(start, end, wanderTime);
            direction = GameUtils.flipAngle(Math.atan2(start.y - end.y, start.x - end.x));
        } else if(pathFrame == currentPath.length) { // last tick
            currentPath = null;
            pathFrame = -1;
        } else {
            x = currentPath[pathFrame].x;
            y = currentPath[pathFrame].y;
        }

        if(totalFrame % wanderShootDelay == 0) {
            GameUtils.Position vec = GameUtils.randomUnitVector();
            projToFire.add(new HeavyBullet(x, y, vec.x, vec.y, this));
        }

        if(totalFrame > wanderDuration && currentPath == null) {
            changeState(nextState);
        }
    }

    // Heavy shots
    private void spiral(MinotaurState nextState) {
        totalFrame++;

        if(Math.random() < spiralFlipProbability) {
            spiralThetaChange = -spiralThetaChange;
        }

        if(totalFrame % spiralShootDelay == 0) {
            for(int beam = 0; beam < spiralBeams; beam++) {
                GameUtils.Position vec = GameUtils.radialLocation(1, spiralTheta + 6.28 / spiralBeams * beam);
                projToFire.add(new MediumBullet(x, y, vec.x, vec.y, this));
            }

            spiralTheta += spiralThetaChange;
            direction += spiralThetaChange;
        }

        if(totalFrame > spiralShootDuration) {
            changeState(nextState);
        }
    }

    // Bring the minotaur back to 0,0
    private void home(MinotaurState nextState) {
        pathFrame++;

        if(currentPath == null) { // first tick
            GameUtils.Position start = new GameUtils.Position(x, y);
            GameUtils.Position end = new GameUtils.Position(0,0);

            currentPath = GameUtils.interpolate(start, end, chargeTime);
            direction = GameUtils.flipAngle(Math.atan2(start.y - end.y, start.x - end.x));
        } else if(pathFrame == currentPath.length) { // last tick
            changeState(nextState);
        } else {
            x = currentPath[pathFrame].x;
            y = currentPath[pathFrame].y;
        }
    }

    private GameUtils.Position getChargeTarget() {
        if(GameUtils.distance(x, y) < 1) {
            return GameUtils.radialLocation(chargeTargetDistance, Math.random() * 6.28);
        }

        double theta = GameUtils.flipAngle(getTheta()) + (Math.random() - 0.5) * chargeThetaRange;
        return GameUtils.radialLocation(chargeTargetDistance, theta);
    };

    private GameUtils.Position getWanderTarget() {
        return GameUtils.radialLocation(wanderRadius, Math.random() * 6.28);
    };
}
