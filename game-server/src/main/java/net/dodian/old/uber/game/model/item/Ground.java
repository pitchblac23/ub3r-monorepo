package net.dodian.old.uber.game.model.item;

import net.dodian.old.uber.game.Server;
import net.dodian.old.uber.game.model.entity.player.Client;
import net.dodian.old.uber.game.model.entity.player.PlayerHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class Ground {
  public static CopyOnWriteArrayList<GroundItem> items = new CopyOnWriteArrayList<GroundItem>();
	public static void deleteItem(GroundItem item) {
		item.setTaken(true);
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			Client p = Server.playerHandler.getClient(i);
			if(p == null || !p.isActive || p.outputStream == null) { //Incase!
			} else if (!item.canDespawn) {
				p.removeGroundItem(item.x, item.y, item.id);
				item.dropped = System.currentTimeMillis();
			} else if (Server.itemManager.isTradable(item.id) || (p.dbId == item.playerId && !Server.itemManager.isTradable(item.id))) {
				p.removeGroundItem(item.x, item.y, item.id);
			}
		}
		if (item.canDespawn)
			items.remove(item);
	}
}