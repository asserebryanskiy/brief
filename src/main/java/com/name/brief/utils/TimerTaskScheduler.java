package com.name.brief.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class TimerTaskScheduler {

    private final TaskScheduler scheduler;
    private final SimpMessagingTemplate template;


    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @Autowired
    public TimerTaskScheduler(TaskScheduler scheduler,
                              SimpMessagingTemplate template) {
        this.scheduler = scheduler;
        this.template = template;
    }

    public void setUpTimer(Long gameId, Duration duration) {
        // if timer for this game is already running clean it up and then create new
        ScheduledFuture<?> running = scheduledTasks.get(gameId);
        if (running != null && !running.isCancelled()) {
            running.cancel(true);
        }

        Runnable task = new Runnable() {
            private long counter = duration.getSeconds();

            @Override
            public void run() {
                counter--;
                ScheduledFuture<?> future = scheduledTasks.get(gameId);
                template.convertAndSend("/topic/game/" + gameId + "/timer", counter);
                if (counter == 0) future.cancel(true);
            }
        };
        ScheduledFuture future = scheduler.scheduleAtFixedRate(task, 1000L);
        scheduledTasks.put(gameId, future);
    }

    public void stopTimer(Long gameId) {
        ScheduledFuture<?> task = scheduledTasks.get(gameId);
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }
}
