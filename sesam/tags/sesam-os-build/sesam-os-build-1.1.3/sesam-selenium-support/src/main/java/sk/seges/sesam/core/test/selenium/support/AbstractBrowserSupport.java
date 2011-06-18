package sk.seges.sesam.core.test.selenium.support;

import sk.seges.sesam.core.test.selenium.exception.SeleniumException;

public abstract class AbstractBrowserSupport {

	public interface ActionHandler {
		boolean doAction();
	}

	public void fail(String message) {
		throw new SeleniumException(message);
	}

	public void fail(Exception e) {
		throw new SeleniumException(e);
	}

//	public void waitFor(ActionHandler actionHandler) {
//		for (int second = 0;; second++) {
//			if (second >= 60) {
//				fail("Timeout exceed while waiting for the action!");
//			}
//			try {
//				if (actionHandler.doAction()) {
//					break;
//				}
//			} catch (Exception e) {
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				fail(e);
//			}
//		}
//	}
}