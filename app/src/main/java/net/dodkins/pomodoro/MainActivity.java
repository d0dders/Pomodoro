package net.dodkins.pomodoro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.dodkins.pomodoro.model.Attempt;
import net.dodkins.pomodoro.model.AttemptKind;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mContainer;
    private Button mRestartButton;
    private Button mResumeButton;
    private Button mPauseButton;
    private TextView title;
    private EditText message;
    private TextView mTimerText;

    private boolean playing = false;

    private Attempt mCurrentAttempt;
    private CountDownTimer2 mTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.backgroundLayout);
        mRestartButton = findViewById(R.id.restartButton);
        mResumeButton = findViewById(R.id.resumeButton);
        title = findViewById(R.id.titleTextView);
        message = findViewById(R.id.taskEditText);
        mTimerText = findViewById(R.id.timeTextView);

        setTimerText(0);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareAttempt(AttemptKind.FOCUS);
            }
        });

        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (playing == true){
                pauseTimer();
            }
            else {
                if (mCurrentAttempt == null) {
                    prepareAttempt(AttemptKind.FOCUS);
                }
                playTimer();
            }
            }
        });

    }




    public void setTimerText (String timerText){
            mTimerText.setText(timerText);
        }

    public void setTimerText ( int remainingSeconds){
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            setTimerText(String.format("%02d:%02d", minutes, seconds));
        }

        private void prepareAttempt (AttemptKind kind){
            reset();
            mCurrentAttempt = new Attempt(kind, "");
            addAttemptStyle(kind);
            title.setText(kind.getDisplayName());
            setTimerText(mCurrentAttempt.getRemainingSeconds());
            mTimeline = new CountDownTimer2(kind.getTotalSeconds() * 1000,1000) {
                @Override
                public void onTick(long l) {
                    mCurrentAttempt.tick();
                    setTimerText(mCurrentAttempt.getRemainingSeconds());


                }

                @Override
                public void onFinish() {
                    saveCurrentAttempt();
                    //TODO: Add applause audioclip
                    prepareAttempt(mCurrentAttempt.getKind() == AttemptKind.FOCUS ?
                            AttemptKind.BREAK : AttemptKind.FOCUS);
                }
            };
        }

        private void saveCurrentAttempt () {
            mCurrentAttempt.setMessage(message.getText().toString());
            mCurrentAttempt.save();
        }

        private void reset () {
            playing = false;
            mResumeButton.setText("Resume");
            if (mTimeline != null) {
                mTimeline.cancel();
            }
        }

        public void playTimer () {
            if (mCurrentAttempt.getRemainingSeconds()< mCurrentAttempt.getKind().getTotalSeconds()){
                mTimeline.resume();
            }
            else{
                mTimeline.start();
            }
            playing = true;
            mResumeButton.setText("Pause");
        }

       public void pauseTimer () {
            playing = false;
            mTimeline.pause();
            mResumeButton.setText("Resume");
        }

        private void addAttemptStyle (AttemptKind kind){
            switch(kind.toString().toLowerCase())
            {
                case "focus" :
                    mContainer.setBackgroundColor(Color.parseColor("#637a91"));
                    break;

                case "break" :
                    mContainer.setBackgroundColor(Color.parseColor("#39add1"));
                    break;

                // We can have any number of case statements
                // below is default statement,used when none of the cases is true.
                // No break is needed in the default case.
                default :
                    // Statements
            }

        }

        /*private void clearAttemptStyles () {
            for (AttemptKind kind : AttemptKind.values()) {
                container.getStyleClass().remove(kind.toString().toLowerCase());
                container.getStyleClass().remove("playing");
            }
        }*/



    }
