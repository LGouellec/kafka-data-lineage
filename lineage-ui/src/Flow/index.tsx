import { Component, Dispatch, ReactNode, SetStateAction, useCallback, useEffect } from 'react';
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
  ReactFlowProps,
  MiniMap,
  Controls,
} from 'reactflow';

import CustomNode from './CustomNode';
import { Client } from '@stomp/stompjs';


// this is important! You need to import the styles from the lib to make it work
import 'reactflow/dist/style.css';

import './Flow.css';

const nodeTypes = {
  custom: CustomNode,
};


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

const SOCKET_URL = 'ws://localhost:8080/ws-message';

interface FlowProps {
  ping: any;
}

class FlowComponent extends Component<FlowProps, {}> {

  private client : Client | undefined;
  public props!: FlowProps;

  constructor(props: FlowProps) {
    super(props);
  }


  componentDidMount() {
    this.client = new Client({
      brokerURL: SOCKET_URL,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: this.onConnected,
      onDisconnect: this.onDisconnected
    });
  
    this.client.activate();
  }

  componentWillUnmount(): void {
      this.client?.deactivate();
  }

  onConnected = () => {
    console.log("Connected!!");
    let p = this.props;
    if(this.client)
      this.client.subscribe('/topic/message', function (msg) {
        console.log(msg);
        p.ping();
      });
  }

  onDisconnected = () => {
    console.log("Disconnected!!")
  }


  render() {
      return <div></div>
  }
}

function Flow () {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  let refresh = () => {
    console.log("refresh");
      fetch('lineage/graph')
      .then<GraphDTO>(response => response.json())
      .then(d => {
        
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

      setNodes(newNodes);
      setEdges(newEdges);
    });
  }

  useEffect(() => {
    refresh();
  }, []);

    return (
      <div className="Flow">
        <FlowComponent ping={refresh}></FlowComponent>
          <ReactFlow
            nodes={nodes}
            onNodesChange={onNodesChange}
            edges={edges}
            onEdgesChange={onEdgesChange}
            fitView
            nodeTypes={nodeTypes}
            snapToGrid={true}
            attributionPosition="top-right">
            <Background variant={BackgroundVariant.Dots} gap={30} size={2} />
            <MiniMap />
            <Controls />
          </ReactFlow>
        </div>
    );
}

export default Flow;