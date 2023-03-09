package novite.rs.networking;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import novite.rs.Constants;
import novite.rs.engine.CoresManager;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.networking.codec.stream.InputStream;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public final class ServerChannelHandler extends SimpleChannelUpstreamHandler {

	public static final void init() {
		new ServerChannelHandler();
	}

	/*
	 * throws exeption so if cant handle channel server closes
	 */
	private ServerChannelHandler() {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(CoresManager.serverBossChannelExecutor, CoresManager.serverWorkerChannelExecutor, CoresManager.serverWorkersCount));
		bootstrap.getPipeline().addLast("handler", this);

		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.connectTimeoutMillis", Constants.CONNECTION_TIMEOUT);
		bootstrap.setOption("child.TcpAckFrequency", true);
		bootstrap.bind(new InetSocketAddress(Constants.PORT_ID));
		System.out.println("Server listening on port " + Constants.PORT_ID + "");
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		ctx.setAttachment(new Session(e.getChannel()));
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Object sessionObject = ctx.getAttachment();
		if (sessionObject != null && sessionObject instanceof Session) {
			Session session = (Session) sessionObject;
			Player player = World.getPlayerByUID(session.getUid());
			if (player != null) {
				player.finish();
			}
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (!(e.getMessage() instanceof ChannelBuffer))
			return;
		Object sessionObject = ctx.getAttachment();
		if (sessionObject != null && sessionObject instanceof Session) {
			Session session = (Session) sessionObject;
			if (session.getDecoder() == null)
				return;
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			buf.markReaderIndex();
			int avail = buf.readableBytes();
			if (avail < 1 || avail > Constants.RECEIVE_DATA_LIMIT) {
				System.out.println("Avail is: " + avail);
				return;
			}
			byte[] buffer = new byte[buf.readableBytes()];
			buf.readBytes(buffer);
			try {
				session.getDecoder().decode(new InputStream(buffer));
			} catch (Throwable er) {
				er.printStackTrace();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		// e.getCause().printStackTrace();
	}

	public static final void shutdown() {
		bootstrap.releaseExternalResources();
	}

	/**
	 * The {@code ServerBootstrap} to use.
	 */
    private static ServerBootstrap bootstrap;

	public static List<Long> usedUIDS = new ArrayList<Long>();

}
