import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Document;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class StanfordExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("example.txt");
        String str = fileToText(file);
        //readSentiment(str);
        //readNER(str);
        //readKBP(str);

            // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
//            props.setProperty("coref.algorithm", "neural");
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
        }
    }
       // }


    public static String fileToText(File file) {
        InputStream is = null;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


//    public static void readSentiment(String text) throws IOException {
//        Properties properties = new Properties();
//        properties.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        StanfordCoreNLP process = new StanfordCoreNLP(properties);
//        Annotation annotation = process.process(text);
//        StringBuilder thing = new StringBuilder();
//        double total = 0;
//        double sent = 0;
//        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
//            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//            sent += sentiment;
//            total++;
//            thing.append(sentence.toString() + ": " + sentiment+"\n");
//        }
//        System.out.println(sent/total);
//        String articleBody = "average sentiment: "+String.valueOf(sent/total)+"\n"+ thing.toString();
//        File myObj = new File("?.txt");
//        myObj.createNewFile();
//        FileWriter myWriter = new FileWriter("?.txt");
//        myWriter.write(articleBody);
//        myWriter.close();
//    }
//
//
//    public static void readNER(String text){
//        Properties properties = new Properties();
//        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
//        properties.setProperty("ner.applyFineGrained", "false");
//        properties.setProperty("ner.applyNumericClassifiers","false");
//        System.out.println("pre pipeline");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
//        System.out.println("post pipeline");
//        System.out.println("pre coreDoc");
//        // make an example document
//        CoreDocument doc = new CoreDocument(text);
//        System.out.println("post coreDoc");
//        // annotate the document
//        pipeline.annotate(doc);
//        // view results
//        HashMap<String,Integer> topics = new HashMap<>();
//        for (CoreEntityMention em : doc.entityMentions()) {
//            if (!topics.containsKey(em.text())) {
//                topics.put(em.text(),1);
//            }
//            else {
//                topics.put(em.text(), topics.get(em.text()) +1);
//            }
//        }
//        int max1 = 0;
//        int max2 = 0;
//        String str1 = "";
//        String str2 = "";
//        for (String str: topics.keySet()) {
//            int n = topics.get(str);
//            if (n > max1) {
//                str2 = str1;
//                max2 = max1;
//                max1 = n;
//                str1 = str;
//            }
//        }
//        System.out.println("Most common topic: "+str1 + ", " + max1);
//        System.out.println("Most common topic: "+str2 + ", " + max2);
//    }
//
//    public static void readKBP(String text) throws IOException, ClassNotFoundException {
////        Properties sentenceProps = new Properties();
////        sentenceProps.setProperty("annotators", "tokenize,ssplit");
////        StanfordCoreNLP sentenceCollection = new StanfordCoreNLP(sentenceProps);
////        Annotation annotation = sentenceCollection.process(text);
////        Annotation document = new Annotation(text);
////        Properties props = new Properties();
////        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
////        props.setProperty("useSUTime","false");
////        props.setProperty("applyNumericClassifiers","false");
////        props.setProperty("ner.applyFineGrained", "false");
////        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
////        pipeline.annotate(document);
////        System.out.println("---");
////        System.out.println("coref chains");
////        String phrase;
////        Annotation segment;
////        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
////            phrase = sentence.get(CoreAnnotations.TextAnnotation.class);
////            segment = pipeline.process(phrase);
////        }
////        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
////            System.out.println("\t" + cc);
////        }
////        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
////            System.out.println("---");
////            System.out.println("mentions");
////            for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
////                System.out.println("\t" + m);
////            }
////        }
////        Properties sentenceProps = new Properties();
////        sentenceProps.setProperty("annotators", "tokenize,ssplit");
////        Properties properties = new Properties();
////        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,dcoref");
////        properties.setProperty("ner.applyFineGrained", "false");
////        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
////        StanfordCoreNLP sentenceCollection = new StanfordCoreNLP(sentenceProps);
////        Annotation annotation = sentenceCollection.process(text);
////        Annotation segment;
////        String phrase = "";
////        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
////            phrase = sentence.get(CoreAnnotations.TextAnnotation.class);
////            segment = pipeline.process(phrase);
////            for (CoreMap one : segment.get(CoreAnnotations.SentencesAnnotation.class)) {
////                System.out.println(one.get(CorefCoreAnnotations.CorefAnnotation.class));
////            }
////        }
//        Properties properties = new Properties();
//        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, ner");
////        properties.setProperty("annotators", "pos, lemma, parse, ner, kbp");
//        properties.setProperty("ner.applyFineGrained", "false");
//        properties.setProperty("ner.useSUTime","false");
//        properties.setProperty("ner.applyNumericClassifiers","false");
////        properties.setProperty("ner.buildEntiyMentions","false");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
//        Properties sentenceProps = new Properties();
//        sentenceProps.setProperty("annotators", "tokenize,ssplit");
//        StanfordCoreNLP sentenceCollection = new StanfordCoreNLP(sentenceProps);
//        CoreDocument hmm = new CoreDocument(text);
//        Annotation annot = new Annotation(text);
//        sentenceCollection.annotate(annot);
//        System.out.println(annot.get(CoreAnnotations.SentencesAnnotation.class).get(0));
//        String strr = annot.get(CoreAnnotations.SentencesAnnotation.class).get(0).toString();
//        System.out.println(strr);
//        CoreDocument hrr = new CoreDocument(strr);
//        //pipeline.annotate(hrr);
//        //System.out.println(hrr.sentences().get(0).entityMentions().get(0));
//        HashMap<String,Integer> topics = new HashMap<>();
//        for(CoreMap coreMap:annot.get(CoreAnnotations.SentencesAnnotation.class)) {
//            hrr = new CoreDocument(coreMap.toString());
//            pipeline.annotate(hrr);
//            for (CoreSentence sentence : hrr.sentences()) {
//                for (String str : sentence.nounPhrases()) {
//                    System.out.println(str);
//                    if (!topics.containsKey(str)) {
//                        topics.put(str, 1);
//                    } else {
//                        topics.put(str, topics.get(str) + 1);
//                    }
//                }
//            }
//        }
//        int max1 = 0;
//        int max2 = 0;
//        String str1 = "";
//        String str2 = "";
//        for (String str: topics.keySet()) {
//            int n = topics.get(str);
//            if (n > max1) {
//                str2 = str1;
//                max2 = max1;
//                max1 = n;
//                str1 = str;
//            }
//        }
//        System.out.println("Most common topic: "+str1 + ", " + max1);
//        System.out.println("Most common topic: "+str2 + ", " + max2);
// //       System.out.println(hmm.sentences().get(0).nounPhrases().get(1));// .relations().get(0));
////        for (CoreMap sentence : hmm.get(CoreAnnotations.SentencesAnnotation.class)) {
////            annot = new Annotation(sentence.get(CoreAnnotations.TextAnnotation.class));
////            pipeline.annotate(annot);
////            System.out.println(annot.get(CoreAnnotations.KBPTriplesAnnotation.class));
////            System.out.println(sentence.);
////            for (RelationTriple trip:annot.get(CoreAnnotations.KBPTriplesAnnotation.class)) {
////                System.out.println("subject: " + trip.subject);
////            }
////        }
//    }
}
