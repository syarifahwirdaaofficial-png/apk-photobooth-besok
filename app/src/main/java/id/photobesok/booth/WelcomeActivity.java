package id.photobesok.booth;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

/** Local-only entry screen; no remote resources or login. */
public class WelcomeActivity extends Activity {
 @Override public void onCreate(Bundle state){super.onCreate(state);
  getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setGravity(Gravity.CENTER);root.setPadding(32,48,32,48);root.setBackgroundColor(Color.rgb(19,59,46));
  TextView brand=text("PHOTOBESOK / STUDIO",18);root.addView(brand);
  TextView heading=text("Senyum dulu,\nyuk!",48);heading.setTypeface(Typeface.DEFAULT,Typeface.BOLD);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=48;root.addView(heading,hp);
  TextView strip=text("01  ·  02  ·  03\nMomen kecil, kenangan besar.",22);strip.setPadding(20,40,20,40);root.addView(strip);
  Button start=new Button(this);start.setText("MULAI FOTO");start.setTextSize(21);start.setTextColor(Color.rgb(19,59,46));GradientDrawable bg=new GradientDrawable();bg.setColor(Color.rgb(245,197,68));bg.setCornerRadius(28);start.setBackground(bg);root.addView(start,new LinearLayout.LayoutParams(-1,80));start.setOnClickListener(v->startActivity(new Intent(this,MainActivity.class)));
  TextView note=text("3 foto • Tersimpan di perangkat\nTanpa akun dan tanpa internet",16);note.setPadding(0,28,0,0);root.addView(note);
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(root);setContentView(scroll);
 }
 private TextView text(String label,int size){TextView v=new TextView(this);v.setText(label);v.setTextSize(size);v.setTextColor(Color.WHITE);v.setGravity(Gravity.CENTER);return v;}
}
