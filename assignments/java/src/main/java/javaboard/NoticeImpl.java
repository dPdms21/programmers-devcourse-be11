package javaboard;

import java.util.List;
import java.util.Scanner;

public class NoticeImpl implements Notice {
    private NoticeDAO noticeDAO = new NoticeDAO();
    private boolean status;
    private String userId, name;

    private Scanner scanner = new Scanner(System.in);

    public int printMenu() {
        System.out.println("===== 게시판 =====");
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("3. 글 등록");
        System.out.println("4. 글 목록");
        System.out.println("5. 글 수정");
        System.out.println("6. 글 삭제");
        System.out.println("7. 로그아웃");
        System.out.println("8. 회원 탈퇴");
        System.out.println("9. 종료");
        System.out.println("=================");
        System.out.print("메뉴 선택: ");

        return Integer.parseInt(scanner.nextLine());
    }

    public void signUp() {
        System.out.println("-----------------");
        System.out.print("아이디: ");
        String userId = scanner.nextLine();

        if (noticeDAO.checkUserId(userId)) {
            System.out.println("이미 가입된 사용자");
            return;
        }

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        System.out.print("이름: ");
        String name = scanner.nextLine();

        if (noticeDAO.signupExc(userId, password, name)) {
            System.out.println("-----------------");
            System.out.println("회원가입 완료");
        }
    }

    public void signIn() {
        System.out.println("-----------------");
        System.out.print("아이디: ");
        String userId = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        SignInResponseDTO res = noticeDAO.signInExc(userId, password);

        if (res == null) {
            System.out.println("-----------------");
            System.out.println("존재하지 않음");
            return;
        }

        if (res.isStatus()) {
            setUserInfo(true, res.getUserId(), res.getName());
            System.out.println("-----------------");
            System.out.println(name + "님 로그인 완료");
        }
        else {
            System.out.println("비밀번호 불일치");
        }
    }

    public void newNotice() {
        if (!checkSignIn()) {
            return;
        }

        System.out.println("-----------------");
        System.out.print("내용: ");
        String content = scanner.nextLine();

        if (noticeDAO.newNotice(userId, content)) {
            System.out.println("-----------------");
            System.out.println("글 등록 완료");
        }
    }

    public void getList() {
        System.out.println("-----------------");
        List<String> list = noticeDAO.getList();

        if (list.isEmpty()) {
            System.out.println("-----------------");
            System.out.println("등록된 글 없음");
            return;
        }

        list.forEach(System.out::println);
    }

    public void updateNotice() {
        if (!checkSignIn()) {
            return;
        }

        System.out.println("-----------------");
        List<String> list = noticeDAO.getListByUserId(userId);

        if (list.isEmpty()) {
            System.out.println("-----------------");
            System.out.println("수정할 글 없음");

            return;
        }

        list.forEach(System.out::println);

        System.out.println("-----------------");
        System.out.print("수정할 글 번호: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("수정할 내용: ");
        String content = scanner.nextLine();

        if (noticeDAO.updateNotice(id, userId, content)) {
            System.out.println("-----------------");
            System.out.println("글 수정 완료");
        }
        else {
            System.out.println("-----------------");
            System.out.println("글 수정 실패");
        }
    }

    public void deleteNotice() {
        if (!checkSignIn()) {
            return;
        }

        System.out.println("-----------------");
        List<String> list = noticeDAO.getListByUserId(userId);

        if (list.isEmpty()) {
            System.out.println("-----------------");
            System.out.println("삭제할 글 없음");
            return;
        }

        list.forEach(System.out::println);

        System.out.println("-----------------");
        System.out.print("삭제할 글 번호: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (noticeDAO.deleteNotice(id, userId)) {
            System.out.println("-----------------");
            System.out.println("글 삭제 완료");
        } else {
            System.out.println("-----------------");
            System.out.println("글 삭제 실패");
        }
    }

    public boolean checkSignIn() {
        if (!status) {
            System.out.println("-----------------");
            System.out.println("로그인 먼저!");

            return false;
        }

        return true;
    }

    public void signOut() {
        setUserInfo(false, null, null);
        System.out.println("-----------------");
        System.out.println("로그아웃 완료");
    }

    private void setUserInfo(boolean status, String userId, String name) {
        this.status = status;
        this.userId = userId;
        this.name = name;
    }

    public void leave() {
        System.out.println("-----------------");
        System.out.print("탈퇴할 아이디: ");
        String deleteUserId = scanner.nextLine();

        if (!noticeDAO.checkUserId(deleteUserId)) {
            System.out.println("-----------------");
            System.out.println("존재하지 않는 사용자");

            return;
        }

        noticeDAO.deleteContentExc(deleteUserId);

        if (noticeDAO.leaveExc(deleteUserId)) {
            System.out.println("-----------------");
            System.out.println("회원 탈퇴 완료");

            if (deleteUserId.equals(userId)) {
                signOut();
            }
        }
    }
}
