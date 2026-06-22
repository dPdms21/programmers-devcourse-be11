package airplanegame;

public class Bullet extends Thread {
    private final GamePanel panel;

    private int x;
    private int y;
    private volatile boolean alive = true;

    public Bullet(int x, int y, GamePanel panel) {
        this.x = x;
        this.y = y;
        this.panel = panel;
    }

    @Override
    public void run() {
        while (alive && panel.isPlaying()) {
            y -= 10;

            if (y < 0) {
                alive = false;
            }

            panel.checkCollisions();
            panel.repaint();

            try {
                Thread.sleep(30);
            }
            catch (InterruptedException e) {
                interrupt();
                break;
            }
        }

        panel.removeBullet(this);
    }

    public void stopMoving() {
        alive = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
