package gov.nih.nlm.textflow.config;

public class Initializer {

	@SuppressWarnings("rawtypes")
	public static void run(String args[], Class launcher) {
		
		if(args==null || args.length < 1) {
			System.err.println("Usage java -cp <TextFlow-core-full.jar> "+launcher.getName()+" <path-to-resources-folder>");
			System.exit(-1);
		}
		System.setProperty("resources_path",args[0]);
		System.setProperty("logfile.name","./logs/log.txt");
	
	}
	
}
