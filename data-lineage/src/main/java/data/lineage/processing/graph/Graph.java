package data.lineage.processing.graph;

public class Graph {

    private Node firstNode;
    public static int indexGenerate = 0;

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

    public void addGraphNode(Node parent, Node child){
        if(child.getIndex() < parent.getIndex()){
            firstNode.removeNode(child.getIndex());
            parent.addNextNode(child);
            indexGenerate = 1;
            recalculateIndex(firstNode);
        }
        else {
            parent.addNextNode(child);
        }
    }

    private void recalculateIndex(Node node) {
        node.setIndex(indexGenerate);
        for(Node n : node.getNext()){
            ++indexGenerate;
            recalculateIndex(n);
        }
    }

    private Node getRecurseNode(Node node, String name){
        if(node.getName().equals(name))
            return node;

        for(Node n : node.getNext()) {
            Node tmp = getRecurseNode(n, name);
            if(tmp != null)
                return tmp;
        }

        return null;
    }
}
