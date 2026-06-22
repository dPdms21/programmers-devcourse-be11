package part3;

// * Map
// 키와 값의 쌍을 저장하는 자료구조를 나타냄
// Map은 중복된 키를 허용하지 않으며, 각 키는 하나의 값에 매핑
// Map 인터페이스를 구현하는 대표적인 클래스들은 HashMap, TreeMap 등

// * 주요 특징
// - 키-값 쌍: Map은 각 요소를 키와 값의 쌍으로 저장
// - 중복된 키 허용 안함: 동일한 키를 두 번 추가할 수 없음. 만약 동일한 키를 다시 추가하면 기존의 값이 덮어씌워짐
// - 빠른 검색: 특정 키를 사용하여 값에 빠르게 접근할 수 있음

// * 주요 메소드
// - put(K key, V value): 맵에 키와 값을 추가한다. 만약 동일한 키가 이미 존재한다면 해당 키의 값이 새 값으로 대체
// - get(Object key): 키에 대응하는 값을 반환한다. 해당 키가 존재하지 않으면 null을 반환
// - remove(Object key): 특정 키에 대응하는 키-값 쌍을 제거
// - containsKey(Object key): 맵에 특정 키가 포함되어 있는지 확인
// - containsValue(Object value): 맵에 특정 값이 포함되어 있는지 확인
// - size(): 맵에 저장된 키-값 쌍의 수를 반환
// - clear(): 맵의 모든 요소를 제거

import java.util.*;

public class A_collections_map {
    // 1. HashMap
    // 설명
    // - 해시 기반: HashMap은 내부적으로 해시 테이블을 사용하여 데이터를 저장
    // - 빠른 성능: 일반적으로 키를 통한 검색, 삽입, 삭제 작업이 O(1)의 시간 복잡도를 가짐
    // - 순서 보장 없음: 요소들이 저장된 순서를 보장하지 않음
    public static void exam1() {

        Map<String, Integer> hashMap = new HashMap<>();

        // 요소 추가
        hashMap.put("apple", 10);
        hashMap.put("banana", 20);
        hashMap.put("orange", 30);
        hashMap.put("apple", 40);

        System.out.println(hashMap);

        // 특정 키의 값 얻기
        int value = hashMap.get("apple");
        System.out.println("value: " + value);

        // 요소 제거
        hashMap.remove("apple");
        System.out.println(hashMap);

        // 키의 존재 여부
        boolean contains = hashMap.containsKey("orange");
        System.out.println("contains: " + contains);

        // 순회 방법 1 : entrySet() 방식
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // 순회 방법 2 : keySet() 방식
        for (String key : hashMap.keySet()) {
            System.out.println(key + " : " + hashMap.get(key));
        }

        // 순회 방법 3 : values() 방법
        for ( Integer val : hashMap.values() ) {
            System.out.println("value: " + val);
        }
    }

    // 2. TreeMap
    // 설명
    // - 이진 검색 트리 기반: TreeMap은 내부적으로 레드-블랙 트리 구조를 사용하여 데이터를 저장
    // - 정렬된 순서: 키에 따라 요소들이 자동으로 정렬 (기본적으로 오름차순).
    // - 성능: 삽입, 삭제, 검색 작업이 O(log n)의 시간 복잡도를 가짐
    // 대부분의 경우는 HashMap이 기본 선택 "키로 빠르게 찾고 저장만 하면 됨, 순서는 상관없음" → HashMap.
    // 반면 키가 정렬돼 있어야 하거나, 범위/이웃 키 탐색이 필요하면 TreeMap 을 씀
    public static void exam2() {
        TreeMap<String, Integer> treeMap = new TreeMap<>();

        treeMap.put("apple", 10);
        treeMap.put("banana", 20);
        treeMap.put("orange", 30);

        System.out.println(treeMap);

        // 첫 번째 요소의 키 얻기
        String firstKey = treeMap.firstKey();
        System.out.println("firstKey: " + firstKey);

        // 마지막 요소의 값 얻기
        int value = treeMap.lastEntry().getValue();
        System.out.println("value: " + value);

    }

    public static void main(String[] args) {
        exam1();
        exam2();
    }
}
