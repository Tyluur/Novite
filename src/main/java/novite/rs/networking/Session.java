package novite.rs.networking;

import novite.rs.game.player.Player;
import novite.rs.networking.codec.Decoder;
import novite.rs.networking.codec.Encoder;
import novite.rs.networking.codec.stream.OutputStream;
import novite.rs.networking.protocol.ClientPacketsDecoder;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.networking.protocol.game.DefaultGameEncoder;
import novite.rs.networking.protocol.js5.GrabPacketsDecoder;
import novite.rs.networking.protocol.js5.GrabPacketsEncoder;
import novite.rs.networking.protocol.login.LoginPacketsDecoder;
import novite.rs.networking.protocol.login.LoginPacketsEncoder;
import novite.rs.utility.Utils;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

public class Session {

	private Channel channel;
	private Decoder decoder;
	private Encoder encoder;
	private long uid;

	public Session(Channel channel) {
		generateUid();
		this.channel = channel;
		setDecoder(0);
	}

	private void generateUid() {
		long uid = Utils.random(0, Integer.MAX_VALUE);
		while (ServerChannelHandler.usedUIDS.contains(uid)) {
			uid = Utils.random(0, Integer.MAX_VALUE);
		}
		this.setUid(uid);
	}

	public final ChannelFuture write(OutputStream outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(outStream.getBuffer(), 0, outStream.getOffset());
		synchronized (channel) {
			return channel.write(buffer);
		}
	}

	public final ChannelFuture write(ChannelBuffer outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		synchronized (channel) {
			return channel.write(outStream);
		}
	}

	public final Channel getChannel() {
		return channel;
	}

	public final Decoder getDecoder() {
		return decoder;
	}

	public GrabPacketsDecoder getGrabPacketsDecoder() {
		return (GrabPacketsDecoder) decoder;
	}

	public final Encoder getEncoder() {
		return encoder;
	}

	public final void setDecoder(int stage) {
		setDecoder(stage, null);
	}

	public final void setDecoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			decoder = new ClientPacketsDecoder(this);
			break;
		case 1:
			decoder = new GrabPacketsDecoder(this);
			break;
		case 2:
			decoder = new LoginPacketsDecoder(this);
			break;
		case 3:
			decoder = new DefaultGameDecoder(this, (Player) attachement);
			break;
		case -1:
		default:
			decoder = null;
			break;
		}
	}

	public final void setEncoder(int stage) {
		setEncoder(stage, null);
	}

	public final void setEncoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			encoder = new GrabPacketsEncoder(this);
			break;
		case 1:
			encoder = new LoginPacketsEncoder(this);
			break;
		case 2:
			encoder = new DefaultGameEncoder(this, (Player) attachement);
			break;
		case -1:
		default:
			encoder = null;
			break;
		}
	}

	public LoginPacketsEncoder getLoginPackets() {
		return (LoginPacketsEncoder) encoder;
	}

	public GrabPacketsEncoder getGrabPackets() {
		return (GrabPacketsEncoder) encoder;
	}

	public DefaultGameEncoder getWorldPackets() {
		return (DefaultGameEncoder) encoder;
	}

	public String getIP() {
		return channel == null ? "" : channel.getRemoteAddress().toString().split(":")[0].replace("/", "");
	}

	public String getLocalAddress() {
		return channel.getLocalAddress().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Session) {
			Session s = (Session) o;
			if (s.getChannel().getId() == getChannel().getId())
				return true;
		}
		return false;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
		ServerChannelHandler.usedUIDS.add(uid);
	}
}
