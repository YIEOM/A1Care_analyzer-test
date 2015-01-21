package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.Cart1stStep;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.TimerDisplay.whichClock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class RunActivity {
	
	final static String HOME_POSITION    = "CH"; //CO
	final static String MEASURE_POSITION = "CM"; //MO
	final static String Step1st_POSITION = "C1";
	final static String Step2nd_POSITION = "C2";
	final static String CARTRIDGE_DUMP   = "CD";
	final static String FILTER_DARK      = "FD"; //DO
//	final static String FILTER_SPto535nm = "FR"; //RO
	final static String FILTER_535nm     = "AR"; 
	final static String FILTER_660nm     = "FG"; //AG
	final static String FILTER_750nm     = "FB"; //AB	
	final static String OPERATE_COMPLETE = "DO";
	final static String MOTOR_COMPLETE   = "AR";
	final static String NEXT_FILTER		 = "FS";
	
	final static byte FIRST = 1;
	final static byte SECOND = 2;
		
	public SerialPort TestSerial;
	public HomeActivity RunHome;
	
	public static TextView TimeText;
	public static TextView RunTimeText;
	
	public static double BlankValue[]     = new double[4];
	public static double Step1stValue[]   = new double[3];
	public static double Step1stValue2[]  = new double[3];
	public static double Step1stValue3[]  = new double[3];
	public static double Step2ndValue[]   = new double[3];
	public static double Step2ndValue2[]  = new double[3];
	public static double Step2ndValue3[]  = new double[3];
	public static double Step1stAbsorb[]  = new double[3];
	public static double Step1stAbsorb2[] = new double[3];
	public static double Step1stAbsorb3[] = new double[3];
	public static double Step2ndAbsorb[]  = new double[3];
	public static double Step2ndAbsorb2[] = new double[3];
	public static double Step2ndAbsorb3[] = new double[3];
	
	public static ArrayList<Double> MeasureValueList = new ArrayList<Double>(25); 
	
	public static byte measureCnt = FIRST;
	public static byte runSec = 0;
	public static byte runMin = 0;
	
	public static int st1RepeatValue;
	public static int st2RepeatValue;
	
	public static int numofMeasure;
	public static int numofElimination;
	
	public static boolean dataHandling;
	
	public static double HbA1cPctDbl;
	public static String HbA1cPctStr = "0.00";
	
	public static double testSum;

	public void TestStart() {
		
		TestSerial = new SerialPort();
		
		MeasureValueList.clear();
		
		HomeActivity.ThreadState1 = false;
		
		Cart1stStep Test1stStep = new Cart1stStep();
		Test1stStep.start();
	}
	
	public class Cart1stStep extends Thread {

		public void run() {
			
			MotionInstruct(Step1st_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!Step1st_POSITION.equals(SerialPort.mReception.toString()));
		
			MotorMotionInstruct(st1RepeatValue, SerialPort.CtrTarget.MotorSet);
			while(!MOTOR_COMPLETE.equals(SerialPort.mReception.toString()));
			SerialPort.Sleep(2000);
			
			MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!MEASURE_POSITION.equals(SerialPort.mReception.toString()));
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 535nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step1stValue[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));
			
			AbsorbCal1st();
			
			if(HomeActivity.Checkbox2) {
				
				HomeActivity.ThreadState2 = false;
				
				Cart1stStep2 Test1stStep2 = new Cart1stStep2();
				Test1stStep2.start();
			} else if(HomeActivity.Checkbox4) {
				
				HomeActivity.ThreadState4 = false;
				
				Cart2ndStep Test2ndStep = new Cart2ndStep();
				Test2ndStep.start();
			} else {
				
				CartArrangement TestArrangement = new CartArrangement();
				TestArrangement.start();
			}
		}
	}
	
	public class Cart1stStep2 extends Thread {

		public void run() {
			
			SerialPort.Sleep(1000);
			
//			Log.w("Cart1stStep2", "run");
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 535nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue2[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue2[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step1stValue2[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));
			
			AbsorbCal1st2();
			
			if(HomeActivity.Checkbox3) {
				
				HomeActivity.ThreadState3 = false;
				
				Cart1stStep3 Test1stStep3 = new Cart1stStep3();
				Test1stStep3.start();
			} else if(HomeActivity.Checkbox4) {
				
				HomeActivity.ThreadState4 = false;
				
				Cart2ndStep Test2ndStep = new Cart2ndStep();
				Test2ndStep.start();
			} else {
				
				CartArrangement TestArrangement = new CartArrangement();
				TestArrangement.start();
			}
		}
	}
	
	public class Cart1stStep3 extends Thread {

		public void run() {

			SerialPort.Sleep(1000);
			
//			Log.w("Cart1stStep3", "run");
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 535nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue3[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step1stValue3[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step1stValue3[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));
			
			AbsorbCal1st3();
			
			if(HomeActivity.Checkbox4) {
				
				HomeActivity.ThreadState4 = false;
				
				Cart2ndStep Test2ndStep = new Cart2ndStep();
				Test2ndStep.start();
			} else {
				
				CartArrangement TestArrangement = new CartArrangement();
				TestArrangement.start();
			}
		}
	}
	
	public class Cart2ndStep extends Thread {
		
		public void run() {			
			
//			Log.w("Cart2ndStep", "run");
			MotionInstruct(Step2nd_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!Step2nd_POSITION.equals(SerialPort.mReception.toString()));	
			
			MotorMotionInstruct(st2RepeatValue, SerialPort.CtrTarget.MotorSet);
			while(!MOTOR_COMPLETE.equals(SerialPort.mReception.toString()));
			SerialPort.Sleep(2000);
			
			MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!MEASURE_POSITION.equals(SerialPort.mReception.toString()));
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 535nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));
			
			AbsorbCal2nd();
			
			if(HomeActivity.Checkbox5) {
				
				HomeActivity.ThreadState5 = false;
				
				Cart2ndStep2 Test2ndStep2 = new Cart2ndStep2();
				Test2ndStep2.start();
			} else {
				
				CartArrangement TestArrangement = new CartArrangement();
				TestArrangement.start();
			}
		}
	}
	
	public class Cart2ndStep2 extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(1000);
			
