package p2p;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class DownloadHandler extends Thread {
	Socket requestSocket; // socket connect to the server
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	int peerID;
	boolean notConnected = true;
	String inputFileName;
	int totalChunks;
	boolean check = true;
	ArrayList<String> listOfChunksWithPeer;
	Utility util;
	static String nameOfFile;
	public DownloadHandler(int peerID) {
		this.peerID = peerID;
		try {
			this.inputFileName =  getFileName();
			this.totalChunks = getNumberOfChunks();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listOfChunksWithPeer = Utility.getChunkIDList(peerID);
		util = new Utility();
	}

	public void run() {
		while (notConnected) {
			try {
				
				
//				System.out.println("Starting the Download Handler for peer " + peerID);
//				System.out.println("Read from file The FILename ***************  " +getFileName());
//				System.out.println("Read from file The SizeOf Chunks ***************  " + getNumberOfChunks() );

				requestSocket = new Socket( "localHost", Utility.getNeigbhourPort(peerID));
				System.out.println("Connected to " + requestSocket.getInetAddress().getHostName() + " in port "
						+ requestSocket.getPort());
				notConnected = false;
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				out.flush();
				in = new ObjectInputStream(requestSocket.getInputStream());
				while (check) {
					// Reading list of chunks with the peer
					ArrayList<String> listOfChunksWeHave = util.getChunkIDListFromFile(peerID);
					// Requesting chunk list from the download neighbor
					System.out.println("DOWNLOAD: " + Constants.MESSAGE_CHUNK_LIST_REQUEST);
					out.writeObject(Constants.MESSAGE_CHUNK_LIST_REQUEST);
					out.flush();

					// extracting the chunks which are not with the peer
					// from the chunks with the download neighbour
					@SuppressWarnings("unchecked")
					ArrayList<String> listOfChunksFromTheNeighour = (ArrayList<String>) in.readObject();
					System.out.println("Received a Chunk List ");
					Utility.printArrayListString(listOfChunksFromTheNeighour);
					
					if(listOfChunksFromTheNeighour.size()==0){
						System.out.println("Sleep for one second before making another request");
						Thread.sleep(1000);
						continue;
					}
					
					ArrayList<String> listOfChunksToBeDownloadedFromTheNeighour = whatChunksToDownload(listOfChunksWeHave,listOfChunksFromTheNeighour);
//					Utility.printArrayListString(listOfChunksWeHave);
//					Utility.printArrayListString(listOfChunksFromTheNeighour);
//					Utility.printArrayListString(listOfChunksToBeDownloadedFromTheNeighour);

					// Requesting for chunks
					System.out.println("Sending Request to get Chunk List from Neighbour ");

					out.writeObject(Constants.MESSAGE_REQUEST_GIVE_CHUNKS);
					out.flush();
					System.out.println("Sending below Chunk List to Neighbour ");
					Utility.printArrayListString(listOfChunksWeHave);
					out.writeObject(listOfChunksWeHave);
					out.flush();
					/*TODO:If we have new chunks to request then only request */
					out.writeObject(listOfChunksToBeDownloadedFromTheNeighour);
					out.flush();
//					Utility.printArrayListString(listOfChunksWeHave);
//					Utility.printArrayListString(listOfChunksToBeDownloadedFromTheNeighour);

					String chunkListFileName = System.getProperty("java.class.path")
							+ System.getProperty("file.separator") + "ChunksClient" + peerID
							+ System.getProperty("file.separator") + "summary.txt";

					// Receiving chunks
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File(chunkListFileName), true));
//					System.out.println("Size of the chunks to be recevied " + listOfChunksToBeDownloadedFromTheNeighour.size());
					
					for (int i = 0; i < listOfChunksToBeDownloadedFromTheNeighour.size(); i++) {
						String chunkID = (String) in.readObject();
						receiveChunk(inputFileName + ".p" + chunkID, peerID);
						bw.append(chunkID);
						listOfChunksWithPeer.add(chunkID);
						bw.newLine();
						bw.flush();
						System.out.println("DOWNLOAD: Received chunk ID " + chunkID + " from the neighbour");
					}
					bw.close();
//					Scanner reader = new Scanner(System.in);  // Reading from System.in
//					System.out.println("Enter a number: ");
//					int n = reader.nextInt(); // Scans the next token of the input as an int.

					// Check for download complete
					if (downloadCompleteCheck() ) {
						check = false;
						Utility.mergeFile(peerID, totalChunks, inputFileName);
						System.out.println(" ");
						System.out.println("****************DOWNLOAD: All chunks received. Download complete!******** "+ peerID);
						System.out.println(" ");

						Thread.sleep(2000);
						out.writeObject(Constants.MESSAGE_NOTIFICATION_DOWNLOAD_COMPLETE);
						out.flush();
						if (((String) in.readObject()).equalsIgnoreCase(Constants.MESSAGE_NOTIFICATION_UPLOADED_WHAT_I_HAVE)) {
							in.close();
							out.close();
							requestSocket.close();
							String folderPath = System.getProperty("java.class.path") + System.getProperty("file.separator")
									+ "ChunksClient" + peerID;
//							Utility.deleteChunksFolder(folderPath);
						}
					} else if (((String) in.readObject())
							.equalsIgnoreCase(Constants.MESSAGE_NOTIFICATION_UPLOADED_WHAT_I_HAVE))
						Thread.sleep(1000);
				}
			} catch (ConnectException e) {
				System.out.println("Cannot connect to download neighbour. Retry again after 2 sec");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (UnknownHostException unknownHost) {
				System.err.println("Trying to connect to an unknown host!");
				try {
					in.close();
					out.close();
					requestSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException ioException) {
				try {
					in.close();
					out.close();
					requestSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ioException.printStackTrace();
			} catch (ClassNotFoundException e) {
				try {
					in.close();
					out.close();
					requestSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				try {
					in.close();
					out.close();
					requestSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	ArrayList<String> whatChunksToDownload(ArrayList<String> listOfchunksWeHave,  ArrayList<String> listOfChunksFromTheNeighour){
		ArrayList<String> diffOfList = new ArrayList<String>();
		for(String chunkID : listOfChunksFromTheNeighour){
			if(!listOfchunksWeHave.contains(chunkID) ){
				diffOfList.add(chunkID);
			}
		}
		//diffOfList.removeAll(listOfchunksWeHave);
		return diffOfList;		
	}

	
	String getFileName() throws IOException{
		String filePath = System.getProperty("java.class.path") + System.getProperty("file.separator")
		+ "ChunksClient" + peerID +  System.getProperty("file.separator") +"FileNameAndNumberOfChunks.txt";
	    String line = null;
	    BufferedReader br = new BufferedReader(new FileReader(filePath));
	    line = br.readLine();
    	return line;

	}
	
	Integer getNumberOfChunks() throws NumberFormatException, IOException{
		String filePath = System.getProperty("java.class.path") + System.getProperty("file.separator")
		+ "ChunksClient" + peerID +  System.getProperty("file.separator") +"FileNameAndNumberOfChunks.txt";
	     String line = null;
	    Integer numberOfChunks = 0;
	    BufferedReader br = new BufferedReader(new FileReader(filePath));
	    br.readLine();
	    numberOfChunks =  Integer.parseInt(br.readLine());
    	
	    return numberOfChunks;

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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	Boolean downloadCompleteCheck(){
		String folderName = System.getProperty("java.class.path")
				+ System.getProperty("file.separator") + "ChunksClient" + peerID
				+ System.getProperty("file.separator");
		if(new File(folderName).listFiles().length >= (totalChunks+2)){
			return true;
		}
		return false;
	}
}
