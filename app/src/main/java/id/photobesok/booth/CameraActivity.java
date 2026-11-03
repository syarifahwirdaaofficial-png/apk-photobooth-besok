package id.photobesok.booth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;

/** Lifecycle-bound camera. JPEG rotation is preserved and decoded by MainActivity. */
public class CameraActivity extends ComponentActivity {
 private PreviewView preview;
 private TextView countdown;
 private Button shot,flip;
 private ImageCapture capture;
 private ProcessCameraProvider provider;
 private int facing=CameraSelector.LENS_FACING_FRONT;
 private boolean busy=false,active=false;
 private final Handler ui=new Handler(Looper.getMainLooper());
 @Override public void onCreate(Bundle state){super.onCreate(state);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  FrameLayout root=new FrameLayout(this);preview=new PreviewView(this);root.addView(preview,new FrameLayout.LayoutParams(-1,-1));
  countdown=new TextView(this);countdown.setTextSize(72);countdown.setTextColor(0xffffffff);countdown.setGravity(Gravity.CENTER);root.addView(countdown,new FrameLayout.LayoutParams(-1,-1));
  LinearLayout controls=new LinearLayout(this);shot=new Button(this);shot.setText("AMBIL FOTO · 3 DETIK");flip=new Button(this);flip.setText("BALIK KAMERA");controls.addView(shot,new LinearLayout.LayoutParams(0,-2,1));controls.addView(flip,new LinearLayout.LayoutParams(0,-2,1));FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);bp.bottomMargin=36;root.addView(controls,bp);setContentView(root);
  shot.setEnabled(false);shot.setOnClickListener(v->{if(!busy&&capture!=null){busy=true;shot.setEnabled(false);flip.setEnabled(false);count(3);}});
  flip.setOnClickListener(v->{facing=facing==CameraSelector.LENS_FACING_FRONT?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT;bind();});
  if(checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)open();else requestPermissions(new String[]{Manifest.permission.CAMERA},9);
 }
 private void open(){ListenableFuture<ProcessCameraProvider> future=ProcessCameraProvider.getInstance(this);future.addListener(()->{try{if(isDestroyed())return;provider=future.get();bind();}catch(Exception e){error(e);}},ContextCompat.getMainExecutor(this));}
 private void bind(){if(provider==null)return;try{CameraSelector selector=new CameraSelector.Builder().requireLensFacing(facing).build();if(!provider.hasCamera(selector)){facing=facing==CameraSelector.LENS_FACING_FRONT?CameraSelector.LENS_FACING_BACK:CameraSelector.LENS_FACING_FRONT;selector=new CameraSelector.Builder().requireLensFacing(facing).build();}provider.unbindAll();Preview live=new Preview.Builder().build();live.setSurfaceProvider(preview.getSurfaceProvider());capture=new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();provider.bindToLifecycle(this,selector,live,capture);shot.setEnabled(true);}catch(Exception e){error(e);}}
 private void count(int n){if(!active)return;if(n>0){countdown.setText(String.valueOf(n));ui.postDelayed(()->count(n-1),1000);return;}countdown.setText("");if(capture==null){reset();return;}capture.setTargetRotation(preview.getDisplay().getRotation());File file=new File(getCacheDir(),"photobesok_capture.jpg");capture.takePicture(new ImageCapture.OutputFileOptions.Builder(file).build(),ContextCompat.getMainExecutor(this),new ImageCapture.OnImageSavedCallback(){public void onImageSaved(ImageCapture.OutputFileResults result){if(!active)return;setResult(RESULT_OK,new Intent().putExtra("photo_path",file.getAbsolutePath()));finish();}public void onError(ImageCaptureException e){error(e);reset();}});}
 private void reset(){busy=false;countdown.setText("");shot.setEnabled(capture!=null);flip.setEnabled(true);}
 private void error(Exception e){Toast.makeText(this,"Kamera: "+e.getMessage(),Toast.LENGTH_LONG).show();}
 @Override public void onRequestPermissionsResult(int code,String[] permissions,int[] grants){super.onRequestPermissionsResult(code,permissions,grants);if(code==9&&grants.length>0&&grants[0]==PackageManager.PERMISSION_GRANTED)open();else{Toast.makeText(this,"Izin kamera diperlukan untuk mengambil foto.",Toast.LENGTH_LONG).show();finish();}}
 @Override protected void onResume(){super.onResume();active=true;reset();}
 @Override protected void onPause(){active=false;ui.removeCallbacksAndMessages(null);reset();super.onPause();}
}
