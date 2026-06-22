package airplanegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final Player player;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();

    private volatile boolean playing = true;

    private int score = 0;
    private int life = 3;

    public GamePanel() {
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.BLACK);

        player = new Player(175, 500, 50, 50);

        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!playing) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT)  {
                    player.moveLeft();
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    player.moveRight(400);
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    addBullet(new Bullet(player.getX() + player.getWidth() / 2, player.getY(), GamePanel.this));
                }

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        g.setColor(Color.YELLOW);

        synchronized (bullets) {
            for (Bullet b : bullets) {
                g.fillRect(b.getX(), b.getY(), 5,12);
            }
        }

        g.setColor(Color.RED);

        synchronized (enemies) {
            for (Enemy e : enemies) {
                g.fillRect(e.getX(), e.getY(), 20, 20
                );
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("SCORE: " + score, 10, 20);
        g.drawString("LIFE: " + life, 10, 40);

        if (!playing) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 105, 300);
        }
    }

    public void addBullet(Bullet b) {
        synchronized (bullets) {
            bullets.add(b);
        }

        b.start();
    }

    public void removeBullet(Bullet b) {
        synchronized (bullets) {
            bullets.remove(b);
        }
    }

    public void addEnemy(Enemy e) {
        synchronized (enemies) {
            enemies.add(e);
        }

        e.start();
    }

    public void removeEnemy(Enemy e) {
        synchronized (enemies) {
            enemies.remove(e);
        }
    }

    public synchronized void checkCollisions() {
        List<Bullet> bulletSnapshot;
        List<Enemy> enemySnapshot;
        List<Enemy> removedEnemies = new ArrayList<>();

        synchronized (bullets) {
            bulletSnapshot = new ArrayList<>(bullets);
        }

        synchronized (enemies) {
            enemySnapshot = new ArrayList<>(enemies);
        }

        for (Bullet b : bulletSnapshot) {
            Rectangle bulletRect = new Rectangle(
                    b.getX(),
                    b.getY(),
                    5,
                    12
            );

            for (Enemy e : enemySnapshot) {
                if (removedEnemies.contains(e)) {
                    continue;
                }

                Rectangle enemyRect = new Rectangle(
                        e.getX(),
                        e.getY(),
                        20,
                        20
                );

                if (bulletRect.intersects(enemyRect)) {
                    b.stopMoving();
                    e.stopMoving();

                    removeBullet(b);
                    removeEnemy(e);
                    removedEnemies.add(e);

                    score++;
                    break;
                }
            }
        }

        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        for (Enemy e : enemySnapshot) {
            if (removedEnemies.contains(e)) {
                continue;
            }

            Rectangle enemyRect = new Rectangle(e.getX(), e.getY(), 20, 20);

            if (playerRect.intersects(enemyRect)) {
                e.stopMoving();
                removeEnemy(e);

                life--;

                if (life <= 0) {
                    gameOver();
                }

                repaint();
                break;
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void startGame() {
        EnemySpawner spawner = new EnemySpawner(this);
        spawner.start();
    }

    public void gameOver() {
        playing = false;

        synchronized (bullets) {
            for (Bullet b : bullets) {
                b.stopMoving();
            }
        }

        synchronized (enemies) {
            for (Enemy e : enemies) {
                e.stopMoving();
            }
        }

        repaint();
    }
}
