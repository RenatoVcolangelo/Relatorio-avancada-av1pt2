package io.sim;

public class Rota {

    private String idRoute;
    private String edge;

    public Rota(String id, String edge){
        this.idRoute = id;
        this.edge = edge;
    }

    public String getId(){
        return idRoute;
    }
    public String getEdge(){
        return edge;
    }

    public String[] getRouteItinerary(){
        return new String[] { this.idRoute, this.edge};
    }
}
