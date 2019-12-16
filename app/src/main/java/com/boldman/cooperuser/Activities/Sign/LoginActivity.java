package com.boldman.cooperuser.Activities.Sign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;
import com.google.firebase.iid.FirebaseInstanceId;

/*
*   LoginActivity
*   Created by Boldman. 2019.05.09
* */

public class LoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout layoutDots;
    private ImageView imgCloudBlue,imgCloudOrange;
    private TextView[] dots;
    private int[] layouts;

    Button btnLogin, btnSignUp, btnSocialLogin;
    TextView btnSkip,tvTermsCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*  ??*/
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_login);

        defineIds();
        setOnClickListeners();

        setTermsCondition();

        //set animation to clouds
        displayAnimation(75,imgCloudOrange);
        displayAnimationSlow(-170,imgCloudBlue);

        // layout variable of all sliders
        layouts = new int[]{
                R.layout.login_slide1,
                R.layout.login_slide2,
                R.layout.login_slide3
        };

        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

        addBottomDots(0);
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        SharedHelper.putKey(this, "fcm_token", FirebaseInstanceId.getInstance().getToken());
    }

    /*  define ids*/
    private void defineIds(){

        viewPager = findViewById(R.id.view_pager);
        layoutDots = findViewById(R.id.layoutDots);
        btnLogin =findViewById(R.id.sign_in_btn);
        btnSkip =  findViewById(R.id.skip);
        btnSignUp = findViewById(R.id.sign_up_btn);
        btnSocialLogin =  findViewById(R.id.social_login_button);
        tvTermsCondition = findViewById(R.id.term_conditions_textview);
        imgCloudOrange = findViewById(R.id.welcome_cloud_orange_image);
        imgCloudBlue = findViewById(R.id.welcome_loud_blue_image);
    }

    /*  setOnClickListeners */
    private void setOnClickListeners() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class)
                        .putExtra("signup", true).putExtra("viewpager", "yes")
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });

        btnSocialLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SocialLoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });

    }

    /*  display animation with xValue and image*/
    public void displayAnimation(int toXValue,ImageView imageView){

        // new TranslateAnimation(xFrom, xTo, yFrom, yTo)
        TranslateAnimation animation = new TranslateAnimation(0, toXValue, 0, 0);
        animation.setDuration(1200);                    // animation duration
        animation.setRepeatCount(Animation.INFINITE);   // animation repeat count
        animation.setRepeatMode(2);                     // repeat animation (left to right, right to left )
        imageView.startAnimation(animation);
    }

    /*  display slow animation with xValue and image*/
    public void displayAnimationSlow(int toXValue,ImageView imageView){

        // new TranslateAnimation(xFrom, xTo, yFrom, yTo)
        TranslateAnimation animation = new TranslateAnimation(0, toXValue, 0, 0);
        animation.setDuration(2000);                    // animation duration
        animation.setRepeatCount(Animation.INFINITE);   // animation repeat count
        animation.setRepeatMode(2);                     // repeat animation (left to right, right to left )
        imageView.startAnimation(animation);
    }

    /*  add bottom dots*/
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            dots[currentPage].setTextColor(colorsActive[currentPage]);
            dots[currentPage].startAnimation(animation);
        }

    }

    /*  Making notification bar transparent*/
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /*  bottom text*/
    public void setTermsCondition(){

        //Terms & Condition
        SpannableString termsSpannableString = new SpannableString("By signing up, you agree to CoOper's Terms and Conditions and Privacy Policy.");

        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

        };
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        termsSpannableString.setSpan(termsSpan, 37, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsSpannableString.setSpan(privacySpan,62,76,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTermsCondition.setText(termsSpannableString);
        tvTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
        tvTermsCondition.setHighlightColor(Color.TRANSPARENT);
    }

    /*  Viewpager change listener */
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageScrollStateChanged(int arg0) { }
    };

    /*  Adapter for showing more than 2 pages. */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
