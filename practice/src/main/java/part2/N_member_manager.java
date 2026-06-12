package part2;

public class N_member_manager {
    private N_member[] members;
    private int memberCount;

    public N_member_manager(int capacity) {
        members = new N_member[capacity];
        memberCount = 0;
    }

    // 회원추가
    public void add(N_member m) {
        members[memberCount++] = m;
    }

    public boolean isFull() {
        return memberCount == members.length;
    }

    // 전체출력
    public void printAll() {
        if (memberCount == 0) {
            System.out.println("등록된 회원이 없습니다.");
            return;
        }

        for (int i = 0; i < memberCount; i++) {
            members[i].printInfo();
        }
    }

    // 이름으로 찾기
    public N_member findByName(String name) {
        for ( int i = 0; i < memberCount; i++ ) {
            if ( name.equals(members[i].getName()) ) {
                return members[i];
            }
        }

        return null;
    }

    // 이메일로 찾기
    public N_member findByEmail(String email) {
        for ( int i = 0; i < memberCount; i++ ) {
            if ( email.equals(members[i].getEmail()) ) {
                return members[i];
            }
        }

        return null;
    }

    // 이메일 중복 체크
    public boolean existsEmail(String email) {
        return findByEmail(email) != null; // -> true or false
    }

    // 수정
    public boolean update(String name, String email, String phone, String byEmail) {
        // 조회
        N_member member = findByEmail(byEmail);
        if ( member == null ) {
            return false;
        }

        // 수정 객체에게 위임
        member.update(name, email, phone);

        return true;
    }

    // 삭제 -> 뒤 회원들을 한 칸씩 당김
    public boolean delete(String email) {
        int index = -1;
        // 조회
        for ( int i = 0; i < memberCount; i++ ) {
            if ( email.equals(members[i].getEmail()) ) {
                index = i;
                break;
            }
        }

        if ( index == -1 ) {
            return false;
        }

        for ( int i = index; i < memberCount; i++ ) {
            members[i] = members[i + 1]; // 객체 참조 하나만 당기면 됨
        }

        members[memberCount - 1] = null;
        memberCount--;

        return true;
    }

    // 현재 회원수
    public int getMemberCount() {
        return memberCount;
    }

    // 현재 요금제
    public int getCapacity() {
        return members.length;
    }
}
