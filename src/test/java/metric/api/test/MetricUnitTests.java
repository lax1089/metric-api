package metric.api.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import metric.api.controller.MetricAPIController;

@RunWith(SpringRunner.class)
public class MetricUnitTests {
	
	MetricAPIController metricApi = new MetricAPIController();
	String metricName = "TestMetric";
	
    @Before
    public void setup() {
    	
    }
    
    @Test
    public void testAddMetric() throws Exception {
    	metricApi.addMetric(metricName, new Float(1.5));
    	ResponseEntity<ArrayList<Float>> metricApiResponse = metricApi.getSpecificMetrics(metricName);
    	ArrayList<Float> metricValues = metricApiResponse.getBody();
    	assertTrue("There should be 1 metrics value", metricValues.size() == 1);
    	assertTrue("The metric value should be 1.5", metricValues.get(0) == 1.5);
    }
    
    @Test
    public void testAddMetrics() throws Exception {
    	addFiveMetrics();
    	ResponseEntity<ArrayList<Float>> metricApiResponse = metricApi.getSpecificMetrics(metricName);
    	ArrayList<Float> metricValues = metricApiResponse.getBody();
    	assertEquals("The amount of metrics is incorrect.", metricValues.size(), 5);
    }
    
    @Test
    public void testMinMetric() throws Exception {
    	addFiveMetrics();
    	ResponseEntity<Float> metricApiResponse = metricApi.getMin(metricName);
    	Float metricValue = metricApiResponse.getBody();
    	assertEquals("The min value is incorrect.", metricValue, new Float(-1));
    }
    
    @Test
    public void testMaxMetric() throws Exception {
    	addFiveMetrics();
		ResponseEntity<Float> metricApiResponse = metricApi.getMax(metricName);
    	Float metricValue = metricApiResponse.getBody();
    	assertEquals("The max value is incorrect.", metricValue, new Float(10));
    }

    @Test
    public void testMedianMetric() throws Exception {
    	addFiveMetrics();
		ResponseEntity<Float> metricApiResponse = metricApi.getMedian(metricName);
    	Float metricValue = metricApiResponse.getBody();
    	assertEquals("The median value is incorrect.", metricValue, new Float(1.5));
    }
    
    @Test
    public void testMeanMetric() throws Exception {
    	addFiveMetrics();
		ResponseEntity<Float> metricApiResponse = metricApi.getMean(metricName);
    	Float metricValue = metricApiResponse.getBody();
    	assertEquals("The mean value is incorrect.", metricValue, new Float(3.2111111));
    }
    
	private void addFiveMetrics() throws Exception {
		metricApi.addMetric(metricName, new Float(1.5));
    	metricApi.addMetric(metricName, new Float(10));
    	metricApi.addMetric(metricName, new Float(-1));
    	metricApi.addMetric(metricName, new Float(0));
    	metricApi.addMetric(metricName, new Float(5.55555555));
	}

}
