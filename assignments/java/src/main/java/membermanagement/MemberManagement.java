package membermanagement;

import java.util.*;

public class MemberManagement {
    private Scanner sc = new Scanner(System.in);

    private int totalCnt = 0;
    private int memberCnt = 0;
    private Member[] members;

    private int printPricePlan() {
        System.out.println("================== 요금제 선택 ==================");
        System.out.println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명");
        System.out.println("===============================================");
        System.out.print("> ");

        return Integer.parseInt(sc.nextLine());
    }

    private int printMenu() {
        System.out.println("===============================================");
        System.out.println("수행할 업무 선택 - 현재 회원수: " + memberCnt +"/" + totalCnt);
        System.out.println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)");
        System.out.println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제");
        System.out.println("[7]프로그램 종료");
        System.out.println("===============================================");
        System.out.print("> ");

        return Integer.parseInt(sc.nextLine());
    }

    private void addMember() {
        if (memberCnt == members.length) {
            System.out.println("------------------- 정원 초과 -------------------");
            return;
        }

        System.out.println("-----------------------------------------------");
        System.out.print("이름 > ");
        String name = sc.nextLine();
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        if (checkEmail(email)) {
            System.out.println("이미 존재하는 이메일");
            return;
        }

        System.out.println("-----------------------------------------------");
        System.out.print("전화번호 > ");
        String phone = sc.nextLine();

        members[memberCnt] = new Member(name, email, phone);
        memberCnt++;
    }

    private boolean checkEmail(String email) {
        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i].getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void selectEmail() {
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i].getEmail())) {
                System.out.println("이름: " + members[i].getName() + " 이메일: " + members[i].getEmail() + " 전화번호: " + members[i].getPhone());
                return;
            }
        }

        System.out.println("존재하지 않는 이메일");
    }

    private void selectName() {
        System.out.println("-----------------------------------------------");
        System.out.print("이름 > ");
        String name = sc.nextLine();

        boolean found = false;

        for (int i=0; i<memberCnt; i++) {
            if (name.equals(members[i].getName())) {
                System.out.println("이름: " + members[i].getName() + " 이메일: " + members[i].getEmail() + " 전화번호: " + members[i].getPhone());
                found = true;
            }
        }

        if (!found) {
            System.out.println("존재하지 않는 이름");
        }
    }

    private void selectAll() {
        for (int i=0; i<memberCnt; i++) {
            System.out.println("이름: " + members[i].getName() + " 이메일: " + members[i].getEmail() + " 전화번호: " + members[i].getPhone());
        }
    }

    private void updateMember() {
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        int idx = -1;

        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i].getEmail())) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            System.out.println("존재하지 않는 회원");
            return;
        }

        System.out.println("-----------------------------------------------");
        System.out.print("이름 > ");
        members[idx].setName(sc.nextLine());
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        members[idx].setEmail(sc.nextLine());
        System.out.println("-----------------------------------------------");
        System.out.print("전화번호 > ");
        members[idx].setPhone(sc.nextLine());
    }

    private void deleteMember() {
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        int idx = -1;

        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i].getEmail())) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            System.out.println("존재하지 않는 회원");
            return;
        }

        for (int i=idx; i<memberCnt-1; i++) {
            members[i] = members[i + 1];
        }

        memberCnt--;
        members[memberCnt] = null;
    }

    public void run() {
        int num = printPricePlan();

        while (num < 1 || num > 3) {
            System.out.println("올바른 요금제 선택!.");
            num = printPricePlan();
        }

        members = new Member[num*10];
        totalCnt = num * 10;

        while (true) {
            int choice = printMenu();

            switch (choice) {
                case 1:
                    addMember();
                    break;
                case 2:
                    selectEmail();
                    break;
                case 3:
                    selectName();
                    break;
                case 4:
                    selectAll();
                    break;
                case 5:
                    updateMember();
                    break;
                case 6:
                    deleteMember();
                    break;
                case 7:
                    System.out.println("================= 프로그램 종료 =================");
                    return;
                default:
                    System.out.println("=============== 올바른 번호 입력! ===============");
            }
        }
    }
}
