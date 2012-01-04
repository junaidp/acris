package sk.seges.sesam.pap.model.provider;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import sk.seges.sesam.core.model.converter.CollectionConfiguration;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror.MutableTypeKind;
import sk.seges.sesam.pap.model.annotation.TransferObjectMapping;
import sk.seges.sesam.pap.model.model.ConfigurationTypeElement;
import sk.seges.sesam.pap.model.model.TransferObjectMappingAccessor;
import sk.seges.sesam.pap.model.model.TransferObjectProcessingEnvironment;
import sk.seges.sesam.pap.model.provider.api.ConfigurationProvider;

public class RoundEnvConfigurationProvider implements ConfigurationProvider {

	protected final TransferObjectProcessingEnvironment processingEnv;
	protected final RoundEnvironment roundEnv;
	
	public RoundEnvConfigurationProvider(TransferObjectProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
		this.processingEnv = processingEnv;
		this.roundEnv = roundEnv;
	}
	
	protected Class<?>[] getCommonConfigurations() {
		return new Class<?> [] {
				CollectionConfiguration.class
		};
	}

	protected boolean isSupportedType(MutableTypeMirror type) {
		return (!type.getKind().equals(MutableTypeKind.PRIMITIVE));
	}
	
	protected Set<? extends Element> getConfigurationElements() {
		return roundEnv.getElementsAnnotatedWith(TransferObjectMapping.class);
	}
	
	public ConfigurationTypeElement getConfigurationForDomain(MutableTypeMirror domainType) {

		if (!isSupportedType(domainType)) {
			return null;
		};

		Element result = null;
		
		Set<? extends Element> elementsAnnotatedWith = getConfigurationElements();
		for (Element annotatedElement : elementsAnnotatedWith) {
			if (annotatedElement.asType().getKind().equals(TypeKind.DECLARED)) {
				ConfigurationTypeElement configurationTypeElement = getConfigurationElement((TypeElement)annotatedElement, processingEnv, roundEnv);
	
				if (configurationTypeElement.appliesForDomainType(domainType)) {
					result = annotatedElement;
					if (configurationTypeElement.getDelegateConfigurationTypeElement() == null) {
						return getConfigurationElement(domainType, null, annotatedElement);
						//return new ConfigurationTypeElement((MutableDeclaredType)domainType, null, (TypeElement)annotatedElement, processingEnv, roundEnv);
					}
				}
			}
		}

		if (getCommonConfigurations() != null) {
			for (Class<?> clazz: getCommonConfigurations()) {
				TypeElement configurationElement = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName());
				if (configurationElement.getAnnotation(TransferObjectMapping.class) != null) {

					ConfigurationTypeElement configurationTypeElement = getConfigurationElement(configurationElement, processingEnv, roundEnv);

					if (configurationTypeElement.appliesForDomainType(domainType)) {
						return getConfigurationElement(domainType, null, configurationElement);
						//return new ConfigurationTypeElement((MutableDeclaredType)domainType, null, configurationElement, processingEnv, roundEnv);
					}
				}
			}
		}

		if (result != null) {
			return getConfigurationElement(domainType, null,  result);
			//return new ConfigurationTypeElement((MutableDeclaredType)domainType, null, (TypeElement)result, processingEnv, roundEnv);
		}

		return null;
	}

	public ConfigurationTypeElement getConfigurationForDto(MutableTypeMirror dtoType) {

		if (!isSupportedType(dtoType)) {
			return null;
		};
		
		Element result = null;
		
		Set<? extends Element> elementsAnnotatedWith = getConfigurationElements();
		for (Element annotatedElement : elementsAnnotatedWith) {
			if (annotatedElement.asType().getKind().equals(TypeKind.DECLARED)) {
				ConfigurationTypeElement configurationTypeElement = getConfigurationElement((TypeElement)annotatedElement, processingEnv, roundEnv);
	
				if (configurationTypeElement.appliesForDtoType(dtoType)) {
					result = annotatedElement;
					
					if (configurationTypeElement.getDelegateConfigurationTypeElement() == null) {
						return getConfigurationElement(null, dtoType, annotatedElement);
						//return new ConfigurationTypeElement(null, (MutableDeclaredType)dtoType, (TypeElement)annotatedElement, processingEnv, roundEnv);
					}
				}
			}
		}

		if (getCommonConfigurations() != null) {
			for (Class<?> clazz: getCommonConfigurations()) {
				TypeElement configurationElement = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName());
				if (configurationElement.getAnnotation(TransferObjectMapping.class) != null) {

					ConfigurationTypeElement configurationTypeElement = getConfigurationElement(configurationElement, processingEnv, roundEnv);

					if (configurationTypeElement.appliesForDtoType(dtoType)) {
						return getConfigurationElement(null, dtoType, configurationElement);
						//return new ConfigurationTypeElement(null, (MutableDeclaredType)dtoType, configurationElement, processingEnv, roundEnv);
					}
				}
			}
		}

		if (result != null) {
			return getConfigurationElement(null, dtoType, result);
			//return new ConfigurationTypeElement(null, (MutableDeclaredType)dtoType, (TypeElement)result, processingEnv, roundEnv);
		}

		//Configuration should be directly in the DTO - when its generated
		if (dtoType.getKind().isDeclared() && (((MutableDeclaredType)dtoType).asType()) != null && ((MutableDeclaredType)dtoType).asType().getKind().equals(TypeKind.DECLARED)) {
			TransferObjectMappingAccessor transferObjectConfiguration = new TransferObjectMappingAccessor(((DeclaredType)((MutableDeclaredType)dtoType).asType()).asElement(), processingEnv);
			if (transferObjectConfiguration.getMappingForDto((MutableDeclaredType)dtoType) != null) {
				return getConfigurationElement(((DeclaredType)((MutableDeclaredType)dtoType).asType()).asElement(), processingEnv, roundEnv);
				//return new ConfigurationTypeElement(((DeclaredType)((MutableDeclaredType)dtoType).asType()).asElement(), processingEnv, roundEnv);
			}
		}
		
		return null;
	}
	
	protected ConfigurationTypeElement getConfigurationElement(MutableTypeMirror domainType, MutableTypeMirror dtoType, Element annotatedElement) {
		return new ConfigurationTypeElement((MutableDeclaredType)domainType, (MutableDeclaredType)dtoType, (TypeElement)annotatedElement, processingEnv, roundEnv);
	}
	
	protected ConfigurationTypeElement getConfigurationElement(Element configurationElement, TransferObjectProcessingEnvironment processingEnv, RoundEnvironment roundEnv, ConfigurationProvider... configurationProviders) {
		return new ConfigurationTypeElement(configurationElement, processingEnv, roundEnv, configurationProviders);
	}
}