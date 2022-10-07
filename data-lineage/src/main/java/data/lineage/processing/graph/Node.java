package data.lineage.processing.graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {

    private final String name;
    private final String type;
    private final List<Node> next;
    //private final List<Node> previous;

    Node(String name, String type){
        this.name = name;
        this.type= type;
        this.next = new ArrayList<>();
        //this.previous = Collections.emptyList();
    }

    Node(String name, String type, List<Node> next){
        this.name = name;
        this.type= type;
        this.next = next;
        //this.previous = Collections.emptyList();
    }

    public boolean containsChild() {
        return this.next.size() > 0;
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
        return new Node(name, type);
    }

    public static Node buildNode(String name, String type, List<Node> next){
        return new Node(name, type, next);
    }
}
