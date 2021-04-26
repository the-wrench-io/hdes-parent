import { ResourceProvider as Provider, ResourceContext as Context } from './ResourceContext';

import { Backend }  from './Backend';
import { Session }  from './Session';
import { ResourceMapper as Resource }  from './ResourceMapper';
import { ResourceBuilderMapper as Builder }  from './ResourceBuilderMapper';

const Mapper = { Resource, Builder };
const Resources = { Provider, Context };

export { Resources, Mapper };

export type { Backend, Session };
