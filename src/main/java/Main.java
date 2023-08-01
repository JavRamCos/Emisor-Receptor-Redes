import lib.Receptor;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            print("No trama was provided ...");
            System.exit(1);
        }
        Receptor receptor = new Receptor(args[0], 32);
        if(receptor.convertOriginalTrama()) {
            print("Success");
        } else {
            print("Error converting original trama ...");
            System.exit(1);
        }
        System.exit(0);
    }

    public static void print(String msg) {
        System.out.println(msg);
    }
}
