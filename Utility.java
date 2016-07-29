package p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utility {
	
	public static int inputFileSize = 0;
	public static Integer numberOfChunksG = 0;
	public static String fileName = "";
	
	public Integer splitFile(String _fileName) throws IOException{
		fileName  = _fileName;
		Integer numberOfChunks = 0;
		try {
			System.out.println("File name : " + _fileName);
			File inputFile = new File(System.getProperty("java.class.path") + System.getProperty("file.separator") + _fileName);
			FileInputStream inputStream;
			String newChunkName;
			FileOutputStream chunkFile;
			int fileSize = (int) inputFile.length();
			inputFileSize = fileSize;
			System.out.println("File size : " + fileSize);
			int read = 0, readLength = Constants.CHUNK_SIZE;
			byte[] byteChunkPart;

			inputStream = new FileInputStream(inputFile);
			while (fileSize > 0) {
				if (fileSize <= Constants.CHUNK_SIZE) {
					readLength = fileSize;
				}
				byteChunkPart = new byte[readLength];
				read = inputStream.read(byteChunkPart, 0, readLength);
				fileSize -= read;
				numberOfChunks++;
				newChunkName = System.getProperty("java.class.path") + System.getProperty("file.separator") +"FileChunks"+System.getProperty("file.separator") + _fileName + ".p"
						+ Integer.toString(numberOfChunks);
				File outputDir = new File(newChunkName);
				outputDir.getParentFile().mkdir();
				outputDir.createNewFile();
				chunkFile = new FileOutputStream(new File(newChunkName));
				chunkFile.write(byteChunkPart);
				chunkFile.flush();
				chunkFile.close();
				byteChunkPart = null;
				chunkFile = null;
			}
			System.out.println("Number of Chunks : " + numberOfChunks);
			inputStream.close();

		} catch (FileNotFoundException ex) {
			throw new FileNotFoundException("'" + _fileName + "' file not found.");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		Utility.numberOfChunksG = numberOfChunks;
		return numberOfChunks;
	}
	// Merge file chunks into a single file
	public static boolean mergeFile(int peerID, int noOfChunks, String fileName) {
		try {
			String outputFilePath = System.getProperty("java.class.path") + System.getProperty("file.separator") + "Client" + peerID + System.getProperty("file.separator") + fileName;
			String chunkFilesPath = System.getProperty("java.class.path") + System.getProperty("file.separator")+ "ChunksClient" + peerID + System.getProperty("file.separator") + fileName;
			File ofile = new File(outputFilePath);
			ofile.getParentFile().mkdir();
			ofile.createNewFile();
			int n = 1;
			FileOutputStream outputStream;
			FileInputStream inputStream;
			byte[] fileBytes;
			List<File> list = new ArrayList<File>();
			while (n >= 1 && n <= noOfChunks) {
				list.add(new File(chunkFilesPath + ".p" + n++));
			}

			outputStream = new FileOutputStream(ofile, true);
			for (File file : list) {
				inputStream = new FileInputStream(file);
				fileBytes = new byte[(int) file.length()];
				inputStream.read(fileBytes, 0, (int) file.length());
				outputStream.write(fileBytes);
				outputStream.flush();
				fileBytes = null;
				inputStream.close();
				inputStream = null;
			}
			outputStream.close();
			outputStream = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		return true;
}

	public Integer getNumberOfChunks(){
		return numberOfChunksG;
	}
	public static void setNumberOfChunks(Integer _numberOfChunks){
		numberOfChunksG = _numberOfChunks;
	}
	
	public File getFileFromName(String fileName) {
		File file = new File(System.getProperty("java.class.path") + fileName);
		return file;
	}
	
	public Integer peerPortID(Integer peerID){
		switch(peerID){
		case 1:
			return Constants.PEER1PORT;
		case 2:
			return Constants.PEER2PORT;
		case 3:
			return Constants.PEER3PORT;
		case 4:
			return Constants.PEER4PORT;
		case 5:
			return Constants.PEER5PORT;
		default:
			return 9000;
		}
		
	}
	
	public static ArrayList<String> getChunkIDList(int peerID) {
		ArrayList<String> chunkIDList = new ArrayList<String>();
		System.out.println("Value of number of Chunks is " + numberOfChunksG );
		for(Integer i=peerID; i<=numberOfChunksG; i=i + Constants.NUMBEROFPEERS){
			chunkIDList.add(i.toString());	
		}
		//System.out.println("Size of chunk IDs " + chunkIDList.size());
		return chunkIDList;
	}
	public ArrayList<String> getChunkIDListFromFile(Integer peerID) throws IOException{
		String chunkListFileName = System.getProperty("java.class.path")
				+ System.getProperty("file.separator") + "ChunksClient" + peerID
				+ System.getProperty("file.separator") + "summary.txt";
		File chunkListFile = new File(chunkListFileName);
		ArrayList<String> listOfChunksWithPeer = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(chunkListFile));
		for (String line; (line = br.readLine()) != null;)
			listOfChunksWithPeer.add(line);
		br.close();
		return listOfChunksWithPeer;
		
	}
	
	public static void printArrayListString(ArrayList<String> arrayListToBePrinted){
		
//		System.out.println("Printing ArrayList ");
		System.out.println();

		for(String name: arrayListToBePrinted){
			System.out.print(name + " ");
		}
		System.out.println();
	}
	public static Integer getNeigbhourPort(Integer myPeerID){
		switch(myPeerID){
		case 1:
			return 9002;
		case 2:
			return 9003;
		case 3:
			return 9004;
		case 4:
			return 9005;
		case 5:
			return 9001;
		
		default:
			return 9001;
		}
		
	}
	public static Integer getMyPortNumber(Integer myPeerID){
		switch(myPeerID){
		case 1:
			return 9001;
		case 2:
			return 9002;
		case 3:
			return 9003;
		case 4:
			return 9004;
		case 5:
			return 9005;
		default:
			System.out.println("Wrong peerID ");
			return 9000;
		}
	}
	
	//to delete all the files in the folder and the folder itself
	public static void deleteChunksFolder(String folderPath) {
		try {
			File folder = new File(folderPath);
			String[] fileNames = folder.list();
			for (String fileName : fileNames) {
				File currentFile = new File(folder.getPath(), fileName);
				currentFile.delete();
			}
			folder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
