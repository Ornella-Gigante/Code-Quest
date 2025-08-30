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
import es.nellagames.codequestadventure.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HiddenPictureView extends View {

    // Hidden image resources array
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

    // English names for the images
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

    // Descriptions for each image
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

    public HiddenPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    private void initializeView() {
        random = new Random();

        // Select a random image
        selectRandomImage();

        // Load the selected image
        loadHiddenPicture();

        // Initialize paints
        revealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        revealPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        coverPaint = new Paint();
        coverPaint.setColor(Color.parseColor("#BDBDBD"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(2, 2, 2, Color.BLACK);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#4CAF50"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);

        puzzlePieces = new ArrayList<>();
    }

    private void selectRandomImage() {
        int index = random.nextInt(HIDDEN_IMAGE_RESOURCES.length);
        currentImageResource = HIDDEN_IMAGE_RESOURCES[index];
        currentImageName = HIDDEN_IMAGE_NAMES[index];
        currentImageDescription = HIDDEN_IMAGE_DESCRIPTIONS[index];
    }

    private void loadHiddenPicture() {
        try {
            // Load the drawable image
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), currentImageResource);

            if (originalBitmap != null) {
                // Scale to desired size (400x400)
                hiddenPicture = Bitmap.createScaledBitmap(originalBitmap, 400, 400, true);

                // Recycle original if different
                if (originalBitmap != hiddenPicture) {
                    originalBitmap.recycle();
                }
            } else {
                // Fallback: create programmatic image if drawable fails
                createFallbackPicture();
            }
        } catch (Exception e) {
            // Error loading drawable, create fallback
            createFallbackPicture();
        }
    }

    private void createFallbackPicture() {
        // Create 400x400 pixels bitmap as fallback
        hiddenPicture = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(hiddenPicture);

        // Gradient background
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#E8F5E8"));
        tempCanvas.drawRect(0, 0, 400, 400, backgroundPaint);

        // Draw simple fallback image (smiley face)
        Paint fallbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Face
        fallbackPaint.setColor(Color.parseColor("#FFD700"));
        tempCanvas.drawCircle(200, 200, 150, fallbackPaint);

        // Eyes
        fallbackPaint.setColor(Color.BLACK);
        tempCanvas.drawCircle(160, 160, 20, fallbackPaint);
        tempCanvas.drawCircle(240, 160, 20, fallbackPaint);

        // Smile
        fallbackPaint.setStyle(Paint.Style.STROKE);
        fallbackPaint.setStrokeWidth(8f);
        RectF smileRect = new RectF(140, 180, 260, 250);
        tempCanvas.drawArc(smileRect, 0, 180, false, fallbackPaint);

        currentImageName = "🎁 Mystery Image";
        currentImageDescription = "A special surprise image";
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Create mask bitmap
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        maskCanvas.drawColor(Color.parseColor("#BDBDBD")); // Initially covered

        createPuzzlePieces();
        updateMask();
    }

    private void createPuzzlePieces() {
        puzzlePieces.clear();

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // Create 10 puzzle pieces (2 rows x 5 columns)
        float pieceWidth = getWidth() / 5f;
        float pieceHeight = getHeight() / 2f;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                float left = col * pieceWidth;
                float top = row * pieceHeight;
                float right = left + pieceWidth;
                float bottom = top + pieceHeight;

                RectF piece = new RectF(left, top, right, bottom);
                puzzlePieces.add(piece);
            }
        }
    }

    public void revealPieces(int numPieces) {
        revealedPieces = Math.min(numPieces, totalPieces);
        updateMask();
        invalidate();
    }

    public void revealNextPiece() {
        if (revealedPieces < totalPieces) {
            revealedPieces++;
            updateMask();
            invalidate();
        }
    }

    public void newRandomImage() {
        selectRandomImage();
        loadHiddenPicture();
        revealedPieces = 0;
        updateMask();
        invalidate();
    }

    private void updateMask() {
        if (maskCanvas == null || puzzlePieces.isEmpty()) return;

        // Clear mask and redraw covered state
        maskCanvas.drawColor(Color.parseColor("#BDBDBD"));

        // Reveal pieces based on progress
        for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
            RectF piece = puzzlePieces.get(i);
            maskCanvas.drawRect(piece, revealPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawColor(Color.parseColor("#E8EAF6"));

        // Draw border
        canvas.drawRect(5, 5, getWidth() - 5, getHeight() - 5, borderPaint);

        if (hiddenPicture != null && maskBitmap != null) {
            // Scale and draw the hidden picture
            RectF destRect = new RectF(10, 10, getWidth() - 10, getHeight() - 10);
            canvas.drawBitmap(hiddenPicture, null, destRect, null);

            // Draw the mask on top
            canvas.drawBitmap(maskBitmap, 0, 0, null);

            // Draw puzzle grid lines for revealed pieces
            Paint gridPaint = new Paint();
            gridPaint.setColor(Color.parseColor("#4CAF50"));
            gridPaint.setStrokeWidth(3f);
            gridPaint.setAlpha(150);

            for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
                RectF piece = puzzlePieces.get(i);
                canvas.drawRect(piece, gridPaint);
            }
        }

        // Draw progress text with image name
        String progressText = currentImageName + ": " + revealedPieces + "/" + totalPieces;
        canvas.drawText(progressText, getWidth() / 2f, getHeight() - 50, textPaint);

        // Draw completion message
        if (revealedPieces >= totalPieces) {
            Paint completePaint = new Paint(textPaint);
            completePaint.setTextSize(32);
            completePaint.setColor(Color.parseColor("#FFD700"));
            canvas.drawText("IMAGE COMPLETE! 🎉", getWidth() / 2f, 50, completePaint);

            // Draw description
            Paint descPaint = new Paint(textPaint);
            descPaint.setTextSize(20);
            descPaint.setColor(Color.parseColor("#4CAF50"));
            canvas.drawText(currentImageDescription, getWidth() / 2f, getHeight() - 20, descPaint);
        }
    }

    // Public methods for external access
    public int getRevealedPieces() {
        return revealedPieces;
    }

    public boolean isComplete() {
        return revealedPieces >= totalPieces;
    }

    public String getCurrentImageName() {
        return currentImageName;
    }

    public String getCurrentImageDescription() {
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

    // Static helper methods
    public static int getRandomHiddenImage() {
        Random rand = new Random();
        return HIDDEN_IMAGE_RESOURCES[rand.nextInt(HIDDEN_IMAGE_RESOURCES.length)];
    }

    public static String getImageName(int resourceId) {
        for (int i = 0; i < HIDDEN_IMAGE_RESOURCES.length; i++) {
            if (HIDDEN_IMAGE_RESOURCES[i] == resourceId) {
                return HIDDEN_IMAGE_NAMES[i];
            }
        }
        return "🎁 Mystery Image";
    }

    public static String getImageDescription(int resourceId) {
        for (int i = 0; i < HIDDEN_IMAGE_RESOURCES.length; i++) {
            if (HIDDEN_IMAGE_RESOURCES[i] == resourceId) {
                return HIDDEN_IMAGE_DESCRIPTIONS[i];
            }
        }
        return "A special image waiting to be discovered";
    }

    public void newRandom() {
    }

    public String getCurrentDescription() {
    }

    public String getCurrentName() {
    }
}
