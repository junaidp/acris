package sk.seges.sesam.pap.service.printer;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import sk.seges.sesam.core.pap.accessor.AnnotationAccessor.AnnotationTypeFilter;
import sk.seges.sesam.core.pap.printer.AnnotationPrinter;
import sk.seges.sesam.core.pap.printer.MethodPrinter;
import sk.seges.sesam.core.pap.writer.FormattedPrintWriter;
import sk.seges.sesam.pap.model.model.Field;
import sk.seges.sesam.pap.model.model.TransferObjectProcessingEnvironment;
import sk.seges.sesam.pap.model.model.api.domain.DomainType;
import sk.seges.sesam.pap.model.model.api.dto.DtoType;
import sk.seges.sesam.pap.model.printer.converter.ConverterProviderPrinter;
import sk.seges.sesam.pap.model.printer.converter.ConverterTargetType;
import sk.seges.sesam.pap.model.resolver.ConverterConstructorParametersResolverProvider;
import sk.seges.sesam.pap.service.printer.model.ServiceConverterPrinterContext;

public class ServiceMethodConverterPrinter extends AbstractServiceMethodPrinter {

	public static final String RESULT_VARIABLE_NAME = "result";

	public ServiceMethodConverterPrinter(TransferObjectProcessingEnvironment processingEnv,
			ConverterConstructorParametersResolverProvider parametersResolverProvider, FormattedPrintWriter pw,
			ConverterProviderPrinter converterProviderPrinter) {
		super(processingEnv, parametersResolverProvider, pw, converterProviderPrinter);
	}

	protected void printCastLocalMethodResult(DtoType returnDtoType, ServiceConverterPrinterContext context) {}
		
	protected void handleMethod(ServiceConverterPrinterContext context, ExecutableElement localMethod, ExecutableElement remoteMethod) {

		DtoType returnDtoType = null;
		
		if (!remoteMethod.getReturnType().getKind().equals(TypeKind.VOID)) {
			returnDtoType = processingEnv.getTransferObjectUtils().getDtoType(remoteMethod.getReturnType());
		}

		new AnnotationPrinter(pw, processingEnv).printMethodAnnotations(localMethod, new AnnotationTypeFilter(false, getSupportedAnnotations(localMethod)));

		//TODO is NULL ok?
		new MethodPrinter(pw, processingEnv).printMethodDefinition(remoteMethod, null);
		
		pw.println("{");

		boolean hasConverter = false;
		
		if (!remoteMethod.getReturnType().getKind().equals(TypeKind.VOID)) {
			if (returnDtoType.getConverter() != null) {
				hasConverter = true;
			}
		}
		
		if (!hasConverter) {
			for (int i = 0; i < localMethod.getParameters().size(); i++) {
				TypeMirror dtoType = remoteMethod.getParameters().get(i).asType();
				DtoType parameterDtoType = processingEnv.getTransferObjectUtils().getDtoType(dtoType);
				
				if (parameterDtoType.getConverter() != null) {
					hasConverter = true;
					break;
				}
			}
		}
	
		if (hasConverter) {
			converterProviderPrinter.printConverterParams(localMethod, pw);
		}
		
		if (!remoteMethod.getReturnType().getKind().equals(TypeKind.VOID)) {
			pw.print(localMethod.getReturnType(), " " + RESULT_VARIABLE_NAME + " = ");
		}
		
		printCastLocalMethodResult(returnDtoType, context);
		
		pw.print(context.getLocalServiceFieldName() + "." + localMethod.getSimpleName().toString() + "(");

		for (int i = 0; i < localMethod.getParameters().size(); i++) {
			if (i > 0) {
				pw.print(", ");
			}

			TypeMirror dtoType = remoteMethod.getParameters().get(i).asType();
			
			DtoType parameterDtoType = processingEnv.getTransferObjectUtils().getDtoType(dtoType);
			DomainType parameterDomainType = parameterDtoType.getDomain();
			
			String parameterName = remoteMethod.getParameters().get(i).getSimpleName().toString();
			
			if (parameterDtoType.getConverter() != null) {
				pw.print("(", parameterDomainType, ")");
				Field field = new Field(parameterName, parameterDtoType);
				pw.print("(");
				//DtoConverter<Object, ClientSession<UserData>> converterForDomain = 
				//converterProvider.getConverterForDomain(result, new MapConvertedInstanceCache());

				converterProviderPrinter.printObtainConverterFromCache(ConverterTargetType.DTO, parameterDomainType, field, localMethod, false);

				//NPE check
				pw.print(" == null ? null : ");
				converterProviderPrinter.printObtainConverterFromCache(ConverterTargetType.DTO, parameterDomainType, field, localMethod, false);
				
				//converterProviderPrinter.printDtoEnsuredConverterMethodName(parameterDtoType, field, localMethod, pw, true);
				pw.print(".fromDto(");
			}

			pw.print(parameterName);

			if (parameterDtoType.getConverter() != null) {
				pw.print("))");
			}
		}

		pw.print(")");
		pw.println(";");

		if (!remoteMethod.getReturnType().getKind().equals(TypeKind.VOID) && returnDtoType.getConverter() != null) {
			pw.print("return (", processingEnv.getTypeUtils().toMutableType(remoteMethod.getReturnType()), ")");
			
			pw.print("(");
			
			Field field = new Field(RESULT_VARIABLE_NAME, processingEnv.getTypeUtils().toMutableType(remoteMethod.getReturnType()));
			//converterProviderPrinter.printDomainEnsuredConverterMethodName(returnDtoType.getDomain(), null, field, localMethod, pw, true);

			converterProviderPrinter.printObtainConverterFromCache(ConverterTargetType.DOMAIN, returnDtoType.getDomain(), field, localMethod, false);
			
			//NPE check
			pw.print(" == null ? null : ");
			converterProviderPrinter.printObtainConverterFromCache(ConverterTargetType.DOMAIN, returnDtoType.getDomain(), field, localMethod, false);
			
			pw.println(".toDto(" + RESULT_VARIABLE_NAME + "));");
		} else if (!remoteMethod.getReturnType().getKind().equals(TypeKind.VOID)) {
			pw.println("return " + RESULT_VARIABLE_NAME + ";");
		}
		
		pw.println("}");
		pw.println();
	}

	protected Class<?>[] getSupportedAnnotations(Element method) {
		return new Class<?>[] {
			Override.class
		};
	}
}