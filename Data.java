import java.util.Comparator;

public class Data implements Comparable<Data>{
    private Character ch;
    private Integer freq;

    public Data(Character ch, Integer freq){
        this.ch = ch;
        this.freq = freq;
    }

    public Data(Integer freq){
        this.freq = freq;
    }

    public Character getChar(){
        return ch;
    }

    public Integer getFreq(){
        return freq;
    }

    @Override
    public int compareTo(Data d2) {
        return freq.compareTo(d2.freq);
    }

    @Override
    public String toString(){
        return ch + " : " + freq;
    }
}
