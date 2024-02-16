package cs1501_p2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserHistory implements Dict {

    public DLB dlb = new DLB(); // DlB object to use for most methods 
    // Hash table that will count frequency of entries
    HashMap<String, Integer> mrHash = new HashMap<>();


    public UserHistory() { 
        this.dlb = new DLB(); 
        this.mrHash = new HashMap<>();
        
    }

    // All this constructor does is initialize a DLB object and hashmap for frequency counting
    public UserHistory(String filer) { 
        this.dlb = new DLB(); 
        this.mrHash = new HashMap<>();

        try { 
            File mrFile = new File(filer);

            if (mrFile.createNewFile()) System.out.println("file created");
            else System.out.println("file already exists");
        }
        catch(IOException error) { 
           error.printStackTrace();
        }
        
    }

    public void add(String key) {
        dlb.add(key); // add key to dlb
       if (key.isEmpty()) return; 
       if (mrHash.containsKey(key)) { 
            int val = mrHash.get(key);
            val = val + 1; 
            mrHash.put(key, val); 
        } 
        else { 
            mrHash.put(key, 1);
        }
    }

  
    public boolean contains(String key) {
        return dlb.contains(key);
    }

   
    public boolean containsPrefix(String pre) {
        return dlb.containsPrefix(pre);
    }

   
    public int searchByChar(char next) {
       return dlb.searchByChar(next);
    }

 
    public void resetByChar() {
        
        dlb.resetByChar();
    }

    public ArrayList<String> suggest() {
      
        if (dlb.count() == 0) return new ArrayList<String>();

        dlb.wordList.clear();

        DLBNode startNode = dlb.findNode(dlb.root, dlb.developingWord.toString(), 0);


        dlb.traverseItAll(startNode, dlb.developingWord.toString().substring(0, dlb.developingWord.toString().length() -1));

        ArrayList<String> suggestList = new ArrayList<String>();
        // Now we need to sort according to frequency, so we use hash table !!

        for (int i = 0; i < dlb.wordList.size(); i++) { 

            for (int x = i + 1; x < dlb.wordList.size(); x++) { 

                if (mrHash.get(dlb.wordList.get(i)) < mrHash.get(dlb.wordList.get(x))) { 

                    String tempora = dlb.wordList.get(i);
                    dlb.wordList.set(i, dlb.wordList.get(x));
                    dlb.wordList.set(x, tempora);
                }
            }
        }

        int stop = 5; 
        if (dlb.wordList.size() < 5) stop = dlb.wordList.size();
        for (int i = 0; i < stop; i++) { 
            suggestList.add(dlb.wordList.get(i));
        }
        return suggestList;
    }

    public ArrayList<String> traverse() {
    
        return dlb.traverse();
    }

    public int count() {
        return dlb.count();
    }

} 
