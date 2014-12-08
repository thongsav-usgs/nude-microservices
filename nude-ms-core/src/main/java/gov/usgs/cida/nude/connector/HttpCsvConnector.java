package gov.usgs.cida.nude.connector;

import gov.usgs.cida.nude.column.Column;
import gov.usgs.cida.nude.column.ColumnGrouping;
import gov.usgs.cida.nude.column.SimpleColumn;
import gov.usgs.cida.nude.connector.parser.IParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class HttpCsvConnector implements IConnector {
	private Client client;
	private boolean isReady;
	private BufferedReader csvResponse;
	private ColumnGrouping cg;
	private Column primaryKey;
	
	public HttpCsvConnector(String url, String primaryKey) throws IOException {

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 10000);
		clientConfig.property(ClientProperties.READ_TIMEOUT, 60000);
		this.client = ClientBuilder.newClient(clientConfig);
		
		Response response = client.target(url)
			.path("")
			.request(new MediaType[]{MediaType.TEXT_PLAIN_TYPE})
			.get();
		InputStream rawResponse = response.readEntity(InputStream.class);
		csvResponse = new BufferedReader(new InputStreamReader(rawResponse));
		
		//assume header row is first line
		String header = csvResponse.readLine();
		String[] columns = header.split(",");

		List<Column> allColumns = new LinkedList<>();
		for(String c : columns) {
			if(c.equals(primaryKey)) {
				this.primaryKey = new SimpleColumn(c);
				allColumns.add(this.primaryKey);
			} else {
				allColumns.add(new SimpleColumn(c));
			}
		}
		
		if(this.primaryKey == null) {
			this.primaryKey = allColumns.get(0);
		}
		this.cg = new ColumnGrouping(this.primaryKey, allColumns);
		isReady = true;
	}

	@Override
	public void addInput(ResultSet in) {
	}

	@Override
	public String getStatement() {
		return null;
	}

	@Override
	public ResultSet getResultSet() {
		return new CsvResultSet(csvResponse, cg);
	}

	@Override
	public IParser getParser() {
		return null;
	}

	@Override
	public boolean isValidInput() {
		return true;
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	@Override
	public ColumnGrouping getExpectedColumns() {
		return this.cg;
	}

}
