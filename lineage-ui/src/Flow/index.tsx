import { useCallback, useEffect } from 'react';
import ReactFlow, {
  Node,
  useNodesState,
  useEdgesState,
  addEdge,
  Connection,
  Edge,
  MarkerType,
  Position,
  Background,
  BackgroundVariant,
} from 'reactflow';

import CustomNode from './CustomNode';

// this is important! You need to import the styles from the lib to make it work
import 'reactflow/dist/style.css';

import './Flow.css';
import internal from 'stream';

const nodeTypes = {
  custom: CustomNode,
};

const initialNodes: Node[] = [
  {
    id: '1',
    type: 'input',
    data: { label: 'Node 1' },
    position: { x: 250, y: 5 },
  },
  {
    id: '2',
    data: { label: 'Node 2' },
    position: { x: 100, y: 100 },
  },
  {
    id: '3',
    data: { label: 'Node 3' },
    position: { x: 400, y: 100 },
  },
  {
    id: '4',
    data: { label: 'Node 4' },
    position: { x: 400, y: 200 },
    type: 'custom',
  },
];

const initialEdges: Edge[] = [
  { id: 'e1-2', source: '1', target: '2', animated: true },
  { id: 'e1-3', source: '1', target: '3', animated: true },
  { id: 'e3-4', source: '3', target: '4', animated: true },
];

interface GraphDTO {
  nodes: NodeDTO[],
  edges: EdgeDTO[]
}

interface EdgeDTO {
  id: string,
  source: string,
  target: string
}

interface NodeDTO{
  id: string,
  type: string,
  name: string,
  x: number,
  y: number
}

function Flow() {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const onConnect = useCallback(
    (params: Connection | Edge) => setEdges((eds) => addEdge(params, eds)),
    [setEdges]
  );

  useEffect(() => {
    fetch('lineage/graph')
      .then<GraphDTO>(response => response.json())
      .then(d => {
        console.log(d);
        var newNodes = d.nodes.map(n => {
            return {
              id: n.id,
              type: 'custom',
              data: { 
                label: n.name,
                type: n.type
              },
              position: { x: n.x, y: n.y },
              sourcePosition: Position.Right,
              targetPosition: Position.Left
          }
        });
        
       var newEdges = d.edges.map(e => {
          return {
            id: e.id,
            source: e.source,
            target: e.target,
            animated: true,
            markerEnd: {
              type: MarkerType.Arrow
            }
          }
        });
      
      console.log(newNodes);
      console.log(newEdges);

      setNodes(newNodes);
      setEdges(newEdges);
    });
  }, []);

  return (
    <div className="Flow">
      <ReactFlow
        nodes={nodes}
        onNodesChange={onNodesChange}
        edges={edges}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        fitView
        nodeTypes={nodeTypes}
      >
        <Background variant={BackgroundVariant.Dots} gap={30} size={2} />
      </ReactFlow>
    </div>
  );
}

export default Flow;