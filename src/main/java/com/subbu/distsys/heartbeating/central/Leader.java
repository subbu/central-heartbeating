package com.subbu.distsys.heartbeating.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class Leader {
    private HashMap<String, LinkedHashMap<Long, Integer>> members;
    private Server leaderServer;
    private Process process;
    private int port = 8088;
    private static Logger logger = LoggerFactory.getLogger(Process.class);

    public Leader(Process process) {
        this.process = process;
        electAsLeader();
    }

    public void electAsLeader() {
        if (members == null) {
            members = new HashMap<String, LinkedHashMap<Long, Integer>>();
        }
        // Open network channel to receive heartbeats
        leaderServer = new Server(port, this);
        Thread serverThread = new Thread(leaderServer);
        serverThread.start();
    }

    public void receiveHeartBeat(String processId, int latestHeartBeat) {
        // Find process using members.get(processID)
        // If not present, add it
        // Update the heartBeatCounter of incoming process
        // Update the timestamp
        LinkedHashMap<Long, Integer> heartBeats = members.get(processId);
        if (heartBeats == null) {
            heartBeats = new LinkedHashMap<Long, Integer>();
        }
        heartBeats.put(System.currentTimeMillis(), latestHeartBeat);
        members.put(processId, heartBeats);
        printProcessHeartBeats();
    }

    private void printProcessHeartBeats() {
        for (Map.Entry<String, LinkedHashMap<Long, Integer>> member : members.entrySet()) {
            Set<Map.Entry<Long, Integer>> entries = member.getValue().entrySet();
            Map.Entry<Long, Integer>[] a = new Map.Entry[entries.size()];
            entries.toArray(a);
            Map.Entry<Long, Integer> lastItem = a[a.length - 1];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String humanTime = simpleDateFormat.format(new Date(lastItem.getKey()));
            logger.info("lastHeartBeat of " + member.getKey() + " at " + humanTime + " - " + lastItem.getValue());
        }
        logger.info("--------------");
    }

}