//			Log.w("Cart2ndStep2", "run");
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 535nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue2[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue2[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue2[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));
			
			AbsorbCal2nd2();
			
			if(HomeActivity.Checkbox6) {
				
				HomeActivity.ThreadState6 = false;
				
				Cart2ndStep3 Test2ndStep3 = new Cart2ndStep3();
				Test2ndStep3.start();
			} else {
				
				CartArrangement TestArrangement = new CartArrangement();
				TestArrangement.start();
			}
		}
	}
	
	public class Cart2ndStep3 extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(1000);
			
//			Log.w("Cart2ndStep3", "run");
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 532nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue3[0] = AbsorbanceChange(); // 535nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 660nm measurement
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue3[1] = AbsorbanceChange(); // 660nm Absorbance
			while(!NEXT_FILTER.equals(SerialPort.mReception.toString()));
			AbsorbanceMeasure(); // 750nm measurement
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			Step2ndValue3[2]  = AbsorbanceChange(); // 750nm Absorbance
			while(!FILTER_DARK.equals(SerialPort.mReception.toString()));

			AbsorbCal2nd3();
				
			CartArrangement TestArrangement = new CartArrangement();
			TestArrangement.start();
		}
	}
	
	public class CartArrangement extends Thread {
		
		public void run() {
			
			MotionInstruct(CARTRIDGE_DUMP, SerialPort.CtrTarget.PhotoSet);			
			while(!CARTRIDGE_DUMP.equals(SerialPort.mReception.toString()));				
			
			MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!HOME_POSITION.equals(SerialPort.mReception.toString()));
			
			HomeActivity.ThreadStop = true;
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) {
		
		SerialPort.RxClear();
		TestSerial.BoardTx(str, target);
	}
	
	public void MotorMotionInstruct(int value, SerialPort.CtrTarget target) {
		
		SerialPort.RxClear();
		TestSerial.BoardTx(MotorRepeatFormat(value), target);
	}
	
	public synchronized void AbsorbanceMeasure() {
	
		String rawValue;
		double douValue;
		
		SerialPort.RxClear();
		
		if(numofMeasure > 18) {
			
			TestSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 72);
			TestSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 144);
			TestSerial.BoardTx("VP" + Integer.toString(numofMeasure-18), SerialPort.CtrTarget.PhotoSet);
		
		} else if(numofMeasure > 9) {
			
			TestSerial.BoardTx("VP9" , SerialPort.CtrTarget.PhotoSet);
			while(SerialPort.mReception.length() != 72);
			TestSerial.BoardTx("VP" + Integer.toString(numofMeasure-9), SerialPort.CtrTarget.PhotoSet);
		
		} else {
			
			TestSerial.BoardTx("VP" + Integer.toString(numofMeasure), SerialPort.CtrTarget.PhotoSet);
		}
		
		while(SerialPort.mReception.length() != RunActivity.numofMeasure*8){
			
			SerialPort.Sleep(100);
		}
		
//		Log.w("AbsorbanceMeasure", "" + SerialPort.mReception);
		for(int i = 0; i < numofMeasure-1; i++) {
//			Log.w("AbsorbanceMeasure", "" + SerialPort.mReception.substring(i*8, (i+1)*8));
			rawValue = SerialPort.mReception.substring(i*8, (i+1)*8);
			douValue = Double.parseDouble(rawValue);
			MeasureValueList.add(douValue);
//			Log.w("AbsorbanceMeasure", "Double Value[" + i + "] :" + douValue);a
		}
		
		rawValue = SerialPort.mReception.substring(numofMeasure*8-8);
		douValue = Double.parseDouble(rawValue);
		MeasureValueList.add(douValue);
