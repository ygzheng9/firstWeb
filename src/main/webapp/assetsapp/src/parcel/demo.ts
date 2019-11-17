const myName = {
  firstName: 'Parcel',
  lastName: 'changed, what? can not change file name.'
};

document.getElementById(
  'parcel'
).innerHTML = `Hello there, ${myName.firstName} ${myName.lastName}`;
