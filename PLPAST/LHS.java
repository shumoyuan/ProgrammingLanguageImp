package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPTypes.Type;

public class LHS extends PLPASTNode {
	
	public final String identifier;
	public Declaration decl;
	public Type type;

	public LHS(Token firstToken, String name) {
		super(firstToken);
		this.identifier = name;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitLHS(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LHS other = (LHS) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	public void setDecl(Declaration decl) {
		this.decl = decl;
	}
	
	public Declaration getDecl() {
		return decl;
	}

	public Type getType()
	{
		return type;
	}
}
