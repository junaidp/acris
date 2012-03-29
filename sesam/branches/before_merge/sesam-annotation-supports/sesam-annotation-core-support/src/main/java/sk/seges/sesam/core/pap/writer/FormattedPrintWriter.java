package sk.seges.sesam.core.pap.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import sk.seges.sesam.core.pap.model.api.ClassSerializer;
import sk.seges.sesam.core.pap.model.mutable.api.MutableArrayType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableArrayTypeValue;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredTypeValue;
import sk.seges.sesam.core.pap.model.mutable.api.MutableType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror.MutableTypeKind;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeValue;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable;
import sk.seges.sesam.core.pap.model.mutable.api.MutableWildcardType;
import sk.seges.sesam.core.pap.model.mutable.utils.MutableProcessingEnvironment;
import sk.seges.sesam.core.pap.writer.api.DelayedPrintWriter;

public class FormattedPrintWriter extends PrintWriter implements DelayedPrintWriter {
	
	public interface FlushListener {
		
		void beforeFlush(FormattedPrintWriter pw);

		void afterFlush(FormattedPrintWriter pw);
	}

	public static final String DEFAULT_OUDENT = "\t";
	public static final int LINE_LENGTH = 120;
	
	private int oudentLevel = 0;
	private boolean startLine = true;
	
	private boolean autoIndent = false;
	private final MutableProcessingEnvironment processingEnv;

	private Set<FlushListener> listeners = new HashSet<FlushListener>();

	public FormattedPrintWriter(Writer out, MutableProcessingEnvironment processingEnv) {
		super(out);
		this.processingEnv = processingEnv;
	}

	public FormattedPrintWriter(OutputStream out, MutableProcessingEnvironment processingEnv) {
		super(out);
		this.processingEnv = processingEnv;
	}

	public FormattedPrintWriter(String fileName, MutableProcessingEnvironment processingEnv) throws FileNotFoundException {
		super(fileName);
		this.processingEnv = processingEnv;
	}

