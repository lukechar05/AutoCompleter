package cs1501_p2;

import java.util.ArrayList;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;


public class AutoCompleter implements AutoComplete_Inter {

    public DLB dlb; 
    public UserHistory history; 

    // Constructor that will take in dictionary and user history
    public AutoCompleter(String dictionaryFile, String userHistoryFile) throws IOException { 
        dlb = new DLB(); 
        // Populate DLB with entire dictionary file 
        try { 
            BufferedReader mrBufBuf = new BufferedReader(new FileReader(dictionaryFile));
            
            String line = mrBufBuf.readLine();
            while (line != null) {
                dlb.add(line);
                line = mrBufBuf.readLine();
            }
            mrBufBuf.close(); // close buffer 
        }
        catch(IOException error) { 
           error.printStackTrace();
        }

        history = new UserHistory(userHistoryFile);
        // Populate UserHistory object with whole userHistory file 
        try { 
            BufferedReader mrBufBuf = new BufferedReader(new FileReader(userHistoryFile));
            
            String line = mrBufBuf.readLine();
            while (line != null) {
                history.add(line);
                line = mrBufBuf.readLine();
            }
            mrBufBuf.close(); // close buffer 
        }
        catch(IOException error) {  //! most helpful thing I have found for debugging 
           error.printStackTrace();
        }
    }


    // Overloaded Constructor just read in dictionary file 
    public AutoCompleter(String dictionaryFile) throws IOException {  //! No user history in this bad boi 
        dlb = new DLB(); 
        history = new UserHistory("build/resources/main/uhist_state.p2");

        try { 
            BufferedReader mrBufBuf = new BufferedReader(new FileReader(dictionaryFile));
            
            String line = mrBufBuf.readLine();
            while (line != null) {
                dlb.add(line);
                line = mrBufBuf.readLine();
            }
            mrBufBuf.close(); // close buffer 
        }
        catch(IOException error) { 
           error.printStackTrace();
        }

    }

    public ArrayList<String> nextChar(char next) {
       
        // Final Array List to return
        ArrayList<String> suggestFinal = new ArrayList<String>();

        // Calll method in both classes
        dlb.searchByChar(next);
        history.searchByChar(next);
        
        ArrayList<String> userHSugg = history.suggest();
        ArrayList<String> DLBSugg = dlb.suggest();


        for (int i = 0; i < userHSugg.size(); i++) { 
            if (!suggestFinal.contains(userHSugg.get(i))) {
                suggestFinal.add(userHSugg.get(i));
            }
            if (suggestFinal.size() == 5) break;
        }
        if (suggestFinal.size() == 5) { 
            return suggestFinal;
        }
        
        else if (suggestFinal.size() < 5) { 

            int index = 0; 
            while (suggestFinal.size() != 5) {
                if (!userHSugg.contains(DLBSugg.get(index))) { //! no duplicates
                    suggestFinal.add(DLBSugg.get(index));
                }
                index++;
            }
            return suggestFinal;
        }
        else { 
            System.out.println("Added more words than we should have there was an error");
            return suggestFinal;
        }
    }

 
    /**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
    public void finishWord(String cur) {
       dlb.add(cur);
       history.add(cur);
       dlb.resetByChar();
       history.resetByChar(); 
        
    }

    
    public void saveUserHistory(String fname) {
       
        HashMap<String, Integer> hashTimeToWrite = history.mrHash; // get hashtable that hopefully has been getting updated


        try { 
            FileWriter letsWrite = new FileWriter(fname);

            for (Map.Entry<String, Integer> wordAndVal : hashTimeToWrite.entrySet()) { 

                String wordie = wordAndVal.getKey();

                int wordFrequency = wordAndVal.getValue(); 

                for (int i = 0; i < wordFrequency; i++) { 
                    letsWrite.write(wordie + "\n");

                }
            }
            letsWrite.close();
        }

        catch (IOException error) { 
            error.printStackTrace();
        }


    }
    
}
