package io.sim;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Itinerary {

	private boolean on;
	private String uriItineraryXML;
	private String[] itinerary;
	private String idItinerary;
	private ArrayList<Rota> listaRoutes; 

	public Itinerary(String _uriRoutesXML, String _idRoute) {
		this.uriItineraryXML = _uriRoutesXML;
		this.idItinerary = _idRoute;
		listaRoutes = new ArrayList<Rota>();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(this.uriItineraryXML);
			NodeList nList = doc.getElementsByTagName("vehicle");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) nNode;
					String idRouteAux = this.idItinerary;
					Node node = elem.getElementsByTagName("route").item(0);
					Element edges = (Element) node;
					this.itinerary = new String[] { idRouteAux, edges.getAttribute("edges") };
					listaRoutes.add(new Rota(Integer.toString(i),edges.getAttribute("edges")));
					
				}
			}

			Thread.sleep(100);
			this.on = true;

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getIDItinerary() {
		return this.idItinerary;
	}

	public String getUriItineraryXML() {
		return this.uriItineraryXML;
	}

	public String[] getItinerary() {
		return this.itinerary;
	}

	public String getIdItinerary() {
		return this.idItinerary;
	}

	public boolean isOn() {
		return this.on;
	}

	public String[] getItinerary1(int i) {
		return this.listaRoutes.get(i).getRouteItinerary();
	}

	public String getIdItinerary1(int i) {
		return this.listaRoutes.get(i).getId();
	}

	public ArrayList<Rota> getRoutes(){
		return listaRoutes;
	}
}