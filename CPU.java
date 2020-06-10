import littlecube.unsigned.*;

import littlecube.bitutil.*;

public class CPU
{
	int Z, S, P, CY;
	
	UnsignedByte a, b, c, d, e, h, l;
	
	UnsignedShort bc;
	UnsignedShort de;
	UnsignedShort hl;
	
	UnsignedShort sp, pc;
	
	UnsignedByte opcode, arg1, arg2;
	
	UnsignedByte memory[];
	
	UnsignedShort stack[];
	
	public CPU()
	{
		init();
	}
	
	void init()
	{
		Z = 0;
		S = 0;
		P = 0;
		CY = 0;
		
		a = new UnsignedByte();
		b = new UnsignedByte();
		c = new UnsignedByte();
		d = new UnsignedByte();
		e = new UnsignedByte();
		h = new UnsignedByte();
		l = new UnsignedByte();
		
		bc = new UnsignedShort(b.get() << 8 | c.get());
		de = new UnsignedShort(e.get() << 8 | e.get());
		hl = new UnsignedShort(h.get() << 8 | l.get());
		
		sp = new UnsignedShort();
		pc = new UnsignedShort();
		
		opcode = new UnsignedByte();
		arg1 = new UnsignedByte();
		arg2 = new UnsignedByte();
		
		memory = new UnsignedByte[0x4000];
		stack = new UnsignedShort[0x10000];
		
		for (int i = 0; i < memory.length; i++)
		{
			memory[i] = new UnsignedByte();
		}
		
		for (int i = 0; i < stack.length; i++)
		{
			stack[i] = new UnsignedShort();
		}
	}
	
	void cycle()
	{
		opcode.set(memory[pc.get()]);
		arg1.set(memory[pc.get() + 1]);
		arg2.set(memory[pc.get() + 2]);
		
		int flagcheck = 0;
		
		switch (opcode.get())
		{
			case 0x00:			// NOP
			{
				pc.add(1);
				break;
			}
			
			case 0x01:			// LXI B, D16
			{
				b.set(arg2);
				c.set(arg1);
				
				pc.add(3);
				break;
			}
			
			case 0x02:			// STAX B
			{
				memory[bc.get()].set(a);
				
				pc.add(1);
				break;
			}
			
			case 0x03:			// INX B
			{
				bc.add(1);
				
				convertBC();
				
				pc.add(1);
				break;
			}
			
			case 0x04:			// INR B
			{
				flagcheck = b.get() + 1;
				checkFlags("ZSP", flagcheck);
				
				b.add(1);
				
				pc.add(1);
				break;
			}
			
			case 0x05:			// DCR B
			{
				flagcheck = b.get() - 1;
				checkFlags("ZSP", flagcheck);
				
				b.sub(1);
				
				pc.add(1);
				break;
			}
			
			case 0x06:			// MVI B, D8
			{
				b.set(arg1);
				
				pc.add(2);
				break;
			}
			
			case 0x07:			// RLC
			{
				int prevbit7 = BitUtil.bit(a.get(), 7);
				
				a.set(a.b << 1);
				
				CY = prevbit7;
				a.or(prevbit7);
				
				pc.add(1);
				break;
			}
			
			case 0x09:			// DAD B
			{
				flagcheck = hl.get() + bc.get();
				checkFlags("CY", flagcheck);
				
				hl.add(bc.get());
				
				convertHL();
				
				pc.add(1);
				break;
			}
			
			case 0x0A:			// LDAX B
			{
				a.set(memory[bc.get()]);
				
				pc.add(1);
				break;
			}
			
			case 0x0B:			// DCX B
			{
				bc.sub(1);
				
				pc.add(1);
				break;
			}
			
			case 0x0C:			// INR C
			{
				flagcheck = c.get() + 1;
				checkFlags("ZSPCY", flagcheck);
				
				c.add(1);
				
				pc.add(1);
				break;
			}
			
			case 0x0D:			// DCR C
			{
				flagcheck = c.get() - 1;
				checkFlags("ZSP", flagcheck);
				checkFlags("CY", c.get(), 1);
				
				c.sub(1);
				
				pc.add(1);
				break;
			}
			
			case 0x0E:			// MVI C, D8
			{
				c.set(arg1);
				
				pc.add(2);
				break;
			}
			
			case 0x0F:			// RRC
			{
				int prevbit0 = BitUtil.bit(a.get(), 0);
				
				a.set(a.b >> 1);
				
				CY = prevbit0;
				
				if (prevbit0 == 1)
				{
					a.or(BitUtil.craftBitByte(7));
				}
				
				pc.add(1);
				break;
			}
			
			case 0x11:			// LXI D, D16
			{
				d.set(arg2);
				e.set(arg1);
				
				pc.add(3);
				break;
			}
			
			case 0x12:			// STAX D
			{
				memory[de.get()].set(a);
				
				pc.add(1);
				break;
			}
			
			case 0x13:			// INX D
			{
				de.add(1);
				
				convertDE();
				
				pc.add(1);
				break;
			}
			
			case 0x14:			// INR D
			{
				// TODO
				
				pc.add(1);
				break;
			}
		}
	}
	
	void checkFlags(String flags, int number)
	{
		if (flags.contains("Z"))
		{
			Z = ((number) == 0) ? 1 : 0;
		}
		
		if (flags.contains("S"))
		{
			S = ((BitUtil.bit(number, 7)) != 0) ? 1 : 0;
		}
		
		if (flags.contains("P"))
		{
			P = BitUtil.parity(number) ^ 1;
		}
		
		if (flags.contains("CY"))
		{
			CY = (number > 0xFF) ? 1 : 0;
		}
	}
	
	void checkFlags(String flags, int number, int subnumber)
	{
		if (flags.contains("CY"))
		{
			CY = (number < subnumber) ? 1 : 0;
		}
	}
	
	void convertBC()
	{
		b.set(BitUtil.subByte(bc.get(), 1));
		c.set(BitUtil.subByte(bc.get(), 0));
	}
	
	void convertDE()
	{
		d.set(BitUtil.subByte(de.get(), 1));
		e.set(BitUtil.subByte(de.get(), 0));
	}
	
	void convertHL()
	{
		h.set(BitUtil.subByte(hl.get(), 1));
		l.set(BitUtil.subByte(hl.get(), 0));
	}
}