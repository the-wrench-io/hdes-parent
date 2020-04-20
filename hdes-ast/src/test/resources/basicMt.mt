id: basic description: 'very descriptive manual task'
inputs: {}
dropdowns: {
  gender: { 'f': 'female', 'm': 'male' }
}
statements: {}
form: { 
  fields: {
    firstName STRING required: { },
    age INTEGER required: { },
    gender INTEGER optional: { single dropdown: genderDropdown }
  } 
}
