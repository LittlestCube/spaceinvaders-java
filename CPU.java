import littlecube.unsigned.*;

public class CPU
{
	static UnsignedByte a, b, c, d, e, h, l;
	
	static UnsignedByte Z, S, P, CY, AC;
	
	static UnsignedShort sp, pc;
	
	static UnsignedByte memory[] = new UnsignedByte[0x4000];
	
	static UnsignedShort stack[] = new UnsignedShort[0x10000];
	
	UnsignedByte opcode;
	
	public CPU()
	{
		init();
	}
	
	void init()
	{
		a = new UnsignedByte();
		b = new UnsignedByte();
		c = new UnsignedByte();
		d = new UnsignedByte();
		e = new UnsignedByte();
		h = new UnsignedByte();
		l = new UnsignedByte();
		
		Z = new UnsignedByte();
		S = new UnsignedByte();
		P = new UnsignedByte();
		CY = new UnsignedByte();
		AC = new UnsignedByte();
		
		sp = new UnsignedShort();
		pc = new UnsignedShort();
		
		for (int i = 0; i < stack.length; i++)
		{
			stack[i] = new UnsignedShort();
		}
		
		for (int i = 0; i < memory.length; i++)
		{
			memory[i] = new UnsignedByte();
		}
	}
	
	void cycle()
	{
		opcode.set(memory[pc.get()].get());
		
		switch (opcode.get())
		{
			case 0x00:			// NOP
			{
				break;
			}
		}
	}
}