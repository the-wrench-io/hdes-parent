import React from 'react';
import { ResourceProvider as Provider, ResourceContext as Context } from './ResourceContext';
import { Backend, Ast, AstMapper }  from './Backend';
import { Session }  from './Session';


const useSession = () => {
  const { session } = React.useContext(Resources.Context);
  return session;
}

const useWorkspace = () => {
  const { session } = React.useContext(Resources.Context);
  const workspace = session.workspace;
  
  if (!workspace) {
    throw new Error("Can't use workspace because workspace is not defined!");
  }
  
  const open = session.history.open;
  const tabData = session.tabs[open];
  const blob = Object.values(workspace.snapshot.blobs).filter(b => b.id === tabData.id)[0];
  
  return { workspace, active: {
    tab: tabData, blob
  }}; 
}

const useContext = () => {
  const result = React.useContext(Resources.Context);
  return result;
}

const Resources = { Provider, Context, useSession, useWorkspace, useContext };

export { Resources, AstMapper, useSession };
export type { Session, Backend, Ast };
