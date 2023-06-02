package com.adayo.app.picture.ui.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.picture.MyViewModel;
import com.adayo.app.picture.R;
import com.adayo.app.picture.adapter.RecyclerListAdapter;
import com.adayo.app.picture.ui.base.BaseFragment;
import com.adayo.app.picture.ui.photoview.OnPhotoTapListener;
import com.adayo.app.picture.ui.photoview.PhotoView;
import com.adayo.app.picture.utils.PreventSecondClickUtils;
import com.adayo.app.picture.view.TimerContainer;
import com.adayo.component.log.Trace;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.app.picture.ui.base.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Map;

import static android.view.View.VISIBLE;
import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_LIST;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_NEXT;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PAUSE;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PLAY_OR_STOP;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PREV;
import static com.adayo.common.picture.constant.Constant.MSG_TYPE.MSG_UNMOUNTED;

public class PicturePlayFragment extends BaseFragment implements
        View.OnClickListener,
        TimerContainer.Callback,
        LifecycleOwner, OnPhotoTapListener {
    private static final String TAG = "Picture" + PicturePlayFragment.class.getSimpleName();
    private MyViewModel mMyViewModel;
    private TextView mName;
    private PhotoView mFullPhoto;
    private RelativeLayout mErrorRl;
    private TextView mErrorTv;
    private ImageView mListButton, mPreButton, mPlayPauseButton, mNextButton, mRotationButton, mScaleButton;
    private LinearLayout mCtrlBar;
    private TimerContainer mTimerContainerView;
    private float bitmapSize = 0;
    private int mrotate=0;

    private static int getBitmapSize(@NonNull Bitmap bitmap) {
        //@NonNull表示null不是合法值.这里的null注释很重要.通过定义@NonNull注释,您可以告诉编译器您不希望位置为空值.但是调用者有责任永远不传递空值,例如,通过显式空检查来确保空值.
        LogUtil.i(TAG, "");
        return bitmap.getAllocationByteCount();
    }

    @Override
    public void onResume() {
        LogUtil.i(TAG, " ");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtil.i(TAG, " ");
        super.onPause();
        mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PAUSE);
        mMyViewModel.setIsctrlbar(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.v(TAG, " ");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtil.v(TAG, " ");
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.i(TAG, "--------------onHiddenChanged----------" + hidden);
        super.onHiddenChanged(hidden);
        if (hidden) {
            mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PAUSE);
            mrotate=0;
        } else {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void setCtrlBarVisibility(boolean bShow) {
        LogUtil.i(TAG, "bShow = " + bShow);
        mCtrlBar.setVisibility(bShow ? VISIBLE : View.GONE);
        mName.setVisibility(bShow ? VISIBLE : View.GONE);
    }



    @Override
    public int getLayout() {
        LogUtil.i(TAG, " ");
        return R.layout.fragment_picture_play;
    }

    private <T extends View> T bindView(int sourceId) {
        LogUtil.i(TAG, " ");
        return mContentView.findViewById(sourceId);
    }

    private void isctrlbarChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getIsctrlbar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean x) {
                LogUtil.d(TAG, "Isctrlbar x = " + x);
                setCtrlBarVisibility(x);
            }
        });
    }

    private void isplayChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getIsplay().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isplay) {
                LogUtil.d(TAG, "isplay = " + isplay);
                if (mPlayPauseButton != null) {
                   mPlayPauseButton.setImageResource(isplay ? R.drawable.pause_btn_pressed : R.drawable.play_btn_pressed);
                }
            }
        });
    }

    private void displayChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getDisplay().observe(this, new Observer<Map<String, String>>() {
            @Override
            public void onChanged(@Nullable Map<String, String> display) {
                LogUtil.d(TAG,"Display START");
                mrotate=0;
                String path = display.keySet().iterator().next();
                String name = display.get(display.keySet().iterator().next());
                LogUtil.d(TAG, "path = " + path + "name = " + name);
                if (path == null || name == null) {
                    LogUtil.d(TAG, "return");
                    return;
                }
                mName.setText(name);
                if (isAdded()) {
                    LogUtil.d(TAG,"Display isAdded");
                    Bitmap imageBitmap = BitmapFactory.decodeFile(path);
                    LogUtil.d(TAG,"Display imageBitmap decodeFile");
                    if (imageBitmap != null) {
                        LogUtil.d(TAG,"Display imageBitmap != null");
                        bitmapSize = getBitmapSize(imageBitmap) / 1024 / 1024;
                        LogUtil.d(TAG,"Display bitmapSize ="+bitmapSize);
                    }else {
                        mFullPhoto.setVisibility(View.GONE);
                        mErrorTv.setText(R.string.picture_error_file);
                        mErrorRl.setVisibility(VISIBLE);
                        LogUtil.w(TAG,"imageBitmap ="+imageBitmap);
                        return;
                    }
                    LogUtil.d(TAG,"IF bitmapSize ");
                    if (bitmapSize < 8) {
                        LogUtil.d(TAG, "bitmapSize < 8");
                        Glide.with(getActivity())
                                .load(path)
//                    .transform(new CircleTransform(getActivity())).override(400, 400)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                                        mErrorTv.setText(R.string.picture_error_file);
                                        mErrorRl.setVisibility(VISIBLE);
                                        mFullPhoto.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                                        mErrorRl.setVisibility(View.GONE);
                                        mFullPhoto.setVisibility(VISIBLE);
                                        return false;
                                    }
                                })
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//原图
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(mFullPhoto);
                    } else {
                        Glide.with(getActivity())
                                .load(path)
//                    .transform(new CircleTransform(getActivity())).override(400, 400)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                                        mErrorTv.setText(R.string.picture_error_file);
                                        mErrorRl.setVisibility(VISIBLE);
                                        mFullPhoto.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                                        mErrorRl.setVisibility(View.GONE);
                                        mFullPhoto.setVisibility(VISIBLE);
                                        return false;
                                    }
                                })
                                 .override(imageBitmap.getWidth()*1920/imageBitmap.getHeight(), 720)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(mFullPhoto);
                    }
                }
            }
        });
    }

    @Override
    public void initView() {
        LogUtil.i(TAG, " ");
        mMyViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        mName = (TextView) bindView(R.id.play_name_tv);
        mFullPhoto = (PhotoView) bindView(R.id.full_screen_photo);
        mErrorRl = bindView(R.id.error_rl);
        mErrorTv = bindView(R.id.error_tv);
        mCtrlBar = (LinearLayout) bindView(R.id.control_bar);
        mListButton = (ImageView) bindView(R.id.photo_play_list);
        mPreButton = (ImageView) bindView(R.id.photo_play_pre);
        mPlayPauseButton = (ImageView) bindView(R.id.photo_play_pause);
        mNextButton = (ImageView) bindView(R.id.photo_play_next);
        mRotationButton = (ImageView) bindView(R.id.photo_play_rotation);
        mTimerContainerView = (TimerContainer) bindView(R.id.view_container);
        mMyViewModel.initPlayPresenter();
        mTimerContainerView.setCallback(this);
        mFullPhoto.setZoomable(true);
        mFullPhoto.setMinimumScale(0.5f);
        mFullPhoto.setOnClickListener(this);
        mTimerContainerView.setOnClickListener(this);
        mListButton.setOnClickListener(this);
        mPreButton.setOnClickListener(new PreventSecondClickUtils() {
            @Override
            public void onMultiClick(View v) {
                LogUtil.d("Picture", " ");
                mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PREV);
                mTimerContainerView.resetTimer();
            }
        });
        mPlayPauseButton.setOnClickListener(this);
        mNextButton.setOnClickListener(new PreventSecondClickUtils() {
            @Override
            public void onMultiClick(View v) {
                LogUtil.d("Picture", " ");
                mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_NEXT);

                mTimerContainerView.resetTimer();
            }
        });
        mRotationButton.setOnClickListener(this);
        isctrlbarChanged();
        isplayChanged();
        displayChanged();
        mMyViewModel.getTag().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.d("Picture", "integer = " + integer);
                switch (integer) {
                    case FRAGMENT_PICTURE_PLAY:
                        mTimerContainerView.resetTimer();
                        break;
                    default:
                        break;
                }

            }
        });

    }

    @Override
    public void initData() {
        LogUtil.v(TAG, " ");
    }



    @Override
    public void onClick(View view) {
        LogUtil.i(TAG, "");
        mTimerContainerView.resetTimer();
        switch (view.getId()) {
            case R.id.photo_play_list:
                Trace.i(TAG, "photo_play_list");
                mrotate=0;
                mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PAUSE);
                mMyViewModel.setTag(FRAGMENT_PICTURE_LIST);
                mTimerContainerView.resetTimer();
                break;
            case R.id.photo_play_pause:
                LogUtil.d(TAG,"photo_play_pause");
                mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PLAY_OR_STOP);
                mTimerContainerView.resetTimer();
                break;
            case R.id.photo_play_rotation:
                if(mrotate<360){
                    mrotate=mrotate+90;
                mFullPhoto.setRotationBy(mrotate);
                }else {
                    mrotate=0;
                    mrotate=mrotate+90;
                    mFullPhoto.setRotationBy(mrotate);
                }
                mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PAUSE);
                mTimerContainerView.resetTimer();
                break;
            case R.id.full_screen_photo:
            case R.id.view_container:
                mMyViewModel.setIsctrlbar(mCtrlBar.getVisibility() != VISIBLE);
                mTimerContainerView.resetTimer();
                break;
            default:
                LogUtil.d(TAG, "onClick: default");
                break;
        }
    }


    @Override
    public void onViewTouched(MotionEvent event) {
        LogUtil.i(TAG, "");
        if (event.getPointerCount() <= 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    mTimerContainerView.resetTimer();
                    break;
                case MotionEvent.ACTION_DOWN:
                    mTimerContainerView.resetTimer();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                default:
                    LogUtil.d(TAG, "onViewTouched: " + event.getPointerCount());
                    break;
            }
        } else {
            mMyViewModel.setIsctrlbar(true);
            switch (event.getAction() & event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_UP:
                    mTimerContainerView.resetTimer();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mTimerContainerView.resetTimer();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTimerContainerView.resetTimer();
                    break;
                default:
                    LogUtil.d(TAG, "onViewTouched: " + event.getPointerCount());
                    break;
            }
        }
    }

    @Override
    public void onTimeOver() {
        Trace.i(TAG, "onTimeOver ");
        if (mMyViewModel.getTag().getValue() == FRAGMENT_PICTURE_PLAY && mMyViewModel.getType().getValue() != MSG_UNMOUNTED) {
            mMyViewModel.setIsctrlbar(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mrotate=0;
        mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PAUSE);

    }

    @Override
    public void onSingleTapUp() {
        LogUtil.v(TAG, "");
    }

    @Override
    public void onGestureFlingLeft() {
        LogUtil.i(TAG, "");
//        mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_PREV);
//        mTimerContainerView.resetTimer();
    }

    @Override
    public void onGestureFlingRight() {
        LogUtil.i(TAG, "");
//        mMyViewModel.playControl(FRAGMENT_PICTURE_PLAY_NEXT);
//        mTimerContainerView.resetTimer();
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {

    }
}
