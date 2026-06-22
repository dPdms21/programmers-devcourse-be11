package part3;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class D_account_book_impl implements D_account_book {
    private final String DIR = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "accountbook";
    private Scanner sc;

    public D_account_book_impl(Scanner sc) {
        this.sc = sc;
        File folder = new File(DIR);

        if (!folder.exists()) {
            folder.mkdir(); // 폴더 생성
        }
    }

    @Override
    public void addAccount() {

        String today = LocalDate.now().toString();
        File file = new File(DIR, today + ".txt");

        int total = 0;
        StringBuilder sb = new StringBuilder();

        while (true) {
            System.out.println("항목 이름 : ");
            String name = sc.nextLine().trim();
            System.out.println("가격 : ");
            int price = readInt();
            sb.append(name).append(" : ").append(price).append("원\n");
            total += price;

            System.out.println("더 추가? (y/n) ");
            String more = sc.nextLine().trim();

            if (more.equals("n")) {
                break;
            }
        }

        sb.append("합계 : ").append(total).append("원\n");

        // append 모드 활성화 : 파일이 없으면 새로 만들고, 있으면 뒤에 이어 붙임
        try ( FileWriter fw = new FileWriter( file, true ) ) {
            fw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("저장 오류 : " + e.getMessage());
            return;
        }

        System.out.println("\n" + today + ".txt 저장 완료!");
        System.out.println(sb);
    }

    @Override
    public void showAccount() {
        String[] dates = listDates();

        if ( dates.length == 0 ) {
            System.out.println("기록 없음");
            return;
        }

        System.out.println("== 기록된 날짜 ==");

        for ( String date : dates ) {
            System.out.println(date);
        }

        System.out.println("조회할 날짜 입력 : ");
        String date = sc.nextLine().trim();
        File file = new File(DIR, date + ".txt");

        if ( !file.exists() ) {
            System.out.println("그런 날짜 없음");
            return;
        }

        System.out.println("\n== " + date + " ==");

        try ( BufferedReader br = new BufferedReader(new FileReader(file)) ) {

            String line;

            while ( (line = br.readLine()) != null ) {
                System.out.println(line);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAccount() {
        String[] dates = listDates();

        if ( dates.length == 0 ) {
            System.out.println("기록 없음");
            return;
        }

        System.out.println("== 기록된 날짜 ==");

        for ( String date : dates ) {
            System.out.println(date);
        }

        System.out.println("삭제할 날짜 입력 ");
        String date = sc.nextLine().trim();
        File file = new File(DIR, date + ".txt");

        if ( !file.exists() ) {
            System.out.println("그런 날짜 없음");
            return;
        }

        if ( file.delete() ) {
            System.out.println("삭제 완료");
        }
        else {
            System.out.println("삭제 실패");
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자로 다시 입력");
            }
        }
    }

    private String[] listDates() {
        File folder = new File(DIR);
        String[] files = folder.list();

        if ( files == null ) {
            return new String[0];
        }

        List<String> dates = new ArrayList<>();
        for ( String name : files ) {
            if ( name.endsWith(".txt") ) {
                dates.add(name.replace(".txt", ""));
            }
        }

        Collections.sort(dates, Collections.reverseOrder()); // 최신순 정렬

        return dates.toArray(new String[0]);
    }
}
