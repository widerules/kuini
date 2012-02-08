package pl.edu.uj.tcs.kuini.gui;

import pl.edu.uj.tcs.kuini.R;
import pl.edu.uj.tcs.kuini.controller.game.AbstractGame;
import pl.edu.uj.tcs.kuini.controller.game.DemoGame;
import pl.edu.uj.tcs.kuini.controller.game.HostGame;
import pl.edu.uj.tcs.kuini.controller.game.IView;
import pl.edu.uj.tcs.kuini.controller.game.JoinGame;
import pl.edu.uj.tcs.kuini.model.IState;
import pl.edu.uj.tcs.kuini.view.KuiniView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class KuiniActivity extends Activity implements IView {

    public static final int HOST_GAME = 0;
    public static final int JOIN_GAME = 1;
    public static final int DEMO_GAME = 2;
   
    private View splash;
    private ProgressBar splashProgressBar;
    private TextView splashText;
    
    private AbstractGame game = null;
    private KuiniView view;
    
    private boolean hasStarted = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.splash);
        
        splash = findViewById(R.id.splash);
        splashProgressBar = (ProgressBar)findViewById(R.id.splashProgressBar);
        splashText = (TextView)findViewById(R.id.splashText);
        
        splashProgressBar.setVisibility(View.VISIBLE);
        splashText.setText(R.string.splash_before);
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels; 
        
        view = new KuiniView(this, width, height);
        
        // TODO: check if BT is enabled
        
        Intent intent = getIntent();
        
        switch (intent.getIntExtra("game", HOST_GAME)) {
        case HOST_GAME:
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
            game = new HostGame(this, adapter);
            break;
        case JOIN_GAME:
            BluetoothDevice device = intent.getParcelableExtra("device");
            game = new JoinGame(this, device);
            break;
        case DEMO_GAME:
            game = new DemoGame(this);
            break;
        }

        view.setCommandProxy(game);
        
        game.start();
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (game != null) game.interrupt();
    }

    @Override
    public void gameStarted(final int playerId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hasStarted = true;
                view.setPlayerId(playerId);
                setContentView(view);
            }
        });
    }

    @Override
    public void gameFailed() {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                splashProgressBar.setVisibility(View.GONE);
                if (hasStarted)
                    splashText.setText(R.string.splash_fail);
                else
                    splashText.setText(R.string.splash_not_started);
                setContentView(splash);
            }
        });
    }

    @Override
    public void stateChanged(final IState state) {
        view.stateChanged(state);
    }
   
}