var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#send").prop("disabled", !connected);
    $("#clear").prop("disabled", !connected);
    if (connected) {
    	$("#connect").html("Connected");
    	$("#disconnect").html("Disconnect");
        //$("#conversation").show();
    }
    else {
    	$("#connect").html("Connect");
    	$("#disconnect").html("Disconnected");
        //$("#conversation").hide();
    }
    $("#metrics").html("");
}

function connect() {
    var socket = new SockJS('/metrics-endpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/metrics', function (metric) {
            showMetric(JSON.parse(metric.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function isEmpty() {
    var x = $( "#name" ).val().length;
    var y = $( "#value" ).val().length;
    if (x == 0 || y == 0) {
        return true;
    };
}

function sendMetric() {
    stompClient.send("/app/metricHandler", {}, JSON.stringify({'name': $("#name").val(), 'value': $("#value").val()}));
    
    var alreadyHave = false;
    config.data.datasets.forEach( function(dataset) {
        if(dataset.label == $("#name").val()) {
        	alreadyHave = true;
        	if (config.data.labels.length == dataset.data.length) {
        		  config.data.labels.push(dataset.data.length+1);
        	}
        	dataset.data.push($("#value").val());
            window.myLine.update();
        }
    });
    
    if (!alreadyHave) {
        var colorName = colorNames[config.data.datasets.length % colorNames.length];
        var newColor = window.chartColors[colorName];
        var newDataset = {
            label: $("#name").val(),
            backgroundColor: newColor,
            borderColor: newColor,
            data: [],
            fill: false
        };
        newDataset.data.push($("#value").val());
        config.data.datasets.push(newDataset);
        window.myLine.update();
    }
}

function showMetric(message) {
    $("#metrics").append("<tr><td>" + message + "</td></tr>");
}

function loadExistingMetricsToChart() {
	$.get( "/metrics", function( data ) {
    	console.log(data);
    	$.each(data, function(i,x) {
    		console.log(i+":"+x);
    		var colorName = colorNames[config.data.datasets.length % colorNames.length];
            var newColor = window.chartColors[colorName];
            var newDataset = {
                label: i,
                backgroundColor: newColor,
                borderColor: newColor,
                data: x,
                fill: false
            };
            config.data.datasets.push(newDataset);
		});
    	window.myLine.update();
	});
}

function loadExistingMetricsToLog() {
	$( "#currentMetrics" ).load( "/metrics" );
}

function clearData() {
	$.get( "/metrics/clear" );
	loadExistingMetricsToLog();
	config.data.datasets.splice(0, 100);
    window.myLine.update();
    $( "#metrics" ).empty();
    $( "#metrics" ).append( "<tr><td>Cleared metrics</td></tr>" );
}

$(function () {
	$(document).ready(function() {
		console.log('document loaded, connecting to ws');
		connect();
		loadExistingMetricsToLog();
		loadExistingMetricsToChart();
	});
    $("form").on('submit', function (e) {
        e.preventDefault();
        loadExistingMetricsToLog();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { 
    	if (isEmpty()) {
    		console.log("Invalid input, not sending request");
    	} else {
    		sendMetric();
    	}
    });
    $( "#clear" ).click(function() { clearData(); });
});

