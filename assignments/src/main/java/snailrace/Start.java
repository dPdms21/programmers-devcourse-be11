package snailrace;

public class Start {
    public static void main(String[] args) {
        Race race = new Race();

        new Snail("달팽이1", race).start();
        new Snail("달팽이2", race).start();
        new Snail("달팽이3", race).start();
    }
}
