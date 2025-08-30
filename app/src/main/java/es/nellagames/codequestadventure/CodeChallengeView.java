package es.nellagames.codequestadventure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import es.nellagames.codequestadventure.Challenge;
import es.nellagames.codequestadventure.CodeBlock;
import java.util.ArrayList;
import java.util.List;

public class CodeChallengeView extends View {

    private Challenge challenge;
    private List<CodeBlock> codeBlocks;
    private CodeBlock draggedBlock;
    private Paint textPaint, backgroundPaint, dropZonePaint, borderPaint;
    private RectF dropZone;
    private String placedAnswer = "";

    private float dragOffsetX, dragOffsetY;
    private static final float BLOCK_WIDTH = 300f;
    private static final float BLOCK_HEIGHT = 80f;

    public CodeChallengeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints();
        codeBlocks = new ArrayList<>();
    }

    private void initializePaints() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);

        dropZonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dropZonePaint.setColor(Color.parseColor("#E8F5E8"));
        dropZonePaint.setStyle(Paint.Style.FILL);
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        setupCodeBlocks();
        placedAnswer = "";
        invalidate();
    }

    private void setupCodeBlocks() {
        codeBlocks.clear();

        if (challenge != null) {
            List<String> options = challenge.getCodeOptions();
            float startY = getHeight() * 0.6f;

            for (int i = 0; i < options.size(); i++) {
                CodeBlock block = new CodeBlock(options.get(i), true);
                float x = 50 + (i * (BLOCK_WIDTH + 20));
                float y = startY + (i % 2) * (BLOCK_HEIGHT + 20);

                block.setX(x);
                block.setY(y);
                block.setOriginalX(x);
                block.setOriginalY(y);
                codeBlocks.add(block);
            }

            // Setup drop zone in the middle of the screen
            float dropZoneX = getWidth() * 0.25f;
            float dropZoneY = getHeight() * 0.35f;
            dropZone = new RectF(dropZoneX, dropZoneY,
                    dropZoneX + BLOCK_WIDTH, dropZoneY + BLOCK_HEIGHT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (challenge != null) {
            setupCodeBlocks();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (challenge == null) return;

        // Draw background
        canvas.drawColor(Color.parseColor("#F8F9FA"));

        // Draw challenge title
        Paint titlePaint = new Paint(textPaint);
        titlePaint.setTextSize(32);
        titlePaint.setColor(Color.parseColor("#2E7D32"));
        canvas.drawText("Complete this code:", getWidth() / 2f, 60, titlePaint);

        // Draw incomplete code with placeholder
        String displayCode = challenge.getIncompleteCode();
        if (!placedAnswer.isEmpty()) {
            displayCode = displayCode.replace("___", placedAnswer);
        }

        Paint codePaint = new Paint(textPaint);
        codePaint.setTextSize(40);
        codePaint.setColor(Color.parseColor("#1565C0"));
        canvas.drawText(displayCode, getWidth() / 2f, 140, codePaint);

        // Draw drop zone
        canvas.drawRoundRect(dropZone, 15, 15, dropZonePaint);
        borderPaint.setColor(placedAnswer.isEmpty() ? Color.parseColor("#4CAF50") : Color.parseColor("#2196F3"));
        canvas.drawRoundRect(dropZone, 15, 15, borderPaint);

        if (placedAnswer.isEmpty()) {
            Paint hintPaint = new Paint(textPaint);
            hintPaint.setTextSize(28);
            hintPaint.setColor(Color.parseColor("#666666"));
            canvas.drawText("Drag here", dropZone.centerX(), dropZone.centerY() + 10, hintPaint);
        }

        // Draw instructions
        Paint instructPaint = new Paint(textPaint);
        instructPaint.setTextSize(24);
        instructPaint.setColor(Color.parseColor("#555555"));
        canvas.drawText("Drag the correct answer onto the blank:",
                getWidth() / 2f, getHeight() * 0.5f, instructPaint);

        // Draw code blocks
        for (CodeBlock block : codeBlocks) {
            if (!block.isPlaced() || block == draggedBlock) {
                drawCodeBlock(canvas, block);
            }
        }

        // Draw dragged block last (on top)
        if (draggedBlock != null) {
            drawCodeBlock(canvas, draggedBlock);
        }
    }

    private void drawCodeBlock(Canvas canvas, CodeBlock block) {
        RectF blockRect = new RectF(block.getX(), block.getY(),
                block.getX() + BLOCK_WIDTH,
                block.getY() + BLOCK_HEIGHT);

        // Choose colors based on block state
        if (block == draggedBlock) {
            backgroundPaint.setColor(Color.parseColor("#FFE0B2"));
            borderPaint.setColor(Color.parseColor("#FF9800"));
        } else if (block.isDraggable()) {
            backgroundPaint.setColor(Color.parseColor("#E3F2FD"));
            borderPaint.setColor(Color.parseColor("#2196F3"));
        } else {
            backgroundPaint.setColor(Color.parseColor("#F5F5F5"));
            borderPaint.setColor(Color.parseColor("#CCCCCC"));
        }

        // Draw block background
        canvas.drawRoundRect(blockRect, 15, 15, backgroundPaint);
        canvas.drawRoundRect(blockRect, 15, 15, borderPaint);

        // Draw text
        Paint blockTextPaint = new Paint(textPaint);
        blockTextPaint.setTextSize(32);
        blockTextPaint.setColor(Color.parseColor("#212121"));
        canvas.drawText(block.getText(), blockRect.centerX(),
                blockRect.centerY() + 10, blockTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                draggedBlock = findBlockAt(x, y);
                if (draggedBlock != null) {
                    dragOffsetX = x - draggedBlock.getX();
                    dragOffsetY = y - draggedBlock.getY();
                    // Reset placed answer if dragging the placed block
                    if (draggedBlock.isPlaced()) {
                        placedAnswer = "";
                        draggedBlock.setPlaced(false);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (draggedBlock != null) {
                    draggedBlock.setX(x - dragOffsetX);
                    draggedBlock.setY(y - dragOffsetY);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (draggedBlock != null) {
                    if (isInDropZone(draggedBlock)) {
                        // Place in drop zone
                        placedAnswer = draggedBlock.getText();
                        draggedBlock.setPlaced(true);
                        draggedBlock.setX(dropZone.left);
                        draggedBlock.setY(dropZone.top);
                    } else {
                        // Snap back to original position
                        draggedBlock.setX(draggedBlock.getOriginalX());
                        draggedBlock.setY(draggedBlock.getOriginalY());
                        draggedBlock.setPlaced(false);
                    }
                    draggedBlock = null;
                    invalidate();
                }
                break;
        }

        return true;
    }

    private CodeBlock findBlockAt(float x, float y) {
        for (CodeBlock block : codeBlocks) {
            if (x >= block.getX() && x <= block.getX() + BLOCK_WIDTH &&
                    y >= block.getY() && y <= block.getY() + BLOCK_HEIGHT) {
                return block;
            }
        }
        return null;
    }

    private boolean isInDropZone(CodeBlock block) {
        float blockCenterX = block.getX() + BLOCK_WIDTH / 2;
        float blockCenterY = block.getY() + BLOCK_HEIGHT / 2;

        return dropZone.contains(blockCenterX, blockCenterY);
    }

    public String getUserAnswer() {
        return placedAnswer;
    }

    public void resetChallenge() {
        placedAnswer = "";
        for (CodeBlock block : codeBlocks) {
            block.setPlaced(false);
            block.setX(block.getOriginalX());
            block.setY(block.getOriginalY());
        }
        invalidate();
    }
}
