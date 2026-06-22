package airplanegame;

public class EnemySpawner extends Thread {
    private final GamePanel panel;

    public EnemySpawner(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (panel.isPlaying()) {
            int x = (int)(Math.random() * (panel.getWidth() - 20));
            panel.addEnemy(new Enemy(x, 0, panel));

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                interrupt();
                break;
            }
        }
    }
}
