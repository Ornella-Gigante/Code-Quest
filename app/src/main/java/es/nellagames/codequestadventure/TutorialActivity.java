package es.nellagames.codequestadventure;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private Button nextButton, prevButton;
    private String[] tutorialPages = {
            "Welcome to CodeQuestAdventure! Learn to code step by step.",
            "Drag and drop the correct code blocks to complete the programs.",
            "Use hints if you get stuck, but try to solve on your own first!",
            "Complete challenges to reveal hidden pictures and advance levels.",
            "Good luck and have fun coding!"
    };
    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialText = findViewById(R.id.tutorialText);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        updateTutorialPage();

        nextButton.setOnClickListener(v -> {
            if (currentPageIndex < tutorialPages.length - 1) {
                currentPageIndex++;
                updateTutorialPage();
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                currentPageIndex--;
                updateTutorialPage();
            }
        });
    }

    private void updateTutorialPage() {
        tutorialText.setText(tutorialPages[currentPageIndex]);
        prevButton.setVisibility(currentPageIndex == 0 ? View.GONE : View.VISIBLE);
        nextButton.setText(currentPageIndex == tutorialPages.length - 1 ? "Finish" : "Next");
        nextButton.setOnClickListener(v -> {
            if (currentPageIndex == tutorialPages.length - 1) {
                finish(); // close tutorial on finish
            } else {
                currentPageIndex++;
                updateTutorialPage();
            }
        });
    }
}
