package com.subbu.distsys.heartbeating.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Leader {
    private HashMap<String, LinkedHashMap<Long, Integer>> members;
    private Server leaderServer;
    private Process process;
    private int port = 8088;
    private final int FAILURE_DETECTION_INTERVAL = 5000;
    private final int HEARTBEAT_PRINT_INTERVAL = 5000;
    private final int TIMEOUT = 10000;
    private final static Logger logger = LoggerFactory.getLogger(Process.class);

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
        detectFailedProcessesPeriodically();
        printProcessHeartBeatsPeriodically();
    }

    private void detectFailedProcessesPeriodically() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                detectProcessFailures();
            }
        }, FAILURE_DETECTION_INTERVAL, FAILURE_DETECTION_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void detectProcessFailures() {
        // If a member's last heard heart beat is older than
        // CurrentTime - TIMEOUT, declare it failed and remove it from members
        for(Map.Entry<String, LinkedHashMap<Long, Integer>> memberProcess : members.entrySet()) {
            Set<Map.Entry<Long, Integer>> heartBeatSet = memberProcess.getValue().entrySet();
            int len = heartBeatSet.size();
            Map.Entry<Long, Integer>[] heartBeatEntries = new Map.Entry[len];
            heartBeatSet.toArray(heartBeatEntries);
            Long lastRecvdTimestamp = heartBeatEntries[len - 1].getKey();
            if (lastRecvdTimestamp + TIMEOUT < System.currentTimeMillis()) {
                // failed process. remove it
                members.remove(memberProcess.getKey());
            }
        }
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
    }

    public void printProcessHeartBeatsPeriodically() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                printProcessHeartBeats();
            }
        }, HEARTBEAT_PRINT_INTERVAL, HEARTBEAT_PRINT_INTERVAL, TimeUnit.MILLISECONDS);
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
