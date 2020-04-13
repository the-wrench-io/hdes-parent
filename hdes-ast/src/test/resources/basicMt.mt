id: basic description: 'very descriptive manual task'
inputs: {}
statements: {}
dropdowns: {
  gender: { 'f': 'female', 'm': 'male' }
}
form: { 
  fields: {
    required STRING firstName: { },
    required INTEGER age: { },
    required INTEGER gender: { single dropdown: genderDropdown }
  } 
}
