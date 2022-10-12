package data.lineage.processing.graph;


import java.util.ArrayList;
import java.util.List;

public class Node {

    public static String ROOT = "root";

    private final String name;
    private final String type;
    private int index;
    private final List<Node> next;

    Node(String name, String type, int index){
        this.name = name;
        this.type= type;
        this.index = index;
        this.next = new ArrayList<>();
    }

    void setIndex(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<Node> getNext() {
        return next;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && ((Node)obj).name.equals(name);
    }

    public void addNextNode(Node next){
        if(!this.next.contains(next))
            this.next.add(next);
    }

    public static Node buildNode(String name, String type){
        ++Graph.indexGenerate;
        return new Node(name, type, Graph.indexGenerate);
    }

    public void removeNode(int index) {
        next.removeIf(n -> n.getIndex() == index);
    }
}
