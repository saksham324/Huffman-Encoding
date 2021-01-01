/*
CS 10 Winter 2020, Tim Pierson
Problem Set 3 Submission, Huffman Encoding
@author : Saksham Arora
@author : Egemen Sahin
Separate code
 */



import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class Encoding {
    private static String pathName;                                             // pathname instance variable
    private static Map<Character, String> encodedMap = new TreeMap<Character, String>();        // encodedMap to be used for compression and decompression
    private static BinaryTree<Data> codeTree;                                   // codeTree created for code retrieval
    private static BufferedReader input;                                        // input file to be compressed
    private static BufferedBitWriter output;                                    // compressed output file
    private static BufferedBitReader compressedInput;                           // compressed input file to be decompressed
    private static BufferedWriter decompressedOutput;                           // decompressed output file


    public Encoding(String pathName){
        this.pathName = pathName;
    }              // constructor


    private static Map<Character, Integer> createFreqT(){                       // method to create frequency table

        Map<Character, Integer> frequencyTable = new TreeMap<>();               // create a new map, empty at first

        try{
            input = new BufferedReader(new FileReader(pathName));               // open the input file for reading
        }
        catch (FileNotFoundException e){
            System.out.println("File not found!" + e.getMessage());             // raise exception if File not found
            return frequencyTable;
        }

        try {
            Character ch;
            int c;
            while ((c = input.read()) != -1) {                                  // while the end of the file hasn't been reached
                ch = (char) c;                                                  // read in the character
                if (frequencyTable.containsKey(ch)) {
                    // Have seen this character before, increment the count
                    frequencyTable.put(ch, frequencyTable.get(ch)+1);
                }
                else {
                    // Have not seen this character before, add the new word
                    frequencyTable.put(ch, 1);
                }

            }
        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());  // raise exception if file cannot be read
        }

        try {
            input.close();                                                     // try closing file
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());       // raise exception if file cannot be closed
        }
        return frequencyTable;                                                 // return frequency table to caller
    }


    private static PriorityQueue<BinaryTree<Data>> createPQ(){                 // method to create Priority Queue

        Map<Character, Integer> ft = createFreqT();                            // create the frequencyTable map by calling createFreqT()

        TreeComparator comparator = new TreeComparator();                      // declare and initalize Treecomparator

        PriorityQueue<BinaryTree<Data>> pq = new PriorityQueue<BinaryTree<Data>>(comparator); // create empty priority queue with custom Treecomparator

        for(Character key : ft.keySet()) {                                     // for each key in the map
            BinaryTree<Data> tree = new BinaryTree<>(new Data(key, ft.get(key)));   // create a new tree with character as key and frequency as value
            pq.add(tree);                                                      // add this tree to the priority queue
        }

        return pq;                                                             // return priority queue
    }


    private static BinaryTree<Data> createTree() {                              // method to create Tree for Code Retrieval

        PriorityQueue<BinaryTree<Data>> pq = createPQ();                        // create priority queue by calling createPQ

        while (pq.size() > 1) {                                                 // while priority queue has more than one tree

            // remove last 2 trees
            BinaryTree<Data> T1 = pq.remove();
            BinaryTree<Data> T2 = pq.remove();

            // create a new tree with its frequency being the sum of the previous two trees
            // and left and right children of this tree are T1 and T2 respectively
            BinaryTree<Data> tree = new BinaryTree<Data>(new Data(T1.getData().getFreq() + T2.getData().getFreq()), T1, T2);

            // add this tree to the priority queue
            pq.add(tree);
        }

        // case 1 if the priority queue has only 1 element
        if(pq.size() == 1){
            // remove the last tree
            BinaryTree<Data> T1 = pq.remove();
            // create a new tree with right child as null and T1 as left child
            BinaryTree<Data> tree = new BinaryTree<Data>(new Data(T1.getData().getFreq()), T1, null);
            // add this tree to the priority queue
            pq.add(tree);
        }

        // case if priority queue is empty
        if (pq.size() == 0){
            // create an arbitrary tree and add to the priority queue with children as null
            pq.add(new BinaryTree<Data>(new Data('n', null), null, null));
        }

        // return the tree created (last in the priority queue)
        return pq.remove();
    }

    // method to create encodedMap
    private static void codeRet(){
        codeTree = createTree();            // call createTree to create the codeTree for code retrieval
        codeHelper(codeTree, "");      // call the recursive helper method for code retrieval
        if(encodedMap.size() == 1){        // if encoded map just has one entry, then
            for (Character c : encodedMap.keySet()){    // for that entry assign its value as "0" or encode it as "0"
                encodedMap.put(c, "0");
            }
        }
    }


    // recursive helper method for code retrieval
    private static void codeHelper(BinaryTree<Data> Tree, String str){
        // check if a leaf has been reached
        if(Tree.isLeaf()) {
                encodedMap.put(Tree.getData().getChar(), str);      // add it to the encoded map with char value as key and str as value
        }

        // traverse left and concatanate "0" to the str value
        if(Tree.hasLeft()) {
            codeHelper(Tree.getLeft(), str + "0");
        }

        // traverse right and concatanate "1" to the str value
        if(Tree.hasRight()) {
            codeHelper(Tree.getRight(), str + "1");
        }
    }

    // method to compress and encode input file character by character into output file
    public static void compress () {
        codeRet();                  // method call for code retrieval to create encodedMap

        try {
            input = new BufferedReader(new FileReader(pathName));               // try opening input file to be compressed
            output = new BufferedBitWriter("outputs/" + pathName.substring(7, pathName.length()-4) + "_compressed.txt");    // open compressed output file

        } catch (FileNotFoundException e) {
            System.out.println("File not found!" + e.getMessage());             // raise exception if input file not found
        }

        try {
            Character ch;                                                       // create char ch
            int c;                                                              // int value of char ch
            while ((c = input.read()) != -1) {                                  // while the end of input file hasn't been reached
                ch = (char) c;                                                  // cast int value of char into char

                for (int i = 0; i < encodedMap.get(ch).length(); i++) {         // run for loop over the string value corresponding to char ch key in encodedMap
                    if (encodedMap.get(ch).charAt(i) == '0') output.writeBit(false);    // if the character in the string is a 0, write a binary 0 into output file
                    else if (encodedMap.get(ch).charAt(i) == '1') output.writeBit(true); // if the character in the string is a 1, write a binary 1 into output file
                }
            }
        } catch (IOException d) {
            System.err.println("IO error while reading.\n" + d.getMessage());   // raise exception if char cannot be read
        }

        try{
            input.close();                                                      // close input file
            output.close();                                                     // close output file

        } catch (IOException f){
            System.out.println("IO error while reading.\n" + f.getMessage());   // raise exception if files cannot be closed
        }
    }

    // method for decompression
    public static void decompress() {
        // create an alias for codeTree's root named root
        BinaryTree<Data> root = codeTree;

        try {
            // open compressed input file
            compressedInput = new BufferedBitReader("outputs/" + pathName.substring(7, pathName.length()-4) + "_compressed.txt");
            // open decompressed output file
            decompressedOutput = new BufferedWriter(new FileWriter("outputs/" + pathName.substring(7, pathName.length()-4) + "_decompressed.txt"));
        }
        catch (IOException d) {
            System.out.println("File not found!" + d.getMessage());     // raise exception if file not found
        }

        try {
            while(compressedInput.hasNext()) {                          // while compressed file's end hasn't been reached

                Boolean c = compressedInput.readBit();                  // read bit from compressed file

                if (!c) root = root.getLeft();                          // if c is 0 bit then go left in the codeTree
                else root = root.getRight();                            // if c is 1 bit then go right in the codeTree

                if (root.isLeaf()) {                                    // if leaf has been reached
                    decompressedOutput.write(root.getData().getChar()); // find the character value corresponding to this leaf and write it into the output file as a char
                    root = codeTree;                                    // start again from the root
                }
            }

        }
        catch (IOException e){
            System.err.println("IO error while reading.\n" + e.getMessage());   // raise exception if reading error
        }

        try{
            compressedInput.close();                                            // try closing input file
            decompressedOutput.close();                                         // try closing output file

        } catch (IOException f){
            System.out.println("IO error while reading.\n" + f.getMessage());   // raise exception if files cannot be closed
        }
    }


    // main method
    public static void main(String[] args) throws Exception {

        // test case 1 : hello world
        Encoding enc_test1 = new Encoding("inputs/test1.txt");
        enc_test1.compress();
        enc_test1.decompress();

        // test case 2 : same character repeated multiple times
        Encoding enc_test2 = new Encoding("inputs/test2.txt");
        enc_test2.compress();
        enc_test2.decompress();

        // test case 3 : file with only one character
        Encoding enc_test3 = new Encoding("inputs/test4.txt");
        enc_test3.compress();
        enc_test3.decompress();

        // Compress and Decompress the US Constitution
        Encoding enc_USConstitution = new Encoding("inputs/USConstitution.txt");
        enc_USConstitution.compress();
        enc_USConstitution.decompress();

        // Compress and Decompress Leo Tolstoy's War And Peace
        Encoding enc_WarAndPeace = new Encoding("inputs/WarAndPeace.txt");
        enc_WarAndPeace.compress();
        enc_WarAndPeace.decompress();

        // Initial Size : 3.2 MB
        // Compressed Size : 2.2 MB
        // Decompressed Size : 3.2 MB

    }

}


