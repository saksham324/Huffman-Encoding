import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<Data>> {
        public int compare(BinaryTree<Data> b1, BinaryTree<Data> b2) {
            if (b1.getData().getFreq() < b2.getData().getFreq()) return -1;
            else if (b1.getData().getFreq() > b2.getData().getFreq()) return 1;
            else return 0;
        }
}

