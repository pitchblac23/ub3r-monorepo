package net.dodian.old.uber.game.model.item;

import net.dodian.old.uber.game.Server;
import net.dodian.old.uber.game.model.Position;
import net.dodian.old.uber.game.model.player.packets.outgoing.CreateGroundItem;

public class GroundItem {
  public int x, y, id, amount, dropper, playerId = -1, npcId = -1;
  public long dropped = 0;
  public boolean visible = false, npc = false;
  public boolean taken = false, canDespawn = true;
  public int timeDespawn = 60000, timeDisplay = 60000;

  public GroundItem(int x, int y, int id, int amount, int dropper, int npcId) {
    this.x = x;
    this.y = y;
    this.id = id;
    this.amount = amount;
    this.dropper = dropper;
    this.npc = npcId >= 0 ? true : false;
    if (npc)
    	this.npcId = npcId;
    this.canDespawn = true;
    dropped = System.currentTimeMillis();
    if (dropper > 0 && Server.playerHandler.validClient(dropper)) {
      Server.playerHandler.getClient(dropper).send(new CreateGroundItem(new GameItem(id, amount), new Position(x, y)));
      playerId = Server.playerHandler.getClient(dropper).dbId;
    } 
  }
  
	public GroundItem(int x, int y, int id, int amount, int display) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.amount = amount;
		this.canDespawn = false;
		this.timeDisplay = display;
		dropped = System.currentTimeMillis();
	}

	public void setTaken(boolean b) {
		this.taken = b;
	}

	public boolean isTaken() {
		return taken;
	}

}
