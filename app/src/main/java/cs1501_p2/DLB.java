package cs1501_p2;

import java.util.ArrayList;

import com.google.common.collect.TreeMultimap;
import com.google.common.io.CountingOutputStream;

public class DLB implements Dict {

    public DLBNode root; 

    public StringBuilder developingWord;
    int howManyWords;
    public ArrayList<String> wordList;

    public DLB() { 
        this.root = null;
        this.developingWord = new StringBuilder();
        this.howManyWords = 0;
        this.wordList = new ArrayList<String>();
    }

    /**
	 * Helper method to make chain, given the first one created
	 * @param key: The string to add
     * @param curr: Node to start chaining down from
	 */
    public void createChain(String key, DLBNode curr) { 
        for (int i = 1; i  < key.length(); i++) {
            
            DLBNode newNode = new DLBNode(key.charAt(i));
            curr.setDown(newNode);
            curr = newNode;
        }
        DLBNode terminate = new DLBNode('^');
        curr.setDown(terminate);
    }

     /**
	 * Helper recurse until a new chain has to be made
	 * @param key: The string to add
     * @param curr: Tracker node as we move around DLB
     * @param count: Int counter of where we are @ in string
	 */
    public void recurseAround(String key, DLBNode curr, DLBNode prevCurr, int count) {

        if (curr == null) { // If curr is null, create the remaining chain
            DLBNode startNode = new DLBNode(key.charAt(count));
            prevCurr.setRight(startNode);
            createChain(key.substring(count, key.length()), startNode);
        }
        else if (curr.getLet() == key.charAt(count)) { // Keep going down if they are equal
            if (count == key.length() - 1) { //! This is a mini word
                curr = curr.getDown();
                while (curr.getRight() != null) { 
                    curr = curr.getRight();
                }
                DLBNode terminate = new DLBNode('^');
                curr.setRight(terminate);
            }
            else recurseAround(key, curr.getDown(), curr, count + 1);
        } else { 
            recurseAround(key, curr.getRight(), curr, count);
        }
    }
    
    public void add(String key) {
        
        // Check for edge case empty string
        if (key.isEmpty() || contains(key)) {
            return;
        }

        howManyWords++;
        // If there are no elements this is first word
        if (root == null) {
            root = new DLBNode(key.charAt(0));
            DLBNode curr = root;
            // Place all chars in the downward direction
            createChain(key, curr);
        }
        // Otherwise go right until you find matching char, if you don't find char create a new line
        else { 
            DLBNode curr = root;
            boolean foundLetter = false; // variable for if first char was found on top level
            // Check root node
            if (curr.getLet() == key.charAt(0)) { 
                foundLetter = true;
            } else {  // otherwise go through whole top level
                while (curr.getRight() != null) {
                    curr = curr.getRight();
                    if (curr.getLet() == key.charAt(0)) { 
                        foundLetter = true;
                        break;
                    }
                }
            }
            // Now we assess if we need to create a new chain or if we found first char
            if (foundLetter) { //! this means we start from curr
                recurseAround(key, curr.getDown(), curr, 1);
            }
            else { // Create whole new chain with terminating character 
                DLBNode startNode = new DLBNode(key.charAt(0));
                curr.setRight(startNode);
                createChain(key, startNode);
            }
        }
    }

    
    public boolean contains(String key) {
        
        // If root is null it can't contain any words or check chance of empty string
        if (root == null || key.isEmpty()) return false;
        else { 
            int count = 0;
            return containWord(key, root, count);
        }
    }


    /**
	 * Helper recursive method to validate if a string is contained in DLB
     * @param key: String to check if its contained; 
     * @param curr: Node we use to traverse around, starts as root,
     * @param count: Tracker of where we are in string
	 */
    public boolean containWord(String key, DLBNode curr, int count) { 

        // If we got to the end of the string, check to see if this is just the prefix,
       
        if (count == key.length()) {  //! next character must be terminating char or its just prefix that is there
            if (curr.getLet() == '^') return true; 
            else {
                while(curr.getRight() != null) {
                    curr = curr.getRight(); 
                    if (curr.getLet() == '^') { 
                        return true;
                    }
                }
                return false; 
            }
        }
        if (curr == null) return false; //! if curr is ever null word DNE in DLB
        else if (curr.getLet() == key.charAt(count)) { // go down if characters are equal
            return containWord(key, curr.getDown(), count + 1);
        }
        else { // Go right to try another chain if characters are not equal
            return containWord(key, curr.getRight(), count);
        }  
    }


