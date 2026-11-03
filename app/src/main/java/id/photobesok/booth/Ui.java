package id.photobesok.booth;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

final class Ui {
 static final int INK=Color.rgb(24,53,44), PAPER=Color.rgb(247,245,237), GOLD=Color.rgb(239,196,94), MUTED=Color.rgb(110,123,113), LINE=Color.rgb(219,224,213);
 static int dp(Context c,float x){return Math.round(x*c.getResources().getDisplayMetrics().density);}
 static GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
 static GradientDrawable border(Context c,int color,boolean chosen){GradientDrawable d=bg(color,dp(c,22));d.setStroke(dp(c,chosen?3:1),chosen?INK:LINE);return d;}
 static TextView text(Context c,String s,int size,int color,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setFontFeatureSettings("kern");t.setTypeface(Typeface.create(bold?"sans-serif-medium":"sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));t.setLineSpacing(dp(c,3),1);return t;}
 static TextView serif(Context c,String s,int size,int color){TextView t=text(c,s,size,color,false);t.setTypeface(Typeface.create("serif",Typeface.NORMAL));return t;}
 static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);return l;}
 static void gap(LinearLayout l,int h){View v=new View(l.getContext());l.addView(v,new LinearLayout.LayoutParams(1,dp(l.getContext(),h)));}
 static Button button(Context c,String s,boolean primary,Runnable click){Button b=new Button(c);b.setText(s);b.setAllCaps(false);b.setTextSize(17);b.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));b.setTextColor(primary?INK:INK);b.setMinHeight(dp(c,58));b.setMinimumHeight(dp(c,58));b.setPadding(dp(c,22),dp(c,10),dp(c,22),dp(c,10));b.setBackground(primary?bg(GOLD,dp(c,18)):border(c,Color.WHITE,false));b.setElevation(dp(c,primary?2:0));b.setOnClickListener(v->click.run());b.setContentDescription(s);return b;}
 static void full(Activity a){a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}
 static int theme(int i){return i==1?Color.rgb(249,243,224):i==2?Color.rgb(128,62,72):INK;}
 static int themeText(int i){return i==1?INK:Color.WHITE;}
 static Rect slot(int index,int count,int w,int h){int cols=count==4?2:1,rows=count==4?2:count;int m=w/24,g=w/50,usable=h-h/9;int cw=(w-2*m-(cols-1)*g)/cols,ch=(usable-m-(rows-1)*g)/rows;int x=m+(index%cols)*(cw+g),y=m+(index/cols)*(ch+g);return new Rect(x,y,x+cw,y+ch);}
 static void sample(Canvas c,RectF r,int variant){Paint p=new Paint(3);c.save();c.clipRect(r);p.setShader(new LinearGradient(r.left,r.top,r.right,r.bottom,new int[]{0xffc9d5bf,0xff8ba89b,0xff446d61},null,Shader.TileMode.CLAMP));c.drawRect(r,p);p.setShader(null);p.setColor(0xfff5d68c);c.drawCircle(r.left+r.width()*(.25f+.16f*variant),r.top+r.height()*.27f,r.width()*.14f,p);p.setColor(0xff678677);c.drawOval(new RectF(r.left-r.width()*.3f,r.top+r.height()*.58f,r.right+r.width()*.2f,r.bottom+r.height()*.7f),p);p.setColor(0xff254e41);c.drawOval(new RectF(r.left+r.width()*.4f,r.top+r.height()*.48f,r.right+r.width()*.5f,r.bottom+r.height()*.65f),p);c.restore();}
 static class Artwork extends View {
  int count,style;boolean tilted;
  Artwork(Context c,int n,int s,boolean tilt){super(c);count=n;style=s;tilted=tilt;setContentDescription(n+" foto, contoh tata letak");}
  protected void onDraw(Canvas canvas){super.onDraw(canvas);float w=getWidth(),h=getHeight();float ph=Math.min(h*.92f,w*1.4f),pw=ph*2/3;float x=(w-pw)/2,y=(h-ph)/2;canvas.save();if(tilted)canvas.rotate(-7,w/2,h/2);Paint p=new Paint(3);p.setColor(0x18000000);canvas.drawRoundRect(new RectF(x+9,y+12,x+pw+9,y+ph+12),6,6,p);canvas.translate(x,y);canvas.scale(pw/1200,ph/1800);p.setColor(theme(style));canvas.drawRect(0,0,1200,1800,p);for(int i=0;i<count;i++){Rect r=slot(i,count,1200,1800);sample(canvas,new RectF(r),i%3);}p.setColor(themeText(style));p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));p.setTextSize(48);canvas.drawText("PHOTOBESOK",600,1715,p);p.setTextSize(20);canvas.drawText("LITTLE MOMENTS, FOREVER.",600,1760,p);canvas.restore();}
 }
}
