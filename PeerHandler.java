package p2p;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/*This will run once to receive Chunks from server */

public class PeerHandler extends Thread {
	Socket requestSocket; // socket connect to the server
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	String message; // message send to the server
	String[] fileParams;
	Integer peerID;
	public PeerHandler(int _peerID ) {
		peerID = _peerID;
	}

	public void run() {
		try {
			// create a socket to connect to the server
			requestSocket = new Socket("localHost", 8000);
			
			System.out.println("Connected to " + requestSocket.getInetAddress().getHostName() + " in port "
					+ requestSocket.getPort());
			// initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			out.writeObject(peerID);
			out.flush();
			message = (String) in.readObject();
//			System.out.println("*************Message Received from Server: " + message);
			fileParams = message.split("\t");

			/*Creating a new file to store name of file and number of chunks */
			write(fileParams[0],Integer.parseInt(fileParams[2]) );
			
			BufferedWriter bWriter = null;
			try {
				String fileName = System.getProperty("java.class.path")
						+ System.getProperty("file.separator") + "ChunksClient" + peerID
						+ System.getProperty("file.separator") + "summary.txt";
				File chunkListFileDir = new File(fileName);
				chunkListFileDir.getParentFile().mkdir();
				chunkListFileDir.createNewFile();
				bWriter = new BufferedWriter(new FileWriter(chunkListFileDir, true));
				Integer numberOfChunksFromServer = 1;
				System.out.println("Should be value of total number of chunks " + fileParams[2]);
				
				while (numberOfChunksFromServer <= getChunkIDListLocal(peerID, Integer.parseInt(fileParams[2]) ).size() ) {
					String chunkID = (String) in.readObject();
					receiveChunk(fileParams[0] + ".p" + chunkID, peerID);
					bWriter.write(chunkID);
					bWriter.newLine();
					System.out.println(
							"Client " + peerID + " receives" + " chunk " + chunkID + " from the server.");
					numberOfChunksFromServer++;
				}
				String message = (String) in.readObject();
				System.out.println("We should come out here once we receive all the chunks from server the first time " + message );
				//Utility.mergeFile(1, 61, Constants.INPUT_FILE_NAME);
				if(message.equalsIgnoreCase(Constants.MESSAGE_SERVER_UPLOADED_CHUNK_COMPLETE)){
					try {
						bWriter.close();						
						in.close();
						out.close();
						requestSocket.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}					
				}

				
			} catch (EOFException e) {
				bWriter.close();
				System.out.println("EOFException " + e );
//				UploadHandler upload = new UploadHandler(clientNo, fileParams[0]);
//				upload.start();
//				DownloadHandler download = new DownloadHandler(clientNo, fileParams[0],
//						Integer.parseInt(fileParams[2]));
//				download.start();
			}
		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found");
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		/*Starting the Download Handler and Upload Handlers for Peer*/
		
	}

	// receive each chunk given chunk filename and client number
	void receiveChunk(String filePartName, int clientNo) {
		ByteArrayOutputStream bArrOutputStream = new ByteArrayOutputStream();
		byte[] rByte = new byte[1];
		int bytesRead;
		if (in != null) {
			FileOutputStream foStream = null;
			BufferedOutputStream boStream = null;
			try {
				String fileName = System.getProperty("java.class.path") + System.getProperty("file.separator")
						+ "ChunksClient" + clientNo + System.getProperty("file.separator") + filePartName;
				foStream = new FileOutputStream(new File(fileName));
				boStream = new BufferedOutputStream(foStream);
				bytesRead = in.read(rByte, 0, rByte.length);
				do {
					bArrOutputStream.write(rByte);
					bytesRead = in.read(rByte);
				} while (bytesRead != -1);

				boStream.write(bArrOutputStream.toByteArray());
				boStream.flush();
				boStream.close();
			
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	
	}
	
	@SuppressWarnings("resource")
	void write(String fileName , Integer numberOfChunks) throws IOException{

		String filePath = System.getProperty("java.class.path") + System.getProperty("file.separator")
		+ "ChunksClient" + peerID +  System.getProperty("file.separator") +"FileNameAndNumberOfChunks.txt";
		File file = new File(filePath);
		file.delete();
		BufferedWriter bWriter = null;

		File chunkListFileDir = new File(filePath);
		chunkListFileDir.getParentFile().mkdir();
		chunkListFileDir.createNewFile();
		bWriter = new BufferedWriter(new FileWriter(chunkListFileDir, true));
		bWriter.write(fileName);
		bWriter.newLine();
		bWriter.write(numberOfChunks.toString());
		bWriter.newLine();		
		bWriter.close();						

		
	}	
	public static ArrayList<String> getChunkIDListLocal(int peerID, int numberOfChunksG) {
		ArrayList<String> chunkIDList = new ArrayList<String>();
//		System.out.println("Value of number of Chunks is " + numberOfChunksG );
		for(Integer i=peerID; i<=numberOfChunksG; i=i + Constants.NUMBEROFPEERS){
			chunkIDList.add(i.toString());	
		}
//		System.out.println("Size of chunk IDs " + chunkIDList.size());
		return chunkIDList;
	}
};
