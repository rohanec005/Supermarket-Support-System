package au.edu.rmit.chaos;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PurchaseOrder {
    private int id;
    private long date;
    private Supplier supplier;
    private Employee employee;
    private ArrayList<PurchaseOrderItem> orderItems = new ArrayList<PurchaseOrderItem>();

    public PurchaseOrder(Supplier supplier, Employee employee) {
        this.supplier = supplier;
        this.employee = employee;
    }

    public boolean addProduct(Product pr, int qty) {
        PurchaseOrderItem item = new PurchaseOrderItem(pr, qty);
        this.orderItems.add(item);
        return true;
    }

    public boolean placeOrder() {
        this.date = System.currentTimeMillis() / 1000l;
        Gson gson = new Gson();
        PurchaseOrder pr = this;
        String jsonInString = gson.toJson(pr);

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .post("http://localhost/supermarket/api.php/purchaseorder")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .body(jsonInString)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.out.println("\t" + json.getJSONObject("error").getString("message"));
            return false;
        }

        // increase local products quantity
        for (PurchaseOrderItem oi : orderItems) {
            oi.getProduct().increaseStockLevel(oi.getQuantity());
        }
        return true;
    }
}
