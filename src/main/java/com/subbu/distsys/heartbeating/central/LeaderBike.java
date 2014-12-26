package com.subbu.distsys.heartbeating.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Whoever rides the LeaderBike is the Leader
 */
public class LeaderBike {
    private static Leader Leader;
    private static final String LEADER_END_POINT = "http://localhost:8088";
    private static final Logger logger = LoggerFactory.getLogger(LeaderBike.class);

    public static void setLeader(Leader leader) {
        Leader = leader;
    }

    public static Leader getLeader() {
        return Leader;
    }

    public static void sendHeartBeat(Process process) {
        //logger.info("LeaderBike: received heart beat from {}. Dispatching to the rider.", process.getId());

        //getLeader().receiveHeartBeat(process.getId(), process.getHeartBeatCounter());
        //getLeader().receiveHeartBeat(process.getId(), process.getHeartBeatCounter());
        URL url;
        HttpURLConnection connection;

        try {
            url = new URL(LEADER_END_POINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            wr.writeBytes("processID=" + process.getId() + "\n");
            wr.writeBytes("heartBeat=" + process.getHeartBeatCounter() + "\n");
            wr.writeBytes("~END~\n");
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            response.append("\n");
            rd.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
