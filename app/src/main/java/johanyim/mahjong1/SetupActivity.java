package johanyim.mahjong1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;


public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        setupMinScore(0, minScore,12);
        setupMaxScore(0, maxScore,12);
        setupStartButton();
    }


    int[] chipArray = {0,1,2,4,8,16,24,32,48,64,96,128,192}; //fun game

//    int[] chipArray = {0,1,2,4,8,16,32,64,128,256,512,1024,2048}; //gambling

//    int[] chipArray = {1,3,9,27,81,243,729,2187,6561,19683,59049};


    //default values

    int minScore = 0;
    int maxScore = 12;
    Double chipValue = 1.0;
    int startingAmount = 1000;



    //exiting the app
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
                        SetupActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void setupMinScore(int lowest, int initial, int maximum){
        SeekBar minScoreSb = (SeekBar) findViewById(R.id.minScoreSlider);
        minScoreSb.setProgress(initial);
        minScoreSb.setMax(maximum);
        final TextView minScoreValueText = (TextView) findViewById(R.id.minScoreCounter);
        minScoreValueText.setText(convertToChineseNumber(initial) + " " + getResources().getString( R.string.fan_measure));
        minScoreSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minScoreValueText.setText(convertToChineseNumber(progress) + " " + getResources().getString( R.string.fan_measure));
                minScore = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupMaxScore(int lowest, int initial, int maximum){
        SeekBar maxScoreSb = (SeekBar) findViewById(R.id.maxScoreSlider);
        maxScoreSb.setProgress(initial);
        maxScoreSb.setMax(maximum);
        final TextView maxScoreValueText = (TextView) findViewById(R.id.maxScoreCounter);
        maxScoreValueText.setText(convertToChineseNumber(initial) + " " + getResources().getString( R.string.fan_measure));
        maxScoreSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxScoreValueText.setText(convertToChineseNumber(progress) + " " + getResources().getString( R.string.fan_measure));
                maxScore = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private int getStartingAmountInput(){

        EditText startingAmountBox = (EditText) findViewById(R.id.startingAmountInputBox);

        int startingAmountInt;
        try{
            String startingAmountStr = startingAmountBox.getText().toString();
            startingAmountInt = Integer.parseInt(startingAmountStr);
        }catch(NumberFormatException e){//default value if blank or an error occurred
            startingAmountInt = 1000;
        }


        return startingAmountInt;
    }

    private double getChipValueInput(){

        EditText chipValueBox = (EditText) findViewById(R.id.chipValueInputBox);

        double chipValueDouble;
        try{
            String chipValueStr = chipValueBox.getText().toString();
            chipValueDouble = Double.parseDouble(chipValueStr);
        }catch(NumberFormatException e){//default value if blank or an error occurred
            chipValueDouble = 1.0;
        }

        return chipValueDouble;
    }







    private void outputMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void setupStartButton() {
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(minScore > maxScore){
                    outputMessage("The minimum score should not be greater than the limit.");
                    //catching if the maximum score is less than the minimum

                }else{
                    chipValue = getChipValueInput();
                    startingAmount = getStartingAmountInput();

                    //making the session in SetupActivity
                    session game = new session(chipArray, minScore, maxScore, chipValue, startingAmount);

                    //sending the session to MainActivity
                    Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                    intent.putExtra("GAMEOBJECT", game);
                    startActivity(intent);
                    finish(); //ends SetupActivity to not use up too much memory

                    //Catch negative value of chips
                    // it seems that android studio doesn't allow negative values anyway. So I don't need to worry about that possibility

                }
            }
        });
    }

    public String convertToChineseNumber(int englishNumber){

        String strEnglishNum = Integer.toString(englishNumber);
        int numLength = strEnglishNum.length();
        String finalChineseNumber = "";
        int exponent;
//        TODO Language pref
//        String[] chineseChar =     {"0","1","2","3","4","5","6","7","8","9" };
//        String[] chinesePlaceVal = {"" ,"1","","","","","","","",""};

        String[] chineseChar =     {"零","一","二","三","四", "五" , "六" , "七" ,"八","九" };
        String[] chinesePlaceVal = {""  ,"十","百","千","万","十万","百万","千万","亿","十亿"};


        //---------------------------------------------------------For english and chinese---------------------------------------------------------//

//        if(Locale.getDefault().getLanguage().equals("zh")){
//            for(int placeValue = 0; placeValue < numLength; placeValue++) {
//                exponent = (numLength-placeValue-1);
//                String nextChineseNumeral = chineseChar[englishNumber%( (int)Math.pow(10,(exponent+1)) )/(int)Math.pow(10,exponent)];
//
//                if(englishNumber != 0 && nextChineseNumeral.equals(chineseChar[0])){ //if digit at placevalue is zero
//                    //do nothing
//                }else if( exponent == 1 && nextChineseNumeral.equals(chineseChar[1]) ){ //place value at 10s column is one
//                    finalChineseNumber = finalChineseNumber.concat(chinesePlaceVal[exponent]);
//                }else {
//                    finalChineseNumber = finalChineseNumber.concat(nextChineseNumeral);
//                    finalChineseNumber = finalChineseNumber.concat(chinesePlaceVal[exponent]);
//                }
//            }
//            return finalChineseNumber;
//        }else{
//            return Integer.toString(englishNumber);
//        }

        //-----------------------------------------------------------For always chinese-------------------------------------------------------------//

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