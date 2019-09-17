package au.edu.rmit.chaos.report;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import net.sf.dynamicreports.report.builder.chart.LineChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kassem on 1/5/17.
 */
public class Report {

    protected String name;

    private ArrayList<Supply> fetchSupplyFromServer() {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/report/supply")
                    .header("accept", "application/json")
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        // retrieve the parsed JSONObject from the response
        if (!request.getBody().isArray()) {
            JSONObject json = request.getBody().getObject();
            if (json.has("error"))
                System.err.println(json.getJSONObject("error").getString("message"));
            return null;
        }
        Gson gson = new Gson();
        ArrayList<Supply> Supplies = gson.fromJson(request.getBody().getArray().toString(),
                new TypeToken<List<Supply>>() {
                }.getType());
        return Supplies;
    }

    private ArrayList<BestSelling> fetchBestSellingFromServer() {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/report/best_selling")
                    .header("accept", "application/json")
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        // retrieve the parsed JSONObject from the response
        if (!request.getBody().isArray()) {
            JSONObject json = request.getBody().getObject();
            if (json.has("error"))
                System.err.println(json.getJSONObject("error").getString("message"));
            return null;
        }
        Gson gson = new Gson();
        ArrayList<BestSelling> products = gson.fromJson(request.getBody().getArray().toString(),
                new TypeToken<List<BestSelling>>() {
                }.getType());
        return products;
    }

    private ArrayList<Sales> fetchSalesFromServer(String from, String to) {
        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest
                    .get("http://localhost/supermarket/api.php/report/sales")
                    .header("accept", "application/json")
                    .queryString("from", from)
                    .queryString("to", to)
                    .queryString("key", "519428fdced64894bb10cd90bd87167c")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        // retrieve the parsed JSONObject from the response
        if (!request.getBody().isArray()) {
            JSONObject json = request.getBody().getObject();
            if (json.has("error"))
                System.err.println(json.getJSONObject("error").getString("message"));
            return null;
        }
        Gson gson = new Gson();
        ArrayList<Sales> sales = gson.fromJson(request.getBody().getArray().toString(),
                new TypeToken<List<Sales>>() {
                }.getType());
        return sales;
    }


    public void salesReport(String from, String to) {

        JRBeanCollectionDataSource dr = new JRBeanCollectionDataSource(fetchSalesFromServer(from, to));

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        TextColumnBuilder<String> dateColumn = col.column("Date", "new_date", type.stringType());
        TextColumnBuilder<Double> salesColumn = col.column("Sales $", "sales", type.doubleType());

        try {
            report()
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .columns(col.column("Date", "new_date", type.stringType()),
                            col.column("Sales $", "sales", type.doubleType()))
                    .title(cmp.text("Sales Report from: " + from + " to " + to + "\n").setStyle(boldCenteredStyle))
                    .summary(
                            cht.lineChart()
                                    .setTitle("\n\nSales")
                                    .setCategory(dateColumn)
                                    .series(cht.serie(salesColumn))
                                    .setCategoryAxisFormat(cht.axisFormat().setLabel("Date")))

                    .setDataSource(dr)
                    .pageFooter(cmp.pageXofY())
                    .show(false);
        } catch (DRException e) {
            e.printStackTrace();
        }
    }


    public void supplyReport() {
        JRBeanCollectionDataSource dr = new JRBeanCollectionDataSource(fetchSupplyFromServer());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        try {
            report()
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .columns(col.column("Purchase Order #", "purchase_order_id", type.integerType()).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)),
                            col.column("Date", "order_date", type.stringType()),
                            col.column("Supplier", "sup_name", type.stringType()),
                            col.column("Product", "prod_name", type.stringType()),
                            col.column("Qty", "item_quantity", type.integerType()),
                            col.column("Total $", "item_total", type.doubleType()))
                    .title(cmp.text("Supply Report\n").setStyle(boldCenteredStyle))
                    .setDataSource(dr)
                    .pageFooter(cmp.pageXofY())
                    .show(false);
        } catch (DRException e) {
            e.printStackTrace();
        }
    }


    public void bestSellingReport() {
        JRBeanCollectionDataSource dr = new JRBeanCollectionDataSource(fetchBestSellingFromServer());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        try {
            report()
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .columns(col.column("Product", "product", type.stringType()),
                            col.column("Sales $", "sales", type.doubleType()))
                    .title(cmp.text("Best Selling Products\n").setStyle(boldCenteredStyle))
                    .setDataSource(dr)
                    .pageFooter(cmp.pageXofY())
                    .show(false);
        } catch (DRException e) {
            e.printStackTrace();
        }
    }
}
