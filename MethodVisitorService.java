import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
public class MethodVisitorService extends VoidVisitorAdapter<List<MethodDeclaration>>
{
	
	public void visit(MethodDeclaration methodDeclaration, List<MethodDeclaration> methodDeclarations)
	{
		super.visit(methodDeclaration,methodDeclarations);
		methodDeclarations.add(methodDeclaration);

	}
}
