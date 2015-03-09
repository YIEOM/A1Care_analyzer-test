package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Text;

import isens.hba1c_analyzer.RunActivity.Cart1stStep;
import isens.hba1c_analyzer.RunActivity.Cart1stStep2;
import isens.hba1c_analyzer.RunActivity.Cart1stStep3;
import isens.hba1c_analyzer.RunActivity.Cart2ndStep;
import isens.hba1c_analyzer.RunActivity.Cart2ndStep2;
import isens.hba1c_analyzer.RunActivity.Cart2ndStep3;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;

public class HomeActivity extends Activity {
	
	final static String FW_VERSION = "QV";
	
	private SerialPort HomeSerial;
	private TimerDisplay HomeTimer;
	private RunActivity HomeRun;
	private Temperature HomeTemp;
	
	private Button runBtn;
	private Button settingBtn;
	private Button blankBtn;
	
	private ImageButton check2Btn;
	private ImageButton check3Btn;
	private ImageButton check4Btn;
	private ImageButton check5Btn;
	private ImageButton check6Btn;
	
	private TextView deviceState;
	private TextView oneOne;
	private TextView oneTwo;
	private TextView oneThree;
	private TextView twoOne;
	private TextView twoTwo;
	private TextView twoThree;
	private TextView threeOne;
	private TextView threeTwo;
	private TextView threeThree;
	private TextView fourOne;
	private TextView fourTwo;
	private TextView fourThree;
	private TextView fiveOne;
	private TextView fiveTwo;
	private TextView fiveThree;
	private TextView sixOne;
	private TextView sixTwo;
	private TextView sixThree;
	
	public Handler handler = new Handler();
	public TimerTask OneHundredmsPeriod;
	private Timer timer;
	
	public static boolean TestFlag = false;
	public static boolean ThreadState1 = false;
	public static boolean ThreadState2 = false;
	public static boolean ThreadState3 = false;
	public static boolean ThreadState4 = false;
	public static boolean ThreadState5 = false;
	public static boolean ThreadState6 = false;
	public static boolean ThreadStop = false;
	
	public static boolean Checkbox2 = false;
	public static boolean Checkbox3 = false;
	public static boolean Checkbox4 = false;
	public static boolean Checkbox5 = false;
	public static boolean Checkbox6 = false;
	
	public enum TargetIntent {Home, HbA1c, NA, Test, Run, Blank, Setting, Memory, Result}
	
	public Toast toast;
	public static TextView TimeText;
	public TextView fwVersionText, swVersionText;
	
	public static boolean InitState = false;
	
	final DecimalFormat hbA1cFormat = new DecimalFormat("0.0000");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		deviceState = (TextView) findViewById(R.id.devicestate);
		
		fwVersionText = (TextView)findViewById(R.id.fwVersionText);
		swVersionText = (TextView)findViewById(R.id.swVersionText);
		
		oneOne = (TextView) findViewById(R.id.oneone);
		oneTwo = (TextView) findViewById(R.id.onetwo);
		oneThree = (TextView) findViewById(R.id.onethree);
		
		twoOne = (TextView) findViewById(R.id.twoone);
		twoTwo = (TextView) findViewById(R.id.twotwo);
		twoThree = (TextView) findViewById(R.id.twothree);

		fourOne = (TextView) findViewById(R.id.fourone);
		fourTwo = (TextView) findViewById(R.id.fourtwo);
		fourThree = (TextView) findViewById(R.id.fourthree);
		
		threeOne = (TextView) findViewById(R.id.threeone);
		threeTwo = (TextView) findViewById(R.id.threetwo);
		threeThree = (TextView) findViewById(R.id.threethree);
		
		fiveOne = (TextView) findViewById(R.id.fiveone);
		fiveTwo = (TextView) findViewById(R.id.fivetwo);
		fiveThree = (TextView) findViewById(R.id.fivethree);
		
		sixOne = (TextView) findViewById(R.id.sixone);
		sixTwo = (TextView) findViewById(R.id.sixtwo);
		sixThree = (TextView) findViewById(R.id.sixthree);
		
