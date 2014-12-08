package gov.usgs.cida.nude.service;

import gov.usgs.cida.nude.column.Column;
import gov.usgs.cida.nude.column.ColumnGrouping;
import gov.usgs.cida.nude.connector.HttpCsvConnector;
import gov.usgs.cida.nude.connector.IConnector;
import gov.usgs.cida.nude.out.Dispatcher;
import gov.usgs.cida.nude.out.StreamResponse;
import gov.usgs.cida.nude.out.TableResponse;
import gov.usgs.cida.nude.plan.Plan;
import gov.usgs.cida.nude.plan.PlanStep;
import gov.usgs.cida.nude.resultset.inmemory.MuxResultSet;
import gov.usgs.webservices.framework.basic.MimeType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.stream.XMLStreamException;

@Path("/mux")
public class MuxService {
	@GET
	@Path("/")
	@Produces("text/plain")
	public void renameFields(@QueryParam("sourceUrl") final List<String> sourceUrls,
			@QueryParam("primaryKey") final String primaryKey,
			@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
		
		//only supporting CSV right now
		MimeType mimeType = MimeType.CSV;
		
		final List<HttpCsvConnector> csvConnections = new ArrayList<>();
		for(String url : sourceUrls) {
			csvConnections.add(new HttpCsvConnector(url, primaryKey));
		}
		
		boolean areAllReady = false;
		while (!areAllReady) {
			boolean readyCheck = true;
			for (IConnector conn : csvConnections) {
				boolean connReady = conn.isReady();
				readyCheck = (readyCheck && connReady);
			}
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException ex) {
			}
			
			
			areAllReady = readyCheck;
		}
		
		List<PlanStep> steps = new LinkedList<>();
		PlanStep connectorStep;
		connectorStep = new PlanStep() {
			@Override
			public ResultSet runStep(ResultSet rs) {
				List<ResultSet> rsets = new ArrayList<>();
				for(HttpCsvConnector conn : csvConnections) {
					rsets.add(conn.getResultSet());
				}
				return new MuxResultSet(rsets);
			}

			@Override
			public ColumnGrouping getExpectedColumns() {
				List<Column> columns = new ArrayList<>();
				for (IConnector conn : csvConnections) {
					//TODO do not add duplicat rows
					columns.addAll(conn.getExpectedColumns().getColumns());
				}
				
				Column pk = null;
				if(primaryKey != null){
					//TODO find pk in list
				} else {
					pk = columns.get(0);
				}
				ColumnGrouping cg = new ColumnGrouping(pk, columns);
				
				
				return cg;
			}
		};
		steps.add(connectorStep);
		
		Plan plan = new Plan(steps);
		
		ResultSet runStep = Plan.runPlan(plan);
		TableResponse tr = new TableResponse(runStep);
		StreamResponse sr = null;
		try {
			sr = Dispatcher.buildFormattedResponse(mimeType, tr);
		} catch (IOException| SQLException | XMLStreamException ex) {
			//TODO fill in
		}
		
		if (sr != null && output != null) {
			StreamResponse.dispatch(sr, new PrintWriter(output));
			output.flush();
		}
	}
}
