package kz.enu.states.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import kz.enu.sprites.Slot;
import kz.enu.sprites.StoneBank;

/**
 * Local save implemented Class
 * Created by Administrator on 04.09.2018.
 */

public class LocalGame extends PlayState{
    private static FileHandle fileHandle = null;

    public LocalGame(GameStateManager gsm, boolean isNewGame) {
        super(gsm);

        // fileHandle
        fileHandle = Gdx.files.local(sSaveFile);

        if (isNewGame)
            try {
                loadSavedFile();
            } catch (FileNotFoundException fnfe) {}
        else {
            turn = true;
        }
    }

    private static void loadGame() throws FileNotFoundException {
        Scanner in = new Scanner(fileHandle.file());
        int tmpi;
        for (Slot slot :slots) {
            tmpi = in.nextInt();
            if (tmpi == -1) {
                slot.currentStonesNumber = 0;
                slot.isTuzdyk = true;
                slot.texture = tuzdykTexture;
            } else {
                slot.currentStonesNumber = tmpi;
            }
        }
        for (StoneBank stoneBank : stoneBanks) {
            stoneBank.currentStonesNumber = in.nextInt();
        }
        String tmp = in.nextLine();

        if (tmp.equals("true")) turn = true;
        else if (tmp.equals("false")) turn = false;
        bAnimationStarted = true;
        in.close();
    }

    @Override
    protected void saveGame() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileHandle.file());
        for (Slot slot :slots) {
            if (!slot.isTuzdyk) pw.println(slot.currentStonesNumber);
            else pw.println(-1);
            pw.flush();
        }
        for (StoneBank stoneBank : stoneBanks) {
            pw.println(stoneBank.currentStonesNumber);
            pw.flush();
        }
        pw.println(turn);
        pw.close();

    }


    private static void loadSavedFile() throws FileNotFoundException {
        if (fileHandle.file().exists()) {
            loadGame();
        }
    }

    @Override
    protected void refreshGame() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileHandle.file());
        for (int i = 0; i < 18; i++) {
            pw.println(9);
        }
        for (int i = 0; i < 2; i++) {
            pw.println(0);
        }
        pw.println(0);

        pw.close();
    }

}
