package sk.seges.corpis.pap.model.hibernate.printer.method;

import java.io.PrintWriter;

import javax.annotation.processing.RoundEnvironment;

import org.hibernate.Hibernate;

import sk.seges.sesam.core.pap.writer.FormattedPrintWriter;
import sk.seges.sesam.pap.model.model.TransferObjectProcessingEnvironment;
import sk.seges.sesam.pap.model.model.api.ElementHolderTypeConverter;
import sk.seges.sesam.pap.model.printer.converter.ConverterProviderPrinter;
import sk.seges.sesam.pap.model.printer.method.CopyToDtoPrinter;
import sk.seges.sesam.pap.model.resolver.api.EntityResolver;
import sk.seges.sesam.pap.model.resolver.api.ParametersResolver;

public class HibernateCopyToDtoPrinter extends CopyToDtoPrinter {

	public HibernateCopyToDtoPrinter(ConverterProviderPrinter converterProviderPrinter, ElementHolderTypeConverter elementHolderTypeConverter,
			EntityResolver entityResolver, ParametersResolver parametersResolver, RoundEnvironment roundEnv,
			TransferObjectProcessingEnvironment processingEnv, FormattedPrintWriter pw) {
		super(converterProviderPrinter, elementHolderTypeConverter, entityResolver, parametersResolver, roundEnv, processingEnv, pw);
	}

	@Override
	protected void printIsInitializedMethod(PrintWriter pw, String instanceName) {
		pw.println("return " + Hibernate.class.getCanonicalName() + ".isInitialized(" + instanceName + ");");
	}
	
}