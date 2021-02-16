import React from 'react';
import moment from 'moment';


const DATE_FORMAT = "MMM Do YY";

export default function DateFormat(props: { children: Date | number[] }) {
  if(props.children instanceof Date) {
    return (<React.Fragment>{moment(props.children).format(DATE_FORMAT)}</React.Fragment>);  
  }
  
  const date = props.children as number[];
  return (<React.Fragment>{`${date[0]}-${date[1]}M-${date[2]}`}</React.Fragment>);
}
