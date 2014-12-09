package gov.usgs.cida.nude.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("example/data")
public class ExampleDataService {
	public static final int NUM_OF_EXAMPLE_ROWS = 10000;
	
	@GET
	@Path("set1")
	@Produces("text/plain")
	public void streamDataSet1(@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
	
		output.write("ID,SET1_COLUMN1,SET1_COLUMN2\n".getBytes());
		
		for(int i = 0; i < NUM_OF_EXAMPLE_ROWS; i++) {
			output.write(((i+1) + ",row " + (i+1) + " from SET1_COLUMN1,row " + (i+1) + " from SET1_COLUMN2\n").getBytes());
		}
	}
	
	@GET
	@Path("set1B")
	@Produces("text/plain")
	public void streamDataSet1B(@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
	
		output.write("ID,SET1B_COLUMN1,SET1B_COLUMN2\n".getBytes());
		
		for(int i = 0; i < NUM_OF_EXAMPLE_ROWS; i++) {
			output.write(((i+1) + ",row " + (i+1) + " from SET1B_COLUMN1,row " + (i+1) + " from SET1B_COLUMN2\n").getBytes());
		}
	}
	
	@GET
	@Path("set2")
	@Produces("text/plain")
	public void streamDataSet2(@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
	
		output.write("IDENTIFIER,SET2_COLUMN1,SET2_COLUMN2\n".getBytes());
		
		for(int i = 0; i < NUM_OF_EXAMPLE_ROWS; i++) {
			output.write(((i+1) + ",row " + (i+1) + " from SET2_COLUMN1,row " + (i+1) + " from SET2_COLUMN2\n").getBytes());
		}
	}
	
	@GET
	@Path("set3")
	@Produces("text/plain")
	public void streamDataSet3(@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
	
		output.write("KEY,SET3_COLUMN1,SET3_COLUMN2\n".getBytes());
		
		for(int i = 0; i < NUM_OF_EXAMPLE_ROWS; i++) {
			output.write(((i+1) + ",row " + (i+1) + " from SET3_COLUMN1,row " + (i+1) + " from SET3_COLUMN2\n").getBytes());
		}
	}

}
