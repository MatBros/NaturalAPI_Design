package HexaTech.Stanford;

import HexaTech.entities.DoubleToken;
import HexaTech.entities.Gherkin;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import java.util.*;

public class Stanford implements iStanford {

    private final StanfordCoreNLP pipeline;
    private final DependencyParser depparser;

    public Stanford() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
        this.depparser = DependencyParser.loadFromModelFile("edu/stanford/nlp/models/parser/nndep/english_UD.gz");
    }

    @Override
    public Gherkin extract(String prova) {
        String delimiter = "[\n]+";
        String arr[] = prova.split(delimiter);
        Gherkin toRit = new Gherkin();
        String sentinella="";

        for (String str : arr) {
            CoreDocument documents = new CoreDocument(str);
            this.pipeline.annotate(documents);
            StringBuilder builder = new StringBuilder();
            String firstToken = documents.sentences().get(0).tokensAsStrings().get(0);
            if (firstToken.equalsIgnoreCase("AND")){
                firstToken=sentinella;
            }
            String toDeparse=str;
            Annotation document = new Annotation(toDeparse);
            this.pipeline.annotate(document);

            GrammaticalStructure gStruct = depparser.predict(document);
            Collection<TypedDependency> dependencies = gStruct.typedDependencies();
            switch (firstToken.toLowerCase()) {
                case ("scenario"):
                    for (int i = 2; i < documents.sentences().get(0).lemmas().size(); i++) {
                        if(i>2)
                            builder.append(documents.sentences().get(0).lemmas().get(i).substring(0,1).toUpperCase()+documents.sentences().get(0).lemmas().get(i).substring(1));
                        else
                            builder.append(documents.sentences().get(0).lemmas().get(i));
                    }
                    toRit.setScenario(builder.toString());
                    System.out.println(toRit.getScenario());
                    break;
                case ("given"):
                    toRit.setGiven("sono bello dentro il given");
                    sentinella="given";
                    break;
                case ("when"):
                    for (TypedDependency dep : dependencies) {
                        if (dep.reln().getShortName().equalsIgnoreCase("obj"))
                            builder.append(dep.dep().lemma()+" ");
                    }
                    toRit.setWhen(builder.toString());
                    System.out.println(toRit.getWhen());
                    sentinella="when";
                    break;
                case ("then"):
                    for (TypedDependency dep : dependencies) {
                        if (dep.reln().getShortName().equalsIgnoreCase("obj"))
                            builder.append(dep.dep().lemma()+" ");
                    }
                    toRit.setThen(builder.toString());
                    System.out.println(toRit.getThen());
                    sentinella="then";
                    break;
            }
        }
        return toRit;
    }
}
