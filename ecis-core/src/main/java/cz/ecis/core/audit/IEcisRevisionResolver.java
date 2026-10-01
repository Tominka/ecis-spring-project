package cz.ecis.core.audit;

import org.apache.commons.lang3.NotImplementedException;

public interface IEcisRevisionResolver {

	default String resolveUsername() {
		throw new NotImplementedException("Username resolver not implemented");
	}
	
	default String resolveApplication() {
		throw new NotImplementedException("Application resolver not implemented");
	}
}
