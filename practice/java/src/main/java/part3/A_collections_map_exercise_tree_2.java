package part3;

/*
 * MyTreeMap : 이진 탐색 트리(BST)로 만든 간단한 Map(키-값 저장소)
 *
 * 핵심 규칙(BST 불변식): 어느 노드를 보든
 *   - 왼쪽 서브트리의 키 < 자기 키 < 오른쪽 서브트리의 키
 *   이 규칙이 항상 지켜짐. 그래서 "작으면 왼쪽, 크면 오른쪽"으로만
 *   내려가면 원하는 키를 빠르게 찾을 수 있음 (평균 O(log n))
 *
 * HashMap과의 차이:
 *   - HashMap은 빠르지만 순서가 없음
 *   - TreeMap은 키가 항상 "정렬된 상태"로 유지 (중위순회하면 오름차순)
 *
 * 키 비교는 String.compareTo로 함
 *   a.compareTo(b) < 0  -> a가 사전순으로 앞(작다)
 *   a.compareTo(b) == 0 -> 같다
 *   a.compareTo(b) > 0  -> a가 사전순으로 뒤(크다)
 */

class MyTreeMap {
    // 트리를 이루는 하나의 칸(노드). 키/값과 양쪽 자식으로의 연결을 가짐
    static class Node {
        String key;
        Integer value;
        Node left;   // 나보다 작은 키들이 매달리는 쪽
        Node right;  // 나보다 큰 키들이 매달리는 쪽

        Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    public Node root;       // 트리의 시작점(뿌리). 비어있으면 null
    private int size = 0;    // 저장된 노드 개수

    // 저장 : 작으면 왼쪽, 크면 오른쪽, 같으면 값만 갱신
    public void put(String key, int value) {
        // 재귀 결과를 root에 다시 대입. 새 노드가 생기면 바뀐 트리가 돌아옴
        root = putNode(root, key, value);
    }

    // node를 뿌리로 하는 서브트리에 (key,value)를 넣고, 갱신된 서브트리의 뿌리를 반환
    private Node putNode(Node node, String key, Integer value) {
        // 내려오다 빈자리(null)를 만남 -> 여기가 새 노드가 들어갈 자리
        if ( node == null ) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);

        if ( cmp < 0 ) {
            // 넣을 키가 더 작음 -> 왼쪽 서브트리로 내려가서 넣고, 그 결과를 다시 매담
            node.left = putNode(node.left, key, value);
        }
        else if ( cmp > 0 ) {
            // 넣을 키가 더 큼 -> 오른쪽 서브트리로 내려감
            node.right = putNode(node.right, key, value);
        }
        else {
            // 키가 이미 존재 -> 새로 만들지 않고 값만 덮어씀 (size 안 늘림)
            node.value = value;
        }

        // 자식 연결이 갱신됐을 수 있으니 자기 자신을 부모에게 돌려줌
        return node;
    }

    // 조회 : 키에 해당하는 값을 찾아 반환. 없으면 null
    public Integer get(String key) {
        Node node = root;

        // 뿌리부터 시작해 비교 결과대로 한쪽으로만 내려감 (반쪽씩 후보가 줄어듬)
        while ( node != null ) {
            int cmp = key.compareTo(node.key);

            if ( cmp < 0 ) {
                node = node.left;       // 찾는 키가 작다 -> 왼쪽으로
            }
            else if ( cmp > 0 ) {
                node = node.right;      // 찾는 키가 크다 -> 오른쪽으로
            }
            else {
                return node.value;      // 같다 -> 찾았다
            }
        }

        return null;    // null까지 내려옴 = 트리에 없음
    }

    // 키 존재 여부 체크 : get과 똑같이 내려가되 값 대신 있고/없음만 알려줌
    public boolean containsKey(String key) {
        Node node = root;

        while ( node != null ) {
            int cmp = key.compareTo(node.key);

            if ( cmp < 0 ) {
                node = node.left;
            }
            else if ( cmp > 0 ) {
                node = node.right;
            }
            else {
                return true;            // 같은 키를 만남 -> 존재
            }
        }

        return false;                   // 끝까지 못 만남 -> 없음
    }

    // 가장 작은 키 : 왼쪽으로만 끝까지 내려가면 나옴 (왼쪽일수록 작으니까)
    public String firstKey() {
        if ( root == null ) {
            return null;
        }

        Node node = root;

        while ( node.left != null ) {
            node = node.left;
        }

        return node.key;
    }

    // 가장 큰 키 : 오른쪽으로만 끝까지 내려가면 나옴 (오른쪽일수록 크니까)
    public String lastKey() {
        if ( root == null ) {
            return null;
        }

        Node node = root;

        while ( node.right != null ) {
            node = node.right;
        }

        return node.key;
    }

    // 출력 : 중위순회(inorder) -> 키가 항상 오름차순으로 출력
    //   왼쪽(작은값) -> 자기자신 -> 오른쪽(큰값) 순서로 찍기 때문에 저절로 정렬
    public void printSorted() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        // 기저 조건 : null이면 더 갈 곳이 없으니 멈추고 되돌아감
        if ( node == null ) return;

