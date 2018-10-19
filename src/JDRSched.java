import bot.Bot;

public class JDRSched {
    public static void main(String[] args) throws Exception{
        if(args.length > 0) {
            new Bot(args[0]);
        } else {
            System.out.println("Vieulliez indiquer le token du bot");
        }
    }
}
