package lib;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Receptor {
    int bit_len;
    List<String> rec_trama;
    List<Integer> og_trama;
    List<Integer> poly;
    String err_msg;
    int RECURSION_LIMIT;

    public Receptor(String trama) {
        this.rec_trama = Arrays.asList(trama.split(""));
        this.RECURSION_LIMIT = 100;
        this.err_msg = "";
        /* IEEE 802 CRC-32 */
        this.poly = Arrays.asList(1, 0, 0, 0, 0, 0, 1, 1, 1);
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
        boolean flag = true;
        while(indx < this.og_trama.size()) {
            flag = indx == 0;
            counter = offset == 0 ? 0 : counter + 1;
            if(counter == this.RECURSION_LIMIT) break;
            for(int x = 0; x < offset; x++) {
                temp.add(this.og_trama.get(indx));
                indx++;
            }
            temp = new ArrayList<>(temp.subList(temp.size()-this.poly.size(), temp.size()));
            xor(temp, flag);
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
        if ( counter == this.RECURSION_LIMIT) {
            this.err_msg = "Max recursion reached ("+this.RECURSION_LIMIT+")";
            return -1;
        }
        if(temp.contains(1)) {
            this.err_msg = "Trama has a mismatch ...";
            return 0;
        }
        this.og_trama = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size() - this.bit_len));
        this.og_trama.addAll(temp);
        return 1;
    }

    public void xor(List<Integer> list1, boolean flag) {
        if (list1.get(0) == 1 || flag) {
            for (int i = 0; i < list1.size(); i++) {
                list1.set(i, Objects.equals(list1.get(i), this.poly.get(i)) ? 0 : 1);
            }
        } else {
            List<Integer> list2 = Collections.nCopies(this.poly.size(), 0);
            for (int i = 0; i < list1.size(); i++) {
                list1.set(i, Objects.equals(list1.get(i), list2.get(i)) ? 0 : 1);
            }
        }
    }

    public int fixTrama(String parity) {
        // HAMMING ALGORITHM
        List<Integer> hammng = Arrays.stream(parity.split(""))
                .map(Integer::parseInt)
                .toList();
        int p = hammng.size(); // Exponent
        int n = p + new ArrayList<>(this.og_trama.subList(0, this.og_trama.size()-this.bit_len)).size(); // p + bits
        List<List<Integer>> t_table = genTruthTable(p, n);
        List<Integer> bits = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size()-this.bit_len));
        bits.add(bits.size(), 0);
        for(int i = 0; i < hammng.size(); i++) {
            int num = (n+1)-((int)Math.pow(2, p-(i+1)))-1;
            bits.add(num, hammng.get(i));
        }
        List<Integer> sums = new ArrayList<>(Collections.nCopies(hammng.size(), 0));
        for(int i = 0; i < t_table.size(); i++) {
            List<Integer> temp = t_table.get(i);
            int counter = 0;
            for(int tmp : temp) {
                counter += bits.get(bits.size()-(tmp+1));
            }
            sums.set(i, counter % 2 == 0 ? 0 : 1);
        }
        int indx = binToDec(sums);
        if (indx == 0) {
            this.err_msg = "Failed to find error bit index ...";
            return 0;
        }
        System.out.println("> Error detected in position ("+indx+")");
        bits = new ArrayList<>(bits.subList(bits.size()-(indx+1), bits.size()-1));
        p = 1;
        while(true) {
            int num = (int)Math.pow(2, p);
            if(num > bits.size()) break;
            p++;
        }
        List<Integer> trama = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size()-this.bit_len));
        trama.set(trama.size()-(bits.size()-p), bits.get(0) == 0 ? 1 : 0);
        trama.addAll(this.og_trama.subList(this.og_trama.size()-this.bit_len, this.og_trama.size()));
        this.og_trama = trama;
        return 1;
    }

    public List<List<Integer>> genTruthTable(int rows, int max) {
        List<List<Integer>> list = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            List<Integer> temp = new ArrayList<>(Collections.nCopies((int) Math.pow(2, i), 0));
            temp.addAll(Collections.nCopies((int) Math.pow(2, i), 1));
            List<Integer> f_list = new ArrayList<>(temp);
            for(int j = 0; j < (int)Math.pow(2, rows)/temp.size()-1; j++) f_list.addAll(temp);
            f_list = new ArrayList<>(f_list.subList(0, max+1));
            List<Integer> indices = new ArrayList<>();
            for(int j = 0; j < f_list.size(); j++) {
                if(f_list.get(j) == 1) indices.add(j);
            }
            list.add(indices);
        }
        return list;
    }

    public int binToDec(List<Integer> list) {
        int result = 0;
        for(int i = 0; i < list.size(); i++) {
            result += list.get(i) == 0 ? 0 : (int)Math.pow(2, i);
        }
        return result;
    }

    public void printError() {
        System.out.println("> Error: "+this.err_msg);
    }

    public void printTramas() {
        System.out.println("\nReceived trama: "+this.rec_trama);
        System.out.println("Converted trama: "+this.og_trama);
    }
}
