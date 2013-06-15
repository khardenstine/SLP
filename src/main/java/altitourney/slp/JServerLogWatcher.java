package altitourney.slp;

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013

import altitourney.slp.events.Events;
import play.api.libs.json.Json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class JServerLogWatcher
{

	private String path;
	private File file;
	private BufferedReader reader;
	private long referenceFileLength;
	private boolean sourceFileChanged;
	private boolean running;

	JServerLogWatcher(File file, boolean debugParseOldLogs) throws Exception
	{
		this.path = file.getAbsolutePath();

		SLP.getLog().debug("Going to initialize server log");
		SLP.getLog().debug("Log file is located at: " + path);

		initializeCheckServerLog(debugParseOldLogs);
	}

	private void initializeCheckServerLog(boolean debugParseOldLogs) throws Exception
	{
		file = new File(path);

		// Makes sure that the file exists otherwise you can't
		// instantiate the reader.
		file.createNewFile();

		reader = new BufferedReader(new FileReader(file));

		SLP.getLog().debug("About to parse old log");

		// If debug parse old logs is false then we skip over the old stuff in the file
		// If it's true we read it to simulate the running of a server.
		if (!debugParseOldLogs)
		{
			while (reader.readLine() != null)
			{
				// skip old data
			}
		}

		SLP.getLog().debug("Finished parsing old log");

		// Get the length of the file so we can check this later to see if the file
		// has been changed at all
		referenceFileLength = file.length();

		SLP.getLog().debug("The reference file has a length of: " + referenceFileLength);

		// Keeps the threads going forever. Can be set to false to stop the daemon threads
		running = true;

		// Make a thread that just repeatedly checks for new data
		// while the program is running. The program can, in turn,
		// do whatever else is important without blocking on this.
		JThreadHelper.startDaemonThread(new Runnable()
		{
			public void run()
			{
				while (running)
				{
					SLP.getLog().debug("running the thread");
					try
					{
						checkServerLogForNewData();
						JThreadHelper.sleep(200);
					} catch (Exception e)
					{
						SLP.getLog().error("Process failed " + e);
					}
				}
			}
		});
	}

	private void checkServerLogForNewData()
	{
		try
		{
			String line;

			// Provided that there is a line to read that's not null,
			// we need to process it
			while ((line = reader.readLine()) != null)
			{
				Events.handle(Json.parse(line));
			}
		} catch (Exception e)
		{
			SLP.getLog().error("Failed to read console command: " + e);
		}

		// The source file gets renamed and moved after it reaches
		// a certain length. Our program will still be pointing to the
		// old source file unless we fix this problem.
		if (sourceFileChanged)
		{

			// After it's been changed we'll take this into account and make
			// this the new source file.
			sourceFileChanged = false;

			// If we've changed the source file we need to close the previous
			// BufferedReader so we can make a new one later
			if (reader != null)
			{
				try
				{
					reader.close();
					SLP.getLog().debug("reader closed");
				} catch (IOException e)
				{
					SLP.getLog().error("Failed to close reader " + e);
				}
			}

			// A new path will have been given if it's changed
			file = new File(path);

			// We need to change the referenceFileLength as it's a new file
			referenceFileLength = file.length();

			// Output some useful information
			SLP.getLog().debug("Initializing reader for " + file + " (" + file.lastModified() + ") at " + (new Date()).toString());

			// Make that new reader I was talking about
			try
			{
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e)
			{
				SLP.getLog().error("Failed to create reader for " + file + " " + e);
			}
		} // end if (sourceFileChanged)

		// This code checks to see if the file changes. If the length of the
		// file is suddenly different then we know the source file has changed.
		long newLength = file.length();
		referenceFileLength = Math.max(referenceFileLength, newLength);

		if (newLength < referenceFileLength)
		{
			SLP.getLog().debug("The reference file has changed");
			sourceFileChanged = true;
		}
	} // checkServerLogForNewData();

	// Not strictly necessary with daemon threads,
	// but it's just good coding.
	public void shutdown()
	{
		running = false;
	}
}
