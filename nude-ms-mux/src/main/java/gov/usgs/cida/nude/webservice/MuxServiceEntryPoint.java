package gov.usgs.cida.nude.webservice;

import gov.usgs.cida.nude.service.MuxService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author thongsav
 */
@ApplicationPath("/service")
public class MuxServiceEntryPoint extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();

		// webservices
		classes.add(MuxService.class);

		return classes;
	}
}