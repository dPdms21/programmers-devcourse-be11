package part3;

// * queue
// 먼저 삽입된 요소가 먼저 제거되는 선입선출(FIFO : First-In-First-Out) 구조를 따르는 자료구조
// Queue 인터페이스는 자바에서 큐의 동작을 정의하며, 다양한 구현체가 있음
// 대표적인 구현체로는 LinkedList, PriorityQueue, ArrayDeque 등이 있음

// * 주요 메서드
//	•	add(E e): 큐의 맨 뒤에 요소를 추가. 큐가 가득 찬 경우 예외를 발생
//	•	offer(E e): 큐의 맨 뒤에 요소를 추가. 큐가 가득 차도 예외를 발생시키지 않고 false를 반환
//	•	remove(): 큐의 맨 앞에 있는 요소를 제거하고 반환. 큐가 비어 있는 경우 예외를 발생
//	•	poll(): 큐의 맨 앞에 있는 요소를 제거하고 반환. 큐가 비어 있으면 null을 반환
//	•	element(): 큐의 맨 앞에 있는 요소를 반환하지만, 제거하지는 않음. 큐가 비어 있는 경우 예외를 발생
//	•	peek(): 큐의 맨 앞에 있는 요소를 반환하지만, 제거하지는 않음. 큐가 비어 있으면 null을 반환

import java.util.*;

public class A_collections_queue {
    // 1. LinkedList
    // Queue 인터페이스를 구현하므로, 큐로 사용할 수 있음
    // LinkedList는 양방향 리스트로, 큐와 리스트로 모두 사용할 수 있음
    public static void exam1() {
        Queue<String> queue = new LinkedList<>();

        // 요소 추가
        queue.add("apple");
        queue.add("banana");
        queue.add("orange");
        queue.add("grape");
        queue.add("watermelon");

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + queue.peek());  // 출력: apple

        // 큐에서 요소 제거 및 반환
        System.out.println("Poll: " + queue.poll());  // 출력: apple

