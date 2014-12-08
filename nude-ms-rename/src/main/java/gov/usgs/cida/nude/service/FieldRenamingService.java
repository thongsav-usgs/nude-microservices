package gov.usgs.cida.nude.service;

import gov.usgs.cida.nude.column.Column;
import gov.usgs.cida.nude.column.ColumnGrouping;
import gov.usgs.cida.nude.column.SimpleColumn;
import gov.usgs.cida.nude.connector.HttpCsvConnector;
import gov.usgs.cida.nude.filter.FilterStageBuilder;
import gov.usgs.cida.nude.filter.FilterStep;
import gov.usgs.cida.nude.filter.NudeFilterBuilder;
import gov.usgs.cida.nude.filter.transform.ColumnAlias;
import gov.usgs.cida.nude.out.Dispatcher;
import gov.usgs.cida.nude.out.StreamResponse;
import gov.usgs.cida.nude.out.TableResponse;
import gov.usgs.cida.nude.plan.Plan;
import gov.usgs.cida.nude.plan.PlanStep;
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

@Path("/rename")
public class FieldRenamingService {
	@GET
	@Path("/")
	@Produces("text/plain")
	public void renameFields(@QueryParam("sourceUrl") final String sourceUrl,
			@QueryParam("primaryKey") final String primaryKey,
			@QueryParam("rename") final List<String> renames,
			@Context HttpServletResponse response) throws IOException {
		OutputStream output = response.getOutputStream();
		
		//only supporting CSV right now
		MimeType mimeType = MimeType.CSV;
		
		final HttpCsvConnector csvConnection = new HttpCsvConnector(sourceUrl, primaryKey);
		
		List<PlanStep> steps = new LinkedList<>();
		
		//create connection step
		PlanStep connectorStep;
		connectorStep = new PlanStep() {
			@Override
			public ResultSet runStep(ResultSet rs) {
				return csvConnection.getResultSet();
			}

			@Override
			public ColumnGrouping getExpectedColumns() {
				return csvConnection.getExpectedColumns();
			}
		};
		steps.add(connectorStep);

		//create column renaming step
		ColumnGrouping originals = connectorStep.getExpectedColumns();
		FilterStageBuilder renameFilterBuilder = new FilterStageBuilder(originals);
		
		for(String xfrm : renames) {
			String[] kvp = xfrm.split(",");
			renameFilterBuilder
				.addTransform(new SimpleColumn(kvp[1]), new ColumnAlias(originals.get(indexOfCol(originals.getColumns(), kvp[0]) + 1)));
		}
		FilterStep renameColsStep = new FilterStep(new NudeFilterBuilder(originals)
						.addFilterStage(renameFilterBuilder.buildFilterStage())
				.buildFilter());
		steps.add(renameColsStep);
		
		//select final column list to display
		List<Column> finalColList = new ArrayList<>();
		List<Column> allCols = renameColsStep.getExpectedColumns().getColumns();
		
		for(Column c : allCols) {
			String renameValue = null;
			for(String xfrm : renames) {
				String[] kvp = xfrm.split(",");
				if(c.getName().equals(kvp[0])) {
					renameValue = kvp[1];
				}
			}
			if(renameValue != null) {
				finalColList.add(allCols.get(indexOfCol(allCols, renameValue)));
			} else {
				finalColList.add(allCols.get(indexOfCol(allCols, c.getName())));
			}
		}
		
		ColumnGrouping finalCols = new ColumnGrouping(finalColList);
		FilterStep renamedColumnStep = new FilterStep(new NudeFilterBuilder(finalCols)
				.addFilterStage(new FilterStageBuilder(finalCols)
						.buildFilterStage())
				.buildFilter());
		steps.add(renamedColumnStep);
		
		//execute plan
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
	
	/**
	 * Helper function to get the index of a column with the given name
	 */
	private int indexOfCol(List<Column> cols, String colName) {
		int index = -1;
		for(int i = 0; i < cols.size(); i++) {
			if(cols.get(i).getName().equals(colName)) {
				index = i;
				break;
			}
		}
		return index;
	}
}
