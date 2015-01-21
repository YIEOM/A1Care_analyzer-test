package isens.hba1c_analyzer;

import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlankActivity extends Activity {

	public RunActivity BlankTest;
	public SerialPort BlankSerial;
	
	public static TextView TimeText;
	
	public ImageView barani;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		BlankInit();
	}                     
	
	public class BlankStep extends Thread { // BLANK run
		
		public void run() {
			
//			Log.w("BlankStep", "run");
			MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
			while(!RunActivity.MEASURE_POSITION.equals(SerialPort.mReception.toString()));
			BarAnimation(178);
			
			/* Dark filter Measurement */
//			Log.w("BlankStep", "Measure position");
			MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.FILTER_DARK.equals(SerialPort.mReception.toString()));
			BarAnimation(206);
			AbsorbanceMeasure();
//			ToastView("dark : " + Raw);
			BarAnimation(234);
			
			/* 535nm filter Measurement */
//			Log.w("BlankStep", "535nm start");
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(262);
			RunActivity.BlankValue[0] = 0;
			RunActivity.BlankValue[0] = BlankTest.AbsorbanceChange(); // Dark Absorbance
			ToastView(Double.toString(RunActivity.testSum));
//			Log.w("BlankStep", "DarkValue : " + RunActivity.BlankValue[0]);
			while(!RunActivity.NEXT_FILTER.equals(SerialPort.mReception.toString()));
			BarAnimation(290);
//			Log.w("BlankStep", "535nm complete");
			AbsorbanceMeasure();
//			ToastView("535nm : " + Raw);
			BarAnimation(318);
			
			/* 660nm filter Measurement */
//			Log.w("BlankStep", "660nm start");
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(346);
			RunActivity.BlankValue[1] = BlankTest.AbsorbanceChange(); // 535nm Absorbance
			ToastView(Double.toString(RunActivity.testSum));
//			Log.w("BlankStep", "535nmValue : " + RunActivity.BlankValue[1]);
			while(!RunActivity.NEXT_FILTER.equals(SerialPort.mReception.toString()));
			BarAnimation(374);
//			Log.w("BlankStep", "660nm complete");
			AbsorbanceMeasure();
//			ToastView("660nm : " + Raw);
			BarAnimation(402);
			
			/* 750nm filter Measurement */
//			Log.w("BlankStep", "750nm start");
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(430);
			RunActivity.BlankValue[2] = BlankTest.AbsorbanceChange(); // 660nm Absorbance
			ToastView(Double.toString(RunActivity.testSum));
//			Log.w("BlankStep", "660nmValue : " + RunActivity.BlankValue[2]);
			while(!RunActivity.NEXT_FILTER.equals(SerialPort.mReception.toString()));
			BarAnimation(458);
//			Log.w("BlankStep", "750nm complete");
			AbsorbanceMeasure();
//			ToastView("750nm : " + Raw);
			BarAnimation(486);
			
			/* Return to the original position */
			MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(514);
			RunActivity.BlankValue[3] = BlankTest.AbsorbanceChange(); // 750nm Absorbance
			ToastView(Double.toString(RunActivity.testSum));
//			Log.w("BlankStep", "750nmValue : " + RunActivity.BlankValue[3]);
			while(!RunActivity.FILTER_DARK.equals(SerialPort.mReception.toString()));
			BarAnimation(542);
			MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.HOME_POSITION.equals(SerialPort.mReception.toString()));
			BarAnimation(579);
			
			SerialPort.Sleep(1000);
			
			BlankValueSave();
						
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			overridePendingTransition(0, 0);
			finish();
		}
	}
	
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
	
	private void BlankInit() {
		
		TimerDisplay.timerState = whichClock.BlankClock;		
		CurrTimeDisplay();
		
		BlankSerial = new SerialPort();
		BlankTest = new RunActivity();
		
		RunActivity.MeasureValueList.clear();
		
		BlankStep BlankBlank = new BlankStep();
		BlankBlank.start();
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		SerialPort.RxClear();
		BlankSerial.BoardTx(str, target);
	}
	
	public synchronized void AbsorbanceMeasure() {
		
		String rawValue;
		double douValue;
		
		SerialPort.RxClear();
		
		if(RunActivity.numofMeasure > 18) {
			
			BlankSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 72);
			BlankSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 144);
			BlankSerial.BoardTx("VP" + Integer.toString(RunActivity.numofMeasure-18), SerialPort.CtrTarget.PhotoSet);
			
		} else if(RunActivity.numofMeasure > 9) {
			
			BlankSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 72);
			BlankSerial.BoardTx("VP" + Integer.toString(RunActivity.numofMeasure-9), SerialPort.CtrTarget.PhotoSet);
			
		} else {
			
			BlankSerial.BoardTx("VP" + Integer.toString(RunActivity.numofMeasure), SerialPort.CtrTarget.PhotoSet);
		}
		
		while(SerialPort.mReception.length() != RunActivity.numofMeasure*8) {
			
//			ToastView(Integer.toString(SerialPort.mReception.length()));
			SerialPort.Sleep(100);
		}
		
//		Log.w("AbsorbanceMeasure", "" + SerialPort.mReception);
		for(int i = 0; i < RunActivity.numofMeasure-1; i++) {
//			Log.w("AbsorbanceMeasure", "" + SerialPort.mReception.substring(i*8, (i+1)*8));
			rawValue = SerialPort.mReception.substring(i*8, (i+1)*8);
			douValue = Double.parseDouble(rawValue);
			RunActivity.MeasureValueList.add(douValue);
//			Log.w("AbsorbanceMeasure", "Double Value[" + i + "] :" + douValue);a
		}
		
		rawValue = SerialPort.mReception.substring(RunActivity.numofMeasure*8-8);
		douValue = Double.parseDouble(rawValue);
		RunActivity.MeasureValueList.add(douValue);
//		Log.w("AbsorbanceMeasure", "MeasureValueList : " + MeasureValueList);
	}
	
	public void BlankValueSave() { // Blank data store
		
		SharedPreferences BlankPref = getSharedPreferences("Blank Value", MODE_PRIVATE);
		SharedPreferences.Editor edit = BlankPref.edit();
		
		edit.putString("BlankDark", Double.toString(RunActivity.BlankValue[0]));
		edit.putString("Blank535nm", Double.toString(RunActivity.BlankValue[1]));
		edit.putString("Blank660nm", Double.toString(RunActivity.BlankValue[2]));
		edit.putString("Blank750nm", Double.toString(RunActivity.BlankValue[3]));
		
		edit.commit();
	}
	
	public void BarAnimation(final int x) {

		barani = (ImageView) findViewById(R.id.progressBar);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(barani.getLayoutParams());
		            	margin.setMargins(x, 80, 0, 0);
		            	barani.setLayoutParams(new RelativeLayout.LayoutParams(margin));
		            }
		        });
		    }
		}).start();	
	}
		
	public void ToastView(final String str) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	Toast.makeText(BlankActivity.this, str, Toast.LENGTH_SHORT).show();    	
		            }
		        });
		    }
		}).start(); 
	}
}
