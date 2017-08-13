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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
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
	
	public static abstract class Key {
		public abstract class Result {
			public static final String DATA = "data";
			public static final String OBJECT = "object";
			public static final String OBJECT1 = "subObject1";
			public static final String OBJECT2 = "subObject2";
			public static final String OBJECT3 = "subObject3";
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
	
	public OntologyQuery(OntologyMapper mapper, OWLReasoner reasoner) {
		this.queryEngine =  QueryEngine.create(mapper.ontology.getOWLOntologyManager(), reasoner);
		this.reasoner = reasoner;
		this.mapper = mapper;
		inferredItem = new HashMap<String, String>();
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
						if ( arg.getValue().matches("(sub|ob|)ject|subObject1|subObject2|subObject3")) {		
						String itemValue = item.getValue();
						IRI currentIndividualIRI = IRI.create(itemValue);
						OWLNamedIndividual currentIndividual = mapper.dataFactory.getOWLNamedIndividual(currentIndividualIRI);
						
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
							for (Iterator<OWLNamedIndividual> indv = listOfSameIndividuals.iterator(); indv.hasNext();) {
								OWLNamedIndividual i = indv.next();
								if ( i.toString().matches("^(<?http://tesis.semantikweb.org/ontoresep)/*") || !indv.hasNext()) {
										resultModel.setSubject(i.toStringID());
										Set<OWLClass> individualTypes = reasoner.getTypes(i, true).getFlattened();
							
										for ( OWLClass indvidualType:individualTypes ) {
							
											QueryResultModel classOfIndividualModel = new QueryResultModel();
											QueryResultModel individualModel = new QueryResultModel();
											classOfIndividualModel.setObject(indvidualType.toStringID());
											individualModel.setObject(i.toStringID());
											
											//////////
											// masukkan daftar hasil query sparql untuk masing-masing individu ke dalam 
											// array list queryResultObject
											///////////
											listOfQueryResultObject.add(individualModel);												
											
											break;
										}
										break;
								}
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
			
//			result.put(Key.Result.DATA, listOfQueryResultData);
			result.put(Key.Result.OBJECT, listOfQueryResultObject);			
			
		} catch (QueryParserException | QueryEngineException e) {
			throw new Exception("Tidak dapat melakukan query terhadap basis pengetahuan!");
		}
		
		return result;
	}
	
//	private LinkedHashMap<String, String> doSPARQLQuery(Set<OWLNamedIndividual> individuals) throws Exception {
//		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
//		
//		Repository repo = null;
//		RepositoryConnection repositoryConnection = null;
//		String query = "";
//		
//		for ( OWLNamedIndividual individual : individuals ) {
//			
//			String path = individual.toString();
//			
//			if ( path.matches("^(<http://id.dbpedia.org).*") ) {
//				
//				try {
//					repo = new SPARQLRepository(Ontology.Path.DBPEDIA_ENDPOINT);
//					repo.initialize();
//					repositoryConnection = repo.getConnection();
//					///////////////
//					// objek path tidak perlu ditambahkan tanda < dan > karena
//					// hasil konversi OWLNamedIndividual menjadi string sudah otomatis memiliki 
//					// tanda < dan >
//					////////////////					
//					query = "SELECT * WHERE {\n"
//							+ path + " ?prop ?value .\n"
//							+ "FILTER(regex(?prop, \"" + DBPEDIA_PROPERTY_FILTER + "\", \"i\"))\n"
//							+ "}";
//					
//				} catch (RepositoryException e) {
//					throw new Exception("Gagal melakukan koneksi dengan server DBPedia");
//				}
//			} else {				
//				try {
//					repo = new SailRepository(new MemoryStore());
//					repo.initialize();
//					
//					URL localRDFFile = null;
//					String localPath = "http://semanticweb.techtalk.web.id/dataset";
//					RDFFormat format = null;
//					if ( individual.toStringID().matches("^<?[a-z0-9]+.*.(techtalk).*") ) {
//						localRDFFile = new URL(Ontology.Path.DATASET);
//						format = RDFFormat.TURTLE;
//					} else {
//						localRDFFile = new URL(Ontology.Path.UNIVERSITAS);
//						format = RDFFormat.RDFXML;
//					}
//									
//					repositoryConnection = repo.getConnection();
//					repositoryConnection.add(localRDFFile, localPath, format);
//					
//					///////////////
//					// objek path tidak perlu ditambahkan tanda < dan > karena
//					// hasil konversi OWLNamedIndividual menjadi string sudah otomatis memiliki 
//					// tanda < dan >
//					////////////////
//					query = "SELECT * WHERE {\n"
//							+ path + " ?prop ?value .\n"
//							+ "}";
//					
//				} catch (Exception e) {
//					throw new Exception("Gagal melakukan koneksi dengan server DATASET");
//				}
//			}
//			
//			try {
//				TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(query);
//				
//				try(TupleQueryResult queryResult = tupleQuery.evaluate()) {
//					List<String> boundVariables = queryResult.getBindingNames();
//					
//					if ( queryResult.hasNext() ) {
//						
//						while ( queryResult.hasNext() ) {
//							
//							BindingSet bs = queryResult.next();
//							
//							Value prop = bs.getValue(boundVariables.get(0));
//							Value val = bs.getValue(boundVariables.get(1));
//							
//							////////
//							// ambil hanya property yang memiliki nilai
//							///////
//							if ( val.stringValue().matches("[a-zA-Z0-9]+.*") ) {
//								result.put(prop.stringValue(), val.stringValue());
//							}
//						}
//					}
//					
//				} catch (Exception e) {
//					// jangan throw exception karena jika koneksi ke DBPedia gagal
//					// akan mengakibatkan gagal secara keseluruhan 
//					// termasuk proses query lokal repository
//				}
//				
//			} catch (OpenRDFException e) {
//				throw new Exception("Gagal membentuk query SPARQL");
//			}
//			
//			repositoryConnection.close();
//			repo.shutDown();
//		}
//		
//		
//		return result;
//	}

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
					inferredItem.put(Key.InferedItem.TYPE, "subObject1");
					inferredItem.put(Key.InferedItem.TYPE, "subObject2");
					inferredItem.put(Key.InferedItem.TYPE, "subObject3");
					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
					
					analyzedQuery = "{\n"
							+ "Type(" + listOfObjects.get(3) + ", " + listOfObjects.get(1) +"),\n"
							+ "PropertyValue(" + listOfObjects.get(3) + ", " + listOfObjects.get(0) + ", ?anonymous),\n"
							+ "PropertyValue(?anonymous, <http://www.tesis.semantikweb.org/ontoresep#nama_bahan>, ?subObject1),\n"
							+ "DataProperty(<http://www.tesis.semantikweb.org/ontoresep/dp#jumlah>),\n"
							+ "PropertyValue(?anonymous, <http://www.tesis.semantikweb.org/ontoresep/dp#jumlah>, ?subObject2),\n"
							+ "DataProperty(<http://www.tesis.semantikweb.org/ontoresep/dp#satuan>),\n"
							+ "PropertyValue(?anonymous, <http://www.tesis.semantikweb.org/ontoresep/dp#satuan>, ?subObject3)\n"
							+ "\n}";					
				break;
				
//				case "DPCOPI":	
//					
//					inferredItem.put(Key.InferedItem.TYPE, "object");
//					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(3).toString());
//					
//					analyzedQuery = "{\n"
//							+ "Type( ?subject ," + listOfObjects.get(1) +"),\n"
//							+ "PropertyValue(?subject," + listOfObjects.get(2) + "," + listOfObjects.get(3) +")"
//							+ "\n}";
//										
//				break;
//				
//				case "OPI":
//					
//					inferredItem.put(Key.InferedItem.TYPE, "object");
//					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(1).toString());
//					
//					analyzedQuery = "{\n"
//							+ "PropertyValue( ?subject, " + listOfObjects.get(0) + "," + listOfObjects.get(1) + ")"
//							+ "\n}";
//										
//				break;
//				
//				case "COPI":
//				case "COPIC":
//					inferredItem.put(Key.InferedItem.TYPE, "object");
//					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
//					
//					analyzedQuery = "{\n"
//							+ "Type( ?subject ," + listOfObjects.get(0) +"),\n"
//							+ "PropertyValue( ?subject, " + listOfObjects.get(1) + "," + listOfObjects.get(2) + ")"
//							+ "\n}";
//										
//				break;
								
//				case "DPCOPCI":
//					
//					inferredItem.put(Key.InferedItem.TYPE, "object");
//					inferredItem.put(Key.InferedItem.URI, listOfObjects.get(4).toString());
//					
//					analyzedQuery = "{\n"
//							+ "Type( ?subject ," + listOfObjects.get(1) +"),\n"
//							+ "PropertyValue(?subject," + listOfObjects.get(2) + "," + listOfObjects.get(4) +")"
//							+ "\n}";
//										
//				break;
//								
//				
//				case "DPCC":
//				
//				inferredItem.put(Key.InferedItem.TYPE, "object");
//				inferredItem.put(Key.InferedItem.URI, listOfObjects.get(2).toString());
//				
//				analyzedQuery = "{\n"
//						+ "Type( ?subject, " + listOfObjects.get(2) +")}";
//									
//			break;
				
			}
		}
	}
	
		String query = "select * where " + analyzedQuery;
	
		System.out.println("\nQuery Pattern : " + queryPattern);
		System.out.println("\nQuery : " + query);
		
		return Query.create(query);
	}
}