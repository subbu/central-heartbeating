package com.subbu.distsys.heartbeating.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process {
    private String id;
    private int heartBeatCounter = 0;
    ScheduledExecutorService scheduledExecutorService;

    private static Logger logger = LoggerFactory.getLogger(Process.class);

    public Process(String processId, String leaderId) {
        this.id = processId;

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
        }, 5, 5, TimeUnit.SECONDS);
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

    public static void main(String[] args) {
        String processId = args[0];
        String leaderID = args[1];
        new Process(processId, leaderID);
    }
}
