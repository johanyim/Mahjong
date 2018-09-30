package johanyim.mahjong1;

import java.io.Serializable; //object must be serializable (can be passed from one activity to another)
@SuppressWarnings("serial")


public class session implements Serializable {

    //-------------------------------------Starting Attributes (Default values)-----------------------------//

    //----------Pre-game-------------//

    private int[] chipArray; //array of integers, the index is the fan, the value is the actual amount of chips owed
    private int minScore = 0;
    private int maxScore = 12;
    private double chipValue = 1.0; //value for each chip
    private int startingAmount = 1000; //default starting amount for each player, can be changed by the user.
    private player[] p;


    //----------Mid-game-------------//

    private int gamesPlayed = 0; //game number counter
    private int prevailingWinds = 0;
    private int eastPos = 0;


    //----------Other----------//

    private int chineseGameNumber = 1;


    //--------------------------Getters and Setters----------------------------//

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getPrevailingWinds() {
        return prevailingWinds;
    }

    public int getEastPos() {
        return eastPos;
    }



    public int getPlayerXChips(int x){
        return p[x].getChips();
    } //returns the number of chips in possession of a specified player, x

    public int getPlayerXWinningChips(int x){
        return getPlayerXChips(x) - getStartingAmount();
    } //returns the specified player's chip's difference from the starting amount

    public void setChineseGameNumber(int chineseGameNumber) {
        this.chineseGameNumber = chineseGameNumber;
    }

    public int getMinScore(){
        return minScore;
    }

    public int getMaxScore(){
        return maxScore;
    }

    public int getStartingAmount() {
        return startingAmount;
    }

    public double getChipValue(){
        return chipValue;
    }

    //-------------------Calculating winning amounts------------------------//

    private int winnerPos;
    public int getWinner() {
        return winnerPos;
    }
    public void setWinner(int winner){
        winnerPos = winner;
    }

    private int loserPos;
    public int getLoser() {
        return loserPos;
    }
    public void setLoser(int loser){
        loserPos = loser;
    }

    private int fan = 0;
    public int getFan() {
        return fan;
    }
    public void setFan(int fan) {
        this.fan = fan;
    }


    //------------------------------------Constructor---------------------------------------------//

    public session(int[] chipArray, int minScore, int maxScore, double chipValue, int startingAmount) {
        this.chipArray = chipArray;
        this.minScore = minScore;
        this.maxScore = maxScore;

        configureChipArray();
        this.chipValue = chipValue;
        this.startingAmount = startingAmount;
        this.p = createPlayers(4);
    }


    //---------------------------------------Methods-----------------------------------------//

    private player[] createPlayers(int amount){ //a function to return an array of players
        player[] playerList = new player[amount]; //initialize an array of players
        for(int i = 0 ; i < amount ; i++){
            playerList[i] = new player(startingAmount); //each player starts with the starting amount defined earlier
        }
        return playerList;
    }

    public int getPlayerChips(int playerNum){
        return p[playerNum].getChips();
    }

    private void nextPosition(){
        this.eastPos = (this.eastPos + 1) % 4; //modulo operator to ensure that the east position is within the four directions

        if(eastPos == 0){ //if the east position resets (e.g. eastPos reaches 4), the prevailing winds will move to the next position.
            this.prevailingWinds = (this.prevailingWinds + 1) % 4; //same modulo operator for the prevailing winds
            resetChineseGameNumber();//only reset the chinese game number after the game has made 1 full cycle
        }
    }



    private void configureChipArray(){
        for(int i = 0 ; i < this.chipArray.length ; i++){
            if(i < minScore){
                this.chipArray[i] = 0;
            }else if(i > maxScore){
                this.chipArray[i] = chipArray[maxScore];
            }
        }
    }

//    public void winPay(int amount, int fromPlayer, int toPlayer ){
//
//        if(fromPlayer != toPlayer){
//            this.p[fromPlayer].subtractChips(amount*2);
//            this.p[toPlayer].addChips(amount*2);        //the amount given to the winning player is doubled if won by discard (as stated in the rules)
//
//        }else{                                          //when the parameter for toPlayer is the same as fromPlayer, it denotes that the winning player obtained ZiMo
//            for(int i = 0 ; i < p.length ; i++){
//                this.p[i].subtractChips(amount);
//                this.p[toPlayer].addChips(amount);
//            }
//        }
//
//        if(toPlayer != eastPos){
//            this.nextPosition();
//        }
//    }// does not convert fan to amount automatically

//
//    public void winPay(int amount, int fromPlayer, int toPlayer){
//        if(fromPlayer == toPlayer){ //if the winner and loser are the same, then the program knows it was a ZiMo
//            for(int i = 0 ; i < p.length ; i++){    //looping through all the players
//                this.p[i].subtractChips(amount);    //subtracts the amount from every player including the winner
//                this.p[toPlayer].addChips(amount);  //adds 4 times the amount to the winner, regaining the amount subtracted earlier
//            }
//        }else{
//            this.p[fromPlayer].subtractChips(amount*2);
//            this.p[toPlayer].addChips(amount*2);        //the amount given to the winning player is doubled if won by discard (as stated in the rules)
//        }
//    }

    public void winPayFan(int fan, int winningPlayer, int losingPlayer){

        int amount = fanToChip(fan);

        if(losingPlayer != winningPlayer){

            //-------------Normal win------------//
            this.p[losingPlayer].subtractChips(amount*2);
            this.p[winningPlayer].addChips(amount*2);
            //-----------End Normal win----------//

        }else{ //when the parameter for winningPlayer is the same as losing Player, it denotes that the winning player obtained ZiMo

            //--------------ZiMo----------------//
            for(int i = 0 ; i < p.length ; i++){
                this.p[i].subtractChips(amount);
                this.p[winningPlayer].addChips(amount);
            }
            //-------------End ZiMo--------------//
        }

        nextChineseGameNumber();//chinese game number should go up before the next position is called, same with draw()

        if(winningPlayer != eastPos){
            this.nextPosition(); //only go to the next position if the winner is not in the east position (see rules)
        }
        this.gamesPlayed ++;
    }

    public void draw(){
        nextChineseGameNumber();//chinese game number should go up before the next position is called, same with winPayFan()
        nextPosition();
        this.gamesPlayed ++;
    }

    //Converts the fan to chips
    public int fanToChip(int fan){
        if(fan < minScore){
            return chipArray[0];
        }else if(fan > maxScore){
            return chipArray[maxScore];
        }else{
            return chipArray[fan];
        }
        //TODO Not sure which one is more efficient, so I'll keep both just in case.

//        int chips = 0;
//        try{
//            chips = this.chipArray[fan];
//        }
//        catch(ArrayIndexOutOfBoundsException e){
//            if(fan < 0){
//                chips = 0;
//            }else{
//                chips = this.chipArray[maxScore];
//            }
//        }
//        finally {
//            return chips;
//        }
    }


//------------------------Chinese game number (for the chinese numerals in the game count)-----------------------//

    public int getChineseGameNumber() {
        return chineseGameNumber;
    }
    public String getChineseGameNumberStr() {
        return Integer.toString(chineseGameNumber);
    }
    private void nextChineseGameNumber(){
        setChineseGameNumber(getChineseGameNumber()+1);
    }
    private void resetChineseGameNumber(){
        setChineseGameNumber(1);
    }


    //for testing purposes only:

    public void endGame(){
        int money = 0;
        for(int i = 0 ; i < p.length ; i++){
            money =  (int) (p[i].getChips()*chipValue);
            System.out.println("player " + i + " gets: " + money);
        }
    }
}
