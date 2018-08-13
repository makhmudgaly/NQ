package kz.enu;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import kz.enu.sprites.Slot;

public class AI {
    public static final float AGRESSIVE_FACTOR = 0.983f;

    public static ArrayList<Integer> getPossibleRandom(Slot[] slots){
        ArrayList arr = new ArrayList();
        for(int i = 9;i<slots.length;i++) {
            if (slots[i].currentStonesNumber != 0) {
                arr.add(i);
                //System.out.print(i+" ");
            }
        }
        //System.out.print("\n");
        return arr;
    }
    public static int makeMoveHardAI(Slot [] slots,Map <Integer,Integer> possibleMoves)
    {
        Slot [] tempSlots = slots;
        int  choice = makeMoveEasyAI(slots);
        boolean nothingToGet = false;
        for(int i=9;i<18;i++)
        {
            if(tempSlots[i].currentStonesNumber != 1 && tempSlots[i].currentStonesNumber !=0)
            {
                if(tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].side==true &&
                        (tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].currentStonesNumber+1) % 2 == 0 )
                {
                    possibleMoves.put(i ,tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].currentStonesNumber+1 );
                }
                if(tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].currentStonesNumber+1 == 3)
                {
                    choice = i;break;
                }
            }


        }
        if(!possibleMoves.isEmpty()){
            if(possibleMoves.size()>1){
                int keyOfMaxValue = Collections.max(
                        possibleMoves.entrySet(),
                        new Comparator<Entry<Integer,Integer>>(){
                            @Override
                            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                                return o1.getValue() > o2.getValue()? 1:-1;
                            }
                        }).getKey();
                choice = keyOfMaxValue;
                possibleMoves.clear();
            }
            else {
                choice = (Integer)possibleMoves.keySet().toArray()[0];
                possibleMoves.clear();
            }
        } else nothingToGet= true;
        if(nothingToGet==true)
            choice = protectMineOdd(tempSlots);
        return choice;
    }


    public static int protectMineOdd(Slot [] slots){
        int  choice = makeMoveEasyAI(slots);
        Slot [] tempSlots = slots;
        int OddSlotsNumber = 0;
        ArrayList<Integer> positionOfOdd = new ArrayList<Integer>();
        for(int i=9;i<tempSlots.length;i++)
        {
            if(tempSlots[i].currentStonesNumber%2!=0)
            {
                OddSlotsNumber++;
                positionOfOdd.add(i);
            }

        }
        if(OddSlotsNumber==1)
        {
            for(int i=9;i<tempSlots.length;i++)
            {
                if(tempSlots[i].currentStonesNumber==2||tempSlots[i].currentStonesNumber==1)
                {
                    if(positionOfOdd.get(0)-i==1)
                    {choice = i;break;}
                    if(	((tempSlots[((tempSlots[i].currentStonesNumber-1)+i)%18].currentStonesNumber+1)%2 == 0)
                            &&( (((tempSlots[i].currentStonesNumber-1)+i)%18==positionOfOdd.get(0))))
                    {
                        if(tempSlots[i].currentStonesNumber!=0){
                            choice = i;
                            break;}
                    }
                }
            }
            if((positionOfOdd.get(0)==17||positionOfOdd.get(0)==16)&& tempSlots[positionOfOdd.get(0)].currentStonesNumber <= positionOfOdd.get(0)-9)
                if(tempSlots[positionOfOdd.get(0)].currentStonesNumber!=0)

                    choice = positionOfOdd.get(0);
        }
        else
        {
            int OddSlots [] = new int [positionOfOdd.size()];
            int Indexs [] = new int [positionOfOdd.size()];
            for(int i=0;i<Indexs.length;i++)
                Indexs[i] = positionOfOdd.get(i);

            for(int i=0;i<OddSlots.length;i++)
                OddSlots[i] = tempSlots[Indexs[i]].currentStonesNumber;

            int temp = 0;
            int tempIndex = 0;
            for(int i=0; i < OddSlots.length; i++)
                for(int j=1; j < (OddSlots.length-i); j++){
                    if(OddSlots[j-1] < OddSlots[j]){
                        //swap elements
                        temp = OddSlots[j-1];
                        tempIndex = Indexs[j-1];
                        OddSlots[j-1] = OddSlots[j];
                        Indexs[j-1]=Indexs[j];
                        OddSlots[j] = temp;
                        Indexs[j] = tempIndex;
                    }
                }
            boolean flag= false;
            for(int i=0;i<OddSlots.length;i++)
            {
                for(int j=9;j<tempSlots.length;j++)
                {
                    if(tempSlots[j].currentStonesNumber==2||tempSlots[j].currentStonesNumber==1)
                    {
                        if(Indexs[i]-j==1)
                        {if(tempSlots[j].currentStonesNumber!=0){
                            choice = j;
                            flag = true;break;
                        }}
                    }
                    if(  ((tempSlots[((tempSlots[j].currentStonesNumber-1)+j)%18].currentStonesNumber+1)%2 == 0)
                            && ((tempSlots[j].currentStonesNumber-1)+j) == Indexs[i])
                    {
                        if(tempSlots[j].currentStonesNumber!=0){
                            choice = j;
                            flag = true;break;}
                    }
                }
                if(flag)break;
                if((Indexs[i]==17||Indexs[i]==16)&& tempSlots[Indexs[i]].currentStonesNumber <= Indexs[i])
                    if(tempSlots[Indexs[i]].currentStonesNumber!=0){
                        choice = Indexs[i];break;}
            }
        }
        if(OddSlotsNumber == 0) return makeMoveEasyAI(slots);
        positionOfOdd.clear();
        OddSlotsNumber=0;
        return choice;
    }

    public static int makeMoveNormalAI(Slot[] slots, Map<Integer,Integer> possibleMoves)
    {

        int  choice = makeMoveEasyAI(slots);
        Slot [] tempSlots = slots;
        //int choice;
        //int temp[] = new int [9];
        for(int i=9;i<18;i++)
        {
            if(tempSlots[i].currentStonesNumber != 1 && tempSlots[i].currentStonesNumber !=0)
            {
                if(tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].side &&
                        (tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].currentStonesNumber+1) % 2 == 0 )
                    possibleMoves.put(i ,tempSlots[(i + (tempSlots[i].currentStonesNumber - 1))%18].currentStonesNumber+1 );

            }
        }
        if(!possibleMoves.isEmpty()){
            int keyOfMaxValue = Collections.max(
                    possibleMoves.entrySet(),
                    new Comparator<Map.Entry<Integer,Integer>>(){
                        @Override
                        public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                            return o1.getValue() > o2.getValue()? 1:-1;
                        }
                    }).getKey();
            possibleMoves.clear();
            return keyOfMaxValue;}

        else return makeMoveEasyAI(slots);


    }

    public static int makeMoveEasyAI(Slot[] slots){
        ArrayList<Integer> random = getPossibleRandom(slots);
        int choice = 0;
        try{
            choice = random.get((int) (Math.random() * random.size()));
        }catch (IndexOutOfBoundsException ex){
        }
        return choice;
    }


    public static float imitateMove(Slot[] slot,int move){
        Slot[] slots = new Slot[18];
        float extraProb = 0;
        int stack = slot[move].currentStonesNumber;
        if(stack>16){
            extraProb = 9 + (stack-16)*0.75f;
        }
        for(int i = 0;i<18;i++){
            slots[i]=new Slot(slot[i]);
        }
        Slot currentSlot = slots[move];

        int currentSlotStoneNumber = currentSlot.currentStonesNumber;
        int gotPoint = 0;
        int savePoint = 0;
        boolean isAlreadyTuzdykCreated = false;
        for(int i = 0;i<9;i++){
            if(slots[i].isTuzdyk)isAlreadyTuzdykCreated=true;
        }
        int lastSlotIndex = makeMoveEasyAI(slots);
        if (currentSlotStoneNumber != 0) {
            currentSlot.currentStonesNumber = 0;

            if (currentSlotStoneNumber == 1) {
                if (slots[(move + 1) % 18].isTuzdyk) {
                    gotPoint++;
                } else {
                    slots[(move + 1) % 18].currentStonesNumber++;
                }
                lastSlotIndex = (move + 1) % 18;
            } else {
                lastSlotIndex = (move + currentSlotStoneNumber - 1) % 18;
                for (int i = 0; i < currentSlotStoneNumber; i++) {
                    if (slots[(move + i) % 18].isTuzdyk) {
                        gotPoint++;
                    } else {
                        slots[(move + i) % 18].currentStonesNumber++;
                    }
                }
            }

        }

        if(slots[lastSlotIndex].currentStonesNumber%2==0)gotPoint+=(slots[lastSlotIndex].currentStonesNumber);
        if(slots[lastSlotIndex].currentStonesNumber==3&&(!isAlreadyTuzdykCreated)&&slots[lastSlotIndex].side)gotPoint+=25;
        if(slots[lastSlotIndex].isTuzdyk)gotPoint-=5;
        for(int i = 9;i<18;i++){
            if(slots[i].currentStonesNumber%2==0)savePoint+=slots[i].currentStonesNumber;
        }
        System.out.println( "**"+move+"** | G:"+gotPoint+"| S:"+savePoint+"| X:"+(gotPoint*AGRESSIVE_FACTOR+savePoint*(1-AGRESSIVE_FACTOR)+extraProb));
        return gotPoint*AGRESSIVE_FACTOR+savePoint*(1-AGRESSIVE_FACTOR)+extraProb;
    }

    public static int makeMoveEffectiveAI(Slot [] slots){
        int choice = 9;
        float maximum = 0;
        if(slots[9].isTuzdyk)
        {maximum = imitateMove(slots,10);choice = 10;}
        else
        {maximum = imitateMove(slots,9);}

        for(int i = 9;i<18;i++){

            if(!slots[i].isTuzdyk)if(imitateMove(slots,i)>maximum){maximum = imitateMove(slots,i);choice = i;}
            System.out.println("MaXIMUX:"+maximum);
        }
        if(slots[choice].currentStonesNumber==0)choice = makeMoveEasyAI(slots);

        return choice;
    }



}
