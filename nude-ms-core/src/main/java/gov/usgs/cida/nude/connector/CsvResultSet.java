package gov.usgs.cida.nude.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import gov.usgs.cida.nude.column.Column;
import gov.usgs.cida.nude.column.ColumnGrouping;
import gov.usgs.cida.nude.resultset.inmemory.PeekingResultSet;
import gov.usgs.cida.nude.resultset.inmemory.TableRow;

public class CsvResultSet extends PeekingResultSet {
	
	private BufferedReader reader;
	
	public CsvResultSet(BufferedReader inputStream, ColumnGrouping colGroups) {
		reader = inputStream;
		columns = colGroups;
	}
	
	@Override
	public void close() throws SQLException {
		super.close();
	}
	
	protected TableRow makeNextRow(){
		TableRow row = null;
		
		try {
			if(reader.ready()) {
				String[] vals = reader.readLine().split(",");
	
				Map<Column, String> ob = new HashMap<>();
				
				for(int i = 0; i < vals.length; i++) {
					ob.put(columns.get(i+1), vals[i]);
				}
				
				row = new TableRow(columns, ob);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return row;
	}
	
	@Override
	public String getCursorName() throws SQLException {
		return "CsvStreamingResultSet";
	}
	
	@Override
	protected void addNextRow() throws SQLException {
		TableRow row = this.makeNextRow();
		if (row != null) {
			this.nextRows.add(row);
		}
	}

}