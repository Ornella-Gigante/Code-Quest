package es.nellagames.codequestadventure;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private ImageView tutorialImage;
    private Button nextButton, prevButton;

    private SoundManager soundManager;

    private String[] pagesText = {
            "Welcome to CodeQuest Adventure!\nLet's learn to program step-by-step.",
            "Drag and drop code blocks to complete the programs.\nHave fun solving!",
            "Use hints wisely if stuck.\nTry to solve it by thinking first!",
            "Complete challenges to reveal awesome hidden pictures!",
            "Good luck!\nReady? Let's start coding!"
    };

    private int[] pagesBgColors = {
            Color.parseColor("#FFC107"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FF5722"),
    };

    private int[] pagesImages = {
            R.drawable.tutorial1_fun,
            R.drawable.tutorial2_fun,
            R.drawable.tutorial3_fun,
            R.drawable.tutorial4_fun,
            R.drawable.tutorial5_fun,
    };

    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialText = findViewById(R.id.tutorialText);
        tutorialImage = findViewById(R.id.tutorialImage);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        // Initialize sound manager safely
        try {
            soundManager = new SoundManager(this);
        } catch (Exception e) {
            // If sound manager fails to initialize, continue without it
            soundManager = null;
        }

        updatePage();

        nextButton.setOnClickListener(v -> {
            if (currentPage < pagesText.length - 1) {
                currentPage++;
                updatePage();
            } else {
                finish();
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });
    }

    private void updatePage() {
        findViewById(android.R.id.content).setBackgroundColor(pagesBgColors[currentPage]);
        tutorialText.setText(pagesText[currentPage]);
        tutorialText.setTextColor(Color.WHITE);
        tutorialText.setTextSize(26f);
        tutorialText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
        tutorialText.setShadowLayer(8f, 4f, 4f, Color.BLACK);
        tutorialImage.setImageResource(pagesImages[currentPage]);
        prevButton.setVisibility(currentPage == 0 ? View.GONE : View.VISIBLE);
        nextButton.setText(currentPage == pagesText.length - 1 ? "Finish" : "Next");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only try to resume music if sound manager exists and music service is available
        if (soundManager != null && soundManager.isSoundEnabled()) {
            try {
                soundManager.resumeBackgroundMusic();
            } catch (Exception e) {
                // Ignore music errors to prevent crashes
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Only try to pause music if sound manager exists
        if (soundManager != null) {
            try {
                soundManager.pauseBackgroundMusic();
            } catch (Exception e) {
                // Ignore music errors to prevent crashes
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Safely release sound manager
        if (soundManager != null) {
            try {
                soundManager.release();
            } catch (Exception e) {
                // Ignore release errors
            } finally {
                soundManager = null;
            }
        }
    }
}