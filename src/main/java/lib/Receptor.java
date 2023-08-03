package lib;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

public class Receptor {
    int bit_len;
    List<String> rec_trama;
    List<String> rec_par;
    List<Integer> og_trama;
    List<Integer> poly;
    List<Integer> hammng;
    String err_msg;
    int RECURSION_LIMIT;

    public Receptor(String trama, String parity) {
        this.rec_trama = Arrays.asList(trama.split(""));
        this.rec_par = Arrays.asList(parity.split(""));
        this.RECURSION_LIMIT = 100;
        this.err_msg = "";
        /* IEEE 802 CRC-32 */
        this.poly = Arrays.asList(1, 0, 0, 1);
        /*this.poly = Arrays.asList(1, 0, 0, 0, 0, 0, 1, 0,
                                    0, 1, 1, 0, 0, 0, 0, 0,
                                    1, 0, 0, 0, 1, 1, 1, 0,
                                    1, 1, 0, 1, 1, 0, 1, 1, 1);*/
        this.bit_len = this.poly.size()-1;
    }

    public boolean convertInput() {
        try {
            this.og_trama = this.rec_trama
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            this.hammng = this.rec_par
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if(this.og_trama.size() < this.poly.size()) {
                this.err_msg = "Original trama is shorter than polynomial length ...";
                return false;
            }
            return true;
        } catch(Exception e) {
            this.err_msg = "Element in received trama or parity is not of type Integer ...";
            return false;
        }
    }

    public int checkTrama() {
        // CRC-32
        int indx = 0, offset = this.poly.size(), counter = 0;
        List<Integer> temp = new ArrayList<>();
        while(indx < this.og_trama.size()) {
            counter = offset == 0 ? 0 : counter + 1;
            if(counter == this.RECURSION_LIMIT) break;
            for(int x = 0; x < offset; x++) {
                temp.add(this.og_trama.get(indx));
                indx++;
            }
            temp = new ArrayList<>(temp.subList(temp.size()-this.poly.size(), temp.size()));
            xor(temp);
            if(temp.contains(1)) {
                offset = temp.indexOf(1);
                if(indx + offset > this.og_trama.size()) offset = this.og_trama.size() - indx;
            } else {
                if(new ArrayList<>(this.og_trama.subList(indx, this.og_trama.size())).contains(1)) {
                    offset = new ArrayList<>(this.og_trama.subList(indx, this.og_trama.size())).indexOf(1)
                            + this.poly.size();
                    if(offset + indx >= this.poly.size()) offset = this.og_trama.size() - indx;
                } else {
                    offset = this.og_trama.size() - indx;
                }
            }

        }
        temp = new ArrayList<>(temp.subList(temp.size()-this.bit_len, temp.size()));
        this.og_trama = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size() - this.bit_len));
        this.og_trama.addAll(temp);
        if ( counter == this.RECURSION_LIMIT) {
            this.err_msg = "Max recursion reached ("+this.RECURSION_LIMIT+")";
            return -1;
        }
        if(temp.contains(1)) {
            this.err_msg = "Error detected on converted trama, correction will be applied ...";
            return 0;
        }
        return 1;
    }

    public void xor(List<Integer> list1) {
        for(int i = 0; i < list1.size(); i++) {
            list1.set(i, Objects.equals(list1.get(i), this.poly.get(i)) ? 0 : 1);
        }
    }

    public void fixTrama() {
        // HAMMING ALGORITHM
    }

    public void printError() {
        System.out.println("> Error: "+this.err_msg);
    }

    public void printTramas() {
        System.out.println("\nReceived trama: "+this.rec_trama);
        System.out.println("Converted trama: "+this.og_trama);
    }
}
