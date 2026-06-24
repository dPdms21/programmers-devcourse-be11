package stream;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== 1. 스트림 만들고 전체 출력 (forEach) =====");
        List<Product> products = new ArrayList<>();
        products.add(new Product("연필", 500));
        products.add(new Product("공책", 1200));
        products.add(new Product("지우개", 300));
        products.add(new Product("필통", 3000));
        products.add(new Product("볼펜", 800));

        products.stream()
                .forEach(p -> System.out.println(p.getName() + " (" + p.getPrice() + "원)"));

        System.out.println("\n===== 2. filter: 1000원 이상만 =====");
        products.stream()
                .filter(p -> p.getPrice() >= 1000)
                .forEach(p -> System.out.println(p.getName() + " (" + p.getPrice() + "원)"));

        System.out.println("\n===== 3. map: 이름만 뽑기 =====");
        products.stream()
                .map(p -> p.getName())
                .forEach(name -> System.out.println(name));

        System.out.println("\n===== 4. map vs flatMap (주문 속 상품 목록) =====");
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, Arrays.asList("연필", "공책")));
        orders.add( new Order(2, Arrays.asList("필통", "볼펜", "공책")));

        List<List<String>> byMap = orders.stream()
                .map(o -> o.getItems())
                .collect(Collectors.toList());

        System.out.println("map      : " + byMap);

        List<String> byFlatMap = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.toList());

        System.out.println("flatMap  : " + byFlatMap);

        System.out.println("\n===== 5. filter + map + collect: 1000원 이상 상품 이름 리스트 =====");
        List<String> expensiveNames = products.stream()
                .filter(p -> p.getPrice() >= 1000)
                .map(p -> p.getName())
                .collect(Collectors.toList());

        System.out.println(expensiveNames);

        System.out.println("\n===== 6. 통계 =====");
        long cnt = products.stream()
                .filter(p -> p.getPrice() >= 1000)
                .count();

        System.out.println("1000원 이상 개수: " + cnt);

        int sum = products.stream()
                .mapToInt(p -> p.getPrice())
                .sum();

        System.out.println("전체 가격 합계: " + sum);

        double avg = products.stream()
                .mapToInt(p -> p.getPrice())
                .average().getAsDouble();

        System.out.println("전체 가격 평균: " + avg);

        List<String> byPrice = products.stream()
                .sorted((a, b) -> a.getPrice() - b.getPrice())
                .map(p -> p.getName())
                .collect(Collectors.toList());

        System.out.println("가격 오름차순: " + byPrice);
    }
}
