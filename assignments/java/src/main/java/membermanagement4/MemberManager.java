package membermanagement4;

import java.util.*;

public class MemberManager {
    private final List<Member> members = new ArrayList<>();
    private final int capacity;

    public MemberManager(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFull() {
        return members.size() >= capacity;
    }

    public boolean existsEmail(String email) {
        for (Member m : members) {
            if (m.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void add(Member member) {
        members.add(member);
    }

    public int size() {
        return members.size();
    }

    public int capacity() {
        return capacity;
    }

    public Member findByEmail(String email) {
        for (Member m : members) {
            if (m.getEmail().equals(email)) {
                return m;
            }
        }

        return null;
    }

    public Member findByName(String name) {
        for (Member m : members) {
            if (m.getName().equals(name)) {
                return m;
            }
        }

        return null;
    }

    public void printAll() {
        if (members.isEmpty()) {
            System.out.println("없는 회원");
            return;
        }

        for (Member m : members) {
            m.printInfo();
        }
    }

    public boolean update(String email, String newName, String newEmail, String newPhone) {
        Member m = findByEmail(email);

        if (m == null) {
            return false;
        }

        m.update(newName, newEmail, newPhone);
        return true;
    }

    public boolean delete(String email) {
        Member m = findByEmail(email);

        if (m == null) {
            return false;
        }

        members.remove(m);
        return true;
    }
}
