package novite.rs.game.player.content;

import java.util.ArrayList;
import java.util.Iterator;

import novite.rs.game.ForceTalk;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.impl.Wilderness;

public class TicketSystem {

	public static final ArrayList<TicketEntry> tickets = new ArrayList<TicketEntry>();

	public static boolean canSubmitTicket() {
		filterTickets();
		return true;
	}

	public static void filterTickets() {
		for (Iterator<TicketEntry> it = tickets.iterator(); it.hasNext();) {
			TicketEntry entry = it.next();
			if (entry.player.hasFinished()) {
				it.remove();
			}
		}
	}

	public static int getTicketsSize() {
		filterTickets();
		return tickets.size();
	}

	public static void removeTicket(Player player) {
		Object att = player.getTemporaryAttributtes().get("ticketTarget");
		if (att == null) {
			return;
		}
		TicketEntry ticket = (TicketEntry) att;
		Player target = ticket.getPlayer();
		target.setNextWorldTile(ticket.getTile());
		target.getTemporaryAttributtes().remove("ticketRequest");
		player.getTemporaryAttributtes().remove("ticketTarget");
	}

	public static void answerTicket(Player player) {
		removeTicket(player);
		filterTickets();
		if (tickets.isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "There are no available tickets to handle.");
			return;
		} else if (player.getTemporaryAttributtes().get("ticketTarget") != null) {
			removeTicket(player);
		}
		while (tickets.size() > 0) {
			TicketEntry ticket = tickets.get(0);// next in line
			Player target = ticket.player;
			if (target == null) {
				continue; // shouldn't happen but k
			}
			if (target.getInterfaceManager().containsChatBoxInter() || target.getControllerManager().getController() != null || target.getInterfaceManager().containsInventoryInter() || target.getInterfaceManager().containsScreenInterface()) {
				tickets.remove(0);
				continue;
			}
			player.getTemporaryAttributtes().put("ticketTarget", ticket);
			target.getTemporaryAttributtes().remove("ticketRequest");
			player.setNextWorldTile(target);
			tickets.remove(ticket);
			player.setNextForceTalk(new ForceTalk("Hello, how may I help you today?"));
			break;
		}
	}

	public static void requestTicket(Player player) {
		if (!canSubmitTicket() || player.getTemporaryAttributtes().get("ticketRequest") != null || player.getControllerManager().getController() != null || Wilderness.isAtWild(player)) {
			player.getPackets().sendGameMessage("You have already submitted a help request!");
			return;
		}
		player.sendMessage("Your help request has been submitted. Please wait for it to be answered.");
		player.getTemporaryAttributtes().put("ticketRequest", true);
		tickets.add(new TicketEntry(player));
		for (Player mod : World.getPlayers()) {
			if (mod == null || mod.hasFinished() || !mod.hasStarted() || (mod.getRights() < 1 && !mod.isSupporter())) {
				continue;
			}
			mod.getPackets().sendGameMessage("A ticket has been submitted by " + player.getDisplayName() + "! ::ticket to solve it!");
			mod.getPackets().sendGameMessage("There is currently " + tickets.size() + " tickets active.");
		}
	}

	public static class TicketEntry {
		private Player player;
		private WorldTile tile;

		public TicketEntry(Player player) {
			this.player = player;
			this.tile = player;
		}

		public Player getPlayer() {
			return player;
		}

		public WorldTile getTile() {
			return tile;
		}
	}
}
