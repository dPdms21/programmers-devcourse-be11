package part3;

import java.util.*;

// * ArrayList : 배열에 물리적으로 나란히 저장
class MyArrayList {

    private String[] arr = new String[10];
    private int size = 0;

    // * 인덱스 접근
    String get(int index) {
        return arr[index];
    }

    // * 맨 뒤에 추가
    void addLast(String data) {
        // 용량이 꽉찼으면 확장후 삽입
        ensureCapacity();
        arr[size++] = data;
    }

    // * 맨 앞에 추가 : 뒤 요소를 전부 한 칸씩 밀어야 자리가 생김 (느림)
    void addFirst(String data) {
        ensureCapacity();

        for (int i = size; i > 0; i--) {
            arr[i] = arr[i - 1]; // 물리적으로 붙어있으니 통째로 밀기
        }

        arr[0] = data;
        size++;
    }

    // * 길이 반환
    int size() {
        return size;
    }

    // * 자동으로 용량 확장
    private void ensureCapacity() {
        if (size == arr.length) {
            arr = Arrays.copyOf(arr, arr.length * 2);
        }
    }

}

// * LinkedList : 노드가 prev/next 주소를 들고 논리적으로 연결
class MyLinkedList {

    // 노드 한 칸
    static class Node {
        String data;
        Node prev;
        Node next;

        Node(String data) {
            this.data = data;
        }
    }

    private Node head; // 첫 노드
    private Node tail; // 마지막 노드
    private int size;

    // * 맨 뒤에 추가
    void addLast(String data) {
        Node node = new Node(data);
        if (head == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    // * 맨 앞에 추가
    void addFirst(String data) {
        Node node = new Node(data);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    // * index번째 노드 찾기 : head부터 next를 타고 한 칸씩 이동
    private Node nodeAt(int index) {
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur;
    }

    // * index 위치에 삽입 : 그 자리 양옆 노드의 연결만 바꿈
    void insert(int index, String data) {

        if ( index == 0 ) addFirst(data);
        if ( index == size ) addLast(data);

        // 원래 그자리에 있던 노드
        Node next = nodeAt(index);
        // 그 이전의 노드
        Node prev = next.prev;
        // 새로 삽입될 노드
        Node node = new Node(data);

        // 새 노드가 양옆을 가리킴
        node.prev = prev;
        node.next = next;
        // 기존 노드의 양옆도 새 노드를 가리킴
        prev.next = node;
        next.prev = node;

        size++;
    }

    // * 각 노드의 정보를 그 대로 출력하여 연결 상태 확인 출력예시 : [ prev <- data -> next ]
    void printLinks() {
        Node cur = head;
        while ( cur != null ) {
            String p = (cur.prev == null) ? "null" : cur.prev.data;
            String n = (cur.next == null) ? "null" : cur.next.data;
            System.out.println("[" + p + " <- " + cur.data + " -> " + n + "]");
            cur = cur.next;
        }
    }

    String get(int index) {
        return nodeAt(index).data;
    }

}

public class A_collections_list_exercise {
    public static void exam1() {
        System.out.println("=== 직접 만든 ArrayList (배열에 물리적으로 나란히) ===");
        MyArrayList arr = new MyArrayList();
        arr.addLast("가");
        arr.addLast("나");
        arr.addLast("다");
        System.out.println("get(2) = " + arr.get(2)
                + "   <- 인덱스로 주소를 바로 계산해서 한 번에 접근");
        arr.addFirst("앞");  // 내부에서 뒤 요소들을 전부 한 칸씩 밀어야 한다
        System.out.println("addFirst(\"앞\") -> 뒤 요소를 한 칸씩 밀어서 0번 자리를 비웠음");
        System.out.println("결과: 0번=" + arr.get(0) + ", 1번=" + arr.get(1) + ", 2번=" + arr.get(2));
    }

    public static void exam2() {
        System.out.println("=== 직접 만든 LinkedList (노드가 prev/next로 논리적 연결) ===");
        MyLinkedList list = new MyLinkedList();
        list.addLast("가");
        list.addLast("나");
        list.addLast("다");
        System.out.println("addLast 3번 후       : ");
        list.printLinks();   // [null <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]

        list.addFirst("앞");
        System.out.println("addFirst(\"앞\") 후     : ");
        list.printLinks();   // 맨 앞 노드의 연결만 바뀐다 (다른 노드는 그대로)

        list.insert(2, "끼움");
        System.out.println("insert(2, \"끼움\") 후   : ");
        list.printLinks();   // 2번 자리 "양옆 노드의 연결"만 바뀐다

        System.out.println("get(2) = " + list.get(2)
                + "   <- head부터 next를 2번 타고 걸어가서 찾음");
        System.out.println();
    }

    public static void main(String[] args) {
        exam2();
    }
}
