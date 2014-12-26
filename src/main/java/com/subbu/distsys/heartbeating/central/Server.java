package com.subbu.distsys.heartbeating.central;

import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Leader leader;
    private int port;
    private ServerSocket serverSocket;
    private boolean isStopped = false;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int port, Leader leader) {
        this.leader = leader;
        this.port = port;
    }

    @Override
    public void run() {
        openServerSocket();

        while (!isStopped) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (isStopped) {
                    System.out.println("server is stopped");
                    return;
                }
                e.printStackTrace();
            }

            if (clientSocket != null) {
                processClientRequest(clientSocket);
            }
        }
    }

    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processClientRequest(Socket clientSocket) {
        try {
            BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());
            BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String input;
            String processId = "";
            String heartBeatCounter = "";
            while ((input = bf.readLine()) != null) {
                //logger.debug("still receiving......." + input);
                if (input.equals("~END~")) {
                    output.write("HTTP/1.1 200 OK\n".getBytes());
                    output.flush();
                    break;
                }

                if (input.startsWith("processID")) {
                    processId = input;
                }

                if (input.startsWith("heartBeat")) {
                    heartBeatCounter = input;
                }
            }
            sendHeartBeat(processId, heartBeatCounter);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHeartBeat(String processIdString, String heartBeatCounterString) {
        String[] processIdSplit = processIdString.split("=");
        String[] heartBeatSplit = heartBeatCounterString.split("=");
        leader.receiveHeartBeat(processIdSplit[1], Integer.parseInt(heartBeatSplit[1]));
    }
}
