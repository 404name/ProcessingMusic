import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class music extends PApplet {


AudioPlayer player;
AudioMetaData data;
Minim minim;
//###################################################################################
//源程序为我暴力识别生源和时间判断让它自动控制的，不适用所有音乐，所以效果只能自己切换
//按键大概q = 外层加一圈  w = 改变效果  t = 圆弧效果
//a切换上一张图，d切换下一张背景(背景图片backgroundxx.png)
//###################################################################################
int skip = 0;
float[] leftRangeMax = new float[2000];
int leftFlag = 0;
float[] rightRangeMax = new float[2000];
int rightFlag = 0;
int musicLen;
PImage image;
PImage bac;
Float[] textsSize = { 50.0f, 60.0f };
int[] textsColor ={0xffDB9019,0xff9900cc,0xffFF6E97,0xffEE3D11,0xff113DEE};  //文本颜色
boolean playFirst = false; //记录准确的音乐开始时间
boolean play = true;
boolean actionT = false;
boolean actionQ = false;
boolean actionW = false;
boolean actionE = false;
//##############################################################################
//设置参数区
int nowbackground = 0;
String backgroundPath = "background";
String textPath = "originalLyr.txt";  //文本文件的路径//
String textPath1 = "translatedLyr.txt";  //文本翻译文件的路径//
//color[] textsColor ={#4CB4E7,#FFC09F,#FFEE93};  //文本颜色
//color backColor = #000000;  //背景颜色
int[] TextsColor ={0xffDB9019,0xff5ED5D1,0xffFF6E97,0xffF1AAA6};  //文本颜色
int backColor = 0xff1A2D27;  //背景颜色
String textsFont = "黑体";   //文本字体名称
float nowTime,lastTime,leftTime;
Object[][]  textMain;  //要显示的文字数组//【0】文字显示时间(float),【1】文字内容(String)//
Object[][]  textMain1;  //要显示的文字数组//【0】文字显示时间(float),【1】文字内容(String)//
int showIndexNow=0,showIndexOld=-1;  //当前显示索引//历史显示索引
int showIndex1Now=0,showIndex1Old=-1;  //当前显示索引//历史显示索引
//##############################################################################
public void setup() {
  nowTime = 0;
  lastTime = 0;
  leftTime = 0;
  //size(600, 600);
  
  //frameRate(5);
  minim = new Minim(this); 
  player = minim.loadFile("music.mp3", 1024);
  musicLen = player.length();
  data = player.getMetaData();
  image = loadImage("loadImage.png");
  bac = loadImage(backgroundPath+nowbackground+".png");
  bac.resize(width, height);
  tint(255, 50);
  GetTextMain();  //获取显示文本
  leftTime=millis();
}

public void draw() {
  if (frameCount % 120 == 0 || play == false) {
    return;
  }
  if (play) {
    if(!playFirst){
        leftTime=millis();
        playFirst = true;
    }
    player.play();
  } else return;
  skip++;
  if (skip == 3) {
    skip = 0;
  } else return;
  background(255);
  fill(200);
  //打印基本边框
  strokeWeight(5);
  triangle(0, 0, 500, height / 2, 0, height);
  triangle(width, 0, width - 500, height / 2, width, height);
  float tempWidth = ((String)textMain[showIndexNow][1]).length()*textsSize[1];
  float tempWidth1 = ((String)textMain1[showIndex1Now][1]).length()*textsSize[1];
  tempWidth = max(tempWidth,tempWidth1);
  if(tempWidth!=0)
  rect(width / 2-tempWidth/2-tempWidth*0.1f, height*7/8-textsSize[1]/2, tempWidth*1.2f, textsSize[1]*2.5f,50);
  image(bac, width/2, height/2, width, height);
  tint(255, 99);
  fill(0, 50);
  stroke(0);
  //打印文字
  textAlign(CENTER, CENTER);
  textFont(createFont("微软雅黑", 30));
  textSize(150);
  fill(color(textsColor[showIndexNow % textsColor.length]));
  text(data.title(), width / 2 - (-4 + map(mouseX, 0, width, 0, 8)), 60 - (-4 + map(mouseY, 0, height, 0, 8)));
  fill(0);
  text(data.title(), width / 2, 60);
  fill(color(textsColor[showIndexNow % textsColor.length]));
  textFont(createFont("黑体", 30));
  textSize(textsSize[1]);
  showIndexNow = GetIndexShow();  //当前显示索引
  showIndex1Now = GetIndex1Show();  //当前显示索引
  if(showIndexOld != showIndexNow){
    println(textMain[showIndexNow]);
  }
  if(showIndex1Old != showIndex1Now){
    println(textMain1[showIndex1Now]);
  }
  showIndex1Old = showIndex1Now;  //历史显示索引
  showIndexOld = showIndexNow;  //历史显示索引
  text((String)textMain[showIndexNow][1],width / 2, height*7/8);
  textSize(random(textsSize[0],textsSize[1]));
  text((String)textMain1[showIndex1Now][1],width / 2, height*7/8+textsSize[1]*1.3f);
  fill(0);
  //text((player.position() / 1000) / 60 + ":" + (player.position() / 1000) % 60 + "/" + (player.length() / 1000) / 60 + ":" + (player.length() / 1000) % 60, width / 2, height / 2 + 10);

  int minSize = 150;
  int range = 130;
  /*for(int i = 0; i < player.mix.size(); i+=4)
   {
   stroke(color(255 - sin(map(i,0, player.mix.size(),0, 1) * PI) * 255, 0, sin(map(i,0, player.mix.size(),0, 1) * PI) * 255));
   line( i*width/player.mix.size(), height, i*width/player.mix.size(), height - abs(player.mix.get(i))*range*8 );
   }*/


  translate(width/2, height/2);
  int maxRange = 0;
  float lastx = 0,lastx1 = 0, startx= 0;
  float lasty = 0,lasty1 = 0, starty= 0;
  int leftsize = player.left.size();
  noFill();
  stroke(color(0xff808080));
  strokeWeight(10);
  arc(0,0,2*minSize,2*minSize,0,2*PI);
  for (int i=0; i<leftsize; i+=8) {
    stroke(color(255 - sin(map(i, 0, leftsize, 0, 1) * PI) * 255, 0, sin(map(i, 0, leftsize, 0, 1) * PI) * 255));
    float r = map(i, 0, leftsize, 0, 2 * PI);
    float s = abs(player.left.get(i))*range;
    maxRange = max(maxRange, PApplet.parseInt(s));
    if (leftFlag == 0) {
      leftRangeMax[i] = s+4;
      leftFlag = 1;
    } else leftRangeMax[i] = max(leftRangeMax[i], s);
    int tempPlace = i*width/leftsize;
    translate(-width/2, -height/2);
    strokeWeight(maxRange%3+4);
    line(0, tempPlace, s/leftRangeMax[i]*(-abs(tempPlace-height/2)+500),  tempPlace);
    if(i!=0)
    //line(s/leftRangeMax[i]*(-abs(tempPlace-height/2)+500),tempPlace,lastx1,lasty1);
    lastx1 = s/leftRangeMax[i]*(-abs(tempPlace-height/2)+500);
    lasty1 = tempPlace;
    translate(width/2, height/2);
    strokeWeight(maxRange%3+2);
    line(sin(r) * (minSize), cos(r) * (minSize), sin(r) * (s + minSize), cos(r) * (s + minSize));
    if(PApplet.parseFloat(i)/leftsize <= (millis()-leftTime)/musicLen){
        strokeWeight(10);
        line(sin(r) * (minSize), -cos(r) * (minSize), sin(r) * (minSize), -cos(r) * (minSize));
    }strokeWeight(maxRange%3+2);
    if (actionW) {
      if (i == 0) {
        startx = sin(r) * (s + minSize);
        starty = cos(r) * (s + minSize);
      } else line(sin(r) * (s + minSize), cos(r) * (s + minSize), lastx, lasty);
      lastx = sin(r) * (s + minSize);
      lasty = cos(r) * (s + minSize);
    }
  }
  if (actionW)
    line( startx, starty, lastx, lasty);

  //step2
  if (actionT) {
    noFill();
    stroke(0);
    for (int i = 0; i < 10; i=i+1) {
      stroke(color(random(0, 255), random(0, 255), random(0, 255)));
      float degree = radians(maxRange*3/6.18f);
      float pingPong = sin(degree);
      float r = 2*i*map(pingPong, -1, 1, 160, 60);
      strokeWeight(maxRange%4+i);
      ellipse(0, 0, r/2, r/2);
    }
    noStroke();
  }

  //
  int rightsize = player.right.size();
  for (int i=0; i< rightsize; i+=8) {
    minSize = 150 + maxRange;
    stroke(color(255 - sin(map(i, 0, rightsize, 0, 1) * PI) * 255, 0, sin(map(i, 0, rightsize, 0, 1) * PI) * 255));
    float r = map(i, 0, player.right.size(), 0, 2 * PI);
    float s = abs(player.right.get(i))*range;
    if (rightFlag == 0) {
      rightRangeMax[i] = s+4;
      rightFlag = 1;
    } else rightRangeMax[i] = max(rightRangeMax[i], s);
    int tempPlace = i*width/rightsize;
    translate(width/2, -height/2);
    strokeWeight(maxRange%3+4);
    line(width, tempPlace, s/rightRangeMax[i]*(abs(tempPlace-height/2)-500), tempPlace );
    if(i!=0)
    //line(s/rightRangeMax[i]*(abs(tempPlace-height/2)-500), tempPlace ,lastx1,lasty1);
    lastx1 = s/rightRangeMax[i]*(abs(tempPlace-height/2)-500);
    lasty1 = tempPlace;
    translate(-width/2, height/2);
    if (actionQ) {
      strokeWeight(maxRange%4+5);
      line(sin(r) * (minSize), cos(r) * (minSize), sin(r) * (s + minSize), cos(r) * (s + minSize));
      if (actionW) {
        if (i == 0) {
          startx = sin(r) * (s + minSize);
          starty = cos(r) * (s + minSize);
        } else line(sin(r) * (s + minSize), cos(r) * (s + minSize), lastx, lasty);
        lastx = sin(r) * (s + minSize);
        lasty = cos(r) * (s + minSize);
      }
    }
  }
  if (actionW)
    line( startx, starty, lastx, lasty);

  imageMode(CENTER);
  rotate(radians(map(player.position(), 0, player.length(), 0, 360)));
  if (actionT)image(image, 0, 0);
  else {
    if (maxRange < minSize *2/3) maxRange = minSize *2/ 3;
    image(image, 0, 0, maxRange % (minSize)*1.5f, maxRange % (minSize)*1.5f);
  }
}
public void keyPressed()
{
  if (key == 't' || key == 'T') {
    if (actionT == true) actionT = false;
    else actionT = true;
  } else if (key == 'q' || key == 'Q') {
    if (actionQ == true) actionQ = false;
    else actionQ = true;
  } else if (key == 'w' || key == 'W') {
    if (actionW == true) actionW = false;
    else actionW = true;
  } else if (key == 'e' || key == 'E') {
    if (actionE == true) actionE = false;
    else actionE = true;
  } else if(key == 'd' || key == 'D'){
       nowbackground++;
       bac = loadImage(backgroundPath+nowbackground+".png");
       if(bac == null) nowbackground = 0;
         bac = loadImage(backgroundPath+nowbackground+".png");
       bac.resize(width, height);
  } else if(key == 'a' || key == 'A'){
       nowbackground--;
       bac = loadImage(backgroundPath+nowbackground+".png");
       if(bac == null) nowbackground = 0;
          bac = loadImage(backgroundPath+nowbackground+".png");
       bac.resize(width, height);
  }
}
public void mousePressed()
{
  if (player.isPlaying()) {
    player.pause();
    lastTime = millis();
    play = false;
    println(key);
  } else {
    player.play();
    leftTime += millis() - lastTime;
    play = true;
  }
}

//获取当前显示文本的索引
public int GetIndexShow(){
    if(textMain.length ==0) return 0;
    for(int s=textMain.length-1;s>=0;s--){
        if( (float)textMain[s][0]*1000 < (millis()-leftTime))   return s;
    }
    return textMain.length-1;
}

public int GetIndex1Show(){
    if(textMain1.length ==0) return 0;
    for(int s=textMain1.length-1;s>=0;s--){
        if( (float)textMain1[s][0]*1000  < (millis()-leftTime))   return s;
    }
    return textMain1.length-1;
}
//************************************************************************
//导入文字
//注意：文本文件的编码格式为“UTF-8 无BOM”，否则中文会出现乱码。
//************************************************************************

//获取显示文本
public void GetTextMain(){ 
    String[] textLines;  //文本数组//从文件导入
    String[] textRow;  //文本行内容
    String[] textLines1;  //文本数组//从文件导入
    String[] textRow1;  //文本行内容
    textLines = TextInput(textPath);  //文本数组//
    textMain = new Object[textLines.length][2];  //要显示的文字数组
    for(int t=0;t<textLines.length;t++){
        if( !(textLines[t].contains("[") && textLines[t].contains("]") && textLines[t].contains(":")) ){
            continue;  //格式不符合
        }
        textRow = textLines[t].split("]");  //文本行内容
        if( textRow.length==1 ) textRow = new String[]{textRow[0],""};  //没有内容
        textRow[0]=(textRow[0].substring(textRow[0].indexOf("[")+1)); 
        textMain[t][0]= PApplet.parseFloat(textRow[0].split(":")[0])*60 + PApplet.parseFloat(textRow[0].split(":")[1]);   //文本时间
        textMain[t][1]=textRow[1];  //文本内容
    }
    
    textLines1 = TextInput(textPath1);  //文本数组//
    textMain1 = new Object[textLines1.length][2];  //要显示的文字数组
    for(int t=0;t<textLines1.length;t++){
        if( !(textLines1[t].contains("[") && textLines1[t].contains("]") && textLines1[t].contains(":")) ){
            continue;  //格式不符合
        }
        textRow1 = textLines1[t].split("]");  //文本行内容
        if( textRow1.length==1 ) textRow1 = new String[]{textRow1[0],""};  //没有内容
        textRow1[0]=(textRow1[0].substring(textRow1[0].indexOf("[")+1)); 
        textMain1[t][0]= PApplet.parseFloat(textRow1[0].split(":")[0])*60 + PApplet.parseFloat(textRow1[0].split(":")[1]);   //文本时间
        textMain1[t][1]=textRow1[1];  //文本内容
    }
}


//导入文本
public String[] TextInput(String tPath){
    String[] textLines = {};  //文本数组//从文件导入
    textLines =  loadStrings(tPath);  //文本数组
    println("一共导入 " + textLines.length + " 行文本");
    for (int p = 0 ; p < textLines.length; p++) {
        println(textLines[p]);
    }
    return textLines;
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "music" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
