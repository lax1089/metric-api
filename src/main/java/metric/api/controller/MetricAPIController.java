package metric.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import metric.api.config.SwaggerConfig;
import metric.api.config.WebAPIConfig;
import metric.api.domain.Metric;
import metric.api.domain.MetricLogMessage;

@Controller
@Import({WebAPIConfig.class, SwaggerConfig.class})
public class MetricAPIController {
	
	HashMap<String, ArrayList<Float>> metricMap = new HashMap<>();
	HashMap<String, Float> minMap = new HashMap<>();
	HashMap<String, Float> maxMap = new HashMap<>();

	// Add a metric/value via the web portal (GUI)
    @MessageMapping("/metricHandler")
    @SendTo("/topic/metrics")
    public MetricLogMessage addMetric(Metric message) throws Exception {
    	String metricName;
    	Float metricValue;
    	try {
    		metricName = message.getName();
        	metricValue = Float.parseFloat(message.getValue());
	    	addMetricToMaps(metricName, metricValue);
	    	logMetrics();
	        return new MetricLogMessage("Added value "+metricValue+" for "+metricName+"!");
    	} catch (Exception e) {
    		System.err.println("Error adding metric via API call: "+e);
    		return new MetricLogMessage("Unable to add metric, check your input!");
    	}
    }

    // Add a metric via API call
    @GetMapping(value="/metrics/add/{metricName}/{metricValue}/")
    public ResponseEntity<ArrayList<Float>> addMetric(@PathVariable String metricName, @PathVariable Float metricValue) throws Exception {
    	try {
	    	addMetricToMaps(metricName, metricValue);
	    	logMetrics();
	    	return new ResponseEntity<>(metricMap.get(metricName), HttpStatus.OK);
    	} catch (Exception e) {
    		System.err.println("Error adding metric via API call: "+e);
    		return new ResponseEntity<>(metricMap.get(metricName), HttpStatus.BAD_REQUEST);
    	}
    }
    
    // Return all metrics
    @GetMapping(value="/metrics")
    public ResponseEntity<HashMap<String, ArrayList<Float>>> getAllMetrics() throws Exception {
    	return new ResponseEntity<>(metricMap, HttpStatus.OK);
    }
    
    // Return all metric value for the specified metric
    @GetMapping(value="/metrics/{metricName}")
    public ResponseEntity<ArrayList<Float>> getSpecificMetrics(@PathVariable String metricName) throws Exception {
    	if (metricMap.containsKey(metricName)) {
    		return new ResponseEntity<>(metricMap.get(metricName), HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    }
    
    // Return the min value for the specified metric
    @GetMapping(value="/metrics/min/{metricName}")
    public ResponseEntity<Float> getMin(@PathVariable String metricName) throws Exception {
    	if (minMap.containsKey(metricName)) {
    		return new ResponseEntity<>(minMap.get(metricName), HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    }
    
    // Return the max value for the specified metric
    @GetMapping(value="/metrics/max/{metricName}")
    public ResponseEntity<Float> getMax(@PathVariable String metricName) throws Exception {
    	if (maxMap.containsKey(metricName)) {
    		return new ResponseEntity<>(maxMap.get(metricName), HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    }
    
    // Return the mean of the specified metric
    @GetMapping(value="/metrics/mean/{metricName}")
    public ResponseEntity<Float> getMean(@PathVariable String metricName) throws Exception {
    	ArrayList<Float> metricList = metricMap.get(metricName);
    	 if (metricList.size() == 0) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	
    	Float sum = new Float(0.0);
    	for (Float f : metricList) {
    		sum+=f;
    	}
    	Float mean = sum/metricList.size();
    	
    	return new ResponseEntity<>(mean, HttpStatus.OK);
    }
    
    // Return the median of the specified metric
    @GetMapping(value="/metrics/median/{metricName}")
    public ResponseEntity<Float> getMedian(@PathVariable String metricName) throws Exception {
	    ArrayList<Float> metricList = metricMap.get(metricName);
	    if (metricList.size() == 0) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    
	    double[] doubleArray = metricList.stream()
	    	    .mapToDouble(f -> f != null ? f : Float.NaN)
	    	    .toArray();
	    
    	Arrays.sort(doubleArray);
    	
	    float median;
	    if (doubleArray.length % 2 == 0)
	        median = ((float)doubleArray[doubleArray.length/2] + (float)doubleArray[doubleArray.length/2 - 1])/2;
	    else
	        median = (float) doubleArray[doubleArray.length/2];
	    
	    return new ResponseEntity<>(median, HttpStatus.OK);
    }
    
    // Clear all the metric maps
    @GetMapping(value="/metrics/clear")
    public ResponseEntity<HttpStatus> clearAllMetrics() throws Exception {
    	metricMap.clear();
    	minMap.clear();
    	maxMap.clear();
    	System.out.println("Cleared metric data");
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
   /**
    * Adds the new metric to all the maps:
    * 1. metricMap: contains all of the metrics grouped by their name which serves as their key
    * 2. minMap: contains a mapping of each key to its lowest metric value
    * 3. maxMap: contains a mapping of each key to its highest metric value 
    * If the metricMap does not contain this key, that means the user is trying to add
    * a completely new metric.
    **/
    private void addMetricToMaps(String metricName, Float metricValue) {
		if (metricMap.containsKey(metricName)) {
    		metricMap.get(metricName).add(metricValue);
    		if (minMap.get(metricName) > metricValue) minMap.put(metricName, metricValue);
    		if (maxMap.get(metricName) < metricValue) maxMap.put(metricName, metricValue);
    	} else {
    		ArrayList<Float> newArrList = new ArrayList<>();
    		newArrList.add(metricValue);
    		metricMap.put(metricName, newArrList);
    		minMap.put(metricName, metricValue);
    		maxMap.put(metricName, metricValue);
    	}
	}
    
    // Logs the current metrics on the server
	private void logMetrics() {
		System.out.println("Metrics: ");
    	for (String metricNameItr: metricMap.keySet()) {
    		System.out.print(metricNameItr+" - ");
    		for (Float valItr : metricMap.get(metricNameItr)) {
    			System.out.print(valItr+" ");
    		}
    		System.out.println();
    	}
    	System.out.println();
	}

}
