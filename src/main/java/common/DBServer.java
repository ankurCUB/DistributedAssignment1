package common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

public abstract class DBServer extends Server{

    protected final String url;

    public DBServer(int port, String url) throws IOException {
        super(port);
        this.url = url;
        // TODO: Add create DB if not exists
    }

    @Override
    protected String processClientRequest(String request) {
        try {
            Connection connection = DriverManager.getConnection(url);
            ResultSet resultSet = connection.createStatement().executeQuery(request);
            JSONArray response = new JSONArray();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()){
                JSONObject recordJSON = new JSONObject();
                for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
                    String label = metaData.getColumnLabel(columnIndex);
                    String value = resultSet.getString(columnIndex);
                    recordJSON.put(label,value);
                }
                response.put(recordJSON);
            }
            return response.toString();
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}
