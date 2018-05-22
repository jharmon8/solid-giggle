package entity.Boss;

/*
 * This is where the fun begins
 */
public class Kraken extends Boss {
    private int frame;

    public Kraken() {

    }

    @Override
    public void update() {
        frame++;
    }
}
