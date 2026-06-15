package part2;

import java.util.Scanner;

public class O_start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("[요금제를 선택하세요]");
        System.out.println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명");

        int plan = Integer.parseInt(sc.nextLine());
        O_member_manager manager = new O_member_manager(plan * 10);

        while (true) {
            System.out.println("\n[수행할 업무 - 현재 회원수 : "
                    + manager.getMemberCount() + "/" + manager.getCapacity() + "]");
            System.out.println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)");
            System.out.println("[4]전체조회 [5]수정 [6]삭제 [7]종료");

            int menu = Integer.parseInt(sc.nextLine());
            switch (menu) {
                case 1:
                    if (manager.isFull()) {
                        System.out.println("회원이 꽉 찼습니다.");
                        break;
                    }

                    System.out.println("등급 [1]일반 [2]VIP");
                    int grade = Integer.parseInt(sc.nextLine());
                    System.out.println("이름 : ");
                    String name = sc.nextLine();
                    System.out.println("이메일 : ");
                    String email = sc.nextLine();
                    System.out.println("연락처 : ");
                    String phone = sc.nextLine();

                    if ( manager.existsEmail(email) ) {
                        System.out.println("이미 존재하는 회원입니다.");
                        break;
                    }

                    O_member m = (grade == 2)
                            ? new O_vip_member(name, email, phone)
                            : new O_normal_member(name, email, phone);
                    manager.add(m);
                    System.out.println("회원이 차가되었습니다.");
                    break;
                case 2:
                    System.out.println("[조회] 이메일 : ");
                    O_member byEmail = manager.findByEmail(sc.nextLine());
                    if ( byEmail == null ) {
                        System.out.println("찾으시는 정보가 없습니다.");
                    } else {
                        byEmail.printInfo();
                    }

                    break;
                case 3:
                    System.out.println("[조회] 이름 : ");
                    O_member byName = manager.findByName(sc.nextLine());
                    if ( byName == null ) {
                        System.out.println("찾으시는 정보가 없습니다.");
                    } else {
                        byName.printInfo();
                    }

                    break;
                case 4:
                    manager.printAll();
                    break;
                case 5:
                    System.out.println("[수정] 대상 이메일 : ");
                    String byMail = sc.nextLine();

                    System.out.println("새 이름 : ");
                    String newName = sc.nextLine();
                    System.out.println("새 이메일 : ");
                    String newEmail = sc.nextLine();
                    System.out.println("새 연락처 : ");
                    String newPhone = sc.nextLine();

                    if ( manager.update(newName, byMail, newPhone, newEmail) ) {
                        System.out.println("수정이 완료되었습니다.");
                    } else {
                        System.out.println("찾으시는 회원이 없습니다.");
                    }

                    break;
                case 6:
                    System.out.println("[삭제] 이메일 : ");
                    if ( manager.delete(sc.nextLine()) ) {
                        System.out.println("삭제가 되었습니다.");
                    } else {
                        System.out.println("찾으시는 회원이 없습니다.");
                    }

                    break;
                case 7:
                    System.out.println("이용해주셔서 감사합니다.");
                    return;
                default:
                    System.out.println("잘 못 입력하셨습니다.");
            }

        }
    }
}
