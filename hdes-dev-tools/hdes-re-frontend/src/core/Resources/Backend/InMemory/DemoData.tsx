import Backend from './../Backend';


const createDemoData = (): { 
  projects: Backend.ProjectResource[],
  heads: Backend.HeadResource[] } => {

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
  
  projects.push({project, heads});
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
  
  projects.push({project, heads});
}


  const heads: Backend.HeadResource[] = [];
  projects.forEach(p => heads.push(...Object.values(p.heads).map(h => createHeadResource(p, h))))

  return { projects, heads }
}

const createHeadResource = (project: Backend.ProjectResource, head: Backend.Head): Backend.HeadResource =>{
  
  const main = project.heads['main'];
  const mainCommits = (main as DemoHead).commits;
  const headCommits = (head as DemoHead).commits;
  const diff = headCommits.length - headCommits.length;

  return { head, state: diff === 0 ? undefined : { commits: Math.abs(diff), type: diff < 0 ? 'ahead' : 'behind'}};
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

const uuid = ():string => {
  return "inmemory-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (char) => {
    let random = Math.random() * 16 | 0;
    let value = char === "x" ? random : (random % 4 + 8);
    return value.toString(16)
  });
}

interface DemoHead extends Backend.Head {
  commits: Backend.Commit[]
}

export default createDemoData;
