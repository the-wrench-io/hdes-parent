const uuid = ():string => {
  return "inmemory-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (char) => {
    let random = Math.random() * 16 | 0;
    let value = char === "x" ? random : (random % 4 + 8);
    return value.toString(16)
  });
}

export default uuid;