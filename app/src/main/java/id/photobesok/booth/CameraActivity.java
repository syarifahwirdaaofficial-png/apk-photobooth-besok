package id.photobesok.booth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends ComponentActivity {
 private PreviewView preview;private TextView countdown,progress,hint;private Button shot,flip;private ImageCapture capture;private ProcessCameraProvider provider;
 private int facing=CameraSelector.LENS_FACING_FRONT,total=3,seconds=3;private boolean busy=false,active=false,finished=false;private long generation=0;
 private final ArrayList<String> photos=new ArrayList<>();private final Handler ui=new Handler(Looper.getMainLooper());private View flash;
 @Override public void onCreate(Bundle state){super.onCreate(state);Ui.full(this);total=Math.max(1,Math.min(4,getIntent().getIntExtra("count",3)));seconds=Math.max(2,Math.min(10,getIntent().getIntExtra("seconds",3)));if(!getIntent().getBooleanExtra("front",true))facing=CameraSelector.LENS_FACING_BACK;
  LinearLayout root=Ui.column(this);root.setPadding(Ui.dp(this,20),Ui.dp(this,28),Ui.dp(this,20),Ui.dp(this,20));root.setBackgroundColor(Ui.INK);
  LinearLayout top=new LinearLayout(this);Button back=Ui.button(this,"Batal",false,()->finish());top.addView(back);progress=Ui.text(this,"02 / SESI FOTO",14,Color.WHITE,true);progress.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);top.addView(progress,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));root.addView(top);Ui.gap(root,16);
  hint=Ui.serif(this,"Siap untuk senyum?",30,Color.WHITE);hint.setGravity(Gravity.CENTER);root.addView(hint);Ui.gap(root,18);
  FrameLayout stage=new FrameLayout(this);stage.setBackground(Ui.bg(0xff10271e,Ui.dp(this,24)));stage.setClipToOutline(true);preview=new PreviewView(this);preview.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);stage.addView(preview,new FrameLayout.LayoutParams(-1,-1));countdown=Ui.text(this,"",100,Color.WHITE,true);countdown.setGravity(Gravity.CENTER);countdown.setShadowLayer(12,0,3,0x66000000);stage.addView(countdown,new FrameLayout.LayoutParams(-1,-1));flash=new View(this);flash.setBackgroundColor(Color.WHITE);flash.setAlpha(0);stage.addView(flash,new FrameLayout.LayoutParams(-1,-1));root.addView(stage,new LinearLayout.LayoutParams(-1,0,1));Ui.gap(root,18);
  LinearLayout controls=new LinearLayout(this);shot=Ui.button(this,"Mulai "+total+" foto   ●",true,this::start);flip=Ui.button(this,"Balik",false,()->{facing=facing==CameraSelector.LENS_FACING_FRONT?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT;bind();});controls.addView(shot,new LinearLayout.LayoutParams(0,Ui.dp(this,64),1));LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-2,Ui.dp(this,64));fp.leftMargin=Ui.dp(this,10);controls.addView(flip,fp);root.addView(controls);Ui.gap(root,12);TextView note=Ui.text(this,total+" foto otomatis • Lihat ke lensa • Ganti pose setiap jeda",12,0xffc4d1c6,false);note.setGravity(Gravity.CENTER);root.addView(note);setContentView(root);shot.setEnabled(false);
  if(checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)open();else requestPermissions(new String[]{Manifest.permission.CAMERA},9);
 }
 private void open(){ListenableFuture<ProcessCameraProvider> f=ProcessCameraProvider.getInstance(this);f.addListener(()->{try{if(isDestroyed())return;provider=f.get();bind();}catch(Exception e){error(e);}},ContextCompat.getMainExecutor(this));}
 private void bind(){if(provider==null)return;try{CameraSelector selector=new CameraSelector.Builder().requireLensFacing(facing).build();if(!provider.hasCamera(selector)){facing=facing==CameraSelector.LENS_FACING_FRONT?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT;selector=new CameraSelector.Builder().requireLensFacing(facing).build();}provider.unbindAll();Preview live=new Preview.Builder().build();live.setSurfaceProvider(preview.getSurfaceProvider());capture=new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();provider.bindToLifecycle(this,selector,live,capture);shot.setEnabled(true);}catch(Exception e){error(e);}}
 private void start(){if(busy||capture==null)return;busy=true;shot.setEnabled(false);flip.setEnabled(false);count(seconds,++generation);}
 private void count(int n,long token){if(!active||token!=generation)return;progress.setText("FOTO "+(photos.size()+1)+" / "+total);hint.setText(photos.isEmpty()?"Lihat ke lensa, ya.":"Pose berikutnya!");if(n>0){countdown.setText(String.valueOf(n));countdown.setScaleX(.7f);countdown.setScaleY(.7f);countdown.animate().scaleX(1).scaleY(1).setDuration(250).start();ui.postDelayed(()->count(n-1,token),1000);return;}countdown.setText("");take(token);}
 private void take(long token){if(capture==null){reset();return;}capture.setTargetRotation(preview.getDisplay().getRotation());File file=new File(getCacheDir(),"session_"+System.currentTimeMillis()+"_"+photos.size()+".jpg");capture.takePicture(new ImageCapture.OutputFileOptions.Builder(file).build(),ContextCompat.getMainExecutor(this),new ImageCapture.OnImageSavedCallback(){public void onImageSaved(ImageCapture.OutputFileResults result){if(!active||token!=generation){file.delete();return;}photos.add(file.getAbsolutePath());flash.setAlpha(.8f);flash.animate().alpha(0).setDuration(280).start();if(photos.size()==total){finished=true;setResult(RESULT_OK,new Intent().putStringArrayListExtra("photos",photos));finish();}else{hint.setText("Bagus! Siapkan pose berikutnya.");ui.postDelayed(()->count(seconds,token),1200);}}public void onError(ImageCaptureException e){if(token==generation){error(e);reset();}}});}
 private void reset(){busy=false;if(countdown!=null){countdown.setText("");shot.setEnabled(capture!=null);shot.setText(photos.isEmpty()?"Mulai "+total+" foto   ●":"Lanjutkan sesi   →");flip.setEnabled(true);}}
 private void error(Exception e){Toast.makeText(this,"Kamera belum siap: "+e.getMessage(),Toast.LENGTH_LONG).show();}
 @Override public void onRequestPermissionsResult(int code,String[] permissions,int[] grants){super.onRequestPermissionsResult(code,permissions,grants);if(code==9&&grants.length>0&&grants[0]==PackageManager.PERMISSION_GRANTED)open();else{Toast.makeText(this,"Izinkan kamera untuk memulai sesi foto.",Toast.LENGTH_LONG).show();finish();}}
 @Override protected void onResume(){super.onResume();active=true;reset();}
 @Override protected void onPause(){active=false;generation++;ui.removeCallbacksAndMessages(null);reset();super.onPause();}
 @Override protected void onDestroy(){if(!finished)for(String p:photos)new File(p).delete();super.onDestroy();}
 @Override public void onWindowFocusChanged(boolean focus){super.onWindowFocusChanged(focus);if(focus)Ui.full(this);}
}
