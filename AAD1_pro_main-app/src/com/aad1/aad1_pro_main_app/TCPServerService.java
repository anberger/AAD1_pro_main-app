//package com.aad1.aad1_pro_main_app;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.math.BigInteger;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.nio.ByteOrder;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.WifiManager;
//import android.os.Handler;
//import android.os.IBinder;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//public class TCPServerService extends Service {
//	 
//    // DEFAULT IP
//    public static String SERVERIP = "192.168.1.11";
// 
//    // DESIGNATE A PORT
//    public static final int SERVERPORT = 6000;
// 
//    private Handler handler = new Handler();
// 
//    private ServerSocket serverSocket;
//
//	@Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//    	
//		SERVERIP = wifiIpAddress(getApplicationContext());
//		 
//        Thread fst = new Thread(new ServerThread());
//        fst.start();
//		
//        return Service.START_FLAG_REDELIVERY;
//    }
//	
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		try {
//            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
//
//	public class ServerThread implements Runnable {
//		 
//        public void run() {
//            try {
//                if (SERVERIP != null) {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            SendData("Listening on IP: " + SERVERIP);
//                        }
//                    });
//                    serverSocket = new ServerSocket(SERVERPORT);
//                    while (true) {
//                        // LISTEN FOR INCOMING CLIENTS
//                        Socket client = serverSocket.accept();
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                SendData("Connected.");        
//                            }
//                        });
// 
//                        try {
//                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                            String line = null;
//                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client
//                                    .getOutputStream())), true);
//                            out.println("FUCK you");
//                            
//                            while ((line = in.readLine()) != null) {
//                                Log.d("ServerActivity", line);
//                                SendData(line);
//                                
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                    	
//                                    	
//                                    }
//                                });
//                            }
//                            break;
//                        } catch (Exception e) {
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    SendData("Oops. Connection interrupted. Please reconnect your phones.");
//                                }
//                            });
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            SendData("Couldn't detect internet connection.");
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        SendData("Error");
//                    }
//                });
//                e.printStackTrace();
//            }
//        }
//    }
//	
//	private void SendData(String message){
//		// Create a new Intent called duration which sends the remaining time to Activity_1 via a LocalBroadcastManager
//		Intent intent = new Intent("message");
//		
//		// Add the time to the Intent
//		intent.putExtra("data", message);
//		
//		// Send the Intent to Activity_1 via LocalBroadcastmanager
//		LocalBroadcastManager.getInstance(TCPServerService.this).sendBroadcast(intent);	
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	protected String wifiIpAddress(Context context) {
// 	    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
// 	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//
// 	    // Convert little-endian to big-endianif needed
// 	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
// 	        ipAddress = Integer.reverseBytes(ipAddress);
// 	    }
//
// 	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
//
// 	    String ipAddressString;
// 	    try {
// 	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
// 	    } catch (UnknownHostException ex) {
// 	        Log.e("WIFIIP", "Unable to get host address.");
// 	        ipAddressString = null;
// 	    }
//
// 	    return ipAddressString;
// 	}
//}