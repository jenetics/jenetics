package org.jenetics.example;

public class Test {

	  private final int MAX_BUFFERED_CHARS;
	  private final int MAX_COLUMN_ERROR_LENGTH;
	  private byte bitmap$init$0;


	  public int MAX_BUFFERED_CHARS()
	  {
	    if (
	      (byte)(this.bitmap$init$0 & 0x1) != 0)
	    {
	      return this.MAX_BUFFERED_CHARS; } throw new Error("Uninitialized field: TransformationFilter.scala: 663".toString());
	  }

	  public int MAX_COLUMN_ERROR_LENGTH()
	  {
	    if (
	      (byte)(this.bitmap$init$0 & 0x2) != 0)
	    {
	      return this.MAX_COLUMN_ERROR_LENGTH; } throw new Error("Uninitialized field: TransformationFilter.scala: 664".toString());
	  }

	  private Test()
	  {
	    this.MAX_BUFFERED_CHARS = 1048576; this.bitmap$init$0 = ((byte)(this.bitmap$init$0 | 0x1));
	    this.MAX_COLUMN_ERROR_LENGTH = 100; this.bitmap$init$0 = ((byte)(this.bitmap$init$0 | 0x2));
	  }
	  
	  public static void main(final String[] args) {
		  final Test instance = new Test();
		  
		  long start = System.currentTimeMillis();
		  for (int j = 0; j < 2000; ++j)
			  for (int i = 1001; i < 1048576; ++i) {
				  if (i > instance.MAX_BUFFERED_CHARS() && i < instance.MAX_COLUMN_ERROR_LENGTH()) {
					  System.out.println("FOO");
				  }
			  }
		  long end = System.currentTimeMillis();
		  
		  System.out.println("Time: " + (end - start));
	  }
	
}
