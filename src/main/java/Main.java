import lib.Receptor;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("> No trama and/or parity was provided ...");
            System.exit(1);
        }
        Receptor receptor = new Receptor(args[0], args[1]);
        if(receptor.convertInput()) {
            switch(receptor.checkTrama()) {
                case -1:
                    receptor.printError();
                    break;
                case 0:
                    receptor.printError();
                    System.out.println("> Applying correction ...");
                    if(receptor.fixTrama()) {
                        System.out.println("> Converting trama again ...");
                        switch(receptor.checkTrama()) {
                            case 1:
                                System.out.println("> Trama converted succesfully");
                                break;
                            default:
                                receptor.printError();
                                break;
                        }
                    } else {
                        receptor.printError();
                    }
                    break;
                default:
                    System.out.println("> Trama converted succesfully");
                    break;
            }
            receptor.printTramas();
        } else {
            receptor.printError();
        }
        System.exit(0);
    }
}
