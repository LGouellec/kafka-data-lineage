package data.lineage.processing.mapper;

import data.lineage.processing.dto.EdgeDTO;
import data.lineage.processing.dto.GraphDTO;
import data.lineage.processing.dto.NodeDTO;
import data.lineage.processing.graph.Graph;
import data.lineage.processing.graph.Node;

public class GraphMapper {

    private int x = 0;
    private int y = 0;

    public GraphDTO getGraphDTO(Graph graph) {
        GraphDTO graphDTO = new GraphDTO();
        for (Node firstNode : graph.getFirstNode().getNext()) {
            buildNode(graphDTO, firstNode);
            x = 0;
            y += 500;
        }
        return graphDTO;
    }

    private void buildNode(GraphDTO graphDTO, Node node) {

        NodeDTO dto = new NodeDTO();
        dto.setId(String.valueOf(node.getIndex()));
        dto.setName(node.getName());
        dto.setType(node.getType());
        dto.setX(x);
        dto.setY(y);
        graphDTO.getNodes().add(dto);
        x += 500;

        for (Node nodeChild : node.getNext()) {
            EdgeDTO edge = new EdgeDTO();
            edge.setId("e" + node.getIndex() + "-" + nodeChild.getIndex());
            edge.setSource(String.valueOf(node.getIndex()));
            edge.setTarget(String.valueOf(nodeChild.getIndex()));
            graphDTO.getEdges().add(edge);

            buildNode(graphDTO, nodeChild);
            if(node.getNext().size() > 1)
                y += 500;
        }
    }
}
