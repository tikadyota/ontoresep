package SemanticQA.controllers;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import SemanticQA.helpers.Printer;
import SemanticQA.constant.Ontology;
import SemanticQA.helpers.AnswerBuilder;
import SemanticQA.model.MySQLDatabase;
import SemanticQA.model.QueryResultModel;
import SemanticQA.model.SemanticToken;
import SemanticQA.model.Sentence;
import SemanticQA.module.nlp.Parser;
import SemanticQA.module.nlp.Tokenizer;
import SemanticQA.module.sw.OntologyMapper;
import SemanticQA.module.sw.OntologyQuery;

public class Tester {

	public static void main(String[] args) {

		String[] ontologies = new String[]{
//				Ontology.Path.ONTOSCHOLARSHIP, 
				Ontology.Path.ONTOPAR, 
				Ontology.Path.ONTOGEO, 
				Ontology.Path.ONTOGOV,
				Ontology.Path.DATASET,
				Ontology.Path.UNIVERSITAS
		};
		
		String question = "Pantai apa yang ada di Lombok Timur";
		
		Tokenizer tokenizer = new Tokenizer(new MySQLDatabase());
		Parser parser = new Parser();
		OntologyMapper ontologyMapper;
		try {
			ontologyMapper = new OntologyMapper(ontologies, Ontology.Path.MERGED_URI);
			OWLOntology ontology = ontologyMapper.getOntology();
			OWLReasoner reasoner = new Reasoner(ontology);
			OntologyQuery queryEngine = new OntologyQuery(ontologyMapper, reasoner);
			
			List<SemanticToken> tokenizerResult = tokenizer.tokenize(question);
			List<Sentence> parsingResult = parser.parse(tokenizerResult);
			Printer.cetakKlausa(parsingResult);
			List<Sentence> bufferedParseResult = Main.clone(parsingResult);
			List<Sentence> mappingResult = ontologyMapper.map(parsingResult);
			Printer.cetakMap(mappingResult);
			
			Map<String, List<? extends QueryResultModel>> queryResult = ( question.toLowerCase().contains("saja") ) 
					? queryEngine.execute(mappingResult) 
					: queryEngine.execute(mappingResult, OntologyQuery.theQuestionIsSingular);
			
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
