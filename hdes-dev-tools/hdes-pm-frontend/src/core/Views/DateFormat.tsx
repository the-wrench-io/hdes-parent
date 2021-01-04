import React from 'react';
import moment from 'moment';


const DATE_FORMAT = "MMM Do YY";

export default function DateFormat(props: { children: Date }) {
  return (<React.Fragment>{moment(props.children).format(DATE_FORMAT)}</React.Fragment>);
}
