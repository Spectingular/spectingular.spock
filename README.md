#Spectingular.Spock [![Build Status](https://travis-ci.org/Spectingular/spectingular.spock.svg?branch=master)](https://travis-ci.org/Spectingular/spectingular.spock)
*The needs of the many outweigh the needs of the few*

spectingular.spock is a dashboard that provides insight into the state of the spectingular.builder.

### Pre-requisites
1. Java 8
2. MongoDB

### Getting started
To be able to use Spock, a MongoDB instance should be created.
Lets call the instance spock. Complete the following steps to do so.

1. login to mongo. (on the commandline type `mongo`)
2. select the db. `use spock`
3. add user and assign roles 

```json
db.createUser(
  {
    "user": "spock",
    "pwd": "spock",
    "roles": [{"role": "userAdmin", "db": "test-spock"}]
  }
)
```

Use `db.addUser` in mongo &lt; 2.6

### Starting the application
```shell
mvn spring-boot:run
```
