package gov.nih.nlm.textflow.models;

import static com.google.common.base.Preconditions.*;
import gov.nih.nlm.textflow.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simmetrics.ListDistance;

public class TextFlow implements ListDistance<String> {

	/**
	 * Parameters names
	 */
	public final static String PARAM_POSITION = "PARAM_POSITION";
	public final static String PARAM_SEQUENCE = "PARAM_SEQUENCE";
	public final static String PARAM_SENSITIVITY = "PARAM_SENSITIVITY";
	public final static String PARAM_SKIP = "PARAM_SKIP";

	/**
	 * Parameters Map
	 */
	protected Map<String, Double> params;
	
	public static double MAX_LENGTH = 0;


	
	public TextFlow() {
		params = new HashMap<String, Double>();
		params.put(PARAM_POSITION, Config.getInstance().immutableSubset(getClass().getName()).getDouble(PARAM_POSITION));
		params.put(PARAM_SEQUENCE, Config.getInstance().immutableSubset(getClass().getName()).getDouble(PARAM_SEQUENCE));
		params.put(PARAM_SENSITIVITY, Config.getInstance().immutableSubset(getClass().getName()).getDouble(PARAM_SENSITIVITY));		
		params.put(PARAM_SKIP, Config.getInstance().immutableSubset(getClass().getName()).getDouble(PARAM_SKIP));		
	}

	public TextFlow(double positionParm, double sequenceParm, double sensitivityParm, int skipAllowed) {
		params = new HashMap<String, Double>();
		params.put(PARAM_POSITION, positionParm);
		params.put(PARAM_SEQUENCE, sequenceParm);
		params.put(PARAM_SENSITIVITY, sensitivityParm);		
		params.put(PARAM_SKIP, (double) skipAllowed);		
	}


	@Override
	public float distance(List<String> s1, List<String> s2){

		checkNotNull(s1, "Null string array provided for comparison");
		checkNotNull(s2, "Null string array provided for comparison");

		double integral = 0.0;

		double commonTokensNumber = 0;
		double s1pos = 0;
		double previousDelta = 0;

		double sequenceLength = 0;
		double sequenceMem = 0;
		double skipped = 0;
	
		double referenceSize =  s2.size();
		double scalingCoef = s1.size() * referenceSize;
		
		double positionCoef = 0.0;
		double sequenceCoef = 0.0;
		double sensitivityCoef = 0.0;
		
		double pos = params.get(PARAM_POSITION);		
		double seq = params.get(PARAM_SEQUENCE);
		double sen = params.get(PARAM_SENSITIVITY);
		double skip = params.get(PARAM_SKIP);
		double unit = 1.0;

		int previous_best_I = -1;
		

		for(String t1: s1){
			
			if(t1 == null || t1.isEmpty())
				continue;

			int best_I = -1;
			s1pos++;
			
			boolean matched = false;
			
			double normalizedPos1 = normalizePosition(s1pos, s1.size(), referenceSize);
			
			double currentDelta = referenceSize;
			

			int i = 1;

			for(String t2: s2){
		
				if(t2 == null || t2.isEmpty())
					continue;

				if(t1.toLowerCase().equals(t2.toLowerCase())){
					
					double diff = Math.abs( i - normalizedPos1 );
					
					if(diff == 0){
						currentDelta = 0;
						break;
					}
					
					if(diff < currentDelta){
						best_I = i;
						currentDelta = diff ;
					}
					
				}

				i++;
			}				
	
			if(currentDelta!=referenceSize){
				commonTokensNumber++;
				matched = true;
			}
			
			double rectangleSurface =  Math.min(currentDelta, previousDelta) * unit;
			double triangleSurface = 0.5 * Math.abs(currentDelta - previousDelta) * unit;

			double surface = rectangleSurface + triangleSurface;
					
			if(!matched){//the word is not in the other sentence
			
				sensitivityCoef += surface;

			}else{			
				//if we found a matching sequence of 2 tokens
				if(best_I == (previous_best_I + 1)){
					sequenceLength++;		
					sequenceMem += surface;
				}else{
					
					if(sequenceLength > 0){
						skipped++;

						if(skipped > skip){
							sequenceCoef += sequenceMem / sequenceLength;
							sequenceMem = 0;
							sequenceLength = 0;
							skipped = 0;
						}
						
					}
					
					if(previousDelta!=referenceSize){
						positionCoef += surface;
					}else{
						sensitivityCoef += triangleSurface;
						positionCoef += rectangleSurface;
					}
					
				}
				
			}


			previousDelta = currentDelta;
			previous_best_I = best_I;

		}
//		
		if(sequenceLength > 0){
			sequenceCoef += sequenceMem / sequenceLength;
		}

	
			
		integral = (positionCoef*pos) + (sequenceCoef*pos*seq) + (sensitivityCoef*sen);
		
		
		float score = 1.0f;
		//if there are no common elements, the distance should not be relative to the text size
		if(commonTokensNumber != 0){

			score = (float) normalizeSize(integral, scalingCoef * sen);	
			//System.out.println(score);

		}
		

		return score;

	}
	
	protected double normalizePosition(double position, double localSize, double referenceSize){

		return  (position *  ( referenceSize / localSize ));

	}

	protected double normalizeSize(double integral, double scalingCoef){

	
		return  integral / scalingCoef ;

	}




}
