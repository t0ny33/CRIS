package com.dialog;

import java.util.ArrayList;
import java.util.List;

import com.alarm.Alarm;
import com.alarm.AlarmContainer;
import com.config.DialogConfig;
import com.google.cloud.dialogflow.v2beta1.Intent;
import com.google.common.collect.Lists;
import com.reader.ExcelReader;

public class TestDialog {

	public static void main(String[] args) {
		List<String> texts = new ArrayList<String>();
		List<Intent> intents = Lists.newArrayList();
		AlarmContainer<Alarm> alarmsHistory = new AlarmContainer<Alarm>();
		ExcelReader exReader = new ExcelReader();
		String text = "Show me alarms for destiantion Germany";
		try {
			Dialog.detectIntentTexts(DialogConfig.PROJECT_ID, text, DialogConfig.SESSION_ID,
					DialogConfig.LANGUAGE_CODE);
			//Dialog.listEntities(DialogConfig.PROJECT_ID, DialogConfig.ENTITY_TYPE_ID);
			
			intents = Dialog.listIntents(DialogConfig.PROJECT_ID);
			for (Intent intent : intents) {
				System.out.println(intent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

/*
 * <=== Intent List ===>
 * 
 * User-requests-history-alarm-table User reqeusts dashboard Dashboard type
 * selection - (OpCo) Dashboard type selection - (Carrier) Default Fallback
 * Intent Dashboard type selection - (OpCo/Carrier)
 * User-requests-history-alarm-table - yes Dashboard type selection - (OpCo) -
 * Inbound - END Dashboard type selection - (Carrier) - Outbound Agent says his
 * name Dashboard type selection - (Carrier) - Inbound Default Welcome Intent
 * User-requests-history-alarm-table - show all alarms Dashboard type selection
 * - (OpCo) - Outbound - END Dashboard type selection - (Carrier) - Inbound -
 * END Dashboard type selection - (OpCo) - Inbound Dashboard type selection -
 * (Carrier) - Outbound - END Dashboard type selection - (OpCo) - Outbound
 */
