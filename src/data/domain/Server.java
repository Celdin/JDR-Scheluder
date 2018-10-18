package data.domain;

import java.util.Map;

import lombok.Data;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;

@Data
public class Server {
	private Map<Guild, Map<Channel, Event>> events; 
}
