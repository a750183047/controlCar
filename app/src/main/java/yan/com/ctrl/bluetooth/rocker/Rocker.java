package yan.com.ctrl.bluetooth.rocker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import yan.com.ctrl.bluetooth.Data.Data;
import yan.com.ctrl.bluetooth.bluetooth.BluetoothComm;

/**
 * 摇杆实现类
 * Created by yan on 2015/9/22.
 */
public class Rocker extends SurfaceView implements Runnable,SurfaceHolder,SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder mHolder;
    private boolean isStop = false;
    private Thread mThread;
    ////
    private Runnable runnable;
    ////

    private Paint mPaint;

    private Point mRockerPosition;                  //摇杆位置
    private Point mCtrlPoint = new Point(80,80);    //摇杆起始位置
    private int mRudderRadius = 30;                 //摇杆半径
    private int mWheelRadius = 80;                  //摇杆活动半径

    Bitmap rocker_bg,rocker_ctrl;

    private RudderListener listener = null;         //事件回调接口
    public static final int ACTION_RUDDER = 1,ACTION_ATTACK = 2;      //1:摇杆事件 ，2：按钮事件（未实现）

    public Rocker(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        //数值修正
        mWheelRadius = DensityUtil.dip2px((ContextThemeWrapper) context, mWheelRadius);
        mRudderRadius = DensityUtil.dip2px((ContextThemeWrapper) context, mRudderRadius);

        this.setKeepScreenOn(true);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);                      //抗锯齿
        mRockerPosition = new Point(mCtrlPoint);        //设置起始位置
        setFocusable(true);                             //可获得焦点（键盘）
        setFocusableInTouchMode(true);                  //可获得焦点（触摸）
        setZOrderOnTop(true);                           //保持在界面最上层
        mHolder.setFormat(PixelFormat.TRANSPARENT);     //设置背景透明


        //画图线程  要执行的动作
        runnable = new Runnable() {
            @Override
            public void run() {
                Canvas canvas =null;
                while(!isStop){
                    try{

                        canvas = mHolder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);              //清楚屏幕
                        if(rocker_bg != null){
                            canvas.drawBitmap(rocker_bg,mCtrlPoint.x - mWheelRadius,mCtrlPoint.y - mWheelRadius,mPaint);        //这里的60px是最外围的图片半径
                        }else{
                            mPaint.setColor(Color.CYAN);
                            canvas.drawCircle(mCtrlPoint.x,mCtrlPoint.y,mWheelRadius,mPaint);       //绘制范围
                        }
                        if(rocker_ctrl != null){
                            canvas.drawBitmap(rocker_ctrl,mRockerPosition.x - mRudderRadius,mRockerPosition.y - mRudderRadius,mPaint); //这里的 20px是最里面的图片半径
                        }else{
                            mPaint.setColor(Color.RED);
                            canvas.drawCircle(mRockerPosition.x,mRockerPosition.y,mWheelRadius,mPaint); //绘制摇杆
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(canvas != null){
                            mHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    try {
                        Thread.sleep(30);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

    }
        /**
         * 设置摇杆背景图片
         * @param bitmap
         * **/
        public void setRockerBackground(Bitmap bitmap){
            rocker_bg = Bitmap.createScaledBitmap(bitmap, mWheelRadius * 2, mWheelRadius * 2, true);
            Log.d("ss","wheelrg :"+mWheelRadius * 2);
        }
    /**
     * 设置摇杆图片
     * @param bitmap
     *
     * **/
    public void setRockerCtrl(Bitmap bitmap){
        rocker_ctrl = Bitmap.createScaledBitmap(bitmap, mRudderRadius * 2, mRudderRadius * 2, true);
    }
    /**
     * 设置摇杆活动半径
     * @param radius
     * */
    public void setmWheelRadius(int radius){
        mWheelRadius = DensityUtil.dip2px((ContextThemeWrapper) context, radius);
    }
    /**
     * 设置摇杆半径
     *
     * @param radius
     * **/
    public void setmRudderRadius(int radius){
        mRudderRadius  = DensityUtil.dip2px((ContextThemeWrapper) context,radius);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //确定中心点
        Data data = new Data();

        Log.e("Thread"," "+data.getisok());
        int width = this.getWidth();
        int height = this.getHeight();
        mCtrlPoint = new Point(width / 2, height / 2);
        mRockerPosition = new Point(mCtrlPoint);
       // mThread.start();

        if(true){
          //  new Thread(runnable).start();
       //
        }
        //

      //  Log.e("Wh", width + ":" + height);


           mThread.start();



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isStop = true;
        Log.e("Thread","画图停止了");

    }

    @Override
    public void run() {
        Canvas canvas =null;
        while(!isStop){
            try{

                canvas = mHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);              //清楚屏幕
                if(rocker_bg != null){
                    canvas.drawBitmap(rocker_bg,mCtrlPoint.x - mWheelRadius,mCtrlPoint.y - mWheelRadius,mPaint);        //这里的60px是最外围的图片半径
                }else{
                    mPaint.setColor(Color.CYAN);
                    canvas.drawCircle(mCtrlPoint.x,mCtrlPoint.y,mWheelRadius,mPaint);       //绘制范围
                }
                if(rocker_ctrl != null){
                    canvas.drawBitmap(rocker_ctrl,mRockerPosition.x - mRudderRadius,mRockerPosition.y - mRudderRadius,mPaint); //这里的 20px是最里面的图片半径
                }else{
                    mPaint.setColor(Color.RED);
                    canvas.drawCircle(mRockerPosition.x,mRockerPosition.y,mWheelRadius,mPaint); //绘制摇杆
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(canvas != null){
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(30);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void addCallback(Callback callback) {

    }

    @Override
    public void removeCallback(Callback callback) {

    }

    @Override
    public boolean isCreating() {
        return false;
    }

    @Override
    public void setType(int type) {

    }

    @Override
    public void setFixedSize(int width, int height) {

    }

    @Override
    public void setSizeFromLayout() {

    }

    @Override
    public void setFormat(int format) {

    }

    @Override
    public Canvas lockCanvas() {
        return null;
    }

    @Override
    public Canvas lockCanvas(Rect dirty) {
        return null;
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {

    }

    @Override
    public Rect getSurfaceFrame() {
        return null;
    }

    @Override
    public Surface getSurface() {
        return null;
    }

    //回调接口
    public interface RudderListener {
        void onSteeringWheelChanged(int action, int angle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = MathUtils.getLength(mCtrlPoint.x,mCtrlPoint.y,event.getX(),event.getY());
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //如果屏幕触摸点不再摇杆范围内，则不处理
            Log.e("Rocket","len:"+len);
            if(len > mWheelRadius){
                return true;
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(listener != null){
                listener.onSteeringWheelChanged(ACTION_RUDDER,0);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(len <= mWheelRadius){
                //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                mRockerPosition.set((int)event.getX(),(int)event.getY());
            }else {
                //设置摇杆位置，使其处于手指触摸方向的摇杆活动范围边缘
                mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint,new Point((int) event.getX(),(int) event.getY()),mWheelRadius);
            }
            if(listener != null){
                float radian = MathUtils.getRadian(mCtrlPoint,new Point((int) event.getX(),(int) event.getY()));
                listener.onSteeringWheelChanged(ACTION_RUDDER,Rocker.this.getAngleCouvert(radian));
            }
        }
        //如果手指离开屏幕，则摇杆返回初始位置
        if(event.getAction() == MotionEvent.ACTION_UP){
            mRockerPosition = new Point(mCtrlPoint);
        }
        return true;
    }
    //获取摇杆偏移角度
    private int getAngleCouvert(float radian){
        int tmp = (int) Math.round(radian /Math.PI * 180);
        if(tmp < 0){
            return -tmp;
        }else{
            return 180 + (180 - tmp);
        }
    }

    //设置回调接口
    public void setRudderListener(RudderListener rockerListener) {
        listener = rockerListener;
    }
}

/**
 * 数学工具
 * **/
class MathUtils{
    //获取两点直线距离
    public static int getLength(float x1,float y1,float x2,float y2){
        return (int) Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
    /**
     * 获取线段上某个点的坐标，长度为 a.x - cutRadius
     *
     * @param a      点 a
     * @param b      点 b
     * @param cutRadius  截断距离
     * @return  截断点
     * **/
    public static Point getBorderPoint(Point a,Point b,int cutRadius){
        float radian = getRadian(a,b);
        return new Point(a.x+(int)(cutRadius * Math.cos(radian)),a.y +(int)(cutRadius * Math.sin(radian)));
    }
    //获得水平夹角弧度
    public static float getRadian(Point a,Point b){
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang  = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y <a.y ? -1 : 1);
        return ang;
    }
}

/**
 * 转换分辨率
 *
 * */
class DensityUtil{

    /***
     *
     * 根据手机分辨率从dx的单位转换为 px
     */
    public static int dip2px(ContextThemeWrapper contextThemeWrapper ,float dpValue){
        final float scale = contextThemeWrapper.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale +0.5f);
    }
    /**
     * 根据手机分辨率从px转换为dp
     * */
    public static int px2dip(ContextThemeWrapper contextThemeWrapper ,float pxValue){
        final float scale = contextThemeWrapper.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale +0.5f);
    }
}