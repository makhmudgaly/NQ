package kz.enu.sprites;

import kz.enu.states.game.PlayState;

/**
 * Created by SLUX on 09.06.2017.
 */

public class Board {
    private int[] slotCounter = new int[18];
    private boolean[] slotTuzdyker = new boolean[18];
    private int[] stoneBankCounter= new int[2];
    private boolean turn;

    public Board(Slot[] slots,StoneBank[] stoneBanks,boolean turn){
        for(int i = 0;i<slots.length;i++){
            this.slotCounter[i]= slots[i].currentStonesNumber;
            this.slotTuzdyker[i] = slots[i].isTuzdyk;
        }
        for(int i = 0;i<stoneBanks.length;i++){
            this.stoneBankCounter[i] = stoneBanks[i].currentStonesNumber;
        }
        this.turn = turn;
    }

    public void getShot(PlayState playState){
        System.out.println(stoneBankCounter[0] + " : "+stoneBankCounter[1]);
        playState.setSlots(this.slotCounter);
        playState.setStoneBanks(this.stoneBankCounter);
        playState.setTurn(this.turn);
        //playState.setSlotTuzdyk(this.slotTuzdyker);
    }
}
