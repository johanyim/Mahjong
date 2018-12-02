


package johanyim.mahjong1;
import java.io.Serializable;

public class player implements Serializable{ //The Serializable Java Interface is used so that the player object can be passed to different activities

    //Variables

    private int chips; //Declared private so that this variable is only accessible through public methods
    //private int position;

    public player(int chips) { //This constructor allows the program to assign a specific number of chips upon initialization
        this.chips = chips;
    }

    public int getChips() {
        return this.chips;
    }

    private void setChips(int chips) {
        this.chips = chips;
    } //Mutation function is encapsulated because the only functions to alter the chips are addChips() and subtractChips()

    public void addChips(int amount) {
        this.setChips(this.chips + amount);
    }

    public void subtractChips(int amount) {
        this.setChips(this.chips - amount);
    }
}
