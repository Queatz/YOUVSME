package youvsme.com.youvsme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.services.StateService;
import youvsme.com.youvsme.services.UserService;

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
