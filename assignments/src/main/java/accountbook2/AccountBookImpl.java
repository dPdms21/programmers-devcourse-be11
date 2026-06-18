package accountbook2;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class AccountBookImpl implements AccountBook {
    private static final String DIR = "accountbook-data";
    private final Scanner sc = new Scanner(System.in);

    public AccountBookImpl() {
        File f = new File(DIR);

        if (!f.exists()) {
            f.mkdir();
        }
    }

    @Override
    public void addAccount() {
        String today = LocalDate.now().toString();
        File f = new File(DIR, today + ".txt");

        int total = 0;
        StringBuilder sb = new StringBuilder();

        while (true) {
            System.out.println("------------------------");
            System.out.print("항목 이름 > ");
            String name = sc.nextLine();

            System.out.print("금액 > ");
            int price;

            try {
                price = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("------------------------");
                System.out.println("숫자만 입력");
                continue;
            }

            sb.append(name).append(" : ").append(price).append("원\n");
            total += price;

            System.out.println("------------------------");
            System.out.print("추가? (y/n) > ");
            String ans = sc.nextLine();

            if (ans.equals("n")) {
                break;
            }
        }

        sb.append("합계: ").append(total).append("원\n");

        try (FileWriter fw = new FileWriter(f, true)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("------------------------");
            System.out.println("저장 중 오류: " + e.getMessage());
        }
    }

    @Override
    public void showAccount() {
        File f = new File(DIR);
        String[] files = f.list();

        if (files == null || files.length == 0) {
            System.out.println("------------------------");
            System.out.println("저장된 내역 없음.");
            return;
        }

        for (String n : files) {
            if (n.endsWith(".txt")) {
                System.out.println(n.replace(".txt", ""));
            }
        }

        System.out.println("------------------------");
        System.out.print("조회할 날짜 > ");
        String date = sc.nextLine();

        File file = new File(DIR, date + ".txt");

        if (!file.exists()) {
            System.out.println("------------------------");
            System.out.println("해당 날짜 내역 없음");
            return;
        }

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("------------------------");
                System.out.println("조회 중 오류: " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteAccount() {
        System.out.println("------------------------");
        System.out.print("삭제할 날짜 > ");
        String date = sc.nextLine();

        File file = new File(DIR, date + ".txt");

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("------------------------");
                System.out.println("삭제 완료");
            }
            else {
                System.out.println("------------------------");
                System.out.println("삭제 불가");
            }
        }
        else {
            System.out.println("------------------------");
            System.out.println("그런 날짜 없음");
        }
    }
}
