package membermanagement5;

import java.util.*;
import java.io.*;

public class MemberManager {
    private final List<Member> members = new ArrayList<>();
    private final int capacity;
    private static final String DIR = "member-data";
    private static final String FILE = DIR + "/members.txt";

    public MemberManager(int capacity) {
        this.capacity = capacity;

        File dir = new File(DIR);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        load();
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
        save();
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
        save();

        return true;
    }

    public boolean delete(String email) {
        Member m = findByEmail(email);

        if (m == null) {
            return false;
        }

        members.remove(m);
        save();

        return true;
    }

    public void save() {
        try (FileWriter fw = new FileWriter(FILE)) {
            for (Member m : members) {
                fw.write(m.toFileString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("저장 오류: " + e.getMessage());
        }
    }

    public void load() {
        File file = new File(FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] p = line.split(",");

                if (p.length != 4) {
                    continue;
                }

                String grade = p[0], name = p[1], email = p[2], phone = p[3];

                Member m = grade.equals("VIP")
                        ? new VipMember(name, email, phone)
                        : new NormalMember(name, email, phone);
                members.add(m);
            }
        } catch (IOException e) {
            System.out.println("불러오기 오류: " + e.getMessage());
        }
    }
}
