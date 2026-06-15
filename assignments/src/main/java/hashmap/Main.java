package hashmap;

public class Main {
    public static void main(String[] args) {
        MyHashMap map = new MyHashMap();

        // 저장 및 조회
        map.put("apple", "사과");
        map.put("banana", "바나나");
        System.out.println(map.get("apple"));      // 사과
        System.out.println(map.get("cherry"));     // null

        // 같은 키 갱신
        map.put("apple", "애플");
        System.out.println(map.get("apple"));      // 애플
        System.out.println(map.size());            // 2

        // 키 존재 여부
        System.out.println(map.containsKey("banana")); // true
        System.out.println(map.containsKey("cherry")); // false

        // 삭제
        System.out.println(map.remove("apple"));   // 애플
        System.out.println(map.get("apple"));      // null
        System.out.println(map.size());            // 1

        // 없는 키 삭제
        System.out.println(map.remove("cherry"));  // null
    }
}
