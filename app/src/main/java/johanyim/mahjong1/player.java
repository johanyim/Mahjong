package johanyim.mahjong1;

        import java.io.Serializable;

public class player implements Serializable{

    //attributes
    int chips;

    public player(int chips) {
        this.chips = chips;

    }

    public int getChips() {
        return this.chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void addChips(int amount) {
        this.setChips(this.chips + amount);
    }

    public void subtractChips(int amount) {
        this.setChips(this.chips - amount);
    }
}
