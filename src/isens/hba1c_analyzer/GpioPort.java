package isens.hba1c_analyzer;

import android.util.Log;

public class GpioPort {

	private enum SensorScan {InitialState, DebounceHLState, SWPressedState, ReleaseState, DebounceLHState}
	
	private static SensorScan SensorState = SensorScan.InitialState;
	
	public void TriggerHigh () {

		int value;
		Open();
		value = GpioControl(1, 1);
//		Log.w("TriggerHigh", "Value : " + value);
		Close();
	}
	
	public void TriggerLow () {
		
		int value;
		Open();
		value = GpioControl(1, 0);
//		Log.w("TriggerLow", "Value : " + value);
		Close();
	}
	
	private int CoverRead () {
		
		int value;
		Open();
		value = GpioControl(3, 0);
		Close();
//		Log.w("CoverRead", "Value : " + value);
		
		return value;
	}
	
	private boolean AnySensorIn () {
		
		if( CoverRead() == 0 ) return true;
//		if() return true; // Cartridge Sensor
		
		return false;
	}
	
	private void GetSensor () {
		
		if( CoverRead() == 0 ) {
	
//			ActionActivity.CoverCheckFlag = true;
//		} else if (  ) {
			
//			ActionActivity.CoverCheckFlag = false;
		} else {
			
//			ActionActivity.CoverCheckFlag = false;
		}
	}	
	
	public void SensorScan() {
			
		switch(SensorState) {
		
		case InitialState		:
			if( AnySensorIn() ) SensorState = SensorScan.DebounceHLState;
			break;
		
		case DebounceHLState	:	
			SensorState = ( AnySensorIn() ) ? SensorScan.SWPressedState : SensorScan.InitialState;
			break;
									
		case SWPressedState		:
			SensorState = SensorScan.ReleaseState;
			break;
			
		case ReleaseState		:
			if( !AnySensorIn() ) SensorState = SensorScan.DebounceLHState;
			GetSensor();
			break;
			
		case DebounceLHState	:
			SensorState = ( AnySensorIn() ) ? SensorScan.ReleaseState : SensorScan.InitialState;
			break;
									
		default :
			SensorState = SensorScan.InitialState;
			break;
		}
	}
	
	static	{
		
		System.loadLibrary("gpio_port");
	}
	
	public native int Open();
	public native int Close();
	public native int GpioControl(int gpioNum, int gpioHighLow);
	public native int unimplementedOpen();
	public native int unimplementedClose();
	public native int unimplementedGpioControl(int gpioNum, int gpioHighLow);
}
