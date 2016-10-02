package youvsme.com.youvsme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.WiggleDreamAnimation;
import youvsme.com.youvsme.services.GameService;
import youvsme.com.youvsme.services.UserService;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.util.Helpers;

public class Entrance extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StateService.use().setActivity(this);

        // Do this so you don't see the game icon when the keyboard opens
        getWindow().setBackgroundDrawableResource(R.color.placeholder);

        if (getIntent() != null) {
            String game = getIntent().getStringExtra("game");

            if (game != null) {
                StateService.use().openGameId(game);
            }
        }

        GameService.use().refreshGamesList();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            addStar(ev.getRawX(), ev.getRawY());
            addStar(ev.getRawX(), ev.getRawY());
            addStar(ev.getRawX(), ev.getRawY());
        }

        return super.dispatchTouchEvent(ev);
    }

    private void addStar(float x, float y) {
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.sex);

        int s = Helpers.px(this, 10);
        float scale = .5f + (float) Math.random() * .5f;

        final ImageView star = new ImageView(this);
        star.setX(x - s);
        star.setY(y - s);
        star.setScaleX(scale);
        star.setScaleY(scale);
        star.setBackgroundResource(R.drawable.star);
        star.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        star.setLayoutParams(new ViewGroup.LayoutParams(s, s));

        viewGroup.addView(star, 0);
        star.requestLayout();

        Animation animation = new WiggleDreamAnimation(star);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(star);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        star.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        if (!StateService.use().back()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entrance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserService.use().onActivityResult(requestCode, resultCode, data);
    }
}
