package isens.hba1c_analyzer;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class SerialPort {
	
	Barcode SerialBarcode;
	
	/* Board Serial set-up */
	private static FileDescriptor mFd;
	private static FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	
	private BoardTxThread mBoardTxThread;
	private static BoardRxThread mBoardRxThread;
	
	public static StringBuffer mReception  = new StringBuffer();
	
	/* Printer Serial set-up */
	private static FileDescriptor pFd;
	private FileOutputStream pFileOutputStream;
	
	private PrinterTxThread pPrinterTxThread;
	
	/* Barcode Serial set-up */
	private static FileDescriptor bFd;
	private static FileInputStream bFileInputStream;
	private FileOutputStream bFileOutputStream;
	
	private BarcodeTxThread bBarcodeTxThread;
	public static BarcodeRxThread bBarcodeRxThread;
	
	public enum CtrTarget {PhotoSet, TmpSet, TmpCall, MotorSet}
	public enum RxTarget {Board, Barcode}
	
	final static byte STX = 0x02;
	final static byte ETX = 0x03;
	final static byte LF = 0x0A;
	final static byte CR = 0x0D;
	final static byte ESC = 0x1B;
	final static byte GS = 0x1D;
		
	private static byte rxBuffer[] = new byte[64];
	private static byte appendBuffer[][] = new byte[64][128];
	
	public static byte debugBuffer[] = new byte[20];
	public static byte debugIndex = 0;
	public static byte debugFlag = 0;
	
	private static byte bufCnt = 0;
	private static byte bufIndex = 0;
	private static byte AppendCnt = 0;
	private static byte barcodeCnt = 0;
		
	private static boolean readStart = false;
	
	private class BoardTxThread extends Thread {

		private String message;
		private CtrTarget target;
		
		BoardTxThread(String message, CtrTarget target) {
			
			this.message = message;
			this.target = target;
		}
		
		public void run() {
			
			try {
				
				mFileOutputStream = new FileOutputStream(mFd);				
//				Log.w("TransmitThread1", "msg : " + message);
				if (mFileOutputStream != null) {
					
					mFileOutputStream.write(STX);
					Log.w("TransmitThread2", "msg : " + message);
					switch(target) {
					
					case PhotoSet	:	
						mFileOutputStream.write(message.getBytes());
//						Log.w("CellBlock", "msg : " + message);
						break;
										
					case TmpSet		:	
						mFileOutputStream.write(new String("R").getBytes());
						mFileOutputStream.write(message.getBytes());
//						Log.w("TmpSet", "msg : " + message);
						break;
										
					case TmpCall	:	
						mFileOutputStream.write(new String("VT").getBytes());
//						Log.w("TmpCall", "run");
						break;
										
					case MotorSet		:	
						mFileOutputStream.write(new String("A").getBytes());
						mFileOutputStream.write(new String("012").getBytes()); // default : 012
						mFileOutputStream.write(new String("R").getBytes());
						mFileOutputStream.write(message.getBytes()); // default : 6.5 * 10(횟占쏙옙) = 0065
//						mFileOutputStream.write(new String("0065").getBytes());
//						Log.w("MotorSet", "run");
						break;
					
					default : 			
						break;
					}
					
					mFileOutputStream.write(ETX);
			
				} else {
					
					return;
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
	}
	
	private class PrinterTxThread extends Thread {
		
		private StringBuffer txData;
		
		PrinterTxThread(StringBuffer txData) {
			
			this.txData = txData;
		}
		
		public void run() {
			
			try {
				
				pFileOutputStream = new FileOutputStream(pFd);				
				Log.w("PrinterTxThread", "pFd : " + pFd);
				if (pFileOutputStream != null) {
					Log.w("PrinterTxThread", "msg : " + txData.toString());
					pFileOutputStream.write(STX);

					/* i-CON */
					pFileOutputStream.write(LF);
					pFileOutputStream.write(CR);
					pFileOutputStream.write(GS); 
					pFileOutputStream.write(33); // size of character 
					pFileOutputStream.write(17); // 2 times of width and 2 times of height
					pFileOutputStream.write("i-CON".getBytes());
					
					/* TM */
					pFileOutputStream.write(GS);
					pFileOutputStream.write(33);
					pFileOutputStream.write(0); // standard width and height
					pFileOutputStream.write("TM".getBytes());
					
					/* Test Date */
					pFileOutputStream.write(LF);
					pFileOutputStream.write(CR);
					pFileOutputStream.write("Test Date".getBytes());
					
					pFileOutputStream.write(ESC);
					pFileOutputStream.write(36);
					pFileOutputStream.write(200);
					pFileOutputStream.write(0);
					
					/* Year */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(3, 7).getBytes());
					pFileOutputStream.write(".".getBytes());
					
					/* Month */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(7, 9).getBytes());
					pFileOutputStream.write(".".getBytes());
					
					/* Day */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(9, 11).getBytes());
					pFileOutputStream.write(" ".getBytes());
					
					/* Hour */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(11, 13).getBytes());
					pFileOutputStream.write(":".getBytes());
					
					/* Minute */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(13, 15).getBytes());

					/* Result */
					pFileOutputStream.write(LF);
					pFileOutputStream.write(CR);
					pFileOutputStream.write("Result".getBytes());
					
					pFileOutputStream.write(ESC);
					pFileOutputStream.write(36);
					pFileOutputStream.write(200);
					pFileOutputStream.write(0);
					
					/* HbA1c percentage */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(16).getBytes());
					
					/* Test No. */
					pFileOutputStream.write(LF);
					pFileOutputStream.write(CR);
					pFileOutputStream.write("Test No.".getBytes());
					
					pFileOutputStream.write(ESC);
					pFileOutputStream.write(36);
					pFileOutputStream.write(200);
					pFileOutputStream.write(0);
					
					/* Test number */
					pFileOutputStream.write(CR);
					pFileOutputStream.write(txData.substring(1, 3).getBytes());
					
					pFileOutputStream.write(ESC);
					pFileOutputStream.write(100); // print and feed n lines
					pFileOutputStream.write(10); // lines of number
					pFileOutputStream.write(ETX);
					
				} else {
					
					return;
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
	}

	private class BarcodeTxThread extends Thread {
		
		private String txData;
		
		BarcodeTxThread(String txData) {
			
			this.txData = txData;
		}
		
		public void run() {
			
			try {
				
				bFileOutputStream = new FileOutputStream(bFd);				
				Log.w("BarcodeTxThread", "bFd : " + bFd);
				if (bFileOutputStream != null) {
					Log.w("BarcodeTxThread", "msg : " + txData);
//					bFileOutputStream.write(txData.getBytes());
					bFileOutputStream.write(0x38);
					bFileOutputStream.write(0x41);
					bFileOutputStream.write(0x43);
					bFileOutputStream.write(0x31);
					
//					bFileOutputStream.write(0x0C);
//					bFileOutputStream.write(0x01);
				} else {
					
					return;
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
	}
	
	private class BoardRxThread extends Thread {
		
		public void run() {
//			Log.w("ReceiveThread", "run");
			while(!isInterrupted()) {
				
				int size;
				
				try {
					
					if(mFileInputStream != null) {
						
						size = mFileInputStream.read(rxBuffer);
						Log.w("BoardRxThread", "BoardInputBuffer : " + new String(rxBuffer));

						if(size > 0) {
						
							BoardDataReceive(size);	
						}
						
					} else {
						
						return;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	private synchronized void BoardDataReceive(int size) {
		
//		Log.w("BoardDataReceive", "start");
		
		for(int i = 0; i < size; i++) {
//			Log.w("BoardDataReceive", "rxBuffer : " + Integer.toString(rxBuffer[i]));
						
			if(readStart == true) {
				
				if(rxBuffer[i] == ETX){
					
//					debugBuffer[19] = rxBuffer[i];
					readStart = false;
					
					DataAppend(bufCnt, bufIndex, RxTarget.Board);
					bufCnt++;
					
					if(bufCnt == 64) bufCnt = 0;
									
				} else {
					
					appendBuffer[bufCnt][bufIndex++] = rxBuffer[i];
				}
				
			} else {
				
				if(rxBuffer[i] == STX) {
					
//					debugBuffer[18] = rxBuffer[i];
					
					bufIndex = 0;
					readStart = true;
				}
			}
		}
//		Log.w("BoardDataReceive", "end");
	}
	
	private synchronized void DataAppend(byte num, byte len, RxTarget target) {
		
//		Log.w("DataAppend", "mReception : " + mReception.toString() + " num : " + num + " AppendCnt : " + AppendCnt + " len : " + len);
		if((num + 1) != AppendCnt) {
			
			try {
				
				mReception.append(new String(appendBuffer[num], 0, len));
				AppendCnt++;
				
//				Log.w("DataAppend", "msg : " + mReception.toString());
//				Log.w("DataAppend", "msg : " + Double.parseDouble(mReception.toString()));
					
				switch(target) {
				
				case Barcode	:	
					SerialBarcode = new Barcode();
					SerialBarcode.BarcodeCheck();
					break;
									
				default			:	
					break;
				}
					
				if(AppendCnt == 64) AppendCnt = 0;

//				debugFlag = 0;
				
			} catch(ArrayIndexOutOfBoundsException e) {
				
				e.printStackTrace();
				AppendCnt = 0;
				bufCnt = 0;
				return;
			}
		}
	}
	
	public class BarcodeRxThread extends Thread {
		
		public void run() {
//			Log.w("BarcodeRxThread", "run : " + isInterrupted());
			while(!isInterrupted()) {
				
				int size;
				
				try {
					
					if(bFileInputStream != null) {
						
						size = bFileInputStream.read(rxBuffer);
						
						if(size > 0) {
//							Log.w("BarcodeRxThread", "msg : " + size);
							BarcodeDataReceive(size);
						}
					} else {
						
						return;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
		
	private synchronized void BarcodeDataReceive(int size) {
		
//		barcodeCnt += size;
		
		if(readStart == false) {
		
			readStart = true;
			bufIndex = 0;
			RxClear();
		}
		
		for(int i = 0; i < size; i++) {

			appendBuffer[bufCnt][bufIndex++] = rxBuffer[i];
//			Log.w("BarcodeDataReceive", "Char : " + Integer.toString(rxBuffer[i]));
			
		}	
		
		if(rxBuffer[size-2] == 0x0D && rxBuffer[size-1] == 0x0A) {

			readStart = false;
			barcodeCnt = 0;
			
			DataAppend(bufCnt, bufIndex, RxTarget.Barcode);
			bufCnt++;
				
			if(bufCnt == 64) bufCnt = 0;
		}
	}
	
	public void BoardSerialInit() {
		
//		Log.w("BoardSerialInit", "run");
		System.loadLibrary("serial_port");
		mFd = open("/dev/ttySAC3", 9600, 0);
	}
	
	public void BoardRxStart() {
		
		mFileInputStream = new FileInputStream(mFd);
				
		mBoardRxThread = new BoardRxThread();
		mBoardRxThread.start();
	}
	
	public void BoardTx(String str, CtrTarget trg) {
		
		switch(trg) {

		case PhotoSet	:	
			//Log.w("BoardTx", "Cell Block : " + str);
			mBoardTxThread = new BoardTxThread(str, CtrTarget.PhotoSet);
			mBoardTxThread.start();
			break;
		
		case MotorSet		:	
			//Log.w("BoardTx", "Motor");
			mBoardTxThread = new BoardTxThread(str, CtrTarget.MotorSet);
			mBoardTxThread.start();
			break;
			
		case TmpSet		:	
			//Log.w("BoardTx", "TmpSet : " + str);
			mBoardTxThread = new BoardTxThread(str, CtrTarget.TmpSet);
			mBoardTxThread.start();
			break;
		
		case TmpCall	:	
			//Log.w("BoardTx", "TmpCall");
			mBoardTxThread = new BoardTxThread(str, CtrTarget.TmpCall);
			mBoardTxThread.start();
			break;
							
		default			: 
			break; 
		}
	}
	
	public static void RxClear() {
//		Log.w("RxClear", "" + mReception.toString());
		mReception.delete(0, mReception.capacity());
	}
	
	public void PrinterSerialInit() {		
//		Log.w("PrinterSerialInit", "run");
//		System.loadLibrary("serial_port");
//		pFd = open("/dev/s3c2410_serial2", 9600, 0);
	}
	
	public void PrinterTxStart(StringBuffer data) {
		
		pPrinterTxThread = new PrinterTxThread(data);
		pPrinterTxThread.start();
	}
	
	public void BarcodeSerialInit() {
//		Log.w("BarcodeSerialInit", "run");
//		System.loadLibrary("serial_port");
//		bFd = open("/dev/s3c2410_serial1", 9600, 0);
	}

	public void BarcodeRxStart() {
		
		bFileInputStream = new FileInputStream(bFd);
				
		bBarcodeRxThread = new BarcodeRxThread();
		bBarcodeRxThread.start();
	}
	
	public void BarcodeTxStart(String data) {
		
		bBarcodeTxThread = new BarcodeTxThread(data);
		bBarcodeTxThread.start();
	}
	
	public static void Sleep(int t) {
		
		try {
			
			Thread.sleep(t);
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();			
			return;
		}
	}
	
	static {
	
		System.loadLibrary("serial_port");
	}
	
	/*JNI*/
	public native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
}
