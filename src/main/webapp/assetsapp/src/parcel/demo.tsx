import * as React from 'react';
import * as ReactDOM from 'react-dom';

import { Hello } from '../app/components/Hello';

const myName = {
  firstName: 'Parcel',
  lastName: 'changed, what? can not change file name.'
};

document.getElementById(
  'parcel'
).innerHTML = `Hello there, ${myName.firstName} ${myName.lastName}`;

ReactDOM.render(
  <Hello compiler="parcel" framework="wow!!!" />,
  document.getElementById('parcel_react')
);
