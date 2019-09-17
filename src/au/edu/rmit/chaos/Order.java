package au.edu.rmit.chaos;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Order {
    private int id = 0;
    private long date = 0;
    private double total = 0; // after applying discount
    private double subtotal = 0; // before discount
    private double discount = 0; // total discount
    private double pointsDiscount = 0; // customer's points discount
    private Customer customer = null;
    private ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
    private OrderStatus status = OrderStatus.pending;


    public Order(Customer customer) {
        this.customer = customer;
    }

    public Order() {

    }


    public boolean createNewOrder() throws UnirestException {
        HttpResponse<JsonNode> request = null;
        request = Unirest.post("http://localhost/supermarket/api.php/order/add").header("accept", "application/json")
                .header("Content-Type", "application/json").queryString("key", "519428fdced64894bb10cd90bd87167c")
                .queryString("customer_id", customer.getId()).asJson();
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.out.println("\t" + json.getJSONObject("error").getString("message"));
            return false;
        }
        this.id = json.getJSONObject("responce").getInt("order_id");
        return true;
    }

    public boolean getOrderByID(int orderID) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/order/{id}")
                    .routeParam("id", Integer.toString(orderID))
                    .queryString("key", "519428fdced64894bb10cd90bd87167c").header("accept", "application/json")
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

        json = json.getJSONObject("order");

        this.id = json.getInt("order_id");
        this.date = json.getInt("order_id");
        this.subtotal = json.getDouble("subtotal");
        this.total = json.getDouble("total");
        this.status = OrderStatus.valueOf(json.getString("status"));
        this.pointsDiscount = json.getDouble("discount");

        JSONArray items = json.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject JsonItem = items.getJSONObject(i);

            String prName = JsonItem.getJSONObject("product").getString("name");
            int prID = JsonItem.getJSONObject("product").getInt("id");
            UnitType type = UnitType.valueOf(JsonItem.getJSONObject("product").getString("type"));

            Product pr = new Product(prID, prName, 0, 0, 0, type, null);

            OrderItem item = new OrderItem(pr, JsonItem.getInt("quantity"));
            item.setTotal(JsonItem.getDouble("item_total"));
            item.setDiscount(JsonItem.getDouble("item_discount"));
            this.orderItems.add(item);
        }
        return true;
    }

    public static boolean cancelOrder(int orderID) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("http://localhost/supermarket/api.php/order/{id}/cancel")
                    .header("accept", "application/json").header("Content-Type", "application/json")
                    .routeParam("id", Integer.toString(orderID))
                    .queryString("key", "519428fdced64894bb10cd90bd87167c").asJson();
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
        return true;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public double getTotal() {
        return total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getPointsDiscount() {
        return pointsDiscount;
    }

    public int getID() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public boolean addProduct(Product pr, int qty) {
        if (this.status == OrderStatus.placed) {
            System.err.println("Cannot add products to already placed orders");
            return false;
        }
        if (this.status == OrderStatus.canceled) {
            System.err.println("Cannot add products to a canceled order");
            return false;
        }

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("http://localhost/supermarket/api.php/order/{id}/add_item")
                    .header("accept", "application/json").routeParam("id", Integer.toString(this.id))
                    .header("Content-Type", "application/json").queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .queryString("product_id", pr.getID()).queryString("quantity", qty).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.err.println(json.getJSONObject("error").getString("message"));
            return false;
        }

        // TODO: update item info if exists (parse response from API)
        boolean productExist = false;
        for (OrderItem item : orderItems) {
            if (item.getProduct().equals(pr))
                productExist = true;
        }
        if (!productExist) {
            OrderItem item = new OrderItem(pr, qty);
            this.orderItems.add(item);
        }
        return true;
    }

    public boolean removeProduct(OrderItem item) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("http://localhost/supermarket/api.php/order/{id}/remove_item")
                    .header("accept", "application/json").header("Content-Type", "application/json")
                    .routeParam("id", Integer.toString(this.getID()))
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .queryString("product_id", Integer.toString(item.product.getID())).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.err.println("\t" + json.getJSONObject("error").getString("message"));
            return false;
        }
        return true;
    }

    public boolean cancelOrder() {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("http://localhost/supermarket/api.php/order/{id}/cancel")
                    .header("accept", "application/json").header("Content-Type", "application/json")
                    .routeParam("id", Integer.toString(this.getID()))
                    .queryString("key", "519428fdced64894bb10cd90bd87167c").asJson();
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
        return true;
    }

    public OrderStatus placeOrder() {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("http://localhost/supermarket/api.php/order/{id}/place")
                    .header("accept", "application/json").routeParam("id", Integer.toString(this.getID()))
                    .header("Content-Type", "application/json").queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .queryString("customer_id", this.customer.getId()).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return OrderStatus.unknown;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            String msg = json.getJSONObject("error").getString("message");
            if (msg.contains("canceled")) {
                return OrderStatus.already_canceled;
            } else if (msg.contains("placed")) {
                return OrderStatus.already_placed;
            } else if (msg.contains("balance")) {
                return OrderStatus.insufficient_balance;
            }
            return OrderStatus.unknown;
        }
        this.subtotal = json.getJSONObject("order").getDouble("subtotal");
        this.total = json.getJSONObject("order").getDouble("total");
        this.pointsDiscount = json.getJSONObject("order").getDouble("discount");
        this.status = OrderStatus.valueOf(json.getJSONObject("order").getString("status"));

        JSONArray items = json.getJSONObject("order").getJSONArray("items");

        ArrayList<OrderItem> removedItems = new ArrayList<OrderItem>();

        // update total for each item
        for (OrderItem item : orderItems) {
            boolean itemExists = false;
            for (int i = 0; i < items.length(); i++) {
                if (item.product.getID() == items.getJSONObject(i).getJSONObject("product").getInt("id")) {
                    item.setTotal(items.getJSONObject(i).getDouble("item_total"));
                    item.setDiscount(items.getJSONObject(i).getDouble("item_discount"));
                    this.discount += item.getDiscount();
                    item.quantity = items.getJSONObject(i).getInt("quantity");
                    itemExists = true;
                }
            }
            if (!itemExists)
                removedItems.add(item);
        }
        this.orderItems.removeAll(removedItems);

        return this.status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

}