package bot.eventListener;

import java.util.Map;

import data.domain.Event;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JDRSchedListener extends ListenerAdapter {
	private Map<Guild, Map<Channel, Event>> datas;
	
	public JDRSchedListener(Map<Guild, Map<Channel, Event>> datas) {
		this.datas = datas;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		super.onMessageReceived(event);
	}
	
	@Override
	public void onGenericMessageReaction(GenericMessageReactionEvent event) {
		super.onGenericMessageReaction(event);
	}
}
