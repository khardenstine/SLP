package altitourny.slp.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

// This class takes in various pieces of information
// and outputs it to a log file
//
// Code adapted from Michael Solomon
// Edited by: Karl Hardenstine
// June-2013

public class Logger
{

	private final File LOG_FILE;
	private final LogLevel logLevel;
	private BufferedWriter writer;

	Logger(String logFileLocation, LogLevel logLevel) throws Exception
	{
		LOG_FILE = new File(logFileLocation);
		this.logLevel = logLevel;
	}

	private void log(String line)
	{
		try
		{
			// The true part makes it so I'm appending to the file and not
			// over-riding the whole thing each time.
			writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
			writer.write(new Date().toString() + " " + line);
			writer.newLine();
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void error(String line)
	{
		if (logLevel.shouldLog(LogLevel.ERROR))
		{
			log("ERROR: " + line);
		}
	}

	public void warn(String line)
	{
		if (logLevel.shouldLog(LogLevel.WARN))
		{
			log("WARN: " + line);
		}
	}

	public void info(String line)
	{
		if (logLevel.shouldLog(LogLevel.INFO))
		{
			log("INFO: " + line);
		}
	}

	public void debug(String line)
	{
		if (logLevel.shouldLog(LogLevel.DEBUG))
		{
			log("DEBUG: " + line);
		}
	}

	// Removes everything from the log to start fresh (notice the lack
	// of true in the creation of FileWriter)
	public void clean()
	{
		try
		{
			writer = new BufferedWriter(new FileWriter(LOG_FILE));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

