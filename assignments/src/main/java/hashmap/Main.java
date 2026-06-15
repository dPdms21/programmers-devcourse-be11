package hashmap;

public class Main {
    public static void main(String[] args) {
        MyHashMap map = new MyHashMap();

        // 저장 및 조회
        map.put("apple", 1);
        map.put("banana", 2);
        System.out.println(map.get("apple"));      // 1
        System.out.println(map.get("cherry"));     // null

        // 같은 키 갱신
        map.put("apple", 10);
        System.out.println(map.get("apple"));      // 10
        System.out.println(map.size());            // 2

        // 키 존재 여부
        System.out.println(map.containsKey("banana")); // true
        System.out.println(map.containsKey("cherry")); // false

        // 삭제
        System.out.println(map.remove("apple"));   // 10
        System.out.println(map.get("apple"));      // null
        System.out.println(map.size());            // 1

        // 없는 키 삭제
        System.out.println(map.remove("cherry"));  // null
    }
}
