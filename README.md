"# metric-api" 

Metrics API

By Alexander Page

Overview

For this project I decided to use Java as my programming language because I am familiar with it. I also chose to use Spring Boot as this provides a robust framework for web applications like the one being asked for in this project. To build the application I chose Gradle as this plays nicely with Spring Boot / Java and makes for quick and easy deployments. Spring Boot utilizes an embedded Tomcat server which is both easy to test with as well as very quick to start/stop.

As a self-documenting option, I chose to utilize swagger. This automatically documents the various endpoints in my API and additionally allows for simplified request/response testing for end users. The Metrics API swagger page can be viewed at the following URL once the application is running: 
http://localhost:8080/swagger-ui.html#/metric-api-controller

Additionally, I created a front-end GUI which allows simplified access to the API by offering some of the more basic functionality in the form of input fields and multiple response displays (the chart, the log, the metric dump). The GUI connects to the API via web socket connection for a more responsive feel when adding metrics. For this front-end, I utilized Chart.js, JQuery, HTML/CSS, and SockJS. The front-end component was not asked for in this project, but I enjoy working with front-end technologies and thought that it complimented the API nicely.

The GUI can be accessed by going to the following URL/port once the application has been booted:
http://localhost:8080/


API Methods

Below is the runtime/space complexity of each API call:

/metrics -> getAllMetrics – runtime: O(1), space: O(n) where n is the total number of metric values among all metrics

/metircs/add/{metricName}/{metricValue}/ -> addMetric – runtime: O(1), space O(1)

/metrics/clear -> clearAllMetrics – runtime: O(1), space O(1)

/metrics/max/{metricName} -> getMax – runtime: O(1), space O(1)

/metrics/mean/{metricName} -> getMean – runtime: O(n), space O(1) where n is the number of metric values for this metric

/metrics/median/{metricName} -> getMedian – runtime: O(n log n), space O(n) where n is the number of metric values for this metric

/metrics/min/{metricName} -> getMin – runtime: O(1), space O(1)

/metrics/{metricName} -> getSpecificMetric – runtime: O(1), space O(n) where n is the number of metric values for this metric


Unit Tests

The unit tests were written as JUnits and are located in the test package. These unit tests individually test each of the main API methods and do not require the application to be running. As part of the Gradle build, these unit tests will be ran and if unsuccessful will not allow deployment (with Option B). 
The report of the unit tests can be found at the following location in the project directory after performing the Gradle build:
metric-api/build/reports/tests/test/classes/metric.api.test.MetricUnitTests.html


Build/Deploy Instructions

Option A: via Gradle in Eclipse
1.	Import the project as a Gradle project in Eclipse
2.	Right click on project, Gradle -> Refresh Gradle Build
3.	Ensure Spring Boot Eclipse plugin is installed
4.	Right click on project, Run As -> Spring Boot App
5.	The application will be booted on Spring Boot’s internal Tomcat server
6.	The application/API will then be made available at http://localhost:8080 

Option B: via Gradle in Windows Command Prompt
1.	Ensure Gradle is installed on the Windows machine
2.	Ensure JAVA_HOME is set to the JDK folder for Java 8
3.	Open Windows Command Prompt
4.	Navigate to the project’s root directory
5.	Run the following command: gradle wrapper
6.	Run the following command: gradlew build
7.	Run the following command: gradlew run
8.	The application will be booted on Spring Boot’s internal Tomcat server
9.	The application/API will then be made available at http://localhost:8080 


Enhancements

If I were to spend more time with developing this API, I would make the following enhancements:
1.	Add log4j logging so the API logs to a file on the server that is managed by a rolling file appender and has a policy to archive/zip log files of a certain age or older to keep a history in a compressed format.
2.	If the getMedian method was called frequently by users, I would implement a red/black tree to store the values for a given metric. This would allow me to make the access of the median value an O(1) operation, but this would also slightly slow down my insert performance when adding a metric value to O(log n), so there would need to be good reason to make this change.
3.	Add integration testing which would actually bring up the API and run certain API calls with expected results. This step would be performed during the build and if any integration tests failed, the build would fail.
