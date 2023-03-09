package novite.rs.networking.codec;

import novite.rs.networking.Session;
import novite.rs.networking.codec.stream.InputStream;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract void decode(InputStream stream);

}
