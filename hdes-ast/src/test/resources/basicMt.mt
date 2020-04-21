define manual-task: basic description: 'very descriptive manual task'
headers: {}
dropdowns: {
  gender: { 'f': 'female', 'm': 'male' }
}
actions: {}
form: { 
  fields: {
    firstName STRING required: { },
    age INTEGER required: { },
    gender INTEGER optional: { single dropdown: genderDropdown }
  } 
}
