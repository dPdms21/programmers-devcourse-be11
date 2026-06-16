package membermanagement4;

import java.util.*;

public class Start {
    static int readInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력");
            return -1;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("============= 회원 관리 =============");
        System.out.println("[1]Lite:10 [2]Basic:20 [3]Premium:30");
        System.out.println("====================================");

        PricePlan plan = null;

        while (plan == null) {
            System.out.print("플랜 선택 > ");
            plan = PricePlan.from(readInt(sc));

            if (plan == null) {
                System.out.println("1~3 중 선택");
            }
        }

        MemberManager manager = new MemberManager(plan.getCapacity());

        while (true) {
            System.out.println("====================================");
            System.out.println("[현재 " + manager.size() + "/" + manager.capacity() + "]");
            System.out.println("[1]추가 [2]메일조회 [3]이름조회 [4]전체");
            System.out.println("[5]수정 [6]삭제 [7]종료");
            System.out.println("====================================");
            int menu = readInt(sc);

            switch (menu) {
                case 1: {
                    if (manager.isFull()) {
                        System.out.println("정원이 찼습니다.");
                        break;
                    }

                    System.out.println("====================================");
                    System.out.println("등급 [1]일반 [2]VIP");
                    System.out.println("====================================");

                    int grade = readInt(sc);

                    if (grade != 1 && grade != 2) {
                        System.out.println("1~2 중 선택");
                        break;
                    }

                    System.out.println("------------------------------------");
                    System.out.print("이름 > ");
                    String name  = sc.nextLine();
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();
                    System.out.print("연락처 > ");
                    String phone = sc.nextLine();

                    if (manager.existsEmail(email)) {
                        System.out.println("------------------------------------");
                        System.out.println("이미 있는 회원");
                        break;
                    }

                    Member m = (grade == 2)
                            ? new VipMember(name, email, phone)
                            : new NormalMember(name, email, phone);
                    manager.add(m);

                    System.out.println("------------------------------------");
                    System.out.println("추가 완료");
                    break;
                }
                case 2: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    Member m = manager.findByEmail(email);

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

                    Member m = manager.findByName(name);

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
                    manager.printAll();
                    break;
                }
                case 5: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    Member m = manager.findByEmail(email);

                    if (m == null) {
                        System.out.println("------------------------------------");
                        System.out.println("회원 없음");
                        break;
                    }

                    System.out.println("------------------------------------");
                    System.out.print("새 이름 > ");
                    String newName = sc.nextLine();

                    System.out.print("새 이메일 > ");
                    String newEmail = sc.nextLine();

                    System.out.print("새 연락처 > ");
                    String newPhone = sc.nextLine();

                    if (!email.equals(newEmail) && manager.existsEmail(newEmail)) {
                        System.out.println("이미 사용 중인 이메일");
                        break;
                    }

                    manager.update(email, newName, newEmail, newPhone);
                    System.out.println("수정 완료");
                    break;
                }
                case 6: {
                    System.out.println("------------------------------------");
                    System.out.print("이메일 > ");
                    String email = sc.nextLine();

                    if (manager.delete(email)) {
                        System.out.println("삭제 완료");
                    }
                    else {
                        System.out.println("회원 없음");
                    }
                    break;
                }
                case 7: {
                    System.out.println("====================================");
                    System.out.println("            프로그램 종료!");
                    System.out.println("====================================");
                    sc.close();
                    return;
                }
                default:
                    System.out.println("1~7 중 선택");
            }
        }
    }
}
