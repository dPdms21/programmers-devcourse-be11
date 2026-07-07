package javaboard;

public class Start {
    public static void main(String[] args) {
        Notice notice = new NoticeImpl();

        while (true) {
            int choice = notice.printMenu();

            switch (choice) {
                case 1:
                    notice.signIn();
                    break;
                case 2:
                    notice.signUp();
                    break;
                case 3:
                    notice.newNotice();
                    break;
                case 4:
                    notice.getList();
                    break;
                case 5:
                    notice.updateNotice();
                    break;
                case 6:
                    notice.deleteNotice();
                    break;
                case 7:
                    notice.signOut();
                    break;
                case 8:
                    notice.leave();
                    break;
                case 9:
                    System.out.println("=================");
                    System.out.println("프로그램 종료");
                    System.out.println("=================");
                    return;
                default:
                    System.out.println("잘못된 메뉴");
            }
        }
    }
}
