import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

public class MethodSequence 
{
	private List<Node> nodes;
	private List<String> sequence;
	private List<String> ngrams;
	private int size;
	public static final int PRIME = 31;
	public String fileName;
	/**
	 * Method that constructs ngrams for a given method sequence.
	 * @param list the list of node information from a given preorder traversal of an AST.
	 * @param n the size of the ngram.
	 * @return a list of AST-based ngrams.
	 */
	public List<String> ngrams(List<String> list, int n) 
	{
		
		ArrayList<String> ngrams = new ArrayList<String>();
		
		int c = list.size();
		for(int i = 0; i < c; i++) 
		{
			if((i + n - 1) < c) 
			{
				int stop = i + n;
				String ngramWords = list.get(i);
				
				for(int j = i + 1; j < stop; j++) 
				{
					ngramWords +=" "+ list.get(j);
				}
				
				ngrams.add(ngramWords);
			}
		}
		this.ngrams = ngrams;
		
		return ngrams;
	}
	
	/**
	 * Method that takes a list of ngrams, hashes them and adds them together in order to generate a file's fingerprint.
	 * @param ngrams is the list of ngrams that are used to generate a file's fingerprint.
	 * @return a fingerprint.
	 */
	public int computeFingerprint(List<String> ngrams)
	{
		int result = 1;
		
		for(String s : ngrams)
		{
			result = result * PRIME + s.hashCode();
		}
		
		return result;
	}
	
	public List<Node> getNodes()
	{
		return nodes;
	}
	
	public List<String> getSequence()
	{
		return sequence;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public void setNodes(List<Node> nodes)
	{
		this.nodes = nodes;
	}
	
	public void setSequence(List<String> sequence)
	{
		this.sequence = sequence;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
}
