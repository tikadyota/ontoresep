/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SemanticQA.models.ontology;

import SemanticQA.listeners.OntologyLoaderListener;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyDocumentAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

/**
 *
 * @author syamsul
 */
public class OntologyLoader {
    
    private static OWLOntologyManager ontologyManager;
    private static OWLOntologyMerger ontologyMerger;
    private static OWLOntology ontology;
    
    private static List<String> ontologyLocations;
    private static String mergedURI = "";
    
    private static OntologyLoader ontologyLoader;
    
    public OntologyLoader(List<String> locations, String mergedIRI){
        ontologyManager = OWLManager.createOWLOntologyManager();
        ontologyLocations = locations;
        if(!mergedIRI.isEmpty()){
            mergedURI = mergedIRI;
        }
    }
    
    public OntologyLoader(final String location){
        this(new ArrayList<String>(){{add(location);}}, "");
    }
    
    public static OntologyLoader load(List<String> locations, String mergedIRI){
        ontologyLoader = new OntologyLoader(locations, mergedIRI);
        return ontologyLoader;
    }
    
    public static OntologyLoader load(final String location){
        return OntologyLoader.load(new ArrayList<String>(){{add(location);}},"");
    }
    
    public static void then(OntologyLoaderListener listener){
        try {
            listener.onOntologyLoaded(ontologyLoader.getOntology());
            
        } catch (OWLOntologyAlreadyExistsException | OWLOntologyDocumentAlreadyExistsException ex) {
            
            listener.onOntologyLoadFail(ex.getMessage());
            
        } catch (OWLOntologyCreationException ex) {
            
            listener.onOntologyLoadFail(ex.getMessage());
            
        }
    }
    
    public OWLOntology getOntology() throws OWLOntologyCreationIOException, 
            OWLOntologyAlreadyExistsException, OWLOntologyDocumentAlreadyExistsException, OWLOntologyCreationException {
        
        if(ontologyLocations.size() > 1){
            ontologyMerger = new OWLOntologyMerger(ontologyManager);
            
            for(String location: ontologyLocations){
                ontologyManager.loadOntologyFromOntologyDocument(IRI.create(location));
            }
            ontology = ontologyMerger.createMergedOntology(ontologyManager, IRI.create(mergedURI));
        } else {
            ontology = ontologyManager.loadOntologyFromOntologyDocument(IRI.create(ontologyLocations.get(0)));
        }
        
        return ontology;
    }
}
