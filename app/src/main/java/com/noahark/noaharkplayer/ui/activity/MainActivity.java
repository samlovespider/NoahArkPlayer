package com.noahark.noaharkplayer.ui.activity;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.noahark.noaharkplayer.R;
import com.noahark.noaharkplayer.adapter.MusicListAdapter;
import com.noahark.noaharkplayer.base.ui.BaseActivity;
import com.noahark.noaharkplayer.model.MusicModel;
import com.noahark.noaharkplayer.util.ImageLoadTask;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {


    @ViewById(R.id.lvMusics)
    ListView lvMusics;
    @ViewById(R.id.linController)
    LinearLayout linController;
    @ViewById(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @ViewById(R.id.tvTotalTime)
    TextView tvTotalTime;
    @ViewById(R.id.sbTime)
    SeekBar sbTime;

    private static final int CODE_FOR_WRITE_PERMISSION = 1;

    private List<MusicModel> mMusicList;
    private ImageLoadTask mImageLoadTask;

    @Override
    public void initView() {
        gotoActivity(MusicActivity_.class);
//        mMusicList = getMusics();
//        if (mMusicList != null) {
//            mImageLoadTask = new ImageLoadTask(this, mMusicList);
//            mImageLoadTask.execute(mMusicList.toArray());
//            mImageLoadTask.setLoadTaskListener(new LoadTaskListener() {
//                @Override
//                public void loadTask(List<MusicModel> musicModels) {
//                    MusicListAdapter musicListAdapter = new MusicListAdapter(MainActivity.this, mMusicList);
//                    lvMusics.setAdapter(musicListAdapter);
//                }
//            });
//        }
//        setList();
    }

    private void setList() {
        mMusicList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MusicModel model = new MusicModel("1" + i, "2" + i, "3" + i, "4" + i, "5" + i, "6" + i, "7" + i, "8" + i);
            mMusicList.add(model);
        }
        MusicListAdapter musicListAdapter = new MusicListAdapter(this, mMusicList);
        lvMusics.setAdapter(musicListAdapter);
    }

}
