package part2;

import java.util.Scanner;

public class N_start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("[요금제를 선택하세요]");
        System.out.println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명");

        // 문자열 -> 숫자 변환 => parse -> 다른 타입으로 변환하는 것을 파싱
        // 숫자 -> 문자열
        int plan = Integer.parseInt(sc.nextLine()); // buffer <- "문자열을 사용자한테 받은 문자열" -> buffer 비워준다.
        N_member_manager manager = new N_member_manager(plan * 10);

        // while
        while (true) {
            System.out.println("\n[수행할 업무 - 현재 회원수 : "
                    + manager.getMemberCount() + "/" + manager.getCapacity() + "]");
            System.out.println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)");
            System.out.println("[4]전체조회 [5]수정 [6]삭제 [7]종료");
            System.out.println("원하는 번호를 입력해주세요.");

            int menu = Integer.parseInt(sc.nextLine());
            switch (menu) {
                case 1: // 회원추가
                    if (manager.isFull()) {
                        System.out.println("회원이 꽉 찼습니다.");
                        break;
                    }

                    System.out.println("등급 [1]일반 [2]VIP");
                    int grade = Integer.parseInt(sc.nextLine());

                    String name = sc.nextLine();
                    String email = sc.nextLine();
                    String phone = sc.nextLine();

                    // 완성해주세요 : 이메일 중복 체크
                    if ( manager.existsEmail(email) ) {
                        System.out.println("이미 존재하는 회원입니다.");
                        break;
                    }

                    // 삼항연산자
                    N_member member = (grade == 2)
                            ? new N_vip_member(name, email, phone)
                            : new N_normal_member(name, email, phone);

                    manager.add(member);
                    System.out.println("회원이 추가되었습니다.");
                    break;
                case 2: // 이메일 조회
                    System.out.println("[조회] 찾고자하는 이메일을 입력해주세요");
                    String userEmail = sc.nextLine();

                    N_member m = manager.findByEmail(userEmail);

                    if ( m == null ) {
                        System.out.println("찾으시는 정보가 없습니다.");
                    } else {
                        m.printInfo();
                    }

                    break;
                case 3: // 이름으로 조회
                    System.out.println("[조회] 찾고자하는 이름을 입력해주세요");
                    String userName = sc.nextLine();

                    N_member byName = manager.findByName(userName);

                    if ( byName == null ) {
                        System.out.println("찾으시는 정보가 없습니다.");
                    } else {
                        byName.printInfo();
                    }

                    break;
                case 4: // 전체 조회
                    manager.printAll();
                    break;
                case 5:
                    System.out.println("[수정] 찾고자하는 이메일을 입력해주세요");
                    String byMail = sc.nextLine();

                    System.out.println("새 이름 ");
                    String newName = sc.nextLine();
                    System.out.println("새 이메일");
                    String newEmail = sc.nextLine();
                    System.out.println("새 연락처 ");
                    String newPhone = sc.nextLine();

                    if ( manager.update(newName, newEmail, newPhone, byMail) ) {
                        System.out.println("수정이 완료되었습니다.");
                    } else {
                        System.out.println("찾으시는 회원이 없습니다.");
                    }

                    break;
                case 6: // 삭제
                    System.out.println("[삭제] 찾고자하는 이메일을 입력해주세요");

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
