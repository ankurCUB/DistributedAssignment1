package customerDB;

import common.DBServer;

import java.io.IOException;


public class CustomerDBServer extends DBServer {

    CustomerDBServer(int port, String url) throws IOException {
        super(port, url);
    }

    public static void main(String[] args) throws IOException {
        CustomerDBServer server = new CustomerDBServer(5003, "jdbc:sqlite:/Users/ankursharma/IdeaProjects/Distributed Systems/DistributedAssignment1/src/main/java/customerDB/CustomerDB.db");
        server.startServer();
    }
}
