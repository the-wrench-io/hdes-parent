import { Session } from '../../Resources';

declare namespace LinksApi {
  
  interface Link {
    id: string;
    label: string; 
    icon: React.ReactNode;
    badge?: {color: string, text: string}
    enabled?: boolean, 
    onClick: () => React.ReactNode | void;
  }
  
  interface Active {
    id?: string;
    view?: React.ReactNode;
  }
  
  interface Links {
    values: Link[];
    handle: (value: Link, active: Active) => void;
    color: (value: Link, active: Active) => "primary" | "inherit";
    find: (id: string) => Link | undefined;
  }
}


class ImmutableLinks implements LinksApi.Links {
  private _instance: Session.Instance
  private _actions: Session.Actions;
  private _values: LinksApi.Link[];
  
  constructor(props: {
    session: Session.Instance, 
    actions: Session.Actions,
    values: LinksApi.Link[]}) {
    
    this._instance = props.session;
    this._actions = props.actions;
    this._values = props.values;
  }
  
  handle(link: LinksApi.Link, active: LinksApi.Active) {
    const alreadyOpen = this._instance.linkId === active.id;    
    if(alreadyOpen && !active.view) {
      const viewInTab = this._instance.findTab(link.id);
      if(viewInTab === undefined) {
        this._actions.handleLink();
      }
    }  
    this._actions.handleLink(link.id); 
  }
  color(link: LinksApi.Link, active: LinksApi.Active) {
    return active.id === link.id && active.view ? "primary" : "inherit";
  }
  find(id: string) {
    const result = this._values.filter(v => v.id === id);
    if(result.length === 1) {
      return result[0];
    }
    return undefined;
  }
  get values(): LinksApi.Link[] {
    return this._values;  
  }
}

export { ImmutableLinks }
export type { LinksApi }