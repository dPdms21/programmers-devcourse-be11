package part3;

/*
 * 해시(hash)란?
 *  - key(문자열)를 정수로 변환한 값. 같은 key는 항상 같은 해시값이 나옴
 *  - 이 해시값으로 "데이터를 배열의 몇 번째 칸(버킷)에 넣을지"를 정함
 *    예) "Hello" → hashCode() → 69609650 → % 16 → 버킷 2번
 *  - 덕분에 처음부터 끝까지 훑지 않고 곧장 해당 버킷으로 가서
 *    저장/조회를 빠르게(평균 O(1)) 할 수 있음
 *  - 단점: 서로 다른 key가 같은 버킷으로 갈 수 있음 (충돌)
 *    이 코드는 충돌 시 Node.next로 연결해(연결리스트) 같은 버킷에 함께 담음
 */

class MyHashMap {

    static class Node {
        String key;
        Integer value;
        Node next; // 충돌 시 다음 노드로 연결

        Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] buckets;
    private int capacity = 16;
    private int size = 0;

    public MyHashMap() {
        buckets = new Node[capacity];
    }

    // 해시 함수: key를 버킷 배열의 인덱스(0 ~ capacity-1)로 변환
    // 배열 크기가 capacity(16)인데 hashCode()는 아주 큰 값이 나올 수 있으니,
    // % capacity로 배열 범위(0~15) 안으로 눌러 담는 것이 이 함수의 본질
    // "어느 칸에 담을지 정하는 주소 계산기"
    private int getIndex(String key) {
        // key.hashCode()      : 문자열로부터 정수 해시값을 계산 (음수가 나올 수도 있음)
        // Math.abs(...)       : 해시값을 양수로 변환 (음수 인덱스 방지)
        // % capacity          : 버킷 배열 크기로 나눈 나머지 → 0 ~ capacity-1 범위로 매핑
        System.out.println(key.hashCode());
        return Math.abs(key.hashCode()) % capacity;
    }

    // 저장
    public void put(String key, int value) {
        int idx = getIndex(key);
        Node head = buckets[idx];

        for ( Node n = head; n != null; n = n.next ) {
            // 이미 있는 키 -> 값만 갱신
            if ( n.key.equals(key) ) {
                n.value = value;
                return;
            }
        }

        // 없으면 맨 앞에 추가 (체이닝)
        Node node = new Node(key, value);
        node.next = head;
        buckets[idx] = node;
        size++;
    }

    // 가져오기
    public Integer get(String key) {
        int idx = getIndex(key);

        for ( Node n = buckets[idx]; n != null; n = n.next ) {
            if ( n.key.equals(key) ) {
                return n.value;
            }
        }

        return null;
    }

    // 키 존재 여부
    public boolean containsKey(String key) {
        int idx = getIndex(key);

        for ( Node n = buckets[idx]; n != null; n = n.next ) {
            if ( n.key.equals(key) ) {
                return true;
            }
        }

        return false;
    }

    // 삭제 : 리스트에서 노드를 떼어낸다. 없으면 null
    public Integer remove(String key) {
        int idx = getIndex(key);
        Node node = buckets[idx];
        Node prev = null;

        while ( node != null ) {
            if ( node.key.equals(key) ) {
                if ( prev == null ) {
                    buckets[idx] = node.next;
                }
                else {
                    prev.next = node.next;
                }

                size--;
                return node.value;
            }

            prev = node;
            node = node.next;
        }

        return null;
    }

    public int size() {
        return size;
    }

    // 출력
    public void printBuckets() {
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                StringBuilder sb = new StringBuilder("buckets[" + i + "] : ");

                for (Node n = buckets[i]; n != null; n = n.next) {
                    sb.append("[").append(n.key).append("=").append(n.value).append("]");
                    if (n.next != null) sb.append(" -> ");
                }

                System.out.println(sb);
            }
        }
    }
}

public class A_collections_map_exercise_hash {
    public static void main(String[] args) {
        // 6. 충돌/체이닝 확인: 키 20개를 16칸에 넣으면 반드시 충돌이 생김
        System.out.println("\n--- 버킷 분포 (-> 가 있으면 체이닝된 것) ---");
        MyHashMap m2 = new MyHashMap();

        String[] words = {"a","b","c","d","e","f","g","h","i","j",
                "k","l","m","n","o","p","q","r","s","t"};

        for (int i = 0; i < words.length; i++) {
            m2.put(words[i], i);
        }

        m2.printBuckets();
    }
}
