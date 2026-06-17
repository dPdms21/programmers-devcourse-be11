package snailrace;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Snail> snails = new ArrayList<>();

        int n, bet, track, speed, delay;

        System.out.println("===============");

        while (true) {
            System.out.println("---------------");
            System.out.print("달팽이 수 > ");
            n = Integer.parseInt(sc.nextLine());

            if (n >= 1) {
                break;
            }

            System.out.println("---------------");
            System.out.println("1 이상 입력!!");
        }

        while (true) {
            System.out.println("---------------");
            System.out.print("트랙 길이 > ");
            track = Integer.parseInt(sc.nextLine());

            if (track >= 5) {
                break;
            }

            System.out.println("---------------");
            System.out.println("5 이상 입력!!");
        }

        while (true) {
            System.out.println("---------------");
            System.out.println("1. 빠름");
            System.out.println("2. 보통");
            System.out.println("3. 느림");
            System.out.println("---------------");
            System.out.print("속도 선택 > ");
            speed = Integer.parseInt(sc.nextLine());

            if (speed >= 1 && speed <= 3) {
                break;
            }

            System.out.println("---------------");
            System.out.println("1~3 중 입력!!");
        }

        if (speed == 1) {
            delay = 100;
        }
        else if (speed == 2) {
            delay = 300;
        }
        else {
            delay = 500;
        }

        while (true) {
            System.out.println("---------------");
            System.out.print("우승할 것 같은 달팽이 번호 > ");
            bet = Integer.parseInt(sc.nextLine());

            if (bet >= 1 && bet <= n) {
                break;
            }

            System.out.println("---------------");
            System.out.println("1 이상 " + n + " 이하 입력!!");
        }

        System.out.println("===============");
        System.out.println("달팽이 경주 시작");
        System.out.println("트랙 길이: " + track);
        System.out.println("===============");

        Race race = new Race();

        for (int i=1; i<=n; i++) {
            Snail snail = new Snail("달팽이" + i, race, track, delay);
            snails.add(snail);
            race.addSnail(snail);
        }

        for (Snail snail : snails) {
            snail.start();
        }

        for (Snail s : snails) {
            try {
                s.join();
            } catch (InterruptedException e) {
                return;
            }
        }

        List<String> rank = race.getRanking();

        System.out.println("\n=== 최종 순위 ===");

        for (int i=0; i<rank.size(); i++) {
            System.out.println((i + 1) + "등: " + rank.get(i));
        }

        System.out.println("===============");

        String betName = "달팽이" + bet;

        System.out.println("\n=== 예측 결과 ===");
        System.out.println("예상: " + betName);
        System.out.println("우승: " + race.getWinner());

        if (betName.equals(race.getWinner())) {
            System.out.println("---------------");
            System.out.println("예상 적중!!");
            System.out.println("===============");
        }
        else {
            System.out.println("---------------");
            System.out.println("예상 실패..");
            System.out.println("===============");
        }
    }
}
