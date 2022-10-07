package data.lineage.processing.graph;

public class Graph {

    private Node firstNode;

    Graph(Node first){
        this.firstNode = first;
    }

    public static Graph buildGraph(Node firstNode){
        return new Graph(firstNode);
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public Node getNode(String name){
        return getRecurseNode(firstNode, name);
    }

    public Node changeFirstNode(Node newFirstNode){
        newFirstNode.addNextNode(firstNode);
        firstNode = newFirstNode;
        return firstNode;
    }

    private Node getRecurseNode(Node node, String name){
        if(node.getName().equals(name))
            return node;

        while(node.containsChild()){
            for(Node n : node.getNext())
            {
                Node tmp = getRecurseNode(n, name);
                if(tmp != null)
                    return tmp;
            }
        }

        return null;
    }
}
