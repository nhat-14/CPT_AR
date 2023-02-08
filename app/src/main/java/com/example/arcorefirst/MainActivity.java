package com.example.arcorefirst;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private static final double MIN_OPENGL_VERSION = 3.0;

    ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // Method invocation 'setOnTapArPlaneListener' may produce 'NullPointerException'
        assert arFragment != null;
        arFragment.setOnTapArPlaneListener(
                (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {
                    //Ignore if tap not on horizontal plane facing upward (e.g. floor or tabletop)
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING)
                        return;

                    //Initial pose of the arrow (virtual object)
                    float[] transl = hitresult.getHitPose().getTranslation();
                    float[] rotation = { 0, (float) 0.707, 0, (float) 0.707};

                    Anchor anchor = hitresult.getTrackable().createAnchor(new Pose(transl, rotation));
                    placeObject(arFragment, anchor, R.raw.arrow);
                }
        );
        connectAction();
    }

    private void placeObject(ArFragment arFragment, Anchor anchor, int uri) {
        ModelRenderable.builder()
            .setSource(arFragment.getContext(), uri)
            .build()
            .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
            .exceptionally(throwable -> {
                    Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                }
            );
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Recover connections.
        Map<String, Connection> connections = Connections.getInstance(this).getConnections();

        //Register receivers again
        for (Connection connection : connections.values()){
            connection.getClient().registerResources(this);
            connection.getClient().setCallback(new MqttCallbackHandler(this, connection.getClient().getServerURI()+connection.getClient().getClientId()));
        }
    }

    @Override
    protected void onDestroy() {
        Map<String, Connection> connections = Connections.getInstance(this).getConnections();
        for (Connection connection : connections.values()){
            connection.getClient().unregisterResources();
        }
        super.onDestroy();
    }



    private void connectAction() {
        MqttConnectOptions conOpt = new MqttConnectOptions();
        String server = "192.168.0.136";
        String clientId = "Ogawa";
        int port = 1883;
        boolean cleanSession = false;
        boolean ssl = false;
        String uri = "tcp://";

        uri = uri + server + ":" + port;

        MqttAndroidClient client;
        client = Connections.getInstance(this).createClient(this, uri, clientId);

        // create a client handle
        String clientHandle = uri + clientId;

        // last will message
        String message = "";
        String topic = "cptdata";
        Integer qos = 0;
        Boolean retained = false;

        int timeout = 1000;
        int keepalive = 10;

        Connection connection = new Connection(clientHandle, clientId, server, port,
                this, client, ssl);

        // connect client
        String[] actionArgs = new String[1];
        actionArgs[0] = clientId;
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

        conOpt.setCleanSession(cleanSession);
        conOpt.setConnectionTimeout(timeout);
        conOpt.setKeepAliveInterval(keepalive);

        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, clientHandle, actionArgs);

        boolean doConnect = true;

        // need to make a message since last will is set
        try {
            conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        }
        catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Exception Occured", e);
            doConnect = false;
            callback.onFailure(null, e);
        }
        client.setCallback(new MqttCallbackHandler(this, clientHandle));


        connection.addConnectionOptions(conOpt);
        Connections.getInstance(this).addConnection(connection);
        if (doConnect) {
            try {
                client.connect(conOpt, null, callback);
            }
            catch (MqttException e) {
                Log.e(this.getClass().getCanonicalName(),
                        "MqttException Occured", e);
            }
        }
    }
}