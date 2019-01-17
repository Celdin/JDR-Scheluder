package message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {
	START(Statics.START_CMD + "START", ""),
	COOKER(Statics.START_CMD + "COOKERS", ""),
	STOP(Statics.START_CMD + "START", "");
	
	String text;
	String paramsRegex;
	
	public static Command findByCommand(String commandTxt) {
		for(Command command : values()) {
			if(command.getText().equals(commandTxt)) {
				return command;
			}
		}
		return null;
	}
}
