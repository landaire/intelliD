package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.Reference;
import dtool.engine.modules.IModuleResolver;

import java.util.Collection;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

/**
 * An Expression wrapping a {@link dtool.ast.references.Reference}
 */
public class ExpReference extends Expression {
	
	public final Reference ref;
	
	public ExpReference(Reference ref) {
		assertNotNull(ref);
		this.ref = parentize(ref);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_REFERENCE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, ref);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(ref);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return ref.findTargetDefElements(moduleResolver, findFirstOnly);
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		return ref.resolveTypeOfUnderlyingValue(mr);
	}
	
}
