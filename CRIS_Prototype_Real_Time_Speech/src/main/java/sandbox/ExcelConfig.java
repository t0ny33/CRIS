package com.config;

public class ExcelConfig {
	private String EXCEL_INPUT_PATH_ALARMS_HISTORY;
	private String EXCEL_INPUT_PATH_ALARMS_DAY;
	private String EXCEL_INPUT_PATH_ALARMS_NIGHT;

	public void setAlarmsHistoryPath(String path) {
		this.EXCEL_INPUT_PATH_ALARMS_HISTORY = path;
	}

	public void setAlarmsDayPath(String path) {
		this.EXCEL_INPUT_PATH_ALARMS_DAY = path;
	}

	public void setAlarmsNightPath(String path) {
		this.EXCEL_INPUT_PATH_ALARMS_NIGHT = path;
	}
	
	public String getAlarmsHistoryPath() {
		return this.EXCEL_INPUT_PATH_ALARMS_HISTORY;
	}
	
	public String getAlarmsDayPath() {
		return this.EXCEL_INPUT_PATH_ALARMS_DAY;
	}
	
	public String getAlarmsNightPath() {
		return this.EXCEL_INPUT_PATH_ALARMS_NIGHT;
	}

}
