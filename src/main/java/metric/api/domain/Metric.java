package metric.api.domain;

public class Metric {

    private String name;
    private String value;

    public Metric() {
    }

    public Metric(String name) {
        this.name = name;
    }
    
    public Metric(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
