package com.androidhuman.example.CameraPreview;


import java.io.IOException;
import java.util.List;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;


public class ProcessCore extends SurfaceView implements SurfaceHolder.Callback {
	SurfaceHolder mHolder;
	Camera mCamera;
	
	boolean mPreviewState;
	private Bitmap prBitmap;
	private CameraPreview _MActivity = null;
	private int ThreshHold = 127;
	private int ThreshHoldData = 127;
	private int Upper = 127;
	private int Under = 127;
	private int resolution = 0;
	private int diff = 0;
	private int post_diff = 0;


	private int data = 0;
	private boolean flag = true;
	private int[] drop_data = new int[100];
	protected boolean toggle=false;


	private boolean flag_start=false;
	private boolean flag_threshold=true;
	private boolean flag_snap=false;
	private boolean flag_snap_delay=true;
	private boolean flag_focus=false;
	private boolean flag_count=false;
	private char snap_delay_filter=0;


	private long start_time=0;
	private long end_time=0;
	private float result_time=0;




	//TODO : native �Լ� ���
	static {
		System.loadLibrary("myproc");
	}
	/*
	 * NativeProc : ����ȭ �� ���� ������ ���ϴ� �Լ�
	 * Gonzalez : �ݺ��� ����ȭ�� ���� �Ӱ谪�� ���ϴ� �Լ�
	 * Upper : ���� n%�� �Ӱ谪�� ���ϴ� �Լ�
	 * Under : ���� n%�� �Ӱ谪�� ���ϴ� �Լ�
	 * 
	 * ��, Gnzalez, Upper, Under���� ���� �Ӱ谪�� ���ϰ� �ǰ� ������ �Ӱ谪��
	 * NativeProc�� �־ ����ȭ �Ǵ� ���� �����ϰ� �ȴ�.
	 */
	private native int NativeProc(Bitmap _outBitmap, byte[] _in, int _ThreshHold, int resolution);
	private native int Gonzalez(Bitmap _outBitmap, byte[] _in, int resolution);
	private native int Upper(Bitmap _outBitmap, byte[] _in, int resolution);
	private native int Under(Bitmap _outBitmap, byte[] _in, int resolution);
	//at bin/classes/$ javah -classpath ~/android/adt-bundle-linux-x86-20130219/sdk/platforms/android-16/android.jar: com.androidhuman.example.CameraPreview.ProcessCore


	ProcessCore(CameraPreview aaa) {
		super(aaa);
		_MActivity = aaa;
		mHolder = getHolder();
		mHolder.addCallback(this);
		//Ǫ�����۴� ���̽�ũ��������ġ(ICS)���ķ� ������ �ʽ��ϴ�.
		//mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
	}


	//���� flag�� ���¸� �����ϱ����� method
	public void SetState(boolean state){
		flag_start = state;
		flag_threshold = state;
		flag = state;
	}


