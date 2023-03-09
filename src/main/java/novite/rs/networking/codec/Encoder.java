package novite.rs.networking.codec;

import novite.rs.networking.Session;

public abstract class Encoder {

	protected Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}
