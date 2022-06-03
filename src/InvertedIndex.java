/*
 * InvertedIndex - Given a set of text files, implement a program to create an 
 * inverted index. Also create a user interface to do a search using that inverted 
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory. 
 *
 */
import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index {

        //--------------------------------------------
        Map<Integer, String> sources;  // store the doc_id and the file name
        HashMap<String, DictEntry> index; // THe inverted index
        HashSet<Integer> doc_id_list ;//doc_id list
        //--------------------------------------------
        Index() {
            sources = new HashMap<Integer, String>();
            index = new HashMap<String, DictEntry>();
            doc_id_list = new HashSet<>();
        }
        //---------------------------------------------
        public void printPostingList(HashSet<Integer> hset) {
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
        }
        //---------------------------------------------
        public void printDictionary() {
        Iterator it = index.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            DictEntry3 dd = (DictEntry3) pair.getValue();
//            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
//            //it.remove(); // avoids a ConcurrentModificationException
//             printPostingList(dd.postingList);
//        }
        System.out.println("------------------------------------------------------");
        System.out.println("*****    Number of terms = " + index.size());
        System.out.println("------------------------------------------------------");

    }
        //-----------------------------------------------
        public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {

            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                doc_id_list.add(i);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
         printDictionary();
    }
        //--------------------------------------------------------------------------
        HashSet<Integer> find_postingsList(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        if(!index.containsKey(words[0])){
            return new HashSet<Integer>();

        }
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        for (String word : words) {
            res.retainAll(index.get(word.toLowerCase()).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");
            return new HashSet<Integer>();
        }
        return res;
    }
        //----------------------------------------------------------------------------
        HashSet<Integer> NOT(HashSet<Integer> pL) {
            HashSet<Integer> temp = new HashSet<Integer>(doc_id_list);
            temp.removeAll(pL);
            if (temp.size() == 0) {
                return new HashSet<>();
            }
            return temp;
        }
        //----------------------------------------------------------------------------
        HashSet<Integer> union(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        if(pL1.size()==0&&pL2.size()==0)
            return new HashSet<>();
        else if(pL1.size()==0)
            return pL2;
        else if(pL2.size()==0)
            return pL1;

        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = pL1.stream().findFirst().get(), docId2 = pL2.stream().findFirst().get();
//        INTERSECT ( p1 , p2 )
//          1 answer ←   {}
        // answer =
//          2 while p1  != NIL and p2  != NIL
        if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();

        while (itP1.hasNext() && itP2.hasNext()) {
            if (docId1 == docId2) {
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            }
            else if (docId1 < docId2) {
                answer.add(docId1);
                if (itP1.hasNext())
                    docId1 = itP1.next();

            } else {
                answer.add(docId2);
                if (itP2.hasNext())
                    docId2 = itP2.next();

            }

        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }
        while (itP1.hasNext()){
            answer.add(docId1);
            docId1 = itP1.next();

        }
        while (itP2.hasNext()){
            answer.add(docId2);
            docId2 = itP2.next();

        }
        answer.add(docId1);
        answer.add(docId2);

//          10 return answer
        return answer;
           }
        //-----------------------------------------------------------------------
        HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
            if(pL1.size()==0||pL2.size()==0)
                return new HashSet<>();
            HashSet<Integer> answer = new HashSet<Integer>();;
            Iterator<Integer> itP1 = pL1.iterator();
            Iterator<Integer> itP2 = pL2.iterator();
            int docId1 = pL1.stream().findFirst().get(), docId2 = pL2.stream().findFirst().get();
    //        INTERSECT ( p1 , p2 )
    //          1 answer ←   {}
            // answer =
    //          2 while p1  != NIL and p2  != NIL
            if (itP1.hasNext())
                docId1 = itP1.next();
            if (itP2.hasNext())
                docId2 = itP2.next();

            while (itP1.hasNext() && itP2.hasNext()) {

    //          3 do if docID ( p 1 ) = docID ( p2 )
                if (docId1 == docId2) {
    //          4   then ADD ( answer, docID ( p1 ))
    //          5       p1 ← next ( p1 )
    //          6       p2 ← next ( p2 )
                    answer.add(docId1);
                    docId1 = itP1.next();
                    docId2 = itP2.next();
                } //          7   else if docID ( p1 ) < docID ( p2 )
                //          8        then p1 ← next ( p1 )
                else if (docId1 < docId2) {
                    if (itP1.hasNext())
                        docId1 = itP1.next();
                     else return answer;

                } else {
    //          9        else p2 ← next ( p2 )
                    if (itP2.hasNext())
                        docId2 = itP2.next();
                    else return answer;

                }

            }
            if (docId1 == docId2) {
                answer.add(docId1);
            }else {
                while (itP1.hasNext()){
                    if (docId1 == docId2) {
                        answer.add(docId1);
                        break;
                    }else
                        docId1=itP1.next();

                }
                while (itP2.hasNext()){
                    if (docId1 == docId2) {
                        answer.add(docId1);
                        break;
                    }else
                        docId2 = itP2.next();

                }
                if (docId1 == docId2) {
                    answer.add(docId1);
                }
            }


    //          10 return answer
            return answer;
        }
        //-----------------------------------------------------------------------
        public String printResult(HashSet<Integer> pL) {
            String result = "";
            for (int num : pL) {

                result += "\t" + sources.get(num) + "\n";
            }
            return result.equals("")?"Not Found 404":result;
        }
}

