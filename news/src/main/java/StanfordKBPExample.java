import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class StanfordKBPExample {

    public static void main (String[] args){
        System.out.println((2));
        File file = new File("example.txt");
        String str = fileToText(file);
        Properties props = new Properties();
        // set the list of annotators to run
        annotationTesting(str);
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner, parse, coref, kbp");
//        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
////            props.setProperty("coref.algorithm", "neural");
//        props.setProperty("ner.applyFineGrained", "false");
//        props.setProperty("ner.useSUTime","false");
//        props.setProperty("ner.applyNumericClassifiers","false");
//        // build pipeline
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        CoreDocument doc = new CoreDocument(str);
//        pipeline.annotate(doc);
//        for(CoreSentence sentence:doc.sentences()) {
//            System.out.println(sentence.text());
//            for (RelationTriple triple : sentence.relations()) {
//                System.out.println(triple.subject);
//                System.out.println(triple.canonicalSubject);
//                System.out.println(triple.object);
//                System.out.println(triple.canonicalObject);
//                System.out.println(triple.relation);
//            }
//        }
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

    public static void annotationTesting (String str){
        AnnotationPipeline test = new AnnotationPipeline();
        Properties properties = new Properties();
        properties.setProperty("annotators","tokenize, ssplit, parse, sentiment");
        properties.setProperty("parse.maxlen","150");
        properties.setProperty("parse.keepPunct", "false");
        properties.setProperty("ner.applyFineGrained", "false");
        properties.setProperty("ner.useSUTime", "false");
        properties.setProperty("ner.applyNumericClassifiers", "false");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        test.addAnnotator(new TokenizerAnnotator(false));
        test.addAnnotator(new WordsToSentencesAnnotator(false));
        Annotation annotation = new Annotation(str);
        test.annotate(annotation);
        String paragraph = "";
        test.unmount();
        test = null;
        System.gc();
        double sum = 0;
        double size = annotation.get(CoreAnnotations.SentencesAnnotation.class).size();
        int i = 0;
        for (; i < size;) {
            int n = i + 2;
            for (; i < n; i++){
                Annotation annotation1 = pipeline.process(paragraph);
                for (CoreMap sentence1 : annotation1.get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence1.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    sum += RNNCoreAnnotations.getPredictedClass(tree);
                }
                paragraph = paragraph.concat(annotation.get(CoreAnnotations.SentencesAnnotation.class).get(i).toString());
            }
            paragraph = "";
            System.gc();
        }
        System.out.println(sum/(double)i);
    }
}
