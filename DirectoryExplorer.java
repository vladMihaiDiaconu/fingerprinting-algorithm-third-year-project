import java.io.File;

/*
 * Class to represent any sort of file (directory or leaf file).
 */
@Deprecated
public class DirectoryExplorer 
{
	private FileHandler fileHandler;
	private Filter filter;
	
	public DirectoryExplorer(Filter filter, FileHandler fileHandler)
	{
		this.fileHandler = fileHandler;
		this.filter = filter;
	}
	
	
	/*
	 * Handles @param file given the level and path of the file.
	 */
	public interface FileHandler
	{
		void handle(int level, String path, File file);
	}
	
	/*
	 * Filters information in a file.
	 */
	public interface Filter
	{
		boolean interested(int level, String path, File file);
	}
	
	/*
	 * Starts traversing a given root directory recursively.
	 */
	public void traverse(File rootFile)
	{
		explore(0 ,"", rootFile);
	}
	
	/*
	 * Recursively traverse the given directory until leafs are found.
	 */
	private void explore(int level, String path, File file)
	{
		if(!file.isDirectory())
		{
			if(filter.interested(level, path, file))
			{
				fileHandler.handle(level, path, file);
			}
		}
		else
		{
			for(File index : file.listFiles())
			{
				explore(level + 1,String.valueOf(path + "/" + index.getName()), index);
			}
		}
	}
}