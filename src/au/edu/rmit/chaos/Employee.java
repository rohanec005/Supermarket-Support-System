package au.edu.rmit.chaos;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Employee {
    private String name = "";
    private int id = 0;
    private String username = "";
    private String password = "";
    private EmployeeRole role;
    boolean loggedIn = false;

    public Employee(String username, String password) {
        if (login(username, password)) {
            System.out.println("\tWelcome " + this.getName() + ", You are now logged in!");
        }
    }

    public boolean login(String username, String password) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/employee/login?username=" + username + "&password="
                            + password)
                    .header("accept", "application/json").queryString("key", "519428fdced64894bb10cd90bd87167c")
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
        this.setID(json.getInt("id"));
        this.name = json.getString("name");
        this.role = EmployeeRole.valueOf(json.getString("role"));
        this.loggedIn = true;

        if (this.loggedIn) {
            return true;
        }
        return false;
    }

    public EmployeeRole getRole() {
        return this.role;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
