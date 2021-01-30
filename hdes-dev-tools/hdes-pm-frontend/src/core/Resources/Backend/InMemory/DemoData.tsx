import Backend from './../Backend';


const createDemoData = () => {
  const users: Backend.User[] = [
      {id: "1", rev: "1r6", name: "bob",         email: "sarg@city",      created: new Date("2019-01-01"), token: "sdkfhksdfh"},
      {id: "2", rev: "2r6", name: "sam",         email: "guard@city",     created: new Date("2019-01-01"), token: "sdfjkxcghjgf"},
      {id: "3", rev: "3r6", name: "goblin",      email: "",               created: new Date("2019-01-01"), token: "vxmcnbghrf"},
      {id: "4", rev: "4r6", name: "bridgetroll", email: "guards@bridges", created: new Date("2019-01-01"), token: "cjlkjhyuk"},
  ];
  const projects: Backend.Project[] = [
    {id: "5", rev: "1xb", name: "scoring project 1", created: new Date("2010-01-01")},
    {id: "6", rev: "2xb", name: "risk project 2",    created: new Date("2010-01-01")},
    {id: "7", rev: "3xb", name: "risk project 31",   created: new Date("2010-01-01")},
    {id: "8", rev: "4xb", name: "risk project 9",    created: new Date("2010-01-01")},
    {id: "9", rev: "5xb", name: "risk project 10",   created: new Date("2010-01-01")},
  ];
  const groups: Backend.Group[] = [
    {id: "10", rev: "1yb", name: "humans",   created: new Date("2010-01-01")},
    {id: "11", rev: "2yb", name: "monsters", created: new Date("2010-01-01")},  
  ];
  const groupUsers: Backend.GroupUser[] = [
    {id: "12", rev: "1ky", userId: "1", groupId: "10", created: new Date("2010-01-01")},
    {id: "14", rev: "2ky", userId: "2", groupId: "10", created: new Date("2010-01-01")},
    
    {id: "15", rev: "3ky", userId: "3", groupId: "11", created: new Date("2010-01-01")},
    {id: "16", rev: "4ky", userId: "4", groupId: "11", created: new Date("2010-01-01")},
  ];
  const access: Backend.Access[] = [
    {id: "001", rev: "1ywt", projectId: "5", groupId: "10", name: "", created: new Date("2010-01-01")},
    {id: "002", rev: "2ywt", projectId: "6", groupId: "10", name: "", created: new Date("2010-01-01")},
    {id: "003", rev: "3ywt", projectId: "7", groupId: "10", name: "", created: new Date("2010-01-01")},
    {id: "004", rev: "4ywt", projectId: "8", groupId: "10", name: "", created: new Date("2010-01-01")},
    {id: "005", rev: "5ywt", projectId: "9", groupId: "10", name: "", created: new Date("2010-01-01")},
    
    {id: "006", rev: "6ywt", projectId: "5", groupId: "11", name: "", created: new Date("2010-01-01")},
    {id: "007", rev: "7ywt", projectId: "8", groupId: "11", name: "", created: new Date("2010-01-01")},
    {id: "008", rev: "8ywt", projectId: "9", groupId: "11", name: "", created: new Date("2010-01-01")},
  ];
  
  return {users, projects, groups, groupUsers, access}
}

export default createDemoData;
