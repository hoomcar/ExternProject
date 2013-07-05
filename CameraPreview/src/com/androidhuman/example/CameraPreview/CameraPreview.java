package com.androidhuman.example.CameraPreview;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;




public class CameraPreview extends Activity{    
	private ProcessCore mPreview;
	public ImageView mImageview;
	public ImageView[] snapImageview = new ImageView[5];
	public DrawOnTop mDraw;
	public DebugText mDebugText;


	//private ImageButton mButton;
	private TextView mButton;
	private CheckBox mCheckbox;
	private FrameLayout.LayoutParams btn_params;
	private FrameLayout.LayoutParams chk_params;

	boolean loaded = false;
	
	private int pxWidth;
	private int pxHeight;

	private Pop pop;
	int id;
	SoundPool beepsound;
	Vibrator vibrator; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the window title and full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		this.beepsound = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		beepsound.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                    int status) {
                loaded = true;
            }
        });
        id = beepsound.load(this, R.raw.water, 1);

		//�⺻���� ȭ���� �����ϰ��ִ� View ��
		mDraw = new DrawOnTop(this);
		// Create our Preview view and set it as the content of our activity.
		mPreview = new ProcessCore(this);
		mImageview = new ImageView(this);
		mDebugText = new DebugText(this);

		//������ �������� �����Ͽ� �����ֱ� ���� Image View��
		snapImageview[0] = new ImageView(this);
		snapImageview[1] = new ImageView(this);
		snapImageview[2] = new ImageView(this);
		snapImageview[3] = new ImageView(this);
		snapImageview[4] = new ImageView(this);
		
		// ��ư�� �߰��մϴ�.
		mButton = new TextView(this);
		mButton.setText("Start");
		mButton.setTextColor(Color.BLACK);
		mButton.setBackgroundResource(R.drawable.slelector);
		mButton.setGravity(Gravity.CENTER);
		

		//mButton.setBackgroundColor(R.drawable.slelector);
		//mButton.setGravity(Gravity.BOTTOM);
		mButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//beepsound.play(id,1.0f,1.0f,0,0,1.0f);
				Toast.makeText(getApplicationContext(), "�ݺ��� ����ȭ�� ���� �Ӱ谪 ������ �����մϴ�.", Toast.LENGTH_SHORT).show();
				mPreview.SetState(true);
			}
		});
		
		/*
		mButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		})*/;


		// http://developer.android.com/reference/android/widget/RelativeLayout.html
		// http://www.verious.com/qa/programmatically-set-image-button-layout-gravity/
		btn_params = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		chk_params = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);


		//�Ʒ� ������ �ּ��� �����ϸ� ��ư�� �Ʒ��� �������ϴ�.
		btn_params.gravity = Gravity.BOTTOM;
		//btn_params.gravity = Gravity.;


		//params.topMargin = displayMetrics.heightPixels - 100;
		//params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);




		//mImageview�� ����� ��ġ��Ű�� 90�� ȸ���ϱ� ���Ͽ� ����� matrix �Դϴ�.
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		pxWidth  = displayMetrics.widthPixels/2;
		pxHeight = displayMetrics.heightPixels/2;
		Matrix m = new Matrix();
		m.setRotate(90);
		m.postTranslate((pxWidth-100), (pxHeight-100));


		mImageview.setScaleType(ScaleType.MATRIX);
		mImageview.setImageMatrix(m);




		//�������� �����ֱ� ���� Imageview��  90�� ȸ�� �� ��ġ������ �մϴ�.
		Matrix snap_m = new Matrix();
		snap_m.setRotate(90);
		snap_m.postScale(0.5f, 0.5f);
		//snap_m.postTranslate(displayMetrics.widthPixels, 0);
		snap_m.postTranslate(360, 0);
		//m.postTranslate((displayMetrics.widthPixels-200), (0));
		snapImageview[0].setScaleType(ScaleType.MATRIX);
		snapImageview[0].setImageMatrix(snap_m);


		//snap_m.postTranslate(0, (202));
		snap_m.postTranslate((102), 0);
		snapImageview[1].setScaleType(ScaleType.MATRIX);
		snapImageview[1].setImageMatrix(snap_m);


		snap_m.postTranslate((102), 0);
		snapImageview[2].setScaleType(ScaleType.MATRIX);
		snapImageview[2].setImageMatrix(snap_m);


		snap_m.postTranslate((102), 0);
		snapImageview[3].setScaleType(ScaleType.MATRIX);
		snapImageview[3].setImageMatrix(snap_m);


		snap_m.postTranslate((102), 0);
		snapImageview[4].setScaleType(ScaleType.MATRIX);
		snapImageview[4].setImageMatrix(snap_m);


		//üũ�ڽ�
		mCheckbox = new CheckBox(this);
		mCheckbox.setText("Debug Mode");
		mCheckbox.setChecked(true);
		mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mImageview.setVisibility(View.VISIBLE);
					mDebugText.setVisibility(View.VISIBLE);
					snapImageview[0].setVisibility(View.VISIBLE);
					snapImageview[1].setVisibility(View.VISIBLE);
					snapImageview[2].setVisibility(View.VISIBLE);
					snapImageview[3].setVisibility(View.VISIBLE);
					snapImageview[4].setVisibility(View.VISIBLE);
				}
				else{
					mImageview.setVisibility(View.INVISIBLE);
					mDebugText.setVisibility(View.INVISIBLE);
					snapImageview[0].setVisibility(View.INVISIBLE);
					snapImageview[1].setVisibility(View.INVISIBLE);
					snapImageview[2].setVisibility(View.INVISIBLE);
					snapImageview[3].setVisibility(View.INVISIBLE);
					snapImageview[4].setVisibility(View.INVISIBLE);
				}
			}	
		});


		//���ݱ��� �����ߴ� ��� View���� ȭ�鿡 �߰��մϴ�.
		setContentView(mPreview);
		addContentView(mDraw,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addContentView(mDebugText,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));


		
		addContentView(mButton,btn_params);
		
		addContentView(mCheckbox, chk_params);

		addContentView(mImageview,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		addContentView(snapImageview[0],new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		addContentView(snapImageview[1],new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		addContentView(snapImageview[2],new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		addContentView(snapImageview[3],new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		addContentView(snapImageview[4],new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}


	//�ڷΰ��� ��ư�� �������� �ٷ� �����°��� �������� �ҽ������� ������ ������� �ʽ��ϴ�.
	@Override 
	public void onBackPressed() {
		/*long tempTime        = System.currentTimeMillis();
		long intervalTime    = tempTime - backPressedTime;
		if ( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime ) {
			super.onBackPressed(); 
		} 
		else { 
			backPressedTime = tempTime; 
			Toast.makeText(getApplicationContext(),"'�ڷ�'��ư���ѹ��������ø�����˴ϴ�.",Toast.LENGTH_SHORT).show(); 
		}*/


		finish();
	}


	//�޴� ����
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,"��� ����");
		menu.add(0,1,0,"About");
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			pop = new Pop(mDraw);  
			pop.show();
			break;
		case 1:
			Toast.makeText(getApplicationContext(), "hoomcar@naver.com", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

