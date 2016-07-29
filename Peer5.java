package p2p;

public class Peer5 {
	public static void main(String args[]) throws InterruptedException
	{
		
		PeerHandler ph = new PeerHandler(5);
		ph.start();
		Thread.sleep(1000);
		DownloadHandler download = new DownloadHandler(5);
		download.start();
		Thread.sleep(1000);

		UploadHandler uh = new UploadHandler(5);
		uh.start();
	}
}
