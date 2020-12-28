import Backend from './Backend';
import { GenericUserBuilder } from './ResourceBuilders';


const store = {
  users: [
    {id: "1", rev: "1xb", name: "bob", externalId: "1@aliasworks", created: new Date("2019-01-16"), projects: [], groups: []},
    {id: "2", rev: "2v6", name: "same", externalId: "", created: new Date("2019-01-16"), projects: [], groups: []},
    {id: "3", rev: "7rf", name: "goblin", externalId: "", created: new Date("2019-01-16"), projects: [], groups: []},
    {id: "4", rev: "5rt", name: "bridgetroll", externalId: "guard@bridges", created: new Date("2019-01-16"), projects: [], groups: []},
  ]
}


class InMemoryUserQuery implements Backend.UserQuery {
  onSuccess(handle: (users: Backend.User[]) => void) {
    handle(store.users as Backend.User[]);
  }
}

class InMemoryUserService implements Backend.UserService {
  query() {
    return new InMemoryUserQuery();
  }
  builder() {
    return new GenericUserBuilder();
  }
}


class InMemoryService implements Backend.Service {
  users: Backend.UserService;
  
  constructor() {
    this.users = new InMemoryUserService();
  }
}

export default InMemoryService;