        // 큐에서 요소 제거 및 반환
        System.out.println("Poll: " + queue.poll());  // 출력: banana

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + queue.peek());  // 출력: orange

        // 큐의 크기 확인
        System.out.println("Queue size: " + queue.size());  // 출력: 3

        // 큐 비우기
        queue.clear();
        System.out.println("Queue is empty: " + queue.isEmpty());  // 출력: true

        // 순회 방법 1: 향상된 for 루프 사용
        for (String element : queue) {
            System.out.println("Element: " + element);
        }

        // 순회 방법 2: Iterator 사용
        Iterator<String> iterator = queue.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            System.out.println("Element: " + element);
        }
    }

    // 2. PriorityQueue
    // 우선순위 큐로, 요소가 자연 순서(기본 순서)나 제공된 비교기(Comparator)에 따라 정렬
    // FIFO 순서가 아니라 우선순위 따라 요소가 처리
    public static void exam2() {
        Queue<Integer> priorityQueue = new PriorityQueue<>();

        // 요소 추가
        priorityQueue.add(50);
        priorityQueue.add(20);
        priorityQueue.add(40);
        priorityQueue.add(10);

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + priorityQueue.peek());  // 출력: 10

        // 큐에서 요소 제거 및 반환 (우선순위가 높은 요소부터)
        System.out.println("Poll: " + priorityQueue.poll());  // 출력: 10
        System.out.println("Poll: " + priorityQueue.poll());  // 출력: 20

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + priorityQueue.peek());  // 출력: 40

        // 큐의 크기 확인
        System.out.println("Queue size: " + priorityQueue.size());  // 출력: 2

        // 큐 비우기
        priorityQueue.clear();
        System.out.println("Queue is empty: " + priorityQueue.isEmpty());  // 출력: true

        // 순회 방법 1: 향상된 for 루프 사용
        for (Integer element : priorityQueue) {
            System.out.println("Element: " + element);
        }

        // 순회 방법 2: Iterator 사용
        Iterator<Integer> iterator = priorityQueue.iterator();
        while (iterator.hasNext()) {
            Integer element = iterator.next();
            System.out.println("Element: " + element);
        }

        // 순회 방법 3: forEach 메서드와 람다 표현식 사용 (Java 8 이상)
        priorityQueue.forEach(element -> {
            System.out.println("Element: " + element);
        });
    }

    public static void exam2_2() {
        // 나이를 기준으로 오름차순 정렬
        //  Queue<A_person> queue = new PriorityQueue<>(Comparator.comparingInt(A_person::getAge));

        Comparator<A_person> ageComparator = new Comparator<A_person>() {
            @Override
            public int compare(A_person p1, A_person p2) {
                // - 음수 값 : p1이 p2보다 작을 때
                // - 0 : p1과 p2가 같을 때
                // - 양수 값 : p1이 p2보다 클 때
                return Integer.compare(p1.getAge(), p2.getAge());
            }
        };

        Queue<A_person> priorityQueue = new PriorityQueue<>(ageComparator);

        // 요소 추가
        priorityQueue.add(new A_person("Alice", 30));
        priorityQueue.add(new A_person("Bob", 25));
        priorityQueue.add(new A_person("Charlie", 35));
        priorityQueue.add(new A_person("Dave", 20));


        // 요소를 하나씩 꺼내면서 출력
        while (!priorityQueue.isEmpty()) {
            System.out.println("Element: " + priorityQueue.poll());
        }

    }

    public static void exam2_3() {
        // 이름을 기준으로 오름차순 정렬
        // Queue<A_person> queue = new PriorityQueue<>(Comparator.comparing(A_person::getName));

        Comparator<A_person> nameComparator = new Comparator<A_person>() {
            @Override
            public int compare(A_person p1, A_person p2) {
                return p1.getName().compareTo(p2.getName());
            }
        };

        Queue<A_person> priorityQueue = new PriorityQueue<>(nameComparator);

        // 요소 추가
        priorityQueue.add(new A_person("Alice", 30));
        priorityQueue.add(new A_person("Bob", 25));
        priorityQueue.add(new A_person("Charlie", 35));
        priorityQueue.add(new A_person("Dave", 20));

        // 우선순위에 따라 요소를 하나씩 처리 (이름의 오름차순 정렬된 순서로)
        while (!priorityQueue.isEmpty()) {
            System.out.println("Poll: " + priorityQueue.poll());
        }

    }

    public static void exam2_4() {
        // 이름을 내림차순으로 정렬하는 Comparator -> 익명클래스로 구현
        Comparator<A_person> nameComparator = new Comparator<A_person>() {
            @Override
            public int compare(A_person p1, A_person p2) {
                return p2.getName().compareTo(p1.getName());
            }
        };
    }

    // 3. ArrayDeque
    // ArrayDeque : 양방향 큐로 사용할 수 있음
    public static void exam3() {
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();

        // 요소 추가
        arrayDeque.add("Apple");
        arrayDeque.add("Banana");
        arrayDeque.add("Orange");

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + arrayDeque.peek());  // 출력: Apple

        // 큐에서 요소 제거 및 반환
        System.out.println("Poll: " + arrayDeque.poll());  // 출력: Apple

        // 큐에서 요소 제거 및 반환
        System.out.println("Poll: " + arrayDeque.poll());  // 출력: Banana

        // 큐의 맨 앞 요소 확인 (제거하지 않음)
        System.out.println("Peek: " + arrayDeque.peek());  // 출력: Orange

        // 큐의 크기 확인
        System.out.println("Queue size: " + arrayDeque.size());  // 출력: 1

        // 큐 비우기
        arrayDeque.clear();
        System.out.println("Queue is empty: " + arrayDeque.isEmpty());  // 출력: true
    }

    public static void main(String[] args) {
        exam3();
    }
}

class A_person {
    String name;
    int age;

    public A_person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return name + "(" + age + ")";
    }
}