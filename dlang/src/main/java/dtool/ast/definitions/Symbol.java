package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

/** A Symbol is node wrapping an identifier. */
public class Symbol extends ASTNode {
	
	public final String name;
	
	public Symbol(String name) {
		assertNotNull(name);
		this.name = name;
	}
	
	@Override
	public final boolean equals(Object obj) {
		return (obj instanceof Symbol) && name.equals(((Symbol) obj).name);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SYMBOL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
}
