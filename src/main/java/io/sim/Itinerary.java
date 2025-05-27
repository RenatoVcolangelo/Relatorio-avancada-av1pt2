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

    // Indica se o carregamento das rotas foi concluído com sucesso
    private boolean on;

    // Caminho do arquivo XML contendo as rotas
    private String uriItineraryXML;

    // Vetor contendo apenas a última rota lida (pouco utilizado)
    private String[] itinerary;

    // Identificador da instância da Itinerary (não interfere diretamente nas rotas)
    private String idItinerary;

    // Lista de rotas extraídas do XML, cada uma representada como um objeto da classe Rota
    private ArrayList<Rota> listaRoutes;

    /**
     * Construtor da classe Itinerary.
     * Recebe o caminho do arquivo XML de rotas e um identificador opcional.
     * Lê o arquivo XML, extrai as rotas e cria objetos Rota.
     */
    public Itinerary(String _uriRoutesXML, String _idRoute) {
        this.uriItineraryXML = _uriRoutesXML;
        this.idItinerary = _idRoute;
        listaRoutes = new ArrayList<Rota>();

        try {
            // Cria uma factory e um construtor para ler o documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Lê o documento XML do caminho especificado
            Document doc = builder.parse(this.uriItineraryXML);

            // Obtém todos os elementos <vehicle> do XML
            NodeList nList = doc.getElementsByTagName("vehicle");

            // Itera sobre cada veículo para extrair a rota
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;

                    // Acessa o elemento <route> dentro de <vehicle>
                    Node node = elem.getElementsByTagName("route").item(0);
                    Element edges = (Element) node;

                    // Gera um ID único para cada rota (ex: rota_0, rota_1...)
                    String idUnico = "rota_" + i;
                    String edgeList = edges.getAttribute("edges");

                    // Atualiza o array itinerary (mantém apenas a última rota lida)
                    this.itinerary = new String[] { idUnico, edgeList };

                    // Cria um objeto Rota e adiciona à lista
                    listaRoutes.add(new Rota(idUnico, edgeList));
                }
            }

            // Espera 100ms ( útil para sincronização em simulação)
            Thread.sleep(100);

            // Marca o carregamento como concluído com sucesso
            this.on = true;

        } catch (SAXException | IOException | ParserConfigurationException | InterruptedException e) {
            e.printStackTrace(); // Exibe qualquer erro durante o parsing do XML
        }
    }

    // Retorna o identificador da itinerary
    public String getIDItinerary() {
        return this.idItinerary;
    }

    // Retorna o caminho do arquivo XML utilizado
    public String getUriItineraryXML() {
        return this.uriItineraryXML;
    }

    // Retorna a última rota lida (ID e edges)
    public String[] getItinerary() {
        return this.itinerary;
    }

    // Retorna o ID da itinerary
    public String getIdItinerary() {
        return this.idItinerary;
    }

    // Indica se o carregamento foi concluído com sucesso
    public boolean isOn() {
        return this.on;
    }

    // Retorna a lista de rotas carregadas
    public ArrayList<Rota> getRoutes() {
        return listaRoutes;
    }
}
