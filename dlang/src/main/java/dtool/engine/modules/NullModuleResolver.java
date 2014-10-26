package dtool.engine.modules;

import dtool.ast.definitions.Module;

import java.util.HashSet;

public class NullModuleResolver extends CommonModuleResolver {
	
	@Override
	protected HashSet<String> findModules_do(String fqNamePrefix) {
		return new HashSet<String>();
	}
	
	@Override
	protected Module findModule_do(String[] packages, String module) throws Exception {
		return null;
	}
	
}