     /**
	 * Helper recursive method to validate if a prefix is contained in DLB
     * @param key: String to check if its contained; 
     * @param curr: Node we use to traverse around, starts as root,
     * @param count: Tracker of where we are in string
	 */
    public boolean containsWordAndOtherPre(String key, DLBNode curr, int count) { 
        
        // If we got to the end of the string it was in the DLB
        if (count == key.length()) {
        
             //! next character must be terminating char or its just prefix that is there.
             // However there is the case that the word is fully contained in a bigger word, meaning the terminating character is somewhere to the right. 
            if (curr.getLet() == '^' && curr.getRight() != null) return true;  // If it is terminating char but we can go right, then it must be a prefix to other words
            else {
                while(curr.getRight() != null) {
                    curr = curr.getRight(); 
                    if (curr.getLet() == '^') { // If you go right and find a terminating, then the word is a prefix to the one you were initially traversing 
                        return true;
                    }
                }
                return false; 
            }
        }
        if (curr == null) return false; //! if curr is ever null prefix DNE in DLB
        else if (curr.getLet() == key.charAt(count)) { // go down if characters are equal
            return containsWordAndOtherPre(key, curr.getDown(), count + 1);
        }
        else { // Go right to try another chain if characters are not equal
            return containsWordAndOtherPre(key, curr.getRight(), count);
        }  
    }
    
    
    /**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
    public boolean containsPrefix(String pre) {
        
        // If root is null it can't contain any prefixes or check chance of empty string
        if (root == null || pre.isEmpty()) return false;
        traverse();
        for(int i = 0; i < wordList.size(); i++) { // loop through all of DLB
        
            String existingWord = wordList.get(i); // grab word
            int length = existingWord.length();
            int preLength = pre.length();

            if(preLength >= length)  continue; // go to next word if word is shorter than pre
            String prefix = existingWord.substring(0,preLength); //! otherwise grab prefix 
            if(prefix.equals(pre)) return true; // condition proved
            
        }
        return false;
    }

    
    /**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
    public int searchByChar(char next) {
        
        developingWord.append(next);

        if (developingWord.toString().isEmpty() || (!containsPrefix(developingWord.toString()) && !contains(developingWord.toString()))) {
            return -1;
        }
        else if (!contains(developingWord.toString()) && containsPrefix(developingWord.toString())) {
            return 0;
        }
        
        else if (contains(developingWord.toString()) && !containsWordAndOtherPre(developingWord.toString(), root, 0)) {
            return 1;
        }
        else if (contains(developingWord.toString()) && containsWordAndOtherPre(developingWord.toString(), root, 0)) {
            return 2;
        }
        //? If this statement is reached there is an error as it should be one of the previous cases/
        return -2;
    }

   /**
	 * Reset the state of the current by-character search
	 */
    public void resetByChar() {
       
        developingWord.setLength(0);
        
    }

    
    public ArrayList<String> suggest() {
        
        wordList = new ArrayList<String>();
        DLBNode startNode = findNode(root, developingWord.toString(), 0);


        traverseItAll(startNode, developingWord.toString().substring(0, developingWord.toString().length() - 1));

        // System.out.println("word list is + " + wordList.toString());

        // So after this call we have an array list of probably more than 5 words, we need to sort them in ASCIBETICAL ORDER
        ArrayList<String> suggestList = new ArrayList<String>();

        //! SORT LEXOGRAPHICALLY
        for (int i = 0; i < wordList.size(); i++) { 
            for (int x = i + 1; x < wordList.size(); x++) { 
                if (wordList.get(i).compareTo(wordList.get(x)) > 0) { 

                    String tempora = wordList.get(i);
                    wordList.set(i, wordList.get(x));
                    wordList.set(x, tempora);
                }
            }
        }
        for (int i = 0; i < 5; i++) { 
            suggestList.add(wordList.get(i));
        }
        return suggestList; 

    }
   
    public DLBNode findNode(DLBNode curr, String sequence, int count) {

       
        // The case this is missing is with one char: we simply want to move to the t letter in DLB. 
        if (sequence.length() == 1) { 
            while (curr != null && curr.getLet() != sequence.charAt(0)) { 
                curr = curr.getRight();
            }
            if (curr == null) System.out.println("error, node does not exist");
            else return curr; 
        }

        if (curr == null) return null; 

        if (count == sequence.length() -1 && curr.getLet() == sequence.charAt(sequence.length() -1)) { 
            return curr;
        }
        else if (curr.getLet() == sequence.charAt(count)) { // go down if characters are equal
            return findNode(curr.getDown(), sequence, count + 1);
        }
        else { // Go right to try another chain if characters are not equal
            return findNode(curr.getRight(), sequence, count);
        }  

    }

    
    public ArrayList<String> traverse() {
        
       

        if (root == null) return new ArrayList<String>();
        wordList = new ArrayList<String>();
        traverseItAll(root, "");
        return wordList;
    }

    public void traverseItAll(DLBNode curr, String wordBuilder) {


        if (curr.getLet() == '^') { // Found a word
            wordList.add(wordBuilder);
        } 
        // add letter 
        
        String wordDown = wordBuilder + curr.getLet();
        if (curr.getDown() != null) {
            
            traverseItAll(curr.getDown(), wordDown);
        }
        if( curr.getRight() != null) {
            traverseItAll(curr.getRight(), wordBuilder);
        }
        
    }
    
    public int count() {
        
      return howManyWords;
    }
    
}
