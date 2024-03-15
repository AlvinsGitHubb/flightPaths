import java.util.ArrayList;
import java.util.List;

public class Path1 {

    private List<String> Path1_list;
    private String newnode;

    // constructor
    public Path1() {
        Path1_list = new ArrayList<String>();
    }

    public void setNewNode(String n) {
        this.newnode = n;
    }

    public String getNewNode() {
        return this.newnode;
    }

    public void addNode(String node1) {
        Path1_list.add(node1);
    }

    public void delNode(String node1) {
        Path1_list.remove(node1);

    }

    public Boolean Path1Exists(String node1) {
        if (Path1_list.contains(node1))
            return true;
        else
            return false;
    }

}