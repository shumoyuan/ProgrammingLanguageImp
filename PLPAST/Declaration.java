package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPTypes.Type;
import java.util.HashMap;

public abstract class Declaration extends PLPASTNode {

	public int slotnum;
	public Type type;
	public HashMap<String, Integer> MapList = new HashMap<String, Integer>();
	
	public int gettimeslot() {
		return slotnum;
	}
	
	public void settimeslot(int tmpslot) {
			this.slotnum = tmpslot;
	}
	
	public Declaration(Token firstToken) {
		super(firstToken);
	}
	
	public Type getType()
	{
		return type;
	}
	
	public void MapAdd(String name,Integer index)
	{
		MapList.put(name, index);
	}
	
	public int MapSearch(String name)
	{
		if(MapList.get(name)!=null) {
			return MapList.get(name);
		}
		else {
			return 0;
		}
	}
}
