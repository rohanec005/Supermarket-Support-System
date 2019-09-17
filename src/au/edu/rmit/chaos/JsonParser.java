package au.edu.rmit.chaos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser {

    public static Discount parseDiscount(JSONObject jsonDiscount) {
        return new Discount(jsonDiscount.getInt("percentage"), jsonDiscount.getInt("quantity"));
    }

    public static Product parseProduct(JSONObject jsonProduct) {
        return new Product(jsonProduct.getInt("id"), jsonProduct.getString("name"), jsonProduct.getDouble("unit_price"),
                jsonProduct.getInt("stock_level"), jsonProduct.getInt("replenish_level"),
                UnitType.valueOf(jsonProduct.getString("type")), parseDiscounts(jsonProduct.getJSONArray("discounts")));
    }

    public static ArrayList<Discount> parseDiscounts(JSONArray jsonDiscounts) {
        ArrayList<Discount> discounts = new ArrayList<Discount>();
        for (int k = 0; k < jsonDiscounts.length(); k++) {
            discounts.add(parseDiscount(jsonDiscounts.getJSONObject(k)));
        }
        return discounts;
    }

    public static ArrayList<Product> parseProducts(JSONArray jsonProducts) {
        ArrayList<Product> products = new ArrayList<Product>();
        for (int j = 0; j < jsonProducts.length(); j++) {
            JSONObject product = jsonProducts.getJSONObject(j);
            products.add(parseProduct(product));
        }
        return products;
    }

    public static ArrayList<Supplier> parseSuppliers(JSONArray jsonSuppliers) {
        ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
        for (int i = 0; i < jsonSuppliers.length(); i++) {
            JSONObject jsonSup = jsonSuppliers.getJSONObject(i);
            suppliers.add(parseSupplier(jsonSup));
        }
        return suppliers;
    }

    public static Supplier parseSupplier(JSONObject jsonSupplier) {
        return new Supplier(jsonSupplier.getInt("id"), jsonSupplier.getString("name"), jsonSupplier.getString("address"),
                jsonSupplier.getInt("postcode"), jsonSupplier.getString("phone"), parseProducts(jsonSupplier.getJSONArray("products")));

    }
}