//		Log.w("AbsorbanceMeasure", "MeasureValueList : " + MeasureValueList);
	}
	
	public synchronized double AbsorbanceChange() {
		
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		int maxIndex = 0;
		int minIndex = 0;
		double sum = 0;
		double avg = 0;
		int numofElimi = numofElimination/2;
		
		for(int j = 0; j < numofElimi; j++) {
			
			for(int i = 0; i < MeasureValueList.size(); i++) {
				
				if(MeasureValueList.get(i) > max) {
					
					max = MeasureValueList.get(i);
					maxIndex = i;
				}
				
				if(MeasureValueList.get(i) < min) {
					
					min = MeasureValueList.get(i);
					minIndex = i;
				}
			}
			
			MeasureValueList.remove(maxIndex);
			
			if(maxIndex < minIndex) {
			
				MeasureValueList.remove(minIndex - 1);
			
			} else {
				
				MeasureValueList.remove(minIndex);
			}
			
			max = Double.MIN_VALUE;
			min = Double.MAX_VALUE;
			maxIndex = 0;
			minIndex = 0;
		}
		
		for(double k : MeasureValueList) {
			
			sum += k;
		}
		
		avg = sum / MeasureValueList.size();
		
		testSum = sum;
		MeasureValueList.clear();
//		Log.w("AbsorbanceChange", "avg : " + avg);
		return (avg - BlankValue[0]); // Average value - dark blank value
	}
	
	public synchronized void AbsorbCal1st() {
		
		Step1stAbsorb[0]  = -Math.log10(Step1stValue[0]/BlankValue[1]);
		Step1stAbsorb[1]  = -Math.log10(Step1stValue[1]/BlankValue[2]);
		Step1stAbsorb[2]  = -Math.log10(Step1stValue[2]/BlankValue[3]);
		
		HomeActivity.ThreadState1 = true;
	}

	public synchronized void AbsorbCal1st2() {
		
		Step1stAbsorb2[0] = -Math.log10(Step1stValue2[0]/BlankValue[1]);
		Step1stAbsorb2[1] = -Math.log10(Step1stValue2[1]/BlankValue[2]);
		Step1stAbsorb2[2] = -Math.log10(Step1stValue2[2]/BlankValue[3]);
		
		HomeActivity.ThreadState2 = true;
	}
	
	public synchronized void AbsorbCal1st3() {
		
		Step1stAbsorb3[0] = -Math.log10(Step1stValue3[0]/BlankValue[1]);
		Step1stAbsorb3[1] = -Math.log10(Step1stValue3[1]/BlankValue[2]);
		Step1stAbsorb3[2] = -Math.log10(Step1stValue3[2]/BlankValue[3]);
		
		HomeActivity.ThreadState3 = true;
	}
	
	public synchronized void AbsorbCal2nd() {
		
		Step2ndAbsorb[0]  = -Math.log10(Step2ndValue[0]/BlankValue[1]); // 535nm
		Step2ndAbsorb[1]  = -Math.log10(Step2ndValue[1]/BlankValue[2]); // 660nm
		Step2ndAbsorb[2]  = -Math.log10(Step2ndValue[2]/BlankValue[3]); // 750nm
		
		HomeActivity.ThreadState4 = true;
	}
	
	public synchronized void AbsorbCal2nd2() {
		
		Step2ndAbsorb2[0] = -Math.log10(Step2ndValue2[0]/BlankValue[1]);
		Step2ndAbsorb2[1] = -Math.log10(Step2ndValue2[1]/BlankValue[2]);
		Step2ndAbsorb2[2] = -Math.log10(Step2ndValue2[2]/BlankValue[3]);
		
		HomeActivity.ThreadState5 = true;
	}
	
	public synchronized void AbsorbCal2nd3() {
				
		Step2ndAbsorb3[0] = -Math.log10(Step2ndValue3[0]/BlankValue[1]);
		Step2ndAbsorb3[1] = -Math.log10(Step2ndValue3[1]/BlankValue[2]);
		Step2ndAbsorb3[2] = -Math.log10(Step2ndValue3[2]/BlankValue[3]);
		
		HomeActivity.ThreadState6 = true;
	}
	
	private String MotorRepeatFormat(int sec) {
		
		double secConv;
		String secStr = "0000";
		DecimalFormat secFormat = new DecimalFormat("#");
		
		secConv = (double) (sec * 6);
		
		if(10 > secConv) {
			
			secStr = "000" + secFormat.format(secConv);
		} else if(100 > secConv) {
			
			secStr = "00" + secFormat.format(secConv);
		} else if(1000 > secConv) {
			
			secStr = "0" + secFormat.format(secConv);
		} else if(10000 > secConv) {
			
			secStr = secFormat.format(secConv);
		}
		
		return secStr;
	}
}
