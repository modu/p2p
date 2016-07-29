package p2p;

public class Constants {
	public static final Integer CHUNK_SIZE = 100000 ;
	public static final Integer SERVERPORT = 9000 ;
	public static final String SERVERADDRESS = "LocalHost" ;

	public static final Integer BACKLOGCLIENTS = 5 ;
	
	
	public static final Integer NUMBEROFPEERS = 5;
	
	public static final Integer PEER1PORT = 9001;
	public static final Integer PEER2PORT = 9002;
	public static final Integer PEER3PORT = 9003;
	public static final Integer PEER4PORT = 9004;
	public static final Integer PEER5PORT = 9005;
	public static final Integer ERRORCODE_WRONG_PEER = 5000;

	public static String INPUT_FILE_NAME = "testfile1.pptx";
	public static Integer INPUT_FILE_SIZE = 0;
	public static Integer NUMBER_OF_CHUNKS = 0;
	public static String MESSAGE_REQUEST_GIVE_CHUNKS = "MESSAGE_REQUEST_GIVE_CHUNKS";

	public static final String MESSAGE_CHUNK_LIST_REQUEST = "MESSAGE_CHUNK_LIST_REQUEST";
	public static final String MESSAGE_CHUNK_REQUEST = "MESSAGE_CHUNK_REQUEST";
	public static final String MESSAGE_NOTIFICATION_DOWNLOAD_COMPLETE = "MESSAGE_NOTIFICATION_DOWNLOAD_COMPLETE";
	public static final String MESSAGE_NOTIFICATION_UPLOADED_WHAT_I_HAVE = "MESSAGE_NOTIFICATION_UPLOADED_WHAT_I_HAVE";
	
	public static final String MESSAGE_SERVER_UPLOADED_CHUNK_COMPLETE = "MESSAGE_SERVER_UPLOADED_CHUNK_COMPLETE";
	
	public static void setFileSize(Integer _fileSize){
		INPUT_FILE_SIZE = _fileSize;
	}
	public static void setNumberOfChunks(Integer _noChunks){
		NUMBER_OF_CHUNKS = _noChunks;
	}
	
	public static void setInputFileName(String _FileName){
		INPUT_FILE_NAME = _FileName;
	}
	
}
