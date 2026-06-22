package dfs;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph(9);

        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(2, 6);
        graph.addEdge(3, 7);
        graph.addEdge(4, 5);
        graph.addEdge(4, 7);
        graph.addEdge(4, 8);
        graph.addEdge(5, 6);
        graph.addEdge(7, 8);
        graph.addEdge(8, 9);

        graph.dfs(1); // 3,5

        graph.dfs2(1);

        System.out.println();
        System.out.println("\n1에서 9까지 이동 가능: " + graph.hasPath(1, 9));

        System.out.println("\n사이클 존재 여부: " + graph.hasCycle());

        System.out.println("\n연결 요소 개수: " + graph.cnt());
    }
}
