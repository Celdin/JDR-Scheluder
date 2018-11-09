package data.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@Data
public class Event {
	private Map<User, Integer> haveCooked = new HashMap<>();
	private Long nextDate = 1540400400000L;
	private Message annonceDate;
	private Message annonceCooker;
}
