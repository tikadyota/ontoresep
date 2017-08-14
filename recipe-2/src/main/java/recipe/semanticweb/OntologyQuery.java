/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.semanticweb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import recipe.constant.Type;
import recipe.model.QueryResultData;
import recipe.model.QueryResultModel;
import recipe.model.SemanticToken;
import recipe.model.Sentence;

public class OntologyQuery {

	public static final boolean theQuestionIsSingular = true;
	private QueryEngine queryEngine;
	private String queryPattern = "";
	
	private OWLObject currentSentencePredicate = null;
	
	public static abstract class Key {
		public abstract class Result {
			public static final String DATA = "data";
			public static final String OBJECT = "object";
		}
		public abstract class InferedItem {
			public static final String TYPE = "type";
			public static final String URI = "uri";
		}
	}
	
	///////////////////////////
	// variabel untuk menyimpan URI dari subjek pertanyaan
	// URI ini nantinya akan digunakan untuk melakukan query informasi tambahan 
	// tentang subjek yang bersangkutan
	// URI ini didapatkan pada saat proses pembentukan query dalam method buidQuery()
	////////////////////////////////////////////////
	private Map<String, String> inferredItem;
	private OWLReasoner reasoner;
	private OntologyMapper mapper;
	
	private static Set<OWLDataProperty> dp;
	private static Set<OWLObjectProperty> op;
	
	public OntologyQuery(OntologyMapper mapper, OWLReasoner reasoner) {
		this.queryEngine =  QueryEngine.create(mapper.ontology.getOWLOntologyManager(), reasoner);
		this.reasoner = reasoner;
		this.mapper = mapper;
		inferredItem = new HashMap<String, String>();
		dp = reasoner.getRootOntology().getDataPropertiesInSignature();
		op = reasoner.getRootOntology().getObjectPropertiesInSignature();
	}
	
