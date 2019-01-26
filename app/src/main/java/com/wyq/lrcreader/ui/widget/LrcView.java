package com.wyq.lrcreader.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author Uni.W
 * @date 2019/1/25 22:42
 */
public class LrcView extends ScrollView {

    public LrcView(Context context) {
        super(context);
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface LrcViewController {
        void ontextSizeChange(int textSize);

        void TextTypeChange(int textType);

        void shareImage();

        void shareText();

        void shareVideo();

    }

    public interface LrcPlayerController {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();

        /**
         * Get the audio session id for the player used by this VideoView. This can be used to
         * apply audio effects to the audio track of a video.
         *
         * @return The audio session, or 0 if there was an error.
         */
        int getAudioSessionId();
    }


}
