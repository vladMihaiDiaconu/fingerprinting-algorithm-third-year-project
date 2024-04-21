import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Main 
{	
	public static GitHubRepositoryHandler repositoryHandler1 = new GitHubRepositoryHandler();
	public static GitHubRepositoryHandler repositoryHandler2 = new GitHubRepositoryHandler();
	
	public static void main(String[] args) throws IOException
	{
		//repositoryHandler.cloneRepositoryLocally(repositoryHandler.getRepositoryURLs());	

		Map<String,Set<String>> fingerprints1 = repositoryHandler1.exploreRepository(GitHubRepositoryHandler.pathForClone + " " + String.valueOf("Experiment1"));	
		ArrayList<Set<String>> fingerprintsRepository1 = new ArrayList<Set<String>>();
		for(Map.Entry<String, Set<String>> entry : fingerprints1.entrySet())
		{
			fingerprintsRepository1.add(entry.getValue());
		}
		
		Map<String,Set<String>> fingerprints2 = repositoryHandler2.exploreRepository(GitHubRepositoryHandler.pathForClone + " " + String.valueOf("Experiment2"));
		ArrayList<Set<String>> fingerprintsRepository2 = new ArrayList<Set<String>>();
		for(Map.Entry<String, Set<String>> entry : fingerprints2.entrySet())
		{
			fingerprintsRepository2.add(entry.getValue());
		}

		System.out.println( "The two documents have " + repositoryHandler1.getSimilarity(fingerprintsRepository1.get(0).toString(), fingerprintsRepository2.get(0).toString()) + " / 1 similarity.");

	}
	
	public static int getIndexOfLargest( int[] array )
	{
	  if ( array == null || array.length == 0 ) return -1; // null or empty

	  int largest = 0;
	  for ( int i = 1; i < array.length; i++ )
	  {
	      if ( array[i] > array[largest] ) largest = i;
	  }
	  return largest; // position of the first largest found
	}
	
	public static void findSimilarDocuments(Set<String> fingeprint1, ArrayList<Set<String>> fingerprintsRepository2) throws IOException
	{
		int counter = 0;
		ArrayList<Double> similarities = new ArrayList<Double>();
	
		for(int j = 0; j < fingerprintsRepository2.size(); ++j)
		{
			
			System.out.println(fingeprint1);
			System.out.println(fingerprintsRepository2.get(j));
			System.out.println("Similarity: " + repositoryHandler1.getSimilarity(fingeprint1.toString(), fingerprintsRepository2.get(j).toString()));
							
			if(repositoryHandler1.getSimilarity(fingeprint1.toString(), fingerprintsRepository2.get(j).toString()) >= 0.70)
			{
				counter += 1;
				similarities.add(repositoryHandler1.getSimilarity(fingeprint1.toString(), fingerprintsRepository2.get(j).toString()));
			}
		}
		
		System.out.println("A total number of " + counter + " were found to be 70% or more similar to one another between the two repositories");
		
		FileWriter writer = new FileWriter("D:\\Eclipse\\Workspace\\similarities.txt"); 
		for(Double similarity: similarities) 
		{
		  try 
		  {
			writer.write(String.valueOf(similarity) + "\n");
		  } 
		  catch (IOException e) 
		  {
			e.printStackTrace();
		  }
		}
		
		writer.close();
	}
}
