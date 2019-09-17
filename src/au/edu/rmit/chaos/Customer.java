package au.edu.rmit.chaos;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Customer {
    private String name;
    private int id;
    private double balance = 0;
    private int points = 0;
    private boolean loggedIn = false;
    private String username = "";
    private String password = "";

    public Customer(String username, String password, boolean login) {
        this.username = username;
        this.password = password;
        if (login) {
            if (login(username, password)) {
                System.out.println("\tWelcome " + this.getName() + ", You are now logged in!");
            }
        }
    }

    public static Customer searchByUsername(String username) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/customer/search")
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .queryString("username", username)
                    .header("accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.out.println("\t" + json.getJSONObject("error").getString("message"));
            return null;
        }

        Customer customer = new Customer(username, "", false);
        customer.name = json.getString("name");
        customer.id = json.getInt("id");
        customer.username = username;
        customer.balance = json.getDouble("balance");
        customer.points = json.getInt("points");
        return customer;
    }

    public boolean login(String username, String password) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/customer/login?username=" + username + "&password="
                            + password)
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

        this.name = json.getString("name");
        this.id = json.getInt("id");
        this.balance = json.getDouble("balance");
        this.points = json.getInt("points");
        this.loggedIn = true;

        if (this.loggedIn) {
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public boolean increaseBalance(double amount) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .post("http://localhost/supermarket/api.php/customer/{id}/add_balance")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .routeParam("id", Integer.toString(this.id))
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .queryString("amount", amount)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
        // retrieve the parsed JSONObject from the response
        JSONObject json = request.getBody().getObject();
        if (json.has("error")) {
            System.out.println(json.getJSONObject("error").getString("message"));
            return false;
        }
        this.balance = json.getDouble("balance");
        return true;
    }

    public int getPoints() {
        return this.points;
    }

    public int getId() {
        return this.id;
    }

    public double getPointsDiscount(double orderSubtotal) {
        double discountAmount = Math.floor(this.points / 20.0) * 5.0;
        return discountAmount;
    }

    public boolean deductPoints(int points) {
        this.points -= points;
        return true;
    }

    /**
     * @param subtotal
     *
     * @return
     *
     * @deprecated deducted on API
     */
    public boolean deductPoints(double subtotal) {
        int points = (int) (Math.floor(this.points / 20.0)) * 20;
        this.points -= points;
        return true;
    }

    public boolean addPoints(int points) {
        this.points += points;
        return true;
    }

    public boolean addPoints(double subtotal) {
        int points = (int) (Math.floor(this.points / 20.0)) * 20;
        this.points += points;
        return true;
    }

}
