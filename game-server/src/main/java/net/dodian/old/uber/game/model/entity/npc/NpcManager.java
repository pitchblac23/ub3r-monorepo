/**
 * 
 */
package net.dodian.old.uber.game.model.entity.npc;

/**
 * @author Owner
 *
 */

import net.dodian.old.jobs.JobScheduler;
import net.dodian.old.jobs.impl.NpcProcessor;
import net.dodian.old.uber.game.model.Position;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.player.packets.outgoing.SendMessage;
import net.dodian.old.utilities.Database;

import org.quartz.SchedulerException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcManager extends Thread {
  Map<Integer, Npc> npcs = new HashMap<Integer, Npc>();
  Map<Integer, NpcData> data = new HashMap<Integer, NpcData>();
  int nextIndex = 1;

  public void run() {
    loadData();
    loadSpawns();
    try {
      JobScheduler.ScheduleStaticRepeatForeverJob(600, NpcProcessor.class);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public Collection<Npc> getNpcs() {
    return npcs.values();
  }

  public void loadSpawns() {
    try {
      int amount = 0;
      ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_spawn");
      while (results.next()) {
        amount++;
        createNpc(results.getInt("id"), new Position(results.getInt("x"), results.getInt("y"), results.getInt("height")), results.getInt("face"));

      }
      System.out.println("Loaded " + amount + " Npc Spawns");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
	
	public void reloadDrops(Client c, int id) {
		try {
			if (data.containsKey(id)) {
				data.get(id).getDrops().clear();
				ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_drops where npcid='"+id+"'");
				while (results.next()) {
					data.get(id).addDrop(results.getInt("itemid"), results.getInt("amt_min"), results.getInt("amt_max"),
			        results.getDouble("percent"), results.getBoolean("rareShout"));
				}
				c.send(new SendMessage("Finished reloading all drops for " + data.get(id).getName()));
			} else 
				c.send(new SendMessage("No npc with id of " + id));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void reloadAllData(Client c, int id) {
    try {
      Statement statement = Database.statement;
      ResultSet results = statement.executeQuery("SELECT * FROM uber3_npcs where id='" + id + "'");
      if (results.next()) {
        data.replace(results.getInt("id"), new NpcData(results));
        /* Reload all npc data! */
        for (Npc n : npcs.values())
          if (n.getId() == id)
            n.reloadData();
        }
        reloadDrops(c, id); //Need to set drops!
        c.send(new SendMessage("Finished updating all '"+getData(id).getName()+"' npcs!"));
      } catch (Exception e) {
      e.printStackTrace();
      }
    }

	public void reloadNpcConfig(Client c, int id, String table, String value) {
        if (!data.containsKey(id)) {
            try {
                Statement statement = Database.statement;
                statement.executeUpdate("INSERT INTO uber3_npcs SET id = " + id + "");
                ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_npcs where id='"+id+"'");
                if (results.next()) {
                    data.put(results.getInt("id"), new NpcData(results));
                    c.send(new SendMessage("Added default config values to the npc!"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!table.equalsIgnoreCase("new npc")) {
          try {
            Statement statement = Database.statement;
            String query = "UPDATE uber3_npcs SET " + table + " = '" + value + "' WHERE id = '" + id + "'";
            statement.executeUpdate(query);
            c.send(new SendMessage("You updated '"+table+"' with value '"+value+"'!"));
            reloadAllData(c, id);
          } catch (Exception e) {
            //System.out.println("test: " + e.getLocalizedMessage() + ", " + e.getMessage());
            if(e.getMessage().contains("Unknown column"))
              c.send(new SendMessage("row name '" + table + "' do not exist in the database!"));
            else if (e.getMessage().contains("Incorrect integer"))
              c.send(new SendMessage("row name '"+table+"' need a int value!"));
            else
              e.printStackTrace();
          }
        }
    }
  
  public void loadData() {
    try {
      int amount = 0;
      ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_npcs");
      while (results.next()) {
        amount++;
        data.put(results.getInt("id"), new NpcData(results));
      }
      System.out.println("Loaded " + amount + " Npc Definitions");
      amount = 0;
      results = Database.statement.executeQuery("SELECT * FROM uber3_drops");
      while (results.next()) {
        if (results.getInt("npcid") > 0) {
          amount++;
          int id = results.getInt("npcid");
          if (data.containsKey(id)) {
				data.get(id).addDrop(results.getInt("itemid"), results.getInt("amt_min"), results.getInt("amt_max"),
				results.getDouble("percent"), results.getBoolean("rareShout"));
          } else
        	  System.out.println("Invalid key: " + results.getInt("npcid"));
        }
      }
      System.out.println("Loaded " + amount + " Npc Drops");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createNpc(int id, Position position, int face) {
    npcs.put(nextIndex, new Npc(nextIndex, id, position, face));
    nextIndex++;
  }

  public Npc getNpc(int index) {
    if (index > 0 && index < nextIndex && npcs.get(index) != null) {
      return npcs.get(index);
    } else {
      return null;
    }
  }

  public String getName(int id) {
    return data.get(id) == null ? "NO NPC NAME YET!" : data.get(id).getName();
  }

  public NpcData getData(int id) {
    return data.get(id);
  }
}