	public Map<String, List<? extends QueryResultModel>> execute(List<Sentence> model) throws Exception {
		
		Map<String, List<? extends QueryResultModel>> result = new HashMap<String, List<? extends QueryResultModel>>();
		
		List<QueryResultData> listOfQueryResultData = new ArrayList<QueryResultData>();
		final List<QueryResultModel> listOfQueryResultObject = new ArrayList<QueryResultModel>();
		
		Set<OWLNamedIndividual> boundedIndividuals = new HashSet<OWLNamedIndividual>();
		
		QueryResult sparqldlQueryResult = null;
		
		try {
			Query query = buildQuery(model);
			sparqldlQueryResult = queryEngine.execute(query);
			
			for ( QueryBinding queryBinding : sparqldlQueryResult ) {
				Set<QueryArgument> args = queryBinding.getBoundArgs();
				
				for ( QueryArgument arg:args ) {
					
					QueryArgument item = queryBinding.get(arg);
					
					if ( arg.getValue().matches("(sub|ob)ject")) 
					{	
						
						String itemValue = item.getValue();
						
						IRI currentIndividualIRI = IRI.create(itemValue);			
						OWLNamedIndividual currentIndividual = mapper.dataFactory.getOWLNamedIndividual(currentIndividualIRI);						
						OWLClass amanTapi = mapper.dataFactory.getOWLClass(IRI.create("http://www.tesis.semantikweb.org/ontoresep#Aman_tapi_dihindari"));
						
						boolean isInstanceOfAmanTapiDihindari = reasoner.getTypes(currentIndividual, true).containsEntity(amanTapi);
						
						if ( isInstanceOfAmanTapiDihindari && this.currentSentencePredicate.toString().equals("<http://www.tesis.semantikweb.org/ontoresep#aman>") ) {
							continue;
						}
						
						if ( !boundedIndividuals.contains(currentIndividual) ) {
								
							final Set<OWLNamedIndividual> listOfSameIndividuals = reasoner.getSameIndividuals(currentIndividual).getEntities();
							boundedIndividuals.addAll(listOfSameIndividuals);
								
							//////////////////////////////////////////////////////////////////////////////////////////////
							// Siapkan objek QueryResultData															//
							// Objek ini akan menyimpan hasil query sparql yang berupa property dan nilai propertynya	//
							//////////////////////////////////////////////////////////////////////////////////////////////
							final QueryResultData resultModel = new QueryResultData();
								
							Runnable decidedTheSubject = new Runnable() {
								
								@Override
								public void run() {
									Iterator<OWLNamedIndividual> iter = listOfSameIndividuals.iterator();
									while( iter.hasNext() )
									{
										OWLNamedIndividual i = iter.next();
										// panggil method untuk mengambil informasi object property dari 
										// individu yang sedang di proses saat ini
										LinkedHashMap<String, String> dp_result = extractDataPropertyValue(i, dp);
										// Panggil method untuk mengambil semua nilai (range) dari object property
										// yang ber-relasi dengan individu yang sedang di proses saat ini
										LinkedHashMap<String, JSONObject> op_result = extractObjectPropertyValue(i, op);
										
										resultModel.setSubject(i.toStringID());
										resultModel.addData(dp_result);
										resultModel.addObjectData(op_result);
										
										Set<OWLClass> individualTypes = reasoner.getTypes(i, true).getFlattened();
									
										List<OWLClass> cls = new ArrayList<OWLClass>(individualTypes);
										OWLClass indvidualType = cls.get(0);									
										QueryResultModel classOfIndividualModel = new QueryResultModel();
										QueryResultModel individualModel = new QueryResultModel();
										classOfIndividualModel.setObject(indvidualType.toStringID());
										individualModel.setObject(i.toStringID());
										
										//////////
										// masukkan daftar hasil query sparql untuk masing-masing individu ke dalam 
										// array list queryResultObject
										///////////
										listOfQueryResultObject.add(individualModel);												
									}
								}
							};
					
							Thread decideTheSubjectThread = new Thread(decidedTheSubject);
							decideTheSubjectThread.start();
							decideTheSubjectThread.join();
							
							//////////////////////////////////////////////////////////////////////////////////////
							// Jika individual berasal dari variabel ?subject maka pastikan ia berada 			//
							// di array paling depan supaya hasil summryText sesuai dengan konteks pertanyaan	//
							//////////////////////////////////////////////////////////////////////////////////////
							if ( arg.getValue().equals("subject") ) {
								listOfQueryResultData.add(0, resultModel);
							} else {
								listOfQueryResultData.add(resultModel);
							}					
						}
					}
				}
			}
			
			String item = inferredItem.get(Key.InferedItem.URI);
			String ii = item.substring(1, item.length() - 1);
			IRI iiIRI = IRI.create(ii);
			
			OWLNamedIndividual mainIndividu = mapper.dataFactory.getOWLNamedIndividual(iiIRI);
		
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Khusus untuk model query PS atau SP yang hanya memiliki mapping Individual (tanpa ada kelas atau properti)	//
			// misalnya "siapakah ali bin dahlan", maka tambahkan nama kelas ke dalam array listOfQueryResultObject			//
			// Hal ini dilakukan karena hasil summery text akan menjadi "ali bin dahlan adalah kabupaten lombok timur"		//
			// karena tidak ada nama kelas. sehingga untuk memperbaikinya maka harus ditambahkan 							//
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if ( queryPattern.matches("I") ) {
				QueryResultModel m = new QueryResultModel();
				Set<OWLClass> classes = reasoner.getTypes(mainIndividu, true).getFlattened();
				for ( OWLClass c:classes ) {
					m.setObject(c.toStringID());
					break;
				}
				
				listOfQueryResultObject.add(0, m);
			}
			
			QueryResultData mainItemModel = new QueryResultData();
			mainItemModel.setSubject(ii);
			
			if ( inferredItem.get(Key.InferedItem.TYPE).equals("subject") ){
				listOfQueryResultData.add(0, mainItemModel);
			} else {
				listOfQueryResultData.add(mainItemModel);			
			}
			
			result.put(Key.Result.DATA, listOfQueryResultData);
			result.put(Key.Result.OBJECT, listOfQueryResultObject);			
			
		} catch (QueryParserException | QueryEngineException e) {
			throw new Exception("Tidak dapat melakukan query terhadap basis pengetahuan!");
		}
		
		return result;
	}
	

