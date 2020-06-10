public class SpaceInvaders
{
	static CPU cpu;
	
	public static void main(String args[])
	{
		cpu = new CPU();
		
		while (true)
		{
			cpu.cycle();
		}
	}
}