package p2p;

public class Peer3 {
	public static void main(String args[]) throws InterruptedException
	{
		
		PeerHandler ph = new PeerHandler(3);
		ph.start();
		Thread.sleep(1000);

		DownloadHandler download = new DownloadHandler(3);
		download.start();
		Thread.sleep(1000);
	
		UploadHandler uh = new UploadHandler(3);
		uh.start();

	}
}
