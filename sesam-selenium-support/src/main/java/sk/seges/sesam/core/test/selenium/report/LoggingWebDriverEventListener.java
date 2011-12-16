package sk.seges.sesam.core.test.selenium.report;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import sk.seges.sesam.core.test.selenium.report.model.CommandResult;
import sk.seges.sesam.core.test.selenium.report.model.SeleniumOperation;
import sk.seges.sesam.core.test.selenium.report.model.SeleniumOperationResult;
import sk.seges.sesam.core.test.selenium.report.model.SeleniumOperationState;
import sk.seges.sesam.core.test.selenium.report.model.api.TestResultCollector;

public class LoggingWebDriverEventListener implements TestResultCollector {

	public LoggingWebDriverEventListener() {}

	private CommandResult commandResult = null;

	@Override
	public CommandResult getCommandResult() {
		return commandResult;
	}
	
	private CommandResult getCommandResult(SeleniumOperationState state, SeleniumOperationResult result, Throwable throwable) {
		CommandResult commandResult = new CommandResult();
//		if (commandResults.size() > 0) {
//			CommandResult lastCommandResult = commandResults.get(commandResults.size() - 1);
//			commandResult.setOperation(lastCommandResult.getOperation());
//		}
		commandResult.setState(state);
		commandResult.setResult(result);
		commandResult.setThrowable(throwable);
//		commandResults.add(commandResult);
		return commandResult;
	}
	
	private CommandResult getCommandResult(SeleniumOperationState state, SeleniumOperation operation, SeleniumOperationResult result, Object... params) {
		CommandResult commandResult = new CommandResult();
		commandResult.setState(state);
		commandResult.setOperation(operation);
		commandResult.setResult(result);
		commandResult.setParameters(params);
//		commandResults.add(commandResult);
		return commandResult;
	}

	@Override
	public void beforeNavigateTo(String url, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.NAVIGATE_TO, SeleniumOperationResult.NONE, url);
	}

	@Override
	public void afterNavigateTo(String url, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.NAVIGATE_TO, SeleniumOperationResult.OK, url);
	}

	@Override
	public void beforeNavigateBack(WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.NAVIGATE_BACK, SeleniumOperationResult.NONE);
	}

	@Override
	public void afterNavigateBack(WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.NAVIGATE_BACK, SeleniumOperationResult.OK);
	}

	@Override
	public void beforeNavigateForward(WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.NAVIGATE_FORWARD, SeleniumOperationResult.NONE);
	}

	@Override
	public void afterNavigateForward(WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.NAVIGATE_FORWARD, SeleniumOperationResult.OK);
	}

	@Override
	public void beforeFindBy(By by, WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.FIND_BY, SeleniumOperationResult.NONE, by, element);
	}

	@Override
	public void afterFindBy(By by, WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.FIND_BY, SeleniumOperationResult.OK, by, element);
	}

	@Override
	public void beforeClickOn(WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.CLICK_ON, SeleniumOperationResult.NONE, element);
	}

	@Override
	public void afterClickOn(WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.CLICK_ON, SeleniumOperationResult.OK, element);
	}

	@Override
	public void beforeChangeValueOf(WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.CHANGE_VALUE, SeleniumOperationResult.NONE, element);
	}

	@Override
	public void afterChangeValueOf(WebElement element, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.CHANGE_VALUE, SeleniumOperationResult.OK, element);
	}

	@Override
	public void beforeScript(String script, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.BEFORE, SeleniumOperation.RUN_SCRIPT, SeleniumOperationResult.NONE, script);
	}

	@Override
	public void afterScript(String script, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperation.RUN_SCRIPT, SeleniumOperationResult.OK, script);
	}

	@Override
	public void onException(Throwable throwable, WebDriver driver) {
		commandResult = getCommandResult(SeleniumOperationState.AFTER, SeleniumOperationResult.FAILURE, throwable);
	}

	@Override
	public void initialize() {}

	@Override
	public void finish() {}
}