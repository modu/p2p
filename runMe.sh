#!/bin/bash 

function compile(){
	javac -cp . p2p/Server.java
	javac -cp . p2p/Peer1.java
	javac -cp . p2p/Peer2.java
	javac -cp . p2p/Peer3.java
	javac -cp . p2p/Peer4.java
	javac -cp . p2p/Peer5.java	
}

function clearAll(){
	find p2p/ -name '*.class' -delete
	rm -r ChunksClient1
	rm -r ChunksClient2
	rm -r ChunksClient3
	rm -r ChunksClient4
	rm -r ChunksClient5

	rm -r Client1
	rm -r Client2
	rm -r Client3
	rm -r Client4
	rm -r Client5
	rm -r FileChunks
}


clearAll
#compile


