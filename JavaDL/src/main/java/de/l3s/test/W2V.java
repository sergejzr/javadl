package de.l3s.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.tools.Tool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class W2V {
	private static Logger log = LoggerFactory.getLogger(W2V.class);
	private Options options;
	private File sentencefile;
	private File modelfile;
	private int layerSize;
	private boolean usemodel;
	private int minWordFrequency;
public W2V(String[] args) {
	options = new Options();
	// add t option

	options.addOption(Option.builder().longOpt("sentencefile").hasArg(false).desc("a sentence file to read").numberOfArgs(1)
			.argName("path").required().build());
	
	options.addOption(Option.builder().longOpt("layerSize").hasArg().desc("Number of features")
			.numberOfArgs(1).argName("").required(false).build());
	

	options.addOption(Option.builder().longOpt("usemodel")
			.desc("A word2vec file will be re-used, if exisis. ").required(false).build());
	
	options.addOption(Option.builder().longOpt("modelfile").hasArg(false)
			.desc("a model file to write to or to read from").numberOfArgs(1)
			.argName("path").required(false)
			.build());
	
	options.addOption(Option.builder().longOpt("minWordFrequency").hasArg(false)
			.desc("a model file to write to or to read from").required(false)
			.build());
	
	
	
	
	options.addOption(
			Option.builder().longOpt("test").hasArg(false).desc("The software will run in demo mode")
					.build());
	options.addOption(
			Option.builder().longOpt("help").hasArg(false).desc("Prints possible options")
					.build());
	
	DefaultParser parser = new DefaultParser();
	try {
		CommandLine cmd = parser.parse(options, args);

		String sentencefilestr = cmd.getOptionValue("sentencefile");
		String modelfilestr = cmd.getOptionValue("modelfile");
		String layerSizestr = cmd.getOptionValue("layerSize","100");
		String usemodelstr = cmd.getOptionValue("usemodel","false");
		String minWordFrequencystr = cmd.getOptionValue("minWordFrequency","5");

		sentencefile=new File(sentencefilestr);
		
		if(!sentencefile.exists())
		{
			throw new ParseException("Error: --indir "+sentencefile+" does not exist");
			
		}
		
		modelfile=new File(modelfilestr);
		
		try {
			layerSize = Integer.parseInt(layerSizestr);
		} catch (Exception e) {
			throw new ParseException(
					"argument" + layerSizestr + " is not correct for the option --layerSize (should be integer)");
		}
		

		try {
			minWordFrequency = Integer.parseInt(minWordFrequencystr);
		} catch (Exception e) {
			throw new ParseException(
					"argument" + minWordFrequencystr + " is not correct for the option --minWordFrequency (should be integer)");
		}
		
		try {
			usemodel = Boolean.parseBoolean(usemodelstr);
		} catch (Exception e) {
			throw new ParseException(
					"argument" + usemodelstr + " is not correct for the option --usemodel (should be integer)");
		}

	

	

	} catch (ParseException e) {
		// TODO Auto-generated catch block

		HelpFormatter formatter = new HelpFormatter();
		System.err.println(e.getMessage());
		formatter.printHelp("java -cp "+this.getClass().getName()+" " + this.getClass().getCanonicalName().trim() + " [OPTIONS]",
				options);
		System.exit(1);
	}

}
public static void main(String[] args) {


	if(args.length==1&&args[0].equals("--test")){
	{
		String argumentline="--indir testtweets/ --lang de --numthreads 4 --outdir testtweetsclean/ --stem"
				;
		System.out.println("DEMO mode.\n"+argumentline+"\n");
		args=(argumentline).split("\\s+");
		

	}
	}
	W2V w2v = new W2V(
			args
					);
	w2v.run();
	
	
    
  
        



}
private  void run() {  
	
	
	
	try {
	


    SentenceIterator iter = new LineSentenceIterator(sentencefile);
    
    iter.setPreProcessor(new SentencePreProcessor() {
		
		@Override
		public String preProcess(String sentence) {
			// TODO Auto-generated method stub
			return sentence.toLowerCase();
		}
	});
    // Split on white spaces in the line to get words
    TokenizerFactory t = new DefaultTokenizerFactory();
    t.setTokenPreProcessor(new CommonPreprocessor());
    
    Word2Vec vec = new Word2Vec.Builder()
            .minWordFrequency(minWordFrequency)
            .layerSize(layerSize)
            .seed(42)
            .windowSize(5)
            .iterate(iter)
            .tokenizerFactory(t)
            .build();

	
    vec.fit();
    // Write word vectors
    WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");

    log.info("Closest Words:");
    Collection<String> lst = vec.wordsNearest("auf", 10);
    System.out.println(lst);
    
   

  
 	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
	/*
		SentenceIterator iter = new LineSentenceIterator(new File("exampledata/poor_text.txt"));
		Word2Vec wv=new Word2Vec(iter);
		wv.setMinWordFrequency(3);
		wv.buildVocab();
		wv.setLayerSize(100);
		wv.setWindow(5);
	*/	
	
        }
}
