package recipe.semanticweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import recipe.constant.Type;
import recipe.model.QueryResultModel;
import recipe.model.SemanticToken;
import recipe.model.Sentence;
import recipe.semanticweb.OntologyQuery.Key;

public class AnswerBuilder {
	
	private static List<String> questionConstituents;
	private static String questionString;
	
	@SuppressWarnings("unchecked")
	public static JSONObject json(List<Sentence> question, Map<String, List<? extends QueryResultModel>> results) throws Exception {		
		JSONObject jsonObject = new JSONObject();
		JSONArray inferedFacts = new JSONArray();
		
		List<String> summryText = getSubject(question);
		List<QueryResultModel> queryResultObject = (List<QueryResultModel>) results.get(Key.Result.OBJECT);
		
		if ( questionString.matches("^di.*") ) {
			summryText.add("adalah di ...");
		} else {
			summryText.add("adalah ...");
		}
		
		int i = 0;
		for ( i = 0 ; i < queryResultObject.size(); i++ ) {
			JSONObject item = new JSONObject();
			QueryResultModel resultObject = queryResultObject.get(i);
			String shortendAboutURI = shorten(resultObject.getObject());
			shortendAboutURI = normalize(shortendAboutURI);
			
			item.put("about", shortendAboutURI);
			inferedFacts.put(item);
			
//				System.out.println("DOK " + i);
//				System.out.println("shortenedURI " + shortendAboutURI);
//				summryText.add(" " + shortendAboutURI);
		}
			
		System.out.println("Dokumen yang dikembalikan: " + i + " buah");
		
		String rawSummrySentence = String.join(" ", summryText);
//			System.out.print("summaryText " + summryText.toString());
		
		String[] splittedRawSentence = rawSummrySentence.split("\\s+");
		
		StringBuffer sb = new StringBuffer();
		String prevString = "";
		
		for ( i = 0; i < splittedRawSentence.length;i++ ) {
			
			String theString = splittedRawSentence[i];
			
			////////////////////////////////////////////////////////////////////////
			// Sebelum menambahkan teks ke dalam summry teks
			// cek terlebih dahulu apakah kata terakhir sama 
			// dengan kata yang akan dimasukkan, jika sama maka abaikan teks 
			// yang bersangkutan. Tujuannya adalah untuk mencegah terjadinya 
			// redundansi kata di dalam summry teks, milsanya:
			// 
			// pantai yang ada di kabupaten lombok timur adalah pantai pantai pink
			//
			// hal ini terjadi karena individual bernama pantai_pink digabungkan 
			// dengan nama kelas pantai, untuk itu perlu di cek terlebih dahulu
			////////////////////////////////////////////////////////////////////////
			if ( i == 0 || !prevString.equals(theString.toLowerCase())) {
				
				if ( i == splittedRawSentence.length - 1 ) {
					sb.append(theString);
				} else {
					sb.append(theString + " ");					
				}
			}
			
			prevString = theString.toLowerCase();
		}
		
		try {
			jsonObject.put("text", sb.toString());
			jsonObject.put("inferedFacts", inferedFacts);
			
		} catch (JSONException e) {
			throw new Exception("Proses pembentukan objek jawaban gagal!");
		}
		
		return jsonObject;
	}
		
	private static String shorten(String uri) {
		String sf = uri.replaceAll("^[a-z].*.(#|/)", "");
		return sf;
	}
		
	private static String normalize(String str) {
			
		String arrNormalized[] = str.split("_");
		String normalized[] = new String[arrNormalized.length];
		
		for (int i = 0; i < arrNormalized.length; i++) {
			String n = arrNormalized[i];
			normalized[i] = n.substring(0, 1).toUpperCase() + n.substring(1, n.length());
		}
		String concatinatedNormalized = String.join(" ", normalized);
		return concatinatedNormalized;
	}
	
	private static List<String> getSubject(List<Sentence> question) {
			
		List<String> returnedString = new ArrayList<>();
		
		for ( int i = 0; i < question.size(); i++ ) {
			
			Sentence s = question.get(i);
			
			if ( s.getType().equals(Type.Token.PRONOMINA) || s.getType().equals(Type.Phrase.PRONOMINAL) ) {
				List<SemanticToken> constituents = s.getConstituents();
				StringBuffer sb = new StringBuffer(constituents.size());
				for ( SemanticToken t:constituents ) {
					sb.append(t.getToken());
				}
				
				questionString = sb.toString();
			}
			
			if ( !s.getType().equals(Type.Token.PRONOMINA) && 
					!s.getType().equals(Type.Phrase.PRONOMINAL) ) {
				
				List<SemanticToken> constituents = s.getConstituents();
				
				for ( SemanticToken c : constituents ) {
					String token = c.getToken();
					if ( returnedString.isEmpty() || c.getType().equals(Type.Token.NOMINA) ) {
						token = normalize(token);
					}
					returnedString.add(token); 
				}	
			}
			
			if ( s.getType().matches("(" + Type.Token.PRONOMINA + "|" + Type.Phrase.PRONOMINAL + ")") ) {
				List<SemanticToken> qConstituents = s.getConstituents();
				AnswerBuilder.questionConstituents = new ArrayList<String>(qConstituents.size());
				for ( int qsize = 0; qsize < qConstituents.size(); qsize++ ) {
					AnswerBuilder.questionConstituents.add(qConstituents.get(qsize).getToken());
				}
			}
			
		}
		return returnedString;
	}
}
