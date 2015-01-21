package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Temperature extends SerialPort {

	public TextView TmpText;
	private Toast toast;
	
//	final static double InitTmp = (double) 36.5;
	
	public static double tempReadValue;
	public static String ADTempRead;
	public static double tempSetValue;
	public static String ADTempSet;
	
	public void TmpInit() {
		
		Log.w("TmpInit", "run");
		double tmpDouble;
		String tmpString;
		DecimalFormat tmpFormat;
		
		tmpDouble = (tempSetValue * (double) 1670.17) + (double) 25891.34;
		tmpFormat = new DecimalFormat("#####0");
		if(tmpFormat.format(tmpDouble).length() == 5) tmpString = "0" + tmpFormat.format(tmpDouble);
		else tmpString = tmpFormat.format(tmpDouble);
		BoardTx(tmpString, CtrTarget.TmpSet);
		ADTempSet = tmpString;
	}
	
	public Double TmpRead() {
		
		double tmpRaw;
		double tmpDouble;

//		Log.w("TmpRead", "run");
	
		RxClear();
		BoardTx("", CtrTarget.TmpCall);
		Sleep(1000);
		
		ADTempRead = mReception.toString();
		tmpRaw = Double.parseDouble(mReception.toString());
//		tmpRaw = 75600;
//		Log.w("TmpRead", "Double mReception : " + tmpRaw);
		tmpDouble = (tmpRaw / (double) 1670.17) - (double) 15.5;
		
		return tmpDouble;
	}	
}
