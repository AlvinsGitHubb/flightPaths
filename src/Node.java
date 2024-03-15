import java.util.Comparator;

class Node implements Comparator<Node> {

    public int time2Cost;
    public String node;

    // constructor
    public Node() {
    }

    // constructor with parameters
    public Node(String n, int timeCost) {
        this.node = n;
        this.time2Cost = timeCost;
    }

    // Override compare()
    @Override
    public int compare(Node n1, Node n2) {
        // if node1 costTime < node2 costTime return -1 else return 1
        if (n1.time2Cost < n2.time2Cost)
            return -1;
        else if (n2.time2Cost < n1.time2Cost)
            return 1;

        return 0;
    }

}
