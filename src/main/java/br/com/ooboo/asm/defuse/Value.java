package br.com.ooboo.asm.defuse;

import java.util.List;

public interface Value extends org.objectweb.asm.tree.analysis.Value {
	
	List<Variable> getVariables();

}
