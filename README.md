Tic-tac-toe
===========

LAN multiplayer tic-tac-toe client/server in scala using http4s & scala.js

Screenshot:

![doc/client.png](doc/client.png)


Run it!
-------

```
sbt build
java -jar server/target/scala-2.12/server-assembly-x.x.x.jar
```

Open `localhost:8080/` in browser.
Then have your friend (assuming you have friends) open `<your ip>:8080/` in their browser.
If you have no friends, open a private/incognito browser window to play against yourself.

If you've run scoverage, make sure you `sbt coverageOff` and `sbt clean` before you build.


Notes
-----

Game States
- Init
- ReadyPlayer1
- Turn
- GameOver
- Reset

Endpoints:
- GET /
    - sets cookie id (determines player 1 vs player 2) & returns html
- GET /js/file
    - returns static javascript
- GET /img/file
    - returns static png files
- GET /status
    - returns status string
- POST /play/index
    - current player takes her turn; returns status string
- POST /reset
    - when in GameOver state, either player can request a new game
- POST /accept-reset
    - when in reset state; next state is Turn

Status string: 12 characters with format PSSCCCCCCCCC where
- P: Player
    - 1: Player1
    - 2: Player2
    - S: Spectator
- SS: Game state
    - IN: Init
    - R1: Ready player 1
    - T1: Turn player 1
    - T2: Turn player 2
    - G1: GameOver; player 1 wins
    - G2: GameOver; player 2 wins
    - GT: GameOver; it's a tie
    - S1: Reset player 1
    - S2: Reset player 2
- C: Board Cell
    - E: Empty
    - X: player 1 has an X here
    - O: player 2 has an O here

Finite State Machine:

![doc/finite-state-machine.png](doc/finite-state-machine.png)


Develop
-------

Test coverage:

```
    sbt checkTestCovergae
```

Shared code:  prefix normal sbt commands with sharedJVM.

```
    sbt sharedJVM/clean
    sbt sharedJVM/compile
    sbt sharedJVM/test
```

Server:  prefix normal sbt commands with server.

```
    sbt server/clean
    sbt server/compile
    sbt server/test
```

Client:

```
    sbt client/clean
    sbt client/compile
```


TODO
----

- Rewrite client using immutable store architecture
