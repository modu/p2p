package p2p;

public class Peer2 {
	
	public static void main(String args[]) throws InterruptedException
	{
		
		PeerHandler ph = new PeerHandler(2);
		ph.start();
		Thread.sleep(1000);
		
		DownloadHandler download = new DownloadHandler(2);
		download.start();
		Thread.sleep(1000);
		
		UploadHandler uh = new UploadHandler(2);
		uh.start();
		
//		Peer peer = new Peer(1);
//		peer.run();
	}
};
