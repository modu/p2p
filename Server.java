package p2p;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import p2p.ServerHandler;

public class Server {
public
	int sPort;    //The server will be listening on this port number
	int backLogClients;
	ServerSocket sSocket;   //serversocket used to lisen on port number 8000
	Socket connection = null; //socket for the connection with the client
	String message;    //message received from the client
	String MESSAGE;    //uppercase message send to the client
	ObjectOutputStream out;  //stream write to the socket
	ObjectInputStream in;    //stream read from the socket
	Integer numberOfChunks;
	public void Server() {
		sPort = Constants.SERVERPORT;
		backLogClients = Constants.BACKLOGCLIENTS;
		numberOfChunks = 0;
	}

	void run(String fileName) throws IOException
	{
		ServerSocket listener = new ServerSocket(8000);		
//		System.out.println("The server is running.");
//		Scanner user_input = new Scanner(System.in);
//		/*Split the files */
//		System.out.println("Please input the name of the file to split :");
//		String fileName = user_input.next();
//		Utility fileSplitter = new Utility();
//		fileSplitter.splitFile(fileName);
//		user_input.close();
		int clientNum = 1;		
		try {
			while (clientNum <=5 ) {
				System.out.println("Server waiting ...");
				Socket socket = listener.accept();
				new ServerHandler(socket,fileName, numberOfChunks).start();
				System.out.println("Number of Clients connected to server " + clientNum);
				clientNum++;
			}
		} finally {
			listener.close();
		}	
	}

 	public static void main(String args[]) throws IOException {
		Scanner user_input = new Scanner(System.in);
		/*Split the files */
		System.out.println("Please input the name of the file to split :");
		String fileName = user_input.nextLine();
		user_input.close();
		Server s = new Server();
//		String fileName = Constants.INPUT_FILE_NAME;
//		Constants.setInputFileName(fileName);
		Utility util = new Utility();
		s.numberOfChunks = util.splitFile(fileName);
		s.run(fileName);  
 
    }

}