package sk.seges.sesam.core.pap.model.mutable.utils;

import java.util.HashSet;
import java.util.Set;

import sk.seges.sesam.core.pap.model.api.ClassSerializer;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable;

class MutableVariable extends MutableType implements MutableTypeVariable {

	protected String variable;
	protected Set<MutableTypeMirror> lowerBounds = new HashSet<MutableTypeMirror>();
	protected Set<MutableTypeMirror> upperBounds = new HashSet<MutableTypeMirror>();
	
	@Override
	public String getVariable() {
		return this.variable;
	};
	
	@Override
	public MutableTypeVariable setVariable(String variable) {
		this.variable = variable;
		return this;
	};
	
	@Override
	public Set<? extends MutableTypeMirror> getLowerBounds() {
		return this.lowerBounds;
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public MutableTypeVariable setLowerBounds(Set<? extends MutableTypeMirror> bounds) {
		this.lowerBounds = (Set<MutableTypeMirror>) bounds;
		if (this.lowerBounds == null) {
			this.lowerBounds = new HashSet<MutableTypeMirror>();
		}
		return this;
	}

	@Override
	public MutableTypeVariable addLowerBound(MutableTypeMirror bound) {
		this.lowerBounds.add(bound);
		return this;
	}

	@Override
	public Set<? extends MutableTypeMirror> getUpperBounds() {
		return this.upperBounds;
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public MutableTypeVariable setUpperBounds(Set<? extends MutableTypeMirror> bounds) {
		this.upperBounds = (Set<MutableTypeMirror>) bounds;
		if (this.upperBounds == null) {
			this.upperBounds = new HashSet<MutableTypeMirror>();
		}
		return this;
	}

	@Override
	public MutableTypeVariable addUpperBound(MutableTypeMirror bound) {
		this.upperBounds.add(bound);
		return this;
	}

	@Override
	public String toString() {
		return toString(ClassSerializer.CANONICAL, true);
	}
	
	public String toString(ClassSerializer serializer) {	
		return toString(serializer, true);
	}

	public String toString(ClassSerializer serializer, boolean typed) {
		String result = "";

		if (getVariable() != null) {
			result += getVariable();
		}

		if (getUpperBounds().size() > 0) {
			
			if (getVariable() != null) {
				result += " extends ";
			}

			int i = 0;
			for (MutableTypeMirror typeVariable : getUpperBounds()) {
				if (i > 0) {
					result += " & ";
				}
				result += typeVariable.toString(serializer, typed);
				i++;
			}
		}

		if (getLowerBounds().size() > 0) {
			
			if (getVariable() != null) {
				result += " super ";
			}

			result += getLowerBounds().iterator().next().toString(serializer, typed);
		}

		return result;
	}
	
	@Override
	public MutableVariable clone() {
		MutableVariable result = new MutableVariable();
		result.variable = variable;
		for (MutableTypeMirror lowerBound: lowerBounds) {
			result.lowerBounds.add(lowerBound);
		}
		for (MutableTypeMirror upperBound: upperBounds) {
			result.upperBounds.add(upperBound);
		}
		return result;
	}

	@Override
	public MutableTypeKind getKind() {
		return MutableTypeKind.TYPEVAR;
	}
}