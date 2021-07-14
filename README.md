# tftp
My attempt at implementing the TFTP protocol in Java.  
Click [here](https://datatracker.ietf.org/doc/html/rfc1350 "TFTP specification") to view the TFTP protocol specification

# Project Objective
The objective of this project was to mainly practice my Java programming skills and get more familiar with network programming.

# Project Structure
This project consists of 3 modules - `client`, `server`, and `shared`. I use JetBrains IntelliJ IDEA for my development.

## Client
The `client` module is an implementation of a TFTP client. Users can use the `rrq` (read-request) or `wrq` (write-request) commands to read/write files from/to the TFTP server.

## Server
The `server` module is an implementation of a TFTP server. It supports file reading/writing, as well as error handling.

## Shared
This module contains code used across both the client and server modules, such as packet types, constants, etc...