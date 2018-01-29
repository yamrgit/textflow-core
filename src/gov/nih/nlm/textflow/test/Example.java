package gov.nih.nlm.textflow.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

import gov.nih.nlm.textflow.config.Config;
import gov.nih.nlm.textflow.config.Initializer;
import gov.nih.nlm.textflow.models.TextFlow;

public class Example {

	private static HashSet<String> _stopw = new HashSet<String>();
	
	public static void main(String[] args) {
		
		Initializer.run(args, Example.class);
		
		readStopwords();
		
		TextFlow txf = new TextFlow();
		
		//Sentence pairs from the MSRP and SNLI datasets
		String[] example_sentences = {
				"Under a blue sky with white clouds, a child reaches up to touch the propeller of a plane standing parked on a field of grass.",
				"A child is reaching to touch the propeller of a plane.",
				"Two men on bicycles competing in a race.",
				"Men are riding bicycles on the street.",
				"The most serious breach of royal security in recent years occurred in 1982 when 30-year-old Michael Fagan broke into the queen's bedroom at Buckingham Palace.",
				"It was the most serious breach of royal security since 1982 when an intruder, Michael Fagan, found his way into the Queen's bedroom at Buckingham Palace.",
				"\"Americans don't cut and run, we have to see this misadventure through,\" she said.",
				"She also pledged to bring peace to Iraq: \"Americans don't cut and run, we have to see this misadventure through.\""
		};
		
		
		//First direction
		System.out.println("## FIRST DIRECTION ##");
		for(int i = 0; i < example_sentences.length - 1; i+=2){
			List<String> s1 = removeStopwords(tokenize(example_sentences[i]));
			List<String> s2 = removeStopwords(tokenize(example_sentences[i+1]));
			float txf_distance = txf.distance(s1, s2);
			System.out.println("SENT ("+(i+1)+"):"+example_sentences[i]);
			System.out.println("SENT ("+(i+2)+"):"+example_sentences[i+1]);
			System.out.println("TXF ("+(i+1)+", "+(i+2)+"):"+txf_distance);
		}

		//Second direction
		System.out.println("\n## SECOND DIRECTION ##");
		for(int i = 0; i < example_sentences.length - 1; i+=2){
			List<String> s1 = removeStopwords(tokenize(example_sentences[i]));
			List<String> s2 = removeStopwords(tokenize(example_sentences[i+1]));
			float txf_distance = txf.distance(s2, s1);
			System.out.println("SENT ("+(i+1)+"):"+example_sentences[i]);
			System.out.println("SENT ("+(i+2)+"):"+example_sentences[i+1]);
			System.out.println("TXF ("+(i+2)+", "+(i+1)+"):"+txf_distance);
		}


	}
	
	
	private static List<String> tokenize(String sentence){
		List<String> result = new ArrayList<String>();
		for(String token: sentence.trim().split("[\\s,:'\\.;\"'\\?!]+"))
			result.add(token);
		return result;		
	}
	
	private static List<String> removeStopwords(List<String> sentence){
		List<String> result = new ArrayList<String>();
		for(String token: sentence)
			if(!_stopw.contains(token))
				result.add(token);
		return result;
	}
	
	private static void readStopwords(){
		try {
			File stopw_file = new File(Config.getInstance().immutableSubset(Example.class.getName()).getString("stopwords_file"));
			for(String line: FileUtils.readLines(stopw_file, "UTF-8")){
				_stopw.add(line.toLowerCase().trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
