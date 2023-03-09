package novite.rs.utility.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * 
 * @author Jonathan
 * @since Jan 26, 2014
 */
public class CacheDownloader {

	public static void main(String[] args) throws UnknownHostException, IOException {
		for (int i = 0; i < 500; i++)
		new CacheDownloader("127.0.0.1", 43594, 666, 2).startHandShake();
	}
	
	public CacheDownloader(String host, int port, int revision, int subRevision) {
		this.host = host;
		this.port = port;
		this.revision = revision;
		this.subRevision = subRevision;
	}

	private String host;
	private int port, revision, subRevision;

	private Socket socket;
	private OutputStream output;
	private InputStream input;

	public void startHandShake() throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		input = socket.getInputStream();
		output = socket.getOutputStream();

		ByteBuffer buffer = ByteBuffer.allocate(11 + 32);
		buffer.put((byte) 15);
		buffer.putInt(revision);
		buffer.putInt(subRevision);
		output.write(buffer.array());
		output.flush();

		
		requestCacheFile(255, 255);
	}

	public void requestCacheFile(int index, int archive) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put((byte) 1);
		buffer.put((byte) index);
		buffer.putInt(archive);
		output.write(buffer.array());
		output.flush();
		
		int available = input.available();
		if (available < 0) {
			throw new IOException();
		}
		System.out.println(available);
		if (available >= 5) {
			buffer = ByteBuffer.allocate(5);
			input.read(buffer.array());
			int recievedIndex = buffer.get();
			int recievedFile = buffer.getInt();
			System.out.println("We got :" + recievedIndex + ", " + recievedFile);
		}
	}
}
