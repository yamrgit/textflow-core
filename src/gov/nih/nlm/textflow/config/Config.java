package gov.nih.nlm.textflow.config;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config extends PropertiesConfiguration {

	private static Configuration instance;

	public static Configuration getInstance() {
		if(instance == null){
			try
			{
			    instance = new Configurations().properties(new File(System.getProperty("resources_path") + "/config.properties"));
			}
			catch (ConfigurationException cex)
			{
			    cex.printStackTrace();
			}
		}
		
		return instance;
				
	}

}
