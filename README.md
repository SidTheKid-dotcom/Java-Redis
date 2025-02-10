# Java-Redis: A Mini Redis Implementation in Java  

This repository contains a **custom Redis-like in-memory key-value store** implemented in Java as a **DSA Lab Mini Project**. It supports **multi-client Pub/Sub messaging** and **thread-based concurrency** while running in a terminal environment.  

---

## Features  

**In-Memory Key-Value Store** 
“ Supports basic SET/GET operations  
**Pub/Sub Messaging**
“ Clients can publish and subscribe to messages  
**Multi-Client Support**
“ Handles multiple concurrent clients via threading  
**Dockerized**
“ Can be run in a containerized environment  
**Terminal-Based Interaction**
“ Clients can connect using `telnet`  

---

## How to Run  

### 1) Clone the Repository  
```sh
git clone https://github.com/SidTheKid-dotcom/Java-Redis.git
cd Java-Redis
```

### 2) Compile the Server  
```sh
javac src/MulticlientServer.java
```

### 3) Run the Server  
```sh
java src.MulticlientServer
```

### 4) Connect Clients  
Open multiple terminal windows and run:  
```sh
telnet localhost 8100
```
Now, you can send commands and interact with the in-memory database.

---

## Commands and Examples  

### 1) **SET Key-Value Pair**  
Stores a value for a given key.  
#### **Usage:**  
```sh
SET name Pikachu
```
#### **Response:**  
```sh
OK
```

### 2) **GET Value by Key**  
Retrieves the value stored for a given key.  
#### **Usage:**  
```sh
GET name
```
#### **Response:**  
```sh
Pikachu
```

### 3) **SUBSCRIBE to a Channel**  
Listens for messages published to a channel.  
#### **Usage:**  
```sh
SUB pokemon_news
```
#### **Response:**  
```sh
Subscribed to pokemon_news
```

### 4) **PUBLISH Message to a Channel**  
Sends a message to all clients subscribed to a specific channel.  
#### **Usage:**  
```sh
PUB pokemon_news "A new Pokemon game is coming!"
```
#### **Response for Subscribers:**  
```sh
Message from pokemon_news: A new Pokemon game is coming!
```

---

## Running with Docker  
To run inside a Docker container:  
```sh
docker build -t java-redis .
docker run -p 8100:8100 java-redis
```
Now connect with `telnet localhost 8100` from your host machine.

---

## Notes  
- Ensure port **8100** is available on your machine.  
- Multiple clients can **subscribe** and **publish** messages in real time.  

### Future Enhancements  
- Persistent data storage  
- More Redis-like commands  
- Optimized concurrency with thread pools  

---

**Happy Coding!**