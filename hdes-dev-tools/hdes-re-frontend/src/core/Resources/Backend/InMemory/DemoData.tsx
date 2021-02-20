import Backend from './../Backend';


const createDemoData = (): { projects: Backend.ProjectResource[] } => {

  const projects: Backend.ProjectResource[] = [];
  
// project 1
{
  const project: Backend.Project = { id: uuid(), name: "Risk project 1" }
  
  const master = createHead("main", 10);
  const dev = createHead("dev", 15);
  const test1 = createHead("test1", 15);
  const test5 = createHead("test5", 15);
  
  const heads: Record<string, Backend.Head> = {}
  heads[master.id] = master;
  heads[dev.id] = dev;
  heads[test1.id] = test1;
  heads[test5.id] = test5;
  
  projects.push({project, heads});
}


// project 2
{
  const project: Backend.Project = { id: uuid(), name: "Scoring project" }
  
  const master = createHead("main", 10);
  const dev = createHead("dev", 15);
  const test1 = createHead("test8", 15);
  const test5 = createHead("test3", 15);
  
  const heads: Record<string, Backend.Head> = {}
  heads[master.id] = master;
  heads[dev.id] = dev;
  heads[test1.id] = test1;
  heads[test5.id] = test5;
  
  projects.push({project, heads});
}

  return { projects }
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
  const head: Backend.Head = { id: uuid(), name, commit: lastCommit };
  
  return head;
}

const uuid = ():string => {
  return "inmemory-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (char) => {
    let random = Math.random() * 16 | 0;
    let value = char === "x" ? random : (random % 4 + 8);
    return value.toString(16)
  });
}

export default createDemoData;