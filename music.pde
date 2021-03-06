import ddf.minim.*;
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
Float[] textsSize = { 50.0, 60.0 };
color[] textsColor ={#DB9019,#9900cc,#FF6E97,#EE3D11,#113DEE};  //文本颜色
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
color[] TextsColor ={#DB9019,#5ED5D1,#FF6E97,#F1AAA6};  //文本颜色
color backColor = #1A2D27;  //背景颜色
String textsFont = "黑体";   //文本字体名称
float nowTime,lastTime,leftTime;
Object[][]  textMain;  //要显示的文字数组//【0】文字显示时间(float),【1】文字内容(String)//
Object[][]  textMain1;  //要显示的文字数组//【0】文字显示时间(float),【1】文字内容(String)//
int showIndexNow=0,showIndexOld=-1;  //当前显示索引//历史显示索引
int showIndex1Now=0,showIndex1Old=-1;  //当前显示索引//历史显示索引
//##############################################################################
void setup() {
  nowTime = 0;
  lastTime = 0;
  leftTime = 0;
  //size(600, 600);
  fullScreen();
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

void draw() {
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
  rect(width / 2-tempWidth/2-tempWidth*0.1, height*7/8-textsSize[1]/2, tempWidth*1.2, textsSize[1]*2.5,50);
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
  text((String)textMain1[showIndex1Now][1],width / 2, height*7/8+textsSize[1]*1.3);
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
  stroke(color(#808080));
  strokeWeight(10);
  arc(0,0,2*minSize,2*minSize,0,2*PI);
  for (int i=0; i<leftsize; i+=8) {
    stroke(color(255 - sin(map(i, 0, leftsize, 0, 1) * PI) * 255, 0, sin(map(i, 0, leftsize, 0, 1) * PI) * 255));
    float r = map(i, 0, leftsize, 0, 2 * PI);
    float s = abs(player.left.get(i))*range;
    maxRange = max(maxRange, int(s));
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
    if(float(i)/leftsize <= (millis()-leftTime)/musicLen){
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
      float degree = radians(maxRange*3/6.18);
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
    image(image, 0, 0, maxRange % (minSize)*1.5, maxRange % (minSize)*1.5);
  }
}
void keyPressed()
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
void mousePressed()
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
