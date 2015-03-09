package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TimerDisplay {
	
	public Handler handler = new Handler();
	public static TimerTask OneHundredmsPeriod;
	
	public enum whichClock 	{HomeClock, TestClock, RunClock, ActionClock, ResultClock, MemoryClock, BlankClock, SettingClock, SystemSettingClock, RemoveClock, PatientClock, ExportClock,
							ImageClock, DataSettingClock, MaintenanceClock, DateClock, TimeClock, DisplayClock, HISClock, HISSettingClock, UserClock, SoundClock}
	public static whichClock timerState;

	final static String rTime[] = new String[8];
	
	private Timer timer;
	private Timer runTimer;
	
	private HomeActivity          TimerHome;
	private BlankActivity         TimerBlank;
	private SettingActivity       TimerSetting;
	
	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 9) { // One second period
						
							cnt = 0;
							
							RealTime();
							
							if(Integer.parseInt(rTime[6]) == 0) { // Whenever 00 second
											
								ClockDecision();
							}
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void RealTime() { // Get current date and time
	
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[0] = Integer.toString(c.get(Calendar.YEAR));
		rTime[1] = dfm.format(c.get(Calendar.MONTH) + 1);
		rTime[2] = dfm.format(c.get(Calendar.DAY_OF_MONTH));
		if(c.get(Calendar.HOUR) != 0) {
			
			rTime[3] = Integer.toString(c.get(Calendar.HOUR));
		} else {
		
			rTime[3]="12";
		}
		rTime[4] = dfm.format(c.get(Calendar.MINUTE));		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
		rTime[7] = dfm.format(c.get(Calendar.HOUR_OF_DAY));
			
		if(c.get(Calendar.AM_PM) == 0) {

			rTime[5] = "AM";			
		} else {

			rTime[5] = "PM";			
		}		
	}
	
	public void ClockDecision() { // Whenever activity change, Corresponding clock action
		
		switch(timerState){
		
		case HomeClock			:	
			TimerHome = new HomeActivity();
			TimerHome.CurrTimeDisplay();
			break;

		case BlankClock			:	
			TimerBlank = new BlankActivity();
			TimerBlank.CurrTimeDisplay();
			break;
								
		case SettingClock		:	
			TimerSetting = new SettingActivity();
			TimerSetting.CurrTimeDisplay();
			break;
			
		default					:	
			break;		
		}
	}
}
