package customerDB;

import common.DBServer;

import java.io.IOException;

import static common.Utils.CUSTOMER_DB_PORT;


public class CustomerDBServer extends DBServer {

    CustomerDBServer(int port, String url) throws IOException {
        super(port, url);
    }

    public static void main(String[] args) throws IOException {
        CustomerDBServer server = new CustomerDBServer(CUSTOMER_DB_PORT, "jdbc:sqlite:/Users/ankursharma/IdeaProjects/Distributed Systems/DistributedAssignment1/src/main/java/customerDB/CustomerDB.db");
        server.startServer();
    }
}
