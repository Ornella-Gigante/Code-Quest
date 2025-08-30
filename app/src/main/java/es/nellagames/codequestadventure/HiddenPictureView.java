package es.nellagames.codequestadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import es.nellagames.codequestadventure.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HiddenPictureView extends View {

    // Arrays of hidden images info, resources, names, and descriptions
    private static final int[] HIDDEN_IMAGE_RESOURCES = {
            R.drawable.bird,
            R.drawable.cabin,
            R.drawable.dino,
            R.drawable.dog,
            R.drawable.dragon,
            R.drawable.fox,
            R.drawable.panda,
            R.drawable.play,
            R.drawable.robot,
            R.drawable.unicorn
    };

    private static final String[] HIDDEN_IMAGE_NAMES = {
            "🐦 Bird",
            "🏠 Cabin",
            "🦕 Dinosaur",
            "🐕 Dog",
            "🐉 Dragon",
            "🦊 Fox",
            "🐼 Panda",
            "🎮 Play",
            "🤖 Robot",
            "🦄 Unicorn"
    };

    private static final String[] HIDDEN_IMAGE_DESCRIPTIONS = {
            "A beautiful little bird singing",
            "A cozy cabin in the woods",
            "A friendly dinosaur",
            "A playful dog",
            "A magical dragon",
            "A clever fox",
            "A cute panda eating bamboo",
            "Fun and games",
            "A smart robot",
            "A magical unicorn with a shining horn"
    };

    private Bitmap hiddenPicture;
    private Bitmap maskBitmap;
    private Canvas maskCanvas;
    private Paint revealPaint, coverPaint, textPaint, borderPaint;
    private List<RectF> puzzlePieces;
    private int revealedPieces = 0;
    private int totalPieces = 10;
    private int currentImageResource;
    private String currentImageName;
    private String currentImageDescription;
    private Random random;



    public HiddenPictureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    private void initializeView() {
        random = new Random();

        // Select random image initially
        selectRandomImage();

        // Load selected image
        loadPicture();

        // Initialize paints
        revealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Paint with Clear xfermode to reveal parts of mask
        revealPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        coverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coverPaint.setColor(Color.parseColor("#BDBDBD"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(2f, 2f, 2f, Color.BLACK);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#4CAF50"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);

        puzzlePieces = new ArrayList<>();
    }

    private void selectRandomImage() {
        int idx = random.nextInt(HIDDEN_IMAGE_RESOURCES.length);
        currentImageResource = HIDDEN_IMAGE_RESOURCES[idx];
        currentImageName = HIDDEN_IMAGE_NAMES[idx];
        currentImageDescription = HIDDEN_IMAGE_DESCRIPTIONS[idx];
    }

    private void loadPicture() {
        try {
            Bitmap original = BitmapFactory.decodeResource(getResources(), currentImageResource);
            if (original != null) {
                hiddenPicture = Bitmap.createScaledBitmap(original, 400, 400, true);
                if (original != hiddenPicture) {
                    original.recycle();
                }
                return;
            }
        } catch (Exception e) {
            // ignore and create fallback
        }
        createFallbackImage();
    }

    private void createFallbackImage() {
        // Create simple fallback image programmatically (smiley face)
        hiddenPicture = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(hiddenPicture);
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#B0BEC5"));
        tempCanvas.drawRect(0, 0, 400, 400, bgPaint);

        Paint facePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        facePaint.setColor(Color.YELLOW);
        tempCanvas.drawCircle(200, 200, 150, facePaint);

        // Eyes
        facePaint.setColor(Color.BLACK);
        tempCanvas.drawCircle(150, 170, 20, facePaint);
        tempCanvas.drawCircle(250, 170, 20, facePaint);

        // Smile
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(10f);
        RectF smileRect = new RectF(120, 220, 280, 320);
        tempCanvas.drawArc(smileRect, 0, 180, false, facePaint);

        currentImageName = "🎁 Mystery Image";
        currentImageDescription = "A special surprise awaits you.";
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(w > 0 && h > 0) {
            maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            maskCanvas = new Canvas(maskBitmap);
            maskCanvas.drawColor(Color.parseColor("#BDBDBD"));

            createPuzzlePieces(w, h);

            updateMask();
        }
    }

    private void createPuzzlePieces(int width, int height) {
        puzzlePieces.clear();

        float pieceWidth = width / 5f;
        float pieceHeight = height / 2f;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                float left = col * pieceWidth;
                float top = row * pieceHeight;
                puzzlePieces.add(new RectF(left, top, left + pieceWidth, top + pieceHeight));
            }
        }
    }

    public void revealPieces(int count) {
        revealedPieces = Math.min(count, totalPieces);
        updateMask();
        invalidate();
    }

    public void revealNext() {
        if(revealedPieces < totalPieces) {
            revealedPieces++;
            updateMask();
            invalidate();
        }
    }

    public void updateMask() {
        if (maskCanvas == null) return;

        // Reset mask canvas with cover color
        maskCanvas.drawColor(Color.parseColor("#BDBDBD"));

        // Reveal puzzle pieces progressively
        for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
            maskCanvas.drawRect(puzzlePieces.get(i), revealPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawColor(Color.parseColor("#E0F2F1"));

        // Draw border around view
        canvas.drawRect(4, 4, getWidth() - 4, getHeight() - 4, borderPaint);

        if (hiddenPicture != null && maskBitmap != null) {
            // Draw scaled hidden picture
            RectF dest = new RectF(10, 10, getWidth() - 10, getHeight() - 10);
            canvas.drawBitmap(hiddenPicture, null, dest, null);

            // Draw mask overlay
            canvas.drawBitmap(maskBitmap, 0, 0, null);

            // Draw grid lines on revealed pieces
            Paint gridPaint = new Paint();
            gridPaint.setColor(Color.GREEN);
            gridPaint.setStrokeWidth(3);
            gridPaint.setAlpha(150);
            for(int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
                canvas.drawRect(puzzlePieces.get(i), gridPaint);
            }
        }

        // Draw progress text
        float centerX = getWidth() / 2f;
        Paint textP = new Paint(textPaint);
        textP.setColor(Color.DKGRAY);
        textP.setTextSize(30);
        canvas.drawText(currentImageName + ": " + revealedPieces + "/" + totalPieces, centerX, getHeight() - 50, textP);

        if(revealedPieces >= totalPieces) {
            Paint completePaint = new Paint(textPaint);
            completePaint.setColor(Color.YELLOW);
            completePaint.setTextSize(35);
            canvas.drawText("🎉 Image Complete! 🎉", centerX, 50, completePaint);

            Paint descPaint = new Paint(textPaint);
            descPaint.setColor(Color.GREEN);
            descPaint.setTextSize(25);
            canvas.drawText(currentImageDescription, centerX, getHeight() - 15, descPaint);
        }
    }

    // Public API methods

    public int getRevealedPieces() {
        return revealedPieces;
    }

    public boolean isComplete() {
        return revealedPieces >= totalPieces;
    }

    public String getCurrentName() {
        return currentImageName;
    }

    public String getCurrentDescription() {
        return currentImageDescription;
    }

    public int getCurrentImageResource() {
        return currentImageResource;
    }

    public void resetPuzzle() {
        revealedPieces = 0;
        updateMask();
        invalidate();
    }

    public void newRandom() {
        selectRandomImage();
        loadPicture();
        revealedPieces = 0;
        updateMask();
        invalidate();
    }
}