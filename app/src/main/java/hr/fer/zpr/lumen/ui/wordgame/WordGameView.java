package hr.fer.zpr.lumen.ui.wordgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import hr.fer.zpr.lumen.dagger.application.LumenApplication;
import hr.fer.zpr.lumen.ui.viewmodels.CoinModel;
import hr.fer.zpr.lumen.ui.viewmodels.GameDrawable;
import hr.fer.zpr.lumen.ui.wordgame.models.ImageModel;
import hr.fer.zpr.lumen.ui.wordgame.models.LetterFieldModel;
import hr.fer.zpr.lumen.ui.wordgame.models.LetterModel;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;


public class WordGameView extends SurfaceView implements SurfaceHolder.Callback {

    public static final int MILLIS_PER_FRAME = 33;
    @Inject
    WordGamePresenter presenter;
    private Disposable gameLoopDisposable = Disposables.disposed();
    private List<LetterFieldModel> fields = new ArrayList<>();
    private List<LetterModel> letters = new ArrayList<>();
    private List<GameDrawable> drawables = new ArrayList<>();
    private SparseArray<LetterModel> mLetterPointer = new SparseArray<>();
    private CoinModel coin;
    private Context context;
    private int screenHeight;
    private int screenWidth;
    private ImageModel image;
    private LetterModel draggedLetter;
    private LetterFieldModel fieldOfLetterDraggedOutOffield;


    public WordGameView(LumenApplication context) {
        super(context);
        this.context = context;
        context.getApplicationComponent().inject(this);
        getHolder().addCallback(this);
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoopDisposable = Flowable.interval(MILLIS_PER_FRAME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> updateView(holder), x -> {
                    Log.d("error", Log.getStackTraceString(x));
                });
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoopDisposable.dispose();
    }

    private void updateView(SurfaceHolder holder) {
        updateAddingLettersToFields(false);
        Canvas canvas = holder.lockCanvas();
        this.draw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        List<GameDrawable> copy = new ArrayList<>(drawables);
        for (GameDrawable drawable : copy) drawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!presenter.shouldHandleTouch()) return super.onTouchEvent(event);
        boolean handled = false;
        LetterModel touchedLetter;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLetterPointer.clear();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                if (coin.isTouched(xTouch, yTouch)) presenter.hintPressed();
                touchedLetter = getTouchedLetter(xTouch, yTouch);
                if (touchedLetter == null) return true;
                touchedLetter.setCenter(xTouch, yTouch);
                setLetterBeingDragged(touchedLetter);


                mLetterPointer.put(event.getPointerId(0), touchedLetter);

                invalidate();
                handled = true;
                break;


            case MotionEvent.ACTION_POINTER_DOWN:
                pointerId = event.getPointerId(actionIndex);


                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);


                touchedLetter = getTouchedLetter(xTouch, yTouch);
                if (touchedLetter == null) return true;

                mLetterPointer.put(pointerId, touchedLetter);
                touchedLetter.setCenter(xTouch, yTouch);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();


                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedLetter = mLetterPointer.get(pointerId);

                    if (null != touchedLetter) {
                        touchedLetter.setCenter(xTouch, yTouch);
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                mLetterPointer.clear();
                invalidate();
                handled = true;
                updateAddingLettersToFields(true);
                setLetterBeingDragged(null);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerId = event.getPointerId(actionIndex);

                mLetterPointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    private void updateAddingLettersToFields(boolean actionUpJustOccured) {
        outerLoop:
        for (LetterFieldModel field : fields) {
            LetterModel letterInside = field.getLetterInside();
            if (letterInside != null && !field.getRect().contains(letterInside.getRect().centerX(), letterInside.getRect().centerY())) {
                field.detachLetter();
                presenter.letterRemoved(field);
            }

            for (LetterModel letter : letters) {
                if (!actionUpJustOccured) {
                    continue;
                }
                if (field.getRect().contains(letter.getRect().centerX(), letter.getRect().centerY()) && letter == draggedLetter && fieldOfLetterDraggedOutOffield != field) {
                    Point newCenter = field.getCenter();
                    if (letterInside != null && letterInside != letter) {
                        if (draggedLetter.getRect().centerY() > field.getRect().centerY()) {
                            letterInside.setCenter(field.getRect().centerX(), field.getRect().top - letterInside.getHeight() / 2);
                        } else {
                            letterInside.setCenter(field.getRect().centerX(), field.getRect().bottom + letterInside.getHeight() / 2);
                        }

                    }
                    if (field.getLetterInside() != letter) {
                        field.attachLetter(letter);
                        presenter.letterInserted(letter, field);
                        letter.setCenter(newCenter.x, newCenter.y);
                    }

                }
            }
        }
    }

    public void setLetterBeingDragged(LetterModel letterBeingDragged) {
        if (letterBeingDragged == null) {
            fieldOfLetterDraggedOutOffield = null;
            return;
        }
        this.draggedLetter = letterBeingDragged;

        for (LetterFieldModel field : fields) {
            if (field.getLetterInside() == letterBeingDragged) {
                fieldOfLetterDraggedOutOffield = field;
                field.attachLetter(null);
                presenter.letterRemoved(field);
                break;
            }
        }
    }


    private LetterModel getTouchedLetter(int x, int y) {
        for (LetterModel letter : letters) {
            if (letter.isTouched(x, y)) return letter;
        }
        return null;
    }


    public void addDrawable(GameDrawable drawable) {
        drawables.add(drawable);
    }

    public void clearDrawables() {
        drawables.clear();
        drawables.add(coin);
    }

    public void removeDrawable(GameDrawable drawable) {
        this.drawables.remove(drawable);
    }

    public void addDrawables(List<GameDrawable> drawables) {
        this.drawables.addAll(drawables);
    }

    public void addFields(List<LetterFieldModel> fields) {
        this.drawables.addAll(fields);
        this.fields = fields;
    }

    public void addLetters(List<LetterModel> letters) {
        this.drawables.addAll(letters);
        this.letters = letters;
    }


    public void setCoin(CoinModel model) {
        this.coin = model;
        this.drawables.add(coin);
    }


}
