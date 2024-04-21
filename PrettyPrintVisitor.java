import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.PrettyPrinterConfiguration;

public class PrettyPrintVisitor extends DefaultPrettyPrinterVisitor
{

	public PrettyPrintVisitor() {
		super(new PrettyPrinterConfiguration());
		// TODO Auto-generated constructor stub
	}
	
	public void visit(CompilationUnit cu, Void string)
	{
		super.visit(cu, string);
		System.out.println(cu);
	}

}
