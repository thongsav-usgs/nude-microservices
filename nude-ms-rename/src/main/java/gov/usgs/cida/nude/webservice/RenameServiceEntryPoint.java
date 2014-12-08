package gov.usgs.cida.nude.webservice;

import gov.usgs.cida.nude.service.ExampleDataService;
import gov.usgs.cida.nude.service.FieldRenamingService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author thongsav
 */
@ApplicationPath("/service")
public class RenameServiceEntryPoint extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();

		// webservices
		classes.add(FieldRenamingService.class);
		classes.add(ExampleDataService.class);

		return classes;
	}
}