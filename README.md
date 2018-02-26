Tic-tac-toe 
===========

tic-tac-toe client/server in scala using http4s & scala.js

Status
------

WIP.  Project structure setup.
Learning http4s/working on server logic.
Client unimplemented

```
sbt client/fullOptJS
bash copy.sh
sbt server/assembly
java -jar server/target/scala-2.12/server-assembly-x.x.x.jar
```

Open `localhost:8080/` in browser.
