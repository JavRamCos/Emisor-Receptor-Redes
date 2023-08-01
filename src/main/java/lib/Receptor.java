package lib;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Receptor {
    int bit_len;
    List<String> og_trama;
    List<Integer> final_trama;
    public Receptor(String trama, int bit_len) {
        this.og_trama = Arrays.asList(trama.split(""));
        this.bit_len = bit_len;
    }

    public boolean convertOriginalTrama() {
        try {
            this.final_trama = new ArrayList<Integer>(this.og_trama
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList()));
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
