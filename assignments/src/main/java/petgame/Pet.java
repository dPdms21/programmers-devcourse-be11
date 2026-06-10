package petgame;

public class Pet {
    private String name;
    private int fullness;
    private int happiness;

    public Pet(String name) {
        this.name = name;
        this.fullness = 50;
        this.happiness = 50;
    }

    public void showStatus() {
        System.out.println("---------------------------");
        System.out.println("[" + name + "] 포만감: " + fullness + " / 행복: " + happiness);

        if (happiness >= 70) {
            System.out.println(name + " 행복!!!");
        }
        else if (happiness >= 40) {
            System.out.println(name + " 보통");
        }
        else {
            System.out.println(name + " 시무룩...");
        }
    }

    public void feed() {
        fullness += 20;

        if (fullness >= 100) {
            fullness = 100;
        }

        happiness += 5;

        if (happiness >= 100) {
            happiness = 100;
        }

        System.out.println("---------------------------");
        System.out.println(name + "에게 먹이주기!");

        passTime();
    }

    public void play() {
        happiness += 20;

        if (happiness >= 100) {
            happiness = 100;
        }

        fullness -= 10;

        if (fullness <= 0) {
            fullness = 0;
        }

        System.out.println("---------------------------");
        System.out.println(name + "와(과) 놀아주기!");

        passTime();
    }

    public void sleep() {
        happiness += 10;

        if (happiness >= 100) {
            happiness = 100;
        }

        fullness += 10;

        if (fullness >= 100) {
            fullness = 100;
        }

        System.out.println("---------------------------");
        System.out.println(name + " 잠자기!");

        passTime();
    }

    private void passTime() {
        happiness -= 5;

        if (happiness <= 0) {
            happiness = 0;
        }

        fullness -= 5;

        if (fullness <= 0) {
            fullness = 0;
        }

        System.out.println("---------------------------");
        System.out.println("       ~ 시간 경과 ~");
    }
}
