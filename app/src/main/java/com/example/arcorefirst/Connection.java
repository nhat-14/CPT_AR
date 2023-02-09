package com.example.arcorefirst;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import android.content.Context;
//import org.eclipse.paho.android.service.MqttAndroidClient;
import info.mqtt.android.service.MqttAndroidClient;

/**
 *
 * Represents a {@link MqttAndroidClient} and the actions it has performed
 *
 */
public class Connection {

  /*
   * Basic Information about the client
   */
  /** ClientHandle for this Connection Object**/
  private String clientHandle = null;
  /** The clientId of the client associated with this <code>Connection</code> object **/
  private String clientId = null;
  /** The host that the {@link MqttAndroidClient} represented by this <code>Connection</code> is represented by **/
  private String host = null;
  /** The port on the server this client is connecting to **/
  private int port = 0;
  /** {@link ConnectionStatus} of the {@link MqttAndroidClient} represented by this <code>Connection</code> object. Default value is {@link ConnectionStatus#NONE} **/
  private ConnectionStatus status = ConnectionStatus.NONE;
  /** The history of the {@link MqttAndroidClient} represented by this <code>Connection</code> object **/
  private ArrayList<String> history = null;
  /** The {@link MqttAndroidClient} instance this class represents**/
  private MqttAndroidClient client = null;

  /** Collection of {@link PropertyChangeListener} **/
  private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

  /** The {@link Context} of the application this object is part of**/
  private Context context = null;

  /** The {@link MqttConnectOptions} that were used to connect this client**/
  private MqttConnectOptions conOpt;

  /** True if this connection is secured using SSL **/
  private boolean sslConnection = false;


  /**
   * Connections status for  a connection
   */
  enum ConnectionStatus {

    /** Client is Connecting **/
    CONNECTING,
    /** Client is Connected **/
    CONNECTED,
    /** Client is Disconnecting **/
    DISCONNECTING,
    /** Client is Disconnected **/
    DISCONNECTED,
    /** Client has encountered an Error **/
    ERROR,
    /** Status is unknown **/
    NONE
  }

  /**
   * Creates a connection object with the server information and the client
   * hand which is the reference used to pass the client around activities
   * @param clientHandle The handle to this <code>Connection</code> object
   * @param clientId The Id of the client
   * @param host The server which the client is connecting to
   * @param port The port on the server which the client will attempt to connect to
   * @param context The application context
   * @param client The MqttAndroidClient which communicates with the service for this connection
   * @param sslConnection true if the connection is secured by SSL
   */
  public Connection(String clientHandle, String clientId, String host,
      int port, Context context, MqttAndroidClient client, boolean sslConnection) {
    //generate the client handle from its hash code
    this.clientHandle = clientHandle;
    this.clientId = clientId;
    this.host = host;
    this.port = port;
    this.context = context;
    this.client = client;
    this.sslConnection = sslConnection;
    history = new ArrayList<String>();
    StringBuffer sb = new StringBuffer();
    sb.append("Client: ");
    sb.append(clientId);
    sb.append(" created");
    addAction(sb.toString());
  }

  /**
   * Add an action to the history of the client
   * @param action the history item to add
   */
  public void addAction(String action) {
    Object[] args = new String[1];
    SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.dateFormat));
    args[0] = sdf.format(new Date());

    String timestamp = context.getString(R.string.timestamp, args);
    history.add(action + timestamp);

    notifyListeners(new PropertyChangeEvent(this, "history", null, null));
  }


  /**
   * Gets the client handle for this connection
   * @return client Handle for this connection
   */
  public String handle() {
    return clientHandle;
  }

  /**
   * Changes the connection status of the client
   * @param connectionStatus The connection status of this connection
   */
  public void changeConnectionStatus(ConnectionStatus connectionStatus) {
    status = connectionStatus;
    notifyListeners((new PropertyChangeEvent(this, "connectionStatus", null, null)));
  }

  /**
   * A string representing the state of the client this connection
   * object represents
   *
   *
   * @return A string representing the state of the client
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(clientId);
    sb.append("\n ");

    switch (status) {

      case CONNECTED :
        sb.append(context.getString(R.string.connectedto));
        break;
      case DISCONNECTED :
        sb.append(context.getString(R.string.disconnected));
        break;
      case NONE :
        sb.append(context.getString(R.string.no_status));
        break;
      case CONNECTING :
        sb.append(context.getString(R.string.connecting));
        break;
      case DISCONNECTING :
        sb.append(context.getString(R.string.disconnecting));
        break;
      case ERROR :
        sb.append(context.getString(R.string.connectionError));
    }
    sb.append(" ");
    sb.append(host);

    return sb.toString();
  }

  /**
   * Compares two connection objects for equality
   * this only takes account of the client handle
   * @param o The object to compare to
   * @return true if the client handles match
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Connection)) {
      return false;
    }

    Connection c = (Connection) o;

    return clientHandle.equals(c.clientHandle);

  }

  /**
   * Get the client Id for the client this object represents
   * @return the client id for the client this object represents
   */
  public String getId() {
    return clientId;
  }

  /**
   * Get the host name of the server that this connection object is associated with
   * @return the host name of the server this connection object is associated with
   */
  public String getHostName() {

    return host;
  }

  /**
   * Gets the client which communicates with the android service.
   * @return the client which communicates with the android service
   */
  public MqttAndroidClient getClient() {
    return client;
  }

  /**
   * Add the connectOptions used to connect the client to the server
   * @param connectOptions the connectOptions used to connect to the server
   */
  public void addConnectionOptions(MqttConnectOptions connectOptions) {
    conOpt = connectOptions;

  }


  /**
   * Notify {@link PropertyChangeListener} objects that the object has been updated
   * @param propertyChangeEvent
   */
  private void notifyListeners(PropertyChangeEvent propertyChangeEvent)
  {
    for (PropertyChangeListener listener : listeners)
    {
      listener.propertyChange(propertyChangeEvent);
    }
  }
}
