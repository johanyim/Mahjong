package johanyim.mahjong1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;


public class MainActivity extends AppCompatActivity {



    ImageView[] diceOnScreen = new ImageView[3];//an array of the IDs for each dice on screen
    int[] dicePicArray = new int[6];//an array of each dice face as pictures



    ImageView dicePot; //the ImageView of the direction arrow seen on screen

    TextView[] playerScoreText = new TextView[4];

    Button[] playerButtons = new Button[4];


    session game;

    int diceNum = 3; //how many dice are to be rolled before each game (currently is always 3)

    //only this method can access the ids of the instances on screen, I use this to define each variable which was previously declared with no value
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----------------------------------------Android Permissions---------------------------------------------//

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//For no timeout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//For fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//https://www.youtube.com/watch?v=tlIqaVJIYBM full screen from this video

        setContentView(R.layout.activity_main);

        //defining the instances

        Intent intent = getIntent();
        game = (session) intent.getSerializableExtra("GAMEOBJECT"); //searches for an intent (object) with the same name tag (GAMEOBJ) that was sent to the system

        //----------Start Game things-----------//

        updateAll();
        setupAll();
        roll();
        hidePlayerButtons();

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
                        MainActivity.this.finish();
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
                        startActivity(new Intent(MainActivity.this, SetupActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    //---------------------------------------------------Methods/Behaviour----------------------------------------------------------------//

    public void endGameWithPopup(String message){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, EndGameActivity.class);
                        intent.putExtra("GAMEOBJECT", game);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void roll(){
        TextView diceTotalText = findViewById(R.id.diceTotalNumber);
        int diceTotal = dice.multiRoll(diceNum, diceOnScreen, dicePicArray);//rolls three dice when the button is pressed
        diceTotalText.setText(Integer.toString(diceTotal));
        dice.diceDirection(game.getEastPos(), diceTotal, dicePot);


        vibrate(50);


    }

    public void showPlayerButtons(){
        for(Button button : playerButtons){
            button.setVisibility(View.VISIBLE);
        }
    }

    public void hidePlayerButtons(){
        for(Button button : playerButtons){
            button.setVisibility(View.INVISIBLE);
        }
    }

    public void showWinDrawButtons(){
        findViewById(R.id.winButton).setVisibility(View.VISIBLE);
        findViewById(R.id.drawButton).setVisibility(View.VISIBLE);
    }

    public void hideWinDrawButtons(){
        findViewById(R.id.winButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.drawButton).setVisibility(View.INVISIBLE);
    }

    //--------------------------------------------------------Update = Called once at beginning or at anytime desired-------------------------------------------------------//

    private void updateAll(){
        updatePlayerButtons();
        updatePlayerScores();
        updateGamesPlayed();
        updatePrevailingWinds();
        updateEastPosition();
        updateWindMarker();
        updateChineseGamesPlayed();
    }//These are called after each game or at any time. Also they are called once at the beginning.


    private void updatePlayerButtons(){

        playerButtons[0] = (Button) findViewById(R.id.playerDownButton);
        playerButtons[1] = (Button) findViewById(R.id.playerRightButton);
        playerButtons[2] = (Button) findViewById(R.id.playerUpButton);
        playerButtons[3] = (Button) findViewById(R.id.playerLeftButton);

        final Button[] finalPlayerButtons = playerButtons; //the array must be declared final to be used


        for(int i = 0; i < playerButtons.length; i++){

            finalPlayerButtons[i].setText(getResources().getString( R.string.win_button_text));
            finalPlayerButtons[i].setTextColor(getResources().getColor(R.color.colorWinButtonText)); //https://stackoverflow.com/questions/29815538/android-set-color-programatically-from-xml-color-constants
            finalPlayerButtons[i].setBackgroundColor(getResources().getColor(R.color.colorWinButtonBackground));

            final int finalI = i;

            playerButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    game.setWinner(finalI);//setting winner
                    vibrate(100);

                    //https://stackoverflow.com/questions/31891481/using-a-loop-to-set-the-buttons-onclicklistener
                    //clever!

                    //change the remaining ones to different buttons for losing. Might not need to set visibility to gone
                    //otherPlayers.setText("Loser");


                    //iterate through the players
                    for(int j = 0; j < finalPlayerButtons.length; j++) {
                        final int finalJ = j;



                        //if the current player button belongs to the winner
                        if (finalJ == finalI) {
                            finalPlayerButtons[finalJ].setText(getResources().getString( R.string.zimo_button_text));
                            finalPlayerButtons[finalJ].setTextColor(getResources().getColor(R.color.colorZimoButtonText)); //https://stackoverflow.com/questions/29815538/android-set-color-programatically-from-xml-color-constants
                            finalPlayerButtons[finalJ].setBackgroundColor(getResources().getColor(R.color.colorZimoButtonBackground));
                        }
                        //else if the current player button belongs to a loser
                        else {
                            finalPlayerButtons[finalJ].setText(getResources().getString( R.string.loser_button_text));
                            finalPlayerButtons[finalJ].setTextColor(getResources().getColor(R.color.colorLoseButtonText)); //https://stackoverflow.com/questions/29815538/android-set-color-programatically-from-xml-color-constants
                            finalPlayerButtons[finalJ].setBackgroundColor(getResources().getColor(R.color.colorLoseButtonBackground));
                        }

                        //all buttons now gain a new onclick to set loser
                        playerButtons[j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                game.setLoser(finalJ);
                                vibrate(100);
                                //show score screen

                                //allow player to choose their score



                                //from https://developer.android.com/guide/topics/ui/dialogs


                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(
                                        getResources().getString( R.string.score_input_screen_title)
                                ).setMessage(
                                        getResources().getString( R.string.score_input_screen_message)
                                ).setCancelable(false);

                                final EditText scoreInput = new EditText(MainActivity.this);
                                scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                scoreInput.setLayoutParams(lp);
                                builder.setView(scoreInput);

                                builder.setPositiveButton(R.string.score_popup_positive, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        try{
                                            vibrate(100);
                                            String strValue = scoreInput.getText().toString();
                                            int intValue = Integer.parseInt(strValue);
                                            game.setFan(intValue);

                                            game.winPayFan(game.getFan(), game.getWinner(), game.getLoser());

                                            if(game.getWinner() != game.getLoser()){
                                                //normal transaction
                                                outputMessage(getResources().getString(R.string.game_number_popup_text)+game.getGamesPlayed()+":\n"+
                                                        intToDirection(game.getWinner()) + " +" + 2*game.fanToChip(game.getFan()) + "\n" +
                                                        intToDirection(game.getLoser()) +" -"+ 2*game.fanToChip(game.getFan()));
                                            }else{
                                                //ZiMo
                                                String finalMessage = "";
                                                finalMessage = finalMessage.concat(getResources().getString(R.string.game_number_popup_text)+game.getGamesPlayed()+":\n");

                                                for(int i = 0; i < 4; i++) {
                                                    if(game.getWinner() == i){
                                                        finalMessage = finalMessage.concat(intToDirection(i) + " +" + 3*game.fanToChip(game.getFan()));
                                                    }else{
                                                        finalMessage = finalMessage.concat(intToDirection(i) + " -" + game.fanToChip(game.getFan()));
                                                    }
                                                    if(i < 3){
                                                        finalMessage = finalMessage.concat("\n");
                                                    }

                                                }
                                                outputMessage(finalMessage);
                                            }
                                            updatePlayerButtons();
                                            hidePlayerButtons();
                                            showWinDrawButtons();

                                            updateAll();
                                        }catch(NumberFormatException e){
                                            outputMessage("You must enter a value");
                                            updatePlayerButtons();
                                            hidePlayerButtons();
                                            showWinDrawButtons();
                                        }

                                    }
                                })
                                        .setNegativeButton(R.string.score_popup_negative, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User clicked cancel button
                                                updatePlayerButtons();
                                                hidePlayerButtons();
                                                showWinDrawButtons();
                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        });
                    }
                }
            });
        }
    } //CAUTION: Really big

    private void updatePlayerScores(){

        playerScoreText[0] = (TextView) findViewById(R.id.playerDownText);
        playerScoreText[1] = (TextView) findViewById(R.id.playerRightText);
        playerScoreText[2] = (TextView) findViewById(R.id.playerUpText);
        playerScoreText[3] = (TextView) findViewById(R.id.playerLeftText);

        for(int i = 0; i < playerScoreText.length; i ++){

            String sign;

            if(game.getPlayerChips(i) > game.getStartingAmount()){
                playerScoreText[i].setTextColor(getResources().getColor(R.color.colorMoneyTextWinning));
                sign = "+";
            }else if(game.getPlayerChips(i) == game.getStartingAmount()){
                playerScoreText[i].setTextColor(getResources().getColor(R.color.colorMoneyTextNeutral));
                sign = "±";
            }else{
                playerScoreText[i].setTextColor(getResources().getColor(R.color.colorMoneyTextLosing));
                sign = "-";
            }

            playerScoreText[i].setText(String.valueOf(game.getPlayerXChips(i)) +
                    " (" + sign +
                    String.valueOf(
                            Math.abs(game.getPlayerXWinningChips(i)))
                    + ")");//1016 (+16)

        }

//        playerWinButton[0] = (Button) findViewById(R.id.playerLeftButton);
//        playerWinButton[1] = (Button) findViewById(R.id.playerRightButton);
//        playerWinButton[2] = (Button) findViewById(R.id.playerUpButton);
//        playerWinButton[3] = (Button) findViewById(R.id.playerLeftButton); //This might not work



    }

    private void updateChineseGamesPlayed(){
        TextView chineseGamesPlayedText = findViewById(R.id.chineseGameNumberText);
        String chineseGameNumber;
        if(game.getGamesPlayed()%4 == 0){
            chineseGameNumber = "一";
        }else if(game.getGamesPlayed()%4 == 1){
            chineseGameNumber = "二";
        }else if(game.getGamesPlayed()%4 == 2){
            chineseGameNumber = "三";
        }else if(game.getGamesPlayed()%4 == 3){
            chineseGameNumber = "四";
        }else{
            chineseGameNumber = "N/A";
        }

        chineseGamesPlayedText.setText(
                getResources().getString( R.string.chinese_game)+ ": " +
                intToWindDirection(game.getPrevailingWinds()) +
                getResources().getString( R.string.chinese_game_count_number) +
                        convertToChineseNumber(game.getChineseGameNumber()) +
                        getResources().getString( R.string.chinese_game_count_game)
        );


    }


    private void updateGamesPlayed(){//TODO: undo this
        TextView gamesPlayedText = findViewById(R.id.gameNumberText);
        gamesPlayedText.setText(getResources().getString( R.string.game_count) + ": " + convertToChineseNumber(game.getGamesPlayed()) + "局");
//        gamesPlayedText.setText(getResources().getString( R.string.game_count) + ": " + game.getGamesPlayed());
    }

    private void updatePrevailingWinds(){
        TextView prevailingWindsText = findViewById(R.id.prevailingWindsText);
        prevailingWindsText.setText(getResources().getString( R.string.prevailing_winds_count) + ": " + intToWindDirection(game.getPrevailingWinds()));
    }

    private void updateEastPosition(){
        TextView eastPositionText = findViewById(R.id.eastPositionText);
        eastPositionText.setText(getResources().getString( R.string.east_position) + ": " + intToDirection(game.getEastPos()));

    }

    private void updateWindMarker(){
        int[] windMarkerImageId = { //This array is of type integer, but we're using the ids to reference the ImageViews
                R.drawable.windmarker_d,
                R.drawable.windmarker_n,
                R.drawable.windmarker_x,
                R.drawable.windmarker_b,
        };
        ImageView windMarker = (ImageView) findViewById(R.id.windMarker);
        windMarker.setImageResource(windMarkerImageId[ game.getPrevailingWinds() ]);
//        if(game.getPrevailingWinds()==0){windMarker.setImageResource(R.drawable.windmarker_d);}
//        else if(game.getPrevailingWinds()==1){windMarker.setImageResource(R.drawable.windmarker_n);}
//        else if(game.getPrevailingWinds()==2){windMarker.setImageResource(R.drawable.windmarker_x);}
//        else if(game.getPrevailingWinds()==3){windMarker.setImageResource(R.drawable.windmarker_b);}
//        else{windMarker.setImageResource(R.drawable.windmarker_d);}

        windMarker.setRotation(((float) game.getEastPos())*-90);



    }

    //-----------------------------------------------------------Setup = Called once only (used for initialization)----------------------------------------------------//


    public void setupAll(){
        setupDice();
        setupNewGameButton();
        setupWinButton();
        setupDrawButton();
        setupRollButton();
        setupEndGameButton();
    }//These are only called once in the beginning


    public void setupDice(){
        diceOnScreen[0] = findViewById(R.id.diceA);
        diceOnScreen[1] = findViewById(R.id.diceB);
        diceOnScreen[2] = findViewById(R.id.diceC);

        dicePicArray[0] = R.drawable.dice1;
        dicePicArray[1] = R.drawable.dice2;
        dicePicArray[2] = R.drawable.dice3;
        dicePicArray[3] = R.drawable.dice4;
        dicePicArray[4] = R.drawable.dice5;
        dicePicArray[5] = R.drawable.dice6;

        dicePot = findViewById(R.id.dicePot);
    }

    public void setupRollButton() {
        final Button rollButton = (Button) findViewById(R.id.diceRollButton);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();

            }
        });
    }

    public void setupDrawButton() {
        final Button drawButton = (Button) findViewById(R.id.drawButton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.draw();
                vibrate(100);
                updateAll();
                hidePlayerButtons();
                showWinDrawButtons();
                outputMessage(getResources().getString( R.string.game_number_popup_text)+game.getGamesPlayed()+":\n"+getResources().getString(R.string.game_draw_popup_text));

            }
        });
    }

    public void setupWinButton() {
        final Button winButton = (Button) findViewById(R.id.winButton);
        winButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate(100);
                showPlayerButtons();
                hideWinDrawButtons();
                updateAll();
            }
        });
    }

    private void setupNewGameButton(){
        Button setupButton = (Button) findViewById(R.id.settingsButton);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGameWithPopup(getResources().getString( R.string.back_to_setup_message));
            }
        });
    }


    public void setupEndGameButton(){
        final Button endButton = (Button) findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameWithPopup(getResources().getString( R.string.endgame_message));

            }
        });

    }



    //-----------------------------------------------------------Extra Android functions-------------------------------------------------------//

    private void outputMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void vibrate(int duration){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

    private String intToDirection(int position){
        if(position == 0){return getResources().getString( R.string.down);}

        else if(position == 1) {return getResources().getString( R.string.right);}

        else if(position == 2){return getResources().getString( R.string.up);}

        else if(position == 3){return getResources().getString( R.string.left);}

        else{return "N/A";}

    }

    private String intToWindDirection(int directionInt){

        if(directionInt == 0){return getResources().getString( R.string.east);}

        else if(directionInt == 1) {return getResources().getString( R.string.south);}

        else if(directionInt == 2){return getResources().getString( R.string.west);}

        else if(directionInt == 3){return getResources().getString( R.string.north);}

        else{return "N/A";}

    }

    private String convertToChineseNumber(int englishNumber){

        String strEnglishNum = Integer.toString(englishNumber);
        int numLength = strEnglishNum.length();
        String finalChineseNumber = "";
        int exponent;
        String[] chineseChar =     {"零","一","二","三","四", "五" , "六" , "七" ,"八","九" };
        String[] chinesePlaceVal = {""  ,"十","百","千","万","十万","百万","千万","亿","十亿"};

        System.out.print(englishNumber + " = ");
        for(int placeValue = 0; placeValue < numLength; placeValue++) {
            exponent = (numLength-placeValue-1);
            String nextChineseNumeral = chineseChar[englishNumber%( (int)Math.pow(10,(exponent+1)) )/(int)Math.pow(10,exponent)];

            if(englishNumber != 0 && nextChineseNumeral.equals(chineseChar[0])){ //if digit at placevalue is zero
                //do nothing
            }else if( exponent == 1 && nextChineseNumeral.equals(chineseChar[1]) ){ //place value at 10s column is one
                finalChineseNumber = finalChineseNumber.concat(chinesePlaceVal[exponent]);
            }else {
                finalChineseNumber = finalChineseNumber.concat(nextChineseNumeral);
                finalChineseNumber = finalChineseNumber.concat(chinesePlaceVal[exponent]);
            }
        }
        return finalChineseNumber;
    }



}

