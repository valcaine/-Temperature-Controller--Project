package com.example.derya.tempcontrol;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView TextView;
    private TextView TextView6;
    private Button Button;
    public String setPointTemp;
    public String IP="172.20.54.66";

    InetAddress serverAddr = null;
    SocketAddress sc_add = null;
    Socket socket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        TextView = (TextView) findViewById(R.id.textView3);
        TextView6 = (TextView) findViewById(R.id.textView6);
        seekBar.setMax(1000);


        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                float currentProgress = seekBar.getProgress() * 0.1f;
                String progress = String.format("%.1f", currentProgress);
                setPointTemp = progress;
                TextView.setText(progress + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        addButtonClickListener();

        //toggle button modifications.


        PrimeRun proc = new PrimeRun();
        new Thread(proc).start();

    }

    public void addButtonClickListener()
    {
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView  text1 = (TextView)findViewById(R.id.textView4);
                System.out.println(TextView.getText()+ "°C ");
                text1.setText(TextView.getText());

                Runnable r = new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            serverAddr = InetAddress.getByName(IP);
                            sc_add= new InetSocketAddress(serverAddr,9000);
                            socket = new Socket();
                            socket.connect(sc_add,500);

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            //out.writeUTF("INFO\n");
                            String message = "SPTEMP "+setPointTemp+"\n";
                            System.out.println(message);
                            out.write(message.getBytes());

                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String read = in.readLine();

                            //String unicode = new String(read.getBytes(), "US-ASCII");
                            //String unicode = new String(read.getBytes(), "windows-1252")
                            String result = convertFromUTF8(read);
                            //System.out.println(convertFromUTF8(read));

                            //debugMsg(result);

                            socket.close();

                        } catch (UnknownHostException e)
                        {
                            System.out.println(e);
                        } catch (SocketException e)
                        {
                            System.out.println(e);
                        } catch(IOException e)
                        {
                            System.out.println(e);
                        }

                    }
                };
                new Thread(r).start();
            }
        });
    }

    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public void debugMsg(String msg)
    {
        final String str = msg;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                TextView6.setText(str + " °C ");
            }
        });
    }

    public class PrimeRun implements Runnable
    {
        public PrimeRun()
        {

        }

        public void run()
        {
            while(true)
            {
                try
                {
                    serverAddr = InetAddress.getByName(IP);
                    sc_add= new InetSocketAddress(serverAddr,9000);
                    socket = new Socket();
                    socket.connect(sc_add,2000);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //out.writeUTF("INFO\n");
                    String message = "PVTEMP\n";
                    out.write(message.getBytes());

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String read = in.readLine();

                    //String unicode = new String(read.getBytes(), "US-ASCII");
                    //String unicode = new String(read.getBytes(), "windows-1252")
                    String result = convertFromUTF8(read);
                    //System.out.println(convertFromUTF8(read));

                    socket.close();

                    debugMsg(result);
                    Thread.sleep(1000);

                    //textField.setText(result);
                } catch (UnknownHostException e)
                {
                    System.out.println(e);
                } catch (SocketException e)
                {
                    System.out.println(e);
                } catch(IOException e)
                {
                    System.out.println(e);
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }

            }
        }
    }


}




