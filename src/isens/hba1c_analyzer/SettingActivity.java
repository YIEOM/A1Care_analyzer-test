package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import isens.hba1c_analyzer.BlankActivity.BlankStep;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingActivity extends Activity {
	
	private Temperature SettingTemp;
	
	private Button backIcon;
	
	private TextView tempReadView;
	private TextView aDTempReadView;
	private Button tempreadBtn;
	
	private Button tempplusBtn;
	private TextView tempSetView;
	private TextView aDTempSetView;
	private Button tempminusBtn;
	private Button tempsetBtn;
	
	private Button st1repeatplusBtn;
	private EditText st1RepeatValueEdit;
	private Button st1repeatminusBtn;
	
	private Button st2repeatplusBtn;
	private EditText st2RepeatValueEdit;
	private Button st2repeatminusBtn;
	
	private Button measure15Btn;
	private Button measure11Btn;
	private Button measure7Btn;

	private Button whichButton;
	
	private Button dataHandlingBtn;
	
	public static TextView TimeText;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		st1RepeatValueEdit = (EditText) findViewById(R.id.st1repeatvalue);
		st1RepeatValueEdit.addTextChangedListener(st1RepeatValueWatcher);
	
		st2RepeatValueEdit = (EditText) findViewById(R.id.st2repeatvalue);
		st2RepeatValueEdit.addTextChangedListener(st2RepeatValueWatcher);
		
		tempReadView = (TextView) findViewById(R.id.tempread);
		aDTempReadView = (TextView) findViewById(R.id.adtempread);
		tempSetView = (TextView) findViewById(R.id.tempset);
		aDTempSetView = (TextView) findViewById(R.id.adtempset);
		
		SettingInit();
		
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				backIcon.setEnabled(false);
				Intent HomeIntent = new Intent(SettingActivity.this, HomeActivity.class);
				startActivity(HomeIntent);
				overridePendingTransition(0, 0);
				finish();
			}
		});
			
		tempreadBtn = (Button)findViewById(R.id.tempreadbtn);
		tempreadBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				TempRead();
			}
		});
		
		tempplusBtn = (Button)findViewById(R.id.tempplusbtn);
		tempplusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				Temperature.tempSetValue = Temperature.tempSetValue + 0.1;
				tempSetView.setText(TempFormat(Temperature.tempSetValue));
			}
		});
		
		tempminusBtn = (Button)findViewById(R.id.tempminusbtn);
		tempminusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				Temperature.tempSetValue = Temperature.tempSetValue - 0.1;
				tempSetView.setText(TempFormat(Temperature.tempSetValue));
			}
		});
		
		tempsetBtn = (Button)findViewById(R.id.tempsetbtn);
		tempsetBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				TempSet();	
			}
		});
		
		st1repeatplusBtn = (Button)findViewById(R.id.st1repeatplusbtn);
		st1repeatplusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				st1RepeatValueEdit.setText(Integer.toString(++RunActivity.st1RepeatValue));
			}
		});
		
		st1repeatminusBtn = (Button)findViewById(R.id.st1repeatminusbtn);
		st1repeatminusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				st1RepeatValueEdit.setText(Integer.toString(--RunActivity.st1RepeatValue));
			}
		});
		
		st2repeatplusBtn = (Button)findViewById(R.id.st2repeatplusbtn);
		st2repeatplusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				st2RepeatValueEdit.setText(Integer.toString(++RunActivity.st2RepeatValue));
			}
		});
		
		st2repeatminusBtn = (Button)findViewById(R.id.st2repeatminusbtn);
		st2repeatminusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				st2RepeatValueEdit.setText(Integer.toString(--RunActivity.st2RepeatValue));
			}
		});
		
		measure15Btn = (Button)findViewById(R.id.measurement15btn);
		measure15Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				RunActivity.numofElimination = 10;
				MeasurementBtnDisplay();
			}
		});
		
		measure11Btn = (Button)findViewById(R.id.measurement11btn);
		measure11Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				RunActivity.numofElimination = 14;
				MeasurementBtnDisplay();
			}
		});
		
		measure7Btn = (Button)findViewById(R.id.measurement7btn);
		measure7Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				RunActivity.numofElimination = 18;
				MeasurementBtnDisplay();
			}
		});
		
		dataHandlingBtn = (Button)findViewById(R.id.datahandling);
		dataHandlingBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
//				if(RunActivity.dataHandling) {
//					
//					RunActivity.dataHandling = false;
//					dataHandlingBtn.setText("AVERAGE");
//				} else {
//					
//					RunActivity.dataHandling = true;
//					dataHandlingBtn.setText("ELIMINATION");
//				}
			}
		});
		
		MeasurementBtnDisplay();
