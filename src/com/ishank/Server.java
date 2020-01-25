package com.ishank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //static ServerSocket variable
    private static ServerSocket serverSocket;
    //static socket variable
    private static Socket socket;
    //socket server port on which it will listen
    private static int port = 6666;

    public static void main(String[] args) throws IOException {
        try {
            //server waits for connection requests
            System.out.println("\nwaiting for incoming connection requests...\n");
            serverSocket = new ServerSocket(port);
            int counter = 0;

            //infinite loop for accepting socket requests
            while (true){
                counter++;
                socket = serverSocket.accept();
                System.out.println("Client " + counter + " started!");
                //passing the client request to a new thread
                ServerClientThread serverClientThread = new ServerClientThread(socket,counter,serverSocket);
                serverClientThread.start();
            }
        }
        catch (Exception e){
            //System.out.println("All the clients have disconnected, Socket closed");
        }
    }
}

//ServerClientThread class extending Thread class
class ServerClientThread extends Thread {
       Socket clientSocket;
       int clientCount;
       ServerSocket ss;
       //ServerClientThread constructor with socket and counter as the parameters
       ServerClientThread(Socket inSocket,int counter, ServerSocket serverSocket){
            clientSocket = inSocket;
            clientCount = counter;
            ss = serverSocket;
       }
       //calling run method
       public void run() {
            try {
                //declaring input and output streams
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                String clientMessage="", serverMessage="Hello";
                dataOutputStream.writeUTF(serverMessage);

                //if clientMessage == Bye, terminate the connection
                while (!(clientMessage.equals("Bye") || clientMessage.equals("bye"))) {
                    //reading message from the client side
                    clientMessage = dataInputStream.readUTF();
                    System.out.println("Message from Client " + clientCount + ": " + clientMessage);
                    if (clientMessage.equals("Bye") || clientMessage.equals("bye"))
                        break;
                    //sending back message to the client
                    dataOutputStream.writeUTF(serverMessage);
                    dataOutputStream.flush();
                }
                dataInputStream.close();
                dataOutputStream.close();
                System.out.println("connection with client " + clientCount + " closed");
                //reducing client number when a client disconnects
                clientCount = clientCount -1;
                //System.out.println("Number of active connections with the server: " + clientCount);
                //closing the socket
                clientSocket.close();
                //if clientNo becomes 0, close the server
                if (clientCount ==0){
                    ss.close();
                }

            }
            catch (Exception ex){
                System.out.println("");
            }
       }
}
