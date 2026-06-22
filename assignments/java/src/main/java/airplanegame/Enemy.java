package airplanegame;

public class Enemy extends Thread {
    private int x;
    private int y;
    private final int speed;
    private volatile boolean alive = true;
    private final GamePanel panel;

    public Enemy(int x, int y, GamePanel panel) {
        this.x = x;
        this.y = y;
        this.speed = 3;
        this.panel = panel;
    }

    @Override
    public void run() {
        while (alive && panel.isPlaying()) {
            y += speed;

            if (y > 600) {
                alive = false;
            }

            panel.checkCollisions();
            panel.repaint();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
        }

        panel.removeEnemy(this);
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
