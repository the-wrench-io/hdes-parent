define manual-task: SuperNameHere 
description: 'very descriptive manual task'

headers: {
  customers ARRAY of OBJECT required IN: {
    firstName INTEGER optional IN,
    lastName  INTEGER required IN
  },
  avgAge INTEGER required OUT FORMULA: sum(out.age)/out.length
}

dropdowns: {
  gender: { 0: 'female', 1: 'male', 2: 'other' }
}

actions: { 
  when validateAge:     age > 30   then show error:   'you are too old',  // context params: 0 - validateAge, 1 - age, 2 - 30, message - 'you are to old'
  when validateAge:     age > 30   then show message: 'you are too old',
  when otherGender:     gender = 2 then show group:    sub-gender-group,
  when otherMaleGender: gender = 1 then show field:    sub-gender-group
}

form of groups: { 
  cars:  { fields: {} },
  boats: { fields: {} },
  soups: { groups: {} },
  ppl:   { fields: {
    firstName STRING  required: { default-value: 'BOB' class: 'super-style-1 super-style-2' },
    age       INTEGER required: { default-value: 1 },
    gender    INTEGER optional: { single-choice dropdown: genderDropdown }
  }
} from customers