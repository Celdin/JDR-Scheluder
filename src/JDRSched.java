import bot.Bot;
import driver.PostgreSQLDriver;

public class JDRSched {
    public static void main(String[] args) throws Exception{
        if(args.length > 0) {
        	if(args.length > 1 ) {
	        	PostgreSQLDriver.initialise(args[1], args[2], args[3]);
	        	PostgreSQLDriver.ckeckDatabase();
	            new Bot(args[0]);
        	} else {
	        	PostgreSQLDriver.initialise(null, null, null);
	        	PostgreSQLDriver.ckeckDatabase();
	            new Bot(args[0]);
        	}
        } else {
            System.out.println("Vieulliez indiquer le token du bot");
        }
    }
}
