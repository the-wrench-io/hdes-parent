
// create mongo user via mongo shell
db.createUser(
  {
    user: "appUser",
    pwd: "123456",
    roles: [
       { role: "readWrite", db: "projects" },
    ]
  }
)


// start mongo and mongo express
docker-compose -f src/main/resources/docker/stack.yml up -d

// quarkus app
mvn clean compile quarkus:dev