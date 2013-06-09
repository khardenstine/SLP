package altitourny.slp.log;

public enum LogLevel
{
	OFF(0), ERROR(1), WARN(2), INFO(3), DEBUG(4);

	private final int val;

	LogLevel(int val)
	{
		this.val = val;
	}

	public boolean shouldLog(LogLevel level)
	{
		return (this.val >= level.val);
	}
}
