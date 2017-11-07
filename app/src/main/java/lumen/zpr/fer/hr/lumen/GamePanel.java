package lumen.zpr.fer.hr.lumen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Alen on 6.11.2017..
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private GamePhase phase;

    private Drawable img = getResources().getDrawable(R.drawable.image);
    private LetterImage letterA = new LetterImage(new Rect(100,100,200,200),img);
    private Point letterAPoint= new Point(150,150); //centar rect

    //za provjeru dropa
    private DropArea dropArea=new DropArea(new Rect(300,300,400,400), Color.rgb(255,0,0) );



    public GamePanel(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

        //phase = GamePhase.PRESENTING_WORD;
        phase = GamePhase.TYPING_WORD; // za testiranje
    }

    private enum GamePhase {
        //faza u kojoj igra prikazuje sliku, slovka i ispisuje riječ
        PRESENTING_WORD,
        //faza u kojoj igrač piše (drag and dropanjem slova) riječ
        TYPING_WORD;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        while(true) {
            try {
                thread.setRunning(false);
                thread.join();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (phase == GamePhase.PRESENTING_WORD) {
            return true;
        }
        //TODO dodati imoplementaciju za TYPING_WORD fazu
        //return super.onTouchEvent(event);

        // bolji drag and drop alg, listener?
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    letterAPoint.set((int)event.getX(), (int)event.getY());
        }


        return true;
    }

    public void update() {
        if (phase == GamePhase.PRESENTING_WORD) {
            updateWordPresentation();
            return;
        }
        letterA.update(letterAPoint);
        if(dropArea.collision(letterA)){
            letterAPoint.set(350,350);//centar drop area
            dropArea.setColor(Color.WHITE);
        }
    }

    private void updateWordPresentation() {
        //TODO dodati implementaciju
    }

    @Override
    public void draw(Canvas canvas) {
        //TODO dodati crtanje objekata zajedničkih objema fazama
        super.draw(canvas);
        canvas.drawColor(Color.WHITE); //zamjena za background
        if (phase == GamePhase.PRESENTING_WORD) {
            //TODO dodati crtanje objekata karakterističnih za PRESENTING_WORD fazu
            return;
        }

        //TODO dodati crtanje objekata karakterističnih za TYPING_WORD fazu
        letterA.draw(canvas);

        //za provjeru dropa
        dropArea.draw(canvas);

    }
}