	public FormattedPrintWriter(String fileName, String csn, MutableProcessingEnvironment processingEnv) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
		this.processingEnv = processingEnv;
	}

	public FormattedPrintWriter(File file, MutableProcessingEnvironment processingEnv) throws FileNotFoundException {
		super(file);
		this.processingEnv = processingEnv;
	}

	public FormattedPrintWriter(File file, String csn, MutableProcessingEnvironment processingEnv) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
		this.processingEnv = processingEnv;
	}
	
	public void setDefaultIdentLevel(int level) {
		this.oudentLevel = level;
	}
	
	public void setAutoIndent(boolean autoIndent) {
		this.autoIndent = autoIndent;
	}
	
	public void indent() {
		if (autoIndent) {
			throw new RuntimeException("Unable to indent manually in auto mode. Please set autoIndent to false.");
		}
		oudentLevel = (oudentLevel <= 0) ? 0 : oudentLevel-1;
	}
	
	public void oudent() {
		if (autoIndent) {
			throw new RuntimeException("Unable to oudent manually in auto mode. Please set autoIndent to false.");
		}
		oudentLevel++;
	}

	private void setAutoOudent(char c) {
		if (c == '{') {
			oudentLevel++;
		}
	}

	private void setAutoIndent(char c) {
		if (c == '}') {
			oudentLevel = (oudentLevel <= 0) ? 0 : oudentLevel-1;
		}
	}

	private boolean processing = false;
	
	private void addIdentation() {
		if (processing) {
			return;
		}
		processing = true;
		if (startLine) {
			String indentation = "";
			
			for (int i = 0; i < oudentLevel; i++) {
				indentation += DEFAULT_OUDENT;
			}
			
			currentPosition += indentation.length();
			super.print(indentation);
			startLine = false;
		}
		processing = false;
	}

	private String lastText = null;
	
	@Override
	public void write(String text, int off, int len) {
		if (!processing) {
			if (currentPosition == 0 && text != null && text.length() > off) {
				setAutoIndent(text.charAt(off));
			}
			addIdentation();
		}
		currentPosition += len;
		super.write(text, off, len);
		if (!processing) {
			lastText += text.substring(off, len);
		}
	}

	private void newLine() {
		if (lastText != null && autoIndent) {
			for (int i = 0; i < lastText.length(); i++) {
				if (i > 0) {
					setAutoIndent(lastText.charAt(i));
				}
				setAutoOudent(lastText.charAt(i));
			}
		}
		super.println();
		startLine = true;
		lastText = "";
		currentPosition = 0;
	}
	
	@Override
	public void println() {
		newLine();
	}

	private ClassSerializer serializer = ClassSerializer.CANONICAL;
	private boolean typed = false;
	
	@Override
	public void setSerializer(ClassSerializer serializer) {
		this.serializer = serializer;
	}

	public void serializeTypeParameters(boolean typed) {
		this.typed = typed;
	}

	@Override
	public void println(Object x) {
		println(new Object[] {x});
	}

	@Override
	public void println(Object... x) {
		print(x);
		newLine();
	}

	private List<MutableDeclaredType> usedTypes = new ArrayList<MutableDeclaredType>();
	
	@Override
	public void print(Object obj) {
		print(new Object[] {obj});
	}

	private Set<MutableDeclaredType> extractDeclaredType(MutableTypeMirror type) {
		return extractDeclaredType(type, new HashSet<MutableDeclaredType>());
	}
	
	private Set<MutableDeclaredType> extractDeclaredType(MutableTypeMirror type, Set<MutableDeclaredType> types) {
			
		if (type instanceof MutableDeclaredType) {
			types.add((MutableDeclaredType)type);
			if (typed) {
				for (MutableTypeVariable variable: ((MutableDeclaredType)type).getTypeVariables()) {
					types.addAll(extractDeclaredType(variable));
				}
			}
			return types;
		} 
		
		if (type instanceof MutableArrayType) {
			extractDeclaredType(((MutableArrayType)type).getComponentType(), types);
			return types;
		} 

		if (type instanceof MutableTypeVariable) {
			MutableTypeVariable variable = ((MutableTypeVariable)type);
			Set<? extends MutableTypeMirror> lowerBounds = variable.getLowerBounds();
			
			for (MutableTypeMirror lowerBound: lowerBounds) {
				extractDeclaredType(lowerBound, types);
			}
			
			Set<? extends MutableTypeMirror> upperBounds = variable.getUpperBounds();

			for (MutableTypeMirror upperBound: upperBounds) {
				extractDeclaredType(upperBound, types);
			}
			
			return types;
		}
		
		if (type instanceof MutableWildcardType) {
			MutableWildcardType wildcard = (MutableWildcardType)type;
			
			if (wildcard.getExtendsBound() != null) {
				extractDeclaredType(wildcard.getExtendsBound(), types);
			}
			
			if (wildcard.getSuperBound() != null) {
				extractDeclaredType(wildcard.getSuperBound(), types);
			}

			return types;
		}
		
		return types;
	}
	
	private MutableTypeMirror toMutableType(Object o) {
		if (o instanceof MutableDeclaredType) {
			return (MutableDeclaredType)o;
		} else if (o instanceof MutableArrayType) {
			return (MutableArrayType)o;
		} else if (o instanceof MutableTypeVariable) {
			return (MutableTypeVariable)o;
		} else if (o instanceof MutableWildcardType) {
			return (MutableWildcardType)o;
		} else if (o instanceof MutableArrayTypeValue) {
			return ((MutableArrayTypeValue)o).asType();
		} else if (o instanceof MutableDeclaredTypeValue) {
			return ((MutableDeclaredTypeValue)o).asType();
		}
		
		return null;
	}

	private String getImportPackage(MutableDeclaredType type) {
		if (type.getEnclosedClass() != null) {
			return type.getEnclosedClass().getCanonicalName();
		}
		return type.getPackageName();
	};

	private boolean isConflictType(MutableTypeValue mutableTypeValue) {
		return false;
	}
	
	private boolean isConflictType(MutableDeclaredType mutableType) {
		for (MutableDeclaredType importType: usedTypes) {
			if (getImportPackage(importType) != null && importType.getSimpleName().equals(mutableType.getSimpleName()) && !getImportPackage(importType).equals(getImportPackage(mutableType))) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isConflictType(MutableTypeMirror mutableType) {
		Set<MutableDeclaredType> declaredTypes = extractDeclaredType(mutableType);
		
		//TODO implement better version - print canonical names only those type variables that are in conflict (not whole declared type)
		for (MutableDeclaredType declaredType: declaredTypes) {
			if (isConflictType(declaredType)) {
				return true;
			}
		}
		
		return false;
	}
	
	private int currentPosition = 0;
	
	public int getCurrentPosition() {
		return currentPosition;
	}
	
	@Override
	public void print(Object... x) {
		
		int length = 0;
		
		for (Object o: x) {
			if (o instanceof TypeMirror) {
				o = processingEnv.getTypeUtils().toMutableType((TypeMirror)o);
			} else if (o instanceof Element) {
				o = processingEnv.getTypeUtils().toMutableType(((Element)o).asType());
			} else if (o instanceof Class) {
				o = processingEnv.getTypeUtils().toMutableType((Class<?>)o);
			}

			if (o instanceof MutableTypeValue) {

				ClassSerializer evalSerializer = serializer;
				if (serializer.equals(ClassSerializer.SIMPLE)) {
					if (isConflictType((MutableTypeValue)o)) {
						evalSerializer = ClassSerializer.CANONICAL;
					} else {
						usedTypes.addAll(extractValueTypes((MutableTypeValue)o));
					}
				}

				String res = ((MutableTypeValue)o).toString(evalSerializer, typed);
				length += res.length();
				super.write(res);
			} else {
				MutableTypeMirror mutableType = toMutableType(o);
				
				if (mutableType != null) {
					
					ClassSerializer evalSerializer = serializer;
					if (serializer.equals(ClassSerializer.SIMPLE)) {
						if (isConflictType(mutableType)) {
							evalSerializer = ClassSerializer.CANONICAL;
						} else {
							usedTypes.addAll(extractDeclaredType(mutableType));
						}
					}
					String res = mutableType.toString(evalSerializer, typed);
					length += res.length();
					write(res);
				} else {
					String res = String.valueOf(o);
					length += res.length();
					super.write(res);
				}
			}
		}
		
		currentPosition += length;
	}
	
	private Set<MutableDeclaredType> extractValueTypes(MutableTypeValue value) {
		
		Set<MutableDeclaredType> types = new HashSet<MutableDeclaredType>();

		if (value == null) {
			return types;
		}

		if (value instanceof MutableDeclaredTypeValue) {

			//value is clazz
			if (value.getValue() instanceof MutableType) {
				return types;
			}

			MutableTypeMirror type = ((MutableDeclaredTypeValue) value).asType();
			
			//primitive types
			if (type.getKind().equals(MutableTypeKind.PRIMITIVE) || unboxType(processingEnv.getTypeUtils().fromMutableType(type)).getKind().isPrimitive()) {
				return types;
			}

			List<Method> methods = getGetterMethods(Arrays.asList(value.getValue().getClass().getDeclaredMethods()));
			
			for (Method method: methods) {
				try {
					MutableTypeValue typeValue = processingEnv.getTypeUtils().getTypeValue(method.invoke(value.getValue()));
					if (typeValue instanceof MutableArrayTypeValue) {
						types.add((MutableDeclaredType)((MutableArrayTypeValue) typeValue).asType().getComponentType());
					} else if (typeValue instanceof MutableDeclaredTypeValue) {
						types.add(((MutableDeclaredTypeValue) typeValue).asType());
					}
				} catch (Exception e) {
				}
			}
			
			types.add(((MutableDeclaredTypeValue)value).asType());
		}

		if (value instanceof MutableArrayTypeValue) {
			types.add((MutableDeclaredType)((MutableArrayTypeValue) value).asType().getComponentType());
			
			MutableTypeValue[] arrayValues = ((MutableArrayTypeValue) value).getValue();
			
			if (arrayValues != null && arrayValues.length > 0) {
				types.addAll(extractValueTypes(arrayValues[0]));
			}
		}

		return types;
	}
	
	protected TypeMirror unboxType(TypeMirror type) {
		try {
			return processingEnv.getTypeUtils().unboxedType(type);
		} catch (Exception e) {
			return type;
		}
	}

	private List<Method> getGetterMethods(List<Method> methods) {
		List<Method> result = new ArrayList<Method>();
		
		for (Method method: methods) {
			if (method.getName().startsWith("get")) {
				result.add(method);
			}
		}
		
		return result;
	}

	public List<MutableDeclaredType> getUsedTypes() {
		return usedTypes;
	}
	
	public void addFlushListener(FlushListener listener) {
		listeners.add(listener);
	}

	public void removeFlushListener(FlushListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void flush() {
		for (FlushListener flushListener: listeners) {
			flushListener.beforeFlush(this);
		}
		super.flush();
		for (FlushListener flushListener: listeners) {
			flushListener.afterFlush(this);
		}
	}
}