package petgame;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("---------------------------");
        System.out.print("반려동물 이름: ");
        String name = sc.nextLine();

        Pet pet = new Pet(name);
        pet.showStatus();

        while (true) {
            System.out.println("===========================");
            System.out.println("[1]먹이주기 [2]놀아주기");
            System.out.println("[3]상태보기 [4] 잠자기 [5]종료");
            System.out.println("===========================");
            System.out.print("숫자 입력 > ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    pet.feed();
                    pet.showStatus();
                    break;
                case 2:
                    pet.play();
                    pet.showStatus();
                    break;
                case 3:
                    pet.showStatus();
                    break;
                case 4:
                    pet.sleep();
                    pet.showStatus();
                    break;
                case 5:
                    System.out.println("===========================");
                    System.out.println("        프로그램 종료");
                    System.out.println("===========================");
                    return;
                default:
                    System.out.println("---------------------------");
                    System.out.println("다시 입력");
            }
        }
    }
}
