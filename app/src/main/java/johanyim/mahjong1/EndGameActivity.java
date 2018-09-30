package johanyim.mahjong1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class EndGameActivity extends AppCompatActivity {

    session game;

    HashMap<Integer, String> textMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Intent intent = getIntent();
        game = (session) intent.getSerializableExtra("GAMEOBJECT");

        //Using a HashMap to setup all the static text in the end game screen is much more efficient. This will be called in the method: setupStaticText()

        textMap.put(R.id.end_maxScoreText,          getResources().getString(R.string.end_max_score_text) + game.getMaxScore());
        textMap.put(R.id.end_minScoreText,          getResources().getString(R.string.end_min_score_text) + game.getMinScore());
        textMap.put(R.id.end_startingAmountText,    getResources().getString(R.string.end_starting_amount_text) + game.getStartingAmount());
        textMap.put(R.id.end_chipValueText,         getResources().getString(R.string.end_chip_value_text) + game.getChipValue());
        textMap.put(R.id.end_gamesPlayedText,       getResources().getString(R.string.end_games_played_text) + game.getGamesPlayed());
        setupAll();


    }

    @Override
    public void onBackPressed() {
        exitGamePopup("Are you sure you want to exit?");
    }
    public void exitGamePopup(String message){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EndGameActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void newGameWithPopup(String message){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(EndGameActivity.this, SetupActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    public void setupAll(){
        setupNewGameButton();
        setupPlayerEndStats();
//        configureMinScoreText();
//        configureMaxScoreText();
//        configureStartingAmountText();
//        configureChipValueText();
        setupStaticText();
    }

    private void setupNewGameButton(){
        Button setupButton = (Button) findViewById(R.id.newGameButton);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGameWithPopup(getResources().getString( R.string.end_new_game_message));
            }
        });
    }

    private void setupStaticText(){
        TextView text;
        for( int key : textMap.keySet() ){
            text = (TextView) findViewById(key);
            text.setText(textMap.get(key));
        }
    }//setup for all the plain text (no interactive buttons/behaviours, just shows text)

    private void setupPlayerEndStats(){
        TextView[] playerStatsText = {
                (TextView) findViewById(R.id.playerDownEndScoreText),
                (TextView) findViewById(R.id.playerRightEndScoreText),
                (TextView) findViewById(R.id.playerUpEndScoreText),
                (TextView) findViewById(R.id.playerLeftEndScoreText)

        };
        String sign;
        for(int i = 0; i < 4; i++){
            if(game.getPlayerChips(i) > game.getStartingAmount()){
                playerStatsText[i].setTextColor(getResources().getColor(R.color.colorEndTextWinning));
                sign = "+$";
            }else if(game.getPlayerChips(i) == game.getStartingAmount()){
                playerStatsText[i].setTextColor(getResources().getColor(R.color.colorEndTextNeutral));
                sign = "±$";
            }else{
                playerStatsText[i].setTextColor(getResources().getColor(R.color.colorEndTextLosing));
                sign = "-$";
            }
            playerStatsText[i].setText(
                    "Player " + (i+1) + " (" + intToDirection(i) + ")\n" +

                    sign + Math.abs(doubleTo2dp(game.getPlayerXWinningChips(i)*game.getChipValue()))

            );
        }
    }


    private String intToDirection(int position){
        if(position == 0){return getResources().getString( R.string.down);}

        else if(position == 1) {return getResources().getString( R.string.right);}

        else if(position == 2){return getResources().getString( R.string.up);}

        else if(position == 3){return getResources().getString( R.string.left);}

        else{return "N/A";}

    }

    private double doubleTo2dp(double number){
        return Math.round(number*100)/100;
    } //multiplying type: double with another double makes very long floating point numbers, this converts the number to 2dp




}
