import Backend from '../Backend';
import uuid from './inmemuuid';
import * as DemoSnapshot from './DemoSnapshotData.json';

const createDemoData = (): { 
  projects: Backend.ProjectResource[],
  heads: Backend.HeadResource[],
  snapshots: Backend.SnapshotResource[] } => {

  const projects: Backend.ProjectResource[] = [];
  
// project 1
{
  const project: Backend.Project = { id: uuid(), name: "Risk project 1" }
  
  const master = createHead("main", 10);
  const dev = createHead("dev", 15);
  const test1 = createHead("test1", 10);
  const test5 = createHead("test5", 15);
  
  const heads: Record<string, Backend.Head> = {}
  heads[master.name] = master;
  heads[dev.name] = dev;
  heads[test1.name] = test1;
  heads[test5.name] = test5;
    
  projects.push({project, heads, states: {}});
}


// project 2
{
  const project: Backend.Project = { id: uuid(), name: "Scoring project" }
  
  const master = createHead("main", 10);
  const dev = createHead("dev", 15);
  const test1 = createHead("test8", 15);
  const test5 = createHead("test3", 10);
  
  const heads: Record<string, Backend.Head> = {}
  heads[master.name] = master;
  heads[dev.name] = dev;
  heads[test1.name] = test1;
  heads[test5.name] = test5;
  
  projects.push({project, heads, states: {}});
}

  const heads: Backend.HeadResource[] = [];
  projects.forEach(p => heads.push(...Object.values(p.heads).map(h => createHeadResource(p, h))))

  const snapshots: Backend.SnapshotResource[] = [];
  for(const head of heads) {
    const snapshot: Backend.SnapshotResource = Object.assign({}, (DemoSnapshot as unknown as { default: any}).default);
    snapshot.head = head.head;
    snapshot.project = projects.filter(p => Object.values(p.heads).filter(h => h.id === head.head.id).length > -1)[0].project;
    snapshots.push(snapshot);
  }
  
  return { projects, heads, snapshots}
}

const createHeadResource = (project: Backend.ProjectResource, head: Backend.Head): Backend.HeadResource =>{
  return { head };
}

const createHead = (name: string, commitCount: number) => {
  const commits: Backend.Commit[] = [];

  const start = new Date();
  start.setDate(start.getDate()-commitCount - 10);
    
  let commit: Backend.Commit;
  for(let index = 0; index < commitCount; index++) {
    const dateTime = new Date();
    dateTime.setDate(start.getDate()+index);
    
    commit = { id: uuid(), author: "Sam Vimes", dateTime: dateTime };
    commits.push(commit);
  }
  
  const lastCommit = commits[commits.length - 1];
  const head: DemoHead = { id: uuid(), name, commit: lastCommit, commits };
  
  return head;
}


interface DemoHead extends Backend.Head {
  commits: Backend.Commit[]
}

export type {DemoHead}
export default createDemoData;
