package accountbook2;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class AccountBookImpl implements AccountBook {
    private static final String DIR = "accountbook-data";
    private static final String BACKUP_DIR = "accountbook-data/backup";
    private final Scanner sc = new Scanner(System.in);

    public AccountBookImpl() {
        File f = new File(DIR);
        File backupDir = new File(BACKUP_DIR);

        if (!f.exists()) {
            f.mkdirs();
        }

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    @Override
    public void addAccount() {
        String today = LocalDate.now().toString();
        File f = new File(DIR, today + ".txt");

        int total = 0;
        StringBuilder sb = new StringBuilder();

        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("합계:")) {
                        total = Integer.parseInt(line.replace("합계:", "").replace("원", "").trim());
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("기존 내역 조회 중 오류: " + e.getMessage());
                return;
            }
        }

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

            if (!ans.equals("y")) {
                break;
            }
        }

        sb.append("합계: ").append(total).append("원\n");

        try (FileWriter fw = new FileWriter(f, false)) {
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

        Arrays.sort(files, Collections.reverseOrder());

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

    @Override
    public void deleteAccount() {
        System.out.println("------------------------");
        System.out.print("삭제할 날짜 > ");
        String date = sc.nextLine();

        File file = new File(DIR, date + ".txt");

        if (!file.exists()) {
            System.out.println("------------------------");
            System.out.println("그런 날짜 없음");
            return;
        }

        File backupFile = new File(BACKUP_DIR, file.getName());

        try (
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(backupFile)
        ) {
            fis.transferTo(fos);
        } catch (IOException e) {
            System.out.println("백업 중 오류: " + e.getMessage());
            return;
        }

        if (file.delete()) {
            System.out.println("------------------------");
            System.out.println("백업 후 삭제 완료");
        } else {
            System.out.println("------------------------");
            System.out.println("삭제 불가");
        }
    }

    @Override
    public void deleteItem() {
        System.out.println("------------------------");
        System.out.print("삭제할 날짜 > ");
        String date = sc.nextLine();

        File file = new File(DIR, date + ".txt");

        if (!file.exists()) {
            System.out.println("해당 날짜 내역 없음");
            return;
        }

        System.out.print("삭제할 항목 이름 > ");
        String keyword = sc.nextLine();

        File tempFile = new File(DIR, "temp.txt");
        int total = 0;
        boolean deleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file)); BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("합계:")) {
                    continue;
                }

                int colonIndex = line.lastIndexOf(":");
                int wonIndex = line.lastIndexOf("원");

                if (colonIndex == -1 || wonIndex == -1) {
                    continue;
                }

                String itemName = line.substring(0, colonIndex).trim();

                if (!deleted && itemName.equals(keyword)) {
                    deleted = true;
                    continue;
                }

                bw.write(line);
                bw.newLine();

                String priceText = line.substring(colonIndex + 1, wonIndex).trim();

                total += Integer.parseInt(priceText);
            }

            bw.write("합계: " + total + "원");
            bw.newLine();
        } catch (IOException | NumberFormatException e) {
            System.out.println("내역 삭제 중 오류: " + e.getMessage());
            return;
        }

        if (!deleted) {
            tempFile.delete();
            System.out.println("해당 항목 없음");
            return;
        }

        if (file.delete() && tempFile.renameTo(file)) {
            System.out.println("내역 삭제 완료");
        } else {
            System.out.println("파일 갱신 실패");
        }
    }

    @Override
    public void searchAccount() {
        System.out.println("------------------------");
        System.out.print("검색할 항목 > ");
        String keyword = sc.nextLine();

        File dir = new File(DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("저장된 내역 없음");
            return;
        }

        Arrays.sort(files, Collections.reverseOrder());

        boolean found = false;

        for (File f : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("합계:") && line.contains(keyword)) {
                        System.out.println(f.getName().replace(".txt", "") + " → " + line);
                        found = true;
                    }
                }
            } catch (IOException e) {
                System.out.println("검색 중 오류: " + e.getMessage());
            }
        }

        if (!found) {
            System.out.println("검색 결과 없음");
        }
    }

    @Override
    public void showMonthlyTotal() {
        System.out.println("------------------------");
        System.out.print("조회할 월 (YYYY-MM) > ");
        String month = sc.nextLine();

        File dir = new File(DIR);
        File[] files = dir.listFiles((d, name) -> name.startsWith(month) && name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("해당 월 내역 없음");
            return;
        }

        int monthlyTotal = 0;

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("합계:")) {
                        int total = Integer.parseInt(line.replace("합계:", "").replace("원", "").trim());

                        monthlyTotal += total;
                        break;
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println(file.getName() + " 조회 중 오류: " + e.getMessage());
            }
        }

        System.out.println("------------------------");
        System.out.println(month + " 합계: " + monthlyTotal + "원");
    }
}