	private Query buildQuery(List<Sentence> model) throws Exception {
		
		String analyzedQuery = "";
		List<OWLObject> listOfObjects = new ArrayList<OWLObject>();
		
		for ( int modelIndex = 0; modelIndex < model.size(); modelIndex++ ) {
			
			Sentence m = model.get(modelIndex);
			List<SemanticToken> tm = m.getConstituents();
			
			String prevTokenType = null;
			
			if ( !m.getType().matches("("+Type.Phrase.PRONOMINAL + "|" + Type.Token.PRONOMINA + ")") ) {
				
				for ( SemanticToken t:tm ) {
					switch (t.getOWLType()) {
					case Type.Ontology.CLASS:
						if ( queryPattern.matches("C")){
							if ( !prevTokenType.equals(Type.Token.NOMINA) ){
								listOfObjects.set(listOfObjects.size() - 1, t.getOWLPath());								
							}
							
						} else {
							queryPattern += "C";							
							listOfObjects.add(t.getOWLPath());
						}
						break;
					case Type.Ontology.OBJECT_PROPERTY:
						queryPattern += "OP";
						listOfObjects.add(t.getOWLPath());
						break;
					case Type.Ontology.DATATYPE_PROPERTY:
						queryPattern += "DP";
						listOfObjects.add(t.getOWLPath());
						break;
					case Type.Ontology.INDIVIDUAL:
						queryPattern += "I";
						listOfObjects.add(t.getOWLPath());
						break;
					}
					
					prevTokenType = t.getType();
				}
				
	
				switch (queryPattern) {
				case "CI":
					inferredItem.put(Key.InferedItem.TYPE, "object");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(0).toString());
					
					analyzedQuery = "{\n"
							+ "Type( ?subject ," + listOfObjects.get(0) +"),\n"
							+ "PropertyValue(?subject, ?op," + listOfObjects.get(1) +")"
							+ "\n}";
					break;
				
				case "COPI":
					inferredItem.put(Key.InferedItem.TYPE, "object");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(0).toString());
					this.currentSentencePredicate = listOfObjects.get(1);
					
					analyzedQuery = "{\n"
							+ "Type( ?subject ," + listOfObjects.get(0) +"),\n"
							+ "PropertyValue(?subject, " + listOfObjects.get(1) + ", " + listOfObjects.get(2) +")"
							+ "\n}";
					
//					String cls = listOfObjects.get(0).toString();
//					if(cls.equals("<http://www.tesis.semantikweb.org/ontoresep#Resep>")){
//						analyzedQuery = "{\n"
//								+ "Type( ?subject ," + listOfObjects.get(0) +"),\n"
//								+ "PropertyValue(?subject, " + listOfObjects.get(1) + ", " + listOfObjects.get(2) +")"
//								+ "\n}";
//					}
					
					break;
					
				case "OPCCI":
					inferredItem.put(Key.InferedItem.TYPE, "object");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
					
					analyzedQuery = "{\n"
							+ "Type(" + listOfObjects.get(3) + ", " + listOfObjects.get(1) +"),\n"
							+ "PropertyValue(" + listOfObjects.get(3) + ", " + listOfObjects.get(0) + ", ?object),\n"
							+ "\n}";					
				break;
				
				case "OPCI":
					inferredItem.put(Key.InferedItem.TYPE, "object");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
					
					analyzedQuery = "{\n"
							+ "PropertyValue("+ listOfObjects.get(2) + "," + listOfObjects.get(0) + ",?object)"
							+ "\n}";				
				break;
				
				case "DPCI":
					inferredItem.put(Key.InferedItem.TYPE, "object");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
					
					analyzedQuery = "{\n"
							+ "DataProperty(" + listOfObjects.get(0) +"),\n"
							+ "PropertyValue("+ listOfObjects.get(2) + "," + listOfObjects.get(0) + ",?object)"
							+ "\n}";				
				break;
				
			}
		}
	}
	
		String query = "select * where " + analyzedQuery;
	
		System.out.println("\nQuery Pattern : " + queryPattern);
		System.out.println("\nQuery : " + query);
		
		return Query.create(query);
	}
	
	private LinkedHashMap<String, String> extractDataPropertyValue(OWLNamedIndividual individu, Set<OWLDataProperty> prop) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for( OWLDataProperty d:prop ) {
			Set<OWLLiteral> lit = reasoner.getDataPropertyValues(individu, d);
			if( !lit.isEmpty() ){
				String namaProperty = normalize(d.toStringID());
				for(OWLLiteral l: lit) {
					String value = l.getLiteral();
					result.put(namaProperty, value);
				}
			}
		}
		
		return result;
	}
	
	private LinkedHashMap<String, JSONObject> extractObjectPropertyValue(OWLNamedIndividual individu, Set<OWLObjectProperty> prop) {
		LinkedHashMap<String, JSONObject> result = new LinkedHashMap<String, JSONObject>();
		for( OWLObjectProperty o:prop ) {
			String normalized_op = normalize(o.toString());			
			Set<OWLNamedIndividual> ind = reasoner.getObjectPropertyValues(individu, (OWLObjectPropertyExpression) o).getFlattened();
			if( !ind.isEmpty() ){
				for( OWLNamedIndividual val: ind ) {
					LinkedHashMap<String, String> res = this.extractDataPropertyValue(val, dp);
					System.out.println(normalized_op);
					result.put(normalized_op, new JSONObject(res));
				}
				
			}
		}
		
		return result;
	}
	
	private static String normalize(String uri) {
		return uri.replaceAll("[a-z<].*#", "").replaceAll(">", "");
	}
}