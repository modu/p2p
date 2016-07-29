package p2p;

public class Peer1 {
	
	//main method
	public static void main(String args[]) throws InterruptedException
	{
		
		PeerHandler ph = new PeerHandler(1);
		ph.start();
		Thread.sleep(1000);
		
		System.out.println("Going to start download Handler");
		
		DownloadHandler download = new DownloadHandler(1);
		download.start();
		Thread.sleep(1000);

		System.out.println("Going to start Uploader Handler");		
		UploadHandler uh = new UploadHandler(1);
		uh.start();
		
	}

};