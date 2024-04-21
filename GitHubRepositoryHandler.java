import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.Node.PreOrderIterator;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;

import 	info.debatty.*;
import info.debatty.java.stringsimilarity.Jaccard;

/*
 * Class that handles the remote GitHub repositories that are going to be investigated.s
 */

public class GitHubRepositoryHandler
{
	public static final String pathForClone = "D:\\Eclipse\\Workspace\\Repository";
	
	private ArrayList<File> javaFiles = new ArrayList<File>();
	private List<Node> nodeCollector = new ArrayList<Node>();
	private Map<String,List<MethodSequence>> methodSequences = new HashMap<String, List<MethodSequence>>();
	private String methodSequenceSample = "";
	private Jaccard similarityChecker = new Jaccard();

	/*
	 * Read the URLs from RepositoryURls.txt line by line.
	 * Returns @param urlList, a list containing all URLs of repositories in the text file.
	 */
	public ArrayList<String> getRepositoryURLs()
	{
		BufferedReader reader;
		ArrayList<String> urlList = new ArrayList<String>();
		
		try
		{
			reader = new BufferedReader(new FileReader("D:\\Eclipse\\Workspace\\RepositoryURLs.txt"));
			String url = reader.readLine();

			while(url != null)
			{
				urlList.add(url);
				url = reader.readLine();
			}	
			reader.close();
		}
		catch(IOException e)
		{
			System.out.println("Exception occured while reading the repository URLs from file: ");
			e.printStackTrace();
		}
		return urlList;
	}
	
	/**
	 * Extract a repository on the local machine at path @param pathForClone with respect to the repository's URL.
	 */
	public boolean cloneRepositoryLocally(ArrayList<String> urlList)
	{
		if(urlList.size() == 0)
		{
			System.out.println("There are no URLs to repositories to be cloned.");
			return false;
		}
		else
		{
			for(int i = 0; i < urlList.size(); ++i)
			{
				if(!(urlList.get(i).equals("") || urlList.get(i).indexOf("https://github.com/") != -1))
				{
					String repoURL = urlList.get(i);
					try 
					{
					    System.out.println("Now cloning: " + repoURL + " into: " + pathForClone + " " + String.valueOf(i + 1));
					    Git.cloneRepository()
					        .setURI(repoURL)
					        .setDirectory(Paths.get(pathForClone + " " + String.valueOf(i + 1)).toFile())
					        .call();
					    
					    System.out.println("Completed cloning successfully!");
					    

					}
					catch (GitAPIException e) 
					{
					    System.out.println("Exception occurred while cloning repository from URL: " + repoURL);
					    e.printStackTrace();
					}
				}
				else
				{
					System.out.println("'" + urlList.get(i) + "' is not a valid URL or does not contain a repository.");
					return false;
				}
			}
			
		    return true;
		}
	}
	
