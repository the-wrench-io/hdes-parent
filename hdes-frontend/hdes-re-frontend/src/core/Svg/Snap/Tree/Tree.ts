declare namespace Tree {
  
  type DimensionsTester = (props: {
    text: Typography;
    limits: { max: Dimensions, min: Dimensions };
  }) => DimensionsTested;
  
  interface DimensionsTested {
    dimensions: Dimensions; // final space to be reserved
    avg: { width: number, height: number }
  }
  
  interface Listeners {
    onClick: (node: Node, event: MouseEvent) => void;
  }
  
  interface Theme {
    stroke: string;
    fill: string;
    background: string;
  }

  interface Coordinates {
    x: number;  y: number;
  }
  
  interface Dimensions {
    height: number, width: number
  }
  
  interface Connectors {
    dimensions: Dimensions;
    center: Coordinates;
    left: Coordinates; right: Coordinates;
    top: Coordinates; bottom: Coordinates;
  }
  
  interface Associations {
    incoming: string[];
    outgoing: string[];
  }
  
  interface Shape<T extends Node> {
    id: string; 
    node: T; 
    dimensions: Dimensions; 
    connectors: Connectors;
    associations: Associations;
  }
  
  interface Order {
    value: number;
  }
  
  interface Node {
    id: string;
    type: NodeType;
    typography: Typography;
    order: Order;
  }
  
  type NodeType = 
    "graph"     | "start" | "end" |
    "switch"    | 
    "decision"  | "decision-loop" |
    "service"   | "service-loop"  |
    "grid" | 
    "row" | "cell" | "header" | "headerrow";
  
  interface Typography {
    header?: string;
    desc?: string;
    text?: string;
    src?: string;
    icon?: string;
  }
  
  interface Shapes extends Node {
    nodes: Record<string, Node>;
    shapes: Record<string, Shape<Node>>;
  }
  
  interface TreeFactory {
    graph(): GraphBuilder;
    grid(): GridBuilder;
  }
  
  interface GraphBuilder {
    start     (id: string, init: InitStart): GraphBuilder;
    end       (id: string, init: InitEnd): GraphBuilder;
    switch    (id: string, init: InitSwitch): GraphBuilder;
    decision  (id: string, init: InitDecision): GraphBuilder;
    service   (id: string, init: InitService): GraphBuilder;
    build(): GraphShapes;
  }
  
  interface InitStart {
    typography?: Typography, next: string; 
  }
  interface InitEnd {
    typography?: Typography 
  }  
  interface InitSwitch {
    typography?: Typography, 
    next: {id: string, typography?: Typography}[]; 
  }  
  interface InitDecision {
    typography?: Typography, next: string, loop?: string 
  }  
  interface InitService {
    typography?: Typography, next: string, loop?: string, async?: boolean
  }
  
  interface GridBuilder {
    row   (id: string, init: InitRow): GridBuilder;
    cell  (id: string, init: InitCell): GridBuilder;
    header(id: string, init: InitHeader): GridBuilder;
    build(): GridShapes;
  }
  interface InitHeader {
    typography: Typography;
    order: number;
    kind: GridHeaderKind;
  }  
  interface InitRow {
    typography: Typography;
    order: number;
  }
  interface InitCell {
    typography: Typography;
    rowId: string;
    headerId: string
  }
  
  
      
  interface GridNodeVisitor<R, C> {
    visit(props: {
      dimensions:  Record<string, Tree.DimensionsTested>;
      headers: Record<string, Tree.GridHeader>; 
      cells: Record<string, Tree.GridCell>; 
      rows: Record<string, Tree.GridRow>;
    }): R;
    visitNode     (node: Node, context: VisitorContext): C;
    visitTypography(node: Typography, context: VisitorContext): C;
    visitHeader   (node: GridHeader, context: VisitorContext): C;
    visitHeaders  (nodes: GridHeader[], context: VisitorContext): C;
    visitRow      (node: GridRow, context: VisitorContext): C;
    visitRows     (nodes: GridRow[], context: VisitorContext): C;
    visitCell     (node: GridCell, context: VisitorContext): C;
    visitCells    (nodes: GridCell[], context: VisitorContext): C;
  }
  interface GridShapeVisitor<R, C>{
    visitShapes   (node: GridShapes, listeners: Listeners): R;
    visitTypography(node: Tree.Shape<Tree.Node>, context: VisitorContext): C; 
    visitHeader   (node: Shape<GridHeader>, context: VisitorContext): C;
    visitHeaders  (nodes: Shape<GridHeaderRow>, context: VisitorContext): C;
    visitRow      (node: Shape<GridRow>, context: VisitorContext): C;
    visitRows     (nodes: readonly Shape<GridRow>[], context: VisitorContext): C;
    visitCell     (node: Shape<GridCell>, context: VisitorContext): C;
    visitCells    (nodes: readonly Shape<GridCell>[], context: VisitorContext): C;
    visitNode     (node: Shape<Node>, context: VisitorContext): C;     
  }
  
  interface GraphNodeVisitor<R, C> {
    visit           (startId: string, node: Record<string, Node>): R;
    visitNode       (node: Node, context: VisitorContext): C;
    visitStart      (node: GraphStart, context: VisitorContext): C;
    visitEnd        (node: GraphEnd, context: VisitorContext): C;
    visitSwitch     (node: GraphSwitch, context: VisitorContext): C;
    visitDecision   (node: GraphDecision, context: VisitorContext): C;
    visitService    (node: GraphService, context: VisitorContext): C;
    visitLoop       (node: GraphDecision | GraphService, context: VisitorContext): C;
  }  
  interface GraphShapeVisitor<R, C> {
    visitShapes     (node: GraphShapes, listeners: Listeners): R;
    visitNode       (node: Shape<Node>, context: VisitorContext): C;
    visitTypography (node: Typography, context: VisitorContext): C;
    visitStart      (node: Shape<GraphStart>, context: VisitorContext): C;
    visitEnd        (node: Shape<GraphEnd>, context: VisitorContext): C;
    visitSwitch     (node: Shape<GraphSwitch>, context: VisitorContext): C;
    visitDecision   (node: Shape<GraphDecision>, context: VisitorContext): C;
    visitService    (node: Shape<GraphService>, context: VisitorContext): C;
    visitLoop       (node: Shape<GraphDecision | GraphService>, context: VisitorContext): C;
  }
  
  interface VisitorIndex {
    value: number; 
    total: number;
    previous?: Node;
  }
  
  interface VisitorContext {
    parent: VisitorContext;
    index: VisitorIndex;
    type: "nullNode" | NodeType;
    
    connectors: Connectors;
    node: Node;
    all: Record<string, Node>;
    root: VisitorContext;
            
    addNode(node: Node, connectors: Connectors, index?: VisitorIndex): VisitorContext; 
    getNode(type: NodeType): VisitorContext;
  }
  
  
  interface GridShapes extends Shapes {
    headersRow: Shape<GridHeaderRow>;
    headers:    Shape<GridHeader>[];
    rows:       Shape<GridRow>[];
    cells:      Shape<GridCell>[];
    getById(id: string): Shape<Node>;
  }
  
  interface GridHeaderRow extends Node {
    cells: readonly GridHeader[];
  }
  interface GridRow extends Node {
    cells: readonly GridCell[];
    getByHeader(headerId: string): GridCell;
  }
  interface GridCell extends Node {
    headerId: string;
    rowId: string;
  }
  interface GridHeader extends Node {
    kind: GridHeaderKind;
  }
  type GridHeaderKind = "IN" | "OUT";
  
  interface GridPaths {
    append(node: Tree.GridCell | Tree.GridHeader): GridPaths;
    row(id: string): GridRowPath;
    header(): GridRowPath;
  }
  interface GridRowPath {
    value: GridCellPath;
    children: readonly GridCellPath[];
  }
  interface GridCellPath {
    dimensions: Tree.Dimensions;
    coords: Tree.Coordinates;
    path: string;
  }
  
  interface GraphShapes extends Shapes {
    start: GraphStart;
    end: GraphEnd;
    getById: (id: string) => Shape<Node>;
  }

  interface GraphStart extends Node {
    children: GraphChild;
  }
  interface GraphEnd extends Node {
  }
  interface GraphSwitch extends Node {
    children: readonly GraphChild[];
  }
  interface GraphDecision extends Node {
    loop?: GraphChild;
    children: GraphChild;
  }
  interface GraphService extends Node {
    async?: boolean;
    loop?: GraphChild;
    children: GraphChild;
  }
  interface GraphChild {
    id: string;
    typography: Tree.Typography,
  }
}

export type { Tree };

