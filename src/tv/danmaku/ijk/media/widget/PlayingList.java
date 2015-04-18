package tv.danmaku.ijk.media.widget;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.widget.ListView;

public class PlayingList extends ListView {
	private AudioManager mAM;

	public PlayingList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	private Boolean init(Context context) {
		mAM = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return true;
	}

}