	/**
	 * Method that traverses all files within a given path of a repository and prints out all methods inside each JAVA file and a fingerprint of the source-code inside said JAVA file.
	 * @param directoryName the path of a repository.
	 * @throws FileNotFoundException 
	 */
	public Map<String, Set<String>> exploreRepository(String directoryName)
	{
		File repositoryDirectory = new File(directoryName);
		
		if(repositoryDirectory != null)
		{
			System.out.println("    Start traversal of Repository with name: " + repositoryDirectory.getAbsolutePath());
			System.out.println("===================================================================================\n");
			File[] initialChildFiles = repositoryDirectory.listFiles();
			
			ArrayList<File> javaFiles = showFiles(initialChildFiles);
			if(javaFiles == null)
			{
				System.out.println("=============== No parsable JAVA files were found in this repository ================\n");
				return null;
			}
			else
			{
				CompilationUnit cu;
				int astCounter = 0;
				int ngramsCounter = 0;
				int fileCounter = 0;
				
				for(File index : javaFiles)
				{
					fileCounter += 1;
					try
					{
						cu = StaticJavaParser.parse(new FileInputStream(index));
						
						ArrayList<MethodDeclaration> methods = new ArrayList<>();
						VoidVisitor<List<MethodDeclaration>> methodCollector = new MethodVisitorService();
						
//							System.out.println("IN FILE WITH NAME:  " + index.getAbsolutePath());
						methodCollector.visit(cu, methods);	
						ArrayList<MethodSequence> sequences = new ArrayList<MethodSequence>();
						
						for(MethodDeclaration declarationIndex : methods)
						{
							astCounter += 1;
//							System.out.println("From Line " + declarationIndex.getBegin().get().line + " to line " + declarationIndex.getEnd().get().line + " " + declarationIndex.getDeclarationAsString());
							Node n = declarationIndex.getParentNodeForChildren();
							
							MethodSequence sequence = methodDeclarationPreOrderTraversal(n);
							sequence.fileName = index.getName();
							sequences.add(sequence);
							
							nodeCollector = new ArrayList<Node>();
							methodSequenceSample = "";
						}
						methodSequences.put(index.getAbsolutePath(), sequences);
						
						sequences = new ArrayList<MethodSequence>();
					}
					catch(Exception e)
					{
						System.out.println("Found unparsable file. The program will skip over it.");
					}
					
				}
				Map<String, Set<String>> fingerprints = new HashMap<String,Set<String>>();
				int counterFingerprints = 0;
				String s1 = "";
				String s2 = "";
				for (Map.Entry<String, List<MethodSequence>> entry : methodSequences.entrySet()) 
				{
					String copy_fileName = "";
					    
					Set<String> ngramsSet = new HashSet<String>(Collections.EMPTY_LIST);
				    for(MethodSequence m : entry.getValue())
				    {
				    	copy_fileName = m.fileName;
				    	System.out.println(m.ngrams(m.getSequence(),4));
				    	ngramsSet.addAll(m.ngrams(m.getSequence(), 4).stream().distinct().collect(Collectors.toSet()));
				    }
				    if(ngramsSet.size() != 0)
				    {
				    	fingerprints.put(copy_fileName,ngramsSet);
				    	ngramsCounter += ngramsSet.size();
				    	counterFingerprints += 1;
				    }

				}
				
				System.out.println("\n= Completed traversal of methods in repository: '" + directoryName + "' =");
				System.out.println("A total of " + counterFingerprints + " fingerprints were built in this repository.");
				System.out.println("A total of " + astCounter + " ASTs were built in this repository.");
				System.out.println("A total of " + ngramsCounter + " ngrams were built in this repository.");
				System.out.println("A total of " + fileCounter + " files were parsed in this repository.");
				
				return fingerprints;
			}
		}
		return null;
	}
	
	/**
	 * A method that adds all child nodes of a given root node to a list in pre-order depth-first traversal.
	 * @param rootNode is the given root node of the Abstract Syntax Tree.
	 */
	private MethodSequence methodDeclarationPreOrderTraversal(Node rootNode)
	{	 
		PreOrderIterator iterator = new PreOrderIterator(rootNode);
		 
		 while(iterator.hasNext())
		 {
			 nodeCollector.add(iterator.next());
		 }
		 
		 for(Node n : nodeCollector)
		 {
			 methodSequenceSample = methodSequenceSample + " "  + n.getMetaModel();
		 }
		 
		 MethodSequence methodSequence = new MethodSequence();
		 methodSequence.setSize(nodeCollector.size());
		 methodSequence.setSequence(Arrays.asList(methodSequenceSample.split(" ")));
		 methodSequence.setNodes(nodeCollector);
		 
		 return methodSequence;
	}

	/**
	 * A method that prints out the sequence of code in a file given the AST nodes by using the class names
	 * for the expressions.
	 * @param codeNodes is a map representing the name of the source code file and the list containing a pre-order 
	 * traversal of the AST within the file.
	 */
	public void printSequence(Map<String, List<Node>> codeNodes)
	{
		for (Entry<String, List<Node>> entry : codeNodes.entrySet())
		{
		   List<Node> nodesInFile = entry.getValue();
		   System.out.println("\n" + entry.getKey());
		   for(Node indexOfNodes : nodesInFile)
		   {
			   System.out.print(indexOfNodes.getMetaModel() + ": ");
			   System.out.print(indexOfNodes + "\n");
		   }
		}
	}
	
	public void methodSequencing(Node node)
	{
		String[] stringifiedNode = node.getMetaModel().toString().split(" ");
		
		for(int i = 0; i < stringifiedNode.length; ++i)
		{
			System.out.print(stringifiedNode[i] + " ");
		}
	}
	
	public double getSimilarity(String s1, String s2)
	{
		return similarityChecker.similarity(s1, s2);
	}
	
	/**
	 * Method to traverse a repository from its root to every 'leaf' file.
	 * @param files stores all JAVA files from the repository.
	 * @return an java.util.ArrayList containing all JAVA files in a given repository. 
	 */
    private ArrayList<File> showFiles(File[] files) 
    {	
    	if(files == null)
    		return null;
    	else
    	{
    	    for (File file : files)
            {
                if (file.isDirectory())
                    showFiles(file.listFiles());
                else if(!file.isDirectory())
                {
                	if(file.getName().endsWith(".java"))
                	{
                		javaFiles.add(file);
                	}
                }
            }
            
            return javaFiles;
    	}
    
    }
}
