Tic-tac-toe 
===========

tic-tac-toe client/server in scala using http4s & scala.js

Status
------

WIP.  Project structure setup.
Learning http4s/working on server logic.
Client unimplemented.
No tests yet.

```
sbt client/fullOptJS
bash copy.sh
sbt server/assembly
java -jar server/target/scala-2.12/server-assembly-x.x.x.jar
```

Open `localhost:8080/` in browser.

Notes
-----

Game States
- Init
- Player1Ready
- Player2Ready
- Player1Turn
- Player2Turn
- GameOver

Endpoints:
- GET /
    - sets cookie id (determines player 1 vs player 2) & returns html
- GET /js/file
    - returns static javascript
- GET /status
    - returns status string
- POST /play/x/y
    - current player takes her turn; returns status string
- POST /reset
    - when in GameOver state, either player can request a new game

Status string: 12 characters with format PSSCCCCCCCCC where
- P: Player
    - 1: Player1
    - 2: Player2
    - S: Spectator
- SS: Game state
    - IN: Init
    - 1R: Player1Ready
    - 2R: Player2Ready
    - 1T: Player1Turn
    - 2T: Player2Turn
    - GO: GameOver
- C: Board Cell
    - E: Empty
    - X: player 1 played an X here
    - O: player 2 played an O here
