package novite.rs.utility.tools;

import java.io.File;
import java.io.IOException;

public class ClientUpdater {

	public static void main(String[] args) {
		ClientUpdater updater = new ClientUpdater();
		File clientFiles = new File("E:/Users/Jonathan/Dropbox/[MAZONIC] Client/");
		File jarFile = new File("C:/Users/Jonathan/Desktop/Test.jar");
		updater.createJarFile("Loader", clientFiles, jarFile);
	}

	public void createJarFile(String mainClass, File targetClasses, File jarPath) {
		String path = targetClasses.getAbsolutePath() + "\\";
		System.out.println("Attempting to create new jar file at: " + jarPath.getAbsolutePath());
		System.out.println("Grabbing classes from: " + path);
		String filesToJar = "\"" + path + "resources\\\"";
		System.out.println(new File(filesToJar).getAbsolutePath());
		System.out.println("Executing: jar cf \"" + jarPath.getAbsolutePath() + "\" " + filesToJar);
		try {
			Runtime.getRuntime().exec("jar cf \"" + jarPath.getAbsolutePath() + "\" " + filesToJar);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jarPath.exists())
			System.out.println("Created jar file successfully!");
		else {
			System.out.println("Failed to create jar file!");
		}
	}
}
