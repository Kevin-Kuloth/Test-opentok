package com.tokbox.android.demo.learningopentok;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import pub.devrel.easypermissions.EasyPermissions;


public class ChatActivity extends Activity implements Session.SessionListener, PublisherKit.PublisherListener {

    private Session mSession;
    private Publisher mPublisher;
    public String mApiKey = "45721772";
    public String mSessionId = "1_MX40NTcyMTc3Mn5-MTQ3OTQ3NDY5NDY2OH5MOE44bXJRU2VIVTE1SGlkN0wyMHk0Ynp-fg";
    public String mToken = "T1==cGFydG5lcl9pZD00NTcyMTc3MiZzaWc9YjM1Y2EzMTlkZDdhZDViYTdlOGU1NzkyMmYxMThjZmNiZTI2M2E1MjpzZXNzaW9uX2lkPTFfTVg0ME5UY3lNVGMzTW41LU1UUTNPVFEzTkRZNU5EWTJPSDVNT0U0NGJYSlJVMlZJVlRFMVNHbGtOMHd5TUhrMFlucC1mZyZjcmVhdGVfdGltZT0xNDc5NDc0NzU5Jm5vbmNlPTAuNDYzNTMzNzQ2NDEyMjUzNTMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ4MjA2NzMzOA==";
    private static final int RC_VIDEO_APP_PERM = 124;
    RelativeLayout mPublisherViewContainer, audiocalllay;
    private ArrayList<Subscriber> mSubscribers = new ArrayList<Subscriber>();
    private HashMap<Stream, Subscriber> mSubscriberStreams = new HashMap<Stream, Subscriber>();
    private static final String TAG = "simple-multiparty " + ChatActivity.class.getSimpleName();
    ImageView endcall, audiotoggle, videotoggle, userImage, chatbutton, callerimage;
    LinearLayout  publisherviewlay, callersviewlay, callerdetaillay;
    SpinKitView spinKitView;
    TextView calltime;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);
        endcall = (ImageView) findViewById(R.id.endcall);
        audiotoggle = (ImageView) findViewById(R.id.audiotoggle);
        videotoggle = (ImageView) findViewById(R.id.videotoggle);
        audiocalllay = (RelativeLayout) findViewById(R.id.audiocalllay);
        publisherviewlay = (LinearLayout) findViewById(R.id.publisherviewlay);
        callersviewlay = (LinearLayout) findViewById(R.id.callersviewlay);
        callerdetaillay = (LinearLayout) findViewById(R.id.callerdetaillay);
        spinKitView = (SpinKitView)findViewById(R.id.spin_kit);
        calltime = (TextView) findViewById(R.id.calltime);
        userImage = (ImageView) findViewById(R.id.userImage);
        callerimage = (ImageView) findViewById(R.id.callerimage);
        chatbutton = (ImageView) findViewById(R.id.chatbutton);
        initializeSession();

        Style style = Style.values()[14];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        endcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disconnectSession();
                finish();
            }
        });

        audiotoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPublisher == null) {
                    return;
                }
                if (!mPublisher.getPublishAudio()) {
                    mPublisher.setPublishAudio(true);
                    audiotoggle.setImageDrawable(getResources().getDrawable(R.drawable.off_audio));
                } else {
                    mPublisher.setPublishAudio(false);
                    audiotoggle.setImageDrawable(getResources().getDrawable(R.drawable.mic_red));
                }

            }
        });

        videotoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                if (!mPublisher.getPublishVideo()) {
//                    audiocalllay.setVisibility(View.GONE);
//                    publisherviewlay.setVisibility(View.VISIBLE);
                    mPublisher.setPublishVideo(true);
                    animatelayout();

//                    callersviewlay.setVisibility(View.VISIBLE);
                    videotoggle.setImageDrawable(getResources().getDrawable(R.drawable.video_red));
                } else {
                    mPublisher.setPublishVideo(false);
                    videotoggle.setImageDrawable(getResources().getDrawable(R.drawable.off_recording));
//                    publisherviewlay.setVisibility(View.GONE);
//                    audiocalllay.setVisibility(View.VISIBLE);
//                    callersviewlay.setVisibility(View.GONE);
                    animatebacklayout();
                }
            }
        });

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatelayout();
            }
        });
    }



    void animatelayout(){

        userImage.animate()
                .translationY(userImage.getHeight())
                .alpha(0.0f).setDuration(1000);

        callerimage.animate()
                .translationY(-userImage.getHeight())
                .alpha(0.0f).setDuration(1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        callersviewlay.setVisibility(View.VISIBLE);
                        publisherviewlay.setVisibility(View.VISIBLE);
                        publisherviewlay.setAlpha(0.0f);
                        callersviewlay.setAlpha(0.0f);
                        audiocalllay.setVisibility(View.GONE);
                        callersviewlay.animate().alpha(1.0f).setDuration(1000);
                        publisherviewlay.animate().alpha(1.0f).setDuration(1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

        spinKitView.animate().alpha(0.0f).setDuration(1000);
        callerdetaillay.animate().alpha(0.0f).setDuration(1000);
    }

    void animatebacklayout(){

        callerdetaillay.setVisibility(View.VISIBLE);
        audiocalllay.setVisibility(View.VISIBLE);

        callerdetaillay.animate().alpha(1.0f).setDuration(1000);
        callersviewlay.animate().alpha(0.0f).setDuration(1000);
        publisherviewlay.animate().alpha(0.0f).setDuration(1000);

        userImage.animate()
                .translationY(0)
                .alpha(1.0f).setDuration(1000);

        callerimage.animate()
                .translationY(0)
                .alpha(1.0f).setDuration(1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        callersviewlay.setVisibility(View.GONE);
                        publisherviewlay.setVisibility(View.GONE);
                        userImage.setAlpha(1.0f);
                        callerimage.setAlpha(1.0f);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }

    private void initializeSession() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            mSession = new Session(this, mApiKey, mSessionId);
            mSession.setSessionListener(this);
            mSession.connect(mToken);
        }else{
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic so you can perform video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onConnected(Session session) {

        Toast.makeText(this,"Session Connected",Toast.LENGTH_LONG).show();
        mPublisher = new Publisher(ChatActivity.this, "publisher");

        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        mPublisherViewContainer.addView(mPublisher.getView());

        mSession.publish(mPublisher);
        mPublisher.setPublishVideo(false);
    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

    Toast.makeText(this,"Useradded",Toast.LENGTH_LONG).show();
        spinKitView.setVisibility(View.GONE);
        calltime.setText("00:05");

        if (mSubscribers.size() + 1 > 4) {
            Toast.makeText(this, "New subscriber ignored. MAX_NUM_SUBSCRIBERS limit reached.", Toast.LENGTH_LONG).show();
            return;
        }

        final Subscriber subscriber = new Subscriber(ChatActivity.this, stream);
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);


        int position = mSubscribers.size() - 1;
        int id = getResources().getIdentifier("callerview" + (new Integer(position)).toString(), "id", ChatActivity.this.getPackageName());
        int ids = getResources().getIdentifier("callerviews" + (new Integer(position)).toString(), "id", ChatActivity.this.getPackageName());
        RelativeLayout subscriberViewContainer = (RelativeLayout) findViewById(id);
        RelativeLayout subscriberViewContainers = (RelativeLayout) findViewById(ids);
        subscriberViewContainers.setVisibility(View.VISIBLE);


        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriberViewContainer.addView(subscriber.getView());

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Subscriber subscriber = mSubscriberStreams.get(stream);
        if (subscriber == null) {
            return;
        }

        int position = mSubscribers.indexOf(subscriber);
        int id = getResources().getIdentifier("callerview" + (new Integer(position)).toString(), "id", ChatActivity.this.getPackageName());
        int ids = getResources().getIdentifier("callerviews" + (new Integer(position)).toString(), "id", ChatActivity.this.getPackageName());

        mSubscribers.remove(subscriber);
        mSubscriberStreams.remove(stream);

        RelativeLayout subscriberViewContainer = (RelativeLayout) findViewById(id);
        RelativeLayout subscriberViewContainers = (RelativeLayout) findViewById(ids);
        subscriberViewContainers.setVisibility(View.GONE);
        subscriberViewContainer.removeView(subscriber.getView());


    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Toast.makeText(this,"Session error"+ opentokError.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

        Toast.makeText(this,"Stream Created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

        Toast.makeText(this,"Stream Destroyed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

        Toast.makeText(this,"Stream error",Toast.LENGTH_LONG).show();
    }

    private void disconnectSession() {
        if (mSession == null) {
            return;
        }

        if (mSubscribers.size() > 0) {
            for (Subscriber subscriber : mSubscribers) {
                if (subscriber != null) {
                    mSession.unsubscribe(subscriber);
                    subscriber.destroy();
                }
            }
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onPause");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        disconnectSession();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }


}
