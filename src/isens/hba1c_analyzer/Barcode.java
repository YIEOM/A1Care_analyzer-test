package isens.hba1c_analyzer;

import android.util.Log;

public class Barcode {

	private GpioPort BarcodeGpio;
	
	public void BarcodeCheck() {
		
		int len, testint, yearTint, yearUint, weekint, dayint, locateint, checkint, sum;
		char testchar, locatechar, tHbSlochar, tHbinterchar, BGSlochar, BGinterchar, A1cSlochar, A1cinterchar, calichar, checkchar;		
		
		len = SerialPort.mReception.length();
		
		Log.w("BarcodeDisplay", "len : " + len);
		try {
				
			testchar     = SerialPort.mReception.charAt(0);
			tHbSlochar   = SerialPort.mReception.charAt(6);
			tHbinterchar = SerialPort.mReception.charAt(7);
			BGSlochar    = SerialPort.mReception.charAt(8);
			BGinterchar  = SerialPort.mReception.charAt(9);
			A1cSlochar   = SerialPort.mReception.charAt(10);
			A1cinterchar = SerialPort.mReception.charAt(11);
			
			testint   = (int) testchar;
			yearTint  = (int) SerialPort.mReception.charAt(1);
			yearUint  = (int) SerialPort.mReception.charAt(2);
			weekint   = (int) SerialPort.mReception.charAt(3);
			dayint    = (int) SerialPort.mReception.charAt(4);
			locateint = (int) SerialPort.mReception.charAt(5);
			checkint  = (int) SerialPort.mReception.charAt(12) - 48;
			
			sum = ( testint + yearTint + yearUint + weekint + dayint + locateint ) % 10;
			
			if( sum == checkint ) {

				BarcodeStop();
				Log.w("BarcodeDisplay", "true : ");
			} else {

				Log.w("BarcodeDisplay", "false, sum : " + sum + " check : " + checkint);
			}
					
//			yint = Integer.parseInt(SerialPort.mReception.substring(0, 2));
//			wint = Integer.parseInt(SerialPort.mReception.substring(2, 4));
//			dint = Integer.parseInt(SerialPort.mReception.substring(4, 5));
//			cint = Integer.parseInt(SerialPort.mReception.substring(len-4, len-2));
			
//			sum = yint+wint+dint;
//			if(sum == cint) {
//								
//				Log.w("BarcodeDisplay", "true : ");
//			} else {
//
//				Log.w("BarcodeDisplay", "false : " + sum);
//			}
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	
	private void BarcodeStop() {
		
//		ActionActivity.BarcodeState = false;
		Log.w("BarcodeStop", "Stop");

		BarcodeGpio = new GpioPort();
		BarcodeGpio.TriggerHigh();
		
		SerialPort.bBarcodeRxThread.interrupt();
		
//		ActionActivity.BarcodeCheckFlag = true;
	}
}
