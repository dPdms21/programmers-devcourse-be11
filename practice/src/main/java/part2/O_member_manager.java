package part2;

public class O_member_manager {
    private O_member[] members;
    private int memberCount;

    public O_member_manager(int capacity) {
        members = new O_member[capacity];
        memberCount = 0;
    }

    public boolean isFull() {
        return memberCount == members.length;
    }

    // 이메일 중복 검사
    public boolean existsEmail(String email) {
        for (int i = 0; i < memberCount; i++) {
            if (email.equals(members[i].getEmail())) {
                return true;
            }
        }

        return false;
    }

    // 이메일로 찾기
    public O_member findByEmail(String email) {
        for ( int i = 0; i < memberCount; i++ ) {
            if ( email.equals(members[i].getEmail()) ) {
                return members[i];
            }
        }

        return null;
    }

    // 이름으로 찾기
    public O_member findByName(String name) {
        for ( int i = 0; i < memberCount; i++ ) {
            if ( name.equals(members[i].getName()) ) {
                return members[i];
            }
        }

        return null;
    }

    // 전체 출력
    public void printAll() {
        if (memberCount == 0) {
            System.out.println("등록된 회원이 없습니다.");
            return;
        }

        for (int i = 0; i < memberCount; i++) {
            members[i].printInfo();
        }
    }

    // 수정
    public boolean update(String name, String email, String phone, String newEmail) {
        // 기존 이메일 검색
        O_member byEmail = findByEmail(email);
        if ( byEmail == null ) {
            return false;
        }

        // 실질적인 수정 기능은 객체한테 위임
        byEmail.update(name, newEmail, phone);
        return true;
    }

    // 삭제
    public boolean delete(String email) {
        int index = -1;
        for (int i = 0; i < memberCount; i++) {
            if (email.equals(members[i].getEmail())) {
                index = i;
                break;
            }
        }

        if (index == -1) return false;
        for (int i = index; i < memberCount - 1; i++) {
            members[i] = members[i + 1];
        }

        members[memberCount - 1] = null;
        memberCount--;

        return true;
    }

    // 추가
    public void add(O_member m) {
        members[memberCount++] = m;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getCapacity() {
        return members.length;
    }
}
