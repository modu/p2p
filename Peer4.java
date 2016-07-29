package p2p;

public class Peer4 {
	public static void main(String args[]) throws InterruptedException
	{
		
		PeerHandler ph = new PeerHandler(4);
		ph.start();
		Thread.sleep(1000);
		
		DownloadHandler download = new DownloadHandler(4);
		download.start();
		Thread.sleep(1000);

		UploadHandler uh = new UploadHandler(4);
		uh.start();
	}
}
