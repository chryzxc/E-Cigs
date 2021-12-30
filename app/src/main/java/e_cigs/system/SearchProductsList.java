package e_cigs.system;

public class SearchProductsList {

    private String product_id, product_type,product_name,product_description,product_store,date_added;
    private Double minimum,maximum;



    public SearchProductsList(String product_id, String product_type, String product_name, String product_description, String product_store, Double minimum, Double maximum, String date_added) {
        this.product_id = product_id;
        this.product_type = product_type;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_store = product_store;

        this.minimum = minimum;
        this.maximum = maximum;
        this.date_added = date_added;


    }

    public String getProduct_id() {

        return product_id;
    }

    public String getProduct_type() {

        return product_type;
    }

    public String getProduct_name() {

        return product_name;
    }

    public String getProduct_description() {

        return product_description;
    }

    public String getProduct_store() {

        return product_store;
    }


    public Double getMinimum(){
        return minimum;
    }

    public Double getMaximum(){
        return maximum;
    }

    public String getDate_added() {

        return date_added;
    }






}