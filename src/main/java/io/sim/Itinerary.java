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

	// Recebe o arquivo de rotas e separa em um ArrayList de Rotas
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

					Node node = elem.getElementsByTagName("route").item(0);
					Element edges = (Element) node;

					String idUnico = "rota_" + i;
					String edgeList = edges.getAttribute("edges");

					// Atualiza o array itinerary apenas com a última rota (mantido conforme original)
					this.itinerary = new String[] { idUnico, edgeList };

					// Adiciona a rota com ID único à lista
					listaRoutes.add(new Rota(idUnico, edgeList));
				}
			}

			Thread.sleep(100);
			this.on = true;

		} catch (SAXException | IOException | ParserConfigurationException | InterruptedException e) {
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

	public ArrayList<Rota> getRoutes() {
		return listaRoutes;
	}
}
