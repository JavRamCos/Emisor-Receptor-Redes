package lib;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

public class Receptor {
    int bit_len;
    List<String> rec_trama;
    List<Integer> og_trama;
    List<Integer> poly;
    String err_msg;

    public Receptor(String trama, int bit_len) {
        this.err_msg = "";
        this.rec_trama = Arrays.asList(trama.split(""));
        this.bit_len = bit_len;
        /* IEEE 802 CRC-32 */
        this.poly = Arrays.asList(1, 0, 0, 0, 0, 0, 1, 0,
                                    0, 1, 1, 0, 0, 0, 0, 0,
                                    1, 0, 0, 0, 1, 1, 1, 0,
                                    1, 1, 0, 1, 1, 0, 1, 1, 1);
    }

    public boolean convertOriginalTrama() {
        if(this.poly.size() != this.bit_len+1) {
            this.err_msg = "Polynomial does not match CRC bit length ...";
            return false;
        }
        try {
            this.og_trama = this.rec_trama
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if(this.og_trama.size() < this.poly.size()) {
                this.err_msg = "Original trama is shorter than polynomial length ...";
                return false;
            } else {
                return true;
            }
        } catch(Exception e) {
            this.err_msg = "Element in received trama is not of type Integer ...";
            return false;
        }
    }

    public boolean checkErrors() {
        // CRC-32
        return true;
    }

    public void printError() {
        System.out.println("> Error: "+this.err_msg);
    }

    public void printTramas() {
        System.out.println("\nReceived trama: "+this.rec_trama);
        System.out.println("Converted trama: "+this.og_trama);
    }
}
