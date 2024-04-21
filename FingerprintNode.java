import java.util.List;

import com.github.javaparser.ast.Node;

/**
 * Class to represent a fingerprint node. This contains @param node (class or interface), @param weight the size, in lines of code and @param fingerprint the hash value assigned to the information contained in node.
 * @author Vlad Diaconu
 *
 */
public class FingerprintNode
{
	private Node node;
	private int weight;
	private String fingerprint;
	
	
	public FingerprintNode(Node node, int weight, String fingerprint)
	{
		this.node = node;
		this.weight = weight;
		this.fingerprint = fingerprint;
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public String getFingerprint()
	{
		return fingerprint;
	}
}