	//surfaceCreated - ���ǽ��䰡 �����Ǿ����� ����Ǵ� �޼ҵ�
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setPreviewCallback(new Camera.PreviewCallback() {
				//���ǽ��� �ݹ��Լ� ���
				public void onPreviewFrame(byte[] _data, Camera _camera) {
					// TODO Auto-generated method stub
					Camera.Parameters params = _camera.getParameters();
					int w = params.getPreviewSize().width;
					int h = params.getPreviewSize().height;
					//Log.i("mydata", "width:"+w+"/height:"+h);


					//�Ʒ� �ּ��� ������Ű�� ���۽ÿ� �÷��ð� �۵��˴ϴ�.
					/*if(flag_start){
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
					}
					else{
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
					}
					_camera.setParameters(params);
					 */




					//prBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
					//�����Ǵ� ����ȭ�� �׸�ڽ��� ũ�⸦ �����ϰ� �ֽ��ϴ�.
					prBitmap = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);




					// ó�� ������ ���� flag. ó���� ���۵Ǹ� ��Ŀ���� ���߰� �˴ϴ�.
					//if(flag_threshold && flag_start){
					if(flag_start){
						mCamera.autoFocus (new Camera.AutoFocusCallback() {
							public void onAutoFocus(boolean success, Camera camera) {
								if(success){
									// do something
									flag_focus=true;
									flag_start=false;
									data=0;
								}
								else{
									flag_focus=false;
								} //���⿡ flag_focue=false�� �־�� �� �Ͱ����� �ȳ־ �� �Ǿ �ϴ� ����
							}
						});
					}


					//��Ŀ���� ���ߴµ� ���������� �������� ������ ���ϴ�.
					if(flag_focus){
						data = 0;


						//ȭ�� �ػ󵵸� �����ϱ� ���� �����Դϴ�.
						//w=1024, h=768 //// 1024 x ((768/2)-(200/2)) = 290816
						// 290816 + 412 ( 1024/2 -100 = 412)
						// 1024 * 359 ( 768/2 - 25) = 367616 + 412 = 368028
						// 1024 * 334 ( 768/2 - 50) = 342016 + 412 = 342428
						//resolution = ((w*(h>>1 -25) + (w>>1-100))<<11) + w;
						resolution = w*(h/2 -100) + (w/2-100);
						resolution = (resolution<<11)+w; 
						Log.i("my_message","Resolution : "+ (resolution>>11));
						Log.i("my_message","Width : "+ (resolution&0x7FF));
						/*int�� ������(32��Ʈ)�� ���� 11��Ʈ�������� ���α��̸� �ֽ��ϴ�.
						 * ������ ���� 21��Ʈ �������� �ػ󵵸� �����ϱ� ���� ���� �ֽ��ϴ�.
						 *  
						 *  Native�Լ��� ȣ���Ҷ� �����ͺ��縦 �ּ�ȭ�ϱ� ����...�̶�⺸��
						 *  ����� �Լ� �ٽ�¥�� �����Ƽ� �̷����ϴ�...�Ф�
						 */


						//TODO : Gonzalez �� ���� �Ӱ谪 ����
						//flag_threshold = false;
						ThreshHoldData = Gonzalez(prBitmap, _data,resolution);
						_MActivity.mDebugText.setStringTrashhold(ThreshHoldData);
						//Toast.makeText(_MActivity, "�Ӱ谪 "+ThreshHoldData+"�� ���Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
						_MActivity.mDebugText.setStringMessege("�Ӱ谪 "+ThreshHoldData+"�� ���Ͽ����ϴ�.");
						flag_focus=false;
						flag_count=true;
						Upper = Upper(prBitmap, _data,resolution);
						Under = Under(prBitmap, _data,resolution);
						_MActivity.mDebugText.setStringUpper(Upper);
						_MActivity.mDebugText.setStringUnder(Under);
					}




					//�Ʒ� �κ� �ּ��� �����ϵǸ� ó������ ������ �׻� ���̰� �˴ϴ�. ������
					//drop_data[1] = drop_data[0];
					//drop_data[0] = NativeProc(prBitmap, _data,ThreshHoldData);
					//drop_data[0] = NativeProc(prBitmap, _data, Upper);
					Log.i("mydata", ""+drop_data[0]);


					//�Ӱ谪�� ������ n���� �������� �ǳʶٱ� ���� flag �����Դϴ�.
					if(flag_snap_delay){
						snap_delay_filter++;
					}
					if(snap_delay_filter>6){ //�������� �ǳʶ���
						flag_snap=true;
						flag_snap_delay=false;
						snap_delay_filter=0;
					}


					//�Ӱ谪�� ����Ǹ� �����ϱ����� flag_count�Դϴ�.
					if(flag_count){
						//ó���� �̹����� prBitmap�� �ѷ��ش�.
						_MActivity.mImageview.setImageBitmap(prBitmap);
						//�׳� ���۸޼����� �ѹ� ����ֱ� ���� flag
						if(flag){
							//Toast.makeText(_MActivity, "������ �����մϴ�.", Toast.LENGTH_SHORT).show();
							_MActivity.mDebugText.setStringMessege("������ �����մϴ�.");
							//start_time = System.currentTimeMillis();
							flag=false;
						}
						//�̺��� ���� 2���� ������ ����
						drop_data[2] = drop_data[1];
						drop_data[1] = drop_data[0];
						drop_data[0] = NativeProc(prBitmap, _data,ThreshHoldData,resolution);

						//TODO : ������ �Ǵ��ϴ� �κ�
						// �̺а��� 500�̻��϶� (���� ����� ���� ������ ����ȭ�� ������ ���� 500�̻�)
						//differentiation = Math.abs(drop_data[0] - drop_data[1]);
						//differentiation = Math.abs(drop_data[2] - drop_data[0]);
						diff = Math.abs(drop_data[2] - drop_data[1]) + Math.abs(drop_data[1] - drop_data[0]);
						post_diff = Math.abs(drop_data[1] - drop_data[0]);
						Log.i("myddata",""+diff);
						if(post_diff<2000){
							_MActivity.mDraw.SetCircle(true);
							_MActivity.mDraw.invalidate();
							if(diff > 600){
								//����� �������� �ǳ� �پ�����
								if(flag_snap)
								{
									//if(data<5)	{
									_MActivity.snapImageview[data].setImageBitmap(prBitmap);
									//}
									data++;
									flag_snap=false;
									flag_snap_delay=true;
									//����� ������� 1���� �� �� �ð��� �����ϱ� �����մϴ�. 
									if(data == 2){
										start_time = System.currentTimeMillis();
									}
									//data++�ڷ� �Űܿ� ȣ�� Ƚ���� ������ ������������ �ϴ� ����
									_MActivity.mDebugText.setStringData(data);
									_MActivity.beepsound.play(_MActivity.id, 1.0f, 1.0f, 0, 0, 1.0f);
									_MActivity.vibrator.vibrate(70);
									
								}
							}
						}
						else{
							_MActivity.mDraw.SetCircle(false);
							_MActivity.mDraw.invalidate();
							flag_count = false;
							_MActivity.mDebugText.setStringMessegeInit();
							_MActivity.mDebugText.setStringMessege("����� ���ƽ��ϴ�. �ٽ� �����ϼ���");
							Toast.makeText(_MActivity, "����� ���ƽ��ϴ�. �ٽ� �����ϼ���", Toast.LENGTH_SHORT).show();
						}

						//����� ������� ������ 5���� �Ǹ� ������ �����Ѵ�.
						if(data==5){
							end_time =  System.currentTimeMillis();
							result_time = (float) ((end_time - start_time)/1000.0);
							flag_count = false;
							_MActivity.mDebugText.setStringMessegeInit();
							//Toast.makeText(_MActivity, "�ɸ��ð� : "+result_time+"��", Toast.LENGTH_SHORT).show();
							_MActivity.mDebugText.setStringMessege("�ɸ��ð� : "+result_time+"��");
							_MActivity.mDraw.SetCircle(false);
							_MActivity.mDraw.invalidate();
						}


						/*if((flag)&&(data>30)){
							Toast.makeText(_MActivity, ""+data, Toast.LENGTH_SHORT).show();
							flag=false;
						}*/
					}
				}
			});
		} catch (IOException exception) {
			//�� ����ó���� ���صθ� �ڷΰ��⸦ �������� ī�޶� release�� �ȵǾ� �����޼����� ȣ��ȴ�.
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			// TODO: add more exception handling logic here
		}
	}


	// surface�� ����Ǿ����� ����Ǵ� �޼ҵ�
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
		mCamera.release();
		mCamera = null;
	}




	// surface�� ����Ǿ����� ����Ǵ� �޼ҵ�. surfaceCreated ������ �ٷ� ����ȴ�.
	//@SuppressLint("NewApi")
	@SuppressLint("InlinedApi")
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		//ī�޶� �Ķ���͸� ����
		Camera.Parameters parameters = mCamera.getParameters();


		//�����Ǵ� ī�޶� �ػ󵵸� �޾ƿ´�.
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();


		//0���� n���� �ػ��� �ϳ��� ���� �� �� ������ �ϴ��� 1��°�� �ػ󵵸� ����
		Camera.Size previewSize = previewSizes.get(1);


		//���ǵ� �ػ󵵷� Preview�� ����.
		parameters.setPreviewSize(previewSize.height, previewSize.width);
		parameters.setRotation(90);
		//parameters.setPreviewFpsRange(28000, 35000);
		//parameters.setFocusMode(Camera.Parameters.FLASH_MODE_TORCH);
		//parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		//parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		//parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		Log.i("mymode", "surCh width:"+w+"/height:"+h);


		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(90);
		/*mCamera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
			}
		});*/
		mCamera.startPreview();
		Log.i("mymode", "startPreview()");
	}
}

