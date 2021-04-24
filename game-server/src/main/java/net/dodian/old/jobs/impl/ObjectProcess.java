package net.dodian.old.jobs.impl;

import net.dodian.old.uber.game.Constants;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.entity.player.PlayerHandler;
import net.dodian.old.uber.game.model.object.GlobalObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution

public class ObjectProcess implements Job {
  public void execute(JobExecutionContext context) throws JobExecutionException {
    for (int i = 0; i < Constants.maxPlayers; i++) {
      Client c = ((Client) (PlayerHandler.players[i]));
      if(c != null)
        GlobalObject.updateObject(c);
    }
  }

}