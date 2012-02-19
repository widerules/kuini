package pl.edu.uj.tcs.kuini.controller.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pl.edu.uj.tcs.kuini.controller.Controller;
import pl.edu.uj.tcs.kuini.model.factories.ModelFactory;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class JoinGame extends AbstractGame {

    private final BluetoothDevice device;
    
    public JoinGame(IView view, BluetoothDevice device) {
        super(view);
        this.device = device;
    }
    
    @Override
    public void run() {
        BluetoothSocket socket = null;
                
        try {
        
            socket = device.createRfcommSocketToServiceRecord(KUINI_UUID);
            socket.connect();
        
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            
            int playerId = (Integer)in.readObject();
            model = ModelFactory.getModel((ModelFactory.Arguments)in.readObject());
            
            /*
            model = ModelFactory.getModel(
                    new PlayerStub[]{
                            new PlayerStub("Host", HostGame.HOST_PLAYER_ID),
                            new PlayerStub("Guest", HostGame.GUEST_PLAYER_ID),
                    }, 800.0f/480.0f, 4475, 1.0f, true);            
            int playerId = HostGame.GUEST_PLAYER_ID;
            */
            
            controller = new Controller(in, out, model, stateChangeListener);
            view.gameStarted(playerId);
            controller.run();

            view.gameFinished();
            
        } catch (Exception e) { /* IOException, ClassNotFoundException, ClassCastException */
            Log.i("JoinGame", Log.getStackTraceString(e));
            view.gameFinished();
        }
        
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {}
    }
    
}