		HomeInit();
		
		runBtn = (Button)findViewById(R.id.runbtn);
		runBtn.setOnClickListener(new View.OnClickListener() { 
		
			public void onClick(View v) {
			
				setButtonState(false);
				Start();
			}
		});
		
		settingBtn = (Button)findViewById(R.id.settingbtn);
		settingBtn.setOnClickListener(new View.OnClickListener() { 
		
			public void onClick(View v) {
			
				setButtonState(false);
				
				WhichIntent(TargetIntent.Setting);
			}
		});
		
		blankBtn = (Button)findViewById(R.id.blankbtn);
		blankBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				setButtonState(false);
				WhichIntent(TargetIntent.Blank);
			}
		});
		
		check2Btn = (ImageButton)findViewById(R.id.check2btn);
		check2Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PressedCheckBox(2);
			}
		});
		
		check3Btn = (ImageButton)findViewById(R.id.check3btn);
		check3Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PressedCheckBox(3);
			}
		});
		
		check4Btn = (ImageButton)findViewById(R.id.check4btn);
		check4Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PressedCheckBox(4);
			}
		});
		
		check5Btn = (ImageButton)findViewById(R.id.check5btn);
		check5Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PressedCheckBox(5);
			}
		});
		
		check6Btn = (ImageButton)findViewById(R.id.check6btn);
		check6Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PressedCheckBox(6);
			}
		});
		
		CheckBoxDisplay();
	}

	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 4)	DeviceStateDisplay1();
						else if (cnt == 9) {
							
							cnt = 0; 
							DeviceStateDisplay2();
						}
						
						if(ThreadState1) {
							ThreadState1 = false;
							AbsorbanceDisplay1();
						} else if(ThreadState2) {
							ThreadState2 = false;
							AbsorbanceDisplay2();
						} else if(ThreadState3) {
							ThreadState3 = false;
							AbsorbanceDisplay3();
						} else if(ThreadState4) {
							ThreadState4 = false;
							AbsorbanceDisplay4();
						} else if(ThreadState5) {
							ThreadState5 = false;
							AbsorbanceDisplay5();
						} else if(ThreadState6) {
							ThreadState6 = false;
							AbsorbanceDisplay6();
						} else if(ThreadStop) {
							Stop();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void CurrTimeDisplay() { // displaying current time
	
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		        		TimeText.setText(TimerDisplay.rTime[5] + " " + TimerDisplay.rTime[3] + ":" + TimerDisplay.rTime[4]);
		            }
		        });
		    }
		}).start();	
	}
	
	public void HomeInit() {
		
		if(!InitState) {

			InitState = true;
			
			/* Serial communication start */
			HomeSerial = new SerialPort();
			HomeSerial.BoardSerialInit();
			HomeSerial.BoardRxStart();
			
			/* Timer start */
			HomeTimer = new TimerDisplay();
			HomeTimer.TimerInit(); 
			HomeTimer.RealTime();
			
			ParameterInit();
			
			HomeTemp = new Temperature();
			HomeTemp.TmpInit();
		}
		
		TimerDisplay.timerState = whichClock.HomeClock;		
		CurrTimeDisplay();
		
		DisplayVersion mDisplayVersion = new DisplayVersion();
		mDisplayVersion.start();
	}
	
	public void Start() {
		
		BlankValueInit();
		
		AbsorbanceDisplay();
		
		TimerInit();
		
		HomeRun = new RunActivity();
		HomeRun.TestStart();		
	}
	
	public void Stop() {
		
		ThreadStop = false;
		
		timer.cancel();
		
		SerialPort.Sleep(300);
		
		deviceState.setTextColor(Color.parseColor("#008000"));
		deviceState.setText("READY");
		
		setButtonState(true);
	}
	
	public void DeviceStateDisplay1() {

    	deviceState.setTextColor(Color.parseColor("#DC143C"));
		deviceState.setText("TEST");
	}
	
	public void DeviceStateDisplay2() {
		
    	deviceState.setTextColor(Color.parseColor("#FFFFFF"));
		deviceState.setText("TEST");
	}
	
	private void CheckBoxDisplay() {
		
		if(Checkbox2)	check2Btn.setBackgroundResource(R.drawable.checkbox_s);
		if(Checkbox3)	check3Btn.setBackgroundResource(R.drawable.checkbox_s);
		if(Checkbox4)	check4Btn.setBackgroundResource(R.drawable.checkbox_s);
		if(Checkbox5)	check5Btn.setBackgroundResource(R.drawable.checkbox_s);
		if(Checkbox6)	check6Btn.setBackgroundResource(R.drawable.checkbox_s);
	}
	
	private void PressedCheckBox(int box) { // displaying the button pressed
		
		switch(box) {
		
		case 2	:
			if(!Checkbox2) { // whether or not box is checked

				Checkbox2 = true;
				check2Btn.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
				
			} else {

				Checkbox2 = false;
				check2Btn.setBackgroundResource(R.drawable.checkbox); // changing to not checked box
			}

			break;
			
		case 3	:
			if(!Checkbox3) { // whether or not box is checked

				Checkbox3 = true;
				check3Btn.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
				
			} else {

				Checkbox3 = false;
				check3Btn.setBackgroundResource(R.drawable.checkbox); // changing to not checked box
			}
			
			break;
			
		case 4	:
			if(!Checkbox4) { // whether or not box is checked

				Checkbox4 = true;
				check4Btn.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
				
			} else {

				Checkbox4 = false;
				check4Btn.setBackgroundResource(R.drawable.checkbox); // changing to not checked box
			}
			
			break;
			
		case 5	:
			if(!Checkbox5) { // whether or not box is checked

				Checkbox5 = true;
				check5Btn.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
				
			} else {

				Checkbox5 = false;
				check5Btn.setBackgroundResource(R.drawable.checkbox); // changing to not checked box
			}
			
			break;
			
		case 6	:
			if(!Checkbox6) { // whether or not box is checked

				Checkbox6 = true;
				check6Btn.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
				
			} else {

				Checkbox6 = false;
				check6Btn.setBackgroundResource(R.drawable.checkbox); // changing to not checked box
			}
			
			break;
			
		default	:
			break;
		}
	}
	
	public void AbsorbanceDisplay() {
		
    	oneOne.setText("");
    	oneTwo.setText("");
    	oneThree.setText("");
			
    	twoOne.setText("");
    	twoTwo.setText("");
    	twoThree.setText(""); 
		
    	threeOne.setText("");
    	threeTwo.setText("");
    	threeThree.setText("");
	
    	fourOne.setText("");
    	fourTwo.setText("");
    	fourThree.setText("");
		
		fiveOne.setText("");
    	fiveTwo.setText("");
    	fiveThree.setText("");
		
    	sixOne.setText("");
    	sixTwo.setText("");
    	sixThree.setText(""); 
	}
	
	public void AbsorbanceDisplay1() {
		
    	oneOne.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb[0]));
    	oneTwo.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb[1]));
    	oneThree.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb[2]));
	}
	
	public void AbsorbanceDisplay2() {
			
    	twoOne.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb2[0]));
    	twoTwo.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb2[1]));
    	twoThree.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb2[2])); 
	}
	
	public void AbsorbanceDisplay3() {
		
    	threeOne.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb3[0]));
    	threeTwo.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb3[1]));
    	threeThree.setText(hbA1cFormat.format(RunActivity.Step1stAbsorb3[2]));
	}
	
	public void AbsorbanceDisplay4() {

    	fourOne.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb[0]));
    	fourTwo.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb[1]));
    	fourThree.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb[2]));
	}
	
	public void AbsorbanceDisplay5() {
		
		fiveOne.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb2[0]));
    	fiveTwo.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb2[1]));
    	fiveThree.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb2[2]));
	}
	
	public void AbsorbanceDisplay6() {
		
    	sixOne.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb3[0]));
    	sixTwo.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb3[1]));
    	sixThree.setText(hbA1cFormat.format(RunActivity.Step2ndAbsorb3[2])); 
	}
	
	public void setButtonState(boolean state) {
		
		runBtn.setEnabled(state);
		settingBtn.setEnabled(state);
		blankBtn.setEnabled(state);
		check2Btn.setEnabled(state);
		check3Btn.setEnabled(state);
		check4Btn.setEnabled(state);
		check5Btn.setEnabled(state);
		check6Btn.setEnabled(state);
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Blank		:			
			Intent BlankIntent = new Intent(getApplicationContext(), BlankActivity.class); // BLANK activity activation
			startActivity(BlankIntent);
			overridePendingTransition(0, 0);
			finish();
			break;
						
		case Setting	:			
			Intent memoryIntent = new Intent(getApplicationContext(), SettingActivity.class); // Change to MEMORY Activity
			startActivity(memoryIntent);
			overridePendingTransition(0, 0);
			finish();
			break;
			
		default		:	
			break;			
		}		
	}
		
	public void ToastView(final String str) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	Toast.makeText(HomeActivity.this, str, Toast.LENGTH_SHORT).show();    	
		            }
		        });
		    }
		}).start(); 
	}
	
	private void ParameterInit() {
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		Temperature.tempSetValue = temperaturePref.getFloat("Cell Block", 27.0f);
		
		RunActivity.dataHandling = false;
		
		RunActivity.numofMeasure = 25;
		RunActivity.numofElimination = 10;
		
		RunActivity.st1RepeatValue = 5;
		RunActivity.st2RepeatValue = 5;
	}
	
	public void BlankValueInit() {
		
		SharedPreferences BlankPref = getSharedPreferences("Blank Value", MODE_PRIVATE);
		RunActivity.BlankValue[0] = Double.parseDouble(BlankPref.getString("BlankDark", "2995.6"));
		RunActivity.BlankValue[1] = Double.parseDouble(BlankPref.getString("Blank535nm", "203094.8")); // Dark Blank값을 제거하고 삽입
		RunActivity.BlankValue[2] = Double.parseDouble(BlankPref.getString("Blank660nm", "400540.0"));
		RunActivity.BlankValue[3] = Double.parseDouble(BlankPref.getString("Blank750nm", "634877.6"));	
	}
	
	public class DisplayVersion extends Thread {
		
		String swVersion, fwVersion;
		
		PackageInfo pi = null;
		
		public void run() {
						
			SerialPort.Sleep(200);
			
			try {

				pi = getPackageManager().getPackageInfo(getPackageName(), 0);

			} catch (NameNotFoundException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			swVersion = pi.versionName;
			
			fwVersion = getFwVersion();
			
			new Thread(new Runnable() {
				public void run() {
					runOnUiThread(new Runnable(){
						public void run() {
	
							swVersionText.setText("SW : " + swVersion);
							fwVersionText.setText("FW : " + fwVersion);
						}
					});
				}
			}).start();
		}
	}
	
	public String getFwVersion() {
		
		GetFwVersion mGetFwVersion = new GetFwVersion();
		mGetFwVersion.start();
		
		try {
			mGetFwVersion.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mGetFwVersion.getVersion();
	}
	
	public class GetFwVersion extends Thread {
		
		String version;
		
		public void run() {
			
			String temp;
			int cnt = 0;
			
			HomeSerial = new SerialPort();
			SerialPort.RxClear();
			HomeSerial.BoardTx(FW_VERSION, CtrTarget.PhotoSet);
			
			do {	
			
				temp = SerialPort.mReception.toString();

				Log.w("GetFwVersion", "temp : " + temp);
				
				if(cnt++ == 15) {
					
					temp = "Nothing";
					break;
				}
				
				SerialPort.Sleep(100);
			
			} while(temp.equals(""));
			
			version = temp;
		}
		
		public String getVersion() {
			
			return version;
		}
	}
}