        inOrder(node.left);                                        // ① 왼쪽 서브트리 먼저
        System.out.print("[" + node.key + "=" + node.value + "] ");// ② 그 다음 자기자신
        inOrder(node.right);                                       // ③ 마지막에 오른쪽 서브트리
    }

    // 삭제 : 키를 지우고 그 값을 반환 없으면 null.
    public Integer remove(String key) {
        Integer old = get(key);

        if (old == null) {
            return null;   // 애초에 없으면 손대지 않고 끝
        }

        root = removeNode(root, key);   // 실제 삭제는 재귀에 맡기고 결과를 root에 재연결
        size--;

        return old;
    }

    // node 서브트리에서 key를 지우고, 갱신된 서브트리의 뿌리를 반환
    private Node removeNode(Node node, String key) {
        if ( node == null ) {
            return null;    // 못 찾고 끝까지 옴 -> 바뀔 것 없음
        }

        int cmp = key.compareTo(node.key);

        if ( cmp < 0 ) {
            // 지울 키가 작음 -> 왼쪽에서 지우고 결과를 다시 매담
            node.left = removeNode(node.left, key);
        }
        else if ( cmp > 0 ) {
            // 지울 키가 큼 -> 오른쪽에서 지움
            node.right = removeNode(node.right, key);
        }
        else {
            // 같은 키를 찾음 -> 자식 개수에 따라 경우를 나눔

            // [경우1] 왼쪽 자식이 없음 -> 오른쪽 자식을 그대로 끌어올려 내 자리에 둠
            //         (오른쪽도 없으면 node.right가 null이라 자연히 "그냥 삭제")
            if ( node.left == null ) {
                return node.right;
            }

            // [경우2] 오른쪽 자식이 없음 -> 왼쪽 자식을 끌어올림
            if ( node.right == null ) {
                return node.left;
            }

            /*
             * [경우3] 자식이 둘 다 있음
             *   그냥 지우면 자식 둘을 어디에 붙일지 곤란
             *   그래서 "지우는 대신, 다른 값으로 갈아끼우는" 트릭을 씀
             *
             *   후계자(successor) = 오른쪽 서브트리에서 가장 작은 키.
             *   (= 나보다 큰 값들 중 가장 작은 값. 내 자리에 와도 BST 규칙이 안 깨짐)
             *   오른쪽으로 한 칸 간 뒤, 왼쪽으로만 끝까지 내려가면 나옴
             *
             *   예) m을 지우면 -> 오른쪽(t)에서 왼쪽 끝(p)이 후계자.
             *               [m=1]
             *               /      \
             *            [f=2]     [t=3]
             *                \      /    \
             *                [h=5] [p=6] [z=7]   <- p가 후계자
             */

            // 1) 오른쪽 서브트리의 최솟값(후계자) 찾기
            Node succ = node.right;

            while ( succ.left != null ) {
                succ = succ.left;
            }

            // 2) 후계자의 키/값을 현재 노드에 복사 (= 값만 갈아끼움)
            node.key = succ.key;
            node.value = succ.value;

            // 3) 원본 후계자는 오른쪽 서브트리에서 삭제
            //    후계자는 왼쪽 자식이 없으므로 [경우1]로 깔끔히 제거
            //    (참조 succ를 직접 끊을 수 없어서, 부모 연결을 고칠 줄 아는 재귀에 맡김)
            node.right = removeNode(node.right, succ.key);
        }

        return node;    // 값만 바뀌었을 수 있으니 자기 자신을 부모에게 돌려줌
    }
}

public class A_collections_map_exercise_tree_2 {
    public static void main(String[] args) {
        MyTreeMap map = new MyTreeMap();

        /*
         * 아래 순서로 put하면 트리가 이렇게 만들어짐 (들어온 순서대로 자리를 잡음)
         *
         *   put 순서: b -> m -> f -> t -> h -> p -> z
         *
         *              [b=4]              <- 맨 처음 b가 뿌리(root)
         *                  \
         *                  [m=1]          b보다 큼 -> 오른쪽
         *                  /     \
         *              [f=2]     [t=3]    f: b<f<m(오->왼) / t: m<t(오)
         *                  \      /   \
         *              [h=5] [p=6] [z=7]  h: f<h<m / p: m<p<t / z: t<z
         *
         *   각 키가 자리를 찾는 경로 (-> 오른쪽, <- 왼쪽):
         *     b  : (첫 노드, root)
         *     m  : b->                (b의 오른쪽)
         *     f  : b-> m<-            (m의 왼쪽)
         *     t  : b-> m->            (m의 오른쪽)
         *     h  : b-> m<- f->        (f의 오른쪽)
         *     p  : b-> m-> t<-        (t의 왼쪽)
         *     z  : b-> m-> t->        (t의 오른쪽)
         *
         *   printSorted()는 중위순회라 입력 순서와 상관없이 항상 정렬되어 나옴:
         *     [b=4] [f=2] [h=5] [m=1] [p=6] [t=3] [z=7]
         */

        map.put("b", 4);
        map.put("m", 1);
        map.put("f", 2);
        map.put("t", 3);
        map.put("h", 5);
        map.put("p", 6);
        map.put("z", 7);

        map.printSorted();

        // 조회
        System.out.println("\nget(p)      = " + map.get("p"));         // 6
        System.out.println("get(x)      = " + map.get("x"));          // null
        System.out.println("contains t? = " + map.containsKey("t"));  // true
        System.out.println("contains x? = " + map.containsKey("x"));  // false
        System.out.println(map.firstKey() + " -> " + map.lastKey());
        System.out.println(map.root.key + " = " + map.root.value);

        // 삭제
        map.remove("m");
    }
}
