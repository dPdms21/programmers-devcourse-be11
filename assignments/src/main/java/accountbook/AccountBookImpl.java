package accountbook;

import java.util.*;

public class AccountBookImpl implements AccountBook{
    private Map<String, List<Item>> data = new TreeMap<>(Collections.reverseOrder());
    private Scanner sc = new Scanner(System.in);

    public void addAccount()  {
        System.out.println("-----------------");
        System.out.print("날짜 입력 (예: 2026-06-05) > ");
        String date = sc.nextLine();

        List<Item> items = data.getOrDefault(date, new ArrayList<>());

        while (true) {
            System.out.print("항목 이름 > ");
            String name = sc.nextLine();

            System.out.print("금액 > ");
            int price;

            try {
                price = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력");
                continue;
            }

            Item item = new Item(name, price);
            items.add(item);

            System.out.println("-----------------");
            System.out.print("추가? (y/n) > ");
            String ans = sc.nextLine();

            if (ans.equals("n")) {
                break;
            }
        }

        data.put(date, items);

        System.out.println("-----------------");
        System.out.println("[" + date + "] 등록 완료");

        int sum = 0;

        for (Item item : items) {
            System.out.println(item.getName() + " : " + item.getPrice() +"원");
            sum += item.getPrice();
        }

        System.out.println("합계 : " + sum + "원");
    }

    public void showAccount() {
        if (data.isEmpty()) {
            System.out.println("기록X");
            return;
        }

        System.out.println("=== 기록된 날짜 ===");

        for (String d : data.keySet()) {
            System.out.println(d);
        }

        System.out.println("-----------------");
        System.out.print("조회할 날짜 입력 > ");
        String date = sc.nextLine();

        if (!data.containsKey(date)) {
            System.out.println("없는 날짜");
            return;
        }

        System.out.println("-----------------");
        System.out.println("[" + date + "]");

        List<Item> items = data.get(date);

        int sum = 0;

        for (Item item : items) {
            System.out.println(item.getName() + " : " + item.getPrice() +"원");
            sum += item.getPrice();
        }

        System.out.println("합계 : " + sum + "원");

        String month = date.substring(0,7);
        int mSum = 0;

        for (String d : data.keySet()) {
            if (d.startsWith(month)) {
                List<Item> mItems = data.get(d);

                for (Item item : mItems) {
                    mSum += item.getPrice();
                }
            }
        }

        System.out.println(month + " 지출 합계 : " + mSum + "원");
    }

    public void deleteAll()   {
        if (data.isEmpty()) {
            System.out.println("기록X");
            return;
        }

        System.out.println("-----------------");
        System.out.print("전체 삭제할 날짜 입력 > ");
        String date = sc.nextLine();

        if (!data.containsKey(date)) {
            System.out.println("없는 날짜");
            return;
        }

        data.remove(date);
        System.out.println("전체 삭제 완료");
    }

    public void deleteItem()  {
        if (data.isEmpty()) {
            System.out.println("기록X");
            return;
        }

        System.out.println("-----------------");
        System.out.print("내역 삭제할 날짜 입력 > ");
        String date = sc.nextLine();

        if (!data.containsKey(date)) {
            System.out.println("없는 날짜");
            return;
        }

        System.out.println("-----------------");
        System.out.println("[" + date + "]");

        List<Item> items = data.get(date);

        for (int i=0; i<items.size(); i++) {
            Item item = items.get(i);
            System.out.println(i+1 + ". " + item.getName() + " : " + item.getPrice() + "원");
        }

        System.out.println("-----------------");
        System.out.print("삭제할 번호 입력 > ");
        int num;

        try {
            num = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력");
            return;
        }

        if (num < 1 || num > items.size()) {
            System.out.println("잘못된 번호");
            return;
        }

        items.remove(num - 1);
        System.out.println("내역 삭제 완료");

        if (items.isEmpty()) {
            data.remove(date);
        }
    }

    public void updateItem()  {
        if (data.isEmpty()) {
            System.out.println("기록X");
            return;
        }

        System.out.println("=== 기록된 날짜 ===");

        for (String d : data.keySet()) {
            System.out.println(d);
        }

        System.out.println("-----------------");
        System.out.print("금액 수정할 날짜 입력 > ");
        String date = sc.nextLine();

        if (!data.containsKey(date)) {
            System.out.println("없는 날짜");
            return;
        }

        System.out.println("-----------------");
        System.out.println("[" + date + "]");

        List<Item> items = data.get(date);

        for (int i=0; i<items.size(); i++) {
            Item item = items.get(i);
            System.out.println(i+1 + ". " + item.getName() + " : " + item.getPrice() + "원");
        }

        System.out.println("-----------------");
        System.out.print("수정할 번호 입력 > ");
        int num;

        try {
            num = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력");
            return;
        }

        if (num < 1 || num > items.size()) {
            System.out.println("잘못된 번호");
            return;
        }

        System.out.println("-----------------");
        System.out.print("수정 금액 입력 > ");
        int newPrice;

        try {
            newPrice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력");
            return;
        }

        items.get(num - 1).setPrice(newPrice);
        System.out.println("금액 수정 완료");
    }
}
