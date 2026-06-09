import java.util.*;

public class MemberManagement {
    private Scanner sc = new Scanner(System.in);

    private int totalCnt = 0;
    private int memberCnt = 0;
    private String[][] members;

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
        
        members[memberCnt][0] = name;
        members[memberCnt][1] = email;
        members[memberCnt][2] = phone;
        memberCnt++;
    }

    private boolean checkEmail(String email) {
        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i][1])) {
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
            if (email.equals(members[i][1])) {
                System.out.println("이름: " + members[i][0] + " 이메일: " + members[i][1] + " 전화번호: " + members[i][2]);
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
            if (name.equals(members[i][0])) {
                System.out.println("이름: " + members[i][0] + " 이메일: " + members[i][1] + " 전화번호: " + members[i][2]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("존재하지 않는 이름");
        }
    }

    private void selectAll() {
        for (int i=0; i<memberCnt; i++) {
            System.out.println("이름: " + members[i][0] + " 이메일: " + members[i][1] + " 전화번호: " + members[i][2]);
        }
    }

    private void updateMember() {
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        int idx = -1;

        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i][1])) {
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
        members[idx][0] = sc.nextLine();
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        members[idx][1] = sc.nextLine();
        System.out.println("-----------------------------------------------");
        System.out.print("전화번호 > ");
        members[idx][2] = sc.nextLine();
    }

    private void deleteMember() {
        System.out.println("-----------------------------------------------");
        System.out.print("이메일 > ");
        String email = sc.nextLine();

        int idx = -1;

        for (int i=0; i<memberCnt; i++) {
            if (email.equals(members[i][1])) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            System.out.println("존재하지 않는 회원");
            return;
        }

        for (int i=idx; i<memberCnt-1; i++) {
            members[i][0] = members[i+1][0];
            members[i][1] = members[i+1][1];
            members[i][2] = members[i+1][2];
        }

        memberCnt--;

        members[memberCnt][0] = null;
        members[memberCnt][1] = null;
        members[memberCnt][2] = null;
    }

    public void run() {
        int num = printPricePlan();

        while (num < 1 || num > 3) {
            System.out.println("올바른 요금제 선택!.");
            num = printPricePlan();
        }

        members = new String[num*10][3];
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

    public static void main(String[] args) {
        MemberManagement mm = new MemberManagement();
        mm.run();
    }
}
