package airplanegame;

public class Player {
    private int x;
    private int y;
    private final int width;
    private final int height;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void moveLeft() {
        x = Math.max(0, x - 15);
    }

    public void moveRight(int panelWidth) {
        x = Math.min(panelWidth - width, x + 15);
    }
}
