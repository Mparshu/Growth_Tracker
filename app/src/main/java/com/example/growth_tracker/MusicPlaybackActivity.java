package com.example.growth_tracker;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MusicPlaybackActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private int currentMusicIndex = -1;
    private SeekBar progressBar1, progressBar2, progressBar3, progressBar4;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);

        // Initialize SeekBars for all music tracks
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);

        // Set listeners for SeekBar changes
        setSeekBarListeners(progressBar1);
        setSeekBarListeners(progressBar2);
        setSeekBarListeners(progressBar3);
        setSeekBarListeners(progressBar4);

        // Initialize click listeners for all music buttons
        setupClickListeners();
    }

    private void setSeekBarListeners(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });
    }

    private void setupClickListeners() {
        ImageButton playButton1 = findViewById(R.id.playButton1);
        ImageButton playButton2 = findViewById(R.id.playButton2);
        ImageButton playButton3 = findViewById(R.id.playButton3);
        ImageButton playButton4 = findViewById(R.id.playButton4);

        playButton1.setOnClickListener(v -> togglePlayPause(0, playButton1));
        playButton2.setOnClickListener(v -> togglePlayPause(1, playButton2));
        playButton3.setOnClickListener(v -> togglePlayPause(2, playButton3));
        playButton4.setOnClickListener(v -> togglePlayPause(3, playButton4));
    }

    private void togglePlayPause(int musicIndex, ImageButton button) {
        if (currentMusicIndex == musicIndex && mediaPlayer != null && mediaPlayer.isPlaying()) {
            // If same music is playing, pause it
            mediaPlayer.pause();
            button.setImageResource(android.R.drawable.ic_media_play);
            progressHandler.removeCallbacks(progressRunnable);  // Stop progress updates
        } else {
            // Stop current playback if any
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            setupMediaPlayer(musicIndex);
            mediaPlayer.start();

            // Start progress updates
            updateProgress();

            // Reset all buttons to play icon
            resetAllPlayButtons();

            // Set clicked button to pause icon
            button.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void setupMediaPlayer(int musicIndex) {
        int musicResource;
        switch (musicIndex) {
            case 0:
                musicResource = R.raw.music1;
                break;
            case 1:
                musicResource = R.raw.music2;
                break;
            case 2:
                musicResource = R.raw.music3;
                break;
            case 3:
                musicResource = R.raw.music4;
                break;
            default:
                musicResource = R.raw.music1;
        }
        mediaPlayer = MediaPlayer.create(this, musicResource);
        currentMusicIndex = musicIndex;
    }

    private void updateProgress() {
        if (mediaPlayer != null) {
            SeekBar currentProgressBar = getSeekBarForCurrentTrack();
            if (currentProgressBar != null) {
                currentProgressBar.setMax(mediaPlayer.getDuration());
                progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            currentProgressBar.setProgress(currentPosition);
                            progressHandler.postDelayed(this, 1000);  // Update every second
                        }
                    }
                };
                progressHandler.post(progressRunnable);
            }
        }
    }

    private SeekBar getSeekBarForCurrentTrack() {
        switch (currentMusicIndex) {
            case 0:
                return progressBar1;
            case 1:
                return progressBar2;
            case 2:
                return progressBar3;
            case 3:
                return progressBar4;
            default:
                return null;
        }
    }

    private void resetAllPlayButtons() {
        ((ImageButton)findViewById(R.id.playButton1)).setImageResource(android.R.drawable.ic_media_play);
        ((ImageButton)findViewById(R.id.playButton2)).setImageResource(android.R.drawable.ic_media_play);
        ((ImageButton)findViewById(R.id.playButton3)).setImageResource(android.R.drawable.ic_media_play);
        ((ImageButton)findViewById(R.id.playButton4)).setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            progressHandler.removeCallbacks(progressRunnable);  // Stop progress updates
        }
    }
}
