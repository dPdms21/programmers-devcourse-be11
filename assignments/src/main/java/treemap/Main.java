package treemap;

public class Main {
    public static void main(String[] args) {
        // 1. 기본 기능 테스트
        MyTreeMap map = new MyTreeMap();

        map.put("banana", 2);
        map.put("apple", 1);
        map.put("cherry", 3);

        map.printSorted();                         // [apple=1] [banana=2] [cherry=3]
        System.out.println(map.get("banana"));     // 2
        System.out.println(map.firstKey());        // apple
        System.out.println(map.lastKey());         // cherry

        System.out.println(map.size());                // 3
        System.out.println(map.containsKey("banana")); // true
        System.out.println(map.remove("banana"));  // 2
        map.printSorted();                             // [apple=1] [cherry=3]
        System.out.println(map.size());                // 2

        // 2. 불균형 트리 실험
        MyTreeMap unbalancedMap = new MyTreeMap();

        unbalancedMap.put("a", 1);
        unbalancedMap.put("b", 2);
        unbalancedMap.put("c", 3);
        unbalancedMap.put("d", 4);
        unbalancedMap.put("e", 5);

        unbalancedMap.printSorted();               // a b c d e 순서
        System.out.println(unbalancedMap.height()); // 5

        // 3. 가장 가까운 키 검색
        MyTreeMap rangeMap = new MyTreeMap();

        rangeMap.put("apple", 1);
        rangeMap.put("banana", 2);
        rangeMap.put("cherry", 3);
        rangeMap.put("grape", 4);
        rangeMap.put("orange", 5);

        System.out.println(rangeMap.ceilingKey("blueberry")); // cherry
        System.out.println(rangeMap.floorKey("blueberry"));   // banana

        // 4. 범위 검색
        rangeMap.headMap("grape");             // grape 미만
        rangeMap.subMap("banana", "orange");   // banana 이상, orange 미만
    }
}