package dtool.engine.modules;

import dtool.parser.DeeLexingUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.StringUtil;

import java.io.File;

import static melnorme.utilbox.core.CoreUtil.array;

/**
 * Naming rules code for compilation units and packages.
 * 
 */
public class ModuleNamingRules {
	
	private static final String DEE_FILE_EXTENSION = ".d";
	private static final String DEE_HEADERFILE_EXTENSION = ".di";
	
	public static final String[] VALID_EXTENSIONS = array(DEE_FILE_EXTENSION, DEE_HEADERFILE_EXTENSION);
	
	
	public static String getDefaultModuleNameFromFileName(String fileName) {
		return StringUtil.substringUntilMatch(fileName, ".");
	}
	
	protected static boolean isValidDFileExtension(String fileExt) {
		return DEE_FILE_EXTENSION.equals(fileExt) || DEE_HEADERFILE_EXTENSION.equals(fileExt);
	}
	
	/* ----------------- ----------------- */
	
	public static ModuleFullName getValidModuleNameOrNull(String fileName) {
		String[] nameSegments = fileName.split(File.separator);
		int fileElementCount = nameSegments.length;
		if(fileElementCount == 0) {
			return null;
		}

		String moduleBaseName = getModuleNameIfValidFileName(fileName, true);
		if(moduleBaseName == null) {
			return null;
		}
		
		if(moduleBaseName.equals("package")) {
			fileElementCount--;
			if(fileElementCount == 0) {
				return null;
			}
			// TODO: This may be a full path, not the name. See original version
			moduleBaseName = nameSegments[fileElementCount-1];
		}
		if(!DeeLexingUtil.isValidDIdentifier(moduleBaseName)) {
			return null;
		}

		nameSegments[fileElementCount - 1] = moduleBaseName;
		
		for (int i = 0; i < fileElementCount - 1; i++) {
			if(!isValidPackageNameSegment(nameSegments[i])) {
				return null;
			}
		}
		
		return new ModuleFullName(nameSegments);
	}
	
	public static boolean isValidCompilationUnitName(String fileName) {
		return getModuleNameIfValidFileName(fileName) != null;
	}
	
	protected static String getModuleNameIfValidFileName(String fileName) {
		return getModuleNameIfValidFileName(fileName, false);
	}
	
	protected static String getModuleNameIfValidFileName(String fileName, boolean allowPackageName) {
		String fileExtension = StringUtil.substringFromMatch(".", fileName);
		if(!isValidDFileExtension(fileExtension)){
			return null;
		}
		String moduleName = StringUtil.substringUntilMatch(fileName, ".");
		if(DeeLexingUtil.isValidDIdentifier(moduleName)) {
			return moduleName;
		}
		if(allowPackageName && moduleName.equals("package")) {
			return moduleName;
		}
		return null;
	}
	
	public static boolean isValidPackageNameSegment(String partname) {
		return DeeLexingUtil.isValidDIdentifier(partname);
	}
	
	/* ----------------- ----------------- */
	
	public static boolean isValidPackagesPath(String packagesPathStr) {
		if(packagesPathStr.equals(""))
			return true;
		
		String[] segments = packagesPathStr.split("/");
		for (String segment : segments) {
			if(!isValidPackageNameSegment(segment))
				return false;
		}
		return true;
	}
	
	
	public static String getModuleFQNameFromFilePath(String packagePath, String fileName) {
		File path = MiscUtil.createPathOrNull(packagePath + "/" + fileName);
		if(path == null) {
			return null;
		}
		
		ModuleFullName moduleValidName = getValidModuleNameOrNull(path.getPath());
		return moduleValidName == null ? null : moduleValidName.getFullNameAsString();
	}
	
}
