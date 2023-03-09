package novite.rs.utility.tools;

import java.net.Socket;

import novite.rs.networking.codec.stream.OutputStream;

/**
 * @author Jonathan
 */
public class LoginBot {

	public static void start(final int numClients) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int rsaSize = 5;
				for (int i = 0; i < numClients; i++) {
					try {
						OutputStream body = new OutputStream();
						body.writeByte(16);
						body.writeByte(1);
						body.writeShort(rsaSize);
						body.writeByte(10);
						// isaac keys
						for (int k = 5; k < 9; k++)
							body.writeInt(k);

						body.writeLong(0);
						body.writeString("password" + i);

						body.writeLong(20);// something random
						body.writeLong(0);
						body.writeLong(0);

						body.writeByte(1);// string username?
						body.writeString(i + "bot");// username
						body.writeByte(1);
						body.writeShort(1920);
						body.writeShort(1080);
						body.writeByte(0);// unknown2;
						for (int k = 0; k < 24; k++)
							body.writeByte(0);// because 24 random bytes
						body.writeString("MACADDRESS");// mac address
						body.writeString("SETTINGS");// settings
						body.writeInt(0);// affid
						body.writeByte(36);// usless byte
						body.writeInt(5);// unknown int
						body.writeLong(5);// user-flow
						body.writeByte(0);// has additional infor
						body.writeByte(1);// theora
						body.writeByte(1);// javascript
						body.writeByte(1);// hc
						body.writeByte(1);// uk4
						body.writeInt(5);// uk5
						body.writeString("unkown6");
						body.writeByte(1);// unknown7
						for (int k = 0; k < 37; k++)
							body.writeInt(1);
						final Socket sock = new Socket("127.0.0.1", 43594);
						sock.getOutputStream().write(body.getBuffer());
						new Thread() {
							@Override
							public void run() {
								while (true) {
									try {
										int avail = sock.getInputStream().available();
										if (avail == -1)
											sock.close();
										Thread.sleep(100L);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}.start();
						System.out.println("User " + i + " logged in");
						Thread.sleep(100L);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							Thread.sleep(10000L);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						continue;
					}
				}
			}

		}).start();
	}
}