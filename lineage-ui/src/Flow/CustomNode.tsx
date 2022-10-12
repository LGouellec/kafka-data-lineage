import { memo, FC, CSSProperties } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';

const sourceHandleStyleA: CSSProperties = { left: 50 };
const sourceHandleStyleB: CSSProperties = {
  right: 50,
  left: 'auto',
};

const CustomNode: FC<NodeProps> = ({ data }) => {
  let nodeType;
  if(data.type == 'connector')
    nodeType = 
    <div>
      <img src="icon-connector.svg"/>
      <div style={{color: "#F54F2B"}}>Connector</div>
    </div>
  else if(data.type =='topic')
    nodeType = 
    <div>
      <img src="icon-topic.svg"/>
      <div style={{color: "#F77EF7"}}>Topic</div>
    </div>
  else if(data.type == 'consumer')
    nodeType = 
    <div>
      <img src="icon-consumer.svg"/>
      <div style={{color: "#F8D768"}}>Consumer</div>
    </div>
  else if(data.type == 'producer')
    nodeType = 
    <div>
      <img src="icon-producer.svg"/>
      <div style={{color: "#7BCD55"}}>Producer</div>
    </div>
  else if(data.type == 'kstreams')
    nodeType = 
    <div>
      <img src="icon-streams.svg"/>
      <div style={{color: "#7EF7DF"}}>Kafka Streams</div>
    </div>
  else if(data.type == 'ksql-query')
    nodeType = 
    <div>
      <img src="icon-ksqldb.svg"/>
      <div style={{color: "#7EA5F7"}}>ksqlDB query </div>
    </div>

  return (
    <div className="gradient-div">
      <Handle type="target" position={Position.Left} isConnectable={false} />
      {nodeType}
      <div style={{marginLeft: "10px"}}>{data.label}</div>
      <Handle
        type="source"
        position={Position.Right}
        isConnectable={false}
      />
    </div>
  );
};

export default memo(CustomNode);