package com.limahao.ticket.utils;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class UtilsImg {
	  /**
     * 将图片截取为圆角图片
     * @param bitmap 原图片
     * @param ratio 截取比例，如果是8，则圆角半径是宽高的1/8，如果是2，则是圆形图片
     * @return 圆角矩形图片
     */
      public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {
              
              Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                              bitmap.getHeight(), Config.ARGB_8888);
              Canvas canvas = new Canvas(output);

              final Paint paint = new Paint();
              final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
              final RectF rectF = new RectF(rect);

              paint.setAntiAlias(true);
              canvas.drawARGB(0, 0, 0, 0);
              canvas.drawRoundRect(rectF, bitmap.getWidth()/ratio, 
                              bitmap.getHeight()/ratio, paint);

              paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
              canvas.drawBitmap(bitmap, rect, rect, paint);
              return output;
      } 
      public static Bitmap getRoundedCornerBitmap(Bitmap bitmap){
          Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
          Canvas canvas = new Canvas(outBitmap);
          final int color =0xff424242;
          final Paint paint = new Paint();
          final Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
          final RectF rectF = new RectF(rect);
          final float roundPX = bitmap.getWidth()/2;
          paint.setAntiAlias(true);
          canvas.drawARGB(0,0,0,0);
          paint.setColor(color);
          canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
          paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
          canvas.drawBitmap(bitmap, rect, rect, paint);
          return outBitmap;
      }
      
      public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {  
          
          Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
          Canvas canvas = new Canvas(output);  
    
          final int color = 0xff424242;  
          final Paint paint = new Paint();  
          final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
          final RectF rectF = new RectF(rect);  
          final float roundPx = pixels;  
    
          paint.setAntiAlias(true);  
          canvas.drawARGB(0, 0, 0, 0);  
          
          paint.setColor(color);  
          canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
          paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
          canvas.drawBitmap(bitmap, rect, rect, paint);  
          System.out.println("pixels+++++++"+pixels);
          
    
          return output;  
      }
      
      public static Bitmap getCircleBitmap(Bitmap bitmap) {
     	 int x = bitmap.getWidth();
          Bitmap output = Bitmap.createBitmap(x,
                  x, Config.ARGB_8888);
          Canvas canvas = new Canvas(output);
   
          final int color = 0xff424242;
          final Paint paint = new Paint();
          // 根据原来图片大小画一个矩形
          final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
          paint.setAntiAlias(true);
          paint.setColor(color);
          // 画出一个圆
          canvas.drawCircle(x/2, x/2, x/2, paint);
          // 取两层绘制交集,显示上层
          paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
          // 将图片画上去
          canvas.drawBitmap(bitmap, rect, rect, paint);
          // 返回Bitmap对象
          return output;
     }
   
}
