package au.edu.rmit.chaos;

import java.util.ArrayList;

import org.json.JSONArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Supplier {

    private int id;
    private String name;
    private String address;
    private int postcode;
    private String phone;
    private ArrayList<Product> products;

    public Supplier(int id, String name, String address, int postcode, String phone, ArrayList<Product> products) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.phone = phone;
        this.products = products;
    }

    /**
     * <p>
     * Retrieves all suppliers from the server
     * </p>
     * <p>
     * You will need to keep track of local changes made in the code
     *
     * @return list of suppliers with their products
     */

    public static ArrayList<Supplier> fetchSuppliersFromServer() {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.get("http://localhost/supermarket/api.php/supplier").header("accept", "application/json")
                    .queryString("key", "519428fdced64894bb10cd90bd87167c").asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        // retrieve the parsed JSONObject from the response
        JSONArray jsonArray = request.getBody().getArray();

        // no data
        if (jsonArray == null)
            return null;

        // error response
        if (jsonArray.getJSONObject(0).has("error")) {
            System.err.println(jsonArray.getJSONObject(0).getJSONObject("error").getString("message"));
            return null;
        }

        // retrieve suppliers
        return JsonParser.parseSuppliers(jsonArray);
    }

    /**
     * <p>
     * Update supplier's information
     * <p>
     * <p>
     * Only variables with data will be changed in the system.<br>
     * For string data, pass "" to keep as is<br>
     * For numbers, pass 0 to keep as is
     *
     * @param name     the name of the
     * @param address
     * @param postcode
     * @param phone
     *
     * @return
     */
    public boolean editSupplier(String name, String address, int postcode, String phone) {

        name = name.isEmpty() ? this.name : name;
        address = address.isEmpty() ? this.address : address;
        postcode = postcode == 0 ? this.postcode : postcode;
        phone = phone.isEmpty() ? this.phone : phone;

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.get("http://localhost/supermarket/api.php/supplier/{id}/update")
                    .header("accept", "application/json").routeParam("id", Integer.toString(this.id))
                    .queryString("name", name).queryString("address", address).queryString("postcode", postcode)
                    .queryString("phone", phone).queryString("key", "519428fdced64894bb10cd90bd87167c").asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
        // retrieve the parsed JSONObject from the response
        JSONArray jsonArray = request.getBody().getArray();

        // no data
        if (jsonArray == null)
            return false;

        // error response
        if (jsonArray.getJSONObject(0).has("error")) {
            System.err.println(jsonArray.getJSONObject(0).getJSONObject("error").getString("message"));
            return false;
        }
        Supplier sup = JsonParser.parseSupplier(jsonArray.getJSONObject(0));
        this.name = sup.getName();
        this.address = sup.getAddress();
        this.postcode = sup.getPostcode();
        this.phone = sup.getPhone();
        return true;
    }

    public ArrayList<Supplier> getAllSuppliers() {
        return fetchSuppliersFromServer();
    }

    /**
     * Get all products provided by the supplier
     *
     * @return products provided by the supplier
     */
    public ArrayList<Product> getSupplierProducts() {
        return this.products;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPostcode() {
        return postcode;
    }

    public String getPhone() {
        return phone;
    }

}
