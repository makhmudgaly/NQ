package kz.enu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kz.enu.sprites.Slot;
import kz.enu.sprites.StoneBank;

/**
 * Created by SLUX on 22.06.2017.
 */

public class NetConnection {
    private static String serverPath;
    private GetBoard getBoard;
    private SendBoard sendBoard;
    private SendRandomNumber sendRandomNumber;
    private int matchId;
    private String result;
    public NetConnection(String serverPath,int matchId){
        this.serverPath = serverPath;
        setGetBoard(new GetBoard(serverPath,matchId));
        setSendBoard(new SendBoard(serverPath,matchId));
    }
    public SendRandomNumber getSendRandomNumber(){
        return this.sendRandomNumber;
    }
    public void getGetBoard() {
        this.getBoard.run();
    }
    public void setGetBoard(GetBoard getBoard) {
        this.getBoard = getBoard;
    }
    public SendBoard getSendBoard() {
        return sendBoard;
    }
    public void setSendBoard(SendBoard sendBoard) {
        this.sendBoard = sendBoard;
    }

    public String getResult() {
        return this.getBoard.result;
    }

    public void sendResult(Slot[] slots, StoneBank[] stoneBanks){
        this.sendBoard.result = "";

        for(int i = 0;i<slots.length;i++){
            if(!slots[i].isTuzdyk)this.sendBoard.result +="&s"+i+"="+slots[i].currentStonesNumber;
            else this.sendBoard.result +="&s"+i+"=-1";

        }
        for (int i = 0;i<stoneBanks.length;i++){
            this.sendBoard.result +="&sb"+i+"="+stoneBanks[i].currentStonesNumber;
        }
        this.sendBoard.run();
    }

    public void sendRandomNumber(int i){
        this.getSendRandomNumber().result = "&rnd="+i;
        this.getSendRandomNumber().run();
    }
}

class GetBoard extends Thread{

    HttpURLConnection conn;
    String serverPath;
    int matchId;
    String result;

    public GetBoard(String serverPath,int matchId){
        this.serverPath = serverPath;
        this.matchId = matchId;
    }
    @Override
    public void run(){
        try {
            String post_url = serverPath+"?action=select&_id="+matchId;

            URL url = new URL(post_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoInput(true);
            conn.connect();

        } catch (Exception e) {

        }
        try{
            InputStream is =conn.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String bfr_st = null;
            while ((bfr_st = br.readLine()) != null) {
                sb.append(bfr_st);
            }
            is.close();
            br.close();
            result = sb.toString();

        } catch (Exception e) {

        } finally {
            conn.disconnect();
        }

    }
}

class SendBoard extends Thread{

    HttpURLConnection conn;
    String serverPath;
    int matchId;
    String result;

    public SendBoard(String serverPath,int matchId){
        this.serverPath = serverPath;
        this.matchId = matchId;
    }
    @Override
    public void run(){
        try {
            String post_url = serverPath+"?action=update&_id="+matchId+this.result;
            URL url = new URL(post_url);
            System.out.println(post_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoInput(true);
            conn.connect();
            System.out.println( conn.getResponseCode());
        } catch (Exception e) {

        } finally {
            conn.disconnect();
        }

    }
}

class SendRandomNumber extends Thread{
    HttpURLConnection conn;
    String serverPath;
    int matchId;
    String result;
    public SendRandomNumber(String serverPath,int matchId){
        this.serverPath = serverPath;
        this.matchId = matchId;
    }
    @Override
    public void run(){
        try {
            String post_url = serverPath+"?action=start&_id="+matchId+this.result;
            URL url = new URL(post_url);
            System.out.println(post_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoInput(true);
            conn.connect();
            System.out.println( conn.getResponseCode());
        } catch (Exception e) {

        } finally {
            conn.disconnect();
        }
    }
}