//		DataHandlingBtnDisplay();
	}
	
	private TextWatcher st1RepeatValueWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try{
				
				RunActivity.st1RepeatValue = Integer.parseInt(st1RepeatValueEdit.getText().toString());
				
				if(RunActivity.st1RepeatValue > 999) {
					
					RunActivity.st1RepeatValue = 999;
					st1RepeatValueEdit.setText(Integer.toString(RunActivity.st1RepeatValue));
					
				}else if(RunActivity.st1RepeatValue < 1) {
					RunActivity.st1RepeatValue = 1;
					st1RepeatValueEdit.setText(Integer.toString(RunActivity.st1RepeatValue));
				}
			} catch (NumberFormatException e) {
				return;
			}			
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		}
	};	
	
	private TextWatcher st2RepeatValueWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try{
			
				RunActivity.st2RepeatValue = Integer.parseInt(st2RepeatValueEdit.getText().toString());
				
				if(RunActivity.st2RepeatValue > 999) {
					
					RunActivity.st2RepeatValue = 999;
					st2RepeatValueEdit.setText(Integer.toString(RunActivity.st2RepeatValue));
				}else if(RunActivity.st2RepeatValue < 1) {
					
					RunActivity.st2RepeatValue = 1;
					st2RepeatValueEdit.setText(Integer.toString(RunActivity.st2RepeatValue));
				}					
			} catch (NumberFormatException e) {
				return;
			}			
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		}
	};
	
	public void CurrTimeDisplay() {
		
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
	
	private void SettingInit() {
		
		TimerDisplay.timerState = whichClock.SettingClock;		
		CurrTimeDisplay();
		
		ParameterDisplay();
		
		aDTempSetView.setText(Temperature.ADTempSet);
		SerialPort.Sleep(100);
		TempRead();
	}
	
	private void ParameterDisplay() {
		
		double tmpSetValue;
		
		st1RepeatValueEdit.setText(Integer.toString(RunActivity.st1RepeatValue));
		st2RepeatValueEdit.setText(Integer.toString(RunActivity.st2RepeatValue));
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		tmpSetValue = temperaturePref.getFloat("Cell Block", 27.0f);
		
		tempSetView.setText(TempFormat(tmpSetValue));
	}
	
	private void DataHandlingBtnDisplay() {
		
		if(RunActivity.dataHandling) {
			
			dataHandlingBtn.setText("ELIMINATION");
		} else {
			
			dataHandlingBtn.setText("AVERAGE");
		}
	}

	private String TempFormat(double tmpDouble) {

		DecimalFormat tmpFormat;
		String tmpString;
		
		tmpFormat = new DecimalFormat("#.0");		
		tmpString = tmpFormat.format(tmpDouble);
		
		return tmpString;
	}
	
	private void TempRead() {
		
		SettingTemp = new Temperature();
		Temperature.tempReadValue = SettingTemp.TmpRead();
		tempReadView.setText(TempFormat(Temperature.tempReadValue));
		aDTempReadView.setText(Temperature.ADTempRead);
	}
	
	private void TempSet() {
		
		SettingTemp = new Temperature();
		SettingTemp.TmpInit();
		aDTempSetView.setText(Temperature.ADTempSet);
		
		TmpSave(Float.valueOf(tempSetView.getText().toString()).floatValue());
	}
	
	private void MeasurementBtnDisplay() {
		
		switch(RunActivity.numofElimination) {
		
		case 10 : 
			measure7Btn.setBackgroundResource(R.drawable.btn8);
			measure11Btn.setBackgroundResource(R.drawable.btn8);
			measure15Btn.setBackgroundResource(R.drawable.btn8_s);
			break;
			
		case 14 : 
			measure7Btn.setBackgroundResource(R.drawable.btn8);
			measure11Btn.setBackgroundResource(R.drawable.btn8_s);
			measure15Btn.setBackgroundResource(R.drawable.btn8);
			break;
			
		case 18 : 
			measure7Btn.setBackgroundResource(R.drawable.btn8_s);
			measure11Btn.setBackgroundResource(R.drawable.btn8);
			measure15Btn.setBackgroundResource(R.drawable.btn8);
			break;
			
		default :
			break;
		}
	}
	
	public void TmpSave(float tmp) {
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		SharedPreferences.Editor temperatureedit = temperaturePref.edit();
		
		temperatureedit.putFloat("Cell Block", tmp);
		temperatureedit.commit();
	}
}
