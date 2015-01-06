package com.subbu.distsys.heartbeating.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process {
    private String id;
    private String group;
    private int heartBeatCounter = 0;
    private boolean simulateHeartBeatDelay = true;
    private static final int HEART_BEAT_INTERVAL = 5000;
    ScheduledExecutorService scheduledExecutorService;

    private static Logger logger = LoggerFactory.getLogger(Process.class);

    public Process(String processId, String leaderId, String processGroup) {
        this.id = processId;
        this.group = processGroup;

        if (processId.equals(leaderId)) {
            // I am the leader
            LeaderBike.setLeader(new Leader(this));
        } else {
            // I am normal process
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleHeartBeatSender();
    }

    public int getHeartBeatCounter() {
        return heartBeatCounter;
    }

    private void scheduleHeartBeatSender() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sendHeartBeat();
            }
        }, heartBeatSendInterval(), heartBeatSendInterval(), TimeUnit.MILLISECONDS);
    }

    public String getId() {
        return id;
    }

    public int incrementHeartBeat() {
        return heartBeatCounter++;
    }

    private void sendHeartBeat() {
        incrementHeartBeat();
        //logger.debug(">> sending {}'s heart beat {}", id, heartBeatCounter);
        LeaderBike.sendHeartBeat(this);
    }

    private int heartBeatSendInterval() {
        if (simulateHeartBeatDelay) {
            // return random int
        } else {
            // send fixed delay
        }
        return HEART_BEAT_INTERVAL;
    }

    public static void main(String[] args) {
        String processId = args[0];
        String leaderID = args[1];
        String processGroup = args[2];
        new Process(processId, leaderID, processGroup);
    }
}
