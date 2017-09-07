package com.ecdav.map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by iamwa on 2017/2/10.
 */

public class MapView extends SurfaceView implements SurfaceHolder.Callback{
    /**
     * 静态
     */
    private static final int MapWidth=3000;//地图宽度
    private static final int MapHeight=3000;//地图高度
    private static final int xPiece=10;//x方向地图块数
    private static final int yPiece=10;//y方向地图块数
    private static final int pieceWidth=300;//每一小块地图的宽度
    private static final int pieceHeight=300;//每一小块地图的高度
    public static double x=0;//手机所在处的x坐标
    public static double y=0;//手机所在处的y坐标
    /**
     *一般变量
     */
    private final SurfaceHolder surfaceHolder=getHolder();//获取surfaceHodler
    public DrawThread drawThread=null;//定义DrawThread类的实例
    /**
     * 与地图移动相关的变量
     */
    private int xStart;//触点开始时的x坐标
    private int xEnd;//触点结束时的x坐标
    private int yStart;//触点开始时的y坐标
    private int yEnd;//触点结束时的y坐标
    private int xD=0;//x方向的偏移量
    private int yD=0;//y方向的偏移量
    private int screenDx=0;//canvas对应的x方向上的偏移量
    private int screenDy=0;//canvas对应的y方向上的偏移量
    private int screenWidth;//控件宽度
    private int screenHeight;//控件高度
    /**
     * 构造器
     * @param context
     */
    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    /*构造器到此为止*/
    /**
     * 初始化
     */
    private void init(){
        drawThread=new DrawThread(surfaceHolder);
        drawThread.start();
    }

    /**
     * 重写的方法
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(drawThread==null){
            drawThread=new DrawThread(surfaceHolder);
            drawThread.start();
        }
        drawThread.isRun=true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.isRun=false;
    }
    /**
     * 绘制图片的类
     */
    class DrawThread extends Thread{
        /**
         * 运行必须参数
         */
        private PointerStatus pointerStatus=new PointerStatus(0,0,0);
        private SurfaceHolder surfaceHolder=null;
        private Canvas canvas=null;
        public boolean isRun=true;
        /**图片资源
         */
        private Bitmap[][] mapPictures=new Bitmap[xPiece][yPiece];
        private Bitmap pointer=BitmapFactory.decodeResource(getResources(),R.drawable.pointer);
        /**
         * 和地图移动有关的变量
         */

        /**
         * 构造器
         * @param surfaceHolder
         */
        public DrawThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder=surfaceHolder;
            getMapPictures();
        }
        public void setPointerStatus(PointerStatus pointerStatus){
            this.pointerStatus=pointerStatus;
        }
        private void getMapPictures(){
            for(int i=0;i<yPiece;i++){
                for(int j=0;j<xPiece;j++){
                    mapPictures[i][j]=getBitmapFromAssets(""+i+"_"+j+".png");
                }
            }
        }
        private Bitmap getBitmapFromAssets(String fileName){
            Bitmap image = null;

            AssetManager am = getResources().getAssets();

            try {

                InputStream is = am.open(fileName);
                image = BitmapFactory.decodeStream(is);
                is.close();
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            return image;
        }
        @Override
        public void run() {
            while (isRun){
                drawMap();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void drawMap(){

            try {
                synchronized(surfaceHolder){
                    canvas=surfaceHolder.lockCanvas();
                    canvas.save();
                    canvas.translate(-screenDx,-screenDy);
                    for(int i=0;i<xPiece;i++){
                        for(int j=0;j<yPiece;j++){
                            canvas.drawBitmap(mapPictures[j][i],pieceWidth*i,pieceHeight*j,null);
                        }
                    }
                    if(pointerStatus.x+pointer.getWidth()/2>=screenDx&&pointerStatus.y+pointer.getHeight()/2>=screenDy)
                    {
                        canvas.translate((float)pointerStatus.x-screenDx,(float)pointerStatus.y-screenDy);
                        canvas.rotate(pointerStatus.direction);
                        canvas.drawBitmap(pointer,0,0,null);
                    }

                }
            }
            catch (Exception e){
                e.printStackTrace();
                canvas=null;
            }
            if(canvas!=null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 地图移动
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()==1){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    xStart=(int)event.getX();
                    yStart=(int)event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    xEnd=(int)event.getX();
                    yEnd=(int)event.getY();
                    if(distanceCaculate(xStart,xEnd,yStart,yEnd)>20){
                        xD=xEnd-xStart;
                        yD=yEnd-yStart;
                        screenDx=screenDx-xD;
                        screenDy=screenDy-yD;
                        if(screenDx<0){
                            screenDx=0;
                        }
                        if(screenDx>MapWidth-screenWidth){
                            screenDx=MapWidth-screenWidth;
                        }
                        if(screenDy<0){
                            screenDy=0;
                        }
                        if(screenDy>MapHeight-screenHeight){
                            screenDy=MapHeight-screenHeight;
                        }
                        xStart=xEnd;
                        yStart=yEnd;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }


        return true;
    }

    /**
     *计算移动前后两点之间的距离
     *
     */
    private int distanceCaculate(int xS,int xE,int yS,int yE){
        int distance=(int)Math.sqrt((xE-xS)*(xE-xS)+(yE-yS)*(yE-yS));
        return distance;
    }

    /**
     * 获控件的宽度和高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWidth=(MeasureSpec.getSize(widthMeasureSpec));
        screenHeight=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(screenWidth,screenHeight);
    }

}
