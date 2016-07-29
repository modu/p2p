package p2p;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class UploadHandler extends Thread{
	int sPort; 
	ServerSocket sSocket; //upload handler socket
	Socket connection = null; // socket for the connection with the client
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	int peerID; 
	String inputFileName;
	boolean check = true;

	public UploadHandler(int peerID) {
		this.peerID = peerID;
		try {
			this.inputFileName = getFileName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// create a serversocket
			sSocket = new ServerSocket(Utility.getMyPortNumber(peerID), Constants.BACKLOGCLIENTS);
			// Wait for connection
			System.out.println("Peer waiting for connection request..");
			// accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			try {
				while (check) {
					String message = (String) in.readObject();
					if (message.equalsIgnoreCase(Constants.MESSAGE_CHUNK_LIST_REQUEST)) {
						System.out.println("UPLOAD: Received chunk List request from neighbour");
						String chunkListFileName = System.getProperty("java.class.path")
								+ System.getProperty("file.separator") + "ChunksClient" + peerID
								+ System.getProperty("file.separator") + "summary.txt";
						File chunkListFile = new File(chunkListFileName);
						ArrayList<String> listOfChunksWithPeer = new ArrayList<String>();
						BufferedReader br = new BufferedReader(new FileReader(chunkListFile));
						for (String line; (line = br.readLine()) != null;)
							listOfChunksWithPeer.add(line);
						br.close();
						
						System.out.println("Sending List of Chunks we have ");
						Utility.printArrayListString(listOfChunksWithPeer);
						out.writeObject(listOfChunksWithPeer);
						out.flush();
						String content = (String) in.readObject();
						if (content.equalsIgnoreCase(Constants.MESSAGE_REQUEST_GIVE_CHUNKS)) {
							System.out.println("Received request for Sending Chunks ");
							@SuppressWarnings("unchecked")
							ArrayList<String> listOfChunksWithUNeighbour = (ArrayList<String>) in.readObject();
//							Utility.printArrayListString(listOfChunksWithUNeighbour);
							@SuppressWarnings("unchecked")
							ArrayList<String> listOfChunksToBeUploaded = (ArrayList<String>) in.readObject();
							System.out.println("Uploading following chunks..");
							Utility.printArrayListString(listOfChunksToBeUploaded);
							//System.out.print("Size of ListOfChunks to be uploaded received on uploader is " + listOfChunksToBeUploaded.size() );
							for (int i = 0; i < listOfChunksToBeUploaded.size(); i++) {
								String chunkID = listOfChunksToBeUploaded.get(i);
								out.writeObject(chunkID);
								out.flush();
								sendChunk(inputFileName + ".p" + chunkID, Integer.parseInt(chunkID), peerID);
							}
							out.writeObject(Constants.MESSAGE_NOTIFICATION_UPLOADED_WHAT_I_HAVE);
							out.flush();
							Thread.sleep(1000);
						}
					}
					if (message.equalsIgnoreCase(Constants.MESSAGE_NOTIFICATION_DOWNLOAD_COMPLETE)) {
						System.out.println("UPLOAD: Upload neighbour gets all chunks - Upload complete!");
						check = false;
					}
				}
			} catch (java.io.EOFException e) {
				System.err.println("UPLOAD: Upload neighbour gets all chunks - Upload complete!");
				check = false;
			} catch (ClassNotFoundException classnot) {
				System.err.println("UPLOAD: Data received in unknown format");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				sSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//print list of chunkIDs
	public void printChunkList(ArrayList<String> listOfChunks) {
		System.out.print("UPLOAD: Upload neighbour has chunks:");
		for (String chunkID : listOfChunks)
			System.out.print("\t" + chunkID);
		System.out.println();
	}

	//send each chunk taking filename chunkID and peer ID as parameters
	public void sendChunk(String fileToSend, int chunkID, int peerID) {
		try {
			File file = new File(System.getProperty("java.class.path") + System.getProperty("file.separator")
					+ "ChunksClient" + peerID + System.getProperty("file.separator") + fileToSend);
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			BufferedInputStream bis = new BufferedInputStream(fis);

			try {
				bis.read(bytearray, 0, bytearray.length);
				out.write(bytearray, 0, bytearray.length);
				System.out.println("UPLOAD: Sending chunk ID " + chunkID + " to upload neighbour");
				out.flush();
				bis.close();
				return;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	String getFileName() throws IOException{
		String filePath = System.getProperty("java.class.path") + System.getProperty("file.separator")
		+ "ChunksClient" + peerID +  System.getProperty("file.separator") +"FileNameAndNumberOfChunks.txt";
	    String line = null;
	    BufferedReader br = new BufferedReader(new FileReader(filePath));
	    line = br.readLine();
    	return line;

	}
	
}
