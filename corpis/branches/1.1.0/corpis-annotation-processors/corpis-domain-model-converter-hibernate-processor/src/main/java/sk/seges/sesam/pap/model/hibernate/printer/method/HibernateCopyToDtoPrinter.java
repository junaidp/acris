package sk.seges.sesam.pap.model.hibernate.printer.method;

import java.io.PrintWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import org.hibernate.Hibernate;

import sk.seges.sesam.pap.model.model.api.ElementHolderTypeConverter;
import sk.seges.sesam.pap.model.printer.converter.ConverterProviderPrinter;
import sk.seges.sesam.pap.model.printer.method.CopyToDtoPrinter;
import sk.seges.sesam.pap.model.resolver.api.EntityResolver;
import sk.seges.sesam.pap.model.resolver.api.IdentityResolver;
import sk.seges.sesam.pap.model.resolver.api.ParametersResolver;

public class HibernateCopyToDtoPrinter extends CopyToDtoPrinter {

	public HibernateCopyToDtoPrinter(ConverterProviderPrinter converterProviderPrinter,
			ElementHolderTypeConverter elementHolderTypeConverter,
			IdentityResolver identityResolver, EntityResolver entityResolver,
			ParametersResolver parametersResolver, RoundEnvironment roundEnv,
			ProcessingEnvironment processingEnv, PrintWriter pw) {
		super(converterProviderPrinter, elementHolderTypeConverter, identityResolver, entityResolver,
				parametersResolver, roundEnv, processingEnv, pw);
	}

	@Override
	protected void printIsInitializedMethod(PrintWriter pw, String instanceName) {
		pw.println("return " + Hibernate.class.getCanonicalName() + ".isInitialized(" + instanceName + ");");
	}
}