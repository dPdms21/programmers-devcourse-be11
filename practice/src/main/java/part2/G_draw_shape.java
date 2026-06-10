package part2;

class Shape {
    private String color = "black";

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void draw() {
        System.out.println("Color is " + color);
    }
}

class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Circle extends Shape {
    Point center; //원의 원점좌표
    int r; // 반지름

    public Circle() {
        this(new Point(0, 0), 100);
    }

    public Circle(Point center, int r) {
        this.center = center;
        this.r = r;
        setColor("red");
    }

    @Override
    public void draw() {
        System.out.println("Center is (" + center.getX() + ", " + center.getY() + "), r is " + r + ", color is " + getColor());
    }
}

class Triangle extends Shape {
    Point[] p = new Point[3];

    public Triangle(Point[] p) {
        this.p = p;
    }

    @Override
    public void draw() {
        System.out.println("P0 is ( " + p[0].getX() + ", " + p[0].getY() + "), P1 is ( " + p[1].getX() + ", " + p[1].getY() + " ), P2 is ( " + p[2].getX() + ", " + p[2].getY() + " )");
    }
}

public class G_draw_shape {
    public static void main(String[] args) {
        Point[] p = new Point[3];
        p[0] = new Point(100, 100);
        p[1] = new Point(140, 50);
        p[2] = new Point(200, 100);

        Triangle t = new Triangle(p);
        Circle c = new Circle(new Point(150, 150), 50);

        t.draw();
        c.draw();
    }
}
