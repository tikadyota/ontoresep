/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.semanticweb;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyLoader {
	
	protected OWLOntology ontology;
	protected OWLOntologyManager manager;
	protected OWLDataFactory dataFactory; 
	
	public OntologyLoader(String path) throws Exception {
		this.manager = OWLManager.createOWLOntologyManager();
		
		try {
			IRI ontologyIRI = IRI.create(path);
			ontology = manager.loadOntology(ontologyIRI);
			dataFactory = manager.getOWLDataFactory();
			
		} catch (OWLOntologyCreationException e) {
			throw new Exception("Load Ontologi gagal!");
		}
	}
	
	public OWLOntology getOntology() {
		return this.ontology;
	}
	
	public OWLOntologyManager getOntologyManager() {
		return this.manager;
	}
}
