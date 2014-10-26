package dtool.ast.expressions;

import dtool.ast.*;
import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.LanguageIntrinsics;

import java.util.Collection;
import java.util.Collections;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

public class InitializerArray extends Expression implements IInitializer {
	
	public final NodeListView<ArrayInitEntry> entries;
	
	public InitializerArray(NodeListView<ArrayInitEntry> indexes) {
		this.entries = parentize(assertNotNull(indexes));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("[", entries, ", ", "]");
	}
	
	public static class ArrayInitEntry extends ASTNode {
		public final Expression index;
		public final IInitializer value;
		
		public ArrayInitEntry(Expression index, IInitializer value) {
			this.index = parentize(index);
			this.value = parentize(assertNotNull(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ARRAY_INIT_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, index);
			acceptVisitor(visitor, value);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(index, " : ");
			cp.append(value);
		}
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<INamedElement>singleton(LanguageIntrinsics.D2_063_intrinsics.dynArrayType);
	}
	
}
