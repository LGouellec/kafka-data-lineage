package data.lineage.processing.dto;

import java.util.ArrayList;
import java.util.List;

public class GraphDTO {

    private List<NodeDTO> nodes;
    private List<EdgeDTO> edges;

    public GraphDTO(){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public List<NodeDTO> getNodes() {
        return nodes;
    }

    public List<EdgeDTO> getEdges() {
        return edges;
    }
}
