import lib.Receptor;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("> No trama and/or parity was provided ...");
            System.exit(1);
        }
        Receptor receptor = new Receptor(args[0], args[1]);
        int R_LIMIT = 100, counter = 0;
        if(receptor.convertInput()) {
            while(true) {
                if(counter >= R_LIMIT) break;
                int check_result = receptor.checkTrama();
                if(check_result < 0) {
                    receptor.printError();
                    break;
                } else if(check_result == 1) {
                    System.out.println("> Trama converted succesfully");
                    break;
                }
                System.out.println("> Applying correction ...");
                int fix_result = receptor.fixTrama();
                if(fix_result == 0) {
                    receptor.printError();
                    break;
                }
                counter++;
            }
            receptor.printTramas();
        } else {
            receptor.printError();
        }
        System.exit(0);
    }
}
