package recipe.controllers;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import recipe.helpers.Printer;
import recipe.constant.Ontology;
import recipe.semanticweb.AnswerBuilder;
import recipe.connect.MySQLDatabase;
import recipe.model.QueryResultModel;
import recipe.model.SemanticToken;
import recipe.model.Sentence;
import recipe.parser.Parser;
import recipe.parser.Tokenizer;
import recipe.semanticweb.OntologyMapper;
import recipe.semanticweb.OntologyQuery;

public class Tester {

	public static void main(String[] args) {
		String recipeOntology = Ontology.Path.RECIPEONTOLOGY;
		
//		String question = "bahan makanan apa yang jadi pencegah diabetes";
//		String question = "apa saja bahan makanan berpurin tinggi";
//		String question = "apa saja pantangan penyakit diabetes";

//		==== UJI COBA TERHADAP PERTANYAAN DARI RESPONDEN ===
//		-- pending, buat method baru untuk menangani query (2)	
//		String question = "resep apa yang baik bagi penderita diabetes"; 
	
//		String question = "apa saja bahan resep sosis saus teriyaki";
//		String question = "apa resep yang dihindari oleh penderita hipertensi";
		String question = "apa resep yang aman untuk penderita hipertensi";
//		String question = "bagaimana cara membuat udang brokoli"; lebih baik diberi bahannya juga
//		String question = "apa bahan makanan yang dapat meningkatkan resiko hipertensi";
//		String question = "apa saja yang dapat meningkatkan gejala asam urat";
//		String question = "bagaimana resep timun aceh yang bisa menurunkan hipertensi";
//		String question = "resep makanan bunda yang dapat menurunkan diabetes tinggi";
		
		
		Tokenizer tokenizer = new Tokenizer(new MySQLDatabase());
		Parser parser = new Parser();
		OntologyMapper ontologyMapper;
		try {
			ontologyMapper = new OntologyMapper(recipeOntology);
			OWLOntology ontology = ontologyMapper.getOntology();
			
			Configuration configuration=new Configuration();
			OWLReasoner reasoner = new Reasoner(configuration, ontology);
			
			OntologyQuery queryEngine = new OntologyQuery(ontologyMapper, reasoner);
			
			List<SemanticToken> tokenizerResult = tokenizer.tokenize(question);
			List<Sentence> parsingResult = parser.parse(tokenizerResult);
			Printer.cetakKlausa(parsingResult);
			List<Sentence> bufferedParseResult = Main.clone(parsingResult);
			List<Sentence> mappingResult = ontologyMapper.map(parsingResult);
			Printer.cetakMap(mappingResult);
			
			Map<String, List<? extends QueryResultModel>> queryResult = queryEngine.execute(mappingResult);  
			
			JSONObject finalResult = AnswerBuilder.json(bufferedParseResult, queryResult);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(finalResult.toString());
			String prettyJsonString = gson.toJson(je);
			
			System.out.println("\n\n--------------------------------------------------------------");
			System.out.println(prettyJsonString);
			System.out.println("--------------------------------------------------------------");
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}