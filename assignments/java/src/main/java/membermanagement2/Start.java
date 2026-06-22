package membermanagement2;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("============= 회원 관리 =============");
        System.out.println("[1]Lite:10 [2]Basic:20 [3]Premium:30");
        System.out.println("====================================");
        System.out.print("플랜 선택 > ");

        int plan = Integer.parseInt(sc.nextLine());
        MemberManager mm = new MemberManager(plan * 10);

        while (true) {
            System.out.println("====================================");
            System.out.println("[수행할 업무 - 현재 회원수 : " + mm.getCount() + "/" + mm.getCapacity() + "]");
            System.out.println("------------------------------------");
            System.out.println("[1]회원추가 [2]회원조회(메일)");
            System.out.println("[3]회원조회(이름) [4]전체조회");
            System.out.println("[5]수정 [6]삭제 [7]종료");
            System.out.println("====================================");
            System.out.print("업무 입력 > ");

            int num = Integer.parseInt(sc.nextLine());

            switch (num) {
                case 1: {
                    if (mm.isFull()) {
                        System.out.println("회원 초과");
                    } else {
                        System.out.println("====================================");
                        System.out.println("등급 [1]일반 [2]VIP");
                        System.out.println("====================================");
                        System.out.print("등급 선택 > ");

                        int grade = Integer.parseInt(sc.nextLine());

                        System.out.println("------------------------------------");
                        System.out.print("이름 > ");
                        String name = sc.nextLine();
                        System.out.print("이메일 > ");
                        String email = sc.nextLine();
                        System.out.print("연락처 > ");
                        String phone = sc.nextLine();

                        if (mm.existsEmail(email)) {
                            System.out.println("------------------------------------");
                            System.out.println("이메일 중복");
                        } else {
                            Member m = (grade == 2) ? new VipMember(name, email, phone) : new NormalMember(name, email, phone);
                            mm.add(m);
                        }
                    }
                    break;
                }
                case 2: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    Member m = mm.findByEmail(email);

                    if (m == null) {
                        System.out.println("------------------------------------");
                        System.out.println("회원 없음");
                    } else {
                        System.out.println("------------------------------------");
                        m.printInfo();
                    }
                    break;
                }
                case 3: {
                    System.out.println("------------------------------------");
                    System.out.print("이름 > ");
                    String name = sc.nextLine();

                    Member m = mm.findByName(name);

                    if (m == null) {
                        System.out.println("------------------------------------");
                        System.out.println("회원 없음");
                    } else {
                        System.out.println("------------------------------------");
                        m.printInfo();
                    }
                    break;
                }
                case 4: {
                    System.out.println("-----------------------------------");
                    mm.printAll();
                    break;
                }
                case 5: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    Member m = mm.findByEmail(email);

                    if (m == null) {
                        System.out.println("------------------------------------");
                        System.out.println("회원 없음");
                    } else {
                        System.out.println("------------------------------------");
                        System.out.print("새 이름 > ");
                        String newName = sc.nextLine();

                        System.out.print("새 이메일 > ");
                        String newEmail = sc.nextLine();

                        System.out.print("새 연락처 > ");
                        String newPhone = sc.nextLine();

                        mm.update(email, newName, newEmail, newPhone);
                    }
                    break;
                }
                case 6: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    Member m = mm.findByEmail(email);

                    if (m == null) {
                        System.out.println("------------------------------------");
                        System.out.println("회원 없음");
                    } else {
                        mm.delete(email);
                    }
                    break;
                }
                case 7:
                    System.out.println("====================================");
                    System.out.println("            프로그램 종료!");
                    System.out.println("====================================");
                    sc.close();
                    return;

                default:
                    System.out.println("다시 입력");
            }
        }
    }
}
