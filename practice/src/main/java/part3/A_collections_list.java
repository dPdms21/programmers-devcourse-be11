package part3;

// * List
// 순서가 있는 요소들의 컬렉션을 나타내는 인터페이스
// List 인터페이스를 구현하는 대표적인 클래스 ArrayList, LinkedList, Vector 등
// List는 중복된 요소를 허용하며, 인덱스를 기반으로 요소에 접근할 수 있음

// * 주요특징
// - 순서 유지: List는 요소들이 추가된 순서를 유지
// - 인덱스로 접근: 각 요소는 인덱스를 통해 접근할 수 있다. 인덱스 0부터 시작
// - (값의) 중복 허용: 동일한 값을 가진 요소가 여러개 있을 수 있음
// - 유연한 크기: 구현체는 동적으로 크기를 조절할 수 있음

// * 주요 메서드
// - add(E e): 리스트에 요소를 추가
// - get(int index): 인덱스에 있는 요소를 반환
// - remove(int index): 인데스에 있는 요소를 제거
// - size(): 리스트의 요소 갯수를 반환
// - contains(Object o): 리스트에 특정 요소가 포함되어 있는지 확인
// - clear(): 리스트의 모든 요소를 제거

// * 요약
// - ArrayList  : 배열 기반의 리스트로 인덱스를 통한 빠른 접근이 가능하지만 중간 삽입/삭제가 느림
// - LinkedList : 노드 기반의 리스트로, 삽입/삭제가 빠르지만 인덱스를 통한 접근이 느림
// - Stack      : 후입선출(LIFO) 구조를 가지며, 요소를 택에 추가하고 제거하는 데 사용

// 배열 -> 시작주소를 알면 나머지 주소를 알 수 있음 -> 물리적으로 연결
//   0   1    2    3    4    5 --------> 10000
// |ㄱ| |ㄴ| |ㄷ| |ㅂ| |ㄹ| |ㅁ|

// linkedlist -> 논리적으로 연결
// &0(자기주소) | prev null | a | &4 next| (node)
// &4 | &0 | b | &3 next
// &3 | &4 | f | &5 |
// &3 | &4 | c | &9 next
// &9 | &5 | d | null
// ....

import java.util.*;

public class A_collections_list {

    // 1. ArrayList
    public void exam1() {

        List<String> list = new ArrayList<>();

        // 요소 추가
        list.add("apple");
        list.add("banana");
        list.add("orange");
        list.add("grape");
        list.add("watermelon");

        // 특정 인덱스에  요소 추가
        list.add(1, "lemon");

        // 리스트의 크기 확인
        System.out.println("List size: " + list.size());

        // 인덱스를 사용하여 요소 접근
        System.out.println("Apple: " + list.get(0));

        // 요소 제거
        list.remove(2);

        // 특정 요소가 리스트에 포함되어 있는지 확인
        if (list.contains("banana")) {
            System.out.println("banana is in the list");
        }

        // 리스트의 모든 요소 제거
        list.clear();

        // 순회 방법 1 : for 루프 사용
        for ( int i = 0; i < list.size(); i++ ) {
            System.out.println(list.get(i));
        }

        // 순회 방법 2 : 향상된 for 루프 사용
        for ( String fruit : list ) {
            System.out.println(fruit);
        }

        // 순회 방법 3 : Iterator 사용
        Iterator<String> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            String element = iterator.next();
            System.out.println(element);
        }

        // 순회 방법 4 : ListIterator 사용 (양방향 순회 가능)
        ListIterator<String> stringListIterator = list.listIterator();

        // 정방향
        while (stringListIterator.hasNext()) {
            String element = stringListIterator.next();
            System.out.println(element);
        }
        // 역방향
        while (stringListIterator.hasPrevious()) {
            String element = stringListIterator.previous();
            System.out.println(element);
        }
    }

    // 2. LinkedList
    public void exam2() {

        LinkedList<String> list = new LinkedList<>();

        // 추가
        list.add("apple");
        list.add(1, "banana");

        list.removeLast(); // Java17 List에서는 에러, LinkedList에 있는 메서드

        // 해당 값을 인덱스로 가져온다.
        System.out.println(list.get(0));

        // 순회 방법 1 : for 루프 사용
        for ( int i = 0; i < list.size(); i++ ) {
            System.out.println(list.get(i));
        }

        // 순회 방법 2 : 향상된 for 루프 사용
        for ( String fruit : list ) {
            System.out.println(fruit);
        }

        // 순회 방법 3 : Iterator 사용
        Iterator<String> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            String element = iterator.next();
            System.out.println(element);
        }

        // 순회 방법 4 : ListIterator 사용 (양방향 순회 가능)
        ListIterator<String> stringListIterator = list.listIterator();

        // 정방향
        while (stringListIterator.hasNext()) {
            String element = stringListIterator.next();
            System.out.println(element);
        }
        // 역방향
        while (stringListIterator.hasPrevious()) {
            String element = stringListIterator.previous();
            System.out.println(element);
        }

    }

    // 3. Stack
    // 설명 : LIFO(Last In First Out) 후입선출 구조를 따름
    // Vector 기반 : Stack은 Vector 클래스를 상속받아 구현
    // 주요 메서드 : push() 요소삽입, pop() 요소제거, peek() 맨 위의 요소를 확인, empty() 스택이 비어 있는지를 확인
    public void exam3() {
        Stack<String> stack = new Stack<>();

        // 추가
        stack.push("apple");
        stack.push("banana");

        // POP
        String topElement = stack.pop();
        System.out.println("topElement: " + topElement);

        // PEEK
        String peekElement = stack.peek();
        System.out.println("peekElement: " + peekElement);

        // EMPTY
        boolean isEmpty = stack.empty();
        System.out.println("isEmpty: " + isEmpty);

        // 순회 방법 1 : for 루프 사용
        for ( int i = 0; i < stack.size(); i++ ) {
            System.out.println( stack.get(i) );
        }

        // 순회 방법 2 : 향상된 for 루프 사용
        for (String element : stack) {
            System.out.println("element: " + element);
        }

        // 순회 방법 3 : Iterator 사용
        Iterator<String> iterator = stack.iterator();
        while ( iterator.hasNext() ) {
            String element = iterator.next();
            System.out.println("element: " + element);
        }

        // 순회 방법 4 : ListIterator 사용 (양방향 순회 가능)
        ListIterator<String> stringListIterator = stack.listIterator();

        // 정방향
        while (stringListIterator.hasNext()) {
            String element = stringListIterator.next();
            System.out.println("element: " + element);
        }
        // 역방향
        while (stringListIterator.hasPrevious()) {
            String element = stringListIterator.previous();
            System.out.println("element: " + element);
        }

        // 순회 방법 5 : pop()을 사용한 순회 (스택의 특성 활용)
        while (!stack.isEmpty()) {
            String element = stack.pop();
            System.out.println("element: " + element);
        }
    }

    public static void main(String[] args) {
        A_collections_list test = new A_collections_list();

        test.exam1();
        test.exam2();
        test.exam3();
    }
}
