package com.example.demouser.scarne_s_dice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        Intent intent = getIntent();
        //gives reference t o the intent we create before, so we can get
        // this is how we pass data while starting new intent
        String score = intent.getStringExtra(MainActivity.USER_SCORE);


        ((TextView)findViewById(R.id.scoreText)).setText(String.format(" You scored %s", score));
        ((Button)findViewById(R.id.playAgain)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
