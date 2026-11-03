package id.photobesok.booth;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/** Offline proof-of-concept: no analytics, authentication, payment, or cloud. */
public class MainActivity extends Activity {
  private static final int CAPTURE=11, PICK_FRAME=12;
  private final ExecutorService io=Executors.newSingleThreadExecutor();
  private ImageView preview;
  private Bitmap photo, frame, composed;
  private EditText printerIp, widthPx, heightPx;
  private Spinner preset;
  private TextView status;
  private final String[] sizes={"4R portrait (1200 × 1800)","4R landscape (1800 × 1200)","Strip portrait (945 × 2126)","Square (1200 × 1200)","Custom"};
  private final int[][] dimensions={{1200,1800},{1800,1200},{945,2126},{1200,1200}};

  @Override public void onCreate(Bundle b){super.onCreate(b);
    getWindow().setFlags(1024,1024);
    LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(20,20,20,20);root.setBackgroundColor(Color.rgb(244,247,245));
    ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
    TextView title=new TextView(this);title.setText("PHOTOBESOK  •  OFFLINE DEMO");title.setTextSize(22);title.setTextColor(Color.rgb(22,74,56));root.addView(title);
    status=new TextView(this);status.setText("Tanpa internet / akun / QRIS. Wi-Fi hanya untuk printer lokal.");root.addView(status);
    preview=new ImageView(this);preview.setBackgroundColor(Color.WHITE);preview.setScaleType(ImageView.ScaleType.FIT_CENTER);root.addView(preview,new LinearLayout.LayoutParams(-1,440));
    preset=new Spinner(this);ArrayAdapter<String> a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,sizes);preset.setAdapter(a);root.addView(preset);
    widthPx=edit(root,"Lebar output (pixel)","1200");heightPx=edit(root,"Tinggi output (pixel)","1800");
    preset.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int position,long id){if(position<dimensions.length){widthPx.setText(""+dimensions[position][0]);heightPx.setText(""+dimensions[position][1]);}render();}});
    button(root,"1. PILIH FRAME PNG (OPSIONAL)",()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/png");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK_FRAME);});
    button(root,"2. AMBIL FOTO",()->new AlertDialog.Builder(this).setMessage("Foto diambil setelah 3 detik. Tekan OK lalu bersiap.").setPositiveButton("Mulai",(d,w)->new Handler(Looper.getMainLooper()).postDelayed(()->{Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);try{startActivityForResult(i,CAPTURE);}catch(Exception e){toast("Aplikasi kamera tidak tersedia");}},3000)).setNegativeButton("Batal",null).show());
    button(root,"3. PREVIEW / TERAPKAN UKURAN",this::render);
    button(root,"4. SIMPAN PNG KE GALERI",this::save);
    TextView printer=new TextView(this);printer.setText("PRINTER LOKAL — Koneksi Wi-Fi / Wi-Fi Direct yang telah dikonfigurasi di Android");root.addView(printer);
    printerIp=edit(root,"IP Epson L8050","192.168.1.50");
    button(root,"UJI KONEKSI PORT 515",this::testPort);
    button(root,"CETAK VIA LAYANAN CETAK ANDROID",this::printAndroid);
    TextView note=new TextView(this);note.setText("Catatan: uji TCP bukan bukti printer mendukung format cetak. Cetak memakai layanan Android yang terpasang; pengiriman LPR native belum tersedia.");root.addView(note);
  }
  private EditText edit(LinearLayout root,String hint,String value){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setText(value);root.addView(e);return e;}
  private void button(LinearLayout root,String title,Runnable run){Button b=new Button(this);b.setText(title);root.addView(b);b.setOnClickListener(v->run.run());}
  @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(result!=RESULT_OK||data==null)return;
    try{if(req==CAPTURE){Object o=data.getExtras()==null?null:data.getExtras().get("data");if(o instanceof Bitmap){photo=(Bitmap)o;render();}else toast("Kamera tidak mengembalikan foto");}
      else if(req==PICK_FRAME){Uri uri=data.getData();if(uri!=null){try(InputStream in=getContentResolver().openInputStream(uri)){frame=BitmapFactory.decodeStream(in);}render();}}
    }catch(Exception e){toast("Gagal membaca foto/frame: "+e.getMessage());}
  }
  private void render(){if(photo==null){status.setText("Pilih ukuran dan frame, lalu ambil foto.");return;}try{
    int w=Integer.parseInt(widthPx.getText().toString().trim()),h=Integer.parseInt(heightPx.getText().toString().trim());if(w<100||h<100||((long)w*h)>18000000L)throw new IllegalArgumentException("Ukuran 100 px minimum, 18 MP maksimum");
    Bitmap out=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(out);c.drawColor(Color.WHITE);Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    float scale=Math.max((float)w/photo.getWidth(),(float)h/photo.getHeight());int sw=Math.round(w/scale),sh=Math.round(h/scale);int x=(photo.getWidth()-sw)/2,y=(photo.getHeight()-sh)/2;
    c.drawBitmap(photo,new Rect(x,y,x+sw,y+sh),new Rect(0,0,w,h),p);
    if(frame!=null)c.drawBitmap(frame,null,new Rect(0,0,w,h),p);
    composed=out;preview.setImageBitmap(out);status.setText("Preview: "+w+" × "+h+" px"+(frame==null?" (tanpa overlay)":" (frame PNG)"));
  }catch(Exception e){toast("Ukuran tidak valid: "+e.getMessage());}}
  private void save(){if(composed==null){toast("Ambil foto terlebih dahulu");return;}try{
    android.content.ContentValues v=new android.content.ContentValues();v.put(MediaStore.Images.Media.DISPLAY_NAME,"photobesok_"+System.currentTimeMillis()+".png");v.put(MediaStore.Images.Media.MIME_TYPE,"image/png");if(Build.VERSION.SDK_INT>=29)v.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Photobesok");
    Uri uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);if(uri==null)throw new IOException("Tidak bisa membuat file");try(OutputStream os=getContentResolver().openOutputStream(uri)){if(os==null||!composed.compress(Bitmap.CompressFormat.PNG,100,os))throw new IOException("Gagal menulis gambar");}toast("Disimpan ke Pictures/Photobesok");
  }catch(Exception e){toast("Gagal menyimpan: "+e.getMessage());}}
  private void testPort(){String ip=printerIp.getText().toString().trim();if(ip.isEmpty()){toast("Isi IP printer");return;}io.execute(()->{String message;try(Socket s=new Socket()){s.connect(new InetSocketAddress(ip,515),3000);message="TCP 515 terbuka. Kemampuan cetak belum terbukti.";}catch(Exception e){message="Tidak terhubung: "+e.getMessage();}final String m=message;runOnUiThread(()->toast(m));});}
  private void printAndroid(){if(composed==null){toast("Ambil foto terlebih dahulu");return;}android.print.PrintManager pm=(android.print.PrintManager)getSystemService(PRINT_SERVICE);if(pm==null){toast("Layanan cetak Android tidak tersedia");return;}
    final Bitmap bitmap=composed;pm.print("Photobesok",new android.print.PrintDocumentAdapter(){
      @Override public void onLayout(android.print.PrintAttributes old,android.print.PrintAttributes attrs,android.os.CancellationSignal signal,LayoutResultCallback callback,Bundle extras){android.print.PrintDocumentInfo info=new android.print.PrintDocumentInfo.Builder("photobesok.pdf").setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_PHOTO).setPageCount(1).build();callback.onLayoutFinished(info,true);}
      @Override public void onWrite(android.print.PageRange[] pages,android.os.ParcelFileDescriptor dest,android.os.CancellationSignal signal,WriteResultCallback callback){android.graphics.pdf.PdfDocument pdf=new android.graphics.pdf.PdfDocument();try{android.graphics.pdf.PdfDocument.PageInfo pi=new android.graphics.pdf.PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();android.graphics.pdf.PdfDocument.Page page=pdf.startPage(pi);page.getCanvas().drawBitmap(bitmap,null,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Paint(3));pdf.finishPage(page);try(FileOutputStream out=new FileOutputStream(dest.getFileDescriptor())){pdf.writeTo(out);}callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});}catch(Exception e){callback.onWriteFailed(e.getMessage());}finally{pdf.close();}}
    },null);
  }
  private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
  @Override protected void onDestroy(){io.shutdownNow();super.onDestroy();}
}
