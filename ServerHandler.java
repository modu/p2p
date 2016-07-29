package p2p;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import p2p.*;
/**
 * A handler thread class for server. Handlers are spawned from the listening loop and
 * are responsible for dealing with a single client's requests.
 */
public class ServerHandler extends Thread {

	private String message; // message received from the client
	private Socket connection;
	private ObjectInputStream in; // stream read from the socket
	private ObjectOutputStream out; // stream write to the socket
	private int peerID; // The index number of the client
	Utility util;
	public String fileName;
	public Integer numberOfChunks;
	public ServerHandler(Socket connection, String _fileName, Integer _numberOfChunks) {
		this.connection = connection;
		this.peerID = 0;
		System.out.println("Constructor of ServerHandler");
		util = new Utility();
		fileName = _fileName;
		numberOfChunks = _numberOfChunks;
	}

	public void run() {
		try {
			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			peerID = (Integer) in.readObject();
			System.out.println("Server Got request from Client ID  " + peerID );
			//TODO: See if it is needed now to send all these things :)
			sendMessage(fileName + "\t" + Constants.INPUT_FILE_SIZE + "\t"
					+ numberOfChunks + "\t" + peerID);
			try {
				sendChunks();
				out.writeObject(Constants.MESSAGE_SERVER_UPLOADED_CHUNK_COMPLETE);
				try {
					in.close();
					out.close();
					connection.close();
				} catch (IOException ioException) {
					System.out.println("Disconnect with Client " + peerID);
				}				
//				if (no == Constants.NUMBEROFPEERS)
//				{
//					//GeneralUtility.deleteChunksFolder(System.getProperty("java.class.path")+ System.getProperty("file.separator") + "FileChunks");
//					in.close();
//					out.close();
//					connection.close();
//					System.out.println("File upload complete!");
//				}
			} catch (Exception e) {
				System.err.println("Error sending chunks to Client " + peerID);
			}
		} catch (IOException ioException) {
			System.out.println("Client disconnected ID: " + peerID);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				connection.close();
			} catch (IOException ioException) {
				System.out.println("Client disconnected ID: " + peerID);
			}
		}
	}

	// to send all the chunks
	public void sendChunks() {
		try {
			System.out.println("Sending Chunks to client " + peerID);
			ArrayList<String> chunkIDList = Utility.getChunkIDList(peerID);
			for (String chunkID : chunkIDList) {
				//System.out.println("Sending chunk .... in "+ chunkID);
				String fileToSend = fileName + ".p" + chunkID;
				sendChunk(fileToSend, chunkID);
			}
			out.writeObject(Constants.MESSAGE_SERVER_UPLOADED_CHUNK_COMPLETE);
		} catch (Exception e) {
			System.err.println("Error sending chunks from server to client");
			e.printStackTrace();
		}
	}

	// sends each chunk file taking filename and chunkID as parameters
	public void sendChunk(String fileToSend, String chunkID) {
		try {
			File file = new File(System.getProperty("java.class.path") + "/FileChunks/" + fileToSend);
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			BufferedInputStream bis = new BufferedInputStream(fis);

			try {
				out.writeObject(chunkID);
				out.flush();
				bis.read(bytearray, 0, bytearray.length);
				out.write(bytearray, 0, bytearray.length);
				System.out.println("Server sending chunk ID " + chunkID + " to Client " + peerID);
				out.flush();
				fis.close();
				bis.close();
				//file.delete();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		
		//send a message to the output stream
		void sendMessage(String msg)
		{
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}		
		
	}

