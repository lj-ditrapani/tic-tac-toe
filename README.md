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
    - R1: Player1Ready
    - R2: Player2Ready
    - T1: Player1Turn
    - T2: Player2Turn
    - G1: GameOver; player 1 wins
    - G2: GameOver; player 2 wins
    - GS: GameOver; it's a tie
- C: Board Cell
    - E: Empty
    - X: player 1 has an X here
    - O: player 2 has an O here


Future
------

Instead of Player; maybe have Client & Player where
- Client is (C1, C2, or Spectator)
- Player is (P1 or P2).
So when you say Player, you mean an active player.
When you say Client, you mean spectator or player.
