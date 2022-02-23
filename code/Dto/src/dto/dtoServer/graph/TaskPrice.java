package dto.dtoServer.graph;

public class TaskPrice {
    public String name;
    public int price;

    public TaskPrice (String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
