package model;

public class TimeserieValue {
    private Integer value;
    private Double time;

    public TimeserieValue(Integer value, Double time) {
        this.value = value;
        this.time = time;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
