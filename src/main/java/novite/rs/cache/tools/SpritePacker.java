package novite.rs.cache.tools;

import java.io.IOException;

import com.alex.store.Store;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class SpritePacker {

	public static void main(String... args) throws IOException {
		Store our = new Store("./data/alotic/");
		our.getIndexes();
	}

}
