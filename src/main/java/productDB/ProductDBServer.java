package productDB;

import common.DBServer;
import customerDB.CustomerDBServer;

import java.io.IOException;

import static common.Utils.PRODUCT_DB_PORT;

public class ProductDBServer extends DBServer {

    ProductDBServer(int port, String url) throws IOException {
        super(port, url);
    }

    public static void main(String[] args) throws IOException {
        ProductDBServer server = new ProductDBServer(PRODUCT_DB_PORT, "jdbc:sqlite:/Users/ankursharma/IdeaProjects/Distributed Systems/DistributedAssignment1/src/main/java/productDB/ProductDB.db");
        server.startServer();
    }
}
