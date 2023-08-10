package lib;

import java.util.*;
import java.util.stream.Collectors;

public class Receptor {
    int bit_len;
    List<String> rec_trama;
    List<Integer> og_trama, poly;
    String err_msg;
    boolean full_mode;

    public Receptor(boolean full_mode) {
        this.err_msg = "";
        /* IEEE 802 CRC-32 */
        this.poly = Arrays.asList(1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1);
        /*this.poly = Arrays.asList(1, 0, 0, 0, 0, 0, 1, 0,
                                    0, 1, 1, 0, 0, 0, 0, 0,
                                    1, 0, 0, 0, 1, 1, 1, 0,
                                    1, 1, 0, 1, 1, 0, 1, 1, 1);*/
        this.bit_len = this.poly.size() - 1;
        this.full_mode = full_mode;
    }

    // Function to reset the current trama with (trama) provided
    public boolean setTrama(String trama) {
        this.rec_trama = Arrays.asList(trama.split(""));
        try {
            this.og_trama = this.rec_trama
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if(this.og_trama.size() < this.poly.size() && this.full_mode) {
                this.err_msg = "Original trama is shorter than polynomial length ...";
                return false;
            }
        } catch(Exception e) {
            this.err_msg = "Element in received trama or parity is not of type Integer ...";
            return false;
        }
        return true;
    }

    // Implementation of CRC algorithm for a (og_trama) trama & with (poly) polynomial
    public int checkTrama() {
        int r_limit = 100;
        int indx = 0, offset = this.poly.size(), counter = 0;
        List<Integer> temp = new ArrayList<>();
        boolean flag;
        while(indx < this.og_trama.size()) {
            flag = indx == 0;
            counter = offset == 0 ? 0 : counter + 1;
            if(counter == r_limit) break;
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
        if ( counter == r_limit) {
            this.err_msg = "Max recursion reached ("+r_limit+")";
            return -1;
        }
        if(temp.contains(1)) {
            this.err_msg = "Trama has a mismatch ...";
            return 0;
        }
        this.og_trama = new ArrayList<>(this.og_trama.subList(0, this.og_trama.size() - this.bit_len));
        return 1;
    }

    // Function for XOR operation between (list1) && (og_trama) || (list1) && [0, 0, 0, ... 0]
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

    // Implementation of Hamming Error Correction Algorithm, with
    // 2^(parity.size()) >= (parity.size()) + (og_trama.size()) + 1
    public int fixTrama(String parity) {
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

    // Function to generate a truth table up to 2^(rows) & with (max) entries each
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

    // Implementation of Hamming 7 4 Error Correction Algorithm for a MOD(7) list
    public int fixTrama() {
        List<Integer> temp;
        if(this.og_trama.size() % 7 != 0) {
            int spaces = ((int)Math.ceil((double)this.og_trama.size()/7)*7) - this.og_trama.size();
            temp = new ArrayList<>(Collections.nCopies(spaces, 0));
            temp.addAll(this.og_trama);
            this.og_trama = temp;
        }
        int indx = 0, pad = 7;
        List<Integer> result = new ArrayList<>();
        boolean flag = false;
        while(indx < this.og_trama.size()) {
            List<Integer> block = new ArrayList<>(this.og_trama.subList(indx, indx+pad));
            temp = parse74Block(block);
            int bit_pos = binToDec(temp);
            if(binToDec(temp) == 0) {
                result.addAll(Arrays.asList(block.get(0), block.get(1),
                        block.get(2), block.get(4)));
            } else {
                for(int i = 0; i < 5; i++) {
                    block.set(7 - bit_pos, block.get(7 - bit_pos) == 0 ? 1 : 0);
                    temp = parse74Block(block);
                    bit_pos = binToDec(temp);
                    if(bit_pos == 0) {
                        result.addAll(Arrays.asList(block.get(0), block.get(1),
                                block.get(2), block.get(4)));
                        break;
                    }
                    if(i == 4) flag = true;
                }
                if(flag) break;
            }
            indx += pad;
        }
        if(flag) return 0;
        this.og_trama = result;
        return 1;
    }

    // Function to calculate bit parity in a block on the Hamming 7 4 Algorithm
    public List<Integer> parse74Block(List<Integer> block) {
        int p1 = (block.get(0)+block.get(2)+block.get(4)+block.get(6)) % 2;
        int p2 = (block.get(0)+block.get(1)+block.get(4)+block.get(5)) % 2;
        int p4 = (block.get(0)+block.get(1)+block.get(2)+block.get(3)) % 2;
        return new ArrayList<>(Arrays.asList(p1, p2, p4));
    }

    // Function used to convert an Integer array [0, 1] into a decimal number
    public int binToDec(List<Integer> list) {
        int result = 0;
        for(int i = 0; i < list.size(); i++) {
            result += list.get(i) == 0 ? 0 : (int)Math.pow(2, i);
        }
        return result;
    }

    // Function to convert a trama into a String
    public String binToString() {
        List<Integer> temp;
        if(this.og_trama.size() % 8 != 0) {
            int spaces = ((int)Math.ceil((double)this.og_trama.size()/8)) - this.og_trama.size();
            temp = new ArrayList<>(Collections.nCopies(spaces, 0));
            temp.addAll(this.og_trama);
            this.og_trama = temp;
        }
        StringBuilder result = new StringBuilder();
        int indx = 0, pad = 8;
        while(indx < this.og_trama.size()) {
            temp = new ArrayList<>(this.og_trama.subList(indx, indx+pad));
            StringBuilder block = new StringBuilder();
            for(int x = 0; x < 8; x++) {
                block.append(temp.get(x));
            }
            result.append((char) Integer.parseInt(block.toString(), 2));
            indx += pad;
        }
        return result.toString();
    }

    // Show error produced during one of the validations
    public void printError() {
        System.out.println("> Error: "+this.err_msg);
    }

    // Show trama received and final trama
    public void printTramas() {
        System.out.println("\nReceived trama: "+this.rec_trama);
        System.out.println("Converted trama: "+this.og_trama);
    }
}
