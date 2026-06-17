package part3;

import java.util.*;

public class B_member_manager {
    private final List<B_member> members = new ArrayList<>();
    private final int capacity;

    public B_member_manager(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFull() {
        return members.size() >= capacity;
    }

    public boolean existsEmail(String email) {
        for (B_member member : members) {
            if (member.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void add(B_member member) {
        members.add(member);
    }

    public B_member findByEmail(String email) {
        for (B_member member : members) {
            if (member.getEmail().equals(email)) {
                return member;
            }
        }

        return null;
    }

    public B_member findByName(String name) {
        for (B_member member : members) {
            if (member.getName().equals(name)) {
                return member;
            }
        }

        return null;
    }

    public boolean update(String email, String name, String phone, String newEmail) {
        B_member member = findByEmail(email);

        if (member != null) {
            member.update(name, newEmail, phone);
            return true;
        }

        return false;
    }

    public boolean delete(String email) {
        B_member byEmail = findByEmail(email);

        if (byEmail == null) return false;

        members.remove(byEmail);

        return true;
    }

    public void printAll() {
        if (members.isEmpty()) {
            System.out.println("등록된 회원이 없음");
            return;
        }

        for (B_member member : members) {
            member.printInfo();
        }
    }

    public int size() {
        return members.size();
    }

    public int capacity() {
        return capacity;
    }
}
