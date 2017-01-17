package com.example.demouser.scarne_s_dice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int turnTotal = 0 ;
    private int playerTotal = 0;
    private int computerTotal = 0;
    private Random random = new Random();
    static enum Players {
        PLAYER,
        COMPUTER,
    }
    private Players whosTurn = Players.PLAYER;
    TextView playerScoreView;
    TextView computerScoreView;
    TextView turnTotalView;
    ImageView diceView;
    Button resetButton;
    Button holdButton;
    Button rollButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    String mEmail;
    public static final String USER_SCORE = "com.example.demouser.scarne_s_dice.USER_SCORE";// package name.USESCORE

    private int computerTurns = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize firebase

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser==null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        else {
            mEmail = mFirebaseUser.getEmail();
        }
        setContentView(R.layout.activity_main);

        holdButton =((Button) findViewById(R.id.hold));
        holdButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                hold();
            }
        });

        resetButton = ((Button) findViewById(R.id.reset));
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                reset(view);
            }
        });

        rollButton = ((Button) findViewById(R.id.rollButton));
        rollButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                roll();
            }
        });

        playerScoreView = (TextView)findViewById(R.id.player1Score);
        computerScoreView = (TextView)findViewById(R.id.computerScore);
        diceView = (ImageView) findViewById(R.id.diceView);
        turnTotalView = (TextView)findViewById(R.id.turnScore);

        /*
         * @TODO Take strings to strings.xml
         */
        computerScoreView.setText(String.format("Computer score: %d", computerTotal));
        turnTotalView.setText(String.format("Turn total: %d", turnTotal));
        playerScoreView.setText(String.format("Player 1 score: %d", playerTotal));

    }

    /**
     * Roll the dice, show the result
     */
    public void roll() {
        //random is exclusive
        int dice = random.nextInt(6) + 1;
        //based on that, take decision
        switch (dice) {
            case 1:
                diceView.setImageResource(R.drawable.dice1);
                break;
            case 2:
                diceView.setImageResource(R.drawable.dice2);
                break;
            case 3:
                diceView.setImageResource(R.drawable.dice3);
                break;
            case 4:
                diceView.setImageResource(R.drawable.dice4);
                break;
            case 5:
                diceView.setImageResource(R.drawable.dice5);
                break;
            case 6:
                diceView.setImageResource(R.drawable.dice6);
                break;
            default:
                //do nothing
                break;
        }
        if (dice == 1) {
            // now it's a new turn
            // don't bother checking if the player could have won because he player would have stopped
            // if the user had already won
            turnTotal = 0;
            changePlayers();

            updateViews();
        }
        else {
            //add the score to the turn total, update turnTotal
            turnTotal+=dice;
            updateViews();
            // show the result
            gameWon();
            if(whosTurn==Players.COMPUTER){
                computerTurns++;
            }
        }
    }

    /**
     */
    public void hold() {
        /*
        * @TODO
        * Add the turn score to the player's scorE
        * Reset turn score to be zero
        * change
        */
        switch (whosTurn) {
            case PLAYER:
                playerScores(turnTotal);
                //change players
                break;
            case COMPUTER:
                computerTurns=0;
                computerScores(turnTotal);
                break;
            default:
                break;

        }


        // change players now
        changePlayers();
        updateViews();

        //don't see if there is a winner because we already checked in rollDice
        // that always gets called before this
        /*
        if (!gameWon()){


        }
        */
    }

    final Handler timerHandler = new Handler();

    public void computerTurnIn1500() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                computerTurn();
                if(whosTurn==Players.COMPUTER) {
                    computerTurnIn1500();
                }
            }
        }, 1500);
    }

    public void computerTurn() {
        System.out.println(String.format("computer turn %d, turn total: %d",computerTurns, turnTotal));
        if (computerTurns > 3 || !(shouldRollAgain() )) {
            System.out.println(computerTurns);
            hold();
        }
        else {
            roll();
            // computerTurns++;
        }
//        // roll the dice
//        int times = 1;
//        //if(computerTimes > )
//        while (shouldRollAgain(times) ) {
//            times++;
//            //roll the dice
//            int rolled = rollDice();
//            // if it's one, change players, return
//            // changeplayers -->  done in the rollDice
//            if (rolled == 1) {
//                return;
//            }
//            //else keep rolling
//            computerTurnIn3500();
//        }
//        //if shouldn't hold again, call hold
//        //need view to hold //redundant
//        computerScores(turnTotal);
//        //see i fthere is a winner
//        if (gameWon()){
//            //make some change
//            turnTotalView.setText("game won");
//        }
//        else {
//            // change players now
//            changePlayers();
//
//            // update turntotal
//            turnTotal = 0;
//            updateViews();
//
//        }
    }

    /**
     * True if should roll again
     * @return
     */
    private boolean shouldRollAgain() {
        if (computerTurns*3.5 > turnTotal) {
            return false;
        }
        return true;
    }

    public void reset(View view) {
        //reset scores
        resetScores();
        // accordingly update the views
        updateViews();
        diceView.setImageResource(R.drawable.dice5);
    }



    private void changePlayers() {
        whosTurn = (whosTurn == Players.COMPUTER) ? Players.PLAYER : Players.COMPUTER;

        turnTotal = 0;
        // if new player is computer, call computerTurn
        if (whosTurn == Players.COMPUTER) {
            computerTurns = 0;
            computerTurnIn1500();
        }
    }


    private void playerScores(int turnTotal) {
        //add the score to the player's score
        playerTotal+=turnTotal;
        //turnTotal can now be 0
        this.turnTotal = 0;
//        //update the score and turntotal
//        playerScoreView.setText(String.format("Player 1 score: %d", playerTotal));
//        turnTotalView.setText(String.format("Turn total: %d", turnTotal));
    }

    private void computerScores(int turnTotal) {
        computerTotal+=turnTotal;
        this.turnTotal = 0;
        computerTurns = 0;
//        //update the score and turntotal
//        computerScoreView.setText(String.format("Computer score: %d", computerTotal));
//        turnTotalView.setText(String.format("Turn total: %d", turnTotal));

    }

    private void resetScores(){
        turnTotal = 0 ;
        playerTotal = 0;
        computerTotal = 0;
        whosTurn = Players.PLAYER;
    }

    private void updateViews() {
        /*
         * @TODO take string to strings.xml
         */
        computerScoreView.setText(String.format("Computer score: %d", computerTotal));
        turnTotalView.setText(String.format("Turn total: %d", turnTotal));
        playerScoreView.setText(String.format("Player 1 score: %d", playerTotal));
        if (whosTurn == Players.COMPUTER) {
            rollButton.setEnabled(false);
            holdButton.setEnabled(false);
            computerScoreView.setBackgroundColor(Color.GREEN);
            playerScoreView.setBackgroundColor(Color.WHITE);
        }
        else {
            rollButton.setEnabled(true);
            holdButton.setEnabled(true);

            computerScoreView.setBackgroundColor(Color.WHITE);
            playerScoreView.setBackgroundColor(Color.GREEN);
        }
    }

    private boolean gameWon() {
        updateViews();
        // check both scores, only one of them can be the winner
        if ((whosTurn==Players.COMPUTER) && (computerTotal + turnTotal) >= 50){
            //disable things
            //void computerWins and playerWins
            // inside playerWins,
            //before you start the intent,
            Intent intent = new Intent(this, LoseActivity.class);
            intent.putExtra(USER_SCORE, String.valueOf(playerTotal));// --> in player wins
            startActivity(intent);
            resetScores();
            updateViews();
            /*
            computerScoreView.setText(String.format("Computer score: %d", computerTotal));
            turnTotalView.setText(String.format("Turn total: %d", turnTotal));
            playerScoreView.setText(String.format("Player 1 score: %d", playerTotal));
            holdButton.setEnabled(false);
            rollButton.setEnabled(false);*/
            return true;
        }
        else if((whosTurn==Players.PLAYER) && (playerTotal + turnTotal >= 50))  {
            Intent intent = new Intent(this, WinActivity.class);
            intent.putExtra(USER_SCORE, String.valueOf(playerTotal +  turnTotal));// --> in player wins
            startActivity(intent);
            resetScores();
            updateViews();
            return true;
        }
        return false;
    }

}
