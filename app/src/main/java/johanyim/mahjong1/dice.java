package johanyim.mahjong1;

import android.widget.ImageView;

import java.util.Random;

public class dice {

  //State

  //Behaviour
  private static int rollDice(ImageView[] diceFacesObject, int[] diceFacesArray, int whichDice) {
    Random rand = new Random();

    float randFloat = rand.nextFloat(); //this gets a random float from 0 to 90
    diceFacesObject[whichDice].setRotation(randFloat*360);

    int randInt = rand.nextInt(6); //this gets a random integer from 0 to 5
    diceFacesObject[whichDice].setImageResource(diceFacesArray[randInt]);
    return randInt + 1;

  }//Returns a random number between 1 and 6



  public static int multiRoll(int diceNum, ImageView[] diceFacesObject, int[] diceFacesArray) {
    int diceSum = 0;

    for(int i = 0; i < diceNum ; i = i + 1){
      int diceFace = rollDice(diceFacesObject, diceFacesArray, i);
      diceSum += diceFace;
    }

    return diceSum;
    
  }//Returns the sum of a the dice faces, as well as calling the method rollDice automatically

  public static void diceDirection(int eastPos, int diceNum, ImageView pointerImage) {
    float direction = (eastPos + diceNum - 1)%4; //because 1 starts with the dealer and 2 is the person on the dealer's right
    pointerImage.setRotation(direction * -90 );
  }//returns the direction where the first tiles will be taken when given the current east position and the dice number
}
