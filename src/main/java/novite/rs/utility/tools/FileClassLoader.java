package novite.rs.utility.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Oct 12, 2013
 */
public class FileClassLoader {

	/**
	 * Gets all of the classes in a directory
	 *
	 * @param packageName The packageName to iterate through
	 * @return The list of classes
	 */
	public static List<Object> getClassesInDirectory(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
			}
			List<Class> classes = new ArrayList<>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			List<Object> list = new ArrayList<>();
			for (Class clazz : classes) {
				if (clazz.isAnnotation()) {
					continue;
				}
				list.add(clazz.newInstance());
			}
			return list;
		} catch (IllegalAccessException | InstantiationException | IOException e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	private static List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			return classes;
		}
		for (File file : files) {
			if (file.getName().contains("$")) {
				continue;
			}
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}

}
