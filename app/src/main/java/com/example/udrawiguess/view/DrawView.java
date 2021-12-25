package com.example.udrawiguess.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.udrawiguess.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Handler;

public class DrawView extends View {
    public ViewGroup.LayoutParams params;
    private Path path = new Path();
    private Paint paint = new Paint();
    private Canvas canvas;
    private Bitmap bitmap;
    private Deque<PathData> deque;
    private Deque<PathData> redoDeque;
    private int w;
    private int h;
    private OnTouchMessageListener onTouchMessageListener;
    private byte[] bitMapByteArray = null;
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(8f);

        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        Request request = new Request.Builder().url("ws://47.254.242.71/udrawiguess/ws").build();
//        listener = new Listener();
//        webSocket = client.newWebSocket(request, listener);
    }

    public void undo() {
        if(deque != null && !deque.isEmpty()) {
            redoDeque.push(deque.pop());
        }
        invalidate();
    }

    public void redo() {
        if(redoDeque != null && !redoDeque.isEmpty()) {
            deque.push(redoDeque.pop());
        }
        invalidate();
    }

    public void refresh() {
        byte[] bytes = createBitMapByteArray(this.bitmap);
        newCanvas();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setBitMapByteArray(bytes);
                System.out.println("set");
            }
        }).start();
    }

    public boolean save(Activity activity) {
        Bitmap bitmap = getBitmap(activity, this);
        if(bitmap == null) {
            return false;
        }
        File dir = new File(activity.getExternalFilesDir("UDrawIGuess").getAbsolutePath());
        if(!dir.exists()) {
            dir.mkdir();
        }
        System.out.println(dir.exists());
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dir)));
//        webSocket.send("save");
        return true;
    }

    private Bitmap getBitmap(Activity activity, View view){
        View screenView = activity.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        Bitmap bitmap = screenView.getDrawingCache();

        if (bitmap != null) {
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();

            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);

            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
//        canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
//        draw(canvas);
        invalidate();
    }

    public void newCanvas(){
        clearDeque();
        bitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        invalidate();
        System.out.println("Im done");
    }

    private void clearDeque() {
        if(deque == null) {
            deque = new LinkedList<>();
        } else {
            deque.clear();
        }

        if(redoDeque == null) {
            redoDeque = new LinkedList<>();
        } else {
            redoDeque.clear();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
        newCanvas();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
//                canvas.drawPath(path, paint);
                PathData pathData = new PathData(new Paint(paint), new Path(path));
                deque.push(pathData);
                if(redoDeque != null) {
                    redoDeque.clear();
                }
                path.reset();

                byte[] bytes = createBitMapByteArray(this.bitmap);
//                if(bitMapByteArray != null) {
//                    Util.compare(bitMapByteArray, bytes);
//                }
                onTouchMessageListener.onTouch(bytes);
//                String str = createBitMapString();
//                onTouchMessageListener.onTouch(str);

                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        for(PathData pathData: deque) {
            pathData.draw(canvas);
        }
        Bitmap bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawPath(path, paint);
        this.bitmap = bmp;
        for(PathData pathData: deque) {
            pathData.draw(c);
        }
//        canvas.drawBitmap(bitmap, 0, 0, paint);
        invalidate();

    }
    public void setOnTouchMessageListener(OnTouchMessageListener onTouchMessageListener) {
        this.onTouchMessageListener = onTouchMessageListener;
    }

    public class PathData {
        Paint paint;
        Path path;

        public PathData(Paint paint, Path path) {
            this.paint = paint;
            this.path = path;
        }

        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @NonNull
        @Override
        public String toString() {
            return "paint: "+paint+"\npath: "+path;
        }
    }

    public byte[] createBitMapByteArray(Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
//        int number = bitmap.getByteCount();
//        ByteBuffer buf = ByteBuffer.allocate(number);
//        bitmap.copyPixelsToBuffer(buf);
//        byte[] bytes = buf.array();

//        System.out.println("createBitMapByteArray() "+Arrays.toString(bytes));
        System.out.println("send length "+bytes.length);
        return bytes;
    }

    public void setBitMapByteArray(byte[] bitMapByteArray) {
//        System.out.println("setBitMapByteArray(byte[] bitMapByteArray)"+Arrays.toString(bitMapByteArray));
        System.out.println("received length "+bitMapByteArray.length);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bitMapByteArray, 0, bitMapByteArray.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitMapByteArray, 0, bitMapByteArray.length).copy(Bitmap.Config.ARGB_8888, true);
//        this.bitmap = Bitmap.createBitmap(bitmap);
        this.bitmap = bitmap;

//        canvas = new Canvas(this.bitmap);
        canvas.drawBitmap(this.bitmap, 0, 0, paint);
//        draw(canvas);
        invalidate();
    }

    public String createBitMapString() {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        System.out.println("createBitMapString() "+str);
        return str;
    }

    public void setBitMapString(String str) {
        System.out.println("setBitMapString(String str) "+str);
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        setBitmap(bitmap);
    }

    public interface OnTouchMessageListener {
        void onTouch(byte[] bytes);
//        void onTouch(String str);
    }


}
