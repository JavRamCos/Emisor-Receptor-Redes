package lib;

import java.util.ArrayList;
import java.util.Objects;
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
            }
            return true;
        } catch(Exception e) {
            this.err_msg = "Element in received trama is not of type Integer ...";
            return false;
        }
    }

    public boolean checkErrors() {
        // CRC-32
        int indx = 0, counter = this.poly.size();
        List<Integer> temp = new ArrayList<>();
        while(indx < this.og_trama.size()) {
            for(int x = 0; x < counter; x++) {
                temp.add(this.og_trama.get(indx));
                indx++;
            }
            temp = new ArrayList<>(temp.subList(temp.size()-this.poly.size(), temp.size()));
            xor(temp);
            if(temp.contains(1)) {
                counter = temp.indexOf(1);
            } else {
                if(new ArrayList<>(this.og_trama.subList(indx, this.og_trama.size())).contains(1)) {
                    counter = new ArrayList<>(this.og_trama.subList(indx, this.og_trama.size())).indexOf(1)
                            + this.poly.size();
                    if(counter + indx >= this.poly.size()) counter = this.og_trama.size() - indx;
                } else {
                    counter = this.og_trama.size() - indx;
                }
            }
        }
        temp = new ArrayList<>(temp.subList(temp.size()-this.bit_len, temp.size()));
        this.og_trama = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size() - this.bit_len));
        this.og_trama.addAll(temp);
        return !temp.contains(1);
    }

    public void xor(List<Integer> list1) {
        for(int i = 0; i < list1.size(); i++) {
            list1.set(i, Objects.equals(list1.get(i), this.poly.get(i)) ? 0 : 1);
        }
    }

    public void printError() {
        System.out.println("> Error: "+this.err_msg);
    }

    public void printTramas() {
        System.out.println("\nReceived trama: "+this.rec_trama);
        System.out.println("Converted trama: "+this.og_trama);
    }
}