//=====================================================================
public class InvertedIndex {

    public static void main(String args[]) throws IOException {
        Index index = new Index();
        String phrase = "";
        index.buildIndex(new String[]{
            "docs\\100.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
            "docs\\101.txt",
            "docs\\102.txt",
            "docs\\103.txt",
            "docs\\104.txt",
            "docs\\105.txt",
            "docs\\500.txt",
            "docs\\501.txt",
            "docs\\502.txt",
            "docs\\503.txt",
            "docs\\504.txt",
            "docs\\505.txt"
        });

        String  word1 = "Autism".toLowerCase(Locale.ROOT),
                word2 = "agile".toLowerCase(Locale.ROOT),
                word3 = "system".toLowerCase(Locale.ROOT),
                word4 = "customization".toLowerCase(Locale.ROOT),
                word5 = "Classification".toLowerCase(Locale.ROOT),
                word6 = "orchestration".toLowerCase(Locale.ROOT);

        HashSet<Integer>    word1_postingsList = index.find_postingsList(word1),
                            word2_postingsList = index.find_postingsList(word2),
                            word3_postingsList = index.find_postingsList(word3),
                            word4_postingsList = index.find_postingsList(word4),
                            word5_postingsList = index.find_postingsList(word5),
                            word6_postingsList = index.find_postingsList(word6);



        System.out.println("Result of finding \""+word1+"\" \nDocuments :-\n"+index.printResult(word1_postingsList));
        System.out.println("Result of finding \""+word2+"\"\nDocuments :-\n"+index.printResult(word2_postingsList));
        System.out.println("Result of finding \""+word3+"\"\nDocuments :-\n"+index.printResult(word3_postingsList));
        System.out.println("Result of finding \""+word4+"\"\nDocuments :-\n"+index.printResult(word4_postingsList));
        System.out.println("Result of finding \""+word5+"\"\nDocuments :-\n"+index.printResult(word5_postingsList));
        System.out.println("Result of finding \""+word6+"\"\nDocuments :-\n"+index.printResult(word6_postingsList));

        System.out.println("*********************************************************");
        // and  not or  and
        System.out.println("\""+word1+"\" AND \""+word2+"\" OR NOT \""+word3+"\" AND \""+word4+"\" \n"+
                "Documents :-\n"+
                index.printResult(
                        index.union(
                                index.intersect(word1_postingsList,word2_postingsList),
                                index.intersect(index.NOT(word3_postingsList),word4_postingsList)
                        )
                )
        );

        System.out.println("*********************************************************");
        // and not and
        System.out.println("\""+word2+"\" AND NOT \""+word3+"\" AND  \""+word5+"\" \n"+
                "Documents :-\n"+
                index.printResult(
                        index.intersect(
                                index.intersect(word2_postingsList,index.NOT(word3_postingsList)),
                                word5_postingsList
                        )
                )
        );
        System.out.println("*********************************************************");
        //and and
        System.out.println("\""+word2+"\" AND \""+word3+"\" AND \""+word5+"\"  \n"+
                "Documents :-\n"+
                index.printResult(
                        index.intersect(
                                index.intersect(word2_postingsList,word3_postingsList),
                                word5_postingsList
                        )
                )
        );


        System.out.println("*********************************************************");
        // or and
        System.out.println("\""+word2+"\" OR \""+word3+"\" AND \""+word1+"\"  \n"+
                "Documents :-\n"+
                index.printResult(
                        index.union(
                                word2_postingsList,
                                index.intersect(word3_postingsList,word1_postingsList))
                )
        );
        System.out.println("*********************************************************");
        //not
        System.out.println("NOT \""+word1+"\" \n"+
                "Documents :-\n"+
                index.printResult(
                        index.NOT(word1_postingsList)
                )
        );
        System.out.println("*********************************************************");
        //not and and not
        System.out.println("NOT \""+word1+"\" AND \""+word3+"\" AND NOT \""+word6+"\"  \n"+
                "Documents :-\n"+
                index.printResult(
                        index.intersect(
                                index.intersect(index.NOT(word1_postingsList),word3_postingsList),
                                index.NOT(word6_postingsList)
                        )
                )
        );



    }
}
