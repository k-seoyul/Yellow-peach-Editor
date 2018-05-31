package application.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Chart {
	private String type;
	
	private String chartName;
	
	private String firstTopic;
	private String firstName;
	private String secondTopic;
	private String secondName;
	
	private String firstControl;
	private String secondControl;
	//private String publishTopic;
	
	private String unit;
	
	private int row;
	private int col;
	private int number;
	private int level;
	
	public Chart(String type) {
		this.type = type;
		this.chartName = "null";
		this.firstTopic = "null";
		this.firstName = "null";
		this.secondTopic = "null";
		this.secondName = "null";
		this.firstControl = "null";
		this.secondControl = "null";
		this.row = 0;
		this.col = 0;
		this.unit = "null";
		//this.publishTopic = "null";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String getFirstTopic() {
		return firstTopic;
	}

	public void setFirstTopic(String firstTopic) {
		this.firstTopic = firstTopic;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondTopic() {
		return secondTopic;
	}

	public void setSecondTopic(String secondTopic) {
		this.secondTopic = secondTopic;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}


	/*
	public String getPublishTopic() {
		return publishTopic;
	}

	public void setPublishTopic(String publishTopic) {
		this.publishTopic = publishTopic;
	}
	*/

	public String getFirstControl() {
		return firstControl;
	}

	public void setFirstControl(String firstControl) {
		this.firstControl = firstControl;
	}

	public String getSecondControl() {
		return secondControl;
	}

	public void setSecondControl(String secondControl) {
		this.secondControl = secondControl;
	}

	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}



}
