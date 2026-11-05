"""Exercise real installed Android screens; save evidence without app test hooks."""
import subprocess,time,xml.etree.ElementTree as ET,re,pathlib,atexit
out=pathlib.Path('ui-evidence');out.mkdir(exist_ok=True)
def adb(*args):return subprocess.check_output(['adb',*args],stderr=subprocess.STDOUT)
def diagnostics():
 (out/'runtime.txt').write_bytes(adb('logcat','-d'))
 (out/'activity.txt').write_bytes(adb('shell','dumpsys','activity','activities'))
 try:
  adb('shell','uiautomator','dump','/sdcard/window.xml');(out/'window.xml').write_bytes(adb('shell','cat','/sdcard/window.xml'))
 except Exception:pass
atexit.register(diagnostics)
def shot(name):
 (out/(name+'.png')).write_bytes(adb('exec-out','screencap','-p'))
def click(label,timeout=25):
 end=time.time()+timeout
 while time.time()<end:
  try:
   adb('shell','uiautomator','dump','/sdcard/window.xml')
   xml=adb('shell','cat','/sdcard/window.xml')
   for n in ET.fromstring(xml).iter('node'):
    if label in (n.get('text','')+' '+n.get('content-desc','')):
     if n.get('enabled')=='false':continue
     nums=list(map(int,re.findall(r'\d+',n.get('bounds',''))))
     if len(nums)==4:
      x,y,x2,y2=nums;adb('shell','input','tap',str((x+x2)//2),str((y+y2)//2));time.sleep(1);return
  except Exception:pass
  time.sleep(1)
 raise RuntimeError('Control not found: '+label)
adb('shell','pm','grant','id.photobesok.booth','android.permission.CAMERA')
adb('shell','am','start','-W','-n','id.photobesok.booth/.WelcomeActivity');time.sleep(3)
shot('01-welcome')
click('Mulai sesi foto');shot('02-layout')
click('Lanjut ke kamera');time.sleep(4);shot('03-camera-ready')
click('Mulai 3 foto');time.sleep(18)
shot('04-review')
click('Simpan foto');time.sleep(3);shot('05-saved')
click('Sesi berikutnya');click('Pengaturan');shot('06-settings')
files=adb('shell','ls','/sdcard/Pictures/Photobesok').decode()
assert '.png' in files,files
log=adb('logcat','-d','-s','AndroidRuntime:E').decode();(out/'runtime.txt').write_text(log)
assert 'FATAL EXCEPTION' not in log,log
(out/'result.txt').write_text('PASS: welcome → layout → 3 automatic camera captures → review → MediaStore save → next session → settings.\n'+files)
print('UI_SMOKE_PASS')
