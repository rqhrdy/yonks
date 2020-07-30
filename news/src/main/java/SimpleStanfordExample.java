import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.simple.*;

import java.beans.BeanProperty;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class SimpleStanfordExample {


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("court.txt");
        String str = fileToText(file);
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner, parse, coref");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        props.setProperty("ner.applyFineGrained", "false");
        props.setProperty("ner.useSUTime","false");
        props.setProperty("ner.applyNumericClassifiers","false");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create a document object
        // annnotate the document
        CoreDocument document;
        document = new CoreDocument(str);
        pipeline.annotate(document);
        SemanticGraph dependencies = document.sentences().get(0).dependencyParse();//
        System.out.println(dependencies.getFirstRoot().getOriginal());
        System.out.println(dependencies.getRoots());

        // .get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
        int max1 = 0;
        int max2 = 0;
        int max3 = 0;
        String str1 = "";
        String str2 = "";
        String str3 = "";
        for (CorefChain chain: document.corefChains().values()){
            int n = chain.getMentionsInTextualOrder().size();
            if (n > max1) {
                str3 = str2;
                str2 = str1;
                str1 = chain.getRepresentativeMention().toString();
                max3 = max2;
                max2 = max1;
                max1 = n;
            }
            else if (n > max2) {
                max2 = n;
                str2 = chain.getRepresentativeMention().toString();
            }
        }
        System.out.println("Most common topic: "+str1 + ", " + max1);
        System.out.println("Most common topic: "+str2 + ", " + max2);
        System.out.println("Most common topic: "+str3 + ", " + max3);
        props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner, parse");
//        props.setProperty("coref.algorithm", "neural");
        props.setProperty("ner.applyFineGrained", "false");
        props.setProperty("ner.useSUTime","false");
        props.setProperty("ner.applyNumericClassifiers","false");
        StanfordCoreNLP.clearAnnotatorPool();
        pipeline = new StanfordCoreNLP(props);
//        System.out.println(str1.substring(1,str1.lastIndexOf("\"")));
        System.out.println(str1.substring(1,str.indexOf("\"",2)));
        CoreDocument doc1 = new CoreDocument(str1);//.substring(1,str1.lastIndexOf("\"")));
        CoreDocument doc2 = new CoreDocument(str2);//.substring(1,str1.lastIndexOf("\"")));
        pipeline.annotate(doc1);
        pipeline.annotate(doc2);
        System.out.println(doc1.sentences().get(0).nounPhrases());
        System.out.println(doc1.sentences().get(0).posTags());
        System.out.println(doc1.sentences().get(0).dependencyParse().getRoots());
        System.out.println(doc1.tokens().get(0).tag());//.entityMentions());
        System.out.println(doc2.tokens().get(0).tag());
//        for (CoreEntityMention em : document.entityMentions()){
//        }
        /*
        HashMap<String, ArrayList<String>> counts = new HashMap<>();
        for (CoreEntityMention em : document.entityMentions()){
            if(counts.containsKey(em.text())) {
                counts.get(em.text()).add(em.sentence().text());
                counts.get(em.text()).trimToSize();
            }
            else {
                ArrayList<String> refs = new ArrayList<>();
                refs.add(em.sentence().text());
                refs.trimToSize();
                counts.put(em.text(), refs);
            }
        }
        int max1 = 0;
        int max2 = 0;
        int max3 = 0;
        String str1 = "";
        String str2 = "";
        String str3 = "";
        for (String key: counts.keySet()) {
            int n = counts.get(key).size();
            if (n > max1) {
                str3 = str2;
                str2 = str1;
                str1 = key;
                max3 = max2;
                max2 = max1;
                max1 = n;
            }
            else if (n > max2) {
                max3 = max2;
                max2 = n;
                str3 = str2;
                str2 = key;
            }
            else {
                max3 = n;
                str3 = key;
            }
        }
        System.out.println("Most common topic: "+str1 + ", " + max1);
        System.out.println("Most common topic: "+str2 + ", " + max2);
        System.out.println("Most common topic: "+str3 + ", " + max3);
        String[] strArr = new String[3];
        strArr[0] = str1;
        strArr[1] = str2;
        strArr[2] = str3;
        String[] mentionsArr = new String[3];
        mentionsArr[0] = "";
        mentionsArr[1] = "";
        mentionsArr[2] = "";
        int mentionsIndexes = 0;
        for (String keyword:strArr) {
            String mentions = "";
            for (String em : counts.get(keyword)) {
                mentions = mentions.concat(em);
            }
            mentionsArr[mentionsIndexes] = mentions;
            mentionsIndexes++;
        }
        counts = null;
        document = null;
        pipeline = null;
        str = null;
        props = null;
        props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner, parse, coref");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        props.setProperty("ner.applyFineGrained", "false");
        props.setProperty("ner.useSUTime","false");
        props.setProperty("ner.applyNumericClassifiers","false");
        StanfordCoreNLP.clearAnnotatorPool();
        pipeline = new StanfordCoreNLP(props);
        CoreDocument doc;
        for(int i = 0; i < mentionsArr.length; i++){
            doc = new CoreDocument(mentionsArr[i]);
            pipeline.annotate(doc);
            Set<Integer> keys = doc.corefChains().keySet();
            for (Integer n:keys){
                if(doc.corefChains().get(n).getRepresentativeMention().toString().contains(strArr[i])) {
                    System.out.println(doc.corefChains().get(n).getRepresentativeMention());
                }
            }
        }*/
    }


    public static String fileToText(File file) {
        InputStream is;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            return  sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
