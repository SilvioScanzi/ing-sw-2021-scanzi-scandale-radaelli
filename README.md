# Overview
Masters of the renaissance is a board game for 1 to 4 players published by Cranio Creations, designed by S. Luciani, N. Mangone. 
In this game, every player is an illustrious citizen of Florence, trying to increase his fame and prestige. 
There are a number of ways to achieve this goal, from hiring famous leaders to building extraordinary architectural works.

# Functionalities
The implemented functionalities are:
* Basic Rules
* Complete Rules
* Socket
* CLI
* GUI
* Multiple Games
* Disconnection Resilience

# Authors
Scaglione Cugola - Gruppo GC36
* Silvio Scanzi
* Francesco Scandale
* Marta Radaelli

# Launching the application
The application is contained in a single JAR file, which can be found in the Deliveries section of the repository

## Server
The machine running the server must be reachable from the clients in order to play the game. To start the server, use the command

java -jar GC36.jar -server

The server, when launched, will ask the user which port he wants to open the connection from.
Once launched, the server will print the events log on the standard output.

## Client
To launch the clients there are a few options:
For a basic setup (Graphical interface), just use the command

java -jar GC36.jar -client

from there, you can add a few extra flags:

-cli lets you launch the application via a command line interface

-cli -color lets you launch the command line interface with added depth, but make sure your Shell fully supports ANSI color codes, otherwise you will encounter a few problems during the game

-cli -gui lets you launch the application via a graphic interface (which is strongly recommended to enjoy the game)



