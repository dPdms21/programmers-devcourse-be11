package membermanagement2;

public class MemberManager {
    private Member[] members;
    private int memberCnt;

    public MemberManager(int capacity) {
        members = new Member[capacity];
        memberCnt = 0;
    }

    public boolean isFull() {
        return memberCnt == members.length;
    }

    public boolean existsEmail(String email) {
        for (int i = 0; i < memberCnt; i++) {
            if (members[i].getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void add(Member m) {
        members[memberCnt] = m;
        memberCnt++;
    }

    public int getCount() {
        return memberCnt;
    }

    public int getCapacity() {
        return members.length;
    }

    public Member findByName(String name) {
        for (int i = 0; i < memberCnt; i++) {
            if (members[i].getName().equals(name)) {
                return members[i];
            }
        }

        return null;
    }

    public Member findByEmail(String email) {
        for (int i = 0; i < memberCnt; i++) {
            if (members[i].getEmail().equals(email)) {
                return members[i];
            }
        }

        return null;
    }

    public void printAll() {
        for (int i = 0; i < memberCnt; i++) {
            members[i].printInfo();
        }
    }

    public boolean update(String email, String name, String newEmail, String phone) {
        Member m = findByEmail(email);

        if (m == null) {
            return false;
        }

        m.update(name, newEmail, phone);

        return true;
    }

    public boolean delete(String email) {
        int idx = -1;

        for (int i = 0; i < memberCnt; i++) {
            if (members[i].getEmail().equals(email)) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            return false;
        }

        for (int i = idx; i < memberCnt-1; i++) {
            members[i] = members[i+1];
        }

        members[memberCnt - 1] = null;
        memberCnt--;

        return true;
    }
}
