Test client request over static https server
============================================

To generate self-signed ssl keys

    openssl req -new -x509 -keyout key.pem -out cert.pem -days 365 -nodes

Run static https server

    sbt build
    cd tic-tac-toe/server/src/main/resources
    python ../../../../https_test/https_server.py
