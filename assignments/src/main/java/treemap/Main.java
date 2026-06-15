package treemap;

public class Main {
    public static void main(String[] args) {
        MyTreeMap map = new MyTreeMap();
        map.put("banana", 2);
        map.put("apple", 1);
        map.put("cherry", 3);

        map.printSorted();          // [apple=1] [banana=2] [cherry=3]  ← 넣은 순서와 무관하게 정렬!
        System.out.println(map.get("banana"));   // 2
        System.out.println(map.firstKey());      // apple (가장 작은 키)
        System.out.println(map.lastKey());       // cherry (가장 큰 키)

        System.out.println(map.size());                  // 3
        System.out.println(map.containsKey("banana"));  // true
        System.out.println(map.remove("banana"));       // 2
        map.printSorted();                                    // [apple=1] [cherry=3]
        System.out.println(map.size());                  // 2
    }
}
